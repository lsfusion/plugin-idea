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
import com.lsfusion.lang.psi.extend.impl.LSFFormExtendImpl;
import com.lsfusion.lang.psi.indexes.*;
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
                    || element.getParent() instanceof LSFLazyMetaStatement
                    || element.getParent() instanceof LSFLazyMetaDeclStatement
                    || element.getParent() instanceof LSFMetaCodeDeclBody
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

        final List<TextRange> ranges = new ArrayList<>();
        final List<LSFExpression> expressions = new ArrayList<>();

        if (!isLsfIdentifierPart(documentText.charAt(offset)) && offset > 0) {
            fillLsfExpressions(file, offset - 1, ranges, expressions);
        }
        fillLsfExpressions(file, offset, ranges, expressions);

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
        // [a-zA-Z_0-9]
        return (ch >= 'a' && ch <='z') || (ch >= 'A' && ch <='Z') || (ch >= '0' && ch <='9') || ch == '_' || ch == '#';
    }

    public static String getPresentableText(LSFGlobalPropDeclaration property) {
        String text = getPresentableText((LSFActionOrGlobalPropDeclaration) property);

        LSFClassSet valueClass = property.resolveValueClass();
        text += ": " + (valueClass == null ? "?" : valueClass);
        return text;
    }

    public static String getPresentableText(LSFActionDeclaration property) {
        return getPresentableText((LSFActionOrGlobalPropDeclaration) property);
    }

    public static String getPresentableText(LSFActionOrGlobalPropDeclaration property) {
        String text = property.getPresentableText();

        String caption = property.getCaption();
        if (caption != null) {
            text += " " + caption;
        }
        return text;
    }

    @NotNull
    public static Set<LSFExprParamDeclaration> getContextParams(@NotNull PsiElement current, LSFLocalSearchScope localScope, boolean objectRef, boolean ignoreUseBeforeDeclarationCheck) {
        return getContextParams(current, current.getTextOffset(), localScope, objectRef, ignoreUseBeforeDeclarationCheck);
    }

    @NotNull
    public static Set<LSFExprParamDeclaration> getContextParams(PsiElement current, int offset, LSFLocalSearchScope localScope, boolean objectRef) {
        return getContextParams(current, offset, localScope, objectRef, false);
    }

    @NotNull
    public static Set<LSFExprParamDeclaration> getContextParams(PsiElement current, int offset, LSFLocalSearchScope localScope, boolean objectRef, boolean ignoreUseBeforeDeclarationCheck) {
        // current instanceof FormContext || current instancof LSFFormStatement
        Set<LSFObjectDeclaration> objects = LSFFormExtendImpl.processFormContext(current, LSFFormExtend::getObjectDecls, offset, localScope, objectRef, ignoreUseBeforeDeclarationCheck);
        if (objects != null) {
            return BaseUtils.immutableCast(objects);
        }

        if (current instanceof ModifyParamContext) {
            ContextModifier contextModifier = ((ModifyParamContext) current).getContextModifier();

            Set<LSFExprParamDeclaration> upParams;
            Set<LSFExprParamDeclaration> result = new HashSet<>();
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
                    upParams = new HashSet<>();
                else
                    upParams = getContextParams(context, upOffset, localScope, objectRef, ignoreUseBeforeDeclarationCheck);
                result.addAll(upParams);
            } else { // не extend - останавливаемся
                upParams = new HashSet<>();
            }
            result.addAll(contextModifier.resolveParams(offset, upParams));
            return result;
        }

        PsiElement parent = current.getParent();
        if (parent != null) {
            return getContextParams(parent, offset, localScope, objectRef, ignoreUseBeforeDeclarationCheck); // бежим выше
        }

        return new HashSet<>();
    }

    @NotNull
    public static List<LSFClassSet> getContextClasses(PsiElement psiElement, LSFLocalSearchScope localScope, boolean objectRef) {
        return LSFPsiImplUtil.resolveParamDeclClasses(getContextParams(psiElement, localScope, objectRef, true));
    }

    @NotNull
    public static List<LSFClassSet> getContextClasses(PsiElement psiElement, int offset, LSFLocalSearchScope localScope, boolean objectRef) {
        return LSFPsiImplUtil.resolveParamDeclClasses(getContextParams(psiElement, offset, localScope, objectRef));
    }

    public static Set<LSFFile> collectInjectedLSFFiles(VirtualFile file, Project project) {
        PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
        if (psiFile == null) {
            return Collections.EMPTY_SET;
        }

        return collectInjectedLSFFiles(psiFile, project);
    }

    public static Set<LSFFile> collectInjectedLSFFiles(PsiElement root, Project project) {
        Set<LSFFile> files = new ArrayListSet<>();
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

    public static <T> boolean allClassesDeclared(List<T> classes) {
        for (T classSet : classes)
            if (classSet == null)
                return false;
        return true;
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

    public static Set<LSFInterfacePropStatement> getPropertiesApplicableToClass(LSFValueClass valueClass, Project project, GlobalSearchScope scope, LSFLocalSearchScope localScope, boolean isLight, boolean isHeavy) {
        return mapPropertiesApplicableToClass(valueClass, project, scope, localScope, ApplicableMapper.STATEMENT, isLight, isHeavy);
    }

    public static Set<LSFInterfacePropStatement> getActionsApplicableToClass(LSFValueClass valueClass, Project project, GlobalSearchScope scope, LSFLocalSearchScope localScope, boolean isLight, boolean isHeavy) {
        return mapActionsApplicableToClass(valueClass, project, scope, localScope, ApplicableMapper.STATEMENT, isLight, isHeavy);
    }

    public static <T> Set<T> mapActionsApplicableToClass(LSFValueClass valueClass, Project project, GlobalSearchScope scope, LSFLocalSearchScope localScope, ApplicableMapper<T> applicableMapper, boolean isLight, boolean isHeavy) {
        Collection<LSFValueClass> classParents = CustomClassSet.getClassParentsRecursively(valueClass);
        Set<LSFInterfacePropStatement> resultStatements = newActionsOrPropertiesWithClassesInSignature(project, scope, localScope, isLight, isHeavy, ExplicitInterfaceActionIndex.getInstance(), valueClass, classParents);
        return mapActionsOrPropertiesApplicableToClass(valueClass, resultStatements, applicableMapper);
    }

    public static <T> Set<T> mapActionsWithClassInSignature(LSFValueClass valueClass, Project project, GlobalSearchScope scope, LSFLocalSearchScope localScope, ApplicableMapper<T> applicableMapper, boolean isLight, boolean isHeavy) {
        Set<LSFInterfacePropStatement> resultStatements = newActionsOrPropertiesWithClassesInSignature(project, scope, localScope, isLight, isHeavy, ExplicitInterfaceActionIndex.getInstance(), valueClass, Collections.singletonList(valueClass));
        return mapActionsOrPropertiesApplicableToClass(valueClass, resultStatements, applicableMapper);
    }
    
    public static <T> Set<T> mapPropertiesApplicableToClass(LSFValueClass valueClass, Project project, GlobalSearchScope scope, LSFLocalSearchScope localScope, ApplicableMapper<T> applicableMapper, boolean isLight, boolean isHeavy) {
        Collection<LSFValueClass> classParents = CustomClassSet.getClassParentsRecursively(valueClass); 
        return mapPropertiesWithClassesInSignature(valueClass, project, scope, localScope, applicableMapper, isLight, isHeavy, classParents);
    }

    public static <T> Set<T> mapPropertiesWithClassInSignature(LSFValueClass valueClass, Project project, GlobalSearchScope scope, LSFLocalSearchScope localScope, ApplicableMapper<T> applicableMapper, boolean isLight, boolean isHeavy) {
        mapPropertiesWithClassesInSignature(valueClass, project, scope, localScope, applicableMapper, isLight, isHeavy, Collections.singletonList(valueClass));
        return mapPropertiesWithClassesInSignature(valueClass, project, scope, localScope, applicableMapper, isLight, isHeavy, Collections.singletonList(valueClass));
    }

    @NotNull
    private static <T> Set<T> mapPropertiesWithClassesInSignature(LSFValueClass valueClass, Project project, GlobalSearchScope scope, LSFLocalSearchScope localScope, ApplicableMapper<T> applicableMapper, boolean isLight, boolean isHeavy, Collection<LSFValueClass> classParents) {
        Set<LSFInterfacePropStatement> resultStatements = newActionsOrPropertiesWithClassesInSignature(project, scope, localScope, isLight, isHeavy, ExplicitInterfacePropIndex.getInstance(), valueClass, classParents);
        if(isHeavy) {
            List<LSFGlobalPropDeclaration<?, ?>> statementsWithClassAsResult = new ArrayList<>();
            for (LSFValueClass clazz : classParents) {
                Collection<LSFExplicitValueProp> evStatements = LSFGlobalResolver.getItemsFromIndex(ExplicitValueIndex.getInstance(), clazz.getName(), project, scope, localScope);
                for (LSFExplicitValueProp<?, ?> evStatement : evStatements) {
                    statementsWithClassAsResult.add(evStatement.getDeclaration());
                }
            }

            Set<String> namesOfStatementsWithClassAsResult = new HashSet<>();
            int i = 0;
            LSFClassSet valueClassSet = valueClass.getUpSet();
            Set<LSFGlobalPropDeclaration<?, ?>> skipStatemens = new HashSet<>();
            while (i < statementsWithClassAsResult.size()) {
                LSFGlobalPropDeclaration<?, ?> statement = statementsWithClassAsResult.get(i);
                String statementName = statement.getName();

                if (!namesOfStatementsWithClassAsResult.contains(statementName) && !skipStatemens.contains(statement)) {
                    LSFClassSet resolvedClass = statement.resolveValueClass();
                    if (resolvedClass != null && resolvedClass.haveCommonChildren(valueClassSet, scope)) {
                        namesOfStatementsWithClassAsResult.add(statementName);

                        Collection<LSFImplicitValuePropStatement> ivStatements = LSFGlobalResolver.getItemsFromIndex(ImplicitValueIndex.getInstance(), statementName, project, scope, localScope);
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
                Collection<LSFImplicitInterfacePropStatement> iiStataments = LSFGlobalResolver.getItemsFromIndex(ImplicitInterfacePropIndex.getInstance(), name, project, scope, localScope);
                for (LSFImplicitInterfacePropStatement iiStatement : iiStataments) {
                    resultStatements.add(iiStatement.getPropertyStatement());
                }
            }
        }

        return mapActionsOrPropertiesApplicableToClass(valueClass, resultStatements, applicableMapper);
    }

    // возвращает mutable set
    public static <T extends LSFExplicitInterfaceProp<?, ?>> Set<LSFInterfacePropStatement> newActionsOrPropertiesWithClassesInSignature(Project project, GlobalSearchScope scope, LSFLocalSearchScope localScope, boolean isLight, boolean isHeavy, ExplicitInterfaceActionOrPropIndex<T> index, LSFValueClass valueClass, Collection<LSFValueClass> classes) {
        assert isLight || isHeavy;

        Set<LSFInterfacePropStatement> resultStatements = new HashSet<>();

        for (LSFValueClass clazz : classes) {
            Collection<T> eiStatements = LSFGlobalResolver.getItemsFromIndex(index, clazz.getName(), project, scope, localScope);
            for (T statement : eiStatements) {
                boolean explicit = statement.getExplicitParams() instanceof LSFExplicitSignature;
                if (isLight && isHeavy) {
                    resultStatements.add(statement.getDeclaration());
                } else if (isLight && explicit) {
                    resultStatements.add(statement);
                } else if (!isLight && !explicit) {
                    resultStatements.add(statement.getDeclaration());
                }
            }
        }

        if (isLight && !isHeavy && resultStatements.size() < 50) {
            Set<LSFInterfacePropStatement> transResultStatements = new HashSet<>();
            for(LSFInterfacePropStatement prop : resultStatements)
                transResultStatements.add(((T)prop).getDeclaration());
            resultStatements = transResultStatements;
        }
        return resultStatements;
    }

    private static <T> Set<T> mapActionsOrPropertiesApplicableToClass(LSFValueClass valueClass, Collection<LSFInterfacePropStatement> statements, ApplicableMapper<T> applicableMapper) {
        LSFClassSet valueClassSet = valueClass instanceof LSFClassDeclaration ? new CustomClassSet((LSFClassDeclaration) valueClass) : (LSFClassSet) valueClass;
        Set<T> result = new HashSet<>();
        for (LSFInterfacePropStatement statement : statements) {
            List<LSFClassSet> paramClasses = statement.resolveParamClasses();
            if (paramClasses != null && !paramClasses.isEmpty()) {
                for (LSFClassSet paramClass : paramClasses) {
                    if (paramClass != null) {
                        if (paramClass.containsAll(valueClassSet, true)) {
                            result.add(applicableMapper.map(statement, paramClass.getCommonClass()));
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

    public static PsiElement getFindUsagesIdentifyingElement(PsiElement element) {
        if (element instanceof LSFPropertyStatement) {
            return ((LSFPropertyStatement) element).getIdentifyingElement();
        } else if (element instanceof LSFFormDecl) {
            return ((LSFFormDecl) element).getIdentifyingElement();
        } else if (element instanceof LSFFormObjectDeclaration) {
            return ((LSFFormObjectDeclaration) element).getIdentifyingElement();
        } else if (element instanceof LSFFormUsage) {
            return ((LSFFormUsage) element).getFormUsageNameIdentifier();
        } else {
            return element;
        }
    }

}
