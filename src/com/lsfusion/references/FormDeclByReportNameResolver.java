package com.lsfusion.references;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.Processor;
import com.intellij.util.Query;
import com.lsfusion.lang.psi.LSFGlobalResolver;
import com.lsfusion.lang.psi.declarations.LSFFormDeclaration;
import com.lsfusion.lang.psi.extend.LSFFormExtend;
import com.lsfusion.lang.psi.stubs.types.LSFStubElementTypes;
import com.lsfusion.lang.psi.indexes.FormIndex;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class FormDeclByReportNameResolver {
    public static Pair<String, String> resolveFormFullNameAndRequires(VirtualFile virtualFile, Project project, GlobalSearchScope scope) {
        LSFFormDeclaration formDeclaration = resolveByVirtualFile(virtualFile, project, scope);

        if (formDeclaration != null) {
            Query<LSFFormExtend> formExtends = LSFGlobalResolver.findExtendElements(formDeclaration, LSFStubElementTypes.EXTENDFORM, project, scope);
            final Set<String> requiredModules = new HashSet<String>();
            formExtends.forEach(new Processor<LSFFormExtend>() {
                @Override
                public boolean process(LSFFormExtend extend) {
                    requiredModules.add(extend.getLSFFile().getModuleDeclaration().getGlobalName());
                    return true;
                }
            });
            String requires = "";
            for (String moduleName : requiredModules) {
                if (requires.length() > 0) {
                    requires += ",";
                }
                requires += moduleName;
            }
            return new Pair<String, String>(formDeclaration.getNamespaceName() + "." + formDeclaration.getGlobalName(), requires);
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

        Collection<LSFFormDeclaration> decls = FormIndex.getInstance().get(formName, project, scope);
        for (LSFFormDeclaration decl : decls) {
            if (namespace.equals(decl.getNamespaceName())) {
                return decl;
            }

        }
        return null;
    }
}
