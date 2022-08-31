package com.lsfusion.reports;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.ProjectScope;
import com.intellij.util.Processor;
import com.intellij.util.indexing.FileBasedIndex;
import com.lsfusion.design.DesignPreviewLineMarkerProvider;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.declarations.LSFFormDeclaration;
import com.lsfusion.lang.psi.declarations.LSFGroupObjectDeclaration;
import com.lsfusion.lang.psi.extend.LSFExtend;
import com.lsfusion.lang.psi.extend.LSFFormExtend;
import com.lsfusion.lang.psi.stubs.types.LSFStubElementTypes;
import com.lsfusion.util.LSFFileUtils;

import java.util.ArrayList;
import java.util.List;

import static com.lsfusion.util.LSFFileUtils.findFilesWithShortName;

public class ReportUtils {

    public static boolean hasReportFiles(PsiElement psi) {
        Result<LSFExtend> declExtend = new Result<>();
        LSFFormDeclaration decl = DesignPreviewLineMarkerProvider.resolveFormDecl(psi, declExtend);
        if (decl != null) {
            return findReportFiles(decl, LSFLocalSearchScope.createFrom(declExtend.getResult()), reportName -> !LSFFileUtils.hasFilesWithShortNameInProject(psi, reportName + ".jrxml"));
        } else {
            return false;
        }
    }

    public static Boolean hasReportFiles(LSFFormDeclaration decl) {
        return findReportFiles(decl, LSFLocalSearchScope.GLOBAL, reportName -> !LSFFileUtils.hasFilesWithShortNameInProject(decl, reportName + ".jrxml"));
    }

    public static List<PsiFile> findReportFiles(LSFFormDeclaration decl) {
        return findReportFiles(decl, LSFLocalSearchScope.GLOBAL);
    }

    public static List<PsiFile> findReportFiles(PsiElement psi) {
        List<PsiFile> files = new ArrayList<>();
        Result<LSFExtend> declExtend = new Result<>();
        LSFFormDeclaration decl = DesignPreviewLineMarkerProvider.resolveFormDecl(psi, declExtend);
        if (decl != null) {
            return findReportFiles(decl, LSFLocalSearchScope.createFrom(declExtend.getResult()));
        }
        return files;
    }

    public static List<PsiFile> findReportFiles(LSFFormDeclaration decl, LSFLocalSearchScope declScope) {
        List<PsiFile> files = new ArrayList<>();
        if(decl != null) {
            findReportFiles(decl, declScope, reportName -> {
                final Project project = decl.getProject();
                final GlobalSearchScope scope = ProjectScope.getAllScope(project);

                FileBasedIndex.getInstance().processAllKeys(FilenameIndex.NAME, fileName -> {
                    if (fileName.endsWith(".jrxml") && (fileName.startsWith(reportName + '.') || fileName.startsWith(reportName + '_'))) {
                        findFilesWithShortName(fileName, files, project, scope);
                    }
                    return true;
                }, scope, null);

                return true;
            });
        }
        return files;
    }

    public static boolean findReportFiles(LSFFormDeclaration decl, LSFLocalSearchScope scope, Processor<String> processor) {
        String namespace = decl.getNamespaceName();
        String name = decl.getDeclName();

        String reportName = namespace + "_" + name;

        if(!processor.process(reportName))
            return true;
        if(!processor.process("xls_" + reportName))
            return true;

        for (LSFFormExtend extend : LSFGlobalResolver.findExtendElements(decl, LSFStubElementTypes.EXTENDFORM, (LSFFile) decl.getContainingFile(), scope)) {
            for (LSFGroupObjectDeclaration groupObjectDecl : extend.getGroupObjectDecls()) {
                String groupName = groupObjectDecl.getDeclName();
                if (groupName != null) {
                    if(!processor.process("table" + groupName + "_" + reportName))
                        return true;
                    if(!processor.process("xls_table" + groupName + "_" + reportName))
                        return true;
                }
            }
        }
        return false;
    }

}
