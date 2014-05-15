package com.lsfusion.design;

import com.intellij.codeHighlighting.Pass;
import com.intellij.codeInsight.daemon.GutterIconNavigationHandler;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.impl.ToolWindowImpl;
import com.intellij.psi.PsiElement;
import com.intellij.util.Function;
import com.lsfusion.LSFIcons;
import com.lsfusion.lang.psi.LSFDesignStatement;
import com.lsfusion.lang.psi.LSFFile;
import com.lsfusion.lang.psi.declarations.LSFFormDeclaration;
import com.lsfusion.lang.psi.extend.LSFFormExtend;
import com.lsfusion.lang.psi.impl.LSFFormStatementImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.List;

public class DesignPreviewLineMarkerProvider implements LineMarkerProvider {

    @Nullable
    @Override
    public LineMarkerInfo getLineMarkerInfo(@NotNull PsiElement psi) {
        if (psi instanceof LSFDesignStatement) {
            if (((LSFDesignStatement) psi).resolveFormDecl() != null) {
                return createLineMarker(psi);
            }
        } else if (psi instanceof LSFFormExtend) {
            if (((LSFFormStatementImpl) psi).resolveFormDecl() != null) {
                return createLineMarker(psi);
            }
        }
        return null;
    }

    private LineMarkerInfo createLineMarker(PsiElement psi) {
        return new LineMarkerInfo(
                psi,
                psi.getTextRange().getStartOffset(),
                LSFIcons.Design.DESIGN,
                Pass.UPDATE_ALL,
                GetFormNameFunction.INSTANCE,
                OpenDesignPreviewNavigationHandler.INSTANCE
        );
    }

    @Override
    public void collectSlowLineMarkers(@NotNull List<PsiElement> elements, @NotNull Collection<LineMarkerInfo> result) {
    }

    private static class GetFormNameFunction implements Function<PsiElement, String> {
        static GetFormNameFunction INSTANCE = new GetFormNameFunction();

        @Override
        public String fun(PsiElement psi) {
            LSFFormDeclaration formDecl = null;
            if (psi instanceof LSFDesignStatement) {
                formDecl = ((LSFDesignStatement) psi).resolveFormDecl();
            } else if (psi instanceof LSFFormExtend) {
                formDecl = ((LSFFormStatementImpl) psi).resolveFormDecl();
            }
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
            LSFFormDeclaration formDecl = null;
            if (psi instanceof LSFDesignStatement) {
                formDecl = ((LSFDesignStatement) psi).resolveFormDecl();
            } else if (psi instanceof LSFFormExtend) {
                formDecl = ((LSFFormStatementImpl) psi).resolveFormDecl();
            }

            if (formDecl != null) {
                ToolWindow toolWindow = ToolWindowManager.getInstance(formDecl.getProject()).getToolWindow("lsFusion Form Design");
                if (toolWindow instanceof ToolWindowImpl) {
                    ((ToolWindowImpl) toolWindow).ensureContentInitialized();
                    toolWindow.activate(null);
                }

                FileEditorManager.getInstance(formDecl.getProject()).getSelectedEditor(psi.getContainingFile().getVirtualFile());
                Editor editor = FileEditorManager.getInstance(formDecl.getProject()).getSelectedTextEditor();
                // переносим курсор, чтобы обновление toolWindow перестало ориентироваться на предыдущее его положение
                editor.getCaretModel().moveToOffset(psi.getTextOffset());

                DesignViewFactory.getInstance().updateView(((LSFFile) psi.getContainingFile()).getModuleDeclaration(), formDecl.getDeclName());
            }
        }
    }

}
