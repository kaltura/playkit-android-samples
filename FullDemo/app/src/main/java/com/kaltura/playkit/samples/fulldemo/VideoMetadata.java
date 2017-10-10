package com.kaltura.playkit.samples.fulldemo;

import java.util.Arrays;
import java.util.List;

import static com.kaltura.playkit.samples.fulldemo.MainActivity.AD_1;
import static com.kaltura.playkit.samples.fulldemo.MainActivity.AD_2;
import static com.kaltura.playkit.samples.fulldemo.MainActivity.AD_3;
import static com.kaltura.playkit.samples.fulldemo.MainActivity.AD_4;
import static com.kaltura.playkit.samples.fulldemo.MainActivity.AD_5;
import static com.kaltura.playkit.samples.fulldemo.MainActivity.AD_6;
import static com.kaltura.playkit.samples.fulldemo.MainActivity.AD_7;
import static com.kaltura.playkit.samples.fulldemo.MainActivity.AD_8;
import static com.kaltura.playkit.samples.fulldemo.MainActivity.AD_GOOGLE_SEARCH;
import static com.kaltura.playkit.samples.fulldemo.MainActivity.AD_SENSE;
import static com.kaltura.playkit.samples.fulldemo.MainActivity.LIC_URL1;
import static com.kaltura.playkit.samples.fulldemo.MainActivity.SOURCE_URL1;


public enum VideoMetadata {
    CUSTOM(
            SOURCE_URL1,
            LIC_URL1,
            "Custom Ad Tag ",
            "custom",
            R.drawable.k_image),

    PRE_ROLL_NO_SKIP(
            SOURCE_URL1,
            LIC_URL1,
            "Pre-roll, linear not skippable",
            AD_1,
            R.drawable.k_image),

    PRE_ROLL_SKIP(
            SOURCE_URL1,
            LIC_URL1,
            "Pre-roll, linear, skippable",
            AD_2,
            R.drawable.k_image),
    VMAP(
            SOURCE_URL1,
            LIC_URL1,
            "VMAP",
            AD_4,
            R.drawable.k_image),

    VMAP_PODS(SOURCE_URL1,
            LIC_URL1,
            "VMAP Pods",
            AD_5,
            R.drawable.k_image),

    WRAPPER(
            SOURCE_URL1,
            LIC_URL1,
            "Wrapper",
            AD_6,
            R.drawable.k_image),

    VMAP_PODS_BUMP1(SOURCE_URL1,
            LIC_URL1,
            "VMAP Pods Bump",
            AD_7,
            R.drawable.k_image),

    VMAP_PODS_BUMP2(SOURCE_URL1,
            LIC_URL1,
            "VMAP Pods Bump every 10 sec",
            AD_8,
            R.drawable.k_image),

    ADSENSE(SOURCE_URL1,
            LIC_URL1,
            "AdSense",
            AD_SENSE,
            R.drawable.k_image),

    GOOGLE_SEARCH(SOURCE_URL1,
            LIC_URL1,
            "Google Search",
            AD_GOOGLE_SEARCH,
            R.drawable.k_image),
    POST_ROLL(
            SOURCE_URL1,
            LIC_URL1,
            "Post-roll",
            AD_3,
            R.drawable.k_image);


    public static final List<VideoMetadata> APP_VIDEOS =
            Arrays.asList(CUSTOM, PRE_ROLL_NO_SKIP, PRE_ROLL_SKIP, POST_ROLL, VMAP, VMAP_PODS, WRAPPER, VMAP_PODS_BUMP1, VMAP_PODS_BUMP2, GOOGLE_SEARCH, ADSENSE);

    /** The thumbnail image for the video. **/
    public final int thumbnail;

    /** The title of the video. **/
    public final String title;

    /** The URL for the video. **/
    public final String videoUrl;

    /** The LICENSE for the video. **/
    public final String videoLic;

    /** The ad tag for the video **/
    public final String adTagUrl;

    private VideoMetadata(String videoUrl, String videoLic, String title, String adTagUrl, int thumbnail) {
        this.videoUrl = videoUrl;
        this.videoLic = videoLic;
        this.title = title;
        this.adTagUrl = adTagUrl;
        this.thumbnail = thumbnail;
    }
}