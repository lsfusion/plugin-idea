package com.lsfusion.design.vfs;

import com.intellij.psi.PsiInvalidElementAccessException;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.util.Query;
import com.lsfusion.design.DesignInfo;
import com.lsfusion.lang.psi.LSFGlobalResolver;
import com.lsfusion.lang.psi.declarations.LSFFormDeclaration;
import com.lsfusion.lang.psi.declarations.LSFModuleDeclaration;
import com.lsfusion.lang.psi.extend.LSFFormExtend;
import com.lsfusion.lang.psi.stubs.types.LSFStubElementTypes;
import com.lsfusion.lang.psi.stubs.types.indexes.FormIndex;

import java.io.IOException;
import java.util.Collection;

public class LSFDesignVirtualFileImpl extends LightVirtualFile {
    public LSFModuleDeclaration module;
    public String formName;

    public LSFDesignVirtualFileImpl(LSFModuleDeclaration module, String formName) {
        super("DesignPreview", LSFDesignFileType.INSTANCE, "");
        this.module = module;
        this.formName = formName;
        setWritable(false);
    }

    public DesignInfo getDesignInfo() {
        DesignInfo designInfo = null;

        try {
            Collection<LSFFormDeclaration> declarations = FormIndex.getInstance().get(formName, module.getProject(), module.getLSFFile().getRequireScope());
            if (!declarations.isEmpty()) {
                LSFFormDeclaration formDeclaration = declarations.iterator().next();
                Query<LSFFormExtend> lsfFormExtends = LSFGlobalResolver.findExtendElements(formDeclaration, LSFStubElementTypes.EXTENDFORM, module.getLSFFile());

                for (LSFFormExtend formExtend : lsfFormExtends.findAll()) {
                    if (formExtend.getLSFFile().getModuleDeclaration() == module) {
                        designInfo = new DesignInfo(formDeclaration, module.getLSFFile());
                        try {
                            rename(null, designInfo.getFormCaption());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                }
            }
        } catch (PsiInvalidElementAccessException ignored) {
        } // вылетает, если вообще проблемы с парсингом - игнорим
        return designInfo;
    }

    public static LSFDesignVirtualFileImpl create(LSFModuleDeclaration module, String formName) {
        LSFDesignVirtualFileImpl file = new LSFDesignVirtualFileImpl(module, formName);
        file.setOriginalFile(module.getContainingFile().getVirtualFile());
        return file;
    }
}
