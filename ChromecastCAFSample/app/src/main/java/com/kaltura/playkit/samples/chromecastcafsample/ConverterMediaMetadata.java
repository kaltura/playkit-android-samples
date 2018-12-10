package com.kaltura.playkit.samples.chromecastcafsample;

/**
 * Created by itanbarpeled on 16/12/2016.
 */

public class ConverterMediaMetadata  {


    String title;
    String subtitle;
    ConverterImageUrl imageUrl;

    public ConverterMediaMetadata(String title, String subtitle, ConverterImageUrl imageUrl) {
        this.title = title;
        this.subtitle = subtitle;
        this.imageUrl = imageUrl;
    }

    public ConverterImageUrl getImageUrl() {
        return imageUrl;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getTitle() {
        return title;
    }
}
