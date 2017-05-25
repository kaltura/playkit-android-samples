package com.kaltura.playkit.samples.androidtv;

import android.text.TextUtils;

import com.kaltura.playkit.player.AudioTrack;
import com.kaltura.playkit.player.PKTracks;
import com.kaltura.playkit.player.TextTrack;
import com.kaltura.playkit.player.VideoTrack;
import com.kaltura.playkit.utils.Consts;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gilad.nadav on 24/05/2017.
 */

public class TracksUtils {
    private static final String AUTO_TRACK_DESCRIPTION = "Auto";

    private static int lastSelectedVideoTrackIndex = -1;
    private static int lastSelectedAudioTrackIndex = -1;
    private static int lastSelectedTextTrackIndex = -1;

    public static List<TrackItem> createTrackItems(int trackType, PKTracks tracks) {
        List<TrackItem> trackItems = new ArrayList<>();
        TrackItem trackItem;
        switch (trackType) {
            case Consts.TRACK_TYPE_VIDEO:
                List<VideoTrack> videoTracksInfo = tracks.getVideoTracks();
                for (int i = 0; i < videoTracksInfo.size(); i++) {
                    VideoTrack trackInfo = videoTracksInfo.get(i);
                    if (trackInfo.isAdaptive()) {
                        trackItem = new TrackItem(trackInfo.getUniqueId(), AUTO_TRACK_DESCRIPTION);
                    } else {
                        trackItem = new TrackItem(trackInfo.getUniqueId(), buildBitrateString(trackInfo.getBitrate()));
                    }
                    trackItems.add(trackItem);
                }
                break;
            case  Consts.TRACK_TYPE_AUDIO:
                List<AudioTrack> audioTracksInfo = tracks.getAudioTracks();
                for (int i = 0; i < audioTracksInfo.size(); i++) {
                    AudioTrack trackInfo = audioTracksInfo.get(i);
                    if (trackInfo.isAdaptive()) {
                        trackItem = new TrackItem(trackInfo.getUniqueId(), buildLanguageString(trackInfo.getLanguage()) + " " + AUTO_TRACK_DESCRIPTION);
                    } else {
                        trackItem = new TrackItem(trackInfo.getUniqueId(), buildLanguageString(trackInfo.getLanguage()) + " " + buildBitrateString(trackInfo.getBitrate()));
                    }

                    trackItems.add(trackItem);
                }
                break;
            case  Consts.TRACK_TYPE_TEXT:
                List<TextTrack> textTracksInfo = tracks.getTextTracks();
                for (int i = 0; i < textTracksInfo.size(); i++) {
                    TextTrack trackInfo = textTracksInfo.get(i);
                    if (trackInfo.isAdaptive()) {
                        trackItem = new TrackItem(trackInfo.getUniqueId(), AUTO_TRACK_DESCRIPTION);
                    } else {
                        trackItem = new TrackItem(trackInfo.getUniqueId(), buildLanguageString(trackInfo.getLanguage()));
                    }

                    trackItems.add(trackItem);
                }
                break;
        }
        return trackItems;
    }

    public static String buildBitrateString(long bitrate) {
        return bitrate == Consts.NO_VALUE ? ""
                : String.format("%.2fMbit", bitrate / 1000000f);
    }

    public static String buildLanguageString(String language) {
        return TextUtils.isEmpty(language) || "und".equals(language) ? ""
                : language;
    }

    public static void setLastSelectedTrack(int trackType, int newIndex) {
        switch (trackType) {
            case Consts.TRACK_TYPE_VIDEO:
                lastSelectedVideoTrackIndex = newIndex;
                break;
            case Consts.TRACK_TYPE_AUDIO:
                lastSelectedAudioTrackIndex = newIndex;
                break;
            case Consts.TRACK_TYPE_TEXT:
                lastSelectedTextTrackIndex = newIndex;
                break;
        }
    }

    public static int getLastSelectedTrack(int trackType) {
        switch (trackType) {
            case Consts.TRACK_TYPE_VIDEO:
                return lastSelectedVideoTrackIndex;

            case Consts.TRACK_TYPE_AUDIO:
                return  lastSelectedAudioTrackIndex;
            case Consts.TRACK_TYPE_TEXT:
                return lastSelectedTextTrackIndex;
        }
        return -1;
    }

    public static void clearSelectedTracks() {
        lastSelectedVideoTrackIndex = -1;
        lastSelectedAudioTrackIndex = -1;
        lastSelectedTextTrackIndex = -1;
    }
}
