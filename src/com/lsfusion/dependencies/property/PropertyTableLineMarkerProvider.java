package com.lsfusion.dependencies.property;

import com.intellij.codeHighlighting.Pass;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.openapi.editor.Document;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.ui.JBColor;
import com.intellij.util.Function;
import com.lsfusion.actions.ToggleShowTableAction;
import com.lsfusion.lang.psi.LSFPropertyStatement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PropertyTableLineMarkerProvider implements LineMarkerProvider {
    @Nullable
    @Override
    public LineMarkerInfo getLineMarkerInfo(@NotNull PsiElement element) {
        return null;
    }

    @Override
    public void collectSlowLineMarkers(@NotNull List<PsiElement> elements, @NotNull Collection<LineMarkerInfo> result) {
        if (!elements.isEmpty() && !ToggleShowTableAction.isShowTableEnabled(elements.iterator().next().getProject())) {
            return;
        }
        
        Document document = null;
        Set<Integer> usedLines = new HashSet<>();
        for (PsiElement element : elements) {
            if (document == null) {
                document = PsiDocumentManager.getInstance(element.getProject()).getDocument(element.getContainingFile());
            }
            if (element instanceof LSFPropertyStatement && ((LSFPropertyStatement) element).isCorrect() && (((LSFPropertyStatement) element).isDataStoredProperty() || ((LSFPropertyStatement) element).isPersistentProperty())) {
                int lineNumber = document.getLineNumber(element.getTextOffset());
                if (!usedLines.contains(lineNumber)) {
                    result.add(createLineMarker((LSFPropertyStatement) element));
                    usedLines.add(lineNumber);
                }
            }
        }
    }

    private LineMarkerInfo createLineMarker(LSFPropertyStatement psi) {
        return new LineMarkerInfo(
                psi,
                psi.getTextRange().getStartOffset(),
                createIcon(),
                Pass.UPDATE_OVERRIDEN_MARKERS,
                PropertyShowTableTooltipProvider.INSTANCE,
                null
        );
    }

    private static Icon createIcon() {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                String text = "T";
                g.setColor(new JBColor(new Color(255, 153, 0), new Color(255, 153, 0)));

                g.drawRoundRect(x, y, 12, 12, 2, 2);
                Font textFont = new Font("Dialog", Font.BOLD, 11);
                g.setFont(textFont);

                int textWidth = g.getFontMetrics(textFont).stringWidth(text);
                g.drawString(text, x + (14 - textWidth) / 2, y + 11);
            }

            @Override
            public int getIconWidth() {
                return 12;
            }

            @Override
            public int getIconHeight() {
                return 12;
            }
        };
    }

    private static class PropertyShowTableTooltipProvider implements Function<PsiElement, String> {
        static PropertyShowTableTooltipProvider INSTANCE = new PropertyShowTableTooltipProvider();

        @Override
        public String fun(PsiElement psi) {
            String name = psi instanceof LSFPropertyStatement ? ((LSFPropertyStatement) psi).getTableName() : null;
            return name == null ? "Unknown table" : ("Table: " + name);
        }
    }
}
