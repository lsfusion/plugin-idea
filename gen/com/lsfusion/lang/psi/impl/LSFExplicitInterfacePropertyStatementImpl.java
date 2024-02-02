// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.lsfusion.lang.psi.LSFTypes.*;
import com.lsfusion.lang.psi.declarations.impl.LSFExplicitInterfacePropStatementImpl;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.stubs.interfaces.ExplicitInterfacePropStubElement;
import com.intellij.psi.stubs.IStubElementType;

public class LSFExplicitInterfacePropertyStatementImpl extends LSFExplicitInterfacePropStatementImpl implements LSFExplicitInterfacePropertyStatement {

  public LSFExplicitInterfacePropertyStatementImpl(ASTNode node) {
    super(node);
  }

  public LSFExplicitInterfacePropertyStatementImpl(ExplicitInterfacePropStubElement stub, IStubElementType stubType) {
    super(stub, stubType);
  }

  public void accept(@NotNull LSFVisitor visitor) {
    visitor.visitExplicitInterfacePropertyStatement(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LSFVisitor) accept((LSFVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public LSFExplicitValuePropertyStatement getExplicitValuePropertyStatement() {
    return notNullChild(PsiTreeUtil.getStubChildOfType(this, LSFExplicitValuePropertyStatement.class));
  }

}
