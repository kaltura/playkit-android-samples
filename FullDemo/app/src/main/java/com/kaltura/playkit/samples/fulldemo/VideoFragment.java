package com.kaltura.playkit.samples.fulldemo;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.kaltura.playkit.PKDrmParams;
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
import com.kaltura.playkit.ads.PKAdEndedReason;
import com.kaltura.playkit.plugins.ads.AdEvent;
import com.kaltura.playkit.plugins.ads.ima.IMAConfig;
import com.kaltura.playkit.plugins.ads.ima.IMAPlugin;
import com.kaltura.playkit.plugins.ads.kaltura.ADConfig;
import com.kaltura.playkit.plugins.ads.kaltura.ADPlugin;
import com.kaltura.playkit.plugins.ovp.KalturaStatsPlugin;
import com.kaltura.playkit.plugins.youbora.YouboraPlugin;

import java.util.ArrayList;
import java.util.List;

import static com.kaltura.playkit.samples.fulldemo.MainActivity.AD_7;

public class VideoFragment extends Fragment {
    private static final String TAG = VideoFragment.class.getSimpleName();
    public static final String STATS_KALTURA_URL = "https://stats.kaltura.com/api_v3/index.php";
    public static final String ANALYTIC_TRIGGER_INTERVAL = "10";

    //private static final String FIRST_SOURCE_URL = "https://devimages.apple.com.edgekey.net/streaming/examples/bipbop_16x9/bipbop_16x9_variant.m3u8";
    //private static final String SECOND_SOURCE_URL = "https://cdnapisec.kaltura.com/p/2215841/sp/221584100/playManifest/entryId/1_w9zx2eti/protocol/https/format/url/falvorIds/1_1obpcggb,1_yyuvftfz,1_1xdbzoa6,1_k16ccgto,1_djdf6bk8/a.mp4";

    private static final String FIRST_SOURCE_URL = "http://cdnapi.kaltura.com/p/243342/sp/24334200/playManifest/entryId/1_sf5ovm7u/flavorIds/1_d2uwy7vv,1_jl7y56al/format/applehttp/protocol/http/a.m3u8";
    private static final String SECOND_SOURCE_URL = "http://fastly.kastatic.org/KA-youtube-converted/PEeUTQ0Gri8.m3u8/PEeUTQ0Gri8.m3u8";

    //id of the first entry
    private static final String FIRST_ENTRY_ID = "entry_id_1";
    //id of the second entry
    private static final String SECOND_ENTRY_ID = "entry_id_2";
    //id of the first media source.
    private static final String FIRST_MEDIA_SOURCE_ID = "source_id_1";
    //id of the second media source.
    private static final String SECOND_MEDIA_SOURCE_ID = "source_id_2";


    //Youbora analytics Constants
    public static final String ACCOUNT_CODE = "your_account_code";
    public static final String USER_NAME = "your_user_name";
    public static final String MEDIA_TITLE = "your_media_title";
    public static final boolean IS_LIVE = false;
    public static final boolean ENABLE_SMART_ADS = true;
    private static final String CAMPAIGN = "your_campaign_name";
    public static final String EXTRA_PARAM_1 = "playKitPlayer";
    public static final String EXTRA_PARAM_2 = "XXX";
    public static final String GENRE = "your_genre";
    public static final String TYPE = "your_type";
    public static final String TRANSACTION_TYPE = "your_trasnsaction_type";
    public static final String YEAR = "your_year";
    public static final String CAST = "your_cast";
    public static final String DIRECTOR = "your_director";
    private static final String OWNER = "your_owner";
    public static final String PARENTAL = "your_parental";
    public static final String PRICE = "your_price";
    public static final String RATING = "your_rating";
    public static final String AUDIO_TYPE = "your_audio_type";
    public static final String AUDIO_CHANNELS = "your_audoi_channels";
    public static final String DEVICE = "your_device";
    public static final String QUALITY = "your_quality";




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

    private void changeMedia() {

        //Before changing media we must call stop on the player.
        player.stop();


        //Check if id of the media entry that is set in mediaConfig.
        if (mediaConfig.getMediaEntry().getId().equals(FIRST_ENTRY_ID)) {
            String AD_HOND = "http://pubads.g.doubleclick.net/gampad/ads?sz=400x300&iu=%2F6062%2Fhanna_MA_group%2Fvideo_comp_app&ciu_szs=&impl=s&gdfp_req=1&env=vp&output=xml_vast3&unviewed_position_start=1&m_ast=vast&url=";
            ADConfig adsConfig = new ADConfig().setAdTagURL(AD_HOND).setPlayerViewContainer(playerLayout).setAdSkinContainer(adSkin).setCompanionAdWidth(728).setCompanionAdHeight(90);

            player.updatePluginConfig(ADPlugin.factory.getName(), adsConfig);
            //If first one is active, prepare second one.
            prepareSecondEntry();
        } else {

            ADConfig adsConfig = new ADConfig().setAdTagURL(AD_7).setPlayerViewContainer(playerLayout).setAdSkinContainer(adSkin).setCompanionAdWidth(728).setCompanionAdHeight(90);

            player.updatePluginConfig(ADPlugin.factory.getName(), adsConfig);
            //If the second one is active, prepare the first one.
            prepareFirstEntry();
        }
    }



    /**
     * Prepare the first entry.
     */
    private void prepareFirstEntry() {
        //Second. Create PKMediaEntry object.
        PKMediaEntry mediaEntry = createFirstMediaEntry();

        //Add it to the mediaConfig.
        mediaConfig.setMediaEntry(mediaEntry);

        //Prepare player with media configuration.
        player.prepare(mediaConfig);
    }

    /**
     * Prepare the second entry.
     */
    private void prepareSecondEntry() {
        //Second. Create PKMediaEntry object.
        PKMediaEntry mediaEntry = createSecondMediaEntry();

        //Add it to the mediaConfig.
        mediaConfig.setMediaEntry(mediaEntry);

        //Prepare player with media configuration.
        player.prepare(mediaConfig);
    }

    private PKMediaEntry createFirstMediaEntry() {
        //Create media entry.
        PKMediaEntry mediaEntry = new PKMediaEntry();

        //Set id for the entry.
        mediaEntry.setId(FIRST_ENTRY_ID);

        //Set media entry type. It could be Live,Vod or Unknown.
        //For now we will use Unknown.
        mediaEntry.setMediaType(PKMediaEntry.MediaEntryType.Vod);

        //Create list that contains at least 1 media source.
        //Each media entry can contain a couple of different media sources.
        //All of them represent the same content, the difference is in it format.
        //For example same entry can contain PKMediaSource with dash and another
        // PKMediaSource can be with hls. The player will decide by itself which source is
        // preferred for playback.
        List<PKMediaSource> mediaSources = createFirstMediaSources();

        //Set media sources to the entry.
        mediaEntry.setSources(mediaSources);

        return mediaEntry;
    }

    /**
     * Create {@link PKMediaEntry} with minimum necessary data.
     *
     * @return - the {@link PKMediaEntry} object.
     */
    private PKMediaEntry createSecondMediaEntry() {
        //Create media entry.
        PKMediaEntry mediaEntry = new PKMediaEntry();

        //Set id for the entry.
        mediaEntry.setId(SECOND_ENTRY_ID);

        //Set media entry type. It could be Live,Vod or Unknown.
        //For now we will use Unknown.
        mediaEntry.setMediaType(PKMediaEntry.MediaEntryType.Vod);

        //Create list that contains at least 1 media source.
        //Each media entry can contain a couple of different media sources.
        //All of them represent the same content, the difference is in it format.
        //For example same entry can contain PKMediaSource with dash and another
        // PKMediaSource can be with hls. The player will decide by itself which source is
        // preferred for playback.
        List<PKMediaSource> mediaSources = createSecondMediaSources();

        //Set media sources to the entry.
        mediaEntry.setSources(mediaSources);

        return mediaEntry;
    }

    private List<PKMediaSource> createFirstMediaSources() {
        //Init list which will hold the PKMediaSources.
        List<PKMediaSource> mediaSources = new ArrayList<>();

        //Create new PKMediaSource instance.
        PKMediaSource mediaSource = new PKMediaSource();

        //Set the id.
        mediaSource.setId(FIRST_MEDIA_SOURCE_ID);

        //Set the content url. In our case it will be link to hls source(.m3u8).
        mediaSource.setUrl(FIRST_SOURCE_URL);

        //Set the format of the source. In our case it will be hls.
        mediaSource.setMediaFormat(PKMediaFormat.hls);

        //Add media source to the list.
        mediaSources.add(mediaSource);

        return mediaSources;
    }

    /**
     * Create list of {@link PKMediaSource}.
     *
     * @return - the list of sources.
     */
    private List<PKMediaSource> createSecondMediaSources() {
        //Init list which will hold the PKMediaSources.
        List<PKMediaSource> mediaSources = new ArrayList<>();

        //Create new PKMediaSource instance.
        PKMediaSource mediaSource = new PKMediaSource();

        //Set the id.
        mediaSource.setId(SECOND_MEDIA_SOURCE_ID);

        mediaSource.setUrl(SECOND_SOURCE_URL);

        mediaSource.setMediaFormat(PKMediaFormat.hls);

        //Add media source to the list.
        mediaSources.add(mediaSource);

        return mediaSources;
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
        //PlayKitManager.registerPlugins(this.getActivity(), IMAPlugin.factory);
        PlayKitManager.registerPlugins(this.getActivity(), KalturaStatsPlugin.factory);
        PlayKitManager.registerPlugins(getActivity(), YouboraPlugin.factory);

        PKPluginConfigs pluginConfig = new PKPluginConfigs();
        addAdPluginConfig(pluginConfig, playerLayout, adSkin);
        //addIMAPluginConfig(pluginConfig, mVideoItem.getAdTagUrl());
        addKalturaStatsPlugin(pluginConfig);
        addYouboraPlugin(pluginConfig);

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

        String adtag = mVideoItem.getAdTagUrl();
        if (adtag.endsWith("correlator=")) {
            adtag += System.currentTimeMillis() + 100000;
        }
        ADConfig adsConfig = new ADConfig().setAdTagURL(adtag).setPlayerViewContainer(layout).setAdSkinContainer(adSkin).setCompanionAdWidth(728).setCompanionAdHeight(90);
        config.setPluginConfig(ADPlugin.factory.getName(), adsConfig);
    }

    private void addIMAPluginConfig(PKPluginConfigs config, String adTagUrl) {

        List<String> videoMimeTypes = new ArrayList<>();
        //videoMimeTypes.add(MimeTypes.APPLICATION_MP4);
        //videoMimeTypes.add(MimeTypes.APPLICATION_M3U8);
        //Map<Double, String> tagTimesMap = new HashMap<>();
        //tagTimesMap.put(2.0,"ADTAG");

        IMAConfig adsConfig = new IMAConfig().setAdTagURL(adTagUrl);
        config.setPluginConfig(IMAPlugin.factory.getName(), adsConfig.toJSONObject());
    }

    private void addKalturaStatsPlugin(PKPluginConfigs config) {
        JsonObject pluginEntry = new JsonObject();
        pluginEntry.addProperty("uiconfId", "123456");
        pluginEntry.addProperty("baseUrl", STATS_KALTURA_URL);
        pluginEntry.addProperty("partnerId", Integer.parseInt("123456"));
        pluginEntry.addProperty("timerInterval", Integer.parseInt(ANALYTIC_TRIGGER_INTERVAL));
        pluginEntry.addProperty("entryId", "1_abcdefg");

        config.setPluginConfig(KalturaStatsPlugin.factory.getName(), pluginEntry);
    }


    private void addYouboraPlugin(PKPluginConfigs pluginConfigs) {
        JsonPrimitive accountCode = new JsonPrimitive(ACCOUNT_CODE);
        JsonPrimitive username = new JsonPrimitive(USER_NAME);
        JsonPrimitive haltOnError = new JsonPrimitive(true);
        JsonPrimitive enableAnalytics = new JsonPrimitive(true);
        JsonPrimitive enableSmartAds = new JsonPrimitive(ENABLE_SMART_ADS);

        JsonObject mediaEntry = new JsonObject();
        mediaEntry.addProperty("isLive", false);
        mediaEntry.addProperty("title", MEDIA_TITLE);

        JsonObject adsEntry = new JsonObject();
        adsEntry.addProperty("campaign", CAMPAIGN);

        JsonObject extraParamEntry = new JsonObject();
        extraParamEntry.addProperty("param1", "mobile");
        extraParamEntry.addProperty("param2", EXTRA_PARAM_2);
        extraParamEntry.addProperty("param3", "CCC");

        JsonObject propertiesEntry = new JsonObject();
        propertiesEntry.addProperty("genre", GENRE);
        propertiesEntry.addProperty("type", TYPE);
        propertiesEntry.addProperty("transaction_type", TRANSACTION_TYPE);
        propertiesEntry.addProperty("year", YEAR);
        propertiesEntry.addProperty("cast", CAST);
        propertiesEntry.addProperty("director", DIRECTOR);
        propertiesEntry.addProperty("owner", OWNER);
        propertiesEntry.addProperty("parental", PARENTAL);
        propertiesEntry.addProperty("price", PRICE);
        propertiesEntry.addProperty("rating", RATING);
        propertiesEntry.addProperty("audioType", AUDIO_TYPE);
        propertiesEntry.addProperty("audioChannels", AUDIO_CHANNELS);
        propertiesEntry.addProperty("device", DEVICE);
        propertiesEntry.addProperty("quality", QUALITY);


        ConverterYoubora converterYoubora = new ConverterYoubora(accountCode, username, haltOnError, enableAnalytics, enableSmartAds,
                mediaEntry,
                adsEntry, extraParamEntry, propertiesEntry);

        pluginConfigs.setPluginConfig(YouboraPlugin.factory.getName(), converterYoubora.toJson());
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
        mediaEntry.setDuration(883 * 1000);
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

        if (!TextUtils.isEmpty(mVideoItem.getVideoLic())) {
            List<PKDrmParams> pkDrmDataList = new ArrayList<>();
            PKDrmParams pkDrmParams = null;
            if (mVideoItem.getVideoUrl().endsWith("mpd")) {
                pkDrmParams = new PKDrmParams(mVideoItem.getVideoLic(), PKDrmParams.Scheme.WidevineCENC);
                mediaSource.setMediaFormat(PKMediaFormat.dash);
            } else {
                pkDrmParams = new PKDrmParams(mVideoItem.getVideoLic(), PKDrmParams.Scheme.WidevineClassic);
                mediaSource.setMediaFormat(PKMediaFormat.wvm);
            }
            pkDrmDataList.add(pkDrmParams);
            mediaSource.setDrmData(pkDrmDataList);

        } else {
            //Set the format of the source. In our case it will be hls.
            mediaSource.setMediaFormat(PKMediaFormat.hls);
        }
        //Set the content url. In our case it will be link to hls source(.m3u8).
        mediaSource.setUrl(mVideoItem.getVideoUrl());


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

        Button changeMediaButton = (Button)rootView.findViewById(R.id.changeMedia);
        //Set click listener.
        changeMediaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Change media.
                changeMedia();
            }
        });
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
        }, AdEvent.Type.ADS_PLAYBACK_ENDED);


        player.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                AdEvent.AdRequestedEvent adRequestEvent = (AdEvent.AdRequestedEvent) event;
                log("AD_REQUESTED");// adtag = " + adRequestEvent.adTagUrl);
            }
        }, AdEvent.Type.AD_REQUESTED);


        player.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                AdEvent.AdProgressUpdateEvent adEventProress = (AdEvent.AdProgressUpdateEvent) event;
                //log.d("received NEW AD_PROGRESS_UPDATE " + adEventProress.currentPosition + "/" +  adEventProress.duration);
            }
        }, AdEvent.Type.AD_POSITION_UPDATED);


        player.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                AdEvent.Error adError = (AdEvent.Error) event;
                Log.d(TAG, "AD_ERROR " + adError.type + " "  + adError.error.message);
                appProgressBar.setVisibility(View.INVISIBLE);
                log("AD_ERROR");
            }
        }, AdEvent.Type.ERROR);


        player.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                log("AD_BREAK_STARTED");
                appProgressBar.setVisibility(View.VISIBLE);
            }
        }, AdEvent.Type.AD_BREAK_STARTED);

        player.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                AdEvent.AdCuePointsChangedEvent cuePointsList = (AdEvent.AdCuePointsChangedEvent) event;
                Log.d(TAG, "Has Postroll = " + cuePointsList.adCuePoints.hasPostRoll());
                log("AD_CUEPOINTS_UPDATED");
                onCuePointChanged();
            }
        }, AdEvent.Type.AD_CUEPOINTS_UPDATED);

        player.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                AdEvent.AdLoadedEvent adLoadedEvent = (AdEvent.AdLoadedEvent) event;
                log("AD_LOADED " + adLoadedEvent.adInfo.getAdIndexInPod() + "/" + adLoadedEvent.adInfo.getTotalAdsInPod());
                appProgressBar.setVisibility(View.INVISIBLE);
            }
        }, AdEvent.Type.AD_LOADED);

        player.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                log("AD_STARTED ");
                appProgressBar.setVisibility(View.INVISIBLE);
            }
        }, AdEvent.Type.AD_STARTED);

        player.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                AdEvent.AdEndedEvent adEndedEvent = (AdEvent.AdEndedEvent) event;
                if (adEndedEvent.adEndedReason == PKAdEndedReason.COMPLETED) {
                    log("AD_ENDED-" + adEndedEvent.adEndedReason);
                } else if (adEndedEvent.adEndedReason == PKAdEndedReason.SKIPPED) {
                    log("AD_ENDED-" + adEndedEvent.adEndedReason);
                    nowPlaying = false;
                }
                appProgressBar.setVisibility(View.INVISIBLE);
            }
        }, AdEvent.Type.AD_ENDED);

        player.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                log("AD_RESUMED");
                nowPlaying = true;
                appProgressBar.setVisibility(View.INVISIBLE);
            }
        }, AdEvent.Type.AD_RESUMED);


        player.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                log("AD_PAUSED");
                nowPlaying = true;
                appProgressBar.setVisibility(View.INVISIBLE);
            }
        }, AdEvent.Type.AD_PAUSED);

        player.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                log("AD_ALL_ADS_COMPLETED");
                appProgressBar.setVisibility(View.INVISIBLE);
            }
        }, AdEvent.Type.ALL_ADS_COMPLETED);

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
                appProgressBar.setVisibility(View.INVISIBLE);
                nowPlaying = false;
            }
        }, PlayerEvent.Type.ERROR);

        player.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                log("FIRST_QUARTILE");
            }
        }, AdEvent.Type.FIRST_QUARTILE);

        player.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                log("MIDPOINT");
            }
        }, AdEvent.Type.MIDPOINT);

        player.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                log("THIRD_QUARTILE");
            }
        }, AdEvent.Type.THIRD_QUARTILE);


        player.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                log("AD_BREAK_ENDED");
            }
        }, AdEvent.Type.AD_BREAK_ENDED);

        player.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                log("AD_CLICKED");
                AdEvent.AdClickEvent advtClickEvent = (AdEvent.AdClickEvent) event;
                Log.d(TAG, "AD_CLICKED url = " + advtClickEvent.advtLink);
                nowPlaying = false;
            }
        }, AdEvent.Type.AD_CLICKED);

        player.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                log("COMPANION_AD_CLICKED");
                AdEvent.CompanionAdClickEvent advtCompanionClickEvent = (AdEvent.CompanionAdClickEvent) event;
                Log.d(TAG, "COMPANION_AD_CLICKED url = " + advtCompanionClickEvent.advtCompanionLink);
                nowPlaying = false;
            }
        }, AdEvent.Type.COMPANION_AD_CLICKED);

        player.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                log("AD_STARTED_BUFFERING");
                appProgressBar.setVisibility(View.VISIBLE);

            }
        }, AdEvent.Type.AD_STARTED_BUFFERING);

        player.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                log("AD_PLAYBACK_READY");
                appProgressBar.setVisibility(View.INVISIBLE);

            }
        }, AdEvent.Type.AD_PLAYBACK_READY);

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

        (adSkin).findViewById(R.id.skip_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (player != null && player.getAdController() != null)
                player.getAdController().skipAd();
            }
        });

        (adSkin).findViewById(R.id.learn_more_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (player != null && player.getAdController() != null) {
                    player.getAdController().openLearnMore();
                }
            }
        });

        LinearLayout companionAdPlaceHolder = (LinearLayout) adSkin.findViewById(R.id.companionAdSlot);
        (companionAdPlaceHolder).findViewById(R.id.imageViewCompanion).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (player != null && player.getAdController() != null) {
                    player.getAdController().openCompanionAdLearnMore();
                }
            }
        });
    }

    private void setFullScreen(boolean isFullScreen) {
        if (isFullScreen == this.isFullScreen) {
            return;
        }

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
