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

    public void setSpan(int span) {
        target.span = span;
    }

    public void setDefaultComponent(boolean defaultComponent) {
        target.defaultComponent = defaultComponent;
    }

    public void setActivated(boolean activated) {
        target.activated = activated;
    }

    /* ========= constraints properties ========= */

    public void setFill(double fill) {
        setFlex(fill);
        setAlignment(fill == 0 ? FlexAlignment.START : FlexAlignment.STRETCH);
    }

    public void setSize(Dimension size) {
        target.size = size;
    }

    public void setHeight(int height) {
        if (target.size == null) {
            target.size = new Dimension(-1, height);
        } else {
            target.size.height = height;
        }
    }

    public void setWidth(int prefWidth) {
        if (target.size == null) {
            target.size = new Dimension(prefWidth, -1);
        } else {
            target.size.width = prefWidth;
        }
    }

    public void setFlex(double flex) {
        target.setFlex(flex);
    }

    public void setShrink(boolean shrink) {
        target.shrink = shrink;
    }

    public void setAlignShrink(boolean alignShrink) {
        target.alignShrink = alignShrink;
    }

    public void setAlign(FlexAlignment alignment) {
        setAlignment(alignment);
    }

    public void setAlignment(FlexAlignment alignment) {
        target.setAlignment(alignment);
    }

    public void setAlignCaption(Boolean alignCaption) {
        target.alignCaption = alignCaption;
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
        target.captionFont = captionFont;
    }

    public void setFont(FontInfo font) {
        target.font = font;
    }

    public void setClass(String elementClass) {
        target.elementClass = elementClass;
    }

    public void setFontSize(int fontSize) {
        FontInfo font = target.font != null ? target.font.derive(fontSize) : new FontInfo(fontSize);
        setFont(font);
    }

    public void setFontStyle(String fontStyle) {
        //no need to parse fontStyle, because this method is never called
        setFont(new FontInfo(false, false));
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

    public void setShowIf(boolean showIf) {
        target.showIf = showIf;
    }
}
