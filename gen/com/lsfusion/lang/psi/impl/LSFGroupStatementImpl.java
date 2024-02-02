// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.lsfusion.lang.psi.LSFTypes.*;
import com.lsfusion.lang.psi.declarations.impl.LSFGroupDeclarationImpl;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.stubs.GroupStubElement;
import com.intellij.psi.stubs.IStubElementType;

public class LSFGroupStatementImpl extends LSFGroupDeclarationImpl implements LSFGroupStatement {

  public LSFGroupStatementImpl(ASTNode node) {
    super(node);
  }

  public LSFGroupStatementImpl(GroupStubElement stub, IStubElementType stubType) {
    super(stub, stubType);
  }

  public void accept(@NotNull LSFVisitor visitor) {
    visitor.visitGroupStatement(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LSFVisitor) accept((LSFVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public LSFFormExtID getFormExtID() {
    return PsiTreeUtil.getChildOfType(this, LSFFormExtID.class);
  }

  @Override
  @Nullable
  public LSFGroupUsage getGroupUsage() {
    return PsiTreeUtil.getChildOfType(this, LSFGroupUsage.class);
  }

  @Override
  @Nullable
  public LSFNativeLiteral getNativeLiteral() {
    return PsiTreeUtil.getChildOfType(this, LSFNativeLiteral.class);
  }

  @Override
  @Nullable
  public LSFSimpleNameWithCaption getSimpleNameWithCaption() {
    return PsiTreeUtil.getChildOfType(this, LSFSimpleNameWithCaption.class);
  }

  @Override
  public String getDocumentation(PsiElement child) {
    return LSFPsiImplUtil.getDocumentation(this, child);
  }

}
