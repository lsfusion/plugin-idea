package com.lsfusion.design.model;

import java.awt.*;
import java.io.Serializable;

public final class FontInfo implements Serializable {
    public static final Integer DEFAULT_FONT_SIZE = 11;
    
    public final String fontFamily;
    public final int fontSize;
    public final boolean bold;
    public final boolean italic;

    public FontInfo(String fontFamily) {
        this(fontFamily, 0, false, false);
    }

    public FontInfo(int fontSize) {
        this(null, fontSize, false, false);
    }

    public FontInfo(boolean bold, boolean italic) {
        this(null, 0, bold, italic);
    }

    public FontInfo(String fontFamily, int fontSize, boolean bold, boolean italic) {
        this.fontFamily = fontFamily;
        this.fontSize = fontSize;
        this.bold = bold;
        this.italic = italic;
    }

    public String getFontFamily() {
        return fontFamily;
    }

    public int getFontSize() {
        return fontSize;
    }

    public boolean isBold() {
        return bold;
    }

    public boolean isItalic() {
        return italic;
    }

    public FontInfo derive(boolean bold, boolean italic) {
        return new FontInfo(fontFamily, fontSize, bold, italic);
    }

    public FontInfo derive(int fontSize) {
        return new FontInfo(fontFamily, fontSize, bold, italic);
    }

    public int getStyle() {
        return (bold ? Font.BOLD : Font.PLAIN) | (italic ? Font.ITALIC : Font.PLAIN);
    }

    @Override
    public String toString() {
        return (fontFamily != null ? fontFamily : "") +
               (bold ? " bold" : "") +
               (italic ? " italic" : "") +
               (fontSize > 0 ? " size:" + fontSize : "");
    }
}
