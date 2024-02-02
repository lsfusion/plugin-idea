// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.lsfusion.lang.psi.LSFTypes.*;
import com.lsfusion.lang.psi.declarations.impl.LSFImportFieldParamDeclartionImpl;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.classes.LSFValueClass;
import com.lsfusion.lang.meta.MetaTransaction;

public class LSFImportFieldDefinitionImpl extends LSFImportFieldParamDeclartionImpl implements LSFImportFieldDefinition {

  public LSFImportFieldDefinitionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull LSFVisitor visitor) {
    visitor.visitImportFieldDefinition(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LSFVisitor) accept((LSFVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public LSFBuiltInClassName getBuiltInClassName() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, LSFBuiltInClassName.class));
  }

  @Override
  @Nullable
  public LSFImportFieldAlias getImportFieldAlias() {
    return PsiTreeUtil.getChildOfType(this, LSFImportFieldAlias.class);
  }

  @Override
  @NotNull
  public LSFImportFieldName getImportFieldName() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, LSFImportFieldName.class));
  }

  @Override
  public @Nullable LSFClassSet resolveClass() {
    return LSFPsiImplUtil.resolveClass(this);
  }

  @Override
  public void ensureClass(@NotNull LSFValueClass valueClass, MetaTransaction metaTrans) {
    LSFPsiImplUtil.ensureClass(this, valueClass, metaTrans);
  }

}
