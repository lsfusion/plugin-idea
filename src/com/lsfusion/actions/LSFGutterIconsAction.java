package com.lsfusion.actions;

import com.intellij.application.options.editor.GutterIconsConfigurable;
import com.intellij.icons.AllIcons;
import com.intellij.ide.actions.ShowSettingsUtilImpl;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.function.Supplier;

public class LSFGutterIconsAction extends AnAction {
    public LSFGutterIconsAction() {
        getTemplatePresentation().setIcon(AllIcons.General.Settings);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        ShowSettingsUtilImpl.showSettingsDialog(e.getProject(), GutterIconsConfigurable.ID, "lsFusion");
    }

    private static final Font TEXT_FONT = new Font("Dialog", Font.BOLD, 11);

    public static Icon createFilledSquareIcon(Color color) {
        return new LSFFixedSizeIcon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                g.setColor(color);
                g.fillRect(x, y, LSFFixedSizeIcon.SIZE, LSFFixedSizeIcon.SIZE);
            }
        };
    }

    public static Icon createTextBadgeIcon(Color color, Supplier<String> textSupplier, int horizontalOffset) {
        return new LSFFixedSizeIcon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                String text = textSupplier.get();
                g.setColor(color);
                g.drawRoundRect(x, y, LSFFixedSizeIcon.SIZE, LSFFixedSizeIcon.SIZE, 2, 2);
                g.setFont(TEXT_FONT);

                int textWidth = g.getFontMetrics(TEXT_FONT).stringWidth(text);
                g.drawString(text, x + (LSFFixedSizeIcon.SIZE + horizontalOffset - textWidth) / 2, y + 11);
            }
        };
    }

    private static abstract class LSFFixedSizeIcon implements Icon {
        static final int SIZE = 12;

        @Override
        public final int getIconWidth() {
            return SIZE;
        }

        @Override
        public final int getIconHeight() {
            return SIZE;
        }
    }
}
