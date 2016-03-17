package com.lsfusion.migration;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.impl.PsiFileFactoryImpl;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.LightVirtualFile;
import com.lsfusion.migration.lang.MigrationFileType;
import com.lsfusion.migration.lang.MigrationLanguage;
import com.lsfusion.migration.lang.psi.MigrationVersionBlockBody;
import org.jetbrains.annotations.NotNull;

public class MigrationElementGenerator {

    @NotNull
    public static Pair<PsiElement, PsiElement> createVersionBlock(Project myProject, String prefix, String version, String innerText) {
        final PsiFile dummyFile = createDummyFile(myProject, prefix + "V" + version + " {\n" + innerText + "\n}");
        return Pair.create(dummyFile.getFirstChild(), dummyFile.getLastChild());
    }

    @NotNull
    public static Pair<PsiElement, PsiElement> createStatementsElements(Project myProject, String statements) {
        final PsiFile dummyFile = createDummyFile(myProject, "V1.0.0 {\n" + statements + "\n}");
        
        MigrationVersionBlockBody versionBlockBody = PsiTreeUtil.findChildOfType(dummyFile, MigrationVersionBlockBody.class);
        
        assert versionBlockBody != null;
        
        //skip "{" and "}"
        PsiElement firstChild = versionBlockBody.getFirstChild().getNextSibling();
        PsiElement lastChild = versionBlockBody.getLastChild().getPrevSibling();
        
        return Pair.create(firstChild, lastChild);
    }

    public static PsiFile createDummyFile(Project myProject, String text) {
        final PsiFileFactory factory = PsiFileFactory.getInstance(myProject);
        final LightVirtualFile virtualFile = new LightVirtualFile("migration.script", MigrationFileType.INSTANCE, text);
        final PsiFile psiFile = ((PsiFileFactoryImpl)factory).trySetupPsiForFile(virtualFile, MigrationLanguage.INSTANCE, false, true);
        assert psiFile != null;
        return psiFile;
    }
}
