// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.lsfusion.lang.psi.LSFTypes.*;
import com.lsfusion.lang.psi.declarations.impl.LSFAggrParamGlobalPropDeclarationImpl;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.stubs.AggrParamPropStubElement;
import com.intellij.psi.stubs.IStubElementType;

public class LSFAggrParamPropDeclareImpl extends LSFAggrParamGlobalPropDeclarationImpl implements LSFAggrParamPropDeclare {

  public LSFAggrParamPropDeclareImpl(ASTNode node) {
    super(node);
  }

  public LSFAggrParamPropDeclareImpl(AggrParamPropStubElement stub, IStubElementType stubType) {
    super(stub, stubType);
  }

  public void accept(@NotNull LSFVisitor visitor) {
    visitor.visitAggrParamPropDeclare(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LSFVisitor) accept((LSFVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public LSFClassName getClassName() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, LSFClassName.class));
  }

  @Override
  @NotNull
  public LSFParamDeclare getParamDeclare() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, LSFParamDeclare.class));
  }

}
