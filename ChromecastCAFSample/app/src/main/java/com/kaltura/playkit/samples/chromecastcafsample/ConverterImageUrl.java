package com.kaltura.playkit.samples.chromecastcafsample;

/**
 * Created by itanbarpeled on 16/12/2016.
 */

public class ConverterImageUrl {


    String URL;
    int width;
    int height;

    public ConverterImageUrl(String URL, int width, int height) {
        this.URL = URL;
        this.width = width;
        this.height = height;
    }

    public int getHeight() {
        return height;
    }

    public String getURL() {
        return URL;
    }

    public int getWidth() {
        return width;
    }

}
