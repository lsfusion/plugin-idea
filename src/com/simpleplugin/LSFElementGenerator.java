package com.simpleplugin;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.impl.PsiFileFactoryImpl;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.LightVirtualFile;
import com.simpleplugin.psi.LSFCompoundID;
import com.simpleplugin.psi.LSFId;
import com.simpleplugin.psi.LSFSimpleName;
import org.jetbrains.annotations.Nullable;

public class LSFElementGenerator {

    @Nullable
    public static LSFId createIdentifierFromText(Project myProject, String name) {
        final PsiFile dummyFile = createDummyFile(myProject, "MODULE " + name + ";");
        for(LSFId child : PsiTreeUtil.findChildrenOfType(dummyFile, LSFId.class))
            if(!child.getText().equals("dummy"))
                return child;
        return null;
    }

    public static PsiFile createDummyFile(Project myProject, String text) {
        final PsiFileFactory factory = PsiFileFactory.getInstance(myProject);
        final String name = "dummy." + LSFFileType.INSTANCE.getDefaultExtension();
        final LightVirtualFile virtualFile = new LightVirtualFile(name, LSFFileType.INSTANCE, text);
        final PsiFile psiFile = ((PsiFileFactoryImpl)factory).trySetupPsiForFile(virtualFile, LSFLanguage.INSTANCE, false, true);
        assert psiFile != null;
        return psiFile;
    }

/*    public static LSFMetaCodeBody createMetaBodyFromText(Project myProject, String text) {
        final PsiFile dummyFile = createDummyFile(myProject, "@dummy() " + text + ";");
        return PsiTreeUtil.findChildrenOfType(dummyFile, LSFMetaCodeBody.class).iterator().next();
    }*/
}
