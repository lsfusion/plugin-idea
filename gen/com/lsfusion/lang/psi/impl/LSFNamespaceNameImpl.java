// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.lsfusion.lang.psi.LSFTypes.*;
import com.lsfusion.lang.psi.declarations.impl.LSFExplicitNamespaceDeclarationImpl;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.stubs.ExplicitNamespaceStubElement;
import com.intellij.psi.stubs.IStubElementType;

public class LSFNamespaceNameImpl extends LSFExplicitNamespaceDeclarationImpl implements LSFNamespaceName {

  public LSFNamespaceNameImpl(ASTNode node) {
    super(node);
  }

  public LSFNamespaceNameImpl(ExplicitNamespaceStubElement stub, IStubElementType stubType) {
    super(stub, stubType);
  }

  public void accept(@NotNull LSFVisitor visitor) {
    visitor.visitNamespaceName(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LSFVisitor) accept((LSFVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public LSFNamespaceUsage getNamespaceUsage() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, LSFNamespaceUsage.class));
  }

}
