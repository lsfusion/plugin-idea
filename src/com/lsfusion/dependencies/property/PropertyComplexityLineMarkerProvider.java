package com.lsfusion.dependencies.property;

import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.ui.JBColor;
import com.intellij.util.Function;
import com.lsfusion.LSFLineMarkerProvider;
import com.lsfusion.actions.ToggleComplexityAction;
import com.lsfusion.lang.psi.LSFId;
import com.lsfusion.lang.psi.LSFSimpleName;
import com.lsfusion.lang.psi.cache.PropertyComplexityCache;
import com.lsfusion.lang.psi.declarations.LSFGlobalPropDeclaration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PropertyComplexityLineMarkerProvider extends LSFLineMarkerProvider {
    @Nullable
    @Override
    protected LineMarkerInfo<?> getLSFLineMarkerInfo(@NotNull PsiElement element) {
        return null;
    }

    @Override
    protected void collectSlowLSFLineMarkers(@NotNull List<? extends PsiElement> elements, @NotNull Collection<? super LineMarkerInfo<?>> result) {
        if (!elements.isEmpty() && !ToggleComplexityAction.isComplexityEnabled(elements.iterator().next().getProject())) {
            return;
        }
        
        Document document = null;
        Set<Integer> usedLines = new HashSet<>();
        for (PsiElement element : elements) {
            if (document == null) {
                document = PsiDocumentManager.getInstance(element.getProject()).getDocument(element.getContainingFile());
            }
            int lineNumber = document.getLineNumber(element.getTextOffset());
            if (!usedLines.contains(lineNumber)) {
                if (element instanceof LeafPsiElement && element.getParent() instanceof LSFSimpleName) {
                    LSFGlobalPropDeclaration<?, ?> propDeclaration = PsiTreeUtil.getParentOfType(element, LSFGlobalPropDeclaration.class);
                    if (propDeclaration != null) {
                        LSFId nameIdentifier = propDeclaration.getNameIdentifier();
                        if (nameIdentifier != null && nameIdentifier.getFirstChild() == element) {
                            if (propDeclaration.isCorrect() && !propDeclaration.isStoredProperty()) {
                                LineMarkerInfo<?> marker = createLineMarker(propDeclaration, element);
                                result.add(marker);
                                usedLines.add(lineNumber);
                            }
                        }
                    }
                }
            }
        }
    }

    private LineMarkerInfo<?> createLineMarker(LSFGlobalPropDeclaration<?, ?> property, PsiElement element) {
        int complexity = PropertyComplexityCache.getInstance(property.getProject()).resolveWithCaching(property);
        return new LineMarkerInfo<>(element,
                element.getTextRange(),
                createIcon(complexity),
                PropertyComplexityTooltipProvider.INSTANCE,
                null,
                GutterIconRenderer.Alignment.RIGHT,
                () -> ""
        );
    }

    private static JBColor markerColor = new JBColor(new Color(114, 174, 108), new Color(99, 146, 97));
    private static Icon createIcon(final int complexity) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                String text = String.valueOf(complexity);
                g.setColor(markerColor);

                g.drawRoundRect(x, y, 12, 12, 2, 2);
                g.setFont(textFont);

                int textWidth = g.getFontMetrics(textFont).stringWidth(text);
                g.drawString(text, x + (12 - textWidth) / 2, y + 11);
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

    private static class PropertyComplexityTooltipProvider implements Function<PsiElement, String> {
        static PropertyComplexityTooltipProvider INSTANCE = new PropertyComplexityTooltipProvider();

        @Override
        public String fun(PsiElement psi) {
            return "Property complexity";
        }
    }
}
