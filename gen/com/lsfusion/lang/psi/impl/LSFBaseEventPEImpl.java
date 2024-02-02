// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.lsfusion.lang.psi.LSFTypes.*;
import com.lsfusion.lang.psi.declarations.impl.LSFBaseEventActionDeclarationImpl;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.stubs.BaseEventActionStubElement;
import com.intellij.psi.stubs.IStubElementType;

public class LSFBaseEventPEImpl extends LSFBaseEventActionDeclarationImpl implements LSFBaseEventPE {

  public LSFBaseEventPEImpl(ASTNode node) {
    super(node);
  }

  public LSFBaseEventPEImpl(BaseEventActionStubElement stub, IStubElementType stubType) {
    super(stub, stubType);
  }

  public void accept(@NotNull LSFVisitor visitor) {
    visitor.visitBaseEventPE(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LSFVisitor) accept((LSFVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public LSFBaseEvent getBaseEvent() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, LSFBaseEvent.class));
  }

  @Override
  @Nullable
  public LSFParamDeclare getParamDeclare() {
    return PsiTreeUtil.getChildOfType(this, LSFParamDeclare.class);
  }

}
