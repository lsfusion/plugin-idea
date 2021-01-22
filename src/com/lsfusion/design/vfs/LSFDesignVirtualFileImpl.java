package com.lsfusion.design.vfs;

import com.intellij.openapi.util.Conditions;
import com.intellij.psi.PsiInvalidElementAccessException;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.util.Query;
import com.lsfusion.design.DesignInfo;
import com.lsfusion.lang.psi.LSFGlobalResolver;
import com.lsfusion.lang.psi.declarations.LSFFormDeclaration;
import com.lsfusion.lang.psi.declarations.LSFModuleDeclaration;
import com.lsfusion.lang.psi.extend.LSFFormExtend;
import com.lsfusion.lang.psi.stubs.types.LSFStubElementTypes;
import com.lsfusion.lang.psi.indexes.FormIndex;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

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
        try {
            for(LSFFormDeclaration formDeclaration : LSFGlobalResolver.findElements(formName, null, module.getLSFFile(), null, Collections.singleton(LSFStubElementTypes.FORM), Conditions.alwaysTrue())) {
                for (LSFFormExtend formExtend : LSFGlobalResolver.findExtendElements(formDeclaration, LSFStubElementTypes.EXTENDFORM, module.getLSFFile())) {
                    if (formExtend.getLSFFile().getModuleDeclaration() == module) {
                        DesignInfo designInfo = new DesignInfo(formDeclaration, module.getLSFFile());
                        try {
                            rename(null, designInfo.getFormCaption());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return designInfo;
                    }
                }
            }
        } catch (PsiInvalidElementAccessException ignored) {
        } // вылетает, если вообще проблемы с парсингом - игнорим
        return null;
    }

    public static LSFDesignVirtualFileImpl create(LSFModuleDeclaration module, String formName) {
        LSFDesignVirtualFileImpl file = new LSFDesignVirtualFileImpl(module, formName);
        file.setOriginalFile(module.getContainingFile().getVirtualFile());
        return file;
    }
}
