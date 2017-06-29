package com.kaltura.playkit.samples.errorhandling;

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
import com.kaltura.playkit.PlayerEvent;
import com.kaltura.playkit.plugins.ads.AdEvent;
import com.kaltura.playkit.plugins.ads.ima.IMAConfig;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //Tag for logging.
    private static final String TAG = MainActivity.class.getSimpleName();

    //The url of the source to play
    private static final String INCORRECT_SOURCE_URL = "incorrect_source_url";
    private static final String ENTRY_ID = "entry_id";
    private static final String MEDIA_SOURCE_ID = "source_id";

    //Ad configuration constants.
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
        PKPluginConfigs pluginConfigs = createIMAPlugin();

        //Create instance of the player.
        player = PlayKitManager.loadPlayer(this, pluginConfigs);

        //Subscribe to the events.
        subscribeToErrorEvents();

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
    private PKPluginConfigs createIMAPlugin() {

        //First register your IMAPlugin.
        PlayKitManager.registerPlugins(this, com.kaltura.playkit.plugins.ads.ima.IMAPlugin.factory);

        //Initialize plugin configuration object.
        PKPluginConfigs pluginConfigs = new PKPluginConfigs();

        //Initialize imaConfigs object.
        IMAConfig imaConfigs = new IMAConfig();

        //Configure ima.
        //!!!NOTE we in purpose place incorrect url as adTag, in order to produce ad error.
        imaConfigs.setAdTagURL(INCORRECT_AD_TAG_URL);
        imaConfigs.setVideoBitrate(PREFERRED_AD_BITRATE);

        //Convert imaConfigs to jsonObject.
        JsonObject imaConfigJsonObject = imaConfigs.toJSONObject();

        //Set jsonObject to the main pluginConfigs object.
        pluginConfigs.setPluginConfig(com.kaltura.playkit.plugins.ads.ima.IMAPlugin.factory.getName(), imaConfigJsonObject);

        //Return created PluginConfigs object.
        return pluginConfigs;
    }

    /**
     * Will subscribe to the error events (in this example PlayerEvent.Type.ERROR and AdEvent.Type.ERROR).
     * For simplicity, in this example we will just print the errorType of the error that happened.
     * For the full list of errorTypes you can check our documentation.
     * !!!Note, we will receive only events, we subscribed to.
     */
    private void subscribeToErrorEvents() {

        //Add event listener. Note, that it have two parameters.
        // 1. PKEvent.Listener itself.
        // 2. Array of events you want to listen to.
        player.addEventListener(new PKEvent.Listener() {

                                    //Event received.
                                    @Override
                                    public void onEvent(PKEvent event) {
                                        //Check type of the received event.
                                        if (event.eventType() == PlayerEvent.Type.ERROR) {
                                            //In case of PlayerEvent.Type.ERROR cast the event object to PlayerEvent.Error
                                            PlayerEvent.Error errorEvent = (PlayerEvent.Error) event;
                                            //Print the type of the received error.
                                            Log.e(TAG, "Error: " + errorEvent.error.errorType.name());
                                        } else if (event.eventType() == AdEvent.Type.ERROR) {
                                            //In case of AdEvent.Type.ERROR cast the event object to AdEvent.Error
                                            AdEvent.Error errorEvent = (AdEvent.Error) event;
                                            //Print the type of the received error.
                                            Log.e(TAG, "Error: " + errorEvent.error.errorType.name());
                                        }
                                    }
                                },
                //Subscribe to the events you are interested in.
                PlayerEvent.Type.ERROR,
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
        mediaSource.setUrl(INCORRECT_SOURCE_URL);

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
