package com.kaltura.playkit.samples.youbora;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;
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

import static com.kaltura.playkit.plugins.youbora.pluginconfig.YouboraConfig.KEY_CONTENT_METADATA_CAST;
import static com.kaltura.playkit.plugins.youbora.pluginconfig.YouboraConfig.KEY_CONTENT_METADATA_DIRECTOR;
import static com.kaltura.playkit.plugins.youbora.pluginconfig.YouboraConfig.KEY_CONTENT_METADATA_OWNER;
import static com.kaltura.playkit.plugins.youbora.pluginconfig.YouboraConfig.KEY_CONTENT_METADATA_PARENTAL;
import static com.kaltura.playkit.plugins.youbora.pluginconfig.YouboraConfig.KEY_CONTENT_METADATA_QUALITY;
import static com.kaltura.playkit.plugins.youbora.pluginconfig.YouboraConfig.KEY_CONTENT_METADATA_RATING;
import static com.kaltura.playkit.plugins.youbora.pluginconfig.YouboraConfig.KEY_CONTENT_METADATA_YEAR;
import static com.kaltura.playkit.plugins.youbora.pluginconfig.YouboraConfig.KEY_HOUSEHOLD_ID;
import static com.npaw.youbora.lib6.plugin.Options.KEY_ACCOUNT_CODE;
import static com.npaw.youbora.lib6.plugin.Options.KEY_AD_CAMPAIGN;
import static com.npaw.youbora.lib6.plugin.Options.KEY_CONTENT_CDN;
import static com.npaw.youbora.lib6.plugin.Options.KEY_CONTENT_CHANNEL;
import static com.npaw.youbora.lib6.plugin.Options.KEY_CONTENT_ENCODING_AUDIO_CODEC;
import static com.npaw.youbora.lib6.plugin.Options.KEY_CONTENT_GENRE;
import static com.npaw.youbora.lib6.plugin.Options.KEY_CONTENT_METADATA;
import static com.npaw.youbora.lib6.plugin.Options.KEY_CONTENT_PRICE;
import static com.npaw.youbora.lib6.plugin.Options.KEY_CONTENT_TITLE;
import static com.npaw.youbora.lib6.plugin.Options.KEY_CONTENT_TRANSACTION_CODE;
import static com.npaw.youbora.lib6.plugin.Options.KEY_CONTENT_TYPE;
import static com.npaw.youbora.lib6.plugin.Options.KEY_CUSTOM_DIMENSION_1;
import static com.npaw.youbora.lib6.plugin.Options.KEY_CUSTOM_DIMENSION_2;
import static com.npaw.youbora.lib6.plugin.Options.KEY_DEVICE_BRAND;
import static com.npaw.youbora.lib6.plugin.Options.KEY_DEVICE_CODE;
import static com.npaw.youbora.lib6.plugin.Options.KEY_DEVICE_MODEL;
import static com.npaw.youbora.lib6.plugin.Options.KEY_DEVICE_OS_NAME;
import static com.npaw.youbora.lib6.plugin.Options.KEY_DEVICE_OS_VERSION;
import static com.npaw.youbora.lib6.plugin.Options.KEY_DEVICE_TYPE;
import static com.npaw.youbora.lib6.plugin.Options.KEY_ENABLED;
import static com.npaw.youbora.lib6.plugin.Options.KEY_USERNAME;
import static com.npaw.youbora.lib6.plugin.Options.KEY_USER_EMAIL;
import static com.npaw.youbora.lib6.plugin.Options.KEY_APP_NAME;
import static com.npaw.youbora.lib6.plugin.Options.KEY_APP_RELEASE_VERSION;


public class MainActivity extends AppCompatActivity {

    //Tag for logging.
    private static final String TAG = MainActivity.class.getSimpleName();

    //The url of the source to play
    private static final String SOURCE_URL = "https://cdnapisec.kaltura.com/p/2215841/playManifest/entryId/1_w9zx2eti/format/mpegdash/protocol/https/a.mpd";

    //The id of the entry.
    private static final String ENTRY_ID = "entry_id";
    //The id of the source.
    private static final String MEDIA_SOURCE_ID = "source_id";

    //Youbora analytics Constants - https://developer.nicepeopleatwork.com/apidocs/js6/youbora.Options.html
    public static final String ACCOUNT_CODE = "kalturatest";
    public static final String UNIQUE_USER_NAME = "a@a.com";
    public static final String MEDIA_TITLE = "your_media_title";
    public static final boolean IS_LIVE = false;
    public static final boolean ENABLE_SMART_ADS = true;
    private static final String CAMPAIGN = "your_campaign_name";
    public static final String EXTRA_PARAM_1 = "playKitPlayer";
    public static final String EXTRA_PARAM_2 = "";
    public static final String GENRE = "your_genre";
    public static final String TYPE = "your_type";
    public static final String TRANSACTION_TYPE = "your_transaction_type";
    public static final String YEAR = "your_year";
    public static final String CAST = "your_cast";
    public static final String DIRECTOR = "your_director";
    private static final String OWNER = "your_owner";
    public static final String PARENTAL = "your_parental";
    public static final String PRICE = "your_price";
    public static final String RATING = "your_rating";
    public static final String AUDIO_TYPE = "your_audio_type";
    public static final String AUDIO_CHANNELS = "your_audio_channels";
    public static final String DEVICE = "your_device";
    public static final String QUALITY = "your_quality";
    /**
     Follow this {@link  http://mapi.youbora.com:8081/cdns}
     */
    public static final String CONTENT_CDN_CODE = "your_cdn_code";
    /**
     Follow this {@link http://mapi.youbora.com:8081/devices}
     */
    public static final String DEVICE_CODE = "your_device_code";

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
        PKPluginConfigs pluginConfigs = getYouboraBundle();

        // @Deprecated Set plugin entry to the plugin configs.
        //JsonObject pluginEntry = createYouboraPluginUsingJson(isLive, title);
        //pluginConfigs.setPluginConfig(YouboraPlugin.factory.getName(), pluginEntry);

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
     * Youbora options Bundle (Recommended)
     *
     * Will create {@link PKPluginConfigs} object with {@link YouboraPlugin}.
     *
     * @return - the pluginConfig object that should be passed as parameter when loading the player.
     */
    private PKPluginConfigs getYouboraBundle() {

        //Important!!! First you need to register your plugin.
        PlayKitManager.registerPlugins(this, YouboraPlugin.factory);

        //Initialize PKPluginConfigs object.
        PKPluginConfigs pluginConfigs = new PKPluginConfigs();
        //Initialize Json object that will hold all the configurations for the plugin.

        Bundle optBundle = new Bundle();

        //Youbora config bundle. Main config goes here.
        optBundle.putString(KEY_ACCOUNT_CODE, ACCOUNT_CODE);
        optBundle.putString(KEY_USERNAME, UNIQUE_USER_NAME);
        optBundle.putString(KEY_USER_EMAIL, UNIQUE_USER_NAME);
        optBundle.putString(KEY_APP_NAME, "TestApp");
        optBundle.putString(KEY_APP_RELEASE_VERSION, "v1.0");
        optBundle.putBoolean(KEY_ENABLED, true);

        //Media entry bundle.
        optBundle.putString(KEY_CONTENT_TITLE, MEDIA_TITLE);

        //Optional - Device bundle o/w youbora will decide by its own.
        optBundle.putString(KEY_DEVICE_CODE, DEVICE_CODE);
        optBundle.putString(KEY_DEVICE_BRAND, "Xiaomi");
        optBundle.putString(KEY_DEVICE_MODEL, "Mii3");
        optBundle.putString(KEY_DEVICE_TYPE, "TvBox");
        optBundle.putString(KEY_DEVICE_OS_NAME, "Android/Oreo");
        optBundle.putString(KEY_DEVICE_OS_VERSION, "8.1");

        //Youbora ads configuration bundle.
        optBundle.putString(KEY_AD_CAMPAIGN, CAMPAIGN);

        optBundle.putString(KEY_HOUSEHOLD_ID, "householdId");

        //Configure custom properties here:
        optBundle.putString(KEY_CONTENT_GENRE, GENRE);
        optBundle.putString(KEY_CONTENT_TYPE, TYPE);
        optBundle.putString(KEY_CONTENT_TRANSACTION_CODE, TRANSACTION_TYPE); // NEED TO CHECK
        optBundle.putString(KEY_CONTENT_CDN, CONTENT_CDN_CODE);

        // Create Content Metadata bundle
        Bundle contentMetaDataBundle = new Bundle();
        contentMetaDataBundle.putString(KEY_CONTENT_METADATA_YEAR, YEAR);
        contentMetaDataBundle.putString(KEY_CONTENT_METADATA_CAST, CAST);
        contentMetaDataBundle.putString(KEY_CONTENT_METADATA_DIRECTOR, DIRECTOR);
        contentMetaDataBundle.putString(KEY_CONTENT_METADATA_OWNER, OWNER);
        contentMetaDataBundle.putString(KEY_CONTENT_METADATA_PARENTAL, PARENTAL);
        contentMetaDataBundle.putString(KEY_CONTENT_METADATA_RATING, RATING);
        contentMetaDataBundle.putString(KEY_CONTENT_METADATA_QUALITY, QUALITY);

        // Add Content Metadata bundle to the main Options bundle
        optBundle.putBundle(KEY_CONTENT_METADATA, contentMetaDataBundle);

        optBundle.putString(KEY_CONTENT_PRICE, PRICE);
        optBundle.putString(KEY_CONTENT_ENCODING_AUDIO_CODEC, AUDIO_TYPE); // NEED TO CHECK
        optBundle.putString(KEY_CONTENT_CHANNEL, AUDIO_CHANNELS);  // NEED TO CHECK

        //You can add some extra params here:
        optBundle.putString(KEY_CUSTOM_DIMENSION_1, EXTRA_PARAM_1);
        optBundle.putString(KEY_CUSTOM_DIMENSION_2, EXTRA_PARAM_2);

        //Set plugin entry to the plugin configs.
        pluginConfigs.setPluginConfig(YouboraPlugin.factory.getName(), optBundle);

        return pluginConfigs;
    }

    /**
     * @Deprecated
     *
     * To send the Youbora options as JSON object is deprecated now, instead of this use {@link MainActivity#getYouboraBundle()}
     *
     * Will create {@link PKPluginConfigs} object with {@link YouboraPlugin}.
     *
     * @return - the pluginConfig object that should be passed as parameter when loading the player.
     */
    private PKPluginConfigs createYouboraPluginUsingJson() {

        //Important!!! First you need to register your plugin.
        PlayKitManager.registerPlugins(this, YouboraPlugin.factory);

        //Initialize PKPluginConfigs object.
        PKPluginConfigs pluginConfigs = new PKPluginConfigs();
        //Initialize Json object that will hold all the configurations for the plugin.

        JsonObject pluginEntry = new JsonObject();

        pluginEntry.addProperty("accountCode", "kalturatest");
        pluginEntry.addProperty("username", "a@a.com");
        pluginEntry.addProperty("haltOnError", true);
        pluginEntry.addProperty("enableAnalytics", true);
        pluginEntry.addProperty("enableSmartAds", true);
        pluginEntry.addProperty("appName", "TestApp");
        pluginEntry.addProperty("appReleaseVersion", "v1.0");


        //Optional - Device json o/w youbora will decide by its own.
        JsonObject deviceJson = new JsonObject();
        deviceJson.addProperty("deviceCode", DEVICE_CODE);
        deviceJson.addProperty("brand", "Xiaomi");
        deviceJson.addProperty("model", "Mii3");
        deviceJson.addProperty("type", "TvBox");
        deviceJson.addProperty("osName", "Android/Oreo");
        deviceJson.addProperty("osVersion", "8.1");


        //Media entry json.
        JsonObject mediaEntryJson = new JsonObject();
        //    mediaEntryJson.addProperty("isLive", isLive);
        //    mediaEntryJson.addProperty("title", title);

        //Youbora ads configuration json.
        JsonObject adsJson = new JsonObject();
        adsJson.addProperty("adsExpected", true);
        adsJson.addProperty("campaign", "zzz");

        //Configure custom properties here:
        JsonObject propertiesJson = new JsonObject();
        propertiesJson.addProperty("genre", "");
        propertiesJson.addProperty("type", "");
        propertiesJson.addProperty("transaction_type", "");
        propertiesJson.addProperty("year", "");
        propertiesJson.addProperty("cast", "");
        propertiesJson.addProperty("director", "");
        propertiesJson.addProperty("owner", "");
        propertiesJson.addProperty("parental", "");
        propertiesJson.addProperty("price", "");
        propertiesJson.addProperty("rating", "");
        propertiesJson.addProperty("audioType", "");
        propertiesJson.addProperty("audioChannels", "");
        propertiesJson.addProperty("device", "");
        propertiesJson.addProperty("quality", "");
        propertiesJson.addProperty("contentCdnCode", CONTENT_CDN_CODE);

        //You can add some extra params here:
        JsonObject extraParamJson = new JsonObject();
        extraParamJson.addProperty("param1", "param1");
        extraParamJson.addProperty("param2", "param2");

        //Add all the json objects created before to the pluginEntry json.
        pluginEntry.add("device", deviceJson);
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
        player.addListener(this, YouboraEvent.reportSent, event -> {
            YouboraEvent.YouboraReport reportEvent = event;

            //Get the event name from the report.
            String reportedEventName = reportEvent.reportedEventName;
            Log.i(TAG, "Youbora report sent. Reported event name: " + reportedEventName);
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
