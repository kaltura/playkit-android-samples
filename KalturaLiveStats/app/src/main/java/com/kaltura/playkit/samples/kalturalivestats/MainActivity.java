package com.kaltura.playkit.samples.kalturalivestats;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.gson.JsonObject;
import com.kaltura.playkit.PKEvent;
import com.kaltura.playkit.PKMediaConfig;
import com.kaltura.playkit.PKMediaEntry;
import com.kaltura.playkit.PKMediaFormat;
import com.kaltura.playkit.PKMediaSource;
import com.kaltura.playkit.PKPluginConfigs;
import com.kaltura.playkit.PlayKitManager;
import com.kaltura.playkit.Player;
import com.kaltura.playkit.plugins.ott.PhoenixAnalyticsEvent;
import com.kaltura.playkit.plugins.ovp.KalturaLiveStatsEvent;
import com.kaltura.playkit.plugins.ovp.KalturaLiveStatsPlugin;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    //Tag for logging.
    private static final String TAG = MainActivity.class.getSimpleName();

    //The url of the source to play
    private static final String SOURCE_URL = "http://qa-apache-php7.dev.kaltura.com/p/1091/sp/109100/playManifest/entryId/0_f8re4ujs/format/applehttp/protocol/http/uiConfId/15068781/a.m3u8";
    //"http://nasatv-lh.akamaihd.net/i/NASA_101@319270/master.m3u8";

    //The id of the entry.
    private static final String ENTRY_ID = "0_f8re4ujs";

    //The entry id of the media.
    private static final String ANALYTICS_MEDIA_ENTRY_ID = "0_f8re4ujs"; // the meida's entryId both for OVP and OTT

    //The id of the source.
    private static final String MEDIA_SOURCE_ID = "source_id";

    //Analytics constants
    private static final int PARTNER_ID = 1091; // your partner id here.
    private static final String KALTURA_LIVE_STATS_URL = "https://livestats.kaltura.com/api_v3/index.php"; //Server url
    private static final int ANALYTICS_TRIGGER_INTERVAL = 30; //Interval in which analytics report should be triggered (in seconds).

    private Player player;
    private PKMediaConfig mediaConfig;
    private Button playPauseButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize media config object.
        createMediaConfig();

        //Initialize PKPluginConfigs object with KalturaStatsPlugin.
        PKPluginConfigs pluginConfigs = createKalturaLiveStatsPlugin();

        //Create instance of the player with specified pluginConfigs.
        player = PlayKitManager.loadPlayer(this, pluginConfigs);

        //Subscribe to analytics report event.
        subscribeToKalturaLiveStatsReportEvent();

        //Add player to the view hierarchy.
        addPlayerToView();

        //Add simple play/pause button.
        addPlayPauseButton();

        //Prepare player with media config.
        player.prepare(mediaConfig);
    }

    /**
     * Will create {@link PKPluginConfigs} object with {@link KalturaLiveStatsPlugin}.
     *
     * @return - the pluginConfig object that should be passed as parameter when loading the player.
     */
    private PKPluginConfigs createKalturaLiveStatsPlugin() {

        //First register your plugin.
        PlayKitManager.registerPlugins(this, KalturaLiveStatsPlugin.factory);

        //Initialize PKPluginConfigs object.
        PKPluginConfigs pluginConfigs = new PKPluginConfigs();
        //Initialize Json object that will hold all the configurations for the plugin.
        JsonObject pluginEntry = new JsonObject();
        //Put url to the kaltura stats server.
        pluginEntry.addProperty("baseUrl", KALTURA_LIVE_STATS_URL);

        //Put the partner id.
        pluginEntry.addProperty("partnerId", PARTNER_ID);

        //Put EntryId.
        pluginEntry.addProperty("entryId", ANALYTICS_MEDIA_ENTRY_ID);

        //Put interval with which analytics reports would be triggered.
        pluginEntry.addProperty("timerInterval", ANALYTICS_TRIGGER_INTERVAL);

        //Set plugin entry to the plugin configs.
        pluginConfigs.setPluginConfig(KalturaLiveStatsPlugin.factory.getName(), pluginEntry);

        return pluginConfigs;
    }

    /**
     * Subscribe to live stats report event.
     * This event will be received each and every time
     * the analytics report is sent.
     */
    private void subscribeToKalturaLiveStatsReportEvent() {
        //Subscribe to the event.

        player.addListener(this, KalturaLiveStatsEvent.reportSent, event -> {
            KalturaLiveStatsEvent.KalturaLiveStatsReport liveReportEvent = event;

            //Get the buffer time from the report.
            long bufferTime = liveReportEvent.bufferTime;
            Log.i(TAG, "Live stats report sent. Buffer time: " + bufferTime);
        });
    }

    /**
     * Will create {@link PKMediaConfig} object.
     */
    private void createMediaConfig() {
        //First. Create PKMediaConfig object.
        mediaConfig = new PKMediaConfig();

        //Second. Create PKMediaEntry object.
        PKMediaEntry mediaEntry = createMediaEntry();

        //Add it to the mediaConfig.
        mediaConfig.setMediaEntry(mediaEntry);
    }

    /**
     * Create {@link PKMediaEntry} with minimum necessary data.
     *
     * @return - the {@link PKMediaEntry} object.
     */
    private PKMediaEntry createMediaEntry() {
        //Create media entry.
        PKMediaEntry mediaEntry = new PKMediaEntry();

        //Set id for the entry.
        mediaEntry.setId(ENTRY_ID);

        //Set media entry type. It could be Live,Vod or Unknown.
        //For now we will use Unknown.
        mediaEntry.setMediaType(PKMediaEntry.MediaEntryType.Live);

        //Create list that contains at least 1 media source.
        //Each media entry can contain a couple of different media sources.
        //All of them represent the same content, the difference is in it format.
        //For example same entry can contain PKMediaSource with dash and another
        // PKMediaSource can be with hls. The player will decide by itself which source is
        // preferred for playback.
        List<PKMediaSource> mediaSources = createMediaSources();

        //Set media sources to the entry.
        mediaEntry.setSources(mediaSources);

        return mediaEntry;
    }

    /**
     * Create list of {@link PKMediaSource}.
     *
     * @return - the list of sources.
     */
    private List<PKMediaSource> createMediaSources() {
        //Init list which will hold the PKMediaSources.
        List<PKMediaSource> mediaSources = new ArrayList<>();

        //Create new PKMediaSource instance.
        PKMediaSource mediaSource = new PKMediaSource();

        //Set the id.
        mediaSource.setId(MEDIA_SOURCE_ID);

        //Set the content url. In our case it will be link to hls source(.m3u8).
        mediaSource.setUrl(SOURCE_URL);

        //Set the format of the source. In our case it will be hls.
        mediaSource.setMediaFormat(PKMediaFormat.hls);

        //Add media source to the list.
        mediaSources.add(mediaSource);

        return mediaSources;
    }

    /**
     * Will add player to the view.
     */
    private void addPlayerToView() {
        //Get the layout, where the player view will be placed.
        LinearLayout layout = (LinearLayout) findViewById(R.id.player_root);
        //Add player view to the layout.
        layout.addView(player.getView());
    }

    /**
     * Just add a simple button which will start/pause playback.
     */
    private void addPlayPauseButton() {
        //Get reference to the play/pause button.
        playPauseButton = (Button) this.findViewById(R.id.play_pause_button);
        //Add clickListener.
        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (player.isPlaying()) {
                    //If player is playing, change text of the button and pause.
                    playPauseButton.setText(R.string.play_text);
                    player.pause();
                } else {
                    //If player is not playing, change text of the button and play.
                    playPauseButton.setText(R.string.pause_text);
                    player.play();
                }
            }
        });
    }
}
