package com.kaltura.playkit.samples.providers;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.kaltura.netkit.connect.response.ResultElement;
import com.kaltura.playkit.PKMediaConfig;
import com.kaltura.playkit.PKMediaEntry;
import com.kaltura.playkit.PlayKitManager;
import com.kaltura.playkit.Player;
import com.kaltura.playkit.api.ovp.SimpleOvpSessionProvider;
import com.kaltura.playkit.mediaproviders.base.OnMediaLoadCompletion;
import com.kaltura.playkit.mediaproviders.ovp.KalturaOvpMediaProvider;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    //Put here your provider base url
    private static final String PROVIDER_BASE_URL = "your_provider_url";
    //Put here your partner id.
    private static final int PARTNER_ID = 0;
    //Put here your KS.
    private static final String KS = "your_ks";
    //Put here your entry id.
    private static final String ENTRY_ID = "your_entry_id";

    private Player player;
    private PKMediaConfig mediaConfig;
    private Button playPauseButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Create instance of the player.
        player = PlayKitManager.loadPlayer(this, null);

        //Add player to the view hierarchy.
        addPlayerToView();

        //Add simple play/pause button.
        addPlayPauseButton();

        //Create media provider and request media.
        createMediaProvider();
    }

    /**
     * Create ovp media provider, that will request for media entry.
     */
    private void createMediaProvider() {

        //Initialize provider.
        KalturaOvpMediaProvider mediaProvider = new KalturaOvpMediaProvider();

        //Initialize ovp session provider.
        SimpleOvpSessionProvider sessionProvider = new SimpleOvpSessionProvider(PROVIDER_BASE_URL, PARTNER_ID, KS);

        //Set entry id for the session provider.
        mediaProvider.setEntryId(ENTRY_ID);

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
