// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.lsfusion.lang.psi.declarations.LSFFormDeclaration;
import com.intellij.psi.StubBasedPsiElement;
import com.lsfusion.lang.psi.stubs.FormStubElement;

public interface LSFFormDecl extends LSFFormDeclaration, StubBasedPsiElement<FormStubElement> {

  @NotNull
  List<LSFAutorefreshLiteral> getAutorefreshLiteralList();

  @NotNull
  List<LSFImageSetting> getImageSettingList();

  @NotNull
  LSFSimpleNameWithCaption getSimpleNameWithCaption();

  String getDocumentation(PsiElement child);

}
