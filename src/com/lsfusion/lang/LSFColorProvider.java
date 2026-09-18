package com.lsfusion.lang;

import com.intellij.openapi.editor.ElementColorProvider;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiElement;
import com.intellij.psi.SmartPointerManager;
import com.intellij.psi.SmartPsiElementPointer;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.lsfusion.lang.psi.LSFColorLiteral;
import com.lsfusion.lang.psi.LSFUintLiteral;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;

// Gutter color preview + picker for '#rrggbb' and 'RGB(r, g, b)' literals; the platform's ColorLineMarkerProvider
// draws the icon, opens the picker and wraps setColorTo in a write action.
public class LSFColorProvider implements ElementColorProvider {

    // The picker applies every change through the element it was opened on. The marker has to sit on a leaf
    // (the '#rrggbb' token or the RGB keyword), and the '#rrggbb' token is what the first write replaces, so the
    // literal is reached through a pointer stored on that leaf rather than through element.getParent().
    private static final Key<SmartPsiElementPointer<LSFColorLiteral>> LITERAL = Key.create("LSF_COLOR_LITERAL");

    // the platform asks about every leaf of every file, so the token check comes first
    @Override
    public @Nullable Color getColorFrom(@NotNull PsiElement element) {
        if (!(element instanceof LeafPsiElement) || !(element.getParent() instanceof LSFColorLiteral literal) || element != literal.getFirstChild()) {
            return null;
        }
        Color color = getColor(literal);
        if (color != null) {
            element.putUserData(LITERAL, SmartPointerManager.createPointer(literal));
        }
        return color;
    }

    private static @Nullable Color getColor(LSFColorLiteral literal) {
        List<LSFUintLiteral> rgb = literal.getUintLiteralList();
        try {
            if (rgb.isEmpty()) {
                return Color.decode(literal.getText());
            }
            return rgb.size() == 3 ? new Color(parse(rgb.get(0)), parse(rgb.get(1)), parse(rgb.get(2))) : null;
        } catch (IllegalArgumentException e) { // RGB(300, 0, 0), or a literal cut short by error recovery
            return null;
        }
    }

    // keeps the form the literal was written in; only the literal's children are replaced, the literal itself
    // (and with it the pointer) survives every write of the picker session
    @Override
    public void setColorTo(@NotNull PsiElement element, @NotNull Color color) {
        SmartPsiElementPointer<LSFColorLiteral> pointer = element.getUserData(LITERAL);
        LSFColorLiteral literal = pointer == null ? null : pointer.getElement();
        if (literal == null) {
            return;
        }
        List<LSFUintLiteral> rgb = literal.getUintLiteralList();
        String text = rgb.isEmpty()
                ? String.format(hasLowerCase(literal.getText()) ? "#%02x%02x%02x" : "#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue())
                : "RGB(" + color.getRed() + ", " + color.getGreen() + ", " + color.getBlue() + ")";
        LSFColorLiteral replacement = LSFElementGenerator.createColorLiteralFromText(literal.getProject(), text);
        if (rgb.isEmpty()) {
            literal.getFirstChild().replace(replacement.getFirstChild()); // the '#rrggbb' token
        } else {
            List<LSFUintLiteral> newRgb = replacement.getUintLiteralList();
            for (int i = 0; i < 3; i++) {
                rgb.get(i).replace(newRgb.get(i));
            }
        }
    }

    private static int parse(LSFUintLiteral literal) {
        return Integer.parseInt(literal.getText());
    }

    // uppercase (the documented spelling) unless the literal itself uses lowercase digits
    private static boolean hasLowerCase(String text) {
        return !text.equals(text.toUpperCase());
    }
}
