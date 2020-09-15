package com.lsfusion.reports;

import com.intellij.codeHighlighting.Pass;
import com.intellij.codeInsight.daemon.GutterIconNavigationHandler;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.ListPopupStep;
import com.intellij.openapi.ui.popup.PopupStep;
import com.intellij.openapi.ui.popup.util.BaseListPopupStep;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.util.Function;
import com.lsfusion.LSFIcons;
import com.lsfusion.lang.psi.declarations.LSFFormDeclaration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.lsfusion.design.DesignPreviewLineMarkerProvider.resolveFormDecl;
import static com.lsfusion.reports.ReportUtils.hasReportFiles;
import static com.lsfusion.util.LSFFileUtils.getFileRelativePath;

public class JrxmlLinkLineMarkerProvider implements LineMarkerProvider {

    @Nullable
    @Override
    public LineMarkerInfo<?> getLineMarkerInfo(@NotNull PsiElement psi) {
        LSFFormDeclaration decl = resolveFormDecl(psi);

        if (decl != null && hasReportFiles(decl)) {
            return createLineMarker(psi);
        }
        
        return null;
    }

    private LineMarkerInfo<?> createLineMarker(PsiElement psi) {
        return new LineMarkerInfo(
                psi,
                psi.getTextRange(),
                LSFIcons.PRINT,
                Pass.LINE_MARKERS,
                TooltipProvider.INSTANCE,
                GotoJrxmlFileNavigationHandler.INSTANCE,
                GutterIconRenderer.Alignment.RIGHT
        );
    }

    @Override
    public void collectSlowLineMarkers(@NotNull List<? extends PsiElement> elements, @NotNull Collection<? super LineMarkerInfo<?>> result) {
    }

    private static class TooltipProvider implements Function<PsiElement, String> {
        static TooltipProvider INSTANCE = new TooltipProvider();

        @Override
        public String fun(PsiElement psi) {
            LSFFormDeclaration formDecl = resolveFormDecl(psi);
            if (formDecl != null) {
                return "Navigate to corresponding report file for " + formDecl.getGlobalName();
            }
            return null;
        }
    }

    private static class PsiFileByPathComparator implements Comparator<PsiFile> {
        public static PsiFileByPathComparator INSTANCE = new PsiFileByPathComparator();

        @Override
        public int compare(PsiFile o1, PsiFile o2) {
            return getFileRelativePath(o1).compareTo(getFileRelativePath(o2));
        }
    }

    private static class GotoJrxmlFileNavigationHandler implements GutterIconNavigationHandler {
        public static GotoJrxmlFileNavigationHandler INSTANCE = new GotoJrxmlFileNavigationHandler();

        @Override
        public void navigate(MouseEvent e, PsiElement psi) {
            LSFFormDeclaration formDecl = resolveFormDecl(psi);

            if (formDecl != null) {
                List<PsiFile> files = ReportUtils.findReportFiles(formDecl);

                try {
                    for (PsiFile file : files) {
                        openFile(file);
                    }
                } catch (Exception ex) {
                    if (files.size() < 1) {
                        JBPopupFactory
                                .getInstance()
                                .createMessage("Can't find related report files")
                                .show(new RelativePoint(e));
                    } else if (files.size() == 1) {
                        files.get(0).navigate(true);
                    } else {
                        Collections.sort(files, PsiFileByPathComparator.INSTANCE);

                        JBPopupFactory
                                .getInstance()
                                .createListPopup(createListPopupStep(files))
                                .show(new RelativePoint(e));
                    }    
                }
            }
        }
        
        private void openFile(PsiFile file) throws Exception {
            String path = file.getVirtualFile().getCanonicalPath();
            File fileInPath = new File(path);
            Desktop.getDesktop().open(fileInPath);
        }

        private ListPopupStep createListPopupStep(final List<PsiFile> files) {
            return new BaseListPopupStep<PsiFile>("Choose .jrxml file to navigate to...", files) {
                @NotNull
                @Override
                public String getTextFor(PsiFile file) {
                    return getFileRelativePath(file);
                }

                @Override
                public Icon getIconFor(PsiFile aValue) {
                    return aValue.getIcon(0);
                }

                @Override
                public PopupStep onChosen(PsiFile selectedFile, boolean finalChoice) {
                    selectedFile.navigate(true);
                    return FINAL_CHOICE;
                }
            };
        }
    }
}
