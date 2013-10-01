package com.simpleplugin.psi.declarations;

import com.simpleplugin.classes.LSFClassSet;

import java.util.List;

public interface LSFGroupObjectDeclaration extends LSFFormElementDeclaration {

    List<LSFClassSet> resolveClasses();

}
