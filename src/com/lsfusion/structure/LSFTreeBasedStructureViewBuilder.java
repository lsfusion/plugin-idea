package com.lsfusion.structure;

import com.intellij.codeInsight.TargetElementUtilBase;
import com.intellij.codeInsight.navigation.ImplementationSearcher;
import com.intellij.ide.structureView.StructureView;
import com.intellij.ide.structureView.StructureViewModel;
import com.intellij.ide.structureView.TreeBasedStructureViewBuilder;
import com.intellij.ide.structureView.newStructureView.StructureViewComponent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiUtilBase;
import com.intellij.util.ui.tree.TreeUtil;
import com.lsfusion.lang.classes.LSFValueClass;
import com.lsfusion.lang.psi.*;
import org.apache.log4j.lf5.viewer.categoryexplorer.TreeModelAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.TreeModelEvent;

public class LSFTreeBasedStructureViewBuilder extends TreeBasedStructureViewBuilder {
    private final LSFFile file;

    private final LSFValueClass valueClass;

    private final LSFStructureFileCaretListener caretListener;

    private final LSFStructureViewNavigationHandler navigationHandler;

    private boolean needTotalExpansion = true;

    public LSFTreeBasedStructureViewBuilder(LSFFile file, LSFStructureFileCaretListener caretListener) {
        this(file, null, caretListener, null);
    }

    public LSFTreeBasedStructureViewBuilder(LSFFile file, LSFValueClass valueClass, LSFStructureViewNavigationHandler navigationHandler) {
        this(file, valueClass, null, navigationHandler);
    }

    public LSFTreeBasedStructureViewBuilder(LSFFile file, LSFValueClass valueClass, LSFStructureFileCaretListener caretListener, LSFStructureViewNavigationHandler navigationHandler) {
        this.file = file;
        this.valueClass = valueClass;
        this.navigationHandler = navigationHandler;
        this.caretListener = caretListener;
    }

    @NotNull
    public StructureViewModel createStructureViewModel(@Nullable Editor editor) {
        if (caretListener != null) {
            caretListener.install(editor);
        }

        LSFValueClass currentClass = valueClass;
        if (currentClass == null) {
            PsiElement targetElement = TargetElementUtilBase.findTargetElement(editor, ImplementationSearcher.getFlags());
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
        return new LSFClassInterfacesTreeModel(file, currentClass, navigationHandler);
    }

    @NotNull
    @Override
    public StructureViewModel createStructureViewModel() {
        return createStructureViewModel(PsiUtilBase.findEditor(file));
    }

    @NotNull
    @Override
    public StructureView createStructureView(FileEditor fileEditor, Project project) {
        StructureView structureView = super.createStructureView(fileEditor, project);

        final JTree tree = ((StructureViewComponent) structureView).getTree();

        tree.getModel().addTreeModelListener(new TreeModelAdapter() {
            @Override
            public void treeStructureChanged(TreeModelEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        if (needTotalExpansion) {
                            TreeUtil.expandAll(tree);
                            needTotalExpansion = false;
                        }
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
