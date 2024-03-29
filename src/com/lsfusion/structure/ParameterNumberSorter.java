package com.lsfusion.structure;

import com.intellij.ide.util.treeView.smartTree.ActionPresentation;
import com.intellij.ide.util.treeView.smartTree.ActionPresentationData;
import com.intellij.ide.util.treeView.smartTree.Sorter;
import com.lsfusion.LSFIcons;
import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.psi.declarations.LSFActionOrPropDeclaration;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;

public class ParameterNumberSorter implements Sorter {
    public static final ParameterNumberSorter INSTANCE = new ParameterNumberSorter();

    @Override
    public Comparator getComparator() {
        return (o1, o2) -> {
            if (o1 instanceof LSFActionOrPropertyStatementTreeElement && o2 instanceof LSFActionOrPropertyStatementTreeElement) {
                LSFActionOrPropDeclaration el1 = ((LSFActionOrPropertyStatementTreeElement<?>) o1).getElement();
                LSFActionOrPropDeclaration el2 = ((LSFActionOrPropertyStatementTreeElement<?>) o2).getElement();
                String class1Name = ((LSFActionOrPropertyStatementTreeElement) o1).getClassName();
                String class2Name = ((LSFActionOrPropertyStatementTreeElement) o2).getClassName();

                List<LSFClassSet> classes1 = el1.resolveParamClasses();
                int index1 = 0;
                for (; index1 < classes1.size(); index1++) {
                    LSFClassSet paramClass1 = classes1.get(index1);
                    if (paramClass1 != null && class1Name.equals(paramClass1.toString())) {
                        break;
                    }
                }
                List<LSFClassSet> classes2 = el2.resolveParamClasses();
                int index2 = 0;
                for (; index2 < classes2.size(); index2++) {
                    LSFClassSet paramClass2 = classes2.get(index2);
                    if (paramClass2 != null && class2Name.equals(paramClass2.toString())) {
                        break;
                    }
                }
                return index1 == index2 ? classes1.size() - classes2.size() : index1 - index2;
            }
            return 0;
        };
    }

    @Override
    public boolean isVisible() {
        return true;
    }

    @NotNull
    @Override
    public ActionPresentation getPresentation() {
        return new ActionPresentationData("Sort by Parameter Number", null, LSFIcons.NUMBER_SORT);
    }

    @NotNull
    @Override
    public String getName() {
        return "NUMBER_SORTER";
    }
}
