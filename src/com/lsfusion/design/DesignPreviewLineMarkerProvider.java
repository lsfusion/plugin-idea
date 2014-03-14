package com.lsfusion.design;

import com.intellij.codeHighlighting.Pass;
import com.intellij.codeInsight.daemon.GutterIconNavigationHandler;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.psi.PsiElement;
import com.intellij.util.Function;
import com.lsfusion.LSFIcons;
import com.lsfusion.design.vfs.LSFDesignVirtualFileImpl;
import com.lsfusion.lang.psi.LSFDesignStatement;
import com.lsfusion.lang.psi.declarations.LSFFormDeclaration;
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
            LSFFormDeclaration formDecl = ((LSFDesignStatement) psi).resolveFormDecl();
            if (formDecl != null) {
                return new LineMarkerInfo(
                        psi,
                        psi.getTextRange().getStartOffset(),
                        LSFIcons.Design.DESIGN,
                        Pass.UPDATE_ALL,
                        GetFormNameFunction.INSTANCE,
                        OpenDesignPreviewNavigationHandler.INSTANCE
                );
            }
        }
        return null;
    }
    
    @Override
    public void collectSlowLineMarkers(@NotNull List<PsiElement> elements, @NotNull Collection<LineMarkerInfo> result) {
    }
    
    private static class GetFormNameFunction implements Function<PsiElement, String> {
        static GetFormNameFunction INSTANCE = new GetFormNameFunction();
        @Override
        public String fun(PsiElement psi) {
            if (psi instanceof LSFDesignStatement) {
                LSFFormDeclaration formDecl = ((LSFDesignStatement) psi).resolveFormDecl();
                if (formDecl != null) {
                    return formDecl.getGlobalName();
                }
            }
            return null;
        }
    }
    
    private static class OpenDesignPreviewNavigationHandler implements GutterIconNavigationHandler {
        static OpenDesignPreviewNavigationHandler INSTANCE = new OpenDesignPreviewNavigationHandler();
        
        @Override
        public void navigate(MouseEvent e, PsiElement psi) {
            if (psi instanceof LSFDesignStatement) {
                LSFFormDeclaration formDecl = ((LSFDesignStatement) psi).resolveFormDecl();
                if (formDecl != null) {
                    DesignInfo designInfo = new DesignInfo(formDecl);
                    LSFDesignVirtualFileImpl file = LSFDesignVirtualFileImpl.create(designInfo);
                    FileEditorManager.getInstance(psi.getProject()).openFile(file, true);
                }
            }
        }
    }
    
}
