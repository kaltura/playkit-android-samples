package com.kaltura.playkit.samples.tvpapistats;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.gson.JsonObject;
import com.kaltura.playkit.PKDrmParams;
import com.kaltura.playkit.PKEvent;
import com.kaltura.playkit.PKMediaConfig;
import com.kaltura.playkit.PKMediaEntry;
import com.kaltura.playkit.PKMediaFormat;
import com.kaltura.playkit.PKMediaSource;
import com.kaltura.playkit.PKPluginConfigs;
import com.kaltura.playkit.PlayKitManager;
import com.kaltura.playkit.Player;
import com.kaltura.playkit.PlayerEvent;
import com.kaltura.playkit.player.PKTracks;
import com.kaltura.playkit.plugins.TVPAPIAnalyticsPlugin;
import com.kaltura.playkit.plugins.TVPapiAnalyticsEvent;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    //Tag for logging.
    private static final String TAG = MainActivity.class.getSimpleName();

    //Analytics constants
    private static final String TVPAPI_ANALYTICS_URL = "http://tvpapi-preprod.ott.kaltura.com/v3_9/gateways/jsonpostgw.aspx?"; //Server url
    private static final int ANALYTIC_TRIGGER_INTERVAL = 30; //Interval in which analytics report should be triggered (in seconds).

    private static final String UDID = "your_udid";
    private static final String TOKEN = "your_token";
    private static final String FILE_ID = "12345"; //your file id here.
    private static final String API_USER = "your_api_user";
    private static final String API_PASS = "your_api_pass";
    private static final String PLATFORM = "your_platform";
    private static final String SITE_GUID = "123456";
    private static final String DOMAIN_ID = "your_domain_id";
    private static final String LOCALE_DEVICE = "your_locale_device";
    private static final String LOCALE_COUNTRY = "your_locale_country";
    private static final String LOCALE_LANGUAGE = "your_locale_language";
    private static final String LOCALE_USER_STATE = "your_locale_user_state";

    private Player player;
    private PKMediaConfig mediaConfig;
    private Button playPauseButton;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize media config object.
        mediaConfig = getMediaConfig();

        //Initialize PKPluginConfigs object with TVPapiAnalyticsPlugin.
        PKPluginConfigs pluginConfigs = createTVPapiAnalyticsPlugin();

        //Create instance of the player with specified pluginConfigs.
        player = PlayKitManager.loadPlayer(this, pluginConfigs);

        //Subscribe to events, which will notify about changes in player states.
        subscribeToPlayerStateChanges();

        //Subscribe to the player events.
        subscribeToPlayerEvents();

        //Subscribe to analytics report event.
        subscribeToTVPapiReportEvent();

        //Add player to the view hierarchy.
        addPlayerToView();

        //Add simple play/pause button.
        addPlayPauseButton();

        //Prepare player with media config.
        player.prepare(mediaConfig);

    }





    /**
     * Will create {@link PKMediaConfig} object.
     */
    private PKMediaConfig getMediaConfig() {
        String videoURL = "https://cdnapisec.kaltura.com/p/1982551/sp/198255100/playManifest/entryId/0_ch70ffu7/format/mpegdash/tags/dash/protocol/https/f/a.mpd?playSessionId=1532d456-c6c8-545a-115c-b4bd5bfca001";
        String licenseUri = "https://udrm.kaltura.com//cenc/widevine/license?custom_data=eyJjYV9zeXN0ZW0iOiJPVFQiLCJ1c2VyX3Rva2VuIjoiIiwiYWNjb3VudF9pZCI6MTk4MjU1MSwiY29udGVudF9pZCI6IjBfY2g3MGZmdTdfMF9mMDAwa2tiMiwwX2NoNzBmZnU3XzBfNG56Ym1xNXEsMF9jaDcwZmZ1N18wX2xwcWI3cGRlIiwiZmlsZXMiOiIiLCJ1ZGlkIjoiYWE1ZTFiNmM5Njk4OGQ2OCIsImFkZGl0aW9uYWxfY2FzX3N5c3RlbSI6MH0%3D&signature=33dgytZdOqUqIwcMnwk6dPazq18%3D&";
        int fileId = 123456; //should get from API
        int mediaId = 654321; //should get from API
        String duration = "90"; //should get from API
        PKMediaEntry mediaEntry = new PKMediaEntry();
        mediaEntry.setId(String.valueOf(mediaId));

        long mediaDuration = 0;
        if (duration != null || !duration.isEmpty()) {
            mediaDuration = Long.parseLong(duration);
        }
        mediaEntry.setDuration(mediaDuration * 1000); // Conversion from seconds to milliseconds

        List<PKMediaSource> mediaSourceList = new ArrayList<>();
        PKMediaSource pkMediaSource = new PKMediaSource();

        pkMediaSource.setId(String.valueOf(fileId));
        pkMediaSource.setMediaFormat(PKMediaFormat.valueOfUrl(videoURL));

        PKDrmParams pkDrmParams = new PKDrmParams(licenseUri, PKDrmParams.Scheme.WidevineCENC);
        List<PKDrmParams> pkDrmDataList = new ArrayList<>();

        pkDrmDataList.add(pkDrmParams);
        pkMediaSource.setDrmData(pkDrmDataList);

        pkMediaSource.setUrl(videoURL);
        mediaSourceList.add(pkMediaSource);
        mediaEntry.setSources(mediaSourceList);
        PKMediaConfig mediaConfig = new PKMediaConfig();
        mediaConfig.setMediaEntry(mediaEntry);
        return mediaConfig;
    }


    /**
     * Will subscribe to the changes in the player states.
     */
    private void subscribeToPlayerStateChanges() {
        //Add event listener to the player.
        player.addStateChangeListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {

                //Cast received event to PlayerEvent.StateChanged.
                PlayerEvent.StateChanged stateChanged = (PlayerEvent.StateChanged) event;

                //Switch on the new state that is received.
                switch (stateChanged.newState) {

                    //Player went to the Idle state.
                    case IDLE:
                        //Print to log.
                        Log.d(TAG, "StateChanged: IDLE.");
                        break;
                    //The player is in Loading state.
                    case LOADING:
                        //Print to log.
                        Log.d(TAG, "StateChanged: LOADING.");
                        break;
                    //The player is ready for playback.
                    case READY:
                        //Print to log.
                        Log.d(TAG, "StateChanged: READY.");
                        break;
                    //Player is buffering now.
                    case BUFFERING:
                        //Print to log.
                        Log.d(TAG, "StateChanged: BUFFERING.");
                        break;
                }
            }
        });
    }

    /**
     * Will subscribe to the player events. The main difference between
     * player state changes and player events, is that events are notify us
     * about playback events like PLAY, PAUSE, TRACKS_AVAILABLE, SEEKING etc.
     * The player state changed events, notify us about more major changes in
     * his states. Like IDLE, LOADING, READY and BUFFERING.
     * For simplicity, in this example we will show subscription to the couple of events.
     * For the full list of events you can check our documentation.
     * !!!Note, we will receive only events, we subscribed to.
     */
    private void subscribeToPlayerEvents() {

        //Add event listener. Note, that it have two parameters.
        // 1. PKEvent.Listener itself.
        // 2. Array of events you want to listen to.
        player.addEventListener(new PKEvent.Listener() {

                                    //Event received.
                                    @Override
                                    public void onEvent(PKEvent event) {

                                        //First check if event is instance of the PlayerEvent.
                                        if (event instanceof PlayerEvent) {

                                            //Switch on the received events.
                                            switch (((PlayerEvent) event).type) {

                                                //Player play triggered.
                                                case PLAY:
                                                    //Print to log.
                                                    Log.d(TAG, "event received: " + event.eventType().name());
                                                    break;

                                                //Player pause triggered.
                                                case PAUSE:
                                                    Log.d(TAG, "event received: " + event.eventType().name());
                                                    break;

                                                //Tracks data is available.
                                                case TRACKS_AVAILABLE:
                                                    //Some events holds additional data objects in them.
                                                    //In order to get access to this object you need first cast event to
                                                    //the object it belongs to. You can learn more about this kind of objects in
                                                    //our documentation.
                                                    PlayerEvent.TracksAvailable tracksAvailable = (PlayerEvent.TracksAvailable) event;

                                                    //Then you can use the data object itself.
                                                    PKTracks tracks = tracksAvailable.getPKTracks();

                                                    //Print to log amount of video tracks that are available for this entry.
                                                    Log.d(TAG, "event received: " + event.eventType().name()
                                                            + ". Additional info: Available video tracks number: "
                                                            + tracks.getVideoTracks().size());
                                                    break;
                                            }
                                        }
                                    }
                                },
                //Subscribe to the events you are interested in.
                PlayerEvent.Type.PLAY,
                PlayerEvent.Type.PAUSE,
                PlayerEvent.Type.TRACKS_AVAILABLE
        );
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
     * Subscribe to TVPapi report event.
     * This event will be received each and every time
     * the analytics report is sent.
     */
    private void subscribeToTVPapiReportEvent() {
        //Subscribe to the event.
        player.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                //Cast received event to AnalyticsEvent.BaseAnalyticsReportEvent.
                TVPapiAnalyticsEvent.TVPapiAnalyticsReport reportEvent = (TVPapiAnalyticsEvent.TVPapiAnalyticsReport) event;

                //Get the event name from the report.
                String reportedEventName = reportEvent.getReportedEventName();
                Log.i(TAG, "TVPapi stats report sent. Reported event name: " + reportedEventName);
            }
            //Event subscription.
        }, TVPapiAnalyticsEvent.Type.REPORT_SENT);
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

    /**
     * Will create {@link PKPluginConfigs} object with {@link TVPAPIAnalyticsPlugin}.
     *
     * @return - the pluginConfig object that should be passed as parameter when loading the player.
     */
    private PKPluginConfigs createTVPapiAnalyticsPlugin() {

        //First register your plugin.
        PlayKitManager.registerPlugins(this, TVPAPIAnalyticsPlugin.factory);

        //Initialize PKPluginConfigs object.
        PKPluginConfigs pluginConfigs = new PKPluginConfigs();
        JsonObject pluginEntry = new JsonObject();

        JsonObject paramsJson = new JsonObject();
        paramsJson.addProperty("fileId", FILE_ID);
        paramsJson.addProperty("baseUrl", TVPAPI_ANALYTICS_URL);
        paramsJson.addProperty("timerInterval", ANALYTIC_TRIGGER_INTERVAL);

        //Initialize user json object and configure it.
        JsonObject initObjJson = new JsonObject();
        initObjJson.addProperty("SiteGuid", SITE_GUID);
        initObjJson.addProperty("ApiUser", API_USER);
        initObjJson.addProperty("DomainID", DOMAIN_ID);
        initObjJson.addProperty("UDID", UDID);
        initObjJson.addProperty("ApiPass", API_PASS);
        initObjJson.addProperty("Platform", PLATFORM);
        initObjJson.addProperty("Token", TOKEN);

        //Initialize locale json object and configure it.
        JsonObject localeJsonObject = new JsonObject();
        localeJsonObject.addProperty("LocaleUserState", LOCALE_USER_STATE);
        localeJsonObject.addProperty("LocaleCountry", LOCALE_COUNTRY);
        localeJsonObject.addProperty("LocaleDevice", LOCALE_DEVICE);
        localeJsonObject.addProperty("LocaleLanguage", LOCALE_LANGUAGE);

        //Add locale json object to user json object with "Locale" as key.
        initObjJson.add("Locale", localeJsonObject);

        //Add user json object to plugin entry json object with "initObj" as key.
        paramsJson.add("initObj", initObjJson);
        pluginEntry.add("params", paramsJson);

        //Set plugin entry to the plugin configs.
        pluginConfigs.setPluginConfig(TVPAPIAnalyticsPlugin.factory.getName(), pluginEntry);

        return pluginConfigs;
    }

}
