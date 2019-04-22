package com.kaltura.playkit.samples.phoenixanalytics;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.gson.JsonObject;
import com.kaltura.netkit.connect.response.PrimitiveResult;
import com.kaltura.netkit.connect.response.ResultElement;
import com.kaltura.netkit.utils.OnCompletion;
import com.kaltura.netkit.utils.SessionProvider;
import com.kaltura.playkit.PKMediaConfig;
import com.kaltura.playkit.PKMediaEntry;
import com.kaltura.playkit.PKMediaFormat;
import com.kaltura.playkit.PKPluginConfigs;
import com.kaltura.playkit.PlayKitManager;
import com.kaltura.playkit.Player;
import com.kaltura.playkit.player.vr.VRInteractionMode;
import com.kaltura.playkit.player.vr.VRPKMediaEntry;
import com.kaltura.playkit.plugins.kava.KavaAnalyticsConfig;
import com.kaltura.playkit.plugins.kava.KavaAnalyticsPlugin;
import com.kaltura.playkit.plugins.ott.PhoenixAnalyticsConfig;
import com.kaltura.playkit.plugins.ott.PhoenixAnalyticsEvent;
import com.kaltura.playkit.plugins.ott.PhoenixAnalyticsPlugin;
import com.kaltura.playkit.providers.api.phoenix.APIDefines;
import com.kaltura.playkit.providers.base.OnMediaLoadCompletion;
import com.kaltura.playkit.providers.ott.PhoenixMediaProvider;
import com.kaltura.playkitvr.VRUtil;


public class MainActivity extends AppCompatActivity {

    //Tag for logging.
    private static final String TAG = MainActivity.class.getSimpleName();

    //Phoenix analytics constants
    private static final String PHOENIX_ANALYTICS_BASE_URL = "https://api-preprod.ott.kaltura.com/v5_1_0/api_v3/"; // analytics base url
    private static final int PHOENIX_ANALYTICS_PARTNER_ID = 198; // your OTT partner id
    private static final String PHOENIX_ANALYTICS_KS = "";
    private static final int ANALYTIC_TRIGGER_INTERVAL = 30; //Interval in which analytics report should be triggered (in seconds).

    //Phoenix provider constans.
    private static final String PHOENIX_PROVIDER_BASE_URL = "https://api-preprod.ott.kaltura.com/v5_1_0/api_v3/"; //your provider base url
    private static final String PHOENIX_PROVIDER_KS = ""; // your user ks if required
    private static final int PHOENIX_PROVIDER_PARTNER_ID = 198; // your OTT partner id
    private static final String FORMAT = "Mobile_Devices_Main_SD";
    private static final String MEDIA_ID = "259153";//"258459"; // asset id to request
    public static final String VR_MEDIA_ID = "258459";


    private Player player;
    private PKMediaConfig mediaConfig;
    private Button playPauseButton;
    private PKPluginConfigs pluginConfigs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize PKPluginConfigs object with PhoenixAnalyticsPlugin.
        pluginConfigs = new PKPluginConfigs();
        createPhoenixAnalyticsPlugin(pluginConfigs);
        createKavaAnalyticsPlugin(pluginConfigs);

        //Create instance of the player with specified pluginConfigs.
        player = PlayKitManager.loadPlayer(this, pluginConfigs);
        player.getSettings().setAllowCrossProtocolRedirect(true);
        player.getSettings().setPreferredMediaFormat(PKMediaFormat.hls); // usually dash

        //Subscribe to analytics report event.
        subscribePhoenixAnalyticsEvents();

        //Add player to the view hierarchy.
        addPlayerToView();

        //Add simple play/pause button.
        addPlayPauseButton();

        //Initialize phoenix media provider.
        createPhoenixMediaProvider();
    }

    /**
     * Will create {@link PKPluginConfigs} object with {@link PhoenixAnalyticsPlugin}.
     *
     * @return - the pluginConfig object that should be passed as parameter when loading the player.
     */
    private void createPhoenixAnalyticsPlugin(PKPluginConfigs pluginConfigs) {

        //Important!!! First you need to register your plugin.
        PlayKitManager.registerPlugins(this, PhoenixAnalyticsPlugin.factory);
        //Initialize Json object that will hold all the configurations for the plugin.
        JsonObject pluginEntry = new JsonObject();

        //PhoenixAnalyticsPlugin config json. Main config goes here.
        PhoenixAnalyticsConfig phoenixAnalyticsConfig = new PhoenixAnalyticsConfig(PHOENIX_ANALYTICS_PARTNER_ID, PHOENIX_ANALYTICS_BASE_URL, PHOENIX_ANALYTICS_KS, ANALYTIC_TRIGGER_INTERVAL);
        //Set plugin entry to the plugin configs.
        pluginConfigs.setPluginConfig(PhoenixAnalyticsPlugin.factory.getName(), phoenixAnalyticsConfig);

    }


    private void createKavaAnalyticsPlugin(PKPluginConfigs pluginConfigs) {

        //Important!!! First you need to register your plugin.
        PlayKitManager.registerPlugins(this, KavaAnalyticsPlugin.factory);

        KavaAnalyticsConfig kavaAnalyticsConfig = new KavaAnalyticsConfig();

        // Each OTT media with mediaPrep has OVP partnerId and OVP entryId (asset list will contain the entryId for here, partner you should ask..
        int MEDIA_PREP_PARTNER_ID = 1774581;
        String MEDIA_ENTRY_ID = "1_mphei4ku";
        kavaAnalyticsConfig.setPartnerId(MEDIA_PREP_PARTNER_ID);
        kavaAnalyticsConfig.setEntryId(MEDIA_ENTRY_ID);
        //Set plugin entry to the plugin configs.
        pluginConfigs.setPluginConfig(KavaAnalyticsPlugin.factory.getName(), kavaAnalyticsConfig);
    }

    /**
     * Subscribe to kaltura stats report event.
     * This event will be received each and every time
     * the analytics report is sent.
     */
    private void subscribePhoenixAnalyticsEvents() {
        //Subscribe to the event.
        player.addListener(this, PhoenixAnalyticsEvent.reportSent, event -> {
            PhoenixAnalyticsEvent.PhoenixAnalyticsReport reportEvent = event;

            //Get the event name from the report.
            String reportedEventName = reportEvent.reportedEventName;
            Log.i(TAG, "PhoenixAnalytics report sent. Reported event name: " + reportedEventName);
        });

        player.addListener(this, PhoenixAnalyticsEvent.bookmarkError, event -> {
            PhoenixAnalyticsEvent.BookmarkErrorEvent bookmarkErrorEvent = event;
            Log.i(TAG, "PhoenixAnalytics bookmarkErrorEvent event name: " + bookmarkErrorEvent.errorMessage + " - " + bookmarkErrorEvent.errorCode);
        });

        player.addListener(this, PhoenixAnalyticsEvent.concurrencyError, event -> {
            PhoenixAnalyticsEvent.ConcurrencyErrorEvent concurrencyError = event;
            Log.i(TAG, "PhoenixAnalytics bookmarkErrorEvent event name: " + concurrencyError.errorMessage + " - " + concurrencyError.errorCode);
        });
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

    /**
     * Will create phoenix media provider, which will request for specified media entry.
     */
    private void createPhoenixMediaProvider() {

        //Initialize provider.
        PhoenixMediaProvider mediaProvider = new PhoenixMediaProvider();
        //Initialize session provider.
        SessionProvider sessionProvider = createSessionProvider();

        //Set entry id for the session provider.
        mediaProvider.setAssetId(VR_MEDIA_ID);
        mediaProvider.setAssetType(APIDefines.KalturaAssetType.Media);
        mediaProvider.setAssetReferenceType(APIDefines.AssetReferenceType.Media);
        mediaProvider.setContextType(APIDefines.PlaybackContextType.Playback);
        mediaProvider.setFormats(FORMAT);
        mediaProvider.setProtocol(PhoenixMediaProvider.HttpProtocol.Http); // usually HTTPS

        //Set session provider to media provider.
        mediaProvider.setSessionProvider(sessionProvider);

        //Load media from media provider.
        mediaProvider.load(new OnMediaLoadCompletion() {
            @Override
            public void onComplete(ResultElement<PKMediaEntry> response) {
                //When response received, check if it was successful.
                if (response.isSuccess()) {
                    //If so, prepare player with received PKMediaEntry.
                    preparePlayer(response.getResponse());
                } else {
                    //If response was not successful print it to console with error message.
                    String error = "failed to fetch media data: " + (response.getError() != null ? response.getError().getMessage() : "");
                    Log.e(TAG, error);
                }
            }
        });

    }

    /**
     * Will create {@link SessionProvider}.
     *
     * @return - {@link SessionProvider}
     */
    private SessionProvider createSessionProvider() {
        return new SessionProvider() {
            @Override
            public String baseUrl() {
                return PHOENIX_PROVIDER_BASE_URL;
            }

            @Override
            public void getSessionToken(OnCompletion<PrimitiveResult> completion) {
                completion.onComplete(new PrimitiveResult(PHOENIX_PROVIDER_KS));
            }

            @Override
            public int partnerId() {
                return PHOENIX_PROVIDER_PARTNER_ID;
            }
        };
    }

    /**
     * Prepare player and start playback.
     *
     * @param mediaEntry - media entry we received from media provider.
     */
    private void preparePlayer(final PKMediaEntry mediaEntry) {
        //The preparePlayer is called from another thread. So first be shure
        //that we are running on ui thread.
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Initialize media config object.
                if (mediaEntry instanceof VRPKMediaEntry) {
                    boolean modeSupported = VRUtil.isModeSupported(getApplicationContext(), VRInteractionMode.Motion);
                    if (modeSupported) {
                        ((VRPKMediaEntry) mediaEntry).getVrSettings().setInteractionMode(VRInteractionMode.MotionWithTouch);
                    }
                }
                createMediaConfig(mediaEntry);
            }
        });
    }

    private void createMediaConfig(final PKMediaEntry mediaEntry) {
        //Initialize empty mediaConfig object.
        mediaConfig = new PKMediaConfig();

        //Set media entry we received from provider.
        mediaConfig.setMediaEntry(mediaEntry);

        //Prepare player with media configurations.
        player.prepare(mediaConfig);
    }
}
