package com.lsfusion.structure;

import com.intellij.ide.structureView.StructureViewModel;
import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.TextEditorBasedStructureViewModel;
import com.intellij.ide.util.treeView.smartTree.Grouper;
import com.intellij.ide.util.treeView.smartTree.Sorter;
import com.intellij.psi.PsiFile;
import com.lsfusion.classes.LSFValueClass;
import org.jetbrains.annotations.NotNull;

public class LSFClassInterfacesTreeModel extends TextEditorBasedStructureViewModel implements StructureViewModel.ElementInfoProvider {
    private final LSFStructureTreeElementBase rootElement;

    protected LSFClassInterfacesTreeModel(PsiFile file, LSFValueClass valueClass, LSFStructureViewNavigationHandler navigationHandler) {
        super(file);
        this.rootElement = new LSFStructureTreeElementBase(file, valueClass, navigationHandler);
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
                ParameterNumberSorter.INSTANCE
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
        return rootElement;
    }
}
