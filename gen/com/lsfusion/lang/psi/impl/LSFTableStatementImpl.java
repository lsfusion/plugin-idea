// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.lsfusion.lang.psi.LSFTypes.*;
import com.lsfusion.lang.psi.declarations.impl.LSFTableDeclarationImpl;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.stubs.TableStubElement;
import com.intellij.psi.stubs.IStubElementType;

public class LSFTableStatementImpl extends LSFTableDeclarationImpl implements LSFTableStatement {

  public LSFTableStatementImpl(ASTNode node) {
    super(node);
  }

  public LSFTableStatementImpl(TableStubElement stub, IStubElementType stubType) {
    super(stub, stubType);
  }

  public void accept(@NotNull LSFVisitor visitor) {
    visitor.visitTableStatement(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LSFVisitor) accept((LSFVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public LSFClassNameList getClassNameList() {
    return PsiTreeUtil.getChildOfType(this, LSFClassNameList.class);
  }

  @Override
  @Nullable
  public LSFNoDefault getNoDefault() {
    return PsiTreeUtil.getChildOfType(this, LSFNoDefault.class);
  }

  @Override
  @NotNull
  public LSFSimpleName getSimpleName() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, LSFSimpleName.class));
  }

  @Override
  @Nullable
  public LSFStringLiteral getStringLiteral() {
    return PsiTreeUtil.getChildOfType(this, LSFStringLiteral.class);
  }

  @Override
  public String getDocumentation(PsiElement child) {
    return LSFPsiImplUtil.getDocumentation(this, child);
  }

}
