package com.lsfusion.psi.declarations;

import com.lsfusion.classes.LSFClassSet;

import java.util.List;

public interface LSFGroupObjectDeclaration extends LSFFormElementDeclaration {

    List<LSFClassSet> resolveClasses();

}
