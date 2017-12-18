package com.kaltura.playkit.samples.tracksselection;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.kaltura.netkit.connect.response.ResultElement;
import com.kaltura.playkit.PKEvent;
import com.kaltura.playkit.PKMediaConfig;
import com.kaltura.playkit.PKMediaEntry;
import com.kaltura.playkit.PKMediaFormat;
import com.kaltura.playkit.PKMediaSource;
import com.kaltura.playkit.PKTrackConfig;
import com.kaltura.playkit.PlayKitManager;
import com.kaltura.playkit.Player;
import com.kaltura.playkit.PlayerEvent;
import com.kaltura.playkit.api.ovp.SimpleOvpSessionProvider;
import com.kaltura.playkit.mediaproviders.base.OnMediaLoadCompletion;
import com.kaltura.playkit.mediaproviders.ovp.KalturaOvpMediaProvider;
import com.kaltura.playkit.player.AudioTrack;
import com.kaltura.playkit.player.PKTracks;
import com.kaltura.playkit.player.TextTrack;
import com.kaltura.playkit.player.VideoTrack;
import com.kaltura.playkit.samples.tracksselection.tracks.TrackItem;
import com.kaltura.playkit.samples.tracksselection.tracks.TrackItemAdapter;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "MainActivity";
    //The url of the source to play
    private static final String SOURCE_URL = "http://cdnapi.kaltura.com/p/243342/sp/24334200/playManifest/entryId/0_uka1msg4/flavorIds/1_vqhfu6uy,1_80sohj7p/format/applehttp/protocol/http/a.m3u8";

    private static final String ENTRY_ID = "entry_id";
    private static final String MEDIA_SOURCE_ID = "source_id";


    public Player player;
    public EventListener eventListener;
    private PKMediaConfig mediaConfig;
    private Button playPauseButton;

    //Android Spinner view, that will actually hold and manipulate tracks selection.
    private Spinner videoSpinner, audioSpinner, textSpinner;
    private boolean userIsInteracting;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize media config object.
        createMediaConfig();

        //Create instance of the player.
        player = PlayKitManager.loadPlayer(this, null);

        //Add player to the view hierarchy.
        addPlayerToView();

        //Add simple play/pause button.
        addPlayPauseButton();

        //Initialize Android spinners view.
        initializeTrackSpinners();

        //Subscribe to the event which will notify us when track data is available.
        subscribeToTracksAvailableEvent();

        //Prepare player with media configuration.
        player.prepare(mediaConfig);
        player.play();
        //createMediaProvider();

    }

    private void createMediaProvider() {
        /*new KalturaOvpMediaProvider()
                .setSessionProvider(new SimpleOvpSessionProvider("https://cdnapisec.kaltura.com",
                        2267831, Globals.ks.toString()))
                .setEntryId(bumper)
                .load(completion);*/
        //Initialize provider.
        KalturaOvpMediaProvider mediaProvider = new KalturaOvpMediaProvider();

        //Initialize ovp session provider.
        SimpleOvpSessionProvider sessionProvider = new SimpleOvpSessionProvider("https://cdnapisec.kaltura.com", 2267831, "");

        //Set entry id for the session provider.
        mediaProvider.setEntryId("1_t0cw5f5x");

        //Set session provider to media provider.
        mediaProvider.setSessionProvider(sessionProvider);

        //Load media from media provider.
        mediaProvider.load(new OnMediaLoadCompletion() {
            @Override
            public void onComplete(ResultElement<PKMediaEntry> response) {
                //When response received check if it was successful.
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
        player.play();
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

        // --->  SELECTING preferred AUDIO/TEXT TRACKS
        //mediaConfig.setPreferredTextTrack(new PKTrackConfig().setPreferredMode(PKTrackConfig.Mode.OFF)); // no text tracks
        mediaConfig.setPreferredTextTrack(new PKTrackConfig().setPreferredMode(PKTrackConfig.Mode.AUTO)); // select the track by locale if does not exist manifest default
        //mediaConfig.setPreferredTextTrack(new PKTrackConfig().setPreferredMode(PKTrackConfig.Mode.EXPLICIT).setTrackLanguage("rus")); // select specific track lang if not exist select manifest default
        mediaConfig.setPreferredAudioTrack(new PKTrackConfig().setPreferredMode(PKTrackConfig.Mode.AUTO));
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
     * Here we are getting access to the Android Spinner views,
     * and set OnItemSelectedListener.
     */
    private void initializeTrackSpinners() {
        videoSpinner = (Spinner) this.findViewById(R.id.videoSpinner);
        audioSpinner = (Spinner) this.findViewById(R.id.audioSpinner);
        textSpinner = (Spinner) this.findViewById(R.id.textSpinner);

        textSpinner.setOnItemSelectedListener(this);
        audioSpinner.setOnItemSelectedListener(this);
        videoSpinner.setOnItemSelectedListener(this);
    }

    /**
     * Subscribe to the TRACKS_AVAILABLE event. This event will be sent
     * every time new source have been loaded and it tracks data is obtained
     * by the player.
     */
    private void subscribeToTracksAvailableEvent() {
        player.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                if (event instanceof PlayerEvent.VideoTrackChanged) {
                    Log.d(TAG, "Event VideoTrackChanged");
                    if (eventListener != null) {
                        eventListener.onVideoTrackChanged((PlayerEvent.VideoTrackChanged)event);
                    }
                } else if (event instanceof PlayerEvent.AudioTrackChanged) {
                    Log.d(TAG, "Event AudioTrackChanged");
                    if (eventListener != null) {
                        eventListener.onAudioTrackChanged((PlayerEvent.AudioTrackChanged)event);
                    }
                } else if (event instanceof PlayerEvent.TextTrackChanged) {
                    Log.d(TAG, "Event TextTrackChanged");
                    if (eventListener != null) {
                        eventListener.onTextTrackChanged((PlayerEvent.TextTrackChanged)event);
                    }
                } else if (event instanceof PlayerEvent.TracksAvailable) {
                    Log.d(TAG, "Event TRACKS_AVAILABLE");

                    //Cast event to the TracksAvailable object that is actually holding the necessary data.
                    PlayerEvent.TracksAvailable tracksAvailable = (PlayerEvent.TracksAvailable) event;

                    //Obtain the actual tracks info from it.
                    PKTracks tracks = tracksAvailable.tracksInfo;
                    int defaultAudioTrackIndex = tracks.getDefaultAudioTrackIndex();
                    int defaultTextTrackIndex = tracks.getDefaultTextTrackIndex();
                    if (tracks.getAudioTracks().size() > 0) {
                        Log.d(TAG, "Default Audio langae = " + tracks.getAudioTracks().get(defaultAudioTrackIndex).getLabel());
                    }
                    if (tracks.getTextTracks().size() > 0) {
                        Log.d(TAG, "Default Text langae = " + tracks.getTextTracks().get(defaultTextTrackIndex).getLabel());
                    }
                    if (tracks.getVideoTracks().size() > 0) {
                        Log.d(TAG, "Default video isAdaptive = " + tracks.getVideoTracks().get(tracks.getDefaultAudioTrackIndex()).isAdaptive() + " bitrate = " + tracks.getVideoTracks().get(tracks.getDefaultAudioTrackIndex()).getBitrate());
                    }
                    //player.changeTrack(tracksAvailable.tracksInfo.getVideoTracks().get(1).getUniqueId());
                    //Populate Android spinner views with received data.
                    populateSpinnersWithTrackInfo(tracks);

                }
            }
            //Event that will be sent when tracks data is available.
        }, PlayerEvent.Type.TRACKS_AVAILABLE, PlayerEvent.Type.AUDIO_TRACK_CHANGED, PlayerEvent.Type.TEXT_TRACK_CHANGED, PlayerEvent.Type.VIDEO_TRACK_CHANGED);
    }

    /**
     * Populate Android Spinners with retrieved tracks data.
     * Here we are building custom {@link TrackItem} objects, with which
     * we will populate our custom spinner adapter.
     * @param tracks - {@link PKTracks} object with all tracks data in it.
     */
    private void populateSpinnersWithTrackInfo(PKTracks tracks) {

        //Build track items that are based on videoTrack data.
        TrackItem[] videoTrackItems = buildVideoTrackItems(tracks.getVideoTracks());
        //populate spinner with this info.
        applyAdapterOnSpinner(videoSpinner, videoTrackItems);

        //Build track items that are based on audioTrack data.
        TrackItem[] audioTrackItems = buildAudioTrackItems(tracks.getAudioTracks());
        //populate spinner with this info.
        applyAdapterOnSpinner(audioSpinner, audioTrackItems);

        //Build track items that are based on textTrack data.
        TrackItem[] textTrackItems = buildTextTrackItems(tracks.getTextTracks());
        //populate spinner with this info.
        applyAdapterOnSpinner(textSpinner, textTrackItems);
    }

    /**
     * Will build array of {@link TrackItem} objects.
     * Each {@link TrackItem} object will hold the readable name.
     * In this case the width and height of the video track.
     * If {@link VideoTrack} is adaptive, we will name it "Auto".
     * We use this name to represent the track selection options.
     * Also each {@link TrackItem} will hold the unique id of the track,
     * which should be passed to the player in order to switch to the desired track.
     * @param videoTracks - the list of available video tracks.
     * @return - array with custom {@link TrackItem} objects.
     */
    private TrackItem[] buildVideoTrackItems(List<VideoTrack> videoTracks) {
        //Initialize TrackItem array with size of videoTracks list.
        TrackItem[] trackItems = new TrackItem[videoTracks.size()];

        //Iterate through all available video tracks.
        for (int i = 0; i < videoTracks.size(); i++) {
            //Get video track from index i.
            VideoTrack videoTrackInfo = videoTracks.get(i);

            //Check if video track is adaptive. If so, give it "Auto" name.
            if (videoTrackInfo.isAdaptive()) {
                //In this case, if this track is selected, the player will
                //adapt the playback bitrate automatically, based on user bandwidth and device capabilities.
                //Initialize TrackItem.
                trackItems[i] = new TrackItem("Auto", videoTrackInfo.getUniqueId());
            } else {

                //If it is not adaptive track, build readable name based on width and height of the track.
                StringBuilder nameStringBuilder = new StringBuilder();
                nameStringBuilder.append(videoTrackInfo.getBitrate());

                //Initialize TrackItem.
                trackItems[i] = new TrackItem(nameStringBuilder.toString(), videoTrackInfo.getUniqueId());
            }
        }
        return trackItems;
    }

    /**
     * Will build array of {@link TrackItem} objects.
     * Each {@link TrackItem} object will hold the readable name.
     * In this case the label of the audio track.
     * If {@link AudioTrack} is adaptive, we will name it "Auto".
     * We use this name to represent the track selection options.
     * Also each {@link TrackItem} will hold the unique id of the track,
     * which should be passed to the player in order to switch to the desired track.
     * @param audioTracks - the list of available audio tracks.
     * @return - array with custom {@link TrackItem} objects.
     */
    private TrackItem[] buildAudioTrackItems(List<AudioTrack> audioTracks) {
        //Initialize TrackItem array with size of audioTracks list.
        TrackItem[] trackItems = new TrackItem[audioTracks.size()];

        //Iterate through all available audio tracks.
        for (int i = 0; i < audioTracks.size(); i++) {

            //Get audio track from index i.
            AudioTrack audioTrackInfo = audioTracks.get(i);

            //Check if audio track is adaptive. If so, give it "Auto" name.
            if (audioTrackInfo.isAdaptive()) {
                //In this case, if this track is selected, the player will
                //adapt the playback bitrate automatically, based on user bandwidth and device capabilities.
                //Initialize TrackItem.
                trackItems[i] = new TrackItem("Auto", audioTrackInfo.getUniqueId());
            } else {
                //If it is not adaptive track, name it based on the audio track label.
                String name = audioTrackInfo.getLabel();

                //Initialize TrackItem.
                trackItems[i] = new TrackItem(name, audioTrackInfo.getUniqueId());
            }
        }
        return trackItems;
    }

    /**
     * Will build array of {@link TrackItem} objects.
     * Each {@link TrackItem} object will hold the readable name.
     * In this case the label of the text track.
     * We use this name to represent the track selection options.
     * Also each {@link TrackItem} will hold the unique id of the track,
     * which should be passed to the player in order to switch to the desired track.
     * @param textTracks - the list of available text tracks.
     * @return - array with custom {@link TrackItem} objects.
     */
    private TrackItem[] buildTextTrackItems(List<TextTrack> textTracks) {
        //Initialize TrackItem array with size of textTracks list.
        TrackItem[] trackItems = new TrackItem[textTracks.size()];

        //Iterate through all available text tracks.
        for (int i = 0; i < textTracks.size(); i++) {

            //Get text track from index i.
            TextTrack textTrackInfo = textTracks.get(i);

            //Name TrackItem based on the text track label.
            String name = textTrackInfo.getLabel();
            trackItems[i] = new TrackItem(name, textTrackInfo.getUniqueId());
        }
        return trackItems;
    }

    /**
     * Initialize and set custom adapter to the Android spinner.
     * @param spinner - spinner to which adapter should be applied.
     * @param trackItems - custom track items array.
     */
    private void applyAdapterOnSpinner(Spinner spinner, TrackItem[] trackItems) {
        //Initialize custom adapter.
        TrackItemAdapter trackItemAdapter = new TrackItemAdapter(this, R.layout.track_items_list_row, trackItems);

        //Apply adapter on spinner.
        spinner.setAdapter(trackItemAdapter);
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //Get the selected TrackItem from adapter.
        if (userIsInteracting) {
            TrackItem trackItem = (TrackItem) parent.getItemAtPosition(position);

            //Important! This will actually do the switch between tracks.
            player.changeTrack(trackItem.getUniqueId());
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        userIsInteracting = true;
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
}
