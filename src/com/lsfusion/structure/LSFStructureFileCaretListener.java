package com.lsfusion.structure;

import com.intellij.codeInsight.TargetElementUtil;
import com.intellij.codeInsight.navigation.ImplementationSearcher;
import com.intellij.ide.impl.StructureViewWrapperImpl;
import com.intellij.ide.structureView.StructureViewFactoryEx;
import com.intellij.ide.structureView.StructureViewWrapper;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.CaretEvent;
import com.intellij.openapi.editor.event.CaretListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.concurrency.AppExecutorUtil;
import com.lsfusion.lang.psi.LSFBuiltInClassName;
import com.lsfusion.lang.psi.LSFClassDecl;
import com.lsfusion.lang.psi.LSFId;

public class LSFStructureFileCaretListener implements CaretListener {
    public final static Key<LSFStructureFileCaretListener> PROJECT_COMPONENT_KEY = Key.create("lsfusion.structureview.caret.listener");

    private final Project project;

    private PsiElement currentClassElement;

    public LSFStructureFileCaretListener(Project project) {
        this.project = project;
    }

    public void install(Editor editor) {
        if (editor != null) {
            editor.getCaretModel().removeCaretListener(this);
            editor.getCaretModel().addCaretListener(this);
        }
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
                            ((StructureViewWrapperImpl) structureViewWrapper).rebuild();
                        }
                    }
                })
                .submit(AppExecutorUtil.getAppExecutorService());
    }
}
