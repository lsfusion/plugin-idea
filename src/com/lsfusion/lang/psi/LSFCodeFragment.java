
package com.lsfusion.lang.psi;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.FileElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.testFramework.LightVirtualFile;
import com.lsfusion.lang.LSFFileType;
import com.lsfusion.lang.psi.context.ContextModifier;
import com.lsfusion.lang.psi.context.ExprsContextModifier;
import com.lsfusion.lang.psi.context.ExtendParamContext;
import com.lsfusion.lang.psi.declarations.LSFModuleDeclaration;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

import static com.lsfusion.lang.LSFParserDefinition.LSF_FILE;
import static com.lsfusion.lang.psi.LSFCodeFragmentElementType.LSF_ACTION_FRAGMENT;
import static com.lsfusion.lang.psi.LSFCodeFragmentElementType.LSF_EXPRESSION_FRAGMENT;
import static java.util.Arrays.asList;

public class LSFCodeFragment extends LSFFile implements JavaCodeFragment, ExtendParamContext {
    private final boolean isExpression;
    
    private PsiType thisType;
    private PsiType superType;
    private ExceptionHandler exceptionHandler;
    private GlobalSearchScope resolveScope;

    private Set<String> imports = new HashSet<String>();

    private PsiElement context;

    private FileViewProvider myViewProvider = null;

    public LSFCodeFragment(boolean isExpression, Project project, PsiElement context, CharSequence text) {
        super(
            new SingleRootFileViewProvider(PsiManager.getInstance(project), new LightVirtualFile("fragment.lsf", LSFFileType.INSTANCE, text), true),
            LSF_FILE,
            isExpression ? LSF_EXPRESSION_FRAGMENT : LSF_ACTION_FRAGMENT
        );
        this.isExpression = isExpression;
        ((SingleRootFileViewProvider) getViewProvider()).forceCachedPsi(this);
        setContext(context);
    }

    public boolean isExpression() {
        return isExpression;
    }

    @Override
    public LSFModuleDeclaration getModuleDeclaration() {
        PsiElement context = getContext();
        if (context != null && context.getContainingFile() instanceof LSFFile) {
            LSFFile file = (LSFFile) context.getContainingFile();
            return file.getModuleDeclaration();
        }
        return null;
    }

    public PsiType getThisType() {
        return thisType;
    }

    public void setThisType(PsiType thisType) {
        this.thisType = thisType;
    }

    public PsiType getSuperType() {
        return superType;
    }

    public void setSuperType(PsiType superType) {
        this.superType = superType;
    }

    public ExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }

    public void setExceptionHandler(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public void setVisibilityChecker(JavaCodeFragment.VisibilityChecker checker) {
    }

    @Override
    public VisibilityChecker getVisibilityChecker() {
        return VisibilityChecker.EVERYTHING_VISIBLE;
    }

    @Override
    public void forceResolveScope(GlobalSearchScope resolveScope) {
        this.resolveScope = resolveScope;
    }

    @Override
    public GlobalSearchScope getForcedResolveScope() {
        return resolveScope;
    }

    @Override
    public boolean importClass(PsiClass aClass) {
        return false;
    }

    @Override
    public String importsToString() {
        return StringUtil.join(imports, ",");
    }

    @Override
    public void addImportsFromString(String imports) {
        if (imports != null) {
            this.imports.addAll(asList(imports.split(",")));
        }
    }

    public void setContext(PsiElement context) {
        if (context != null) {
            this.context = context;
        }
    }

    @Override
    public PsiElement getContext() {
        if (context != null) {
            return context;
        }
        return super.getContext();
    }

    @Override
    public boolean isValid() {
        if (!super.isValid()) return false;
        PsiElement context = getContext();
        return context == null || context.isValid();
    }
    
    public LSFPropertyExpression getPropertyExpression() {
        final ASTNode childNode = getNode().findChildByType(LSFTypes.PROPERTY_EXPRESSION);
        if (childNode != null) {
            return (LSFPropertyExpression)childNode.getPsi();
        }
        return null;
    }

    @Override
    public ContextModifier getContextModifier() {
        LSFPropertyExpression pe = getPropertyExpression();
        return pe != null
               ? new ExprsContextModifier(pe)
               : ContextModifier.EMPTY;
    }

    @Override
    @NotNull
    public FileViewProvider getViewProvider() {
        if (myViewProvider != null) return myViewProvider;
        return super.getViewProvider();
    }

    @Override
    protected LSFCodeFragment clone() {
        final LSFCodeFragment clone = (LSFCodeFragment) cloneImpl((FileElement) calcTreeElement().clone());
        clone.myOriginalFile = this;
        clone.imports.addAll(imports);

        SingleRootFileViewProvider cloneViewProvider = new SingleRootFileViewProvider(
            getManager(),
            new LightVirtualFile(getName(), getLanguage(), getText()),
            false
        );

        cloneViewProvider.forceCachedPsi(clone);
        clone.myViewProvider = cloneViewProvider;
        return clone;
    }
}
