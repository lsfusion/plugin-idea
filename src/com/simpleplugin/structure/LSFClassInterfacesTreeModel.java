package com.simpleplugin.structure;

import com.intellij.codeInsight.TargetElementUtilBase;
import com.intellij.codeInsight.navigation.ImplementationSearcher;
import com.intellij.ide.structureView.StructureViewModel;
import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.TextEditorBasedStructureViewModel;
import com.intellij.ide.util.treeView.smartTree.Grouper;
import com.intellij.ide.util.treeView.smartTree.Sorter;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.simpleplugin.psi.LSFClassDecl;
import com.simpleplugin.psi.LSFId;
import org.jetbrains.annotations.NotNull;

public class LSFClassInterfacesTreeModel extends TextEditorBasedStructureViewModel implements StructureViewModel.ElementInfoProvider {
    private LSFStructureTreeElementBase rootElement;

    protected LSFClassInterfacesTreeModel(PsiFile file) {
        super(file);
    }

    @Override
    public boolean isAlwaysShowsPlus(StructureViewTreeElement element) {
        return false;
    }

    @Override
    public boolean isAlwaysLeaf(StructureViewTreeElement element) {
        return element instanceof LSFPropertyStatementTreeElement;
    }

    @NotNull
    @Override
    public Sorter[] getSorters() {
        return new Sorter[]{
                ParameterNumberSorter.INSTANCE,
                Sorter.ALPHA_SORTER
        };
    }

    @NotNull
    @Override
    public Grouper[] getGroupers() {
        return new Grouper[]{
                new ExtendedClassGrouper(),
                new ModuleGrouper()
        };
    }

    @NotNull
    @Override
    public StructureViewTreeElement getRoot() {
        if (rootElement == null) {
            PsiElement targetElement = TargetElementUtilBase.findTargetElement(getEditor(), ImplementationSearcher.getFlags());

            if (targetElement instanceof LSFId) {

                PsiElement parent = targetElement;
                while (parent.getParent() != null) {
                    if (parent instanceof LSFClassDecl) {
                        rootElement = new LSFStructureTreeElementBase(parent);
                        return rootElement;
                    }
                    parent = parent.getParent();
                }

            }

            rootElement = new LSFStructureTreeElementBase(getPsiFile());

        }
        return rootElement;
    }
}
