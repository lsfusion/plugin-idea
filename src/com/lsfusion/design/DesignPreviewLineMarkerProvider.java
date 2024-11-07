package com.lsfusion.design;

import com.intellij.codeInsight.daemon.GutterIconNavigationHandler;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.impl.ToolWindowImpl;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Function;
import com.lsfusion.LSFIcons;
import com.lsfusion.LSFLineMarkerProvider;
import com.lsfusion.design.view.DesignView;
import com.lsfusion.design.view.DesignViewFactory;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.declarations.LSFFormDeclaration;
import com.lsfusion.lang.psi.extend.LSFExtend;
import com.lsfusion.lang.psi.impl.LSFFormStatementImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.event.MouseEvent;

public class DesignPreviewLineMarkerProvider extends LSFLineMarkerProvider {

    @Nullable
    @Override
    protected LineMarkerInfo<?> getLSFLineMarkerInfo(@NotNull PsiElement psi) {
        LSFFormDeclaration decl = resolveFormDecl(psi);
        return decl == null ? null : createLineMarker(psi);
    }

    private LineMarkerInfo<?> createLineMarker(PsiElement psi) {
        return new LineMarkerInfo<PsiElement>(psi,
                psi.getTextRange(),
                LSFIcons.Design.DESIGN,
                GetFormNameTooltipProvider.INSTANCE,
                OpenDesignPreviewNavigationHandler.INSTANCE,
                GutterIconRenderer.Alignment.RIGHT,
                () -> ""
        );
    }

    public static LSFFormDeclaration resolveFormDecl(PsiElement psi) {
        return resolveFormDecl(psi, null);
    }
    public static LSFFormDeclaration resolveFormDecl(PsiElement psi, Result<LSFExtend<?, ?>> extend) {
        if (psi instanceof LeafPsiElement && psi.getParent() instanceof LSFSimpleName) {
            LSFFormDeclaration formDeclaration = null;
            LSFId nameIdentifier = null;
            LSFFormStatementImpl formExtend = PsiTreeUtil.getParentOfType(psi, LSFFormStatementImpl.class);
            if (formExtend != null) {
                if(extend != null)
                    extend.setResult(formExtend);
                formDeclaration = formExtend.resolveFormDecl();
                if (formDeclaration != null) {
                    LSFFormDecl formDecl = formExtend.getFormDecl();
                    if (formDecl != null) {
                        nameIdentifier = formDecl.getNameIdentifier();
                    } else {
                        LSFExtendingFormDeclaration extendingFormDeclaration = formExtend.getExtendingFormDeclaration();
                        if (extendingFormDeclaration != null) {
                            LSFFormUsageWrapper formUsageWrapper = extendingFormDeclaration.getFormUsageWrapper();
                            if(formUsageWrapper != null) {
                                nameIdentifier = formUsageWrapper.getFormUsage().getFormUsageNameIdentifier();
                            }
                        }
                    }
                }
            } else {
                LSFDesignStatement designStatement = PsiTreeUtil.getParentOfType(psi, LSFDesignStatement.class);
                if (designStatement != null) {
                    if(extend != null)
                        extend.setResult(designStatement);
                    formDeclaration = designStatement.resolveFormDecl();
                    if (formDeclaration != null) {
                        nameIdentifier = designStatement.getFormUsageNameIdentifier();
                    }
                }
            }
            if (nameIdentifier != null && nameIdentifier.getFirstChild() == psi) {
                return formDeclaration;
            }
        }
        return null;
    }

    private static class GetFormNameTooltipProvider implements Function<PsiElement, String> {
        static GetFormNameTooltipProvider INSTANCE = new GetFormNameTooltipProvider();

        @Override
        public String fun(PsiElement psi) {
            LSFFormDeclaration formDecl = resolveFormDecl(psi);
            if (formDecl != null) {
                return formDecl.getGlobalName();
            }
            return null;
        }
    }

    private static class OpenDesignPreviewNavigationHandler implements GutterIconNavigationHandler {
        static OpenDesignPreviewNavigationHandler INSTANCE = new OpenDesignPreviewNavigationHandler();

        @Override
        public void navigate(MouseEvent e, PsiElement psi) {
            Result<LSFExtend<?, ?>> formExtend = new Result<>();
            LSFFormDeclaration formDecl = resolveFormDecl(psi, formExtend);

            if (formDecl != null) {
                ToolWindow toolWindow = ToolWindowManager.getInstance(formDecl.getProject()).getToolWindow("Design");
                if (toolWindow instanceof ToolWindowImpl) {
                    toolWindow.activate(null);
                }

                Editor editor = FileEditorManager.getInstance(formDecl.getProject()).getSelectedTextEditor();
                if (editor != null) {
                    // переносим курсор, чтобы обновление toolWindow перестало ориентироваться на предыдущее его положение
                    editor.getCaretModel().moveToOffset(psi.getTextOffset());
                }
                
                LSFFile file = null;
                if (formExtend.getResult().getContainingFile() instanceof LSFFile) {
                    file = (LSFFile) formExtend.getResult().getContainingFile();
                }

                DesignViewFactory.getInstance().updateView(new DesignView.TargetForm(formDecl, 
                        ((LSFFile) psi.getContainingFile()).getModuleDeclaration(), 
                        LSFLocalSearchScope.createFrom(formExtend.getResult()),
                        file
                ));
            }
        }
    }
}
