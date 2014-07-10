package com.lsfusion.util;

import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.OrderEnumerator;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileSystemItem;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.ProjectScope;
import com.intellij.psi.search.PsiElementProcessor;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Processor;
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
import com.lsfusion.lang.psi.references.impl.LSFFormElementReferenceImpl;
import com.lsfusion.lang.psi.stubs.interfaces.types.indexes.ExplicitInterfaceIndex;
import com.lsfusion.lang.psi.stubs.interfaces.types.indexes.ExplicitValueIndex;
import com.lsfusion.lang.psi.stubs.interfaces.types.indexes.ImplicitInterfaceIndex;
import com.lsfusion.lang.psi.stubs.interfaces.types.indexes.ImplicitValueIndex;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
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
    
    public static boolean hasFilesWithShortNameInProject(PsiElement scopeElement, final String fileName) {
        Project project = scopeElement.getProject();
        GlobalSearchScope scope = ProjectScope.getAllScope(project);
        return hasFilesWithShortName(fileName, project, scope);
    }
    
    public static boolean hasFilesWithShortNameInModule(PsiElement scopeElement, final String fileName) {
        Module module = ModuleUtil.findModuleForPsiElement(scopeElement);
        return hasFilesWithShortNameInModule(module, fileName);
    }
    
    public static boolean hasFilesWithShortNameInModule(Module module, final String fileName) {
        if (module != null) {
            GlobalSearchScope scope = GlobalSearchScope.moduleWithDependentsScope(module);
            return hasFilesWithShortName(fileName, module.getProject(), scope);
            
        }
        return false;
    }

    private static boolean hasFilesWithShortName(String fileName, Project project, GlobalSearchScope scope) {
        final Result<Boolean> hasFiles = new Result<Boolean>(false);
        FilenameIndex.processFilesByName(
                fileName, false, new Processor<PsiFileSystemItem>() {
                    @Override
                    public boolean process(PsiFileSystemItem file) {
                        if (!file.isDirectory() && file instanceof PsiFile) {
                            hasFiles.setResult(true);
                            return false;
                        }
                        return true;
                    }
                },
                scope,
                project,
                null
        );
        return hasFiles.getResult();
    }
    
    public static void findFilesWithShortName(String fileName, final List<PsiFile> files, Project project, GlobalSearchScope scope) {
        FilenameIndex.processFilesByName(
                fileName, false, new Processor<PsiFileSystemItem>() {
                    @Override
                    public boolean process(PsiFileSystemItem file) {
                        if (!file.isDirectory() && file instanceof PsiFile) {
                            files.add((PsiFile) file);
                        }
                        return true;
                    }
                },
                scope,
                project,
                null
        );
    }

    public static List<PsiFile> findFilesByPath(PsiElement scopeElement, final String path) {
        Module module = ModuleUtil.findModuleForPsiElement(scopeElement);
        return findFilesByPath(module, path);
    }
    
    public static List<PsiFile> findFilesByPath(Module module, final String path) {
        final PsiManager psiManager = PsiManager.getInstance(module.getProject());

        final List<PsiFile> result = new ArrayList<PsiFile>();

        final OrderEnumerator orderEnumerator = ModuleRootManager.getInstance(module).orderEntries();

        proceedModuleRoots(module, path, result, psiManager);
        orderEnumerator.forEachModule(new Processor<Module>() {
            @Override
            public boolean process(Module module) {
                proceedModuleRoots(module, path, result, psiManager);
                return true;
            }
        });
        
        if (result.isEmpty()) {
            //almost orderEnumerator.getAllLibrariesAndSdkClassesRoots(), but without sdk
            VirtualFile[] classRoots = orderEnumerator.withoutModuleSourceEntries().recursively().withoutSdk().exportedOnly().classes().usingCache().getRoots();
            for (VirtualFile classRoot : classRoots) {
                VirtualFile file = classRoot.findFileByRelativePath(path);
                if (file != null) {
                    result.add(psiManager.findFile(file));
                }
            }
        }


        return result;
    }

    private static void proceedModuleRoots(Module module, String path, List<PsiFile> result, PsiManager psiManager) {
        VirtualFile[] sourceRoots = ModuleRootManager.getInstance(module).getSourceRoots();
        if (sourceRoots.length > 0) {
            for (VirtualFile sourceRoot : sourceRoots) {
                VirtualFile file = sourceRoot.findFileByRelativePath(path);
                if (file != null) {
                    result.add(psiManager.findFile(file));
                }
            }
        }
    }

    @NotNull
    public static String getFileRelativePath(PsiFile file) {
        String relativePath = getRelativePath(file.getVirtualFile(), file.getProject());
        return relativePath == null ? file.getName() : relativePath;
    }

    // c/p from com.intellij.ide.util.gotoByName.GotoFileCellRenderer.getRelativePath()
    @Nullable
    public static String getRelativePath(final VirtualFile virtualFile, final Project project) {
        String url = virtualFile.getPresentableUrl();
        if (project == null) {
            return url;
        }
        VirtualFile root = ProjectFileIndex.SERVICE.getInstance(project).getContentRootForFile(virtualFile);
        if (root != null) {
            return root.getName() + File.separatorChar + VfsUtilCore.getRelativePath(virtualFile, root, File.separatorChar);
        }

        final VirtualFile baseDir = project.getBaseDir();
        if (baseDir != null) {
            //noinspection ConstantConditions
            final String projectHomeUrl = baseDir.getPresentableUrl();
            if (url.startsWith(projectHomeUrl)) {
                final String cont = url.substring(projectHomeUrl.length());
                if (cont.isEmpty()) {
                    return null;
                }
                url = "..." + cont;
            }
        }
        return url;
    }

    public static GlobalSearchScope getModuleScope(@NotNull PsiElement psi) {
        Module module = ModuleUtil.findModuleForPsiElement(psi);
        return module == null
               ? GlobalSearchScope.allScope(psi.getProject())
               : GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(module);
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
                            result.add((T) resultHandler.getResult(statement, paramClass.getCommonClass()));
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
    
    public static boolean isInjected(PsiElement element) {
        PsiFile injectedFile = element.getContainingFile();
        if (injectedFile == null) return false;
        Project project = injectedFile.getProject();
        InjectedLanguageManager languageManager = InjectedLanguageManager.getInstance(project);
        return languageManager.isInjectedFragment(injectedFile);
    }
}
