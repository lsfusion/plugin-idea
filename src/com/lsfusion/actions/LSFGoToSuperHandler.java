package com.lsfusion.actions;

import com.intellij.codeInsight.navigation.NavigationUtil;
import com.intellij.ide.util.DefaultPsiElementCellRenderer;
import com.intellij.lang.LanguageCodeInsightActionHandler;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import com.lsfusion.lang.LSFLanguage;
import com.lsfusion.lang.meta.MetaChangeDetector;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.declarations.LSFFormDeclaration;
import com.lsfusion.util.LSFPsiUtils;
import org.jetbrains.annotations.NotNull;

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
            List<PsiElement> targets = new ArrayList<PsiElement>();

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
                } else if (statement instanceof LSFDesignStatement && ((LSFDesignStatement) statement).getExtendDesignDeclaration() != null) {
                    LSFFormDeclaration formDecl = ((LSFDesignStatement) statement).resolveFormDecl();
                    if (formDecl != null) {
                        for (PsiReference reference : ReferencesSearch.search(formDecl.getNameIdentifier()).findAll()) {
                            PsiElement st = LSFPsiUtils.getStatementParent(reference.getElement());
                            if (st instanceof LSFDesignStatement && ((LSFDesignStatement) st).getExtendDesignDeclaration() == null) {
                                targets.add(((LSFDesignStatement) st).getDesignDeclaration().getFormUsage());
                                break;
                            }
                        }
                    }
                } else if (statement instanceof LSFClassStatement && ((LSFClassStatement) statement).getExtendingClassDeclaration() != null) {
                    targets.add(((LSFClassStatement) statement).resolveDecl().getNameIdentifier());
                } else if (statement instanceof LSFOverrideStatement) {
                    targets.add(((LSFOverrideStatement) statement).getMappedPropertyClassParamDeclare().getPropertyUsageWrapper().getPropertyUsage().resolveDecl());
                }
            }


            if (targets.size() == 1) {
                ((Navigatable) targets.get(0)).navigate(true);
            } else if (!targets.isEmpty()) {
                NavigationUtil.getPsiElementPopup(targets.toArray(new PsiElement[targets.size()]), new GoToSuperElementRenderer(mapped), "Choose Element").showInBestPositionFor(editor);
            }
        }
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }

    class GoToSuperElementRenderer extends DefaultPsiElementCellRenderer {
        private PsiElement metaElement;

        public GoToSuperElementRenderer(PsiElement metaElement) {
            this.metaElement = metaElement;
        }

        @Override
        public String getElementText(PsiElement element) {
            if (metaElement == element) {
                LSFMetaCodeDeclarationStatement metaDecl = PsiTreeUtil.getParentOfType(element, LSFMetaCodeDeclarationStatement.class);
                if (metaDecl != null) {
                    return super.getElementText(metaDecl.getNameIdentifier());
                }
            }
            return super.getElementText(element);
        }

        @Override
        public String getContainerText(PsiElement element, String name) {
            return LSFPsiUtils.getLocationString(element);
        }
    }
}
