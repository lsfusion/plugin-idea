package com.lsfusion.lang.meta;

import com.intellij.codeHighlighting.Pass;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.ui.Gray;
import com.intellij.ui.JBColor;
import com.intellij.util.Function;
import com.lsfusion.lang.psi.LSFMetaCodeStatement;
import com.lsfusion.lang.psi.LSFSimpleName;
import com.lsfusion.lang.psi.references.LSFMetaReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;
import java.util.List;

public class MetaNestingLineMarkerProvider implements LineMarkerProvider {
    @Nullable
    @Override
    public LineMarkerInfo getLineMarkerInfo(@NotNull PsiElement element) {
        if (element instanceof LeafPsiElement && element.getParent() instanceof LSFSimpleName) {
            LSFMetaCodeStatement metaReference = PsiTreeUtil.getParentOfType(element, LSFMetaCodeStatement.class);
            if (metaReference != null) {
                if (metaReference.getMetaCodeStatementHeader().getMetacodeUsage().getCompoundID().getSimpleName().getFirstChild() == element) {
                    int level = resolveNestingLevel(metaReference);
                    if (level > 0) {
                        return new LineMarkerInfo(element,
                                element.getTextRange(),
                                createIcon(level),
                                Pass.LINE_MARKERS,
                                MetaNestingLevelTooltipProvider.INSTANCE,
                                null,
                                GutterIconRenderer.Alignment.RIGHT);
                    }
                }
            }
        }
        return null;
    }

    private static Icon createIcon(final int level) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                String text = "" + level;
                g.setColor(new JBColor(Gray._160, Gray._90));

                g.drawRoundRect(x, y, 12, 12, 2, 2);
                Font textFont = new Font("Dialog", Font.BOLD, 11);
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

    @Override
    public void collectSlowLineMarkers(@NotNull List<? extends PsiElement> psiElements, @NotNull Collection<? super LineMarkerInfo<?>> lineMarkerInfos) {
    }

    public static int resolveNestingLevel(PsiElement element) {
        LSFMetaReference meta = PsiTreeUtil.getParentOfType(element, LSFMetaReference.class);
        if (meta != null) {
            return 1 + resolveNestingLevel(meta);
        }
        return 0;
    }

    private static class MetaNestingLevelTooltipProvider implements Function<PsiElement, String> {
        static MetaNestingLevelTooltipProvider INSTANCE = new MetaNestingLevelTooltipProvider();

        @Override
        public String fun(PsiElement psi) {
            return "Metacode usage nesting level";
        }
    }
}
