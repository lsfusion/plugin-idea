package com.simpleplugin.util;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.simpleplugin.BaseUtils;
import com.simpleplugin.LSFPsiImplUtil;
import com.simpleplugin.classes.CustomClassSet;
import com.simpleplugin.classes.LSFClassSet;
import com.simpleplugin.classes.LSFValueClass;
import com.simpleplugin.psi.LSFFile;
import com.simpleplugin.psi.LSFImplicitValuePropertyStatement;
import com.simpleplugin.psi.LSFPropertyStatement;
import com.simpleplugin.psi.context.ContextModifier;
import com.simpleplugin.psi.context.ExtendParamContext;
import com.simpleplugin.psi.context.LSFExpression;
import com.simpleplugin.psi.context.ModifyParamContext;
import com.simpleplugin.psi.declarations.*;
import com.simpleplugin.psi.extend.LSFFormExtend;
import com.simpleplugin.psi.references.impl.LSFFormElementReferenceImpl;
import com.simpleplugin.psi.stubs.interfaces.types.indexes.ExplicitInterfaceIndex;
import com.simpleplugin.psi.stubs.interfaces.types.indexes.ExplicitValueIndex;
import com.simpleplugin.psi.stubs.interfaces.types.indexes.ImplicitInterfaceIndex;
import com.simpleplugin.psi.stubs.interfaces.types.indexes.ImplicitValueIndex;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class LSFPsiUtils {

    public static List<LSFExpression> collectExpressions(final PsiFile file, final Editor editor, int offset) {
        Document document = editor.getDocument();
        int textLength = document.getTextLength();
        if (textLength == 0) {
            return Collections.emptyList();
        }

        if (offset >= textLength) {
            offset = textLength - 1;
        } else if (offset < 0) {
            offset = 0;
        }

        CharSequence documentText = document.getCharsSequence();

        if (!isLsfIdentifierPart(documentText.charAt(offset)) && offset > 0 && isLsfIdentifierPart(documentText.charAt(offset - 1))) {
            offset--;
        }

        final PsiElement elementAtCaret = file.findElementAt(offset);
        final List<LSFExpression> expressions = new ArrayList<LSFExpression>();
        final List<TextRange> ranges = new ArrayList<TextRange>();

        LSFExpression expression = PsiTreeUtil.getParentOfType(elementAtCaret, LSFExpression.class);
        while (expression != null) {
            //пропускаем скобочные выражения
            TextRange range = expression.getTextRange();
            if (!ranges.contains(range)) {
                expressions.add(expression);
                ranges.add(range);
            }
            expression = PsiTreeUtil.getParentOfType(expression, LSFExpression.class);
        }
        return expressions;
    }

    public static boolean isLsfIdentifierPart(char ch) {
        return Character.isJavaIdentifierPart(ch);
    }

    public static String getPropertyStatementPresentableText(LSFPropertyStatement property) {
        String text = property.getPresentableText();

        String caption = property.getCaption();
        if (caption != null) {
            text += " " + caption;
        }

        if (!property.isAction()) {
            LSFClassSet valueClass = property.resolveValueClass(false);
            text += ": " + (valueClass == null ? "?" : valueClass);
        }

        return text;
    }

    @NotNull
    public static Set<LSFExprParamDeclaration> getContextParams(@NotNull PsiElement current, boolean objectRef) {
        return getContextParams(current, current.getTextOffset(), objectRef);
    }

    @NotNull
    public static Set<LSFExprParamDeclaration> getContextParams(PsiElement current, int offset, boolean objectRef) {
        if (current instanceof ModifyParamContext) {
            ContextModifier contextModifier = ((ModifyParamContext) current).getContextModifier();

            Set<LSFExprParamDeclaration> upParams;
            Set<LSFExprParamDeclaration> result = new HashSet<LSFExprParamDeclaration>();
            if (current instanceof ExtendParamContext) {
                upParams = getContextParams(current.getParent(), offset, objectRef);
                result.addAll(upParams);
            } else { // не extend - останавливаемся
                upParams = new HashSet<LSFExprParamDeclaration>();
            }
            result.addAll(contextModifier.resolveParams(offset, upParams));
            return result;
        } else {
            // current instanceof FormContext || current instancof LSFFormStatement
            Set<LSFObjectDeclaration> objects = LSFFormElementReferenceImpl.processFormContext(current, new LSFFormElementReferenceImpl.FormExtendProcessor<LSFObjectDeclaration>() {
                public Collection<LSFObjectDeclaration> process(LSFFormExtend formExtend) {
                    return formExtend.getObjectDecls();
                }
            }, objectRef);
            if (objects != null) {
                return BaseUtils.<LSFExprParamDeclaration, LSFObjectDeclaration>immutableCast(objects);
            }
        }

        PsiElement parent = current.getParent();
        if (!(parent == null || parent instanceof LSFFile)) {
            return getContextParams(parent, offset, objectRef); // бежим выше
        }

        return new HashSet<LSFExprParamDeclaration>();
    }

    @NotNull
    public static List<LSFClassSet> getContextClasses(PsiElement psiElement, boolean objectRef) {
        return LSFPsiImplUtil.resolveParamDeclClasses(getContextParams(psiElement, objectRef));
    }

    @NotNull
    public static List<LSFClassSet> getContextClasses(PsiElement psiElement, int offset, boolean objectRef) {
        return LSFPsiImplUtil.resolveParamDeclClasses(getContextParams(psiElement, offset, objectRef));
    }

    public abstract static class ResultHandler<T> {
        public static ResultHandler DEFAULT_INSTANCE = new ResultHandler<LSFPropertyStatement>() {
            @Override
            public LSFPropertyStatement getResult(LSFPropertyStatement statement, LSFValueClass valueClass) {
                return statement;
            }
        };

        public abstract <T> T getResult(LSFPropertyStatement statement, LSFValueClass valueClass);
    }

    public static Set<LSFPropertyStatement> getClassInterfaces(LSFValueClass valueClass, Project project, GlobalSearchScope scope) {
        return getClassInterfaces(valueClass, project, scope, ResultHandler.DEFAULT_INSTANCE);
    }

    public static <T> Set<T> getClassInterfaces(LSFValueClass valueClass, Project project, GlobalSearchScope scope, ResultHandler<T> resultHandler) {
        Set<LSFPropertyStatement> resultStatements = new HashSet<LSFPropertyStatement>();

        List<LSFPropertyStatement> valueClassStatements = new ArrayList<LSFPropertyStatement>();
        for (LSFValueClass vc : CustomClassSet.getClassParentsRecursively(valueClass)) {
            String parentClassName = vc.getName();
            Collection<LSFExplicitInterfacePropStatement> eiStatements = ExplicitInterfaceIndex.getInstance().get(parentClassName, project, scope);
            for (LSFExplicitInterfacePropStatement statement : eiStatements) {
                resultStatements.add(statement.getPropertyStatement());
            }

            Collection<LSFExplicitValuePropStatement> evStatements = ExplicitValueIndex.getInstance().get(parentClassName, project, scope);
            for (LSFExplicitValuePropStatement evStatement : evStatements) {
                valueClassStatements.add(evStatement.getPropertyStatement());
            }
        }

        Set<String> valueClassStatementNames = new HashSet<String>();
        int i = 0;
        Set<LSFPropertyStatement> notFittingStatemens = new HashSet<LSFPropertyStatement>();
        while (i < valueClassStatements.size()) {
            LSFPropertyStatement vcStatement = valueClassStatements.get(i);
            String evsName = vcStatement.getName();
            if (!valueClassStatementNames.contains(evsName) && !notFittingStatemens.contains(vcStatement)) {
                LSFClassSet resolvedClass = vcStatement.resolveValueClass(false);
                LSFClassSet valueClassSet = valueClass instanceof LSFClassDeclaration ? new CustomClassSet((LSFClassDeclaration) valueClass) : (LSFClassSet) valueClass;
                if (resolvedClass != null && resolvedClass.haveCommonChilds(valueClassSet, scope)) {
                    valueClassStatementNames.add(evsName);

                    Collection<LSFImplicitValuePropertyStatement> ivStatements = ImplicitValueIndex.getInstance().get(evsName, project, scope);
                    for (LSFImplicitValuePropertyStatement ivpStatement : ivStatements) {
                        valueClassStatements.add(ivpStatement.getPropertyStatement());
                    }
                } else {
                    notFittingStatemens.add(vcStatement);
                }
            }
            i++;
        }

        for (String statementName : valueClassStatementNames) {
            Collection<LSFImplicitInterfacePropStatement> iiStataments = ImplicitInterfaceIndex.getInstance().get(statementName, project, scope);
            for (LSFImplicitInterfacePropStatement iiStatement : iiStataments) {
                resultStatements.add(iiStatement.getPropertyStatement());
            }
        }

        return filterClassInterfaces(resultStatements, valueClass, resultHandler);
    }

    public static <T> Set<T> filterClassInterfaces(Collection<LSFPropertyStatement> statements, LSFValueClass valueClass) {
        return filterClassInterfaces(statements, valueClass, ResultHandler.DEFAULT_INSTANCE);
    }

    public static <T> Set<T> filterClassInterfaces(Collection<LSFPropertyStatement> statements, LSFValueClass valueClass, ResultHandler<T> resultHandler) {
        Set<T> result = new HashSet<T>();
        for (LSFPropertyStatement statement : statements) {
            List<LSFClassSet> paramClasses = statement.resolveParamClasses();
            if (paramClasses != null && !paramClasses.isEmpty()) {
                for (LSFClassSet paramClass : paramClasses) {
                    if (paramClass != null) {
                        LSFClassSet valueClassSet = valueClass instanceof LSFClassDeclaration ? new CustomClassSet((LSFClassDeclaration) valueClass) : (LSFClassSet) valueClass;
                        if (paramClass.containsAll(valueClassSet)) {
                            result.add((T) resultHandler.getResult(statement, valueClass));
                            break;
                        }
                    }
                }
            }
        }
        return result;
    }
}
