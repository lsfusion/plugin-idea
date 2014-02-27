package com.lsfusion.actions;

import com.intellij.codeInsight.TargetElementUtilBase;
import com.intellij.codeInsight.navigation.GotoImplementationHandler;
import com.intellij.codeInsight.navigation.ImplementationSearcher;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.lsfusion.lang.psi.LSFId;
import com.lsfusion.lang.psi.declarations.LSFDeclaration;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;

public class GoToExtensionHandler extends GotoImplementationHandler {
    @Nullable
    @Override
    public GotoData getSourceAndTargetElements(Editor editor, PsiFile file) {
        PsiElement sourceElement = TargetElementUtilBase.findTargetElement(editor, ImplementationSearcher.getFlags());
        if (sourceElement == null) {
            return null;
        }

        if (sourceElement instanceof LSFId) {

            PsiElement nearestExtendableParent = sourceElement;
            while (nearestExtendableParent.getParent() != null) {
                if (nearestExtendableParent instanceof LSFDeclaration) {
                    break;
                }
                nearestExtendableParent = nearestExtendableParent.getParent();
            }

            return nearestExtendableParent.getParent() == null ? null : new GotoData(sourceElement, ((LSFDeclaration) nearestExtendableParent).processExtensionsSearch(), Collections.<AdditionalAction>emptyList());
        }
        return null;
    }

    @Override
    protected String getChooserTitle(PsiElement sourceElement, String name, int length) {
        return String.format("<html><body>Choose Extension of <b>%s</b> (%s found)</body></html>", name, length);
    }

    @Override
    protected String getFindUsagesTitle(PsiElement sourceElement, String name, int length) {
        return String.format("Extensions of %s", name);
    }

    @Override
    protected String getNotFoundMessage(Project project, Editor editor, PsiFile file) {
        return "No extensions found";
    }
}
