package com.lsfusion.mcp;

import com.intellij.openapi.application.ReadAction;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.declarations.*;
import com.lsfusion.lang.psi.extend.LSFClassExtend;
import com.lsfusion.lang.psi.extend.LSFExtend;
import com.lsfusion.lang.psi.extend.LSFFormExtend;
import com.lsfusion.lang.psi.stubs.types.GlobalDeclStubElementType;
import com.lsfusion.lang.psi.stubs.types.LSFStubElementTypes;
import com.lsfusion.util.BaseUtils;

import java.util.Collection;

/**
 * Marker PSI interface for elements that can be returned by MCP search.
 */
public interface LSFMCPDeclaration extends LSFMCPStatement {

    static Collection<LSFMCPDeclaration> getMCPDeclarations(LSFFile file) {
        return getMCPDeclarationsInFileOrder(file);
    }

    static java.util.List<LSFMCPDeclaration> getMCPDeclarationsInFileOrder(LSFFile file) {
        return ReadAction.compute(() -> {
            if (file == null) return java.util.Collections.emptyList();

            // Use ordered PSI traversal instead of TextRange sorting.
            // IMPORTANT: we must include ALL descendants of LSFMCPDeclaration, including nested ones.
            // (LSFPsiUtils.findChildrenOfType() stops descending into a subtree once it matches the class.)
            java.util.Collection<LSFMCPDeclaration> children = PsiTreeUtil.findChildrenOfType(file, LSFMCPDeclaration.class);
            if (children.isEmpty()) return java.util.Collections.emptyList();

            java.util.ArrayList<LSFMCPDeclaration> out = new java.util.ArrayList<>(children.size());
            for (LSFMCPDeclaration stmt : children) {
                if (getMetaCode(stmt) != null) continue;
                if (stmt instanceof LSFExtend<?, ?> ext && ext.getExtendingReference() != null) continue;
                out.add(stmt);
            }
            return out;
        });
    }

    static LSFMCPDeclaration getMCPDeclaration(PsiElement element) {
        return ReadAction.compute(() -> {
            // For all other cases return the topmost MCP statement (non-strict), not the nearest one.
            LSFMCPDeclaration mcpElement = PsiTreeUtil.getParentOfType(element, LSFMCPDeclaration.class, false);

            // Prefer returning the enclosing meta declaration statement instead of inner statements.
            LSFMCPDeclaration metaDecl = getMetaCode(mcpElement);
            if (metaDecl != null) return metaDecl;

            return mcpElement;
        });
    }

    // private
    static LSFMetaDeclaration getMetaCode(LSFMCPDeclaration element) { // strict
        return PsiTreeUtil.getParentOfType(element, LSFMetaDeclaration.class);
    }

    // next uses / used, matches names / classes, result
    static Collection<LSFGlobalDeclaration<?, ?>> getNameDeclarations(LSFMCPDeclaration stmt) {
        return BaseUtils.immutableCast(ReadAction.compute(() ->
                PsiTreeUtil.findChildrenOfAnyType(stmt, false, LSFGlobalDeclaration.class)
                        .stream()
                        .filter(decl ->
                                decl.getDeclName() != null &&
                                !(decl instanceof LSFAggrParamGlobalPropDeclaration && ((LSFAggrParamGlobalPropDeclaration) decl).getAggrPropertyDefinition() == null))
                        .toList()));
    }

    default ElementType getMCPType() {
        if (this instanceof LSFModuleDeclaration) return ElementType.MODULE;

        if (this instanceof LSFStatementGlobalPropDeclaration) return ElementType.PROPERTY;
        if (this instanceof LSFStatementActionDeclaration) return ElementType.ACTION;
        if (this instanceof LSFGroupDeclaration) return ElementType.GROUP;
        if (this instanceof LSFClassExtend) return ElementType.CLASS;
        if (this instanceof LSFFormExtend) return ElementType.FORM;
        if (this instanceof LSFNavigatorStatement) return ElementType.NAVIGATOR_ELEMENT;

        if (this instanceof LSFMetaDeclaration) return ElementType.META;
        if (this instanceof LSFWindowStatement) return ElementType.WINDOW;
        if (this instanceof LSFTableDeclaration) return ElementType.TABLE;
        if (this instanceof LSFIndexStatement) return ElementType.INDEX;

        if (this instanceof LSFEventStatement || this instanceof LSFGlobalEventStatement) return ElementType.EVENT;
        if (this instanceof LSFWriteWhenStatement) return ElementType.CALCULATED_EVENT;
        if (this instanceof LSFConstraintStatement || this instanceof LSFFollowsStatement) return ElementType.CONSTRAINT;

        throw new UnsupportedOperationException();
    }

    int EVENT_EXPRESSION_PREFIX_LEN = 120;
    int EVENT_ACTION_PREFIX_LEN = 120;
    int FORM_PROPERTIES_PREFIX_LEN = 120;
    int CONSTRAINT_EXPRESSION_PREFIX_LEN = 50;

    enum ElementType {
        MODULE("module", LSFStubElementTypes.MODULE, LSFPriorityList.class, LSFNamespaceName.class),

        // no need in property expressions, or actions because there are names / captions / signatures
        CLASS("class", LSFStubElementTypes.CLASS),
        PROPERTY("property", LSFStubElementTypes.STATEMENTPROP, LSFPropertyExpression.class, LSFNonEmptyPropertyOptions.class),
        ACTION("action", LSFStubElementTypes.STATEMENTACTION, LSFListActionPropertyDefinitionBody.class, LSFNonEmptyActionOptions.class),
        GROUP("group", LSFStubElementTypes.GROUP),
        FORM("form", LSFStubElementTypes.FORM, LSFComponentBody.class,
                LSFFormObjectOptions.class, LSFFormGroupObjectOptions.class, LSFFormPropertyOptionsList.class, LSFFormDeclOptions.class, LSFFormOptions.class,
                LSFFormPropertiesList.class, FORM_PROPERTIES_PREFIX_LEN,
                LSFFormFiltersList.class, LSFFormFilterGroupDeclaration.class, LSFFormExtendFilterGroupDeclaration.class, LSFUserFiltersDeclaration.class,
                LSFFormOrderByList.class, LSFFormPivotOptionsDeclaration.class, LSFFormEventsList.class,
                LSFPropertyExpression.class, LSFListActionPropertyDefinitionBody.class),
        NAVIGATOR_ELEMENT("navigatorElement", LSFStubElementTypes.NAVIGATORELEMENT, LSFNavigatorElementOptions.class, LSFNavigatorSchedulerStatement.class),

        // maybe not needed in the short code at all
        META("metacode", LSFStubElementTypes.META, LSFMetaCodeDeclBody.class),
        WINDOW("window", LSFStubElementTypes.WINDOW, LSFWindowOptions.class),
        TABLE("table", LSFStubElementTypes.TABLE),
        INDEX("index", null),

        // here we need it "short" expressions and "action" because it's the essense of the events
        EVENT("event", null, LSFPropertyExpression.class, LSFMCPDeclaration.EVENT_EXPRESSION_PREFIX_LEN, LSFActionPropertyDefinitionBody.class, LSFMCPDeclaration.EVENT_ACTION_PREFIX_LEN, LSFBaseEventPE.class, LSFBaseEventNotPE.class, LSFEventOrder.class),
        CALCULATED_EVENT("calculatedEvent", null, LSFPropertyExpression.class, LSFMCPDeclaration.EVENT_EXPRESSION_PREFIX_LEN),
        // here we have a message that pretty good describes the constraint
        CONSTRAINT("constraint", null, LSFConstraintPropertyExpression.class, LSFMCPDeclaration.CONSTRAINT_EXPRESSION_PREFIX_LEN, LSFBaseEventPE.class, LSFConstraintCheckedBy.class, LSFConstraintProperties.class);

        public final String apiName;

        public final GlobalDeclStubElementType<?, ?> stubType;

        /**
         * Rules for producing a "short" code snippet.
         */
        public final LSFPsiImplUtil.TextCutRules shortCodeRules;

        ElementType(String apiName,
                    GlobalDeclStubElementType<?, ?> stubType,
                    Object... shortCodeCutRules) {
            this.apiName = apiName;
            this.stubType = stubType;
            this.shortCodeRules = new LSFPsiImplUtil.TextCutRules(parseCutRules(shortCodeCutRules));
        }

        private static LSFPsiImplUtil.TextCutRules.CutRule[] parseCutRules(Object[] specs) {
            if (specs == null || specs.length == 0) {
                return null;
            }

            java.util.ArrayList<LSFPsiImplUtil.TextCutRules.CutRule> out = new java.util.ArrayList<>();
            int i = 0;
            while (i < specs.length) {
                Object token = specs[i];
                if (!(token instanceof Class<?> clazz) || !PsiElement.class.isAssignableFrom(clazz)) {
                    throw new IllegalArgumentException("Expected PSI rule class at index " + i + ", got: " + (token == null ? "null" : token.getClass().getName()));
                }

                @SuppressWarnings("unchecked")
                Class<? extends PsiElement> ruleClass = (Class<? extends PsiElement>) clazz;
                int keepPrefix = 0;

                if (i + 1 < specs.length && specs[i + 1] instanceof Number n) {
                    keepPrefix = n.intValue();
                    i += 2;
                } else {
                    i += 1;
                }

                out.add(new LSFPsiImplUtil.TextCutRules.CutRule(ruleClass, keepPrefix));
            }

            return out.isEmpty() ? null : out.toArray(new LSFPsiImplUtil.TextCutRules.CutRule[0]);
        }

        public static ElementType fromString(String s) {
            if (s == null) return null;
            for (ElementType t : values()) {
                if (t.apiName.equalsIgnoreCase(s)) return t;
            }
            return null;
        }
    }
}
