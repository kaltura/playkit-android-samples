package com.kaltura.playkit.samples.tracksselection;

import com.kaltura.playkit.PlayerEvent;

/**
 * Created by root on 12/15/17.
 */

public interface EventListener {
    void onTracksAvailable(PlayerEvent.TracksAvailable tracksAvailable);
    void onVideoTrackChanged(PlayerEvent.VideoTrackChanged videoTrackChanged);
    void onAudioTrackChanged(PlayerEvent.AudioTrackChanged audioTrackChanged);
    void onTextTrackChanged(PlayerEvent.TextTrackChanged textTrackChanged);
}
