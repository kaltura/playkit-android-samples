package com.kaltura.playkit.samples.eventsregistration;

import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

    private Player player;
    private PKMediaConfig mediaConfig;
    private Button playPauseButton;
    private Spinner speedSpinner;
    private boolean userIsInteracting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize media config object.
        createMediaConfig();
        addItemsOnSpeedSpinner();
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

    }

    public void addItemsOnSpeedSpinner() {

        speedSpinner = findViewById(R.id.sppedSpinner);
        List<Float> list = new ArrayList();
        list.add(0.5f);
        list.add(1.0f);
        list.add(1.5f);
        list.add(2.0f);
        ArrayAdapter<Float> dataAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        speedSpinner.setAdapter(dataAdapter);
        speedSpinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());
        speedSpinner.setSelection(1);

    }

    /**
     * Will subscribe to the changes in the player states.
     */
    private void subscribeToPlayerStateChanges() {

        player.addListener(this, PlayerEvent.stateChanged, event -> {
            PlayerEvent.StateChanged stateChanged = event;
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

        player.addListener(this, PlayerEvent.play, event -> {
            Log.d(TAG, "event received: " + event.eventType().name());

        });

        player.addListener(this, PlayerEvent.pause, event -> {
            Log.d(TAG, "event received: " + event.eventType().name());
        });

        player.addListener(this, PlayerEvent.playbackRateChanged, event -> {
            PlayerEvent.PlaybackRateChanged playbackRateChanged = event;
            Log.d(TAG, "event received: " + event.eventType().name() + " Rate = " + playbackRateChanged.rate);

        });

        player.addListener(this, PlayerEvent.tracksAvailable, event -> {
            Log.d(TAG, "Event TRACKS_AVAILABLE");

            PlayerEvent.TracksAvailable tracksAvailable = event;

            //Then you can use the data object itself.
            PKTracks tracks = tracksAvailable.tracksInfo;

            //Print to log amount of video tracks that are available for this entry.
            Log.d(TAG, "event received: " + event.eventType().name()
                    + ". Additional info: Available video tracks number: "
                    + tracks.getVideoTracks().size());
        });

        player.addListener(this, PlayerEvent.error, event -> {
            PlayerEvent.Error errorEvent = event;
            Log.e(TAG, "Error Event: " + errorEvent.error.errorType  + " " + event.error.message);
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

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        userIsInteracting = true;
    }

    @Override
    protected void onPause() {
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


    class CustomOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            if (userIsInteracting) {
                Toast.makeText(parent.getContext(),
                        "OnItemSelectedListener : " + parent.getItemAtPosition(pos).toString() + "X",
                        Toast.LENGTH_SHORT).show();
                if (player != null) {
                    player.setPlaybackRate((float) parent.getItemAtPosition(pos));
                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }


    }
}

