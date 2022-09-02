package com.lsfusion.dependencies.property;

import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.psi.PsiElement;
import com.intellij.ui.JBColor;
import com.lsfusion.LSFLineMarkerInfo;
import com.lsfusion.lang.psi.LSFColorLiteral;
import com.lsfusion.lang.psi.LSFUintLiteral;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PropertyColorLineMarkerProvider implements LineMarkerProvider {
    @Nullable
    @Override
    public LineMarkerInfo<?> getLineMarkerInfo(@NotNull PsiElement element) {

        if(element instanceof LSFColorLiteral) {
            List<LSFUintLiteral> rgb = ((LSFColorLiteral) element).getUintLiteralList();
            if(!rgb.isEmpty()) {
                int red = Integer.parseInt(rgb.get(0).getText());
                int green = Integer.parseInt(rgb.get(1).getText());
                int blue = Integer.parseInt(rgb.get(2).getText());
                return createLineMarker(element, red, green, blue);
            } else {
                Color c = Color.decode(element.getText());
                return createLineMarker(element, c.getRed(), c.getGreen(), c.getBlue());
            }
        }
        return null;
    }

    private LineMarkerInfo<?> createLineMarker(PsiElement psi, int red, int green, int blue) {
        return LSFLineMarkerInfo.create(psi, psi.getTextRange(), createIcon(red, green, blue), null, null, GutterIconRenderer.Alignment.RIGHT);
    }

    private static Icon createIcon(int red, int green, int blue) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                g.setColor(new JBColor(new Color(red, green, blue), new Color(red, green, blue)));
                g.fillRect(x, y, 12, 12);
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
}
