// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.lsfusion.lang.psi.LSFTypes.*;
import com.lsfusion.lang.psi.declarations.impl.LSFMetaDeclarationImpl;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.stubs.MetaStubElement;
import com.intellij.psi.stubs.IStubElementType;

public class LSFMetaCodeDeclarationStatementImpl extends LSFMetaDeclarationImpl implements LSFMetaCodeDeclarationStatement {

  public LSFMetaCodeDeclarationStatementImpl(ASTNode node) {
    super(node);
  }

  public LSFMetaCodeDeclarationStatementImpl(MetaStubElement stub, IStubElementType stubType) {
    super(stub, stubType);
  }

  public void accept(@NotNull LSFVisitor visitor) {
    visitor.visitMetaCodeDeclarationStatement(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LSFVisitor) accept((LSFVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public LSFMetaCodeDeclBody getMetaCodeDeclBody() {
    return PsiTreeUtil.getChildOfType(this, LSFMetaCodeDeclBody.class);
  }

  @Override
  @Nullable
  public LSFMetaDeclIdList getMetaDeclIdList() {
    return PsiTreeUtil.getChildOfType(this, LSFMetaDeclIdList.class);
  }

  @Override
  @NotNull
  public LSFSimpleName getSimpleName() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, LSFSimpleName.class));
  }

  @Override
  public String getDocumentation(PsiElement child) {
    return LSFPsiImplUtil.getDocumentation(this, child);
  }

}
