package com.lsfusion.lang.psi.references;

import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.meta.MetaTransaction;
import com.lsfusion.lang.psi.declarations.LSFGlobalPropDeclaration;
import com.lsfusion.lang.psi.declarations.LSFPropDeclaration;

import java.util.List;

public interface LSFPropReference extends LSFFullNameReference<LSFPropDeclaration, LSFGlobalPropDeclaration> {

    boolean isDirect();

    boolean isNoContext();

    boolean isImplement();

    boolean onlyNotEquals();

    List<LSFClassSet> getExplicitClasses();

    boolean hasExplicitClasses();

    void setExplicitClasses(List<LSFClassSet> classes, MetaTransaction transaction);

    void dropExplicitClasses(MetaTransaction transaction);

    void dropFullNameRef(MetaTransaction transaction);
}
