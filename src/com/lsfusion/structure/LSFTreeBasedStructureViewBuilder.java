package com.lsfusion.structure;

import com.intellij.codeInsight.TargetElementUtil;
import com.intellij.codeInsight.navigation.ImplementationSearcher;
import com.intellij.ide.structureView.StructureView;
import com.intellij.ide.structureView.StructureViewModel;
import com.intellij.ide.structureView.TreeBasedStructureViewBuilder;
import com.intellij.ide.structureView.newStructureView.StructureViewComponent;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.IndexNotReadyException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.psi.PsiElement;
import com.intellij.util.ui.tree.TreeModelAdapter;
import com.intellij.util.ui.tree.TreeUtil;
import com.lsfusion.LSFProjectDisposable;
import com.lsfusion.lang.classes.LSFValueClass;
import com.lsfusion.lang.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.TreeModelEvent;

public class LSFTreeBasedStructureViewBuilder extends TreeBasedStructureViewBuilder {
    private final LSFFile file;

    private final LSFValueClass valueClass;
    private final ActionOrPropType type;

    private final LSFStructureFileCaretListener caretListener;

    private final LSFStructureViewNavigationHandler navigationHandler;

    private boolean needTotalExpansion = true;

    public LSFTreeBasedStructureViewBuilder(LSFFile file, LSFStructureFileCaretListener caretListener) {
        this(file, null, caretListener, null, ActionOrPropType.ACTION_OR_PROP);
    }

    public LSFTreeBasedStructureViewBuilder(LSFFile file, LSFValueClass valueClass, LSFStructureViewNavigationHandler navigationHandler) {
        this(file, valueClass, null, navigationHandler, ActionOrPropType.PROP);
    }

    public LSFTreeBasedStructureViewBuilder(LSFFile file, LSFValueClass valueClass, LSFStructureFileCaretListener caretListener, LSFStructureViewNavigationHandler navigationHandler, ActionOrPropType type) {
        this.file = file;
        this.valueClass = valueClass;
        this.navigationHandler = navigationHandler;
        this.caretListener = caretListener;
        this.type = type;
    }

    @NotNull
    public StructureViewModel createStructureViewModel(@Nullable final Editor editor) {
        if (caretListener != null) {
            caretListener.install(editor);
        }

        LSFValueClass currentClass = valueClass;
        if (currentClass == null && editor != null) {
            PsiElement targetElement;
            try {
                targetElement = DumbService.getInstance(editor.getProject()).runReadActionInSmartMode(() -> TargetElementUtil.findTargetElement(editor, ImplementationSearcher.getFlags()));
            } catch (IndexNotReadyException e) {
                // Structure view rebuilds run on EDT under the write-intent lock, where
                // runReadActionInSmartMode can't wait for smart mode and resolve fails while
                // indexing (e.g. on IDE startup) — show the view without caret preselection.
                targetElement = null;
            }

            if (targetElement instanceof LSFId) {
                PsiElement parent = targetElement;
                while (parent != null) {
                    if (parent instanceof LSFClassDecl) {
                        currentClass = (LSFClassDecl) parent;
                        break;
                    } else if (parent instanceof LSFBuiltInClassName) {
                        currentClass = LSFPsiImplUtil.resolve((LSFBuiltInClassName) parent).getCommonClass();
                        break;
                    }
                    parent = parent.getParent();
                }
            }
        }

        needTotalExpansion = true;
        return new LSFClassInterfacesTreeModel(file, currentClass, navigationHandler, type);
    }

    @NotNull
    @Override
    public StructureView createStructureView(FileEditor fileEditor, @NotNull Project project) {
        StructureView structureView = super.createStructureView(fileEditor, project);

        // StructureViewWrapperImpl (2025.3) parents the returned view via Disposer.register on a later
        // EDT dispatch that its collectLatest rebuild loop can cancel first, orphaning the component in
        // the Disposer tree ("Memory leak detected" blaming this plugin on IDE exit). Parent it to the
        // project lifecycle up front; the platform's register() re-parents it in the normal flow.
        // On IDE exit the wrapper rebuilds once more ("clear a structure on hide") while the project
        // container is being disposed — getService then throws AlreadyDisposedException (a quiet
        // cancellation) and tryRegister fails. Nobody would ever dispose the just-created view in that
        // case, so drop it here and cancel the rebuild.
        Disposable leakGuard = null;
        try {
            leakGuard = project.getService(LSFProjectDisposable.class);
        } catch (RuntimeException ignored) {
        }
        if (leakGuard == null || !Disposer.tryRegister(leakGuard, structureView)) {
            Disposer.dispose(structureView);
            throw new ProcessCanceledException();
        }

        final JTree tree = ((StructureViewComponent) structureView).getTree();

        tree.getModel().addTreeModelListener(new TreeModelAdapter() {
            @Override
            public void treeStructureChanged(TreeModelEvent e) {
                SwingUtilities.invokeLater(() -> {
                    if (needTotalExpansion) {
                        TreeUtil.expandAll(tree);
                        needTotalExpansion = false;
                    }
                });
            }
        });
        return structureView;
    }

    @Override
    public boolean isRootNodeShown() {
        return false;
    }
}
