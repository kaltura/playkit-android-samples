package com.kaltura.playkit.samples.youbora;

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
import com.kaltura.playkit.plugins.youbora.YouboraEvent;
import com.kaltura.playkit.plugins.youbora.YouboraPlugin;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    //Tag for logging.
    private static final String TAG = MainActivity.class.getSimpleName();

    //The url of the source to play
    private static final String SOURCE_URL = "https://cdnapisec.kaltura.com/p/2215841/playManifest/entryId/1_w9zx2eti/format/mpegdash/protocol/https/a.mpd";

    //The id of the entry.
    private static final String ENTRY_ID = "entry_id";
    //The id of the source.
    private static final String MEDIA_SOURCE_ID = "source_id";

    //Youbora analytics Constants
    public static final String ACCOUNT_CODE = "your_account_code";
    public static final String USER_NAME = "your_user_name";
    public static final String MEDIA_TITLE = "your_media_title";
    public static final boolean IS_LIVE = false;
    public static final boolean ENABLE_SMART_ADS = true;
    private static final String CAMPAIGN = "your_campaign_name";
    public static final String EXTRA_PARAM_1 = "playKitPlayer";
    public static final String EXTRA_PARAM_2 = "";
    public static final String GENRE = "your_genre";
    public static final String TYPE = "your_type";
    public static final String TRANSACTION_TYPE = "your_trasnsaction_type";
    public static final String YEAR = "your_year";
    public static final String CAST = "your_cast";
    public static final String DIRECTOR = "your_director";
    private static final String OWNER = "your_owner";
    public static final String PARENTAL = "your_parental";
    public static final String PRICE = "your_price";
    public static final String RATING = "your_rating";
    public static final String AUDIO_TYPE = "your_audio_type";
    public static final String AUDIO_CHANNELS = "your_audoi_channels";
    public static final String DEVICE = "your_device";
    public static final String QUALITY = "your_quality";


    private Player player;
    private PKMediaConfig mediaConfig;
    private Button playPauseButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize media config object.
        createMediaConfig();

        //Initialize PKPluginConfigs object with Youbora.
        PKPluginConfigs pluginConfigs = createYouboraPlugin();

        //Create instance of the player with specified pluginConfigs.
        player = PlayKitManager.loadPlayer(this, pluginConfigs);

        //Subscribe to analytics report event.
        subscribeToYouboraReportEvent();

        //Add player to the view hierarchy.
        addPlayerToView();

        //Add simple play/pause button.
        addPlayPauseButton();

        //Prepare player with media config.
        player.prepare(mediaConfig);

    }

    /**
     * Will create {@link PKPluginConfigs} object with {@link YouboraPlugin}.
     *
     * @return - the pluginConfig object that should be passed as parameter when loading the player.
     */
    private PKPluginConfigs createYouboraPlugin() {

        //Important!!! First you need to register your plugin.
        PlayKitManager.registerPlugins(this, YouboraPlugin.factory);

        //Initialize PKPluginConfigs object.
        PKPluginConfigs pluginConfigs = new PKPluginConfigs();
        //Initialize Json object that will hold all the configurations for the plugin.
        JsonObject pluginEntry = new JsonObject();

        //Youbora config json. Main config goes here.
        JsonObject youboraConfigJson = new JsonObject();
        youboraConfigJson.addProperty("accountCode", ACCOUNT_CODE);
        youboraConfigJson.addProperty("username", USER_NAME);
        youboraConfigJson.addProperty("haltOnError", true);
        youboraConfigJson.addProperty("enableAnalytics", true);
        youboraConfigJson.addProperty("enableSmartAds", ENABLE_SMART_ADS);


        //Media entry json.
        JsonObject mediaEntryJson = new JsonObject();
        mediaEntryJson.addProperty("isLive", IS_LIVE);
        mediaEntryJson.addProperty("title", MEDIA_TITLE);

        //Youbora ads configuration json.
        JsonObject adsJson = new JsonObject();
        adsJson.addProperty("adsExpected", true);
        adsJson.addProperty("campaign", CAMPAIGN);

        //Configure custom properties here:
        JsonObject propertiesJson = new JsonObject();
        propertiesJson.addProperty("genre", GENRE);
        propertiesJson.addProperty("type", TYPE);
        propertiesJson.addProperty("transaction_type", TRANSACTION_TYPE);
        propertiesJson.addProperty("year", YEAR);
        propertiesJson.addProperty("cast", CAST);
        propertiesJson.addProperty("director", DIRECTOR);
        propertiesJson.addProperty("owner", OWNER);
        propertiesJson.addProperty("parental", PARENTAL);
        propertiesJson.addProperty("price", PRICE);
        propertiesJson.addProperty("rating", RATING);
        propertiesJson.addProperty("audioType", AUDIO_TYPE);
        propertiesJson.addProperty("audioChannels", AUDIO_CHANNELS);
        propertiesJson.addProperty("device", DEVICE);
        propertiesJson.addProperty("quality", QUALITY);

        //You can add some extra params here:
        JsonObject extraParamJson = new JsonObject();
        extraParamJson.addProperty("param1", EXTRA_PARAM_1);
        extraParamJson.addProperty("param2", EXTRA_PARAM_2);

        //Add all the json objects created before to the pluginEntry json.
        pluginEntry.add("media", mediaEntryJson);
        pluginEntry.add("ads", adsJson);
        pluginEntry.add("properties", propertiesJson);
        pluginEntry.add("extraParams", extraParamJson);

        //Set plugin entry to the plugin configs.
        pluginConfigs.setPluginConfig(YouboraPlugin.factory.getName(), pluginEntry);

        return pluginConfigs;
    }

    /**
     * Subscribe to kaltura stats report event.
     * This event will be received each and every time
     * the analytics report is sent.
     */
    private void subscribeToYouboraReportEvent() {
        //Subscribe to the event.
        player.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                //Cast received event to AnalyticsEvent.BaseAnalyticsReportEvent.
                YouboraEvent.YouboraReport reportEvent = (YouboraEvent.YouboraReport) event;

                //Get the event name from the report.
                String reportedEventName = reportEvent.reportedEventName;
                Log.i(TAG, "Youbora report sent. Reported event name: " + reportedEventName);
            }
            //Event subscription.
        }, YouboraEvent.Type.REPORT_SENT);
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
        mediaEntry.setMediaType(PKMediaEntry.MediaEntryType.Unknown);

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
        mediaSource.setMediaFormat(PKMediaFormat.dash);

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
