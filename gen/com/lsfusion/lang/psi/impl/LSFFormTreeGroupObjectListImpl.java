// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.lsfusion.lang.psi.LSFTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.lsfusion.lang.psi.*;

public class LSFFormTreeGroupObjectListImpl extends ASTWrapperPsiElement implements LSFFormTreeGroupObjectList {

  public LSFFormTreeGroupObjectListImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull LSFVisitor visitor) {
    visitor.visitFormTreeGroupObjectList(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LSFVisitor) accept((LSFVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<LSFFormTreeGroupObjectDeclaration> getFormTreeGroupObjectDeclarationList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFFormTreeGroupObjectDeclaration.class);
  }

  @Override
  @Nullable
  public LSFFormTreeGroupObjectOptions getFormTreeGroupObjectOptions() {
    return PsiTreeUtil.getChildOfType(this, LSFFormTreeGroupObjectOptions.class);
  }

  @Override
  @Nullable
  public LSFTreeGroupDeclaration getTreeGroupDeclaration() {
    return PsiTreeUtil.getChildOfType(this, LSFTreeGroupDeclaration.class);
  }

  @Override
  public String getDocumentation(PsiElement child) {
    return LSFPsiImplUtil.getDocumentation(this, child);
  }

}
