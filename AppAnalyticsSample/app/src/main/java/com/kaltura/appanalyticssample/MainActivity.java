package com.kaltura.appanalyticssample;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.kaltura.playkit.PKLog;
import com.kaltura.playkit.PKMediaConfig;
import com.kaltura.playkit.PlayKitManager;
import com.kaltura.playkit.Player;
import com.kaltura.playkit.PlayerEvent;
import com.kaltura.playkit.plugins.ads.AdEvent;

import com.kaltura.playkit.providers.api.SimpleSessionProvider;
import com.kaltura.playkit.providers.ovp.KalturaOvpMediaProvider;

class AnalyticsEventHandler {
    private static final String TAG = "AnalyticsEventHandler";
    private static final PKLog log = PKLog.get(TAG);
    // Should be replaced by an instance of the analytics engine.
    private Object analyticsEngine;

    public AnalyticsEventHandler(Object analyticsEngine) {
        this.analyticsEngine = analyticsEngine;
    }

    void register(Player player) {

        player.addListener(this, PlayerEvent.canPlay, event -> {
            log.d("canPlay event");
        });

        player.addListener(this, PlayerEvent.ended, event -> {
            log.d("ended event");
        });

        player.addListener(this, PlayerEvent.pause, event -> {
            log.d("pause event");
        });

        player.addListener(this, PlayerEvent.play, event -> {
            log.d("play event");
        });

        player.addListener(this, PlayerEvent.playing, event -> {
            log.d("playing event");
        });

        player.addListener(this, PlayerEvent.seeked, event -> {
            log.d("seeked event");
        });

        player.addListener(this, PlayerEvent.stopped, event -> {
            log.d("stopped event");
        });

        player.addListener(this, PlayerEvent.sourceSelected, event -> {
            log.d("sourceSelected event");
            PlayerEvent.SourceSelected e = (PlayerEvent.SourceSelected) event;
            // A specific source (playback url) was selected.
            Log.d(TAG, "Player will play: " + e.source.getUrl());
        });

        player.addListener(this, PlayerEvent.sourceSelected, event -> {
            log.d("sourceSelected event");
            PlayerEvent.SourceSelected e = (PlayerEvent.SourceSelected) event;
            // A specific source (playback url) was selected.
            Log.d(TAG, "Player will play: " + e.source.getUrl());
        });

        player.addListener(this, PlayerEvent.tracksAvailable, event -> {
            log.d("tracksAvailable event");
        });

        player.addListener(this, PlayerEvent.videoTrackChanged, event -> {
            log.d("videoTrackChanged event");
        });

        player.addListener(this, PlayerEvent.audioTrackChanged, event -> {
            log.d("audioTrackChanged event");
        });

        player.addListener(this, PlayerEvent.textTrackChanged, event -> {
            log.d("textTrackChanged event");
        });

        player.addListener(this, PlayerEvent.playheadUpdated, event -> {
            log.d("playheadUpdated event");
        });

        player.addListener(this, PlayerEvent.metadataAvailable, event -> {
            log.d("metadataAvailable event");
        });

        player.addListener(this, PlayerEvent.durationChanged, event -> {
            log.d("durationChanged event");
        });


        player.addListener(this, AdEvent.started, event -> {
            log.d("ad started event");
        });

        player.addListener(this, AdEvent.contentPauseRequested, event -> {
            log.d("ad contentPauseRequested event");
        });

        player.addListener(this, AdEvent.contentResumeRequested, event -> {
            log.d("ad contentResumeRequested event");
        });

        player.addListener(this, AdEvent.allAdsCompleted, event -> {
            log.d("ad allAdsCompleted event");
        });
    }

    void setEngine(Object engine) {
        analyticsEngine = engine;
    }
}




public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Player player;
    private AnalyticsEventHandler listener;
    private static final PKLog log = PKLog.get("MainActivity");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupPlayer();

        listener = new AnalyticsEventHandler(new Object());
        listener.register(player);

        loadMedia();

        findViewById(R.id.play).setOnClickListener(v -> {
            if (player != null) {
                if (player.isPlaying()) {
                    player.pause();
                    ((android.widget.Button)findViewById(R.id.play)).setText("play");
                } else {
                    player.play();
                    ((android.widget.Button)findViewById(R.id.play)).setText("pause");

                }
            }
        });
    }

    private void loadMedia() {
        new KalturaOvpMediaProvider()
                .setSessionProvider(new SimpleSessionProvider("https://cdnapisec.kaltura.com", 2215841, null))
                .setEntryId("1_cl4ic86v")
                .load(response -> runOnUiThread(() -> player.prepare(new PKMediaConfig().setMediaEntry(response.getResponse()))));
    }

    private void setupPlayer() {

        player = PlayKitManager.loadPlayer(this, null);

        FrameLayout container = findViewById(R.id.playerContainer);

        container.addView(player.getView());
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (player != null) {
            player.onApplicationPaused();
        }
    }

    @Override
    protected void onResume() {
        log.d("Application onResume");
        super.onResume();
        if (player != null) {
            player.onApplicationResumed();
            player.play();
        }
    }

    @Override
    public void onDestroy() {
        if (player != null) {
            player.removeListeners(this);
            player.destroy();
            player = null;
        }
        super.onDestroy();
    }

}
