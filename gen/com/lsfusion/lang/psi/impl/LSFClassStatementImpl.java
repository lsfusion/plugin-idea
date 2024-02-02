// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.lsfusion.lang.psi.LSFTypes.*;
import com.lsfusion.lang.psi.extend.impl.LSFClassExtendImpl;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.stubs.extend.ExtendClassStubElement;
import com.intellij.psi.stubs.IStubElementType;

public class LSFClassStatementImpl extends LSFClassExtendImpl implements LSFClassStatement {

  public LSFClassStatementImpl(ASTNode node) {
    super(node);
  }

  public LSFClassStatementImpl(ExtendClassStubElement stub, IStubElementType stubType) {
    super(stub, stubType);
  }

  public void accept(@NotNull LSFVisitor visitor) {
    visitor.visitClassStatement(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LSFVisitor) accept((LSFVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public LSFClassDecl getClassDecl() {
    return PsiTreeUtil.getStubChildOfType(this, LSFClassDecl.class);
  }

  @Override
  @Nullable
  public LSFClassParentsList getClassParentsList() {
    return PsiTreeUtil.getChildOfType(this, LSFClassParentsList.class);
  }

  @Override
  @Nullable
  public LSFExtendingClassDeclaration getExtendingClassDeclaration() {
    return PsiTreeUtil.getChildOfType(this, LSFExtendingClassDeclaration.class);
  }

  @Override
  @Nullable
  public LSFStaticObjectDeclList getStaticObjectDeclList() {
    return PsiTreeUtil.getChildOfType(this, LSFStaticObjectDeclList.class);
  }

  @Override
  public String getDocumentation(PsiElement child) {
    return LSFPsiImplUtil.getDocumentation(this, child);
  }

}
