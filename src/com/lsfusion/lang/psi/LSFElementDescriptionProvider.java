package com.lsfusion.lang.psi;

import com.intellij.ide.util.DeleteNameDescriptionLocation;
import com.intellij.ide.util.DeleteTypeDescriptionLocation;
import com.intellij.psi.ElementDescriptionLocation;
import com.intellij.psi.ElementDescriptionProvider;
import com.intellij.psi.PsiElement;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.declarations.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LSFElementDescriptionProvider implements ElementDescriptionProvider {
    @Nullable
    @Override
    public String getElementDescription(@NotNull PsiElement element, @NotNull ElementDescriptionLocation location) {
        String name = null;
        String type = null;
        if (element instanceof LSFClassStatement) {
            LSFClassDecl classDecl = ((LSFClassStatement) element).getClassDecl();
            if (classDecl != null) {
                type = "class";
                name = classDecl.getNameIdentifier().getText();
            }
        } else if (element instanceof LSFFormStatement) {
            LSFFormDecl formDecl = ((LSFFormStatement) element).getFormDecl();
            if (formDecl != null) {
                type = "form";
                name = formDecl.getNameIdentifier().getText();
            }
        } else if (element instanceof LSFWindowStatement) {
            LSFWindowCreateStatement statement = ((LSFWindowStatement) element).getWindowCreateStatement();
            if (statement != null) {
                type = "window";
                name = statement.getNameIdentifier().getText();
            }
        } else if (element instanceof LSFExplicitInterfaceActionOrPropStatement) {
            LSFActionOrPropDeclaration propertyStatement = ((LSFExplicitInterfaceActionOrPropStatement) element).getDeclaration();
            if (propertyStatement != null) {
                type = propertyStatement.isAction()? "action" : "property";
                name = propertyStatement.getNameIdentifier().getText();
            }
        } else if (element instanceof LSFNewComponentStatement) {
            LSFComponentDecl componentDecl = ((LSFNewComponentStatement) element).getComponentDecl();
            if (componentDecl != null) {
                type = "component";
                name = componentDecl.getNameIdentifier().getText();
            }
        } else if (element instanceof LSFTableDeclaration) {
            type = "table";
            name = ((LSFTableDeclaration) element).getNameIdentifier().getText();
        } else if (element instanceof LSFGroupDeclaration) {
            type = "group";
            name = ((LSFGroupDeclaration) element).getNameIdentifier().getText();
        } else if (element instanceof LSFMetaCodeDeclarationStatement) {
            type = "metacode";
            name = ((LSFMetaCodeDeclarationStatement) element).getNameIdentifier().getText();
        } else if (element instanceof LSFNavigatorElementDeclaration) {
            type = "navigator element";
            name = ((LSFNavigatorElementDeclaration) element).getNameIdentifier().getText();
        } else if (element instanceof LSFGroupObjectDeclaration) {
            type = "group object";
            name = ((LSFGroupObjectDeclaration) element).getNameIdentifier().getText();
        } else if (element instanceof LSFPropertyDrawDeclaration) {
            type = "property draw";
            name = ((LSFPropertyDrawDeclaration) element).getNameIdentifier().getText();
        } else if (element instanceof LSFStaticObjectDeclaration) {
            type = "static object";
            name = ((LSFStaticObjectDeclaration) element).getNameIdentifier().getText();
        }
        
        if (location instanceof DeleteTypeDescriptionLocation) {
            return type;
        } else if (location instanceof DeleteNameDescriptionLocation) {
            return name;
        }
        
        return null;
    }
}
