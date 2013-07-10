package com.simpleplugin.psi;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveState;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.ContainerUtil;
import com.simpleplugin.psi.impl.LSFSimpleNameWithCaptionImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
        import java.util.Collections;
import java.util.List;

public class LSFResolver implements ResolveCache.AbstractResolver<LSFElementReference, List<? extends PsiElement>> {
    public static final LSFResolver INSTANCE = new LSFResolver();

    @Nullable
    @Override
    public List<? extends PsiElement> resolve(@NotNull LSFElementReference reference, boolean incompleteCode) {

        // global
        List<PsiElement> result = new ArrayList<PsiElement>();

        String refText = reference.getText();

        PsiFile psiFile = reference.getContainingFile();
        PsiElement[] declarations = psiFile.getChildren();//
        for(PsiElement declaration : declarations) {
            if(reference instanceof LSFClassReference) {
                if(declaration instanceof LSFClassStatement) {
                    LSFClassStatement classStatement = (LSFClassStatement) declaration;
                    LSFSimpleNameWithCaption decl = classStatement.getSimpleNameWithCaption();
                    if(refText.equals(decl.getCompoundID().getText()))
                        result.add(decl);
                }
            } else if(reference instanceof LSFPropReference) {
                if(declaration instanceof LSFPropertyStatement) {
                    LSFPropertyStatement propStatement = (LSFPropertyStatement) declaration;
                    LSFSimpleNameWithCaption decl = propStatement.getPropertyDeclaration().getSimpleNameWithCaption();
                    if(refText.equals(decl.getCompoundID().getText()))
                        result.add(decl);
                }
            }
        }
        return result;
    }

    public List<LSFSimpleNameWithCaption> getStatements(PsiFile psiFile, boolean classes) {
        List<LSFSimpleNameWithCaption> result = new ArrayList<LSFSimpleNameWithCaption>();
        PsiElement[] declarations = psiFile.getChildren();//
        for(PsiElement declaration : declarations) {
            if(classes) {
                if(declaration instanceof LSFClassStatement) {
                    LSFClassStatement classStatement = (LSFClassStatement) declaration;
                    result.add(classStatement.getSimpleNameWithCaption());
                }
            } else
            if(declaration instanceof LSFPropertyStatement) {
                LSFPropertyStatement propStatement = (LSFPropertyStatement) declaration;
                LSFSimpleNameWithCaption decl = propStatement.getPropertyDeclaration().getSimpleNameWithCaption();
                result.add(decl);
            }
        }
        return result;
    }
}

