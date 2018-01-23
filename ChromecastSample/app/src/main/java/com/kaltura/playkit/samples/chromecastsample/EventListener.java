package com.kaltura.playkit.samples.chromecastsample;

import com.google.android.gms.cast.MediaInfo;

public interface EventListener {
    void onInitMediaInfo(MediaInfo info);
}