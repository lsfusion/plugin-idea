// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.lsfusion.lang.psi.declarations.LSFGroupDeclaration;
import com.intellij.psi.StubBasedPsiElement;
import com.lsfusion.lang.psi.stubs.GroupStubElement;

public interface LSFGroupStatement extends LSFGroupDeclaration, StubBasedPsiElement<GroupStubElement> {

  @Nullable
  LSFFormExtID getFormExtID();

  @Nullable
  LSFGroupUsage getGroupUsage();

  @Nullable
  LSFNativeLiteral getNativeLiteral();

  @Nullable
  LSFSimpleNameWithCaption getSimpleNameWithCaption();

  String getDocumentation(PsiElement child);

}
