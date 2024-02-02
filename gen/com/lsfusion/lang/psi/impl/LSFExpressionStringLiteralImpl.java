// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.lsfusion.lang.psi.LSFTypes.*;
import com.lsfusion.lang.psi.LSFExpressionStringValueLiteralImpl;
import com.lsfusion.lang.psi.*;

public class LSFExpressionStringLiteralImpl extends LSFExpressionStringValueLiteralImpl implements LSFExpressionStringLiteral {

  public LSFExpressionStringLiteralImpl(ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull LSFVisitor visitor) {
    visitor.visitExpressionStringLiteral(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LSFVisitor) accept((LSFVisitor)visitor);
    else super.accept(visitor);
  }

}
