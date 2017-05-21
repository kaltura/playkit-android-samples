package com.kaltura.playkit.samples.changemedia;

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
import com.kaltura.playkit.plugins.Youbora.YouboraEvent;
import com.kaltura.playkit.plugins.Youbora.YouboraPlugin;
import com.kaltura.playkit.plugins.ads.ima.IMAConfig;
import com.kaltura.playkit.plugins.ads.ima.IMAPlugin;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    //The url of the first source to play
    private static final String FIRST_SOURCE_URL = "http://qa-apache-testing-ubu-01.dev.kaltura.com/p/1091/sp/109100/playManifest/entryId/0_2hv7lhga/flavorIds/0_cxckre0q,0_yual4izy,0_qswhyfht,0_ozgle3np/format/applehttp/protocol/http/a.m3u8";//"http://qa-apache-testing-ubu-01.dev.kaltura.com/p/1091/sp/109100/playManifest/entryId/0_o757mkwo/flavorIds/0_ofkik57d,0_e44oqzzy,0_knkzmv8z,0_effkqka8/format/applehttp/protocol/http/a.m3u8?"; //"https://devimages.apple.com.edgekey.net/streaming/examples/bipbop_16x9/bipbop_16x9_variant.m3u8";
    //The url of the second source to play
    private static final String SECOND_SOURCE_URL = "http://www.html5videoplayer.net/videos/toystory.mp4";//"http://qa-apache-testing-ubu-01.dev.kaltura.com/p/1091/sp/109100/playManifest/entryId/0_kf6dw4jr/flavorIds/0_olodnwem,0_k1xomk0s,0_89mswgn1/format/applehttp/protocol/http/a.m3u8";//"https://cdnapisec.kaltura.com/p/2215841/sp/221584100/playManifest/entryId/1_w9zx2eti/protocol/https/format/url/falvorIds/1_1obpcggb,1_yyuvftfz,1_1xdbzoa6,1_k16ccgto,1_djdf6bk8/a.mp4";

    private static final String AD_TAG_URL = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/ad_rule_samples&ciu_szs=300x250&ad_rule=1&impl=s&gdfp_req=1&env=vp&output=vmap&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ar%3Dpreonlybumper&cmsid=496&vid=short_onecue&correlator=";//"https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dskippablelinear&correlator=";
    private static final String NEW_AD_TAG_URL = "http://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/ad_rule_samples&ciu_szs=300x250&ad_rule=1&impl=s&gdfp_req=1&env=vp&output=xml_vmap1&unviewed_position_start=1&cust_params=sample_ar%3Dpremidpost&cmsid=496&vid=short_onecue&correlator=[timestamp]";
            //"https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/ad_rule_samples&ciu_szs=300x250&ad_rule=1&impl=s&gdfp_req=1&env=vp&output=xml_vmap1&unviewed_position_start=1&cust_params=sample_ar%3Dpremidpost&cmsid=496&vid=short_onecue&correlator=[timestamp]";//"https://pubads.g.doubleclick.net/gampad/ads?sz=640x360&iu=/6062/iab_vast_samples/skippable&ciu_szs=300x250,728x90&impl=s&gdfp_req=1&env=vp&output=xml_vast2&unviewed_position_start=1&url=[referrer_url]&correlator=[timestamp]";
    private static final int PREFERRED_AD_BITRATE = 600;
    //id of the first entry
    private static final String FIRST_ENTRY_ID = "entry_id_1";
    //id of the second entry
    private static final String SECOND_ENTRY_ID = "entry_id_2";
    //id of the first media source.
    private static final String FIRST_MEDIA_SOURCE_ID = "source_id_1";
    //id of the second media source.
    private static final String SECOND_MEDIA_SOURCE_ID = "source_id_2";


    //Youbora analytics Constants
    public static final String ACCOUNT_CODE = "kalturatest";
    public static final String USER_NAME = "TEST_HLS_CHANGE_MEIDA";
    public static final boolean ENABLE_SMART_ADS = true;
    public static final String MEDIA_TITLE = "media101";
    private static final String CAMPAIGN = "camp101";
    public static final String EXTRA_PARAM_1 = "gilad";
    public static final String EXTRA_PARAM_2 = "nadav";
    public static final String GENRE = "aaa";
    public static final String TYPE = "video";
    public static final boolean IS_LIVE = true;
    public static final String TRANSACTION_TYPE = "trasnsaction_code";
    public static final String YEAR = "2017";
    public static final String CAST = "xxx";
    public static final String DIRECTOR = "zzz";
    private static final String OWNER = "eee";
    public static final String PARENTAL = "yes";
    public static final String PRICE = "100";
    public static final String RATING = "9";
    public static final String AUDIO_TYPE = "NA";
    public static final String AUDIO_CHANNELS = "NA";
    public static final String DEVICE = "TV";
    public static final String QUALITY = "HD";

    private Player player;
    private PKMediaConfig mediaConfig;
    private Button playPauseButton;
    private Button seek30;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Create plugin configurations.
        PKPluginConfigs pluginConfigs = new PKPluginConfigs();
        createIMAPlugin(pluginConfigs);
        createYouboraPlugin(pluginConfigs);

        //First. Create PKMediaConfig object.
        mediaConfig = new PKMediaConfig();

        //Create instance of the player.
        player = PlayKitManager.loadPlayer(this, pluginConfigs);

        //Add player to the view hierarchy.
        addPlayerToView();

        //Add simple play/pause button.
        addPlayPauseButton();
        addSeek30Button();

        //Init change media button which will switch between entries.
        initChangeMediaButton();

        //Prepare the first entry.
        prepareFirstEntry();
        player.play();
    }

    /**
     * Create IMAPlugin object.
     *
     * @return - {@link PKPluginConfigs} object with IMAPlugin.
     */
    private PKPluginConfigs createIMAPlugin(PKPluginConfigs pluginConfigs) {

        //First register your IMAPlugin.
        PlayKitManager.registerPlugins(this, IMAPlugin.factory);

        //Initialize imaConfigs object.
        IMAConfig imaConfigs = new IMAConfig();

        //Configure ima.
        imaConfigs.setAdTagURL(AD_TAG_URL);
        imaConfigs.setVideoBitrate(PREFERRED_AD_BITRATE);

        //Convert imaConfigs to jsonObject.
        JsonObject imaConfigJsonObject = imaConfigs.toJSONObject();

        //Set jsonObject to the main pluginConfigs object.
        pluginConfigs.setPluginConfig(IMAPlugin.factory.getName(), imaConfigJsonObject);

        //Return created PluginConfigs object.
        return pluginConfigs;
    }

    /**
     * Initialize the changeMedia button. On click it will change media.
     */
    private void initChangeMediaButton() {
        //Get reference to the button.
        Button changeMediaButton = (Button) this.findViewById(R.id.change_media_button);
        //Set click listener.
        changeMediaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Change media.
                changeMedia();
            }
        });
    }

    /**
     * Will switch between entries. If the first entry is currently active it will
     * prepare the second one. Otherwise it will prepare the first one.
     */
    private void changeMedia() {

        //Before changing media we must call stop on the player.
        player.stop();

        //Initialize imaConfigs object.
        IMAConfig imaConfig = new IMAConfig();



        //Check if id of the media entry that is set in mediaConfig.
        if (mediaConfig.getMediaEntry().getId().equals(FIRST_ENTRY_ID)) {
            //Configure ima.x
            imaConfig.setAdTagURL(NEW_AD_TAG_URL);
            imaConfig.setVideoBitrate(PREFERRED_AD_BITRATE);
            player.updatePluginConfig(IMAPlugin.factory.getName(), imaConfig.toJSONObject());


            JsonObject youboraPluginEntry = new JsonObject();

            //Youbora config json. Main config goes here.
            //JsonObject youboraConfigJson = new JsonObject();
            youboraPluginEntry.addProperty("accountCode", ACCOUNT_CODE);
            youboraPluginEntry.addProperty("username", USER_NAME);
            youboraPluginEntry.addProperty("enableAnalytics", true);
            youboraPluginEntry.addProperty("enableSmartAds", ENABLE_SMART_ADS);


            //Media entry json.
            JsonObject mediaEntryJson = new JsonObject();
            mediaEntryJson.addProperty("isLive", false);
            mediaEntryJson.addProperty("title", "change_media");

            //Youbora ads configuration json.
            JsonObject adsJson = new JsonObject();
            adsJson.addProperty("adsExpected", true);
            adsJson.addProperty("campaign", "change_media1");

            //Configure custom properties here:
            JsonObject propertiesJson = new JsonObject();
            propertiesJson.addProperty("genre", GENRE);
            propertiesJson.addProperty("type", "CHANGE_MEDIA");
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
            //propertiesJson.addProperty("device", DEVICE);
            propertiesJson.addProperty("quality", "SD");

            //You can add some extra params here:
            JsonObject extraParamJson = new JsonObject();
            extraParamJson.addProperty("param1", "change_media11");
            extraParamJson.addProperty("param2", "change_media12");

            //Add all the json objects created before to the pluginEntry json.
            //youboraPluginEntry.add("youboraConfig", youboraConfigJson);
            youboraPluginEntry.add("media", mediaEntryJson);
            youboraPluginEntry.add("ads", adsJson);
            youboraPluginEntry.add("properties", propertiesJson);
            youboraPluginEntry.add("extraParams", extraParamJson);
            player.updatePluginConfig(YouboraPlugin.factory.getName(), youboraPluginEntry);
            //If first one is active, prepare second one.
            prepareSecondEntry();
        } else {
            //If the second one is active, prepare the first one.
            //Configure ima.
            imaConfig.setAdTagURL(AD_TAG_URL);
            imaConfig.setVideoBitrate(PREFERRED_AD_BITRATE);
            player.updatePluginConfig(IMAPlugin.factory.getName(), imaConfig.toJSONObject());
            prepareFirstEntry();
        }
        player.play();
        //Just reset the playPauseButton text to "Play".
        resetPlayPauseButtonToPlayText();
    }



    /**
     * Prepare the first entry.
     */
    private void prepareFirstEntry() {
        //Second. Create PKMediaEntry object.
        PKMediaEntry mediaEntry = createFirstMediaEntry();

        //Add it to the mediaConfig.
        mediaConfig.setMediaEntry(mediaEntry);
        //Prepare player with media configuration.
        player.prepare(mediaConfig);

    }

    /**
     * Prepare the second entry.
     */
    private void prepareSecondEntry() {
        //Second. Create PKMediaEntry object.
        PKMediaEntry mediaEntry = createSecondMediaEntry();

        //Add it to the mediaConfig.
        mediaConfig.setMediaEntry(mediaEntry);

        //Prepare player with media configuration.
        player.prepare(mediaConfig);
    }

    /**
     * Create {@link PKMediaEntry} with minimum necessary data.
     *
     * @return - the {@link PKMediaEntry} object.
     */
    private PKMediaEntry createFirstMediaEntry() {
        //Create media entry.
        PKMediaEntry mediaEntry = new PKMediaEntry();

        //Set id for the entry.
        mediaEntry.setId(FIRST_ENTRY_ID);

        //Set media entry type. It could be Live,Vod or Unknown.
        //For now we will use Unknown.
        mediaEntry.setMediaType(PKMediaEntry.MediaEntryType.Unknown);

        //Create list that contains at least 1 media source.
        //Each media entry can contain a couple of different media sources.
        //All of them represent the same content, the difference is in it format.
        //For example same entry can contain PKMediaSource with dash and another
        // PKMediaSource can be with hls. The player will decide by itself which source is
        // preferred for playback.
        List<PKMediaSource> mediaSources = createFirstMediaSources();

        //Set media sources to the entry.
        mediaEntry.setSources(mediaSources);

        return mediaEntry;
    }

    /**
     * Create {@link PKMediaEntry} with minimum necessary data.
     *
     * @return - the {@link PKMediaEntry} object.
     */
    private PKMediaEntry createSecondMediaEntry() {
        //Create media entry.
        PKMediaEntry mediaEntry = new PKMediaEntry();

        //Set id for the entry.
        mediaEntry.setId(SECOND_ENTRY_ID);

        //Set media entry type. It could be Live,Vod or Unknown.
        //For now we will use Unknown.
        mediaEntry.setMediaType(PKMediaEntry.MediaEntryType.Unknown);

        //Create list that contains at least 1 media source.
        //Each media entry can contain a couple of different media sources.
        //All of them represent the same content, the difference is in it format.
        //For example same entry can contain PKMediaSource with dash and another
        // PKMediaSource can be with hls. The player will decide by itself which source is
        // preferred for playback.
        List<PKMediaSource> mediaSources = createSecondMediaSources();

        //Set media sources to the entry.
        mediaEntry.setSources(mediaSources);

        return mediaEntry;
    }

    /**
     * Create list of {@link PKMediaSource}.
     *
     * @return - the list of sources.
     */
    private List<PKMediaSource> createFirstMediaSources() {
        //Init list which will hold the PKMediaSources.
        List<PKMediaSource> mediaSources = new ArrayList<>();

        //Create new PKMediaSource instance.
        PKMediaSource mediaSource = new PKMediaSource();

        //Set the id.
        mediaSource.setId(FIRST_MEDIA_SOURCE_ID);

        //Set the content url. In our case it will be link to hls source(.m3u8).
        mediaSource.setUrl(FIRST_SOURCE_URL);

        //Set the format of the source. In our case it will be hls.
        mediaSource.setMediaFormat(PKMediaFormat.hls);

        //Add media source to the list.
        mediaSources.add(mediaSource);

        return mediaSources;
    }

    /**
     * Create list of {@link PKMediaSource}.
     *
     * @return - the list of sources.
     */
    private List<PKMediaSource> createSecondMediaSources() {
        //Init list which will hold the PKMediaSources.
        List<PKMediaSource> mediaSources = new ArrayList<>();

        //Create new PKMediaSource instance.
        PKMediaSource mediaSource = new PKMediaSource();

        //Set the id.
        mediaSource.setId(SECOND_MEDIA_SOURCE_ID);

        //Set the content url. In our case it will be link to mp4 source(.mp4).
        mediaSource.setUrl(SECOND_SOURCE_URL);

        //Set the format of the source. In our case it will be mp4.
        mediaSource.setMediaFormat(PKMediaFormat.mp4);

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

    private void addSeek30Button() {
        //Get reference to the play/pause button.
        seek30 = (Button) this.findViewById(R.id.button111);
        //Add clickListener.
        seek30.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (player != null) {
                    player.seekTo(player.getCurrentPosition() + 30000);
                }
            }
        });
    }

    /**
     * Just reset the play/pause button text to "Play".
     */
    private void resetPlayPauseButtonToPlayText() {
        playPauseButton.setText(R.string.play_text);
    }

    private PKPluginConfigs createYouboraPlugin(PKPluginConfigs pluginConfigs) {

        //Important!!! First you need to register your plugin.
        PlayKitManager.registerPlugins(this, YouboraPlugin.factory);

        //Initialize PKPluginConfigs object.

        //Initialize Json object that will hold all the configurations for the plugin.
        JsonObject pluginEntry = new JsonObject();

        //Youbora config json. Main config goes here.
        //JsonObject youboraConfigJson = new JsonObject();
        pluginEntry.addProperty("accountCode", ACCOUNT_CODE);
        pluginEntry.addProperty("username", USER_NAME);
        pluginEntry.addProperty("username", USER_NAME);
        pluginEntry.addProperty("enableAnalytics", true);
        pluginEntry.addProperty("enableSmartAds", ENABLE_SMART_ADS);


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
        //pluginEntry.add("youboraConfig", youboraConfigJson);
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
                String reportedEventName = reportEvent.getReportedEventName();
                Log.i(TAG, "Youbora report sent. Reported event name: " + reportedEventName);
            }
            //Event subscription.
        }, YouboraEvent.Type.REPORT_SENT);
    }
}