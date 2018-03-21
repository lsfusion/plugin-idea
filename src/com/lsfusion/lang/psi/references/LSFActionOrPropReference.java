package com.lsfusion.lang.psi.references;

import com.intellij.psi.PsiElement;
import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.meta.MetaTransaction;
import com.lsfusion.lang.psi.LSFCompoundID;
import com.lsfusion.lang.psi.LSFSimpleName;
import com.lsfusion.lang.psi.declarations.LSFActionOrGlobalPropDeclaration;
import com.lsfusion.lang.psi.declarations.LSFActionOrPropDeclaration;

import java.util.List;

public interface LSFActionOrPropReference<T extends LSFActionOrPropDeclaration, G extends LSFActionOrGlobalPropDeclaration> extends LSFFullNameReference<T, G> {
    boolean isDirect();

    boolean isNoContext();

    boolean isImplement();

    boolean onlyNotEquals();

    List<LSFClassSet> getExplicitClasses();

    boolean hasExplicitClasses();

    void setExplicitClasses(List<LSFClassSet> classes, MetaTransaction transaction);

    void dropExplicitClasses(MetaTransaction transaction);

    void dropFullNameRef(MetaTransaction transaction);
    
    PsiElement getWrapper();
}
