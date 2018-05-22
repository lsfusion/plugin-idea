package com.lsfusion.lang.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import com.lsfusion.lang.psi.LSFImportFieldAlias;
import com.lsfusion.lang.psi.LSFImportFieldName;
import com.lsfusion.lang.psi.LSFSimpleName;
import com.lsfusion.lang.psi.declarations.LSFImportFieldParamDeclaration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class LSFImportFieldParamDeclartionImpl extends LSFParamDeclarationImpl implements LSFImportFieldParamDeclaration {

    public LSFImportFieldParamDeclartionImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Nullable
    public abstract LSFImportFieldAlias getImportFieldAlias();

    @NotNull
    public abstract LSFImportFieldName getImportFieldName();

    @Nullable
    @Override
    protected LSFSimpleName getSimpleName() {
        LSFImportFieldAlias alias = getImportFieldAlias();
        if(alias != null)
            return alias.getSimpleName();

        LSFImportFieldName name = getImportFieldName();
        if(name != null)
            return name.getSimpleName();
            
        return null;
    }
}
