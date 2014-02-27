package com.lsfusion.lang.psi.declarations;

import com.lsfusion.lang.classes.LSFClassSet;

import java.util.List;

public interface LSFGroupObjectDeclaration extends LSFFormElementDeclaration {

    List<LSFClassSet> resolveClasses();

}
