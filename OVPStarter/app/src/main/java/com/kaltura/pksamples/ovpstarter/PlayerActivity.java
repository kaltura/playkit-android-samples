package com.kaltura.pksamples.ovpstarter;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
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

public class PlayerActivity extends PlayerActivityBase {
    private static final String TAG = "PlayerActivity";
    public static final String ENTRY_ID = "1_djnefl4e";
    public static final int MSEC_IN_SEC = 1000;
    public static final int SEEKBAR_MSEC_FACTOR = 200;
    private static final String BASE_URL = "https://cdnapisec.kaltura.com";
    private static final int PARTNER_ID = 1424501;
    private static final String KAVA_BASE_URL = "https://analytics.kaltura.com/api_v3/index.php";
    // UIConf id -- optional for KAVA
    private static final int UI_CONF_ID = 0;
    private ImageButton playPauseButton;
    private Player player;
    private String ks;
    private String entryId = ENTRY_ID;
    private boolean ended;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        registerPlugins();

        playPauseButton = findViewById(R.id.play_pause_button);

        setupPlayer();

        fullscreenContentContainer.setKeepScreenOn(true);


        loadMedia();
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

    public void setKs(String ks) {
        this.ks = ks;
    }

    public void setEntryId(String entryId) {
        this.entryId = entryId;
    }

    private void setupPlayer() {
        player = PlayKitManager.loadPlayer(this, createPluginConfigs());
        fullscreenContentContainer.addView(player.getView());

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
                        fullscreenContentContainer.setKeepScreenOn(true);
                        playPauseButton.setImageResource(R.drawable.ic_pause_black_24dp);
                        break;

                    // Change play/pause button to PLAY
                    case PAUSE:
                        fullscreenContentContainer.setKeepScreenOn(false);
                        playPauseButton.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                        break;

                    case ENDED:
                        fullscreenContentContainer.setKeepScreenOn(false);
                        playPauseButton.setImageResource(R.drawable.ic_replay_black_24dp);
                        ended = true;
                        player.pause();
                        break;
                }
            }
        }, PlayerEvent.Type.DURATION_CHANGE, PlayerEvent.Type.PLAYHEAD_UPDATED,
                PlayerEvent.Type.PLAYING, PlayerEvent.Type.PAUSE, PlayerEvent.Type.ENDED);

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
                } else if (ended) {
                    player.replay();
                } else {
                    player.play();
                }
            }
        });
    }

    private void loadMedia() {

        new KalturaOvpMediaProvider(BASE_URL, PARTNER_ID, this.ks)
                .setEntryId(entryId)
                .load(new OnMediaLoadCompletion() {
                    @Override
                    public void onComplete(final ResultElement<PKMediaEntry> response) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (response.isSuccess()) {

                                    // Update with entryId and KS
                                    player.updatePluginConfig(KavaAnalyticsPlugin.factory.getName(), getKavaConfig());

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

        KavaAnalyticsConfig kavaAnalyticsConfig = getKavaConfig();


        // Set plugin entry to the plugin configs.
        pluginConfigs.setPluginConfig(KavaAnalyticsPlugin.factory.getName(), kavaAnalyticsConfig);

        return pluginConfigs;
    }

    private KavaAnalyticsConfig getKavaConfig() {
        return new KavaAnalyticsConfig()
                    .setBaseUrl(KAVA_BASE_URL)
                    .setPartnerId(PARTNER_ID)
                    .setEntryId(entryId)
                    .setKs(ks)
                    .setUiConfId(UI_CONF_ID);
    }

    private void registerPlugins() {
        PlayKitManager.registerPlugins(this, KavaAnalyticsPlugin.factory);
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
}
