package com.lsfusion.lang.classes.link;

public class ImageLinkClass extends StaticFormatLinkClass {

    public final static ImageLinkClass instance = new ImageLinkClass();

    @Override
    public String getCaption() {
        return "Image link";
    }

    @Override
    public String getName() {
        return "IMAGELINK";
    }
}