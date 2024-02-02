// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.lsfusion.lang.psi.LSFTypes.*;
import com.lsfusion.lang.psi.extend.impl.LSFFormExtendImpl;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.declarations.LSFFormDeclaration;
import com.lsfusion.lang.psi.stubs.extend.ExtendFormStubElement;
import com.intellij.psi.stubs.IStubElementType;

public class LSFFormStatementImpl extends LSFFormExtendImpl implements LSFFormStatement {

  public LSFFormStatementImpl(ASTNode node) {
    super(node);
  }

  public LSFFormStatementImpl(ExtendFormStubElement stub, IStubElementType stubType) {
    super(stub, stubType);
  }

  public void accept(@NotNull LSFVisitor visitor) {
    visitor.visitFormStatement(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LSFVisitor) accept((LSFVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<LSFEditFormDeclaration> getEditFormDeclarationList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFEditFormDeclaration.class);
  }

  @Override
  @Nullable
  public LSFEmptyStatement getEmptyStatement() {
    return PsiTreeUtil.getChildOfType(this, LSFEmptyStatement.class);
  }

  @Override
  @Nullable
  public LSFExtendingFormDeclaration getExtendingFormDeclaration() {
    return PsiTreeUtil.getChildOfType(this, LSFExtendingFormDeclaration.class);
  }

  @Override
  @Nullable
  public LSFFormDecl getFormDecl() {
    return PsiTreeUtil.getStubChildOfType(this, LSFFormDecl.class);
  }

  @Override
  @NotNull
  public List<LSFFormEventsList> getFormEventsListList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFFormEventsList.class);
  }

  @Override
  @NotNull
  public List<LSFFormExtIDSetting> getFormExtIDSettingList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFFormExtIDSetting.class);
  }

  @Override
  @NotNull
  public List<LSFFormExtendFilterGroupDeclaration> getFormExtendFilterGroupDeclarationList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFFormExtendFilterGroupDeclaration.class);
  }

  @Override
  @NotNull
  public List<LSFFormFilterGroupDeclaration> getFormFilterGroupDeclarationList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFFormFilterGroupDeclaration.class);
  }

  @Override
  @NotNull
  public List<LSFFormFiltersList> getFormFiltersListList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFFormFiltersList.class);
  }

  @Override
  @NotNull
  public List<LSFFormGroupObjectsList> getFormGroupObjectsListList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFFormGroupObjectsList.class);
  }

  @Override
  @NotNull
  public List<LSFFormHintsList> getFormHintsListList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFFormHintsList.class);
  }

  @Override
  @NotNull
  public List<LSFFormOrderByList> getFormOrderByListList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFFormOrderByList.class);
  }

  @Override
  @NotNull
  public List<LSFFormPivotOptionsDeclaration> getFormPivotOptionsDeclarationList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFFormPivotOptionsDeclaration.class);
  }

  @Override
  @NotNull
  public List<LSFFormPropertiesList> getFormPropertiesListList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFFormPropertiesList.class);
  }

  @Override
  @NotNull
  public List<LSFFormTreeGroupObjectList> getFormTreeGroupObjectListList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFFormTreeGroupObjectList.class);
  }

  @Override
  @NotNull
  public List<LSFListFormDeclaration> getListFormDeclarationList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFListFormDeclaration.class);
  }

  @Override
  @NotNull
  public List<LSFReportFilesDeclaration> getReportFilesDeclarationList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFReportFilesDeclaration.class);
  }

  @Override
  @NotNull
  public List<LSFReportSetting> getReportSettingList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFReportSetting.class);
  }

  @Override
  @NotNull
  public List<LSFUserFiltersDeclaration> getUserFiltersDeclarationList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFUserFiltersDeclaration.class);
  }

  @Override
  public @Nullable LSFFormDeclaration resolveFormDecl() {
    return LSFPsiImplUtil.resolveFormDecl(this);
  }

}
