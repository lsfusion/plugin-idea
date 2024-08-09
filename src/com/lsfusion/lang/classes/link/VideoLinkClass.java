package com.lsfusion.lang.classes.link;

public class VideoLinkClass extends StaticFormatLinkClass {

    public final static VideoLinkClass instance = new VideoLinkClass();

    @Override
    public String getCaption() {
        return "Video link";
    }

    @Override
    public String getName() {
        return "VIDEOLINK";
    }
}