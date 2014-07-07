package com.lsfusion.reports;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.ProjectScope;
import com.intellij.util.Processor;
import com.intellij.util.indexing.FileBasedIndex;
import com.lsfusion.lang.psi.LSFGlobalResolver;
import com.lsfusion.lang.psi.declarations.LSFFormDeclaration;
import com.lsfusion.lang.psi.declarations.LSFGroupObjectDeclaration;
import com.lsfusion.lang.psi.extend.LSFFormExtend;
import com.lsfusion.lang.psi.stubs.types.LSFStubElementTypes;
import com.lsfusion.util.LSFPsiUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.lsfusion.util.LSFPsiUtils.findFilesWithShortName;

public class ReportUtils {
    public static boolean hasReportFiles(LSFFormDeclaration decl) {
        String namespace = decl.getNamespaceName();
        String name = decl.getDeclName();

        String reportName = namespace + "_" + name;

        if (hasCustomReportFiles(decl, reportName) ||
            hasCustomReportFiles(decl, "xls_" + reportName)) {
            return true;
        }

        Collection<LSFFormExtend> formExtends = LSFGlobalResolver.findExtendElements(decl, LSFStubElementTypes.EXTENDFORM, decl.getLSFFile()).findAll();
        for (LSFFormExtend extend : formExtends) {
            for (LSFGroupObjectDeclaration groupObjectDecl : extend.getGroupObjectDecls()) {
                String groupName = groupObjectDecl.getDeclName();
                if (groupName != null) {
                    if (hasCustomReportFiles(decl, "table" + groupName + "_" + reportName) ||
                        hasCustomReportFiles(decl, "xls_table" + groupName + "_" + reportName)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static boolean hasCustomReportFiles(LSFFormDeclaration decl, String reportName) {
        return LSFPsiUtils.hasFilesWithShortNameInProject(decl, reportName + ".jrxml");
    }

    public static List<PsiFile> findReportFiles(LSFFormDeclaration decl) {
        List<PsiFile> files = new ArrayList<PsiFile>();
        
        String namespace = decl.getNamespaceName();
        String name = decl.getDeclName();

        String reportName = namespace + "_" + name;

        findRelatedReportFiles(decl, reportName, files);
        findRelatedReportFiles(decl, "xls_" + reportName, files);

        Collection<LSFFormExtend> formExtends = LSFGlobalResolver.findExtendElements(decl, LSFStubElementTypes.EXTENDFORM, decl.getLSFFile()).findAll();
        for (LSFFormExtend extend : formExtends) {
            for (LSFGroupObjectDeclaration groupObjectDecl : extend.getGroupObjectDecls()) {
                String groupName = groupObjectDecl.getDeclName();
                if (groupName != null) {
                    findRelatedReportFiles(decl, "table" + groupName + "_" + reportName, files);
                    findRelatedReportFiles(decl, "xls_table" + groupName + "_" + reportName, files);
                }
            }
        }
        return files;
    }

    public static void findRelatedReportFiles(LSFFormDeclaration decl, final String reportName, final List<PsiFile> files) {
        final Project project = decl.getProject();
        final GlobalSearchScope scope = ProjectScope.getAllScope(project);
        
        FileBasedIndex.getInstance().processAllKeys(FilenameIndex.NAME, new Processor<String>() {
            @Override
            public boolean process(String fileName) {
                if (fileName.startsWith(reportName) && fileName.endsWith(".jrxml")) {
                    findFilesWithShortName(fileName, files, project, scope);
                }
                return true;
            }
        }, scope, null);
    }
}
