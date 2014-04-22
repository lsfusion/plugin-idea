
package com.lsfusion.lang.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.ProjectScope;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import com.intellij.util.SmartList;
import com.lsfusion.lang.LSFFileType;
import com.lsfusion.lang.LSFLanguage;
import com.lsfusion.lang.psi.context.ContextInferrer;
import com.lsfusion.lang.psi.context.ContextModifier;
import com.lsfusion.lang.psi.context.ModifyParamContext;
import com.lsfusion.lang.psi.declarations.LSFModuleDeclaration;
import com.lsfusion.lang.psi.stubs.types.LSFStubElementTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

public class LSFFile extends PsiFileBase implements ModifyParamContext {
    public LSFFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, LSFLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return LSFFileType.INSTANCE;
    }

    public GlobalSearchScope getScope() {
        Project project = getProject();
        if(getVirtualFile()==null) // в fillVariants не узнаешь какой конкретно сейчас файл
            return GlobalSearchScope.allScope(project);  
        Module moduleForFile = ModuleUtilCore.findModuleForFile(getVirtualFile(), project);
        if(moduleForFile==null) // library
            return ProjectScope.getLibrariesScope(project);
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
        List<PsiElement> result = new SmartList<PsiElement>();
        for (PsiElement child = getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child instanceof LSFLazyScriptStatement) {
                for (PsiElement ch = child.getFirstChild(); ch != null; ch = ch.getNextSibling()) {
                    if (ch instanceof LSFScriptStatement) {
                        result.add(ch.getFirstChild());
                    }
                }
            }
        }
        return result;
    }

    public List<LSFMetaCodeStatement> getMetaCodeStatementList() {
        List<LSFMetaCodeStatement> result = new SmartList<LSFMetaCodeStatement>();
        for (PsiElement statement : getStatements()) {
            if (statement instanceof LSFMetaCodeStatement) {
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