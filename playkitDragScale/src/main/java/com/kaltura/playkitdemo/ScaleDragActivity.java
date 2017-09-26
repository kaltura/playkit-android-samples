package com.kaltura.playkitdemo;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.kaltura.playkit.MediaEntryProvider;
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
import com.kaltura.playkit.plugins.ads.AdCuePoints;
import com.kaltura.playkit.plugins.ads.AdEvent;
import com.kaltura.playkit.plugins.ads.ima.IMAConfig;
import com.kaltura.playkit.plugins.ads.ima.IMAPlugin;
import com.kaltura.playkitdemo.dragging.DragView;
import com.kaltura.playkitdemo.dragging.DragViewController;

import java.util.ArrayList;
import java.util.List;

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
            mIsShowing = false;
            mHandler.removeCallbacks(mHideControlsRunnable);
            controlsView.setVisibility(View.GONE);
        }
    };


    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler();
        setContentView(R.layout.scale_drag_activity);
        root = (FrameLayout) findViewById(R.id.player_root);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        controlsView = (PlaybackControlsView) findViewById(R.id.playerControls);
        registerPlugins();
        onMediaLoaded(createMediaEntry());
        mDragView = (DragView)findViewById(R.id.drag_view);
        mDragView.setEventListener(new DragViewController.EventListener() {

            @Override
            public void onClick() {
                if (!mIsShowing) {
                    mIsShowing = true;
                    controlsView.setVisibility(View.VISIBLE);
                    mHandler.postDelayed(mHideControlsRunnable, TIME_SHOW_CONTROLS);
                }
                else {
                    mIsShowing = false;
                    mHandler.removeCallbacks(mHideControlsRunnable);
                    controlsView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onUpdateViewSize(float scaleFactor, int originW, int originH) {
                int bottom = (int)(getResources().getDimension(R.dimen.player_controls_height) * scaleFactor);
                controlsView.setPivotY(0);
                controlsView.setScaleY(scaleFactor);
                controlsView.getLayoutParams().height = bottom;
                root.setPadding(0, 0, 0, bottom);
            }
        });
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
        PKMediaConfig mediaConfig = new PKMediaConfig().setMediaEntry(mediaEntry).setStartPosition(0);
        PKPluginConfigs pluginConfig = new PKPluginConfigs();
        if (player == null) {

            addIMAPluginConfig(pluginConfig);

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

    private void addIMAPluginConfig(PKPluginConfigs config) {
        //String adTagUrl = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dskippablelinear&correlator=";
        String adTagUrl = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/3274935/preroll&impl=s&gdfp_req=1&env=vp&output=xml_vast2&unviewed_position_start=1&url=[referrer_url]&description_url=[description_url]&correlator=[timestamp]";
        List<String> videoMimeTypes = new ArrayList<>();
        IMAConfig adsConfig = new IMAConfig().setAdTagURL(adTagUrl);
        config.setPluginConfig(IMAPlugin.factory.getName(), adsConfig);
    }

    private void registerPlugins() {
        PlayKitManager.registerPlugins(this, IMAPlugin.factory);
    }

    private void addPlayerListeners(final ProgressBar appProgressBar) {
        player.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                log.d("AD_CONTENT_PAUSE_REQUESTED");
                appProgressBar.setVisibility(View.VISIBLE);
            }
        }, AdEvent.Type.CONTENT_PAUSE_REQUESTED);
        player.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                AdEvent.AdCuePointsUpdateEvent cuePointsList = (AdEvent.AdCuePointsUpdateEvent) event;
                AdCuePoints adCuePoints = cuePointsList.cuePoints;
                if (adCuePoints != null) {
                    log.d("Has Postroll = " + adCuePoints.hasPostRoll());
                }
            }
        }, AdEvent.Type.CUEPOINTS_CHANGED);
        player.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                log.d("AD_STARTED");
                appProgressBar.setVisibility(View.INVISIBLE);
            }
        }, AdEvent.Type.STARTED);
        player.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                log.d("Ad Event AD_RESUMED");
                nowPlaying = true;
                appProgressBar.setVisibility(View.INVISIBLE);
            }
        }, AdEvent.Type.RESUMED);
        player.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                log.d("Ad Event AD_ALL_ADS_COMPLETED");
                appProgressBar.setVisibility(View.INVISIBLE);
            }
        }, AdEvent.Type.ALL_ADS_COMPLETED);
        player.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                nowPlaying = true;
            }
        }, PlayerEvent.Type.PLAY);

        player.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                nowPlaying = false;
            }
        }, PlayerEvent.Type.PAUSE);

        player.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                nowPlaying = true;
            }
        }, AdEvent.Type.SKIPPED);

        player.addStateChangeListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                if (event instanceof PlayerEvent.StateChanged) {
                    PlayerEvent.StateChanged stateChanged = (PlayerEvent.StateChanged) event;
                    log.d("State changed from " + stateChanged.oldState + " to " + stateChanged.newState);

                    if(controlsView != null){
                        controlsView.setPlayerState(stateChanged.newState);
                    }
                }
            }
        });
        player.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                log.d("player play");

            }
        }, PlayerEvent.Type.PLAY);
        player.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                //When the track data available, this event occurs. It brings the info object with it.
                PlayerEvent.TracksAvailable tracksAvailable = (PlayerEvent.TracksAvailable) event;

            }
        }, PlayerEvent.Type.TRACKS_AVAILABLE);
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
