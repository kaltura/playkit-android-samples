/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.kaltura.playkit.samples.androidtv;

import android.app.Activity;
import android.graphics.Bitmap;
import android.media.MediaMetadata;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.kaltura.netkit.connect.response.ResultElement;
import com.kaltura.playkit.PKDrmParams;
import com.kaltura.playkit.PKEvent;
import com.kaltura.playkit.PKMediaConfig;
import com.kaltura.playkit.PKMediaEntry;
import com.kaltura.playkit.PKMediaFormat;
import com.kaltura.playkit.PKMediaSource;
import com.kaltura.playkit.PKPluginConfigs;
import com.kaltura.playkit.PlayKitManager;
import com.kaltura.playkit.Player;
import com.kaltura.playkit.PlayerEvent;
import com.kaltura.playkit.api.ovp.SimpleOvpSessionProvider;
import com.kaltura.playkit.mediaproviders.base.OnMediaLoadCompletion;
import com.kaltura.playkit.mediaproviders.ovp.KalturaOvpMediaProvider;
import com.kaltura.playkit.player.PKTracks;
import com.kaltura.playkit.plugins.Youbora.YouboraPlugin;
import com.kaltura.playkit.plugins.ads.AdError;
import com.kaltura.playkit.plugins.ads.AdEvent;
import com.kaltura.playkit.plugins.ads.ima.IMAConfig;
import com.kaltura.playkit.plugins.ads.ima.IMAPlugin;

import java.util.ArrayList;
import java.util.List;


/**
 * PlaybackOverlayActivity for video playback that loads PlaybackOverlayFragment
 */
public class PlaybackOverlayActivity extends Activity implements
        PlaybackOverlayFragment.OnPlayPauseClickedListener {
    private static final String TAG = "PlaybackOverlayActivity";
    public static final String STATS_KALTURA_COM = "https://stats.kaltura.com/api_v3/index.php";

    //private VideoView mVideoView;
    protected Player player;
    private boolean isFirstPlay = true;
    private Movie currentMovie;
    private LeanbackPlaybackState mPlaybackState = LeanbackPlaybackState.IDLE;
    private MediaSession mSession;
    private PKTracks pkTracks;


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playback_controls);
        loadViews();
        setupCallbacks();
        mSession = new MediaSession(this, "LeanbackSampleApp");
        mSession.setCallback(new MediaSessionCallback());
        mSession.setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS);

        mSession.setActive(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        player.destroy();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        PlaybackOverlayFragment playbackOverlayFragment = (PlaybackOverlayFragment) getFragmentManager().findFragmentById(R.id.playback_controls_fragment);
        switch (keyCode) {
            case KeyEvent.KEYCODE_MEDIA_PLAY:
                playbackOverlayFragment.togglePlayback(false);
                return true;
            case KeyEvent.KEYCODE_MEDIA_PAUSE:
                playbackOverlayFragment.togglePlayback(false);
                return true;
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                if (mPlaybackState == LeanbackPlaybackState.PLAYING) {
                    playbackOverlayFragment.togglePlayback(false);
                } else {
                    playbackOverlayFragment.togglePlayback(true);
                }
                return true;
            default:
                return super.onKeyUp(keyCode, event);
        }
    }

    /**
     * Implementation of OnPlayPauseClickedListener
     */
    public void onFragmentPlayPause(Movie movie, int position, Boolean playPause) {
        if (currentMovie == null || !currentMovie.getVideoUrl().equals(movie.getVideoUrl())) {
            Log.d(TAG, "NEW Movie " + mPlaybackState.name());
            isFirstPlay = true;
            TracksUtils.clearSelectedTracks();
            currentMovie = movie;
            PKMediaEntry mediaEntry = movie.getPkMediaEntry();//new PKMediaEntry();
            currentMovie.setDuration((int) mediaEntry.getDuration());
            PKMediaConfig mediaConfig = new PKMediaConfig();
            mediaConfig.setMediaEntry(mediaEntry);
            mediaConfig.setStartPosition(position);
            if (isFirstPlay) {
                player.prepare(mediaConfig);
                isFirstPlay = false;
            }
        }

        if (position == 0 || mPlaybackState == LeanbackPlaybackState.IDLE) {

            mPlaybackState = LeanbackPlaybackState.IDLE;
        }

        if (playPause && mPlaybackState != LeanbackPlaybackState.PLAYING) {
            mPlaybackState = LeanbackPlaybackState.PLAYING;
            player.play();
        } else {
            mPlaybackState = LeanbackPlaybackState.PAUSED;
            player.pause();
        }
        updatePlaybackState(position);
        updateMetadata(movie);
    }

    @Override
    public PKTracks onTracksAvailable() {
        return pkTracks;
    }

    @Override
    public void changeTrack(String uniqueId) {
        if (player != null) {
            player.changeTrack(uniqueId);
        }
    }

    @Override
    public void seekTo(long position) {
        if (player != null) {
            player.seekTo(position);
        }
    }

    @Override
    public long getPosition() {
        if (player != null) {
            return player.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public long getDuration() {
        if (player != null) {
            return player.getDuration();
        }
        return 0;
    }

    @Override
    public void replay() {
        if (player != null) {
            player.replay();
        }
    }

    @Override
    public void stop() {
        if (player != null) {
            player.stop();
        }
    }

    private void updatePlaybackState(int position) {
        PlaybackState.Builder stateBuilder = new PlaybackState.Builder()
                .setActions(getAvailableActions());
        int state = PlaybackState.STATE_PLAYING;
        if (mPlaybackState == LeanbackPlaybackState.PAUSED) {
            state = PlaybackState.STATE_PAUSED;
        }
        stateBuilder.setState(state, position, 1.0f);
        mSession.setPlaybackState(stateBuilder.build());
    }

    private long getAvailableActions() {
        long actions = PlaybackState.ACTION_PLAY |
                PlaybackState.ACTION_PLAY_FROM_MEDIA_ID |
                PlaybackState.ACTION_PLAY_FROM_SEARCH;

        if (mPlaybackState == LeanbackPlaybackState.PLAYING) {
            actions |= PlaybackState.ACTION_PAUSE;
        }

        return actions;
    }

    private void updateMetadata(final Movie movie) {
        final MediaMetadata.Builder metadataBuilder = new MediaMetadata.Builder();

        String title = movie.getTitle().replace("_", " -");

        metadataBuilder.putString(MediaMetadata.METADATA_KEY_DISPLAY_TITLE, title);
        metadataBuilder.putString(MediaMetadata.METADATA_KEY_DISPLAY_SUBTITLE,
                movie.getDescription());
        metadataBuilder.putString(MediaMetadata.METADATA_KEY_DISPLAY_ICON_URI,
                movie.getCardImageUrl());

        // And at minimum the title and artist for legacy support
        metadataBuilder.putString(MediaMetadata.METADATA_KEY_TITLE, title);
        metadataBuilder.putString(MediaMetadata.METADATA_KEY_ARTIST, movie.getStudio());

        Glide.with(this)
                .load(Uri.parse(movie.getCardImageUrl()))
                .asBitmap()
                .into(new SimpleTarget<Bitmap>(500, 500) {
                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                        metadataBuilder.putBitmap(MediaMetadata.METADATA_KEY_ART, bitmap);
                        mSession.setMetadata(metadataBuilder.build());
                    }
                });
    }

    private void loadViews() {
        if (player == null) {
            //PlayKitManager.registerPlugins(this, IMAPlugin.factory);
            //PlayKitManager.registerPlugins(this, KalturaStatsPlugin.factory);
            //PlayKitManager.registerPlugins(this, YouboraPlugin.factory);

            PKPluginConfigs pluginConfigs = new PKPluginConfigs();
            //addKalturaStatsPlugin(pluginConfigs);
            //addIMAPluginConfig(pluginConfigs);
            //addYouboraPlugin(pluginConfigs);
            player = PlayKitManager.loadPlayer(this, pluginConfigs);
            setupCallbacks();
            getWindow().getDecorView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    getWindow().getDecorView().getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    final FrameLayout playerKitViewContainer = (FrameLayout) findViewById(R.id.player_root);
                    ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(getWindow().getDecorView().getMeasuredWidth(),
                            getWindow().getDecorView().getMeasuredHeight());

                    View playerKitView = player.getView();
                    playerKitView.setLayoutParams(layoutParams);
                    playerKitViewContainer.addView(playerKitView);
                    //mVideoView = (VideoView) findViewById(R.id.videoView);
                    playerKitView.setFocusable(false);
                    playerKitView.setFocusableInTouchMode(false);
                    //playerKitView.setVisibility(View.VISIBLE);

                }
            });
        }
    }

    private void addYouboraPlugin(PKPluginConfigs pluginConfig) {
        JsonPrimitive accountCode = new JsonPrimitive("XXXX");
        JsonPrimitive username = new JsonPrimitive("YYYY");
        JsonPrimitive haltOnError = new JsonPrimitive(true);
        JsonPrimitive enableAnalytics = new JsonPrimitive(true);
        JsonPrimitive enableSmartAds = new JsonPrimitive(true);

        JsonObject mediaEntry = new JsonObject();
        mediaEntry.addProperty("isLive", false);
        mediaEntry.addProperty("title", "Media1");

        JsonObject adsEntry = new JsonObject();
        adsEntry.addProperty("campaign", "camp");

        JsonObject extraParamEntry = new JsonObject();
        extraParamEntry.addProperty("param1", "Mobile");
        extraParamEntry.addProperty("param2", "playKitPlayer");

        JsonObject propertiesEntry = new JsonObject();
        propertiesEntry.addProperty("genre", "");
        propertiesEntry.addProperty("type", "");
        propertiesEntry.addProperty("transaction_type", "");
        propertiesEntry.addProperty("year", "");
        propertiesEntry.addProperty("cast", "");
        propertiesEntry.addProperty("director", "");
        propertiesEntry.addProperty("owner", "");
        propertiesEntry.addProperty("parental", "");
        propertiesEntry.addProperty("price", "");
        propertiesEntry.addProperty("rating", "");
        propertiesEntry.addProperty("audioType", "");
        propertiesEntry.addProperty("audioChannels", "");
        propertiesEntry.addProperty("device", "");
        propertiesEntry.addProperty("quality", "");

        ConverterYoubora converterYoubora = new ConverterYoubora(accountCode, username, haltOnError, enableAnalytics, enableSmartAds,
                mediaEntry,
                adsEntry, extraParamEntry, propertiesEntry);

        pluginConfig.setPluginConfig(YouboraPlugin.factory.getName(), converterYoubora.toJson());
    }

    private void addKalturaStatsPlugin(PKPluginConfigs config) {
        JsonObject pluginEntry = new JsonObject();
        // pluginEntry.addProperty("sessionId", "b3460681-b994-6fad-cd8b-f0b65736e837"); // sent by playKit now since version v0.2.16
        pluginEntry.addProperty("uiconfId", Integer.parseInt("12345"));
        pluginEntry.addProperty("baseUrl", STATS_KALTURA_COM);
        pluginEntry.addProperty("partnerId", Integer.parseInt("123444"));
        /*int videoDuration = Integer.parseInt(mVideoDetailsModel.getDuration());
        //int analyticReportInterval = (int)(videoDuration / 1000) * 0.1;*/
        pluginEntry.addProperty("timerInterval", Integer.parseInt("15"));
        pluginEntry.addProperty("entryId", "1_sssssss");

        //config.setPluginConfig(KalturaStatsPlugin.factory.getName(), pluginEntry);
    }

    private void addIMAPluginConfig(PKPluginConfigs config) {
        String adTagUrl = //"https://in-viacom18.videoplaza.tv/proxy/distributor/v2?s=viacom18/youth/MTV&t=Content+Type=Highlights,Series+Title=MTV+Roadies+Rising,Gender=,GeoCity=,Age=,Carrier=,Media+ID=491591,Genre=Reality,SBU=MTV,Content+Name=Highlights%3A+Rannvijay+makes+his+choice,OEM=LGE,Language=Hindi,WiFi=Y,appversion=1.6.119,useragent=Android+LGE+google+Nexus+5,KidsPinEnabled=false&tt=p%2Cm%2Cpo&bp=365.40002&rnd=8170019078998&cd=489&vbw=400&ang_pbname=https%3A%2F%2Fplay.google.com%2Fstore%2Fapps%2Fdetails%3Fid%3Dcom.tv.v18.viola&pid=73274a17-920a-4666-b844-540ab8dd29f9&rt=vmap_1.0&pf=and_6.0&cp.useragent=Android+LGE+google+Nexus+5&cp.adid=d4d36fd0-e5e2-4b28-a225-ee0dca05d724&cp.optout=false&cp.deviceid=aa5e1b6c96988d68&cp.osversion=6.0";
                //"https://in-viacom18.videoplaza.tv/proxy/distributor/v2?s=vmap&t=Content+Type=Full+Episode,Series+Title=MTV+Unplugged+S06,Gender=U,Media+ID=476975,Genre=Music,SBU=MTV,Content+Name=The+fusion+of+stars,OEM=LGE,Language=Hindi,WiFi=Y,appversion=0.1.102,useragent=Android+LGE+google+Nexus+5,KidsPinEnabled=false,&tid=43cdeafd-77f5-11e6-bff1-02a55d5f3a8d&tt=p%2Cm%2Cpo%2Co&bp=336.0,681.0,1128.0,1464.0,1655.4,2052.0&rnd=8170019078998&cd=2485&rt=vmap_1.0&pf=and_6.0.1&rt=vmap_1.0&cp.useragent=Android+LGE+google+Nexus+5&cp.adid=d928acbe-4276-422e-b97a-9d9b681f94c3&cp.optout=false";
                //"https://in-viacom18.videoplaza.tv/proxy/distributor/v2?s=viacom18/youth/MTV&t=Content+Type=Highlights,Series+Title=MTV+Roadies+Rising,Gender=,GeoCity=,Age=,Carrier=,Media+ID=491591,Genre=Reality,SBU=MTV,Content+Name=Highlights%3A+Rannvijay+makes+his+choice,OEM=LGE,Language=Hindi,WiFi=Y,appversion=1.6.119,useragent=Android+LGE+google+Nexus+5,KidsPinEnabled=false&tt=p%2Cm%2Cpo&bp=365.40002&rnd=8170019078998&cd=489&vbw=400&ang_pbname=https%3A%2F%2Fplay.google.com%2Fstore%2Fapps%2Fdetails%3Fid%3Dcom.tv.v18.viola&pid=73274a17-920a-4666-b844-540ab8dd29f9&rt=vmap_1.0&pf=and_6.0&cp.useragent=Android+LGE+google+Nexus+5&cp.adid=d4d36fd0-e5e2-4b28-a225-ee0dca05d724&cp.optout=false&cp.deviceid=aa5e1b6c96988d68&cp.osversion=6.0";
                "https://pubads.g.doubleclick.net/gampad/ads?sz=640x360&iu=/6062/iab_vast_samples/skippable&ciu_szs=300x250,728x90&impl=s&gdfp_req=1&env=vp&output=xml_vast2&unviewed_position_start=1&url=[referrer_url]&correlator=[timestamp]";
                //"https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/ad_rule_samples&ciu_szs=300x250&ad_rule=1&impl=s&gdfp_req=1&env=vp&output=vmap&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ar%3Dpremidpostoptimizedpodbumper&cmsid=496&vid=short_onecue&correlator=";
                //"https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dskippablelinear&correlator=";
        //"https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/3274935/preroll&impl=s&gdfp_req=1&env=vp&output=xml_vast2&unviewed_position_start=1&url=[referrer_url]&description_url=[description_url]&correlator=[timestamp]";
        //"https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/ad_rule_samples&ciu_szs=300x250&ad_rule=1&impl=s&gdfp_req=1&env=vp&output=vmap&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ar%3Dpremidpostpod&cmsid=496&vid=short_onecue&correlator=";
        List<String> videoMimeTypes = new ArrayList<>();
        //videoMimeTypes.add(MimeTypes.APPLICATION_MP4);
        //videoMimeTypes.add(MimeTypes.APPLICATION_M3U8);
        //Map<Double, String> tagTimesMap = new HashMap<>();
        //tagTimesMap.put(2.0,"ADTAG");

        IMAConfig adsConfig = new IMAConfig().setAdTagURL(adTagUrl);//.enableDebugMode(true);
        config.setPluginConfig(IMAPlugin.factory.getName(), adsConfig.toJSONObject());
    }

    private void setupCallbacks() {
        player.addEventListener(new PKEvent.Listener() {

                                    @Override
                                    public void onEvent(PKEvent event) {



                                        Enum receivedEventType = event.eventType();
                                        if (event instanceof PlayerEvent) {
                                            switch (((PlayerEvent) event).type) {
                                                case CAN_PLAY:

                                                    break;
                                                case PLAY:

                                                    break;
                                                case PLAYING:
                                                    mPlaybackState = LeanbackPlaybackState.PLAYING;
                                                    break;
                                                case PAUSE:

                                                    break;
                                                case SEEKING:

                                                    break;
                                                case SEEKED:

                                                    break;
                                                case ENDED:
                                                    mPlaybackState = LeanbackPlaybackState.IDLE;
                                                    break;
                                                case TRACKS_AVAILABLE:
                                                    pkTracks = ((PlayerEvent.TracksAvailable) event).getPKTracks();
                                                    break;
                                                case ERROR:
                                                    String msg = "Player Error";
                                                    Log.d(TAG, "PLAYER ERROR");

                                                    player.stop();
                                                    mPlaybackState = LeanbackPlaybackState.IDLE;
                                                    break;
                                            }
                                        } else if (event instanceof AdEvent) {
                                            switch (((AdEvent) event).type) {
                                                case LOADED:

                                                    break;
                                                case CUEPOINTS_CHANGED:

                                                    break;
                                                case ALL_ADS_COMPLETED:

                                                    break;
                                                case AD_BREAK_IGNORED:

                                                    break;
                                                case CONTENT_PAUSE_REQUESTED:
                                                    //isAdStarted = true;
                                                    //showOrHideContentLoaderProgress(false);
                                                    //hideSbuLogo();
                                                    break;
                                                //case AD_DISPLAYED_AFTER_CONTENT_PAUSE:
                                                //    VideoPlaybackTimer.getInstance().stopTimer();
                                                //    showOrHideContentLoaderProgress(false);
                                                //    break;
                                                case CONTENT_RESUME_REQUESTED:

                                                    break;
                                                case STARTED:

                                                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                                                    break;
                                                case PAUSED:

                                                    break;
                                                case TAPPED:
                                                    break;
                                                case COMPLETED:

                                                    break;
                                                case SKIPPED:

                                                    break;
                                                case CLICKED:
                                                    break;
                                            }
                                        } else if (event instanceof AdError) {
                                            switch (((AdError) event).errorType) {
                                                case ADS_REQUEST_NETWORK_ERROR:
                                                case INTERNAL_ERROR:
                                                case VAST_MALFORMED_RESPONSE:
                                                case UNKNOWN_AD_RESPONSE:
                                                case VAST_LOAD_TIMEOUT:
                                                case VAST_TOO_MANY_REDIRECTS:
                                                case VIDEO_PLAY_ERROR:
                                                case VAST_MEDIA_LOAD_TIMEOUT:
                                                case VAST_LINEAR_ASSET_MISMATCH:
                                                case OVERLAY_AD_PLAYING_FAILED:
                                                case OVERLAY_AD_LOADING_FAILED:
                                                case VAST_NONLINEAR_ASSET_MISMATCH:
                                                case COMPANION_AD_LOADING_FAILED:
                                                case UNKNOWN_ERROR:
                                                case VAST_EMPTY_RESPONSE:
                                                case FAILED_TO_REQUEST_ADS:
                                                case VAST_ASSET_NOT_FOUND:
                                                case INVALID_ARGUMENTS:
                                                case QUIET_LOG_ERROR:
                                                case PLAYLIST_NO_CONTENT_TRACKING:
                                                    break;
                                            }
                                        }

                                    }

                                }, PlayerEvent.Type.PLAY, PlayerEvent.Type.PAUSE, PlayerEvent.Type.CAN_PLAY, PlayerEvent.Type.SEEKING, PlayerEvent.Type.SEEKED, PlayerEvent.Type.PLAYING,
                PlayerEvent.Type.ENDED, PlayerEvent.Type.TRACKS_AVAILABLE, PlayerEvent.Type.ERROR,
                AdEvent.Type.LOADED, AdEvent.Type.SKIPPED, AdEvent.Type.TAPPED, AdEvent.Type.CONTENT_PAUSE_REQUESTED, AdEvent.Type.CONTENT_RESUME_REQUESTED, AdEvent.Type.STARTED, AdEvent.Type.PAUSED, AdEvent.Type.RESUMED,
                AdEvent.Type.COMPLETED, AdEvent.Type.ALL_ADS_COMPLETED, AdError.Type.ADS_REQUEST_NETWORK_ERROR,
                AdEvent.Type.CUEPOINTS_CHANGED, AdEvent.Type.CLICKED, AdEvent.Type.AD_BREAK_IGNORED,
                AdError.Type.VAST_EMPTY_RESPONSE, AdError.Type.COMPANION_AD_LOADING_FAILED, AdError.Type.FAILED_TO_REQUEST_ADS,
                AdError.Type.INTERNAL_ERROR, AdError.Type.OVERLAY_AD_LOADING_FAILED, AdError.Type.PLAYLIST_NO_CONTENT_TRACKING,
                AdError.Type.UNKNOWN_ERROR, AdError.Type.VAST_LINEAR_ASSET_MISMATCH, AdError.Type.VAST_MALFORMED_RESPONSE, AdError.Type.QUIET_LOG_ERROR,
                AdError.Type.VAST_LOAD_TIMEOUT, AdError.Type.INVALID_ARGUMENTS, AdError.Type.VAST_TOO_MANY_REDIRECTS);
//        mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
//
//            @Override
//            public boolean onError(MediaPlayer mp, int what, int extra) {
//                String msg = "";
//                if (extra == MediaPlayer.MEDIA_ERROR_TIMED_OUT) {
//                    msg = getString(R.string.video_error_media_load_timeout);
//                } else if (what == MediaPlayer.MEDIA_ERROR_SERVER_DIED) {
//                    msg = getString(R.string.video_error_server_inaccessible);
//                } else {
//                    msg = getString(R.string.video_error_unknown_error);
//                }
//                player.stop();
//                mPlaybackState = LeanbackPlaybackState.IDLE;
//                return false;
//            }
//        });
//
//        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//                if (mPlaybackState == LeanbackPlaybackState.PLAYING) {
//                    mVideoView.start();
//                }
//            }
//        });
//
//        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//                mPlaybackState = LeanbackPlaybackState.IDLE;
//            }
//        });

    }

    @Override
    public void onResume() {
        super.onResume();
        mSession.setActive(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (player.isPlaying()) {
            if (!requestVisibleBehind(true)) {
                // Try to play behind launcher, but if it fails, stop playback.
                stopPlayback();
            }
        } else {
            requestVisibleBehind(false);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mSession.release();
    }


    @Override
    public void onVisibleBehindCanceled() {
        super.onVisibleBehindCanceled();
    }

    private void stopPlayback() {
        if (player != null) {
            Log.d(TAG, "STOP");

            player.stop();
        }
    }

    /*
     * List of various states that we can be in
     */
    public enum LeanbackPlaybackState {
        PLAYING, PAUSED, BUFFERING, IDLE
    }

    private class MediaSessionCallback extends MediaSession.Callback {
    }
}
