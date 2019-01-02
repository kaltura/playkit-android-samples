package com.kaltura.playkit.samples.basicsample;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;


import com.kaltura.playkit.PKMediaConfig;
import com.kaltura.playkit.PKMediaEntry;
import com.kaltura.playkit.PKMediaFormat;
import com.kaltura.playkit.PKMediaSource;
import com.kaltura.playkit.PlayKitManager;
import com.kaltura.playkit.Player;
import com.kaltura.ptrescue.DownloadTracker;
import com.kaltura.ptrescue.PrefetchSdk;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class PlayerActivity extends AppCompatActivity implements DownloadTracker.Listener {
    private static final String TAG = PlayerActivity.class.getSimpleName();

    private static final Long START_POSITION = 0L; // position tp start playback in seconds.

    //The url of the source to play
    //private static final String SOURCE_URL = "https://cdnapisec.kaltura.com/p/2215841/sp/221584100/playManifest/entryId/1_w9zx2eti/protocol/https/format/applehttp/falvorIds/1_1obpcggb,1_yyuvftfz,1_1xdbzoa6,1_k16ccgto,1_djdf6bk8/a.m3u8";
    //private static final String SOURCE_URL = "http://cdnapi.kaltura.com/p/1774581/sp/177458100/playManifest/entryId/1_mphei4ku/format/applehttp/tags/mbr/protocol/http/f/a.m3u8";
    private static final String SOURCE_URL = "https://cdnapisec.kaltura.com/p/1982551/sp/198255100/playManifest/entryId/1_aworxd15/format/applehttp/protocol/https/a.m3u8";

    private static final String ENTRY_ID = "1_w9zx2eti";
    private static final String MEDIA_SOURCE_ID = "source_id";

    private Player player;
    private PKMediaConfig mediaConfig;
    private Button playPauseButton;
    private Button downloadButton;
    private Button prepareButton;

    private String entryId;
    private String entryUrl;

//    private DownloadTracker downloadTracker;
//    private DataSource.Factory dataSourceFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //scheduleDownloadOnDemanAlarm();
//        dataSourceFactory = buildDataSourceFactory();
        addPrepareButton();
        addSeekToButton();
        addDownloadButton();
        final SharedPreferences prefs = prefs();
        entryId = prefs.getString("id", null);
        entryUrl = prefs.getString("url", null);

        //Initialize media config object.
        createMediaConfig();

        //Create instance of the player without plugins.
        player = PlayKitManager.loadPlayer(this, null);

        //Add player to the view hierarchy.
        addPlayerToView();

        //Add simple play/pause button.
        addPlayPauseButton();

        //Prepare player with media configuration.
//        player.getSettings().setOfflineDataSourceFactory(dataSourceFactory);
//        player.getSettings().setOfflineStreamKeys(getOfflineStreamKeys(Uri.parse(SOURCE_URL)));
        PrefetchSdk.shared(this).install(player);
//        player.prepare(mediaConfig);

        //Start playback.
//        player.play();
    }

//    /** Returns a new DataSource factory. */
//    private DataSource.Factory buildDataSourceFactory() {
//        return ((PrimeTimeRescueApplication) getApplication()).buildDataSourceFactory();
//    }
//
//    private List<StreamKey> getOfflineStreamKeys(Uri uri) {
//        return ((PrimeTimeRescueApplication) getApplication()).getDownloadTracker().getOfflineStreamKeys(uri);
//    }
//
    /**
     * Will create {@link } object.
     */
    private void createMediaConfig() {
        //First. Create PKMediaConfig object.
        mediaConfig = new PKMediaConfig();

        //Set start position of the media. This will
        //automatically start playback from specified position.
        mediaConfig.setStartPosition(START_POSITION);

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
        mediaEntry.setId(entryId);

        //Set media entry type. It could be Live,Vod or Unknown.
        //In this sample we use Vod.
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
        mediaSource.setUrl(entryUrl);

        //Set the format of the source. In our case it will be hls in case of mpd/wvm formats you have to to call mediaSource.setDrmData method as well
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

    private void addPrepareButton() {
        //Get reference to the play/pause button.
        prepareButton = (Button) this.findViewById(R.id.prepare_button);
        //Add clickListener.
        prepareButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                createMediaConfig();
                player.prepare(mediaConfig);
                player.play();
            }
        });
    }

    private void addSeekToButton() {
        //Get reference to the play/pause button.
        playPauseButton = (Button) this.findViewById(R.id.seek_button);
        //Add clickListener.
        playPauseButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (player.isPlaying()) {
                    Log.d(TAG, "XXX DURATION = " + player.getDuration());
                    player.seekTo(1000 * 60 * 10);
                }
            }
        });
    }

    private void addDownloadButton() {
        //Get reference to the play/pause button.
        downloadButton = (Button) this.findViewById(R.id.download_button);
        //Add clickListener.
        downloadButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onDownloadButtonClicked();
            }
        });
    }

    private void onDownloadButtonClicked() {
        ///////////////////////////////////
        PrefetchSdk.shared(this).prefetchNow((strings, e) -> {
            final Map.Entry<String, String> first = strings.entrySet().iterator().next();
            entryId = first.getKey();
            entryUrl = first.getValue();
            prefs().edit().clear().
                    putString("id", entryId).putString("url", entryUrl).apply();
            Log.d(TAG, "onDownloadButtonClicked: prefetched entries");
        });

//        downloadTracker = application.getDownloadTracker();
//        downloadTracker.toggleDownload(this, "Test", Uri.parse(SOURCE_URL), "m3u8");
        //////////////////////////////////
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (player != null) {
            player.onApplicationResumed();
            player.play();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            player.onApplicationPaused();
        }
    }

    private SharedPreferences prefs() {
        return getSharedPreferences("ptr", MODE_PRIVATE);
    }

    @Override
    public void onDownloadsChanged() {
        Log.d(TAG, "XXX onDownloadsChanged");
    }

    // Setup a recurring alarm every half hour
    public void scheduleDownloadOnDemanAlarm() {
        // Construct an intent that will execute the AlarmReceiver
        Intent intent = new Intent(getApplicationContext(), DownloadOnDemandAlarmReceiver.class);
        // Create a PendingIntent to be triggered when the alarm goes off
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, DownloadOnDemandAlarmReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Setup periodic alarm every every half hour from this point onwards
        long firstMillis = System.currentTimeMillis(); // alarm is set right away
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        // First parameter is the type: ELAPSED_REALTIME, ELAPSED_REALTIME_WAKEUP, RTC_WAKEUP
        // Interval can be INTERVAL_FIFTEEN_MINUTES, INTERVAL_HALF_HOUR, INTERVAL_HOUR, INTERVAL_DAY
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis,
                5 * 1000, pIntent);
    }
}
