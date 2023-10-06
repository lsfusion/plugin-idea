package com.lsfusion.dependencies.property;

import com.intellij.codeInsight.daemon.GutterIconNavigationHandler;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.psi.PsiElement;
import com.intellij.ui.JBColor;
import com.lsfusion.LSFLineMarkerProvider;
import com.lsfusion.lang.LSFElementGenerator;
import com.lsfusion.lang.psi.LSFColorLiteral;
import com.lsfusion.lang.psi.LSFUintLiteral;
import com.lsfusion.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.List;

public class PropertyColorLineMarkerProvider extends LSFLineMarkerProvider {
    @Nullable
    @Override
    protected LineMarkerInfo<?> getLSFLineMarkerInfo(@NotNull PsiElement element) {
        Pair<Color, Boolean> color = getColor(element);
        if(color != null)  {
            return createLineMarker(element.getFirstChild(), color.first);
        }
        return null;
    }

    private LineMarkerInfo<?> createLineMarker(PsiElement psi, Color color) {
        return new LineMarkerInfo<>(psi,
                psi.getTextRange(),
                createIcon(color),
                null,
                ShowTableNavigationProvider.INSTANCE,
                GutterIconRenderer.Alignment.LEFT,
                () -> ""
        );
    }

    private static Icon createIcon(Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                g.setColor(new JBColor(color, color));
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

    private static class ShowTableNavigationProvider implements GutterIconNavigationHandler<PsiElement> {
        static ShowTableNavigationProvider INSTANCE = new ShowTableNavigationProvider();

        @Override
        public void navigate(MouseEvent e, PsiElement element) {
            if (e.getID() == MouseEvent.MOUSE_RELEASED) {
                PsiElement colorLiteral = element.getParent();
                Pair<Color, Boolean> color = getColor(colorLiteral);
                if (color != null) {
                    Color newColor = JColorChooser.showDialog(null, "Choose a color", color.first);
                    if (newColor != null) {
                        List<LSFUintLiteral> rgb = ((LSFColorLiteral) colorLiteral).getUintLiteralList();
                        String replaceText;
                        if (!rgb.isEmpty()) {
                            replaceText = "RGB(" + newColor.getRed() + ", " + newColor.getGreen() + ", " + newColor.getBlue() + ")";
                        } else {
                            replaceText = String.format("#%02x%02x%02x", newColor.getRed(), newColor.getGreen(), newColor.getBlue());
                        }
                        ApplicationManager.getApplication().runWriteAction(() -> {
                            colorLiteral.replace(LSFElementGenerator.createColorLiteralFromText(colorLiteral.getProject(), replaceText));
                        });
                    }
                }
            }
        }
    }

    private static Pair<Color, Boolean> getColor(PsiElement element) {
        if(element instanceof LSFColorLiteral) {
            List<LSFUintLiteral> rgb = ((LSFColorLiteral) element).getUintLiteralList();
            if(!rgb.isEmpty()) {
                int red = Integer.parseInt(rgb.get(0).getText());
                int green = Integer.parseInt(rgb.get(1).getText());
                int blue = Integer.parseInt(rgb.get(2).getText());
                return new Pair<>(new Color(red, green, blue), true);
            } else {
                return new Pair<>(Color.decode(element.getText()), false);
            }
        }
        return null;
    }
}
