package com.kaltura.playkit.samples.phoenixanalytics;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.gson.JsonObject;
import com.kaltura.playkit.MediaEntryProvider;
import com.kaltura.playkit.OnCompletion;
import com.kaltura.playkit.PKEvent;
import com.kaltura.playkit.PKMediaConfig;
import com.kaltura.playkit.PKMediaEntry;
import com.kaltura.playkit.PKPluginConfigs;
import com.kaltura.playkit.PlayKitManager;
import com.kaltura.playkit.Player;
import com.kaltura.playkit.PlayerEvent;
import com.kaltura.playkit.backend.PrimitiveResult;
import com.kaltura.playkit.backend.SessionProvider;
import com.kaltura.playkit.backend.base.OnMediaLoadCompletion;
import com.kaltura.playkit.backend.phoenix.PhoenixMediaProvider;
import com.kaltura.playkit.connect.ResultElement;
import com.kaltura.playkit.plugins.PhoenixAnalyticsEvent;
import com.kaltura.playkit.plugins.PhoenixAnalyticsPlugin;




public class MainActivity extends AppCompatActivity {

    //Tag for logging.
    private static final String TAG = MainActivity.class.getSimpleName();

    public String baseUrl = "http://api-preprod.ott.kaltura.com/v4_2/api_v3/";
    private PlayerEvent event;
    private String fileId = "YOUR_FILE_ID";
    private String partnerId = "YOUR_PARTNER_ID";
    private String ks = "YOUR_KS";
    private int timerInterval = 30;



    private Player player;
    private PKMediaConfig mediaConfig;
    private Button playPauseButton;
    private PKPluginConfigs pluginConfigs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Initialize PKPluginConfigs object with Youbora.
        pluginConfigs = createPhoenixAnalyticsPlugin();
        setPhoenixMediaProvider();

    }

    /**
     * Will create {@link PKPluginConfigs} object with {@link PhoenixAnalyticsPlugin}.
     *
     * @return - the pluginConfig object that should be passed as parameter when loading the player.
     */
    private PKPluginConfigs createPhoenixAnalyticsPlugin() {

        //Important!!! First you need to register your plugin.
        PlayKitManager.registerPlugins(this, PhoenixAnalyticsPlugin.factory);

        //Initialize PKPluginConfigs object.
        PKPluginConfigs pluginConfigs = new PKPluginConfigs();
        //Initialize Json object that will hold all the configurations for the plugin.
        JsonObject pluginEntry = new JsonObject();

        //PhoenixAnalyticsPlugin config json. Main config goes here.
        JsonObject phoenixPluginConfigJson = new JsonObject();

        phoenixPluginConfigJson.addProperty("fileId", fileId);
        phoenixPluginConfigJson.addProperty("baseUrl", baseUrl);
        phoenixPluginConfigJson.addProperty("timerInterval", timerInterval);
        phoenixPluginConfigJson.addProperty("ks", ks);
        phoenixPluginConfigJson.addProperty("partnerId", partnerId);


        //Set plugin entry to the plugin configs.
        pluginConfigs.setPluginConfig(PhoenixAnalyticsPlugin.factory.getName(), pluginEntry);

        return pluginConfigs;
    }

    /**
     * Subscribe to kaltura stats report event.
     * This event will be received each and every time
     * the analytics report is sent.
     */
    private void subscribePhoenixAnalyticsReportEvent() {
        //Subscribe to the event.
        player.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                //Cast received event to AnalyticsEvent.BaseAnalyticsReportEvent.
                PhoenixAnalyticsEvent.PhoenixAnalyticsReport reportEvent = (PhoenixAnalyticsEvent.PhoenixAnalyticsReport) event;

                //Get the event name from the report.
                String reportedEventName = reportEvent.getReportedEventName();
                Log.i(TAG, "PhoenixAnalytics report sent. Reported event name: " + reportedEventName);
            }
            //Event subscription.
        }, PhoenixAnalyticsEvent.Type.REPORT_SENT);
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

    private void setPhoenixMediaProvider() {


        SessionProvider sessionProvider = new SessionProvider() {
            @Override
            public String baseUrl() {
                return baseUrl;
            }

            @Override
            public void getSessionToken(OnCompletion<PrimitiveResult> completion) {
                completion.onComplete(new PrimitiveResult(ks));
            }

            @Override
            public int partnerId() {
                return 198;
            }
        };


        String assetId = "485384";
        String referenceType = "media";
       // List<String> format = new ArrayList<>(converterPhoenixMediaProvider.getFormats());
       // String[] formatVarargs = {"Mobile_Devices_Main_SD"};

        MediaEntryProvider phoenixMediaProvider = new PhoenixMediaProvider().setSessionProvider(sessionProvider).setAssetId(assetId);

        loadMediaProvider(phoenixMediaProvider);

    }

    private void loadMediaProvider(MediaEntryProvider mediaEntryProvider) {

        mediaEntryProvider.load(new OnMediaLoadCompletion() {

            @Override
            public void onComplete(final ResultElement<PKMediaEntry> response) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (response.isSuccess()) {
                            PKMediaEntry mediaEntry = response.getResponse();
                            mediaConfig = new PKMediaConfig();
                            //Add it to the mediaConfig.
                            mediaConfig.setMediaEntry(mediaEntry);
                            //Create instance of the player with specified pluginConfigs.
                            player = PlayKitManager.loadPlayer(getApplicationContext(), pluginConfigs);

                            //Subscribe to analytics report event.
                            subscribePhoenixAnalyticsReportEvent();

                            //Add player to the view hierarchy.
                            addPlayerToView();

                            //Add simple play/pause button.
                            addPlayPauseButton();

                            //Prepare player with media config.
                            player.prepare(mediaConfig);
                        }
                    }
                });
            }
        });
    }
}
