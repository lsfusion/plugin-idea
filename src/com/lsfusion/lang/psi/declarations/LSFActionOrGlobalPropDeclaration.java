package com.lsfusion.lang.psi.declarations;

import com.intellij.psi.PsiReference;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import com.lsfusion.lang.psi.LSFExplicitClasses;
import com.lsfusion.lang.psi.LSFId;
import com.lsfusion.lang.psi.LSFInterfacePropStatement;
import com.lsfusion.lang.psi.LSFNonEmptyPropertyOptions;
import com.lsfusion.lang.psi.stubs.ActionOrPropStubElement;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.HashSet;
import java.util.Set;

public interface LSFActionOrGlobalPropDeclaration<This extends LSFActionOrGlobalPropDeclaration<This,Stub>, Stub extends ActionOrPropStubElement<Stub, This>> extends LSFFullNameDeclaration<This, Stub>, LSFInterfacePropStatement, LSFActionOrPropDeclaration {

    @Nullable
    LSFNonEmptyPropertyOptions getNonEmptyPropertyOptions();
    
    LSFExplicitClasses getExplicitParams();

    byte getPropType();

    String getCaption();

    Set<LSFActionOrGlobalPropDeclaration<?, ?>> getDependencies();

    default Set<LSFActionOrGlobalPropDeclaration<?, ?>> getDependents() {
        Set<LSFActionOrGlobalPropDeclaration<?, ?>> result = new HashSet<>();

        LSFId nameIdentifier = getNameIdentifier();
        if(nameIdentifier != null) {
            Set<PsiReference> refs = new HashSet<>(ReferencesSearch.search(nameIdentifier, getUseScope()).findAll());

            for (PsiReference ref : refs) {
                LSFActionOrGlobalPropDeclaration<?, ?> dependent = PsiTreeUtil.getParentOfType(ref.getElement(), LSFActionOrGlobalPropDeclaration.class);
                if (dependent != null) {
                    result.add(dependent);
                }
            }
        }

        return result;
    }

    String getPresentableText();

    default Icon getIcon() {
        return getIcon(getPropType());
    }
}
