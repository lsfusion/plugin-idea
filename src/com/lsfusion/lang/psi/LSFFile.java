
package com.lsfusion.lang.psi;

import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.PsiFileImpl;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.ProjectScope;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.SmartList;
import com.lsfusion.lang.LSFFileType;
import com.lsfusion.lang.LSFLanguage;
import com.lsfusion.lang.LSFParserDefinition;
import com.lsfusion.lang.psi.context.ContextInferrer;
import com.lsfusion.lang.psi.context.ContextModifier;
import com.lsfusion.lang.psi.context.ModifyParamContext;
import com.lsfusion.lang.psi.declarations.LSFModuleDeclaration;
import com.lsfusion.lang.psi.stubs.types.LSFStubElementTypes;
import com.lsfusion.util.BaseUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Collection;
import java.util.List;

public class LSFFile extends PsiFileImpl implements ModifyParamContext {
    public LSFFile(@NotNull FileViewProvider viewProvider) {
        this(viewProvider, LSFParserDefinition.LSF_FILE, LSFParserDefinition.LSF_FILE);
    }

    protected LSFFile(@NotNull FileViewProvider viewProvider, IFileElementType fileElementType, IFileElementType fileContentType) {
        super(viewProvider);
        init(fileElementType, fileContentType);
    }

    @Override
    public boolean isWritable() {
        return true; // фиксили IncorrectOperationException: Cannot modify a read-only file на разворачивании метакодов
    }

    @Override
    public void accept(@NotNull PsiElementVisitor visitor) {
        visitor.visitFile(this);
    }

    @NotNull
    @Override
    public Language getLanguage() {
        return LSFLanguage.INSTANCE;
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return LSFFileType.INSTANCE;
    }

    public GlobalSearchScope getScope() {
        if (this instanceof LSFCodeFragment && getContext() != null) {
            PsiFile containingFile = getContext().getContainingFile();
            if (containingFile instanceof LSFFile && containingFile != this) {
                return ((LSFFile) containingFile).getScope();
            }
        }
        Project project = getProject();
        
        if (getVirtualFile() == null) {
            // в fillVariants не узнаешь какой конкретно сейчас файл
            return GlobalSearchScope.allScope(project);
        }
        Module moduleForFile = ModuleUtilCore.findModuleForFile(getVirtualFile(), project);
        if (moduleForFile == null) {
            // library
            return ProjectScope.getLibrariesScope(project);
        }
        return GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(moduleForFile);
    }
    public GlobalSearchScope getRequireScope() {
        return LSFGlobalResolver.getRequireScope(this);
    }

    @Nullable
    public <Psi extends PsiElement> Psi getStubOrPsiChild(final IStubElementType<? extends StubElement, Psi> elementType) {
        StubElement stub = getStub();
        if (stub != null) {
            final StubElement<Psi> element = stub.findChildStubByType(elementType);
            if (element != null) {
                return element.getPsi();
            }
        }
        else {
            final ASTNode childNode = getNode().findChildByType(elementType);
            if (childNode != null) {
                return (Psi)childNode.getPsi();
            }
        }
        return null;
    }

    public LSFModuleDeclaration getModuleDeclaration() {
        return getStubOrPsiChild(LSFStubElementTypes.MODULE);
    }

    public List<PsiElement> getStatements() {
        List<PsiElement> result = new SmartList<>();
        for (PsiElement child = getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child instanceof LSFLazyScriptStatement) {
                for (LSFScriptStatement scriptStatement : ((LSFLazyScriptStatement) child).getScriptStatementList()) {
                    PsiElement scriptChild = scriptStatement.getFirstChild();
                    if (scriptChild instanceof LSFMetaCodeDeclarationStatement) {
                        LSFMetaCodeDeclBody metaBody = ((LSFMetaCodeDeclarationStatement) scriptChild).getMetaCodeDeclBody();
                        if (metaBody != null)
                            for (LSFLazyMetaDeclStatement metaDeclStatement : metaBody.getLazyMetaDeclStatementList())
                                result.addAll(BaseUtils.toList(metaDeclStatement.getChildren()));
                    } else
                        result.add(scriptChild);
                }
            }
        }
        return result;
    }

    public Collection<LSFScriptStatement> getScriptStatements() {
        return PsiTreeUtil.findChildrenOfType(this, LSFScriptStatement.class);
    }
    public Collection<LSFLazyScriptStatement> getLazyScriptStatements() {
        return PsiTreeUtil.findChildrenOfType(this, LSFLazyScriptStatement.class);
    }

    public List<LSFMetaCodeStatement> getMetaCodeStatementList() {
        List<LSFMetaCodeStatement> result = new SmartList<>();
        for (PsiElement statement : getStatements()) {
            if (statement instanceof LSFMetaCodeStatement) {
                result.add((LSFMetaCodeStatement) statement);
            }
        }
        return result;
    }
    
    public List<LSFMetaCodeStatement> getDisabledMetaCodeStatementList() {
        List<LSFMetaCodeStatement> result = new SmartList<>();
        for (PsiElement statement : getStatements()) {
            if (statement instanceof LSFMetaCodeStatement && ((LSFMetaCodeStatement) statement).getMetaCodeBody() == null) {
                result.add((LSFMetaCodeStatement) statement);
            }
        }
        return result;    
    }

    @Override
    public String toString() {
        return "Lsf File";
    }

    @Override
    public Icon getIcon(int flags) {
        return super.getIcon(flags);
    }

    @Override
    public ContextModifier getContextModifier() {
        return ContextModifier.EMPTY;
    }

    @Override
    public ContextInferrer getContextInferrer() {
        return ContextInferrer.EMPTY;
    }
}