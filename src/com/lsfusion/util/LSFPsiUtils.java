package com.lsfusion.util;

import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.Segment;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.injected.InjectedLanguageManagerImpl;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiElementProcessor;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.ArrayListSet;
import com.intellij.util.containers.ContainerUtil;
import com.lsfusion.lang.classes.CustomClassSet;
import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.classes.LSFValueClass;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.context.ContextModifier;
import com.lsfusion.lang.psi.context.ExtendParamContext;
import com.lsfusion.lang.psi.context.LSFExpression;
import com.lsfusion.lang.psi.context.ModifyParamContext;
import com.lsfusion.lang.psi.declarations.*;
import com.lsfusion.lang.psi.extend.LSFFormExtend;
import com.lsfusion.lang.psi.indexes.ExplicitInterfaceIndex;
import com.lsfusion.lang.psi.indexes.ExplicitValueIndex;
import com.lsfusion.lang.psi.indexes.ImplicitInterfaceIndex;
import com.lsfusion.lang.psi.indexes.ImplicitValueIndex;
import com.lsfusion.lang.psi.references.impl.LSFFormElementReferenceImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class LSFPsiUtils {

    @Nullable
    public static PsiElement getStatementParent(PsiElement element) {
        if (element == null) {
            return null;
        }

        while (element.getParent() != null) {
            //parent == scriptStatement OR metaCodeBody
            if (element.getParent() instanceof LSFFile
                    || element.getParent() instanceof LSFScriptStatement
                    || element.getParent() instanceof LSFMetaCodeBody) {
                break;
            }
            element = element.getParent();
        }

        return element.getParent() == null ? null : element;
    }

    public static TextRange subRange(TextRange range, TextRange inner) {
        int start = range.getStartOffset() + inner.getStartOffset();
        int end = start + inner.getLength();
        if (end > range.getEndOffset()) {
            throw new IllegalArgumentException("Incorrect inner range.");
        }
        return new TextRange(start, end);
    }

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

        final List<TextRange> ranges = new ArrayList<TextRange>();
        final List<LSFExpression> expressions = new ArrayList<LSFExpression>();

        fillLsfExpressions(file, offset, ranges, expressions);
        if (!isLsfIdentifierPart(documentText.charAt(offset)) && offset > 0) {
            fillLsfExpressions(file, offset - 1, ranges, expressions);
        }

        return expressions;
    }

    private static void fillLsfExpressions(PsiFile file, int offset, List<TextRange> ranges, List<LSFExpression> expressions) {
        final PsiElement elementAtCaret = file.findElementAt(offset);

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
            LSFClassSet valueClass = property.resolveValueClass();
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
                int upOffset = offset;
                PsiElement context = current.getContext();
                if (context != null && current instanceof LSFCodeFragment) {
                    upOffset = context.getTextOffset();
                    context = PsiTreeUtil.getParentOfType(context, LSFActionPropertyDefinitionBody.class);
                    if (context != null) {
                        context = context.getContext();
                    }
                }                
                if(context == null)
                    upParams = new HashSet<LSFExprParamDeclaration>();
                else
                    upParams = getContextParams(context, upOffset, objectRef);
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
        if (parent != null) {
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

    public static Set<LSFFile> collectInjectedLSFFiles(VirtualFile file, Project project) {
        PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
        if (psiFile == null) {
            return Collections.EMPTY_SET;
        }

        return collectInjectedLSFFiles(psiFile, project);
    }

    public static Set<LSFFile> collectInjectedLSFFiles(PsiElement root, Project project) {
        Set<LSFFile> files = new ArrayListSet<LSFFile>();
        collectInjectedLSFFiles(root, project, files);
        return files;
    }

    public static void collectInjectedLSFFiles(PsiElement root, Project project, Set<LSFFile> lsfFiles) {
        List<Pair<PsiElement, TextRange>> injectedPsiFiles = InjectedLanguageManagerImpl.getInstance(project).getInjectedPsiFiles(root);
        if (injectedPsiFiles != null) {
            for (Pair<PsiElement, TextRange> injectedPsiFile : injectedPsiFiles) {
                PsiElement file = injectedPsiFile.first;
                if (file instanceof LSFFile) {
                    lsfFiles.add((LSFFile) file);
                }
            }
        }
        for (PsiElement child : root.getChildren()) {
            collectInjectedLSFFiles(child, project, lsfFiles);
        }
    }

    public abstract static class ApplicableMapper<T> {
        public static ApplicableMapper STATEMENT = new ApplicableMapper<LSFInterfacePropStatement>() {
            @Override
            public LSFInterfacePropStatement map(LSFInterfacePropStatement statement, LSFValueClass valueClass) {
                return statement;
            }
        };

        public abstract <T> T map(LSFInterfacePropStatement statement, LSFValueClass valueClass);
    }

    public static Set<LSFInterfacePropStatement> getPropertiesApplicableToClass(LSFValueClass valueClass, Project project, GlobalSearchScope scope, boolean isLight, boolean isHeavy) {
        return mapPropertiesApplicableToClass(valueClass, project, scope, ApplicableMapper.STATEMENT, isLight, isHeavy);
    }

    public static <T> Set<T> mapPropertiesApplicableToClass(LSFValueClass valueClass, Project project, GlobalSearchScope scope, ApplicableMapper<T> applicableMapper, boolean isLight, boolean isHeavy) {
        assert isLight || isHeavy;

        LSFClassSet valueClassSet = valueClass instanceof LSFClassDeclaration ? new CustomClassSet((LSFClassDeclaration) valueClass) : (LSFClassSet) valueClass;

        Set<LSFInterfacePropStatement> resultStatements = new HashSet<LSFInterfacePropStatement>();

        List<LSFPropertyStatement> statementsWithClassAsResult = new ArrayList<LSFPropertyStatement>();
        for (LSFValueClass clazz : CustomClassSet.getClassParentsRecursively(valueClass)) {
            String className = clazz.getName();
            Collection<LSFExplicitInterfacePropStatement> eiStatements = ExplicitInterfaceIndex.getInstance().get(className, project, scope);
            for (LSFExplicitInterfacePropStatement statement : eiStatements) {
                boolean explicit = statement.getExplicitParams() instanceof LSFExplicitSignature;
                if(isLight) {
                    if(isHeavy)
                        resultStatements.add(statement.getPropertyStatement());
                    else
                        if(explicit)
                            resultStatements.add(statement);
                } else {
                    assert isHeavy;
                    if(!explicit)
                        resultStatements.add(statement.getPropertyStatement());
                }
            }

            if(isHeavy) {
                Collection<LSFExplicitValuePropStatement> evStatements = ExplicitValueIndex.getInstance().get(className, project, scope);
                for (LSFExplicitValuePropStatement evStatement : evStatements) {
                    statementsWithClassAsResult.add(evStatement.getPropertyStatement());
                }
            }
        }

        if(isLight && !isHeavy && resultStatements.size() < 50) {
            Set<LSFInterfacePropStatement> transResultStatements = new HashSet<>();
            for(LSFInterfacePropStatement prop : resultStatements)
                transResultStatements.add(((LSFExplicitInterfacePropStatement)prop).getPropertyStatement());
            resultStatements = transResultStatements;
        }

        if(isHeavy) {
            Set<String> namesOfStatementsWithClassAsResult = new HashSet<String>();
            int i = 0;
            Set<LSFPropertyStatement> skipStatemens = new HashSet<LSFPropertyStatement>();
            while (i < statementsWithClassAsResult.size()) {
                LSFPropertyStatement statement = statementsWithClassAsResult.get(i);
                String statementName = statement.getName();

                if (!namesOfStatementsWithClassAsResult.contains(statementName) && !skipStatemens.contains(statement)) {
                    LSFClassSet resolvedClass = statement.resolveValueClass();
                    if (resolvedClass != null && resolvedClass.haveCommonChildren(valueClassSet, scope)) {
                        namesOfStatementsWithClassAsResult.add(statementName);

                        Collection<LSFImplicitValuePropStatement> ivStatements = ImplicitValueIndex.getInstance().get(statementName, project, scope);
                        for (LSFImplicitValuePropStatement ivpStatement : ivStatements) {
                            statementsWithClassAsResult.add(ivpStatement.getPropertyStatement());
                        }
                    } else {
                        skipStatemens.add(statement);
                    }
                }
                i++;
            }

            for (String name : namesOfStatementsWithClassAsResult) {
                Collection<LSFImplicitInterfacePropStatement> iiStataments = ImplicitInterfaceIndex.getInstance().get(name, project, scope);
                for (LSFImplicitInterfacePropStatement iiStatement : iiStataments) {
                    resultStatements.add(iiStatement.getPropertyStatement());
                }
            }
        }

        return mapPropertiesApplicableToClass(valueClassSet, resultStatements, applicableMapper);
    }

    private static <T> Set<T> mapPropertiesApplicableToClass(LSFClassSet valueClassSet, Collection<LSFInterfacePropStatement> statements, ApplicableMapper<T> applicableMapper) {

        Set<T> result = new HashSet<T>();
        for (LSFInterfacePropStatement statement : statements) {
            List<LSFClassSet> paramClasses = statement.resolveParamClasses();
            if (paramClasses != null && !paramClasses.isEmpty()) {
                for (LSFClassSet paramClass : paramClasses) {
                    if (paramClass != null) {
                        if (paramClass.containsAll(valueClassSet, true)) {
                            result.add((T) applicableMapper.map(statement, paramClass.getCommonClass()));
                            break;
                        }
                    }
                }
            }
        }
        return result;
    }

    public static Collection<PsiElement> findChildrenOfType(final PsiElement element, final Class<? extends PsiElement>... classes) {
        if (element == null) {
            return ContainerUtil.emptyList();
        }

        PsiElementProcessor.CollectElements<PsiElement> processor = new PsiElementProcessor.CollectElements<PsiElement>() {
            @Override
            public boolean execute(@NotNull PsiElement each) {
                if (each == element) return true;
                if (PsiTreeUtil.instanceOf(each, classes)) {
                    super.execute(each);
                    return false;
                }
                return true;
            }
        };
        PsiTreeUtil.processElements(element, processor);
        return processor.getCollection();
    }

    public static <T extends PsiElement> T getLastChildOfType(@Nullable PsiElement element, @NotNull Class<T> aClass) {
        if (element == null) return null;
        for (PsiElement child = element.getLastChild(); child != null; child = child.getPrevSibling()) {
            if (aClass.isInstance(child)) {
                //noinspection unchecked
                return (T) child;
            }
        }
        return null;
    }

    public static <T extends PsiElement> T getLastChildOfAnyType(@Nullable PsiElement element, @NotNull Class<? extends T>... classes) {
        if (element == null) return null;
        for (PsiElement child = element.getLastChild(); child != null; child = child.getPrevSibling()) {
            for (Class<? extends T> aClass : classes) {
                if (aClass.isInstance(child)) {
                    //noinspection unchecked
                    return (T) child;
                }
            }
        }
        return null;
    }

    public static boolean isInjected(PsiElement element) {
        PsiFile injectedFile = element.getContainingFile();
        if (injectedFile == null) return false;
        Project project = injectedFile.getProject();
        InjectedLanguageManager languageManager = InjectedLanguageManager.getInstance(project);
        return languageManager.isInjectedFragment(injectedFile);
    }

    public static String getLocationString(PsiElement element) {
        final PsiFile file = element.getContainingFile();
        final Document document = PsiDocumentManager.getInstance(element.getProject()).getDocument(file);
        final SmartPsiElementPointer pointer = SmartPointerManager.getInstance(element.getProject()).createSmartPsiElementPointer(element);
        final Segment range = pointer.getRange();
        int lineNumber = -1;
        int linePosition = -1;
        if (document != null && range != null) {
            lineNumber = document.getLineNumber(range.getStartOffset()) + 1;
            linePosition = range.getStartOffset() - document.getLineStartOffset(lineNumber - 1) + 1;
        }

        return file.getName() + "(" + lineNumber + ":" + linePosition + ")";
    }

}
