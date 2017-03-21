package com.kaltura.playkit.samples.tvpapistats;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;

import com.google.gson.JsonObject;
import com.kaltura.playkit.PKMediaConfig;
import com.kaltura.playkit.PKMediaEntry;
import com.kaltura.playkit.PKMediaFormat;
import com.kaltura.playkit.PKMediaSource;
import com.kaltura.playkit.PKPluginConfigs;
import com.kaltura.playkit.PlayKitManager;
import com.kaltura.playkit.Player;
import com.kaltura.playkit.plugins.KalturaStatsPlugin;
import com.kaltura.playkit.plugins.TVPAPIAnalyticsPlugin;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    //The url of the source to play
    private static final String SOURCE_URL = "https://cdnapisec.kaltura.com/p/2215841/sp/221584100/playManifest/entryId/1_w9zx2eti/protocol/https/format/applehttp/falvorIds/1_1obpcggb,1_yyuvftfz,1_1xdbzoa6,1_k16ccgto,1_djdf6bk8/a.m3u8";

    //The id of the entry.
    private static final String ENTRY_ID = "entry_id";
    //The id of the source.
    private static final String MEDIA_SOURCE_ID = "source_id";
    private static final String SESSION_ID = "your_session_id";

    //Analytics constants
    private static final String TVPAPI_ANALYTICS_URL = "http://tvpapi-as.ott.kaltura.com/v3_9/gateways/jsonpostgw.aspx?";//Server url
    private static final String FILE_ID = "12345"; //your file id here.
    private static final int PARTNER_ID = 12345; // your partner id here.
    private static final int ANALYTIC_TRIGGER_INTERVAL = 30; //Interval in which analytics report should be triggered (in seconds).
    private static final String SITE_GUID = "123456";
    private static final String API_USER = "your_api_user";
    private static final String DOMAIN_ID = "your_domain_id";
    private static final String UDID = "your_udid";
    private static final String API_PASS = "your_api_pass";
    private static final String LOCALE_USER_STATE = "your_locale_user_state";
    private static final String LOCALE_COUNTRY = "your_locale_country";
    private static final String LOCALE_DEVICE = "your_locale_device";
    private static final String LOCALE_LANGUAGE = "your_locale_language";
    private static final String PLATFORM = "your_platform";

    private Player player;
    private PKMediaConfig mediaConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize media config object.
        createMediaConfig();

        //Initialize PKPluginConfigs object with TVPapiAnalyticsPlugin.
        PKPluginConfigs pluginConfigs = createTVPapiAnalyticsPlugin();

        //Create instance of the player with specified pluginConfigs.
        player = PlayKitManager.loadPlayer(this, pluginConfigs);

        //Add player to the view hierarchy.
        addPlayerToView();

        //Prepare player with media config and start playback.
        startPlayback();

    }

    /**
     * Will create {@link PKPluginConfigs} object with {@link KalturaStatsPlugin}.
     *
     * @return - the pluginConfig object that should be passed as parameter when loading the player.
     */
    private PKPluginConfigs createTVPapiAnalyticsPlugin() {

        //First register your plugin.
        PlayKitManager.registerPlugins(this, TVPAPIAnalyticsPlugin.factory);

        //Initialize PKPluginConfigs object.
        PKPluginConfigs pluginConfigs = new PKPluginConfigs();
        JsonObject pluginEntry = new JsonObject();
        pluginEntry.addProperty("fileId", FILE_ID);
        pluginEntry.addProperty("baseUrl", TVPAPI_ANALYTICS_URL);
        pluginEntry.addProperty("timerInterval", ANALYTIC_TRIGGER_INTERVAL);

        //Initialize user json object and configure it.
        JsonObject userJson = new JsonObject();
        userJson.addProperty("SiteGuid", SITE_GUID);
        userJson.addProperty("ApiUser", API_USER);
        userJson.addProperty("DomainID", DOMAIN_ID);
        userJson.addProperty("UDID", UDID);
        userJson.addProperty("ApiPass", API_PASS);
        userJson.addProperty("Platform", PLATFORM);

        //Initialize locale json object and configure it.
        JsonObject localeJsonObject = new JsonObject();
        localeJsonObject.addProperty("LocaleUserState", LOCALE_USER_STATE);
        localeJsonObject.addProperty("LocaleCountry", LOCALE_COUNTRY);
        localeJsonObject.addProperty("LocaleDevice", LOCALE_DEVICE);
        localeJsonObject.addProperty("LocaleLanguage", LOCALE_LANGUAGE);

        //Add locale json object to user json object with "Locale" as key.
        userJson.add("Locale", localeJsonObject);

        //Add user json object to plugin entry json object with "initObj" as key.
        pluginEntry.add("initObj", userJson);

        //Set plugin entry to the plugin configs.
        pluginConfigs.setPluginConfig(TVPAPIAnalyticsPlugin.factory.getName(), pluginEntry);

        return pluginConfigs;
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
     * Will prepare player with media configurations and start playback.
     */
    private void startPlayback() {
        //Prepare player with media configuration.
        player.prepare(mediaConfig);

        //Start playback.
        player.play();
    }
}
