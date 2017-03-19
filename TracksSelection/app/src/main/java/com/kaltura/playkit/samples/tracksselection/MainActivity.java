package com.kaltura.playkit.samples.tracksselection;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.kaltura.playkit.PKEvent;
import com.kaltura.playkit.PKMediaConfig;
import com.kaltura.playkit.PKMediaEntry;
import com.kaltura.playkit.PKMediaFormat;
import com.kaltura.playkit.PKMediaSource;
import com.kaltura.playkit.PlayKitManager;
import com.kaltura.playkit.Player;
import com.kaltura.playkit.PlayerEvent;
import com.kaltura.playkit.player.AudioTrack;
import com.kaltura.playkit.player.PKTracks;
import com.kaltura.playkit.player.TextTrack;
import com.kaltura.playkit.player.VideoTrack;
import com.kaltura.playkit.samples.basicpluginssetup.R;
import com.kaltura.playkit.samples.tracksselection.tracks.TrackItem;
import com.kaltura.playkit.samples.tracksselection.tracks.TrackItemAdapter;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final int START_POSITION = 60; // one minute.

    //The url of the source to play
    private static final String SOURCE_URL = "https://devimages.apple.com.edgekey.net/streaming/examples/bipbop_16x9/bipbop_16x9_variant.m3u8";

    private static final String ENTRY_ID = "entry_id";
    private static final String MEDIA_SOURCE_ID = "source_id";

    //Instance of the Player.
    private Player player;

    //Android Spinner view, that will actually hold and manipulate tracks selection.
    private Spinner videoSpinner, audioSpinner, textSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //First. Create PKMediaConfig object.
        PKMediaConfig mediaConfig = new PKMediaConfig()
                // You can configure the start position for it.
                // by default it will be 0.
                // If start position is grater then duration of the source it will be reset to 0.
                .setStartPosition(START_POSITION);

        //Second. Create PKMediaEntry object.
        PKMediaEntry mediaEntry = createMediaEntry();

        //Add it to the mediaConfig.
        mediaConfig.setMediaEntry(mediaEntry);

        //Create instance of the player.
        player = PlayKitManager.loadPlayer(this, null);

        //Get the layout, where the player view will be placed.
        LinearLayout layout = (LinearLayout) findViewById(R.id.player_root);
        //Add player view to the layout.
        layout.addView(player.getView());

        //Initialize Android spinners view.
        initializeTrackSpinners();

        //Subscribe to the event which will notify us when track data is available.
        subscribeToTracksAvailableEvent();

        //Prepare player with media configuration.
        player.prepare(mediaConfig);

        //Start playback.
        player.play();
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
                //Cast event to the TracksAvailable object that is actually holding the necessary data.
                PlayerEvent.TracksAvailable tracksAvailable = (PlayerEvent.TracksAvailable) event;

                //Obtain the actual tracks info from it.
                PKTracks tracks = tracksAvailable.getPKTracks();

                //Populate Android spinner views with received data.
                populateSpinnersWithTrackInfo(tracks);

            }
            //Event that will be sent when tracks data is available.
        }, PlayerEvent.Type.TRACKS_AVAILABLE);
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
                nameStringBuilder.append(videoTrackInfo.getWidth())
                        .append("x")
                        .append(videoTrackInfo.getHeight());

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
        TrackItem trackItem = (TrackItem) parent.getItemAtPosition(position);

        //Important! This will actually do the switch between tracks.
        player.changeTrack(trackItem.getUniqueId());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
