package com.lsfusion.refactoring;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.lsfusion.migration.MigrationElementGenerator;
import com.lsfusion.migration.lang.MigrationFileType;
import com.lsfusion.migration.lang.psi.MigrationFile;
import com.lsfusion.migration.lang.psi.MigrationStatement;
import com.lsfusion.migration.lang.psi.MigrationVersionBlock;
import com.lsfusion.migration.lang.psi.MigrationVersionBlockBody;
import com.lsfusion.util.LSFFileUtils;

import java.util.Collection;
import java.util.List;

import static com.lsfusion.util.LSFPsiUtils.getLastChildOfType;

public class MigrationScriptUtils {
    private static final String INITIAL_MIGRATION_VERSION = "1.0.0";

    public static void modifyMigrationScripts(List<ElementMigration> migrations, MigrationChangePolicy migrationChangePolicy, GlobalSearchScope scope) {
        Project project = scope.getProject();
        assert project != null;
        Collection<VirtualFile> migrationFiles = FileTypeIndex.getFiles(MigrationFileType.INSTANCE, scope);
        for (VirtualFile file : migrationFiles) {
            PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
            if (psiFile instanceof MigrationFile) {
                boolean createNewVersionBlock = migrationChangePolicy == MigrationChangePolicy.INCREMENT_VERSION;
                if (migrationChangePolicy == MigrationChangePolicy.INCREMENT_VERSION_IF_COMMITED) {
                    createNewVersionBlock = LSFFileUtils.isFileCommited(project, file);
                }

                List<String> topModulesNames = LSFFileUtils.getPossibleTopModules(psiFile);
                
                GlobalSearchScope searchScope = GlobalSearchScope.EMPTY_SCOPE;
                for (String topModuleName : topModulesNames) {
                    GlobalSearchScope moduleScope = LSFFileUtils.getElementRequireScope(psiFile, topModuleName, true);
                    searchScope = searchScope.union(moduleScope);
                }
                
                MigrationFile migrationFile = (MigrationFile)psiFile;
                
                 String statements = filterMigrationsToString(migrations, searchScope);
                if (statements.isEmpty()) {
                    continue;
                }

                MigrationVersionBlock lastVersionBlock = getLastChildOfType(migrationFile, MigrationVersionBlock.class);
                if (createNewVersionBlock || lastVersionBlock == null) {
                    String newVersion = lastVersionBlock == null ? INITIAL_MIGRATION_VERSION : incrementedVersion(lastVersionBlock.getVersionLiteral().getText());

                    String prefix = "";
                    PsiElement lastChild = migrationFile.getLastChild();
                    if (lastVersionBlock != null && lastChild != null && !lastChild.getText().endsWith("\n\n")) {
                        prefix = "\n\n";
                    }

                    Pair<PsiElement, PsiElement> newElements = MigrationElementGenerator.createVersionBlock(project, prefix, newVersion, statements);
                    migrationFile.addRange(newElements.first, newElements.second);
                } else {

                    Pair<PsiElement, PsiElement> newStatements = MigrationElementGenerator.createStatementsElements(project, statements);

                    MigrationVersionBlockBody lastVersionBlockBody = lastVersionBlock.getVersionBlockBody();
                    PsiElement anchor = getLastChildOfType(lastVersionBlockBody, MigrationStatement.class);
                    if (anchor == null) {
                        anchor = lastVersionBlockBody.getFirstChild();
                    }
                    
                    lastVersionBlockBody.addRangeAfter(newStatements.first, newStatements.second, anchor);
                }
            }
        }
    }

    private static String incrementedVersion(String currentVersionWithPrefix) {
        String currenVersion = currentVersionWithPrefix.trim().substring(1);
        
        int dotIndex = currenVersion.lastIndexOf('.');
        String lastPart;
        String prefix;
        if (dotIndex != -1) {
            prefix = currenVersion.substring(0, dotIndex + 1);
            lastPart = currenVersion.substring(dotIndex + 1).trim();
        } else {
            prefix = "";
            lastPart = currenVersion.trim();
        }

        int lastPartInt;
        try {
            lastPartInt = Integer.parseInt(lastPart) + 1;
        } catch (NumberFormatException nfe) {
            lastPartInt = 1;
        }
        
        return prefix + lastPartInt;
    }

    private static String filterMigrationsToString(List<ElementMigration> migrations, GlobalSearchScope moduleScope) {
        StringBuilder result = new StringBuilder();
        for (ElementMigration migration : migrations) {
            VirtualFile declarationFile = migration.getDeclarationFile();
            if (declarationFile != null && moduleScope.accept(declarationFile)) {
                if (result.length() != 0) {
                    result.append("\n");
                }

                result.append("    ");
                result.append(migration.getMigrationString());
            }
        }
        return result.toString();
    }
}
