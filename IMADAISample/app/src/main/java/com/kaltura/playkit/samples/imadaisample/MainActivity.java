package com.kaltura.playkit.samples.imadaisample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.ads.interactivemedia.v3.api.StreamRequest;
import com.google.gson.JsonObject;
import com.kaltura.playkit.PKEvent;
import com.kaltura.playkit.PKMediaConfig;
import com.kaltura.playkit.PKMediaEntry;
import com.kaltura.playkit.PKMediaFormat;
import com.kaltura.playkit.PKMediaSource;
import com.kaltura.playkit.PKPluginConfigs;
import com.kaltura.playkit.PlayKitManager;
import com.kaltura.playkit.Player;
import com.kaltura.playkit.PlayerEvent;
import com.kaltura.playkit.plugins.ads.AdEvent;
import com.kaltura.playkit.plugins.ads.AdInfo;
import com.kaltura.playkit.plugins.ima.IMAConfig;
import com.kaltura.playkit.plugins.ima.IMAPlugin;
import com.kaltura.playkit.plugins.imadai.IMADAIConfig;
import com.kaltura.playkit.plugins.imadai.IMADAIPlugin;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    //Tag for logging.
    private static final String TAG = MainActivity.class.getSimpleName();

    //Media entry configuration constants.
    private static final String SOURCE_URL = "https://cdnapisec.kaltura.com/p/2215841/sp/221584100/playManifest/entryId/1_w9zx2eti/protocol/https/format/applehttp/falvorIds/1_1obpcggb,1_yyuvftfz,1_1xdbzoa6,1_k16ccgto,1_djdf6bk8/a.m3u8";
    private static final String ENTRY_ID = "entry_id";
    private static final String MEDIA_SOURCE_ID = "source_id";

    //Ad configuration constants.
    private static final String AD_TAG_URL = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/ad_rule_samples&ciu_szs=300x250&ad_rule=1&impl=s&gdfp_req=1&env=vp&output=vmap&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ar%3Dpremidpostpod&cmsid=496&vid=short_onecue&correlator=";
    //"https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/ad_rule_samples&ciu_szs=300x250&ad_rule=1&impl=s&gdfp_req=1&env=vp&output=vmap&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ar%3Dpremidpost&cmsid=496&vid=short_onecue&correlator=";
            //"https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dskippablelinear&correlator=";
    private static final String INCORRECT_AD_TAG_URL = "incorrect_ad_tag_url";
    private static final int PREFERRED_AD_BITRATE = 600;

    private Player player;
    private PKMediaConfig mediaConfig;
    private Button playPauseButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize media config object.
        createMediaConfig();

        //Create plugin configurations.
        PKPluginConfigs pluginConfigs = createIMADAIPlugin();

        //Create instance of the player with plugin configurations.
        player = PlayKitManager.loadPlayer(this, pluginConfigs);

        //Subscribe to the ad events.
        subscribeToAdEvents();

        //Add player to the view hierarchy.
        addPlayerToView();

        //Add simple play/pause button.
        addPlayPauseButton();

        //Prepare player with media configuration.
        player.prepare(mediaConfig);

    }

    /**
     * Create IMAPlugin object.
     *
     * @return - {@link PKPluginConfigs} object with IMAPlugin.
     */
    private PKPluginConfigs createIMADAIPlugin() {

        //First register your IMADAIPlugin.
        PlayKitManager.registerPlugins(this, IMADAIPlugin.factory);

        //Initialize plugin configuration object.
        PKPluginConfigs pluginConfigs = new PKPluginConfigs();

        //Initialize + Configure  IMADAIConfig object.
        IMADAIConfig adsConfigVodHls1 = getDAIConfigVodHls1().enableDebugMode(true).setAlwaysStartWithPreroll(true);
        IMADAIConfig adsConfigVodLive = getDAIConfigLiveHls().enableDebugMode(true).setAlwaysStartWithPreroll(true);
        IMADAIConfig adsConfigVodDash = getDAIConfigVodDash().enableDebugMode(true).setAlwaysStartWithPreroll(true);
        IMADAIConfig adsConfigError = getDAIConfigError().enableDebugMode(true).setAlwaysStartWithPreroll(true);

        IMADAIConfig adsConfigVodHls2 = getDAIConfigVodHls2().enableDebugMode(true).setAlwaysStartWithPreroll(true);
        IMADAIConfig adsConfigVodHls3 = getDAIConfigVodHls3().enableDebugMode(true).setAlwaysStartWithPreroll(true);



        IMADAIConfig adsConfigVodHls4 = getDAIConfigVodHls4().enableDebugMode(true).setAlwaysStartWithPreroll(true);

        /* For MOAT call this API:
        List<View> overlaysList = new ArrayList<>();
        //overlaysList.add(....)
        adsConfigVodHls4.setControlsOverlayList(overlaysList);
        */

        //Set jsonObject to the main pluginConfigs object.
        pluginConfigs.setPluginConfig(IMADAIPlugin.factory.getName(), adsConfigVodHls1);

        /*
            NOTE!  for change media before player.prepare api please call:
            player.updatePluginConfig(IMADAIPlugin.factory.getName(), getDAIConfigVodHls2());
        */

        //Return created PluginConfigs object.
        return pluginConfigs;
    }

    private IMADAIConfig getDAIConfigLiveHls() {
        String assetTitle = "Live Video - Big Buck Bunny";
        String assetKey = "sN_IYUG8STe1ZzhIIE_ksA";
        String apiKey = null;
        StreamRequest.StreamFormat streamFormat = StreamRequest.StreamFormat.HLS;
        String licenseUrl = null;
        return IMADAIConfig.getLiveIMADAIConfig(assetTitle,
                assetKey,
                apiKey,
                streamFormat,
                licenseUrl).setAlwaysStartWithPreroll(true).enableDebugMode(true);
    }

    private IMADAIConfig getDAIConfigVodHls1() {
        String assetTitle = "VOD - Tears of Steel";
        String apiKey = null;
        String contentSourceId = "19463";
        String videoId = "tears-of-steel";
        StreamRequest.StreamFormat streamFormat = StreamRequest.StreamFormat.HLS;
        String licenseUrl = null;

        return IMADAIConfig.getVodIMADAIConfig(assetTitle,
                contentSourceId,
                videoId,
                apiKey,
                streamFormat,
                licenseUrl).enableDebugMode(true).setAlwaysStartWithPreroll(true);
    }

    private IMADAIConfig getDAIConfigVodHls2() {
        String assetTitle = "VOD - Google I/O";
        String apiKey = null;
        String contentSourceId = "19463";
        String videoId = "googleio-highlights";
        StreamRequest.StreamFormat streamFormat = StreamRequest.StreamFormat.HLS;
        String licenseUrl = null;
        return IMADAIConfig.getVodIMADAIConfig(assetTitle,
                contentSourceId,
                videoId,
                apiKey,
                streamFormat,
                licenseUrl).enableDebugMode(true);
    }

    private IMADAIConfig getDAIConfigVodHls3() {
        String assetTitle = "HLS3";
        String apiKey = null;
        String contentSourceId = "19823";
        String videoId = "ima-test";
        StreamRequest.StreamFormat streamFormat = StreamRequest.StreamFormat.HLS;
        String licenseUrl = null;
        return IMADAIConfig.getVodIMADAIConfig(assetTitle,
                contentSourceId,
                videoId,
                apiKey,
                streamFormat,
                licenseUrl).enableDebugMode(true);
    }

    private IMADAIConfig getDAIConfigVodHls4() {
        String assetTitle3 = "HLS4";
        String assetKey3 = null;
        String apiKey3 = null;
        String contentSourceId3 = "2472176";
        String videoId3 = "2504847";
        StreamRequest.StreamFormat streamFormat3 = StreamRequest.StreamFormat.HLS;
        String licenseUrl3 = null;
        return IMADAIConfig.getVodIMADAIConfig(assetTitle3,
                contentSourceId3,
                videoId3,
                apiKey3,
                streamFormat3,
                licenseUrl3);
    }

    private IMADAIConfig getDAIConfigVodDash() {
        String assetTitle = "BBB-widevine";
        String apiKey = null;
        String contentSourceId = "2474148";
        String videoId = "bbb-widevine";
        StreamRequest.StreamFormat streamFormat = StreamRequest.StreamFormat.DASH;
        String licenseUrl = "https://proxy.uat.widevine.com/proxy";
        return IMADAIConfig.getVodIMADAIConfig(assetTitle,
                contentSourceId,
                videoId,
                apiKey,
                streamFormat,
                licenseUrl).enableDebugMode(true);
    }


    private IMADAIConfig getDAIConfigError() {
        String assetTitle = "ERROR";
        String assetKey = null;
        String apiKey = null;
        String contentSourceId = "19823";
        String videoId = "ima-test";
        StreamRequest.StreamFormat streamFormat = StreamRequest.StreamFormat.HLS;
        String licenseUrl = null;
        return IMADAIConfig.getVodIMADAIConfig(assetTitle,
                contentSourceId + "AAAA",
                videoId,
                apiKey,
                streamFormat,
                licenseUrl).enableDebugMode(true);
    }


    /**
     * Will subscribe to ad events.
     * For simplicity, in this example we will show subscription to the couple of events.
     * For the full list of ad events you can check our documentation.
     * !!!Note, we will receive only ad events, we subscribed to.
     */
    private void subscribeToAdEvents() {
        // Add ad event listener. Note, that it have two parameters.
        // 1. PKEvent.Listener itself.
        // 2. Array of ad events you want to listen to.
        player.addEventListener(new PKEvent.Listener() {
                                    @Override
                                    public void onEvent(PKEvent event) {
                                        if (event.eventType() == PlayerEvent.Type.ERROR) {
                                            //In case of PlayerEvent.Type.ERROR cast the event object to PlayerEvent.Error
                                            PlayerEvent.Error errorEvent = (PlayerEvent.Error) event;
                                            //Print the type of the received error.
                                            Log.e(TAG, "Player Error: " + errorEvent.error.errorType.name());
                                        }
                                        //First check if event is instance of the AdEvent.
                                        if (event instanceof AdEvent) {

                                            //Switch on the received events.
                                            switch (((AdEvent) event).type) {

                                                //Ad started event triggered.
                                                case STARTED:
                                                    //Some events holds additional data objects in them.
                                                    //In order to get access to this object you need first cast event to
                                                    //the object it belongs to. You can learn more about this kind of objects in
                                                    //our documentation.
                                                    AdEvent.AdStartedEvent adStartedEvent = (AdEvent.AdStartedEvent) event;

                                                    //Then you can use the data object itself.
                                                    AdInfo adInfo = adStartedEvent.adInfo;

                                                    //Print to log content type of this ad.
                                                    Log.d(TAG, "ad event received: " + event.eventType().name()
                                                            + ". Additional info: ad content type is: "
                                                            + adInfo.getAdContentType());
                                                    break;

                                                //Ad skipped triggered.
                                                case SKIPPED:
                                                    Log.d(TAG, "ad event received: " + event.eventType().name());
                                                    break;
                                                //Ad completed triggered.
                                                case COMPLETED:
                                                    Log.d(TAG, "ad event received: " + event.eventType().name());
                                                    break;
                                                case ERROR:
                                                    AdEvent.Error errorEvent = (AdEvent.Error) event;
                                                    //Print the type of the received error.
                                                    Log.e(TAG, "Error: " + errorEvent.error.errorType.name());
                                                    break;
                                            }
                                        }
                                    }
                                },
                //Subscribe to the ad events you are interested in.
                PlayerEvent.Type.ERROR,
                AdEvent.Type.STARTED,
                AdEvent.Type.SKIPPED,
                AdEvent.Type.COMPLETED,
                AdEvent.Type.ERROR
        );
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
