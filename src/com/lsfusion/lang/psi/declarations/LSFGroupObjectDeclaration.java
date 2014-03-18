package com.lsfusion.lang.psi.declarations;

import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.psi.LSFFormObjectDeclaration;

import java.util.Collection;
import java.util.List;

public interface LSFGroupObjectDeclaration extends LSFFormElementDeclaration {

    List<LSFClassSet> resolveClasses();

    Collection<LSFFormObjectDeclaration> findObjectDecarations();

}
