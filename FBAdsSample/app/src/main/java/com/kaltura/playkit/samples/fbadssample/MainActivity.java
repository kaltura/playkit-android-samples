package com.kaltura.playkit.samples.fbadssample;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.kaltura.playkit.PKMediaConfig;
import com.kaltura.playkit.PKMediaEntry;
import com.kaltura.playkit.PKMediaFormat;
import com.kaltura.playkit.PKMediaSource;
import com.kaltura.playkit.PKPluginConfigs;
import com.kaltura.playkit.PlayKitManager;
import com.kaltura.playkit.Player;
import com.kaltura.playkit.PlayerEvent;
import com.kaltura.playkit.ads.AdController;
import com.kaltura.playkit.plugins.ads.AdEvent;
import com.kaltura.playkit.plugins.ads.AdInfo;
import com.kaltura.playkit.plugins.ads.AdPositionType;
import com.kaltura.playkit.plugins.fbads.fbinstream.FBInStreamAd;
import com.kaltura.playkit.plugins.fbads.fbinstream.FBInStreamAdBreak;
import com.kaltura.playkit.plugins.fbads.fbinstream.FBInstreamConfig;
import com.kaltura.playkit.plugins.fbads.fbinstream.FBInstreamPlugin;
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

    //Media entry configuration constants.
    private static final String SOURCE_URL = "https://bitdash-a.akamaihd.net/content/sintel/hls/playlist.m3u8";
    private static final String ENTRY_ID = "entry_id";
    private static final String MEDIA_SOURCE_ID = "source_id";

    private Player player;
    private PKMediaConfig mediaConfig;
    private Button playPauseButton;
    private Button seekButton;

    //Youbora analytics Constants
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize media config object.
        createMediaConfig();

        //Initialize plugin configuration object.
        PKPluginConfigs pluginConfigs = new PKPluginConfigs();

        //Create plugin configurations.
        PKPluginConfigs setPlugins = createFBInStreamPlugin(pluginConfigs);

        setYouboraConfigBundle(pluginConfigs);

        //Create instance of the player with plugin configurations.
        player = PlayKitManager.loadPlayer(this, pluginConfigs);

        //Subscribe to the ad events.
        subscribeToAdEvents();

        //Add player to the view hierarchy.
        addPlayerToView();

        //Add simple play/pause button.
        addPlayPauseButton();
        addSeekButton();
        //Prepare player with media configuration.
        player.getSettings().setAllowCrossProtocolRedirect(true);
        player.prepare(mediaConfig);
        player.play();
    }

    /**
     * Create FBInStreamPlugin object.
     *
     * @return - {@link PKPluginConfigs} object with FBInStreamPlugin.
     */
    private PKPluginConfigs createFBInStreamPlugin(PKPluginConfigs pluginConfigs) {

        //First register FBIntreamPlugin.
        PlayKitManager.registerPlugins(this, FBInstreamPlugin.factory);

        addFBInStreamPluginConfig(pluginConfigs);

        //Return created PluginConfigs object.
        return pluginConfigs;
    }

    /**
     * Will subscribe to ad events.
     * For simplicity, in this example we will show subscription to the couple of events.
     * For the full list of ad events you can check our documentation.
     * !!!Note, we will receive only ad events, we subscribed to.
     */
    private void subscribeToAdEvents() {

        player.addListener(this, AdEvent.started, event -> {
            //Some events holds additional data objects in them.
            //In order to get access to this object you need first cast event to
            //the object it belongs to. You can learn more about this kind of objects in
            //our documentation.
            AdEvent.AdStartedEvent adStartedEvent = event;

            //Then you can use the data object itself.
            AdInfo adInfo = adStartedEvent.adInfo;

            //Print to log content type of this ad.
            Log.d(TAG, "ad event received: " + event.eventType().name()
                    + ". Additional info: ad content type is: "
                    + adInfo.getAdContentType());
        });

        player.addListener(this, AdEvent.contentResumeRequested, event -> {
            Log.d(TAG, "ADS_PLAYBACK_ENDED");
        });

        player.addListener(this, AdEvent.adPlaybackInfoUpdated, event -> {
            AdEvent.AdPlaybackInfoUpdated playbackInfoUpdated = event;
            Log.d(TAG, "AD_PLAYBACK_INFO_UPDATED  = " + playbackInfoUpdated.width + "/" + playbackInfoUpdated.height + "/" + playbackInfoUpdated.bitrate);
        });

        player.addListener(this, AdEvent.skippableStateChanged, event -> {
            Log.d(TAG, "SKIPPABLE_STATE_CHANGED");
        });

        player.addListener(this, AdEvent.adRequested, event -> {
            AdEvent.AdRequestedEvent adRequestEvent = event;
            Log.d(TAG, "AD_REQUESTED adtag = " + adRequestEvent.adTagUrl);
        });

        player.addListener(this, AdEvent.playHeadChanged, event -> {
            AdEvent.AdPlayHeadEvent adEventProress = event;
            //Log.d(TAG, "received AD PLAY_HEAD_CHANGED " + adEventProress.adPlayHead);
        });


        player.addListener(this, AdEvent.adBreakStarted, event -> {
            Log.d(TAG, "AD_BREAK_STARTED");
        });

        player.addListener(this, AdEvent.cuepointsChanged, event -> {
            AdEvent.AdCuePointsUpdateEvent cuePointsList = event;
            Log.d(TAG, "AD_CUEPOINTS_UPDATED HasPostroll = " + cuePointsList.cuePoints.hasPostRoll());
        });

        player.addListener(this, AdEvent.loaded, event -> {
            AdEvent.AdLoadedEvent adLoadedEvent = event;
//            Log.d(TAG, "AD_LOADED " + adLoadedEvent.adInfo.getAdIndexInPod() + "/" + adLoadedEvent.adInfo.getTotalAdsInPod());
        });

        player.addListener(this, AdEvent.started, event -> {
            AdEvent.AdStartedEvent adStartedEvent = event;
            Log.d(TAG, "AD_STARTED w/h - " + adStartedEvent.adInfo.getAdWidth() + "/" + adStartedEvent.adInfo.getAdHeight());
        });

        player.addListener(this, AdEvent.resumed, event -> {
            Log.d(TAG, "AD_RESUMED");
        });

        player.addListener(this, AdEvent.paused, event -> {
            Log.d(TAG, "AD_PAUSED");
        });

        player.addListener(this, AdEvent.skipped, event -> {
            Log.d(TAG, "AD_SKIPPED");
        });

        player.addListener(this, AdEvent.allAdsCompleted, event -> {
            Log.d(TAG, "AD_ALL_ADS_COMPLETED");
        });

        player.addListener(this, AdEvent.completed, event -> {
            Log.d(TAG, "AD_COMPLETED");
        });

        player.addListener(this, AdEvent.adBreakEnded, event -> {
            Log.d(TAG, "AD_BREAK_ENDED");
        });

        player.addListener(this, AdEvent.adClickedEvent, event -> {
            AdEvent.AdClickedEvent advtClickEvent = event;
            Log.d(TAG, "AD_CLICKED url = " + advtClickEvent.clickThruUrl);
        });

        player.addListener(this, AdEvent.error, event -> {
            AdEvent.Error adError = event;
            Log.e(TAG, "AD_ERROR : " + adError.error.errorType.name());
        });

        player.addListener(this, PlayerEvent.error, event -> {
            Log.e(TAG, "PLAYER ERROR " + event.error.message);
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

        mediaConfig.setStartPosition(0L);
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
        mediaEntry.setMediaType(PKMediaEntry.MediaEntryType.Vod);

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
        playPauseButton = this.findViewById(R.id.play_pause_button);
        //Add clickListener.
        playPauseButton.setOnClickListener(v -> {
            if (player == null) {
                return;
            }
            AdController adController = player.getController(AdController.class);
            if (player.isPlaying() || adController != null && adController.isAdPlaying()) {
                //If player is playing, change text of the button and pause.
                playPauseButton.setText(R.string.play_text);
                player.pause();
            } else {
                //If player is not playing, change text of the button and play.
                playPauseButton.setText(R.string.pause_text);
                player.play();
            }
        });
    }

    private void addSeekButton() {

        seekButton = this.findViewById(R.id.seekTo0);
        seekButton.setOnClickListener(v -> {
            if (player == null) {
                return;
            }
            AdController adController = player.getController(AdController.class);
            if (adController != null && adController.isAdPlaying()) {
               return;
            } else {
                player.seekTo(0L);
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

    private void addFBInStreamPluginConfig(PKPluginConfigs config) {
        List<FBInStreamAd> preRollFBInStreamAdList = new ArrayList<>();
        FBInStreamAd preRoll1 = new FBInStreamAd("156903085045437_239184776817267");
        FBInStreamAd preRoll2 = new FBInStreamAd("156903085045437_239184776817267");
        preRollFBInStreamAdList.add(preRoll1);
        preRollFBInStreamAdList.add(preRoll2);
        FBInStreamAdBreak preRollAdBreak = new FBInStreamAdBreak(AdPositionType.PRE_ROLL, 0, preRollFBInStreamAdList);

        List<FBInStreamAd> midRoll1FBInStreamAdList = new ArrayList<>();
        FBInStreamAd midRoll1 = new FBInStreamAd("156903085045437_239184776817267");
        midRoll1FBInStreamAdList.add(midRoll1);
        FBInStreamAd midRoll11 = new FBInStreamAd("156903085045437_239184776817267");
        midRoll1FBInStreamAdList.add(midRoll11);
        FBInStreamAd midRoll111 = new FBInStreamAd("156903085045437_239184776817267");
        midRoll1FBInStreamAdList.add(midRoll111);
        FBInStreamAdBreak midRoll1AdBreak = new FBInStreamAdBreak(AdPositionType.MID_ROLL, 15000, midRoll1FBInStreamAdList);


        List<FBInStreamAd> midRoll2FBInStreamAdList = new ArrayList<>();
        FBInStreamAd midRoll2 = new FBInStreamAd("156903085045437_239184776817267");
        midRoll2FBInStreamAdList.add(midRoll2);
        FBInStreamAdBreak midRoll2AdBreak = new FBInStreamAdBreak(AdPositionType.MID_ROLL, 30000, midRoll2FBInStreamAdList);

        List<FBInStreamAd> postRollFBInStreamAdList = new ArrayList<>();
        FBInStreamAd postRoll1 = new FBInStreamAd("156903085045437_239184776817267");
        postRollFBInStreamAdList.add(postRoll1);
        FBInStreamAdBreak postRollAdBreak = new FBInStreamAdBreak(AdPositionType.POST_ROLL, Long.MAX_VALUE, postRollFBInStreamAdList);

        List<FBInStreamAdBreak> fbInStreamAdBreakList = new ArrayList<>();
        fbInStreamAdBreakList.add(preRollAdBreak);
        fbInStreamAdBreakList.add(midRoll1AdBreak);
        fbInStreamAdBreakList.add(midRoll2AdBreak);
        fbInStreamAdBreakList.add(postRollAdBreak);

        //"294d7470-4781-4795-9493-36602bf29231");//("7450a453-4ba6-464b-85b6-6f319c7f7326");
        FBInstreamConfig fbInstreamConfig = new FBInstreamConfig(fbInStreamAdBreakList).enableDebugMode(true).
                setTestDevice("294d7470-4781-4795-9493-36602bf29231").
                setAlwaysStartWithPreroll(false);
        config.setPluginConfig(FBInstreamPlugin.factory.getName(), fbInstreamConfig);
    }

    /**
     * Youbora options Bundle (Recommended)
     *
     * Will create {@link PKPluginConfigs} object with {@link YouboraPlugin}.
     *
     * @return - the pluginConfig object that should be passed as parameter when loading the player.
     */
    private PKPluginConfigs setYouboraConfigBundle(PKPluginConfigs pluginConfigs) {

        //Important!!! First you need to register your plugin.
        PlayKitManager.registerPlugins(this, YouboraPlugin.factory);

        //Initialize Json object that will hold all the configurations for the plugin.

        Bundle optBundle = new Bundle();

        //Youbora config bundle. Main config goes here.
        optBundle.putString(KEY_ACCOUNT_CODE, ACCOUNT_CODE);
        optBundle.putString(KEY_USERNAME, UNIQUE_USER_NAME);
        optBundle.putString(KEY_USER_EMAIL, UNIQUE_USER_NAME);
        optBundle.putBoolean(KEY_ENABLED, true);

        //Media entry bundle.
        optBundle.putString(KEY_CONTENT_TITLE, MEDIA_TITLE);

        //Optional - Device bundle o/w youbora will decide by its own.
        optBundle.putString(KEY_DEVICE_CODE, "AndroidTV");
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


}
