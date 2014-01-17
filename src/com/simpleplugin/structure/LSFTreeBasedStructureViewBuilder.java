package com.simpleplugin.structure;

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
import com.intellij.util.ui.tree.TreeUtil;
import com.simpleplugin.LSFFileCaretListener;
import com.simpleplugin.psi.LSFClassDecl;
import com.simpleplugin.psi.LSFFile;
import com.simpleplugin.psi.LSFId;
import org.apache.log4j.lf5.viewer.categoryexplorer.TreeModelAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.TreeModelEvent;

public class LSFTreeBasedStructureViewBuilder extends TreeBasedStructureViewBuilder {
    public static final LSFTreeBasedStructureViewBuilder INSTANCE = new LSFTreeBasedStructureViewBuilder();

    private LSFFileCaretListener caretListener = new LSFFileCaretListener();

    private LSFFile file;

    private LSFClassInterfacesTreeModel model;

    private boolean needTotalExpansion = false;

    public void setFile(LSFFile file) {

        if (this.file != file) {
            needTotalExpansion = false;
        }
        this.file = file;
    }

    @Override
    @NotNull
    public StructureViewModel createStructureViewModel(@Nullable Editor editor) {
        editor.getCaretModel().removeCaretListener(caretListener);
        editor.getCaretModel().addCaretListener(caretListener);
        caretListener.setFile(file);

        if (model == null) {
            model = new LSFClassInterfacesTreeModel(editor, file);
            needTotalExpansion = false;
            return model;
        }

        PsiElement targetElement = TargetElementUtilBase.findTargetElement(editor, ImplementationSearcher.getFlags());

        if (targetElement instanceof LSFId) {

            PsiElement parent = targetElement;
            while (parent != null) {
                if (parent instanceof LSFClassDecl) {
                    model = new LSFClassInterfacesTreeModel(editor, file);
                    needTotalExpansion = false;
                    break;
                }
                parent = parent.getParent();
            }
        }

        return model;
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
                        if (!needTotalExpansion) {
                            TreeUtil.expandAll(tree);
                            needTotalExpansion = true;
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
