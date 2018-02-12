package com.lsfusion.design.model.proxy;

import com.lsfusion.design.model.ComponentView;
import com.lsfusion.design.model.FontInfo;
import com.lsfusion.design.ui.FlexAlignment;

import java.awt.*;

@SuppressWarnings("unused")
public class ComponentViewProxy<T extends ComponentView> extends ViewProxy<T> {
    public ComponentViewProxy(T target) {
        super(target);
    }

    public void setAutoSize(boolean autoSize) {
        target.autoSize = autoSize;
    }

    public void setDefaultComponent(boolean defaultComponent) {
        target.defaultComponent = defaultComponent;
    }

    /* ========= constraints properties ========= */

    public void setFill(double fill) {
        setFlex(fill);
        setAlignment(fill == 0 ? FlexAlignment.START : FlexAlignment.STRETCH);
    }

    public void setFlex(double flex) {
        target.setFlex(flex);
    }

    public void setAlign(FlexAlignment alignment) {
        setAlignment(alignment);
    }

    public void setAlignment(FlexAlignment alignment) {
        target.setAlignment(alignment);
    }

    public void setMarginTop(int marginTop) {
        target.setMarginTop(marginTop);
    }

    public void setMarginBottom(int marginBottom) {
        target.setMarginBottom(marginBottom);
    }

    public void setMarginLeft(int marginLeft) {
        target.setMarginLeft(marginLeft);
    }

    public void setMarginRight(int marginRight) {
        target.setMarginRight(marginRight);
    }

    public void setMargin(int margin) {
        target.setMargin(margin);
    }

    /* ========= design properties ========= */

    public void setCaptionFont(FontInfo captionFont) {
        target.setCaptionFont(captionFont);
    }

    public void setFont(FontInfo font) {
        target.setFont(font);
    }

    public void setFontSize(int fontSize) {
        FontInfo font = target.font != null ? target.font.derive(fontSize) : new FontInfo(fontSize);

        target.setFont(font);
    }

    public void setFontStyle(String fontStyle) {
        boolean bold;
        boolean italic;
        //чтобы не заморачиваться с лишним типом для стиля просто перечисляем все варианты...
        if ("bold".equals(fontStyle)) {
            bold = true;
            italic = false;
        } else if ("italic".equals(fontStyle)) {
            bold = false;
            italic = true;
        } else if ("bold italic".equals(fontStyle) || "italic bold".equals(fontStyle)) {
            bold = true;
            italic = true;
        } else if ("".equals(fontStyle)) {
            bold = false;
            italic = false;
        } else {
            throw new IllegalArgumentException("fontStyle value must be a combination of strings bold and italic");
        }

        FontInfo font = target.font != null ? target.font.derive(bold, italic) : new FontInfo(bold, italic);

        target.setFont(font);
    }

    public void setBackground(Color background) {
        target.background = background;
    }

    public void setForeground(Color foreground) {
        target.foreground = foreground;
    }

    public void setImagePath(String imagePath) {
        target.setImagePath(imagePath);
    }

    public void setSize(Dimension size) {
        target.setSize(size);
    }
    public void setHeight(int prefHeight) {
        target.setHeight(prefHeight);
    }
    public void setWidth(int prefWidth) {
        target.setWidth(prefWidth);
    }
}
