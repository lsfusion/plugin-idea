// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.lsfusion.lang.psi.declarations.LSFExplicitValuePropStatement;
import com.intellij.psi.StubBasedPsiElement;
import com.lsfusion.lang.psi.stubs.interfaces.ExplicitValueStubElement;

public interface LSFExplicitValuePropertyStatement extends LSFExplicitValuePropStatement, StubBasedPsiElement<ExplicitValueStubElement> {

  @NotNull
  LSFImplicitValuePropertyStatement getImplicitValuePropertyStatement();

}
