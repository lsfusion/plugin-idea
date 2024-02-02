// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.lsfusion.lang.psi.LSFTypes.*;
import com.lsfusion.lang.psi.declarations.impl.LSFClassDeclarationImpl;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.stubs.ClassStubElement;
import com.intellij.psi.stubs.IStubElementType;

public class LSFClassDeclImpl extends LSFClassDeclarationImpl implements LSFClassDecl {

  public LSFClassDeclImpl(ASTNode node) {
    super(node);
  }

  public LSFClassDeclImpl(ClassStubElement stub, IStubElementType stubType) {
    super(stub, stubType);
  }

  public void accept(@NotNull LSFVisitor visitor) {
    visitor.visitClassDecl(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LSFVisitor) accept((LSFVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public LSFSimpleNameWithCaption getSimpleNameWithCaption() {
    return PsiTreeUtil.getChildOfType(this, LSFSimpleNameWithCaption.class);
  }

  @Override
  @Nullable
  public LSFStaticObjectImage getStaticObjectImage() {
    return PsiTreeUtil.getChildOfType(this, LSFStaticObjectImage.class);
  }

  @Override
  public String getDocumentation(PsiElement child) {
    return LSFPsiImplUtil.getDocumentation(this, child);
  }

}
