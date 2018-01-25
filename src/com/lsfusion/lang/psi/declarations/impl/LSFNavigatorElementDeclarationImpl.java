package com.lsfusion.lang.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;
import com.lsfusion.LSFIcons;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.declarations.LSFNavigatorElementDeclaration;
import com.lsfusion.lang.psi.stubs.NavigatorElementStubElement;
import com.lsfusion.lang.psi.stubs.types.FullNameStubElementType;
import com.lsfusion.lang.psi.stubs.types.LSFStubElementTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public abstract class LSFNavigatorElementDeclarationImpl extends LSFFullNameDeclarationImpl<LSFNavigatorElementDeclaration, NavigatorElementStubElement> implements LSFNavigatorElementDeclaration {

    protected LSFNavigatorElementDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }

    protected LSFNavigatorElementDeclarationImpl(@NotNull NavigatorElementStubElement navigatorElementStubElement, @NotNull IStubElementType nodeType) {
        super(navigatorElementStubElement, nodeType);
    }

    @Nullable
    @Override
    public LSFId getNameIdentifier() {
        LSFId result = null;
        if (getSimpleName() != null) {
            result = getSimpleName();
        } else {
            LSFNavigatorElementDescription descr = getNavigatorElementDescription();
            if (descr != null) {
                LSFFormUsage form = descr.getFormUsage();
                if (form != null) {
                    result = form.getSimpleName();
                } else {
                    LSFNoContextPropertyUsage action = descr.getNoContextPropertyUsage();
                    if (action != null) {
                        result = action.getPropertyUsage().getSimpleName();
                    }
                }
            }
        }
        return result;
    }

    @Nullable
    @Override
    public Icon getIcon(int flags) {
        return LSFIcons.NAVIGATOR_ELEMENT;
    }

    @Override
    public LSFSimpleName getSimpleName() {
        LSFNavigatorElementDescription navigatorElementDescription = getNavigatorElementDescription();
        if(navigatorElementDescription != null)
            return navigatorElementDescription.getSimpleName();
        return null;
    }

    @Override
    protected FullNameStubElementType getType() {
        return LSFStubElementTypes.NAVIGATORELEMENT;
    }
}
