package com.lsfusion.structure;

import com.intellij.codeInsight.TargetElementUtil;
import com.intellij.codeInsight.navigation.ImplementationSearcher;
import com.intellij.ide.impl.StructureViewWrapperImpl;
import com.intellij.ide.structureView.StructureViewFactoryEx;
import com.intellij.ide.structureView.StructureViewWrapper;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.CaretEvent;
import com.intellij.openapi.editor.event.CaretListener;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.concurrency.AppExecutorUtil;
import com.intellij.util.containers.ContainerUtil;
import com.lsfusion.lang.psi.LSFBuiltInClassName;
import com.lsfusion.lang.psi.LSFClassDecl;
import com.lsfusion.lang.psi.LSFId;

import java.util.Collections;
import java.util.Set;

// A project service rather than project user data: user data outlives a plugin unload, and an entry holding
// a plugin object pins the plugin's class loader, so IDEA can apply an update only by restarting.
@Service(Service.Level.PROJECT)
public final class LSFStructureFileCaretListener implements CaretListener, Disposable {
    private final Project project;

    // Editors this listener is attached to. Their caret models outlive an unload the same way, hence dispose().
    private final Set<Editor> installedEditors = Collections.newSetFromMap(ContainerUtil.createConcurrentWeakMap());

    private PsiElement currentClassElement;

    public LSFStructureFileCaretListener(Project project) {
        this.project = project;
    }

    public void install(Editor editor) {
        if (editor != null) {
            CaretModel caretModel = editor.getCaretModel();
            caretModel.removeCaretListener(this);
            // not the addCaretListener(listener, parentDisposable) overload: it registers a Disposer child that
            // holds the caret model, and the only parent available here is this service, which lives as long as
            // the project - install() runs on every structure view build, so those children would pile up and
            // keep every editor they ever saw alive. Hence the weak set and the unhook in dispose() below.
            caretModel.addCaretListener(this);
            installedEditors.add(editor);
        }
    }

    @Override
    public void dispose() {
        for (Editor editor : installedEditors) {
            if (!editor.isDisposed()) {
                editor.getCaretModel().removeCaretListener(this);
            }
        }
        installedEditors.clear();
    }

    @Override
    public void caretPositionChanged(CaretEvent e) {
        Editor editor = e.getEditor();
        int offset = editor.getCaretModel().getOffset();
        // findTargetElement() reads the stub index — a slow operation prohibited on the EDT. Resolve in a
        // background smart-mode read action, then update the structure view on the UI thread.
        ReadAction.nonBlocking(() -> {
                    PsiElement targetElement = TargetElementUtil.getInstance().findTargetElement(editor, ImplementationSearcher.getFlags(), offset);
                    return targetElement instanceof LSFId
                            ? PsiTreeUtil.getParentOfType(targetElement, LSFClassDecl.class, LSFBuiltInClassName.class)
                            : null;
                })
                .inSmartMode(project)
                .expireWhen(() -> project.isDisposed() || editor.isDisposed())
                .coalesceBy(this, editor)
                .finishOnUiThread(ModalityState.defaultModalityState(), parent -> {
                    if (parent != null && parent != currentClassElement) {
                        currentClassElement = parent;
                        StructureViewWrapper structureViewWrapper = StructureViewFactoryEx.getInstanceEx(project).getStructureViewWrapper();
                        if (structureViewWrapper instanceof StructureViewWrapperImpl) {
                            ((StructureViewWrapperImpl) structureViewWrapper).rebuildNow("lsFusion caret class changed");
                        }
                    }
                })
                .submit(AppExecutorUtil.getAppExecutorService());
    }
}
