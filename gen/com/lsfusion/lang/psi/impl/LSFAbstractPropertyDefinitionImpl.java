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
import com.intellij.openapi.util.Pair;
import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.psi.declarations.LSFParamDeclaration;
import com.lsfusion.lang.typeinfer.LSFExClassSet;
import java.util.Map;

public class LSFAbstractPropertyDefinitionImpl extends ASTWrapperPsiElement implements LSFAbstractPropertyDefinition {

  public LSFAbstractPropertyDefinitionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull LSFVisitor visitor) {
    visitor.visitAbstractPropertyDefinition(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LSFVisitor) accept((LSFVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public LSFAbstractExclusiveOverrideOption getAbstractExclusiveOverrideOption() {
    return PsiTreeUtil.getChildOfType(this, LSFAbstractExclusiveOverrideOption.class);
  }

  @Override
  @Nullable
  public LSFClassName getClassName() {
    return PsiTreeUtil.getChildOfType(this, LSFClassName.class);
  }

  @Override
  @Nullable
  public LSFClassNameList getClassNameList() {
    return PsiTreeUtil.getChildOfType(this, LSFClassNameList.class);
  }

  @Override
  public @Nullable LSFExClassSet resolveUnfriendValueClass(boolean infer) {
    return LSFPsiImplUtil.resolveUnfriendValueClass(this, infer);
  }

  @Override
  public @Nullable List<LSFExClassSet> resolveValueParamClasses(List<LSFParamDeclaration> declareParams) {
    return LSFPsiImplUtil.resolveValueParamClasses(this, declareParams);
  }

  @Override
  public @NotNull Pair<List<LSFParamDeclaration>, Map<PsiElement, Pair<LSFClassSet, LSFClassSet>>> checkValueParamClasses(PsiElement singleElement, List<LSFParamDeclaration> declareParams) {
    return LSFPsiImplUtil.checkValueParamClasses(this, singleElement, declareParams);
  }

  @Override
  public @NotNull Pair<List<LSFParamDeclaration>, Map<PsiElement, Pair<LSFClassSet, LSFClassSet>>> checkValueParamClasses(LSFClassNameList classNameList, List<LSFParamDeclaration> declareParams) {
    return LSFPsiImplUtil.checkValueParamClasses(this, classNameList, declareParams);
  }

  @Override
  public @NotNull Pair<List<LSFParamDeclaration>, Map<PsiElement, Pair<LSFClassSet, LSFClassSet>>> checkValueParamClasses(List<? extends PsiElement> elements, List<LSFParamDeclaration> declareParams) {
    return LSFPsiImplUtil.checkValueParamClasses(this, elements, declareParams);
  }

  @Override
  public @NotNull Pair<List<LSFParamDeclaration>, Map<PsiElement, Pair<LSFClassSet, LSFClassSet>>> checkValueParamClasses(List<LSFParamDeclaration> declareParams) {
    return LSFPsiImplUtil.checkValueParamClasses(this, declareParams);
  }

  @Override
  public LSFExplicitClasses getValueParamClassNames() {
    return LSFPsiImplUtil.getValueParamClassNames(this);
  }

  @Override
  public List<String> getValueClassNames() {
    return LSFPsiImplUtil.getValueClassNames(this);
  }

  @Override
  public List<String> getValuePropertyNames() {
    return LSFPsiImplUtil.getValuePropertyNames(this);
  }

  @Override
  public String getDocumentation(PsiElement child) {
    return LSFPsiImplUtil.getDocumentation(this, child);
  }

}
