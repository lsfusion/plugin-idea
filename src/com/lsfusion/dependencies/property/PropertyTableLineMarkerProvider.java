package com.lsfusion.dependencies.property;

import com.intellij.codeHighlighting.Pass;
import com.intellij.codeInsight.daemon.GutterIconNavigationHandler;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.ui.JBColor;
import com.intellij.util.Function;
import com.lsfusion.actions.ToggleShowTableAction;
import com.lsfusion.lang.psi.LSFPropertyStatement;
import com.lsfusion.lang.psi.declarations.LSFTableDeclaration;
import com.lsfusion.lang.psi.declarations.impl.LSFTableDeclarationImpl;
import com.lsfusion.lang.psi.indexes.TableIndex;
import com.lsfusion.util.LSFFileUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
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
                psi.getTextRange(),
                createIcon(),
                Pass.UPDATE_OVERRIDDEN_MARKERS,
                PropertyShowTableTooltipProvider.INSTANCE,
                ShowTableNavigationProvider.INSTANCE,
                GutterIconRenderer.Alignment.RIGHT
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
    
    private static class ShowTableNavigationProvider implements GutterIconNavigationHandler {
        static ShowTableNavigationProvider INSTANCE = new ShowTableNavigationProvider();

        @Override
        public void navigate(MouseEvent e, PsiElement psi) {
            if (e.getID() == MouseEvent.MOUSE_RELEASED) {
                String name = psi instanceof LSFPropertyStatement ? ((LSFPropertyStatement) psi).getTableName() : null;
                if (name != null) {
                    int underscoreIndex = name.indexOf("_");
                    if (underscoreIndex != -1) {
                        String namespace = name.substring(0, underscoreIndex);
                        String tableName = name.substring(underscoreIndex + 1);
                        Collection<LSFTableDeclaration> tableDeclarations = TableIndex.getInstance().get(tableName, psi.getProject(), LSFFileUtils.getModuleWithDependenciesScope(psi));
                        for (LSFTableDeclaration declaration : tableDeclarations) {
                            if (declaration.getNamespaceName().equals(namespace)) {
                                ((LSFTableDeclarationImpl) declaration).navigate(true);
                                return;
                            }
                        }
                    }
                }
            }
        }
    }
}
