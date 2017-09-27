package com.kaltura.playkit.samples.fulldemo;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.kaltura.playkit.PKEvent;
import com.kaltura.playkit.PKMediaConfig;
import com.kaltura.playkit.PKMediaEntry;
import com.kaltura.playkit.PKMediaFormat;
import com.kaltura.playkit.PKMediaSource;
import com.kaltura.playkit.PKPluginConfigs;
import com.kaltura.playkit.PlayKitManager;
import com.kaltura.playkit.Player;
import com.kaltura.playkit.PlayerEvent;
import com.kaltura.playkit.PlayerState;
import com.kaltura.playkit.plugins.ads.kaltura.ADConfig;
import com.kaltura.playkit.plugins.ads.kaltura.ADPlugin;
import com.kaltura.playkit.plugins.ads.kaltura.events.AdPluginErrorEvent;
import com.kaltura.playkit.plugins.ads.kaltura.events.AdPluginEvent;

import java.util.ArrayList;
import java.util.List;

public class VideoFragment extends Fragment {
    private static final String TAG = VideoFragment.class.getSimpleName();

    private VideoItem mVideoItem;
    private TextView mVideoTitle;
    private FrameLayout playerLayout;
    private RelativeLayout adSkin;
    private Player player;
    private PlaybackControlsView controlsView;
    private boolean nowPlaying;
    private ProgressBar progressBar;
    private boolean isFullScreen;
    private AppCompatImageView fullScreenBtn;
    private PKMediaConfig mediaConfig;
    private Logger mLog;
    private OnVideoFragmentViewCreatedListener mViewCreatedCallback;

    /**
     * Listener called when the fragment's onCreateView is fired.
     */
    public interface OnVideoFragmentViewCreatedListener {
        public void onVideoFragmentViewCreated();
    }

    @Override
    public void onAttach(Activity activity) {
        try {
            mViewCreatedCallback = (OnVideoFragmentViewCreatedListener) activity;

        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement " + OnVideoFragmentViewCreatedListener.class.getName());
        }

        super.onAttach(activity);
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_video, container, false);
        initUi(rootView);
        if (mViewCreatedCallback != null) {
            mViewCreatedCallback.onVideoFragmentViewCreated();
        }
        return rootView;
    }

    public void loadVideo(VideoItem videoItem) {
        if (mViewCreatedCallback == null) {
            mVideoItem = videoItem;
            return;
        }

        mVideoItem = videoItem;
        //Initialize media config object.
        createMediaConfig();
        PlayKitManager.registerPlugins(this.getActivity(), ADPlugin.factory);
        PKPluginConfigs pluginConfig = new PKPluginConfigs();
        addAdPluginConfig(pluginConfig, playerLayout, adSkin);
        //Create instance of the player.
        player = PlayKitManager.loadPlayer(this.getActivity(), pluginConfig);
        addPlayerListeners(progressBar);
        //Add player to the view hierarchy.
        addPlayerToView();

        //mVideoPlayerController.setContentVideo(mVideoItem.getVideoUrl());
        //mVideoPlayerController.setAdTagUrl(videoItem.getAdTagUrl());

        controlsView.setPlayer(player);
        mVideoTitle.setText(videoItem.getTitle());
        player.prepare(mediaConfig);

        //Start playback.
        player.play();
    }




    private void addAdPluginConfig(PKPluginConfigs config, FrameLayout layout, RelativeLayout adSkin) {
        ADConfig adsConfig = new ADConfig().setAdTagURL(mVideoItem.getAdTagUrl()).setPlayerViewContainer(layout).setAdSkinContainer(adSkin).setCompanionAdWidth(728).setCompanionAdHeight(90).setStartAdFromPosition(0);
        config.setPluginConfig(ADPlugin.factory.getName(), adsConfig);
    }

    /**
     * Will create {@link } object.
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
        mediaEntry.setId("1_w9zx2eti");

        //Set media entry type. It could be Live,Vod or Unknown.
        //For now we will use Unknown.
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
        mediaSource.setId("11111");

        //Set the content url. In our case it will be link to hls source(.m3u8).
        mediaSource.setUrl(mVideoItem.getVideoUrl());

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

        //Add player view to the layout.
        playerLayout.addView(player.getView());
    }

    private void initUi(View rootView) {

        mVideoTitle = (TextView) rootView.findViewById(R.id.video_title);
        playerLayout = (FrameLayout) rootView.findViewById(R.id.player_root);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBarSpinner);
        controlsView = (PlaybackControlsView) rootView.findViewById(R.id.playerControls);
        progressBar.setVisibility(View.INVISIBLE);
        adSkin = (RelativeLayout) rootView.findViewById(R.id.ad_skin);
        fullScreenBtn = (AppCompatImageView)rootView.findViewById(R.id.full_screen_switcher);
        fullScreenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int orient;
                if (isFullScreen) {
                    orient = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    setFullScreen(false);
                }
                else {
                    orient = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    setFullScreen(true);
                }
                getActivity().setRequestedOrientation(orient);
            }
        });

        final TextView logText = (TextView) rootView.findViewById(R.id.logText);
        final ScrollView logScroll = (ScrollView) rootView.findViewById(R.id.logScroll);

        // Provide an implementation of a logger so we can output SDK events to the UI.
        Logger logger = new Logger() {
            @Override
            public void log(String message) {
                Log.i(TAG, message);
                if (logText != null) {
                    logText.append(message);
                }
                if (logScroll != null) {
                    logScroll.post(new Runnable() {
                        @Override
                        public void run() {
                            logScroll.fullScroll(View.FOCUS_DOWN);
                        }
                    });
                }
            }
        };

        // If we've already selected a video, load it now.
        mLog = logger;
        if (mVideoItem != null) {
            loadVideo(mVideoItem);
        }
    }


    public void makeFullscreen(boolean isFullscreen) {
        setFullScreen(isFullscreen);
    }

    @Override
    public void onPause() {
        if (player != null) {
            player.onApplicationPaused();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (player != null) {
            player.onApplicationResumed();
            //player.play();
        }
    }

    private void addPlayerListeners(final ProgressBar appProgressBar) {

        player.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                log("ADS_PLAYBACK_ENDED");
            }
        }, AdPluginEvent.Type.ADS_PLAYBACK_ENDED);


        player.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                AdPluginEvent.AdRequestedEvent adRequestEvent = (AdPluginEvent.AdRequestedEvent) event;
                log("AD_REQUESTED");// adtag = " + adRequestEvent.adTagUrl);
            }
        }, AdPluginEvent.Type.AD_REQUESTED);


        player.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                AdPluginEvent.ProgressUpdateEvent adPluginEventProress = (AdPluginEvent.ProgressUpdateEvent) event;
                //log.d("received NEW AD_PROGRESS_UPDATE " + adPluginEventProress.currentPosition + "/" +  adPluginEventProress.duration);
            }
        }, AdPluginEvent.Type.AD_PROGRESS_UPDATE);


        player.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                //AdPluginErrorEvent.AdErrorEvent adError = (AdPluginErrorEvent.AdErrorEvent) event;
                log("AD_ERROR");// + adError.adErrorEvent.type + " "  + adError.adErrorMessage);
            }
        }, AdPluginErrorEvent.Type.AD_ERROR);


        player.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                log("AD_BREAK_STARTED");
                appProgressBar.setVisibility(View.VISIBLE);
            }
        }, AdPluginEvent.Type.AD_BREAK_STARTED);

        player.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                //AdPluginEvent.CuePointsChangedEvent cuePointsList = (AdPluginEvent.CuePointsChangedEvent) event;
                //AdCuePoints adCuePoints = new AdCuePoints(cuePointsList.cuePoints);
                //if (adCuePoints != null) {
                    log("CUEPOINTS_CHANGED");//"Has Postroll = " + adCuePoints.hasPostRoll());
                //}
                onCuePointChanged();
            }
        }, AdPluginEvent.Type.CUEPOINTS_CHANGED);

        player.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                log("AD_STARTED");
                appProgressBar.setVisibility(View.INVISIBLE);
            }
        }, AdPluginEvent.Type.STARTED);

        player.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                log("AD COMPLETED");
            }
        }, AdPluginEvent.Type.COMPLETED);

        player.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                log("AD_RESUMED");
                nowPlaying = true;
                appProgressBar.setVisibility(View.INVISIBLE);
            }
        }, AdPluginEvent.Type.RESUMED);


        player.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                log("AD_PAUSED");
                nowPlaying = true;
                appProgressBar.setVisibility(View.INVISIBLE);
            }
        }, AdPluginEvent.Type.PAUSED);

        player.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                log("AD_ALL_ADS_COMPLETED");
                appProgressBar.setVisibility(View.INVISIBLE);
            }
        }, AdPluginEvent.Type.ALL_ADS_COMPLETED);

        player.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                //log("PLAYER PLAY");
                nowPlaying = true;
            }
        }, PlayerEvent.Type.PLAY);

        player.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                //log("PLAYER PAUSE");
                nowPlaying = false;
            }
        }, PlayerEvent.Type.PAUSE);

        player.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                //log("PLAYER ENDED");
                appProgressBar.setVisibility(View.INVISIBLE);
                nowPlaying = false;
            }
        }, PlayerEvent.Type.ENDED);

        player.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                log("SKIPPED");
                nowPlaying = false;
            }
        }, AdPluginEvent.Type.SKIPPED);

        player.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                log("FIRST_QUARTILE");
            }
        }, AdPluginEvent.Type.FIRST_QUARTILE);

        player.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                log("MIDPOINT");
            }
        }, AdPluginEvent.Type.MIDPOINT);

        player.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                log("THIRD_QUARTILE");
            }
        }, AdPluginEvent.Type.THIRD_QUARTILE);

        player.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                log("CLICKED");
                nowPlaying = true;
            }
        }, AdPluginEvent.Type.CLICKED);

        player.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                AdPluginEvent.AdBufferEvent buffEvent = (AdPluginEvent.AdBufferEvent) event;
                log("AD_BUFFER show = " + buffEvent.show);

            }
        }, AdPluginEvent.Type.AD_BUFFER);


        player.addStateChangeListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                if (event instanceof PlayerEvent.StateChanged) {
                    PlayerEvent.StateChanged stateChanged = (PlayerEvent.StateChanged) event;
                    //log("State changed from " + stateChanged.oldState + " to " + stateChanged.newState);
                    if (stateChanged.newState == PlayerState.BUFFERING) {
                        appProgressBar.setVisibility(View.VISIBLE);

                    } else if (stateChanged.newState == PlayerState.READY) {
                        appProgressBar.setVisibility(View.INVISIBLE);
                    }
                    if(controlsView != null){
                        controlsView.setPlayerState(stateChanged.newState);
                    }
                }
            }
        });

        player.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                //When the track data available, this event occurs. It brings the info object with it.
                //PlayerEvent.TracksAvailable tracksAvailable = (PlayerEvent.TracksAvailable) event;
                //populateSpinnersWithTrackInfo(tracksAvailable.tracksInfo);
                //log("PLAYER TRACKS_AVAILABLE");

            }
        }, PlayerEvent.Type.TRACKS_AVAILABLE);

        player.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                //When the track data available, this event occurs. It brings the info object with it.
                //PlayerEvent.PlayheadUpdated playheadUpdated = (PlayerEvent.PlayheadUpdated) event;
                //log.d("playheadUpdated event  position = " + playheadUpdated.position + " duration = " + playheadUpdated.duration);

            }
        }, PlayerEvent.Type.PLAYHEAD_UPDATED);
    }


    private void onCuePointChanged() {

        ((View) adSkin).findViewById(R.id.skip_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (player != null && player.getAdController() != null)
                player.getAdController().skipAd();
            }
        });

        ((View) adSkin).findViewById(R.id.learn_more_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (player != null && player.getAdController() != null) {
                    player.getAdController().openLearnMore();
                }
            }
        });

        LinearLayout companionAdPlaceHolder = (LinearLayout) adSkin.findViewById(R.id.companionAdSlot);
        ((View) companionAdPlaceHolder).findViewById(R.id.imageViewCompanion).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (player != null && player.getAdController() != null) {
                    player.getAdController().openCompanionAdLearnMore();
                }
            }
        });
    }

    private void setFullScreen(boolean isFullScreen) {
        if (player != null && player.getAdController() != null) {
            player.getAdController().screenOrientationChanged(isFullScreen);
        }

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)playerLayout.getLayoutParams();
        // Checks the orientation of the screen
        this.isFullScreen = isFullScreen;
        if (isFullScreen) {
            ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            fullScreenBtn.setImageResource(R.drawable.ic_no_fullscreen);
            params.height = RelativeLayout.LayoutParams.MATCH_PARENT;
            params.width = RelativeLayout.LayoutParams.MATCH_PARENT;

        } else {
            ((AppCompatActivity)getActivity()).getSupportActionBar().show();
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            fullScreenBtn.setImageResource(R.drawable.ic_fullscreen);

            params.height = (int)getResources().getDimension(R.dimen.player_height);
            params.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        }
        playerLayout.requestLayout();
    }

    public interface Logger {
        void log(String logMessage);
    }

    private void log(String message) {
        if (mLog != null) {
            mLog.log(message + "\n");
        }
    }
}
