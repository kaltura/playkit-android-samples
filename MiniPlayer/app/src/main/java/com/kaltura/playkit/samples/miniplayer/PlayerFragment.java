package com.kaltura.playkit.samples.miniplayer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.kaltura.playkit.PKLog;
import com.kaltura.playkit.PKMediaConfig;
import com.kaltura.playkit.PKMediaEntry;
import com.kaltura.playkit.PKMediaFormat;
import com.kaltura.playkit.PKMediaSource;
import com.kaltura.playkit.PKPluginConfigs;
import com.kaltura.playkit.PlayKitManager;
import com.kaltura.playkit.Player;
import com.kaltura.playkit.PlayerEvent;
import com.kaltura.playkit.plugins.ads.AdCuePoints;
import com.kaltura.playkit.plugins.ads.AdEvent;
//import com.kaltura.playkit.plugins.ima.IMAConfig;
//import com.kaltura.playkit.plugins.ima.IMAPlugin;
import com.kaltura.playkit.providers.MediaEntryProvider;

import java.util.ArrayList;

/**
 * Created by glebgleb on 7/13/17.
 */

public class PlayerFragment extends Fragment {

    private ViewGroup root;

    private static final PKLog log = PKLog.get("Activity");

    private Player player;
    private MediaEntryProvider mediaProvider;
    private PlaybackControlsView controlsView;
    private boolean nowPlaying;
    ProgressBar progressBar;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.player_fragment, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        root = (ViewGroup) view.findViewById(R.id.root);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        //registerPlugins();
        onMediaLoaded(createMediaEntry());
    }

    private PKMediaEntry createMediaEntry() {
        PKMediaEntry entry = new PKMediaEntry();
        PKMediaSource source = new PKMediaSource();
        source.setId("1_w9zx2eti");
        source.setUrl("https://cdnapisec.kaltura.com/p/2215841/sp/221584100/playManifest/entryId/1_w9zx2eti/protocol/https/format/applehttp/falvorIds/1_1obpcggb,1_yyuvftfz,1_1xdbzoa6,1_k16ccgto,1_djdf6bk8/a.m3u8");
        source.setMediaFormat(PKMediaFormat.hls);
        entry.setId("1_w9zx2eti");
        entry.setDuration(102000);
        entry.setMediaType(PKMediaEntry.MediaEntryType.Unknown);
        ArrayList<PKMediaSource> sourceList = new ArrayList<>();
        sourceList.add(source);
        entry.setSources(sourceList);
        return entry;
    }

    /*private void startMockMediaLoading(OnMediaLoadCompletion completion) {
        mediaProvider = new MockMediaProvider("mock/entries.playkit.json", getActivity(), "hls");
        mediaProvider.load(completion);
    }*/

    private void onMediaLoaded(PKMediaEntry mediaEntry) {
        PKMediaConfig mediaConfig = new PKMediaConfig().setMediaEntry(mediaEntry).setStartPosition(0L);
        PKPluginConfigs pluginConfig = new PKPluginConfigs();
        if (player == null) {

            //addIMAPluginConfig(pluginConfig);

            player = PlayKitManager.loadPlayer(getActivity(), pluginConfig);
            player.getSettings().useTextureView(PlayerActivity.USE_TEXTURE);
            /*SurfaceView view = player.getView();
            view.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);*/
            log.d("Player: " + player.getClass());
            addPlayerListeners(progressBar);
            root.addView(player.getView(), 0);

            controlsView = (PlaybackControlsView) root.findViewById(R.id.playerControls);
            controlsView.setPlayer(player);
        }
        player.prepare(mediaConfig);
        player.play();
    }

//    private void addIMAPluginConfig(PKPluginConfigs config) {
//        //String adTagUrl = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dskippablelinear&correlator=";
//        String adTagUrl = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/3274935/preroll&impl=s&gdfp_req=1&env=vp&output=xml_vast2&unviewed_position_start=1&url=[referrer_url]&description_url=[description_url]&correlator=[timestamp]";
//        List<String> videoMimeTypes = new ArrayList<>();
//        IMAConfig adsConfig = new IMAConfig().setAdTagUrl(adTagUrl);
//        config.setPluginConfig(IMAPlugin.factory.getName(), adsConfig);
//    }
//
//    private void registerPlugins() {
//        PlayKitManager.registerPlugins(getActivity(), IMAPlugin.factory);
//    }

    private void addPlayerListeners(final ProgressBar appProgressBar) {
        player.addListener(this, AdEvent.contentPauseRequested, event -> {
            log.d("AD_CONTENT_PAUSE_REQUESTED");
            appProgressBar.setVisibility(View.VISIBLE);
        });

        player.addListener(this, AdEvent.contentResumeRequested, event -> {
            log.d("AD_CONTENT_RESUME_REQUESTED");
        });


        player.addListener(this, AdEvent.cuepointsChanged, event -> {
            AdEvent.AdCuePointsUpdateEvent cuePointsList = event;
            AdCuePoints adCuePoints = cuePointsList.cuePoints;
            if (adCuePoints != null) {
                log.d("Has Postroll = " + adCuePoints.hasPostRoll());
            }
        });

        player.addListener(this, AdEvent.started, event -> {
            log.d("AD_STARTED");
            appProgressBar.setVisibility(View.INVISIBLE);
        });


        player.addListener(this, AdEvent.resumed, event -> {
            log.d("Ad Event AD_RESUMED");
            nowPlaying = true;
            appProgressBar.setVisibility(View.INVISIBLE);
        });

        player.addListener(this, AdEvent.skipped, event -> {
            log.d("SKIPPED");
            nowPlaying = true;

        });

        player.addListener(this, AdEvent.allAdsCompleted, event -> {
            log.d("Ad Event AD_ALL_ADS_COMPLETED");
            appProgressBar.setVisibility(View.INVISIBLE);
        });

        player.addListener(this, PlayerEvent.play, event -> {
            log.d("PLAY");
            nowPlaying = true;
            root.getLayoutParams().width = root.getWidth() + 1;
            root.getLayoutParams().height = root.getHeight() + 1;
            root.requestLayout();

        });

        player.addListener(this, PlayerEvent.pause, event -> {
            log.d("PAUSE");
            nowPlaying = false;
        });


        player.addListener(this, PlayerEvent.stateChanged, event -> {
            PlayerEvent.StateChanged stateChanged = event;
            log.d("State changed from " + stateChanged.oldState + " to " + stateChanged.newState);
            if(controlsView != null){
                controlsView.setPlayerState(stateChanged.newState);
            }

        });

        player.addListener(this, PlayerEvent.tracksAvailable, event -> {
            PlayerEvent.TracksAvailable tracksAvailable = event;
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (controlsView != null) {
            controlsView.release();
        }
        if (player != null) {
            player.onApplicationPaused();
        }
    }

    @Override
    public void onResume() {
        log.d("onResume");
        super.onResume();
        if (player != null) {
            player.onApplicationResumed();
            if (nowPlaying) {
                player.play();
            }
        }
        if (controlsView != null) {
            controlsView.resume();
        }
    }

}
