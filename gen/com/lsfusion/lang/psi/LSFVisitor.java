// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiElement;
import com.lsfusion.lang.psi.context.ActionExpression;
import com.lsfusion.lang.psi.context.FormContext;
import com.lsfusion.lang.psi.context.ModifyParamContext;
import com.lsfusion.lang.psi.context.ClassParamDeclareContext;
import com.lsfusion.lang.psi.context.UnfriendlyPE;
import com.lsfusion.lang.psi.context.ExtendParamContext;
import com.lsfusion.lang.psi.references.LSFPropertyDrawReference;
import com.lsfusion.lang.psi.references.LSFPropElseActionReference;
import com.lsfusion.lang.psi.declarations.LSFModuleDeclaration;
import com.lsfusion.lang.psi.context.PropertyUsageContext;
import com.lsfusion.documentation.LSFDocumentation;
import com.lsfusion.lang.psi.context.LSFExpression;
import com.lsfusion.lang.psi.references.LSFFormReference;
import com.lsfusion.lang.psi.references.LSFTreeGroupReference;
import com.lsfusion.lang.psi.context.ExtendDoParamContext;
import com.lsfusion.lang.psi.references.LSFFormElseNoParamsActionReference;
import com.lsfusion.lang.psi.declarations.LSFObjectInputParamDeclaration;
import com.lsfusion.lang.psi.declarations.LSFLocalPropDeclaration;
import com.lsfusion.lang.psi.references.LSFActionReference;
import com.lsfusion.lang.psi.references.LSFMetaReference;
import com.lsfusion.lang.psi.declarations.LSFPropertyDrawMappedDeclaration;
import com.lsfusion.lang.psi.references.LSFStaticObjectReference;
import com.lsfusion.lang.psi.references.LSFNavigatorElementReference;
import com.lsfusion.lang.psi.extend.LSFDesign;
import com.lsfusion.lang.psi.declarations.LSFTreeGroupDecl;
import com.lsfusion.lang.psi.declarations.LSFParamDeclaration;
import com.lsfusion.lang.psi.declarations.LSFAggrParamGlobalPropDeclaration;
import com.lsfusion.lang.psi.declarations.LSFBaseEventActionDeclaration;
import com.lsfusion.lang.psi.declarations.LSFClassDeclaration;
import com.lsfusion.lang.psi.declarations.LSFExplicitInterfaceActionStatement;
import com.lsfusion.lang.psi.declarations.LSFGroupObjectDeclaration;
import com.lsfusion.lang.psi.declarations.LSFMetaDeclaration;
import com.lsfusion.lang.psi.references.LSFGroupReference;
import com.lsfusion.lang.psi.declarations.LSFExplicitInterfacePropStatement;
import com.lsfusion.lang.psi.declarations.LSFGroupDeclaration;
import com.lsfusion.lang.psi.declarations.LSFStatementActionDeclaration;
import com.intellij.psi.PsiNamedElement;
import com.lsfusion.lang.psi.references.LSFWindowReference;
import com.lsfusion.lang.psi.references.LSFExprParamReference;
import com.lsfusion.lang.psi.declarations.LSFFilterGroupDeclaration;
import com.lsfusion.lang.psi.references.LSFFilterGroupReference;
import com.lsfusion.lang.psi.declarations.LSFNavigatorElementDeclaration;
import com.lsfusion.lang.psi.references.LSFNamespaceReference;
import com.lsfusion.lang.psi.references.LSFClassReference;
import com.lsfusion.lang.psi.references.LSFPropReference;
import com.lsfusion.lang.psi.declarations.LSFImplicitInterfacePropStatement;
import com.lsfusion.lang.psi.declarations.LSFStaticObjectDeclaration;
import com.lsfusion.lang.psi.declarations.LSFExplicitNamespaceDeclaration;
import com.lsfusion.lang.psi.references.LSFModuleReference;
import com.lsfusion.lang.psi.declarations.LSFExplicitValuePropStatement;
import com.lsfusion.lang.psi.references.LSFJavaClassStringReference;
import com.lsfusion.lang.psi.declarations.LSFComponentDeclaration;
import com.lsfusion.lang.psi.references.LSFObjectReference;
import com.lsfusion.lang.psi.declarations.LSFPropertyDrawNameDeclaration;
import com.lsfusion.lang.psi.declarations.LSFObjectDeclaration;
import com.lsfusion.lang.psi.references.LSFGroupObjectReference;
import com.lsfusion.lang.psi.declarations.LSFImplicitValuePropStatement;
import com.lsfusion.lang.psi.references.LSFComponentReference;
import com.lsfusion.lang.psi.declarations.LSFTableDeclaration;
import com.lsfusion.lang.psi.extend.LSFFormExtend;
import com.lsfusion.lang.psi.declarations.LSFStatementGlobalPropDeclaration;
import com.lsfusion.lang.psi.references.LSFTableReference;
import com.lsfusion.lang.psi.declarations.LSFFormDeclaration;
import com.lsfusion.lang.psi.declarations.LSFWindowDeclaration;
import com.lsfusion.lang.psi.extend.LSFClassExtend;
import com.lsfusion.lang.psi.declarations.LSFImportFieldParamDeclaration;

public class LSFVisitor extends PsiElementVisitor {

  public void visitAbstractActionPropertyDefinition(@NotNull LSFAbstractActionPropertyDefinition o) {
    visitUnfriendlyPE(o);
  }

  public void visitAbstractCaseAddOption(@NotNull LSFAbstractCaseAddOption o) {
    visitPsiElement(o);
  }

  public void visitAbstractExclusiveOverrideOption(@NotNull LSFAbstractExclusiveOverrideOption o) {
    visitDocumentation(o);
  }

  public void visitAbstractPropertyDefinition(@NotNull LSFAbstractPropertyDefinition o) {
    visitUnfriendlyPE(o);
  }

  public void visitActionCaseBranchBody(@NotNull LSFActionCaseBranchBody o) {
    visitPsiElement(o);
  }

  public void visitActionPropertyDefinitionBody(@NotNull LSFActionPropertyDefinitionBody o) {
    visitActionExpression(o);
  }

  public void visitActionStatement(@NotNull LSFActionStatement o) {
    visitInterfacePropStatement(o);
  }

  public void visitActionUnfriendlyPD(@NotNull LSFActionUnfriendlyPD o) {
    visitPsiElement(o);
  }

  public void visitActionUsage(@NotNull LSFActionUsage o) {
    visitActionReference(o);
  }

  public void visitActionUsageWrapper(@NotNull LSFActionUsageWrapper o) {
    visitPsiElement(o);
  }

  public void visitActivateActionPropertyDefinitionBody(@NotNull LSFActivateActionPropertyDefinitionBody o) {
    visitActionExpression(o);
  }

  public void visitActiveFormActionPropertyDefinitionBody(@NotNull LSFActiveFormActionPropertyDefinitionBody o) {
    visitActionExpression(o);
  }

  public void visitActiveTabPropertyDefinition(@NotNull LSFActiveTabPropertyDefinition o) {
    visitExpression(o);
  }

  public void visitAdditiveORPE(@NotNull LSFAdditiveORPE o) {
    visitExpression(o);
  }

  public void visitAdditivePE(@NotNull LSFAdditivePE o) {
    visitExpression(o);
  }

  public void visitAggrParamPropDeclare(@NotNull LSFAggrParamPropDeclare o) {
    visitAggrParamGlobalPropDeclaration(o);
  }

  public void visitAggrPropertyDefinition(@NotNull LSFAggrPropertyDefinition o) {
    visitUnfriendlyPE(o);
  }

  public void visitAggrSetting(@NotNull LSFAggrSetting o) {
    visitPsiElement(o);
  }

  public void visitAliasUsage(@NotNull LSFAliasUsage o) {
    visitPsiElement(o);
  }

  public void visitAliasedPropertyExpression(@NotNull LSFAliasedPropertyExpression o) {
    visitPsiElement(o);
  }

  public void visitAlignmentLiteral(@NotNull LSFAlignmentLiteral o) {
    visitPsiElement(o);
  }

  public void visitAndPE(@NotNull LSFAndPE o) {
    visitExpression(o);
  }

  public void visitApplyActionPropertyDefinitionBody(@NotNull LSFApplyActionPropertyDefinitionBody o) {
    visitActionExpression(o);
  }

  public void visitAsEditActionSetting(@NotNull LSFAsEditActionSetting o) {
    visitPsiElement(o);
  }

  public void visitAspectAfter(@NotNull LSFAspectAfter o) {
    visitDocumentation(o);
  }

  public void visitAspectBefore(@NotNull LSFAspectBefore o) {
    visitDocumentation(o);
  }

  public void visitAspectStatement(@NotNull LSFAspectStatement o) {
    visitModifyParamContext(o);
  }

  public void visitAssignActionPropertyDefinitionBody(@NotNull LSFAssignActionPropertyDefinitionBody o) {
    visitActionExpression(o);
  }

  public void visitAsyncUpdateActionPropertyDefinitionBody(@NotNull LSFAsyncUpdateActionPropertyDefinitionBody o) {
    visitActionExpression(o);
  }

  public void visitAutorefreshLiteral(@NotNull LSFAutorefreshLiteral o) {
    visitPsiElement(o);
  }

  public void visitAutosetSetting(@NotNull LSFAutosetSetting o) {
    visitPsiElement(o);
  }

  public void visitBaseEvent(@NotNull LSFBaseEvent o) {
    visitPsiElement(o);
  }

  public void visitBaseEventNotPE(@NotNull LSFBaseEventNotPE o) {
    visitBaseEventActionDeclaration(o);
  }

  public void visitBaseEventPE(@NotNull LSFBaseEventPE o) {
    visitBaseEventActionDeclaration(o);
  }

  public void visitBooleanLiteral(@NotNull LSFBooleanLiteral o) {
    visitPsiElement(o);
  }

  public void visitBorderPosition(@NotNull LSFBorderPosition o) {
    visitPsiElement(o);
  }

  public void visitBoundsDoubleLiteral(@NotNull LSFBoundsDoubleLiteral o) {
    visitPsiElement(o);
  }

  public void visitBoundsIntLiteral(@NotNull LSFBoundsIntLiteral o) {
    visitPsiElement(o);
  }

  public void visitBracketedClassNameList(@NotNull LSFBracketedClassNameList o) {
    visitPsiElement(o);
  }

  public void visitBreakActionOperator(@NotNull LSFBreakActionOperator o) {
    visitDocumentation(o);
  }

  public void visitBuiltInClassName(@NotNull LSFBuiltInClassName o) {
    visitId(o);
  }

  public void visitCancelActionPropertyDefinitionBody(@NotNull LSFCancelActionPropertyDefinitionBody o) {
    visitActionExpression(o);
  }

  public void visitCaseActionPropertyDefinitionBody(@NotNull LSFCaseActionPropertyDefinitionBody o) {
    visitActionExpression(o);
  }

  public void visitCaseBranchBody(@NotNull LSFCaseBranchBody o) {
    visitPsiElement(o);
  }

  public void visitCasePropertyDefinition(@NotNull LSFCasePropertyDefinition o) {
    visitExpression(o);
  }

  public void visitCastPropertyDefinition(@NotNull LSFCastPropertyDefinition o) {
    visitExpression(o);
  }

  public void visitChangeClassActionPropertyDefinitionBody(@NotNull LSFChangeClassActionPropertyDefinitionBody o) {
    visitActionExpression(o);
  }

  public void visitChangeClassWhere(@NotNull LSFChangeClassWhere o) {
    visitPsiElement(o);
  }

  public void visitChangeInput(@NotNull LSFChangeInput o) {
    visitPsiElement(o);
  }

  public void visitChangeInputPropertyCustomView(@NotNull LSFChangeInputPropertyCustomView o) {
    visitPsiElement(o);
  }

  public void visitChangeKeySetting(@NotNull LSFChangeKeySetting o) {
    visitPsiElement(o);
  }

  public void visitChangeMouseSetting(@NotNull LSFChangeMouseSetting o) {
    visitPsiElement(o);
  }

  public void visitChangePropertyBody(@NotNull LSFChangePropertyBody o) {
    visitPropertyUsageContext(o);
  }

  public void visitChangePropertyCustomView(@NotNull LSFChangePropertyCustomView o) {
    visitPsiElement(o);
  }

  public void visitCharWidthSetting(@NotNull LSFCharWidthSetting o) {
    visitPsiElement(o);
  }

  public void visitClassDecl(@NotNull LSFClassDecl o) {
    visitClassDeclaration(o);
  }

  public void visitClassName(@NotNull LSFClassName o) {
    visitPsiElement(o);
  }

  public void visitClassNameList(@NotNull LSFClassNameList o) {
    visitPsiElement(o);
  }

  public void visitClassOrExpression(@NotNull LSFClassOrExpression o) {
    visitPsiElement(o);
  }

  public void visitClassParamDeclare(@NotNull LSFClassParamDeclare o) {
    visitClassParamDeclareContext(o);
  }

  public void visitClassParamDeclareList(@NotNull LSFClassParamDeclareList o) {
    visitPsiElement(o);
  }

  public void visitClassParentsList(@NotNull LSFClassParentsList o) {
    visitPsiElement(o);
  }

  public void visitClassStatement(@NotNull LSFClassStatement o) {
    visitClassExtend(o);
  }

  public void visitClassViewType(@NotNull LSFClassViewType o) {
    visitPsiElement(o);
  }

  public void visitCloseFormActionPropertyDefinitionBody(@NotNull LSFCloseFormActionPropertyDefinitionBody o) {
    visitActionExpression(o);
  }

  public void visitCodeLiteral(@NotNull LSFCodeLiteral o) {
    visitPsiElement(o);
  }

  public void visitCollapseGroupObjectActionPropertyDefinitionBody(@NotNull LSFCollapseGroupObjectActionPropertyDefinitionBody o) {
    visitFormContext(o);
  }

  public void visitColorLiteral(@NotNull LSFColorLiteral o) {
    visitPsiElement(o);
  }

  public void visitComplexSetting(@NotNull LSFComplexSetting o) {
    visitPsiElement(o);
  }

  public void visitComponentBlockStatement(@NotNull LSFComponentBlockStatement o) {
    visitPsiElement(o);
  }

  public void visitComponentBody(@NotNull LSFComponentBody o) {
    visitPsiElement(o);
  }

  public void visitComponentDecl(@NotNull LSFComponentDecl o) {
    visitComponentDeclaration(o);
  }

  public void visitComponentID(@NotNull LSFComponentID o) {
    visitFormContext(o);
  }

  public void visitComponentInsertPosition(@NotNull LSFComponentInsertPosition o) {
    visitPsiElement(o);
  }

  public void visitComponentPropertyValue(@NotNull LSFComponentPropertyValue o) {
    visitPsiElement(o);
  }

  public void visitComponentSelector(@NotNull LSFComponentSelector o) {
    visitPsiElement(o);
  }

  public void visitComponentStatement(@NotNull LSFComponentStatement o) {
    visitPsiElement(o);
  }

  public void visitComponentStubStatement(@NotNull LSFComponentStubStatement o) {
    visitPsiElement(o);
  }

  public void visitComponentUsage(@NotNull LSFComponentUsage o) {
    visitComponentReference(o);
  }

  public void visitCompoundID(@NotNull LSFCompoundID o) {
    visitPsiElement(o);
  }

  public void visitConcatPropertyDefinition(@NotNull LSFConcatPropertyDefinition o) {
    visitExpression(o);
  }

  public void visitConfirmActionPropertyDefinitionBody(@NotNull LSFConfirmActionPropertyDefinitionBody o) {
    visitExtendDoParamContext(o);
  }

  public void visitConfirmSetting(@NotNull LSFConfirmSetting o) {
    visitPsiElement(o);
  }

  public void visitConstraintFilter(@NotNull LSFConstraintFilter o) {
    visitPsiElement(o);
  }

  public void visitConstraintStatement(@NotNull LSFConstraintStatement o) {
    visitModifyParamContext(o);
  }

  public void visitContainerTypeLiteral(@NotNull LSFContainerTypeLiteral o) {
    visitPsiElement(o);
  }

  public void visitContextAction(@NotNull LSFContextAction o) {
    visitExtendParamContext(o);
  }

  public void visitContextActions(@NotNull LSFContextActions o) {
    visitExtendParamContext(o);
  }

  public void visitContextFiltersClause(@NotNull LSFContextFiltersClause o) {
    visitExtendParamContext(o);
  }

  public void visitContextMenuEventType(@NotNull LSFContextMenuEventType o) {
    visitPsiElement(o);
  }

  public void visitContinueActionOperator(@NotNull LSFContinueActionOperator o) {
    visitDocumentation(o);
  }

  public void visitCustomActionPropertyDefinitionBody(@NotNull LSFCustomActionPropertyDefinitionBody o) {
    visitUnfriendlyPE(o);
  }

  public void visitCustomClassUsage(@NotNull LSFCustomClassUsage o) {
    visitClassReference(o);
  }

  public void visitCustomClassUsageWrapper(@NotNull LSFCustomClassUsageWrapper o) {
    visitPsiElement(o);
  }

  public void visitCustomFormDesignOption(@NotNull LSFCustomFormDesignOption o) {
    visitPsiElement(o);
  }

  public void visitCustomHeaderLiteral(@NotNull LSFCustomHeaderLiteral o) {
    visitPsiElement(o);
  }

  public void visitCustomViewSetting(@NotNull LSFCustomViewSetting o) {
    visitPsiElement(o);
  }

  public void visitDataPropertyDefinition(@NotNull LSFDataPropertyDefinition o) {
    visitUnfriendlyPE(o);
  }

  public void visitDataPropertySessionModifier(@NotNull LSFDataPropertySessionModifier o) {
    visitPsiElement(o);
  }

  public void visitDateLiteral(@NotNull LSFDateLiteral o) {
    visitPsiElement(o);
  }

  public void visitDateTimeLiteral(@NotNull LSFDateTimeLiteral o) {
    visitPsiElement(o);
  }

  public void visitDefaultCompareSetting(@NotNull LSFDefaultCompareSetting o) {
    visitPsiElement(o);
  }

  public void visitDeleteActionPropertyDefinitionBody(@NotNull LSFDeleteActionPropertyDefinitionBody o) {
    visitActionExpression(o);
  }

  public void visitDesignCalcPropertyObject(@NotNull LSFDesignCalcPropertyObject o) {
    visitPsiElement(o);
  }

  public void visitDesignHeader(@NotNull LSFDesignHeader o) {
    visitPsiElement(o);
  }

  public void visitDesignStatement(@NotNull LSFDesignStatement o) {
    visitFormContext(o);
  }

  public void visitDialogActionPropertyDefinitionBody(@NotNull LSFDialogActionPropertyDefinitionBody o) {
    visitExtendDoParamContext(o);
  }

  public void visitDimensionLiteral(@NotNull LSFDimensionLiteral o) {
    visitPsiElement(o);
  }

  public void visitDoInputBody(@NotNull LSFDoInputBody o) {
    visitPsiElement(o);
  }

  public void visitDoMainBody(@NotNull LSFDoMainBody o) {
    visitExtendParamContext(o);
  }

  public void visitDockPosition(@NotNull LSFDockPosition o) {
    visitPsiElement(o);
  }

  public void visitDoubleLiteral(@NotNull LSFDoubleLiteral o) {
    visitPsiElement(o);
  }

  public void visitDrawRoot(@NotNull LSFDrawRoot o) {
    visitPsiElement(o);
  }

  public void visitDrillDownActionPropertyDefinitionBody(@NotNull LSFDrillDownActionPropertyDefinitionBody o) {
    visitActionExpression(o);
  }

  public void visitEchoSymbolsSetting(@NotNull LSFEchoSymbolsSetting o) {
    visitPsiElement(o);
  }

  public void visitEditFormDeclaration(@NotNull LSFEditFormDeclaration o) {
    visitDocumentation(o);
  }

  public void visitEmailActionPropertyDefinitionBody(@NotNull LSFEmailActionPropertyDefinitionBody o) {
    visitActionExpression(o);
  }

  public void visitEmailPropertyUsage(@NotNull LSFEmailPropertyUsage o) {
    visitPropertyUsageContext(o);
  }

  public void visitEmailRecipientTypeLiteral(@NotNull LSFEmailRecipientTypeLiteral o) {
    visitPsiElement(o);
  }

  public void visitEmptyActionPropertyDefinitionBody(@NotNull LSFEmptyActionPropertyDefinitionBody o) {
    visitPsiElement(o);
  }

  public void visitEmptyExplicitPropClassList(@NotNull LSFEmptyExplicitPropClassList o) {
    visitPsiElement(o);
  }

  public void visitEmptyStatement(@NotNull LSFEmptyStatement o) {
    visitPsiElement(o);
  }

  public void visitEqualityPE(@NotNull LSFEqualityPE o) {
    visitExpression(o);
  }

  public void visitEqualsSign(@NotNull LSFEqualsSign o) {
    visitDocumentation(o);
  }

  public void visitEvalActionPropertyDefinitionBody(@NotNull LSFEvalActionPropertyDefinitionBody o) {
    visitActionExpression(o);
  }

  public void visitEventIdSetting(@NotNull LSFEventIdSetting o) {
    visitPsiElement(o);
  }

  public void visitEventStatement(@NotNull LSFEventStatement o) {
    visitModifyParamContext(o);
  }

  public void visitExclusiveOperator(@NotNull LSFExclusiveOperator o) {
    visitDocumentation(o);
  }

  public void visitExclusiveOverrideOption(@NotNull LSFExclusiveOverrideOption o) {
    visitDocumentation(o);
  }

  public void visitExecActionPropertyDefinitionBody(@NotNull LSFExecActionPropertyDefinitionBody o) {
    visitActionExpression(o);
  }

  public void visitExpandCollapseActionPropertyDefinitionBody(@NotNull LSFExpandCollapseActionPropertyDefinitionBody o) {
    visitActionExpression(o);
  }

  public void visitExpandGroupObjectActionPropertyDefinitionBody(@NotNull LSFExpandGroupObjectActionPropertyDefinitionBody o) {
    visitFormContext(o);
  }

  public void visitExplicitInterfaceActStatement(@NotNull LSFExplicitInterfaceActStatement o) {
    visitExplicitInterfaceActionStatement(o);
  }

  public void visitExplicitInterfacePropertyStatement(@NotNull LSFExplicitInterfacePropertyStatement o) {
    visitExplicitInterfacePropStatement(o);
  }

  public void visitExplicitPropClass(@NotNull LSFExplicitPropClass o) {
    visitPsiElement(o);
  }

  public void visitExplicitPropClassUsage(@NotNull LSFExplicitPropClassUsage o) {
    visitPsiElement(o);
  }

  public void visitExplicitValuePropertyStatement(@NotNull LSFExplicitValuePropertyStatement o) {
    visitExplicitValuePropStatement(o);
  }

  public void visitExportActionPropertyDefinitionBody(@NotNull LSFExportActionPropertyDefinitionBody o) {
    visitFormContext(o);
  }

  public void visitExportDataActionPropertyDefinitionBody(@NotNull LSFExportDataActionPropertyDefinitionBody o) {
    visitExtendParamContext(o);
  }

  public void visitExprParameterNameUsage(@NotNull LSFExprParameterNameUsage o) {
    visitExprParamReference(o);
  }

  public void visitExprParameterUsage(@NotNull LSFExprParameterUsage o) {
    visitPsiElement(o);
  }

  public void visitExprParameterUsageList(@NotNull LSFExprParameterUsageList o) {
    visitPsiElement(o);
  }

  public void visitExpressionFriendlyPD(@NotNull LSFExpressionFriendlyPD o) {
    visitExpression(o);
  }

  public void visitExpressionLiteral(@NotNull LSFExpressionLiteral o) {
    visitExpression(o);
  }

  public void visitExpressionPrimitive(@NotNull LSFExpressionPrimitive o) {
    visitExpression(o);
  }

  public void visitExpressionStringLiteral(@NotNull LSFExpressionStringLiteral o) {
    visitExpressionStringValueLiteral(o);
  }

  public void visitExpressionUnfriendlyPD(@NotNull LSFExpressionUnfriendlyPD o) {
    visitPsiElement(o);
  }

  public void visitExtendingClassDeclaration(@NotNull LSFExtendingClassDeclaration o) {
    visitDocumentation(o);
  }

  public void visitExtendingFormDeclaration(@NotNull LSFExtendingFormDeclaration o) {
    visitDocumentation(o);
  }

  public void visitExternalActionPropertyDefinitionBody(@NotNull LSFExternalActionPropertyDefinitionBody o) {
    visitActionExpression(o);
  }

  public void visitFilterActionPropertyDefinitionBody(@NotNull LSFFilterActionPropertyDefinitionBody o) {
    visitFormContext(o);
  }

  public void visitFilterGroupName(@NotNull LSFFilterGroupName o) {
    visitFilterGroupDeclaration(o);
  }

  public void visitFilterGroupSelector(@NotNull LSFFilterGroupSelector o) {
    visitPsiElement(o);
  }

  public void visitFilterGroupUsage(@NotNull LSFFilterGroupUsage o) {
    visitFilterGroupReference(o);
  }

  public void visitFilterPropertyDefinition(@NotNull LSFFilterPropertyDefinition o) {
    visitUnfriendlyPE(o);
  }

  public void visitFilterPropertySelector(@NotNull LSFFilterPropertySelector o) {
    visitPsiElement(o);
  }

  public void visitFilterSetDefault(@NotNull LSFFilterSetDefault o) {
    visitPsiElement(o);
  }

  public void visitFlexAlignmentLiteral(@NotNull LSFFlexAlignmentLiteral o) {
    visitPsiElement(o);
  }

  public void visitFlexCharWidthSetting(@NotNull LSFFlexCharWidthSetting o) {
    visitPsiElement(o);
  }

  public void visitFollowsStatement(@NotNull LSFFollowsStatement o) {
    visitModifyParamContext(o);
  }

  public void visitForActionPropertyDefinitionBody(@NotNull LSFForActionPropertyDefinitionBody o) {
    visitActionExpression(o);
  }

  public void visitForActionPropertyMainBody(@NotNull LSFForActionPropertyMainBody o) {
    visitExtendParamContext(o);
  }

  public void visitForAddObjClause(@NotNull LSFForAddObjClause o) {
    visitClassParamDeclareContext(o);
  }

  public void visitFormActionDeclaration(@NotNull LSFFormActionDeclaration o) {
    visitPsiElement(o);
  }

  public void visitFormActionObject(@NotNull LSFFormActionObject o) {
    visitPropertyUsageContext(o);
  }

  public void visitFormActionObjectList(@NotNull LSFFormActionObjectList o) {
    visitPsiElement(o);
  }

  public void visitFormActionObjectUsage(@NotNull LSFFormActionObjectUsage o) {
    visitObjectInputParamDeclaration(o);
  }

  public void visitFormActionPropertyDefinitionBody(@NotNull LSFFormActionPropertyDefinitionBody o) {
    visitFormContext(o);
  }

  public void visitFormActionPropertyObject(@NotNull LSFFormActionPropertyObject o) {
    visitPsiElement(o);
  }

  public void visitFormCalcPropertyObject(@NotNull LSFFormCalcPropertyObject o) {
    visitPsiElement(o);
  }

  public void visitFormDecl(@NotNull LSFFormDecl o) {
    visitFormDeclaration(o);
  }

  public void visitFormElseNoParamsActionUsage(@NotNull LSFFormElseNoParamsActionUsage o) {
    visitFormElseNoParamsActionReference(o);
  }

  public void visitFormEventDeclaration(@NotNull LSFFormEventDeclaration o) {
    visitPsiElement(o);
  }

  public void visitFormEventType(@NotNull LSFFormEventType o) {
    visitPsiElement(o);
  }

  public void visitFormEventsList(@NotNull LSFFormEventsList o) {
    visitDocumentation(o);
  }

  public void visitFormExprDeclaration(@NotNull LSFFormExprDeclaration o) {
    visitPsiElement(o);
  }

  public void visitFormExtID(@NotNull LSFFormExtID o) {
    visitPsiElement(o);
  }

  public void visitFormExtIDSetting(@NotNull LSFFormExtIDSetting o) {
    visitDocumentation(o);
  }

  public void visitFormExtKey(@NotNull LSFFormExtKey o) {
    visitPsiElement(o);
  }

  public void visitFormExtendFilterGroupDeclaration(@NotNull LSFFormExtendFilterGroupDeclaration o) {
    visitDocumentation(o);
  }

  public void visitFormFilterGroupDeclaration(@NotNull LSFFormFilterGroupDeclaration o) {
    visitDocumentation(o);
  }

  public void visitFormFiltersList(@NotNull LSFFormFiltersList o) {
    visitDocumentation(o);
  }

  public void visitFormGroupObject(@NotNull LSFFormGroupObject o) {
    visitGroupObjectDeclaration(o);
  }

  public void visitFormGroupObjectBackground(@NotNull LSFFormGroupObjectBackground o) {
    visitPsiElement(o);
  }

  public void visitFormGroupObjectDeclaration(@NotNull LSFFormGroupObjectDeclaration o) {
    visitPsiElement(o);
  }

  public void visitFormGroupObjectForeground(@NotNull LSFFormGroupObjectForeground o) {
    visitPsiElement(o);
  }

  public void visitFormGroupObjectInitViewType(@NotNull LSFFormGroupObjectInitViewType o) {
    visitPsiElement(o);
  }

  public void visitFormGroupObjectOptions(@NotNull LSFFormGroupObjectOptions o) {
    visitDocumentation(o);
  }

  public void visitFormGroupObjectPageSize(@NotNull LSFFormGroupObjectPageSize o) {
    visitPsiElement(o);
  }

  public void visitFormGroupObjectRelativePosition(@NotNull LSFFormGroupObjectRelativePosition o) {
    visitPsiElement(o);
  }

  public void visitFormGroupObjectUpdate(@NotNull LSFFormGroupObjectUpdate o) {
    visitPsiElement(o);
  }

  public void visitFormGroupObjectViewType(@NotNull LSFFormGroupObjectViewType o) {
    visitPsiElement(o);
  }

  public void visitFormGroupObjectsList(@NotNull LSFFormGroupObjectsList o) {
    visitDocumentation(o);
  }

  public void visitFormHintsList(@NotNull LSFFormHintsList o) {
    visitPsiElement(o);
  }

  public void visitFormInGroup(@NotNull LSFFormInGroup o) {
    visitPsiElement(o);
  }

  public void visitFormMappedNamePropertiesList(@NotNull LSFFormMappedNamePropertiesList o) {
    visitPropertyUsageContext(o);
  }

  public void visitFormMappedPropertiesList(@NotNull LSFFormMappedPropertiesList o) {
    visitPsiElement(o);
  }

  public void visitFormMultiGroupObjectDeclaration(@NotNull LSFFormMultiGroupObjectDeclaration o) {
    visitPsiElement(o);
  }

  public void visitFormObjectDeclaration(@NotNull LSFFormObjectDeclaration o) {
    visitObjectDeclaration(o);
  }

  public void visitFormOptionColumns(@NotNull LSFFormOptionColumns o) {
    visitPsiElement(o);
  }

  public void visitFormOptionCustomView(@NotNull LSFFormOptionCustomView o) {
    visitPsiElement(o);
  }

  public void visitFormOptionEventId(@NotNull LSFFormOptionEventId o) {
    visitPsiElement(o);
  }

  public void visitFormOptionForce(@NotNull LSFFormOptionForce o) {
    visitPsiElement(o);
  }

  public void visitFormOptionInsertType(@NotNull LSFFormOptionInsertType o) {
    visitPsiElement(o);
  }

  public void visitFormOptionQuickFilter(@NotNull LSFFormOptionQuickFilter o) {
    visitPsiElement(o);
  }

  public void visitFormOptionSession(@NotNull LSFFormOptionSession o) {
    visitPsiElement(o);
  }

  public void visitFormOptionToDraw(@NotNull LSFFormOptionToDraw o) {
    visitPsiElement(o);
  }

  public void visitFormOptionsOnEvents(@NotNull LSFFormOptionsOnEvents o) {
    visitDocumentation(o);
  }

  public void visitFormOptionsWithCalcPropertyObject(@NotNull LSFFormOptionsWithCalcPropertyObject o) {
    visitPsiElement(o);
  }

  public void visitFormOptionsWithOptionalCalcPropertyObject(@NotNull LSFFormOptionsWithOptionalCalcPropertyObject o) {
    visitPsiElement(o);
  }

  public void visitFormOrderByList(@NotNull LSFFormOrderByList o) {
    visitDocumentation(o);
  }

  public void visitFormPivotOptionsDeclaration(@NotNull LSFFormPivotOptionsDeclaration o) {
    visitDocumentation(o);
  }

  public void visitFormPropertiesList(@NotNull LSFFormPropertiesList o) {
    visitDocumentation(o);
  }

  public void visitFormPropertiesNamesDeclList(@NotNull LSFFormPropertiesNamesDeclList o) {
    visitPsiElement(o);
  }

  public void visitFormPropertyDrawID(@NotNull LSFFormPropertyDrawID o) {
    visitFormContext(o);
  }

  public void visitFormPropertyDrawMappedDecl(@NotNull LSFFormPropertyDrawMappedDecl o) {
    visitPropertyDrawMappedDeclaration(o);
  }

  public void visitFormPropertyDrawNameDecl(@NotNull LSFFormPropertyDrawNameDecl o) {
    visitPropertyDrawNameDeclaration(o);
  }

  public void visitFormPropertyDrawObject(@NotNull LSFFormPropertyDrawObject o) {
    visitPropertyUsageContext(o);
  }

  public void visitFormPropertyDrawPropertyUsage(@NotNull LSFFormPropertyDrawPropertyUsage o) {
    visitPsiElement(o);
  }

  public void visitFormPropertyDrawUsage(@NotNull LSFFormPropertyDrawUsage o) {
    visitPropertyDrawReference(o);
  }

  public void visitFormPropertyDrawWithOrder(@NotNull LSFFormPropertyDrawWithOrder o) {
    visitPsiElement(o);
  }

  public void visitFormPropertyName(@NotNull LSFFormPropertyName o) {
    visitPsiElement(o);
  }

  public void visitFormPropertyObject(@NotNull LSFFormPropertyObject o) {
    visitPropertyUsageContext(o);
  }

  public void visitFormPropertyOptionsList(@NotNull LSFFormPropertyOptionsList o) {
    visitPsiElement(o);
  }

  public void visitFormSessionScopeClause(@NotNull LSFFormSessionScopeClause o) {
    visitPsiElement(o);
  }

  public void visitFormSingleActionObject(@NotNull LSFFormSingleActionObject o) {
    visitPsiElement(o);
  }

  public void visitFormSingleGroupObjectDeclaration(@NotNull LSFFormSingleGroupObjectDeclaration o) {
    visitPsiElement(o);
  }

  public void visitFormStatement(@NotNull LSFFormStatement o) {
    visitFormContext(o);
  }

  public void visitFormSubReport(@NotNull LSFFormSubReport o) {
    visitPsiElement(o);
  }

  public void visitFormTreeGroupObjectDeclaration(@NotNull LSFFormTreeGroupObjectDeclaration o) {
    visitPropertyUsageContext(o);
  }

  public void visitFormTreeGroupObjectList(@NotNull LSFFormTreeGroupObjectList o) {
    visitDocumentation(o);
  }

  public void visitFormTreeGroupObjectOptions(@NotNull LSFFormTreeGroupObjectOptions o) {
    visitPsiElement(o);
  }

  public void visitFormUsage(@NotNull LSFFormUsage o) {
    visitFormReference(o);
  }

  public void visitFormUsageWrapper(@NotNull LSFFormUsageWrapper o) {
    visitPsiElement(o);
  }

  public void visitFormulaPropertyDefinition(@NotNull LSFFormulaPropertyDefinition o) {
    visitUnfriendlyPE(o);
  }

  public void visitFormulaPropertySyntax(@NotNull LSFFormulaPropertySyntax o) {
    visitPsiElement(o);
  }

  public void visitFormulaPropertySyntaxList(@NotNull LSFFormulaPropertySyntaxList o) {
    visitPsiElement(o);
  }

  public void visitFormulaPropertySyntaxType(@NotNull LSFFormulaPropertySyntaxType o) {
    visitPsiElement(o);
  }

  public void visitGlobalEventStatement(@NotNull LSFGlobalEventStatement o) {
    visitModifyParamContext(o);
  }

  public void visitGlobalSingleSelectorType(@NotNull LSFGlobalSingleSelectorType o) {
    visitPsiElement(o);
  }

  public void visitGroupExprPropertyDefinition(@NotNull LSFGroupExprPropertyDefinition o) {
    visitExpression(o);
  }

  public void visitGroupObjectDestination(@NotNull LSFGroupObjectDestination o) {
    visitPsiElement(o);
  }

  public void visitGroupObjectID(@NotNull LSFGroupObjectID o) {
    visitFormContext(o);
  }

  public void visitGroupObjectReportPath(@NotNull LSFGroupObjectReportPath o) {
    visitPsiElement(o);
  }

  public void visitGroupObjectSelector(@NotNull LSFGroupObjectSelector o) {
    visitPsiElement(o);
  }

  public void visitGroupObjectTreeSingleSelectorType(@NotNull LSFGroupObjectTreeSingleSelectorType o) {
    visitPsiElement(o);
  }

  public void visitGroupObjectUsage(@NotNull LSFGroupObjectUsage o) {
    visitGroupObjectReference(o);
  }

  public void visitGroupPropertyBody(@NotNull LSFGroupPropertyBody o) {
    visitPsiElement(o);
  }

  public void visitGroupPropertyBy(@NotNull LSFGroupPropertyBy o) {
    visitPsiElement(o);
  }

  public void visitGroupPropertyDefinition(@NotNull LSFGroupPropertyDefinition o) {
    visitUnfriendlyPE(o);
  }

  public void visitGroupSelector(@NotNull LSFGroupSelector o) {
    visitPsiElement(o);
  }

  public void visitGroupSingleSelectorType(@NotNull LSFGroupSingleSelectorType o) {
    visitPsiElement(o);
  }

  public void visitGroupStatement(@NotNull LSFGroupStatement o) {
    visitGroupDeclaration(o);
  }

  public void visitGroupUsage(@NotNull LSFGroupUsage o) {
    visitGroupReference(o);
  }

  public void visitGroupingType(@NotNull LSFGroupingType o) {
    visitPsiElement(o);
  }

  public void visitGroupingTypeOrder(@NotNull LSFGroupingTypeOrder o) {
    visitPsiElement(o);
  }

  public void visitHasHeaderOption(@NotNull LSFHasHeaderOption o) {
    visitPsiElement(o);
  }

  public void visitHeadersPropertyUsage(@NotNull LSFHeadersPropertyUsage o) {
    visitPropertyUsageContext(o);
  }

  public void visitHideEditKey(@NotNull LSFHideEditKey o) {
    visitPsiElement(o);
  }

  public void visitHintSetting(@NotNull LSFHintSetting o) {
    visitPsiElement(o);
  }

  public void visitIfActionPropertyDefinitionBody(@NotNull LSFIfActionPropertyDefinitionBody o) {
    visitActionExpression(o);
  }

  public void visitIfElsePropertyDefinition(@NotNull LSFIfElsePropertyDefinition o) {
    visitExpression(o);
  }

  public void visitIfPE(@NotNull LSFIfPE o) {
    visitExpression(o);
  }

  public void visitImageSetting(@NotNull LSFImageSetting o) {
    visitPsiElement(o);
  }

  public void visitImplicitInterfacePropertyStatement(@NotNull LSFImplicitInterfacePropertyStatement o) {
    visitImplicitInterfacePropStatement(o);
  }

  public void visitImplicitValuePropertyStatement(@NotNull LSFImplicitValuePropertyStatement o) {
    visitImplicitValuePropStatement(o);
  }

  public void visitImportActionPropertyDefinitionBody(@NotNull LSFImportActionPropertyDefinitionBody o) {
    visitExtendDoParamContext(o);
  }

  public void visitImportActionSourceType(@NotNull LSFImportActionSourceType o) {
    visitPsiElement(o);
  }

  public void visitImportFieldAlias(@NotNull LSFImportFieldAlias o) {
    visitPsiElement(o);
  }

  public void visitImportFieldDefinition(@NotNull LSFImportFieldDefinition o) {
    visitImportFieldParamDeclaration(o);
  }

  public void visitImportFieldName(@NotNull LSFImportFieldName o) {
    visitPsiElement(o);
  }

  public void visitImportFormActionPropertyDefinitionBody(@NotNull LSFImportFormActionPropertyDefinitionBody o) {
    visitFormContext(o);
  }

  public void visitImportFormHierarchicalActionSourceType(@NotNull LSFImportFormHierarchicalActionSourceType o) {
    visitPsiElement(o);
  }

  public void visitImportFormPlainActionSourceType(@NotNull LSFImportFormPlainActionSourceType o) {
    visitPsiElement(o);
  }

  public void visitImportPropertyUsage(@NotNull LSFImportPropertyUsage o) {
    visitPropertyUsageContext(o);
  }

  public void visitImportPropertyUsageWithId(@NotNull LSFImportPropertyUsageWithId o) {
    visitPsiElement(o);
  }

  public void visitIndexSetting(@NotNull LSFIndexSetting o) {
    visitPsiElement(o);
  }

  public void visitIndexStatement(@NotNull LSFIndexStatement o) {
    visitModifyParamContext(o);
  }

  public void visitInlineOption(@NotNull LSFInlineOption o) {
    visitPsiElement(o);
  }

  public void visitInputActionPropertyDefinitionBody(@NotNull LSFInputActionPropertyDefinitionBody o) {
    visitExtendDoParamContext(o);
  }

  public void visitInsertRelativePositionLiteral(@NotNull LSFInsertRelativePositionLiteral o) {
    visitPsiElement(o);
  }

  public void visitIntLiteral(@NotNull LSFIntLiteral o) {
    visitPsiElement(o);
  }

  public void visitInternalAction(@NotNull LSFInternalAction o) {
    visitPsiElement(o);
  }

  public void visitInternalActionPropertyDefinitionBody(@NotNull LSFInternalActionPropertyDefinitionBody o) {
    visitActionExpression(o);
  }

  public void visitInternalClass(@NotNull LSFInternalClass o) {
    visitPsiElement(o);
  }

  public void visitInternalFormObject(@NotNull LSFInternalFormObject o) {
    visitFormContext(o);
  }

  public void visitInternalModule(@NotNull LSFInternalModule o) {
    visitPsiElement(o);
  }

  public void visitInternalProperty(@NotNull LSFInternalProperty o) {
    visitPsiElement(o);
  }

  public void visitInternalPropertyDraw(@NotNull LSFInternalPropertyDraw o) {
    visitFormContext(o);
  }

  public void visitInternalStatement(@NotNull LSFInternalStatement o) {
    visitPsiElement(o);
  }

  public void visitJavaClassStringUsage(@NotNull LSFJavaClassStringUsage o) {
    visitJavaClassStringReference(o);
  }

  public void visitJoinPropertyDefinition(@NotNull LSFJoinPropertyDefinition o) {
    visitPropertyUsageContext(o);
  }

  public void visitJsStringUsage(@NotNull LSFJsStringUsage o) {
    visitPsiElement(o);
  }

  public void visitJsonFormPropertyDefinition(@NotNull LSFJsonFormPropertyDefinition o) {
    visitFormContext(o);
  }

  public void visitJsonPropertyDefinition(@NotNull LSFJsonPropertyDefinition o) {
    visitExtendParamContext(o);
  }

  public void visitKeyPressedEventType(@NotNull LSFKeyPressedEventType o) {
    visitPsiElement(o);
  }

  public void visitLazyMetaDeclStatement(@NotNull LSFLazyMetaDeclStatement o) {
    visitPsiElement(o);
  }

  public void visitLazyMetaStatement(@NotNull LSFLazyMetaStatement o) {
    visitPsiElement(o);
  }

  public void visitLazyScriptStatement(@NotNull LSFLazyScriptStatement o) {
    visitPsiElement(o);
  }

  public void visitLikePE(@NotNull LSFLikePE o) {
    visitExpression(o);
  }

  public void visitListActionPropertyDefinitionBody(@NotNull LSFListActionPropertyDefinitionBody o) {
    visitActionExpression(o);
  }

  public void visitListFormDeclaration(@NotNull LSFListFormDeclaration o) {
    visitDocumentation(o);
  }

  public void visitListViewType(@NotNull LSFListViewType o) {
    visitPsiElement(o);
  }

  public void visitListWhereInputProps(@NotNull LSFListWhereInputProps o) {
    visitExtendParamContext(o);
  }

  public void visitLiteral(@NotNull LSFLiteral o) {
    visitDocumentation(o);
  }

  public void visitLocalDataPropertyDefinition(@NotNull LSFLocalDataPropertyDefinition o) {
    visitDocumentation(o);
  }

  public void visitLocalPropertyDeclarationName(@NotNull LSFLocalPropertyDeclarationName o) {
    visitLocalPropDeclaration(o);
  }

  public void visitLocalizedStringLiteral(@NotNull LSFLocalizedStringLiteral o) {
    visitLocalizedStringValueLiteral(o);
  }

  public void visitLoggableSetting(@NotNull LSFLoggableSetting o) {
    visitPsiElement(o);
  }

  public void visitLoggableStatement(@NotNull LSFLoggableStatement o) {
    visitPsiElement(o);
  }

  public void visitManageSessionClause(@NotNull LSFManageSessionClause o) {
    visitPsiElement(o);
  }

  public void visitMapOptions(@NotNull LSFMapOptions o) {
    visitPsiElement(o);
  }

  public void visitMappedActionClassParamDeclare(@NotNull LSFMappedActionClassParamDeclare o) {
    visitPropertyUsageContext(o);
  }

  public void visitMappedPropertyClassParamDeclare(@NotNull LSFMappedPropertyClassParamDeclare o) {
    visitPropertyUsageContext(o);
  }

  public void visitMappedPropertyExprParam(@NotNull LSFMappedPropertyExprParam o) {
    visitPropertyUsageContext(o);
  }

  public void visitMappedPropertyOrSimpleExprParam(@NotNull LSFMappedPropertyOrSimpleExprParam o) {
    visitPsiElement(o);
  }

  public void visitMaxPropertyDefinition(@NotNull LSFMaxPropertyDefinition o) {
    visitExpression(o);
  }

  public void visitMessageActionPropertyDefinitionBody(@NotNull LSFMessageActionPropertyDefinitionBody o) {
    visitActionExpression(o);
  }

  public void visitMessagePropertyExpression(@NotNull LSFMessagePropertyExpression o) {
    visitModifyParamContext(o);
  }

  public void visitMetaCodeBody(@NotNull LSFMetaCodeBody o) {
    visitPsiElement(o);
  }

  public void visitMetaCodeBodyLeftBrace(@NotNull LSFMetaCodeBodyLeftBrace o) {
    visitPsiElement(o);
  }

  public void visitMetaCodeBodyRightBrace(@NotNull LSFMetaCodeBodyRightBrace o) {
    visitPsiElement(o);
  }

  public void visitMetaCodeDeclBody(@NotNull LSFMetaCodeDeclBody o) {
    visitPsiElement(o);
  }

  public void visitMetaCodeDeclarationStatement(@NotNull LSFMetaCodeDeclarationStatement o) {
    visitMetaDeclaration(o);
  }

  public void visitMetaCodeId(@NotNull LSFMetaCodeId o) {
    visitPsiElement(o);
  }

  public void visitMetaCodeIdList(@NotNull LSFMetaCodeIdList o) {
    visitPsiElement(o);
  }

  public void visitMetaCodeLiteral(@NotNull LSFMetaCodeLiteral o) {
    visitPsiElement(o);
  }

  public void visitMetaCodeStatement(@NotNull LSFMetaCodeStatement o) {
    visitMetaReference(o);
  }

  public void visitMetaCodeStatementHeader(@NotNull LSFMetaCodeStatementHeader o) {
    visitPsiElement(o);
  }

  public void visitMetaCodeStatementSemi(@NotNull LSFMetaCodeStatementSemi o) {
    visitPsiElement(o);
  }

  public void visitMetaCodeStatementType(@NotNull LSFMetaCodeStatementType o) {
    visitPsiElement(o);
  }

  public void visitMetaCodeStringLiteral(@NotNull LSFMetaCodeStringLiteral o) {
    visitMetacodeStringValueLiteral(o);
  }

  public void visitMetaDeclId(@NotNull LSFMetaDeclId o) {
    visitPsiElement(o);
  }

  public void visitMetaDeclIdList(@NotNull LSFMetaDeclIdList o) {
    visitPsiElement(o);
  }

  public void visitMetacodeUsage(@NotNull LSFMetacodeUsage o) {
    visitDocumentation(o);
  }

  public void visitModuleHeader(@NotNull LSFModuleHeader o) {
    visitModuleDeclaration(o);
  }

  public void visitModuleName(@NotNull LSFModuleName o) {
    visitPsiElement(o);
  }

  public void visitModuleNameStatement(@NotNull LSFModuleNameStatement o) {
    visitPsiElement(o);
  }

  public void visitModuleUsage(@NotNull LSFModuleUsage o) {
    visitModuleReference(o);
  }

  public void visitMoveComponentStatement(@NotNull LSFMoveComponentStatement o) {
    visitPsiElement(o);
  }

  public void visitMoveNavigatorElementStatement(@NotNull LSFMoveNavigatorElementStatement o) {
    visitPsiElement(o);
  }

  public void visitMultiActionPropertyDefinitionBody(@NotNull LSFMultiActionPropertyDefinitionBody o) {
    visitActionExpression(o);
  }

  public void visitMultiPropertyDefinition(@NotNull LSFMultiPropertyDefinition o) {
    visitExpression(o);
  }

  public void visitMultiplicativePE(@NotNull LSFMultiplicativePE o) {
    visitExpression(o);
  }

  public void visitNamespaceName(@NotNull LSFNamespaceName o) {
    visitExplicitNamespaceDeclaration(o);
  }

  public void visitNamespaceUsage(@NotNull LSFNamespaceUsage o) {
    visitNamespaceReference(o);
  }

  public void visitNativeLiteral(@NotNull LSFNativeLiteral o) {
    visitPsiElement(o);
  }

  public void visitNativePropertyDefinition(@NotNull LSFNativePropertyDefinition o) {
    visitUnfriendlyPE(o);
  }

  public void visitNavigatorElementBodyStatement(@NotNull LSFNavigatorElementBodyStatement o) {
    visitPsiElement(o);
  }

  public void visitNavigatorElementDescription(@NotNull LSFNavigatorElementDescription o) {
    visitPsiElement(o);
  }

  public void visitNavigatorElementInsertPosition(@NotNull LSFNavigatorElementInsertPosition o) {
    visitPsiElement(o);
  }

  public void visitNavigatorElementOptions(@NotNull LSFNavigatorElementOptions o) {
    visitPsiElement(o);
  }

  public void visitNavigatorElementSelector(@NotNull LSFNavigatorElementSelector o) {
    visitPsiElement(o);
  }

  public void visitNavigatorElementStatementBody(@NotNull LSFNavigatorElementStatementBody o) {
    visitPsiElement(o);
  }

  public void visitNavigatorElementUsage(@NotNull LSFNavigatorElementUsage o) {
    visitNavigatorElementReference(o);
  }

  public void visitNavigatorStatement(@NotNull LSFNavigatorStatement o) {
    visitModifyParamContext(o);
  }

  public void visitNeStub(@NotNull LSFNeStub o) {
    visitPsiElement(o);
  }

  public void visitNestedLocalModifier(@NotNull LSFNestedLocalModifier o) {
    visitDocumentation(o);
  }

  public void visitNestedSessionOperator(@NotNull LSFNestedSessionOperator o) {
    visitDocumentation(o);
  }

  public void visitNewActionPropertyDefinitionBody(@NotNull LSFNewActionPropertyDefinitionBody o) {
    visitActionExpression(o);
  }

  public void visitNewComponentStatement(@NotNull LSFNewComponentStatement o) {
    visitPsiElement(o);
  }

  public void visitNewExecutorActionPropertyDefinitionBody(@NotNull LSFNewExecutorActionPropertyDefinitionBody o) {
    visitActionExpression(o);
  }

  public void visitNewNavigatorElementStatement(@NotNull LSFNewNavigatorElementStatement o) {
    visitNavigatorElementDeclaration(o);
  }

  public void visitNewSessionActionPropertyDefinitionBody(@NotNull LSFNewSessionActionPropertyDefinitionBody o) {
    visitActionExpression(o);
  }

  public void visitNewSessionOperator(@NotNull LSFNewSessionOperator o) {
    visitDocumentation(o);
  }

  public void visitNewThreadActionPropertyDefinitionBody(@NotNull LSFNewThreadActionPropertyDefinitionBody o) {
    visitActionExpression(o);
  }

  public void visitNewWhereActionPropertyDefinitionBody(@NotNull LSFNewWhereActionPropertyDefinitionBody o) {
    visitActionExpression(o);
  }

  public void visitNoCancelClause(@NotNull LSFNoCancelClause o) {
    visitPsiElement(o);
  }

  public void visitNoContextActionOrPropertyUsage(@NotNull LSFNoContextActionOrPropertyUsage o) {
    visitPropertyUsageContext(o);
  }

  public void visitNoContextActionUsage(@NotNull LSFNoContextActionUsage o) {
    visitPropertyUsageContext(o);
  }

  public void visitNoContextPropertyUsage(@NotNull LSFNoContextPropertyUsage o) {
    visitPropertyUsageContext(o);
  }

  public void visitNoDefault(@NotNull LSFNoDefault o) {
    visitPsiElement(o);
  }

  public void visitNoEscapeOption(@NotNull LSFNoEscapeOption o) {
    visitPsiElement(o);
  }

  public void visitNoParamsActionUsage(@NotNull LSFNoParamsActionUsage o) {
    visitPropertyUsageContext(o);
  }

  public void visitNoParamsPropertyUsage(@NotNull LSFNoParamsPropertyUsage o) {
    visitPropertyUsageContext(o);
  }

  public void visitNonEmptyActionPDBList(@NotNull LSFNonEmptyActionPDBList o) {
    visitPsiElement(o);
  }

  public void visitNonEmptyAliasedPropertyExpressionList(@NotNull LSFNonEmptyAliasedPropertyExpressionList o) {
    visitPsiElement(o);
  }

  public void visitNonEmptyClassNameList(@NotNull LSFNonEmptyClassNameList o) {
    visitPsiElement(o);
  }

  public void visitNonEmptyClassParamDeclareList(@NotNull LSFNonEmptyClassParamDeclareList o) {
    visitPsiElement(o);
  }

  public void visitNonEmptyCustomClassUsageList(@NotNull LSFNonEmptyCustomClassUsageList o) {
    visitPsiElement(o);
  }

  public void visitNonEmptyExplicitPropClassList(@NotNull LSFNonEmptyExplicitPropClassList o) {
    visitPsiElement(o);
  }

  public void visitNonEmptyFormUsageList(@NotNull LSFNonEmptyFormUsageList o) {
    visitPsiElement(o);
  }

  public void visitNonEmptyGroupObjectUsageList(@NotNull LSFNonEmptyGroupObjectUsageList o) {
    visitPsiElement(o);
  }

  public void visitNonEmptyImportFieldDefinitions(@NotNull LSFNonEmptyImportFieldDefinitions o) {
    visitPsiElement(o);
  }

  public void visitNonEmptyImportPropertyUsageListWithIds(@NotNull LSFNonEmptyImportPropertyUsageListWithIds o) {
    visitPsiElement(o);
  }

  public void visitNonEmptyLocalPropertyDeclarationNameList(@NotNull LSFNonEmptyLocalPropertyDeclarationNameList o) {
    visitPsiElement(o);
  }

  public void visitNonEmptyMappedPropertyOrSimpleExprParamList(@NotNull LSFNonEmptyMappedPropertyOrSimpleExprParamList o) {
    visitPsiElement(o);
  }

  public void visitNonEmptyModuleUsageList(@NotNull LSFNonEmptyModuleUsageList o) {
    visitPsiElement(o);
  }

  public void visitNonEmptyNamespaceUsageList(@NotNull LSFNonEmptyNamespaceUsageList o) {
    visitPsiElement(o);
  }

  public void visitNonEmptyNoContextActionOrPropertyUsageList(@NotNull LSFNonEmptyNoContextActionOrPropertyUsageList o) {
    visitPsiElement(o);
  }

  public void visitNonEmptyNoContextActionUsageList(@NotNull LSFNonEmptyNoContextActionUsageList o) {
    visitPsiElement(o);
  }

  public void visitNonEmptyNoContextPropertyUsageList(@NotNull LSFNonEmptyNoContextPropertyUsageList o) {
    visitPsiElement(o);
  }

  public void visitNonEmptyNoParamsPropertyUsageList(@NotNull LSFNonEmptyNoParamsPropertyUsageList o) {
    visitPsiElement(o);
  }

  public void visitNonEmptyObjectUsageList(@NotNull LSFNonEmptyObjectUsageList o) {
    visitPsiElement(o);
  }

  public void visitNonEmptyParameterOrExpressionList(@NotNull LSFNonEmptyParameterOrExpressionList o) {
    visitPsiElement(o);
  }

  public void visitNonEmptyPropertyExpressionList(@NotNull LSFNonEmptyPropertyExpressionList o) {
    visitPsiElement(o);
  }

  public void visitNonEmptyPropertyOptions(@NotNull LSFNonEmptyPropertyOptions o) {
    visitDocumentation(o);
  }

  public void visitNonEmptyStaticObjectDeclList(@NotNull LSFNonEmptyStaticObjectDeclList o) {
    visitPsiElement(o);
  }

  public void visitNotNullSetting(@NotNull LSFNotNullSetting o) {
    visitPsiElement(o);
  }

  public void visitNotPE(@NotNull LSFNotPE o) {
    visitExpression(o);
  }

  public void visitNullLiteral(@NotNull LSFNullLiteral o) {
    visitPsiElement(o);
  }

  public void visitNullOption(@NotNull LSFNullOption o) {
    visitPsiElement(o);
  }

  public void visitObjectExpr(@NotNull LSFObjectExpr o) {
    visitPsiElement(o);
  }

  public void visitObjectID(@NotNull LSFObjectID o) {
    visitFormContext(o);
  }

  public void visitObjectInProps(@NotNull LSFObjectInProps o) {
    visitPsiElement(o);
  }

  public void visitObjectInputProps(@NotNull LSFObjectInputProps o) {
    visitDocumentation(o);
  }

  public void visitObjectListInputProps(@NotNull LSFObjectListInputProps o) {
    visitExtendParamContext(o);
  }

  public void visitObjectUsage(@NotNull LSFObjectUsage o) {
    visitObjectReference(o);
  }

  public void visitObjectUsageList(@NotNull LSFObjectUsageList o) {
    visitPsiElement(o);
  }

  public void visitOnEditEventSetting(@NotNull LSFOnEditEventSetting o) {
    visitPsiElement(o);
  }

  public void visitOrPE(@NotNull LSFOrPE o) {
    visitExpression(o);
  }

  public void visitOrderActionPropertyDefinitionBody(@NotNull LSFOrderActionPropertyDefinitionBody o) {
    visitFormContext(o);
  }

  public void visitOrderPropertyBy(@NotNull LSFOrderPropertyBy o) {
    visitPsiElement(o);
  }

  public void visitOrientation(@NotNull LSFOrientation o) {
    visitPsiElement(o);
  }

  public void visitOverrideActionStatement(@NotNull LSFOverrideActionStatement o) {
    visitModifyParamContext(o);
  }

  public void visitOverrideOperator(@NotNull LSFOverrideOperator o) {
    visitDocumentation(o);
  }

  public void visitOverridePropertyDefinition(@NotNull LSFOverridePropertyDefinition o) {
    visitExpression(o);
  }

  public void visitOverridePropertyStatement(@NotNull LSFOverridePropertyStatement o) {
    visitModifyParamContext(o);
  }

  public void visitParamDeclare(@NotNull LSFParamDeclare o) {
    visitParamDeclaration(o);
  }

  public void visitParameterOrExpression(@NotNull LSFParameterOrExpression o) {
    visitPsiElement(o);
  }

  public void visitParameterOrExpressionList(@NotNull LSFParameterOrExpressionList o) {
    visitPsiElement(o);
  }

  public void visitPartitionPropertyBy(@NotNull LSFPartitionPropertyBy o) {
    visitPsiElement(o);
  }

  public void visitPartitionPropertyDefinition(@NotNull LSFPartitionPropertyDefinition o) {
    visitPropertyUsageContext(o);
  }

  public void visitPatternSetting(@NotNull LSFPatternSetting o) {
    visitPsiElement(o);
  }

  public void visitPersistentSetting(@NotNull LSFPersistentSetting o) {
    visitPsiElement(o);
  }

  public void visitPivotOptions(@NotNull LSFPivotOptions o) {
    visitPsiElement(o);
  }

  public void visitPivotPropertyDrawList(@NotNull LSFPivotPropertyDrawList o) {
    visitPsiElement(o);
  }

  public void visitPostfixUnaryPE(@NotNull LSFPostfixUnaryPE o) {
    visitExpression(o);
  }

  public void visitPredefinedAddPropertyName(@NotNull LSFPredefinedAddPropertyName o) {
    visitPsiElement(o);
  }

  public void visitPredefinedFormPropertyName(@NotNull LSFPredefinedFormPropertyName o) {
    visitId(o);
  }

  public void visitPrereadSetting(@NotNull LSFPrereadSetting o) {
    visitPsiElement(o);
  }

  public void visitPrintActionPropertyDefinitionBody(@NotNull LSFPrintActionPropertyDefinitionBody o) {
    visitFormContext(o);
  }

  public void visitPriorityList(@NotNull LSFPriorityList o) {
    visitPsiElement(o);
  }

  public void visitPropertyCalcStatement(@NotNull LSFPropertyCalcStatement o) {
    visitPsiElement(o);
  }

  public void visitPropertyCustomView(@NotNull LSFPropertyCustomView o) {
    visitPsiElement(o);
  }

  public void visitPropertyDeclParams(@NotNull LSFPropertyDeclParams o) {
    visitPsiElement(o);
  }

  public void visitPropertyDeclaration(@NotNull LSFPropertyDeclaration o) {
    visitPsiElement(o);
  }

  public void visitPropertyElseActionUsage(@NotNull LSFPropertyElseActionUsage o) {
    visitPropElseActionReference(o);
  }

  public void visitPropertyExprObject(@NotNull LSFPropertyExprObject o) {
    visitExtendParamContext(o);
  }

  public void visitPropertyExpression(@NotNull LSFPropertyExpression o) {
    visitExpression(o);
  }

  public void visitPropertyExpressionList(@NotNull LSFPropertyExpressionList o) {
    visitPsiElement(o);
  }

  public void visitPropertyExpressionWithOrder(@NotNull LSFPropertyExpressionWithOrder o) {
    visitPsiElement(o);
  }

  public void visitPropertySelector(@NotNull LSFPropertySelector o) {
    visitPsiElement(o);
  }

  public void visitPropertyStatement(@NotNull LSFPropertyStatement o) {
    visitInterfacePropStatement(o);
  }

  public void visitPropertyUsage(@NotNull LSFPropertyUsage o) {
    visitPropReference(o);
  }

  public void visitPropertyUsageWrapper(@NotNull LSFPropertyUsageWrapper o) {
    visitPsiElement(o);
  }

  public void visitReadActionPropertyDefinitionBody(@NotNull LSFReadActionPropertyDefinitionBody o) {
    visitActionExpression(o);
  }

  public void visitReadFilterActionPropertyDefinitionBody(@NotNull LSFReadFilterActionPropertyDefinitionBody o) {
    visitFormContext(o);
  }

  public void visitReadOrderActionPropertyDefinitionBody(@NotNull LSFReadOrderActionPropertyDefinitionBody o) {
    visitFormContext(o);
  }

  public void visitRecalculateActionPropertyDefinitionBody(@NotNull LSFRecalculateActionPropertyDefinitionBody o) {
    visitActionExpression(o);
  }

  public void visitRecursivePropertyDefinition(@NotNull LSFRecursivePropertyDefinition o) {
    visitExpression(o);
  }

  public void visitReflectionPropertyDefinition(@NotNull LSFReflectionPropertyDefinition o) {
    visitUnfriendlyPE(o);
  }

  public void visitReflectionPropertyType(@NotNull LSFReflectionPropertyType o) {
    visitPsiElement(o);
  }

  public void visitRegexpSetting(@NotNull LSFRegexpSetting o) {
    visitPsiElement(o);
  }

  public void visitRegularFilterDeclaration(@NotNull LSFRegularFilterDeclaration o) {
    visitPsiElement(o);
  }

  public void visitRelationalPE(@NotNull LSFRelationalPE o) {
    visitExpression(o);
  }

  public void visitRemoveComponentStatement(@NotNull LSFRemoveComponentStatement o) {
    visitPsiElement(o);
  }

  public void visitRenderPropertyCustomView(@NotNull LSFRenderPropertyCustomView o) {
    visitPsiElement(o);
  }

  public void visitReportFilesDeclaration(@NotNull LSFReportFilesDeclaration o) {
    visitPsiElement(o);
  }

  public void visitReportSetting(@NotNull LSFReportSetting o) {
    visitDocumentation(o);
  }

  public void visitRequestActionPropertyDefinitionBody(@NotNull LSFRequestActionPropertyDefinitionBody o) {
    visitActionExpression(o);
  }

  public void visitRequireList(@NotNull LSFRequireList o) {
    visitPsiElement(o);
  }

  public void visitReturnActionOperator(@NotNull LSFReturnActionOperator o) {
    visitDocumentation(o);
  }

  public void visitRoundPropertyDefinition(@NotNull LSFRoundPropertyDefinition o) {
    visitExpression(o);
  }

  public void visitScriptStatement(@NotNull LSFScriptStatement o) {
    visitPsiElement(o);
  }

  public void visitSeekObjectActionPropertyDefinitionBody(@NotNull LSFSeekObjectActionPropertyDefinitionBody o) {
    visitFormContext(o);
  }

  public void visitSelectTop(@NotNull LSFSelectTop o) {
    visitPsiElement(o);
  }

  public void visitSelectTops(@NotNull LSFSelectTops o) {
    visitPsiElement(o);
  }

  public void visitSessionPropertyDefinition(@NotNull LSFSessionPropertyDefinition o) {
    visitExpression(o);
  }

  public void visitSessionPropertyType(@NotNull LSFSessionPropertyType o) {
    visitPsiElement(o);
  }

  public void visitSetObjectPropertyStatement(@NotNull LSFSetObjectPropertyStatement o) {
    visitPsiElement(o);
  }

  public void visitSetupComponentStatement(@NotNull LSFSetupComponentStatement o) {
    visitPsiElement(o);
  }

  public void visitSetupNavigatorElementStatement(@NotNull LSFSetupNavigatorElementStatement o) {
    visitPsiElement(o);
  }

  public void visitSheetExpression(@NotNull LSFSheetExpression o) {
    visitPsiElement(o);
  }

  public void visitShowDepStatement(@NotNull LSFShowDepStatement o) {
    visitPsiElement(o);
  }

  public void visitSignaturePropertyDefinition(@NotNull LSFSignaturePropertyDefinition o) {
    visitExpression(o);
  }

  public void visitSimpleElementDescription(@NotNull LSFSimpleElementDescription o) {
    visitPsiElement(o);
  }

  public void visitSimpleName(@NotNull LSFSimpleName o) {
    visitPsiNamedElement(o);
  }

  public void visitSimpleNameWithCaption(@NotNull LSFSimpleNameWithCaption o) {
    visitPsiElement(o);
  }

  public void visitSimplePE(@NotNull LSFSimplePE o) {
    visitExpression(o);
  }

  public void visitStaticDestination(@NotNull LSFStaticDestination o) {
    visitPsiElement(o);
  }

  public void visitStaticObjectDecl(@NotNull LSFStaticObjectDecl o) {
    visitStaticObjectDeclaration(o);
  }

  public void visitStaticObjectDeclList(@NotNull LSFStaticObjectDeclList o) {
    visitPsiElement(o);
  }

  public void visitStaticObjectID(@NotNull LSFStaticObjectID o) {
    visitStaticObjectReference(o);
  }

  public void visitStaticObjectImage(@NotNull LSFStaticObjectImage o) {
    visitPsiElement(o);
  }

  public void visitStaticRelativePosition(@NotNull LSFStaticRelativePosition o) {
    visitPsiElement(o);
  }

  public void visitStickyOption(@NotNull LSFStickyOption o) {
    visitPsiElement(o);
  }

  public void visitStringLiteral(@NotNull LSFStringLiteral o) {
    visitStringValueLiteral(o);
  }

  public void visitStructCreationPropertyDefinition(@NotNull LSFStructCreationPropertyDefinition o) {
    visitExpression(o);
  }

  public void visitStubStatement(@NotNull LSFStubStatement o) {
    visitPsiElement(o);
  }

  public void visitSyncTypeLiteral(@NotNull LSFSyncTypeLiteral o) {
    visitPsiElement(o);
  }

  public void visitTableStatement(@NotNull LSFTableStatement o) {
    visitTableDeclaration(o);
  }

  public void visitTableUsage(@NotNull LSFTableUsage o) {
    visitTableReference(o);
  }

  public void visitTbooleanLiteral(@NotNull LSFTbooleanLiteral o) {
    visitPsiElement(o);
  }

  public void visitTerminalFlowActionPropertyDefinitionBody(@NotNull LSFTerminalFlowActionPropertyDefinitionBody o) {
    visitActionExpression(o);
  }

  public void visitTimeLiteral(@NotNull LSFTimeLiteral o) {
    visitPsiElement(o);
  }

  public void visitTreeGroupDeclaration(@NotNull LSFTreeGroupDeclaration o) {
    visitTreeGroupDecl(o);
  }

  public void visitTreeGroupParentDeclaration(@NotNull LSFTreeGroupParentDeclaration o) {
    visitPsiElement(o);
  }

  public void visitTreeGroupSelector(@NotNull LSFTreeGroupSelector o) {
    visitPsiElement(o);
  }

  public void visitTreeGroupUsage(@NotNull LSFTreeGroupUsage o) {
    visitTreeGroupReference(o);
  }

  public void visitTryActionPropertyDefinitionBody(@NotNull LSFTryActionPropertyDefinitionBody o) {
    visitActionExpression(o);
  }

  public void visitTypeIs(@NotNull LSFTypeIs o) {
    visitPsiElement(o);
  }

  public void visitTypeMult(@NotNull LSFTypeMult o) {
    visitPsiElement(o);
  }

  public void visitTypePropertyDefinition(@NotNull LSFTypePropertyDefinition o) {
    visitDocumentation(o);
  }

  public void visitUdoubleLiteral(@NotNull LSFUdoubleLiteral o) {
    visitPsiElement(o);
  }

  public void visitUintLiteral(@NotNull LSFUintLiteral o) {
    visitPsiElement(o);
  }

  public void visitUlongLiteral(@NotNull LSFUlongLiteral o) {
    visitPsiElement(o);
  }

  public void visitUnaryMinusPE(@NotNull LSFUnaryMinusPE o) {
    visitExpression(o);
  }

  public void visitUntypedParamDeclare(@NotNull LSFUntypedParamDeclare o) {
    visitPsiElement(o);
  }

  public void visitUnumericLiteral(@NotNull LSFUnumericLiteral o) {
    visitPsiElement(o);
  }

  public void visitUserFiltersDeclaration(@NotNull LSFUserFiltersDeclaration o) {
    visitPsiElement(o);
  }

  public void visitViewTypeSetting(@NotNull LSFViewTypeSetting o) {
    visitPsiElement(o);
  }

  public void visitWherePropertyExpression(@NotNull LSFWherePropertyExpression o) {
    visitPsiElement(o);
  }

  public void visitWhileActionPropertyDefinitionBody(@NotNull LSFWhileActionPropertyDefinitionBody o) {
    visitActionExpression(o);
  }

  public void visitWindowCreateStatement(@NotNull LSFWindowCreateStatement o) {
    visitWindowDeclaration(o);
  }

  public void visitWindowHideStatement(@NotNull LSFWindowHideStatement o) {
    visitPsiElement(o);
  }

  public void visitWindowOptions(@NotNull LSFWindowOptions o) {
    visitPsiElement(o);
  }

  public void visitWindowStatement(@NotNull LSFWindowStatement o) {
    visitPsiElement(o);
  }

  public void visitWindowType(@NotNull LSFWindowType o) {
    visitPsiElement(o);
  }

  public void visitWindowTypeLiteral(@NotNull LSFWindowTypeLiteral o) {
    visitPsiElement(o);
  }

  public void visitWindowUsage(@NotNull LSFWindowUsage o) {
    visitWindowReference(o);
  }

  public void visitWriteActionPropertyDefinitionBody(@NotNull LSFWriteActionPropertyDefinitionBody o) {
    visitActionExpression(o);
  }

  public void visitWriteWhenStatement(@NotNull LSFWriteWhenStatement o) {
    visitModifyParamContext(o);
  }

  public void visitXorPE(@NotNull LSFXorPE o) {
    visitExpression(o);
  }

  public void visitDocumentation(@NotNull LSFDocumentation o) {
    visitPsiElement(o);
  }

  public void visitModifyParamContext(@NotNull ModifyParamContext o) {
    visitElement(o);
  }

  public void visitExpressionStringValueLiteral(@NotNull LSFExpressionStringValueLiteral o) {
    visitPsiElement(o);
  }

  public void visitId(@NotNull LSFId o) {
    visitPsiElement(o);
  }

  public void visitPsiNamedElement(@NotNull PsiNamedElement o) {
    visitElement(o);
  }

  public void visitActionExpression(@NotNull ActionExpression o) {
    visitElement(o);
  }

  public void visitLocalizedStringValueLiteral(@NotNull LSFLocalizedStringValueLiteral o) {
    visitPsiElement(o);
  }

  public void visitMetacodeStringValueLiteral(@NotNull LSFMetacodeStringValueLiteral o) {
    visitPsiElement(o);
  }

  public void visitStringValueLiteral(@NotNull LSFStringValueLiteral o) {
    visitPsiElement(o);
  }

  public void visitExtendDoParamContext(@NotNull ExtendDoParamContext o) {
    visitElement(o);
  }

  public void visitExtendParamContext(@NotNull ExtendParamContext o) {
    visitElement(o);
  }

  public void visitFormContext(@NotNull FormContext o) {
    visitElement(o);
  }

  public void visitClassParamDeclareContext(@NotNull ClassParamDeclareContext o) {
    visitElement(o);
  }

  public void visitImportFieldParamDeclaration(@NotNull LSFImportFieldParamDeclaration o) {
    visitPsiElement(o);
  }

  public void visitObjectInputParamDeclaration(@NotNull LSFObjectInputParamDeclaration o) {
    visitPsiElement(o);
  }

  public void visitExpression(@NotNull LSFExpression o) {
    visitPsiElement(o);
  }

  public void visitUnfriendlyPE(@NotNull UnfriendlyPE o) {
    visitElement(o);
  }

  public void visitPropertyUsageContext(@NotNull PropertyUsageContext o) {
    visitElement(o);
  }

  public void visitWindowDeclaration(@NotNull LSFWindowDeclaration o) {
    visitPsiElement(o);
  }

  public void visitAggrParamGlobalPropDeclaration(@NotNull LSFAggrParamGlobalPropDeclaration o) {
    visitPsiElement(o);
  }

  public void visitBaseEventActionDeclaration(@NotNull LSFBaseEventActionDeclaration o) {
    visitPsiElement(o);
  }

  public void visitClassDeclaration(@NotNull LSFClassDeclaration o) {
    visitPsiElement(o);
  }

  public void visitComponentDeclaration(@NotNull LSFComponentDeclaration o) {
    visitPsiElement(o);
  }

  public void visitExplicitInterfaceActionStatement(@NotNull LSFExplicitInterfaceActionStatement o) {
    visitPsiElement(o);
  }

  public void visitExplicitInterfacePropStatement(@NotNull LSFExplicitInterfacePropStatement o) {
    visitPsiElement(o);
  }

  public void visitExplicitNamespaceDeclaration(@NotNull LSFExplicitNamespaceDeclaration o) {
    visitPsiElement(o);
  }

  public void visitExplicitValuePropStatement(@NotNull LSFExplicitValuePropStatement o) {
    visitPsiElement(o);
  }

  public void visitFilterGroupDeclaration(@NotNull LSFFilterGroupDeclaration o) {
    visitPsiElement(o);
  }

  public void visitFormDeclaration(@NotNull LSFFormDeclaration o) {
    visitPsiElement(o);
  }

  public void visitGroupDeclaration(@NotNull LSFGroupDeclaration o) {
    visitPsiElement(o);
  }

  public void visitGroupObjectDeclaration(@NotNull LSFGroupObjectDeclaration o) {
    visitPsiElement(o);
  }

  public void visitImplicitInterfacePropStatement(@NotNull LSFImplicitInterfacePropStatement o) {
    visitPsiElement(o);
  }

  public void visitImplicitValuePropStatement(@NotNull LSFImplicitValuePropStatement o) {
    visitPsiElement(o);
  }

  public void visitLocalPropDeclaration(@NotNull LSFLocalPropDeclaration o) {
    visitPsiElement(o);
  }

  public void visitMetaDeclaration(@NotNull LSFMetaDeclaration o) {
    visitPsiElement(o);
  }

  public void visitModuleDeclaration(@NotNull LSFModuleDeclaration o) {
    visitPsiElement(o);
  }

  public void visitNavigatorElementDeclaration(@NotNull LSFNavigatorElementDeclaration o) {
    visitPsiElement(o);
  }

  public void visitObjectDeclaration(@NotNull LSFObjectDeclaration o) {
    visitPsiElement(o);
  }

  public void visitParamDeclaration(@NotNull LSFParamDeclaration o) {
    visitPsiElement(o);
  }

  public void visitPropertyDrawMappedDeclaration(@NotNull LSFPropertyDrawMappedDeclaration o) {
    visitPsiElement(o);
  }

  public void visitPropertyDrawNameDeclaration(@NotNull LSFPropertyDrawNameDeclaration o) {
    visitPsiElement(o);
  }

  public void visitInterfacePropStatement(@NotNull LSFInterfacePropStatement o) {
    visitPsiElement(o);
  }

  public void visitStaticObjectDeclaration(@NotNull LSFStaticObjectDeclaration o) {
    visitPsiElement(o);
  }

  public void visitTableDeclaration(@NotNull LSFTableDeclaration o) {
    visitPsiElement(o);
  }

  public void visitTreeGroupDecl(@NotNull LSFTreeGroupDecl o) {
    visitPsiElement(o);
  }

  public void visitClassExtend(@NotNull LSFClassExtend o) {
    visitPsiElement(o);
  }

  public void visitActionReference(@NotNull LSFActionReference o) {
    visitPsiElement(o);
  }

  public void visitClassReference(@NotNull LSFClassReference o) {
    visitPsiElement(o);
  }

  public void visitComponentReference(@NotNull LSFComponentReference o) {
    visitPsiElement(o);
  }

  public void visitExprParamReference(@NotNull LSFExprParamReference o) {
    visitPsiElement(o);
  }

  public void visitFilterGroupReference(@NotNull LSFFilterGroupReference o) {
    visitPsiElement(o);
  }

  public void visitFormElseNoParamsActionReference(@NotNull LSFFormElseNoParamsActionReference o) {
    visitPsiElement(o);
  }

  public void visitFormReference(@NotNull LSFFormReference o) {
    visitPsiElement(o);
  }

  public void visitGroupObjectReference(@NotNull LSFGroupObjectReference o) {
    visitPsiElement(o);
  }

  public void visitGroupReference(@NotNull LSFGroupReference o) {
    visitPsiElement(o);
  }

  public void visitJavaClassStringReference(@NotNull LSFJavaClassStringReference o) {
    visitPsiElement(o);
  }

  public void visitMetaReference(@NotNull LSFMetaReference o) {
    visitPsiElement(o);
  }

  public void visitModuleReference(@NotNull LSFModuleReference o) {
    visitPsiElement(o);
  }

  public void visitNamespaceReference(@NotNull LSFNamespaceReference o) {
    visitPsiElement(o);
  }

  public void visitNavigatorElementReference(@NotNull LSFNavigatorElementReference o) {
    visitPsiElement(o);
  }

  public void visitObjectReference(@NotNull LSFObjectReference o) {
    visitPsiElement(o);
  }

  public void visitPropElseActionReference(@NotNull LSFPropElseActionReference o) {
    visitPsiElement(o);
  }

  public void visitPropReference(@NotNull LSFPropReference o) {
    visitPsiElement(o);
  }

  public void visitPropertyDrawReference(@NotNull LSFPropertyDrawReference o) {
    visitPsiElement(o);
  }

  public void visitStaticObjectReference(@NotNull LSFStaticObjectReference o) {
    visitPsiElement(o);
  }

  public void visitTableReference(@NotNull LSFTableReference o) {
    visitPsiElement(o);
  }

  public void visitTreeGroupReference(@NotNull LSFTreeGroupReference o) {
    visitPsiElement(o);
  }

  public void visitWindowReference(@NotNull LSFWindowReference o) {
    visitPsiElement(o);
  }

  public void visitPsiElement(@NotNull PsiElement o) {
    visitElement(o);
  }

}
