// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.lsfusion.lang.psi.LSFTypes.*;
import com.lsfusion.lang.psi.declarations.impl.LSFNavigatorElementDeclarationImpl;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.stubs.NavigatorElementStubElement;
import com.intellij.psi.stubs.IStubElementType;

public class LSFNewNavigatorElementStatementImpl extends LSFNavigatorElementDeclarationImpl implements LSFNewNavigatorElementStatement {

  public LSFNewNavigatorElementStatementImpl(ASTNode node) {
    super(node);
  }

  public LSFNewNavigatorElementStatementImpl(NavigatorElementStubElement stub, IStubElementType stubType) {
    super(stub, stubType);
  }

  public void accept(@NotNull LSFVisitor visitor) {
    visitor.visitNewNavigatorElementStatement(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LSFVisitor) accept((LSFVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public LSFNavigatorElementDescription getNavigatorElementDescription() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, LSFNavigatorElementDescription.class));
  }

  @Override
  @Nullable
  public LSFNavigatorElementOptions getNavigatorElementOptions() {
    return PsiTreeUtil.getChildOfType(this, LSFNavigatorElementOptions.class);
  }

  @Override
  @Nullable
  public LSFNavigatorElementStatementBody getNavigatorElementStatementBody() {
    return PsiTreeUtil.getChildOfType(this, LSFNavigatorElementStatementBody.class);
  }

}
