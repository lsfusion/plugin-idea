// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.lsfusion.lang.psi.LSFTypes.*;
import com.lsfusion.lang.psi.declarations.impl.LSFFormDeclarationImpl;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.stubs.FormStubElement;
import com.intellij.psi.stubs.IStubElementType;

public class LSFFormDeclImpl extends LSFFormDeclarationImpl implements LSFFormDecl {

  public LSFFormDeclImpl(ASTNode node) {
    super(node);
  }

  public LSFFormDeclImpl(FormStubElement stub, IStubElementType stubType) {
    super(stub, stubType);
  }

  public void accept(@NotNull LSFVisitor visitor) {
    visitor.visitFormDecl(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LSFVisitor) accept((LSFVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<LSFAutorefreshLiteral> getAutorefreshLiteralList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFAutorefreshLiteral.class);
  }

  @Override
  @NotNull
  public List<LSFImageSetting> getImageSettingList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFImageSetting.class);
  }

  @Override
  @NotNull
  public LSFSimpleNameWithCaption getSimpleNameWithCaption() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, LSFSimpleNameWithCaption.class));
  }

  @Override
  public String getDocumentation(PsiElement child) {
    return LSFPsiImplUtil.getDocumentation(this, child);
  }

}
