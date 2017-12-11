package com.kaltura.playkit.samples.eventsregistration;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.kaltura.playkit.PKEvent;
import com.kaltura.playkit.PKMediaConfig;
import com.kaltura.playkit.PKMediaEntry;
import com.kaltura.playkit.PKMediaFormat;
import com.kaltura.playkit.PKMediaSource;
import com.kaltura.playkit.PlayKitManager;
import com.kaltura.playkit.Player;
import com.kaltura.playkit.PlayerEvent;
import com.kaltura.playkit.player.PKTracks;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //Tag for logging.
    private static final String TAG = MainActivity.class.getSimpleName();

    //The url of the source to play
    private static final String SOURCE_URL = "https://cdnapisec.kaltura.com/p/2215841/sp/221584100/playManifest/entryId/1_w9zx2eti/protocol/https/format/applehttp/falvorIds/1_1obpcggb,1_yyuvftfz,1_1xdbzoa6,1_k16ccgto,1_djdf6bk8/a.m3u8";

    private static final String ENTRY_ID = "entry_id";
    private static final String MEDIA_SOURCE_ID = "source_id";

    public Player player;
    private PKMediaConfig mediaConfig;
    private Button playPauseButton;
    public EventListener mEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize media config object.
        createMediaConfig();

        //Create instance of the player.
        player = PlayKitManager.loadPlayer(this, null);

        //Subscribe to events, which will notify about changes in player states.
        subscribeToPlayerStateChanges();

        //Subscribe to the player events.
        subscribeToPlayerEvents();

        //Add player to the view hierarchy.
        addPlayerToView();

        //Add simple play/pause button.
        addPlayPauseButton();

        //Prepare player with media configuration.
        player.prepare(mediaConfig);

        if (mEventListener != null) {
            mEventListener.onPlayerInit();
        }

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
                                            if (mEventListener != null) {
                                                mEventListener.onPlayerStart((PlayerEvent) event);
                                            }
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
                                                    PKTracks tracks = tracksAvailable.tracksInfo;

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

