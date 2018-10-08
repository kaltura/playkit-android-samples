package com.kaltura.pksamples.ovpstarter;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kaltura.netkit.connect.response.ResultElement;
import com.kaltura.playkit.PKEvent;
import com.kaltura.playkit.PKMediaConfig;
import com.kaltura.playkit.PKMediaEntry;
import com.kaltura.playkit.PKPluginConfigs;
import com.kaltura.playkit.PlayKitManager;
import com.kaltura.playkit.Player;
import com.kaltura.playkit.PlayerEvent;
import com.kaltura.playkit.plugins.kava.KavaAnalyticsConfig;
import com.kaltura.playkit.plugins.kava.KavaAnalyticsEvent;
import com.kaltura.playkit.plugins.kava.KavaAnalyticsPlugin;
import com.kaltura.playkit.providers.base.OnMediaLoadCompletion;
import com.kaltura.playkit.providers.ovp.KalturaOvpMediaProvider;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class PlayerActivity extends AppCompatActivity {

    private static final String TAG = "PlayerActivity";
    private static final String BASE_URL = "https://cdnapisec.kaltura.com";
    private static final String ENTRY_ID = "1_bc69i9jw";
    private static final int PARTNER_ID = 2344301;

    private static final String KAVA_BASE_URL = "https://analytics.kaltura.com/api_v3/index.php";

    // UIConf id -- optional for KAVA
    private static final int UI_CONF_ID = 0;

    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    public static final int MSEC_IN_SEC = 1000;
    public static final int SEEKBAR_MSEC_FACTOR = 200;
    private final Handler mHideHandler = new Handler();
    private ImageButton playPauseButton;
    private LinearLayout playerContainer;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            playerContainer.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View controlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            controlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };
    private Player player;
    private String currentKS;

    public PlayerActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_player);
        registerPlugins();

        mVisible = true;
        controlsView = findViewById(R.id.fullscreen_content_controls);

        playPauseButton = findViewById(R.id.play_pause_button);


        playerContainer = findViewById(R.id.player_container);


        // Set up the user interaction to manually show or hide the system UI.
        playerContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        player = PlayKitManager.loadPlayer(this, createPluginConfigs());
        addPlayerToView();
        playerContainer.setKeepScreenOn(true);

        controlsView.setOnTouchListener(mDelayHideTouchListener);

        loadMedia(ENTRY_ID, currentKS);
    }

    private void addPlayerToView() {
        playerContainer.addView(player.getView());

        final SeekBar seekBar = findViewById(R.id.mediacontroller_progress);
        final TextView currentTimeLabel = findViewById(R.id.time_current);
        currentTimeLabel.setText(formatSeconds(0));
        final TextView durationLabel = findViewById(R.id.time);
        durationLabel.setText(formatSeconds(0));

        player.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(final PKEvent event) {
                switch (((PlayerEvent) event).type) {

                    // Update the seekbar position and the position label
                    case PLAYHEAD_UPDATED:
                        final long position = ((PlayerEvent.PlayheadUpdated) event).position;
                        seekBar.setProgress((int) (position / SEEKBAR_MSEC_FACTOR));
                        currentTimeLabel.setText(formatSeconds(position / MSEC_IN_SEC));
                        break;

                    // Update the seekbar size and the duration label
                    case DURATION_CHANGE:
                        final long duration = ((PlayerEvent.DurationChanged) event).duration;
                        seekBar.setMax((int) (duration / SEEKBAR_MSEC_FACTOR));
                        durationLabel.setText(formatSeconds(duration / MSEC_IN_SEC));
                        break;

                    // Change play/pause button to PAUSE
                    case PLAYING:
                        playPauseButton.setImageResource(R.drawable.ic_pause_black_24dp);
                        break;

                    // Change play/pause button to PLAY
                    case PAUSE:
                        playPauseButton.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                        break;
                }
            }
        }, PlayerEvent.Type.DURATION_CHANGE, PlayerEvent.Type.PLAYHEAD_UPDATED, PlayerEvent.Type.PLAYING, PlayerEvent.Type.PAUSE);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    player.seekTo(progress * SEEKBAR_MSEC_FACTOR);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (player.isPlaying()) {
                    player.pause();
                } else {
                    player.play();
                }
            }
        });

        player.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                final String reportedEventName = ((KavaAnalyticsEvent.KavaAnalyticsReport) event).reportedEventName;
                Log.d(TAG, "KAVA reported: " + reportedEventName);
            }
        }, KavaAnalyticsEvent.Type.REPORT_SENT);
    }

    public static String formatSeconds(long timeInSeconds) {

        long secondsLeft = timeInSeconds % 60;
        long minutes = timeInSeconds % 3600 / 60;
        long hours = timeInSeconds / 3600;

        String HH = hours < 10 ? "0" + hours : "" + hours;
        String MM = minutes < 10 ? "0" + minutes : "" + minutes;
        String SS = secondsLeft < 10 ? "0" + secondsLeft : "" + secondsLeft;

        return HH + ":" + MM + ":" + SS;
    }

    private void loadMedia(String entryId, String ks) {

        new KalturaOvpMediaProvider(BASE_URL, PARTNER_ID, ks)
                .setEntryId(entryId)
                .load(new OnMediaLoadCompletion() {
                    @Override
                    public void onComplete(final ResultElement<PKMediaEntry> response) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (response.isSuccess()) {

                                    player.prepare(new PKMediaConfig().setMediaEntry(response.getResponse()));
                                    player.play();  // Will play when ready

                                } else {
                                    Toast.makeText(PlayerActivity.this, "Failed loading media: " + response.getError(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
    }

    private PKPluginConfigs createPluginConfigs() {

        PKPluginConfigs pluginConfigs = new PKPluginConfigs();

        KavaAnalyticsConfig kavaAnalyticsConfig = new KavaAnalyticsConfig()
                .setBaseUrl(KAVA_BASE_URL)
                .setPartnerId(PARTNER_ID)
                .setUiConfId(UI_CONF_ID); // optional


        // Set plugin entry to the plugin configs.
        pluginConfigs.setPluginConfig(KavaAnalyticsPlugin.factory.getName(), kavaAnalyticsConfig);

        return pluginConfigs;
    }

    private void registerPlugins() {
        PlayKitManager.registerPlugins(this, KavaAnalyticsPlugin.factory);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(3000);
    }

    @Override
    protected void onPause() {

        player.onApplicationPaused();

        super.onPause();
    }

    @Override
    protected void onDestroy() {

        player.destroy();

        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        player.onOrientationChanged();

        super.onConfigurationChanged(newConfig);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        controlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        playerContainer.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}
