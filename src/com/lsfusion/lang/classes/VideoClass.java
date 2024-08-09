package com.lsfusion.lang.classes;

public class VideoClass extends StaticFormatFileClass {

    public final static VideoClass instance = new VideoClass();

    @Override
    public String getCaption() {
        return "Video file";
    }

    @Override
    public String getName() {
        return "VIDEOFILE";
    }
}