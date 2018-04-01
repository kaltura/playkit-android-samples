package com.kaltura.appanalyticssample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.kaltura.netkit.connect.response.ResultElement;
import com.kaltura.playkit.PKEvent;
import com.kaltura.playkit.PKMediaConfig;
import com.kaltura.playkit.PKMediaEntry;
import com.kaltura.playkit.PlayKitManager;
import com.kaltura.playkit.Player;
import com.kaltura.playkit.PlayerEvent;
import com.kaltura.playkit.api.ovp.SimpleOvpSessionProvider;
import com.kaltura.playkit.mediaproviders.base.OnMediaLoadCompletion;
import com.kaltura.playkit.mediaproviders.ovp.KalturaOvpMediaProvider;
import com.kaltura.playkit.player.PKTracks;
import com.kaltura.playkit.plugins.ads.AdEvent;

class AnalyticsEventHandler {
    private static final String TAG = "AnalyticsEventHandler";

    // Should be replaced by an instance of the analytics engine.
    private Object analyticsEngine;

    public AnalyticsEventHandler(Object analyticsEngine) {
        this.analyticsEngine = analyticsEngine;
    }

    void register(Player player) {

        // Basic events.
        player.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                PlayerEvent e = (PlayerEvent) event;
                switch (e.type) {
                    // Handle
                }
            }
        }, PlayerEvent.Type.CAN_PLAY, PlayerEvent.Type.ENDED, PlayerEvent.Type.PAUSE, PlayerEvent.Type.PLAY, PlayerEvent.Type.PLAYING, PlayerEvent.Type.SEEKED, PlayerEvent.Type.STOPPED);


        // Other events typically have companion classes -- cast the event to the matching type.

        // Source selected
        player.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                PlayerEvent.SourceSelected e = (PlayerEvent.SourceSelected) event;
                // A specific source (playback url) was selected.
                Log.d(TAG, "Player will play: " + e.source.getUrl());
            }
        }, PlayerEvent.Type.SOURCE_SELECTED);

        // Tracks availability and selection
        player.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                PlayerEvent e = (PlayerEvent) event;
            }
        }, PlayerEvent.Type.TRACKS_AVAILABLE, PlayerEvent.Type.AUDIO_TRACK_CHANGED, PlayerEvent.Type.VIDEO_TRACK_CHANGED, PlayerEvent.Type.TEXT_TRACK_CHANGED);

        // Playhead
        player.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                // Report first quartile, midpoint, etc
            }
        }, PlayerEvent.Type.PLAYHEAD_UPDATED);

        // More metadata
        player.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {

            }
        }, PlayerEvent.Type.DURATION_CHANGE, PlayerEvent.Type.METADATA_AVAILABLE);

        // Ad events
        player.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {

            }
        }, AdEvent.Type.values());
    }

    void setEngine(Object engine) {
        analyticsEngine = engine;
    }
}

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Player player;
    private AnalyticsEventHandler listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupPlayer();

        listener = new AnalyticsEventHandler(new Object());
        listener.register(player);

        loadMedia();

        findViewById(R.id.play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (player != null) {
                    player.play();
                }
            }
        });
    }

    private void loadMedia() {
        new KalturaOvpMediaProvider()
                .setSessionProvider(new SimpleOvpSessionProvider("https://cdnapisec.kaltura.com", 2215841, null))
                .setEntryId("1_cl4ic86v")
                .load(new OnMediaLoadCompletion() {
                    @Override
                    public void onComplete(final ResultElement<PKMediaEntry> response) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                player.prepare(new PKMediaConfig().setMediaEntry(response.getResponse()));
                            }
                        });
                    }
                });
    }

    private void setupPlayer() {

        player = PlayKitManager.loadPlayer(this, null);

        FrameLayout container = findViewById(R.id.playerContainer);

        container.addView(player.getView());
    }
}
