package com.kaltura.playkit.samples.miniplayer;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.kaltura.playkit.PKEvent;
import com.kaltura.playkit.PKLog;
import com.kaltura.playkit.PKMediaConfig;
import com.kaltura.playkit.PKMediaEntry;
import com.kaltura.playkit.PKMediaFormat;
import com.kaltura.playkit.PKMediaSource;
import com.kaltura.playkit.PKPluginConfigs;
import com.kaltura.playkit.PlayKitManager;
import com.kaltura.playkit.Player;
import com.kaltura.playkit.PlayerEvent;
import com.kaltura.playkit.PlayerState;
import com.kaltura.playkit.plugins.ads.AdCuePoints;
import com.kaltura.playkit.plugins.ads.AdEvent;
import com.kaltura.playkit.plugins.ott.OttEvent;
import com.kaltura.playkit.plugins.ott.PhoenixAnalyticsEvent;
import com.kaltura.playkit.providers.MediaEntryProvider;
import com.kaltura.playkit.samples.miniplayer.dragging.DragView;
import com.kaltura.playkit.samples.miniplayer.dragging.DragViewController;
import com.kaltura.playkitdemo.R;

import java.util.ArrayList;
import java.util.List;

//import com.kaltura.playkit.plugins.ima.IMAConfig;
//import com.kaltura.playkit.plugins.ima.IMAPlugin;

/**
 * Created by glebgleb on 7/13/17.
 */

public class ScaleDragActivity extends Activity {

    private FrameLayout root;

    private static final PKLog log = PKLog.get("Activity");

    private static final int TIME_SHOW_CONTROLS = 5000;

    private Player player;
    private MediaEntryProvider mediaProvider;
    private PlaybackControlsView controlsView;
    private DragView mDragView;
    private boolean nowPlaying;
    private boolean mIsShowing;
    private Handler mHandler;
    ProgressBar progressBar;

    private Runnable mHideControlsRunnable = new Runnable() {
        @Override
        public void run() {
            showControls(false);
        }
    };


    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler();
        setContentView(R.layout.scale_drag_activity);
        root = findViewById(R.id.player_root);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        controlsView = findViewById(R.id.playerControls);
        //registerPlugins();
        onMediaLoaded(createMediaEntry());
        mDragView = findViewById(R.id.drag_view);
        mDragView.setEventListener(new DragViewController.EventListener() {

            @Override
            public void onClick() {
                Log.v("event", "onClick");
                showControls(!mIsShowing);
            }

            @Override
            public void onUpdateViewSize(float scaleFactor, int originW, int originH) {
                Log.v("event", "onUpdateSize");
                int bottom = (int)(getResources().getDimension(R.dimen.player_controls_height) * scaleFactor);
                controlsView.setPivotY(0);
                controlsView.setScaleY(scaleFactor);
                controlsView.getLayoutParams().height = bottom;
                root.setPadding(0, 0, 0, bottom);
            }

            @Override
            public void onDragStart() {
                Log.v("event", "onStart");
                showControls(false);
            }

            @Override
            public void onDragEnd() {
                Log.v("event", "onEnd");
                showControls(true);
            }
        });
    }

    private void showControls(boolean isShow) {
        if (isShow) {
            mIsShowing = true;
            controlsView.setVisibility(View.VISIBLE);
            mHandler.removeCallbacks(mHideControlsRunnable);
            mHandler.postDelayed(mHideControlsRunnable, TIME_SHOW_CONTROLS);
        }
        else {
            mIsShowing = false;
            mHandler.removeCallbacks(mHideControlsRunnable);
            controlsView.setVisibility(View.GONE);
        }
    }

    /*private int convertDpToPixel(int dp) {
        Resources r = getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }

    private int getScreenWidth() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }*/

    private PKMediaEntry createMediaEntry() {
        PKMediaEntry entry = new PKMediaEntry();
        PKMediaSource source = new PKMediaSource();
        source.setId("0_uka1msg4");
        source.setUrl("http://api-preprod.ott.kaltura.com/v4_2/api_v3/service/assetFile/action/playManifest/partnerId/198/assetId/259295/assetType/media/assetFileId/516109/contextType/PLAYBACK/a.m3u8");
        source.setMediaFormat(PKMediaFormat.hls);
        entry.setId("0_uka1msg4");
        entry.setDuration(102000);
        entry.setMediaType(PKMediaEntry.MediaEntryType.Unknown);
        ArrayList<PKMediaSource> sourceList = new ArrayList<>();
        sourceList.add(source);
        entry.setSources(sourceList);
        return entry;
    }

    private void onMediaLoaded(PKMediaEntry mediaEntry) {
        PKMediaConfig mediaConfig = new PKMediaConfig().setMediaEntry(mediaEntry).setStartPosition(0L);
        PKPluginConfigs pluginConfig = new PKPluginConfigs();
        if (player == null) {

            //addIMAPluginConfig(pluginConfig);

            player = PlayKitManager.loadPlayer(this, pluginConfig);
            log.d("Player: " + player.getClass());
            addPlayerListeners(progressBar);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            root.addView(player.getView(), params);

            controlsView.setPlayer(player);
        }
        player.prepare(mediaConfig);
        player.play();
    }

//    private void addIMAPluginConfig(PKPluginConfigs config) {
//        //String adTagUrl = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dskippablelinear&correlator=";
//        String adTagUrl = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/3274935/preroll&impl=s&gdfp_req=1&env=vp&output=xml_vast2&unviewed_position_start=1&url=[referrer_url]&description_url=[description_url]&correlator=[timestamp]";
//        List<String> videoMimeTypes = new ArrayList<>();
//        IMAConfig adsConfig = new IMAConfig().setAdTagUrl(adTagUrl);
//        config.setPluginConfig(IMAPlugin.factory.getName(), adsConfig);
//    }
//
//    private void registerPlugins() {
//        PlayKitManager.registerPlugins(this, IMAPlugin.factory);
//    }


    private void addPlayerListeners(final ProgressBar appProgressBar) {

        player.addListener(this, AdEvent.contentPauseRequested, event -> {
            log.d("AD_CONTENT_PAUSE_REQUESTED");
            appProgressBar.setVisibility(View.VISIBLE);
        });

        player.addListener(this, AdEvent.cuepointsChanged, event -> {
            AdEvent.AdCuePointsUpdateEvent cuePointsList = (AdEvent.AdCuePointsUpdateEvent) event;
            AdCuePoints adCuePoints = cuePointsList.cuePoints;
            if (adCuePoints != null) {
                log.d("Has Postroll = " + adCuePoints.hasPostRoll());
            }
        });

        player.addListener(this, AdEvent.contentResumeRequested, event -> {
            log.d("CONTENT_RESUME_REQUESTED");
            appProgressBar.setVisibility(View.INVISIBLE);
            controlsView.setPlayerState(PlayerState.READY);
        });

        player.addListener(this, AdEvent.daiSourceSelected, event -> {
            log.d("DAI_SOURCE_SELECTED: " + event.sourceURL);

        });

        player.addListener(this, AdEvent.adPlaybackInfoUpdated, event -> {
            log.d("AD_PLAYBACK_INFO_UPDATED");
            log.d("playbackInfoUpdated  = " + event.width + "/" + event.height + "/" + event.bitrate);
        });



        player.addListener(this, AdEvent.adBufferStart, event -> {
            log.d("AD_BUFFER_START pos = " + event.adPosition);
            appProgressBar.setVisibility(View.VISIBLE);
        });

        player.addListener(this, AdEvent.adBufferEnd, event -> {
            log.d("AD_BUFFER_END pos = " + event.adPosition);
            appProgressBar.setVisibility(View.INVISIBLE);
        });

        player.addListener(this, AdEvent.adFirstPlay, event -> {
            log.d("AD_FIRST_PLAY");
            appProgressBar.setVisibility(View.INVISIBLE);
        });

        player.addListener(this, AdEvent.resumed, event -> {
            log.d("Ad Event AD_RESUMED");
            nowPlaying = true;
            appProgressBar.setVisibility(View.INVISIBLE);
        });

        player.addListener(this, AdEvent.playHeadChanged, event -> {
            appProgressBar.setVisibility(View.INVISIBLE);
            //log.d("received AD PLAY_HEAD_CHANGED " + event.adPlayHead);
        });

        player.addListener(this, AdEvent.allAdsCompleted, event -> {
            log.d("Ad Event AD_ALL_ADS_COMPLETED");
            appProgressBar.setVisibility(View.INVISIBLE);
        });

        player.addListener(this, AdEvent.error, event -> {
            if (event != null && event.error != null) {
                log.e("ERROR: " + event.error.errorType + ", " + event.error.message);
            }
        });

        player.addListener(this, AdEvent.skipped, event -> {
            log.d("Ad Event SKIPPED");
            nowPlaying = true;
        });

        player.addListener(this, PlayerEvent.surfaceAspectRationSizeModeChanged, event -> {
            log.d("resizeMode updated" + event.resizeMode);
        });


        /////// PLAYER EVENTS

        player.addListener(this, PlayerEvent.play, event -> {
            log.d("Player Event PLAY");
            nowPlaying = true;
        });

        player.addListener(this, PlayerEvent.playing, event -> {
            log.d("Player Event PLAYING");
            appProgressBar.setVisibility(View.INVISIBLE);
            nowPlaying = true;
        });

        player.addListener(this, PlayerEvent.pause, event -> {
            log.d("Player Event PAUSE");
            nowPlaying = false;
        });

        player.addListener(this, PlayerEvent.playbackRateChanged, event -> {
            log.d("playbackRateChanged event  rate = " + event.rate);
        });

        player.addListener(this, PlayerEvent.tracksAvailable, event -> {
            //When the track data available, this event occurs. It brings the info object with it.
        });

        player.addListener(this, PlayerEvent.sourceSelected, event -> {
            log.d("sourceSelected event source = " + event.source);
        });

        player.addListener(this, PlayerEvent.playbackRateChanged, event -> {
            log.d("playbackRateChanged event  rate = " + event.rate);
        });

        player.addListener(this, PlayerEvent.error, event -> {
            //When the track data available, this event occurs. It brings the info object with it.
            if (event != null && event.error != null) {
                log.d("PlayerEvent.Error event  position = " + event.error.errorType + " errorMessage = " + event.error.message);
            }
        });

        player.addListener(this, PlayerEvent.ended, event -> {
            appProgressBar.setVisibility(View.INVISIBLE);
        });

        player.addListener(this, PlayerEvent.playheadUpdated, event -> {
            //When the track data available, this event occurs. It brings the info object with it.
            //log.d("playheadUpdated event  position = " + event.position + " duration = " + event.duration);
        });

        player.addListener(this, PlayerEvent.videoFramesDropped, event -> {
            //log.d("VIDEO_FRAMES_DROPPED " + event.droppedVideoFrames);
        });

        player.addListener(this, PlayerEvent.bytesLoaded, event -> {
            //log.d("BYTES_LOADED " + event.bytesLoaded);
        });

        player.addListener(this, PlayerEvent.stateChanged, new PKEvent.Listener<PlayerEvent.StateChanged>() {
            @Override
            public void onEvent(PlayerEvent.StateChanged event) {
                log.d("State changed from " + event.oldState + " to " + event.newState);
                if (event.newState == PlayerState.BUFFERING) {
                    appProgressBar.setVisibility(View.VISIBLE);
                }
                if ((event.oldState == PlayerState.LOADING || event.oldState == PlayerState.BUFFERING) && event.newState == PlayerState.READY) {
                    appProgressBar.setVisibility(View.INVISIBLE);

                }
                if(controlsView != null){
                    controlsView.setPlayerState(event.newState);
                }
            }
        });

        /////Phoenix events

        player.addListener(this, PhoenixAnalyticsEvent.bookmarkError, event -> {
            log.d("bookmarkErrorEvent errorCode = " + event.errorCode + " message = " + event.errorMessage);
        });

        player.addListener(this, PhoenixAnalyticsEvent.concurrencyError, event -> {
            log.d("ConcurrencyErrorEvent errorCode = " + event.errorCode + " message = " + event.errorMessage);
        });

        player.addListener(this, PhoenixAnalyticsEvent.error, event -> {
            log.d("Phoenox Analytics errorEvent errorCode = " + event.errorCode + " message = " + event.errorMessage);
        });

        player.addListener(this, PhoenixAnalyticsEvent.error, event -> {
            log.d("Phoenox Analytics errorEvent errorCode = " + event.errorCode + " message = " + event.errorMessage);
        });

        player.addListener(this, OttEvent.ottEvent, event -> {
            log.d("Concurrency event = " + event.type);
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (controlsView != null) {
            controlsView.release();
        }
        if (player != null) {
            player.onApplicationPaused();
        }
    }

    @Override
    public void onResume() {
        log.d("Ad Event onResume");
        super.onResume();
        if (player != null) {
            player.onApplicationResumed();
            if (nowPlaying) {
                player.play();
            }
        }
        if (controlsView != null) {
            controlsView.resume();
        }
    }

}
