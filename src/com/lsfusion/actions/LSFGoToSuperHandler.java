package com.lsfusion.actions;

import com.intellij.codeInsight.navigation.NavigationUtil;
import com.intellij.codeInsight.navigation.impl.PsiTargetPresentationRenderer;
import com.intellij.lang.LanguageCodeInsightActionHandler;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.lsfusion.lang.LSFLanguage;
import com.lsfusion.lang.meta.MetaChangeDetector;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.declarations.LSFActionDeclaration;
import com.lsfusion.lang.psi.declarations.LSFFormDeclaration;
import com.lsfusion.lang.psi.declarations.LSFPropDeclaration;
import com.lsfusion.util.LSFPsiUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LSFGoToSuperHandler implements LanguageCodeInsightActionHandler {
    @Override
    public boolean isValidFor(Editor editor, PsiFile file) {
        return file.getLanguage() == LSFLanguage.INSTANCE;
    }

    @Override
    public void invoke(@NotNull Project project, @NotNull Editor editor, @NotNull PsiFile file) {
        final PsiElement at = file.findElementAt(editor.getCaretModel().getOffset());
        if (at != null) {
            List<PsiElement> targets = new ArrayList<>();

            // metacode
            LeafPsiElement mapped = (LeafPsiElement) MetaChangeDetector.mapOffset(at);
            if (mapped != null) {
                targets.add(mapped);
            }

            // declaration
            PsiElement statement = LSFPsiUtils.getStatementParent(at);
            if (statement != null) {
                if (statement instanceof LSFFormStatement && ((LSFFormStatement) statement).getExtendingFormDeclaration() != null) {
                    LSFFormDeclaration formDecl = ((LSFFormStatement) statement).resolveFormDecl();
                    if (formDecl != null) {
                        targets.add(formDecl.getNameIdentifier());
                    }
                } else if (statement instanceof LSFClassStatement && ((LSFClassStatement) statement).getExtendingClassDeclaration() != null) {
                    LSFId statementId = ((LSFClassStatement) statement).resolveDecl() != null ? ((LSFClassStatement) statement).resolveDecl().getNameIdentifier() : null;
                    if (statementId != null) {
                        targets.add(statementId);
                    }
                } else if (statement instanceof LSFOverridePropertyStatement) {
                    LSFPropDeclaration propertyDecl = ((LSFOverridePropertyStatement) statement).getMappedPropertyClassParamDeclare().getPropertyUsageWrapper().getPropertyUsage().resolveDecl();
                    if (propertyDecl != null) {
                        targets.add(propertyDecl);
                    }
                } else if (statement instanceof LSFOverrideActionStatement) {
                    LSFActionDeclaration propertyDecl = ((LSFOverrideActionStatement) statement).getMappedActionClassParamDeclare().getActionUsageWrapper().getActionUsage().resolveDecl();
                    if (propertyDecl != null) {
                        targets.add(propertyDecl);
                    }
                }
            }


            if (targets.size() == 1) {
                ((Navigatable) targets.get(0)).navigate(true);
            } else if (!targets.isEmpty()) {
                NavigationUtil.getPsiElementPopup(() -> targets, new GoToSuperPresentationRenderer(mapped), "Choose Element", project).showInBestPositionFor(editor);
            }
        }
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }

    class GoToSuperPresentationRenderer extends PsiTargetPresentationRenderer {
        private PsiElement metaElement;

        public GoToSuperPresentationRenderer(PsiElement metaElement) {
            this.metaElement = metaElement;
        }

        @Override
        public @Nls
        @NotNull String getElementText(@NotNull PsiElement element) {
            if (metaElement == element) {
                LSFMetaCodeDeclarationStatement metaDecl = PsiTreeUtil.getParentOfType(element, LSFMetaCodeDeclarationStatement.class);
                if (metaDecl != null) {
                    return super.getElementText(metaDecl.getNameIdentifier());
                }
            }
            return super.getElementText(element);
        }

        @Override
        public @Nls @Nullable String getContainerText(@NotNull PsiElement element) {
            return LSFPsiUtils.getLocationString(element);
        }
    }
}
