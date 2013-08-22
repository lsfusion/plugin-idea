
package com.simpleplugin.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.UserDataHolderEx;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.ProjectScope;
import com.intellij.psi.search.ProjectScopeBuilder;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.PsiFileStub;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.simpleplugin.LSFFileType;
import com.simpleplugin.LSFLanguage;
import com.simpleplugin.psi.declarations.LSFModuleDeclaration;
import com.simpleplugin.psi.stubs.types.LSFStubElementTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

public class LSFFile extends PsiFileBase {
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

    public List<LSFMetaCodeStatement> getMetaCodeStatementList() {
        return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFMetaCodeStatement.class);
    }

    @Override
    public String toString() {
        return "Lsf File";
    }

    @Override
    public Icon getIcon(int flags) {
        return super.getIcon(flags);
    }
}