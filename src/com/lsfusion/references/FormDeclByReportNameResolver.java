package com.lsfusion.references;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.lsfusion.lang.psi.LSFGlobalResolver;
import com.lsfusion.lang.psi.LSFLocalSearchScope;
import com.lsfusion.lang.psi.declarations.LSFFormDeclaration;
import com.lsfusion.lang.psi.extend.LSFFormExtend;
import com.lsfusion.lang.psi.stubs.extend.ExtendFormStubElement;
import com.lsfusion.lang.psi.stubs.types.LSFStubElementTypes;
import com.lsfusion.util.LSFFileUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class FormDeclByReportNameResolver {
    public static Pair<String, String> resolveFormFullNameAndRequires(VirtualFile virtualFile, Project project, PsiElement source) {
        LSFFormDeclaration formDeclaration = resolveByVirtualFile(virtualFile, project, LSFFileUtils.getModuleWithDependenciesScope(source));

        if (formDeclaration != null) {
            final Set<String> requiredModules = new HashSet<>();
            for(LSFFormExtend extend : LSFGlobalResolver.<ExtendFormStubElement, LSFFormExtend>findExtendElements(formDeclaration, formDeclaration.getProject(), LSFFileUtils.getModuleWithDependenciesScope(source), LSFLocalSearchScope.GLOBAL))
                requiredModules.add(extend.getLSFFile().getModuleDeclaration().getGlobalName());

            String requires = "";
            for (String moduleName : requiredModules) {
                if (requires.length() > 0) {
                    requires += ",";
                }
                requires += moduleName;
            }
            return Pair.create(formDeclaration.getNamespaceName() + "." + formDeclaration.getGlobalName(), requires);
        }

        return null;
    }

    public static LSFFormDeclaration resolveByVirtualFile(VirtualFile file, Project project, GlobalSearchScope scope) {
        return resolveByFullFileName(file.getNameWithoutExtension(), project, scope);
    }
    
    public static LSFFormDeclaration resolveByFullFileName(String fileName, Project project, GlobalSearchScope scope) {
        //[tableid_].*
        LSFFormDeclaration formDeclaration = resolveWithMaybeTablePrefix(fileName, project, scope);
        if (formDeclaration != null) {
            return formDeclaration;
        }
        //xls_.*
        return resolveWithXLSPrefix(fileName, project, scope);
    }

    private static LSFFormDeclaration resolveWithXLSPrefix(String fileName, Project project, GlobalSearchScope scope) {
        if (fileName.startsWith("xls_")) {
            return resolveWithMaybeTablePrefix(fileName.substring(4), project, scope);
        }
        return null;
    }

    private static LSFFormDeclaration resolveWithMaybeTablePrefix(String fileName, Project project, GlobalSearchScope scope) {
        LSFFormDeclaration formDecl = resolveWithMaybePostfix(fileName, project, scope);
        if (formDecl != null) {
            return formDecl;
        }

        if (fileName.startsWith("table")) {
            int underscoreInd = fileName.indexOf('_', 6);
            while (underscoreInd != -1) {
                formDecl = resolveWithMaybePostfix(fileName.substring(underscoreInd+1), project, scope);
                if (formDecl != null) {
                    return formDecl;
                }
                underscoreInd = fileName.indexOf('_', underscoreInd + 1);
            }
        }

        return null;
    }

    private static LSFFormDeclaration resolveWithMaybePostfix(String fileName, Project project, GlobalSearchScope scope) {
        LSFFormDeclaration formDecl = resolve(fileName, project, scope);
        if (formDecl != null) {
            return formDecl;
        }

        int underscoreInd = fileName.lastIndexOf('_');
        while (underscoreInd != -1) {
            formDecl = resolve(fileName.substring(0, underscoreInd), project, scope);
            if (formDecl != null) {
                return formDecl;
            }
            underscoreInd = fileName.lastIndexOf('_', underscoreInd - 1);
        }

        return null;
    }

    private static LSFFormDeclaration resolve(String fileName, Project project, GlobalSearchScope scope) {
        int underscoreInd = fileName.indexOf("_");
        if (underscoreInd == -1) {
            return null;
        }

        String namespace = fileName.substring(0, underscoreInd);
        String formName = fileName.substring(underscoreInd + 1);

        Collection<LSFFormDeclaration> decls = LSFGlobalResolver.findElements(formName, namespace, project, scope, LSFStubElementTypes.FORM);
        for (LSFFormDeclaration decl : decls)
            return decl;
        return null;
    }
}
