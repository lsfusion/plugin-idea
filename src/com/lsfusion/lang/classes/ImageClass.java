package com.lsfusion.lang.classes;

public class ImageClass extends StaticFormatFileClass {

    public final static ImageClass instance = new ImageClass();

    @Override
    public String getCaption() {
        return "Image";
    }

    @Override
    public String getName() {
        return "IMAGEFILE";
    }
}
