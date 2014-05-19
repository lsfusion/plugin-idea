package com.lsfusion.lang.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Condition;
import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.declarations.LSFDeclaration;
import com.lsfusion.lang.psi.declarations.LSFObjectDeclaration;
import com.lsfusion.lang.psi.extend.LSFFormExtend;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

import static com.lsfusion.lang.psi.declarations.impl.LSFFormElementDeclarationImpl.Processor;

public abstract class LSFObjectDeclarationImpl extends LSFExprParamDeclarationImpl implements LSFObjectDeclaration, LSFFormExtendElement {

    public LSFObjectDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Nullable
    protected abstract LSFSimpleName getSimpleName();

    @NotNull
    protected abstract LSFClassName getClassName();

    @Nullable
    public LSFClassSet resolveClass() {
        return LSFPsiImplUtil.resolveClass(getClassName());
    }

    @Nullable
    @Override
    public LSFId getNameIdentifier() {
        LSFSimpleName simpleName = getSimpleName();
        if(simpleName!=null)
            return simpleName;

        LSFClassName className = getClassName();
        LSFBuiltInClassName builtInClassName = className.getBuiltInClassName();
        if(builtInClassName!=null)
            return builtInClassName;
        
        return className.getCustomClassUsage().getSimpleName();
    }

    public static Processor<LSFObjectDeclaration> getProcessor() {
        return new Processor<LSFObjectDeclaration>() {
            public Collection<LSFObjectDeclaration> process(LSFFormExtend formExtend) {
                return formExtend.getObjectDecls();
            }
        };
    }

    @Override
    public Condition<? extends LSFDeclaration> getDuplicateCondition() {
        return new Condition<LSFObjectDeclaration>() {
            @Override
            public boolean value(LSFObjectDeclaration lsfDeclaration) {
                return getNameIdentifier().getText().equals(lsfDeclaration.getNameIdentifier().getText());
            }
        };
    }
}
