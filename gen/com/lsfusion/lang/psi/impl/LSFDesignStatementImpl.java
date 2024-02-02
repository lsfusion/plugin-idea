// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.lsfusion.lang.psi.LSFTypes.*;
import com.lsfusion.lang.psi.extend.impl.LSFDesignImpl;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.declarations.LSFFormDeclaration;
import javax.swing.Icon;
import com.lsfusion.lang.psi.stubs.extend.DesignStubElement;
import com.intellij.psi.stubs.IStubElementType;

public class LSFDesignStatementImpl extends LSFDesignImpl implements LSFDesignStatement {

  public LSFDesignStatementImpl(ASTNode node) {
    super(node);
  }

  public LSFDesignStatementImpl(DesignStubElement stub, IStubElementType stubType) {
    super(stub, stubType);
  }

  public void accept(@NotNull LSFVisitor visitor) {
    visitor.visitDesignStatement(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LSFVisitor) accept((LSFVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public LSFComponentBody getComponentBody() {
    return PsiTreeUtil.getChildOfType(this, LSFComponentBody.class);
  }

  @Override
  @NotNull
  public LSFDesignHeader getDesignHeader() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, LSFDesignHeader.class));
  }

  @Override
  public @Nullable Icon getIcon(int flags) {
    return LSFPsiImplUtil.getIcon(this, flags);
  }

  @Override
  public @Nullable LSFFormDeclaration resolveFormDecl() {
    return LSFPsiImplUtil.resolveFormDecl(this);
  }

  @Override
  public String getDocumentation(PsiElement child) {
    return LSFPsiImplUtil.getDocumentation(this, child);
  }

}
