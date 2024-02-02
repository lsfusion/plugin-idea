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

public class LSFFormGroupObjectOptionsImpl extends ASTWrapperPsiElement implements LSFFormGroupObjectOptions {

  public LSFFormGroupObjectOptionsImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull LSFVisitor visitor) {
    visitor.visitFormGroupObjectOptions(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LSFVisitor) accept((LSFVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<LSFFormExtID> getFormExtIDList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFFormExtID.class);
  }

  @Override
  @NotNull
  public List<LSFFormExtKey> getFormExtKeyList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFFormExtKey.class);
  }

  @Override
  @NotNull
  public List<LSFFormGroupObjectBackground> getFormGroupObjectBackgroundList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFFormGroupObjectBackground.class);
  }

  @Override
  @NotNull
  public List<LSFFormGroupObjectForeground> getFormGroupObjectForegroundList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFFormGroupObjectForeground.class);
  }

  @Override
  @NotNull
  public List<LSFFormGroupObjectInitViewType> getFormGroupObjectInitViewTypeList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFFormGroupObjectInitViewType.class);
  }

  @Override
  @NotNull
  public List<LSFFormGroupObjectPageSize> getFormGroupObjectPageSizeList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFFormGroupObjectPageSize.class);
  }

  @Override
  @NotNull
  public List<LSFFormGroupObjectRelativePosition> getFormGroupObjectRelativePositionList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFFormGroupObjectRelativePosition.class);
  }

  @Override
  @NotNull
  public List<LSFFormGroupObjectUpdate> getFormGroupObjectUpdateList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFFormGroupObjectUpdate.class);
  }

  @Override
  @NotNull
  public List<LSFFormGroupObjectViewType> getFormGroupObjectViewTypeList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFFormGroupObjectViewType.class);
  }

  @Override
  @NotNull
  public List<LSFFormInGroup> getFormInGroupList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFFormInGroup.class);
  }

  @Override
  @NotNull
  public List<LSFFormSubReport> getFormSubReportList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFFormSubReport.class);
  }

  @Override
  public String getDocumentation(PsiElement child) {
    return LSFPsiImplUtil.getDocumentation(this, child);
  }

}
