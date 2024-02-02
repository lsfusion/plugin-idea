// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.lsfusion.lang.psi.LSFTypes.*;
import com.lsfusion.lang.psi.declarations.impl.LSFModuleDeclarationImpl;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.stubs.ModuleStubElement;
import com.intellij.psi.stubs.IStubElementType;

public class LSFModuleHeaderImpl extends LSFModuleDeclarationImpl implements LSFModuleHeader {

  public LSFModuleHeaderImpl(ASTNode node) {
    super(node);
  }

  public LSFModuleHeaderImpl(ModuleStubElement stub, IStubElementType stubType) {
    super(stub, stubType);
  }

  public void accept(@NotNull LSFVisitor visitor) {
    visitor.visitModuleHeader(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LSFVisitor) accept((LSFVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public LSFModuleNameStatement getModuleNameStatement() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, LSFModuleNameStatement.class));
  }

  @Override
  @Nullable
  public LSFNamespaceName getNamespaceName() {
    return PsiTreeUtil.getStubChildOfType(this, LSFNamespaceName.class);
  }

  @Override
  @Nullable
  public LSFPriorityList getPriorityList() {
    return PsiTreeUtil.getChildOfType(this, LSFPriorityList.class);
  }

  @Override
  @Nullable
  public LSFRequireList getRequireList() {
    return PsiTreeUtil.getChildOfType(this, LSFRequireList.class);
  }

  @Override
  public String getDocumentation(PsiElement child) {
    return LSFPsiImplUtil.getDocumentation(this, child);
  }

}
