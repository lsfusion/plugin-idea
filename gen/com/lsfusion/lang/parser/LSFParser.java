// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static com.lsfusion.lang.psi.LSFTypes.*;
import static com.lsfusion.lang.parser.LSFParserUtil.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;
import com.intellij.lang.LightPsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class LSFParser implements PsiParser, LightPsiParser {

  public ASTNode parse(IElementType t, PsiBuilder b) {
    parseLight(t, b);
    return b.getTreeBuilt();
  }

  public void parseLight(IElementType t, PsiBuilder b) {
    boolean r;
    b = adapt_builder_(t, b, this, null);
    Marker m = enter_section_(b, 0, _COLLAPSE_, null, null);
    r = parse_root_(t, b);
    exit_section_(b, 0, m, t, r, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(IElementType t, PsiBuilder b) {
    return parse_root_(t, b, 0);
  }

  boolean parse_root_(IElementType t, PsiBuilder b, int l) {
    return script(b, l + 1);
  }

  /* ********************************************************** */
  // ABSTRACT
  //                                     (   ((CASE | MULTI) abstractExclusiveOverrideOption?)
  //                                     |	(LIST abstractCaseAddOption?)
  //                                     )?
  //                                     (FULL)?
  //                                     (LBRAC classNameList RBRAC)?
  public boolean abstractActionPropertyDefinition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "abstractActionPropertyDefinition")) return false;
    if (!nextTokenIs(b, ABSTRACT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, ABSTRACT_ACTION_PROPERTY_DEFINITION, null);
    r = consumeToken(b, ABSTRACT);
    p = r; // pin = 1
    r = r && abstractActionPropertyDefinition_1(b, l + 1);
    r = r && abstractActionPropertyDefinition_2(b, l + 1);
    r = r && abstractActionPropertyDefinition_3(b, l + 1);
    exit_section_(b, l, m, ABSTRACT_ACTION_PROPERTY_DEFINITION, r, p, null);
    return r || p;
  }

  // (   ((CASE | MULTI) abstractExclusiveOverrideOption?)
  //                                     |	(LIST abstractCaseAddOption?)
  //                                     )?
  private boolean abstractActionPropertyDefinition_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "abstractActionPropertyDefinition_1")) return false;
    abstractActionPropertyDefinition_1_0(b, l + 1);
    return true;
  }

  // ((CASE | MULTI) abstractExclusiveOverrideOption?)
  //                                     |	(LIST abstractCaseAddOption?)
  private boolean abstractActionPropertyDefinition_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "abstractActionPropertyDefinition_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = abstractActionPropertyDefinition_1_0_0(b, l + 1);
    if (!r) r = abstractActionPropertyDefinition_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (CASE | MULTI) abstractExclusiveOverrideOption?
  private boolean abstractActionPropertyDefinition_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "abstractActionPropertyDefinition_1_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = abstractActionPropertyDefinition_1_0_0_0(b, l + 1);
    r = r && abstractActionPropertyDefinition_1_0_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // CASE | MULTI
  private boolean abstractActionPropertyDefinition_1_0_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "abstractActionPropertyDefinition_1_0_0_0")) return false;
    boolean r;
    r = consumeToken(b, CASE);
    if (!r) r = consumeToken(b, MULTI);
    return r;
  }

  // abstractExclusiveOverrideOption?
  private boolean abstractActionPropertyDefinition_1_0_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "abstractActionPropertyDefinition_1_0_0_1")) return false;
    abstractExclusiveOverrideOption(b, l + 1);
    return true;
  }

  // LIST abstractCaseAddOption?
  private boolean abstractActionPropertyDefinition_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "abstractActionPropertyDefinition_1_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, LIST);
    r = r && abstractActionPropertyDefinition_1_0_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // abstractCaseAddOption?
  private boolean abstractActionPropertyDefinition_1_0_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "abstractActionPropertyDefinition_1_0_1_1")) return false;
    abstractCaseAddOption(b, l + 1);
    return true;
  }

  // (FULL)?
  private boolean abstractActionPropertyDefinition_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "abstractActionPropertyDefinition_2")) return false;
    consumeToken(b, FULL);
    return true;
  }

  // (LBRAC classNameList RBRAC)?
  private boolean abstractActionPropertyDefinition_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "abstractActionPropertyDefinition_3")) return false;
    abstractActionPropertyDefinition_3_0(b, l + 1);
    return true;
  }

  // LBRAC classNameList RBRAC
  private boolean abstractActionPropertyDefinition_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "abstractActionPropertyDefinition_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, LBRAC);
    r = r && classNameList(b, l + 1);
    r = r && consumeToken(b, RBRAC);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // FIRST | LAST
  public boolean abstractCaseAddOption(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "abstractCaseAddOption")) return false;
    if (!nextTokenIs(b, "<abstract case add option>", FIRST, LAST)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ABSTRACT_CASE_ADD_OPTION, "<abstract case add option>");
    r = consumeToken(b, FIRST);
    if (!r) r = consumeToken(b, LAST);
    exit_section_(b, l, m, ABSTRACT_CASE_ADD_OPTION, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // (OVERRIDE abstractCaseAddOption? ) | EXCLUSIVE
  public boolean abstractExclusiveOverrideOption(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "abstractExclusiveOverrideOption")) return false;
    if (!nextTokenIs(b, "<abstract exclusive override option>", EXCLUSIVE, OVERRIDE)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ABSTRACT_EXCLUSIVE_OVERRIDE_OPTION, "<abstract exclusive override option>");
    r = abstractExclusiveOverrideOption_0(b, l + 1);
    if (!r) r = consumeToken(b, EXCLUSIVE);
    exit_section_(b, l, m, ABSTRACT_EXCLUSIVE_OVERRIDE_OPTION, r, false, null);
    return r;
  }

  // OVERRIDE abstractCaseAddOption?
  private boolean abstractExclusiveOverrideOption_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "abstractExclusiveOverrideOption_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, OVERRIDE);
    r = r && abstractExclusiveOverrideOption_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // abstractCaseAddOption?
  private boolean abstractExclusiveOverrideOption_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "abstractExclusiveOverrideOption_0_1")) return false;
    abstractCaseAddOption(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // ABSTRACT ((CASE | MULTI | VALUE) abstractExclusiveOverrideOption?)? 
  //                                (FULL)? className (LBRAC classNameList RBRAC)?
  public boolean abstractPropertyDefinition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "abstractPropertyDefinition")) return false;
    if (!nextTokenIs(b, ABSTRACT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, ABSTRACT_PROPERTY_DEFINITION, null);
    r = consumeToken(b, ABSTRACT);
    p = r; // pin = 1
    r = r && abstractPropertyDefinition_1(b, l + 1);
    r = r && abstractPropertyDefinition_2(b, l + 1);
    r = r && className(b, l + 1);
    r = r && abstractPropertyDefinition_4(b, l + 1);
    exit_section_(b, l, m, ABSTRACT_PROPERTY_DEFINITION, r, p, null);
    return r || p;
  }

  // ((CASE | MULTI | VALUE) abstractExclusiveOverrideOption?)?
  private boolean abstractPropertyDefinition_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "abstractPropertyDefinition_1")) return false;
    abstractPropertyDefinition_1_0(b, l + 1);
    return true;
  }

  // (CASE | MULTI | VALUE) abstractExclusiveOverrideOption?
  private boolean abstractPropertyDefinition_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "abstractPropertyDefinition_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = abstractPropertyDefinition_1_0_0(b, l + 1);
    r = r && abstractPropertyDefinition_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // CASE | MULTI | VALUE
  private boolean abstractPropertyDefinition_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "abstractPropertyDefinition_1_0_0")) return false;
    boolean r;
    r = consumeToken(b, CASE);
    if (!r) r = consumeToken(b, MULTI);
    if (!r) r = consumeToken(b, VALUE);
    return r;
  }

  // abstractExclusiveOverrideOption?
  private boolean abstractPropertyDefinition_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "abstractPropertyDefinition_1_0_1")) return false;
    abstractExclusiveOverrideOption(b, l + 1);
    return true;
  }

  // (FULL)?
  private boolean abstractPropertyDefinition_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "abstractPropertyDefinition_2")) return false;
    consumeToken(b, FULL);
    return true;
  }

  // (LBRAC classNameList RBRAC)?
  private boolean abstractPropertyDefinition_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "abstractPropertyDefinition_4")) return false;
    abstractPropertyDefinition_4_0(b, l + 1);
    return true;
  }

  // LBRAC classNameList RBRAC
  private boolean abstractPropertyDefinition_4_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "abstractPropertyDefinition_4_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, LBRAC);
    r = r && classNameList(b, l + 1);
    r = r && consumeToken(b, RBRAC);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // WHEN propertyExpression (THEN actionPropertyDefinitionBody)
  public boolean actionCaseBranchBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "actionCaseBranchBody")) return false;
    if (!nextTokenIs(b, WHEN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, ACTION_CASE_BRANCH_BODY, null);
    r = consumeToken(b, WHEN);
    p = r; // pin = 1
    r = r && propertyExpression(b, l + 1);
    r = r && actionCaseBranchBody_2(b, l + 1);
    exit_section_(b, l, m, ACTION_CASE_BRANCH_BODY, r, p, null);
    return r || p;
  }

  // THEN actionPropertyDefinitionBody
  private boolean actionCaseBranchBody_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "actionCaseBranchBody_2")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, THEN);
    p = r; // pin = 1
    r = r && actionPropertyDefinitionBody(b, l + 1);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // ACTION actionUsage | propertyElseActionUsage
  boolean actionOrPropertyUsage(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "actionOrPropertyUsage")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = actionOrPropertyUsage_0(b, l + 1);
    if (!r) r = propertyElseActionUsage(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ACTION actionUsage
  private boolean actionOrPropertyUsage_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "actionOrPropertyUsage_0")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, ACTION);
    p = r; // pin = 1
    r = r && actionUsage(b, l + 1);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // leafActionPDB
  //                                     |   recursiveActionPDB
  public boolean actionPropertyDefinitionBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "actionPropertyDefinitionBody")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ACTION_PROPERTY_DEFINITION_BODY, "<action property definition body>");
    r = leafActionPDB(b, l + 1);
    if (!r) r = recursiveActionPDB(b, l + 1);
    exit_section_(b, l, m, ACTION_PROPERTY_DEFINITION_BODY, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // propertyDeclaration
  //                         (   actionUnfriendlyPD (nonEmptyPropertyOptions | SEMI)
  //                         |   topActionPropertyDefinitionBody nonEmptyPropertyOptions?
  //                         )
  public boolean actionStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "actionStatement")) return false;
    if (!nextTokenIs(b, ID)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, ACTION_STATEMENT, null);
    r = propertyDeclaration(b, l + 1);
    p = r; // pin = 1
    r = r && actionStatement_1(b, l + 1);
    exit_section_(b, l, m, ACTION_STATEMENT, r, p, null);
    return r || p;
  }

  // actionUnfriendlyPD (nonEmptyPropertyOptions | SEMI)
  //                         |   topActionPropertyDefinitionBody nonEmptyPropertyOptions?
  private boolean actionStatement_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "actionStatement_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = actionStatement_1_0(b, l + 1);
    if (!r) r = actionStatement_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // actionUnfriendlyPD (nonEmptyPropertyOptions | SEMI)
  private boolean actionStatement_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "actionStatement_1_0")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = actionUnfriendlyPD(b, l + 1);
    p = r; // pin = 1
    r = r && actionStatement_1_0_1(b, l + 1);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  // nonEmptyPropertyOptions | SEMI
  private boolean actionStatement_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "actionStatement_1_0_1")) return false;
    boolean r;
    r = nonEmptyPropertyOptions(b, l + 1);
    if (!r) r = consumeToken(b, SEMI);
    return r;
  }

  // topActionPropertyDefinitionBody nonEmptyPropertyOptions?
  private boolean actionStatement_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "actionStatement_1_1")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = topActionPropertyDefinitionBody(b, l + 1);
    p = r; // pin = 1
    r = r && actionStatement_1_1_1(b, l + 1);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  // nonEmptyPropertyOptions?
  private boolean actionStatement_1_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "actionStatement_1_1_1")) return false;
    nonEmptyPropertyOptions(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // abstractActionPropertyDefinition
  //                         |	customActionPropertyDefinitionBody
  public boolean actionUnfriendlyPD(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "actionUnfriendlyPD")) return false;
    if (!nextTokenIs(b, "<action unfriendly pd>", ABSTRACT, INTERNAL)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ACTION_UNFRIENDLY_PD, "<action unfriendly pd>");
    r = abstractActionPropertyDefinition(b, l + 1);
    if (!r) r = customActionPropertyDefinitionBody(b, l + 1);
    exit_section_(b, l, m, ACTION_UNFRIENDLY_PD, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // compoundID explicitPropClassUsage?
  public boolean actionUsage(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "actionUsage")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ACTION_USAGE, "<action usage>");
    r = compoundID(b, l + 1);
    r = r && actionUsage_1(b, l + 1);
    exit_section_(b, l, m, ACTION_USAGE, r, false, null);
    return r;
  }

  // explicitPropClassUsage?
  private boolean actionUsage_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "actionUsage_1")) return false;
    explicitPropClassUsage(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // actionUsage
  public boolean actionUsageWrapper(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "actionUsageWrapper")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ACTION_USAGE_WRAPPER, "<action usage wrapper>");
    r = actionUsage(b, l + 1);
    exit_section_(b, l, m, ACTION_USAGE_WRAPPER, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // ACTIVATE (  FORM formUsage 
  //                                                    | TAB componentID
  //                                                    | PROPERTY formPropertyDrawID
  //                                                    )
  public boolean activateActionPropertyDefinitionBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "activateActionPropertyDefinitionBody")) return false;
    if (!nextTokenIs(b, ACTIVATE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, ACTIVATE_ACTION_PROPERTY_DEFINITION_BODY, null);
    r = consumeToken(b, ACTIVATE);
    p = r; // pin = 1
    r = r && activateActionPropertyDefinitionBody_1(b, l + 1);
    exit_section_(b, l, m, ACTIVATE_ACTION_PROPERTY_DEFINITION_BODY, r, p, null);
    return r || p;
  }

  // FORM formUsage 
  //                                                    | TAB componentID
  //                                                    | PROPERTY formPropertyDrawID
  private boolean activateActionPropertyDefinitionBody_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "activateActionPropertyDefinitionBody_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = activateActionPropertyDefinitionBody_1_0(b, l + 1);
    if (!r) r = activateActionPropertyDefinitionBody_1_1(b, l + 1);
    if (!r) r = activateActionPropertyDefinitionBody_1_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // FORM formUsage
  private boolean activateActionPropertyDefinitionBody_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "activateActionPropertyDefinitionBody_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, FORM);
    r = r && formUsage(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // TAB componentID
  private boolean activateActionPropertyDefinitionBody_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "activateActionPropertyDefinitionBody_1_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, TAB);
    r = r && componentID(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // PROPERTY formPropertyDrawID
  private boolean activateActionPropertyDefinitionBody_1_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "activateActionPropertyDefinitionBody_1_2")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, PROPERTY);
    r = r && formPropertyDrawID(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // ACTIVE FORM formUsage
  public boolean activeFormActionPropertyDefinitionBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "activeFormActionPropertyDefinitionBody")) return false;
    if (!nextTokenIs(b, ACTIVE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, ACTIVE_FORM_ACTION_PROPERTY_DEFINITION_BODY, null);
    r = consumeTokens(b, 1, ACTIVE, FORM);
    p = r; // pin = 1
    r = r && formUsage(b, l + 1);
    exit_section_(b, l, m, ACTIVE_FORM_ACTION_PROPERTY_DEFINITION_BODY, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // ACTIVE TAB componentID
  public boolean activeTabPropertyDefinition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "activeTabPropertyDefinition")) return false;
    if (!nextTokenIs(b, ACTIVE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, ACTIVE_TAB_PROPERTY_DEFINITION, null);
    r = consumeTokens(b, 1, ACTIVE, TAB);
    p = r; // pin = 1
    r = r && componentID(b, l + 1);
    exit_section_(b, l, m, ACTIVE_TAB_PROPERTY_DEFINITION, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // additivePE (ADDOR_OPERAND additivePE)*
  public boolean additiveORPE(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "additiveORPE")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ADDITIVE_ORPE, "<additive orpe>");
    r = additivePE(b, l + 1);
    r = r && additiveORPE_1(b, l + 1);
    exit_section_(b, l, m, ADDITIVE_ORPE, r, false, null);
    return r;
  }

  // (ADDOR_OPERAND additivePE)*
  private boolean additiveORPE_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "additiveORPE_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!additiveORPE_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "additiveORPE_1", c)) break;
    }
    return true;
  }

  // ADDOR_OPERAND additivePE
  private boolean additiveORPE_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "additiveORPE_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, ADDOR_OPERAND);
    r = r && additivePE(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // multiplicativePE ((PLUS | MINUS) multiplicativePE)*
  public boolean additivePE(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "additivePE")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ADDITIVE_PE, "<additive pe>");
    r = multiplicativePE(b, l + 1);
    r = r && additivePE_1(b, l + 1);
    exit_section_(b, l, m, ADDITIVE_PE, r, false, null);
    return r;
  }

  // ((PLUS | MINUS) multiplicativePE)*
  private boolean additivePE_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "additivePE_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!additivePE_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "additivePE_1", c)) break;
    }
    return true;
  }

  // (PLUS | MINUS) multiplicativePE
  private boolean additivePE_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "additivePE_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = additivePE_1_0_0(b, l + 1);
    r = r && multiplicativePE(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // PLUS | MINUS
  private boolean additivePE_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "additivePE_1_0_0")) return false;
    boolean r;
    r = consumeToken(b, PLUS);
    if (!r) r = consumeToken(b, MINUS);
    return r;
  }

  /* ********************************************************** */
  // className paramDeclare
  public boolean aggrParamPropDeclare(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "aggrParamPropDeclare")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, AGGR_PARAM_PROP_DECLARE, "<aggr param prop declare>");
    r = className(b, l + 1);
    r = r && paramDeclare(b, l + 1);
    exit_section_(b, l, m, AGGR_PARAM_PROP_DECLARE, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // AGGR baseEventPE customClassUsage
  //                            WHERE propertyExpression
  //                            (NEW baseEventNotPE)?
  //                            (DELETE baseEventNotPE)?
  public boolean aggrPropertyDefinition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "aggrPropertyDefinition")) return false;
    if (!nextTokenIs(b, AGGR)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, AGGR_PROPERTY_DEFINITION, null);
    r = consumeToken(b, AGGR);
    p = r; // pin = 1
    r = r && baseEventPE(b, l + 1);
    r = r && customClassUsage(b, l + 1);
    r = r && consumeToken(b, WHERE);
    r = r && propertyExpression(b, l + 1);
    r = r && aggrPropertyDefinition_5(b, l + 1);
    r = r && aggrPropertyDefinition_6(b, l + 1);
    exit_section_(b, l, m, AGGR_PROPERTY_DEFINITION, r, p, null);
    return r || p;
  }

  // (NEW baseEventNotPE)?
  private boolean aggrPropertyDefinition_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "aggrPropertyDefinition_5")) return false;
    aggrPropertyDefinition_5_0(b, l + 1);
    return true;
  }

  // NEW baseEventNotPE
  private boolean aggrPropertyDefinition_5_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "aggrPropertyDefinition_5_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, NEW);
    r = r && baseEventNotPE(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (DELETE baseEventNotPE)?
  private boolean aggrPropertyDefinition_6(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "aggrPropertyDefinition_6")) return false;
    aggrPropertyDefinition_6_0(b, l + 1);
    return true;
  }

  // DELETE baseEventNotPE
  private boolean aggrPropertyDefinition_6_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "aggrPropertyDefinition_6_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, DELETE);
    r = r && baseEventNotPE(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // AGGR
  public boolean aggrSetting(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "aggrSetting")) return false;
    if (!nextTokenIs(b, AGGR)) return false;
    boolean r;
    Marker m = enter_section_(b, l, AGGR_SETTING);
    r = consumeToken(b, AGGR);
    exit_section_(b, m, AGGR_SETTING, r);
    return r;
  }

  /* ********************************************************** */
  // simpleName
  public boolean aliasUsage(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "aliasUsage")) return false;
    if (!nextTokenIs(b, ID)) return false;
    boolean r;
    Marker m = enter_section_(b, l, ALIAS_USAGE);
    r = simpleName(b, l + 1);
    exit_section_(b, m, ALIAS_USAGE, r);
    return r;
  }

  /* ********************************************************** */
  // ((ID | stringLiteral) EQUALS)? propertyExpression
  public boolean aliasedPropertyExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "aliasedPropertyExpression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ALIASED_PROPERTY_EXPRESSION, "<aliased property expression>");
    r = aliasedPropertyExpression_0(b, l + 1);
    r = r && propertyExpression(b, l + 1);
    exit_section_(b, l, m, ALIASED_PROPERTY_EXPRESSION, r, false, null);
    return r;
  }

  // ((ID | stringLiteral) EQUALS)?
  private boolean aliasedPropertyExpression_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "aliasedPropertyExpression_0")) return false;
    aliasedPropertyExpression_0_0(b, l + 1);
    return true;
  }

  // (ID | stringLiteral) EQUALS
  private boolean aliasedPropertyExpression_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "aliasedPropertyExpression_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = aliasedPropertyExpression_0_0_0(b, l + 1);
    r = r && consumeToken(b, EQUALS);
    exit_section_(b, m, null, r);
    return r;
  }

  // ID | stringLiteral
  private boolean aliasedPropertyExpression_0_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "aliasedPropertyExpression_0_0_0")) return false;
    boolean r;
    r = consumeToken(b, ID);
    if (!r) r = stringLiteral(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // START | CENTER | END
  public boolean alignmentLiteral(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "alignmentLiteral")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ALIGNMENT_LITERAL, "<alignment literal>");
    r = consumeToken(b, START);
    if (!r) r = consumeToken(b, CENTER);
    if (!r) r = consumeToken(b, END);
    exit_section_(b, l, m, ALIGNMENT_LITERAL, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // notPE (AND notPE)*
  public boolean andPE(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "andPE")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, AND_PE, "<and pe>");
    r = notPE(b, l + 1);
    r = r && andPE_1(b, l + 1);
    exit_section_(b, l, m, AND_PE, r, false, null);
    return r;
  }

  // (AND notPE)*
  private boolean andPE_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "andPE_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!andPE_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "andPE_1", c)) break;
    }
    return true;
  }

  // AND notPE
  private boolean andPE_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "andPE_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, AND);
    r = r && notPE(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // APPLY nestedPropertiesSelector? SINGLE? SERIALIZABLE? actionPropertyDefinitionBody
  public boolean applyActionPropertyDefinitionBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "applyActionPropertyDefinitionBody")) return false;
    if (!nextTokenIs(b, APPLY)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, APPLY_ACTION_PROPERTY_DEFINITION_BODY, null);
    r = consumeToken(b, APPLY);
    p = r; // pin = 1
    r = r && applyActionPropertyDefinitionBody_1(b, l + 1);
    r = r && applyActionPropertyDefinitionBody_2(b, l + 1);
    r = r && applyActionPropertyDefinitionBody_3(b, l + 1);
    r = r && actionPropertyDefinitionBody(b, l + 1);
    exit_section_(b, l, m, APPLY_ACTION_PROPERTY_DEFINITION_BODY, r, p, null);
    return r || p;
  }

  // nestedPropertiesSelector?
  private boolean applyActionPropertyDefinitionBody_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "applyActionPropertyDefinitionBody_1")) return false;
    nestedPropertiesSelector(b, l + 1);
    return true;
  }

  // SINGLE?
  private boolean applyActionPropertyDefinitionBody_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "applyActionPropertyDefinitionBody_2")) return false;
    consumeToken(b, SINGLE);
    return true;
  }

  // SERIALIZABLE?
  private boolean applyActionPropertyDefinitionBody_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "applyActionPropertyDefinitionBody_3")) return false;
    consumeToken(b, SERIALIZABLE);
    return true;
  }

  /* ********************************************************** */
  // ASON formEventType noContextActionOrPropertyUsage
  public boolean asEditActionSetting(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "asEditActionSetting")) return false;
    if (!nextTokenIs(b, ASON)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, AS_EDIT_ACTION_SETTING, null);
    r = consumeToken(b, ASON);
    p = r; // pin = 1
    r = r && formEventType(b, l + 1);
    r = r && noContextActionOrPropertyUsage(b, l + 1);
    exit_section_(b, l, m, AS_EDIT_ACTION_SETTING, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // AFTER
  public boolean aspectAfter(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "aspectAfter")) return false;
    if (!nextTokenIs(b, AFTER)) return false;
    boolean r;
    Marker m = enter_section_(b, l, ASPECT_AFTER);
    r = consumeToken(b, AFTER);
    exit_section_(b, m, ASPECT_AFTER, r);
    return r;
  }

  /* ********************************************************** */
  // BEFORE
  public boolean aspectBefore(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "aspectBefore")) return false;
    if (!nextTokenIs(b, BEFORE)) return false;
    boolean r;
    Marker m = enter_section_(b, l, ASPECT_BEFORE);
    r = consumeToken(b, BEFORE);
    exit_section_(b, m, ASPECT_BEFORE, r);
    return r;
  }

  /* ********************************************************** */
  // (aspectBefore | aspectAfter) mappedActionClassParamDeclare DO actionPropertyDefinitionBody
  public boolean aspectStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "aspectStatement")) return false;
    if (!nextTokenIs(b, "<aspect statement>", AFTER, BEFORE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, ASPECT_STATEMENT, "<aspect statement>");
    r = aspectStatement_0(b, l + 1);
    p = r; // pin = 1
    r = r && mappedActionClassParamDeclare(b, l + 1);
    r = r && consumeToken(b, DO);
    r = r && actionPropertyDefinitionBody(b, l + 1);
    exit_section_(b, l, m, ASPECT_STATEMENT, r, p, null);
    return r || p;
  }

  // aspectBefore | aspectAfter
  private boolean aspectStatement_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "aspectStatement_0")) return false;
    boolean r;
    r = aspectBefore(b, l + 1);
    if (!r) r = aspectAfter(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // assignActionPropertyDefinitionBody1 | assignActionPropertyDefinitionBody2
  public boolean assignActionPropertyDefinitionBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "assignActionPropertyDefinitionBody")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ASSIGN_ACTION_PROPERTY_DEFINITION_BODY, "<assign action property definition body>");
    r = assignActionPropertyDefinitionBody1(b, l + 1);
    if (!r) r = assignActionPropertyDefinitionBody2(b, l + 1);
    exit_section_(b, l, m, ASSIGN_ACTION_PROPERTY_DEFINITION_BODY, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // CHANGE changePropertyBody ARROW propertyExpression (WHERE propertyExpression)?
  boolean assignActionPropertyDefinitionBody1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "assignActionPropertyDefinitionBody1")) return false;
    if (!nextTokenIs(b, CHANGE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, CHANGE);
    p = r; // pin = 1
    r = r && changePropertyBody(b, l + 1);
    r = r && consumeToken(b, ARROW);
    r = r && propertyExpression(b, l + 1);
    r = r && assignActionPropertyDefinitionBody1_4(b, l + 1);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  // (WHERE propertyExpression)?
  private boolean assignActionPropertyDefinitionBody1_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "assignActionPropertyDefinitionBody1_4")) return false;
    assignActionPropertyDefinitionBody1_4_0(b, l + 1);
    return true;
  }

  // WHERE propertyExpression
  private boolean assignActionPropertyDefinitionBody1_4_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "assignActionPropertyDefinitionBody1_4_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, WHERE);
    r = r && propertyExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // changePropertyBody ARROW propertyExpression (WHERE propertyExpression)?
  boolean assignActionPropertyDefinitionBody2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "assignActionPropertyDefinitionBody2")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = changePropertyBody(b, l + 1);
    r = r && consumeToken(b, ARROW);
    p = r; // pin = 2
    r = r && propertyExpression(b, l + 1);
    r = r && assignActionPropertyDefinitionBody2_3(b, l + 1);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  // (WHERE propertyExpression)?
  private boolean assignActionPropertyDefinitionBody2_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "assignActionPropertyDefinitionBody2_3")) return false;
    assignActionPropertyDefinitionBody2_3_0(b, l + 1);
    return true;
  }

  // WHERE propertyExpression
  private boolean assignActionPropertyDefinitionBody2_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "assignActionPropertyDefinitionBody2_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, WHERE);
    r = r && propertyExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // ASYNCUPDATE propertyExpression
  public boolean asyncUpdateActionPropertyDefinitionBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "asyncUpdateActionPropertyDefinitionBody")) return false;
    if (!nextTokenIs(b, ASYNCUPDATE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, ASYNC_UPDATE_ACTION_PROPERTY_DEFINITION_BODY, null);
    r = consumeToken(b, ASYNCUPDATE);
    p = r; // pin = 1
    r = r && propertyExpression(b, l + 1);
    exit_section_(b, l, m, ASYNC_UPDATE_ACTION_PROPERTY_DEFINITION_BODY, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // AUTOREFRESH intLiteral
  public boolean autorefreshLiteral(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "autorefreshLiteral")) return false;
    if (!nextTokenIs(b, AUTOREFRESH)) return false;
    boolean r;
    Marker m = enter_section_(b, l, AUTOREFRESH_LITERAL);
    r = consumeToken(b, AUTOREFRESH);
    r = r && intLiteral(b, l + 1);
    exit_section_(b, m, AUTOREFRESH_LITERAL, r);
    return r;
  }

  /* ********************************************************** */
  // AUTOSET
  public boolean autosetSetting(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "autosetSetting")) return false;
    if (!nextTokenIs(b, AUTOSET)) return false;
    boolean r;
    Marker m = enter_section_(b, l, AUTOSET_SETTING);
    r = consumeToken(b, AUTOSET);
    exit_section_(b, m, AUTOSET_SETTING, r);
    return r;
  }

  /* ********************************************************** */
  // (GLOBAL | LOCAL)? (FORMS nonEmptyFormUsageList)? ((GOAFTER | AFTER) nonEmptyNoContextActionOrPropertyUsageList)?
  public boolean baseEvent(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "baseEvent")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, BASE_EVENT, "<base event>");
    r = baseEvent_0(b, l + 1);
    r = r && baseEvent_1(b, l + 1);
    r = r && baseEvent_2(b, l + 1);
    exit_section_(b, l, m, BASE_EVENT, r, false, null);
    return r;
  }

  // (GLOBAL | LOCAL)?
  private boolean baseEvent_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "baseEvent_0")) return false;
    baseEvent_0_0(b, l + 1);
    return true;
  }

  // GLOBAL | LOCAL
  private boolean baseEvent_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "baseEvent_0_0")) return false;
    boolean r;
    r = consumeToken(b, GLOBAL);
    if (!r) r = consumeToken(b, LOCAL);
    return r;
  }

  // (FORMS nonEmptyFormUsageList)?
  private boolean baseEvent_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "baseEvent_1")) return false;
    baseEvent_1_0(b, l + 1);
    return true;
  }

  // FORMS nonEmptyFormUsageList
  private boolean baseEvent_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "baseEvent_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, FORMS);
    r = r && nonEmptyFormUsageList(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ((GOAFTER | AFTER) nonEmptyNoContextActionOrPropertyUsageList)?
  private boolean baseEvent_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "baseEvent_2")) return false;
    baseEvent_2_0(b, l + 1);
    return true;
  }

  // (GOAFTER | AFTER) nonEmptyNoContextActionOrPropertyUsageList
  private boolean baseEvent_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "baseEvent_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = baseEvent_2_0_0(b, l + 1);
    r = r && nonEmptyNoContextActionOrPropertyUsageList(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // GOAFTER | AFTER
  private boolean baseEvent_2_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "baseEvent_2_0_0")) return false;
    boolean r;
    r = consumeToken(b, GOAFTER);
    if (!r) r = consumeToken(b, AFTER);
    return r;
  }

  /* ********************************************************** */
  // (paramDeclare)? baseEvent
  public boolean baseEventNotPE(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "baseEventNotPE")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, BASE_EVENT_NOT_PE, "<base event not pe>");
    r = baseEventNotPE_0(b, l + 1);
    r = r && baseEvent(b, l + 1);
    exit_section_(b, l, m, BASE_EVENT_NOT_PE, r, false, null);
    return r;
  }

  // (paramDeclare)?
  private boolean baseEventNotPE_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "baseEventNotPE_0")) return false;
    baseEventNotPE_0_0(b, l + 1);
    return true;
  }

  // (paramDeclare)
  private boolean baseEventNotPE_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "baseEventNotPE_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = paramDeclare(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // baseEvent (paramDeclare EQUALS)?
  public boolean baseEventPE(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "baseEventPE")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, BASE_EVENT_PE, "<base event pe>");
    r = baseEvent(b, l + 1);
    r = r && baseEventPE_1(b, l + 1);
    exit_section_(b, l, m, BASE_EVENT_PE, r, false, null);
    return r;
  }

  // (paramDeclare EQUALS)?
  private boolean baseEventPE_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "baseEventPE_1")) return false;
    baseEventPE_1_0(b, l + 1);
    return true;
  }

  // paramDeclare EQUALS
  private boolean baseEventPE_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "baseEventPE_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = paramDeclare(b, l + 1);
    r = r && consumeToken(b, EQUALS);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // LEX_LOGICAL_LITERAL
  public boolean booleanLiteral(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "booleanLiteral")) return false;
    if (!nextTokenIs(b, LEX_LOGICAL_LITERAL)) return false;
    boolean r;
    Marker m = enter_section_(b, l, BOOLEAN_LITERAL);
    r = consumeToken(b, LEX_LOGICAL_LITERAL);
    exit_section_(b, m, BOOLEAN_LITERAL, r);
    return r;
  }

  /* ********************************************************** */
  // LEFT	| RIGHT | TOP | BOTTOM
  public boolean borderPosition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "borderPosition")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, BORDER_POSITION, "<border position>");
    r = consumeToken(b, LEFT);
    if (!r) r = consumeToken(b, RIGHT);
    if (!r) r = consumeToken(b, TOP);
    if (!r) r = consumeToken(b, BOTTOM);
    exit_section_(b, l, m, BORDER_POSITION, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // LBRAC doubleLiteral COMMA doubleLiteral COMMA doubleLiteral COMMA doubleLiteral RBRAC
  public boolean boundsDoubleLiteral(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "boundsDoubleLiteral")) return false;
    if (!nextTokenIs(b, LBRAC)) return false;
    boolean r;
    Marker m = enter_section_(b, l, BOUNDS_DOUBLE_LITERAL);
    r = consumeToken(b, LBRAC);
    r = r && doubleLiteral(b, l + 1);
    r = r && consumeToken(b, COMMA);
    r = r && doubleLiteral(b, l + 1);
    r = r && consumeToken(b, COMMA);
    r = r && doubleLiteral(b, l + 1);
    r = r && consumeToken(b, COMMA);
    r = r && doubleLiteral(b, l + 1);
    r = r && consumeToken(b, RBRAC);
    exit_section_(b, m, BOUNDS_DOUBLE_LITERAL, r);
    return r;
  }

  /* ********************************************************** */
  // LBRAC intLiteral COMMA intLiteral COMMA intLiteral COMMA intLiteral RBRAC
  public boolean boundsIntLiteral(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "boundsIntLiteral")) return false;
    if (!nextTokenIs(b, LBRAC)) return false;
    boolean r;
    Marker m = enter_section_(b, l, BOUNDS_INT_LITERAL);
    r = consumeToken(b, LBRAC);
    r = r && intLiteral(b, l + 1);
    r = r && consumeToken(b, COMMA);
    r = r && intLiteral(b, l + 1);
    r = r && consumeToken(b, COMMA);
    r = r && intLiteral(b, l + 1);
    r = r && consumeToken(b, COMMA);
    r = r && intLiteral(b, l + 1);
    r = r && consumeToken(b, RBRAC);
    exit_section_(b, m, BOUNDS_INT_LITERAL, r);
    return r;
  }

  /* ********************************************************** */
  // LBRAC classNameList RBRAC
  public boolean bracketedClassNameList(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "bracketedClassNameList")) return false;
    if (!nextTokenIs(b, LBRAC)) return false;
    boolean r;
    Marker m = enter_section_(b, l, BRACKETED_CLASS_NAME_LIST);
    r = consumeToken(b, LBRAC);
    r = r && classNameList(b, l + 1);
    r = r && consumeToken(b, RBRAC);
    exit_section_(b, m, BRACKETED_CLASS_NAME_LIST, r);
    return r;
  }

  /* ********************************************************** */
  // BREAK
  public boolean breakActionOperator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "breakActionOperator")) return false;
    if (!nextTokenIs(b, BREAK)) return false;
    boolean r;
    Marker m = enter_section_(b, l, BREAK_ACTION_OPERATOR);
    r = consumeToken(b, BREAK);
    exit_section_(b, m, BREAK_ACTION_OPERATOR, r);
    return r;
  }

  /* ********************************************************** */
  // PRIMITIVE_TYPE | JSON | JSONTEXT | HTML
  public boolean builtInClassName(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "builtInClassName")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, BUILT_IN_CLASS_NAME, "<built in class name>");
    r = consumeToken(b, PRIMITIVE_TYPE);
    if (!r) r = consumeToken(b, JSON);
    if (!r) r = consumeToken(b, JSONTEXT);
    if (!r) r = consumeToken(b, HTML);
    exit_section_(b, l, m, BUILT_IN_CLASS_NAME, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // CANCEL nestedPropertiesSelector?
  public boolean cancelActionPropertyDefinitionBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "cancelActionPropertyDefinitionBody")) return false;
    if (!nextTokenIs(b, CANCEL)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, CANCEL_ACTION_PROPERTY_DEFINITION_BODY, null);
    r = consumeToken(b, CANCEL);
    p = r; // pin = 1
    r = r && cancelActionPropertyDefinitionBody_1(b, l + 1);
    exit_section_(b, l, m, CANCEL_ACTION_PROPERTY_DEFINITION_BODY, r, p, null);
    return r || p;
  }

  // nestedPropertiesSelector?
  private boolean cancelActionPropertyDefinitionBody_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "cancelActionPropertyDefinitionBody_1")) return false;
    nestedPropertiesSelector(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // CASE (exclusiveOverrideOption)? (actionCaseBranchBody)+ (ELSE actionPropertyDefinitionBody)?
  public boolean caseActionPropertyDefinitionBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "caseActionPropertyDefinitionBody")) return false;
    if (!nextTokenIs(b, CASE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, CASE_ACTION_PROPERTY_DEFINITION_BODY, null);
    r = consumeToken(b, CASE);
    p = r; // pin = 1
    r = r && caseActionPropertyDefinitionBody_1(b, l + 1);
    r = r && caseActionPropertyDefinitionBody_2(b, l + 1);
    r = r && caseActionPropertyDefinitionBody_3(b, l + 1);
    exit_section_(b, l, m, CASE_ACTION_PROPERTY_DEFINITION_BODY, r, p, null);
    return r || p;
  }

  // (exclusiveOverrideOption)?
  private boolean caseActionPropertyDefinitionBody_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "caseActionPropertyDefinitionBody_1")) return false;
    caseActionPropertyDefinitionBody_1_0(b, l + 1);
    return true;
  }

  // (exclusiveOverrideOption)
  private boolean caseActionPropertyDefinitionBody_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "caseActionPropertyDefinitionBody_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = exclusiveOverrideOption(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (actionCaseBranchBody)+
  private boolean caseActionPropertyDefinitionBody_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "caseActionPropertyDefinitionBody_2")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = caseActionPropertyDefinitionBody_2_0(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!caseActionPropertyDefinitionBody_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "caseActionPropertyDefinitionBody_2", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // (actionCaseBranchBody)
  private boolean caseActionPropertyDefinitionBody_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "caseActionPropertyDefinitionBody_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = actionCaseBranchBody(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (ELSE actionPropertyDefinitionBody)?
  private boolean caseActionPropertyDefinitionBody_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "caseActionPropertyDefinitionBody_3")) return false;
    caseActionPropertyDefinitionBody_3_0(b, l + 1);
    return true;
  }

  // ELSE actionPropertyDefinitionBody
  private boolean caseActionPropertyDefinitionBody_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "caseActionPropertyDefinitionBody_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, ELSE);
    r = r && actionPropertyDefinitionBody(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // WHEN propertyExpression (THEN propertyExpression)
  public boolean caseBranchBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "caseBranchBody")) return false;
    if (!nextTokenIs(b, WHEN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, CASE_BRANCH_BODY, null);
    r = consumeToken(b, WHEN);
    p = r; // pin = 1
    r = r && propertyExpression(b, l + 1);
    r = r && caseBranchBody_2(b, l + 1);
    exit_section_(b, l, m, CASE_BRANCH_BODY, r, p, null);
    return r || p;
  }

  // THEN propertyExpression
  private boolean caseBranchBody_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "caseBranchBody_2")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, THEN);
    p = r; // pin = 1
    r = r && propertyExpression(b, l + 1);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // CASE (exclusiveOverrideOption)?
  //                            (caseBranchBody)+
  //                            (ELSE propertyExpression)?
  public boolean casePropertyDefinition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "casePropertyDefinition")) return false;
    if (!nextTokenIs(b, CASE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, CASE_PROPERTY_DEFINITION, null);
    r = consumeToken(b, CASE);
    p = r; // pin = 1
    r = r && casePropertyDefinition_1(b, l + 1);
    r = r && casePropertyDefinition_2(b, l + 1);
    r = r && casePropertyDefinition_3(b, l + 1);
    exit_section_(b, l, m, CASE_PROPERTY_DEFINITION, r, p, null);
    return r || p;
  }

  // (exclusiveOverrideOption)?
  private boolean casePropertyDefinition_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "casePropertyDefinition_1")) return false;
    casePropertyDefinition_1_0(b, l + 1);
    return true;
  }

  // (exclusiveOverrideOption)
  private boolean casePropertyDefinition_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "casePropertyDefinition_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = exclusiveOverrideOption(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (caseBranchBody)+
  private boolean casePropertyDefinition_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "casePropertyDefinition_2")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = casePropertyDefinition_2_0(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!casePropertyDefinition_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "casePropertyDefinition_2", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // (caseBranchBody)
  private boolean casePropertyDefinition_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "casePropertyDefinition_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = caseBranchBody(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (ELSE propertyExpression)?
  private boolean casePropertyDefinition_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "casePropertyDefinition_3")) return false;
    casePropertyDefinition_3_0(b, l + 1);
    return true;
  }

  // ELSE propertyExpression
  private boolean casePropertyDefinition_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "casePropertyDefinition_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, ELSE);
    r = r && propertyExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // builtInClassName LBRAC propertyExpression RBRAC
  public boolean castPropertyDefinition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "castPropertyDefinition")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, CAST_PROPERTY_DEFINITION, "<cast property definition>");
    r = builtInClassName(b, l + 1);
    r = r && consumeToken(b, LBRAC);
    r = r && propertyExpression(b, l + 1);
    r = r && consumeToken(b, RBRAC);
    exit_section_(b, l, m, CAST_PROPERTY_DEFINITION, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // CHANGECLASS parameterOrExpression TO customClassUsage changeClassWhere?
  public boolean changeClassActionPropertyDefinitionBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "changeClassActionPropertyDefinitionBody")) return false;
    if (!nextTokenIs(b, CHANGECLASS)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, CHANGE_CLASS_ACTION_PROPERTY_DEFINITION_BODY, null);
    r = consumeToken(b, CHANGECLASS);
    p = r; // pin = 1
    r = r && parameterOrExpression(b, l + 1);
    r = r && consumeToken(b, TO);
    r = r && customClassUsage(b, l + 1);
    r = r && changeClassActionPropertyDefinitionBody_4(b, l + 1);
    exit_section_(b, l, m, CHANGE_CLASS_ACTION_PROPERTY_DEFINITION_BODY, r, p, null);
    return r || p;
  }

  // changeClassWhere?
  private boolean changeClassActionPropertyDefinitionBody_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "changeClassActionPropertyDefinitionBody_4")) return false;
    changeClassWhere(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // WHERE propertyExpression
  public boolean changeClassWhere(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "changeClassWhere")) return false;
    if (!nextTokenIs(b, WHERE)) return false;
    boolean r;
    Marker m = enter_section_(b, l, CHANGE_CLASS_WHERE);
    r = consumeToken(b, WHERE);
    r = r && propertyExpression(b, l + 1);
    exit_section_(b, m, CHANGE_CLASS_WHERE, r);
    return r;
  }

  /* ********************************************************** */
  // CHANGE (EQUALS propertyExpression)? NOCONSTRAINTFILTER? NOCHANGE?
  public boolean changeInput(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "changeInput")) return false;
    if (!nextTokenIs(b, CHANGE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, CHANGE_INPUT, null);
    r = consumeToken(b, CHANGE);
    p = r; // pin = 1
    r = r && changeInput_1(b, l + 1);
    r = r && changeInput_2(b, l + 1);
    r = r && changeInput_3(b, l + 1);
    exit_section_(b, l, m, CHANGE_INPUT, r, p, null);
    return r || p;
  }

  // (EQUALS propertyExpression)?
  private boolean changeInput_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "changeInput_1")) return false;
    changeInput_1_0(b, l + 1);
    return true;
  }

  // EQUALS propertyExpression
  private boolean changeInput_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "changeInput_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, EQUALS);
    r = r && propertyExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // NOCONSTRAINTFILTER?
  private boolean changeInput_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "changeInput_2")) return false;
    consumeToken(b, NOCONSTRAINTFILTER);
    return true;
  }

  // NOCHANGE?
  private boolean changeInput_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "changeInput_3")) return false;
    consumeToken(b, NOCHANGE);
    return true;
  }

  /* ********************************************************** */
  // CUSTOM stringLiteral?
  public boolean changeInputPropertyCustomView(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "changeInputPropertyCustomView")) return false;
    if (!nextTokenIs(b, CUSTOM)) return false;
    boolean r;
    Marker m = enter_section_(b, l, CHANGE_INPUT_PROPERTY_CUSTOM_VIEW);
    r = consumeToken(b, CUSTOM);
    r = r && changeInputPropertyCustomView_1(b, l + 1);
    exit_section_(b, m, CHANGE_INPUT_PROPERTY_CUSTOM_VIEW, r);
    return r;
  }

  // stringLiteral?
  private boolean changeInputPropertyCustomView_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "changeInputPropertyCustomView_1")) return false;
    stringLiteral(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // CHANGEKEY stringLiteral (SHOW | hideEditKey)?
  public boolean changeKeySetting(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "changeKeySetting")) return false;
    if (!nextTokenIs(b, CHANGEKEY)) return false;
    boolean r;
    Marker m = enter_section_(b, l, CHANGE_KEY_SETTING);
    r = consumeToken(b, CHANGEKEY);
    r = r && stringLiteral(b, l + 1);
    r = r && changeKeySetting_2(b, l + 1);
    exit_section_(b, m, CHANGE_KEY_SETTING, r);
    return r;
  }

  // (SHOW | hideEditKey)?
  private boolean changeKeySetting_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "changeKeySetting_2")) return false;
    changeKeySetting_2_0(b, l + 1);
    return true;
  }

  // SHOW | hideEditKey
  private boolean changeKeySetting_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "changeKeySetting_2_0")) return false;
    boolean r;
    r = consumeToken(b, SHOW);
    if (!r) r = hideEditKey(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // CHANGEMOUSE stringLiteral (SHOW | hideEditKey)?
  public boolean changeMouseSetting(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "changeMouseSetting")) return false;
    if (!nextTokenIs(b, CHANGEMOUSE)) return false;
    boolean r;
    Marker m = enter_section_(b, l, CHANGE_MOUSE_SETTING);
    r = consumeToken(b, CHANGEMOUSE);
    r = r && stringLiteral(b, l + 1);
    r = r && changeMouseSetting_2(b, l + 1);
    exit_section_(b, m, CHANGE_MOUSE_SETTING, r);
    return r;
  }

  // (SHOW | hideEditKey)?
  private boolean changeMouseSetting_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "changeMouseSetting_2")) return false;
    changeMouseSetting_2_0(b, l + 1);
    return true;
  }

  // SHOW | hideEditKey
  private boolean changeMouseSetting_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "changeMouseSetting_2_0")) return false;
    boolean r;
    r = consumeToken(b, SHOW);
    if (!r) r = hideEditKey(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // propertyUsage LBRAC parameterOrExpressionList RBRAC
  public boolean changePropertyBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "changePropertyBody")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, CHANGE_PROPERTY_BODY, "<change property body>");
    r = propertyUsage(b, l + 1);
    r = r && consumeToken(b, LBRAC);
    r = r && parameterOrExpressionList(b, l + 1);
    r = r && consumeToken(b, RBRAC);
    exit_section_(b, l, m, CHANGE_PROPERTY_BODY, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // CHANGE stringLiteral?
  public boolean changePropertyCustomView(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "changePropertyCustomView")) return false;
    if (!nextTokenIs(b, CHANGE)) return false;
    boolean r;
    Marker m = enter_section_(b, l, CHANGE_PROPERTY_CUSTOM_VIEW);
    r = consumeToken(b, CHANGE);
    r = r && changePropertyCustomView_1(b, l + 1);
    exit_section_(b, m, CHANGE_PROPERTY_CUSTOM_VIEW, r);
    return r;
  }

  // stringLiteral?
  private boolean changePropertyCustomView_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "changePropertyCustomView_1")) return false;
    stringLiteral(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // CHARWIDTH intLiteral
  public boolean charWidthSetting(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "charWidthSetting")) return false;
    if (!nextTokenIs(b, CHARWIDTH)) return false;
    boolean r;
    Marker m = enter_section_(b, l, CHAR_WIDTH_SETTING);
    r = consumeToken(b, CHARWIDTH);
    r = r && intLiteral(b, l + 1);
    exit_section_(b, m, CHAR_WIDTH_SETTING, r);
    return r;
  }

  /* ********************************************************** */
  // CLASS (ABSTRACT | NATIVE)? COMPLEX? simpleNameWithCaption staticObjectImage?
  public boolean classDecl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "classDecl")) return false;
    if (!nextTokenIs(b, CLASS)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, CLASS_DECL, null);
    r = consumeToken(b, CLASS);
    p = r; // pin = 1
    r = r && classDecl_1(b, l + 1);
    r = r && classDecl_2(b, l + 1);
    r = r && simpleNameWithCaption(b, l + 1);
    r = r && classDecl_4(b, l + 1);
    exit_section_(b, l, m, CLASS_DECL, r, p, null);
    return r || p;
  }

  // (ABSTRACT | NATIVE)?
  private boolean classDecl_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "classDecl_1")) return false;
    classDecl_1_0(b, l + 1);
    return true;
  }

  // ABSTRACT | NATIVE
  private boolean classDecl_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "classDecl_1_0")) return false;
    boolean r;
    r = consumeToken(b, ABSTRACT);
    if (!r) r = consumeToken(b, NATIVE);
    return r;
  }

  // COMPLEX?
  private boolean classDecl_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "classDecl_2")) return false;
    consumeToken(b, COMPLEX);
    return true;
  }

  // staticObjectImage?
  private boolean classDecl_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "classDecl_4")) return false;
    staticObjectImage(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // LBRACE staticObjectDeclList RBRACE (classParentsList SEMI)? | (classParentsList)? SEMI
  boolean classInstancesAndParents(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "classInstancesAndParents")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = classInstancesAndParents_0(b, l + 1);
    if (!r) r = classInstancesAndParents_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // LBRACE staticObjectDeclList RBRACE (classParentsList SEMI)?
  private boolean classInstancesAndParents_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "classInstancesAndParents_0")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, LBRACE);
    p = r; // pin = 1
    r = r && staticObjectDeclList(b, l + 1);
    r = r && consumeToken(b, RBRACE);
    r = r && classInstancesAndParents_0_3(b, l + 1);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  // (classParentsList SEMI)?
  private boolean classInstancesAndParents_0_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "classInstancesAndParents_0_3")) return false;
    classInstancesAndParents_0_3_0(b, l + 1);
    return true;
  }

  // classParentsList SEMI
  private boolean classInstancesAndParents_0_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "classInstancesAndParents_0_3_0")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = classParentsList(b, l + 1);
    p = r; // pin = 1
    r = r && consumeToken(b, SEMI);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  // (classParentsList)? SEMI
  private boolean classInstancesAndParents_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "classInstancesAndParents_1")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = classInstancesAndParents_1_0(b, l + 1);
    p = r; // pin = 1
    r = r && consumeToken(b, SEMI);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  // (classParentsList)?
  private boolean classInstancesAndParents_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "classInstancesAndParents_1_0")) return false;
    classInstancesAndParents_1_0_0(b, l + 1);
    return true;
  }

  // (classParentsList)
  private boolean classInstancesAndParents_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "classInstancesAndParents_1_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = classParentsList(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // builtInClassName | customClassUsage
  public boolean className(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "className")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, CLASS_NAME, "<class name>");
    r = builtInClassName(b, l + 1);
    if (!r) r = customClassUsage(b, l + 1);
    exit_section_(b, l, m, CLASS_NAME, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // (nonEmptyClassNameList)?
  public boolean classNameList(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "classNameList")) return false;
    Marker m = enter_section_(b, l, _NONE_, CLASS_NAME_LIST, "<class name list>");
    classNameList_0(b, l + 1);
    exit_section_(b, l, m, CLASS_NAME_LIST, true, false, null);
    return true;
  }

  // (nonEmptyClassNameList)
  private boolean classNameList_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "classNameList_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = nonEmptyClassNameList(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // (<<checkClassOrExpression>> &((customClassUsage <<matchedClass>>)?) propertyExpression <<matchedClassOrExpression>>) |
  //         customClassUsage
  public boolean classOrExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "classOrExpression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, CLASS_OR_EXPRESSION, "<class or expression>");
    r = classOrExpression_0(b, l + 1);
    if (!r) r = customClassUsage(b, l + 1);
    exit_section_(b, l, m, CLASS_OR_EXPRESSION, r, false, null);
    return r;
  }

  // <<checkClassOrExpression>> &((customClassUsage <<matchedClass>>)?) propertyExpression <<matchedClassOrExpression>>
  private boolean classOrExpression_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "classOrExpression_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = checkClassOrExpression(b, l + 1);
    r = r && classOrExpression_0_1(b, l + 1);
    r = r && propertyExpression(b, l + 1);
    r = r && matchedClassOrExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // &((customClassUsage <<matchedClass>>)?)
  private boolean classOrExpression_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "classOrExpression_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _AND_);
    r = classOrExpression_0_1_0(b, l + 1);
    exit_section_(b, l, m, null, r, false, null);
    return r;
  }

  // (customClassUsage <<matchedClass>>)?
  private boolean classOrExpression_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "classOrExpression_0_1_0")) return false;
    classOrExpression_0_1_0_0(b, l + 1);
    return true;
  }

  // customClassUsage <<matchedClass>>
  private boolean classOrExpression_0_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "classOrExpression_0_1_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = customClassUsage(b, l + 1);
    r = r && matchedClass(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // aggrParamPropDeclare | untypedParamDeclare
  public boolean classParamDeclare(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "classParamDeclare")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, CLASS_PARAM_DECLARE, "<class param declare>");
    r = aggrParamPropDeclare(b, l + 1);
    if (!r) r = untypedParamDeclare(b, l + 1);
    exit_section_(b, l, m, CLASS_PARAM_DECLARE, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // (nonEmptyClassParamDeclareList)?
  public boolean classParamDeclareList(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "classParamDeclareList")) return false;
    Marker m = enter_section_(b, l, _NONE_, CLASS_PARAM_DECLARE_LIST, "<class param declare list>");
    classParamDeclareList_0(b, l + 1);
    exit_section_(b, l, m, CLASS_PARAM_DECLARE_LIST, true, false, null);
    return true;
  }

  // (nonEmptyClassParamDeclareList)
  private boolean classParamDeclareList_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "classParamDeclareList_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = nonEmptyClassParamDeclareList(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // COLON nonEmptyCustomClassUsageList
  public boolean classParentsList(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "classParentsList")) return false;
    if (!nextTokenIs(b, COLON)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, CLASS_PARENTS_LIST, null);
    r = consumeToken(b, COLON);
    p = r; // pin = 1
    r = r && nonEmptyCustomClassUsageList(b, l + 1);
    exit_section_(b, l, m, CLASS_PARENTS_LIST, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // (classDecl | extendingClassDeclaration) classInstancesAndParents
  public boolean classStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "classStatement")) return false;
    if (!nextTokenIs(b, "<class statement>", CLASS, EXTEND)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, CLASS_STATEMENT, "<class statement>");
    r = classStatement_0(b, l + 1);
    p = r; // pin = 1
    r = r && classInstancesAndParents(b, l + 1);
    exit_section_(b, l, m, CLASS_STATEMENT, r, p, null);
    return r || p;
  }

  // classDecl | extendingClassDeclaration
  private boolean classStatement_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "classStatement_0")) return false;
    boolean r;
    r = classDecl(b, l + 1);
    if (!r) r = extendingClassDeclaration(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // PANEL | GRID | TOOLBAR
  public boolean classViewType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "classViewType")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, CLASS_VIEW_TYPE, "<class view type>");
    r = consumeToken(b, PANEL);
    if (!r) r = consumeToken(b, GRID);
    if (!r) r = consumeToken(b, TOOLBAR);
    exit_section_(b, l, m, CLASS_VIEW_TYPE, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // CLOSE FORM stringLiteral
  public boolean closeFormActionPropertyDefinitionBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "closeFormActionPropertyDefinitionBody")) return false;
    if (!nextTokenIs(b, CLOSE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, CLOSE_FORM_ACTION_PROPERTY_DEFINITION_BODY, null);
    r = consumeTokens(b, 1, CLOSE, FORM);
    p = r; // pin = 1
    r = r && stringLiteral(b, l + 1);
    exit_section_(b, l, m, CLOSE_FORM_ACTION_PROPERTY_DEFINITION_BODY, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // LEX_CODE_LITERAL
  public boolean codeLiteral(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "codeLiteral")) return false;
    if (!nextTokenIs(b, LEX_CODE_LITERAL)) return false;
    boolean r;
    Marker m = enter_section_(b, l, CODE_LITERAL);
    r = consumeToken(b, LEX_CODE_LITERAL);
    exit_section_(b, m, CODE_LITERAL, r);
    return r;
  }

  /* ********************************************************** */
  // COLLAPSE (DOWN | ALL (TOP)?)?
  //                                             groupObjectID (OBJECTS objectExpr (COMMA objectExpr)*)?
  public boolean collapseGroupObjectActionPropertyDefinitionBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "collapseGroupObjectActionPropertyDefinitionBody")) return false;
    if (!nextTokenIs(b, COLLAPSE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, COLLAPSE_GROUP_OBJECT_ACTION_PROPERTY_DEFINITION_BODY, null);
    r = consumeToken(b, COLLAPSE);
    r = r && collapseGroupObjectActionPropertyDefinitionBody_1(b, l + 1);
    r = r && groupObjectID(b, l + 1);
    p = r; // pin = 3
    r = r && collapseGroupObjectActionPropertyDefinitionBody_3(b, l + 1);
    exit_section_(b, l, m, COLLAPSE_GROUP_OBJECT_ACTION_PROPERTY_DEFINITION_BODY, r, p, null);
    return r || p;
  }

  // (DOWN | ALL (TOP)?)?
  private boolean collapseGroupObjectActionPropertyDefinitionBody_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "collapseGroupObjectActionPropertyDefinitionBody_1")) return false;
    collapseGroupObjectActionPropertyDefinitionBody_1_0(b, l + 1);
    return true;
  }

  // DOWN | ALL (TOP)?
  private boolean collapseGroupObjectActionPropertyDefinitionBody_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "collapseGroupObjectActionPropertyDefinitionBody_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, DOWN);
    if (!r) r = collapseGroupObjectActionPropertyDefinitionBody_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ALL (TOP)?
  private boolean collapseGroupObjectActionPropertyDefinitionBody_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "collapseGroupObjectActionPropertyDefinitionBody_1_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, ALL);
    r = r && collapseGroupObjectActionPropertyDefinitionBody_1_0_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (TOP)?
  private boolean collapseGroupObjectActionPropertyDefinitionBody_1_0_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "collapseGroupObjectActionPropertyDefinitionBody_1_0_1_1")) return false;
    consumeToken(b, TOP);
    return true;
  }

  // (OBJECTS objectExpr (COMMA objectExpr)*)?
  private boolean collapseGroupObjectActionPropertyDefinitionBody_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "collapseGroupObjectActionPropertyDefinitionBody_3")) return false;
    collapseGroupObjectActionPropertyDefinitionBody_3_0(b, l + 1);
    return true;
  }

  // OBJECTS objectExpr (COMMA objectExpr)*
  private boolean collapseGroupObjectActionPropertyDefinitionBody_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "collapseGroupObjectActionPropertyDefinitionBody_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, OBJECTS);
    r = r && objectExpr(b, l + 1);
    r = r && collapseGroupObjectActionPropertyDefinitionBody_3_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (COMMA objectExpr)*
  private boolean collapseGroupObjectActionPropertyDefinitionBody_3_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "collapseGroupObjectActionPropertyDefinitionBody_3_0_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!collapseGroupObjectActionPropertyDefinitionBody_3_0_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "collapseGroupObjectActionPropertyDefinitionBody_3_0_2", c)) break;
    }
    return true;
  }

  // COMMA objectExpr
  private boolean collapseGroupObjectActionPropertyDefinitionBody_3_0_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "collapseGroupObjectActionPropertyDefinitionBody_3_0_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, COMMA);
    r = r && objectExpr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // LEX_COLOR_LITERAL | (RGB LBRAC uintLiteral COMMA uintLiteral COMMA uintLiteral RBRAC)
  public boolean colorLiteral(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "colorLiteral")) return false;
    if (!nextTokenIs(b, "<color literal>", LEX_COLOR_LITERAL, RGB)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, COLOR_LITERAL, "<color literal>");
    r = consumeToken(b, LEX_COLOR_LITERAL);
    if (!r) r = colorLiteral_1(b, l + 1);
    exit_section_(b, l, m, COLOR_LITERAL, r, false, null);
    return r;
  }

  // RGB LBRAC uintLiteral COMMA uintLiteral COMMA uintLiteral RBRAC
  private boolean colorLiteral_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "colorLiteral_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeTokens(b, 0, RGB, LBRAC);
    r = r && uintLiteral(b, l + 1);
    r = r && consumeToken(b, COMMA);
    r = r && uintLiteral(b, l + 1);
    r = r && consumeToken(b, COMMA);
    r = r && uintLiteral(b, l + 1);
    r = r && consumeToken(b, RBRAC);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // ulongLiteral
  // 	|	uintLiteral
  // 	|	udoubleLiteral
  // 	|	unumericLiteral
  // 	|	booleanLiteral
  // 	|	tbooleanLiteral
  // 	|	dateTimeLiteral
  // 	|	dateLiteral
  // 	|	timeLiteral
  // 	|	nullLiteral
  // 	|	staticObjectID
  // 	|	colorLiteral
  boolean commonLiteral(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "commonLiteral")) return false;
    boolean r;
    r = ulongLiteral(b, l + 1);
    if (!r) r = uintLiteral(b, l + 1);
    if (!r) r = udoubleLiteral(b, l + 1);
    if (!r) r = unumericLiteral(b, l + 1);
    if (!r) r = booleanLiteral(b, l + 1);
    if (!r) r = tbooleanLiteral(b, l + 1);
    if (!r) r = dateTimeLiteral(b, l + 1);
    if (!r) r = dateLiteral(b, l + 1);
    if (!r) r = timeLiteral(b, l + 1);
    if (!r) r = nullLiteral(b, l + 1);
    if (!r) r = staticObjectID(b, l + 1);
    if (!r) r = colorLiteral(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // COMPLEX | NOCOMPLEX
  public boolean complexSetting(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "complexSetting")) return false;
    if (!nextTokenIs(b, "<complex setting>", COMPLEX, NOCOMPLEX)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, COMPLEX_SETTING, "<complex setting>");
    r = consumeToken(b, COMPLEX);
    if (!r) r = consumeToken(b, NOCOMPLEX);
    exit_section_(b, l, m, COMPLEX_SETTING, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // LBRACE componentStatement* RBRACE
  public boolean componentBlockStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "componentBlockStatement")) return false;
    if (!nextTokenIs(b, LBRACE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, COMPONENT_BLOCK_STATEMENT, null);
    r = consumeToken(b, LBRACE);
    p = r; // pin = 1
    r = r && componentBlockStatement_1(b, l + 1);
    r = r && consumeToken(b, RBRACE);
    exit_section_(b, l, m, COMPONENT_BLOCK_STATEMENT, r, p, null);
    return r || p;
  }

  // componentStatement*
  private boolean componentBlockStatement_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "componentBlockStatement_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!componentStatement(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "componentBlockStatement_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // componentBlockStatement | emptyStatement
  public boolean componentBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "componentBody")) return false;
    if (!nextTokenIs(b, "<component body>", LBRACE, SEMI)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, COMPONENT_BODY, "<component body>");
    r = componentBlockStatement(b, l + 1);
    if (!r) r = emptyStatement(b, l + 1);
    exit_section_(b, l, m, COMPONENT_BODY, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // simpleName
  public boolean componentDecl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "componentDecl")) return false;
    if (!nextTokenIs(b, ID)) return false;
    boolean r;
    Marker m = enter_section_(b, l, COMPONENT_DECL);
    r = simpleName(b, l + 1);
    exit_section_(b, m, COMPONENT_DECL, r);
    return r;
  }

  /* ********************************************************** */
  // <<innerIDCheck>> formUsage POINT componentSelector
  public boolean componentID(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "componentID")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, COMPONENT_ID, "<component id>");
    r = innerIDCheck(b, l + 1);
    r = r && formUsage(b, l + 1);
    r = r && consumeToken(b, POINT);
    p = r; // pin = 3
    r = r && componentSelector(b, l + 1);
    exit_section_(b, l, m, COMPONENT_ID, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // (   insertRelativePositionLiteral componentSelector
  //                             |	staticRelativePosition
  //                             )?
  public boolean componentInsertPosition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "componentInsertPosition")) return false;
    Marker m = enter_section_(b, l, _NONE_, COMPONENT_INSERT_POSITION, "<component insert position>");
    componentInsertPosition_0(b, l + 1);
    exit_section_(b, l, m, COMPONENT_INSERT_POSITION, true, false, null);
    return true;
  }

  // insertRelativePositionLiteral componentSelector
  //                             |	staticRelativePosition
  private boolean componentInsertPosition_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "componentInsertPosition_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = componentInsertPosition_0_0(b, l + 1);
    if (!r) r = staticRelativePosition(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // insertRelativePositionLiteral componentSelector
  private boolean componentInsertPosition_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "componentInsertPosition_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = insertRelativePositionLiteral(b, l + 1);
    r = r && componentSelector(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // dimensionLiteral
  //                         |	booleanLiteral
  //                         |	tbooleanLiteral
  //                         |	boundsIntLiteral
  //                         |	boundsDoubleLiteral
  //                         |   containerTypeLiteral
  //                         |   flexAlignmentLiteral
  //                         |   designCalcPropertyObject
  public boolean componentPropertyValue(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "componentPropertyValue")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, COMPONENT_PROPERTY_VALUE, "<component property value>");
    r = dimensionLiteral(b, l + 1);
    if (!r) r = booleanLiteral(b, l + 1);
    if (!r) r = tbooleanLiteral(b, l + 1);
    if (!r) r = boundsIntLiteral(b, l + 1);
    if (!r) r = boundsDoubleLiteral(b, l + 1);
    if (!r) r = containerTypeLiteral(b, l + 1);
    if (!r) r = flexAlignmentLiteral(b, l + 1);
    if (!r) r = designCalcPropertyObject(b, l + 1);
    exit_section_(b, l, m, COMPONENT_PROPERTY_VALUE, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // PARENT LBRAC componentSelector RBRAC
  //                 	|	PROPERTY LBRAC propertySelector RBRAC
  //                 	|	FILTER LBRAC filterPropertySelector RBRAC
  //                 	|   FILTERGROUP LBRAC filterGroupSelector RBRAC
  //                 	|   groupSingleSelectorType LBRAC groupComponentSelector RBRAC
  //                 	|   pinnedGroupObjectTreeSelector // pinned для нижнего правила
  //                 	|   globalSingleSelectorType
  //                     |	componentUsage
  public boolean componentSelector(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "componentSelector")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, COMPONENT_SELECTOR, "<component selector>");
    r = componentSelector_0(b, l + 1);
    if (!r) r = componentSelector_1(b, l + 1);
    if (!r) r = componentSelector_2(b, l + 1);
    if (!r) r = componentSelector_3(b, l + 1);
    if (!r) r = componentSelector_4(b, l + 1);
    if (!r) r = pinnedGroupObjectTreeSelector(b, l + 1);
    if (!r) r = globalSingleSelectorType(b, l + 1);
    if (!r) r = componentUsage(b, l + 1);
    exit_section_(b, l, m, COMPONENT_SELECTOR, r, false, null);
    return r;
  }

  // PARENT LBRAC componentSelector RBRAC
  private boolean componentSelector_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "componentSelector_0")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeTokens(b, 1, PARENT, LBRAC);
    p = r; // pin = 1
    r = r && componentSelector(b, l + 1);
    r = r && consumeToken(b, RBRAC);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  // PROPERTY LBRAC propertySelector RBRAC
  private boolean componentSelector_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "componentSelector_1")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeTokens(b, 1, PROPERTY, LBRAC);
    p = r; // pin = 1
    r = r && propertySelector(b, l + 1);
    r = r && consumeToken(b, RBRAC);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  // FILTER LBRAC filterPropertySelector RBRAC
  private boolean componentSelector_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "componentSelector_2")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeTokens(b, 1, FILTER, LBRAC);
    p = r; // pin = 1
    r = r && filterPropertySelector(b, l + 1);
    r = r && consumeToken(b, RBRAC);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  // FILTERGROUP LBRAC filterGroupSelector RBRAC
  private boolean componentSelector_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "componentSelector_3")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeTokens(b, 1, FILTERGROUP, LBRAC);
    p = r; // pin = 1
    r = r && filterGroupSelector(b, l + 1);
    r = r && consumeToken(b, RBRAC);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  // groupSingleSelectorType LBRAC groupComponentSelector RBRAC
  private boolean componentSelector_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "componentSelector_4")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = groupSingleSelectorType(b, l + 1);
    p = r; // pin = 1
    r = r && consumeToken(b, LBRAC);
    r = r && groupComponentSelector(b, l + 1);
    r = r && consumeToken(b, RBRAC);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // BOX | OBJECTS | PANEL | TOOLBARBOX | TOOLBARLEFT | TOOLBARRIGHT | TOOLBAR
  boolean componentSingleSelectorType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "componentSingleSelectorType")) return false;
    boolean r;
    r = consumeToken(b, BOX);
    if (!r) r = consumeToken(b, OBJECTS);
    if (!r) r = consumeToken(b, PANEL);
    if (!r) r = consumeToken(b, TOOLBARBOX);
    if (!r) r = consumeToken(b, TOOLBARLEFT);
    if (!r) r = consumeToken(b, TOOLBARRIGHT);
    if (!r) r = consumeToken(b, TOOLBAR);
    return r;
  }

  /* ********************************************************** */
  // setObjectPropertyStatement
  //                         |	setupComponentStatement
  //                         |	newComponentStatement
  //                         |   moveComponentStatement
  //                         |	removeComponentStatement
  //                         |	emptyStatement
  //                         |   componentStubStatement
  public boolean componentStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "componentStatement")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, COMPONENT_STATEMENT, "<component statement>");
    r = setObjectPropertyStatement(b, l + 1);
    if (!r) r = setupComponentStatement(b, l + 1);
    if (!r) r = newComponentStatement(b, l + 1);
    if (!r) r = moveComponentStatement(b, l + 1);
    if (!r) r = removeComponentStatement(b, l + 1);
    if (!r) r = emptyStatement(b, l + 1);
    if (!r) r = componentStubStatement(b, l + 1);
    exit_section_(b, l, m, COMPONENT_STATEMENT, r, false, this::component_statement_recover);
    return r;
  }

  /* ********************************************************** */
  // NEW ID
  public boolean componentStubStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "componentStubStatement")) return false;
    if (!nextTokenIs(b, NEW)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, COMPONENT_STUB_STATEMENT, null);
    r = consumeTokens(b, 1, NEW, ID);
    p = r; // pin = 1
    exit_section_(b, l, m, COMPONENT_STUB_STATEMENT, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // simpleName
  public boolean componentUsage(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "componentUsage")) return false;
    if (!nextTokenIs(b, ID)) return false;
    boolean r;
    Marker m = enter_section_(b, l, COMPONENT_USAGE);
    r = simpleName(b, l + 1);
    exit_section_(b, m, COMPONENT_USAGE, r);
    return r;
  }

  /* ********************************************************** */
  // !component_statement_recover_start
  boolean component_statement_recover(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "component_statement_recover")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !component_statement_recover_start(b, l + 1);
    exit_section_(b, l, m, null, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // ID | PARENT | PROPERTY | NEW | MOVE | REMOVE | SEMI | RBRACE | groupObjectTreeSingleSelectorType | globalSingleSelectorType | GROUP | FILTERGROUP
  boolean component_statement_recover_start(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "component_statement_recover_start")) return false;
    boolean r;
    r = consumeToken(b, ID);
    if (!r) r = consumeToken(b, PARENT);
    if (!r) r = consumeToken(b, PROPERTY);
    if (!r) r = consumeToken(b, NEW);
    if (!r) r = consumeToken(b, MOVE);
    if (!r) r = consumeToken(b, REMOVE);
    if (!r) r = consumeToken(b, SEMI);
    if (!r) r = consumeToken(b, RBRACE);
    if (!r) r = groupObjectTreeSingleSelectorType(b, l + 1);
    if (!r) r = globalSingleSelectorType(b, l + 1);
    if (!r) r = consumeToken(b, GROUP);
    if (!r) r = consumeToken(b, FILTERGROUP);
    return r;
  }

  /* ********************************************************** */
  // (<<innerIDStop>> namespaceUsage POINT simpleName) | simpleName
  public boolean compoundID(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "compoundID")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, COMPOUND_ID, "<compound id>");
    r = compoundID_0(b, l + 1);
    if (!r) r = simpleName(b, l + 1);
    exit_section_(b, l, m, COMPOUND_ID, r, false, null);
    return r;
  }

  // <<innerIDStop>> namespaceUsage POINT simpleName
  private boolean compoundID_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "compoundID_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = innerIDStop(b, l + 1);
    r = r && namespaceUsage(b, l + 1);
    r = r && consumeToken(b, POINT);
    r = r && simpleName(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // CONCAT stringLiteral COMMA nonEmptyPropertyExpressionList
  public boolean concatPropertyDefinition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "concatPropertyDefinition")) return false;
    if (!nextTokenIs(b, CONCAT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, CONCAT_PROPERTY_DEFINITION, null);
    r = consumeToken(b, CONCAT);
    p = r; // pin = 1
    r = r && stringLiteral(b, l + 1);
    r = r && consumeToken(b, COMMA);
    r = r && nonEmptyPropertyExpressionList(b, l + 1);
    exit_section_(b, l, m, CONCAT_PROPERTY_DEFINITION, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // ASK propertyExpression ((paramDeclare EQUALS)? YESNO)? doInputBody
  public boolean confirmActionPropertyDefinitionBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "confirmActionPropertyDefinitionBody")) return false;
    if (!nextTokenIs(b, ASK)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, CONFIRM_ACTION_PROPERTY_DEFINITION_BODY, null);
    r = consumeToken(b, ASK);
    p = r; // pin = 1
    r = r && propertyExpression(b, l + 1);
    r = r && confirmActionPropertyDefinitionBody_2(b, l + 1);
    r = r && doInputBody(b, l + 1);
    exit_section_(b, l, m, CONFIRM_ACTION_PROPERTY_DEFINITION_BODY, r, p, null);
    return r || p;
  }

  // ((paramDeclare EQUALS)? YESNO)?
  private boolean confirmActionPropertyDefinitionBody_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "confirmActionPropertyDefinitionBody_2")) return false;
    confirmActionPropertyDefinitionBody_2_0(b, l + 1);
    return true;
  }

  // (paramDeclare EQUALS)? YESNO
  private boolean confirmActionPropertyDefinitionBody_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "confirmActionPropertyDefinitionBody_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = confirmActionPropertyDefinitionBody_2_0_0(b, l + 1);
    r = r && consumeToken(b, YESNO);
    exit_section_(b, m, null, r);
    return r;
  }

  // (paramDeclare EQUALS)?
  private boolean confirmActionPropertyDefinitionBody_2_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "confirmActionPropertyDefinitionBody_2_0_0")) return false;
    confirmActionPropertyDefinitionBody_2_0_0_0(b, l + 1);
    return true;
  }

  // paramDeclare EQUALS
  private boolean confirmActionPropertyDefinitionBody_2_0_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "confirmActionPropertyDefinitionBody_2_0_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = paramDeclare(b, l + 1);
    r = r && consumeToken(b, EQUALS);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // CONFIRM
  public boolean confirmSetting(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "confirmSetting")) return false;
    if (!nextTokenIs(b, CONFIRM)) return false;
    boolean r;
    Marker m = enter_section_(b, l, CONFIRM_SETTING);
    r = consumeToken(b, CONFIRM);
    exit_section_(b, m, CONFIRM_SETTING, r);
    return r;
  }

  /* ********************************************************** */
  // CONSTRAINTFILTER (EQUALS propertyExpression)?
  public boolean constraintFilter(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "constraintFilter")) return false;
    if (!nextTokenIs(b, CONSTRAINTFILTER)) return false;
    boolean r;
    Marker m = enter_section_(b, l, CONSTRAINT_FILTER);
    r = consumeToken(b, CONSTRAINTFILTER);
    r = r && constraintFilter_1(b, l + 1);
    exit_section_(b, m, CONSTRAINT_FILTER, r);
    return r;
  }

  // (EQUALS propertyExpression)?
  private boolean constraintFilter_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "constraintFilter_1")) return false;
    constraintFilter_1_0(b, l + 1);
    return true;
  }

  // EQUALS propertyExpression
  private boolean constraintFilter_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "constraintFilter_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, EQUALS);
    r = r && propertyExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // CONSTRAINT baseEventPE propertyExpression
  // 		                (CHECKED (BY nonEmptyNoContextPropertyUsageList)? )?
  // 		                MESSAGE messagePropertyExpression
  // 		                (PROPERTIES nonEmptyPropertyExpressionList)?
  // 		                SEMI
  public boolean constraintStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "constraintStatement")) return false;
    if (!nextTokenIs(b, CONSTRAINT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, CONSTRAINT_STATEMENT, null);
    r = consumeToken(b, CONSTRAINT);
    p = r; // pin = 1
    r = r && baseEventPE(b, l + 1);
    r = r && propertyExpression(b, l + 1);
    r = r && constraintStatement_3(b, l + 1);
    r = r && consumeToken(b, MESSAGE);
    r = r && messagePropertyExpression(b, l + 1);
    r = r && constraintStatement_6(b, l + 1);
    r = r && consumeToken(b, SEMI);
    exit_section_(b, l, m, CONSTRAINT_STATEMENT, r, p, null);
    return r || p;
  }

  // (CHECKED (BY nonEmptyNoContextPropertyUsageList)? )?
  private boolean constraintStatement_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "constraintStatement_3")) return false;
    constraintStatement_3_0(b, l + 1);
    return true;
  }

  // CHECKED (BY nonEmptyNoContextPropertyUsageList)?
  private boolean constraintStatement_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "constraintStatement_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, CHECKED);
    r = r && constraintStatement_3_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (BY nonEmptyNoContextPropertyUsageList)?
  private boolean constraintStatement_3_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "constraintStatement_3_0_1")) return false;
    constraintStatement_3_0_1_0(b, l + 1);
    return true;
  }

  // BY nonEmptyNoContextPropertyUsageList
  private boolean constraintStatement_3_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "constraintStatement_3_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, BY);
    r = r && nonEmptyNoContextPropertyUsageList(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (PROPERTIES nonEmptyPropertyExpressionList)?
  private boolean constraintStatement_6(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "constraintStatement_6")) return false;
    constraintStatement_6_0(b, l + 1);
    return true;
  }

  // PROPERTIES nonEmptyPropertyExpressionList
  private boolean constraintStatement_6_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "constraintStatement_6_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, PROPERTIES);
    r = r && nonEmptyPropertyExpressionList(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // CONTAINERV
  // 	                    |	CONTAINERH
  // 	                    |	COLUMNS
  // 	                    |	TABBED
  // 	                    |   SCROLL
  // 	                    |	SPLITH
  // 	                    |	SPLITV
  public boolean containerTypeLiteral(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "containerTypeLiteral")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, CONTAINER_TYPE_LITERAL, "<container type literal>");
    r = consumeToken(b, CONTAINERV);
    if (!r) r = consumeToken(b, CONTAINERH);
    if (!r) r = consumeToken(b, COLUMNS);
    if (!r) r = consumeToken(b, TABBED);
    if (!r) r = consumeToken(b, SCROLL);
    if (!r) r = consumeToken(b, SPLITH);
    if (!r) r = consumeToken(b, SPLITV);
    exit_section_(b, l, m, CONTAINER_TYPE_LITERAL, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // stringLiteral (KEYPRESS stringLiteral)? (TOOLBAR ((ALL | SELECTED | FOCUSED) HOVER?)*)? listActionPropertyDefinitionBody
  public boolean contextAction(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "contextAction")) return false;
    if (!nextTokenIs(b, "<context action>", ID, LEX_STRING_LITERAL)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, CONTEXT_ACTION, "<context action>");
    r = stringLiteral(b, l + 1);
    r = r && contextAction_1(b, l + 1);
    r = r && contextAction_2(b, l + 1);
    r = r && listActionPropertyDefinitionBody(b, l + 1);
    exit_section_(b, l, m, CONTEXT_ACTION, r, false, null);
    return r;
  }

  // (KEYPRESS stringLiteral)?
  private boolean contextAction_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "contextAction_1")) return false;
    contextAction_1_0(b, l + 1);
    return true;
  }

  // KEYPRESS stringLiteral
  private boolean contextAction_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "contextAction_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, KEYPRESS);
    r = r && stringLiteral(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (TOOLBAR ((ALL | SELECTED | FOCUSED) HOVER?)*)?
  private boolean contextAction_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "contextAction_2")) return false;
    contextAction_2_0(b, l + 1);
    return true;
  }

  // TOOLBAR ((ALL | SELECTED | FOCUSED) HOVER?)*
  private boolean contextAction_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "contextAction_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, TOOLBAR);
    r = r && contextAction_2_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ((ALL | SELECTED | FOCUSED) HOVER?)*
  private boolean contextAction_2_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "contextAction_2_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!contextAction_2_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "contextAction_2_0_1", c)) break;
    }
    return true;
  }

  // (ALL | SELECTED | FOCUSED) HOVER?
  private boolean contextAction_2_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "contextAction_2_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = contextAction_2_0_1_0_0(b, l + 1);
    r = r && contextAction_2_0_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ALL | SELECTED | FOCUSED
  private boolean contextAction_2_0_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "contextAction_2_0_1_0_0")) return false;
    boolean r;
    r = consumeToken(b, ALL);
    if (!r) r = consumeToken(b, SELECTED);
    if (!r) r = consumeToken(b, FOCUSED);
    return r;
  }

  // HOVER?
  private boolean contextAction_2_0_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "contextAction_2_0_1_0_1")) return false;
    consumeToken(b, HOVER);
    return true;
  }

  /* ********************************************************** */
  // ACTIONS contextAction (COMMA contextAction)*
  public boolean contextActions(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "contextActions")) return false;
    if (!nextTokenIs(b, ACTIONS)) return false;
    boolean r;
    Marker m = enter_section_(b, l, CONTEXT_ACTIONS);
    r = consumeToken(b, ACTIONS);
    r = r && contextAction(b, l + 1);
    r = r && contextActions_2(b, l + 1);
    exit_section_(b, m, CONTEXT_ACTIONS, r);
    return r;
  }

  // (COMMA contextAction)*
  private boolean contextActions_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "contextActions_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!contextActions_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "contextActions_2", c)) break;
    }
    return true;
  }

  // COMMA contextAction
  private boolean contextActions_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "contextActions_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, COMMA);
    r = r && contextAction(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // FILTERS propertyExpression (COMMA propertyExpression)*
  public boolean contextFiltersClause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "contextFiltersClause")) return false;
    if (!nextTokenIs(b, FILTERS)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, CONTEXT_FILTERS_CLAUSE, null);
    r = consumeToken(b, FILTERS);
    p = r; // pin = 1
    r = r && propertyExpression(b, l + 1);
    r = r && contextFiltersClause_2(b, l + 1);
    exit_section_(b, l, m, CONTEXT_FILTERS_CLAUSE, r, p, null);
    return r || p;
  }

  // (COMMA propertyExpression)*
  private boolean contextFiltersClause_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "contextFiltersClause_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!contextFiltersClause_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "contextFiltersClause_2", c)) break;
    }
    return true;
  }

  // COMMA propertyExpression
  private boolean contextFiltersClause_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "contextFiltersClause_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, COMMA);
    r = r && propertyExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // CONTEXTMENU (<<noIDCheck>> localizedStringLiteral)?
  public boolean contextMenuEventType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "contextMenuEventType")) return false;
    if (!nextTokenIs(b, CONTEXTMENU)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, CONTEXT_MENU_EVENT_TYPE, null);
    r = consumeToken(b, CONTEXTMENU);
    p = r; // pin = 1
    r = r && contextMenuEventType_1(b, l + 1);
    exit_section_(b, l, m, CONTEXT_MENU_EVENT_TYPE, r, p, null);
    return r || p;
  }

  // (<<noIDCheck>> localizedStringLiteral)?
  private boolean contextMenuEventType_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "contextMenuEventType_1")) return false;
    contextMenuEventType_1_0(b, l + 1);
    return true;
  }

  // <<noIDCheck>> localizedStringLiteral
  private boolean contextMenuEventType_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "contextMenuEventType_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = noIDCheck(b, l + 1);
    r = r && localizedStringLiteral(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // CONTINUE
  public boolean continueActionOperator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "continueActionOperator")) return false;
    if (!nextTokenIs(b, CONTINUE)) return false;
    boolean r;
    Marker m = enter_section_(b, l, CONTINUE_ACTION_OPERATOR);
    r = consumeToken(b, CONTINUE);
    exit_section_(b, m, CONTINUE_ACTION_OPERATOR, r);
    return r;
  }

  /* ********************************************************** */
  // INTERNAL ((CLIENT jsStringUsage bracketedClassNameList?) | (javaClassStringUsage bracketedClassNameList?)  | codeLiteral) (NULL)?
  public boolean customActionPropertyDefinitionBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "customActionPropertyDefinitionBody")) return false;
    if (!nextTokenIs(b, INTERNAL)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, CUSTOM_ACTION_PROPERTY_DEFINITION_BODY, null);
    r = consumeToken(b, INTERNAL);
    p = r; // pin = 1
    r = r && customActionPropertyDefinitionBody_1(b, l + 1);
    r = r && customActionPropertyDefinitionBody_2(b, l + 1);
    exit_section_(b, l, m, CUSTOM_ACTION_PROPERTY_DEFINITION_BODY, r, p, null);
    return r || p;
  }

  // (CLIENT jsStringUsage bracketedClassNameList?) | (javaClassStringUsage bracketedClassNameList?)  | codeLiteral
  private boolean customActionPropertyDefinitionBody_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "customActionPropertyDefinitionBody_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = customActionPropertyDefinitionBody_1_0(b, l + 1);
    if (!r) r = customActionPropertyDefinitionBody_1_1(b, l + 1);
    if (!r) r = codeLiteral(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // CLIENT jsStringUsage bracketedClassNameList?
  private boolean customActionPropertyDefinitionBody_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "customActionPropertyDefinitionBody_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, CLIENT);
    r = r && jsStringUsage(b, l + 1);
    r = r && customActionPropertyDefinitionBody_1_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // bracketedClassNameList?
  private boolean customActionPropertyDefinitionBody_1_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "customActionPropertyDefinitionBody_1_0_2")) return false;
    bracketedClassNameList(b, l + 1);
    return true;
  }

  // javaClassStringUsage bracketedClassNameList?
  private boolean customActionPropertyDefinitionBody_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "customActionPropertyDefinitionBody_1_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = javaClassStringUsage(b, l + 1);
    r = r && customActionPropertyDefinitionBody_1_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // bracketedClassNameList?
  private boolean customActionPropertyDefinitionBody_1_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "customActionPropertyDefinitionBody_1_1_1")) return false;
    bracketedClassNameList(b, l + 1);
    return true;
  }

  // (NULL)?
  private boolean customActionPropertyDefinitionBody_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "customActionPropertyDefinitionBody_2")) return false;
    consumeToken(b, NULL);
    return true;
  }

  /* ********************************************************** */
  // compoundID
  public boolean customClassUsage(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "customClassUsage")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, CUSTOM_CLASS_USAGE, "<custom class usage>");
    r = compoundID(b, l + 1);
    exit_section_(b, l, m, CUSTOM_CLASS_USAGE, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // customClassUsage
  public boolean customClassUsageWrapper(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "customClassUsageWrapper")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, CUSTOM_CLASS_USAGE_WRAPPER, "<custom class usage wrapper>");
    r = customClassUsage(b, l + 1);
    exit_section_(b, l, m, CUSTOM_CLASS_USAGE_WRAPPER, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // CUSTOM
  public boolean customFormDesignOption(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "customFormDesignOption")) return false;
    if (!nextTokenIs(b, CUSTOM)) return false;
    boolean r;
    Marker m = enter_section_(b, l, CUSTOM_FORM_DESIGN_OPTION);
    r = consumeToken(b, CUSTOM);
    exit_section_(b, m, CUSTOM_FORM_DESIGN_OPTION, r);
    return r;
  }

  /* ********************************************************** */
  // HEADER | OPTIONS
  public boolean customHeaderLiteral(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "customHeaderLiteral")) return false;
    if (!nextTokenIs(b, "<custom header literal>", HEADER, OPTIONS)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, CUSTOM_HEADER_LITERAL, "<custom header literal>");
    r = consumeToken(b, HEADER);
    if (!r) r = consumeToken(b, OPTIONS);
    exit_section_(b, l, m, CUSTOM_HEADER_LITERAL, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // propertyCustomView
  public boolean customViewSetting(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "customViewSetting")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, CUSTOM_VIEW_SETTING, "<custom view setting>");
    r = propertyCustomView(b, l + 1);
    exit_section_(b, l, m, CUSTOM_VIEW_SETTING, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // DATA (dataPropertySessionModifier)? className (LBRAC classNameList RBRAC)?
  public boolean dataPropertyDefinition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dataPropertyDefinition")) return false;
    if (!nextTokenIs(b, DATA)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, DATA_PROPERTY_DEFINITION, null);
    r = consumeToken(b, DATA);
    p = r; // pin = 1
    r = r && dataPropertyDefinition_1(b, l + 1);
    r = r && className(b, l + 1);
    r = r && dataPropertyDefinition_3(b, l + 1);
    exit_section_(b, l, m, DATA_PROPERTY_DEFINITION, r, p, null);
    return r || p;
  }

  // (dataPropertySessionModifier)?
  private boolean dataPropertyDefinition_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dataPropertyDefinition_1")) return false;
    dataPropertyDefinition_1_0(b, l + 1);
    return true;
  }

  // (dataPropertySessionModifier)
  private boolean dataPropertyDefinition_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dataPropertyDefinition_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = dataPropertySessionModifier(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (LBRAC classNameList RBRAC)?
  private boolean dataPropertyDefinition_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dataPropertyDefinition_3")) return false;
    dataPropertyDefinition_3_0(b, l + 1);
    return true;
  }

  // LBRAC classNameList RBRAC
  private boolean dataPropertyDefinition_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dataPropertyDefinition_3_0")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, LBRAC);
    p = r; // pin = 1
    r = r && classNameList(b, l + 1);
    r = r && consumeToken(b, RBRAC);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // LOCAL nestedLocalModifier
  public boolean dataPropertySessionModifier(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dataPropertySessionModifier")) return false;
    if (!nextTokenIs(b, LOCAL)) return false;
    boolean r;
    Marker m = enter_section_(b, l, DATA_PROPERTY_SESSION_MODIFIER);
    r = consumeToken(b, LOCAL);
    r = r && nestedLocalModifier(b, l + 1);
    exit_section_(b, m, DATA_PROPERTY_SESSION_MODIFIER, r);
    return r;
  }

  /* ********************************************************** */
  // LEX_DATE_LITERAL
  public boolean dateLiteral(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dateLiteral")) return false;
    if (!nextTokenIs(b, LEX_DATE_LITERAL)) return false;
    boolean r;
    Marker m = enter_section_(b, l, DATE_LITERAL);
    r = consumeToken(b, LEX_DATE_LITERAL);
    exit_section_(b, m, DATE_LITERAL, r);
    return r;
  }

  /* ********************************************************** */
  // LEX_DATETIME_LITERAL
  public boolean dateTimeLiteral(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dateTimeLiteral")) return false;
    if (!nextTokenIs(b, LEX_DATETIME_LITERAL)) return false;
    boolean r;
    Marker m = enter_section_(b, l, DATE_TIME_LITERAL);
    r = consumeToken(b, LEX_DATETIME_LITERAL);
    exit_section_(b, m, DATE_TIME_LITERAL, r);
    return r;
  }

  /* ********************************************************** */
  // DEFAULTCOMPARE stringLiteral
  public boolean defaultCompareSetting(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "defaultCompareSetting")) return false;
    if (!nextTokenIs(b, DEFAULTCOMPARE)) return false;
    boolean r;
    Marker m = enter_section_(b, l, DEFAULT_COMPARE_SETTING);
    r = consumeToken(b, DEFAULTCOMPARE);
    r = r && stringLiteral(b, l + 1);
    exit_section_(b, m, DEFAULT_COMPARE_SETTING, r);
    return r;
  }

  /* ********************************************************** */
  // DELETE parameterOrExpression changeClassWhere?
  public boolean deleteActionPropertyDefinitionBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "deleteActionPropertyDefinitionBody")) return false;
    if (!nextTokenIs(b, DELETE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, DELETE_ACTION_PROPERTY_DEFINITION_BODY, null);
    r = consumeToken(b, DELETE);
    p = r; // pin = 1
    r = r && parameterOrExpression(b, l + 1);
    r = r && deleteActionPropertyDefinitionBody_2(b, l + 1);
    exit_section_(b, l, m, DELETE_ACTION_PROPERTY_DEFINITION_BODY, r, p, null);
    return r || p;
  }

  // changeClassWhere?
  private boolean deleteActionPropertyDefinitionBody_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "deleteActionPropertyDefinitionBody_2")) return false;
    changeClassWhere(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // formCalcPropertyObject
  public boolean designCalcPropertyObject(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "designCalcPropertyObject")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, DESIGN_CALC_PROPERTY_OBJECT, "<design calc property object>");
    r = formCalcPropertyObject(b, l + 1);
    exit_section_(b, l, m, DESIGN_CALC_PROPERTY_OBJECT, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // DESIGN formUsage (localizedStringLiteral)? customFormDesignOption?
  public boolean designHeader(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "designHeader")) return false;
    if (!nextTokenIs(b, DESIGN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, DESIGN_HEADER, null);
    r = consumeToken(b, DESIGN);
    r = r && formUsage(b, l + 1);
    p = r; // pin = 2
    r = r && designHeader_2(b, l + 1);
    r = r && designHeader_3(b, l + 1);
    exit_section_(b, l, m, DESIGN_HEADER, r, p, null);
    return r || p;
  }

  // (localizedStringLiteral)?
  private boolean designHeader_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "designHeader_2")) return false;
    designHeader_2_0(b, l + 1);
    return true;
  }

  // (localizedStringLiteral)
  private boolean designHeader_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "designHeader_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = localizedStringLiteral(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // customFormDesignOption?
  private boolean designHeader_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "designHeader_3")) return false;
    customFormDesignOption(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // designHeader componentBody
  public boolean designStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "designStatement")) return false;
    if (!nextTokenIs(b, DESIGN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, DESIGN_STATEMENT, null);
    r = designHeader(b, l + 1);
    p = r; // pin = 1
    r = r && componentBody(b, l + 1);
    exit_section_(b, l, m, DESIGN_STATEMENT, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // DIALOG
  // 	    mappedForm
  // 		(
  // 		    contextFiltersClause
  //         |   windowTypeLiteral
  // 		|   manageSessionClause
  // 		|   noCancelClause
  // 		|   formSessionScopeClause
  // 		|   CHECK
  // 		|   READONLY
  // 		)*
  // 		doInputBody
  public boolean dialogActionPropertyDefinitionBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dialogActionPropertyDefinitionBody")) return false;
    if (!nextTokenIs(b, DIALOG)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, DIALOG_ACTION_PROPERTY_DEFINITION_BODY, null);
    r = consumeToken(b, DIALOG);
    p = r; // pin = 1
    r = r && mappedForm(b, l + 1);
    r = r && dialogActionPropertyDefinitionBody_2(b, l + 1);
    r = r && doInputBody(b, l + 1);
    exit_section_(b, l, m, DIALOG_ACTION_PROPERTY_DEFINITION_BODY, r, p, null);
    return r || p;
  }

  // (
  // 		    contextFiltersClause
  //         |   windowTypeLiteral
  // 		|   manageSessionClause
  // 		|   noCancelClause
  // 		|   formSessionScopeClause
  // 		|   CHECK
  // 		|   READONLY
  // 		)*
  private boolean dialogActionPropertyDefinitionBody_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dialogActionPropertyDefinitionBody_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!dialogActionPropertyDefinitionBody_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "dialogActionPropertyDefinitionBody_2", c)) break;
    }
    return true;
  }

  // contextFiltersClause
  //         |   windowTypeLiteral
  // 		|   manageSessionClause
  // 		|   noCancelClause
  // 		|   formSessionScopeClause
  // 		|   CHECK
  // 		|   READONLY
  private boolean dialogActionPropertyDefinitionBody_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dialogActionPropertyDefinitionBody_2_0")) return false;
    boolean r;
    r = contextFiltersClause(b, l + 1);
    if (!r) r = windowTypeLiteral(b, l + 1);
    if (!r) r = manageSessionClause(b, l + 1);
    if (!r) r = noCancelClause(b, l + 1);
    if (!r) r = formSessionScopeClause(b, l + 1);
    if (!r) r = consumeToken(b, CHECK);
    if (!r) r = consumeToken(b, READONLY);
    return r;
  }

  /* ********************************************************** */
  // LBRAC intLiteral COMMA intLiteral RBRAC
  public boolean dimensionLiteral(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dimensionLiteral")) return false;
    if (!nextTokenIs(b, LBRAC)) return false;
    boolean r;
    Marker m = enter_section_(b, l, DIMENSION_LITERAL);
    r = consumeToken(b, LBRAC);
    r = r && intLiteral(b, l + 1);
    r = r && consumeToken(b, COMMA);
    r = r && intLiteral(b, l + 1);
    r = r && consumeToken(b, RBRAC);
    exit_section_(b, m, DIMENSION_LITERAL, r);
    return r;
  }

  /* ********************************************************** */
  // SEMI | (doMainBody (ELSE actionPropertyDefinitionBody)?)
  public boolean doInputBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "doInputBody")) return false;
    if (!nextTokenIs(b, "<do input body>", DO, SEMI)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, DO_INPUT_BODY, "<do input body>");
    r = consumeToken(b, SEMI);
    if (!r) r = doInputBody_1(b, l + 1);
    exit_section_(b, l, m, DO_INPUT_BODY, r, false, null);
    return r;
  }

  // doMainBody (ELSE actionPropertyDefinitionBody)?
  private boolean doInputBody_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "doInputBody_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = doMainBody(b, l + 1);
    r = r && doInputBody_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (ELSE actionPropertyDefinitionBody)?
  private boolean doInputBody_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "doInputBody_1_1")) return false;
    doInputBody_1_1_0(b, l + 1);
    return true;
  }

  // ELSE actionPropertyDefinitionBody
  private boolean doInputBody_1_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "doInputBody_1_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, ELSE);
    r = r && actionPropertyDefinitionBody(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // DO actionPropertyDefinitionBody
  public boolean doMainBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "doMainBody")) return false;
    if (!nextTokenIs(b, DO)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, DO_MAIN_BODY, null);
    r = consumeToken(b, DO);
    p = r; // pin = 1
    r = r && actionPropertyDefinitionBody(b, l + 1);
    exit_section_(b, l, m, DO_MAIN_BODY, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // POSITION LBRAC intLiteral COMMA intLiteral COMMA intLiteral COMMA intLiteral RBRAC
  public boolean dockPosition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dockPosition")) return false;
    if (!nextTokenIs(b, POSITION)) return false;
    boolean r;
    Marker m = enter_section_(b, l, DOCK_POSITION);
    r = consumeTokens(b, 0, POSITION, LBRAC);
    r = r && intLiteral(b, l + 1);
    r = r && consumeToken(b, COMMA);
    r = r && intLiteral(b, l + 1);
    r = r && consumeToken(b, COMMA);
    r = r && intLiteral(b, l + 1);
    r = r && consumeToken(b, COMMA);
    r = r && intLiteral(b, l + 1);
    r = r && consumeToken(b, RBRAC);
    exit_section_(b, m, DOCK_POSITION, r);
    return r;
  }

  /* ********************************************************** */
  // (MINUS)? unumericLiteral
  public boolean doubleLiteral(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "doubleLiteral")) return false;
    if (!nextTokenIs(b, "<double literal>", LEX_UNUMERIC_LITERAL, MINUS)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, DOUBLE_LITERAL, "<double literal>");
    r = doubleLiteral_0(b, l + 1);
    r = r && unumericLiteral(b, l + 1);
    exit_section_(b, l, m, DOUBLE_LITERAL, r, false, null);
    return r;
  }

  // (MINUS)?
  private boolean doubleLiteral_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "doubleLiteral_0")) return false;
    consumeToken(b, MINUS);
    return true;
  }

  /* ********************************************************** */
  // DRAWROOT
  public boolean drawRoot(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "drawRoot")) return false;
    if (!nextTokenIs(b, DRAWROOT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, DRAW_ROOT);
    r = consumeToken(b, DRAWROOT);
    exit_section_(b, m, DRAW_ROOT, r);
    return r;
  }

  /* ********************************************************** */
  // DRILLDOWN propertyExpression
  public boolean drillDownActionPropertyDefinitionBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "drillDownActionPropertyDefinitionBody")) return false;
    if (!nextTokenIs(b, DRILLDOWN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, DRILL_DOWN_ACTION_PROPERTY_DEFINITION_BODY, null);
    r = consumeToken(b, DRILLDOWN);
    p = r; // pin = 1
    r = r && propertyExpression(b, l + 1);
    exit_section_(b, l, m, DRILL_DOWN_ACTION_PROPERTY_DEFINITION_BODY, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // ECHO
  public boolean echoSymbolsSetting(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "echoSymbolsSetting")) return false;
    if (!nextTokenIs(b, ECHO)) return false;
    boolean r;
    Marker m = enter_section_(b, l, ECHO_SYMBOLS_SETTING);
    r = consumeToken(b, ECHO);
    exit_section_(b, m, ECHO_SYMBOLS_SETTING, r);
    return r;
  }

  /* ********************************************************** */
  // EDIT customClassUsage OBJECT objectUsage
  public boolean editFormDeclaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "editFormDeclaration")) return false;
    if (!nextTokenIs(b, EDIT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, EDIT_FORM_DECLARATION, null);
    r = consumeToken(b, EDIT);
    p = r; // pin = 1
    r = r && customClassUsage(b, l + 1);
    r = r && consumeToken(b, OBJECT);
    r = r && objectUsage(b, l + 1);
    exit_section_(b, l, m, EDIT_FORM_DECLARATION, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // EMAIL
  // 		(FROM propertyExpression)?
  // 		(SUBJECT propertyExpression)?
  // 		(emailRecipientTypeLiteral propertyExpression)+
  // 		(BODY propertyExpression)?
  // 		(ATTACH ((propertyExpression (NAME propertyExpression)?) | (LIST emailPropertyUsage (NAME emailPropertyUsage)?)))*
  //         syncTypeLiteral?
  public boolean emailActionPropertyDefinitionBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "emailActionPropertyDefinitionBody")) return false;
    if (!nextTokenIs(b, EMAIL)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, EMAIL_ACTION_PROPERTY_DEFINITION_BODY, null);
    r = consumeToken(b, EMAIL);
    p = r; // pin = 1
    r = r && emailActionPropertyDefinitionBody_1(b, l + 1);
    r = r && emailActionPropertyDefinitionBody_2(b, l + 1);
    r = r && emailActionPropertyDefinitionBody_3(b, l + 1);
    r = r && emailActionPropertyDefinitionBody_4(b, l + 1);
    r = r && emailActionPropertyDefinitionBody_5(b, l + 1);
    r = r && emailActionPropertyDefinitionBody_6(b, l + 1);
    exit_section_(b, l, m, EMAIL_ACTION_PROPERTY_DEFINITION_BODY, r, p, null);
    return r || p;
  }

  // (FROM propertyExpression)?
  private boolean emailActionPropertyDefinitionBody_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "emailActionPropertyDefinitionBody_1")) return false;
    emailActionPropertyDefinitionBody_1_0(b, l + 1);
    return true;
  }

  // FROM propertyExpression
  private boolean emailActionPropertyDefinitionBody_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "emailActionPropertyDefinitionBody_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, FROM);
    r = r && propertyExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (SUBJECT propertyExpression)?
  private boolean emailActionPropertyDefinitionBody_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "emailActionPropertyDefinitionBody_2")) return false;
    emailActionPropertyDefinitionBody_2_0(b, l + 1);
    return true;
  }

  // SUBJECT propertyExpression
  private boolean emailActionPropertyDefinitionBody_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "emailActionPropertyDefinitionBody_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, SUBJECT);
    r = r && propertyExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (emailRecipientTypeLiteral propertyExpression)+
  private boolean emailActionPropertyDefinitionBody_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "emailActionPropertyDefinitionBody_3")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = emailActionPropertyDefinitionBody_3_0(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!emailActionPropertyDefinitionBody_3_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "emailActionPropertyDefinitionBody_3", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // emailRecipientTypeLiteral propertyExpression
  private boolean emailActionPropertyDefinitionBody_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "emailActionPropertyDefinitionBody_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = emailRecipientTypeLiteral(b, l + 1);
    r = r && propertyExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (BODY propertyExpression)?
  private boolean emailActionPropertyDefinitionBody_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "emailActionPropertyDefinitionBody_4")) return false;
    emailActionPropertyDefinitionBody_4_0(b, l + 1);
    return true;
  }

  // BODY propertyExpression
  private boolean emailActionPropertyDefinitionBody_4_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "emailActionPropertyDefinitionBody_4_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, BODY);
    r = r && propertyExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (ATTACH ((propertyExpression (NAME propertyExpression)?) | (LIST emailPropertyUsage (NAME emailPropertyUsage)?)))*
  private boolean emailActionPropertyDefinitionBody_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "emailActionPropertyDefinitionBody_5")) return false;
    while (true) {
      int c = current_position_(b);
      if (!emailActionPropertyDefinitionBody_5_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "emailActionPropertyDefinitionBody_5", c)) break;
    }
    return true;
  }

  // ATTACH ((propertyExpression (NAME propertyExpression)?) | (LIST emailPropertyUsage (NAME emailPropertyUsage)?))
  private boolean emailActionPropertyDefinitionBody_5_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "emailActionPropertyDefinitionBody_5_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, ATTACH);
    r = r && emailActionPropertyDefinitionBody_5_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (propertyExpression (NAME propertyExpression)?) | (LIST emailPropertyUsage (NAME emailPropertyUsage)?)
  private boolean emailActionPropertyDefinitionBody_5_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "emailActionPropertyDefinitionBody_5_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = emailActionPropertyDefinitionBody_5_0_1_0(b, l + 1);
    if (!r) r = emailActionPropertyDefinitionBody_5_0_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // propertyExpression (NAME propertyExpression)?
  private boolean emailActionPropertyDefinitionBody_5_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "emailActionPropertyDefinitionBody_5_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = propertyExpression(b, l + 1);
    r = r && emailActionPropertyDefinitionBody_5_0_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (NAME propertyExpression)?
  private boolean emailActionPropertyDefinitionBody_5_0_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "emailActionPropertyDefinitionBody_5_0_1_0_1")) return false;
    emailActionPropertyDefinitionBody_5_0_1_0_1_0(b, l + 1);
    return true;
  }

  // NAME propertyExpression
  private boolean emailActionPropertyDefinitionBody_5_0_1_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "emailActionPropertyDefinitionBody_5_0_1_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, NAME);
    r = r && propertyExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // LIST emailPropertyUsage (NAME emailPropertyUsage)?
  private boolean emailActionPropertyDefinitionBody_5_0_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "emailActionPropertyDefinitionBody_5_0_1_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, LIST);
    r = r && emailPropertyUsage(b, l + 1);
    r = r && emailActionPropertyDefinitionBody_5_0_1_1_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (NAME emailPropertyUsage)?
  private boolean emailActionPropertyDefinitionBody_5_0_1_1_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "emailActionPropertyDefinitionBody_5_0_1_1_2")) return false;
    emailActionPropertyDefinitionBody_5_0_1_1_2_0(b, l + 1);
    return true;
  }

  // NAME emailPropertyUsage
  private boolean emailActionPropertyDefinitionBody_5_0_1_1_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "emailActionPropertyDefinitionBody_5_0_1_1_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, NAME);
    r = r && emailPropertyUsage(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // syncTypeLiteral?
  private boolean emailActionPropertyDefinitionBody_6(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "emailActionPropertyDefinitionBody_6")) return false;
    syncTypeLiteral(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // propertyUsage
  public boolean emailPropertyUsage(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "emailPropertyUsage")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, EMAIL_PROPERTY_USAGE, "<email property usage>");
    r = propertyUsage(b, l + 1);
    exit_section_(b, l, m, EMAIL_PROPERTY_USAGE, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // TO | CC | BCC
  public boolean emailRecipientTypeLiteral(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "emailRecipientTypeLiteral")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, EMAIL_RECIPIENT_TYPE_LITERAL, "<email recipient type literal>");
    r = consumeToken(b, TO);
    if (!r) r = consumeToken(b, CC);
    if (!r) r = consumeToken(b, BCC);
    exit_section_(b, l, m, EMAIL_RECIPIENT_TYPE_LITERAL, r, false, null);
    return r;
  }

  /* ********************************************************** */
  public boolean emptyActionPropertyDefinitionBody(PsiBuilder b, int l) {
    Marker m = enter_section_(b, l, EMPTY_ACTION_PROPERTY_DEFINITION_BODY);
    exit_section_(b, m, EMPTY_ACTION_PROPERTY_DEFINITION_BODY, true);
    return true;
  }

  /* ********************************************************** */
  // nonEmptyExplicitPropClassList?
  public boolean emptyExplicitPropClassList(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "emptyExplicitPropClassList")) return false;
    Marker m = enter_section_(b, l, _NONE_, EMPTY_EXPLICIT_PROP_CLASS_LIST, "<empty explicit prop class list>");
    nonEmptyExplicitPropClassList(b, l + 1);
    exit_section_(b, l, m, EMPTY_EXPLICIT_PROP_CLASS_LIST, true, false, null);
    return true;
  }

  /* ********************************************************** */
  // SEMI
  public boolean emptyStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "emptyStatement")) return false;
    if (!nextTokenIs(b, SEMI)) return false;
    boolean r;
    Marker m = enter_section_(b, l, EMPTY_STATEMENT);
    r = consumeToken(b, SEMI);
    exit_section_(b, m, EMPTY_STATEMENT, r);
    return r;
  }

  /* ********************************************************** */
  // relationalPE ((EQ_OPERAND | EQUALS) relationalPE)?
  public boolean equalityPE(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "equalityPE")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, EQUALITY_PE, "<equality pe>");
    r = relationalPE(b, l + 1);
    r = r && equalityPE_1(b, l + 1);
    exit_section_(b, l, m, EQUALITY_PE, r, false, null);
    return r;
  }

  // ((EQ_OPERAND | EQUALS) relationalPE)?
  private boolean equalityPE_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "equalityPE_1")) return false;
    equalityPE_1_0(b, l + 1);
    return true;
  }

  // (EQ_OPERAND | EQUALS) relationalPE
  private boolean equalityPE_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "equalityPE_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = equalityPE_1_0_0(b, l + 1);
    r = r && relationalPE(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // EQ_OPERAND | EQUALS
  private boolean equalityPE_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "equalityPE_1_0_0")) return false;
    boolean r;
    r = consumeToken(b, EQ_OPERAND);
    if (!r) r = consumeToken(b, EQUALS);
    return r;
  }

  /* ********************************************************** */
  // EQUALS
  public boolean equalsSign(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "equalsSign")) return false;
    if (!nextTokenIs(b, EQUALS)) return false;
    boolean r;
    Marker m = enter_section_(b, l, EQUALS_SIGN);
    r = consumeToken(b, EQUALS);
    exit_section_(b, m, EQUALS_SIGN, r);
    return r;
  }

  /* ********************************************************** */
  // EVAL (ACTION)? propertyExpression (PARAMS propertyExpressionList)?
  public boolean evalActionPropertyDefinitionBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "evalActionPropertyDefinitionBody")) return false;
    if (!nextTokenIs(b, EVAL)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, EVAL_ACTION_PROPERTY_DEFINITION_BODY, null);
    r = consumeToken(b, EVAL);
    p = r; // pin = 1
    r = r && evalActionPropertyDefinitionBody_1(b, l + 1);
    r = r && propertyExpression(b, l + 1);
    r = r && evalActionPropertyDefinitionBody_3(b, l + 1);
    exit_section_(b, l, m, EVAL_ACTION_PROPERTY_DEFINITION_BODY, r, p, null);
    return r || p;
  }

  // (ACTION)?
  private boolean evalActionPropertyDefinitionBody_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "evalActionPropertyDefinitionBody_1")) return false;
    consumeToken(b, ACTION);
    return true;
  }

  // (PARAMS propertyExpressionList)?
  private boolean evalActionPropertyDefinitionBody_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "evalActionPropertyDefinitionBody_3")) return false;
    evalActionPropertyDefinitionBody_3_0(b, l + 1);
    return true;
  }

  // PARAMS propertyExpressionList
  private boolean evalActionPropertyDefinitionBody_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "evalActionPropertyDefinitionBody_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, PARAMS);
    r = r && propertyExpressionList(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // EVENTID stringLiteral
  public boolean eventIdSetting(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "eventIdSetting")) return false;
    if (!nextTokenIs(b, EVENTID)) return false;
    boolean r;
    Marker m = enter_section_(b, l, EVENT_ID_SETTING);
    r = consumeToken(b, EVENTID);
    r = r && stringLiteral(b, l + 1);
    exit_section_(b, m, EVENT_ID_SETTING, r);
    return r;
  }

  /* ********************************************************** */
  // WHEN baseEventPE propertyExpression (ORDER (DESC)? nonEmptyPropertyExpressionList)?
  // 		                inlineOption DO actionPropertyDefinitionBody
  public boolean eventStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "eventStatement")) return false;
    if (!nextTokenIs(b, WHEN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, EVENT_STATEMENT, null);
    r = consumeToken(b, WHEN);
    p = r; // pin = 1
    r = r && baseEventPE(b, l + 1);
    r = r && propertyExpression(b, l + 1);
    r = r && eventStatement_3(b, l + 1);
    r = r && inlineOption(b, l + 1);
    r = r && consumeToken(b, DO);
    r = r && actionPropertyDefinitionBody(b, l + 1);
    exit_section_(b, l, m, EVENT_STATEMENT, r, p, null);
    return r || p;
  }

  // (ORDER (DESC)? nonEmptyPropertyExpressionList)?
  private boolean eventStatement_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "eventStatement_3")) return false;
    eventStatement_3_0(b, l + 1);
    return true;
  }

  // ORDER (DESC)? nonEmptyPropertyExpressionList
  private boolean eventStatement_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "eventStatement_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, ORDER);
    r = r && eventStatement_3_0_1(b, l + 1);
    r = r && nonEmptyPropertyExpressionList(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (DESC)?
  private boolean eventStatement_3_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "eventStatement_3_0_1")) return false;
    consumeToken(b, DESC);
    return true;
  }

  /* ********************************************************** */
  // EXCLUSIVE
  public boolean exclusiveOperator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exclusiveOperator")) return false;
    if (!nextTokenIs(b, EXCLUSIVE)) return false;
    boolean r;
    Marker m = enter_section_(b, l, EXCLUSIVE_OPERATOR);
    r = consumeToken(b, EXCLUSIVE);
    exit_section_(b, m, EXCLUSIVE_OPERATOR, r);
    return r;
  }

  /* ********************************************************** */
  // OVERRIDE | EXCLUSIVE
  public boolean exclusiveOverrideOption(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exclusiveOverrideOption")) return false;
    if (!nextTokenIs(b, "<exclusive override option>", EXCLUSIVE, OVERRIDE)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, EXCLUSIVE_OVERRIDE_OPTION, "<exclusive override option>");
    r = consumeToken(b, OVERRIDE);
    if (!r) r = consumeToken(b, EXCLUSIVE);
    exit_section_(b, l, m, EXCLUSIVE_OVERRIDE_OPTION, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // execActionPropertyDefinitionBody1 | execActionPropertyDefinitionBody2
  public boolean execActionPropertyDefinitionBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "execActionPropertyDefinitionBody")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, EXEC_ACTION_PROPERTY_DEFINITION_BODY, "<exec action property definition body>");
    r = execActionPropertyDefinitionBody1(b, l + 1);
    if (!r) r = execActionPropertyDefinitionBody2(b, l + 1);
    exit_section_(b, l, m, EXEC_ACTION_PROPERTY_DEFINITION_BODY, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // EXEC actionUsage LBRAC propertyExpressionList RBRAC
  boolean execActionPropertyDefinitionBody1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "execActionPropertyDefinitionBody1")) return false;
    if (!nextTokenIs(b, EXEC)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, EXEC);
    p = r; // pin = 1
    r = r && actionUsage(b, l + 1);
    r = r && consumeToken(b, LBRAC);
    r = r && propertyExpressionList(b, l + 1);
    r = r && consumeToken(b, RBRAC);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // actionUsage LBRAC propertyExpressionList RBRAC
  boolean execActionPropertyDefinitionBody2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "execActionPropertyDefinitionBody2")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = actionUsage(b, l + 1);
    p = r; // pin = 1
    r = r && consumeToken(b, LBRAC);
    r = r && propertyExpressionList(b, l + 1);
    r = r && consumeToken(b, RBRAC);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // (COLLAPSE | EXPAND) CONTAINER componentID
  public boolean expandCollapseActionPropertyDefinitionBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expandCollapseActionPropertyDefinitionBody")) return false;
    if (!nextTokenIs(b, "<expand collapse action property definition body>", COLLAPSE, EXPAND)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, EXPAND_COLLAPSE_ACTION_PROPERTY_DEFINITION_BODY, "<expand collapse action property definition body>");
    r = expandCollapseActionPropertyDefinitionBody_0(b, l + 1);
    r = r && consumeToken(b, CONTAINER);
    r = r && componentID(b, l + 1);
    exit_section_(b, l, m, EXPAND_COLLAPSE_ACTION_PROPERTY_DEFINITION_BODY, r, false, null);
    return r;
  }

  // COLLAPSE | EXPAND
  private boolean expandCollapseActionPropertyDefinitionBody_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expandCollapseActionPropertyDefinitionBody_0")) return false;
    boolean r;
    r = consumeToken(b, COLLAPSE);
    if (!r) r = consumeToken(b, EXPAND);
    return r;
  }

  /* ********************************************************** */
  // EXPAND (DOWN | UP | ALL (TOP)?)?
  //                                             groupObjectID (OBJECTS objectExpr (COMMA objectExpr)*)?
  public boolean expandGroupObjectActionPropertyDefinitionBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expandGroupObjectActionPropertyDefinitionBody")) return false;
    if (!nextTokenIs(b, EXPAND)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, EXPAND_GROUP_OBJECT_ACTION_PROPERTY_DEFINITION_BODY, null);
    r = consumeToken(b, EXPAND);
    r = r && expandGroupObjectActionPropertyDefinitionBody_1(b, l + 1);
    r = r && groupObjectID(b, l + 1);
    p = r; // pin = 3
    r = r && expandGroupObjectActionPropertyDefinitionBody_3(b, l + 1);
    exit_section_(b, l, m, EXPAND_GROUP_OBJECT_ACTION_PROPERTY_DEFINITION_BODY, r, p, null);
    return r || p;
  }

  // (DOWN | UP | ALL (TOP)?)?
  private boolean expandGroupObjectActionPropertyDefinitionBody_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expandGroupObjectActionPropertyDefinitionBody_1")) return false;
    expandGroupObjectActionPropertyDefinitionBody_1_0(b, l + 1);
    return true;
  }

  // DOWN | UP | ALL (TOP)?
  private boolean expandGroupObjectActionPropertyDefinitionBody_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expandGroupObjectActionPropertyDefinitionBody_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, DOWN);
    if (!r) r = consumeToken(b, UP);
    if (!r) r = expandGroupObjectActionPropertyDefinitionBody_1_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ALL (TOP)?
  private boolean expandGroupObjectActionPropertyDefinitionBody_1_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expandGroupObjectActionPropertyDefinitionBody_1_0_2")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, ALL);
    r = r && expandGroupObjectActionPropertyDefinitionBody_1_0_2_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (TOP)?
  private boolean expandGroupObjectActionPropertyDefinitionBody_1_0_2_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expandGroupObjectActionPropertyDefinitionBody_1_0_2_1")) return false;
    consumeToken(b, TOP);
    return true;
  }

  // (OBJECTS objectExpr (COMMA objectExpr)*)?
  private boolean expandGroupObjectActionPropertyDefinitionBody_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expandGroupObjectActionPropertyDefinitionBody_3")) return false;
    expandGroupObjectActionPropertyDefinitionBody_3_0(b, l + 1);
    return true;
  }

  // OBJECTS objectExpr (COMMA objectExpr)*
  private boolean expandGroupObjectActionPropertyDefinitionBody_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expandGroupObjectActionPropertyDefinitionBody_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, OBJECTS);
    r = r && objectExpr(b, l + 1);
    r = r && expandGroupObjectActionPropertyDefinitionBody_3_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (COMMA objectExpr)*
  private boolean expandGroupObjectActionPropertyDefinitionBody_3_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expandGroupObjectActionPropertyDefinitionBody_3_0_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!expandGroupObjectActionPropertyDefinitionBody_3_0_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "expandGroupObjectActionPropertyDefinitionBody_3_0_2", c)) break;
    }
    return true;
  }

  // COMMA objectExpr
  private boolean expandGroupObjectActionPropertyDefinitionBody_3_0_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expandGroupObjectActionPropertyDefinitionBody_3_0_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, COMMA);
    r = r && objectExpr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // actionStatement
  public boolean explicitInterfaceActStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "explicitInterfaceActStatement")) return false;
    if (!nextTokenIs(b, ID)) return false;
    boolean r;
    Marker m = enter_section_(b, l, EXPLICIT_INTERFACE_ACT_STATEMENT);
    r = actionStatement(b, l + 1);
    exit_section_(b, m, EXPLICIT_INTERFACE_ACT_STATEMENT, r);
    return r;
  }

  /* ********************************************************** */
  // explicitValuePropertyStatement
  public boolean explicitInterfacePropertyStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "explicitInterfacePropertyStatement")) return false;
    if (!nextTokenIs(b, ID)) return false;
    boolean r;
    Marker m = enter_section_(b, l, EXPLICIT_INTERFACE_PROPERTY_STATEMENT);
    r = explicitValuePropertyStatement(b, l + 1);
    exit_section_(b, m, EXPLICIT_INTERFACE_PROPERTY_STATEMENT, r);
    return r;
  }

  /* ********************************************************** */
  // className | QUESTION
  public boolean explicitPropClass(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "explicitPropClass")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, EXPLICIT_PROP_CLASS, "<explicit prop class>");
    r = className(b, l + 1);
    if (!r) r = consumeToken(b, QUESTION);
    exit_section_(b, l, m, EXPLICIT_PROP_CLASS, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // LSQBR emptyExplicitPropClassList RSQBR
  public boolean explicitPropClassUsage(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "explicitPropClassUsage")) return false;
    if (!nextTokenIs(b, LSQBR)) return false;
    boolean r;
    Marker m = enter_section_(b, l, EXPLICIT_PROP_CLASS_USAGE);
    r = consumeToken(b, LSQBR);
    r = r && emptyExplicitPropClassList(b, l + 1);
    r = r && consumeToken(b, RSQBR);
    exit_section_(b, m, EXPLICIT_PROP_CLASS_USAGE, r);
    return r;
  }

  /* ********************************************************** */
  // implicitValuePropertyStatement
  public boolean explicitValuePropertyStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "explicitValuePropertyStatement")) return false;
    if (!nextTokenIs(b, ID)) return false;
    boolean r;
    Marker m = enter_section_(b, l, EXPLICIT_VALUE_PROPERTY_STATEMENT);
    r = implicitValuePropertyStatement(b, l + 1);
    exit_section_(b, m, EXPLICIT_VALUE_PROPERTY_STATEMENT, r);
    return r;
  }

  /* ********************************************************** */
  // EXPORT mappedForm contextFiltersClause?
  //         (
  //             (
  //                 (   CSV (stringLiteral)? hasHeaderOption? noEscapeOption? (CHARSET stringLiteral)?
  //                 |   DBF (CHARSET stringLiteral)?
  //                 |   XLS sheetExpression? hasHeaderOption?
  //                 |   XLSX sheetExpression? hasHeaderOption?
  //                 |   TABLE
  //                 )
  //                 selectTops?
  //                 groupObjectDestination
  //             )
  //         |
  //             (
  //                 (   JSON (CHARSET stringLiteral)?
  //                 |   XML hasHeaderOption? (CHARSET stringLiteral)?
  //                 )?
  //                 selectTops?
  //                 staticDestination?
  //             )
  //         )
  public boolean exportActionPropertyDefinitionBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exportActionPropertyDefinitionBody")) return false;
    if (!nextTokenIs(b, EXPORT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, EXPORT_ACTION_PROPERTY_DEFINITION_BODY, null);
    r = consumeToken(b, EXPORT);
    r = r && mappedForm(b, l + 1);
    p = r; // pin = 2
    r = r && exportActionPropertyDefinitionBody_2(b, l + 1);
    r = r && exportActionPropertyDefinitionBody_3(b, l + 1);
    exit_section_(b, l, m, EXPORT_ACTION_PROPERTY_DEFINITION_BODY, r, p, null);
    return r || p;
  }

  // contextFiltersClause?
  private boolean exportActionPropertyDefinitionBody_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exportActionPropertyDefinitionBody_2")) return false;
    contextFiltersClause(b, l + 1);
    return true;
  }

  // (
  //                 (   CSV (stringLiteral)? hasHeaderOption? noEscapeOption? (CHARSET stringLiteral)?
  //                 |   DBF (CHARSET stringLiteral)?
  //                 |   XLS sheetExpression? hasHeaderOption?
  //                 |   XLSX sheetExpression? hasHeaderOption?
  //                 |   TABLE
  //                 )
  //                 selectTops?
  //                 groupObjectDestination
  //             )
  //         |
  //             (
  //                 (   JSON (CHARSET stringLiteral)?
  //                 |   XML hasHeaderOption? (CHARSET stringLiteral)?
  //                 )?
  //                 selectTops?
  //                 staticDestination?
  //             )
  private boolean exportActionPropertyDefinitionBody_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exportActionPropertyDefinitionBody_3")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = exportActionPropertyDefinitionBody_3_0(b, l + 1);
    if (!r) r = exportActionPropertyDefinitionBody_3_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (   CSV (stringLiteral)? hasHeaderOption? noEscapeOption? (CHARSET stringLiteral)?
  //                 |   DBF (CHARSET stringLiteral)?
  //                 |   XLS sheetExpression? hasHeaderOption?
  //                 |   XLSX sheetExpression? hasHeaderOption?
  //                 |   TABLE
  //                 )
  //                 selectTops?
  //                 groupObjectDestination
  private boolean exportActionPropertyDefinitionBody_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exportActionPropertyDefinitionBody_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = exportActionPropertyDefinitionBody_3_0_0(b, l + 1);
    r = r && exportActionPropertyDefinitionBody_3_0_1(b, l + 1);
    r = r && groupObjectDestination(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // CSV (stringLiteral)? hasHeaderOption? noEscapeOption? (CHARSET stringLiteral)?
  //                 |   DBF (CHARSET stringLiteral)?
  //                 |   XLS sheetExpression? hasHeaderOption?
  //                 |   XLSX sheetExpression? hasHeaderOption?
  //                 |   TABLE
  private boolean exportActionPropertyDefinitionBody_3_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exportActionPropertyDefinitionBody_3_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = exportActionPropertyDefinitionBody_3_0_0_0(b, l + 1);
    if (!r) r = exportActionPropertyDefinitionBody_3_0_0_1(b, l + 1);
    if (!r) r = exportActionPropertyDefinitionBody_3_0_0_2(b, l + 1);
    if (!r) r = exportActionPropertyDefinitionBody_3_0_0_3(b, l + 1);
    if (!r) r = consumeToken(b, TABLE);
    exit_section_(b, m, null, r);
    return r;
  }

  // CSV (stringLiteral)? hasHeaderOption? noEscapeOption? (CHARSET stringLiteral)?
  private boolean exportActionPropertyDefinitionBody_3_0_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exportActionPropertyDefinitionBody_3_0_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, CSV);
    r = r && exportActionPropertyDefinitionBody_3_0_0_0_1(b, l + 1);
    r = r && exportActionPropertyDefinitionBody_3_0_0_0_2(b, l + 1);
    r = r && exportActionPropertyDefinitionBody_3_0_0_0_3(b, l + 1);
    r = r && exportActionPropertyDefinitionBody_3_0_0_0_4(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (stringLiteral)?
  private boolean exportActionPropertyDefinitionBody_3_0_0_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exportActionPropertyDefinitionBody_3_0_0_0_1")) return false;
    exportActionPropertyDefinitionBody_3_0_0_0_1_0(b, l + 1);
    return true;
  }

  // (stringLiteral)
  private boolean exportActionPropertyDefinitionBody_3_0_0_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exportActionPropertyDefinitionBody_3_0_0_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = stringLiteral(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // hasHeaderOption?
  private boolean exportActionPropertyDefinitionBody_3_0_0_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exportActionPropertyDefinitionBody_3_0_0_0_2")) return false;
    hasHeaderOption(b, l + 1);
    return true;
  }

  // noEscapeOption?
  private boolean exportActionPropertyDefinitionBody_3_0_0_0_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exportActionPropertyDefinitionBody_3_0_0_0_3")) return false;
    noEscapeOption(b, l + 1);
    return true;
  }

  // (CHARSET stringLiteral)?
  private boolean exportActionPropertyDefinitionBody_3_0_0_0_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exportActionPropertyDefinitionBody_3_0_0_0_4")) return false;
    exportActionPropertyDefinitionBody_3_0_0_0_4_0(b, l + 1);
    return true;
  }

  // CHARSET stringLiteral
  private boolean exportActionPropertyDefinitionBody_3_0_0_0_4_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exportActionPropertyDefinitionBody_3_0_0_0_4_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, CHARSET);
    r = r && stringLiteral(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // DBF (CHARSET stringLiteral)?
  private boolean exportActionPropertyDefinitionBody_3_0_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exportActionPropertyDefinitionBody_3_0_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, DBF);
    r = r && exportActionPropertyDefinitionBody_3_0_0_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (CHARSET stringLiteral)?
  private boolean exportActionPropertyDefinitionBody_3_0_0_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exportActionPropertyDefinitionBody_3_0_0_1_1")) return false;
    exportActionPropertyDefinitionBody_3_0_0_1_1_0(b, l + 1);
    return true;
  }

  // CHARSET stringLiteral
  private boolean exportActionPropertyDefinitionBody_3_0_0_1_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exportActionPropertyDefinitionBody_3_0_0_1_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, CHARSET);
    r = r && stringLiteral(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // XLS sheetExpression? hasHeaderOption?
  private boolean exportActionPropertyDefinitionBody_3_0_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exportActionPropertyDefinitionBody_3_0_0_2")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, XLS);
    r = r && exportActionPropertyDefinitionBody_3_0_0_2_1(b, l + 1);
    r = r && exportActionPropertyDefinitionBody_3_0_0_2_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // sheetExpression?
  private boolean exportActionPropertyDefinitionBody_3_0_0_2_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exportActionPropertyDefinitionBody_3_0_0_2_1")) return false;
    sheetExpression(b, l + 1);
    return true;
  }

  // hasHeaderOption?
  private boolean exportActionPropertyDefinitionBody_3_0_0_2_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exportActionPropertyDefinitionBody_3_0_0_2_2")) return false;
    hasHeaderOption(b, l + 1);
    return true;
  }

  // XLSX sheetExpression? hasHeaderOption?
  private boolean exportActionPropertyDefinitionBody_3_0_0_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exportActionPropertyDefinitionBody_3_0_0_3")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, XLSX);
    r = r && exportActionPropertyDefinitionBody_3_0_0_3_1(b, l + 1);
    r = r && exportActionPropertyDefinitionBody_3_0_0_3_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // sheetExpression?
  private boolean exportActionPropertyDefinitionBody_3_0_0_3_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exportActionPropertyDefinitionBody_3_0_0_3_1")) return false;
    sheetExpression(b, l + 1);
    return true;
  }

  // hasHeaderOption?
  private boolean exportActionPropertyDefinitionBody_3_0_0_3_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exportActionPropertyDefinitionBody_3_0_0_3_2")) return false;
    hasHeaderOption(b, l + 1);
    return true;
  }

  // selectTops?
  private boolean exportActionPropertyDefinitionBody_3_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exportActionPropertyDefinitionBody_3_0_1")) return false;
    selectTops(b, l + 1);
    return true;
  }

  // (   JSON (CHARSET stringLiteral)?
  //                 |   XML hasHeaderOption? (CHARSET stringLiteral)?
  //                 )?
  //                 selectTops?
  //                 staticDestination?
  private boolean exportActionPropertyDefinitionBody_3_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exportActionPropertyDefinitionBody_3_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = exportActionPropertyDefinitionBody_3_1_0(b, l + 1);
    r = r && exportActionPropertyDefinitionBody_3_1_1(b, l + 1);
    r = r && exportActionPropertyDefinitionBody_3_1_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (   JSON (CHARSET stringLiteral)?
  //                 |   XML hasHeaderOption? (CHARSET stringLiteral)?
  //                 )?
  private boolean exportActionPropertyDefinitionBody_3_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exportActionPropertyDefinitionBody_3_1_0")) return false;
    exportActionPropertyDefinitionBody_3_1_0_0(b, l + 1);
    return true;
  }

  // JSON (CHARSET stringLiteral)?
  //                 |   XML hasHeaderOption? (CHARSET stringLiteral)?
  private boolean exportActionPropertyDefinitionBody_3_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exportActionPropertyDefinitionBody_3_1_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = exportActionPropertyDefinitionBody_3_1_0_0_0(b, l + 1);
    if (!r) r = exportActionPropertyDefinitionBody_3_1_0_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // JSON (CHARSET stringLiteral)?
  private boolean exportActionPropertyDefinitionBody_3_1_0_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exportActionPropertyDefinitionBody_3_1_0_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, JSON);
    r = r && exportActionPropertyDefinitionBody_3_1_0_0_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (CHARSET stringLiteral)?
  private boolean exportActionPropertyDefinitionBody_3_1_0_0_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exportActionPropertyDefinitionBody_3_1_0_0_0_1")) return false;
    exportActionPropertyDefinitionBody_3_1_0_0_0_1_0(b, l + 1);
    return true;
  }

  // CHARSET stringLiteral
  private boolean exportActionPropertyDefinitionBody_3_1_0_0_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exportActionPropertyDefinitionBody_3_1_0_0_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, CHARSET);
    r = r && stringLiteral(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // XML hasHeaderOption? (CHARSET stringLiteral)?
  private boolean exportActionPropertyDefinitionBody_3_1_0_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exportActionPropertyDefinitionBody_3_1_0_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, XML);
    r = r && exportActionPropertyDefinitionBody_3_1_0_0_1_1(b, l + 1);
    r = r && exportActionPropertyDefinitionBody_3_1_0_0_1_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // hasHeaderOption?
  private boolean exportActionPropertyDefinitionBody_3_1_0_0_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exportActionPropertyDefinitionBody_3_1_0_0_1_1")) return false;
    hasHeaderOption(b, l + 1);
    return true;
  }

  // (CHARSET stringLiteral)?
  private boolean exportActionPropertyDefinitionBody_3_1_0_0_1_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exportActionPropertyDefinitionBody_3_1_0_0_1_2")) return false;
    exportActionPropertyDefinitionBody_3_1_0_0_1_2_0(b, l + 1);
    return true;
  }

  // CHARSET stringLiteral
  private boolean exportActionPropertyDefinitionBody_3_1_0_0_1_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exportActionPropertyDefinitionBody_3_1_0_0_1_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, CHARSET);
    r = r && stringLiteral(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // selectTops?
  private boolean exportActionPropertyDefinitionBody_3_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exportActionPropertyDefinitionBody_3_1_1")) return false;
    selectTops(b, l + 1);
    return true;
  }

  // staticDestination?
  private boolean exportActionPropertyDefinitionBody_3_1_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exportActionPropertyDefinitionBody_3_1_2")) return false;
    staticDestination(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // EXPORT
  //         (   CSV (stringLiteral)? hasHeaderOption? noEscapeOption? (CHARSET stringLiteral)?
  //         |   DBF (CHARSET stringLiteral)?
  //         |   XLS sheetExpression? hasHeaderOption?
  //         |   XLSX sheetExpression? hasHeaderOption?
  //         |   JSON (CHARSET stringLiteral)?
  //         |   XML hasHeaderOption? (ROOT propertyExpression)? (TAG propertyExpression)? ATTR? (CHARSET stringLiteral)?
  //         |   TABLE
  //         )?
  //         selectTop?
  //         FROM nonEmptyAliasedPropertyExpressionList
  //         wherePropertyExpression?
  //         (ORDER propertyExpressionWithOrder (COMMA propertyExpressionWithOrder)*)?
  //         staticDestination?
  public boolean exportDataActionPropertyDefinitionBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exportDataActionPropertyDefinitionBody")) return false;
    if (!nextTokenIs(b, EXPORT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, EXPORT_DATA_ACTION_PROPERTY_DEFINITION_BODY, null);
    r = consumeToken(b, EXPORT);
    p = r; // pin = 1
    r = r && exportDataActionPropertyDefinitionBody_1(b, l + 1);
    r = r && exportDataActionPropertyDefinitionBody_2(b, l + 1);
    r = r && consumeToken(b, FROM);
    r = r && nonEmptyAliasedPropertyExpressionList(b, l + 1);
    r = r && exportDataActionPropertyDefinitionBody_5(b, l + 1);
    r = r && exportDataActionPropertyDefinitionBody_6(b, l + 1);
    r = r && exportDataActionPropertyDefinitionBody_7(b, l + 1);
    exit_section_(b, l, m, EXPORT_DATA_ACTION_PROPERTY_DEFINITION_BODY, r, p, null);
    return r || p;
  }

  // (   CSV (stringLiteral)? hasHeaderOption? noEscapeOption? (CHARSET stringLiteral)?
  //         |   DBF (CHARSET stringLiteral)?
  //         |   XLS sheetExpression? hasHeaderOption?
  //         |   XLSX sheetExpression? hasHeaderOption?
  //         |   JSON (CHARSET stringLiteral)?
  //         |   XML hasHeaderOption? (ROOT propertyExpression)? (TAG propertyExpression)? ATTR? (CHARSET stringLiteral)?
  //         |   TABLE
  //         )?
  private boolean exportDataActionPropertyDefinitionBody_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exportDataActionPropertyDefinitionBody_1")) return false;
    exportDataActionPropertyDefinitionBody_1_0(b, l + 1);
    return true;
  }

  // CSV (stringLiteral)? hasHeaderOption? noEscapeOption? (CHARSET stringLiteral)?
  //         |   DBF (CHARSET stringLiteral)?
  //         |   XLS sheetExpression? hasHeaderOption?
  //         |   XLSX sheetExpression? hasHeaderOption?
  //         |   JSON (CHARSET stringLiteral)?
  //         |   XML hasHeaderOption? (ROOT propertyExpression)? (TAG propertyExpression)? ATTR? (CHARSET stringLiteral)?
  //         |   TABLE
  private boolean exportDataActionPropertyDefinitionBody_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exportDataActionPropertyDefinitionBody_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = exportDataActionPropertyDefinitionBody_1_0_0(b, l + 1);
    if (!r) r = exportDataActionPropertyDefinitionBody_1_0_1(b, l + 1);
    if (!r) r = exportDataActionPropertyDefinitionBody_1_0_2(b, l + 1);
    if (!r) r = exportDataActionPropertyDefinitionBody_1_0_3(b, l + 1);
    if (!r) r = exportDataActionPropertyDefinitionBody_1_0_4(b, l + 1);
    if (!r) r = exportDataActionPropertyDefinitionBody_1_0_5(b, l + 1);
    if (!r) r = consumeToken(b, TABLE);
    exit_section_(b, m, null, r);
    return r;
  }

  // CSV (stringLiteral)? hasHeaderOption? noEscapeOption? (CHARSET stringLiteral)?
  private boolean exportDataActionPropertyDefinitionBody_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exportDataActionPropertyDefinitionBody_1_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, CSV);
    r = r && exportDataActionPropertyDefinitionBody_1_0_0_1(b, l + 1);
    r = r && exportDataActionPropertyDefinitionBody_1_0_0_2(b, l + 1);
    r = r && exportDataActionPropertyDefinitionBody_1_0_0_3(b, l + 1);
    r = r && exportDataActionPropertyDefinitionBody_1_0_0_4(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (stringLiteral)?
  private boolean exportDataActionPropertyDefinitionBody_1_0_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exportDataActionPropertyDefinitionBody_1_0_0_1")) return false;
    exportDataActionPropertyDefinitionBody_1_0_0_1_0(b, l + 1);
    return true;
  }

  // (stringLiteral)
  private boolean exportDataActionPropertyDefinitionBody_1_0_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exportDataActionPropertyDefinitionBody_1_0_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = stringLiteral(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // hasHeaderOption?
  private boolean exportDataActionPropertyDefinitionBody_1_0_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exportDataActionPropertyDefinitionBody_1_0_0_2")) return false;
    hasHeaderOption(b, l + 1);
    return true;
  }

  // noEscapeOption?
  private boolean exportDataActionPropertyDefinitionBody_1_0_0_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exportDataActionPropertyDefinitionBody_1_0_0_3")) return false;
    noEscapeOption(b, l + 1);
    return true;
  }

  // (CHARSET stringLiteral)?
  private boolean exportDataActionPropertyDefinitionBody_1_0_0_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exportDataActionPropertyDefinitionBody_1_0_0_4")) return false;
    exportDataActionPropertyDefinitionBody_1_0_0_4_0(b, l + 1);
    return true;
  }

  // CHARSET stringLiteral
  private boolean exportDataActionPropertyDefinitionBody_1_0_0_4_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exportDataActionPropertyDefinitionBody_1_0_0_4_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, CHARSET);
    r = r && stringLiteral(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // DBF (CHARSET stringLiteral)?
  private boolean exportDataActionPropertyDefinitionBody_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exportDataActionPropertyDefinitionBody_1_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, DBF);
    r = r && exportDataActionPropertyDefinitionBody_1_0_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (CHARSET stringLiteral)?
  private boolean exportDataActionPropertyDefinitionBody_1_0_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exportDataActionPropertyDefinitionBody_1_0_1_1")) return false;
    exportDataActionPropertyDefinitionBody_1_0_1_1_0(b, l + 1);
    return true;
  }

  // CHARSET stringLiteral
  private boolean exportDataActionPropertyDefinitionBody_1_0_1_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exportDataActionPropertyDefinitionBody_1_0_1_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, CHARSET);
    r = r && stringLiteral(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // XLS sheetExpression? hasHeaderOption?
  private boolean exportDataActionPropertyDefinitionBody_1_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exportDataActionPropertyDefinitionBody_1_0_2")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, XLS);
    r = r && exportDataActionPropertyDefinitionBody_1_0_2_1(b, l + 1);
    r = r && exportDataActionPropertyDefinitionBody_1_0_2_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // sheetExpression?
  private boolean exportDataActionPropertyDefinitionBody_1_0_2_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exportDataActionPropertyDefinitionBody_1_0_2_1")) return false;
    sheetExpression(b, l + 1);
    return true;
  }

  // hasHeaderOption?
  private boolean exportDataActionPropertyDefinitionBody_1_0_2_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exportDataActionPropertyDefinitionBody_1_0_2_2")) return false;
    hasHeaderOption(b, l + 1);
    return true;
  }

  // XLSX sheetExpression? hasHeaderOption?
  private boolean exportDataActionPropertyDefinitionBody_1_0_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exportDataActionPropertyDefinitionBody_1_0_3")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, XLSX);
    r = r && exportDataActionPropertyDefinitionBody_1_0_3_1(b, l + 1);
    r = r && exportDataActionPropertyDefinitionBody_1_0_3_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // sheetExpression?
  private boolean exportDataActionPropertyDefinitionBody_1_0_3_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exportDataActionPropertyDefinitionBody_1_0_3_1")) return false;
    sheetExpression(b, l + 1);
    return true;
  }

  // hasHeaderOption?
  private boolean exportDataActionPropertyDefinitionBody_1_0_3_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exportDataActionPropertyDefinitionBody_1_0_3_2")) return false;
    hasHeaderOption(b, l + 1);
    return true;
  }

  // JSON (CHARSET stringLiteral)?
  private boolean exportDataActionPropertyDefinitionBody_1_0_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exportDataActionPropertyDefinitionBody_1_0_4")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, JSON);
    r = r && exportDataActionPropertyDefinitionBody_1_0_4_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (CHARSET stringLiteral)?
  private boolean exportDataActionPropertyDefinitionBody_1_0_4_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exportDataActionPropertyDefinitionBody_1_0_4_1")) return false;
    exportDataActionPropertyDefinitionBody_1_0_4_1_0(b, l + 1);
    return true;
  }

  // CHARSET stringLiteral
  private boolean exportDataActionPropertyDefinitionBody_1_0_4_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exportDataActionPropertyDefinitionBody_1_0_4_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, CHARSET);
    r = r && stringLiteral(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // XML hasHeaderOption? (ROOT propertyExpression)? (TAG propertyExpression)? ATTR? (CHARSET stringLiteral)?
  private boolean exportDataActionPropertyDefinitionBody_1_0_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exportDataActionPropertyDefinitionBody_1_0_5")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, XML);
    r = r && exportDataActionPropertyDefinitionBody_1_0_5_1(b, l + 1);
    r = r && exportDataActionPropertyDefinitionBody_1_0_5_2(b, l + 1);
    r = r && exportDataActionPropertyDefinitionBody_1_0_5_3(b, l + 1);
    r = r && exportDataActionPropertyDefinitionBody_1_0_5_4(b, l + 1);
    r = r && exportDataActionPropertyDefinitionBody_1_0_5_5(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // hasHeaderOption?
  private boolean exportDataActionPropertyDefinitionBody_1_0_5_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exportDataActionPropertyDefinitionBody_1_0_5_1")) return false;
    hasHeaderOption(b, l + 1);
    return true;
  }

  // (ROOT propertyExpression)?
  private boolean exportDataActionPropertyDefinitionBody_1_0_5_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exportDataActionPropertyDefinitionBody_1_0_5_2")) return false;
    exportDataActionPropertyDefinitionBody_1_0_5_2_0(b, l + 1);
    return true;
  }

  // ROOT propertyExpression
  private boolean exportDataActionPropertyDefinitionBody_1_0_5_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exportDataActionPropertyDefinitionBody_1_0_5_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, ROOT);
    r = r && propertyExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (TAG propertyExpression)?
  private boolean exportDataActionPropertyDefinitionBody_1_0_5_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exportDataActionPropertyDefinitionBody_1_0_5_3")) return false;
    exportDataActionPropertyDefinitionBody_1_0_5_3_0(b, l + 1);
    return true;
  }

  // TAG propertyExpression
  private boolean exportDataActionPropertyDefinitionBody_1_0_5_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exportDataActionPropertyDefinitionBody_1_0_5_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, TAG);
    r = r && propertyExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ATTR?
  private boolean exportDataActionPropertyDefinitionBody_1_0_5_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exportDataActionPropertyDefinitionBody_1_0_5_4")) return false;
    consumeToken(b, ATTR);
    return true;
  }

  // (CHARSET stringLiteral)?
  private boolean exportDataActionPropertyDefinitionBody_1_0_5_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exportDataActionPropertyDefinitionBody_1_0_5_5")) return false;
    exportDataActionPropertyDefinitionBody_1_0_5_5_0(b, l + 1);
    return true;
  }

  // CHARSET stringLiteral
  private boolean exportDataActionPropertyDefinitionBody_1_0_5_5_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exportDataActionPropertyDefinitionBody_1_0_5_5_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, CHARSET);
    r = r && stringLiteral(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // selectTop?
  private boolean exportDataActionPropertyDefinitionBody_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exportDataActionPropertyDefinitionBody_2")) return false;
    selectTop(b, l + 1);
    return true;
  }

  // wherePropertyExpression?
  private boolean exportDataActionPropertyDefinitionBody_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exportDataActionPropertyDefinitionBody_5")) return false;
    wherePropertyExpression(b, l + 1);
    return true;
  }

  // (ORDER propertyExpressionWithOrder (COMMA propertyExpressionWithOrder)*)?
  private boolean exportDataActionPropertyDefinitionBody_6(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exportDataActionPropertyDefinitionBody_6")) return false;
    exportDataActionPropertyDefinitionBody_6_0(b, l + 1);
    return true;
  }

  // ORDER propertyExpressionWithOrder (COMMA propertyExpressionWithOrder)*
  private boolean exportDataActionPropertyDefinitionBody_6_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exportDataActionPropertyDefinitionBody_6_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, ORDER);
    r = r && propertyExpressionWithOrder(b, l + 1);
    r = r && exportDataActionPropertyDefinitionBody_6_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (COMMA propertyExpressionWithOrder)*
  private boolean exportDataActionPropertyDefinitionBody_6_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exportDataActionPropertyDefinitionBody_6_0_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!exportDataActionPropertyDefinitionBody_6_0_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "exportDataActionPropertyDefinitionBody_6_0_2", c)) break;
    }
    return true;
  }

  // COMMA propertyExpressionWithOrder
  private boolean exportDataActionPropertyDefinitionBody_6_0_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exportDataActionPropertyDefinitionBody_6_0_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, COMMA);
    r = r && propertyExpressionWithOrder(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // staticDestination?
  private boolean exportDataActionPropertyDefinitionBody_7(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exportDataActionPropertyDefinitionBody_7")) return false;
    staticDestination(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // classParamDeclare
  public boolean exprParameterNameUsage(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exprParameterNameUsage")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, EXPR_PARAMETER_NAME_USAGE, "<expr parameter name usage>");
    r = classParamDeclare(b, l + 1);
    exit_section_(b, l, m, EXPR_PARAMETER_NAME_USAGE, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // (DOLLAR)? exprParameterNameUsage
  public boolean exprParameterUsage(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exprParameterUsage")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, EXPR_PARAMETER_USAGE, "<expr parameter usage>");
    r = exprParameterUsage_0(b, l + 1);
    r = r && exprParameterNameUsage(b, l + 1);
    exit_section_(b, l, m, EXPR_PARAMETER_USAGE, r, false, null);
    return r;
  }

  // (DOLLAR)?
  private boolean exprParameterUsage_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exprParameterUsage_0")) return false;
    consumeToken(b, DOLLAR);
    return true;
  }

  /* ********************************************************** */
  // (exprParameterUsage (COMMA exprParameterUsage)*)?
  public boolean exprParameterUsageList(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exprParameterUsageList")) return false;
    Marker m = enter_section_(b, l, _NONE_, EXPR_PARAMETER_USAGE_LIST, "<expr parameter usage list>");
    exprParameterUsageList_0(b, l + 1);
    exit_section_(b, l, m, EXPR_PARAMETER_USAGE_LIST, true, false, null);
    return true;
  }

  // exprParameterUsage (COMMA exprParameterUsage)*
  private boolean exprParameterUsageList_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exprParameterUsageList_0")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = exprParameterUsage(b, l + 1);
    p = r; // pin = 1
    r = r && exprParameterUsageList_0_1(b, l + 1);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  // (COMMA exprParameterUsage)*
  private boolean exprParameterUsageList_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exprParameterUsageList_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!exprParameterUsageList_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "exprParameterUsageList_0_1", c)) break;
    }
    return true;
  }

  // COMMA exprParameterUsage
  private boolean exprParameterUsageList_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "exprParameterUsageList_0_1_0")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, COMMA);
    p = r; // pin = 1
    r = r && exprParameterUsage(b, l + 1);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // joinPropertyDefinition
  // 	    |	multiPropertyDefinition
  // 	    |	overridePropertyDefinition
  // 	    |	ifElsePropertyDefinition
  // 	    |	maxPropertyDefinition
  // 	    |	casePropertyDefinition
  // 	    |	partitionPropertyDefinition
  // 	    |	groupExprPropertyDefinition
  // 	    |	recursivePropertyDefinition
  // 	    |	structCreationPropertyDefinition
  // 	    |	concatPropertyDefinition
  // 	    |	jsonPropertyDefinition
  // 	    |	jsonFormPropertyDefinition
  // 	    |	castPropertyDefinition
  // 	    |	sessionPropertyDefinition
  // 	    |	signaturePropertyDefinition
  // 	    |   activeTabPropertyDefinition
  // 	    |   roundPropertyDefinition
  // 	    |	expressionLiteral
  public boolean expressionFriendlyPD(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expressionFriendlyPD")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, EXPRESSION_FRIENDLY_PD, "<expression friendly pd>");
    r = joinPropertyDefinition(b, l + 1);
    if (!r) r = multiPropertyDefinition(b, l + 1);
    if (!r) r = overridePropertyDefinition(b, l + 1);
    if (!r) r = ifElsePropertyDefinition(b, l + 1);
    if (!r) r = maxPropertyDefinition(b, l + 1);
    if (!r) r = casePropertyDefinition(b, l + 1);
    if (!r) r = partitionPropertyDefinition(b, l + 1);
    if (!r) r = groupExprPropertyDefinition(b, l + 1);
    if (!r) r = recursivePropertyDefinition(b, l + 1);
    if (!r) r = structCreationPropertyDefinition(b, l + 1);
    if (!r) r = concatPropertyDefinition(b, l + 1);
    if (!r) r = jsonPropertyDefinition(b, l + 1);
    if (!r) r = jsonFormPropertyDefinition(b, l + 1);
    if (!r) r = castPropertyDefinition(b, l + 1);
    if (!r) r = sessionPropertyDefinition(b, l + 1);
    if (!r) r = signaturePropertyDefinition(b, l + 1);
    if (!r) r = activeTabPropertyDefinition(b, l + 1);
    if (!r) r = roundPropertyDefinition(b, l + 1);
    if (!r) r = expressionLiteral(b, l + 1);
    exit_section_(b, l, m, EXPRESSION_FRIENDLY_PD, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // commonLiteral | (<<noIDCheck>> expressionStringLiteral)
  public boolean expressionLiteral(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expressionLiteral")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, EXPRESSION_LITERAL, "<expression literal>");
    r = commonLiteral(b, l + 1);
    if (!r) r = expressionLiteral_1(b, l + 1);
    exit_section_(b, l, m, EXPRESSION_LITERAL, r, false, null);
    return r;
  }

  // <<noIDCheck>> expressionStringLiteral
  private boolean expressionLiteral_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expressionLiteral_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = noIDCheck(b, l + 1);
    r = r && expressionStringLiteral(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // <<fullCompoundParamDeclareCheck>> expressionFriendlyPD | exprParameterUsage
  public boolean expressionPrimitive(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expressionPrimitive")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, EXPRESSION_PRIMITIVE, "<expression primitive>");
    r = expressionPrimitive_0(b, l + 1);
    if (!r) r = exprParameterUsage(b, l + 1);
    exit_section_(b, l, m, EXPRESSION_PRIMITIVE, r, false, null);
    return r;
  }

  // <<fullCompoundParamDeclareCheck>> expressionFriendlyPD
  private boolean expressionPrimitive_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expressionPrimitive_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = fullCompoundParamDeclareCheck(b, l + 1);
    r = r && expressionFriendlyPD(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // LEX_STRING_LITERAL | ID
  public boolean expressionStringLiteral(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expressionStringLiteral")) return false;
    if (!nextTokenIs(b, "<expression string literal>", ID, LEX_STRING_LITERAL)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, EXPRESSION_STRING_LITERAL, "<expression string literal>");
    r = consumeToken(b, LEX_STRING_LITERAL);
    if (!r) r = consumeToken(b, ID);
    exit_section_(b, l, m, EXPRESSION_STRING_LITERAL, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // dataPropertyDefinition
  //                            |   nativePropertyDefinition
  //                            |   abstractPropertyDefinition
  //                            |   formulaPropertyDefinition
  //                            |   groupPropertyDefinition
  //                            |   aggrPropertyDefinition
  //                            |   filterPropertyDefinition
  //                            |   reflectionPropertyDefinition
  public boolean expressionUnfriendlyPD(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expressionUnfriendlyPD")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, EXPRESSION_UNFRIENDLY_PD, "<expression unfriendly pd>");
    r = dataPropertyDefinition(b, l + 1);
    if (!r) r = nativePropertyDefinition(b, l + 1);
    if (!r) r = abstractPropertyDefinition(b, l + 1);
    if (!r) r = formulaPropertyDefinition(b, l + 1);
    if (!r) r = groupPropertyDefinition(b, l + 1);
    if (!r) r = aggrPropertyDefinition(b, l + 1);
    if (!r) r = filterPropertyDefinition(b, l + 1);
    if (!r) r = reflectionPropertyDefinition(b, l + 1);
    exit_section_(b, l, m, EXPRESSION_UNFRIENDLY_PD, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // EXTEND CLASS customClassUsageWrapper
  public boolean extendingClassDeclaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "extendingClassDeclaration")) return false;
    if (!nextTokenIs(b, EXTEND)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, EXTENDING_CLASS_DECLARATION, null);
    r = consumeTokens(b, 2, EXTEND, CLASS);
    p = r; // pin = 2
    r = r && customClassUsageWrapper(b, l + 1);
    exit_section_(b, l, m, EXTENDING_CLASS_DECLARATION, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // EXTEND FORM formUsageWrapper
  public boolean extendingFormDeclaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "extendingFormDeclaration")) return false;
    if (!nextTokenIs(b, EXTEND)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, EXTENDING_FORM_DECLARATION, null);
    r = consumeTokens(b, 2, EXTEND, FORM);
    p = r; // pin = 2
    r = r && formUsageWrapper(b, l + 1);
    exit_section_(b, l, m, EXTENDING_FORM_DECLARATION, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // EXTERNAL externalType (PARAMS nonEmptyPropertyExpressionList)? (TO nonEmptyNoParamsPropertyUsageList)?
  public boolean externalActionPropertyDefinitionBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "externalActionPropertyDefinitionBody")) return false;
    if (!nextTokenIs(b, EXTERNAL)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, EXTERNAL_ACTION_PROPERTY_DEFINITION_BODY, null);
    r = consumeToken(b, EXTERNAL);
    p = r; // pin = 1
    r = r && externalType(b, l + 1);
    r = r && externalActionPropertyDefinitionBody_2(b, l + 1);
    r = r && externalActionPropertyDefinitionBody_3(b, l + 1);
    exit_section_(b, l, m, EXTERNAL_ACTION_PROPERTY_DEFINITION_BODY, r, p, null);
    return r || p;
  }

  // (PARAMS nonEmptyPropertyExpressionList)?
  private boolean externalActionPropertyDefinitionBody_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "externalActionPropertyDefinitionBody_2")) return false;
    externalActionPropertyDefinitionBody_2_0(b, l + 1);
    return true;
  }

  // PARAMS nonEmptyPropertyExpressionList
  private boolean externalActionPropertyDefinitionBody_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "externalActionPropertyDefinitionBody_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, PARAMS);
    r = r && nonEmptyPropertyExpressionList(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (TO nonEmptyNoParamsPropertyUsageList)?
  private boolean externalActionPropertyDefinitionBody_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "externalActionPropertyDefinitionBody_3")) return false;
    externalActionPropertyDefinitionBody_3_0(b, l + 1);
    return true;
  }

  // TO nonEmptyNoParamsPropertyUsageList
  private boolean externalActionPropertyDefinitionBody_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "externalActionPropertyDefinitionBody_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, TO);
    r = r && nonEmptyNoParamsPropertyUsageList(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // SQL propertyExpression EXEC propertyExpression
  //               |   TCP (CLIENT)? propertyExpression
  //               |   UDP (CLIENT)? propertyExpression
  //               |   HTTP (CLIENT)? (DELETE| GET | PATCH | POST | PUT)? propertyExpression (BODYURL propertyExpression)?
  //                     (BODYPARAMNAMES propertyExpression (COMMA propertyExpression)*)?
  //                     (BODYPARAMHEADERS headersPropertyUsage (COMMA headersPropertyUsage)*)?
  //                     (HEADERS headersPropertyUsage)? (COOKIES headersPropertyUsage)? (HEADERSTO headersPropertyUsage)? (COOKIESTO headersPropertyUsage)?
  //               |   DBF propertyExpression APPEND (CHARSET stringLiteral)?
  //               |   LSF propertyExpression (EXEC | (EVAL (ACTION)?)) propertyExpression
  //               |   JAVA propertyExpression
  boolean externalType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "externalType")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = externalType_0(b, l + 1);
    if (!r) r = externalType_1(b, l + 1);
    if (!r) r = externalType_2(b, l + 1);
    if (!r) r = externalType_3(b, l + 1);
    if (!r) r = externalType_4(b, l + 1);
    if (!r) r = externalType_5(b, l + 1);
    if (!r) r = externalType_6(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // SQL propertyExpression EXEC propertyExpression
  private boolean externalType_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "externalType_0")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, SQL);
    p = r; // pin = 1
    r = r && propertyExpression(b, l + 1);
    r = r && consumeToken(b, EXEC);
    r = r && propertyExpression(b, l + 1);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  // TCP (CLIENT)? propertyExpression
  private boolean externalType_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "externalType_1")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, TCP);
    p = r; // pin = 1
    r = r && externalType_1_1(b, l + 1);
    r = r && propertyExpression(b, l + 1);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  // (CLIENT)?
  private boolean externalType_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "externalType_1_1")) return false;
    consumeToken(b, CLIENT);
    return true;
  }

  // UDP (CLIENT)? propertyExpression
  private boolean externalType_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "externalType_2")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, UDP);
    p = r; // pin = 1
    r = r && externalType_2_1(b, l + 1);
    r = r && propertyExpression(b, l + 1);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  // (CLIENT)?
  private boolean externalType_2_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "externalType_2_1")) return false;
    consumeToken(b, CLIENT);
    return true;
  }

  // HTTP (CLIENT)? (DELETE| GET | PATCH | POST | PUT)? propertyExpression (BODYURL propertyExpression)?
  //                     (BODYPARAMNAMES propertyExpression (COMMA propertyExpression)*)?
  //                     (BODYPARAMHEADERS headersPropertyUsage (COMMA headersPropertyUsage)*)?
  //                     (HEADERS headersPropertyUsage)? (COOKIES headersPropertyUsage)? (HEADERSTO headersPropertyUsage)? (COOKIESTO headersPropertyUsage)?
  private boolean externalType_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "externalType_3")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, HTTP);
    p = r; // pin = 1
    r = r && externalType_3_1(b, l + 1);
    r = r && externalType_3_2(b, l + 1);
    r = r && propertyExpression(b, l + 1);
    r = r && externalType_3_4(b, l + 1);
    r = r && externalType_3_5(b, l + 1);
    r = r && externalType_3_6(b, l + 1);
    r = r && externalType_3_7(b, l + 1);
    r = r && externalType_3_8(b, l + 1);
    r = r && externalType_3_9(b, l + 1);
    r = r && externalType_3_10(b, l + 1);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  // (CLIENT)?
  private boolean externalType_3_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "externalType_3_1")) return false;
    consumeToken(b, CLIENT);
    return true;
  }

  // (DELETE| GET | PATCH | POST | PUT)?
  private boolean externalType_3_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "externalType_3_2")) return false;
    externalType_3_2_0(b, l + 1);
    return true;
  }

  // DELETE| GET | PATCH | POST | PUT
  private boolean externalType_3_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "externalType_3_2_0")) return false;
    boolean r;
    r = consumeToken(b, DELETE);
    if (!r) r = consumeToken(b, GET);
    if (!r) r = consumeToken(b, PATCH);
    if (!r) r = consumeToken(b, POST);
    if (!r) r = consumeToken(b, PUT);
    return r;
  }

  // (BODYURL propertyExpression)?
  private boolean externalType_3_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "externalType_3_4")) return false;
    externalType_3_4_0(b, l + 1);
    return true;
  }

  // BODYURL propertyExpression
  private boolean externalType_3_4_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "externalType_3_4_0")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, BODYURL);
    p = r; // pin = 1
    r = r && propertyExpression(b, l + 1);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  // (BODYPARAMNAMES propertyExpression (COMMA propertyExpression)*)?
  private boolean externalType_3_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "externalType_3_5")) return false;
    externalType_3_5_0(b, l + 1);
    return true;
  }

  // BODYPARAMNAMES propertyExpression (COMMA propertyExpression)*
  private boolean externalType_3_5_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "externalType_3_5_0")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, BODYPARAMNAMES);
    p = r; // pin = 1
    r = r && propertyExpression(b, l + 1);
    r = r && externalType_3_5_0_2(b, l + 1);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  // (COMMA propertyExpression)*
  private boolean externalType_3_5_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "externalType_3_5_0_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!externalType_3_5_0_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "externalType_3_5_0_2", c)) break;
    }
    return true;
  }

  // COMMA propertyExpression
  private boolean externalType_3_5_0_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "externalType_3_5_0_2_0")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, COMMA);
    p = r; // pin = 1
    r = r && propertyExpression(b, l + 1);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  // (BODYPARAMHEADERS headersPropertyUsage (COMMA headersPropertyUsage)*)?
  private boolean externalType_3_6(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "externalType_3_6")) return false;
    externalType_3_6_0(b, l + 1);
    return true;
  }

  // BODYPARAMHEADERS headersPropertyUsage (COMMA headersPropertyUsage)*
  private boolean externalType_3_6_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "externalType_3_6_0")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, BODYPARAMHEADERS);
    p = r; // pin = 1
    r = r && headersPropertyUsage(b, l + 1);
    r = r && externalType_3_6_0_2(b, l + 1);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  // (COMMA headersPropertyUsage)*
  private boolean externalType_3_6_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "externalType_3_6_0_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!externalType_3_6_0_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "externalType_3_6_0_2", c)) break;
    }
    return true;
  }

  // COMMA headersPropertyUsage
  private boolean externalType_3_6_0_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "externalType_3_6_0_2_0")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, COMMA);
    p = r; // pin = 1
    r = r && headersPropertyUsage(b, l + 1);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  // (HEADERS headersPropertyUsage)?
  private boolean externalType_3_7(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "externalType_3_7")) return false;
    externalType_3_7_0(b, l + 1);
    return true;
  }

  // HEADERS headersPropertyUsage
  private boolean externalType_3_7_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "externalType_3_7_0")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, HEADERS);
    p = r; // pin = 1
    r = r && headersPropertyUsage(b, l + 1);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  // (COOKIES headersPropertyUsage)?
  private boolean externalType_3_8(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "externalType_3_8")) return false;
    externalType_3_8_0(b, l + 1);
    return true;
  }

  // COOKIES headersPropertyUsage
  private boolean externalType_3_8_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "externalType_3_8_0")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, COOKIES);
    p = r; // pin = 1
    r = r && headersPropertyUsage(b, l + 1);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  // (HEADERSTO headersPropertyUsage)?
  private boolean externalType_3_9(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "externalType_3_9")) return false;
    externalType_3_9_0(b, l + 1);
    return true;
  }

  // HEADERSTO headersPropertyUsage
  private boolean externalType_3_9_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "externalType_3_9_0")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, HEADERSTO);
    p = r; // pin = 1
    r = r && headersPropertyUsage(b, l + 1);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  // (COOKIESTO headersPropertyUsage)?
  private boolean externalType_3_10(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "externalType_3_10")) return false;
    externalType_3_10_0(b, l + 1);
    return true;
  }

  // COOKIESTO headersPropertyUsage
  private boolean externalType_3_10_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "externalType_3_10_0")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, COOKIESTO);
    p = r; // pin = 1
    r = r && headersPropertyUsage(b, l + 1);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  // DBF propertyExpression APPEND (CHARSET stringLiteral)?
  private boolean externalType_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "externalType_4")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, DBF);
    p = r; // pin = 1
    r = r && propertyExpression(b, l + 1);
    r = r && consumeToken(b, APPEND);
    r = r && externalType_4_3(b, l + 1);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  // (CHARSET stringLiteral)?
  private boolean externalType_4_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "externalType_4_3")) return false;
    externalType_4_3_0(b, l + 1);
    return true;
  }

  // CHARSET stringLiteral
  private boolean externalType_4_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "externalType_4_3_0")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, CHARSET);
    p = r; // pin = 1
    r = r && stringLiteral(b, l + 1);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  // LSF propertyExpression (EXEC | (EVAL (ACTION)?)) propertyExpression
  private boolean externalType_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "externalType_5")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, LSF);
    p = r; // pin = 1
    r = r && propertyExpression(b, l + 1);
    r = r && externalType_5_2(b, l + 1);
    r = r && propertyExpression(b, l + 1);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  // EXEC | (EVAL (ACTION)?)
  private boolean externalType_5_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "externalType_5_2")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, EXEC);
    if (!r) r = externalType_5_2_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // EVAL (ACTION)?
  private boolean externalType_5_2_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "externalType_5_2_1")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, EVAL);
    p = r; // pin = 1
    r = r && externalType_5_2_1_1(b, l + 1);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  // (ACTION)?
  private boolean externalType_5_2_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "externalType_5_2_1_1")) return false;
    consumeToken(b, ACTION);
    return true;
  }

  // JAVA propertyExpression
  private boolean externalType_6(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "externalType_6")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, JAVA);
    p = r; // pin = 1
    r = r && propertyExpression(b, l + 1);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // FILTER groupObjectID (FROM propertyUsage)?
  public boolean filterActionPropertyDefinitionBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "filterActionPropertyDefinitionBody")) return false;
    if (!nextTokenIs(b, FILTER)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, FILTER_ACTION_PROPERTY_DEFINITION_BODY, null);
    r = consumeToken(b, FILTER);
    r = r && groupObjectID(b, l + 1);
    p = r; // pin = 2
    r = r && filterActionPropertyDefinitionBody_2(b, l + 1);
    exit_section_(b, l, m, FILTER_ACTION_PROPERTY_DEFINITION_BODY, r, p, null);
    return r || p;
  }

  // (FROM propertyUsage)?
  private boolean filterActionPropertyDefinitionBody_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "filterActionPropertyDefinitionBody_2")) return false;
    filterActionPropertyDefinitionBody_2_0(b, l + 1);
    return true;
  }

  // FROM propertyUsage
  private boolean filterActionPropertyDefinitionBody_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "filterActionPropertyDefinitionBody_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, FROM);
    r = r && propertyUsage(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // simpleName
  public boolean filterGroupName(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "filterGroupName")) return false;
    if (!nextTokenIs(b, ID)) return false;
    boolean r;
    Marker m = enter_section_(b, l, FILTER_GROUP_NAME);
    r = simpleName(b, l + 1);
    exit_section_(b, m, FILTER_GROUP_NAME, r);
    return r;
  }

  /* ********************************************************** */
  // filterGroupUsage
  public boolean filterGroupSelector(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "filterGroupSelector")) return false;
    if (!nextTokenIs(b, ID)) return false;
    boolean r;
    Marker m = enter_section_(b, l, FILTER_GROUP_SELECTOR);
    r = filterGroupUsage(b, l + 1);
    exit_section_(b, m, FILTER_GROUP_SELECTOR, r);
    return r;
  }

  /* ********************************************************** */
  // simpleName
  public boolean filterGroupUsage(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "filterGroupUsage")) return false;
    if (!nextTokenIs(b, ID)) return false;
    boolean r;
    Marker m = enter_section_(b, l, FILTER_GROUP_USAGE);
    r = simpleName(b, l + 1);
    exit_section_(b, m, FILTER_GROUP_USAGE, r);
    return r;
  }

  /* ********************************************************** */
  // (FILTER | ORDER | VIEW) groupObjectID
  public boolean filterPropertyDefinition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "filterPropertyDefinition")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, FILTER_PROPERTY_DEFINITION, "<filter property definition>");
    r = filterPropertyDefinition_0(b, l + 1);
    p = r; // pin = 1
    r = r && groupObjectID(b, l + 1);
    exit_section_(b, l, m, FILTER_PROPERTY_DEFINITION, r, p, null);
    return r || p;
  }

  // FILTER | ORDER | VIEW
  private boolean filterPropertyDefinition_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "filterPropertyDefinition_0")) return false;
    boolean r;
    r = consumeToken(b, FILTER);
    if (!r) r = consumeToken(b, ORDER);
    if (!r) r = consumeToken(b, VIEW);
    return r;
  }

  /* ********************************************************** */
  // propertySelector
  public boolean filterPropertySelector(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "filterPropertySelector")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FILTER_PROPERTY_SELECTOR, "<filter property selector>");
    r = propertySelector(b, l + 1);
    exit_section_(b, l, m, FILTER_PROPERTY_SELECTOR, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // DEFAULT
  public boolean filterSetDefault(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "filterSetDefault")) return false;
    if (!nextTokenIs(b, DEFAULT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, FILTER_SET_DEFAULT);
    r = consumeToken(b, DEFAULT);
    exit_section_(b, m, FILTER_SET_DEFAULT, r);
    return r;
  }

  /* ********************************************************** */
  // START | CENTER | END | STRETCH
  public boolean flexAlignmentLiteral(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "flexAlignmentLiteral")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FLEX_ALIGNMENT_LITERAL, "<flex alignment literal>");
    r = consumeToken(b, START);
    if (!r) r = consumeToken(b, CENTER);
    if (!r) r = consumeToken(b, END);
    if (!r) r = consumeToken(b, STRETCH);
    exit_section_(b, l, m, FLEX_ALIGNMENT_LITERAL, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // CHARWIDTH intLiteral (FLEX | NOFLEX)
  public boolean flexCharWidthSetting(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "flexCharWidthSetting")) return false;
    if (!nextTokenIs(b, CHARWIDTH)) return false;
    boolean r;
    Marker m = enter_section_(b, l, FLEX_CHAR_WIDTH_SETTING);
    r = consumeToken(b, CHARWIDTH);
    r = r && intLiteral(b, l + 1);
    r = r && flexCharWidthSetting_2(b, l + 1);
    exit_section_(b, m, FLEX_CHAR_WIDTH_SETTING, r);
    return r;
  }

  // FLEX | NOFLEX
  private boolean flexCharWidthSetting_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "flexCharWidthSetting_2")) return false;
    boolean r;
    r = consumeToken(b, FLEX);
    if (!r) r = consumeToken(b, NOFLEX);
    return r;
  }

  /* ********************************************************** */
  // FOLDER simpleName localizedStringLiteral?
  boolean folderElementDescription(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "folderElementDescription")) return false;
    if (!nextTokenIs(b, FOLDER)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, FOLDER);
    r = r && simpleName(b, l + 1);
    p = r; // pin = 2
    r = r && folderElementDescription_2(b, l + 1);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  // localizedStringLiteral?
  private boolean folderElementDescription_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "folderElementDescription_2")) return false;
    localizedStringLiteral(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // mappedPropertyClassParamDeclare FOLLOWS baseEventPE
  //                      propertyExpression (RESOLVE LEFT? RIGHT?)?		             
  // 		             SEMI
  public boolean followsStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "followsStatement")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, FOLLOWS_STATEMENT, "<follows statement>");
    r = mappedPropertyClassParamDeclare(b, l + 1);
    r = r && consumeToken(b, FOLLOWS);
    p = r; // pin = 2
    r = r && baseEventPE(b, l + 1);
    r = r && propertyExpression(b, l + 1);
    r = r && followsStatement_4(b, l + 1);
    r = r && consumeToken(b, SEMI);
    exit_section_(b, l, m, FOLLOWS_STATEMENT, r, p, null);
    return r || p;
  }

  // (RESOLVE LEFT? RIGHT?)?
  private boolean followsStatement_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "followsStatement_4")) return false;
    followsStatement_4_0(b, l + 1);
    return true;
  }

  // RESOLVE LEFT? RIGHT?
  private boolean followsStatement_4_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "followsStatement_4_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, RESOLVE);
    r = r && followsStatement_4_0_1(b, l + 1);
    r = r && followsStatement_4_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // LEFT?
  private boolean followsStatement_4_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "followsStatement_4_0_1")) return false;
    consumeToken(b, LEFT);
    return true;
  }

  // RIGHT?
  private boolean followsStatement_4_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "followsStatement_4_0_2")) return false;
    consumeToken(b, RIGHT);
    return true;
  }

  /* ********************************************************** */
  // forActionPropertyMainBody
  // 		                            (ELSE actionPropertyDefinitionBody)?
  public boolean forActionPropertyDefinitionBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "forActionPropertyDefinitionBody")) return false;
    if (!nextTokenIs(b, FOR)) return false;
    boolean r;
    Marker m = enter_section_(b, l, FOR_ACTION_PROPERTY_DEFINITION_BODY);
    r = forActionPropertyMainBody(b, l + 1);
    r = r && forActionPropertyDefinitionBody_1(b, l + 1);
    exit_section_(b, m, FOR_ACTION_PROPERTY_DEFINITION_BODY, r);
    return r;
  }

  // (ELSE actionPropertyDefinitionBody)?
  private boolean forActionPropertyDefinitionBody_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "forActionPropertyDefinitionBody_1")) return false;
    forActionPropertyDefinitionBody_1_0(b, l + 1);
    return true;
  }

  // ELSE actionPropertyDefinitionBody
  private boolean forActionPropertyDefinitionBody_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "forActionPropertyDefinitionBody_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, ELSE);
    r = r && actionPropertyDefinitionBody(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // FOR propertyExpression (ORDER (DESC)? nonEmptyPropertyExpressionList)?
  //                               inlineOption
  //                               (forAddObjClause)?
  //                               DO actionPropertyDefinitionBody
  public boolean forActionPropertyMainBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "forActionPropertyMainBody")) return false;
    if (!nextTokenIs(b, FOR)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, FOR_ACTION_PROPERTY_MAIN_BODY, null);
    r = consumeToken(b, FOR);
    p = r; // pin = 1
    r = r && propertyExpression(b, l + 1);
    r = r && forActionPropertyMainBody_2(b, l + 1);
    r = r && inlineOption(b, l + 1);
    r = r && forActionPropertyMainBody_4(b, l + 1);
    r = r && consumeToken(b, DO);
    r = r && actionPropertyDefinitionBody(b, l + 1);
    exit_section_(b, l, m, FOR_ACTION_PROPERTY_MAIN_BODY, r, p, null);
    return r || p;
  }

  // (ORDER (DESC)? nonEmptyPropertyExpressionList)?
  private boolean forActionPropertyMainBody_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "forActionPropertyMainBody_2")) return false;
    forActionPropertyMainBody_2_0(b, l + 1);
    return true;
  }

  // ORDER (DESC)? nonEmptyPropertyExpressionList
  private boolean forActionPropertyMainBody_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "forActionPropertyMainBody_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, ORDER);
    r = r && forActionPropertyMainBody_2_0_1(b, l + 1);
    r = r && nonEmptyPropertyExpressionList(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (DESC)?
  private boolean forActionPropertyMainBody_2_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "forActionPropertyMainBody_2_0_1")) return false;
    consumeToken(b, DESC);
    return true;
  }

  // (forAddObjClause)?
  private boolean forActionPropertyMainBody_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "forActionPropertyMainBody_4")) return false;
    forActionPropertyMainBody_4_0(b, l + 1);
    return true;
  }

  // (forAddObjClause)
  private boolean forActionPropertyMainBody_4_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "forActionPropertyMainBody_4_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = forAddObjClause(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // NEW (paramDeclare EQUALS)? customClassUsage AUTOSET?
  public boolean forAddObjClause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "forAddObjClause")) return false;
    if (!nextTokenIs(b, NEW)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, FOR_ADD_OBJ_CLAUSE, null);
    r = consumeToken(b, NEW);
    p = r; // pin = 1
    r = r && forAddObjClause_1(b, l + 1);
    r = r && customClassUsage(b, l + 1);
    r = r && forAddObjClause_3(b, l + 1);
    exit_section_(b, l, m, FOR_ADD_OBJ_CLAUSE, r, p, null);
    return r || p;
  }

  // (paramDeclare EQUALS)?
  private boolean forAddObjClause_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "forAddObjClause_1")) return false;
    forAddObjClause_1_0(b, l + 1);
    return true;
  }

  // paramDeclare EQUALS
  private boolean forAddObjClause_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "forAddObjClause_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = paramDeclare(b, l + 1);
    r = r && consumeToken(b, EQUALS);
    exit_section_(b, m, null, r);
    return r;
  }

  // AUTOSET?
  private boolean forAddObjClause_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "forAddObjClause_3")) return false;
    consumeToken(b, AUTOSET);
    return true;
  }

  /* ********************************************************** */
  // topActionPropertyDefinitionBody
  public boolean formActionDeclaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formActionDeclaration")) return false;
    if (!nextTokenIs(b, LBRACE)) return false;
    boolean r;
    Marker m = enter_section_(b, l, FORM_ACTION_DECLARATION);
    r = topActionPropertyDefinitionBody(b, l + 1);
    exit_section_(b, m, FORM_ACTION_DECLARATION, r);
    return r;
  }

  /* ********************************************************** */
  // actionUsage LBRAC objectUsageList RBRAC
  public boolean formActionObject(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formActionObject")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FORM_ACTION_OBJECT, "<form action object>");
    r = actionUsage(b, l + 1);
    r = r && consumeToken(b, LBRAC);
    r = r && objectUsageList(b, l + 1);
    r = r && consumeToken(b, RBRAC);
    exit_section_(b, l, m, FORM_ACTION_OBJECT, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // OBJECTS formActionObjectUsage (COMMA formActionObjectUsage)*
  public boolean formActionObjectList(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formActionObjectList")) return false;
    if (!nextTokenIs(b, OBJECTS)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, FORM_ACTION_OBJECT_LIST, null);
    r = consumeToken(b, OBJECTS);
    p = r; // pin = 1
    r = r && formActionObjectUsage(b, l + 1);
    r = r && formActionObjectList_2(b, l + 1);
    exit_section_(b, l, m, FORM_ACTION_OBJECT_LIST, r, p, null);
    return r || p;
  }

  // (COMMA formActionObjectUsage)*
  private boolean formActionObjectList_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formActionObjectList_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!formActionObjectList_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "formActionObjectList_2", c)) break;
    }
    return true;
  }

  // COMMA formActionObjectUsage
  private boolean formActionObjectList_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formActionObjectList_2_0")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, COMMA);
    p = r; // pin = 1
    r = r && formActionObjectUsage(b, l + 1);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // objectInProps? objectInputProps?
  boolean formActionObjectProps(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formActionObjectProps")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = formActionObjectProps_0(b, l + 1);
    r = r && formActionObjectProps_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // objectInProps?
  private boolean formActionObjectProps_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formActionObjectProps_0")) return false;
    objectInProps(b, l + 1);
    return true;
  }

  // objectInputProps?
  private boolean formActionObjectProps_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formActionObjectProps_1")) return false;
    objectInputProps(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // objectUsage formActionObjectProps
  public boolean formActionObjectUsage(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formActionObjectUsage")) return false;
    if (!nextTokenIs(b, ID)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, FORM_ACTION_OBJECT_USAGE, null);
    r = objectUsage(b, l + 1);
    p = r; // pin = 1
    r = r && formActionObjectProps(b, l + 1);
    exit_section_(b, l, m, FORM_ACTION_OBJECT_USAGE, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // SHOW (stringLiteral equalsSign)?
  // 	     mappedForm
  // 		(
  // 		    contextFiltersClause
  //         |   syncTypeLiteral
  // 		|   windowTypeLiteral
  // 		|   manageSessionClause
  // 		|   noCancelClause
  // 		|   formSessionScopeClause
  // 		|   CHECK
  // 		|   READONLY
  // 		)*
  public boolean formActionPropertyDefinitionBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formActionPropertyDefinitionBody")) return false;
    if (!nextTokenIs(b, SHOW)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, FORM_ACTION_PROPERTY_DEFINITION_BODY, null);
    r = consumeToken(b, SHOW);
    p = r; // pin = 1
    r = r && formActionPropertyDefinitionBody_1(b, l + 1);
    r = r && mappedForm(b, l + 1);
    r = r && formActionPropertyDefinitionBody_3(b, l + 1);
    exit_section_(b, l, m, FORM_ACTION_PROPERTY_DEFINITION_BODY, r, p, null);
    return r || p;
  }

  // (stringLiteral equalsSign)?
  private boolean formActionPropertyDefinitionBody_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formActionPropertyDefinitionBody_1")) return false;
    formActionPropertyDefinitionBody_1_0(b, l + 1);
    return true;
  }

  // stringLiteral equalsSign
  private boolean formActionPropertyDefinitionBody_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formActionPropertyDefinitionBody_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = stringLiteral(b, l + 1);
    r = r && equalsSign(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (
  // 		    contextFiltersClause
  //         |   syncTypeLiteral
  // 		|   windowTypeLiteral
  // 		|   manageSessionClause
  // 		|   noCancelClause
  // 		|   formSessionScopeClause
  // 		|   CHECK
  // 		|   READONLY
  // 		)*
  private boolean formActionPropertyDefinitionBody_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formActionPropertyDefinitionBody_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!formActionPropertyDefinitionBody_3_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "formActionPropertyDefinitionBody_3", c)) break;
    }
    return true;
  }

  // contextFiltersClause
  //         |   syncTypeLiteral
  // 		|   windowTypeLiteral
  // 		|   manageSessionClause
  // 		|   noCancelClause
  // 		|   formSessionScopeClause
  // 		|   CHECK
  // 		|   READONLY
  private boolean formActionPropertyDefinitionBody_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formActionPropertyDefinitionBody_3_0")) return false;
    boolean r;
    r = contextFiltersClause(b, l + 1);
    if (!r) r = syncTypeLiteral(b, l + 1);
    if (!r) r = windowTypeLiteral(b, l + 1);
    if (!r) r = manageSessionClause(b, l + 1);
    if (!r) r = noCancelClause(b, l + 1);
    if (!r) r = formSessionScopeClause(b, l + 1);
    if (!r) r = consumeToken(b, CHECK);
    if (!r) r = consumeToken(b, READONLY);
    return r;
  }

  /* ********************************************************** */
  // formActionObject | formActionDeclaration
  public boolean formActionPropertyObject(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formActionPropertyObject")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FORM_ACTION_PROPERTY_OBJECT, "<form action property object>");
    r = formActionObject(b, l + 1);
    if (!r) r = formActionDeclaration(b, l + 1);
    exit_section_(b, l, m, FORM_ACTION_PROPERTY_OBJECT, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // formExprDeclaration
  public boolean formCalcPropertyObject(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formCalcPropertyObject")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FORM_CALC_PROPERTY_OBJECT, "<form calc property object>");
    r = formExprDeclaration(b, l + 1);
    exit_section_(b, l, m, FORM_CALC_PROPERTY_OBJECT, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // FORM simpleNameWithCaption
  // 		            (   imageSetting
  // 		            |   autorefreshLiteral
  // 		            |   LOCALASYNC
  // 		            )*
  public boolean formDecl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formDecl")) return false;
    if (!nextTokenIs(b, FORM)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, FORM_DECL, null);
    r = consumeToken(b, FORM);
    r = r && simpleNameWithCaption(b, l + 1);
    p = r; // pin = 2
    r = r && formDecl_2(b, l + 1);
    exit_section_(b, l, m, FORM_DECL, r, p, null);
    return r || p;
  }

  // (   imageSetting
  // 		            |   autorefreshLiteral
  // 		            |   LOCALASYNC
  // 		            )*
  private boolean formDecl_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formDecl_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!formDecl_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "formDecl_2", c)) break;
    }
    return true;
  }

  // imageSetting
  // 		            |   autorefreshLiteral
  // 		            |   LOCALASYNC
  private boolean formDecl_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formDecl_2_0")) return false;
    boolean r;
    r = imageSetting(b, l + 1);
    if (!r) r = autorefreshLiteral(b, l + 1);
    if (!r) r = consumeToken(b, LOCALASYNC);
    return r;
  }

  /* ********************************************************** */
  // compoundID
  public boolean formElseNoParamsActionUsage(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formElseNoParamsActionUsage")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FORM_ELSE_NO_PARAMS_ACTION_USAGE, "<form else no params action usage>");
    r = compoundID(b, l + 1);
    exit_section_(b, l, m, FORM_ELSE_NO_PARAMS_ACTION_USAGE, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // ON
  //                         (    OK (BEFORE | AFTER)?
  //                         |    APPLY (BEFORE | AFTER)?
  //                         |    CLOSE
  //                         |    INIT
  //                         |    CANCEL
  //                         |    DROP
  //                         |    QUERYCLOSE
  //                         |    CHANGE ID
  //                         |    SCHEDULE PERIOD intLiteral FIXED?
  //                         |    ORDER groupObjectUsage (TO propertyUsage)?
  //                         |    FILTER groupObjectUsage (TO propertyUsage)?
  //                         )
  //                         formActionPropertyObject
  public boolean formEventDeclaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formEventDeclaration")) return false;
    if (!nextTokenIs(b, ON)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, FORM_EVENT_DECLARATION, null);
    r = consumeToken(b, ON);
    p = r; // pin = 1
    r = r && formEventDeclaration_1(b, l + 1);
    r = r && formActionPropertyObject(b, l + 1);
    exit_section_(b, l, m, FORM_EVENT_DECLARATION, r, p, null);
    return r || p;
  }

  // OK (BEFORE | AFTER)?
  //                         |    APPLY (BEFORE | AFTER)?
  //                         |    CLOSE
  //                         |    INIT
  //                         |    CANCEL
  //                         |    DROP
  //                         |    QUERYCLOSE
  //                         |    CHANGE ID
  //                         |    SCHEDULE PERIOD intLiteral FIXED?
  //                         |    ORDER groupObjectUsage (TO propertyUsage)?
  //                         |    FILTER groupObjectUsage (TO propertyUsage)?
  private boolean formEventDeclaration_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formEventDeclaration_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = formEventDeclaration_1_0(b, l + 1);
    if (!r) r = formEventDeclaration_1_1(b, l + 1);
    if (!r) r = consumeToken(b, CLOSE);
    if (!r) r = consumeToken(b, INIT);
    if (!r) r = consumeToken(b, CANCEL);
    if (!r) r = consumeToken(b, DROP);
    if (!r) r = consumeToken(b, QUERYCLOSE);
    if (!r) r = parseTokens(b, 0, CHANGE, ID);
    if (!r) r = formEventDeclaration_1_8(b, l + 1);
    if (!r) r = formEventDeclaration_1_9(b, l + 1);
    if (!r) r = formEventDeclaration_1_10(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // OK (BEFORE | AFTER)?
  private boolean formEventDeclaration_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formEventDeclaration_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, OK);
    r = r && formEventDeclaration_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (BEFORE | AFTER)?
  private boolean formEventDeclaration_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formEventDeclaration_1_0_1")) return false;
    formEventDeclaration_1_0_1_0(b, l + 1);
    return true;
  }

  // BEFORE | AFTER
  private boolean formEventDeclaration_1_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formEventDeclaration_1_0_1_0")) return false;
    boolean r;
    r = consumeToken(b, BEFORE);
    if (!r) r = consumeToken(b, AFTER);
    return r;
  }

  // APPLY (BEFORE | AFTER)?
  private boolean formEventDeclaration_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formEventDeclaration_1_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, APPLY);
    r = r && formEventDeclaration_1_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (BEFORE | AFTER)?
  private boolean formEventDeclaration_1_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formEventDeclaration_1_1_1")) return false;
    formEventDeclaration_1_1_1_0(b, l + 1);
    return true;
  }

  // BEFORE | AFTER
  private boolean formEventDeclaration_1_1_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formEventDeclaration_1_1_1_0")) return false;
    boolean r;
    r = consumeToken(b, BEFORE);
    if (!r) r = consumeToken(b, AFTER);
    return r;
  }

  // SCHEDULE PERIOD intLiteral FIXED?
  private boolean formEventDeclaration_1_8(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formEventDeclaration_1_8")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeTokens(b, 0, SCHEDULE, PERIOD);
    r = r && intLiteral(b, l + 1);
    r = r && formEventDeclaration_1_8_3(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // FIXED?
  private boolean formEventDeclaration_1_8_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formEventDeclaration_1_8_3")) return false;
    consumeToken(b, FIXED);
    return true;
  }

  // ORDER groupObjectUsage (TO propertyUsage)?
  private boolean formEventDeclaration_1_9(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formEventDeclaration_1_9")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, ORDER);
    r = r && groupObjectUsage(b, l + 1);
    r = r && formEventDeclaration_1_9_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (TO propertyUsage)?
  private boolean formEventDeclaration_1_9_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formEventDeclaration_1_9_2")) return false;
    formEventDeclaration_1_9_2_0(b, l + 1);
    return true;
  }

  // TO propertyUsage
  private boolean formEventDeclaration_1_9_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formEventDeclaration_1_9_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, TO);
    r = r && propertyUsage(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // FILTER groupObjectUsage (TO propertyUsage)?
  private boolean formEventDeclaration_1_10(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formEventDeclaration_1_10")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, FILTER);
    r = r && groupObjectUsage(b, l + 1);
    r = r && formEventDeclaration_1_10_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (TO propertyUsage)?
  private boolean formEventDeclaration_1_10_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formEventDeclaration_1_10_2")) return false;
    formEventDeclaration_1_10_2_0(b, l + 1);
    return true;
  }

  // TO propertyUsage
  private boolean formEventDeclaration_1_10_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formEventDeclaration_1_10_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, TO);
    r = r && propertyUsage(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // CHANGE | GROUPCHANGE | CHANGEWYS | EDIT | contextMenuEventType | keyPressedEventType
  public boolean formEventType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formEventType")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FORM_EVENT_TYPE, "<form event type>");
    r = consumeToken(b, CHANGE);
    if (!r) r = consumeToken(b, GROUPCHANGE);
    if (!r) r = consumeToken(b, CHANGEWYS);
    if (!r) r = consumeToken(b, EDIT);
    if (!r) r = contextMenuEventType(b, l + 1);
    if (!r) r = keyPressedEventType(b, l + 1);
    exit_section_(b, l, m, FORM_EVENT_TYPE, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // EVENTS formEventDeclaration (COMMA formEventDeclaration)*
  public boolean formEventsList(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formEventsList")) return false;
    if (!nextTokenIs(b, EVENTS)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, FORM_EVENTS_LIST, null);
    r = consumeToken(b, EVENTS);
    p = r; // pin = 1
    r = r && formEventDeclaration(b, l + 1);
    r = r && formEventsList_2(b, l + 1);
    exit_section_(b, l, m, FORM_EVENTS_LIST, r, p, null);
    return r || p;
  }

  // (COMMA formEventDeclaration)*
  private boolean formEventsList_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formEventsList_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!formEventsList_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "formEventsList_2", c)) break;
    }
    return true;
  }

  // COMMA formEventDeclaration
  private boolean formEventsList_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formEventsList_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, COMMA);
    r = r && formEventDeclaration(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // propertyExpression
  public boolean formExprDeclaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formExprDeclaration")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FORM_EXPR_DECLARATION, "<form expr declaration>");
    r = propertyExpression(b, l + 1);
    exit_section_(b, l, m, FORM_EXPR_DECLARATION, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // EXTID stringLiteral
  public boolean formExtID(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formExtID")) return false;
    if (!nextTokenIs(b, EXTID)) return false;
    boolean r;
    Marker m = enter_section_(b, l, FORM_EXT_ID);
    r = consumeToken(b, EXTID);
    r = r && stringLiteral(b, l + 1);
    exit_section_(b, m, FORM_EXT_ID, r);
    return r;
  }

  /* ********************************************************** */
  // FORMEXTID stringLiteral
  public boolean formExtIDSetting(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formExtIDSetting")) return false;
    if (!nextTokenIs(b, FORMEXTID)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, FORM_EXT_ID_SETTING, null);
    r = consumeToken(b, FORMEXTID);
    p = r; // pin = 1
    r = r && stringLiteral(b, l + 1);
    exit_section_(b, l, m, FORM_EXT_ID_SETTING, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // EXTKEY
  public boolean formExtKey(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formExtKey")) return false;
    if (!nextTokenIs(b, EXTKEY)) return false;
    boolean r;
    Marker m = enter_section_(b, l, FORM_EXT_KEY);
    r = consumeToken(b, EXTKEY);
    exit_section_(b, m, FORM_EXT_KEY, r);
    return r;
  }

  /* ********************************************************** */
  // EXTEND FILTERGROUP filterGroupUsage regularFilterDeclaration+
  public boolean formExtendFilterGroupDeclaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formExtendFilterGroupDeclaration")) return false;
    if (!nextTokenIs(b, EXTEND)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, FORM_EXTEND_FILTER_GROUP_DECLARATION, null);
    r = consumeTokens(b, 1, EXTEND, FILTERGROUP);
    p = r; // pin = 1
    r = r && filterGroupUsage(b, l + 1);
    r = r && formExtendFilterGroupDeclaration_3(b, l + 1);
    exit_section_(b, l, m, FORM_EXTEND_FILTER_GROUP_DECLARATION, r, p, null);
    return r || p;
  }

  // regularFilterDeclaration+
  private boolean formExtendFilterGroupDeclaration_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formExtendFilterGroupDeclaration_3")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = regularFilterDeclaration(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!regularFilterDeclaration(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "formExtendFilterGroupDeclaration_3", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // FILTERGROUP filterGroupName regularFilterDeclaration*
  public boolean formFilterGroupDeclaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formFilterGroupDeclaration")) return false;
    if (!nextTokenIs(b, FILTERGROUP)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, FORM_FILTER_GROUP_DECLARATION, null);
    r = consumeToken(b, FILTERGROUP);
    p = r; // pin = 1
    r = r && filterGroupName(b, l + 1);
    r = r && formFilterGroupDeclaration_2(b, l + 1);
    exit_section_(b, l, m, FORM_FILTER_GROUP_DECLARATION, r, p, null);
    return r || p;
  }

  // regularFilterDeclaration*
  private boolean formFilterGroupDeclaration_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formFilterGroupDeclaration_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!regularFilterDeclaration(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "formFilterGroupDeclaration_2", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // FILTERS formExprDeclaration (COMMA formExprDeclaration)*
  public boolean formFiltersList(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formFiltersList")) return false;
    if (!nextTokenIs(b, FILTERS)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, FORM_FILTERS_LIST, null);
    r = consumeToken(b, FILTERS);
    p = r; // pin = 1
    r = r && formExprDeclaration(b, l + 1);
    r = r && formFiltersList_2(b, l + 1);
    exit_section_(b, l, m, FORM_FILTERS_LIST, r, p, null);
    return r || p;
  }

  // (COMMA formExprDeclaration)*
  private boolean formFiltersList_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formFiltersList_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!formFiltersList_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "formFiltersList_2", c)) break;
    }
    return true;
  }

  // COMMA formExprDeclaration
  private boolean formFiltersList_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formFiltersList_2_0")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, COMMA);
    p = r; // pin = 1
    r = r && formExprDeclaration(b, l + 1);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // formSingleGroupObjectDeclaration | formMultiGroupObjectDeclaration
  public boolean formGroupObject(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formGroupObject")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FORM_GROUP_OBJECT, "<form group object>");
    r = formSingleGroupObjectDeclaration(b, l + 1);
    if (!r) r = formMultiGroupObjectDeclaration(b, l + 1);
    exit_section_(b, l, m, FORM_GROUP_OBJECT, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // BACKGROUND formCalcPropertyObject
  public boolean formGroupObjectBackground(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formGroupObjectBackground")) return false;
    if (!nextTokenIs(b, BACKGROUND)) return false;
    boolean r;
    Marker m = enter_section_(b, l, FORM_GROUP_OBJECT_BACKGROUND);
    r = consumeToken(b, BACKGROUND);
    r = r && formCalcPropertyObject(b, l + 1);
    exit_section_(b, m, FORM_GROUP_OBJECT_BACKGROUND, r);
    return r;
  }

  /* ********************************************************** */
  // formGroupObject formGroupObjectOptions
  public boolean formGroupObjectDeclaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formGroupObjectDeclaration")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FORM_GROUP_OBJECT_DECLARATION, "<form group object declaration>");
    r = formGroupObject(b, l + 1);
    r = r && formGroupObjectOptions(b, l + 1);
    exit_section_(b, l, m, FORM_GROUP_OBJECT_DECLARATION, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // FOREGROUND formCalcPropertyObject
  public boolean formGroupObjectForeground(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formGroupObjectForeground")) return false;
    if (!nextTokenIs(b, FOREGROUND)) return false;
    boolean r;
    Marker m = enter_section_(b, l, FORM_GROUP_OBJECT_FOREGROUND);
    r = consumeToken(b, FOREGROUND);
    r = r && formCalcPropertyObject(b, l + 1);
    exit_section_(b, m, FORM_GROUP_OBJECT_FOREGROUND, r);
    return r;
  }

  /* ********************************************************** */
  // INIT | FIXED
  public boolean formGroupObjectInitViewType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formGroupObjectInitViewType")) return false;
    if (!nextTokenIs(b, "<form group object init view type>", FIXED, INIT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FORM_GROUP_OBJECT_INIT_VIEW_TYPE, "<form group object init view type>");
    r = consumeToken(b, INIT);
    if (!r) r = consumeToken(b, FIXED);
    exit_section_(b, l, m, FORM_GROUP_OBJECT_INIT_VIEW_TYPE, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // (    formGroupObjectViewType
  //                            |    formExtKey
  //                            |    formSubReport 
  //                            |    formGroupObjectInitViewType
  //                            |    formGroupObjectPageSize
  //                            |    formGroupObjectUpdate
  //                            |    formGroupObjectRelativePosition
  //                            |    formGroupObjectBackground
  //                            |    formGroupObjectForeground
  //                            |    formInGroup
  //                            |    formExtID
  //                            )*
  public boolean formGroupObjectOptions(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formGroupObjectOptions")) return false;
    Marker m = enter_section_(b, l, _NONE_, FORM_GROUP_OBJECT_OPTIONS, "<form group object options>");
    while (true) {
      int c = current_position_(b);
      if (!formGroupObjectOptions_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "formGroupObjectOptions", c)) break;
    }
    exit_section_(b, l, m, FORM_GROUP_OBJECT_OPTIONS, true, false, null);
    return true;
  }

  // formGroupObjectViewType
  //                            |    formExtKey
  //                            |    formSubReport 
  //                            |    formGroupObjectInitViewType
  //                            |    formGroupObjectPageSize
  //                            |    formGroupObjectUpdate
  //                            |    formGroupObjectRelativePosition
  //                            |    formGroupObjectBackground
  //                            |    formGroupObjectForeground
  //                            |    formInGroup
  //                            |    formExtID
  private boolean formGroupObjectOptions_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formGroupObjectOptions_0")) return false;
    boolean r;
    r = formGroupObjectViewType(b, l + 1);
    if (!r) r = formExtKey(b, l + 1);
    if (!r) r = formSubReport(b, l + 1);
    if (!r) r = formGroupObjectInitViewType(b, l + 1);
    if (!r) r = formGroupObjectPageSize(b, l + 1);
    if (!r) r = formGroupObjectUpdate(b, l + 1);
    if (!r) r = formGroupObjectRelativePosition(b, l + 1);
    if (!r) r = formGroupObjectBackground(b, l + 1);
    if (!r) r = formGroupObjectForeground(b, l + 1);
    if (!r) r = formInGroup(b, l + 1);
    if (!r) r = formExtID(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // PAGESIZE intLiteral
  public boolean formGroupObjectPageSize(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formGroupObjectPageSize")) return false;
    if (!nextTokenIs(b, PAGESIZE)) return false;
    boolean r;
    Marker m = enter_section_(b, l, FORM_GROUP_OBJECT_PAGE_SIZE);
    r = consumeToken(b, PAGESIZE);
    r = r && intLiteral(b, l + 1);
    exit_section_(b, m, FORM_GROUP_OBJECT_PAGE_SIZE, r);
    return r;
  }

  /* ********************************************************** */
  // staticRelativePosition | (AFTER | BEFORE) groupObjectUsage
  public boolean formGroupObjectRelativePosition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formGroupObjectRelativePosition")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FORM_GROUP_OBJECT_RELATIVE_POSITION, "<form group object relative position>");
    r = staticRelativePosition(b, l + 1);
    if (!r) r = formGroupObjectRelativePosition_1(b, l + 1);
    exit_section_(b, l, m, FORM_GROUP_OBJECT_RELATIVE_POSITION, r, false, null);
    return r;
  }

  // (AFTER | BEFORE) groupObjectUsage
  private boolean formGroupObjectRelativePosition_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formGroupObjectRelativePosition_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = formGroupObjectRelativePosition_1_0(b, l + 1);
    r = r && groupObjectUsage(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // AFTER | BEFORE
  private boolean formGroupObjectRelativePosition_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formGroupObjectRelativePosition_1_0")) return false;
    boolean r;
    r = consumeToken(b, AFTER);
    if (!r) r = consumeToken(b, BEFORE);
    return r;
  }

  /* ********************************************************** */
  // FIRST | LAST | PREV | NULL
  public boolean formGroupObjectUpdate(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formGroupObjectUpdate")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FORM_GROUP_OBJECT_UPDATE, "<form group object update>");
    r = consumeToken(b, FIRST);
    if (!r) r = consumeToken(b, LAST);
    if (!r) r = consumeToken(b, PREV);
    if (!r) r = consumeToken(b, NULL);
    exit_section_(b, l, m, FORM_GROUP_OBJECT_UPDATE, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // classViewType | listViewType
  public boolean formGroupObjectViewType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formGroupObjectViewType")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FORM_GROUP_OBJECT_VIEW_TYPE, "<form group object view type>");
    r = classViewType(b, l + 1);
    if (!r) r = listViewType(b, l + 1);
    exit_section_(b, l, m, FORM_GROUP_OBJECT_VIEW_TYPE, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // OBJECTS formGroupObjectDeclaration (COMMA formGroupObjectDeclaration)*
  public boolean formGroupObjectsList(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formGroupObjectsList")) return false;
    if (!nextTokenIs(b, OBJECTS)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, FORM_GROUP_OBJECTS_LIST, null);
    r = consumeToken(b, OBJECTS);
    p = r; // pin = 1
    r = r && formGroupObjectDeclaration(b, l + 1);
    r = r && formGroupObjectsList_2(b, l + 1);
    exit_section_(b, l, m, FORM_GROUP_OBJECTS_LIST, r, p, null);
    return r || p;
  }

  // (COMMA formGroupObjectDeclaration)*
  private boolean formGroupObjectsList_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formGroupObjectsList_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!formGroupObjectsList_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "formGroupObjectsList_2", c)) break;
    }
    return true;
  }

  // COMMA formGroupObjectDeclaration
  private boolean formGroupObjectsList_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formGroupObjectsList_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, COMMA);
    r = r && formGroupObjectDeclaration(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // (HINTNOUPDATE | HINTTABLE) LIST nonEmptyNoContextPropertyUsageList
  public boolean formHintsList(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formHintsList")) return false;
    if (!nextTokenIs(b, "<form hints list>", HINTNOUPDATE, HINTTABLE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, FORM_HINTS_LIST, "<form hints list>");
    r = formHintsList_0(b, l + 1);
    p = r; // pin = 1
    r = r && consumeToken(b, LIST);
    r = r && nonEmptyNoContextPropertyUsageList(b, l + 1);
    exit_section_(b, l, m, FORM_HINTS_LIST, r, p, null);
    return r || p;
  }

  // HINTNOUPDATE | HINTTABLE
  private boolean formHintsList_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formHintsList_0")) return false;
    boolean r;
    r = consumeToken(b, HINTNOUPDATE);
    if (!r) r = consumeToken(b, HINTTABLE);
    return r;
  }

  /* ********************************************************** */
  // IN groupUsage
  public boolean formInGroup(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formInGroup")) return false;
    if (!nextTokenIs(b, IN)) return false;
    boolean r;
    Marker m = enter_section_(b, l, FORM_IN_GROUP);
    r = consumeToken(b, IN);
    r = r && groupUsage(b, l + 1);
    exit_section_(b, m, FORM_IN_GROUP, r);
    return r;
  }

  /* ********************************************************** */
  // LBRAC objectUsageList RBRAC formPropertyOptionsList formPropertiesNamesDeclList
  public boolean formMappedNamePropertiesList(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formMappedNamePropertiesList")) return false;
    if (!nextTokenIs(b, LBRAC)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, FORM_MAPPED_NAME_PROPERTIES_LIST, null);
    r = consumeToken(b, LBRAC);
    p = r; // pin = 1
    r = r && objectUsageList(b, l + 1);
    r = r && consumeToken(b, RBRAC);
    r = r && formPropertyOptionsList(b, l + 1);
    r = r && formPropertiesNamesDeclList(b, l + 1);
    exit_section_(b, l, m, FORM_MAPPED_NAME_PROPERTIES_LIST, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // formPropertyDrawMappedDecl (COMMA formPropertyDrawMappedDecl)*
  public boolean formMappedPropertiesList(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formMappedPropertiesList")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, FORM_MAPPED_PROPERTIES_LIST, "<form mapped properties list>");
    r = formPropertyDrawMappedDecl(b, l + 1);
    p = r; // pin = 1
    r = r && formMappedPropertiesList_1(b, l + 1);
    exit_section_(b, l, m, FORM_MAPPED_PROPERTIES_LIST, r, p, null);
    return r || p;
  }

  // (COMMA formPropertyDrawMappedDecl)*
  private boolean formMappedPropertiesList_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formMappedPropertiesList_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!formMappedPropertiesList_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "formMappedPropertiesList_1", c)) break;
    }
    return true;
  }

  // COMMA formPropertyDrawMappedDecl
  private boolean formMappedPropertiesList_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formMappedPropertiesList_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, COMMA);
    r = r && formPropertyDrawMappedDecl(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // (simpleName EQUALS)?
  // 		                            LBRAC
  // 			                        formObjectDeclaration (COMMA formObjectDeclaration)*
  // 		                            RBRAC
  public boolean formMultiGroupObjectDeclaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formMultiGroupObjectDeclaration")) return false;
    if (!nextTokenIs(b, "<form multi group object declaration>", ID, LBRAC)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FORM_MULTI_GROUP_OBJECT_DECLARATION, "<form multi group object declaration>");
    r = formMultiGroupObjectDeclaration_0(b, l + 1);
    r = r && consumeToken(b, LBRAC);
    r = r && formObjectDeclaration(b, l + 1);
    r = r && formMultiGroupObjectDeclaration_3(b, l + 1);
    r = r && consumeToken(b, RBRAC);
    exit_section_(b, l, m, FORM_MULTI_GROUP_OBJECT_DECLARATION, r, false, null);
    return r;
  }

  // (simpleName EQUALS)?
  private boolean formMultiGroupObjectDeclaration_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formMultiGroupObjectDeclaration_0")) return false;
    formMultiGroupObjectDeclaration_0_0(b, l + 1);
    return true;
  }

  // simpleName EQUALS
  private boolean formMultiGroupObjectDeclaration_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formMultiGroupObjectDeclaration_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = simpleName(b, l + 1);
    r = r && consumeToken(b, EQUALS);
    exit_section_(b, m, null, r);
    return r;
  }

  // (COMMA formObjectDeclaration)*
  private boolean formMultiGroupObjectDeclaration_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formMultiGroupObjectDeclaration_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!formMultiGroupObjectDeclaration_3_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "formMultiGroupObjectDeclaration_3", c)) break;
    }
    return true;
  }

  // COMMA formObjectDeclaration
  private boolean formMultiGroupObjectDeclaration_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formMultiGroupObjectDeclaration_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, COMMA);
    r = r && formObjectDeclaration(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // (simpleName? localizedStringLiteral? EQUALS)?
  // 		                  className
  // 		                  (ON CHANGE formActionPropertyObject)?
  public boolean formObjectDeclaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formObjectDeclaration")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FORM_OBJECT_DECLARATION, "<form object declaration>");
    r = formObjectDeclaration_0(b, l + 1);
    r = r && className(b, l + 1);
    r = r && formObjectDeclaration_2(b, l + 1);
    exit_section_(b, l, m, FORM_OBJECT_DECLARATION, r, false, null);
    return r;
  }

  // (simpleName? localizedStringLiteral? EQUALS)?
  private boolean formObjectDeclaration_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formObjectDeclaration_0")) return false;
    formObjectDeclaration_0_0(b, l + 1);
    return true;
  }

  // simpleName? localizedStringLiteral? EQUALS
  private boolean formObjectDeclaration_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formObjectDeclaration_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = formObjectDeclaration_0_0_0(b, l + 1);
    r = r && formObjectDeclaration_0_0_1(b, l + 1);
    r = r && consumeToken(b, EQUALS);
    exit_section_(b, m, null, r);
    return r;
  }

  // simpleName?
  private boolean formObjectDeclaration_0_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formObjectDeclaration_0_0_0")) return false;
    simpleName(b, l + 1);
    return true;
  }

  // localizedStringLiteral?
  private boolean formObjectDeclaration_0_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formObjectDeclaration_0_0_1")) return false;
    localizedStringLiteral(b, l + 1);
    return true;
  }

  // (ON CHANGE formActionPropertyObject)?
  private boolean formObjectDeclaration_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formObjectDeclaration_2")) return false;
    formObjectDeclaration_2_0(b, l + 1);
    return true;
  }

  // ON CHANGE formActionPropertyObject
  private boolean formObjectDeclaration_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formObjectDeclaration_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeTokens(b, 0, ON, CHANGE);
    r = r && formActionPropertyObject(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // COLUMNS (stringLiteral)? LBRAC nonEmptyGroupObjectUsageList RBRAC
  public boolean formOptionColumns(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formOptionColumns")) return false;
    if (!nextTokenIs(b, COLUMNS)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, FORM_OPTION_COLUMNS, null);
    r = consumeToken(b, COLUMNS);
    p = r; // pin = 1
    r = r && formOptionColumns_1(b, l + 1);
    r = r && consumeToken(b, LBRAC);
    r = r && nonEmptyGroupObjectUsageList(b, l + 1);
    r = r && consumeToken(b, RBRAC);
    exit_section_(b, l, m, FORM_OPTION_COLUMNS, r, p, null);
    return r || p;
  }

  // (stringLiteral)?
  private boolean formOptionColumns_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formOptionColumns_1")) return false;
    formOptionColumns_1_0(b, l + 1);
    return true;
  }

  // (stringLiteral)
  private boolean formOptionColumns_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formOptionColumns_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = stringLiteral(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // propertyCustomView
  public boolean formOptionCustomView(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formOptionCustomView")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FORM_OPTION_CUSTOM_VIEW, "<form option custom view>");
    r = propertyCustomView(b, l + 1);
    exit_section_(b, l, m, FORM_OPTION_CUSTOM_VIEW, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // EVENTID stringLiteral
  public boolean formOptionEventId(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formOptionEventId")) return false;
    if (!nextTokenIs(b, EVENTID)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, FORM_OPTION_EVENT_ID, null);
    r = consumeToken(b, EVENTID);
    p = r; // pin = 1
    r = r && stringLiteral(b, l + 1);
    exit_section_(b, l, m, FORM_OPTION_EVENT_ID, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // classViewType
  public boolean formOptionForce(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formOptionForce")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FORM_OPTION_FORCE, "<form option force>");
    r = classViewType(b, l + 1);
    exit_section_(b, l, m, FORM_OPTION_FORCE, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // staticRelativePosition | (BEFORE | AFTER) formPropertyDrawUsage
  public boolean formOptionInsertType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formOptionInsertType")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FORM_OPTION_INSERT_TYPE, "<form option insert type>");
    r = staticRelativePosition(b, l + 1);
    if (!r) r = formOptionInsertType_1(b, l + 1);
    exit_section_(b, l, m, FORM_OPTION_INSERT_TYPE, r, false, null);
    return r;
  }

  // (BEFORE | AFTER) formPropertyDrawUsage
  private boolean formOptionInsertType_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formOptionInsertType_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = formOptionInsertType_1_0(b, l + 1);
    r = r && formPropertyDrawUsage(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // BEFORE | AFTER
  private boolean formOptionInsertType_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formOptionInsertType_1_0")) return false;
    boolean r;
    r = consumeToken(b, BEFORE);
    if (!r) r = consumeToken(b, AFTER);
    return r;
  }

  /* ********************************************************** */
  // QUICKFILTER formPropertyDrawUsage
  public boolean formOptionQuickFilter(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formOptionQuickFilter")) return false;
    if (!nextTokenIs(b, QUICKFILTER)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, FORM_OPTION_QUICK_FILTER, null);
    r = consumeToken(b, QUICKFILTER);
    p = r; // pin = 1
    r = r && formPropertyDrawUsage(b, l + 1);
    exit_section_(b, l, m, FORM_OPTION_QUICK_FILTER, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // NEWSESSION | NESTEDSESSION
  public boolean formOptionSession(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formOptionSession")) return false;
    if (!nextTokenIs(b, "<form option session>", NESTEDSESSION, NEWSESSION)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FORM_OPTION_SESSION, "<form option session>");
    r = consumeToken(b, NEWSESSION);
    if (!r) r = consumeToken(b, NESTEDSESSION);
    exit_section_(b, l, m, FORM_OPTION_SESSION, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // DRAW groupObjectUsage
  public boolean formOptionToDraw(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formOptionToDraw")) return false;
    if (!nextTokenIs(b, DRAW)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, FORM_OPTION_TO_DRAW, null);
    r = consumeToken(b, DRAW);
    p = r; // pin = 1
    r = r && groupObjectUsage(b, l + 1);
    exit_section_(b, l, m, FORM_OPTION_TO_DRAW, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // ON formEventType formActionPropertyObject
  public boolean formOptionsOnEvents(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formOptionsOnEvents")) return false;
    if (!nextTokenIs(b, ON)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, FORM_OPTIONS_ON_EVENTS, null);
    r = consumeToken(b, ON);
    p = r; // pin = 1
    r = r && formEventType(b, l + 1);
    r = r && formActionPropertyObject(b, l + 1);
    exit_section_(b, l, m, FORM_OPTIONS_ON_EVENTS, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // (SHOWIF | READONLYIF | DISABLEIF | CLASS | BACKGROUND | FOREGROUND | HEADER | FOOTER) formCalcPropertyObject
  public boolean formOptionsWithCalcPropertyObject(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formOptionsWithCalcPropertyObject")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, FORM_OPTIONS_WITH_CALC_PROPERTY_OBJECT, "<form options with calc property object>");
    r = formOptionsWithCalcPropertyObject_0(b, l + 1);
    p = r; // pin = 1
    r = r && formCalcPropertyObject(b, l + 1);
    exit_section_(b, l, m, FORM_OPTIONS_WITH_CALC_PROPERTY_OBJECT, r, p, null);
    return r || p;
  }

  // SHOWIF | READONLYIF | DISABLEIF | CLASS | BACKGROUND | FOREGROUND | HEADER | FOOTER
  private boolean formOptionsWithCalcPropertyObject_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formOptionsWithCalcPropertyObject_0")) return false;
    boolean r;
    r = consumeToken(b, SHOWIF);
    if (!r) r = consumeToken(b, READONLYIF);
    if (!r) r = consumeToken(b, DISABLEIF);
    if (!r) r = consumeToken(b, CLASS);
    if (!r) r = consumeToken(b, BACKGROUND);
    if (!r) r = consumeToken(b, FOREGROUND);
    if (!r) r = consumeToken(b, HEADER);
    if (!r) r = consumeToken(b, FOOTER);
    return r;
  }

  /* ********************************************************** */
  // (IMAGE (AUTO | formCalcPropertyObject)?) | NOIMAGE
  public boolean formOptionsWithOptionalCalcPropertyObject(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formOptionsWithOptionalCalcPropertyObject")) return false;
    if (!nextTokenIs(b, "<form options with optional calc property object>", IMAGE, NOIMAGE)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FORM_OPTIONS_WITH_OPTIONAL_CALC_PROPERTY_OBJECT, "<form options with optional calc property object>");
    r = formOptionsWithOptionalCalcPropertyObject_0(b, l + 1);
    if (!r) r = consumeToken(b, NOIMAGE);
    exit_section_(b, l, m, FORM_OPTIONS_WITH_OPTIONAL_CALC_PROPERTY_OBJECT, r, false, null);
    return r;
  }

  // IMAGE (AUTO | formCalcPropertyObject)?
  private boolean formOptionsWithOptionalCalcPropertyObject_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formOptionsWithOptionalCalcPropertyObject_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, IMAGE);
    r = r && formOptionsWithOptionalCalcPropertyObject_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (AUTO | formCalcPropertyObject)?
  private boolean formOptionsWithOptionalCalcPropertyObject_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formOptionsWithOptionalCalcPropertyObject_0_1")) return false;
    formOptionsWithOptionalCalcPropertyObject_0_1_0(b, l + 1);
    return true;
  }

  // AUTO | formCalcPropertyObject
  private boolean formOptionsWithOptionalCalcPropertyObject_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formOptionsWithOptionalCalcPropertyObject_0_1_0")) return false;
    boolean r;
    r = consumeToken(b, AUTO);
    if (!r) r = formCalcPropertyObject(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // ORDERS FIRST? formPropertyDrawWithOrder (COMMA formPropertyDrawWithOrder)*
  public boolean formOrderByList(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formOrderByList")) return false;
    if (!nextTokenIs(b, ORDERS)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, FORM_ORDER_BY_LIST, null);
    r = consumeToken(b, ORDERS);
    p = r; // pin = 1
    r = r && formOrderByList_1(b, l + 1);
    r = r && formPropertyDrawWithOrder(b, l + 1);
    r = r && formOrderByList_3(b, l + 1);
    exit_section_(b, l, m, FORM_ORDER_BY_LIST, r, p, null);
    return r || p;
  }

  // FIRST?
  private boolean formOrderByList_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formOrderByList_1")) return false;
    consumeToken(b, FIRST);
    return true;
  }

  // (COMMA formPropertyDrawWithOrder)*
  private boolean formOrderByList_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formOrderByList_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!formOrderByList_3_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "formOrderByList_3", c)) break;
    }
    return true;
  }

  // COMMA formPropertyDrawWithOrder
  private boolean formOrderByList_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formOrderByList_3_0")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, COMMA);
    p = r; // pin = 1
    r = r && formPropertyDrawWithOrder(b, l + 1);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // PIVOT (
  //         (groupObjectUsage pivotOptions)
  //      |  (COLUMNS pivotPropertyDrawList (COMMA pivotPropertyDrawList)*)
  //      |  (ROWS pivotPropertyDrawList (COMMA pivotPropertyDrawList)*)
  //      |  (MEASURES formPropertyDrawUsage (COMMA formPropertyDrawUsage)*)
  //      )+
  public boolean formPivotOptionsDeclaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formPivotOptionsDeclaration")) return false;
    if (!nextTokenIs(b, PIVOT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, FORM_PIVOT_OPTIONS_DECLARATION);
    r = consumeToken(b, PIVOT);
    r = r && formPivotOptionsDeclaration_1(b, l + 1);
    exit_section_(b, m, FORM_PIVOT_OPTIONS_DECLARATION, r);
    return r;
  }

  // (
  //         (groupObjectUsage pivotOptions)
  //      |  (COLUMNS pivotPropertyDrawList (COMMA pivotPropertyDrawList)*)
  //      |  (ROWS pivotPropertyDrawList (COMMA pivotPropertyDrawList)*)
  //      |  (MEASURES formPropertyDrawUsage (COMMA formPropertyDrawUsage)*)
  //      )+
  private boolean formPivotOptionsDeclaration_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formPivotOptionsDeclaration_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = formPivotOptionsDeclaration_1_0(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!formPivotOptionsDeclaration_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "formPivotOptionsDeclaration_1", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // (groupObjectUsage pivotOptions)
  //      |  (COLUMNS pivotPropertyDrawList (COMMA pivotPropertyDrawList)*)
  //      |  (ROWS pivotPropertyDrawList (COMMA pivotPropertyDrawList)*)
  //      |  (MEASURES formPropertyDrawUsage (COMMA formPropertyDrawUsage)*)
  private boolean formPivotOptionsDeclaration_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formPivotOptionsDeclaration_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = formPivotOptionsDeclaration_1_0_0(b, l + 1);
    if (!r) r = formPivotOptionsDeclaration_1_0_1(b, l + 1);
    if (!r) r = formPivotOptionsDeclaration_1_0_2(b, l + 1);
    if (!r) r = formPivotOptionsDeclaration_1_0_3(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // groupObjectUsage pivotOptions
  private boolean formPivotOptionsDeclaration_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formPivotOptionsDeclaration_1_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = groupObjectUsage(b, l + 1);
    r = r && pivotOptions(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // COLUMNS pivotPropertyDrawList (COMMA pivotPropertyDrawList)*
  private boolean formPivotOptionsDeclaration_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formPivotOptionsDeclaration_1_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, COLUMNS);
    r = r && pivotPropertyDrawList(b, l + 1);
    r = r && formPivotOptionsDeclaration_1_0_1_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (COMMA pivotPropertyDrawList)*
  private boolean formPivotOptionsDeclaration_1_0_1_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formPivotOptionsDeclaration_1_0_1_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!formPivotOptionsDeclaration_1_0_1_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "formPivotOptionsDeclaration_1_0_1_2", c)) break;
    }
    return true;
  }

  // COMMA pivotPropertyDrawList
  private boolean formPivotOptionsDeclaration_1_0_1_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formPivotOptionsDeclaration_1_0_1_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, COMMA);
    r = r && pivotPropertyDrawList(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ROWS pivotPropertyDrawList (COMMA pivotPropertyDrawList)*
  private boolean formPivotOptionsDeclaration_1_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formPivotOptionsDeclaration_1_0_2")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, ROWS);
    r = r && pivotPropertyDrawList(b, l + 1);
    r = r && formPivotOptionsDeclaration_1_0_2_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (COMMA pivotPropertyDrawList)*
  private boolean formPivotOptionsDeclaration_1_0_2_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formPivotOptionsDeclaration_1_0_2_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!formPivotOptionsDeclaration_1_0_2_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "formPivotOptionsDeclaration_1_0_2_2", c)) break;
    }
    return true;
  }

  // COMMA pivotPropertyDrawList
  private boolean formPivotOptionsDeclaration_1_0_2_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formPivotOptionsDeclaration_1_0_2_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, COMMA);
    r = r && pivotPropertyDrawList(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // MEASURES formPropertyDrawUsage (COMMA formPropertyDrawUsage)*
  private boolean formPivotOptionsDeclaration_1_0_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formPivotOptionsDeclaration_1_0_3")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, MEASURES);
    r = r && formPropertyDrawUsage(b, l + 1);
    r = r && formPivotOptionsDeclaration_1_0_3_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (COMMA formPropertyDrawUsage)*
  private boolean formPivotOptionsDeclaration_1_0_3_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formPivotOptionsDeclaration_1_0_3_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!formPivotOptionsDeclaration_1_0_3_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "formPivotOptionsDeclaration_1_0_3_2", c)) break;
    }
    return true;
  }

  // COMMA formPropertyDrawUsage
  private boolean formPivotOptionsDeclaration_1_0_3_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formPivotOptionsDeclaration_1_0_3_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, COMMA);
    r = r && formPropertyDrawUsage(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // PROPERTIES (formMappedNamePropertiesList | formPropertyOptionsList formMappedPropertiesList)
  public boolean formPropertiesList(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formPropertiesList")) return false;
    if (!nextTokenIs(b, PROPERTIES)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, FORM_PROPERTIES_LIST, null);
    r = consumeToken(b, PROPERTIES);
    p = r; // pin = 1
    r = r && formPropertiesList_1(b, l + 1);
    exit_section_(b, l, m, FORM_PROPERTIES_LIST, r, p, null);
    return r || p;
  }

  // formMappedNamePropertiesList | formPropertyOptionsList formMappedPropertiesList
  private boolean formPropertiesList_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formPropertiesList_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = formMappedNamePropertiesList(b, l + 1);
    if (!r) r = formPropertiesList_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // formPropertyOptionsList formMappedPropertiesList
  private boolean formPropertiesList_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formPropertiesList_1_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = formPropertyOptionsList(b, l + 1);
    r = r && formMappedPropertiesList(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // formPropertyDrawNameDecl (COMMA formPropertyDrawNameDecl)*
  public boolean formPropertiesNamesDeclList(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formPropertiesNamesDeclList")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FORM_PROPERTIES_NAMES_DECL_LIST, "<form properties names decl list>");
    r = formPropertyDrawNameDecl(b, l + 1);
    r = r && formPropertiesNamesDeclList_1(b, l + 1);
    exit_section_(b, l, m, FORM_PROPERTIES_NAMES_DECL_LIST, r, false, null);
    return r;
  }

  // (COMMA formPropertyDrawNameDecl)*
  private boolean formPropertiesNamesDeclList_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formPropertiesNamesDeclList_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!formPropertiesNamesDeclList_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "formPropertiesNamesDeclList_1", c)) break;
    }
    return true;
  }

  // COMMA formPropertyDrawNameDecl
  private boolean formPropertiesNamesDeclList_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formPropertiesNamesDeclList_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, COMMA);
    r = r && formPropertyDrawNameDecl(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // <<innerIDCheck>> formUsage POINT formPropertyDrawUsage
  public boolean formPropertyDrawID(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formPropertyDrawID")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, FORM_PROPERTY_DRAW_ID, "<form property draw id>");
    r = innerIDCheck(b, l + 1);
    r = r && formUsage(b, l + 1);
    r = r && consumeToken(b, POINT);
    p = r; // pin = 3
    r = r && formPropertyDrawUsage(b, l + 1);
    exit_section_(b, l, m, FORM_PROPERTY_DRAW_ID, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // (formPropertyDrawObject  
  //                                | (simpleNameOrWithCaption? EQUALS (
  //                                                         // pretty tricky solution, we do lookahead to match formPropertyDrawObject, then we save last token and compare it to last token in formExprDeclaration, if the same - move to formPropertyDrawObject, otherwise map formExprDeclaration, the same approach is used in CHANGECLASS / DELETE  
  //                                                         (<<checkFormExpr>> &((formPropertyDrawObject <<matchedFormDrawObject>>)?) formExprDeclaration <<matchedFormExpr>>) | 
  //                                                         formPropertyDrawObject | 
  //                                                         formActionDeclaration)))
  //                                 formPropertyOptionsList
  public boolean formPropertyDrawMappedDecl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formPropertyDrawMappedDecl")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, FORM_PROPERTY_DRAW_MAPPED_DECL, "<form property draw mapped decl>");
    r = formPropertyDrawMappedDecl_0(b, l + 1);
    p = r; // pin = 1
    r = r && formPropertyOptionsList(b, l + 1);
    exit_section_(b, l, m, FORM_PROPERTY_DRAW_MAPPED_DECL, r, p, null);
    return r || p;
  }

  // formPropertyDrawObject  
  //                                | (simpleNameOrWithCaption? EQUALS (
  //                                                         // pretty tricky solution, we do lookahead to match formPropertyDrawObject, then we save last token and compare it to last token in formExprDeclaration, if the same - move to formPropertyDrawObject, otherwise map formExprDeclaration, the same approach is used in CHANGECLASS / DELETE  
  //                                                         (<<checkFormExpr>> &((formPropertyDrawObject <<matchedFormDrawObject>>)?) formExprDeclaration <<matchedFormExpr>>) | 
  //                                                         formPropertyDrawObject | 
  //                                                         formActionDeclaration))
  private boolean formPropertyDrawMappedDecl_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formPropertyDrawMappedDecl_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = formPropertyDrawObject(b, l + 1);
    if (!r) r = formPropertyDrawMappedDecl_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // simpleNameOrWithCaption? EQUALS (
  //                                                         // pretty tricky solution, we do lookahead to match formPropertyDrawObject, then we save last token and compare it to last token in formExprDeclaration, if the same - move to formPropertyDrawObject, otherwise map formExprDeclaration, the same approach is used in CHANGECLASS / DELETE  
  //                                                         (<<checkFormExpr>> &((formPropertyDrawObject <<matchedFormDrawObject>>)?) formExprDeclaration <<matchedFormExpr>>) | 
  //                                                         formPropertyDrawObject | 
  //                                                         formActionDeclaration)
  private boolean formPropertyDrawMappedDecl_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formPropertyDrawMappedDecl_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = formPropertyDrawMappedDecl_0_1_0(b, l + 1);
    r = r && consumeToken(b, EQUALS);
    r = r && formPropertyDrawMappedDecl_0_1_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // simpleNameOrWithCaption?
  private boolean formPropertyDrawMappedDecl_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formPropertyDrawMappedDecl_0_1_0")) return false;
    simpleNameOrWithCaption(b, l + 1);
    return true;
  }

  // (<<checkFormExpr>> &((formPropertyDrawObject <<matchedFormDrawObject>>)?) formExprDeclaration <<matchedFormExpr>>) | 
  //                                                         formPropertyDrawObject | 
  //                                                         formActionDeclaration
  private boolean formPropertyDrawMappedDecl_0_1_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formPropertyDrawMappedDecl_0_1_2")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = formPropertyDrawMappedDecl_0_1_2_0(b, l + 1);
    if (!r) r = formPropertyDrawObject(b, l + 1);
    if (!r) r = formActionDeclaration(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // <<checkFormExpr>> &((formPropertyDrawObject <<matchedFormDrawObject>>)?) formExprDeclaration <<matchedFormExpr>>
  private boolean formPropertyDrawMappedDecl_0_1_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formPropertyDrawMappedDecl_0_1_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = checkFormExpr(b, l + 1);
    r = r && formPropertyDrawMappedDecl_0_1_2_0_1(b, l + 1);
    r = r && formExprDeclaration(b, l + 1);
    r = r && matchedFormExpr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // &((formPropertyDrawObject <<matchedFormDrawObject>>)?)
  private boolean formPropertyDrawMappedDecl_0_1_2_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formPropertyDrawMappedDecl_0_1_2_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _AND_);
    r = formPropertyDrawMappedDecl_0_1_2_0_1_0(b, l + 1);
    exit_section_(b, l, m, null, r, false, null);
    return r;
  }

  // (formPropertyDrawObject <<matchedFormDrawObject>>)?
  private boolean formPropertyDrawMappedDecl_0_1_2_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formPropertyDrawMappedDecl_0_1_2_0_1_0")) return false;
    formPropertyDrawMappedDecl_0_1_2_0_1_0_0(b, l + 1);
    return true;
  }

  // formPropertyDrawObject <<matchedFormDrawObject>>
  private boolean formPropertyDrawMappedDecl_0_1_2_0_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formPropertyDrawMappedDecl_0_1_2_0_1_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = formPropertyDrawObject(b, l + 1);
    r = r && matchedFormDrawObject(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // (simpleNameOrWithCaption EQUALS)? formPropertyName formPropertyOptionsList
  public boolean formPropertyDrawNameDecl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formPropertyDrawNameDecl")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FORM_PROPERTY_DRAW_NAME_DECL, "<form property draw name decl>");
    r = formPropertyDrawNameDecl_0(b, l + 1);
    r = r && formPropertyName(b, l + 1);
    r = r && formPropertyOptionsList(b, l + 1);
    exit_section_(b, l, m, FORM_PROPERTY_DRAW_NAME_DECL, r, false, null);
    return r;
  }

  // (simpleNameOrWithCaption EQUALS)?
  private boolean formPropertyDrawNameDecl_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formPropertyDrawNameDecl_0")) return false;
    formPropertyDrawNameDecl_0_0(b, l + 1);
    return true;
  }

  // simpleNameOrWithCaption EQUALS
  private boolean formPropertyDrawNameDecl_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formPropertyDrawNameDecl_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = simpleNameOrWithCaption(b, l + 1);
    r = r && consumeToken(b, EQUALS);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // formPropertyName LBRAC objectUsageList RBRAC
  public boolean formPropertyDrawObject(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formPropertyDrawObject")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FORM_PROPERTY_DRAW_OBJECT, "<form property draw object>");
    r = formPropertyName(b, l + 1);
    r = r && consumeToken(b, LBRAC);
    r = r && objectUsageList(b, l + 1);
    r = r && consumeToken(b, RBRAC);
    exit_section_(b, l, m, FORM_PROPERTY_DRAW_OBJECT, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // simpleName
  public boolean formPropertyDrawPropertyUsage(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formPropertyDrawPropertyUsage")) return false;
    if (!nextTokenIs(b, ID)) return false;
    boolean r;
    Marker m = enter_section_(b, l, FORM_PROPERTY_DRAW_PROPERTY_USAGE);
    r = simpleName(b, l + 1);
    exit_section_(b, m, FORM_PROPERTY_DRAW_PROPERTY_USAGE, r);
    return r;
  }

  /* ********************************************************** */
  // (<< notSimpleIdAhead >> formPropertyDrawPropertyUsage LBRAC objectUsageList RBRAC) | aliasUsage
  public boolean formPropertyDrawUsage(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formPropertyDrawUsage")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FORM_PROPERTY_DRAW_USAGE, "<form property draw usage>");
    r = formPropertyDrawUsage_0(b, l + 1);
    if (!r) r = aliasUsage(b, l + 1);
    exit_section_(b, l, m, FORM_PROPERTY_DRAW_USAGE, r, false, null);
    return r;
  }

  // << notSimpleIdAhead >> formPropertyDrawPropertyUsage LBRAC objectUsageList RBRAC
  private boolean formPropertyDrawUsage_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formPropertyDrawUsage_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = notSimpleIdAhead(b, l + 1);
    r = r && formPropertyDrawPropertyUsage(b, l + 1);
    r = r && consumeToken(b, LBRAC);
    r = r && objectUsageList(b, l + 1);
    r = r && consumeToken(b, RBRAC);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // formPropertyDrawUsage (DESC)?
  public boolean formPropertyDrawWithOrder(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formPropertyDrawWithOrder")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FORM_PROPERTY_DRAW_WITH_ORDER, "<form property draw with order>");
    r = formPropertyDrawUsage(b, l + 1);
    r = r && formPropertyDrawWithOrder_1(b, l + 1);
    exit_section_(b, l, m, FORM_PROPERTY_DRAW_WITH_ORDER, r, false, null);
    return r;
  }

  // (DESC)?
  private boolean formPropertyDrawWithOrder_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formPropertyDrawWithOrder_1")) return false;
    consumeToken(b, DESC);
    return true;
  }

  /* ********************************************************** */
  // actionOrPropertyUsage | predefinedFormPropertyName
  public boolean formPropertyName(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formPropertyName")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FORM_PROPERTY_NAME, "<form property name>");
    r = actionOrPropertyUsage(b, l + 1);
    if (!r) r = predefinedFormPropertyName(b, l + 1);
    exit_section_(b, l, m, FORM_PROPERTY_NAME, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // propertyUsage LBRAC objectUsageList RBRAC
  public boolean formPropertyObject(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formPropertyObject")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FORM_PROPERTY_OBJECT, "<form property object>");
    r = propertyUsage(b, l + 1);
    r = r && consumeToken(b, LBRAC);
    r = r && objectUsageList(b, l + 1);
    r = r && consumeToken(b, RBRAC);
    exit_section_(b, l, m, FORM_PROPERTY_OBJECT, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // (    CHANGEABLE | READONLY | SELECTOR
  //                             |    formOptionSession
  //                             |    HINTNOUPDATE
  //                             |    HINTTABLE
  //                             |    OPTIMISTICASYNC
  //                             |    formOptionForce
  //                             |    formOptionCustomView
  //                             |    formOptionToDraw
  //                             |    formOptionEventId
  //                             |    formOptionColumns
  //                             |    formOptionsWithCalcPropertyObject
  //                             |    formOptionsWithOptionalCalcPropertyObject
  //                             |    formOptionsOnEvents
  //                             |    formOptionQuickFilter
  //                             |    formOptionInsertType
  //                             |    ATTR
  //                             |    formInGroup
  //                             |    formExtID | NOEXTID
  //                             |    EXTNULL
  //                             |    ORDER (DESC)?
  //                             |    FILTER
  //                             |    COLUMN
  //                             |    ROW
  //                             |    MEASURE
  //                             |    stickyOption
  //                             |    syncTypeLiteral
  //                             )*
  public boolean formPropertyOptionsList(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formPropertyOptionsList")) return false;
    Marker m = enter_section_(b, l, _NONE_, FORM_PROPERTY_OPTIONS_LIST, "<form property options list>");
    while (true) {
      int c = current_position_(b);
      if (!formPropertyOptionsList_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "formPropertyOptionsList", c)) break;
    }
    exit_section_(b, l, m, FORM_PROPERTY_OPTIONS_LIST, true, false, null);
    return true;
  }

  // CHANGEABLE | READONLY | SELECTOR
  //                             |    formOptionSession
  //                             |    HINTNOUPDATE
  //                             |    HINTTABLE
  //                             |    OPTIMISTICASYNC
  //                             |    formOptionForce
  //                             |    formOptionCustomView
  //                             |    formOptionToDraw
  //                             |    formOptionEventId
  //                             |    formOptionColumns
  //                             |    formOptionsWithCalcPropertyObject
  //                             |    formOptionsWithOptionalCalcPropertyObject
  //                             |    formOptionsOnEvents
  //                             |    formOptionQuickFilter
  //                             |    formOptionInsertType
  //                             |    ATTR
  //                             |    formInGroup
  //                             |    formExtID | NOEXTID
  //                             |    EXTNULL
  //                             |    ORDER (DESC)?
  //                             |    FILTER
  //                             |    COLUMN
  //                             |    ROW
  //                             |    MEASURE
  //                             |    stickyOption
  //                             |    syncTypeLiteral
  private boolean formPropertyOptionsList_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formPropertyOptionsList_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, CHANGEABLE);
    if (!r) r = consumeToken(b, READONLY);
    if (!r) r = consumeToken(b, SELECTOR);
    if (!r) r = formOptionSession(b, l + 1);
    if (!r) r = consumeToken(b, HINTNOUPDATE);
    if (!r) r = consumeToken(b, HINTTABLE);
    if (!r) r = consumeToken(b, OPTIMISTICASYNC);
    if (!r) r = formOptionForce(b, l + 1);
    if (!r) r = formOptionCustomView(b, l + 1);
    if (!r) r = formOptionToDraw(b, l + 1);
    if (!r) r = formOptionEventId(b, l + 1);
    if (!r) r = formOptionColumns(b, l + 1);
    if (!r) r = formOptionsWithCalcPropertyObject(b, l + 1);
    if (!r) r = formOptionsWithOptionalCalcPropertyObject(b, l + 1);
    if (!r) r = formOptionsOnEvents(b, l + 1);
    if (!r) r = formOptionQuickFilter(b, l + 1);
    if (!r) r = formOptionInsertType(b, l + 1);
    if (!r) r = consumeToken(b, ATTR);
    if (!r) r = formInGroup(b, l + 1);
    if (!r) r = formExtID(b, l + 1);
    if (!r) r = consumeToken(b, NOEXTID);
    if (!r) r = consumeToken(b, EXTNULL);
    if (!r) r = formPropertyOptionsList_0_22(b, l + 1);
    if (!r) r = consumeToken(b, FILTER);
    if (!r) r = consumeToken(b, COLUMN);
    if (!r) r = consumeToken(b, ROW);
    if (!r) r = consumeToken(b, MEASURE);
    if (!r) r = stickyOption(b, l + 1);
    if (!r) r = syncTypeLiteral(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ORDER (DESC)?
  private boolean formPropertyOptionsList_0_22(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formPropertyOptionsList_0_22")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, ORDER);
    r = r && formPropertyOptionsList_0_22_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (DESC)?
  private boolean formPropertyOptionsList_0_22_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formPropertyOptionsList_0_22_1")) return false;
    consumeToken(b, DESC);
    return true;
  }

  /* ********************************************************** */
  // NEWSESSION | NESTEDSESSION
  public boolean formSessionScopeClause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formSessionScopeClause")) return false;
    if (!nextTokenIs(b, "<form session scope clause>", NESTEDSESSION, NEWSESSION)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FORM_SESSION_SCOPE_CLAUSE, "<form session scope clause>");
    r = consumeToken(b, NEWSESSION);
    if (!r) r = consumeToken(b, NESTEDSESSION);
    exit_section_(b, l, m, FORM_SESSION_SCOPE_CLAUSE, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // formActionObjectProps
  public boolean formSingleActionObject(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formSingleActionObject")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FORM_SINGLE_ACTION_OBJECT, "<form single action object>");
    r = formActionObjectProps(b, l + 1);
    exit_section_(b, l, m, FORM_SINGLE_ACTION_OBJECT, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // formObjectDeclaration
  public boolean formSingleGroupObjectDeclaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formSingleGroupObjectDeclaration")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FORM_SINGLE_GROUP_OBJECT_DECLARATION, "<form single group object declaration>");
    r = formObjectDeclaration(b, l + 1);
    exit_section_(b, l, m, FORM_SINGLE_GROUP_OBJECT_DECLARATION, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // (	formDecl
  //             		|	extendingFormDeclaration
  // 		            )
  //                     (	formGroupObjectsList
  //                     |	formTreeGroupObjectList
  //                     |	formFiltersList
  //                     |	formPropertiesList
  //                     |	formHintsList
  //                     |	formEventsList
  //                     |	formFilterGroupDeclaration
  //                     |	formExtendFilterGroupDeclaration
  //                     |   userFiltersDeclaration
  //                     |	formOrderByList
  //                     |	formPivotOptionsDeclaration
  //                     |	listFormDeclaration
  //                     |	editFormDeclaration
  //                     |   reportFilesDeclaration
  //                     |   reportSetting
  //                     |   formExtIDSetting
  //                     )*
  //                     emptyStatement
  public boolean formStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formStatement")) return false;
    if (!nextTokenIs(b, "<form statement>", EXTEND, FORM)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, FORM_STATEMENT, "<form statement>");
    r = formStatement_0(b, l + 1);
    p = r; // pin = 1
    r = r && formStatement_1(b, l + 1);
    r = r && emptyStatement(b, l + 1);
    exit_section_(b, l, m, FORM_STATEMENT, r, p, null);
    return r || p;
  }

  // formDecl
  //             		|	extendingFormDeclaration
  private boolean formStatement_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formStatement_0")) return false;
    boolean r;
    r = formDecl(b, l + 1);
    if (!r) r = extendingFormDeclaration(b, l + 1);
    return r;
  }

  // (	formGroupObjectsList
  //                     |	formTreeGroupObjectList
  //                     |	formFiltersList
  //                     |	formPropertiesList
  //                     |	formHintsList
  //                     |	formEventsList
  //                     |	formFilterGroupDeclaration
  //                     |	formExtendFilterGroupDeclaration
  //                     |   userFiltersDeclaration
  //                     |	formOrderByList
  //                     |	formPivotOptionsDeclaration
  //                     |	listFormDeclaration
  //                     |	editFormDeclaration
  //                     |   reportFilesDeclaration
  //                     |   reportSetting
  //                     |   formExtIDSetting
  //                     )*
  private boolean formStatement_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formStatement_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!formStatement_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "formStatement_1", c)) break;
    }
    return true;
  }

  // formGroupObjectsList
  //                     |	formTreeGroupObjectList
  //                     |	formFiltersList
  //                     |	formPropertiesList
  //                     |	formHintsList
  //                     |	formEventsList
  //                     |	formFilterGroupDeclaration
  //                     |	formExtendFilterGroupDeclaration
  //                     |   userFiltersDeclaration
  //                     |	formOrderByList
  //                     |	formPivotOptionsDeclaration
  //                     |	listFormDeclaration
  //                     |	editFormDeclaration
  //                     |   reportFilesDeclaration
  //                     |   reportSetting
  //                     |   formExtIDSetting
  private boolean formStatement_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formStatement_1_0")) return false;
    boolean r;
    r = formGroupObjectsList(b, l + 1);
    if (!r) r = formTreeGroupObjectList(b, l + 1);
    if (!r) r = formFiltersList(b, l + 1);
    if (!r) r = formPropertiesList(b, l + 1);
    if (!r) r = formHintsList(b, l + 1);
    if (!r) r = formEventsList(b, l + 1);
    if (!r) r = formFilterGroupDeclaration(b, l + 1);
    if (!r) r = formExtendFilterGroupDeclaration(b, l + 1);
    if (!r) r = userFiltersDeclaration(b, l + 1);
    if (!r) r = formOrderByList(b, l + 1);
    if (!r) r = formPivotOptionsDeclaration(b, l + 1);
    if (!r) r = listFormDeclaration(b, l + 1);
    if (!r) r = editFormDeclaration(b, l + 1);
    if (!r) r = reportFilesDeclaration(b, l + 1);
    if (!r) r = reportSetting(b, l + 1);
    if (!r) r = formExtIDSetting(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // SUBREPORT formCalcPropertyObject?
  public boolean formSubReport(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formSubReport")) return false;
    if (!nextTokenIs(b, SUBREPORT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, FORM_SUB_REPORT);
    r = consumeToken(b, SUBREPORT);
    r = r && formSubReport_1(b, l + 1);
    exit_section_(b, m, FORM_SUB_REPORT, r);
    return r;
  }

  // formCalcPropertyObject?
  private boolean formSubReport_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formSubReport_1")) return false;
    formCalcPropertyObject(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // formGroupObject (treeGroupParentDeclaration)?
  public boolean formTreeGroupObjectDeclaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formTreeGroupObjectDeclaration")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FORM_TREE_GROUP_OBJECT_DECLARATION, "<form tree group object declaration>");
    r = formGroupObject(b, l + 1);
    r = r && formTreeGroupObjectDeclaration_1(b, l + 1);
    exit_section_(b, l, m, FORM_TREE_GROUP_OBJECT_DECLARATION, r, false, null);
    return r;
  }

  // (treeGroupParentDeclaration)?
  private boolean formTreeGroupObjectDeclaration_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formTreeGroupObjectDeclaration_1")) return false;
    formTreeGroupObjectDeclaration_1_0(b, l + 1);
    return true;
  }

  // (treeGroupParentDeclaration)
  private boolean formTreeGroupObjectDeclaration_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formTreeGroupObjectDeclaration_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = treeGroupParentDeclaration(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // TREE (treeGroupDeclaration)? formTreeGroupObjectDeclaration (COMMA formTreeGroupObjectDeclaration)* formTreeGroupObjectOptions
  public boolean formTreeGroupObjectList(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formTreeGroupObjectList")) return false;
    if (!nextTokenIs(b, TREE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, FORM_TREE_GROUP_OBJECT_LIST, null);
    r = consumeToken(b, TREE);
    p = r; // pin = 1
    r = r && formTreeGroupObjectList_1(b, l + 1);
    r = r && formTreeGroupObjectDeclaration(b, l + 1);
    r = r && formTreeGroupObjectList_3(b, l + 1);
    r = r && formTreeGroupObjectOptions(b, l + 1);
    exit_section_(b, l, m, FORM_TREE_GROUP_OBJECT_LIST, r, p, null);
    return r || p;
  }

  // (treeGroupDeclaration)?
  private boolean formTreeGroupObjectList_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formTreeGroupObjectList_1")) return false;
    formTreeGroupObjectList_1_0(b, l + 1);
    return true;
  }

  // (treeGroupDeclaration)
  private boolean formTreeGroupObjectList_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formTreeGroupObjectList_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = treeGroupDeclaration(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (COMMA formTreeGroupObjectDeclaration)*
  private boolean formTreeGroupObjectList_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formTreeGroupObjectList_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!formTreeGroupObjectList_3_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "formTreeGroupObjectList_3", c)) break;
    }
    return true;
  }

  // COMMA formTreeGroupObjectDeclaration
  private boolean formTreeGroupObjectList_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formTreeGroupObjectList_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, COMMA);
    r = r && formTreeGroupObjectDeclaration(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // (   formGroupObjectRelativePosition
  //                                )*
  public boolean formTreeGroupObjectOptions(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formTreeGroupObjectOptions")) return false;
    Marker m = enter_section_(b, l, _NONE_, FORM_TREE_GROUP_OBJECT_OPTIONS, "<form tree group object options>");
    while (true) {
      int c = current_position_(b);
      if (!formTreeGroupObjectOptions_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "formTreeGroupObjectOptions", c)) break;
    }
    exit_section_(b, l, m, FORM_TREE_GROUP_OBJECT_OPTIONS, true, false, null);
    return true;
  }

  // (   formGroupObjectRelativePosition
  //                                )
  private boolean formTreeGroupObjectOptions_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formTreeGroupObjectOptions_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = formGroupObjectRelativePosition(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // compoundID
  public boolean formUsage(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formUsage")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FORM_USAGE, "<form usage>");
    r = compoundID(b, l + 1);
    exit_section_(b, l, m, FORM_USAGE, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // formUsage
  public boolean formUsageWrapper(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formUsageWrapper")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FORM_USAGE_WRAPPER, "<form usage wrapper>");
    r = formUsage(b, l + 1);
    exit_section_(b, l, m, FORM_USAGE_WRAPPER, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // FORMULA (NULL)? (builtInClassName)? formulaPropertySyntaxList
  public boolean formulaPropertyDefinition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formulaPropertyDefinition")) return false;
    if (!nextTokenIs(b, FORMULA)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, FORMULA_PROPERTY_DEFINITION, null);
    r = consumeToken(b, FORMULA);
    p = r; // pin = 1
    r = r && formulaPropertyDefinition_1(b, l + 1);
    r = r && formulaPropertyDefinition_2(b, l + 1);
    r = r && formulaPropertySyntaxList(b, l + 1);
    exit_section_(b, l, m, FORMULA_PROPERTY_DEFINITION, r, p, null);
    return r || p;
  }

  // (NULL)?
  private boolean formulaPropertyDefinition_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formulaPropertyDefinition_1")) return false;
    consumeToken(b, NULL);
    return true;
  }

  // (builtInClassName)?
  private boolean formulaPropertyDefinition_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formulaPropertyDefinition_2")) return false;
    formulaPropertyDefinition_2_0(b, l + 1);
    return true;
  }

  // (builtInClassName)
  private boolean formulaPropertyDefinition_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formulaPropertyDefinition_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = builtInClassName(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // formulaPropertySyntaxType stringLiteral
  public boolean formulaPropertySyntax(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formulaPropertySyntax")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FORMULA_PROPERTY_SYNTAX, "<formula property syntax>");
    r = formulaPropertySyntaxType(b, l + 1);
    r = r && stringLiteral(b, l + 1);
    exit_section_(b, l, m, FORMULA_PROPERTY_SYNTAX, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // formulaPropertySyntax (COMMA formulaPropertySyntax)*
  public boolean formulaPropertySyntaxList(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formulaPropertySyntaxList")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FORMULA_PROPERTY_SYNTAX_LIST, "<formula property syntax list>");
    r = formulaPropertySyntax(b, l + 1);
    r = r && formulaPropertySyntaxList_1(b, l + 1);
    exit_section_(b, l, m, FORMULA_PROPERTY_SYNTAX_LIST, r, false, null);
    return r;
  }

  // (COMMA formulaPropertySyntax)*
  private boolean formulaPropertySyntaxList_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formulaPropertySyntaxList_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!formulaPropertySyntaxList_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "formulaPropertySyntaxList_1", c)) break;
    }
    return true;
  }

  // COMMA formulaPropertySyntax
  private boolean formulaPropertySyntaxList_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formulaPropertySyntaxList_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, COMMA);
    r = r && formulaPropertySyntax(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // (PG | MS)?
  public boolean formulaPropertySyntaxType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formulaPropertySyntaxType")) return false;
    Marker m = enter_section_(b, l, _NONE_, FORMULA_PROPERTY_SYNTAX_TYPE, "<formula property syntax type>");
    formulaPropertySyntaxType_0(b, l + 1);
    exit_section_(b, l, m, FORMULA_PROPERTY_SYNTAX_TYPE, true, false, null);
    return true;
  }

  // PG | MS
  private boolean formulaPropertySyntaxType_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "formulaPropertySyntaxType_0")) return false;
    boolean r;
    r = consumeToken(b, PG);
    if (!r) r = consumeToken(b, MS);
    return r;
  }

  /* ********************************************************** */
  // ON baseEventNotPE (SINGLE)? (SHOWDEP noContextActionOrPropertyUsage)? actionPropertyDefinitionBody
  public boolean globalEventStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "globalEventStatement")) return false;
    if (!nextTokenIs(b, ON)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, GLOBAL_EVENT_STATEMENT, null);
    r = consumeToken(b, ON);
    p = r; // pin = 1
    r = r && baseEventNotPE(b, l + 1);
    r = r && globalEventStatement_2(b, l + 1);
    r = r && globalEventStatement_3(b, l + 1);
    r = r && actionPropertyDefinitionBody(b, l + 1);
    exit_section_(b, l, m, GLOBAL_EVENT_STATEMENT, r, p, null);
    return r || p;
  }

  // (SINGLE)?
  private boolean globalEventStatement_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "globalEventStatement_2")) return false;
    consumeToken(b, SINGLE);
    return true;
  }

  // (SHOWDEP noContextActionOrPropertyUsage)?
  private boolean globalEventStatement_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "globalEventStatement_3")) return false;
    globalEventStatement_3_0(b, l + 1);
    return true;
  }

  // SHOWDEP noContextActionOrPropertyUsage
  private boolean globalEventStatement_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "globalEventStatement_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, SHOWDEP);
    r = r && noContextActionOrPropertyUsage(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // componentSingleSelectorType
  public boolean globalSingleSelectorType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "globalSingleSelectorType")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, GLOBAL_SINGLE_SELECTOR_TYPE, "<global single selector type>");
    r = componentSingleSelectorType(b, l + 1);
    exit_section_(b, l, m, GLOBAL_SINGLE_SELECTOR_TYPE, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // GROUP groupPropertyBody groupPropertyBy
  boolean groupByBefore(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "groupByBefore")) return false;
    if (!nextTokenIs(b, GROUP)) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, GROUP);
    r = r && groupPropertyBody(b, l + 1);
    r = r && groupPropertyBy(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // groupSelector? (COMMA groupObjectTreeSelector)?
  boolean groupComponentSelector(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "groupComponentSelector")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = groupComponentSelector_0(b, l + 1);
    p = r; // pin = 1
    r = r && groupComponentSelector_1(b, l + 1);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  // groupSelector?
  private boolean groupComponentSelector_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "groupComponentSelector_0")) return false;
    groupSelector(b, l + 1);
    return true;
  }

  // (COMMA groupObjectTreeSelector)?
  private boolean groupComponentSelector_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "groupComponentSelector_1")) return false;
    groupComponentSelector_1_0(b, l + 1);
    return true;
  }

  // COMMA groupObjectTreeSelector
  private boolean groupComponentSelector_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "groupComponentSelector_1_0")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, COMMA);
    p = r; // pin = 1
    r = r && groupObjectTreeSelector(b, l + 1);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // GROUP groupPropertyBody
  public boolean groupExprPropertyDefinition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "groupExprPropertyDefinition")) return false;
    if (!nextTokenIs(b, GROUP)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, GROUP_EXPR_PROPERTY_DEFINITION, null);
    r = consumeToken(b, GROUP);
    p = r; // pin = 1
    r = r && groupPropertyBody(b, l + 1);
    exit_section_(b, l, m, GROUP_EXPR_PROPERTY_DEFINITION, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // TO groupObjectUsage EQUALS propertyUsage (COMMA groupObjectUsage EQUALS propertyUsage)*
  public boolean groupObjectDestination(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "groupObjectDestination")) return false;
    if (!nextTokenIs(b, TO)) return false;
    boolean r;
    Marker m = enter_section_(b, l, GROUP_OBJECT_DESTINATION);
    r = consumeToken(b, TO);
    r = r && groupObjectUsage(b, l + 1);
    r = r && consumeToken(b, EQUALS);
    r = r && propertyUsage(b, l + 1);
    r = r && groupObjectDestination_4(b, l + 1);
    exit_section_(b, m, GROUP_OBJECT_DESTINATION, r);
    return r;
  }

  // (COMMA groupObjectUsage EQUALS propertyUsage)*
  private boolean groupObjectDestination_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "groupObjectDestination_4")) return false;
    while (true) {
      int c = current_position_(b);
      if (!groupObjectDestination_4_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "groupObjectDestination_4", c)) break;
    }
    return true;
  }

  // COMMA groupObjectUsage EQUALS propertyUsage
  private boolean groupObjectDestination_4_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "groupObjectDestination_4_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, COMMA);
    r = r && groupObjectUsage(b, l + 1);
    r = r && consumeToken(b, EQUALS);
    r = r && propertyUsage(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // <<innerIDCheck>> formUsage POINT groupObjectUsage
  public boolean groupObjectID(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "groupObjectID")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, GROUP_OBJECT_ID, "<group object id>");
    r = innerIDCheck(b, l + 1);
    r = r && formUsage(b, l + 1);
    r = r && consumeToken(b, POINT);
    p = r; // pin = 3
    r = r && groupObjectUsage(b, l + 1);
    exit_section_(b, l, m, GROUP_OBJECT_ID, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // (TOP | groupObjectUsage) formCalcPropertyObject
  public boolean groupObjectReportPath(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "groupObjectReportPath")) return false;
    if (!nextTokenIs(b, "<group object report path>", ID, TOP)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, GROUP_OBJECT_REPORT_PATH, "<group object report path>");
    r = groupObjectReportPath_0(b, l + 1);
    r = r && formCalcPropertyObject(b, l + 1);
    exit_section_(b, l, m, GROUP_OBJECT_REPORT_PATH, r, false, null);
    return r;
  }

  // TOP | groupObjectUsage
  private boolean groupObjectReportPath_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "groupObjectReportPath_0")) return false;
    boolean r;
    r = consumeToken(b, TOP);
    if (!r) r = groupObjectUsage(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // groupObjectUsage
  public boolean groupObjectSelector(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "groupObjectSelector")) return false;
    if (!nextTokenIs(b, ID)) return false;
    boolean r;
    Marker m = enter_section_(b, l, GROUP_OBJECT_SELECTOR);
    r = groupObjectUsage(b, l + 1);
    exit_section_(b, m, GROUP_OBJECT_SELECTOR, r);
    return r;
  }

  /* ********************************************************** */
  // TREE treeGroupSelector | groupObjectSelector
  boolean groupObjectTreeSelector(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "groupObjectTreeSelector")) return false;
    if (!nextTokenIs(b, "", ID, TREE)) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = groupObjectTreeSelector_0(b, l + 1);
    if (!r) r = groupObjectSelector(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // TREE treeGroupSelector
  private boolean groupObjectTreeSelector_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "groupObjectTreeSelector_0")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, TREE);
    p = r; // pin = 1
    r = r && treeGroupSelector(b, l + 1);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // componentSingleSelectorType | TOOLBARSYSTEM | FILTERGROUPS | USERFILTER | GRIDBOX | CLASSCHOOSER | GRID | FILTERBOX | FILTERS | FILTERCONTROLS
  public boolean groupObjectTreeSingleSelectorType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "groupObjectTreeSingleSelectorType")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, GROUP_OBJECT_TREE_SINGLE_SELECTOR_TYPE, "<group object tree single selector type>");
    r = componentSingleSelectorType(b, l + 1);
    if (!r) r = consumeToken(b, TOOLBARSYSTEM);
    if (!r) r = consumeToken(b, FILTERGROUPS);
    if (!r) r = consumeToken(b, USERFILTER);
    if (!r) r = consumeToken(b, GRIDBOX);
    if (!r) r = consumeToken(b, CLASSCHOOSER);
    if (!r) r = consumeToken(b, GRID);
    if (!r) r = consumeToken(b, FILTERBOX);
    if (!r) r = consumeToken(b, FILTERS);
    if (!r) r = consumeToken(b, FILTERCONTROLS);
    exit_section_(b, l, m, GROUP_OBJECT_TREE_SINGLE_SELECTOR_TYPE, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // simpleName
  public boolean groupObjectUsage(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "groupObjectUsage")) return false;
    if (!nextTokenIs(b, ID)) return false;
    boolean r;
    Marker m = enter_section_(b, l, GROUP_OBJECT_USAGE);
    r = simpleName(b, l + 1);
    exit_section_(b, m, GROUP_OBJECT_USAGE, r);
    return r;
  }

  /* ********************************************************** */
  // ((groupingType nonEmptyPropertyExpressionList) | (groupingTypeOrder nonEmptyPropertyExpressionList orderPropertyBy))
  //                         (WHERE propertyExpression)?
  public boolean groupPropertyBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "groupPropertyBody")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, GROUP_PROPERTY_BODY, "<group property body>");
    r = groupPropertyBody_0(b, l + 1);
    r = r && groupPropertyBody_1(b, l + 1);
    exit_section_(b, l, m, GROUP_PROPERTY_BODY, r, false, null);
    return r;
  }

  // (groupingType nonEmptyPropertyExpressionList) | (groupingTypeOrder nonEmptyPropertyExpressionList orderPropertyBy)
  private boolean groupPropertyBody_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "groupPropertyBody_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = groupPropertyBody_0_0(b, l + 1);
    if (!r) r = groupPropertyBody_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // groupingType nonEmptyPropertyExpressionList
  private boolean groupPropertyBody_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "groupPropertyBody_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = groupingType(b, l + 1);
    r = r && nonEmptyPropertyExpressionList(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // groupingTypeOrder nonEmptyPropertyExpressionList orderPropertyBy
  private boolean groupPropertyBody_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "groupPropertyBody_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = groupingTypeOrder(b, l + 1);
    r = r && nonEmptyPropertyExpressionList(b, l + 1);
    r = r && orderPropertyBy(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (WHERE propertyExpression)?
  private boolean groupPropertyBody_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "groupPropertyBody_1")) return false;
    groupPropertyBody_1_0(b, l + 1);
    return true;
  }

  // WHERE propertyExpression
  private boolean groupPropertyBody_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "groupPropertyBody_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, WHERE);
    r = r && propertyExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // BY nonEmptyPropertyExpressionList
  public boolean groupPropertyBy(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "groupPropertyBy")) return false;
    if (!nextTokenIs(b, BY)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, GROUP_PROPERTY_BY, null);
    r = consumeToken(b, BY);
    p = r; // pin = 1
    r = r && nonEmptyPropertyExpressionList(b, l + 1);
    exit_section_(b, l, m, GROUP_PROPERTY_BY, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // groupByBefore
  public boolean groupPropertyDefinition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "groupPropertyDefinition")) return false;
    if (!nextTokenIs(b, GROUP)) return false;
    boolean r;
    Marker m = enter_section_(b, l, GROUP_PROPERTY_DEFINITION);
    r = groupByBefore(b, l + 1);
    exit_section_(b, m, GROUP_PROPERTY_DEFINITION, r);
    return r;
  }

  /* ********************************************************** */
  // groupUsage
  public boolean groupSelector(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "groupSelector")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, GROUP_SELECTOR, "<group selector>");
    r = groupUsage(b, l + 1);
    exit_section_(b, l, m, GROUP_SELECTOR, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // GROUP
  public boolean groupSingleSelectorType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "groupSingleSelectorType")) return false;
    if (!nextTokenIs(b, GROUP)) return false;
    boolean r;
    Marker m = enter_section_(b, l, GROUP_SINGLE_SELECTOR_TYPE);
    r = consumeToken(b, GROUP);
    exit_section_(b, m, GROUP_SINGLE_SELECTOR_TYPE, r);
    return r;
  }

  /* ********************************************************** */
  // GROUP nativeLiteral? simpleNameWithCaption formExtID? (COLON groupUsage)? SEMI
  public boolean groupStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "groupStatement")) return false;
    if (!nextTokenIs(b, GROUP)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, GROUP_STATEMENT, null);
    r = consumeToken(b, GROUP);
    r = r && groupStatement_1(b, l + 1);
    p = r; // pin = 2
    r = r && simpleNameWithCaption(b, l + 1);
    r = r && groupStatement_3(b, l + 1);
    r = r && groupStatement_4(b, l + 1);
    r = r && consumeToken(b, SEMI);
    exit_section_(b, l, m, GROUP_STATEMENT, r, p, null);
    return r || p;
  }

  // nativeLiteral?
  private boolean groupStatement_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "groupStatement_1")) return false;
    nativeLiteral(b, l + 1);
    return true;
  }

  // formExtID?
  private boolean groupStatement_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "groupStatement_3")) return false;
    formExtID(b, l + 1);
    return true;
  }

  // (COLON groupUsage)?
  private boolean groupStatement_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "groupStatement_4")) return false;
    groupStatement_4_0(b, l + 1);
    return true;
  }

  // COLON groupUsage
  private boolean groupStatement_4_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "groupStatement_4_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, COLON);
    r = r && groupUsage(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // compoundID
  public boolean groupUsage(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "groupUsage")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, GROUP_USAGE, "<group usage>");
    r = compoundID(b, l + 1);
    exit_section_(b, l, m, GROUP_USAGE, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // SUM | MAX | MIN | AGGR | NAGGR | EQUAL
  public boolean groupingType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "groupingType")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, GROUPING_TYPE, "<grouping type>");
    r = consumeToken(b, SUM);
    if (!r) r = consumeToken(b, MAX);
    if (!r) r = consumeToken(b, MIN);
    if (!r) r = consumeToken(b, AGGR);
    if (!r) r = consumeToken(b, NAGGR);
    if (!r) r = consumeToken(b, EQUAL);
    exit_section_(b, l, m, GROUPING_TYPE, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // CONCAT | LAST
  public boolean groupingTypeOrder(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "groupingTypeOrder")) return false;
    if (!nextTokenIs(b, "<grouping type order>", CONCAT, LAST)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, GROUPING_TYPE_ORDER, "<grouping type order>");
    r = consumeToken(b, CONCAT);
    if (!r) r = consumeToken(b, LAST);
    exit_section_(b, l, m, GROUPING_TYPE_ORDER, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // HEADER | NOHEADER
  public boolean hasHeaderOption(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "hasHeaderOption")) return false;
    if (!nextTokenIs(b, "<has header option>", HEADER, NOHEADER)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, HAS_HEADER_OPTION, "<has header option>");
    r = consumeToken(b, HEADER);
    if (!r) r = consumeToken(b, NOHEADER);
    exit_section_(b, l, m, HAS_HEADER_OPTION, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // propertyUsage
  public boolean headersPropertyUsage(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "headersPropertyUsage")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, HEADERS_PROPERTY_USAGE, "<headers property usage>");
    r = propertyUsage(b, l + 1);
    exit_section_(b, l, m, HEADERS_PROPERTY_USAGE, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // HIDE
  public boolean hideEditKey(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "hideEditKey")) return false;
    if (!nextTokenIs(b, HIDE)) return false;
    boolean r;
    Marker m = enter_section_(b, l, HIDE_EDIT_KEY);
    r = consumeToken(b, HIDE);
    exit_section_(b, m, HIDE_EDIT_KEY, r);
    return r;
  }

  /* ********************************************************** */
  // HINT | NOHINT
  public boolean hintSetting(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "hintSetting")) return false;
    if (!nextTokenIs(b, "<hint setting>", HINT, NOHINT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, HINT_SETTING, "<hint setting>");
    r = consumeToken(b, HINT);
    if (!r) r = consumeToken(b, NOHINT);
    exit_section_(b, l, m, HINT_SETTING, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // IF propertyExpression THEN actionPropertyDefinitionBody (ELSE actionPropertyDefinitionBody)?
  public boolean ifActionPropertyDefinitionBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ifActionPropertyDefinitionBody")) return false;
    if (!nextTokenIs(b, IF)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, IF_ACTION_PROPERTY_DEFINITION_BODY, null);
    r = consumeToken(b, IF);
    p = r; // pin = 1
    r = r && propertyExpression(b, l + 1);
    r = r && consumeToken(b, THEN);
    r = r && actionPropertyDefinitionBody(b, l + 1);
    r = r && ifActionPropertyDefinitionBody_4(b, l + 1);
    exit_section_(b, l, m, IF_ACTION_PROPERTY_DEFINITION_BODY, r, p, null);
    return r || p;
  }

  // (ELSE actionPropertyDefinitionBody)?
  private boolean ifActionPropertyDefinitionBody_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ifActionPropertyDefinitionBody_4")) return false;
    ifActionPropertyDefinitionBody_4_0(b, l + 1);
    return true;
  }

  // ELSE actionPropertyDefinitionBody
  private boolean ifActionPropertyDefinitionBody_4_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ifActionPropertyDefinitionBody_4_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, ELSE);
    r = r && actionPropertyDefinitionBody(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // IF propertyExpression
  //                                     THEN propertyExpression
  //                                     (ELSE propertyExpression)?
  public boolean ifElsePropertyDefinition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ifElsePropertyDefinition")) return false;
    if (!nextTokenIs(b, IF)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, IF_ELSE_PROPERTY_DEFINITION, null);
    r = consumeToken(b, IF);
    p = r; // pin = 1
    r = r && propertyExpression(b, l + 1);
    r = r && consumeToken(b, THEN);
    r = r && propertyExpression(b, l + 1);
    r = r && ifElsePropertyDefinition_4(b, l + 1);
    exit_section_(b, l, m, IF_ELSE_PROPERTY_DEFINITION, r, p, null);
    return r || p;
  }

  // (ELSE propertyExpression)?
  private boolean ifElsePropertyDefinition_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ifElsePropertyDefinition_4")) return false;
    ifElsePropertyDefinition_4_0(b, l + 1);
    return true;
  }

  // ELSE propertyExpression
  private boolean ifElsePropertyDefinition_4_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ifElsePropertyDefinition_4_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, ELSE);
    r = r && propertyExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // orPE (IF orPE)*
  public boolean ifPE(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ifPE")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, IF_PE, "<if pe>");
    r = orPE(b, l + 1);
    r = r && ifPE_1(b, l + 1);
    exit_section_(b, l, m, IF_PE, r, false, null);
    return r;
  }

  // (IF orPE)*
  private boolean ifPE_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ifPE_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!ifPE_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "ifPE_1", c)) break;
    }
    return true;
  }

  // IF orPE
  private boolean ifPE_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ifPE_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, IF);
    r = r && orPE(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // (IMAGE stringLiteral?) | NOIMAGE
  public boolean imageSetting(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "imageSetting")) return false;
    if (!nextTokenIs(b, "<image setting>", IMAGE, NOIMAGE)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, IMAGE_SETTING, "<image setting>");
    r = imageSetting_0(b, l + 1);
    if (!r) r = consumeToken(b, NOIMAGE);
    exit_section_(b, l, m, IMAGE_SETTING, r, false, null);
    return r;
  }

  // IMAGE stringLiteral?
  private boolean imageSetting_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "imageSetting_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, IMAGE);
    r = r && imageSetting_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // stringLiteral?
  private boolean imageSetting_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "imageSetting_0_1")) return false;
    stringLiteral(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // propertyStatement
  public boolean implicitInterfacePropertyStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "implicitInterfacePropertyStatement")) return false;
    if (!nextTokenIs(b, ID)) return false;
    boolean r;
    Marker m = enter_section_(b, l, IMPLICIT_INTERFACE_PROPERTY_STATEMENT);
    r = propertyStatement(b, l + 1);
    exit_section_(b, m, IMPLICIT_INTERFACE_PROPERTY_STATEMENT, r);
    return r;
  }

  /* ********************************************************** */
  // implicitInterfacePropertyStatement
  public boolean implicitValuePropertyStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "implicitValuePropertyStatement")) return false;
    if (!nextTokenIs(b, ID)) return false;
    boolean r;
    Marker m = enter_section_(b, l, IMPLICIT_VALUE_PROPERTY_STATEMENT);
    r = implicitInterfacePropertyStatement(b, l + 1);
    exit_section_(b, m, IMPLICIT_VALUE_PROPERTY_STATEMENT, r);
    return r;
  }

  /* ********************************************************** */
  // IMPORT importActionSourceType? FROM propertyExpression
  //     ((FIELDS (LBRAC classParamDeclareList RBRAC)? nonEmptyImportFieldDefinitions doInputBody) | (TO (LBRAC classNameList RBRAC)? nonEmptyImportPropertyUsageListWithIds (WHERE propertyUsage)?))
  public boolean importActionPropertyDefinitionBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importActionPropertyDefinitionBody")) return false;
    if (!nextTokenIs(b, IMPORT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, IMPORT_ACTION_PROPERTY_DEFINITION_BODY, null);
    r = consumeToken(b, IMPORT);
    p = r; // pin = 1
    r = r && importActionPropertyDefinitionBody_1(b, l + 1);
    r = r && consumeToken(b, FROM);
    r = r && propertyExpression(b, l + 1);
    r = r && importActionPropertyDefinitionBody_4(b, l + 1);
    exit_section_(b, l, m, IMPORT_ACTION_PROPERTY_DEFINITION_BODY, r, p, null);
    return r || p;
  }

  // importActionSourceType?
  private boolean importActionPropertyDefinitionBody_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importActionPropertyDefinitionBody_1")) return false;
    importActionSourceType(b, l + 1);
    return true;
  }

  // (FIELDS (LBRAC classParamDeclareList RBRAC)? nonEmptyImportFieldDefinitions doInputBody) | (TO (LBRAC classNameList RBRAC)? nonEmptyImportPropertyUsageListWithIds (WHERE propertyUsage)?)
  private boolean importActionPropertyDefinitionBody_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importActionPropertyDefinitionBody_4")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = importActionPropertyDefinitionBody_4_0(b, l + 1);
    if (!r) r = importActionPropertyDefinitionBody_4_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // FIELDS (LBRAC classParamDeclareList RBRAC)? nonEmptyImportFieldDefinitions doInputBody
  private boolean importActionPropertyDefinitionBody_4_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importActionPropertyDefinitionBody_4_0")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, FIELDS);
    p = r; // pin = 1
    r = r && importActionPropertyDefinitionBody_4_0_1(b, l + 1);
    r = r && nonEmptyImportFieldDefinitions(b, l + 1);
    r = r && doInputBody(b, l + 1);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  // (LBRAC classParamDeclareList RBRAC)?
  private boolean importActionPropertyDefinitionBody_4_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importActionPropertyDefinitionBody_4_0_1")) return false;
    importActionPropertyDefinitionBody_4_0_1_0(b, l + 1);
    return true;
  }

  // LBRAC classParamDeclareList RBRAC
  private boolean importActionPropertyDefinitionBody_4_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importActionPropertyDefinitionBody_4_0_1_0")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, LBRAC);
    p = r; // pin = 1
    r = r && classParamDeclareList(b, l + 1);
    r = r && consumeToken(b, RBRAC);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  // TO (LBRAC classNameList RBRAC)? nonEmptyImportPropertyUsageListWithIds (WHERE propertyUsage)?
  private boolean importActionPropertyDefinitionBody_4_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importActionPropertyDefinitionBody_4_1")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, TO);
    p = r; // pin = 1
    r = r && importActionPropertyDefinitionBody_4_1_1(b, l + 1);
    r = r && nonEmptyImportPropertyUsageListWithIds(b, l + 1);
    r = r && importActionPropertyDefinitionBody_4_1_3(b, l + 1);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  // (LBRAC classNameList RBRAC)?
  private boolean importActionPropertyDefinitionBody_4_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importActionPropertyDefinitionBody_4_1_1")) return false;
    importActionPropertyDefinitionBody_4_1_1_0(b, l + 1);
    return true;
  }

  // LBRAC classNameList RBRAC
  private boolean importActionPropertyDefinitionBody_4_1_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importActionPropertyDefinitionBody_4_1_1_0")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, LBRAC);
    p = r; // pin = 1
    r = r && classNameList(b, l + 1);
    r = r && consumeToken(b, RBRAC);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  // (WHERE propertyUsage)?
  private boolean importActionPropertyDefinitionBody_4_1_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importActionPropertyDefinitionBody_4_1_3")) return false;
    importActionPropertyDefinitionBody_4_1_3_0(b, l + 1);
    return true;
  }

  // WHERE propertyUsage
  private boolean importActionPropertyDefinitionBody_4_1_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importActionPropertyDefinitionBody_4_1_3_0")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, WHERE);
    p = r; // pin = 1
    r = r && propertyUsage(b, l + 1);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // CSV (stringLiteral)? (HEADER|NOHEADER)? (ESCAPE|NOESCAPE)? (WHERE propertyExpression)? (CHARSET stringLiteral)?
  //                         |   DBF (MEMO propertyExpression)? (WHERE propertyExpression)? (CHARSET stringLiteral)?
  //                         |   XLS (HEADER|NOHEADER)? (SHEET (propertyExpression | ALL))? (WHERE propertyExpression)?
  //                         |   JSON (ROOT propertyExpression)? (CHARSET stringLiteral)?
  //                         |   XML (ROOT propertyExpression)? (ATTR)?
  //                         |   TABLE (WHERE propertyExpression)?
  public boolean importActionSourceType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importActionSourceType")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, IMPORT_ACTION_SOURCE_TYPE, "<import action source type>");
    r = importActionSourceType_0(b, l + 1);
    if (!r) r = importActionSourceType_1(b, l + 1);
    if (!r) r = importActionSourceType_2(b, l + 1);
    if (!r) r = importActionSourceType_3(b, l + 1);
    if (!r) r = importActionSourceType_4(b, l + 1);
    if (!r) r = importActionSourceType_5(b, l + 1);
    exit_section_(b, l, m, IMPORT_ACTION_SOURCE_TYPE, r, false, null);
    return r;
  }

  // CSV (stringLiteral)? (HEADER|NOHEADER)? (ESCAPE|NOESCAPE)? (WHERE propertyExpression)? (CHARSET stringLiteral)?
  private boolean importActionSourceType_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importActionSourceType_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, CSV);
    r = r && importActionSourceType_0_1(b, l + 1);
    r = r && importActionSourceType_0_2(b, l + 1);
    r = r && importActionSourceType_0_3(b, l + 1);
    r = r && importActionSourceType_0_4(b, l + 1);
    r = r && importActionSourceType_0_5(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (stringLiteral)?
  private boolean importActionSourceType_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importActionSourceType_0_1")) return false;
    importActionSourceType_0_1_0(b, l + 1);
    return true;
  }

  // (stringLiteral)
  private boolean importActionSourceType_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importActionSourceType_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = stringLiteral(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (HEADER|NOHEADER)?
  private boolean importActionSourceType_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importActionSourceType_0_2")) return false;
    importActionSourceType_0_2_0(b, l + 1);
    return true;
  }

  // HEADER|NOHEADER
  private boolean importActionSourceType_0_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importActionSourceType_0_2_0")) return false;
    boolean r;
    r = consumeToken(b, HEADER);
    if (!r) r = consumeToken(b, NOHEADER);
    return r;
  }

  // (ESCAPE|NOESCAPE)?
  private boolean importActionSourceType_0_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importActionSourceType_0_3")) return false;
    importActionSourceType_0_3_0(b, l + 1);
    return true;
  }

  // ESCAPE|NOESCAPE
  private boolean importActionSourceType_0_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importActionSourceType_0_3_0")) return false;
    boolean r;
    r = consumeToken(b, ESCAPE);
    if (!r) r = consumeToken(b, NOESCAPE);
    return r;
  }

  // (WHERE propertyExpression)?
  private boolean importActionSourceType_0_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importActionSourceType_0_4")) return false;
    importActionSourceType_0_4_0(b, l + 1);
    return true;
  }

  // WHERE propertyExpression
  private boolean importActionSourceType_0_4_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importActionSourceType_0_4_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, WHERE);
    r = r && propertyExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (CHARSET stringLiteral)?
  private boolean importActionSourceType_0_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importActionSourceType_0_5")) return false;
    importActionSourceType_0_5_0(b, l + 1);
    return true;
  }

  // CHARSET stringLiteral
  private boolean importActionSourceType_0_5_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importActionSourceType_0_5_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, CHARSET);
    r = r && stringLiteral(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // DBF (MEMO propertyExpression)? (WHERE propertyExpression)? (CHARSET stringLiteral)?
  private boolean importActionSourceType_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importActionSourceType_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, DBF);
    r = r && importActionSourceType_1_1(b, l + 1);
    r = r && importActionSourceType_1_2(b, l + 1);
    r = r && importActionSourceType_1_3(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (MEMO propertyExpression)?
  private boolean importActionSourceType_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importActionSourceType_1_1")) return false;
    importActionSourceType_1_1_0(b, l + 1);
    return true;
  }

  // MEMO propertyExpression
  private boolean importActionSourceType_1_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importActionSourceType_1_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, MEMO);
    r = r && propertyExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (WHERE propertyExpression)?
  private boolean importActionSourceType_1_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importActionSourceType_1_2")) return false;
    importActionSourceType_1_2_0(b, l + 1);
    return true;
  }

  // WHERE propertyExpression
  private boolean importActionSourceType_1_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importActionSourceType_1_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, WHERE);
    r = r && propertyExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (CHARSET stringLiteral)?
  private boolean importActionSourceType_1_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importActionSourceType_1_3")) return false;
    importActionSourceType_1_3_0(b, l + 1);
    return true;
  }

  // CHARSET stringLiteral
  private boolean importActionSourceType_1_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importActionSourceType_1_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, CHARSET);
    r = r && stringLiteral(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // XLS (HEADER|NOHEADER)? (SHEET (propertyExpression | ALL))? (WHERE propertyExpression)?
  private boolean importActionSourceType_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importActionSourceType_2")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, XLS);
    r = r && importActionSourceType_2_1(b, l + 1);
    r = r && importActionSourceType_2_2(b, l + 1);
    r = r && importActionSourceType_2_3(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (HEADER|NOHEADER)?
  private boolean importActionSourceType_2_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importActionSourceType_2_1")) return false;
    importActionSourceType_2_1_0(b, l + 1);
    return true;
  }

  // HEADER|NOHEADER
  private boolean importActionSourceType_2_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importActionSourceType_2_1_0")) return false;
    boolean r;
    r = consumeToken(b, HEADER);
    if (!r) r = consumeToken(b, NOHEADER);
    return r;
  }

  // (SHEET (propertyExpression | ALL))?
  private boolean importActionSourceType_2_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importActionSourceType_2_2")) return false;
    importActionSourceType_2_2_0(b, l + 1);
    return true;
  }

  // SHEET (propertyExpression | ALL)
  private boolean importActionSourceType_2_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importActionSourceType_2_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, SHEET);
    r = r && importActionSourceType_2_2_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // propertyExpression | ALL
  private boolean importActionSourceType_2_2_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importActionSourceType_2_2_0_1")) return false;
    boolean r;
    r = propertyExpression(b, l + 1);
    if (!r) r = consumeToken(b, ALL);
    return r;
  }

  // (WHERE propertyExpression)?
  private boolean importActionSourceType_2_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importActionSourceType_2_3")) return false;
    importActionSourceType_2_3_0(b, l + 1);
    return true;
  }

  // WHERE propertyExpression
  private boolean importActionSourceType_2_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importActionSourceType_2_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, WHERE);
    r = r && propertyExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // JSON (ROOT propertyExpression)? (CHARSET stringLiteral)?
  private boolean importActionSourceType_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importActionSourceType_3")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, JSON);
    r = r && importActionSourceType_3_1(b, l + 1);
    r = r && importActionSourceType_3_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (ROOT propertyExpression)?
  private boolean importActionSourceType_3_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importActionSourceType_3_1")) return false;
    importActionSourceType_3_1_0(b, l + 1);
    return true;
  }

  // ROOT propertyExpression
  private boolean importActionSourceType_3_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importActionSourceType_3_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, ROOT);
    r = r && propertyExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (CHARSET stringLiteral)?
  private boolean importActionSourceType_3_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importActionSourceType_3_2")) return false;
    importActionSourceType_3_2_0(b, l + 1);
    return true;
  }

  // CHARSET stringLiteral
  private boolean importActionSourceType_3_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importActionSourceType_3_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, CHARSET);
    r = r && stringLiteral(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // XML (ROOT propertyExpression)? (ATTR)?
  private boolean importActionSourceType_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importActionSourceType_4")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, XML);
    r = r && importActionSourceType_4_1(b, l + 1);
    r = r && importActionSourceType_4_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (ROOT propertyExpression)?
  private boolean importActionSourceType_4_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importActionSourceType_4_1")) return false;
    importActionSourceType_4_1_0(b, l + 1);
    return true;
  }

  // ROOT propertyExpression
  private boolean importActionSourceType_4_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importActionSourceType_4_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, ROOT);
    r = r && propertyExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (ATTR)?
  private boolean importActionSourceType_4_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importActionSourceType_4_2")) return false;
    consumeToken(b, ATTR);
    return true;
  }

  // TABLE (WHERE propertyExpression)?
  private boolean importActionSourceType_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importActionSourceType_5")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, TABLE);
    r = r && importActionSourceType_5_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (WHERE propertyExpression)?
  private boolean importActionSourceType_5_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importActionSourceType_5_1")) return false;
    importActionSourceType_5_1_0(b, l + 1);
    return true;
  }

  // WHERE propertyExpression
  private boolean importActionSourceType_5_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importActionSourceType_5_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, WHERE);
    r = r && propertyExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // simpleName
  public boolean importFieldAlias(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importFieldAlias")) return false;
    if (!nextTokenIs(b, ID)) return false;
    boolean r;
    Marker m = enter_section_(b, l, IMPORT_FIELD_ALIAS);
    r = simpleName(b, l + 1);
    exit_section_(b, m, IMPORT_FIELD_ALIAS, r);
    return r;
  }

  /* ********************************************************** */
  // builtInClassName (importFieldAlias EQUALS)? importFieldName NULL?
  public boolean importFieldDefinition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importFieldDefinition")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, IMPORT_FIELD_DEFINITION, "<import field definition>");
    r = builtInClassName(b, l + 1);
    r = r && importFieldDefinition_1(b, l + 1);
    r = r && importFieldName(b, l + 1);
    r = r && importFieldDefinition_3(b, l + 1);
    exit_section_(b, l, m, IMPORT_FIELD_DEFINITION, r, false, null);
    return r;
  }

  // (importFieldAlias EQUALS)?
  private boolean importFieldDefinition_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importFieldDefinition_1")) return false;
    importFieldDefinition_1_0(b, l + 1);
    return true;
  }

  // importFieldAlias EQUALS
  private boolean importFieldDefinition_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importFieldDefinition_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = importFieldAlias(b, l + 1);
    r = r && consumeToken(b, EQUALS);
    exit_section_(b, m, null, r);
    return r;
  }

  // NULL?
  private boolean importFieldDefinition_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importFieldDefinition_3")) return false;
    consumeToken(b, NULL);
    return true;
  }

  /* ********************************************************** */
  // simpleName | stringLiteral
  public boolean importFieldName(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importFieldName")) return false;
    if (!nextTokenIs(b, "<import field name>", ID, LEX_STRING_LITERAL)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, IMPORT_FIELD_NAME, "<import field name>");
    r = simpleName(b, l + 1);
    if (!r) r = stringLiteral(b, l + 1);
    exit_section_(b, l, m, IMPORT_FIELD_NAME, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // IMPORT formUsage (
  //     (importFormPlainActionSourceType FROM groupObjectUsage EQUALS propertyExpression (COMMA groupObjectUsage EQUALS propertyExpression)*)
  //     |
  //     (importFormHierarchicalActionSourceType? (FROM (propertyExpression))?))
  public boolean importFormActionPropertyDefinitionBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importFormActionPropertyDefinitionBody")) return false;
    if (!nextTokenIs(b, IMPORT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, IMPORT_FORM_ACTION_PROPERTY_DEFINITION_BODY, null);
    r = consumeToken(b, IMPORT);
    r = r && formUsage(b, l + 1);
    p = r; // pin = 2
    r = r && importFormActionPropertyDefinitionBody_2(b, l + 1);
    exit_section_(b, l, m, IMPORT_FORM_ACTION_PROPERTY_DEFINITION_BODY, r, p, null);
    return r || p;
  }

  // (importFormPlainActionSourceType FROM groupObjectUsage EQUALS propertyExpression (COMMA groupObjectUsage EQUALS propertyExpression)*)
  //     |
  //     (importFormHierarchicalActionSourceType? (FROM (propertyExpression))?)
  private boolean importFormActionPropertyDefinitionBody_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importFormActionPropertyDefinitionBody_2")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = importFormActionPropertyDefinitionBody_2_0(b, l + 1);
    if (!r) r = importFormActionPropertyDefinitionBody_2_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // importFormPlainActionSourceType FROM groupObjectUsage EQUALS propertyExpression (COMMA groupObjectUsage EQUALS propertyExpression)*
  private boolean importFormActionPropertyDefinitionBody_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importFormActionPropertyDefinitionBody_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = importFormPlainActionSourceType(b, l + 1);
    r = r && consumeToken(b, FROM);
    r = r && groupObjectUsage(b, l + 1);
    r = r && consumeToken(b, EQUALS);
    r = r && propertyExpression(b, l + 1);
    r = r && importFormActionPropertyDefinitionBody_2_0_5(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (COMMA groupObjectUsage EQUALS propertyExpression)*
  private boolean importFormActionPropertyDefinitionBody_2_0_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importFormActionPropertyDefinitionBody_2_0_5")) return false;
    while (true) {
      int c = current_position_(b);
      if (!importFormActionPropertyDefinitionBody_2_0_5_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "importFormActionPropertyDefinitionBody_2_0_5", c)) break;
    }
    return true;
  }

  // COMMA groupObjectUsage EQUALS propertyExpression
  private boolean importFormActionPropertyDefinitionBody_2_0_5_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importFormActionPropertyDefinitionBody_2_0_5_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, COMMA);
    r = r && groupObjectUsage(b, l + 1);
    r = r && consumeToken(b, EQUALS);
    r = r && propertyExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // importFormHierarchicalActionSourceType? (FROM (propertyExpression))?
  private boolean importFormActionPropertyDefinitionBody_2_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importFormActionPropertyDefinitionBody_2_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = importFormActionPropertyDefinitionBody_2_1_0(b, l + 1);
    r = r && importFormActionPropertyDefinitionBody_2_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // importFormHierarchicalActionSourceType?
  private boolean importFormActionPropertyDefinitionBody_2_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importFormActionPropertyDefinitionBody_2_1_0")) return false;
    importFormHierarchicalActionSourceType(b, l + 1);
    return true;
  }

  // (FROM (propertyExpression))?
  private boolean importFormActionPropertyDefinitionBody_2_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importFormActionPropertyDefinitionBody_2_1_1")) return false;
    importFormActionPropertyDefinitionBody_2_1_1_0(b, l + 1);
    return true;
  }

  // FROM (propertyExpression)
  private boolean importFormActionPropertyDefinitionBody_2_1_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importFormActionPropertyDefinitionBody_2_1_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, FROM);
    r = r && importFormActionPropertyDefinitionBody_2_1_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (propertyExpression)
  private boolean importFormActionPropertyDefinitionBody_2_1_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importFormActionPropertyDefinitionBody_2_1_1_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = propertyExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // JSON (ROOT propertyExpression)? (CHARSET stringLiteral)?
  //                                         |   XML (ROOT propertyExpression)? (CHARSET stringLiteral)?
  public boolean importFormHierarchicalActionSourceType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importFormHierarchicalActionSourceType")) return false;
    if (!nextTokenIs(b, "<import form hierarchical action source type>", JSON, XML)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, IMPORT_FORM_HIERARCHICAL_ACTION_SOURCE_TYPE, "<import form hierarchical action source type>");
    r = importFormHierarchicalActionSourceType_0(b, l + 1);
    if (!r) r = importFormHierarchicalActionSourceType_1(b, l + 1);
    exit_section_(b, l, m, IMPORT_FORM_HIERARCHICAL_ACTION_SOURCE_TYPE, r, false, null);
    return r;
  }

  // JSON (ROOT propertyExpression)? (CHARSET stringLiteral)?
  private boolean importFormHierarchicalActionSourceType_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importFormHierarchicalActionSourceType_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, JSON);
    r = r && importFormHierarchicalActionSourceType_0_1(b, l + 1);
    r = r && importFormHierarchicalActionSourceType_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (ROOT propertyExpression)?
  private boolean importFormHierarchicalActionSourceType_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importFormHierarchicalActionSourceType_0_1")) return false;
    importFormHierarchicalActionSourceType_0_1_0(b, l + 1);
    return true;
  }

  // ROOT propertyExpression
  private boolean importFormHierarchicalActionSourceType_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importFormHierarchicalActionSourceType_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, ROOT);
    r = r && propertyExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (CHARSET stringLiteral)?
  private boolean importFormHierarchicalActionSourceType_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importFormHierarchicalActionSourceType_0_2")) return false;
    importFormHierarchicalActionSourceType_0_2_0(b, l + 1);
    return true;
  }

  // CHARSET stringLiteral
  private boolean importFormHierarchicalActionSourceType_0_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importFormHierarchicalActionSourceType_0_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, CHARSET);
    r = r && stringLiteral(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // XML (ROOT propertyExpression)? (CHARSET stringLiteral)?
  private boolean importFormHierarchicalActionSourceType_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importFormHierarchicalActionSourceType_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, XML);
    r = r && importFormHierarchicalActionSourceType_1_1(b, l + 1);
    r = r && importFormHierarchicalActionSourceType_1_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (ROOT propertyExpression)?
  private boolean importFormHierarchicalActionSourceType_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importFormHierarchicalActionSourceType_1_1")) return false;
    importFormHierarchicalActionSourceType_1_1_0(b, l + 1);
    return true;
  }

  // ROOT propertyExpression
  private boolean importFormHierarchicalActionSourceType_1_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importFormHierarchicalActionSourceType_1_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, ROOT);
    r = r && propertyExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (CHARSET stringLiteral)?
  private boolean importFormHierarchicalActionSourceType_1_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importFormHierarchicalActionSourceType_1_2")) return false;
    importFormHierarchicalActionSourceType_1_2_0(b, l + 1);
    return true;
  }

  // CHARSET stringLiteral
  private boolean importFormHierarchicalActionSourceType_1_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importFormHierarchicalActionSourceType_1_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, CHARSET);
    r = r && stringLiteral(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // CSV (stringLiteral)? (HEADER|NOHEADER)? (ESCAPE|NOESCAPE)? (CHARSET stringLiteral)?
  //                                  |   DBF (CHARSET stringLiteral)?
  //                                  |   XLS (HEADER|NOHEADER)? (SHEET (propertyExpression | ALL))?
  //                                  |   TABLE
  public boolean importFormPlainActionSourceType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importFormPlainActionSourceType")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, IMPORT_FORM_PLAIN_ACTION_SOURCE_TYPE, "<import form plain action source type>");
    r = importFormPlainActionSourceType_0(b, l + 1);
    if (!r) r = importFormPlainActionSourceType_1(b, l + 1);
    if (!r) r = importFormPlainActionSourceType_2(b, l + 1);
    if (!r) r = consumeToken(b, TABLE);
    exit_section_(b, l, m, IMPORT_FORM_PLAIN_ACTION_SOURCE_TYPE, r, false, null);
    return r;
  }

  // CSV (stringLiteral)? (HEADER|NOHEADER)? (ESCAPE|NOESCAPE)? (CHARSET stringLiteral)?
  private boolean importFormPlainActionSourceType_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importFormPlainActionSourceType_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, CSV);
    r = r && importFormPlainActionSourceType_0_1(b, l + 1);
    r = r && importFormPlainActionSourceType_0_2(b, l + 1);
    r = r && importFormPlainActionSourceType_0_3(b, l + 1);
    r = r && importFormPlainActionSourceType_0_4(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (stringLiteral)?
  private boolean importFormPlainActionSourceType_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importFormPlainActionSourceType_0_1")) return false;
    importFormPlainActionSourceType_0_1_0(b, l + 1);
    return true;
  }

  // (stringLiteral)
  private boolean importFormPlainActionSourceType_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importFormPlainActionSourceType_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = stringLiteral(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (HEADER|NOHEADER)?
  private boolean importFormPlainActionSourceType_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importFormPlainActionSourceType_0_2")) return false;
    importFormPlainActionSourceType_0_2_0(b, l + 1);
    return true;
  }

  // HEADER|NOHEADER
  private boolean importFormPlainActionSourceType_0_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importFormPlainActionSourceType_0_2_0")) return false;
    boolean r;
    r = consumeToken(b, HEADER);
    if (!r) r = consumeToken(b, NOHEADER);
    return r;
  }

  // (ESCAPE|NOESCAPE)?
  private boolean importFormPlainActionSourceType_0_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importFormPlainActionSourceType_0_3")) return false;
    importFormPlainActionSourceType_0_3_0(b, l + 1);
    return true;
  }

  // ESCAPE|NOESCAPE
  private boolean importFormPlainActionSourceType_0_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importFormPlainActionSourceType_0_3_0")) return false;
    boolean r;
    r = consumeToken(b, ESCAPE);
    if (!r) r = consumeToken(b, NOESCAPE);
    return r;
  }

  // (CHARSET stringLiteral)?
  private boolean importFormPlainActionSourceType_0_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importFormPlainActionSourceType_0_4")) return false;
    importFormPlainActionSourceType_0_4_0(b, l + 1);
    return true;
  }

  // CHARSET stringLiteral
  private boolean importFormPlainActionSourceType_0_4_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importFormPlainActionSourceType_0_4_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, CHARSET);
    r = r && stringLiteral(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // DBF (CHARSET stringLiteral)?
  private boolean importFormPlainActionSourceType_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importFormPlainActionSourceType_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, DBF);
    r = r && importFormPlainActionSourceType_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (CHARSET stringLiteral)?
  private boolean importFormPlainActionSourceType_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importFormPlainActionSourceType_1_1")) return false;
    importFormPlainActionSourceType_1_1_0(b, l + 1);
    return true;
  }

  // CHARSET stringLiteral
  private boolean importFormPlainActionSourceType_1_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importFormPlainActionSourceType_1_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, CHARSET);
    r = r && stringLiteral(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // XLS (HEADER|NOHEADER)? (SHEET (propertyExpression | ALL))?
  private boolean importFormPlainActionSourceType_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importFormPlainActionSourceType_2")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, XLS);
    r = r && importFormPlainActionSourceType_2_1(b, l + 1);
    r = r && importFormPlainActionSourceType_2_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (HEADER|NOHEADER)?
  private boolean importFormPlainActionSourceType_2_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importFormPlainActionSourceType_2_1")) return false;
    importFormPlainActionSourceType_2_1_0(b, l + 1);
    return true;
  }

  // HEADER|NOHEADER
  private boolean importFormPlainActionSourceType_2_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importFormPlainActionSourceType_2_1_0")) return false;
    boolean r;
    r = consumeToken(b, HEADER);
    if (!r) r = consumeToken(b, NOHEADER);
    return r;
  }

  // (SHEET (propertyExpression | ALL))?
  private boolean importFormPlainActionSourceType_2_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importFormPlainActionSourceType_2_2")) return false;
    importFormPlainActionSourceType_2_2_0(b, l + 1);
    return true;
  }

  // SHEET (propertyExpression | ALL)
  private boolean importFormPlainActionSourceType_2_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importFormPlainActionSourceType_2_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, SHEET);
    r = r && importFormPlainActionSourceType_2_2_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // propertyExpression | ALL
  private boolean importFormPlainActionSourceType_2_2_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importFormPlainActionSourceType_2_2_0_1")) return false;
    boolean r;
    r = propertyExpression(b, l + 1);
    if (!r) r = consumeToken(b, ALL);
    return r;
  }

  /* ********************************************************** */
  // propertyUsage
  public boolean importPropertyUsage(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importPropertyUsage")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, IMPORT_PROPERTY_USAGE, "<import property usage>");
    r = propertyUsage(b, l + 1);
    exit_section_(b, l, m, IMPORT_PROPERTY_USAGE, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // importPropertyUsage (EQUALS (ID | stringLiteral))?
  public boolean importPropertyUsageWithId(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importPropertyUsageWithId")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, IMPORT_PROPERTY_USAGE_WITH_ID, "<import property usage with id>");
    r = importPropertyUsage(b, l + 1);
    r = r && importPropertyUsageWithId_1(b, l + 1);
    exit_section_(b, l, m, IMPORT_PROPERTY_USAGE_WITH_ID, r, false, null);
    return r;
  }

  // (EQUALS (ID | stringLiteral))?
  private boolean importPropertyUsageWithId_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importPropertyUsageWithId_1")) return false;
    importPropertyUsageWithId_1_0(b, l + 1);
    return true;
  }

  // EQUALS (ID | stringLiteral)
  private boolean importPropertyUsageWithId_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importPropertyUsageWithId_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, EQUALS);
    r = r && importPropertyUsageWithId_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ID | stringLiteral
  private boolean importPropertyUsageWithId_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "importPropertyUsageWithId_1_0_1")) return false;
    boolean r;
    r = consumeToken(b, ID);
    if (!r) r = stringLiteral(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // INDEXED stringLiteral? (LIKE | MATCH)?
  public boolean indexSetting(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "indexSetting")) return false;
    if (!nextTokenIs(b, INDEXED)) return false;
    boolean r;
    Marker m = enter_section_(b, l, INDEX_SETTING);
    r = consumeToken(b, INDEXED);
    r = r && indexSetting_1(b, l + 1);
    r = r && indexSetting_2(b, l + 1);
    exit_section_(b, m, INDEX_SETTING, r);
    return r;
  }

  // stringLiteral?
  private boolean indexSetting_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "indexSetting_1")) return false;
    stringLiteral(b, l + 1);
    return true;
  }

  // (LIKE | MATCH)?
  private boolean indexSetting_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "indexSetting_2")) return false;
    indexSetting_2_0(b, l + 1);
    return true;
  }

  // LIKE | MATCH
  private boolean indexSetting_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "indexSetting_2_0")) return false;
    boolean r;
    r = consumeToken(b, LIKE);
    if (!r) r = consumeToken(b, MATCH);
    return r;
  }

  /* ********************************************************** */
  // INDEX ( ((LIKE | MATCH)? nonEmptyMappedPropertyOrSimpleExprParamList)
  //                          | (stringLiteral? (LIKE | MATCH)? nonEmptyMappedPropertyOrSimpleExprParamList)
  //                          )
  //                    SEMI
  public boolean indexStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "indexStatement")) return false;
    if (!nextTokenIs(b, INDEX)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, INDEX_STATEMENT, null);
    r = consumeToken(b, INDEX);
    p = r; // pin = 1
    r = r && indexStatement_1(b, l + 1);
    r = r && consumeToken(b, SEMI);
    exit_section_(b, l, m, INDEX_STATEMENT, r, p, null);
    return r || p;
  }

  // ((LIKE | MATCH)? nonEmptyMappedPropertyOrSimpleExprParamList)
  //                          | (stringLiteral? (LIKE | MATCH)? nonEmptyMappedPropertyOrSimpleExprParamList)
  private boolean indexStatement_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "indexStatement_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = indexStatement_1_0(b, l + 1);
    if (!r) r = indexStatement_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (LIKE | MATCH)? nonEmptyMappedPropertyOrSimpleExprParamList
  private boolean indexStatement_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "indexStatement_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = indexStatement_1_0_0(b, l + 1);
    r = r && nonEmptyMappedPropertyOrSimpleExprParamList(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (LIKE | MATCH)?
  private boolean indexStatement_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "indexStatement_1_0_0")) return false;
    indexStatement_1_0_0_0(b, l + 1);
    return true;
  }

  // LIKE | MATCH
  private boolean indexStatement_1_0_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "indexStatement_1_0_0_0")) return false;
    boolean r;
    r = consumeToken(b, LIKE);
    if (!r) r = consumeToken(b, MATCH);
    return r;
  }

  // stringLiteral? (LIKE | MATCH)? nonEmptyMappedPropertyOrSimpleExprParamList
  private boolean indexStatement_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "indexStatement_1_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = indexStatement_1_1_0(b, l + 1);
    r = r && indexStatement_1_1_1(b, l + 1);
    r = r && nonEmptyMappedPropertyOrSimpleExprParamList(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // stringLiteral?
  private boolean indexStatement_1_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "indexStatement_1_1_0")) return false;
    stringLiteral(b, l + 1);
    return true;
  }

  // (LIKE | MATCH)?
  private boolean indexStatement_1_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "indexStatement_1_1_1")) return false;
    indexStatement_1_1_1_0(b, l + 1);
    return true;
  }

  // LIKE | MATCH
  private boolean indexStatement_1_1_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "indexStatement_1_1_1_0")) return false;
    boolean r;
    r = consumeToken(b, LIKE);
    if (!r) r = consumeToken(b, MATCH);
    return r;
  }

  /* ********************************************************** */
  // (NOINLINE (LBRAC exprParameterUsageList RBRAC)? )? (INLINE)?
  public boolean inlineOption(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "inlineOption")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, INLINE_OPTION, "<inline option>");
    r = inlineOption_0(b, l + 1);
    r = r && inlineOption_1(b, l + 1);
    exit_section_(b, l, m, INLINE_OPTION, r, false, null);
    return r;
  }

  // (NOINLINE (LBRAC exprParameterUsageList RBRAC)? )?
  private boolean inlineOption_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "inlineOption_0")) return false;
    inlineOption_0_0(b, l + 1);
    return true;
  }

  // NOINLINE (LBRAC exprParameterUsageList RBRAC)?
  private boolean inlineOption_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "inlineOption_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, NOINLINE);
    r = r && inlineOption_0_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (LBRAC exprParameterUsageList RBRAC)?
  private boolean inlineOption_0_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "inlineOption_0_0_1")) return false;
    inlineOption_0_0_1_0(b, l + 1);
    return true;
  }

  // LBRAC exprParameterUsageList RBRAC
  private boolean inlineOption_0_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "inlineOption_0_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, LBRAC);
    r = r && exprParameterUsageList(b, l + 1);
    r = r && consumeToken(b, RBRAC);
    exit_section_(b, m, null, r);
    return r;
  }

  // (INLINE)?
  private boolean inlineOption_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "inlineOption_1")) return false;
    consumeToken(b, INLINE);
    return true;
  }

  /* ********************************************************** */
  // constraintStatement
  //                     |	groupStatement
  //                     |	overrideActionStatement // starts with ID
  //                     |	overridePropertyStatement // starts with ID
  //                     |	classStatement
  //                     |	followsStatement // starts with ID
  //                     |	writeWhenStatement // starts with ID
  //                     |	explicitInterfacePropertyStatement
  //                     |   explicitInterfaceActStatement
  //                     |	eventStatement
  //                     |	showDepStatement
  //                     |	globalEventStatement
  //                     |	aspectStatement
  //                     |	tableStatement
  //                     |	loggableStatement
  //                     |	indexStatement
  //                     |	formStatement
  //                     |	designStatement
  //                     |	windowStatement
  //                     |	navigatorStatement
  //                     |	metaCodeStatement // ?
  //                     |   internalStatement
  //                     |	emptyStatement
  //                     |   stubStatement
  boolean innerStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "innerStatement")) return false;
    boolean r;
    r = constraintStatement(b, l + 1);
    if (!r) r = groupStatement(b, l + 1);
    if (!r) r = overrideActionStatement(b, l + 1);
    if (!r) r = overridePropertyStatement(b, l + 1);
    if (!r) r = classStatement(b, l + 1);
    if (!r) r = followsStatement(b, l + 1);
    if (!r) r = writeWhenStatement(b, l + 1);
    if (!r) r = explicitInterfacePropertyStatement(b, l + 1);
    if (!r) r = explicitInterfaceActStatement(b, l + 1);
    if (!r) r = eventStatement(b, l + 1);
    if (!r) r = showDepStatement(b, l + 1);
    if (!r) r = globalEventStatement(b, l + 1);
    if (!r) r = aspectStatement(b, l + 1);
    if (!r) r = tableStatement(b, l + 1);
    if (!r) r = loggableStatement(b, l + 1);
    if (!r) r = indexStatement(b, l + 1);
    if (!r) r = formStatement(b, l + 1);
    if (!r) r = designStatement(b, l + 1);
    if (!r) r = windowStatement(b, l + 1);
    if (!r) r = navigatorStatement(b, l + 1);
    if (!r) r = metaCodeStatement(b, l + 1);
    if (!r) r = internalStatement(b, l + 1);
    if (!r) r = emptyStatement(b, l + 1);
    if (!r) r = stubStatement(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // ID | AFTER | BEFORE | HIDE | INDEX | LOGGABLE | NAVIGATOR | ON | SHOWDEP | WHEN | CONSTRAINT | INTERNAL
  //                             | CLASS | ATSIGN | ATSIGN2 | FORM | GROUP | DESIGN | TABLE | WINDOW | EXTEND | SEMI
  boolean inner_statement_start(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "inner_statement_start")) return false;
    boolean r;
    r = consumeToken(b, ID);
    if (!r) r = consumeToken(b, AFTER);
    if (!r) r = consumeToken(b, BEFORE);
    if (!r) r = consumeToken(b, HIDE);
    if (!r) r = consumeToken(b, INDEX);
    if (!r) r = consumeToken(b, LOGGABLE);
    if (!r) r = consumeToken(b, NAVIGATOR);
    if (!r) r = consumeToken(b, ON);
    if (!r) r = consumeToken(b, SHOWDEP);
    if (!r) r = consumeToken(b, WHEN);
    if (!r) r = consumeToken(b, CONSTRAINT);
    if (!r) r = consumeToken(b, INTERNAL);
    if (!r) r = consumeToken(b, CLASS);
    if (!r) r = consumeToken(b, ATSIGN);
    if (!r) r = consumeToken(b, ATSIGN2);
    if (!r) r = consumeToken(b, FORM);
    if (!r) r = consumeToken(b, GROUP);
    if (!r) r = consumeToken(b, DESIGN);
    if (!r) r = consumeToken(b, TABLE);
    if (!r) r = consumeToken(b, WINDOW);
    if (!r) r = consumeToken(b, EXTEND);
    if (!r) r = consumeToken(b, SEMI);
    return r;
  }

  /* ********************************************************** */
  // INPUT (((paramDeclare EQUALS)? builtInClassName) | (paramDeclare? EQUALS classOrExpression)) (CHANGE (EQUALS propertyExpression)? NOCONSTRAINTFILTER? NOCHANGE?)? changeInputPropertyCustomView? listWhereInputProps contextActions? formSessionScopeClause? staticDestination? doInputBody
  public boolean inputActionPropertyDefinitionBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "inputActionPropertyDefinitionBody")) return false;
    if (!nextTokenIs(b, INPUT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, INPUT_ACTION_PROPERTY_DEFINITION_BODY, null);
    r = consumeToken(b, INPUT);
    p = r; // pin = 1
    r = r && inputActionPropertyDefinitionBody_1(b, l + 1);
    r = r && inputActionPropertyDefinitionBody_2(b, l + 1);
    r = r && inputActionPropertyDefinitionBody_3(b, l + 1);
    r = r && listWhereInputProps(b, l + 1);
    r = r && inputActionPropertyDefinitionBody_5(b, l + 1);
    r = r && inputActionPropertyDefinitionBody_6(b, l + 1);
    r = r && inputActionPropertyDefinitionBody_7(b, l + 1);
    r = r && doInputBody(b, l + 1);
    exit_section_(b, l, m, INPUT_ACTION_PROPERTY_DEFINITION_BODY, r, p, null);
    return r || p;
  }

  // ((paramDeclare EQUALS)? builtInClassName) | (paramDeclare? EQUALS classOrExpression)
  private boolean inputActionPropertyDefinitionBody_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "inputActionPropertyDefinitionBody_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = inputActionPropertyDefinitionBody_1_0(b, l + 1);
    if (!r) r = inputActionPropertyDefinitionBody_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (paramDeclare EQUALS)? builtInClassName
  private boolean inputActionPropertyDefinitionBody_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "inputActionPropertyDefinitionBody_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = inputActionPropertyDefinitionBody_1_0_0(b, l + 1);
    r = r && builtInClassName(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (paramDeclare EQUALS)?
  private boolean inputActionPropertyDefinitionBody_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "inputActionPropertyDefinitionBody_1_0_0")) return false;
    inputActionPropertyDefinitionBody_1_0_0_0(b, l + 1);
    return true;
  }

  // paramDeclare EQUALS
  private boolean inputActionPropertyDefinitionBody_1_0_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "inputActionPropertyDefinitionBody_1_0_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = paramDeclare(b, l + 1);
    r = r && consumeToken(b, EQUALS);
    exit_section_(b, m, null, r);
    return r;
  }

  // paramDeclare? EQUALS classOrExpression
  private boolean inputActionPropertyDefinitionBody_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "inputActionPropertyDefinitionBody_1_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = inputActionPropertyDefinitionBody_1_1_0(b, l + 1);
    r = r && consumeToken(b, EQUALS);
    r = r && classOrExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // paramDeclare?
  private boolean inputActionPropertyDefinitionBody_1_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "inputActionPropertyDefinitionBody_1_1_0")) return false;
    paramDeclare(b, l + 1);
    return true;
  }

  // (CHANGE (EQUALS propertyExpression)? NOCONSTRAINTFILTER? NOCHANGE?)?
  private boolean inputActionPropertyDefinitionBody_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "inputActionPropertyDefinitionBody_2")) return false;
    inputActionPropertyDefinitionBody_2_0(b, l + 1);
    return true;
  }

  // CHANGE (EQUALS propertyExpression)? NOCONSTRAINTFILTER? NOCHANGE?
  private boolean inputActionPropertyDefinitionBody_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "inputActionPropertyDefinitionBody_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, CHANGE);
    r = r && inputActionPropertyDefinitionBody_2_0_1(b, l + 1);
    r = r && inputActionPropertyDefinitionBody_2_0_2(b, l + 1);
    r = r && inputActionPropertyDefinitionBody_2_0_3(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (EQUALS propertyExpression)?
  private boolean inputActionPropertyDefinitionBody_2_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "inputActionPropertyDefinitionBody_2_0_1")) return false;
    inputActionPropertyDefinitionBody_2_0_1_0(b, l + 1);
    return true;
  }

  // EQUALS propertyExpression
  private boolean inputActionPropertyDefinitionBody_2_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "inputActionPropertyDefinitionBody_2_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, EQUALS);
    r = r && propertyExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // NOCONSTRAINTFILTER?
  private boolean inputActionPropertyDefinitionBody_2_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "inputActionPropertyDefinitionBody_2_0_2")) return false;
    consumeToken(b, NOCONSTRAINTFILTER);
    return true;
  }

  // NOCHANGE?
  private boolean inputActionPropertyDefinitionBody_2_0_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "inputActionPropertyDefinitionBody_2_0_3")) return false;
    consumeToken(b, NOCHANGE);
    return true;
  }

  // changeInputPropertyCustomView?
  private boolean inputActionPropertyDefinitionBody_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "inputActionPropertyDefinitionBody_3")) return false;
    changeInputPropertyCustomView(b, l + 1);
    return true;
  }

  // contextActions?
  private boolean inputActionPropertyDefinitionBody_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "inputActionPropertyDefinitionBody_5")) return false;
    contextActions(b, l + 1);
    return true;
  }

  // formSessionScopeClause?
  private boolean inputActionPropertyDefinitionBody_6(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "inputActionPropertyDefinitionBody_6")) return false;
    formSessionScopeClause(b, l + 1);
    return true;
  }

  // staticDestination?
  private boolean inputActionPropertyDefinitionBody_7(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "inputActionPropertyDefinitionBody_7")) return false;
    staticDestination(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // BEFORE | AFTER
  public boolean insertRelativePositionLiteral(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "insertRelativePositionLiteral")) return false;
    if (!nextTokenIs(b, "<insert relative position literal>", AFTER, BEFORE)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, INSERT_RELATIVE_POSITION_LITERAL, "<insert relative position literal>");
    r = consumeToken(b, BEFORE);
    if (!r) r = consumeToken(b, AFTER);
    exit_section_(b, l, m, INSERT_RELATIVE_POSITION_LITERAL, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // (MINUS)? uintLiteral
  public boolean intLiteral(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "intLiteral")) return false;
    if (!nextTokenIs(b, "<int literal>", LEX_UINT_LITERAL, MINUS)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, INT_LITERAL, "<int literal>");
    r = intLiteral_0(b, l + 1);
    r = r && uintLiteral(b, l + 1);
    exit_section_(b, l, m, INT_LITERAL, r, false, null);
    return r;
  }

  // (MINUS)?
  private boolean intLiteral_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "intLiteral_0")) return false;
    consumeToken(b, MINUS);
    return true;
  }

  /* ********************************************************** */
  // ACTION nonEmptyNoContextActionUsageList
  public boolean internalAction(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "internalAction")) return false;
    if (!nextTokenIs(b, ACTION)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, INTERNAL_ACTION, null);
    r = consumeToken(b, ACTION);
    p = r; // pin = 1
    r = r && nonEmptyNoContextActionUsageList(b, l + 1);
    exit_section_(b, l, m, INTERNAL_ACTION, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // INTERNAL (DB | (CLIENT syncTypeLiteral?)) propertyExpression (PARAMS nonEmptyPropertyExpressionList)? (TO nonEmptyNoParamsPropertyUsageList)?
  public boolean internalActionPropertyDefinitionBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "internalActionPropertyDefinitionBody")) return false;
    if (!nextTokenIs(b, INTERNAL)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, INTERNAL_ACTION_PROPERTY_DEFINITION_BODY, null);
    r = consumeToken(b, INTERNAL);
    p = r; // pin = 1
    r = r && internalActionPropertyDefinitionBody_1(b, l + 1);
    r = r && propertyExpression(b, l + 1);
    r = r && internalActionPropertyDefinitionBody_3(b, l + 1);
    r = r && internalActionPropertyDefinitionBody_4(b, l + 1);
    exit_section_(b, l, m, INTERNAL_ACTION_PROPERTY_DEFINITION_BODY, r, p, null);
    return r || p;
  }

  // DB | (CLIENT syncTypeLiteral?)
  private boolean internalActionPropertyDefinitionBody_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "internalActionPropertyDefinitionBody_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, DB);
    if (!r) r = internalActionPropertyDefinitionBody_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // CLIENT syncTypeLiteral?
  private boolean internalActionPropertyDefinitionBody_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "internalActionPropertyDefinitionBody_1_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, CLIENT);
    r = r && internalActionPropertyDefinitionBody_1_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // syncTypeLiteral?
  private boolean internalActionPropertyDefinitionBody_1_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "internalActionPropertyDefinitionBody_1_1_1")) return false;
    syncTypeLiteral(b, l + 1);
    return true;
  }

  // (PARAMS nonEmptyPropertyExpressionList)?
  private boolean internalActionPropertyDefinitionBody_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "internalActionPropertyDefinitionBody_3")) return false;
    internalActionPropertyDefinitionBody_3_0(b, l + 1);
    return true;
  }

  // PARAMS nonEmptyPropertyExpressionList
  private boolean internalActionPropertyDefinitionBody_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "internalActionPropertyDefinitionBody_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, PARAMS);
    r = r && nonEmptyPropertyExpressionList(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (TO nonEmptyNoParamsPropertyUsageList)?
  private boolean internalActionPropertyDefinitionBody_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "internalActionPropertyDefinitionBody_4")) return false;
    internalActionPropertyDefinitionBody_4_0(b, l + 1);
    return true;
  }

  // TO nonEmptyNoParamsPropertyUsageList
  private boolean internalActionPropertyDefinitionBody_4_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "internalActionPropertyDefinitionBody_4_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, TO);
    r = r && nonEmptyNoParamsPropertyUsageList(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // CLASS nonEmptyClassNameList
  public boolean internalClass(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "internalClass")) return false;
    if (!nextTokenIs(b, CLASS)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, INTERNAL_CLASS, null);
    r = consumeToken(b, CLASS);
    p = r; // pin = 1
    r = r && nonEmptyClassNameList(b, l + 1);
    exit_section_(b, l, m, INTERNAL_CLASS, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // OBJECT formUsage objectUsage
  public boolean internalFormObject(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "internalFormObject")) return false;
    if (!nextTokenIs(b, OBJECT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, INTERNAL_FORM_OBJECT, null);
    r = consumeToken(b, OBJECT);
    p = r; // pin = 1
    r = r && formUsage(b, l + 1);
    r = r && objectUsage(b, l + 1);
    exit_section_(b, l, m, INTERNAL_FORM_OBJECT, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // MODULE nonEmptyModuleUsageList
  public boolean internalModule(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "internalModule")) return false;
    if (!nextTokenIs(b, MODULE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, INTERNAL_MODULE, null);
    r = consumeToken(b, MODULE);
    p = r; // pin = 1
    r = r && nonEmptyModuleUsageList(b, l + 1);
    exit_section_(b, l, m, INTERNAL_MODULE, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // PROPERTY nonEmptyNoContextPropertyUsageList
  public boolean internalProperty(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "internalProperty")) return false;
    if (!nextTokenIs(b, PROPERTY)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, INTERNAL_PROPERTY, null);
    r = consumeToken(b, PROPERTY);
    p = r; // pin = 1
    r = r && nonEmptyNoContextPropertyUsageList(b, l + 1);
    exit_section_(b, l, m, INTERNAL_PROPERTY, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // PROPERTYDRAW formUsage formPropertyDrawUsage
  public boolean internalPropertyDraw(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "internalPropertyDraw")) return false;
    if (!nextTokenIs(b, PROPERTYDRAW)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, INTERNAL_PROPERTY_DRAW, null);
    r = consumeToken(b, PROPERTYDRAW);
    p = r; // pin = 1
    r = r && formUsage(b, l + 1);
    r = r && formPropertyDrawUsage(b, l + 1);
    exit_section_(b, l, m, INTERNAL_PROPERTY_DRAW, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // INTERNAL (internalProperty | internalAction | internalClass | internalModule | internalPropertyDraw | internalFormObject) SEMI
  public boolean internalStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "internalStatement")) return false;
    if (!nextTokenIs(b, INTERNAL)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, INTERNAL_STATEMENT, null);
    r = consumeToken(b, INTERNAL);
    p = r; // pin = 1
    r = r && internalStatement_1(b, l + 1);
    r = r && consumeToken(b, SEMI);
    exit_section_(b, l, m, INTERNAL_STATEMENT, r, p, null);
    return r || p;
  }

  // internalProperty | internalAction | internalClass | internalModule | internalPropertyDraw | internalFormObject
  private boolean internalStatement_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "internalStatement_1")) return false;
    boolean r;
    r = internalProperty(b, l + 1);
    if (!r) r = internalAction(b, l + 1);
    if (!r) r = internalClass(b, l + 1);
    if (!r) r = internalModule(b, l + 1);
    if (!r) r = internalPropertyDraw(b, l + 1);
    if (!r) r = internalFormObject(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // stringLiteral
  public boolean javaClassStringUsage(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "javaClassStringUsage")) return false;
    if (!nextTokenIs(b, "<java class string usage>", ID, LEX_STRING_LITERAL)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, JAVA_CLASS_STRING_USAGE, "<java class string usage>");
    r = stringLiteral(b, l + 1);
    exit_section_(b, l, m, JAVA_CLASS_STRING_USAGE, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // propertyExprObject LBRAC propertyExpressionList RBRAC
  boolean joinExprObjectPropertyDefinition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "joinExprObjectPropertyDefinition")) return false;
    if (!nextTokenIs(b, LSQBR)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = propertyExprObject(b, l + 1);
    p = r; // pin = 1
    r = r && consumeToken(b, LBRAC);
    r = r && propertyExpressionList(b, l + 1);
    r = r && consumeToken(b, RBRAC);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // JOIN? (joinUsagePropertyDefinition | joinExprObjectPropertyDefinition)
  public boolean joinPropertyDefinition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "joinPropertyDefinition")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, JOIN_PROPERTY_DEFINITION, "<join property definition>");
    r = joinPropertyDefinition_0(b, l + 1);
    r = r && joinPropertyDefinition_1(b, l + 1);
    exit_section_(b, l, m, JOIN_PROPERTY_DEFINITION, r, false, null);
    return r;
  }

  // JOIN?
  private boolean joinPropertyDefinition_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "joinPropertyDefinition_0")) return false;
    consumeToken(b, JOIN);
    return true;
  }

  // joinUsagePropertyDefinition | joinExprObjectPropertyDefinition
  private boolean joinPropertyDefinition_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "joinPropertyDefinition_1")) return false;
    boolean r;
    r = joinUsagePropertyDefinition(b, l + 1);
    if (!r) r = joinExprObjectPropertyDefinition(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // propertyUsage LBRAC propertyExpressionList RBRAC
  boolean joinUsagePropertyDefinition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "joinUsagePropertyDefinition")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = propertyUsage(b, l + 1);
    r = r && consumeToken(b, LBRAC);
    p = r; // pin = 2
    r = r && propertyExpressionList(b, l + 1);
    r = r && consumeToken(b, RBRAC);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // stringLiteral
  public boolean jsStringUsage(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "jsStringUsage")) return false;
    if (!nextTokenIs(b, "<js string usage>", ID, LEX_STRING_LITERAL)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, JS_STRING_USAGE, "<js string usage>");
    r = stringLiteral(b, l + 1);
    exit_section_(b, l, m, JS_STRING_USAGE, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // (JSON | JSONTEXT) LBRAC mappedForm contextFiltersClause? RBRAC
  public boolean jsonFormPropertyDefinition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "jsonFormPropertyDefinition")) return false;
    if (!nextTokenIs(b, "<json form property definition>", JSON, JSONTEXT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, JSON_FORM_PROPERTY_DEFINITION, "<json form property definition>");
    r = jsonFormPropertyDefinition_0(b, l + 1);
    r = r && consumeToken(b, LBRAC);
    r = r && mappedForm(b, l + 1);
    p = r; // pin = 3
    r = r && jsonFormPropertyDefinition_3(b, l + 1);
    r = r && consumeToken(b, RBRAC);
    exit_section_(b, l, m, JSON_FORM_PROPERTY_DEFINITION, r, p, null);
    return r || p;
  }

  // JSON | JSONTEXT
  private boolean jsonFormPropertyDefinition_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "jsonFormPropertyDefinition_0")) return false;
    boolean r;
    r = consumeToken(b, JSON);
    if (!r) r = consumeToken(b, JSONTEXT);
    return r;
  }

  // contextFiltersClause?
  private boolean jsonFormPropertyDefinition_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "jsonFormPropertyDefinition_3")) return false;
    contextFiltersClause(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // (JSON | JSONTEXT) FROM nonEmptyAliasedPropertyExpressionList wherePropertyExpression? (ORDER propertyExpressionWithOrder (COMMA propertyExpressionWithOrder)*)?
  public boolean jsonPropertyDefinition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "jsonPropertyDefinition")) return false;
    if (!nextTokenIs(b, "<json property definition>", JSON, JSONTEXT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, JSON_PROPERTY_DEFINITION, "<json property definition>");
    r = jsonPropertyDefinition_0(b, l + 1);
    r = r && consumeToken(b, FROM);
    r = r && nonEmptyAliasedPropertyExpressionList(b, l + 1);
    p = r; // pin = 3
    r = r && jsonPropertyDefinition_3(b, l + 1);
    r = r && jsonPropertyDefinition_4(b, l + 1);
    exit_section_(b, l, m, JSON_PROPERTY_DEFINITION, r, p, null);
    return r || p;
  }

  // JSON | JSONTEXT
  private boolean jsonPropertyDefinition_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "jsonPropertyDefinition_0")) return false;
    boolean r;
    r = consumeToken(b, JSON);
    if (!r) r = consumeToken(b, JSONTEXT);
    return r;
  }

  // wherePropertyExpression?
  private boolean jsonPropertyDefinition_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "jsonPropertyDefinition_3")) return false;
    wherePropertyExpression(b, l + 1);
    return true;
  }

  // (ORDER propertyExpressionWithOrder (COMMA propertyExpressionWithOrder)*)?
  private boolean jsonPropertyDefinition_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "jsonPropertyDefinition_4")) return false;
    jsonPropertyDefinition_4_0(b, l + 1);
    return true;
  }

  // ORDER propertyExpressionWithOrder (COMMA propertyExpressionWithOrder)*
  private boolean jsonPropertyDefinition_4_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "jsonPropertyDefinition_4_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, ORDER);
    r = r && propertyExpressionWithOrder(b, l + 1);
    r = r && jsonPropertyDefinition_4_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (COMMA propertyExpressionWithOrder)*
  private boolean jsonPropertyDefinition_4_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "jsonPropertyDefinition_4_0_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!jsonPropertyDefinition_4_0_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "jsonPropertyDefinition_4_0_2", c)) break;
    }
    return true;
  }

  // COMMA propertyExpressionWithOrder
  private boolean jsonPropertyDefinition_4_0_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "jsonPropertyDefinition_4_0_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, COMMA);
    r = r && propertyExpressionWithOrder(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // KEYPRESS stringLiteral
  public boolean keyPressedEventType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keyPressedEventType")) return false;
    if (!nextTokenIs(b, KEYPRESS)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, KEY_PRESSED_EVENT_TYPE, null);
    r = consumeToken(b, KEYPRESS);
    p = r; // pin = 1
    r = r && stringLiteral(b, l + 1);
    exit_section_(b, l, m, KEY_PRESSED_EVENT_TYPE, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // metaDeclStatement*
  public boolean lazyMetaDeclStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lazyMetaDeclStatement")) return false;
    Marker m = enter_section_(b, l, _NONE_, LAZY_META_DECL_STATEMENT, "<lazy meta decl statement>");
    while (true) {
      int c = current_position_(b);
      if (!metaDeclStatement(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "lazyMetaDeclStatement", c)) break;
    }
    exit_section_(b, l, m, LAZY_META_DECL_STATEMENT, true, false, null);
    return true;
  }

  /* ********************************************************** */
  // metaStatement*
  public boolean lazyMetaStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lazyMetaStatement")) return false;
    Marker m = enter_section_(b, l, _NONE_, LAZY_META_STATEMENT, "<lazy meta statement>");
    while (true) {
      int c = current_position_(b);
      if (!metaStatement(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "lazyMetaStatement", c)) break;
    }
    exit_section_(b, l, m, LAZY_META_STATEMENT, true, false, null);
    return true;
  }

  /* ********************************************************** */
  // scriptStatement*
  public boolean lazyScriptStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lazyScriptStatement")) return false;
    Marker m = enter_section_(b, l, _NONE_, LAZY_SCRIPT_STATEMENT, "<lazy script statement>");
    while (true) {
      int c = current_position_(b);
      if (!scriptStatement(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "lazyScriptStatement", c)) break;
    }
    exit_section_(b, l, m, LAZY_SCRIPT_STATEMENT, true, false, null);
    return true;
  }

  /* ********************************************************** */
  // (leafExtendContextActionPDB | leafKeepContextActionPDB) SEMI | SEMI
  boolean leafActionPDB(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "leafActionPDB")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = leafActionPDB_0(b, l + 1);
    if (!r) r = consumeToken(b, SEMI);
    exit_section_(b, m, null, r);
    return r;
  }

  // (leafExtendContextActionPDB | leafKeepContextActionPDB) SEMI
  private boolean leafActionPDB_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "leafActionPDB_0")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = leafActionPDB_0_0(b, l + 1);
    p = r; // pin = 1
    r = r && consumeToken(b, SEMI);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  // leafExtendContextActionPDB | leafKeepContextActionPDB
  private boolean leafActionPDB_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "leafActionPDB_0_0")) return false;
    boolean r;
    r = leafExtendContextActionPDB(b, l + 1);
    if (!r) r = leafKeepContextActionPDB(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // assignActionPropertyDefinitionBody // should be before exec
  //                                         |	    changeClassActionPropertyDefinitionBody
  //                                         |	    deleteActionPropertyDefinitionBody
  //                                         |	    newWhereActionPropertyDefinitionBody // should be before new
  //                                         |       recalculateActionPropertyDefinitionBody
  boolean leafExtendContextActionPDB(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "leafExtendContextActionPDB")) return false;
    boolean r;
    r = assignActionPropertyDefinitionBody(b, l + 1);
    if (!r) r = changeClassActionPropertyDefinitionBody(b, l + 1);
    if (!r) r = deleteActionPropertyDefinitionBody(b, l + 1);
    if (!r) r = newWhereActionPropertyDefinitionBody(b, l + 1);
    if (!r) r = recalculateActionPropertyDefinitionBody(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // execActionPropertyDefinitionBody // should be after assign	
  //                                         |	    terminalFlowActionPropertyDefinitionBody
  //                                       	|  	    cancelActionPropertyDefinitionBody
  //                                       	|	    formActionPropertyDefinitionBody
  //                                       	|	    printActionPropertyDefinitionBody
  //                                       	|	    exportActionPropertyDefinitionBody
  //                                       	|       exportDataActionPropertyDefinitionBody // should be after export
  //                                       	|	    messageActionPropertyDefinitionBody
  //                                       	|	    asyncUpdateActionPropertyDefinitionBody
  //                                       	|	    seekObjectActionPropertyDefinitionBody
  //                                       	|	    expandGroupObjectActionPropertyDefinitionBody
  //                                       	|	    collapseGroupObjectActionPropertyDefinitionBody
  //                                       	|       orderActionPropertyDefinitionBody
  //                                       	|       readOrderActionPropertyDefinitionBody
  //                                       	|       filterActionPropertyDefinitionBody
  //                                       	|       readFilterActionPropertyDefinitionBody
  //                                       	|	    emailActionPropertyDefinitionBody
  //                                       	|	    evalActionPropertyDefinitionBody
  //                                       	|	    drillDownActionPropertyDefinitionBody
  //                                       	|	    readActionPropertyDefinitionBody
  //                                       	|	    writeActionPropertyDefinitionBody
  //                                       	|	    importFormActionPropertyDefinitionBody
  //                                       	|	    activeFormActionPropertyDefinitionBody
  //                                       	|	    activateActionPropertyDefinitionBody
  //                                       	|	    closeFormActionPropertyDefinitionBody
  //                                       	|       expandCollapseActionPropertyDefinitionBody
  //                                       	|       internalActionPropertyDefinitionBody
  //                                       	|       externalActionPropertyDefinitionBody
  boolean leafKeepContextActionPDB(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "leafKeepContextActionPDB")) return false;
    boolean r;
    r = execActionPropertyDefinitionBody(b, l + 1);
    if (!r) r = terminalFlowActionPropertyDefinitionBody(b, l + 1);
    if (!r) r = cancelActionPropertyDefinitionBody(b, l + 1);
    if (!r) r = formActionPropertyDefinitionBody(b, l + 1);
    if (!r) r = printActionPropertyDefinitionBody(b, l + 1);
    if (!r) r = exportActionPropertyDefinitionBody(b, l + 1);
    if (!r) r = exportDataActionPropertyDefinitionBody(b, l + 1);
    if (!r) r = messageActionPropertyDefinitionBody(b, l + 1);
    if (!r) r = asyncUpdateActionPropertyDefinitionBody(b, l + 1);
    if (!r) r = seekObjectActionPropertyDefinitionBody(b, l + 1);
    if (!r) r = expandGroupObjectActionPropertyDefinitionBody(b, l + 1);
    if (!r) r = collapseGroupObjectActionPropertyDefinitionBody(b, l + 1);
    if (!r) r = orderActionPropertyDefinitionBody(b, l + 1);
    if (!r) r = readOrderActionPropertyDefinitionBody(b, l + 1);
    if (!r) r = filterActionPropertyDefinitionBody(b, l + 1);
    if (!r) r = readFilterActionPropertyDefinitionBody(b, l + 1);
    if (!r) r = emailActionPropertyDefinitionBody(b, l + 1);
    if (!r) r = evalActionPropertyDefinitionBody(b, l + 1);
    if (!r) r = drillDownActionPropertyDefinitionBody(b, l + 1);
    if (!r) r = readActionPropertyDefinitionBody(b, l + 1);
    if (!r) r = writeActionPropertyDefinitionBody(b, l + 1);
    if (!r) r = importFormActionPropertyDefinitionBody(b, l + 1);
    if (!r) r = activeFormActionPropertyDefinitionBody(b, l + 1);
    if (!r) r = activateActionPropertyDefinitionBody(b, l + 1);
    if (!r) r = closeFormActionPropertyDefinitionBody(b, l + 1);
    if (!r) r = expandCollapseActionPropertyDefinitionBody(b, l + 1);
    if (!r) r = internalActionPropertyDefinitionBody(b, l + 1);
    if (!r) r = externalActionPropertyDefinitionBody(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // additiveORPE ((LIKE | MATCH) additiveORPE)?
  public boolean likePE(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "likePE")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, LIKE_PE, "<like pe>");
    r = additiveORPE(b, l + 1);
    r = r && likePE_1(b, l + 1);
    exit_section_(b, l, m, LIKE_PE, r, false, null);
    return r;
  }

  // ((LIKE | MATCH) additiveORPE)?
  private boolean likePE_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "likePE_1")) return false;
    likePE_1_0(b, l + 1);
    return true;
  }

  // (LIKE | MATCH) additiveORPE
  private boolean likePE_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "likePE_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = likePE_1_0_0(b, l + 1);
    r = r && additiveORPE(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // LIKE | MATCH
  private boolean likePE_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "likePE_1_0_0")) return false;
    boolean r;
    r = consumeToken(b, LIKE);
    if (!r) r = consumeToken(b, MATCH);
    return r;
  }

  /* ********************************************************** */
  // LBRACE
  // 			                            listActionStatement*
  // 		                                RBRACE
  public boolean listActionPropertyDefinitionBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "listActionPropertyDefinitionBody")) return false;
    if (!nextTokenIs(b, LBRACE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, LIST_ACTION_PROPERTY_DEFINITION_BODY, null);
    r = consumeToken(b, LBRACE);
    p = r; // pin = 1
    r = r && listActionPropertyDefinitionBody_1(b, l + 1);
    r = r && consumeToken(b, RBRACE);
    exit_section_(b, l, m, LIST_ACTION_PROPERTY_DEFINITION_BODY, r, p, null);
    return r || p;
  }

  // listActionStatement*
  private boolean listActionPropertyDefinitionBody_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "listActionPropertyDefinitionBody_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!listActionStatement(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "listActionPropertyDefinitionBody_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // actionPropertyDefinitionBody //  !} => ;
  //                                 |   localDataPropertyDefinition SEMI
  boolean listActionStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "listActionStatement")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_);
    r = actionPropertyDefinitionBody(b, l + 1);
    if (!r) r = listActionStatement_1(b, l + 1);
    exit_section_(b, l, m, null, r, false, this::list_action_statement_recover);
    return r;
  }

  // localDataPropertyDefinition SEMI
  private boolean listActionStatement_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "listActionStatement_1")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = localDataPropertyDefinition(b, l + 1);
    p = r; // pin = 1
    r = r && consumeToken(b, SEMI);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // LIST customClassUsage OBJECT objectUsage
  public boolean listFormDeclaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "listFormDeclaration")) return false;
    if (!nextTokenIs(b, LIST)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, LIST_FORM_DECLARATION, null);
    r = consumeToken(b, LIST);
    p = r; // pin = 1
    r = r && customClassUsage(b, l + 1);
    r = r && consumeToken(b, OBJECT);
    r = r && objectUsage(b, l + 1);
    exit_section_(b, l, m, LIST_FORM_DECLARATION, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // LIST propertyExpression
  boolean listInputProp(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "listInputProp")) return false;
    if (!nextTokenIs(b, LIST)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, LIST);
    p = r; // pin = 1
    r = r && propertyExpression(b, l + 1);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // (PIVOT <<pivotCheck>> (DEFAULT | NODEFAULT)? pivotOptions) | (MAP mapOptions?) | (CUSTOM stringLiteral (customHeaderLiteral propertyExpression)?) | CALENDAR
  public boolean listViewType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "listViewType")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, LIST_VIEW_TYPE, "<list view type>");
    r = listViewType_0(b, l + 1);
    if (!r) r = listViewType_1(b, l + 1);
    if (!r) r = listViewType_2(b, l + 1);
    if (!r) r = consumeToken(b, CALENDAR);
    exit_section_(b, l, m, LIST_VIEW_TYPE, r, false, null);
    return r;
  }

  // PIVOT <<pivotCheck>> (DEFAULT | NODEFAULT)? pivotOptions
  private boolean listViewType_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "listViewType_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, PIVOT);
    r = r && pivotCheck(b, l + 1);
    r = r && listViewType_0_2(b, l + 1);
    r = r && pivotOptions(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (DEFAULT | NODEFAULT)?
  private boolean listViewType_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "listViewType_0_2")) return false;
    listViewType_0_2_0(b, l + 1);
    return true;
  }

  // DEFAULT | NODEFAULT
  private boolean listViewType_0_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "listViewType_0_2_0")) return false;
    boolean r;
    r = consumeToken(b, DEFAULT);
    if (!r) r = consumeToken(b, NODEFAULT);
    return r;
  }

  // MAP mapOptions?
  private boolean listViewType_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "listViewType_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, MAP);
    r = r && listViewType_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // mapOptions?
  private boolean listViewType_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "listViewType_1_1")) return false;
    mapOptions(b, l + 1);
    return true;
  }

  // CUSTOM stringLiteral (customHeaderLiteral propertyExpression)?
  private boolean listViewType_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "listViewType_2")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, CUSTOM);
    r = r && stringLiteral(b, l + 1);
    r = r && listViewType_2_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (customHeaderLiteral propertyExpression)?
  private boolean listViewType_2_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "listViewType_2_2")) return false;
    listViewType_2_2_0(b, l + 1);
    return true;
  }

  // customHeaderLiteral propertyExpression
  private boolean listViewType_2_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "listViewType_2_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = customHeaderLiteral(b, l + 1);
    r = r && propertyExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // listInputProp? whereInputProp?
  public boolean listWhereInputProps(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "listWhereInputProps")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, LIST_WHERE_INPUT_PROPS, "<list where input props>");
    r = listWhereInputProps_0(b, l + 1);
    r = r && listWhereInputProps_1(b, l + 1);
    exit_section_(b, l, m, LIST_WHERE_INPUT_PROPS, r, false, null);
    return r;
  }

  // listInputProp?
  private boolean listWhereInputProps_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "listWhereInputProps_0")) return false;
    listInputProp(b, l + 1);
    return true;
  }

  // whereInputProp?
  private boolean listWhereInputProps_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "listWhereInputProps_1")) return false;
    whereInputProp(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // !(ACTIVATE | ACTIVE | NEW | NEWEDIT | APPLY | CANCEL | CHANGE | ASK | ASYNCUPDATE
  //                                             | BREAK | CASE | CHANGECLASS | CONFIRM | CONTINUE | INTERNAL | DELETE
  //                                             | DRILLDOWN | EDIT | EMAIL | EVAL | EXEC | FILTER | FOR | SHOW | CLOSE | DIALOG | PRINT | EXPORT | TRY | IF | IMPORT | LOCAL | MESSAGE | MULTI
  //                                             | ORDER | READ | REQUEST | INPUT | RETURN | SEEK | WHILE| WRITE | RECALCULATE
  //                                             | NEWEXECUTOR | NEWSESSION | NEWTHREAD | NESTEDSESSION
  //                                             | COLLAPSE | EXPAND
  //                                             | EXTERNAL
  //                                             | ID // pinned with execActionPropertyDefinitionBody
  //                                             | SEMI
  //                                             | LBRACE
  //                                             | RBRACE // would not be pinned (matched) but we will consider it the end of list statement
  //                                            )
  boolean list_action_statement_recover(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "list_action_statement_recover")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !list_action_statement_recover_0(b, l + 1);
    exit_section_(b, l, m, null, r, false, null);
    return r;
  }

  // ACTIVATE | ACTIVE | NEW | NEWEDIT | APPLY | CANCEL | CHANGE | ASK | ASYNCUPDATE
  //                                             | BREAK | CASE | CHANGECLASS | CONFIRM | CONTINUE | INTERNAL | DELETE
  //                                             | DRILLDOWN | EDIT | EMAIL | EVAL | EXEC | FILTER | FOR | SHOW | CLOSE | DIALOG | PRINT | EXPORT | TRY | IF | IMPORT | LOCAL | MESSAGE | MULTI
  //                                             | ORDER | READ | REQUEST | INPUT | RETURN | SEEK | WHILE| WRITE | RECALCULATE
  //                                             | NEWEXECUTOR | NEWSESSION | NEWTHREAD | NESTEDSESSION
  //                                             | COLLAPSE | EXPAND
  //                                             | EXTERNAL
  //                                             | ID // pinned with execActionPropertyDefinitionBody
  //                                             | SEMI
  //                                             | LBRACE
  //                                             | RBRACE
  private boolean list_action_statement_recover_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "list_action_statement_recover_0")) return false;
    boolean r;
    r = consumeToken(b, ACTIVATE);
    if (!r) r = consumeToken(b, ACTIVE);
    if (!r) r = consumeToken(b, NEW);
    if (!r) r = consumeToken(b, NEWEDIT);
    if (!r) r = consumeToken(b, APPLY);
    if (!r) r = consumeToken(b, CANCEL);
    if (!r) r = consumeToken(b, CHANGE);
    if (!r) r = consumeToken(b, ASK);
    if (!r) r = consumeToken(b, ASYNCUPDATE);
    if (!r) r = consumeToken(b, BREAK);
    if (!r) r = consumeToken(b, CASE);
    if (!r) r = consumeToken(b, CHANGECLASS);
    if (!r) r = consumeToken(b, CONFIRM);
    if (!r) r = consumeToken(b, CONTINUE);
    if (!r) r = consumeToken(b, INTERNAL);
    if (!r) r = consumeToken(b, DELETE);
    if (!r) r = consumeToken(b, DRILLDOWN);
    if (!r) r = consumeToken(b, EDIT);
    if (!r) r = consumeToken(b, EMAIL);
    if (!r) r = consumeToken(b, EVAL);
    if (!r) r = consumeToken(b, EXEC);
    if (!r) r = consumeToken(b, FILTER);
    if (!r) r = consumeToken(b, FOR);
    if (!r) r = consumeToken(b, SHOW);
    if (!r) r = consumeToken(b, CLOSE);
    if (!r) r = consumeToken(b, DIALOG);
    if (!r) r = consumeToken(b, PRINT);
    if (!r) r = consumeToken(b, EXPORT);
    if (!r) r = consumeToken(b, TRY);
    if (!r) r = consumeToken(b, IF);
    if (!r) r = consumeToken(b, IMPORT);
    if (!r) r = consumeToken(b, LOCAL);
    if (!r) r = consumeToken(b, MESSAGE);
    if (!r) r = consumeToken(b, MULTI);
    if (!r) r = consumeToken(b, ORDER);
    if (!r) r = consumeToken(b, READ);
    if (!r) r = consumeToken(b, REQUEST);
    if (!r) r = consumeToken(b, INPUT);
    if (!r) r = consumeToken(b, RETURN);
    if (!r) r = consumeToken(b, SEEK);
    if (!r) r = consumeToken(b, WHILE);
    if (!r) r = consumeToken(b, WRITE);
    if (!r) r = consumeToken(b, RECALCULATE);
    if (!r) r = consumeToken(b, NEWEXECUTOR);
    if (!r) r = consumeToken(b, NEWSESSION);
    if (!r) r = consumeToken(b, NEWTHREAD);
    if (!r) r = consumeToken(b, NESTEDSESSION);
    if (!r) r = consumeToken(b, COLLAPSE);
    if (!r) r = consumeToken(b, EXPAND);
    if (!r) r = consumeToken(b, EXTERNAL);
    if (!r) r = consumeToken(b, ID);
    if (!r) r = consumeToken(b, SEMI);
    if (!r) r = consumeToken(b, LBRACE);
    if (!r) r = consumeToken(b, RBRACE);
    return r;
  }

  /* ********************************************************** */
  // commonLiteral |	(<<noIDCheck>> localizedStringLiteral)
  public boolean literal(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literal")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, LITERAL, "<literal>");
    r = commonLiteral(b, l + 1);
    if (!r) r = literal_1(b, l + 1);
    exit_section_(b, l, m, LITERAL, r, false, null);
    return r;
  }

  // <<noIDCheck>> localizedStringLiteral
  private boolean literal_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literal_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = noIDCheck(b, l + 1);
    r = r && localizedStringLiteral(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // LOCAL nestedLocalModifier nonEmptyLocalPropertyDeclarationNameList EQUALS className LBRAC classNameList RBRAC
  public boolean localDataPropertyDefinition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "localDataPropertyDefinition")) return false;
    if (!nextTokenIs(b, LOCAL)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, LOCAL_DATA_PROPERTY_DEFINITION, null);
    r = consumeToken(b, LOCAL);
    p = r; // pin = 1
    r = r && nestedLocalModifier(b, l + 1);
    r = r && nonEmptyLocalPropertyDeclarationNameList(b, l + 1);
    r = r && consumeToken(b, EQUALS);
    r = r && className(b, l + 1);
    r = r && consumeToken(b, LBRAC);
    r = r && classNameList(b, l + 1);
    r = r && consumeToken(b, RBRAC);
    exit_section_(b, l, m, LOCAL_DATA_PROPERTY_DEFINITION, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // simpleName
  public boolean localPropertyDeclarationName(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "localPropertyDeclarationName")) return false;
    if (!nextTokenIs(b, ID)) return false;
    boolean r;
    Marker m = enter_section_(b, l, LOCAL_PROPERTY_DECLARATION_NAME);
    r = simpleName(b, l + 1);
    exit_section_(b, m, LOCAL_PROPERTY_DECLARATION_NAME, r);
    return r;
  }

  /* ********************************************************** */
  // LEX_STRING_LITERAL | ID
  public boolean localizedStringLiteral(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "localizedStringLiteral")) return false;
    if (!nextTokenIs(b, "<localized string literal>", ID, LEX_STRING_LITERAL)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, LOCALIZED_STRING_LITERAL, "<localized string literal>");
    r = consumeToken(b, LEX_STRING_LITERAL);
    if (!r) r = consumeToken(b, ID);
    exit_section_(b, l, m, LOCALIZED_STRING_LITERAL, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // LOGGABLE
  public boolean loggableSetting(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "loggableSetting")) return false;
    if (!nextTokenIs(b, LOGGABLE)) return false;
    boolean r;
    Marker m = enter_section_(b, l, LOGGABLE_SETTING);
    r = consumeToken(b, LOGGABLE);
    exit_section_(b, m, LOGGABLE_SETTING, r);
    return r;
  }

  /* ********************************************************** */
  // LOGGABLE nonEmptyNoContextPropertyUsageList SEMI
  public boolean loggableStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "loggableStatement")) return false;
    if (!nextTokenIs(b, LOGGABLE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, LOGGABLE_STATEMENT, null);
    r = consumeToken(b, LOGGABLE);
    p = r; // pin = 1
    r = r && nonEmptyNoContextPropertyUsageList(b, l + 1);
    r = r && consumeToken(b, SEMI);
    exit_section_(b, l, m, LOGGABLE_STATEMENT, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // MANAGESESSION | NOMANAGESESSION
  public boolean manageSessionClause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "manageSessionClause")) return false;
    if (!nextTokenIs(b, "<manage session clause>", MANAGESESSION, NOMANAGESESSION)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, MANAGE_SESSION_CLAUSE, "<manage session clause>");
    r = consumeToken(b, MANAGESESSION);
    if (!r) r = consumeToken(b, NOMANAGESESSION);
    exit_section_(b, l, m, MANAGE_SESSION_CLAUSE, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // stringLiteral
  public boolean mapOptions(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "mapOptions")) return false;
    if (!nextTokenIs(b, "<map options>", ID, LEX_STRING_LITERAL)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, MAP_OPTIONS, "<map options>");
    r = stringLiteral(b, l + 1);
    exit_section_(b, l, m, MAP_OPTIONS, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // actionUsageWrapper LBRAC classParamDeclareList RBRAC
  public boolean mappedActionClassParamDeclare(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "mappedActionClassParamDeclare")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, MAPPED_ACTION_CLASS_PARAM_DECLARE, "<mapped action class param declare>");
    r = actionUsageWrapper(b, l + 1);
    p = r; // pin = 1
    r = r && consumeToken(b, LBRAC);
    r = r && classParamDeclareList(b, l + 1);
    r = r && consumeToken(b, RBRAC);
    exit_section_(b, l, m, MAPPED_ACTION_CLASS_PARAM_DECLARE, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // (   (LIST | EDIT) customClassUsage formSingleActionObject)
  //                         |   (formUsage formActionObjectList?)
  boolean mappedForm(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "mappedForm")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = mappedForm_0(b, l + 1);
    if (!r) r = mappedForm_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (LIST | EDIT) customClassUsage formSingleActionObject
  private boolean mappedForm_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "mappedForm_0")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = mappedForm_0_0(b, l + 1);
    p = r; // pin = 1
    r = r && customClassUsage(b, l + 1);
    r = r && formSingleActionObject(b, l + 1);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  // LIST | EDIT
  private boolean mappedForm_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "mappedForm_0_0")) return false;
    boolean r;
    r = consumeToken(b, LIST);
    if (!r) r = consumeToken(b, EDIT);
    return r;
  }

  // formUsage formActionObjectList?
  private boolean mappedForm_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "mappedForm_1")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = formUsage(b, l + 1);
    p = r; // pin = 1
    r = r && mappedForm_1_1(b, l + 1);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  // formActionObjectList?
  private boolean mappedForm_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "mappedForm_1_1")) return false;
    formActionObjectList(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // propertyUsageWrapper LBRAC classParamDeclareList RBRAC
  public boolean mappedPropertyClassParamDeclare(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "mappedPropertyClassParamDeclare")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, MAPPED_PROPERTY_CLASS_PARAM_DECLARE, "<mapped property class param declare>");
    r = propertyUsageWrapper(b, l + 1);
    p = r; // pin = 1
    r = r && consumeToken(b, LBRAC);
    r = r && classParamDeclareList(b, l + 1);
    r = r && consumeToken(b, RBRAC);
    exit_section_(b, l, m, MAPPED_PROPERTY_CLASS_PARAM_DECLARE, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // propertyUsage LBRAC exprParameterUsageList RBRAC
  public boolean mappedPropertyExprParam(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "mappedPropertyExprParam")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, MAPPED_PROPERTY_EXPR_PARAM, "<mapped property expr param>");
    r = propertyUsage(b, l + 1);
    r = r && consumeToken(b, LBRAC);
    r = r && exprParameterUsageList(b, l + 1);
    r = r && consumeToken(b, RBRAC);
    exit_section_(b, l, m, MAPPED_PROPERTY_EXPR_PARAM, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // mappedPropertyExprParam | exprParameterUsage
  public boolean mappedPropertyOrSimpleExprParam(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "mappedPropertyOrSimpleExprParam")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, MAPPED_PROPERTY_OR_SIMPLE_EXPR_PARAM, "<mapped property or simple expr param>");
    r = mappedPropertyExprParam(b, l + 1);
    if (!r) r = exprParameterUsage(b, l + 1);
    exit_section_(b, l, m, MAPPED_PROPERTY_OR_SIMPLE_EXPR_PARAM, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // (MAX | MIN) nonEmptyPropertyExpressionList
  public boolean maxPropertyDefinition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "maxPropertyDefinition")) return false;
    if (!nextTokenIs(b, "<max property definition>", MAX, MIN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, MAX_PROPERTY_DEFINITION, "<max property definition>");
    r = maxPropertyDefinition_0(b, l + 1);
    p = r; // pin = 1
    r = r && nonEmptyPropertyExpressionList(b, l + 1);
    exit_section_(b, l, m, MAX_PROPERTY_DEFINITION, r, p, null);
    return r || p;
  }

  // MAX | MIN
  private boolean maxPropertyDefinition_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "maxPropertyDefinition_0")) return false;
    boolean r;
    r = consumeToken(b, MAX);
    if (!r) r = consumeToken(b, MIN);
    return r;
  }

  /* ********************************************************** */
  // MESSAGE propertyExpression (syncTypeLiteral | LOG)*
  public boolean messageActionPropertyDefinitionBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "messageActionPropertyDefinitionBody")) return false;
    if (!nextTokenIs(b, MESSAGE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, MESSAGE_ACTION_PROPERTY_DEFINITION_BODY, null);
    r = consumeToken(b, MESSAGE);
    p = r; // pin = 1
    r = r && propertyExpression(b, l + 1);
    r = r && messageActionPropertyDefinitionBody_2(b, l + 1);
    exit_section_(b, l, m, MESSAGE_ACTION_PROPERTY_DEFINITION_BODY, r, p, null);
    return r || p;
  }

  // (syncTypeLiteral | LOG)*
  private boolean messageActionPropertyDefinitionBody_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "messageActionPropertyDefinitionBody_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!messageActionPropertyDefinitionBody_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "messageActionPropertyDefinitionBody_2", c)) break;
    }
    return true;
  }

  // syncTypeLiteral | LOG
  private boolean messageActionPropertyDefinitionBody_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "messageActionPropertyDefinitionBody_2_0")) return false;
    boolean r;
    r = syncTypeLiteral(b, l + 1);
    if (!r) r = consumeToken(b, LOG);
    return r;
  }

  /* ********************************************************** */
  // propertyExpression
  public boolean messagePropertyExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "messagePropertyExpression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, MESSAGE_PROPERTY_EXPRESSION, "<message property expression>");
    r = propertyExpression(b, l + 1);
    exit_section_(b, l, m, MESSAGE_PROPERTY_EXPRESSION, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // metaCodeBodyLeftBrace (lazyMetaStatement*) metaCodeBodyRightBrace
  public boolean metaCodeBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "metaCodeBody")) return false;
    if (!nextTokenIs(b, LBRACE)) return false;
    boolean r;
    Marker m = enter_section_(b, l, META_CODE_BODY);
    r = metaCodeBodyLeftBrace(b, l + 1);
    r = r && metaCodeBody_1(b, l + 1);
    r = r && metaCodeBodyRightBrace(b, l + 1);
    exit_section_(b, m, META_CODE_BODY, r);
    return r;
  }

  // lazyMetaStatement*
  private boolean metaCodeBody_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "metaCodeBody_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!lazyMetaStatement(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "metaCodeBody_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // LBRACE
  public boolean metaCodeBodyLeftBrace(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "metaCodeBodyLeftBrace")) return false;
    if (!nextTokenIs(b, LBRACE)) return false;
    boolean r;
    Marker m = enter_section_(b, l, META_CODE_BODY_LEFT_BRACE);
    r = consumeToken(b, LBRACE);
    exit_section_(b, m, META_CODE_BODY_LEFT_BRACE, r);
    return r;
  }

  /* ********************************************************** */
  // RBRACE
  public boolean metaCodeBodyRightBrace(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "metaCodeBodyRightBrace")) return false;
    if (!nextTokenIs(b, RBRACE)) return false;
    boolean r;
    Marker m = enter_section_(b, l, META_CODE_BODY_RIGHT_BRACE);
    r = consumeToken(b, RBRACE);
    exit_section_(b, m, META_CODE_BODY_RIGHT_BRACE, r);
    return r;
  }

  /* ********************************************************** */
  // lazyMetaDeclStatement*
  public boolean metaCodeDeclBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "metaCodeDeclBody")) return false;
    Marker m = enter_section_(b, l, _NONE_, META_CODE_DECL_BODY, "<meta code decl body>");
    while (true) {
      int c = current_position_(b);
      if (!lazyMetaDeclStatement(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "metaCodeDeclBody", c)) break;
    }
    exit_section_(b, l, m, META_CODE_DECL_BODY, true, false, null);
    return true;
  }

  /* ********************************************************** */
  // META simpleName LBRAC metaDeclIdList? RBRAC metaCodeDeclBody END
  public boolean metaCodeDeclarationStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "metaCodeDeclarationStatement")) return false;
    if (!nextTokenIs(b, META)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, META_CODE_DECLARATION_STATEMENT, null);
    r = consumeToken(b, META);
    r = r && simpleName(b, l + 1);
    p = r; // pin = 2
    r = r && consumeToken(b, LBRAC);
    r = r && metaCodeDeclarationStatement_3(b, l + 1);
    r = r && consumeToken(b, RBRAC);
    r = r && metaCodeDeclBody(b, l + 1);
    r = r && consumeToken(b, END);
    exit_section_(b, l, m, META_CODE_DECLARATION_STATEMENT, r, p, null);
    return r || p;
  }

  // metaDeclIdList?
  private boolean metaCodeDeclarationStatement_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "metaCodeDeclarationStatement_3")) return false;
    metaDeclIdList(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // (PRIMITIVE_TYPE | compoundID | metaCodeLiteral)?
  public boolean metaCodeId(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "metaCodeId")) return false;
    Marker m = enter_section_(b, l, _NONE_, META_CODE_ID, "<meta code id>");
    metaCodeId_0(b, l + 1);
    exit_section_(b, l, m, META_CODE_ID, true, false, null);
    return true;
  }

  // PRIMITIVE_TYPE | compoundID | metaCodeLiteral
  private boolean metaCodeId_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "metaCodeId_0")) return false;
    boolean r;
    r = consumeToken(b, PRIMITIVE_TYPE);
    if (!r) r = compoundID(b, l + 1);
    if (!r) r = metaCodeLiteral(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // (metaCodeId (COMMA metaCodeId)*)?
  public boolean metaCodeIdList(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "metaCodeIdList")) return false;
    Marker m = enter_section_(b, l, _NONE_, META_CODE_ID_LIST, "<meta code id list>");
    metaCodeIdList_0(b, l + 1);
    exit_section_(b, l, m, META_CODE_ID_LIST, true, false, null);
    return true;
  }

  // metaCodeId (COMMA metaCodeId)*
  private boolean metaCodeIdList_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "metaCodeIdList_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = metaCodeId(b, l + 1);
    r = r && metaCodeIdList_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (COMMA metaCodeId)*
  private boolean metaCodeIdList_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "metaCodeIdList_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!metaCodeIdList_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "metaCodeIdList_0_1", c)) break;
    }
    return true;
  }

  // COMMA metaCodeId
  private boolean metaCodeIdList_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "metaCodeIdList_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, COMMA);
    r = r && metaCodeId(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // metaCodeStringLiteral
  //                 | 	LEX_UINT_LITERAL
  //                 |	LEX_UNUMERIC_LITERAL
  //                 |	LEX_UDOUBLE_LITERAL
  //                 |	LEX_ULONG_LITERAL
  //                 |	LEX_LOGICAL_LITERAL
  //                 |	LEX_T_LOGICAL_LITERAL
  //                 |	LEX_DATE_LITERAL
  //                 |	LEX_DATETIME_LITERAL
  //                 |	LEX_TIME_LITERAL
  //                 |	LEX_COLOR_LITERAL
  //                 |	NULL
  public boolean metaCodeLiteral(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "metaCodeLiteral")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, META_CODE_LITERAL, "<meta code literal>");
    r = metaCodeStringLiteral(b, l + 1);
    if (!r) r = consumeToken(b, LEX_UINT_LITERAL);
    if (!r) r = consumeToken(b, LEX_UNUMERIC_LITERAL);
    if (!r) r = consumeToken(b, LEX_UDOUBLE_LITERAL);
    if (!r) r = consumeToken(b, LEX_ULONG_LITERAL);
    if (!r) r = consumeToken(b, LEX_LOGICAL_LITERAL);
    if (!r) r = consumeToken(b, LEX_T_LOGICAL_LITERAL);
    if (!r) r = consumeToken(b, LEX_DATE_LITERAL);
    if (!r) r = consumeToken(b, LEX_DATETIME_LITERAL);
    if (!r) r = consumeToken(b, LEX_TIME_LITERAL);
    if (!r) r = consumeToken(b, LEX_COLOR_LITERAL);
    if (!r) r = consumeToken(b, NULL);
    exit_section_(b, l, m, META_CODE_LITERAL, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // metaCodeStatementHeader metaCodeBody? metaCodeStatementSemi
  public boolean metaCodeStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "metaCodeStatement")) return false;
    if (!nextTokenIs(b, "<meta code statement>", ATSIGN, ATSIGN2)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, META_CODE_STATEMENT, "<meta code statement>");
    r = metaCodeStatementHeader(b, l + 1);
    r = r && metaCodeStatement_1(b, l + 1);
    p = r; // pin = 2
    r = r && metaCodeStatementSemi(b, l + 1);
    exit_section_(b, l, m, META_CODE_STATEMENT, r, p, null);
    return r || p;
  }

  // metaCodeBody?
  private boolean metaCodeStatement_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "metaCodeStatement_1")) return false;
    metaCodeBody(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // metaCodeStatementType metacodeUsage LBRAC metaCodeIdList RBRAC
  public boolean metaCodeStatementHeader(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "metaCodeStatementHeader")) return false;
    if (!nextTokenIs(b, "<meta code statement header>", ATSIGN, ATSIGN2)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, META_CODE_STATEMENT_HEADER, "<meta code statement header>");
    r = metaCodeStatementType(b, l + 1);
    r = r && metacodeUsage(b, l + 1);
    r = r && consumeToken(b, LBRAC);
    r = r && metaCodeIdList(b, l + 1);
    r = r && consumeToken(b, RBRAC);
    exit_section_(b, l, m, META_CODE_STATEMENT_HEADER, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // SEMI
  public boolean metaCodeStatementSemi(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "metaCodeStatementSemi")) return false;
    if (!nextTokenIs(b, SEMI)) return false;
    boolean r;
    Marker m = enter_section_(b, l, META_CODE_STATEMENT_SEMI);
    r = consumeToken(b, SEMI);
    exit_section_(b, m, META_CODE_STATEMENT_SEMI, r);
    return r;
  }

  /* ********************************************************** */
  // ATSIGN | ATSIGN2
  public boolean metaCodeStatementType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "metaCodeStatementType")) return false;
    if (!nextTokenIs(b, "<meta code statement type>", ATSIGN, ATSIGN2)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, META_CODE_STATEMENT_TYPE, "<meta code statement type>");
    r = consumeToken(b, ATSIGN);
    if (!r) r = consumeToken(b, ATSIGN2);
    exit_section_(b, l, m, META_CODE_STATEMENT_TYPE, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // LEX_STRING_LITERAL
  public boolean metaCodeStringLiteral(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "metaCodeStringLiteral")) return false;
    if (!nextTokenIs(b, LEX_STRING_LITERAL)) return false;
    boolean r;
    Marker m = enter_section_(b, l, META_CODE_STRING_LITERAL);
    r = consumeToken(b, LEX_STRING_LITERAL);
    exit_section_(b, m, META_CODE_STRING_LITERAL, r);
    return r;
  }

  /* ********************************************************** */
  // ID
  public boolean metaDeclId(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "metaDeclId")) return false;
    if (!nextTokenIs(b, ID)) return false;
    boolean r;
    Marker m = enter_section_(b, l, META_DECL_ID);
    r = consumeToken(b, ID);
    exit_section_(b, m, META_DECL_ID, r);
    return r;
  }

  /* ********************************************************** */
  // metaDeclId (COMMA metaDeclId)*
  public boolean metaDeclIdList(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "metaDeclIdList")) return false;
    if (!nextTokenIs(b, ID)) return false;
    boolean r;
    Marker m = enter_section_(b, l, META_DECL_ID_LIST);
    r = metaDeclId(b, l + 1);
    r = r && metaDeclIdList_1(b, l + 1);
    exit_section_(b, m, META_DECL_ID_LIST, r);
    return r;
  }

  // (COMMA metaDeclId)*
  private boolean metaDeclIdList_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "metaDeclIdList_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!metaDeclIdList_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "metaDeclIdList_1", c)) break;
    }
    return true;
  }

  // COMMA metaDeclId
  private boolean metaDeclIdList_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "metaDeclIdList_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, COMMA);
    r = r && metaDeclId(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // innerStatement
  boolean metaDeclStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "metaDeclStatement")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_);
    r = innerStatement(b, l + 1);
    exit_section_(b, l, m, null, r, false, this::meta_decl_statement_recover);
    return r;
  }

  /* ********************************************************** */
  // innerStatement
  boolean metaStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "metaStatement")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_);
    r = innerStatement(b, l + 1);
    exit_section_(b, l, m, null, r, false, this::meta_statement_recover);
    return r;
  }

  /* ********************************************************** */
  // !(inner_statement_start | END)
  boolean meta_decl_statement_recover(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "meta_decl_statement_recover")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !meta_decl_statement_recover_0(b, l + 1);
    exit_section_(b, l, m, null, r, false, null);
    return r;
  }

  // inner_statement_start | END
  private boolean meta_decl_statement_recover_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "meta_decl_statement_recover_0")) return false;
    boolean r;
    r = inner_statement_start(b, l + 1);
    if (!r) r = consumeToken(b, END);
    return r;
  }

  /* ********************************************************** */
  // !(inner_statement_start | metaCodeBodyRightBrace)
  boolean meta_statement_recover(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "meta_statement_recover")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !meta_statement_recover_0(b, l + 1);
    exit_section_(b, l, m, null, r, false, null);
    return r;
  }

  // inner_statement_start | metaCodeBodyRightBrace
  private boolean meta_statement_recover_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "meta_statement_recover_0")) return false;
    boolean r;
    r = inner_statement_start(b, l + 1);
    if (!r) r = metaCodeBodyRightBrace(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // compoundID
  public boolean metacodeUsage(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "metacodeUsage")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, METACODE_USAGE, "<metacode usage>");
    r = compoundID(b, l + 1);
    exit_section_(b, l, m, METACODE_USAGE, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // moduleNameStatement
  //                  requireList?
  //                  priorityList?
  //                  namespaceName?
  public boolean moduleHeader(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "moduleHeader")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, MODULE_HEADER, "<module header>");
    r = moduleNameStatement(b, l + 1);
    r = r && moduleHeader_1(b, l + 1);
    p = r; // pin = 2
    r = r && moduleHeader_2(b, l + 1);
    r = r && moduleHeader_3(b, l + 1);
    exit_section_(b, l, m, MODULE_HEADER, r, p, this::script_statement_recover);
    return r || p;
  }

  // requireList?
  private boolean moduleHeader_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "moduleHeader_1")) return false;
    requireList(b, l + 1);
    return true;
  }

  // priorityList?
  private boolean moduleHeader_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "moduleHeader_2")) return false;
    priorityList(b, l + 1);
    return true;
  }

  // namespaceName?
  private boolean moduleHeader_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "moduleHeader_3")) return false;
    namespaceName(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // simpleName
  public boolean moduleName(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "moduleName")) return false;
    if (!nextTokenIs(b, ID)) return false;
    boolean r;
    Marker m = enter_section_(b, l, MODULE_NAME);
    r = simpleName(b, l + 1);
    exit_section_(b, m, MODULE_NAME, r);
    return r;
  }

  /* ********************************************************** */
  // MODULE moduleName SEMI
  public boolean moduleNameStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "moduleNameStatement")) return false;
    if (!nextTokenIs(b, MODULE)) return false;
    boolean r;
    Marker m = enter_section_(b, l, MODULE_NAME_STATEMENT);
    r = consumeToken(b, MODULE);
    r = r && moduleName(b, l + 1);
    r = r && consumeToken(b, SEMI);
    exit_section_(b, m, MODULE_NAME_STATEMENT, r);
    return r;
  }

  /* ********************************************************** */
  // simpleName
  public boolean moduleUsage(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "moduleUsage")) return false;
    if (!nextTokenIs(b, ID)) return false;
    boolean r;
    Marker m = enter_section_(b, l, MODULE_USAGE);
    r = simpleName(b, l + 1);
    exit_section_(b, m, MODULE_USAGE, r);
    return r;
  }

  /* ********************************************************** */
  // MOVE componentSelector componentInsertPosition componentBody
  public boolean moveComponentStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "moveComponentStatement")) return false;
    if (!nextTokenIs(b, MOVE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, MOVE_COMPONENT_STATEMENT, null);
    r = consumeToken(b, MOVE);
    p = r; // pin = 1
    r = r && componentSelector(b, l + 1);
    r = r && componentInsertPosition(b, l + 1);
    r = r && componentBody(b, l + 1);
    exit_section_(b, l, m, MOVE_COMPONENT_STATEMENT, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // MOVE navigatorElementSelector (localizedStringLiteral)? navigatorElementOptions navigatorElementStatementBody
  public boolean moveNavigatorElementStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "moveNavigatorElementStatement")) return false;
    if (!nextTokenIs(b, MOVE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, MOVE_NAVIGATOR_ELEMENT_STATEMENT, null);
    r = consumeToken(b, MOVE);
    p = r; // pin = 1
    r = r && navigatorElementSelector(b, l + 1);
    r = r && moveNavigatorElementStatement_2(b, l + 1);
    r = r && navigatorElementOptions(b, l + 1);
    r = r && navigatorElementStatementBody(b, l + 1);
    exit_section_(b, l, m, MOVE_NAVIGATOR_ELEMENT_STATEMENT, r, p, null);
    return r || p;
  }

  // (localizedStringLiteral)?
  private boolean moveNavigatorElementStatement_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "moveNavigatorElementStatement_2")) return false;
    moveNavigatorElementStatement_2_0(b, l + 1);
    return true;
  }

  // (localizedStringLiteral)
  private boolean moveNavigatorElementStatement_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "moveNavigatorElementStatement_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = localizedStringLiteral(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // MULTI (exclusiveOverrideOption)? nonEmptyActionPDBList
  public boolean multiActionPropertyDefinitionBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "multiActionPropertyDefinitionBody")) return false;
    if (!nextTokenIs(b, MULTI)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, MULTI_ACTION_PROPERTY_DEFINITION_BODY, null);
    r = consumeToken(b, MULTI);
    p = r; // pin = 1
    r = r && multiActionPropertyDefinitionBody_1(b, l + 1);
    r = r && nonEmptyActionPDBList(b, l + 1);
    exit_section_(b, l, m, MULTI_ACTION_PROPERTY_DEFINITION_BODY, r, p, null);
    return r || p;
  }

  // (exclusiveOverrideOption)?
  private boolean multiActionPropertyDefinitionBody_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "multiActionPropertyDefinitionBody_1")) return false;
    multiActionPropertyDefinitionBody_1_0(b, l + 1);
    return true;
  }

  // (exclusiveOverrideOption)
  private boolean multiActionPropertyDefinitionBody_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "multiActionPropertyDefinitionBody_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = exclusiveOverrideOption(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // MULTI (exclusiveOverrideOption)? nonEmptyPropertyExpressionList
  public boolean multiPropertyDefinition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "multiPropertyDefinition")) return false;
    if (!nextTokenIs(b, MULTI)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, MULTI_PROPERTY_DEFINITION, null);
    r = consumeToken(b, MULTI);
    p = r; // pin = 1
    r = r && multiPropertyDefinition_1(b, l + 1);
    r = r && nonEmptyPropertyExpressionList(b, l + 1);
    exit_section_(b, l, m, MULTI_PROPERTY_DEFINITION, r, p, null);
    return r || p;
  }

  // (exclusiveOverrideOption)?
  private boolean multiPropertyDefinition_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "multiPropertyDefinition_1")) return false;
    multiPropertyDefinition_1_0(b, l + 1);
    return true;
  }

  // (exclusiveOverrideOption)
  private boolean multiPropertyDefinition_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "multiPropertyDefinition_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = exclusiveOverrideOption(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // unaryMinusPE (typeMult unaryMinusPE)*
  public boolean multiplicativePE(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "multiplicativePE")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, MULTIPLICATIVE_PE, "<multiplicative pe>");
    r = unaryMinusPE(b, l + 1);
    r = r && multiplicativePE_1(b, l + 1);
    exit_section_(b, l, m, MULTIPLICATIVE_PE, r, false, null);
    return r;
  }

  // (typeMult unaryMinusPE)*
  private boolean multiplicativePE_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "multiplicativePE_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!multiplicativePE_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "multiplicativePE_1", c)) break;
    }
    return true;
  }

  // typeMult unaryMinusPE
  private boolean multiplicativePE_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "multiplicativePE_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = typeMult(b, l + 1);
    r = r && unaryMinusPE(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // NAMESPACE namespaceUsage SEMI
  public boolean namespaceName(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "namespaceName")) return false;
    if (!nextTokenIs(b, NAMESPACE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, NAMESPACE_NAME, null);
    r = consumeToken(b, NAMESPACE);
    r = r && namespaceUsage(b, l + 1);
    p = r; // pin = 2
    r = r && consumeToken(b, SEMI);
    exit_section_(b, l, m, NAMESPACE_NAME, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // simpleName
  public boolean namespaceUsage(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "namespaceUsage")) return false;
    if (!nextTokenIs(b, ID)) return false;
    boolean r;
    Marker m = enter_section_(b, l, NAMESPACE_USAGE);
    r = simpleName(b, l + 1);
    exit_section_(b, m, NAMESPACE_USAGE, r);
    return r;
  }

  /* ********************************************************** */
  // NATIVE
  public boolean nativeLiteral(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nativeLiteral")) return false;
    if (!nextTokenIs(b, NATIVE)) return false;
    boolean r;
    Marker m = enter_section_(b, l, NATIVE_LITERAL);
    r = consumeToken(b, NATIVE);
    exit_section_(b, m, NATIVE_LITERAL, r);
    return r;
  }

  /* ********************************************************** */
  // NATIVE className LBRAC classNameList RBRAC
  public boolean nativePropertyDefinition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nativePropertyDefinition")) return false;
    if (!nextTokenIs(b, NATIVE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, NATIVE_PROPERTY_DEFINITION, null);
    r = consumeToken(b, NATIVE);
    p = r; // pin = 1
    r = r && className(b, l + 1);
    r = r && consumeToken(b, LBRAC);
    r = r && classNameList(b, l + 1);
    r = r && consumeToken(b, RBRAC);
    exit_section_(b, l, m, NATIVE_PROPERTY_DEFINITION, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // moveNavigatorElementStatement
  //                                   |     setupNavigatorElementStatement
  //                                   |     newNavigatorElementStatement
  //                                   |     emptyStatement
  //                                   |     neStub
  public boolean navigatorElementBodyStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "navigatorElementBodyStatement")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, NAVIGATOR_ELEMENT_BODY_STATEMENT, "<navigator element body statement>");
    r = moveNavigatorElementStatement(b, l + 1);
    if (!r) r = setupNavigatorElementStatement(b, l + 1);
    if (!r) r = newNavigatorElementStatement(b, l + 1);
    if (!r) r = emptyStatement(b, l + 1);
    if (!r) r = neStub(b, l + 1);
    exit_section_(b, l, m, NAVIGATOR_ELEMENT_BODY_STATEMENT, r, false, this::ne_statement_recover);
    return r;
  }

  /* ********************************************************** */
  // ACTION (simpleName? localizedStringLiteral? EQUALS)? noParamsActionUsage
  //                                 |   FORM (simpleName? localizedStringLiteral? EQUALS)? formUsage
  //                                 |   folderElementDescription
  //                                 |   simpleElementDescription
  public boolean navigatorElementDescription(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "navigatorElementDescription")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, NAVIGATOR_ELEMENT_DESCRIPTION, "<navigator element description>");
    r = navigatorElementDescription_0(b, l + 1);
    if (!r) r = navigatorElementDescription_1(b, l + 1);
    if (!r) r = folderElementDescription(b, l + 1);
    if (!r) r = simpleElementDescription(b, l + 1);
    exit_section_(b, l, m, NAVIGATOR_ELEMENT_DESCRIPTION, r, false, null);
    return r;
  }

  // ACTION (simpleName? localizedStringLiteral? EQUALS)? noParamsActionUsage
  private boolean navigatorElementDescription_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "navigatorElementDescription_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, ACTION);
    r = r && navigatorElementDescription_0_1(b, l + 1);
    r = r && noParamsActionUsage(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (simpleName? localizedStringLiteral? EQUALS)?
  private boolean navigatorElementDescription_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "navigatorElementDescription_0_1")) return false;
    navigatorElementDescription_0_1_0(b, l + 1);
    return true;
  }

  // simpleName? localizedStringLiteral? EQUALS
  private boolean navigatorElementDescription_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "navigatorElementDescription_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = navigatorElementDescription_0_1_0_0(b, l + 1);
    r = r && navigatorElementDescription_0_1_0_1(b, l + 1);
    r = r && consumeToken(b, EQUALS);
    exit_section_(b, m, null, r);
    return r;
  }

  // simpleName?
  private boolean navigatorElementDescription_0_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "navigatorElementDescription_0_1_0_0")) return false;
    simpleName(b, l + 1);
    return true;
  }

  // localizedStringLiteral?
  private boolean navigatorElementDescription_0_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "navigatorElementDescription_0_1_0_1")) return false;
    localizedStringLiteral(b, l + 1);
    return true;
  }

  // FORM (simpleName? localizedStringLiteral? EQUALS)? formUsage
  private boolean navigatorElementDescription_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "navigatorElementDescription_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, FORM);
    r = r && navigatorElementDescription_1_1(b, l + 1);
    r = r && formUsage(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (simpleName? localizedStringLiteral? EQUALS)?
  private boolean navigatorElementDescription_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "navigatorElementDescription_1_1")) return false;
    navigatorElementDescription_1_1_0(b, l + 1);
    return true;
  }

  // simpleName? localizedStringLiteral? EQUALS
  private boolean navigatorElementDescription_1_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "navigatorElementDescription_1_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = navigatorElementDescription_1_1_0_0(b, l + 1);
    r = r && navigatorElementDescription_1_1_0_1(b, l + 1);
    r = r && consumeToken(b, EQUALS);
    exit_section_(b, m, null, r);
    return r;
  }

  // simpleName?
  private boolean navigatorElementDescription_1_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "navigatorElementDescription_1_1_0_0")) return false;
    simpleName(b, l + 1);
    return true;
  }

  // localizedStringLiteral?
  private boolean navigatorElementDescription_1_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "navigatorElementDescription_1_1_0_1")) return false;
    localizedStringLiteral(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // insertRelativePositionLiteral navigatorElementSelector
  //                                     |	staticRelativePosition
  public boolean navigatorElementInsertPosition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "navigatorElementInsertPosition")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, NAVIGATOR_ELEMENT_INSERT_POSITION, "<navigator element insert position>");
    r = navigatorElementInsertPosition_0(b, l + 1);
    if (!r) r = staticRelativePosition(b, l + 1);
    exit_section_(b, l, m, NAVIGATOR_ELEMENT_INSERT_POSITION, r, false, null);
    return r;
  }

  // insertRelativePositionLiteral navigatorElementSelector
  private boolean navigatorElementInsertPosition_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "navigatorElementInsertPosition_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = insertRelativePositionLiteral(b, l + 1);
    r = r && navigatorElementSelector(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // (	(WINDOW windowUsage PARENT?)
  //                             |	navigatorElementInsertPosition 
  //                             |	((IMAGE (propertyExpression | stringLiteral)?) | NOIMAGE)
  //                             |   CLASS propertyExpression
  //                             |	HEADER propertyExpression
  //                             )*
  public boolean navigatorElementOptions(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "navigatorElementOptions")) return false;
    Marker m = enter_section_(b, l, _NONE_, NAVIGATOR_ELEMENT_OPTIONS, "<navigator element options>");
    while (true) {
      int c = current_position_(b);
      if (!navigatorElementOptions_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "navigatorElementOptions", c)) break;
    }
    exit_section_(b, l, m, NAVIGATOR_ELEMENT_OPTIONS, true, false, null);
    return true;
  }

  // (WINDOW windowUsage PARENT?)
  //                             |	navigatorElementInsertPosition 
  //                             |	((IMAGE (propertyExpression | stringLiteral)?) | NOIMAGE)
  //                             |   CLASS propertyExpression
  //                             |	HEADER propertyExpression
  private boolean navigatorElementOptions_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "navigatorElementOptions_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = navigatorElementOptions_0_0(b, l + 1);
    if (!r) r = navigatorElementInsertPosition(b, l + 1);
    if (!r) r = navigatorElementOptions_0_2(b, l + 1);
    if (!r) r = navigatorElementOptions_0_3(b, l + 1);
    if (!r) r = navigatorElementOptions_0_4(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // WINDOW windowUsage PARENT?
  private boolean navigatorElementOptions_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "navigatorElementOptions_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, WINDOW);
    r = r && windowUsage(b, l + 1);
    r = r && navigatorElementOptions_0_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // PARENT?
  private boolean navigatorElementOptions_0_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "navigatorElementOptions_0_0_2")) return false;
    consumeToken(b, PARENT);
    return true;
  }

  // (IMAGE (propertyExpression | stringLiteral)?) | NOIMAGE
  private boolean navigatorElementOptions_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "navigatorElementOptions_0_2")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = navigatorElementOptions_0_2_0(b, l + 1);
    if (!r) r = consumeToken(b, NOIMAGE);
    exit_section_(b, m, null, r);
    return r;
  }

  // IMAGE (propertyExpression | stringLiteral)?
  private boolean navigatorElementOptions_0_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "navigatorElementOptions_0_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, IMAGE);
    r = r && navigatorElementOptions_0_2_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (propertyExpression | stringLiteral)?
  private boolean navigatorElementOptions_0_2_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "navigatorElementOptions_0_2_0_1")) return false;
    navigatorElementOptions_0_2_0_1_0(b, l + 1);
    return true;
  }

  // propertyExpression | stringLiteral
  private boolean navigatorElementOptions_0_2_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "navigatorElementOptions_0_2_0_1_0")) return false;
    boolean r;
    r = propertyExpression(b, l + 1);
    if (!r) r = stringLiteral(b, l + 1);
    return r;
  }

  // CLASS propertyExpression
  private boolean navigatorElementOptions_0_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "navigatorElementOptions_0_3")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, CLASS);
    r = r && propertyExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // HEADER propertyExpression
  private boolean navigatorElementOptions_0_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "navigatorElementOptions_0_4")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, HEADER);
    r = r && propertyExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // navigatorElementUsage
  public boolean navigatorElementSelector(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "navigatorElementSelector")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, NAVIGATOR_ELEMENT_SELECTOR, "<navigator element selector>");
    r = navigatorElementUsage(b, l + 1);
    exit_section_(b, l, m, NAVIGATOR_ELEMENT_SELECTOR, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // (   LBRACE
  //                                         navigatorElementBodyStatement*
  //                                         RBRACE
  //                                     ) 
  //                                     |   emptyStatement
  public boolean navigatorElementStatementBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "navigatorElementStatementBody")) return false;
    if (!nextTokenIs(b, "<navigator element statement body>", LBRACE, SEMI)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, NAVIGATOR_ELEMENT_STATEMENT_BODY, "<navigator element statement body>");
    r = navigatorElementStatementBody_0(b, l + 1);
    if (!r) r = emptyStatement(b, l + 1);
    exit_section_(b, l, m, NAVIGATOR_ELEMENT_STATEMENT_BODY, r, false, null);
    return r;
  }

  // LBRACE
  //                                         navigatorElementBodyStatement*
  //                                         RBRACE
  private boolean navigatorElementStatementBody_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "navigatorElementStatementBody_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, LBRACE);
    r = r && navigatorElementStatementBody_0_1(b, l + 1);
    r = r && consumeToken(b, RBRACE);
    exit_section_(b, m, null, r);
    return r;
  }

  // navigatorElementBodyStatement*
  private boolean navigatorElementStatementBody_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "navigatorElementStatementBody_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!navigatorElementBodyStatement(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "navigatorElementStatementBody_0_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // compoundID
  public boolean navigatorElementUsage(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "navigatorElementUsage")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, NAVIGATOR_ELEMENT_USAGE, "<navigator element usage>");
    r = compoundID(b, l + 1);
    exit_section_(b, l, m, NAVIGATOR_ELEMENT_USAGE, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // NAVIGATOR navigatorElementStatementBody
  public boolean navigatorStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "navigatorStatement")) return false;
    if (!nextTokenIs(b, NAVIGATOR)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, NAVIGATOR_STATEMENT, null);
    r = consumeToken(b, NAVIGATOR);
    p = r; // pin = 1
    r = r && navigatorElementStatementBody(b, l + 1);
    exit_section_(b, l, m, NAVIGATOR_STATEMENT, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // NEW ID SEMI
  public boolean neStub(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "neStub")) return false;
    if (!nextTokenIs(b, NEW)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, NE_STUB, null);
    r = consumeTokens(b, 1, NEW, ID, SEMI);
    p = r; // pin = 1
    exit_section_(b, l, m, NE_STUB, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // !(MOVE | NEW | SEMI | RBRACE | ID)
  boolean ne_statement_recover(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ne_statement_recover")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !ne_statement_recover_0(b, l + 1);
    exit_section_(b, l, m, null, r, false, null);
    return r;
  }

  // MOVE | NEW | SEMI | RBRACE | ID
  private boolean ne_statement_recover_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ne_statement_recover_0")) return false;
    boolean r;
    r = consumeToken(b, MOVE);
    if (!r) r = consumeToken(b, NEW);
    if (!r) r = consumeToken(b, SEMI);
    if (!r) r = consumeToken(b, RBRACE);
    if (!r) r = consumeToken(b, ID);
    return r;
  }

  /* ********************************************************** */
  // (NESTED manageSessionClause?)?
  public boolean nestedLocalModifier(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nestedLocalModifier")) return false;
    Marker m = enter_section_(b, l, _NONE_, NESTED_LOCAL_MODIFIER, "<nested local modifier>");
    nestedLocalModifier_0(b, l + 1);
    exit_section_(b, l, m, NESTED_LOCAL_MODIFIER, true, false, null);
    return true;
  }

  // NESTED manageSessionClause?
  private boolean nestedLocalModifier_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nestedLocalModifier_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, NESTED);
    r = r && nestedLocalModifier_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // manageSessionClause?
  private boolean nestedLocalModifier_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nestedLocalModifier_0_1")) return false;
    manageSessionClause(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // NESTED (LOCAL | (LBRAC nonEmptyNoContextPropertyUsageList RBRAC))? CLASSES?
  boolean nestedPropertiesSelector(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nestedPropertiesSelector")) return false;
    if (!nextTokenIs(b, NESTED)) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, NESTED);
    r = r && nestedPropertiesSelector_1(b, l + 1);
    r = r && nestedPropertiesSelector_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (LOCAL | (LBRAC nonEmptyNoContextPropertyUsageList RBRAC))?
  private boolean nestedPropertiesSelector_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nestedPropertiesSelector_1")) return false;
    nestedPropertiesSelector_1_0(b, l + 1);
    return true;
  }

  // LOCAL | (LBRAC nonEmptyNoContextPropertyUsageList RBRAC)
  private boolean nestedPropertiesSelector_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nestedPropertiesSelector_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, LOCAL);
    if (!r) r = nestedPropertiesSelector_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // LBRAC nonEmptyNoContextPropertyUsageList RBRAC
  private boolean nestedPropertiesSelector_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nestedPropertiesSelector_1_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, LBRAC);
    r = r && nonEmptyNoContextPropertyUsageList(b, l + 1);
    r = r && consumeToken(b, RBRAC);
    exit_section_(b, m, null, r);
    return r;
  }

  // CLASSES?
  private boolean nestedPropertiesSelector_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nestedPropertiesSelector_2")) return false;
    consumeToken(b, CLASSES);
    return true;
  }

  /* ********************************************************** */
  // NESTEDSESSION
  public boolean nestedSessionOperator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nestedSessionOperator")) return false;
    if (!nextTokenIs(b, NESTEDSESSION)) return false;
    boolean r;
    Marker m = enter_section_(b, l, NESTED_SESSION_OPERATOR);
    r = consumeToken(b, NESTEDSESSION);
    exit_section_(b, m, NESTED_SESSION_OPERATOR, r);
    return r;
  }

  /* ********************************************************** */
  // forAddObjClause
  //                               actionPropertyDefinitionBody
  public boolean newActionPropertyDefinitionBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "newActionPropertyDefinitionBody")) return false;
    if (!nextTokenIs(b, NEW)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, NEW_ACTION_PROPERTY_DEFINITION_BODY, null);
    r = forAddObjClause(b, l + 1);
    p = r; // pin = 1
    r = r && actionPropertyDefinitionBody(b, l + 1);
    exit_section_(b, l, m, NEW_ACTION_PROPERTY_DEFINITION_BODY, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // NEW componentDecl componentInsertPosition componentBody
  public boolean newComponentStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "newComponentStatement")) return false;
    if (!nextTokenIs(b, NEW)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, NEW_COMPONENT_STATEMENT, null);
    r = consumeToken(b, NEW);
    r = r && componentDecl(b, l + 1);
    p = r; // pin = 2
    r = r && componentInsertPosition(b, l + 1);
    r = r && componentBody(b, l + 1);
    exit_section_(b, l, m, NEW_COMPONENT_STATEMENT, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // NEWEXECUTOR actionPropertyDefinitionBody THREADS propertyExpression SEMI
  public boolean newExecutorActionPropertyDefinitionBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "newExecutorActionPropertyDefinitionBody")) return false;
    if (!nextTokenIs(b, NEWEXECUTOR)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, NEW_EXECUTOR_ACTION_PROPERTY_DEFINITION_BODY, null);
    r = consumeToken(b, NEWEXECUTOR);
    p = r; // pin = 1
    r = r && actionPropertyDefinitionBody(b, l + 1);
    r = r && consumeToken(b, THREADS);
    r = r && propertyExpression(b, l + 1);
    r = r && consumeToken(b, SEMI);
    exit_section_(b, l, m, NEW_EXECUTOR_ACTION_PROPERTY_DEFINITION_BODY, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // NEW navigatorElementDescription navigatorElementOptions navigatorElementStatementBody
  public boolean newNavigatorElementStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "newNavigatorElementStatement")) return false;
    if (!nextTokenIs(b, NEW)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, NEW_NAVIGATOR_ELEMENT_STATEMENT, null);
    r = consumeToken(b, NEW);
    r = r && navigatorElementDescription(b, l + 1);
    p = r; // pin = 2
    r = r && navigatorElementOptions(b, l + 1);
    r = r && navigatorElementStatementBody(b, l + 1);
    exit_section_(b, l, m, NEW_NAVIGATOR_ELEMENT_STATEMENT, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // (    newSessionOperator (NEWSQL)? (FORMS nonEmptyFormUsageList)? nestedPropertiesSelector?
  //                                            |    nestedSessionOperator
  //                                            )
  //                                            (SINGLE)?
  //                                            actionPropertyDefinitionBody
  public boolean newSessionActionPropertyDefinitionBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "newSessionActionPropertyDefinitionBody")) return false;
    if (!nextTokenIs(b, "<new session action property definition body>", NESTEDSESSION, NEWSESSION)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, NEW_SESSION_ACTION_PROPERTY_DEFINITION_BODY, "<new session action property definition body>");
    r = newSessionActionPropertyDefinitionBody_0(b, l + 1);
    p = r; // pin = 1
    r = r && newSessionActionPropertyDefinitionBody_1(b, l + 1);
    r = r && actionPropertyDefinitionBody(b, l + 1);
    exit_section_(b, l, m, NEW_SESSION_ACTION_PROPERTY_DEFINITION_BODY, r, p, null);
    return r || p;
  }

  // newSessionOperator (NEWSQL)? (FORMS nonEmptyFormUsageList)? nestedPropertiesSelector?
  //                                            |    nestedSessionOperator
  private boolean newSessionActionPropertyDefinitionBody_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "newSessionActionPropertyDefinitionBody_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = newSessionActionPropertyDefinitionBody_0_0(b, l + 1);
    if (!r) r = nestedSessionOperator(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // newSessionOperator (NEWSQL)? (FORMS nonEmptyFormUsageList)? nestedPropertiesSelector?
  private boolean newSessionActionPropertyDefinitionBody_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "newSessionActionPropertyDefinitionBody_0_0")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = newSessionOperator(b, l + 1);
    p = r; // pin = 1
    r = r && newSessionActionPropertyDefinitionBody_0_0_1(b, l + 1);
    r = r && newSessionActionPropertyDefinitionBody_0_0_2(b, l + 1);
    r = r && newSessionActionPropertyDefinitionBody_0_0_3(b, l + 1);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  // (NEWSQL)?
  private boolean newSessionActionPropertyDefinitionBody_0_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "newSessionActionPropertyDefinitionBody_0_0_1")) return false;
    consumeToken(b, NEWSQL);
    return true;
  }

  // (FORMS nonEmptyFormUsageList)?
  private boolean newSessionActionPropertyDefinitionBody_0_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "newSessionActionPropertyDefinitionBody_0_0_2")) return false;
    newSessionActionPropertyDefinitionBody_0_0_2_0(b, l + 1);
    return true;
  }

  // FORMS nonEmptyFormUsageList
  private boolean newSessionActionPropertyDefinitionBody_0_0_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "newSessionActionPropertyDefinitionBody_0_0_2_0")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, FORMS);
    p = r; // pin = 1
    r = r && nonEmptyFormUsageList(b, l + 1);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  // nestedPropertiesSelector?
  private boolean newSessionActionPropertyDefinitionBody_0_0_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "newSessionActionPropertyDefinitionBody_0_0_3")) return false;
    nestedPropertiesSelector(b, l + 1);
    return true;
  }

  // (SINGLE)?
  private boolean newSessionActionPropertyDefinitionBody_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "newSessionActionPropertyDefinitionBody_1")) return false;
    consumeToken(b, SINGLE);
    return true;
  }

  /* ********************************************************** */
  // NEWSESSION
  public boolean newSessionOperator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "newSessionOperator")) return false;
    if (!nextTokenIs(b, NEWSESSION)) return false;
    boolean r;
    Marker m = enter_section_(b, l, NEW_SESSION_OPERATOR);
    r = consumeToken(b, NEWSESSION);
    exit_section_(b, m, NEW_SESSION_OPERATOR, r);
    return r;
  }

  /* ********************************************************** */
  // NEWTHREAD actionPropertyDefinitionBody
  //                                         (   (   CONNECTION propertyExpression
  //                                             |   (SCHEDULE (PERIOD propertyExpression)? (DELAY propertyExpression)?)
  //                                             )
  //                                             SEMI
  //                                         )?
  public boolean newThreadActionPropertyDefinitionBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "newThreadActionPropertyDefinitionBody")) return false;
    if (!nextTokenIs(b, NEWTHREAD)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, NEW_THREAD_ACTION_PROPERTY_DEFINITION_BODY, null);
    r = consumeToken(b, NEWTHREAD);
    p = r; // pin = 1
    r = r && actionPropertyDefinitionBody(b, l + 1);
    r = r && newThreadActionPropertyDefinitionBody_2(b, l + 1);
    exit_section_(b, l, m, NEW_THREAD_ACTION_PROPERTY_DEFINITION_BODY, r, p, null);
    return r || p;
  }

  // (   (   CONNECTION propertyExpression
  //                                             |   (SCHEDULE (PERIOD propertyExpression)? (DELAY propertyExpression)?)
  //                                             )
  //                                             SEMI
  //                                         )?
  private boolean newThreadActionPropertyDefinitionBody_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "newThreadActionPropertyDefinitionBody_2")) return false;
    newThreadActionPropertyDefinitionBody_2_0(b, l + 1);
    return true;
  }

  // (   CONNECTION propertyExpression
  //                                             |   (SCHEDULE (PERIOD propertyExpression)? (DELAY propertyExpression)?)
  //                                             )
  //                                             SEMI
  private boolean newThreadActionPropertyDefinitionBody_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "newThreadActionPropertyDefinitionBody_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = newThreadActionPropertyDefinitionBody_2_0_0(b, l + 1);
    r = r && consumeToken(b, SEMI);
    exit_section_(b, m, null, r);
    return r;
  }

  // CONNECTION propertyExpression
  //                                             |   (SCHEDULE (PERIOD propertyExpression)? (DELAY propertyExpression)?)
  private boolean newThreadActionPropertyDefinitionBody_2_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "newThreadActionPropertyDefinitionBody_2_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = newThreadActionPropertyDefinitionBody_2_0_0_0(b, l + 1);
    if (!r) r = newThreadActionPropertyDefinitionBody_2_0_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // CONNECTION propertyExpression
  private boolean newThreadActionPropertyDefinitionBody_2_0_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "newThreadActionPropertyDefinitionBody_2_0_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, CONNECTION);
    r = r && propertyExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // SCHEDULE (PERIOD propertyExpression)? (DELAY propertyExpression)?
  private boolean newThreadActionPropertyDefinitionBody_2_0_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "newThreadActionPropertyDefinitionBody_2_0_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, SCHEDULE);
    r = r && newThreadActionPropertyDefinitionBody_2_0_0_1_1(b, l + 1);
    r = r && newThreadActionPropertyDefinitionBody_2_0_0_1_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (PERIOD propertyExpression)?
  private boolean newThreadActionPropertyDefinitionBody_2_0_0_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "newThreadActionPropertyDefinitionBody_2_0_0_1_1")) return false;
    newThreadActionPropertyDefinitionBody_2_0_0_1_1_0(b, l + 1);
    return true;
  }

  // PERIOD propertyExpression
  private boolean newThreadActionPropertyDefinitionBody_2_0_0_1_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "newThreadActionPropertyDefinitionBody_2_0_0_1_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, PERIOD);
    r = r && propertyExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (DELAY propertyExpression)?
  private boolean newThreadActionPropertyDefinitionBody_2_0_0_1_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "newThreadActionPropertyDefinitionBody_2_0_0_1_2")) return false;
    newThreadActionPropertyDefinitionBody_2_0_0_1_2_0(b, l + 1);
    return true;
  }

  // DELAY propertyExpression
  private boolean newThreadActionPropertyDefinitionBody_2_0_0_1_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "newThreadActionPropertyDefinitionBody_2_0_0_1_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, DELAY);
    r = r && propertyExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // NEW customClassUsage newWhereClause
  public boolean newWhereActionPropertyDefinitionBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "newWhereActionPropertyDefinitionBody")) return false;
    if (!nextTokenIs(b, NEW)) return false;
    boolean r;
    Marker m = enter_section_(b, l, NEW_WHERE_ACTION_PROPERTY_DEFINITION_BODY);
    r = consumeToken(b, NEW);
    r = r && customClassUsage(b, l + 1);
    r = r && newWhereClause(b, l + 1);
    exit_section_(b, m, NEW_WHERE_ACTION_PROPERTY_DEFINITION_BODY, r);
    return r;
  }

  /* ********************************************************** */
  // WHERE propertyExpression newWhereToClause?
  boolean newWhereClause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "newWhereClause")) return false;
    if (!nextTokenIs(b, WHERE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, WHERE);
    p = r; // pin = 1
    r = r && propertyExpression(b, l + 1);
    r = r && newWhereClause_2(b, l + 1);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  // newWhereToClause?
  private boolean newWhereClause_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "newWhereClause_2")) return false;
    newWhereToClause(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // TO mappedPropertyExprParam
  boolean newWhereToClause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "newWhereToClause")) return false;
    if (!nextTokenIs(b, TO)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, TO);
    p = r; // pin = 1
    r = r && mappedPropertyExprParam(b, l + 1);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // CANCEL | NOCANCEL
  public boolean noCancelClause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "noCancelClause")) return false;
    if (!nextTokenIs(b, "<no cancel clause>", CANCEL, NOCANCEL)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, NO_CANCEL_CLAUSE, "<no cancel clause>");
    r = consumeToken(b, CANCEL);
    if (!r) r = consumeToken(b, NOCANCEL);
    exit_section_(b, l, m, NO_CANCEL_CLAUSE, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // actionOrPropertyUsage
  public boolean noContextActionOrPropertyUsage(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "noContextActionOrPropertyUsage")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, NO_CONTEXT_ACTION_OR_PROPERTY_USAGE, "<no context action or property usage>");
    r = actionOrPropertyUsage(b, l + 1);
    exit_section_(b, l, m, NO_CONTEXT_ACTION_OR_PROPERTY_USAGE, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // actionUsage
  public boolean noContextActionUsage(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "noContextActionUsage")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, NO_CONTEXT_ACTION_USAGE, "<no context action usage>");
    r = actionUsage(b, l + 1);
    exit_section_(b, l, m, NO_CONTEXT_ACTION_USAGE, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // propertyUsage
  public boolean noContextPropertyUsage(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "noContextPropertyUsage")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, NO_CONTEXT_PROPERTY_USAGE, "<no context property usage>");
    r = propertyUsage(b, l + 1);
    exit_section_(b, l, m, NO_CONTEXT_PROPERTY_USAGE, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // NODEFAULT
  public boolean noDefault(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "noDefault")) return false;
    if (!nextTokenIs(b, NODEFAULT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, NO_DEFAULT);
    r = consumeToken(b, NODEFAULT);
    exit_section_(b, m, NO_DEFAULT, r);
    return r;
  }

  /* ********************************************************** */
  // ESCAPE | NOESCAPE
  public boolean noEscapeOption(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "noEscapeOption")) return false;
    if (!nextTokenIs(b, "<no escape option>", ESCAPE, NOESCAPE)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, NO_ESCAPE_OPTION, "<no escape option>");
    r = consumeToken(b, ESCAPE);
    if (!r) r = consumeToken(b, NOESCAPE);
    exit_section_(b, l, m, NO_ESCAPE_OPTION, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // actionUsage
  public boolean noParamsActionUsage(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "noParamsActionUsage")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, NO_PARAMS_ACTION_USAGE, "<no params action usage>");
    r = actionUsage(b, l + 1);
    exit_section_(b, l, m, NO_PARAMS_ACTION_USAGE, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // propertyUsage
  public boolean noParamsPropertyUsage(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "noParamsPropertyUsage")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, NO_PARAMS_PROPERTY_USAGE, "<no params property usage>");
    r = propertyUsage(b, l + 1);
    exit_section_(b, l, m, NO_PARAMS_PROPERTY_USAGE, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // actionPropertyDefinitionBody (COMMA actionPropertyDefinitionBody)*
  public boolean nonEmptyActionPDBList(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonEmptyActionPDBList")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, NON_EMPTY_ACTION_PDB_LIST, "<non empty action pdb list>");
    r = actionPropertyDefinitionBody(b, l + 1);
    p = r; // pin = 1
    r = r && nonEmptyActionPDBList_1(b, l + 1);
    exit_section_(b, l, m, NON_EMPTY_ACTION_PDB_LIST, r, p, null);
    return r || p;
  }

  // (COMMA actionPropertyDefinitionBody)*
  private boolean nonEmptyActionPDBList_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonEmptyActionPDBList_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!nonEmptyActionPDBList_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "nonEmptyActionPDBList_1", c)) break;
    }
    return true;
  }

  // COMMA actionPropertyDefinitionBody
  private boolean nonEmptyActionPDBList_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonEmptyActionPDBList_1_0")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, COMMA);
    p = r; // pin = 1
    r = r && actionPropertyDefinitionBody(b, l + 1);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // aliasedPropertyExpression (COMMA aliasedPropertyExpression)*
  public boolean nonEmptyAliasedPropertyExpressionList(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonEmptyAliasedPropertyExpressionList")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, NON_EMPTY_ALIASED_PROPERTY_EXPRESSION_LIST, "<non empty aliased property expression list>");
    r = aliasedPropertyExpression(b, l + 1);
    r = r && nonEmptyAliasedPropertyExpressionList_1(b, l + 1);
    exit_section_(b, l, m, NON_EMPTY_ALIASED_PROPERTY_EXPRESSION_LIST, r, false, null);
    return r;
  }

  // (COMMA aliasedPropertyExpression)*
  private boolean nonEmptyAliasedPropertyExpressionList_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonEmptyAliasedPropertyExpressionList_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!nonEmptyAliasedPropertyExpressionList_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "nonEmptyAliasedPropertyExpressionList_1", c)) break;
    }
    return true;
  }

  // COMMA aliasedPropertyExpression
  private boolean nonEmptyAliasedPropertyExpressionList_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonEmptyAliasedPropertyExpressionList_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, COMMA);
    r = r && aliasedPropertyExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // className (COMMA className)*
  public boolean nonEmptyClassNameList(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonEmptyClassNameList")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, NON_EMPTY_CLASS_NAME_LIST, "<non empty class name list>");
    r = className(b, l + 1);
    p = r; // pin = 1
    r = r && nonEmptyClassNameList_1(b, l + 1);
    exit_section_(b, l, m, NON_EMPTY_CLASS_NAME_LIST, r, p, null);
    return r || p;
  }

  // (COMMA className)*
  private boolean nonEmptyClassNameList_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonEmptyClassNameList_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!nonEmptyClassNameList_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "nonEmptyClassNameList_1", c)) break;
    }
    return true;
  }

  // COMMA className
  private boolean nonEmptyClassNameList_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonEmptyClassNameList_1_0")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, COMMA);
    p = r; // pin = 1
    r = r && className(b, l + 1);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // classParamDeclare (COMMA classParamDeclare)*
  public boolean nonEmptyClassParamDeclareList(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonEmptyClassParamDeclareList")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, NON_EMPTY_CLASS_PARAM_DECLARE_LIST, "<non empty class param declare list>");
    r = classParamDeclare(b, l + 1);
    r = r && nonEmptyClassParamDeclareList_1(b, l + 1);
    exit_section_(b, l, m, NON_EMPTY_CLASS_PARAM_DECLARE_LIST, r, false, null);
    return r;
  }

  // (COMMA classParamDeclare)*
  private boolean nonEmptyClassParamDeclareList_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonEmptyClassParamDeclareList_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!nonEmptyClassParamDeclareList_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "nonEmptyClassParamDeclareList_1", c)) break;
    }
    return true;
  }

  // COMMA classParamDeclare
  private boolean nonEmptyClassParamDeclareList_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonEmptyClassParamDeclareList_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, COMMA);
    r = r && classParamDeclare(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // customClassUsage (COMMA customClassUsage)*
  public boolean nonEmptyCustomClassUsageList(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonEmptyCustomClassUsageList")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, NON_EMPTY_CUSTOM_CLASS_USAGE_LIST, "<non empty custom class usage list>");
    r = customClassUsage(b, l + 1);
    p = r; // pin = 1
    r = r && nonEmptyCustomClassUsageList_1(b, l + 1);
    exit_section_(b, l, m, NON_EMPTY_CUSTOM_CLASS_USAGE_LIST, r, p, null);
    return r || p;
  }

  // (COMMA customClassUsage)*
  private boolean nonEmptyCustomClassUsageList_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonEmptyCustomClassUsageList_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!nonEmptyCustomClassUsageList_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "nonEmptyCustomClassUsageList_1", c)) break;
    }
    return true;
  }

  // COMMA customClassUsage
  private boolean nonEmptyCustomClassUsageList_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonEmptyCustomClassUsageList_1_0")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, COMMA);
    p = r; // pin = 1
    r = r && customClassUsage(b, l + 1);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // explicitPropClass (COMMA explicitPropClass)*
  public boolean nonEmptyExplicitPropClassList(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonEmptyExplicitPropClassList")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, NON_EMPTY_EXPLICIT_PROP_CLASS_LIST, "<non empty explicit prop class list>");
    r = explicitPropClass(b, l + 1);
    r = r && nonEmptyExplicitPropClassList_1(b, l + 1);
    exit_section_(b, l, m, NON_EMPTY_EXPLICIT_PROP_CLASS_LIST, r, false, null);
    return r;
  }

  // (COMMA explicitPropClass)*
  private boolean nonEmptyExplicitPropClassList_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonEmptyExplicitPropClassList_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!nonEmptyExplicitPropClassList_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "nonEmptyExplicitPropClassList_1", c)) break;
    }
    return true;
  }

  // COMMA explicitPropClass
  private boolean nonEmptyExplicitPropClassList_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonEmptyExplicitPropClassList_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, COMMA);
    r = r && explicitPropClass(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // formUsage (COMMA formUsage)*
  public boolean nonEmptyFormUsageList(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonEmptyFormUsageList")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, NON_EMPTY_FORM_USAGE_LIST, "<non empty form usage list>");
    r = formUsage(b, l + 1);
    p = r; // pin = 1
    r = r && nonEmptyFormUsageList_1(b, l + 1);
    exit_section_(b, l, m, NON_EMPTY_FORM_USAGE_LIST, r, p, null);
    return r || p;
  }

  // (COMMA formUsage)*
  private boolean nonEmptyFormUsageList_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonEmptyFormUsageList_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!nonEmptyFormUsageList_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "nonEmptyFormUsageList_1", c)) break;
    }
    return true;
  }

  // COMMA formUsage
  private boolean nonEmptyFormUsageList_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonEmptyFormUsageList_1_0")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, COMMA);
    p = r; // pin = 1
    r = r && formUsage(b, l + 1);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // groupObjectUsage (COMMA groupObjectUsage)*
  public boolean nonEmptyGroupObjectUsageList(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonEmptyGroupObjectUsageList")) return false;
    if (!nextTokenIs(b, ID)) return false;
    boolean r;
    Marker m = enter_section_(b, l, NON_EMPTY_GROUP_OBJECT_USAGE_LIST);
    r = groupObjectUsage(b, l + 1);
    r = r && nonEmptyGroupObjectUsageList_1(b, l + 1);
    exit_section_(b, m, NON_EMPTY_GROUP_OBJECT_USAGE_LIST, r);
    return r;
  }

  // (COMMA groupObjectUsage)*
  private boolean nonEmptyGroupObjectUsageList_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonEmptyGroupObjectUsageList_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!nonEmptyGroupObjectUsageList_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "nonEmptyGroupObjectUsageList_1", c)) break;
    }
    return true;
  }

  // COMMA groupObjectUsage
  private boolean nonEmptyGroupObjectUsageList_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonEmptyGroupObjectUsageList_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, COMMA);
    r = r && groupObjectUsage(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // importFieldDefinition (COMMA importFieldDefinition)*
  public boolean nonEmptyImportFieldDefinitions(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonEmptyImportFieldDefinitions")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, NON_EMPTY_IMPORT_FIELD_DEFINITIONS, "<non empty import field definitions>");
    r = importFieldDefinition(b, l + 1);
    r = r && nonEmptyImportFieldDefinitions_1(b, l + 1);
    exit_section_(b, l, m, NON_EMPTY_IMPORT_FIELD_DEFINITIONS, r, false, null);
    return r;
  }

  // (COMMA importFieldDefinition)*
  private boolean nonEmptyImportFieldDefinitions_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonEmptyImportFieldDefinitions_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!nonEmptyImportFieldDefinitions_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "nonEmptyImportFieldDefinitions_1", c)) break;
    }
    return true;
  }

  // COMMA importFieldDefinition
  private boolean nonEmptyImportFieldDefinitions_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonEmptyImportFieldDefinitions_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, COMMA);
    r = r && importFieldDefinition(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // importPropertyUsageWithId (COMMA importPropertyUsageWithId)*
  public boolean nonEmptyImportPropertyUsageListWithIds(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonEmptyImportPropertyUsageListWithIds")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, NON_EMPTY_IMPORT_PROPERTY_USAGE_LIST_WITH_IDS, "<non empty import property usage list with ids>");
    r = importPropertyUsageWithId(b, l + 1);
    r = r && nonEmptyImportPropertyUsageListWithIds_1(b, l + 1);
    exit_section_(b, l, m, NON_EMPTY_IMPORT_PROPERTY_USAGE_LIST_WITH_IDS, r, false, null);
    return r;
  }

  // (COMMA importPropertyUsageWithId)*
  private boolean nonEmptyImportPropertyUsageListWithIds_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonEmptyImportPropertyUsageListWithIds_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!nonEmptyImportPropertyUsageListWithIds_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "nonEmptyImportPropertyUsageListWithIds_1", c)) break;
    }
    return true;
  }

  // COMMA importPropertyUsageWithId
  private boolean nonEmptyImportPropertyUsageListWithIds_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonEmptyImportPropertyUsageListWithIds_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, COMMA);
    r = r && importPropertyUsageWithId(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // localPropertyDeclarationName (COMMA localPropertyDeclarationName)*
  public boolean nonEmptyLocalPropertyDeclarationNameList(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonEmptyLocalPropertyDeclarationNameList")) return false;
    if (!nextTokenIs(b, ID)) return false;
    boolean r;
    Marker m = enter_section_(b, l, NON_EMPTY_LOCAL_PROPERTY_DECLARATION_NAME_LIST);
    r = localPropertyDeclarationName(b, l + 1);
    r = r && nonEmptyLocalPropertyDeclarationNameList_1(b, l + 1);
    exit_section_(b, m, NON_EMPTY_LOCAL_PROPERTY_DECLARATION_NAME_LIST, r);
    return r;
  }

  // (COMMA localPropertyDeclarationName)*
  private boolean nonEmptyLocalPropertyDeclarationNameList_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonEmptyLocalPropertyDeclarationNameList_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!nonEmptyLocalPropertyDeclarationNameList_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "nonEmptyLocalPropertyDeclarationNameList_1", c)) break;
    }
    return true;
  }

  // COMMA localPropertyDeclarationName
  private boolean nonEmptyLocalPropertyDeclarationNameList_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonEmptyLocalPropertyDeclarationNameList_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, COMMA);
    r = r && localPropertyDeclarationName(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // mappedPropertyOrSimpleExprParam (COMMA mappedPropertyOrSimpleExprParam)*
  public boolean nonEmptyMappedPropertyOrSimpleExprParamList(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonEmptyMappedPropertyOrSimpleExprParamList")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, NON_EMPTY_MAPPED_PROPERTY_OR_SIMPLE_EXPR_PARAM_LIST, "<non empty mapped property or simple expr param list>");
    r = mappedPropertyOrSimpleExprParam(b, l + 1);
    p = r; // pin = 1
    r = r && nonEmptyMappedPropertyOrSimpleExprParamList_1(b, l + 1);
    exit_section_(b, l, m, NON_EMPTY_MAPPED_PROPERTY_OR_SIMPLE_EXPR_PARAM_LIST, r, p, null);
    return r || p;
  }

  // (COMMA mappedPropertyOrSimpleExprParam)*
  private boolean nonEmptyMappedPropertyOrSimpleExprParamList_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonEmptyMappedPropertyOrSimpleExprParamList_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!nonEmptyMappedPropertyOrSimpleExprParamList_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "nonEmptyMappedPropertyOrSimpleExprParamList_1", c)) break;
    }
    return true;
  }

  // COMMA mappedPropertyOrSimpleExprParam
  private boolean nonEmptyMappedPropertyOrSimpleExprParamList_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonEmptyMappedPropertyOrSimpleExprParamList_1_0")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, COMMA);
    p = r; // pin = 1
    r = r && mappedPropertyOrSimpleExprParam(b, l + 1);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // moduleUsage (COMMA moduleUsage)*
  public boolean nonEmptyModuleUsageList(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonEmptyModuleUsageList")) return false;
    if (!nextTokenIs(b, ID)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, NON_EMPTY_MODULE_USAGE_LIST, null);
    r = moduleUsage(b, l + 1);
    p = r; // pin = 1
    r = r && nonEmptyModuleUsageList_1(b, l + 1);
    exit_section_(b, l, m, NON_EMPTY_MODULE_USAGE_LIST, r, p, null);
    return r || p;
  }

  // (COMMA moduleUsage)*
  private boolean nonEmptyModuleUsageList_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonEmptyModuleUsageList_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!nonEmptyModuleUsageList_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "nonEmptyModuleUsageList_1", c)) break;
    }
    return true;
  }

  // COMMA moduleUsage
  private boolean nonEmptyModuleUsageList_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonEmptyModuleUsageList_1_0")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, COMMA);
    p = r; // pin = 1
    r = r && moduleUsage(b, l + 1);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // namespaceUsage (COMMA namespaceUsage)*
  public boolean nonEmptyNamespaceUsageList(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonEmptyNamespaceUsageList")) return false;
    if (!nextTokenIs(b, ID)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, NON_EMPTY_NAMESPACE_USAGE_LIST, null);
    r = namespaceUsage(b, l + 1);
    p = r; // pin = 1
    r = r && nonEmptyNamespaceUsageList_1(b, l + 1);
    exit_section_(b, l, m, NON_EMPTY_NAMESPACE_USAGE_LIST, r, p, null);
    return r || p;
  }

  // (COMMA namespaceUsage)*
  private boolean nonEmptyNamespaceUsageList_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonEmptyNamespaceUsageList_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!nonEmptyNamespaceUsageList_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "nonEmptyNamespaceUsageList_1", c)) break;
    }
    return true;
  }

  // COMMA namespaceUsage
  private boolean nonEmptyNamespaceUsageList_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonEmptyNamespaceUsageList_1_0")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, COMMA);
    p = r; // pin = 1
    r = r && namespaceUsage(b, l + 1);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // noContextActionOrPropertyUsage (COMMA noContextActionOrPropertyUsage)*
  public boolean nonEmptyNoContextActionOrPropertyUsageList(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonEmptyNoContextActionOrPropertyUsageList")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, NON_EMPTY_NO_CONTEXT_ACTION_OR_PROPERTY_USAGE_LIST, "<non empty no context action or property usage list>");
    r = noContextActionOrPropertyUsage(b, l + 1);
    p = r; // pin = 1
    r = r && nonEmptyNoContextActionOrPropertyUsageList_1(b, l + 1);
    exit_section_(b, l, m, NON_EMPTY_NO_CONTEXT_ACTION_OR_PROPERTY_USAGE_LIST, r, p, null);
    return r || p;
  }

  // (COMMA noContextActionOrPropertyUsage)*
  private boolean nonEmptyNoContextActionOrPropertyUsageList_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonEmptyNoContextActionOrPropertyUsageList_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!nonEmptyNoContextActionOrPropertyUsageList_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "nonEmptyNoContextActionOrPropertyUsageList_1", c)) break;
    }
    return true;
  }

  // COMMA noContextActionOrPropertyUsage
  private boolean nonEmptyNoContextActionOrPropertyUsageList_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonEmptyNoContextActionOrPropertyUsageList_1_0")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, COMMA);
    p = r; // pin = 1
    r = r && noContextActionOrPropertyUsage(b, l + 1);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // noContextActionUsage (COMMA noContextActionUsage)*
  public boolean nonEmptyNoContextActionUsageList(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonEmptyNoContextActionUsageList")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, NON_EMPTY_NO_CONTEXT_ACTION_USAGE_LIST, "<non empty no context action usage list>");
    r = noContextActionUsage(b, l + 1);
    p = r; // pin = 1
    r = r && nonEmptyNoContextActionUsageList_1(b, l + 1);
    exit_section_(b, l, m, NON_EMPTY_NO_CONTEXT_ACTION_USAGE_LIST, r, p, null);
    return r || p;
  }

  // (COMMA noContextActionUsage)*
  private boolean nonEmptyNoContextActionUsageList_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonEmptyNoContextActionUsageList_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!nonEmptyNoContextActionUsageList_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "nonEmptyNoContextActionUsageList_1", c)) break;
    }
    return true;
  }

  // COMMA noContextActionUsage
  private boolean nonEmptyNoContextActionUsageList_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonEmptyNoContextActionUsageList_1_0")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, COMMA);
    p = r; // pin = 1
    r = r && noContextActionUsage(b, l + 1);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // noContextPropertyUsage (COMMA noContextPropertyUsage)*
  public boolean nonEmptyNoContextPropertyUsageList(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonEmptyNoContextPropertyUsageList")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, NON_EMPTY_NO_CONTEXT_PROPERTY_USAGE_LIST, "<non empty no context property usage list>");
    r = noContextPropertyUsage(b, l + 1);
    p = r; // pin = 1
    r = r && nonEmptyNoContextPropertyUsageList_1(b, l + 1);
    exit_section_(b, l, m, NON_EMPTY_NO_CONTEXT_PROPERTY_USAGE_LIST, r, p, null);
    return r || p;
  }

  // (COMMA noContextPropertyUsage)*
  private boolean nonEmptyNoContextPropertyUsageList_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonEmptyNoContextPropertyUsageList_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!nonEmptyNoContextPropertyUsageList_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "nonEmptyNoContextPropertyUsageList_1", c)) break;
    }
    return true;
  }

  // COMMA noContextPropertyUsage
  private boolean nonEmptyNoContextPropertyUsageList_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonEmptyNoContextPropertyUsageList_1_0")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, COMMA);
    p = r; // pin = 1
    r = r && noContextPropertyUsage(b, l + 1);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // noParamsPropertyUsage (COMMA noParamsPropertyUsage)*
  public boolean nonEmptyNoParamsPropertyUsageList(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonEmptyNoParamsPropertyUsageList")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, NON_EMPTY_NO_PARAMS_PROPERTY_USAGE_LIST, "<non empty no params property usage list>");
    r = noParamsPropertyUsage(b, l + 1);
    p = r; // pin = 1
    r = r && nonEmptyNoParamsPropertyUsageList_1(b, l + 1);
    exit_section_(b, l, m, NON_EMPTY_NO_PARAMS_PROPERTY_USAGE_LIST, r, p, null);
    return r || p;
  }

  // (COMMA noParamsPropertyUsage)*
  private boolean nonEmptyNoParamsPropertyUsageList_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonEmptyNoParamsPropertyUsageList_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!nonEmptyNoParamsPropertyUsageList_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "nonEmptyNoParamsPropertyUsageList_1", c)) break;
    }
    return true;
  }

  // COMMA noParamsPropertyUsage
  private boolean nonEmptyNoParamsPropertyUsageList_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonEmptyNoParamsPropertyUsageList_1_0")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, COMMA);
    p = r; // pin = 1
    r = r && noParamsPropertyUsage(b, l + 1);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // objectUsage (COMMA objectUsage)*
  public boolean nonEmptyObjectUsageList(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonEmptyObjectUsageList")) return false;
    if (!nextTokenIs(b, ID)) return false;
    boolean r;
    Marker m = enter_section_(b, l, NON_EMPTY_OBJECT_USAGE_LIST);
    r = objectUsage(b, l + 1);
    r = r && nonEmptyObjectUsageList_1(b, l + 1);
    exit_section_(b, m, NON_EMPTY_OBJECT_USAGE_LIST, r);
    return r;
  }

  // (COMMA objectUsage)*
  private boolean nonEmptyObjectUsageList_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonEmptyObjectUsageList_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!nonEmptyObjectUsageList_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "nonEmptyObjectUsageList_1", c)) break;
    }
    return true;
  }

  // COMMA objectUsage
  private boolean nonEmptyObjectUsageList_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonEmptyObjectUsageList_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, COMMA);
    r = r && objectUsage(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // parameterOrExpression (COMMA parameterOrExpression)*
  public boolean nonEmptyParameterOrExpressionList(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonEmptyParameterOrExpressionList")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, NON_EMPTY_PARAMETER_OR_EXPRESSION_LIST, "<non empty parameter or expression list>");
    r = parameterOrExpression(b, l + 1);
    p = r; // pin = 1
    r = r && nonEmptyParameterOrExpressionList_1(b, l + 1);
    exit_section_(b, l, m, NON_EMPTY_PARAMETER_OR_EXPRESSION_LIST, r, p, null);
    return r || p;
  }

  // (COMMA parameterOrExpression)*
  private boolean nonEmptyParameterOrExpressionList_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonEmptyParameterOrExpressionList_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!nonEmptyParameterOrExpressionList_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "nonEmptyParameterOrExpressionList_1", c)) break;
    }
    return true;
  }

  // COMMA parameterOrExpression
  private boolean nonEmptyParameterOrExpressionList_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonEmptyParameterOrExpressionList_1_0")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, COMMA);
    p = r; // pin = 1
    r = r && parameterOrExpression(b, l + 1);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // propertyExpression (COMMA propertyExpression)*
  public boolean nonEmptyPropertyExpressionList(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonEmptyPropertyExpressionList")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, NON_EMPTY_PROPERTY_EXPRESSION_LIST, "<non empty property expression list>");
    r = propertyExpression(b, l + 1);
    p = r; // pin = 1
    r = r && nonEmptyPropertyExpressionList_1(b, l + 1);
    exit_section_(b, l, m, NON_EMPTY_PROPERTY_EXPRESSION_LIST, r, p, null);
    return r || p;
  }

  // (COMMA propertyExpression)*
  private boolean nonEmptyPropertyExpressionList_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonEmptyPropertyExpressionList_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!nonEmptyPropertyExpressionList_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "nonEmptyPropertyExpressionList_1", c)) break;
    }
    return true;
  }

  // COMMA propertyExpression
  private boolean nonEmptyPropertyExpressionList_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonEmptyPropertyExpressionList_1_0")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, COMMA);
    p = r; // pin = 1
    r = r && propertyExpression(b, l + 1);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // (nonSemiPropertyOption* semiPropertyOption)+ SEMI | (semiPropertyOption* nonSemiPropertyOption)+
  public boolean nonEmptyPropertyOptions(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonEmptyPropertyOptions")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, NON_EMPTY_PROPERTY_OPTIONS, "<non empty property options>");
    r = nonEmptyPropertyOptions_0(b, l + 1);
    if (!r) r = nonEmptyPropertyOptions_1(b, l + 1);
    exit_section_(b, l, m, NON_EMPTY_PROPERTY_OPTIONS, r, false, null);
    return r;
  }

  // (nonSemiPropertyOption* semiPropertyOption)+ SEMI
  private boolean nonEmptyPropertyOptions_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonEmptyPropertyOptions_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = nonEmptyPropertyOptions_0_0(b, l + 1);
    r = r && consumeToken(b, SEMI);
    exit_section_(b, m, null, r);
    return r;
  }

  // (nonSemiPropertyOption* semiPropertyOption)+
  private boolean nonEmptyPropertyOptions_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonEmptyPropertyOptions_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = nonEmptyPropertyOptions_0_0_0(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!nonEmptyPropertyOptions_0_0_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "nonEmptyPropertyOptions_0_0", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // nonSemiPropertyOption* semiPropertyOption
  private boolean nonEmptyPropertyOptions_0_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonEmptyPropertyOptions_0_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = nonEmptyPropertyOptions_0_0_0_0(b, l + 1);
    r = r && semiPropertyOption(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // nonSemiPropertyOption*
  private boolean nonEmptyPropertyOptions_0_0_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonEmptyPropertyOptions_0_0_0_0")) return false;
    while (true) {
      int c = current_position_(b);
      if (!nonSemiPropertyOption(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "nonEmptyPropertyOptions_0_0_0_0", c)) break;
    }
    return true;
  }

  // (semiPropertyOption* nonSemiPropertyOption)+
  private boolean nonEmptyPropertyOptions_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonEmptyPropertyOptions_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = nonEmptyPropertyOptions_1_0(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!nonEmptyPropertyOptions_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "nonEmptyPropertyOptions_1", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // semiPropertyOption* nonSemiPropertyOption
  private boolean nonEmptyPropertyOptions_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonEmptyPropertyOptions_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = nonEmptyPropertyOptions_1_0_0(b, l + 1);
    r = r && nonSemiPropertyOption(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // semiPropertyOption*
  private boolean nonEmptyPropertyOptions_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonEmptyPropertyOptions_1_0_0")) return false;
    while (true) {
      int c = current_position_(b);
      if (!semiPropertyOption(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "nonEmptyPropertyOptions_1_0_0", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // staticObjectDecl staticObjectImage? (COMMA staticObjectDecl staticObjectImage?)*
  public boolean nonEmptyStaticObjectDeclList(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonEmptyStaticObjectDeclList")) return false;
    if (!nextTokenIs(b, ID)) return false;
    boolean r;
    Marker m = enter_section_(b, l, NON_EMPTY_STATIC_OBJECT_DECL_LIST);
    r = staticObjectDecl(b, l + 1);
    r = r && nonEmptyStaticObjectDeclList_1(b, l + 1);
    r = r && nonEmptyStaticObjectDeclList_2(b, l + 1);
    exit_section_(b, m, NON_EMPTY_STATIC_OBJECT_DECL_LIST, r);
    return r;
  }

  // staticObjectImage?
  private boolean nonEmptyStaticObjectDeclList_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonEmptyStaticObjectDeclList_1")) return false;
    staticObjectImage(b, l + 1);
    return true;
  }

  // (COMMA staticObjectDecl staticObjectImage?)*
  private boolean nonEmptyStaticObjectDeclList_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonEmptyStaticObjectDeclList_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!nonEmptyStaticObjectDeclList_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "nonEmptyStaticObjectDeclList_2", c)) break;
    }
    return true;
  }

  // COMMA staticObjectDecl staticObjectImage?
  private boolean nonEmptyStaticObjectDeclList_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonEmptyStaticObjectDeclList_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, COMMA);
    r = r && staticObjectDecl(b, l + 1);
    r = r && nonEmptyStaticObjectDeclList_2_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // staticObjectImage?
  private boolean nonEmptyStaticObjectDeclList_2_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nonEmptyStaticObjectDeclList_2_0_2")) return false;
    staticObjectImage(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // onEditEventSetting
  boolean nonSemiPropertyOption(PsiBuilder b, int l) {
    return onEditEventSetting(b, l + 1);
  }

  /* ********************************************************** */
  // nullOption (DELETE)? baseEventNotPE
  public boolean notNullSetting(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "notNullSetting")) return false;
    if (!nextTokenIs(b, NONULL)) return false;
    boolean r;
    Marker m = enter_section_(b, l, NOT_NULL_SETTING);
    r = nullOption(b, l + 1);
    r = r && notNullSetting_1(b, l + 1);
    r = r && baseEventNotPE(b, l + 1);
    exit_section_(b, m, NOT_NULL_SETTING, r);
    return r;
  }

  // (DELETE)?
  private boolean notNullSetting_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "notNullSetting_1")) return false;
    consumeToken(b, DELETE);
    return true;
  }

  /* ********************************************************** */
  // (NOT notPE) | equalityPE
  public boolean notPE(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "notPE")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, NOT_PE, "<not pe>");
    r = notPE_0(b, l + 1);
    if (!r) r = equalityPE(b, l + 1);
    exit_section_(b, l, m, NOT_PE, r, false, null);
    return r;
  }

  // NOT notPE
  private boolean notPE_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "notPE_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, NOT);
    r = r && notPE(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // NULL
  public boolean nullLiteral(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nullLiteral")) return false;
    if (!nextTokenIs(b, NULL)) return false;
    boolean r;
    Marker m = enter_section_(b, l, NULL_LITERAL);
    r = consumeToken(b, NULL);
    exit_section_(b, m, NULL_LITERAL, r);
    return r;
  }

  /* ********************************************************** */
  // NONULL
  public boolean nullOption(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nullOption")) return false;
    if (!nextTokenIs(b, NONULL)) return false;
    boolean r;
    Marker m = enter_section_(b, l, NULL_OPTION);
    r = consumeToken(b, NONULL);
    exit_section_(b, m, NULL_OPTION, r);
    return r;
  }

  /* ********************************************************** */
  // objectUsage EQUALS propertyExpression
  public boolean objectExpr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "objectExpr")) return false;
    if (!nextTokenIs(b, ID)) return false;
    boolean r;
    Marker m = enter_section_(b, l, OBJECT_EXPR);
    r = objectUsage(b, l + 1);
    r = r && consumeToken(b, EQUALS);
    r = r && propertyExpression(b, l + 1);
    exit_section_(b, m, OBJECT_EXPR, r);
    return r;
  }

  /* ********************************************************** */
  // <<innerIDCheck>> formUsage POINT objectUsage
  public boolean objectID(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "objectID")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, OBJECT_ID, "<object id>");
    r = innerIDCheck(b, l + 1);
    r = r && formUsage(b, l + 1);
    r = r && consumeToken(b, POINT);
    p = r; // pin = 3
    r = r && objectUsage(b, l + 1);
    exit_section_(b, l, m, OBJECT_ID, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // EQUALS propertyExpression NULL?
  public boolean objectInProps(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "objectInProps")) return false;
    if (!nextTokenIs(b, EQUALS)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, OBJECT_IN_PROPS, null);
    r = consumeToken(b, EQUALS);
    p = r; // pin = 1
    r = r && propertyExpression(b, l + 1);
    r = r && objectInProps_2(b, l + 1);
    exit_section_(b, l, m, OBJECT_IN_PROPS, r, p, null);
    return r || p;
  }

  // NULL?
  private boolean objectInProps_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "objectInProps_2")) return false;
    consumeToken(b, NULL);
    return true;
  }

  /* ********************************************************** */
  // (INPUT | changeInput) simpleName? NULL? staticDestination? constraintFilter? objectListInputProps?
  public boolean objectInputProps(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "objectInputProps")) return false;
    if (!nextTokenIs(b, "<object input props>", CHANGE, INPUT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, OBJECT_INPUT_PROPS, "<object input props>");
    r = objectInputProps_0(b, l + 1);
    p = r; // pin = 1
    r = r && objectInputProps_1(b, l + 1);
    r = r && objectInputProps_2(b, l + 1);
    r = r && objectInputProps_3(b, l + 1);
    r = r && objectInputProps_4(b, l + 1);
    r = r && objectInputProps_5(b, l + 1);
    exit_section_(b, l, m, OBJECT_INPUT_PROPS, r, p, null);
    return r || p;
  }

  // INPUT | changeInput
  private boolean objectInputProps_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "objectInputProps_0")) return false;
    boolean r;
    r = consumeToken(b, INPUT);
    if (!r) r = changeInput(b, l + 1);
    return r;
  }

  // simpleName?
  private boolean objectInputProps_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "objectInputProps_1")) return false;
    simpleName(b, l + 1);
    return true;
  }

  // NULL?
  private boolean objectInputProps_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "objectInputProps_2")) return false;
    consumeToken(b, NULL);
    return true;
  }

  // staticDestination?
  private boolean objectInputProps_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "objectInputProps_3")) return false;
    staticDestination(b, l + 1);
    return true;
  }

  // constraintFilter?
  private boolean objectInputProps_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "objectInputProps_4")) return false;
    constraintFilter(b, l + 1);
    return true;
  }

  // objectListInputProps?
  private boolean objectInputProps_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "objectInputProps_5")) return false;
    objectListInputProps(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // LIST propertyExpression
  public boolean objectListInputProps(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "objectListInputProps")) return false;
    if (!nextTokenIs(b, LIST)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, OBJECT_LIST_INPUT_PROPS, null);
    r = consumeToken(b, LIST);
    p = r; // pin = 1
    r = r && propertyExpression(b, l + 1);
    exit_section_(b, l, m, OBJECT_LIST_INPUT_PROPS, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // simpleName
  public boolean objectUsage(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "objectUsage")) return false;
    if (!nextTokenIs(b, ID)) return false;
    boolean r;
    Marker m = enter_section_(b, l, OBJECT_USAGE);
    r = simpleName(b, l + 1);
    exit_section_(b, m, OBJECT_USAGE, r);
    return r;
  }

  /* ********************************************************** */
  // (nonEmptyObjectUsageList)?
  public boolean objectUsageList(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "objectUsageList")) return false;
    Marker m = enter_section_(b, l, _NONE_, OBJECT_USAGE_LIST, "<object usage list>");
    objectUsageList_0(b, l + 1);
    exit_section_(b, l, m, OBJECT_USAGE_LIST, true, false, null);
    return true;
  }

  // (nonEmptyObjectUsageList)
  private boolean objectUsageList_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "objectUsageList_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = nonEmptyObjectUsageList(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // ON formEventType topActionPropertyDefinitionBody
  public boolean onEditEventSetting(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "onEditEventSetting")) return false;
    if (!nextTokenIs(b, ON)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, ON_EDIT_EVENT_SETTING, null);
    r = consumeToken(b, ON);
    r = r && formEventType(b, l + 1);
    p = r; // pin = 2
    r = r && topActionPropertyDefinitionBody(b, l + 1);
    exit_section_(b, l, m, ON_EDIT_EVENT_SETTING, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // xorPE (OR xorPE)*
  public boolean orPE(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "orPE")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OR_PE, "<or pe>");
    r = xorPE(b, l + 1);
    r = r && orPE_1(b, l + 1);
    exit_section_(b, l, m, OR_PE, r, false, null);
    return r;
  }

  // (OR xorPE)*
  private boolean orPE_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "orPE_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!orPE_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "orPE_1", c)) break;
    }
    return true;
  }

  // OR xorPE
  private boolean orPE_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "orPE_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, OR);
    r = r && xorPE(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // ORDER groupObjectID (FROM propertyUsage)?
  public boolean orderActionPropertyDefinitionBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "orderActionPropertyDefinitionBody")) return false;
    if (!nextTokenIs(b, ORDER)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, ORDER_ACTION_PROPERTY_DEFINITION_BODY, null);
    r = consumeToken(b, ORDER);
    r = r && groupObjectID(b, l + 1);
    p = r; // pin = 2
    r = r && orderActionPropertyDefinitionBody_2(b, l + 1);
    exit_section_(b, l, m, ORDER_ACTION_PROPERTY_DEFINITION_BODY, r, p, null);
    return r || p;
  }

  // (FROM propertyUsage)?
  private boolean orderActionPropertyDefinitionBody_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "orderActionPropertyDefinitionBody_2")) return false;
    orderActionPropertyDefinitionBody_2_0(b, l + 1);
    return true;
  }

  // FROM propertyUsage
  private boolean orderActionPropertyDefinitionBody_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "orderActionPropertyDefinitionBody_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, FROM);
    r = r && propertyUsage(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // ORDER (DESC)? nonEmptyPropertyExpressionList
  public boolean orderPropertyBy(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "orderPropertyBy")) return false;
    if (!nextTokenIs(b, ORDER)) return false;
    boolean r;
    Marker m = enter_section_(b, l, ORDER_PROPERTY_BY);
    r = consumeToken(b, ORDER);
    r = r && orderPropertyBy_1(b, l + 1);
    r = r && nonEmptyPropertyExpressionList(b, l + 1);
    exit_section_(b, m, ORDER_PROPERTY_BY, r);
    return r;
  }

  // (DESC)?
  private boolean orderPropertyBy_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "orderPropertyBy_1")) return false;
    consumeToken(b, DESC);
    return true;
  }

  /* ********************************************************** */
  // VERTICAL | HORIZONTAL
  public boolean orientation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "orientation")) return false;
    if (!nextTokenIs(b, "<orientation>", HORIZONTAL, VERTICAL)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ORIENTATION, "<orientation>");
    r = consumeToken(b, VERTICAL);
    if (!r) r = consumeToken(b, HORIZONTAL);
    exit_section_(b, l, m, ORIENTATION, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // mappedActionClassParamDeclare PLUS
  // 		                (WHEN propertyExpression THEN)?
  // 		                topActionPropertyDefinitionBody OPTIMISTICASYNC?
  public boolean overrideActionStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "overrideActionStatement")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OVERRIDE_ACTION_STATEMENT, "<override action statement>");
    r = mappedActionClassParamDeclare(b, l + 1);
    r = r && consumeToken(b, PLUS);
    r = r && overrideActionStatement_2(b, l + 1);
    r = r && topActionPropertyDefinitionBody(b, l + 1);
    r = r && overrideActionStatement_4(b, l + 1);
    exit_section_(b, l, m, OVERRIDE_ACTION_STATEMENT, r, false, null);
    return r;
  }

  // (WHEN propertyExpression THEN)?
  private boolean overrideActionStatement_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "overrideActionStatement_2")) return false;
    overrideActionStatement_2_0(b, l + 1);
    return true;
  }

  // WHEN propertyExpression THEN
  private boolean overrideActionStatement_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "overrideActionStatement_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, WHEN);
    r = r && propertyExpression(b, l + 1);
    r = r && consumeToken(b, THEN);
    exit_section_(b, m, null, r);
    return r;
  }

  // OPTIMISTICASYNC?
  private boolean overrideActionStatement_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "overrideActionStatement_4")) return false;
    consumeToken(b, OPTIMISTICASYNC);
    return true;
  }

  /* ********************************************************** */
  // OVERRIDE
  public boolean overrideOperator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "overrideOperator")) return false;
    if (!nextTokenIs(b, OVERRIDE)) return false;
    boolean r;
    Marker m = enter_section_(b, l, OVERRIDE_OPERATOR);
    r = consumeToken(b, OVERRIDE);
    exit_section_(b, m, OVERRIDE_OPERATOR, r);
    return r;
  }

  /* ********************************************************** */
  // (overrideOperator | exclusiveOperator) nonEmptyPropertyExpressionList
  public boolean overridePropertyDefinition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "overridePropertyDefinition")) return false;
    if (!nextTokenIs(b, "<override property definition>", EXCLUSIVE, OVERRIDE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, OVERRIDE_PROPERTY_DEFINITION, "<override property definition>");
    r = overridePropertyDefinition_0(b, l + 1);
    p = r; // pin = 1
    r = r && nonEmptyPropertyExpressionList(b, l + 1);
    exit_section_(b, l, m, OVERRIDE_PROPERTY_DEFINITION, r, p, null);
    return r || p;
  }

  // overrideOperator | exclusiveOperator
  private boolean overridePropertyDefinition_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "overridePropertyDefinition_0")) return false;
    boolean r;
    r = overrideOperator(b, l + 1);
    if (!r) r = exclusiveOperator(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // mappedPropertyClassParamDeclare PLUSEQ
  // 		                (WHEN propertyExpression THEN)?
  // 		                propertyExpression SEMI
  public boolean overridePropertyStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "overridePropertyStatement")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, OVERRIDE_PROPERTY_STATEMENT, "<override property statement>");
    r = mappedPropertyClassParamDeclare(b, l + 1);
    r = r && consumeToken(b, PLUSEQ);
    p = r; // pin = 2
    r = r && overridePropertyStatement_2(b, l + 1);
    r = r && propertyExpression(b, l + 1);
    r = r && consumeToken(b, SEMI);
    exit_section_(b, l, m, OVERRIDE_PROPERTY_STATEMENT, r, p, null);
    return r || p;
  }

  // (WHEN propertyExpression THEN)?
  private boolean overridePropertyStatement_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "overridePropertyStatement_2")) return false;
    overridePropertyStatement_2_0(b, l + 1);
    return true;
  }

  // WHEN propertyExpression THEN
  private boolean overridePropertyStatement_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "overridePropertyStatement_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, WHEN);
    r = r && propertyExpression(b, l + 1);
    r = r && consumeToken(b, THEN);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // simpleName
  public boolean paramDeclare(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "paramDeclare")) return false;
    if (!nextTokenIs(b, ID)) return false;
    boolean r;
    Marker m = enter_section_(b, l, PARAM_DECLARE);
    r = simpleName(b, l + 1);
    exit_section_(b, m, PARAM_DECLARE, r);
    return r;
  }

  /* ********************************************************** */
  // (<<checkParameterOrExpression>> &((exprParameterUsage <<matchedSingleParameter>>)?) propertyExpression <<matchedParameterOrExpression>>) |
  //         exprParameterUsage
  public boolean parameterOrExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameterOrExpression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PARAMETER_OR_EXPRESSION, "<parameter or expression>");
    r = parameterOrExpression_0(b, l + 1);
    if (!r) r = exprParameterUsage(b, l + 1);
    exit_section_(b, l, m, PARAMETER_OR_EXPRESSION, r, false, null);
    return r;
  }

  // <<checkParameterOrExpression>> &((exprParameterUsage <<matchedSingleParameter>>)?) propertyExpression <<matchedParameterOrExpression>>
  private boolean parameterOrExpression_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameterOrExpression_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = checkParameterOrExpression(b, l + 1);
    r = r && parameterOrExpression_0_1(b, l + 1);
    r = r && propertyExpression(b, l + 1);
    r = r && matchedParameterOrExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // &((exprParameterUsage <<matchedSingleParameter>>)?)
  private boolean parameterOrExpression_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameterOrExpression_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _AND_);
    r = parameterOrExpression_0_1_0(b, l + 1);
    exit_section_(b, l, m, null, r, false, null);
    return r;
  }

  // (exprParameterUsage <<matchedSingleParameter>>)?
  private boolean parameterOrExpression_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameterOrExpression_0_1_0")) return false;
    parameterOrExpression_0_1_0_0(b, l + 1);
    return true;
  }

  // exprParameterUsage <<matchedSingleParameter>>
  private boolean parameterOrExpression_0_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameterOrExpression_0_1_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = exprParameterUsage(b, l + 1);
    r = r && matchedSingleParameter(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // (nonEmptyParameterOrExpressionList)?
  public boolean parameterOrExpressionList(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameterOrExpressionList")) return false;
    Marker m = enter_section_(b, l, _NONE_, PARAMETER_OR_EXPRESSION_LIST, "<parameter or expression list>");
    parameterOrExpressionList_0(b, l + 1);
    exit_section_(b, l, m, PARAMETER_OR_EXPRESSION_LIST, true, false, null);
    return true;
  }

  // (nonEmptyParameterOrExpressionList)
  private boolean parameterOrExpressionList_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameterOrExpressionList_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = nonEmptyParameterOrExpressionList(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // BY nonEmptyPropertyExpressionList
  public boolean partitionPropertyBy(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "partitionPropertyBy")) return false;
    if (!nextTokenIs(b, BY)) return false;
    boolean r;
    Marker m = enter_section_(b, l, PARTITION_PROPERTY_BY);
    r = consumeToken(b, BY);
    r = r && nonEmptyPropertyExpressionList(b, l + 1);
    exit_section_(b, m, PARTITION_PROPERTY_BY, r);
    return r;
  }

  /* ********************************************************** */
  // PARTITION
  //                                 (   (SUM | PREV)
  //                                 |   UNGROUP propertyUsage
  //                                     (   PROPORTION (STRICT)? ROUND LBRAC LEX_UINT_LITERAL RBRAC
  //                                     |   LIMIT (STRICT)?
  //                                     )
  //                                 )
  //                                 propertyExpression
  //                                 (ORDER (DESC )? nonEmptyPropertyExpressionList)?
  //                                 (WINDOW EXCEPTLAST)?
  //                                 partitionPropertyBy?
  public boolean partitionPropertyDefinition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "partitionPropertyDefinition")) return false;
    if (!nextTokenIs(b, PARTITION)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, PARTITION_PROPERTY_DEFINITION, null);
    r = consumeToken(b, PARTITION);
    p = r; // pin = 1
    r = r && partitionPropertyDefinition_1(b, l + 1);
    r = r && propertyExpression(b, l + 1);
    r = r && partitionPropertyDefinition_3(b, l + 1);
    r = r && partitionPropertyDefinition_4(b, l + 1);
    r = r && partitionPropertyDefinition_5(b, l + 1);
    exit_section_(b, l, m, PARTITION_PROPERTY_DEFINITION, r, p, null);
    return r || p;
  }

  // (SUM | PREV)
  //                                 |   UNGROUP propertyUsage
  //                                     (   PROPORTION (STRICT)? ROUND LBRAC LEX_UINT_LITERAL RBRAC
  //                                     |   LIMIT (STRICT)?
  //                                     )
  private boolean partitionPropertyDefinition_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "partitionPropertyDefinition_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = partitionPropertyDefinition_1_0(b, l + 1);
    if (!r) r = partitionPropertyDefinition_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // SUM | PREV
  private boolean partitionPropertyDefinition_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "partitionPropertyDefinition_1_0")) return false;
    boolean r;
    r = consumeToken(b, SUM);
    if (!r) r = consumeToken(b, PREV);
    return r;
  }

  // UNGROUP propertyUsage
  //                                     (   PROPORTION (STRICT)? ROUND LBRAC LEX_UINT_LITERAL RBRAC
  //                                     |   LIMIT (STRICT)?
  //                                     )
  private boolean partitionPropertyDefinition_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "partitionPropertyDefinition_1_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, UNGROUP);
    r = r && propertyUsage(b, l + 1);
    r = r && partitionPropertyDefinition_1_1_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // PROPORTION (STRICT)? ROUND LBRAC LEX_UINT_LITERAL RBRAC
  //                                     |   LIMIT (STRICT)?
  private boolean partitionPropertyDefinition_1_1_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "partitionPropertyDefinition_1_1_2")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = partitionPropertyDefinition_1_1_2_0(b, l + 1);
    if (!r) r = partitionPropertyDefinition_1_1_2_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // PROPORTION (STRICT)? ROUND LBRAC LEX_UINT_LITERAL RBRAC
  private boolean partitionPropertyDefinition_1_1_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "partitionPropertyDefinition_1_1_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, PROPORTION);
    r = r && partitionPropertyDefinition_1_1_2_0_1(b, l + 1);
    r = r && consumeTokens(b, 0, ROUND, LBRAC, LEX_UINT_LITERAL, RBRAC);
    exit_section_(b, m, null, r);
    return r;
  }

  // (STRICT)?
  private boolean partitionPropertyDefinition_1_1_2_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "partitionPropertyDefinition_1_1_2_0_1")) return false;
    consumeToken(b, STRICT);
    return true;
  }

  // LIMIT (STRICT)?
  private boolean partitionPropertyDefinition_1_1_2_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "partitionPropertyDefinition_1_1_2_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, LIMIT);
    r = r && partitionPropertyDefinition_1_1_2_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (STRICT)?
  private boolean partitionPropertyDefinition_1_1_2_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "partitionPropertyDefinition_1_1_2_1_1")) return false;
    consumeToken(b, STRICT);
    return true;
  }

  // (ORDER (DESC )? nonEmptyPropertyExpressionList)?
  private boolean partitionPropertyDefinition_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "partitionPropertyDefinition_3")) return false;
    partitionPropertyDefinition_3_0(b, l + 1);
    return true;
  }

  // ORDER (DESC )? nonEmptyPropertyExpressionList
  private boolean partitionPropertyDefinition_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "partitionPropertyDefinition_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, ORDER);
    r = r && partitionPropertyDefinition_3_0_1(b, l + 1);
    r = r && nonEmptyPropertyExpressionList(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (DESC )?
  private boolean partitionPropertyDefinition_3_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "partitionPropertyDefinition_3_0_1")) return false;
    consumeToken(b, DESC);
    return true;
  }

  // (WINDOW EXCEPTLAST)?
  private boolean partitionPropertyDefinition_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "partitionPropertyDefinition_4")) return false;
    partitionPropertyDefinition_4_0(b, l + 1);
    return true;
  }

  // WINDOW EXCEPTLAST
  private boolean partitionPropertyDefinition_4_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "partitionPropertyDefinition_4_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeTokens(b, 0, WINDOW, EXCEPTLAST);
    exit_section_(b, m, null, r);
    return r;
  }

  // partitionPropertyBy?
  private boolean partitionPropertyDefinition_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "partitionPropertyDefinition_5")) return false;
    partitionPropertyBy(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // PATTERN localizedStringLiteral
  public boolean patternSetting(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "patternSetting")) return false;
    if (!nextTokenIs(b, PATTERN)) return false;
    boolean r;
    Marker m = enter_section_(b, l, PATTERN_SETTING);
    r = consumeToken(b, PATTERN);
    r = r && localizedStringLiteral(b, l + 1);
    exit_section_(b, m, PATTERN_SETTING, r);
    return r;
  }

  /* ********************************************************** */
  // MATERIALIZED stringLiteral?
  public boolean persistentSetting(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "persistentSetting")) return false;
    if (!nextTokenIs(b, MATERIALIZED)) return false;
    boolean r;
    Marker m = enter_section_(b, l, PERSISTENT_SETTING);
    r = consumeToken(b, MATERIALIZED);
    r = r && persistentSetting_1(b, l + 1);
    exit_section_(b, m, PERSISTENT_SETTING, r);
    return r;
  }

  // stringLiteral?
  private boolean persistentSetting_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "persistentSetting_1")) return false;
    stringLiteral(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // groupObjectTreeSingleSelectorType LBRAC groupObjectTreeSelector RBRAC
  boolean pinnedGroupObjectTreeSelector(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pinnedGroupObjectTreeSelector")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = groupObjectTreeSingleSelectorType(b, l + 1);
    r = r && consumeToken(b, LBRAC);
    p = r; // pin = 2
    r = r && groupObjectTreeSelector(b, l + 1);
    r = r && consumeToken(b, RBRAC);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // (stringLiteral | (SUM | MAX | MIN) | (SETTINGS | NOSETTINGS) | (CONFIG stringLiteral))*
  public boolean pivotOptions(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pivotOptions")) return false;
    Marker m = enter_section_(b, l, _NONE_, PIVOT_OPTIONS, "<pivot options>");
    while (true) {
      int c = current_position_(b);
      if (!pivotOptions_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "pivotOptions", c)) break;
    }
    exit_section_(b, l, m, PIVOT_OPTIONS, true, false, null);
    return true;
  }

  // stringLiteral | (SUM | MAX | MIN) | (SETTINGS | NOSETTINGS) | (CONFIG stringLiteral)
  private boolean pivotOptions_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pivotOptions_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = stringLiteral(b, l + 1);
    if (!r) r = pivotOptions_0_1(b, l + 1);
    if (!r) r = pivotOptions_0_2(b, l + 1);
    if (!r) r = pivotOptions_0_3(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // SUM | MAX | MIN
  private boolean pivotOptions_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pivotOptions_0_1")) return false;
    boolean r;
    r = consumeToken(b, SUM);
    if (!r) r = consumeToken(b, MAX);
    if (!r) r = consumeToken(b, MIN);
    return r;
  }

  // SETTINGS | NOSETTINGS
  private boolean pivotOptions_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pivotOptions_0_2")) return false;
    boolean r;
    r = consumeToken(b, SETTINGS);
    if (!r) r = consumeToken(b, NOSETTINGS);
    return r;
  }

  // CONFIG stringLiteral
  private boolean pivotOptions_0_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pivotOptions_0_3")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, CONFIG);
    r = r && stringLiteral(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // formPropertyDrawUsage | (LBRAC formPropertyDrawUsage (COMMA formPropertyDrawUsage)* RBRAC)
  public boolean pivotPropertyDrawList(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pivotPropertyDrawList")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PIVOT_PROPERTY_DRAW_LIST, "<pivot property draw list>");
    r = formPropertyDrawUsage(b, l + 1);
    if (!r) r = pivotPropertyDrawList_1(b, l + 1);
    exit_section_(b, l, m, PIVOT_PROPERTY_DRAW_LIST, r, false, null);
    return r;
  }

  // LBRAC formPropertyDrawUsage (COMMA formPropertyDrawUsage)* RBRAC
  private boolean pivotPropertyDrawList_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pivotPropertyDrawList_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, LBRAC);
    r = r && formPropertyDrawUsage(b, l + 1);
    r = r && pivotPropertyDrawList_1_2(b, l + 1);
    r = r && consumeToken(b, RBRAC);
    exit_section_(b, m, null, r);
    return r;
  }

  // (COMMA formPropertyDrawUsage)*
  private boolean pivotPropertyDrawList_1_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pivotPropertyDrawList_1_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!pivotPropertyDrawList_1_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "pivotPropertyDrawList_1_2", c)) break;
    }
    return true;
  }

  // COMMA formPropertyDrawUsage
  private boolean pivotPropertyDrawList_1_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pivotPropertyDrawList_1_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, COMMA);
    r = r && formPropertyDrawUsage(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // simplePE ((LSQBR uintLiteral RSQBR) | typePropertyDefinition)?
  public boolean postfixUnaryPE(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "postfixUnaryPE")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, POSTFIX_UNARY_PE, "<postfix unary pe>");
    r = simplePE(b, l + 1);
    r = r && postfixUnaryPE_1(b, l + 1);
    exit_section_(b, l, m, POSTFIX_UNARY_PE, r, false, null);
    return r;
  }

  // ((LSQBR uintLiteral RSQBR) | typePropertyDefinition)?
  private boolean postfixUnaryPE_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "postfixUnaryPE_1")) return false;
    postfixUnaryPE_1_0(b, l + 1);
    return true;
  }

  // (LSQBR uintLiteral RSQBR) | typePropertyDefinition
  private boolean postfixUnaryPE_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "postfixUnaryPE_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = postfixUnaryPE_1_0_0(b, l + 1);
    if (!r) r = typePropertyDefinition(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // LSQBR uintLiteral RSQBR
  private boolean postfixUnaryPE_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "postfixUnaryPE_1_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, LSQBR);
    r = r && uintLiteral(b, l + 1);
    r = r && consumeToken(b, RSQBR);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // NEW | NEWEDIT | EDIT
  public boolean predefinedAddPropertyName(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "predefinedAddPropertyName")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PREDEFINED_ADD_PROPERTY_NAME, "<predefined add property name>");
    r = consumeToken(b, NEW);
    if (!r) r = consumeToken(b, NEWEDIT);
    if (!r) r = consumeToken(b, EDIT);
    exit_section_(b, l, m, PREDEFINED_ADD_PROPERTY_NAME, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // VALUE
  //                            |    INTERVAL
  //                            |    predefinedAddPropertyName (LSQBR explicitPropClass RSQBR)?
  //                            |    DELETE
  public boolean predefinedFormPropertyName(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "predefinedFormPropertyName")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PREDEFINED_FORM_PROPERTY_NAME, "<predefined form property name>");
    r = consumeToken(b, VALUE);
    if (!r) r = consumeToken(b, INTERVAL);
    if (!r) r = predefinedFormPropertyName_2(b, l + 1);
    if (!r) r = consumeToken(b, DELETE);
    exit_section_(b, l, m, PREDEFINED_FORM_PROPERTY_NAME, r, false, null);
    return r;
  }

  // predefinedAddPropertyName (LSQBR explicitPropClass RSQBR)?
  private boolean predefinedFormPropertyName_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "predefinedFormPropertyName_2")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = predefinedAddPropertyName(b, l + 1);
    r = r && predefinedFormPropertyName_2_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (LSQBR explicitPropClass RSQBR)?
  private boolean predefinedFormPropertyName_2_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "predefinedFormPropertyName_2_1")) return false;
    predefinedFormPropertyName_2_1_0(b, l + 1);
    return true;
  }

  // LSQBR explicitPropClass RSQBR
  private boolean predefinedFormPropertyName_2_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "predefinedFormPropertyName_2_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, LSQBR);
    r = r && explicitPropClass(b, l + 1);
    r = r && consumeToken(b, RSQBR);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // PREREAD
  public boolean prereadSetting(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "prereadSetting")) return false;
    if (!nextTokenIs(b, PREREAD)) return false;
    boolean r;
    Marker m = enter_section_(b, l, PREREAD_SETTING);
    r = consumeToken(b, PREREAD);
    exit_section_(b, m, PREREAD_SETTING, r);
    return r;
  }

  /* ********************************************************** */
  // PRINT (CLIENT | SERVER)? mappedForm contextFiltersClause?
  // 	    (
  // 		    (
  // 		        MESSAGE
  // 		        syncTypeLiteral?
  // 		        selectTop?
  // 		    )
  // 		    |
  // 		    (
  //                 (
  //                     (   XLSX sheetExpression? (PASSWORD propertyExpression)?
  //                     |   XLS sheetExpression? (PASSWORD propertyExpression)?
  //                     |   PDF
  //                     |   DOC
  //                     |   DOCX
  //                     |   RTF
  //                     |   HTML
  //                     )
  //                     staticDestination?
  //                 )?
  //                 (PREVIEW | NOPREVIEW)?
  //                 syncTypeLiteral?
  //                 (TO propertyExpression)?
  // 		    )
  // 	    )
  public boolean printActionPropertyDefinitionBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "printActionPropertyDefinitionBody")) return false;
    if (!nextTokenIs(b, PRINT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, PRINT_ACTION_PROPERTY_DEFINITION_BODY, null);
    r = consumeToken(b, PRINT);
    p = r; // pin = 1
    r = r && printActionPropertyDefinitionBody_1(b, l + 1);
    r = r && mappedForm(b, l + 1);
    r = r && printActionPropertyDefinitionBody_3(b, l + 1);
    r = r && printActionPropertyDefinitionBody_4(b, l + 1);
    exit_section_(b, l, m, PRINT_ACTION_PROPERTY_DEFINITION_BODY, r, p, null);
    return r || p;
  }

  // (CLIENT | SERVER)?
  private boolean printActionPropertyDefinitionBody_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "printActionPropertyDefinitionBody_1")) return false;
    printActionPropertyDefinitionBody_1_0(b, l + 1);
    return true;
  }

  // CLIENT | SERVER
  private boolean printActionPropertyDefinitionBody_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "printActionPropertyDefinitionBody_1_0")) return false;
    boolean r;
    r = consumeToken(b, CLIENT);
    if (!r) r = consumeToken(b, SERVER);
    return r;
  }

  // contextFiltersClause?
  private boolean printActionPropertyDefinitionBody_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "printActionPropertyDefinitionBody_3")) return false;
    contextFiltersClause(b, l + 1);
    return true;
  }

  // (
  // 		        MESSAGE
  // 		        syncTypeLiteral?
  // 		        selectTop?
  // 		    )
  // 		    |
  // 		    (
  //                 (
  //                     (   XLSX sheetExpression? (PASSWORD propertyExpression)?
  //                     |   XLS sheetExpression? (PASSWORD propertyExpression)?
  //                     |   PDF
  //                     |   DOC
  //                     |   DOCX
  //                     |   RTF
  //                     |   HTML
  //                     )
  //                     staticDestination?
  //                 )?
  //                 (PREVIEW | NOPREVIEW)?
  //                 syncTypeLiteral?
  //                 (TO propertyExpression)?
  // 		    )
  private boolean printActionPropertyDefinitionBody_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "printActionPropertyDefinitionBody_4")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = printActionPropertyDefinitionBody_4_0(b, l + 1);
    if (!r) r = printActionPropertyDefinitionBody_4_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // MESSAGE
  // 		        syncTypeLiteral?
  // 		        selectTop?
  private boolean printActionPropertyDefinitionBody_4_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "printActionPropertyDefinitionBody_4_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, MESSAGE);
    r = r && printActionPropertyDefinitionBody_4_0_1(b, l + 1);
    r = r && printActionPropertyDefinitionBody_4_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // syncTypeLiteral?
  private boolean printActionPropertyDefinitionBody_4_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "printActionPropertyDefinitionBody_4_0_1")) return false;
    syncTypeLiteral(b, l + 1);
    return true;
  }

  // selectTop?
  private boolean printActionPropertyDefinitionBody_4_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "printActionPropertyDefinitionBody_4_0_2")) return false;
    selectTop(b, l + 1);
    return true;
  }

  // (
  //                     (   XLSX sheetExpression? (PASSWORD propertyExpression)?
  //                     |   XLS sheetExpression? (PASSWORD propertyExpression)?
  //                     |   PDF
  //                     |   DOC
  //                     |   DOCX
  //                     |   RTF
  //                     |   HTML
  //                     )
  //                     staticDestination?
  //                 )?
  //                 (PREVIEW | NOPREVIEW)?
  //                 syncTypeLiteral?
  //                 (TO propertyExpression)?
  private boolean printActionPropertyDefinitionBody_4_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "printActionPropertyDefinitionBody_4_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = printActionPropertyDefinitionBody_4_1_0(b, l + 1);
    r = r && printActionPropertyDefinitionBody_4_1_1(b, l + 1);
    r = r && printActionPropertyDefinitionBody_4_1_2(b, l + 1);
    r = r && printActionPropertyDefinitionBody_4_1_3(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (
  //                     (   XLSX sheetExpression? (PASSWORD propertyExpression)?
  //                     |   XLS sheetExpression? (PASSWORD propertyExpression)?
  //                     |   PDF
  //                     |   DOC
  //                     |   DOCX
  //                     |   RTF
  //                     |   HTML
  //                     )
  //                     staticDestination?
  //                 )?
  private boolean printActionPropertyDefinitionBody_4_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "printActionPropertyDefinitionBody_4_1_0")) return false;
    printActionPropertyDefinitionBody_4_1_0_0(b, l + 1);
    return true;
  }

  // (   XLSX sheetExpression? (PASSWORD propertyExpression)?
  //                     |   XLS sheetExpression? (PASSWORD propertyExpression)?
  //                     |   PDF
  //                     |   DOC
  //                     |   DOCX
  //                     |   RTF
  //                     |   HTML
  //                     )
  //                     staticDestination?
  private boolean printActionPropertyDefinitionBody_4_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "printActionPropertyDefinitionBody_4_1_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = printActionPropertyDefinitionBody_4_1_0_0_0(b, l + 1);
    r = r && printActionPropertyDefinitionBody_4_1_0_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // XLSX sheetExpression? (PASSWORD propertyExpression)?
  //                     |   XLS sheetExpression? (PASSWORD propertyExpression)?
  //                     |   PDF
  //                     |   DOC
  //                     |   DOCX
  //                     |   RTF
  //                     |   HTML
  private boolean printActionPropertyDefinitionBody_4_1_0_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "printActionPropertyDefinitionBody_4_1_0_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = printActionPropertyDefinitionBody_4_1_0_0_0_0(b, l + 1);
    if (!r) r = printActionPropertyDefinitionBody_4_1_0_0_0_1(b, l + 1);
    if (!r) r = consumeToken(b, PDF);
    if (!r) r = consumeToken(b, DOC);
    if (!r) r = consumeToken(b, DOCX);
    if (!r) r = consumeToken(b, RTF);
    if (!r) r = consumeToken(b, HTML);
    exit_section_(b, m, null, r);
    return r;
  }

  // XLSX sheetExpression? (PASSWORD propertyExpression)?
  private boolean printActionPropertyDefinitionBody_4_1_0_0_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "printActionPropertyDefinitionBody_4_1_0_0_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, XLSX);
    r = r && printActionPropertyDefinitionBody_4_1_0_0_0_0_1(b, l + 1);
    r = r && printActionPropertyDefinitionBody_4_1_0_0_0_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // sheetExpression?
  private boolean printActionPropertyDefinitionBody_4_1_0_0_0_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "printActionPropertyDefinitionBody_4_1_0_0_0_0_1")) return false;
    sheetExpression(b, l + 1);
    return true;
  }

  // (PASSWORD propertyExpression)?
  private boolean printActionPropertyDefinitionBody_4_1_0_0_0_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "printActionPropertyDefinitionBody_4_1_0_0_0_0_2")) return false;
    printActionPropertyDefinitionBody_4_1_0_0_0_0_2_0(b, l + 1);
    return true;
  }

  // PASSWORD propertyExpression
  private boolean printActionPropertyDefinitionBody_4_1_0_0_0_0_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "printActionPropertyDefinitionBody_4_1_0_0_0_0_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, PASSWORD);
    r = r && propertyExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // XLS sheetExpression? (PASSWORD propertyExpression)?
  private boolean printActionPropertyDefinitionBody_4_1_0_0_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "printActionPropertyDefinitionBody_4_1_0_0_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, XLS);
    r = r && printActionPropertyDefinitionBody_4_1_0_0_0_1_1(b, l + 1);
    r = r && printActionPropertyDefinitionBody_4_1_0_0_0_1_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // sheetExpression?
  private boolean printActionPropertyDefinitionBody_4_1_0_0_0_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "printActionPropertyDefinitionBody_4_1_0_0_0_1_1")) return false;
    sheetExpression(b, l + 1);
    return true;
  }

  // (PASSWORD propertyExpression)?
  private boolean printActionPropertyDefinitionBody_4_1_0_0_0_1_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "printActionPropertyDefinitionBody_4_1_0_0_0_1_2")) return false;
    printActionPropertyDefinitionBody_4_1_0_0_0_1_2_0(b, l + 1);
    return true;
  }

  // PASSWORD propertyExpression
  private boolean printActionPropertyDefinitionBody_4_1_0_0_0_1_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "printActionPropertyDefinitionBody_4_1_0_0_0_1_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, PASSWORD);
    r = r && propertyExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // staticDestination?
  private boolean printActionPropertyDefinitionBody_4_1_0_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "printActionPropertyDefinitionBody_4_1_0_0_1")) return false;
    staticDestination(b, l + 1);
    return true;
  }

  // (PREVIEW | NOPREVIEW)?
  private boolean printActionPropertyDefinitionBody_4_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "printActionPropertyDefinitionBody_4_1_1")) return false;
    printActionPropertyDefinitionBody_4_1_1_0(b, l + 1);
    return true;
  }

  // PREVIEW | NOPREVIEW
  private boolean printActionPropertyDefinitionBody_4_1_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "printActionPropertyDefinitionBody_4_1_1_0")) return false;
    boolean r;
    r = consumeToken(b, PREVIEW);
    if (!r) r = consumeToken(b, NOPREVIEW);
    return r;
  }

  // syncTypeLiteral?
  private boolean printActionPropertyDefinitionBody_4_1_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "printActionPropertyDefinitionBody_4_1_2")) return false;
    syncTypeLiteral(b, l + 1);
    return true;
  }

  // (TO propertyExpression)?
  private boolean printActionPropertyDefinitionBody_4_1_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "printActionPropertyDefinitionBody_4_1_3")) return false;
    printActionPropertyDefinitionBody_4_1_3_0(b, l + 1);
    return true;
  }

  // TO propertyExpression
  private boolean printActionPropertyDefinitionBody_4_1_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "printActionPropertyDefinitionBody_4_1_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, TO);
    r = r && propertyExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // PRIORITY nonEmptyNamespaceUsageList SEMI
  public boolean priorityList(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "priorityList")) return false;
    if (!nextTokenIs(b, PRIORITY)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, PRIORITY_LIST, null);
    r = consumeToken(b, PRIORITY);
    p = r; // pin = 1
    r = r && nonEmptyNamespaceUsageList(b, l + 1);
    r = r && consumeToken(b, SEMI);
    exit_section_(b, l, m, PRIORITY_LIST, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // expressionUnfriendlyPD | propertyExpression
  public boolean propertyCalcStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "propertyCalcStatement")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PROPERTY_CALC_STATEMENT, "<property calc statement>");
    r = expressionUnfriendlyPD(b, l + 1);
    if (!r) r = propertyExpression(b, l + 1);
    exit_section_(b, l, m, PROPERTY_CALC_STATEMENT, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // (CUSTOM ((renderPropertyCustomView changePropertyCustomView) | renderPropertyCustomView | changePropertyCustomView))
  //                         | (SELECT (AUTO | (<<noIDCheck>> stringLiteral))?)
  //                         | NOSELECT
  public boolean propertyCustomView(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "propertyCustomView")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PROPERTY_CUSTOM_VIEW, "<property custom view>");
    r = propertyCustomView_0(b, l + 1);
    if (!r) r = propertyCustomView_1(b, l + 1);
    if (!r) r = consumeToken(b, NOSELECT);
    exit_section_(b, l, m, PROPERTY_CUSTOM_VIEW, r, false, null);
    return r;
  }

  // CUSTOM ((renderPropertyCustomView changePropertyCustomView) | renderPropertyCustomView | changePropertyCustomView)
  private boolean propertyCustomView_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "propertyCustomView_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, CUSTOM);
    r = r && propertyCustomView_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (renderPropertyCustomView changePropertyCustomView) | renderPropertyCustomView | changePropertyCustomView
  private boolean propertyCustomView_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "propertyCustomView_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = propertyCustomView_0_1_0(b, l + 1);
    if (!r) r = renderPropertyCustomView(b, l + 1);
    if (!r) r = changePropertyCustomView(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // renderPropertyCustomView changePropertyCustomView
  private boolean propertyCustomView_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "propertyCustomView_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = renderPropertyCustomView(b, l + 1);
    r = r && changePropertyCustomView(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // SELECT (AUTO | (<<noIDCheck>> stringLiteral))?
  private boolean propertyCustomView_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "propertyCustomView_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, SELECT);
    r = r && propertyCustomView_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (AUTO | (<<noIDCheck>> stringLiteral))?
  private boolean propertyCustomView_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "propertyCustomView_1_1")) return false;
    propertyCustomView_1_1_0(b, l + 1);
    return true;
  }

  // AUTO | (<<noIDCheck>> stringLiteral)
  private boolean propertyCustomView_1_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "propertyCustomView_1_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, AUTO);
    if (!r) r = propertyCustomView_1_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // <<noIDCheck>> stringLiteral
  private boolean propertyCustomView_1_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "propertyCustomView_1_1_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = noIDCheck(b, l + 1);
    r = r && stringLiteral(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // LBRAC classParamDeclareList RBRAC
  public boolean propertyDeclParams(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "propertyDeclParams")) return false;
    if (!nextTokenIs(b, LBRAC)) return false;
    boolean r;
    Marker m = enter_section_(b, l, PROPERTY_DECL_PARAMS);
    r = consumeToken(b, LBRAC);
    r = r && classParamDeclareList(b, l + 1);
    r = r && consumeToken(b, RBRAC);
    exit_section_(b, m, PROPERTY_DECL_PARAMS, r);
    return r;
  }

  /* ********************************************************** */
  // simpleNameWithCaption propertyDeclParams?
  public boolean propertyDeclaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "propertyDeclaration")) return false;
    if (!nextTokenIs(b, ID)) return false;
    boolean r;
    Marker m = enter_section_(b, l, PROPERTY_DECLARATION);
    r = simpleNameWithCaption(b, l + 1);
    r = r && propertyDeclaration_1(b, l + 1);
    exit_section_(b, m, PROPERTY_DECLARATION, r);
    return r;
  }

  // propertyDeclParams?
  private boolean propertyDeclaration_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "propertyDeclaration_1")) return false;
    propertyDeclParams(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // compoundID explicitPropClassUsage?
  public boolean propertyElseActionUsage(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "propertyElseActionUsage")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PROPERTY_ELSE_ACTION_USAGE, "<property else action usage>");
    r = compoundID(b, l + 1);
    r = r && propertyElseActionUsage_1(b, l + 1);
    exit_section_(b, l, m, PROPERTY_ELSE_ACTION_USAGE, r, false, null);
    return r;
  }

  // explicitPropClassUsage?
  private boolean propertyElseActionUsage_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "propertyElseActionUsage_1")) return false;
    explicitPropClassUsage(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // LSQBR (propertyCalcStatement) RSQBR
  public boolean propertyExprObject(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "propertyExprObject")) return false;
    if (!nextTokenIs(b, LSQBR)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, PROPERTY_EXPR_OBJECT, null);
    r = consumeToken(b, LSQBR);
    p = r; // pin = 1
    r = r && propertyExprObject_1(b, l + 1);
    r = r && consumeToken(b, RSQBR);
    exit_section_(b, l, m, PROPERTY_EXPR_OBJECT, r, p, null);
    return r || p;
  }

  // (propertyCalcStatement)
  private boolean propertyExprObject_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "propertyExprObject_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = propertyCalcStatement(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // ifPE
  public boolean propertyExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "propertyExpression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PROPERTY_EXPRESSION, "<property expression>");
    r = ifPE(b, l + 1);
    exit_section_(b, l, m, PROPERTY_EXPRESSION, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // (nonEmptyPropertyExpressionList)?
  public boolean propertyExpressionList(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "propertyExpressionList")) return false;
    Marker m = enter_section_(b, l, _NONE_, PROPERTY_EXPRESSION_LIST, "<property expression list>");
    propertyExpressionList_0(b, l + 1);
    exit_section_(b, l, m, PROPERTY_EXPRESSION_LIST, true, false, null);
    return true;
  }

  // (nonEmptyPropertyExpressionList)
  private boolean propertyExpressionList_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "propertyExpressionList_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = nonEmptyPropertyExpressionList(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // propertyExpression (DESC)?
  public boolean propertyExpressionWithOrder(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "propertyExpressionWithOrder")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PROPERTY_EXPRESSION_WITH_ORDER, "<property expression with order>");
    r = propertyExpression(b, l + 1);
    r = r && propertyExpressionWithOrder_1(b, l + 1);
    exit_section_(b, l, m, PROPERTY_EXPRESSION_WITH_ORDER, r, false, null);
    return r;
  }

  // (DESC)?
  private boolean propertyExpressionWithOrder_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "propertyExpressionWithOrder_1")) return false;
    consumeToken(b, DESC);
    return true;
  }

  /* ********************************************************** */
  // formPropertyDrawUsage
  public boolean propertySelector(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "propertySelector")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PROPERTY_SELECTOR, "<property selector>");
    r = formPropertyDrawUsage(b, l + 1);
    exit_section_(b, l, m, PROPERTY_SELECTOR, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // propertyDeclaration equalsSign propertyCalcStatement (nonEmptyPropertyOptions | SEMI)
  public boolean propertyStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "propertyStatement")) return false;
    if (!nextTokenIs(b, ID)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, PROPERTY_STATEMENT, null);
    r = propertyDeclaration(b, l + 1);
    r = r && equalsSign(b, l + 1);
    r = r && propertyCalcStatement(b, l + 1);
    p = r; // pin = 3
    r = r && propertyStatement_3(b, l + 1);
    exit_section_(b, l, m, PROPERTY_STATEMENT, r, p, null);
    return r || p;
  }

  // nonEmptyPropertyOptions | SEMI
  private boolean propertyStatement_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "propertyStatement_3")) return false;
    boolean r;
    r = nonEmptyPropertyOptions(b, l + 1);
    if (!r) r = consumeToken(b, SEMI);
    return r;
  }

  /* ********************************************************** */
  // compoundID explicitPropClassUsage?
  public boolean propertyUsage(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "propertyUsage")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PROPERTY_USAGE, "<property usage>");
    r = compoundID(b, l + 1);
    r = r && propertyUsage_1(b, l + 1);
    exit_section_(b, l, m, PROPERTY_USAGE, r, false, null);
    return r;
  }

  // explicitPropClassUsage?
  private boolean propertyUsage_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "propertyUsage_1")) return false;
    explicitPropClassUsage(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // propertyUsage
  public boolean propertyUsageWrapper(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "propertyUsageWrapper")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PROPERTY_USAGE_WRAPPER, "<property usage wrapper>");
    r = propertyUsage(b, l + 1);
    exit_section_(b, l, m, PROPERTY_USAGE_WRAPPER, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // READ (CLIENT DIALOG?)? propertyExpression (TO noParamsPropertyUsage)?
  public boolean readActionPropertyDefinitionBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "readActionPropertyDefinitionBody")) return false;
    if (!nextTokenIs(b, READ)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, READ_ACTION_PROPERTY_DEFINITION_BODY, null);
    r = consumeToken(b, READ);
    p = r; // pin = 1
    r = r && readActionPropertyDefinitionBody_1(b, l + 1);
    r = r && propertyExpression(b, l + 1);
    r = r && readActionPropertyDefinitionBody_3(b, l + 1);
    exit_section_(b, l, m, READ_ACTION_PROPERTY_DEFINITION_BODY, r, p, null);
    return r || p;
  }

  // (CLIENT DIALOG?)?
  private boolean readActionPropertyDefinitionBody_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "readActionPropertyDefinitionBody_1")) return false;
    readActionPropertyDefinitionBody_1_0(b, l + 1);
    return true;
  }

  // CLIENT DIALOG?
  private boolean readActionPropertyDefinitionBody_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "readActionPropertyDefinitionBody_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, CLIENT);
    r = r && readActionPropertyDefinitionBody_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // DIALOG?
  private boolean readActionPropertyDefinitionBody_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "readActionPropertyDefinitionBody_1_0_1")) return false;
    consumeToken(b, DIALOG);
    return true;
  }

  // (TO noParamsPropertyUsage)?
  private boolean readActionPropertyDefinitionBody_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "readActionPropertyDefinitionBody_3")) return false;
    readActionPropertyDefinitionBody_3_0(b, l + 1);
    return true;
  }

  // TO noParamsPropertyUsage
  private boolean readActionPropertyDefinitionBody_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "readActionPropertyDefinitionBody_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, TO);
    r = r && noParamsPropertyUsage(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // FILTERS groupObjectID (TO propertyUsage)?
  public boolean readFilterActionPropertyDefinitionBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "readFilterActionPropertyDefinitionBody")) return false;
    if (!nextTokenIs(b, FILTERS)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, READ_FILTER_ACTION_PROPERTY_DEFINITION_BODY, null);
    r = consumeToken(b, FILTERS);
    r = r && groupObjectID(b, l + 1);
    p = r; // pin = 2
    r = r && readFilterActionPropertyDefinitionBody_2(b, l + 1);
    exit_section_(b, l, m, READ_FILTER_ACTION_PROPERTY_DEFINITION_BODY, r, p, null);
    return r || p;
  }

  // (TO propertyUsage)?
  private boolean readFilterActionPropertyDefinitionBody_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "readFilterActionPropertyDefinitionBody_2")) return false;
    readFilterActionPropertyDefinitionBody_2_0(b, l + 1);
    return true;
  }

  // TO propertyUsage
  private boolean readFilterActionPropertyDefinitionBody_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "readFilterActionPropertyDefinitionBody_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, TO);
    r = r && propertyUsage(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // ORDERS groupObjectID (TO propertyUsage)?
  public boolean readOrderActionPropertyDefinitionBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "readOrderActionPropertyDefinitionBody")) return false;
    if (!nextTokenIs(b, ORDERS)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, READ_ORDER_ACTION_PROPERTY_DEFINITION_BODY, null);
    r = consumeToken(b, ORDERS);
    r = r && groupObjectID(b, l + 1);
    p = r; // pin = 2
    r = r && readOrderActionPropertyDefinitionBody_2(b, l + 1);
    exit_section_(b, l, m, READ_ORDER_ACTION_PROPERTY_DEFINITION_BODY, r, p, null);
    return r || p;
  }

  // (TO propertyUsage)?
  private boolean readOrderActionPropertyDefinitionBody_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "readOrderActionPropertyDefinitionBody_2")) return false;
    readOrderActionPropertyDefinitionBody_2_0(b, l + 1);
    return true;
  }

  // TO propertyUsage
  private boolean readOrderActionPropertyDefinitionBody_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "readOrderActionPropertyDefinitionBody_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, TO);
    r = r && propertyUsage(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // RECALCULATE propertyExpression (WHERE propertyExpression)?
  public boolean recalculateActionPropertyDefinitionBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "recalculateActionPropertyDefinitionBody")) return false;
    if (!nextTokenIs(b, RECALCULATE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, RECALCULATE_ACTION_PROPERTY_DEFINITION_BODY, null);
    r = consumeToken(b, RECALCULATE);
    p = r; // pin = 1
    r = r && propertyExpression(b, l + 1);
    r = r && recalculateActionPropertyDefinitionBody_2(b, l + 1);
    exit_section_(b, l, m, RECALCULATE_ACTION_PROPERTY_DEFINITION_BODY, r, p, null);
    return r || p;
  }

  // (WHERE propertyExpression)?
  private boolean recalculateActionPropertyDefinitionBody_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "recalculateActionPropertyDefinitionBody_2")) return false;
    recalculateActionPropertyDefinitionBody_2_0(b, l + 1);
    return true;
  }

  // WHERE propertyExpression
  private boolean recalculateActionPropertyDefinitionBody_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "recalculateActionPropertyDefinitionBody_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, WHERE);
    r = r && propertyExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // recursiveExtendContextActionPDB | recursiveKeepContextActionPDB
  boolean recursiveActionPDB(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "recursiveActionPDB")) return false;
    boolean r;
    r = recursiveExtendContextActionPDB(b, l + 1);
    if (!r) r = recursiveKeepContextActionPDB(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // forActionPropertyDefinitionBody
  //                                             |   whileActionPropertyDefinitionBody
  //                                             |	dialogActionPropertyDefinitionBody // mixed, input
  //                                             |	inputActionPropertyDefinitionBody // mixed, input
  //                                             |	newActionPropertyDefinitionBody
  boolean recursiveExtendContextActionPDB(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "recursiveExtendContextActionPDB")) return false;
    boolean r;
    r = forActionPropertyDefinitionBody(b, l + 1);
    if (!r) r = whileActionPropertyDefinitionBody(b, l + 1);
    if (!r) r = dialogActionPropertyDefinitionBody(b, l + 1);
    if (!r) r = inputActionPropertyDefinitionBody(b, l + 1);
    if (!r) r = newActionPropertyDefinitionBody(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // listActionPropertyDefinitionBody
  //                                         |	    confirmActionPropertyDefinitionBody// mixed, input
  //                                         |	    newSessionActionPropertyDefinitionBody
  //                                         |	    requestActionPropertyDefinitionBody
  //                                         |	    tryActionPropertyDefinitionBody // mixed
  //                                         |	    ifActionPropertyDefinitionBody
  //                                         |	    caseActionPropertyDefinitionBody
  //                                         |	    multiActionPropertyDefinitionBody	
  //                                         |	    applyActionPropertyDefinitionBody
  //                                         |	    importActionPropertyDefinitionBody // mixed
  //                                         |       newThreadActionPropertyDefinitionBody // mixed
  //                                         |	    newExecutorActionPropertyDefinitionBody
  boolean recursiveKeepContextActionPDB(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "recursiveKeepContextActionPDB")) return false;
    boolean r;
    r = listActionPropertyDefinitionBody(b, l + 1);
    if (!r) r = confirmActionPropertyDefinitionBody(b, l + 1);
    if (!r) r = newSessionActionPropertyDefinitionBody(b, l + 1);
    if (!r) r = requestActionPropertyDefinitionBody(b, l + 1);
    if (!r) r = tryActionPropertyDefinitionBody(b, l + 1);
    if (!r) r = ifActionPropertyDefinitionBody(b, l + 1);
    if (!r) r = caseActionPropertyDefinitionBody(b, l + 1);
    if (!r) r = multiActionPropertyDefinitionBody(b, l + 1);
    if (!r) r = applyActionPropertyDefinitionBody(b, l + 1);
    if (!r) r = importActionPropertyDefinitionBody(b, l + 1);
    if (!r) r = newThreadActionPropertyDefinitionBody(b, l + 1);
    if (!r) r = newExecutorActionPropertyDefinitionBody(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // RECURSION propertyExpression STEP propertyExpression (CYCLES (YES |	NO | IMPOSSIBLE))?
  public boolean recursivePropertyDefinition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "recursivePropertyDefinition")) return false;
    if (!nextTokenIs(b, RECURSION)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, RECURSIVE_PROPERTY_DEFINITION, null);
    r = consumeToken(b, RECURSION);
    p = r; // pin = 1
    r = r && propertyExpression(b, l + 1);
    r = r && consumeToken(b, STEP);
    r = r && propertyExpression(b, l + 1);
    r = r && recursivePropertyDefinition_4(b, l + 1);
    exit_section_(b, l, m, RECURSIVE_PROPERTY_DEFINITION, r, p, null);
    return r || p;
  }

  // (CYCLES (YES |	NO | IMPOSSIBLE))?
  private boolean recursivePropertyDefinition_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "recursivePropertyDefinition_4")) return false;
    recursivePropertyDefinition_4_0(b, l + 1);
    return true;
  }

  // CYCLES (YES |	NO | IMPOSSIBLE)
  private boolean recursivePropertyDefinition_4_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "recursivePropertyDefinition_4_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, CYCLES);
    r = r && recursivePropertyDefinition_4_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // YES |	NO | IMPOSSIBLE
  private boolean recursivePropertyDefinition_4_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "recursivePropertyDefinition_4_0_1")) return false;
    boolean r;
    r = consumeToken(b, YES);
    if (!r) r = consumeToken(b, NO);
    if (!r) r = consumeToken(b, IMPOSSIBLE);
    return r;
  }

  /* ********************************************************** */
  // REFLECTION reflectionPropertyType actionOrPropertyUsage
  public boolean reflectionPropertyDefinition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reflectionPropertyDefinition")) return false;
    if (!nextTokenIs(b, REFLECTION)) return false;
    boolean r;
    Marker m = enter_section_(b, l, REFLECTION_PROPERTY_DEFINITION);
    r = consumeToken(b, REFLECTION);
    r = r && reflectionPropertyType(b, l + 1);
    r = r && actionOrPropertyUsage(b, l + 1);
    exit_section_(b, m, REFLECTION_PROPERTY_DEFINITION, r);
    return r;
  }

  /* ********************************************************** */
  // CANONICALNAME
  public boolean reflectionPropertyType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reflectionPropertyType")) return false;
    if (!nextTokenIs(b, CANONICALNAME)) return false;
    boolean r;
    Marker m = enter_section_(b, l, REFLECTION_PROPERTY_TYPE);
    r = consumeToken(b, CANONICALNAME);
    exit_section_(b, m, REFLECTION_PROPERTY_TYPE, r);
    return r;
  }

  /* ********************************************************** */
  // REGEXP localizedStringLiteral (localizedStringLiteral)?
  public boolean regexpSetting(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "regexpSetting")) return false;
    if (!nextTokenIs(b, REGEXP)) return false;
    boolean r;
    Marker m = enter_section_(b, l, REGEXP_SETTING);
    r = consumeToken(b, REGEXP);
    r = r && localizedStringLiteral(b, l + 1);
    r = r && regexpSetting_2(b, l + 1);
    exit_section_(b, m, REGEXP_SETTING, r);
    return r;
  }

  // (localizedStringLiteral)?
  private boolean regexpSetting_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "regexpSetting_2")) return false;
    regexpSetting_2_0(b, l + 1);
    return true;
  }

  // (localizedStringLiteral)
  private boolean regexpSetting_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "regexpSetting_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = localizedStringLiteral(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // FILTER localizedStringLiteral formExprDeclaration (stringLiteral)? (filterSetDefault)?
  public boolean regularFilterDeclaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "regularFilterDeclaration")) return false;
    if (!nextTokenIs(b, FILTER)) return false;
    boolean r;
    Marker m = enter_section_(b, l, REGULAR_FILTER_DECLARATION);
    r = consumeToken(b, FILTER);
    r = r && localizedStringLiteral(b, l + 1);
    r = r && formExprDeclaration(b, l + 1);
    r = r && regularFilterDeclaration_3(b, l + 1);
    r = r && regularFilterDeclaration_4(b, l + 1);
    exit_section_(b, m, REGULAR_FILTER_DECLARATION, r);
    return r;
  }

  // (stringLiteral)?
  private boolean regularFilterDeclaration_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "regularFilterDeclaration_3")) return false;
    regularFilterDeclaration_3_0(b, l + 1);
    return true;
  }

  // (stringLiteral)
  private boolean regularFilterDeclaration_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "regularFilterDeclaration_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = stringLiteral(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (filterSetDefault)?
  private boolean regularFilterDeclaration_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "regularFilterDeclaration_4")) return false;
    regularFilterDeclaration_4_0(b, l + 1);
    return true;
  }

  // (filterSetDefault)
  private boolean regularFilterDeclaration_4_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "regularFilterDeclaration_4_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = filterSetDefault(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // likePE ((LESS | GREATER | LESS_EQUALS | GREATER_EQUALS) likePE)?
  public boolean relationalPE(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "relationalPE")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, RELATIONAL_PE, "<relational pe>");
    r = likePE(b, l + 1);
    r = r && relationalPE_1(b, l + 1);
    exit_section_(b, l, m, RELATIONAL_PE, r, false, null);
    return r;
  }

  // ((LESS | GREATER | LESS_EQUALS | GREATER_EQUALS) likePE)?
  private boolean relationalPE_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "relationalPE_1")) return false;
    relationalPE_1_0(b, l + 1);
    return true;
  }

  // (LESS | GREATER | LESS_EQUALS | GREATER_EQUALS) likePE
  private boolean relationalPE_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "relationalPE_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = relationalPE_1_0_0(b, l + 1);
    r = r && likePE(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // LESS | GREATER | LESS_EQUALS | GREATER_EQUALS
  private boolean relationalPE_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "relationalPE_1_0_0")) return false;
    boolean r;
    r = consumeToken(b, LESS);
    if (!r) r = consumeToken(b, GREATER);
    if (!r) r = consumeToken(b, LESS_EQUALS);
    if (!r) r = consumeToken(b, GREATER_EQUALS);
    return r;
  }

  /* ********************************************************** */
  // REMOVE componentSelector SEMI
  public boolean removeComponentStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "removeComponentStatement")) return false;
    if (!nextTokenIs(b, REMOVE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, REMOVE_COMPONENT_STATEMENT, null);
    r = consumeToken(b, REMOVE);
    p = r; // pin = 1
    r = r && componentSelector(b, l + 1);
    r = r && consumeToken(b, SEMI);
    exit_section_(b, l, m, REMOVE_COMPONENT_STATEMENT, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // stringLiteral
  public boolean renderPropertyCustomView(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "renderPropertyCustomView")) return false;
    if (!nextTokenIs(b, "<render property custom view>", ID, LEX_STRING_LITERAL)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, RENDER_PROPERTY_CUSTOM_VIEW, "<render property custom view>");
    r = stringLiteral(b, l + 1);
    exit_section_(b, l, m, RENDER_PROPERTY_CUSTOM_VIEW, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // (REPORTS | REPORTFILES) groupObjectReportPath (COMMA groupObjectReportPath)*
  public boolean reportFilesDeclaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reportFilesDeclaration")) return false;
    if (!nextTokenIs(b, "<report files declaration>", REPORTFILES, REPORTS)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, REPORT_FILES_DECLARATION, "<report files declaration>");
    r = reportFilesDeclaration_0(b, l + 1);
    p = r; // pin = 1
    r = r && groupObjectReportPath(b, l + 1);
    r = r && reportFilesDeclaration_2(b, l + 1);
    exit_section_(b, l, m, REPORT_FILES_DECLARATION, r, p, null);
    return r || p;
  }

  // REPORTS | REPORTFILES
  private boolean reportFilesDeclaration_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reportFilesDeclaration_0")) return false;
    boolean r;
    r = consumeToken(b, REPORTS);
    if (!r) r = consumeToken(b, REPORTFILES);
    return r;
  }

  // (COMMA groupObjectReportPath)*
  private boolean reportFilesDeclaration_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reportFilesDeclaration_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!reportFilesDeclaration_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "reportFilesDeclaration_2", c)) break;
    }
    return true;
  }

  // COMMA groupObjectReportPath
  private boolean reportFilesDeclaration_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reportFilesDeclaration_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, COMMA);
    r = r && groupObjectReportPath(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // REPORT formCalcPropertyObject
  public boolean reportSetting(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reportSetting")) return false;
    if (!nextTokenIs(b, REPORT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, REPORT_SETTING, null);
    r = consumeToken(b, REPORT);
    p = r; // pin = 1
    r = r && formCalcPropertyObject(b, l + 1);
    exit_section_(b, l, m, REPORT_SETTING, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // REQUEST actionPropertyDefinitionBody DO actionPropertyDefinitionBody (ELSE actionPropertyDefinitionBody)?
  public boolean requestActionPropertyDefinitionBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "requestActionPropertyDefinitionBody")) return false;
    if (!nextTokenIs(b, REQUEST)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, REQUEST_ACTION_PROPERTY_DEFINITION_BODY, null);
    r = consumeToken(b, REQUEST);
    p = r; // pin = 1
    r = r && actionPropertyDefinitionBody(b, l + 1);
    r = r && consumeToken(b, DO);
    r = r && actionPropertyDefinitionBody(b, l + 1);
    r = r && requestActionPropertyDefinitionBody_4(b, l + 1);
    exit_section_(b, l, m, REQUEST_ACTION_PROPERTY_DEFINITION_BODY, r, p, null);
    return r || p;
  }

  // (ELSE actionPropertyDefinitionBody)?
  private boolean requestActionPropertyDefinitionBody_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "requestActionPropertyDefinitionBody_4")) return false;
    requestActionPropertyDefinitionBody_4_0(b, l + 1);
    return true;
  }

  // ELSE actionPropertyDefinitionBody
  private boolean requestActionPropertyDefinitionBody_4_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "requestActionPropertyDefinitionBody_4_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, ELSE);
    r = r && actionPropertyDefinitionBody(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // REQUIRE nonEmptyModuleUsageList SEMI
  public boolean requireList(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "requireList")) return false;
    if (!nextTokenIs(b, REQUIRE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, REQUIRE_LIST, null);
    r = consumeToken(b, REQUIRE);
    p = r; // pin = 1
    r = r && nonEmptyModuleUsageList(b, l + 1);
    r = r && consumeToken(b, SEMI);
    exit_section_(b, l, m, REQUIRE_LIST, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // RETURN
  public boolean returnActionOperator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "returnActionOperator")) return false;
    if (!nextTokenIs(b, RETURN)) return false;
    boolean r;
    Marker m = enter_section_(b, l, RETURN_ACTION_OPERATOR);
    r = consumeToken(b, RETURN);
    exit_section_(b, m, RETURN_ACTION_OPERATOR, r);
    return r;
  }

  /* ********************************************************** */
  // ROUND LBRAC propertyExpression (COMMA propertyExpression)? RBRAC
  public boolean roundPropertyDefinition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "roundPropertyDefinition")) return false;
    if (!nextTokenIs(b, ROUND)) return false;
    boolean r;
    Marker m = enter_section_(b, l, ROUND_PROPERTY_DEFINITION);
    r = consumeTokens(b, 0, ROUND, LBRAC);
    r = r && propertyExpression(b, l + 1);
    r = r && roundPropertyDefinition_3(b, l + 1);
    r = r && consumeToken(b, RBRAC);
    exit_section_(b, m, ROUND_PROPERTY_DEFINITION, r);
    return r;
  }

  // (COMMA propertyExpression)?
  private boolean roundPropertyDefinition_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "roundPropertyDefinition_3")) return false;
    roundPropertyDefinition_3_0(b, l + 1);
    return true;
  }

  // COMMA propertyExpression
  private boolean roundPropertyDefinition_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "roundPropertyDefinition_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, COMMA);
    r = r && propertyExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // << isExpressionParsing >> propertyExpression
  //         | << isActionParsing >> listActionStatement*
  //         | moduleHeader lazyScriptStatement*
  boolean script(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "script")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = script_0(b, l + 1);
    if (!r) r = script_1(b, l + 1);
    if (!r) r = script_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // << isExpressionParsing >> propertyExpression
  private boolean script_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "script_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = isExpressionParsing(b, l + 1);
    r = r && propertyExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // << isActionParsing >> listActionStatement*
  private boolean script_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "script_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = isActionParsing(b, l + 1);
    r = r && script_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // listActionStatement*
  private boolean script_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "script_1_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!listActionStatement(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "script_1_1", c)) break;
    }
    return true;
  }

  // moduleHeader lazyScriptStatement*
  private boolean script_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "script_2")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = moduleHeader(b, l + 1);
    r = r && script_2_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // lazyScriptStatement*
  private boolean script_2_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "script_2_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!lazyScriptStatement(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "script_2_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // statement
  public boolean scriptStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "scriptStatement")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, SCRIPT_STATEMENT, "<script statement>");
    r = statement(b, l + 1);
    exit_section_(b, l, m, SCRIPT_STATEMENT, r, false, this::script_statement_recover);
    return r;
  }

  /* ********************************************************** */
  // !statement_start
  boolean script_statement_recover(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "script_statement_recover")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !statement_start(b, l + 1);
    exit_section_(b, l, m, null, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // SEEK (FIRST | LAST | NULL)? 
  //                                             (   (objectID EQUALS propertyExpression) 
  //                                             |   (groupObjectID (OBJECTS objectExpr (COMMA objectExpr)*)?)
  //                                             )
  public boolean seekObjectActionPropertyDefinitionBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "seekObjectActionPropertyDefinitionBody")) return false;
    if (!nextTokenIs(b, SEEK)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, SEEK_OBJECT_ACTION_PROPERTY_DEFINITION_BODY, null);
    r = consumeToken(b, SEEK);
    p = r; // pin = 1
    r = r && seekObjectActionPropertyDefinitionBody_1(b, l + 1);
    r = r && seekObjectActionPropertyDefinitionBody_2(b, l + 1);
    exit_section_(b, l, m, SEEK_OBJECT_ACTION_PROPERTY_DEFINITION_BODY, r, p, null);
    return r || p;
  }

  // (FIRST | LAST | NULL)?
  private boolean seekObjectActionPropertyDefinitionBody_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "seekObjectActionPropertyDefinitionBody_1")) return false;
    seekObjectActionPropertyDefinitionBody_1_0(b, l + 1);
    return true;
  }

  // FIRST | LAST | NULL
  private boolean seekObjectActionPropertyDefinitionBody_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "seekObjectActionPropertyDefinitionBody_1_0")) return false;
    boolean r;
    r = consumeToken(b, FIRST);
    if (!r) r = consumeToken(b, LAST);
    if (!r) r = consumeToken(b, NULL);
    return r;
  }

  // (objectID EQUALS propertyExpression) 
  //                                             |   (groupObjectID (OBJECTS objectExpr (COMMA objectExpr)*)?)
  private boolean seekObjectActionPropertyDefinitionBody_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "seekObjectActionPropertyDefinitionBody_2")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = seekObjectActionPropertyDefinitionBody_2_0(b, l + 1);
    if (!r) r = seekObjectActionPropertyDefinitionBody_2_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // objectID EQUALS propertyExpression
  private boolean seekObjectActionPropertyDefinitionBody_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "seekObjectActionPropertyDefinitionBody_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = objectID(b, l + 1);
    r = r && consumeToken(b, EQUALS);
    r = r && propertyExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // groupObjectID (OBJECTS objectExpr (COMMA objectExpr)*)?
  private boolean seekObjectActionPropertyDefinitionBody_2_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "seekObjectActionPropertyDefinitionBody_2_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = groupObjectID(b, l + 1);
    r = r && seekObjectActionPropertyDefinitionBody_2_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (OBJECTS objectExpr (COMMA objectExpr)*)?
  private boolean seekObjectActionPropertyDefinitionBody_2_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "seekObjectActionPropertyDefinitionBody_2_1_1")) return false;
    seekObjectActionPropertyDefinitionBody_2_1_1_0(b, l + 1);
    return true;
  }

  // OBJECTS objectExpr (COMMA objectExpr)*
  private boolean seekObjectActionPropertyDefinitionBody_2_1_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "seekObjectActionPropertyDefinitionBody_2_1_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, OBJECTS);
    r = r && objectExpr(b, l + 1);
    r = r && seekObjectActionPropertyDefinitionBody_2_1_1_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (COMMA objectExpr)*
  private boolean seekObjectActionPropertyDefinitionBody_2_1_1_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "seekObjectActionPropertyDefinitionBody_2_1_1_0_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!seekObjectActionPropertyDefinitionBody_2_1_1_0_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "seekObjectActionPropertyDefinitionBody_2_1_1_0_2", c)) break;
    }
    return true;
  }

  // COMMA objectExpr
  private boolean seekObjectActionPropertyDefinitionBody_2_1_1_0_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "seekObjectActionPropertyDefinitionBody_2_1_1_0_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, COMMA);
    r = r && objectExpr(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // TOP intLiteral
  public boolean selectTop(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "selectTop")) return false;
    if (!nextTokenIs(b, TOP)) return false;
    boolean r;
    Marker m = enter_section_(b, l, SELECT_TOP);
    r = consumeToken(b, TOP);
    r = r && intLiteral(b, l + 1);
    exit_section_(b, m, SELECT_TOP, r);
    return r;
  }

  /* ********************************************************** */
  // TOP ((groupObjectUsage EQUALS intLiteral (COMMA groupObjectUsage EQUALS intLiteral)*) | intLiteral)
  public boolean selectTops(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "selectTops")) return false;
    if (!nextTokenIs(b, TOP)) return false;
    boolean r;
    Marker m = enter_section_(b, l, SELECT_TOPS);
    r = consumeToken(b, TOP);
    r = r && selectTops_1(b, l + 1);
    exit_section_(b, m, SELECT_TOPS, r);
    return r;
  }

  // (groupObjectUsage EQUALS intLiteral (COMMA groupObjectUsage EQUALS intLiteral)*) | intLiteral
  private boolean selectTops_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "selectTops_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = selectTops_1_0(b, l + 1);
    if (!r) r = intLiteral(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // groupObjectUsage EQUALS intLiteral (COMMA groupObjectUsage EQUALS intLiteral)*
  private boolean selectTops_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "selectTops_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = groupObjectUsage(b, l + 1);
    r = r && consumeToken(b, EQUALS);
    r = r && intLiteral(b, l + 1);
    r = r && selectTops_1_0_3(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (COMMA groupObjectUsage EQUALS intLiteral)*
  private boolean selectTops_1_0_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "selectTops_1_0_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!selectTops_1_0_3_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "selectTops_1_0_3", c)) break;
    }
    return true;
  }

  // COMMA groupObjectUsage EQUALS intLiteral
  private boolean selectTops_1_0_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "selectTops_1_0_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, COMMA);
    r = r && groupObjectUsage(b, l + 1);
    r = r && consumeToken(b, EQUALS);
    r = r && intLiteral(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // IN groupUsage
  //                            |	persistentSetting
  //                            |    complexSetting
  //                            |    prereadSetting
  //                            |    hintSetting
  //                            |	TABLE tableUsage
  //                            |	asEditActionSetting
  //                            |	viewTypeSetting
  //                            |	customViewSetting
  //                            |	flexCharWidthSetting
  //                            |	charWidthSetting
  //                            |	imageSetting
  //                            |    defaultCompareSetting
  //                            |	changeKeySetting
  //                            |	changeMouseSetting
  //                            |	autosetSetting
  //                            |	confirmSetting
  //                            |	patternSetting
  //                            |	regexpSetting
  //                            |	loggableSetting
  //                            |	echoSymbolsSetting
  //                            |	indexSetting
  //                            |    aggrSetting
  //                            |	notNullSetting
  //                            |	eventIdSetting
  //                            |    stickyOption
  //                            |    syncTypeLiteral
  //                            |   ATSIGN2 simpleName
  boolean semiPropertyOption(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "semiPropertyOption")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = semiPropertyOption_0(b, l + 1);
    if (!r) r = persistentSetting(b, l + 1);
    if (!r) r = complexSetting(b, l + 1);
    if (!r) r = prereadSetting(b, l + 1);
    if (!r) r = hintSetting(b, l + 1);
    if (!r) r = semiPropertyOption_5(b, l + 1);
    if (!r) r = asEditActionSetting(b, l + 1);
    if (!r) r = viewTypeSetting(b, l + 1);
    if (!r) r = customViewSetting(b, l + 1);
    if (!r) r = flexCharWidthSetting(b, l + 1);
    if (!r) r = charWidthSetting(b, l + 1);
    if (!r) r = imageSetting(b, l + 1);
    if (!r) r = defaultCompareSetting(b, l + 1);
    if (!r) r = changeKeySetting(b, l + 1);
    if (!r) r = changeMouseSetting(b, l + 1);
    if (!r) r = autosetSetting(b, l + 1);
    if (!r) r = confirmSetting(b, l + 1);
    if (!r) r = patternSetting(b, l + 1);
    if (!r) r = regexpSetting(b, l + 1);
    if (!r) r = loggableSetting(b, l + 1);
    if (!r) r = echoSymbolsSetting(b, l + 1);
    if (!r) r = indexSetting(b, l + 1);
    if (!r) r = aggrSetting(b, l + 1);
    if (!r) r = notNullSetting(b, l + 1);
    if (!r) r = eventIdSetting(b, l + 1);
    if (!r) r = stickyOption(b, l + 1);
    if (!r) r = syncTypeLiteral(b, l + 1);
    if (!r) r = semiPropertyOption_27(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // IN groupUsage
  private boolean semiPropertyOption_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "semiPropertyOption_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, IN);
    r = r && groupUsage(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // TABLE tableUsage
  private boolean semiPropertyOption_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "semiPropertyOption_5")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, TABLE);
    r = r && tableUsage(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ATSIGN2 simpleName
  private boolean semiPropertyOption_27(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "semiPropertyOption_27")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, ATSIGN2);
    r = r && simpleName(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // sessionPropertyType LBRAC propertyExpression RBRAC
  public boolean sessionPropertyDefinition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "sessionPropertyDefinition")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, SESSION_PROPERTY_DEFINITION, "<session property definition>");
    r = sessionPropertyType(b, l + 1);
    p = r; // pin = 1
    r = r && consumeToken(b, LBRAC);
    r = r && propertyExpression(b, l + 1);
    r = r && consumeToken(b, RBRAC);
    exit_section_(b, l, m, SESSION_PROPERTY_DEFINITION, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // PREV | CHANGED | SET | DROPPED | SETCHANGED | DROPCHANGED | SETDROPPED
  public boolean sessionPropertyType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "sessionPropertyType")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, SESSION_PROPERTY_TYPE, "<session property type>");
    r = consumeToken(b, PREV);
    if (!r) r = consumeToken(b, CHANGED);
    if (!r) r = consumeToken(b, SET);
    if (!r) r = consumeToken(b, DROPPED);
    if (!r) r = consumeToken(b, SETCHANGED);
    if (!r) r = consumeToken(b, DROPCHANGED);
    if (!r) r = consumeToken(b, SETDROPPED);
    exit_section_(b, l, m, SESSION_PROPERTY_TYPE, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // ID EQUALS componentPropertyValue SEMI
  public boolean setObjectPropertyStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "setObjectPropertyStatement")) return false;
    if (!nextTokenIs(b, ID)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, SET_OBJECT_PROPERTY_STATEMENT, null);
    r = consumeTokens(b, 2, ID, EQUALS);
    p = r; // pin = 2
    r = r && componentPropertyValue(b, l + 1);
    r = r && consumeToken(b, SEMI);
    exit_section_(b, l, m, SET_OBJECT_PROPERTY_STATEMENT, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // componentSelector componentBody
  public boolean setupComponentStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "setupComponentStatement")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, SETUP_COMPONENT_STATEMENT, "<setup component statement>");
    r = componentSelector(b, l + 1);
    p = r; // pin = 1
    r = r && componentBody(b, l + 1);
    exit_section_(b, l, m, SETUP_COMPONENT_STATEMENT, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // navigatorElementSelector (localizedStringLiteral)? navigatorElementOptions navigatorElementStatementBody
  public boolean setupNavigatorElementStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "setupNavigatorElementStatement")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, SETUP_NAVIGATOR_ELEMENT_STATEMENT, "<setup navigator element statement>");
    r = navigatorElementSelector(b, l + 1);
    p = r; // pin = 1
    r = r && setupNavigatorElementStatement_1(b, l + 1);
    r = r && navigatorElementOptions(b, l + 1);
    r = r && navigatorElementStatementBody(b, l + 1);
    exit_section_(b, l, m, SETUP_NAVIGATOR_ELEMENT_STATEMENT, r, p, null);
    return r || p;
  }

  // (localizedStringLiteral)?
  private boolean setupNavigatorElementStatement_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "setupNavigatorElementStatement_1")) return false;
    setupNavigatorElementStatement_1_0(b, l + 1);
    return true;
  }

  // (localizedStringLiteral)
  private boolean setupNavigatorElementStatement_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "setupNavigatorElementStatement_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = localizedStringLiteral(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // SHEET propertyExpression
  public boolean sheetExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "sheetExpression")) return false;
    if (!nextTokenIs(b, SHEET)) return false;
    boolean r;
    Marker m = enter_section_(b, l, SHEET_EXPRESSION);
    r = consumeToken(b, SHEET);
    r = r && propertyExpression(b, l + 1);
    exit_section_(b, m, SHEET_EXPRESSION, r);
    return r;
  }

  /* ********************************************************** */
  // SHOWDEP noContextActionOrPropertyUsage FROM noContextActionOrPropertyUsage SEMI
  public boolean showDepStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "showDepStatement")) return false;
    if (!nextTokenIs(b, SHOWDEP)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, SHOW_DEP_STATEMENT, null);
    r = consumeToken(b, SHOWDEP);
    p = r; // pin = 1
    r = r && noContextActionOrPropertyUsage(b, l + 1);
    r = r && consumeToken(b, FROM);
    r = r && noContextActionOrPropertyUsage(b, l + 1);
    r = r && consumeToken(b, SEMI);
    exit_section_(b, l, m, SHOW_DEP_STATEMENT, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // IS CLASS LBRAC propertyExpression RBRAC
  public boolean signaturePropertyDefinition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "signaturePropertyDefinition")) return false;
    if (!nextTokenIs(b, IS)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, SIGNATURE_PROPERTY_DEFINITION, null);
    r = consumeTokens(b, 2, IS, CLASS, LBRAC);
    p = r; // pin = 2
    r = r && propertyExpression(b, l + 1);
    r = r && consumeToken(b, RBRAC);
    exit_section_(b, l, m, SIGNATURE_PROPERTY_DEFINITION, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // (simpleName? localizedStringLiteral? EQUALS)? formElseNoParamsActionUsage
  public boolean simpleElementDescription(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "simpleElementDescription")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, SIMPLE_ELEMENT_DESCRIPTION, "<simple element description>");
    r = simpleElementDescription_0(b, l + 1);
    r = r && formElseNoParamsActionUsage(b, l + 1);
    exit_section_(b, l, m, SIMPLE_ELEMENT_DESCRIPTION, r, false, null);
    return r;
  }

  // (simpleName? localizedStringLiteral? EQUALS)?
  private boolean simpleElementDescription_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "simpleElementDescription_0")) return false;
    simpleElementDescription_0_0(b, l + 1);
    return true;
  }

  // simpleName? localizedStringLiteral? EQUALS
  private boolean simpleElementDescription_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "simpleElementDescription_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = simpleElementDescription_0_0_0(b, l + 1);
    r = r && simpleElementDescription_0_0_1(b, l + 1);
    r = r && consumeToken(b, EQUALS);
    exit_section_(b, m, null, r);
    return r;
  }

  // simpleName?
  private boolean simpleElementDescription_0_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "simpleElementDescription_0_0_0")) return false;
    simpleName(b, l + 1);
    return true;
  }

  // localizedStringLiteral?
  private boolean simpleElementDescription_0_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "simpleElementDescription_0_0_1")) return false;
    localizedStringLiteral(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // ID
  public boolean simpleName(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "simpleName")) return false;
    if (!nextTokenIs(b, ID)) return false;
    boolean r;
    Marker m = enter_section_(b, l, SIMPLE_NAME);
    r = consumeToken(b, ID);
    exit_section_(b, m, SIMPLE_NAME, r);
    return r;
  }

  /* ********************************************************** */
  // (simpleName (localizedStringLiteral)?) | localizedStringLiteral
  boolean simpleNameOrWithCaption(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "simpleNameOrWithCaption")) return false;
    if (!nextTokenIs(b, "", ID, LEX_STRING_LITERAL)) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = simpleNameOrWithCaption_0(b, l + 1);
    if (!r) r = localizedStringLiteral(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // simpleName (localizedStringLiteral)?
  private boolean simpleNameOrWithCaption_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "simpleNameOrWithCaption_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = simpleName(b, l + 1);
    r = r && simpleNameOrWithCaption_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (localizedStringLiteral)?
  private boolean simpleNameOrWithCaption_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "simpleNameOrWithCaption_0_1")) return false;
    simpleNameOrWithCaption_0_1_0(b, l + 1);
    return true;
  }

  // (localizedStringLiteral)
  private boolean simpleNameOrWithCaption_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "simpleNameOrWithCaption_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = localizedStringLiteral(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // simpleName (localizedStringLiteral)?
  public boolean simpleNameWithCaption(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "simpleNameWithCaption")) return false;
    if (!nextTokenIs(b, ID)) return false;
    boolean r;
    Marker m = enter_section_(b, l, SIMPLE_NAME_WITH_CAPTION);
    r = simpleName(b, l + 1);
    r = r && simpleNameWithCaption_1(b, l + 1);
    exit_section_(b, m, SIMPLE_NAME_WITH_CAPTION, r);
    return r;
  }

  // (localizedStringLiteral)?
  private boolean simpleNameWithCaption_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "simpleNameWithCaption_1")) return false;
    simpleNameWithCaption_1_0(b, l + 1);
    return true;
  }

  // (localizedStringLiteral)
  private boolean simpleNameWithCaption_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "simpleNameWithCaption_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = localizedStringLiteral(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // (LBRAC propertyExpression RBRAC) | expressionPrimitive
  public boolean simplePE(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "simplePE")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, SIMPLE_PE, "<simple pe>");
    r = simplePE_0(b, l + 1);
    if (!r) r = expressionPrimitive(b, l + 1);
    exit_section_(b, l, m, SIMPLE_PE, r, false, null);
    return r;
  }

  // LBRAC propertyExpression RBRAC
  private boolean simplePE_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "simplePE_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, LBRAC);
    r = r && propertyExpression(b, l + 1);
    r = r && consumeToken(b, RBRAC);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // metaCodeDeclarationStatement | innerStatement
  boolean statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "statement")) return false;
    boolean r;
    r = metaCodeDeclarationStatement(b, l + 1);
    if (!r) r = innerStatement(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // META | inner_statement_start
  boolean statement_start(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "statement_start")) return false;
    boolean r;
    r = consumeToken(b, META);
    if (!r) r = inner_statement_start(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // TO noParamsPropertyUsage
  public boolean staticDestination(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "staticDestination")) return false;
    if (!nextTokenIs(b, TO)) return false;
    boolean r;
    Marker m = enter_section_(b, l, STATIC_DESTINATION);
    r = consumeToken(b, TO);
    r = r && noParamsPropertyUsage(b, l + 1);
    exit_section_(b, m, STATIC_DESTINATION, r);
    return r;
  }

  /* ********************************************************** */
  // simpleNameWithCaption
  public boolean staticObjectDecl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "staticObjectDecl")) return false;
    if (!nextTokenIs(b, ID)) return false;
    boolean r;
    Marker m = enter_section_(b, l, STATIC_OBJECT_DECL);
    r = simpleNameWithCaption(b, l + 1);
    exit_section_(b, m, STATIC_OBJECT_DECL, r);
    return r;
  }

  /* ********************************************************** */
  // (nonEmptyStaticObjectDeclList)?
  public boolean staticObjectDeclList(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "staticObjectDeclList")) return false;
    Marker m = enter_section_(b, l, _NONE_, STATIC_OBJECT_DECL_LIST, "<static object decl list>");
    staticObjectDeclList_0(b, l + 1);
    exit_section_(b, l, m, STATIC_OBJECT_DECL_LIST, true, false, null);
    return true;
  }

  // (nonEmptyStaticObjectDeclList)
  private boolean staticObjectDeclList_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "staticObjectDeclList_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = nonEmptyStaticObjectDeclList(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // <<fullCompoundParamDeclareStop>> <<innerIDCheck>> customClassUsage POINT simpleName
  public boolean staticObjectID(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "staticObjectID")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, STATIC_OBJECT_ID, "<static object id>");
    r = fullCompoundParamDeclareStop(b, l + 1);
    r = r && innerIDCheck(b, l + 1);
    r = r && customClassUsage(b, l + 1);
    r = r && consumeToken(b, POINT);
    r = r && simpleName(b, l + 1);
    exit_section_(b, l, m, STATIC_OBJECT_ID, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // (IMAGE stringLiteral) | NOIMAGE
  public boolean staticObjectImage(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "staticObjectImage")) return false;
    if (!nextTokenIs(b, "<static object image>", IMAGE, NOIMAGE)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, STATIC_OBJECT_IMAGE, "<static object image>");
    r = staticObjectImage_0(b, l + 1);
    if (!r) r = consumeToken(b, NOIMAGE);
    exit_section_(b, l, m, STATIC_OBJECT_IMAGE, r, false, null);
    return r;
  }

  // IMAGE stringLiteral
  private boolean staticObjectImage_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "staticObjectImage_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, IMAGE);
    r = r && stringLiteral(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // FIRST | LAST | DEFAULT
  public boolean staticRelativePosition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "staticRelativePosition")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, STATIC_RELATIVE_POSITION, "<static relative position>");
    r = consumeToken(b, FIRST);
    if (!r) r = consumeToken(b, LAST);
    if (!r) r = consumeToken(b, DEFAULT);
    exit_section_(b, l, m, STATIC_RELATIVE_POSITION, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // STICKY | NOSTICKY
  public boolean stickyOption(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "stickyOption")) return false;
    if (!nextTokenIs(b, "<sticky option>", NOSTICKY, STICKY)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, STICKY_OPTION, "<sticky option>");
    r = consumeToken(b, STICKY);
    if (!r) r = consumeToken(b, NOSTICKY);
    exit_section_(b, l, m, STICKY_OPTION, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // LEX_STRING_LITERAL | ID
  public boolean stringLiteral(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "stringLiteral")) return false;
    if (!nextTokenIs(b, "<string literal>", ID, LEX_STRING_LITERAL)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, STRING_LITERAL, "<string literal>");
    r = consumeToken(b, LEX_STRING_LITERAL);
    if (!r) r = consumeToken(b, ID);
    exit_section_(b, l, m, STRING_LITERAL, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // STRUCT LBRAC nonEmptyPropertyExpressionList RBRAC
  public boolean structCreationPropertyDefinition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "structCreationPropertyDefinition")) return false;
    if (!nextTokenIs(b, STRUCT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, STRUCT_CREATION_PROPERTY_DEFINITION, null);
    r = consumeTokens(b, 1, STRUCT, LBRAC);
    p = r; // pin = 1
    r = r && nonEmptyPropertyExpressionList(b, l + 1);
    r = r && consumeToken(b, RBRAC);
    exit_section_(b, l, m, STRUCT_CREATION_PROPERTY_DEFINITION, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // EXTEND (CLASS | FORM) ID
  //     | CLASS (ABSTRACT | NATIVE)? COMPLEX? ID
  //     | (ATSIGN | ATSIGN2 | FORM | GROUP | META | DESIGN | TABLE) ID
  //     | WINDOW windowType ID
  public boolean stubStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "stubStatement")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, STUB_STATEMENT, "<stub statement>");
    r = stubStatement_0(b, l + 1);
    if (!r) r = stubStatement_1(b, l + 1);
    if (!r) r = stubStatement_2(b, l + 1);
    if (!r) r = stubStatement_3(b, l + 1);
    exit_section_(b, l, m, STUB_STATEMENT, r, false, null);
    return r;
  }

  // EXTEND (CLASS | FORM) ID
  private boolean stubStatement_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "stubStatement_0")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, EXTEND);
    p = r; // pin = 1
    r = r && stubStatement_0_1(b, l + 1);
    r = r && consumeToken(b, ID);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  // CLASS | FORM
  private boolean stubStatement_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "stubStatement_0_1")) return false;
    boolean r;
    r = consumeToken(b, CLASS);
    if (!r) r = consumeToken(b, FORM);
    return r;
  }

  // CLASS (ABSTRACT | NATIVE)? COMPLEX? ID
  private boolean stubStatement_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "stubStatement_1")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, CLASS);
    p = r; // pin = 1
    r = r && stubStatement_1_1(b, l + 1);
    r = r && stubStatement_1_2(b, l + 1);
    r = r && consumeToken(b, ID);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  // (ABSTRACT | NATIVE)?
  private boolean stubStatement_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "stubStatement_1_1")) return false;
    stubStatement_1_1_0(b, l + 1);
    return true;
  }

  // ABSTRACT | NATIVE
  private boolean stubStatement_1_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "stubStatement_1_1_0")) return false;
    boolean r;
    r = consumeToken(b, ABSTRACT);
    if (!r) r = consumeToken(b, NATIVE);
    return r;
  }

  // COMPLEX?
  private boolean stubStatement_1_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "stubStatement_1_2")) return false;
    consumeToken(b, COMPLEX);
    return true;
  }

  // (ATSIGN | ATSIGN2 | FORM | GROUP | META | DESIGN | TABLE) ID
  private boolean stubStatement_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "stubStatement_2")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = stubStatement_2_0(b, l + 1);
    p = r; // pin = 1
    r = r && consumeToken(b, ID);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  // ATSIGN | ATSIGN2 | FORM | GROUP | META | DESIGN | TABLE
  private boolean stubStatement_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "stubStatement_2_0")) return false;
    boolean r;
    r = consumeToken(b, ATSIGN);
    if (!r) r = consumeToken(b, ATSIGN2);
    if (!r) r = consumeToken(b, FORM);
    if (!r) r = consumeToken(b, GROUP);
    if (!r) r = consumeToken(b, META);
    if (!r) r = consumeToken(b, DESIGN);
    if (!r) r = consumeToken(b, TABLE);
    return r;
  }

  // WINDOW windowType ID
  private boolean stubStatement_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "stubStatement_3")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, WINDOW);
    p = r; // pin = 1
    r = r && windowType(b, l + 1);
    r = r && consumeToken(b, ID);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // WAIT | NOWAIT
  public boolean syncTypeLiteral(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "syncTypeLiteral")) return false;
    if (!nextTokenIs(b, "<sync type literal>", NOWAIT, WAIT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, SYNC_TYPE_LITERAL, "<sync type literal>");
    r = consumeToken(b, WAIT);
    if (!r) r = consumeToken(b, NOWAIT);
    exit_section_(b, l, m, SYNC_TYPE_LITERAL, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // TABLE simpleName stringLiteral? LBRAC classNameList RBRAC (FULL | noDefault)? SEMI
  public boolean tableStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tableStatement")) return false;
    if (!nextTokenIs(b, TABLE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, TABLE_STATEMENT, null);
    r = consumeToken(b, TABLE);
    r = r && simpleName(b, l + 1);
    p = r; // pin = 2
    r = r && tableStatement_2(b, l + 1);
    r = r && consumeToken(b, LBRAC);
    r = r && classNameList(b, l + 1);
    r = r && consumeToken(b, RBRAC);
    r = r && tableStatement_6(b, l + 1);
    r = r && consumeToken(b, SEMI);
    exit_section_(b, l, m, TABLE_STATEMENT, r, p, null);
    return r || p;
  }

  // stringLiteral?
  private boolean tableStatement_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tableStatement_2")) return false;
    stringLiteral(b, l + 1);
    return true;
  }

  // (FULL | noDefault)?
  private boolean tableStatement_6(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tableStatement_6")) return false;
    tableStatement_6_0(b, l + 1);
    return true;
  }

  // FULL | noDefault
  private boolean tableStatement_6_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tableStatement_6_0")) return false;
    boolean r;
    r = consumeToken(b, FULL);
    if (!r) r = noDefault(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // compoundID
  public boolean tableUsage(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tableUsage")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, TABLE_USAGE, "<table usage>");
    r = compoundID(b, l + 1);
    exit_section_(b, l, m, TABLE_USAGE, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // LEX_T_LOGICAL_LITERAL
  public boolean tbooleanLiteral(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tbooleanLiteral")) return false;
    if (!nextTokenIs(b, LEX_T_LOGICAL_LITERAL)) return false;
    boolean r;
    Marker m = enter_section_(b, l, TBOOLEAN_LITERAL);
    r = consumeToken(b, LEX_T_LOGICAL_LITERAL);
    exit_section_(b, m, TBOOLEAN_LITERAL, r);
    return r;
  }

  /* ********************************************************** */
  // breakActionOperator | continueActionOperator | returnActionOperator
  public boolean terminalFlowActionPropertyDefinitionBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "terminalFlowActionPropertyDefinitionBody")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, TERMINAL_FLOW_ACTION_PROPERTY_DEFINITION_BODY, "<terminal flow action property definition body>");
    r = breakActionOperator(b, l + 1);
    if (!r) r = continueActionOperator(b, l + 1);
    if (!r) r = returnActionOperator(b, l + 1);
    exit_section_(b, l, m, TERMINAL_FLOW_ACTION_PROPERTY_DEFINITION_BODY, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // LEX_TIME_LITERAL
  public boolean timeLiteral(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "timeLiteral")) return false;
    if (!nextTokenIs(b, LEX_TIME_LITERAL)) return false;
    boolean r;
    Marker m = enter_section_(b, l, TIME_LITERAL);
    r = consumeToken(b, LEX_TIME_LITERAL);
    exit_section_(b, m, TIME_LITERAL, r);
    return r;
  }

  /* ********************************************************** */
  // listActionPropertyDefinitionBody
  boolean topActionPropertyDefinitionBody(PsiBuilder b, int l) {
    return listActionPropertyDefinitionBody(b, l + 1);
  }

  /* ********************************************************** */
  // simpleName
  public boolean treeGroupDeclaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "treeGroupDeclaration")) return false;
    if (!nextTokenIs(b, ID)) return false;
    boolean r;
    Marker m = enter_section_(b, l, TREE_GROUP_DECLARATION);
    r = simpleName(b, l + 1);
    exit_section_(b, m, TREE_GROUP_DECLARATION, r);
    return r;
  }

  /* ********************************************************** */
  // (PARENT formExprDeclaration) | (LBRAC PARENT formExprDeclaration (COMMA formExprDeclaration)* RBRAC)
  public boolean treeGroupParentDeclaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "treeGroupParentDeclaration")) return false;
    if (!nextTokenIs(b, "<tree group parent declaration>", LBRAC, PARENT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, TREE_GROUP_PARENT_DECLARATION, "<tree group parent declaration>");
    r = treeGroupParentDeclaration_0(b, l + 1);
    if (!r) r = treeGroupParentDeclaration_1(b, l + 1);
    exit_section_(b, l, m, TREE_GROUP_PARENT_DECLARATION, r, false, null);
    return r;
  }

  // PARENT formExprDeclaration
  private boolean treeGroupParentDeclaration_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "treeGroupParentDeclaration_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, PARENT);
    r = r && formExprDeclaration(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // LBRAC PARENT formExprDeclaration (COMMA formExprDeclaration)* RBRAC
  private boolean treeGroupParentDeclaration_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "treeGroupParentDeclaration_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeTokens(b, 0, LBRAC, PARENT);
    r = r && formExprDeclaration(b, l + 1);
    r = r && treeGroupParentDeclaration_1_3(b, l + 1);
    r = r && consumeToken(b, RBRAC);
    exit_section_(b, m, null, r);
    return r;
  }

  // (COMMA formExprDeclaration)*
  private boolean treeGroupParentDeclaration_1_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "treeGroupParentDeclaration_1_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!treeGroupParentDeclaration_1_3_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "treeGroupParentDeclaration_1_3", c)) break;
    }
    return true;
  }

  // COMMA formExprDeclaration
  private boolean treeGroupParentDeclaration_1_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "treeGroupParentDeclaration_1_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, COMMA);
    r = r && formExprDeclaration(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // treeGroupUsage
  public boolean treeGroupSelector(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "treeGroupSelector")) return false;
    if (!nextTokenIs(b, ID)) return false;
    boolean r;
    Marker m = enter_section_(b, l, TREE_GROUP_SELECTOR);
    r = treeGroupUsage(b, l + 1);
    exit_section_(b, m, TREE_GROUP_SELECTOR, r);
    return r;
  }

  /* ********************************************************** */
  // simpleName
  public boolean treeGroupUsage(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "treeGroupUsage")) return false;
    if (!nextTokenIs(b, ID)) return false;
    boolean r;
    Marker m = enter_section_(b, l, TREE_GROUP_USAGE);
    r = simpleName(b, l + 1);
    exit_section_(b, m, TREE_GROUP_USAGE, r);
    return r;
  }

  /* ********************************************************** */
  // TRY actionPropertyDefinitionBody (CATCH actionPropertyDefinitionBody)? (FINALLY actionPropertyDefinitionBody)?
  public boolean tryActionPropertyDefinitionBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tryActionPropertyDefinitionBody")) return false;
    if (!nextTokenIs(b, TRY)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, TRY_ACTION_PROPERTY_DEFINITION_BODY, null);
    r = consumeToken(b, TRY);
    p = r; // pin = 1
    r = r && actionPropertyDefinitionBody(b, l + 1);
    r = r && tryActionPropertyDefinitionBody_2(b, l + 1);
    r = r && tryActionPropertyDefinitionBody_3(b, l + 1);
    exit_section_(b, l, m, TRY_ACTION_PROPERTY_DEFINITION_BODY, r, p, null);
    return r || p;
  }

  // (CATCH actionPropertyDefinitionBody)?
  private boolean tryActionPropertyDefinitionBody_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tryActionPropertyDefinitionBody_2")) return false;
    tryActionPropertyDefinitionBody_2_0(b, l + 1);
    return true;
  }

  // CATCH actionPropertyDefinitionBody
  private boolean tryActionPropertyDefinitionBody_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tryActionPropertyDefinitionBody_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, CATCH);
    r = r && actionPropertyDefinitionBody(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (FINALLY actionPropertyDefinitionBody)?
  private boolean tryActionPropertyDefinitionBody_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tryActionPropertyDefinitionBody_3")) return false;
    tryActionPropertyDefinitionBody_3_0(b, l + 1);
    return true;
  }

  // FINALLY actionPropertyDefinitionBody
  private boolean tryActionPropertyDefinitionBody_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tryActionPropertyDefinitionBody_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, FINALLY);
    r = r && actionPropertyDefinitionBody(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // IS | AS
  public boolean typeIs(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "typeIs")) return false;
    if (!nextTokenIs(b, "<type is>", AS, IS)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, TYPE_IS, "<type is>");
    r = consumeToken(b, IS);
    if (!r) r = consumeToken(b, AS);
    exit_section_(b, l, m, TYPE_IS, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // MULT | DIV
  public boolean typeMult(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "typeMult")) return false;
    if (!nextTokenIs(b, "<type mult>", DIV, MULT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, TYPE_MULT, "<type mult>");
    r = consumeToken(b, MULT);
    if (!r) r = consumeToken(b, DIV);
    exit_section_(b, l, m, TYPE_MULT, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // typeIs className
  public boolean typePropertyDefinition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "typePropertyDefinition")) return false;
    if (!nextTokenIs(b, "<type property definition>", AS, IS)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, TYPE_PROPERTY_DEFINITION, "<type property definition>");
    r = typeIs(b, l + 1);
    p = r; // pin = 1
    r = r && className(b, l + 1);
    exit_section_(b, l, m, TYPE_PROPERTY_DEFINITION, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // LEX_UDOUBLE_LITERAL
  public boolean udoubleLiteral(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "udoubleLiteral")) return false;
    if (!nextTokenIs(b, LEX_UDOUBLE_LITERAL)) return false;
    boolean r;
    Marker m = enter_section_(b, l, UDOUBLE_LITERAL);
    r = consumeToken(b, LEX_UDOUBLE_LITERAL);
    exit_section_(b, m, UDOUBLE_LITERAL, r);
    return r;
  }

  /* ********************************************************** */
  // LEX_UINT_LITERAL
  public boolean uintLiteral(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "uintLiteral")) return false;
    if (!nextTokenIs(b, LEX_UINT_LITERAL)) return false;
    boolean r;
    Marker m = enter_section_(b, l, UINT_LITERAL);
    r = consumeToken(b, LEX_UINT_LITERAL);
    exit_section_(b, m, UINT_LITERAL, r);
    return r;
  }

  /* ********************************************************** */
  // LEX_ULONG_LITERAL
  public boolean ulongLiteral(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ulongLiteral")) return false;
    if (!nextTokenIs(b, LEX_ULONG_LITERAL)) return false;
    boolean r;
    Marker m = enter_section_(b, l, ULONG_LITERAL);
    r = consumeToken(b, LEX_ULONG_LITERAL);
    exit_section_(b, m, ULONG_LITERAL, r);
    return r;
  }

  /* ********************************************************** */
  // (MINUS unaryMinusPE) | postfixUnaryPE
  public boolean unaryMinusPE(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unaryMinusPE")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, UNARY_MINUS_PE, "<unary minus pe>");
    r = unaryMinusPE_0(b, l + 1);
    if (!r) r = postfixUnaryPE(b, l + 1);
    exit_section_(b, l, m, UNARY_MINUS_PE, r, false, null);
    return r;
  }

  // MINUS unaryMinusPE
  private boolean unaryMinusPE_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unaryMinusPE_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, MINUS);
    r = r && unaryMinusPE(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // paramDeclare
  public boolean untypedParamDeclare(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "untypedParamDeclare")) return false;
    if (!nextTokenIs(b, ID)) return false;
    boolean r;
    Marker m = enter_section_(b, l, UNTYPED_PARAM_DECLARE);
    r = paramDeclare(b, l + 1);
    exit_section_(b, m, UNTYPED_PARAM_DECLARE, r);
    return r;
  }

  /* ********************************************************** */
  // LEX_UNUMERIC_LITERAL
  public boolean unumericLiteral(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unumericLiteral")) return false;
    if (!nextTokenIs(b, LEX_UNUMERIC_LITERAL)) return false;
    boolean r;
    Marker m = enter_section_(b, l, UNUMERIC_LITERAL);
    r = consumeToken(b, LEX_UNUMERIC_LITERAL);
    exit_section_(b, m, UNUMERIC_LITERAL, r);
    return r;
  }

  /* ********************************************************** */
  // USERFILTERS formPropertyDrawUsage (COMMA formPropertyDrawUsage)*
  public boolean userFiltersDeclaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "userFiltersDeclaration")) return false;
    if (!nextTokenIs(b, USERFILTERS)) return false;
    boolean r;
    Marker m = enter_section_(b, l, USER_FILTERS_DECLARATION);
    r = consumeToken(b, USERFILTERS);
    r = r && formPropertyDrawUsage(b, l + 1);
    r = r && userFiltersDeclaration_2(b, l + 1);
    exit_section_(b, m, USER_FILTERS_DECLARATION, r);
    return r;
  }

  // (COMMA formPropertyDrawUsage)*
  private boolean userFiltersDeclaration_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "userFiltersDeclaration_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!userFiltersDeclaration_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "userFiltersDeclaration_2", c)) break;
    }
    return true;
  }

  // COMMA formPropertyDrawUsage
  private boolean userFiltersDeclaration_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "userFiltersDeclaration_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, COMMA);
    r = r && formPropertyDrawUsage(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // classViewType
  public boolean viewTypeSetting(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "viewTypeSetting")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, VIEW_TYPE_SETTING, "<view type setting>");
    r = classViewType(b, l + 1);
    exit_section_(b, l, m, VIEW_TYPE_SETTING, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // WHERE propertyExpression
  boolean whereInputProp(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "whereInputProp")) return false;
    if (!nextTokenIs(b, WHERE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, WHERE);
    p = r; // pin = 1
    r = r && propertyExpression(b, l + 1);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // WHERE propertyExpression
  public boolean wherePropertyExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "wherePropertyExpression")) return false;
    if (!nextTokenIs(b, WHERE)) return false;
    boolean r;
    Marker m = enter_section_(b, l, WHERE_PROPERTY_EXPRESSION);
    r = consumeToken(b, WHERE);
    r = r && propertyExpression(b, l + 1);
    exit_section_(b, m, WHERE_PROPERTY_EXPRESSION, r);
    return r;
  }

  /* ********************************************************** */
  // WHILE (propertyExpression (ORDER (DESC)? nonEmptyPropertyExpressionList)? )?
  // 		                              inlineOption
  // 		                              (forAddObjClause)?
  // 		                              DO actionPropertyDefinitionBody
  public boolean whileActionPropertyDefinitionBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "whileActionPropertyDefinitionBody")) return false;
    if (!nextTokenIs(b, WHILE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, WHILE_ACTION_PROPERTY_DEFINITION_BODY, null);
    r = consumeToken(b, WHILE);
    p = r; // pin = 1
    r = r && whileActionPropertyDefinitionBody_1(b, l + 1);
    r = r && inlineOption(b, l + 1);
    r = r && whileActionPropertyDefinitionBody_3(b, l + 1);
    r = r && consumeToken(b, DO);
    r = r && actionPropertyDefinitionBody(b, l + 1);
    exit_section_(b, l, m, WHILE_ACTION_PROPERTY_DEFINITION_BODY, r, p, null);
    return r || p;
  }

  // (propertyExpression (ORDER (DESC)? nonEmptyPropertyExpressionList)? )?
  private boolean whileActionPropertyDefinitionBody_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "whileActionPropertyDefinitionBody_1")) return false;
    whileActionPropertyDefinitionBody_1_0(b, l + 1);
    return true;
  }

  // propertyExpression (ORDER (DESC)? nonEmptyPropertyExpressionList)?
  private boolean whileActionPropertyDefinitionBody_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "whileActionPropertyDefinitionBody_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = propertyExpression(b, l + 1);
    r = r && whileActionPropertyDefinitionBody_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (ORDER (DESC)? nonEmptyPropertyExpressionList)?
  private boolean whileActionPropertyDefinitionBody_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "whileActionPropertyDefinitionBody_1_0_1")) return false;
    whileActionPropertyDefinitionBody_1_0_1_0(b, l + 1);
    return true;
  }

  // ORDER (DESC)? nonEmptyPropertyExpressionList
  private boolean whileActionPropertyDefinitionBody_1_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "whileActionPropertyDefinitionBody_1_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, ORDER);
    r = r && whileActionPropertyDefinitionBody_1_0_1_0_1(b, l + 1);
    r = r && nonEmptyPropertyExpressionList(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (DESC)?
  private boolean whileActionPropertyDefinitionBody_1_0_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "whileActionPropertyDefinitionBody_1_0_1_0_1")) return false;
    consumeToken(b, DESC);
    return true;
  }

  // (forAddObjClause)?
  private boolean whileActionPropertyDefinitionBody_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "whileActionPropertyDefinitionBody_3")) return false;
    whileActionPropertyDefinitionBody_3_0(b, l + 1);
    return true;
  }

  // (forAddObjClause)
  private boolean whileActionPropertyDefinitionBody_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "whileActionPropertyDefinitionBody_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = forAddObjClause(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // WINDOW simpleName (localizedStringLiteral)? windowType windowOptions SEMI
  public boolean windowCreateStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "windowCreateStatement")) return false;
    if (!nextTokenIs(b, WINDOW)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, WINDOW_CREATE_STATEMENT, null);
    r = consumeToken(b, WINDOW);
    r = r && simpleName(b, l + 1);
    p = r; // pin = 2
    r = r && windowCreateStatement_2(b, l + 1);
    r = r && windowType(b, l + 1);
    r = r && windowOptions(b, l + 1);
    r = r && consumeToken(b, SEMI);
    exit_section_(b, l, m, WINDOW_CREATE_STATEMENT, r, p, null);
    return r || p;
  }

  // (localizedStringLiteral)?
  private boolean windowCreateStatement_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "windowCreateStatement_2")) return false;
    windowCreateStatement_2_0(b, l + 1);
    return true;
  }

  // (localizedStringLiteral)
  private boolean windowCreateStatement_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "windowCreateStatement_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = localizedStringLiteral(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // HIDE WINDOW windowUsage SEMI
  public boolean windowHideStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "windowHideStatement")) return false;
    if (!nextTokenIs(b, HIDE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, WINDOW_HIDE_STATEMENT, null);
    r = consumeTokens(b, 2, HIDE, WINDOW);
    p = r; // pin = 2
    r = r && windowUsage(b, l + 1);
    r = r && consumeToken(b, SEMI);
    exit_section_(b, l, m, WINDOW_HIDE_STATEMENT, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // (	HIDETITLE
  //                     |	drawRoot
  //                     |	HIDESCROLLBARS
  //                     |	orientation
  //                     |	dockPosition
  //                     |	borderPosition
  //                     |	HALIGN LBRAC alignmentLiteral RBRAC
  //                     |	VALIGN LBRAC alignmentLiteral RBRAC
  //                     |	TEXTHALIGN LBRAC alignmentLiteral RBRAC
  //                     |	TEXTVALIGN LBRAC alignmentLiteral RBRAC
  //                     |   CLASS propertyExpression
  //                     )*
  public boolean windowOptions(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "windowOptions")) return false;
    Marker m = enter_section_(b, l, _NONE_, WINDOW_OPTIONS, "<window options>");
    while (true) {
      int c = current_position_(b);
      if (!windowOptions_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "windowOptions", c)) break;
    }
    exit_section_(b, l, m, WINDOW_OPTIONS, true, false, null);
    return true;
  }

  // HIDETITLE
  //                     |	drawRoot
  //                     |	HIDESCROLLBARS
  //                     |	orientation
  //                     |	dockPosition
  //                     |	borderPosition
  //                     |	HALIGN LBRAC alignmentLiteral RBRAC
  //                     |	VALIGN LBRAC alignmentLiteral RBRAC
  //                     |	TEXTHALIGN LBRAC alignmentLiteral RBRAC
  //                     |	TEXTVALIGN LBRAC alignmentLiteral RBRAC
  //                     |   CLASS propertyExpression
  private boolean windowOptions_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "windowOptions_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, HIDETITLE);
    if (!r) r = drawRoot(b, l + 1);
    if (!r) r = consumeToken(b, HIDESCROLLBARS);
    if (!r) r = orientation(b, l + 1);
    if (!r) r = dockPosition(b, l + 1);
    if (!r) r = borderPosition(b, l + 1);
    if (!r) r = windowOptions_0_6(b, l + 1);
    if (!r) r = windowOptions_0_7(b, l + 1);
    if (!r) r = windowOptions_0_8(b, l + 1);
    if (!r) r = windowOptions_0_9(b, l + 1);
    if (!r) r = windowOptions_0_10(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // HALIGN LBRAC alignmentLiteral RBRAC
  private boolean windowOptions_0_6(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "windowOptions_0_6")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeTokens(b, 0, HALIGN, LBRAC);
    r = r && alignmentLiteral(b, l + 1);
    r = r && consumeToken(b, RBRAC);
    exit_section_(b, m, null, r);
    return r;
  }

  // VALIGN LBRAC alignmentLiteral RBRAC
  private boolean windowOptions_0_7(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "windowOptions_0_7")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeTokens(b, 0, VALIGN, LBRAC);
    r = r && alignmentLiteral(b, l + 1);
    r = r && consumeToken(b, RBRAC);
    exit_section_(b, m, null, r);
    return r;
  }

  // TEXTHALIGN LBRAC alignmentLiteral RBRAC
  private boolean windowOptions_0_8(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "windowOptions_0_8")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeTokens(b, 0, TEXTHALIGN, LBRAC);
    r = r && alignmentLiteral(b, l + 1);
    r = r && consumeToken(b, RBRAC);
    exit_section_(b, m, null, r);
    return r;
  }

  // TEXTVALIGN LBRAC alignmentLiteral RBRAC
  private boolean windowOptions_0_9(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "windowOptions_0_9")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeTokens(b, 0, TEXTVALIGN, LBRAC);
    r = r && alignmentLiteral(b, l + 1);
    r = r && consumeToken(b, RBRAC);
    exit_section_(b, m, null, r);
    return r;
  }

  // CLASS propertyExpression
  private boolean windowOptions_0_10(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "windowOptions_0_10")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, CLASS);
    r = r && propertyExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // windowCreateStatement | windowHideStatement
  public boolean windowStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "windowStatement")) return false;
    if (!nextTokenIs(b, "<window statement>", HIDE, WINDOW)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, WINDOW_STATEMENT, "<window statement>");
    r = windowCreateStatement(b, l + 1);
    if (!r) r = windowHideStatement(b, l + 1);
    exit_section_(b, l, m, WINDOW_STATEMENT, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // MENU | PANEL | TOOLBAR | TREE | NATIVE
  public boolean windowType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "windowType")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, WINDOW_TYPE, "<window type>");
    r = consumeToken(b, MENU);
    if (!r) r = consumeToken(b, PANEL);
    if (!r) r = consumeToken(b, TOOLBAR);
    if (!r) r = consumeToken(b, TREE);
    if (!r) r = consumeToken(b, NATIVE);
    exit_section_(b, l, m, WINDOW_TYPE, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // FLOAT | DOCKED | EMBEDDED | POPUP | (IN componentID)
  public boolean windowTypeLiteral(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "windowTypeLiteral")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, WINDOW_TYPE_LITERAL, "<window type literal>");
    r = consumeToken(b, FLOAT);
    if (!r) r = consumeToken(b, DOCKED);
    if (!r) r = consumeToken(b, EMBEDDED);
    if (!r) r = consumeToken(b, POPUP);
    if (!r) r = windowTypeLiteral_4(b, l + 1);
    exit_section_(b, l, m, WINDOW_TYPE_LITERAL, r, false, null);
    return r;
  }

  // IN componentID
  private boolean windowTypeLiteral_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "windowTypeLiteral_4")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, IN);
    r = r && componentID(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // compoundID
  public boolean windowUsage(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "windowUsage")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, WINDOW_USAGE, "<window usage>");
    r = compoundID(b, l + 1);
    exit_section_(b, l, m, WINDOW_USAGE, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // WRITE (CLIENT DIALOG?)? propertyExpression TO propertyExpression APPEND?
  public boolean writeActionPropertyDefinitionBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "writeActionPropertyDefinitionBody")) return false;
    if (!nextTokenIs(b, WRITE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, WRITE_ACTION_PROPERTY_DEFINITION_BODY, null);
    r = consumeToken(b, WRITE);
    p = r; // pin = 1
    r = r && writeActionPropertyDefinitionBody_1(b, l + 1);
    r = r && propertyExpression(b, l + 1);
    r = r && consumeToken(b, TO);
    r = r && propertyExpression(b, l + 1);
    r = r && writeActionPropertyDefinitionBody_5(b, l + 1);
    exit_section_(b, l, m, WRITE_ACTION_PROPERTY_DEFINITION_BODY, r, p, null);
    return r || p;
  }

  // (CLIENT DIALOG?)?
  private boolean writeActionPropertyDefinitionBody_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "writeActionPropertyDefinitionBody_1")) return false;
    writeActionPropertyDefinitionBody_1_0(b, l + 1);
    return true;
  }

  // CLIENT DIALOG?
  private boolean writeActionPropertyDefinitionBody_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "writeActionPropertyDefinitionBody_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, CLIENT);
    r = r && writeActionPropertyDefinitionBody_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // DIALOG?
  private boolean writeActionPropertyDefinitionBody_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "writeActionPropertyDefinitionBody_1_0_1")) return false;
    consumeToken(b, DIALOG);
    return true;
  }

  // APPEND?
  private boolean writeActionPropertyDefinitionBody_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "writeActionPropertyDefinitionBody_5")) return false;
    consumeToken(b, APPEND);
    return true;
  }

  /* ********************************************************** */
  // mappedPropertyClassParamDeclare ARROW propertyExpression WHEN (DO)? propertyExpression SEMI
  public boolean writeWhenStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "writeWhenStatement")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, WRITE_WHEN_STATEMENT, "<write when statement>");
    r = mappedPropertyClassParamDeclare(b, l + 1);
    r = r && consumeToken(b, ARROW);
    p = r; // pin = 2
    r = r && propertyExpression(b, l + 1);
    r = r && consumeToken(b, WHEN);
    r = r && writeWhenStatement_4(b, l + 1);
    r = r && propertyExpression(b, l + 1);
    r = r && consumeToken(b, SEMI);
    exit_section_(b, l, m, WRITE_WHEN_STATEMENT, r, p, null);
    return r || p;
  }

  // (DO)?
  private boolean writeWhenStatement_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "writeWhenStatement_4")) return false;
    consumeToken(b, DO);
    return true;
  }

  /* ********************************************************** */
  // andPE (XOR andPE)*
  public boolean xorPE(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "xorPE")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, XOR_PE, "<xor pe>");
    r = andPE(b, l + 1);
    r = r && xorPE_1(b, l + 1);
    exit_section_(b, l, m, XOR_PE, r, false, null);
    return r;
  }

  // (XOR andPE)*
  private boolean xorPE_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "xorPE_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!xorPE_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "xorPE_1", c)) break;
    }
    return true;
  }

  // XOR andPE
  private boolean xorPE_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "xorPE_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, null);
    r = consumeToken(b, XOR);
    r = r && andPE(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

}
