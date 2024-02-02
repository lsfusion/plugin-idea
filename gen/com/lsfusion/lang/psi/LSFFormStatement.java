// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.lsfusion.lang.psi.extend.LSFFormExtend;
import com.lsfusion.lang.psi.context.FormContext;
import com.intellij.psi.StubBasedPsiElement;
import com.lsfusion.lang.psi.stubs.extend.ExtendFormStubElement;
import com.lsfusion.lang.psi.declarations.LSFFormDeclaration;

public interface LSFFormStatement extends LSFFormExtend, FormContext, StubBasedPsiElement<ExtendFormStubElement> {

  @NotNull
  List<LSFEditFormDeclaration> getEditFormDeclarationList();

  @Nullable
  LSFEmptyStatement getEmptyStatement();

  @Nullable
  LSFExtendingFormDeclaration getExtendingFormDeclaration();

  @Nullable
  LSFFormDecl getFormDecl();

  @NotNull
  List<LSFFormEventsList> getFormEventsListList();

  @NotNull
  List<LSFFormExtIDSetting> getFormExtIDSettingList();

  @NotNull
  List<LSFFormExtendFilterGroupDeclaration> getFormExtendFilterGroupDeclarationList();

  @NotNull
  List<LSFFormFilterGroupDeclaration> getFormFilterGroupDeclarationList();

  @NotNull
  List<LSFFormFiltersList> getFormFiltersListList();

  @NotNull
  List<LSFFormGroupObjectsList> getFormGroupObjectsListList();

  @NotNull
  List<LSFFormHintsList> getFormHintsListList();

  @NotNull
  List<LSFFormOrderByList> getFormOrderByListList();

  @NotNull
  List<LSFFormPivotOptionsDeclaration> getFormPivotOptionsDeclarationList();

  @NotNull
  List<LSFFormPropertiesList> getFormPropertiesListList();

  @NotNull
  List<LSFFormTreeGroupObjectList> getFormTreeGroupObjectListList();

  @NotNull
  List<LSFListFormDeclaration> getListFormDeclarationList();

  @NotNull
  List<LSFReportFilesDeclaration> getReportFilesDeclarationList();

  @NotNull
  List<LSFReportSetting> getReportSettingList();

  @NotNull
  List<LSFUserFiltersDeclaration> getUserFiltersDeclarationList();

  @Nullable LSFFormDeclaration resolveFormDecl();

}
