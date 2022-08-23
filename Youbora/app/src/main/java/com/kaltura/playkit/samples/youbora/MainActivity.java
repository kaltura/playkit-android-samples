package com.kaltura.playkit.samples.youbora;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonArray;
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
import static com.npaw.youbora.lib6.plugin.Options.KEY_APP_NAME;
import static com.npaw.youbora.lib6.plugin.Options.KEY_APP_RELEASE_VERSION;
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

    //Follow this {http://mapi.youbora.com:8081/cdns}
    public static final String CONTENT_CDN_CODE = "your_cdn_code";

    //Follow this {http://mapi.youbora.com:8081/devices}
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

        // MUST to have config
        pluginEntry.addProperty("accountCode", "kalturatest");

        // Backward compatibility DEPRECATED
//        pluginEntry.addProperty("appName", "Kaltura Full demo sample deprecated");
//        pluginEntry.addProperty("appReleaseVersion", "1.0.1 deprecated");

        // App JSON
        JsonObject appJson = new JsonObject();
        appJson.addProperty("appName", "Kaltura Full demo sample");
        appJson.addProperty("appReleaseVersion", "1.0.1");

        // Backward compatibility DEPRECATED
//        pluginEntry.addProperty("userEmail", "test@at.com DEPRECATED");
//        pluginEntry.addProperty("userAnonymousId", "my anonymousId DEPRECATED");
//        pluginEntry.addProperty("userType", "my user type DEPRECATED");
//        pluginEntry.addProperty("userObfuscateIp", false);
//        pluginEntry.addProperty("privacyProtocol", "https DEPRECATED");

        // USER Json
        JsonObject userJson = new JsonObject();
        userJson.addProperty("email", "playkitsample@at.com");
        userJson.addProperty("anonymousId", "my anonymousId");
        userJson.addProperty("type", "my user type");
        userJson.addProperty("obfuscateIp", true);
        userJson.addProperty("privacyProtocol", "https");

        // Miscellaneous Configs
        pluginEntry.addProperty("authToken", "myTokenString");
        pluginEntry.addProperty("authType", "Bearer");
        pluginEntry.addProperty("username", "youboraTest");
        pluginEntry.addProperty("linkedViewId", "my linked View ID");
        pluginEntry.addProperty("urlToParse", "http://abcasd.com");

        pluginEntry.addProperty("householdId", "My householdId");
        pluginEntry.addProperty("host", "a-fds.youborafds01.com");
        pluginEntry.addProperty("autoStart", true);
        pluginEntry.addProperty("autoDetectBackground", true);
        pluginEntry.addProperty("enabled", true);
        pluginEntry.addProperty("forceInit", false);
        pluginEntry.addProperty("offline", false);
        pluginEntry.addProperty("httpSecure", true);
        pluginEntry.addProperty("waitForMetadata", false);

        // Pending Meta Data JSON
        JsonArray pendingMetaDataArray = new JsonArray();
        pendingMetaDataArray.add("title");
        pendingMetaDataArray.add("userName");
        pluginEntry.add("pendingMetadata", pendingMetaDataArray);

        //Youbora ads configuration json.
        JsonObject adsJson = new JsonObject();
        adsJson.addProperty("blockerDetected", false);
        // Create AdMetaData
        JsonObject adMetaData = new JsonObject();
        adMetaData.addProperty("year", "2022");
        adMetaData.addProperty("cast", "cast 2022");
        adMetaData.addProperty("director", "director 2022");
        adMetaData.addProperty("owner", "owner 2022");
        adMetaData.addProperty("parental", "parental 2022");
        adMetaData.addProperty("rating", "rating 2022");
        adMetaData.addProperty("device", "device 2022");
        adMetaData.addProperty("audioChannels", "audioChannels 2022");
        adsJson.add("metadata", adMetaData);

        adsJson.addProperty("campaign", "ad campaign 2022");
        adsJson.addProperty("title", "ad title 2022");
        adsJson.addProperty("resource", "resource 2022");
        adsJson.addProperty("givenBreaks", 5);
        adsJson.addProperty("expectedBreaks", 4);
        // Create expectedPattern for Ads
        JsonObject expectedPatternJson = new JsonObject();
        JsonArray preRoll = new JsonArray();
        preRoll.add(2);
        JsonArray midRoll = new JsonArray();
        midRoll.add(1);
        midRoll.add(4);
        JsonArray postRoll = new JsonArray();
        postRoll.add(3);
        expectedPatternJson.add("pre", preRoll);
        expectedPatternJson.add("mid", midRoll);
        expectedPatternJson.add("post", postRoll);
        adsJson.add("expectedPattern", expectedPatternJson);
        // create adBreaksTime
        JsonArray adBreaksTimeArray = new JsonArray();
        adBreaksTimeArray.add(0);
        adBreaksTimeArray.add(25);
        adBreaksTimeArray.add(60);
        adBreaksTimeArray.add(75);
        adsJson.add("adBreaksTime", adBreaksTimeArray);
        adsJson.addProperty("adGivenAds", 7);
        adsJson.addProperty("adCreativeId", "ad creativeId");
        adsJson.addProperty("adProvider", "ad provider");
        // Create Ad Custom Dimensions
        JsonObject adCustomDimensions = new JsonObject();
        adCustomDimensions.addProperty("param1" , "my adCustomDimension1");
        adCustomDimensions.addProperty("10" , "my adCustomDimension10");
        adsJson.add("adCustomDimension", adCustomDimensions);

        // Error JSON
        JsonObject errorJson = new JsonObject();
        JsonArray ignoredErrors = new JsonArray();
        ignoredErrors.add("Asset Not Found.");
        errorJson.add("errorsIgnore", ignoredErrors);

        // Create Network JSON object
        JsonObject networkJson = new JsonObject();
        networkJson.addProperty("networkConnectionType", "Wireless");
        networkJson.addProperty("networkIP", "18212.16218.01.012132");
        networkJson.addProperty("networkIsp", "XYZ TTML");

        // Create Parse JSON object
        JsonObject parseJson = new JsonObject();
//        parseJson.addProperty("parseManifest", true); // Deprecated way to pass value
        JsonObject parseManifestJson = new JsonObject(); // New way to pass value
        parseManifestJson.addProperty("manifest", true);
        JsonObject manifestAuthMap = new JsonObject();
        manifestAuthMap.addProperty("AUTH1", "VALUE1");
        manifestAuthMap.addProperty("AUTH2", "VALUE2");
        manifestAuthMap.addProperty("AUTH3", "VALUE3");
        manifestAuthMap.addProperty("AUTH4", "VALUE4");
        parseManifestJson.add("auth", manifestAuthMap);
        parseJson.add("parseManifest", parseManifestJson);

        parseJson.addProperty("parseCdnSwitchHeader", true);

//        parseJson.addProperty("parseCdnNode", true); // Deprecated way to pass value
//        JsonArray cdnNodeListArray = new JsonArray(); // Deprecated way to pass value
//        cdnNodeListArray.add("Akamai");
//        cdnNodeListArray.add("Cloudfront");
//        cdnNodeListArray.add("NosOtt");
//        parseJson.add("parseCdnNodeList", cdnNodeListArray);

        JsonObject cdnNodeJson = new JsonObject(); // New way to pass value
        cdnNodeJson.addProperty("requestDebugHeaders", true);
        JsonArray cdnNodeListJson = new JsonArray();
        cdnNodeListJson.add("Akamai");
        cdnNodeListJson.add("Cloudfront");
        cdnNodeListJson.add("NosOtt");
        cdnNodeJson.add("parseCdnNodeList", cdnNodeListJson);

        parseJson.add("cdnNode", cdnNodeJson);

        parseJson.addProperty("parseCdnNameHeader", "x-cdn");
        parseJson.addProperty("parseNodeHeader", "x-node");
        parseJson.addProperty("parseCdnTTL", 60);

        //Optional - Device json o/w youbora will decide by its own.
        JsonObject deviceJson = new JsonObject();
        deviceJson.addProperty("deviceCode", DEVICE_CODE);
        deviceJson.addProperty("deviceBrand", "Brand Xiaomi");
        deviceJson.addProperty("deviceCode", "Code Xiaomi");
        deviceJson.addProperty("deviceId", "Device ID Xiaomi");
        deviceJson.addProperty("deviceEdId", "EdId Xiaomi");
        deviceJson.addProperty("deviceModel", "Model MI3");
        deviceJson.addProperty("deviceOsName", "Android/Oreo");
        deviceJson.addProperty("deviceOsVersion", "8.1");
        deviceJson.addProperty("deviceType", "TvBox TYPE");
        deviceJson.addProperty("deviceName", "TvBox");
        deviceJson.addProperty("deviceIsAnonymous", "TvBox");

        //Media entry json. [Content JSON]
        JsonObject mediaEntryJson = new JsonObject();
        //mediaEntryJson.addProperty("isLive", isLive); // IT's REMOVED NOW USE `isLive` class instead
        JsonObject isLiveJson = new JsonObject();
        isLiveJson.addProperty("isLiveContent", true);
        isLiveJson.addProperty("noSeek", true);
        isLiveJson.addProperty("noMonitor", true);
        mediaEntryJson.add("isLive", isLiveJson);

//        mediaEntryJson.addProperty("isLive", true);

        mediaEntryJson.addProperty("contentBitrate", 480000);
        JsonObject encodingJson = new JsonObject();
        encodingJson.addProperty("videoCodec", "video codec name");
        JsonObject codecSettingsMap = new JsonObject();
        codecSettingsMap.addProperty("AV1_KEY", "AV1_VALUE");
        codecSettingsMap.addProperty("HEVC_KEY", "HEVC_VALUE");
        codecSettingsMap.addProperty("AC3_KEY", "AC3_VALUE");
        encodingJson.add("contentEncodingCodecSettings", codecSettingsMap);
        mediaEntryJson.add("encoding", encodingJson);

        // Create Content MetaData
        JsonObject contentMetaData = new JsonObject();
        contentMetaData.addProperty("year", "2022");
        contentMetaData.addProperty("cast", "cast 2022");
        contentMetaData.addProperty("director", "director 2022");
        contentMetaData.addProperty("owner", "owner 2022");
        contentMetaData.addProperty("parental", "parental 2022");
        contentMetaData.addProperty("rating", "rating 2022");
        contentMetaData.addProperty("device", "device 2022");
        contentMetaData.addProperty("audioChannels", "audioChannels 2022");
        mediaEntryJson.add("metadata", contentMetaData);
        // Create Content Custom Dimensions
        JsonObject contentCustomDimensions = new JsonObject();
        contentCustomDimensions.addProperty("param1", "param1");
        contentCustomDimensions.addProperty("param2", "param2");
        mediaEntryJson.add("customDimensions", contentCustomDimensions);

        JsonObject sessionMetricsMap = new JsonObject();
        sessionMetricsMap.addProperty("sessionKey", "sessionValue");
        JsonObject sessionJson = new JsonObject();
        sessionJson.add("metrics", sessionMetricsMap);
        //Configure custom properties here: DEPRECATED in the Youbora Plugin
//        JsonObject propertiesJson = new JsonObject();
//        propertiesJson.addProperty("genre", "");
//        propertiesJson.addProperty("type", "");
//        propertiesJson.addProperty("transactionType", "TransactionType-1");
//        propertiesJson.addProperty("year", "");
//        propertiesJson.addProperty("cast", "");
//        propertiesJson.addProperty("director", "");
//        propertiesJson.addProperty("owner", "");
//        propertiesJson.addProperty("parental", "");
//        propertiesJson.addProperty("price", "");
//        propertiesJson.addProperty("rating", "");
//        propertiesJson.addProperty("audioType", "");
//        propertiesJson.addProperty("audioChannels", "");
//        propertiesJson.addProperty("device", "");
//        propertiesJson.addProperty("quality", "");
//        propertiesJson.addProperty("contentCdnCode", CONTENT_CDN_CODE);

        //You can add some extra params here: DEPRECATED in the plugin
//        JsonObject extraParamJson = new JsonObject();
//        extraParamJson.addProperty("1", "param1");
//        extraParamJson.addProperty("2", "param2");

        //Add all the json objects created before to the pluginEntry json.
        pluginEntry.add("user", userJson);
        pluginEntry.add("parse", parseJson);
        pluginEntry.add("network", networkJson);
        pluginEntry.add("device", deviceJson);
        pluginEntry.add("media", mediaEntryJson);
        pluginEntry.add("ad", adsJson);
        pluginEntry.add("app", appJson);
        pluginEntry.add("errors", errorJson);
        pluginEntry.add("session", sessionJson);
//        pluginEntry.add("properties", propertiesJson);
//        pluginEntry.add("extraParams", extraParamJson);

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

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
        if (player != null) {
            player.onApplicationPaused();
        }
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();

        if (player != null && player.getView() != null &&  player.getView().getChildCount() > 0) {
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
