package com.kaltura.playkit.samples.fulldemo;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    //private VideoPlayerController mVideoPlayerController;
    private VideoItem mVideoItem;
    private TextView mVideoTitle;
    FrameLayout playerLayout;
    RelativeLayout adSkin;
    private Player player;
    private PlaybackControlsView controlsView;
    private boolean nowPlaying;
    ProgressBar progressBar;
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

        String error_ad = "https://in-viacom18.videoplaza.tv/proxy/distributor/v2?s=viacom18/hindi/COH&t=Content+Type=Full+Episode,Series+Title=Shani,Gender=,GeoCity=,Age=,Carrier=,Media+ID=490060,Genre=Mythology,SBU=COH,Content+Name=From+royalty+to+rubble,OEM=LGE,Language=Hindi,WiFi=Y,appversion=0.5.119,useragent=Android+LGE+google+Nexus+5,KidsPinEnabled=false&tt=p%2Cm%2Cpo&bp=366.6,727.2,1095.6&rnd=8170019078998&cd=1224&vbw=400&ang_pbname=https%3A%2F%2Fplay.google.com%2Fstore%2Fapps%2Fdetails%3Fid%3Dcom.tv.v18.viola&pid=62952433-ccf0-4952-8bef-27920bfd739a&rt=vmap_1.0&pf=and_6.0.1&cp.useragent=Android+LGE+google+Nexus+5&cp.adid=d928acbe-4276-422e-b97a-9d9b681f94c3&cp.optout=false&cp.deviceid=3a6cabcc961b0229&cp.osversion=6.0.1";
        String multi_ad_vast = "https://pubads.g.doubleclick.net/gampad/ads?slotname=/124319096/external/ad_rule_samples&sz=640x480&ciu_szs=300x250&unviewed_position_start=1&output=xml_vast3&impl=s&env=vp&gdfp_req=1&ad_rule=0&cue=15000&vad_type=linear&vpos=midroll&pod=2&mridx=1&pmnd=0&pmxd=31000&pmad=-1&vrid=6616&cust_params=deployment%3Ddevsite%26sample_ar%3Dpremidpostoptimizedpod&url=https://developers.google.com/interactive-media-ads/docs/sdks/html5/tags&video_doc_id=short_onecue&cmsid=496&kfa=0&tfcd=0";
        String skip_ad = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dskippablelinear&correlator=";
        String adTagUrl = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/ad_rule_samples&ciu_szs=300x250&ad_rule=1&impl=s&gdfp_req=1&env=vp&output=vmap&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ar%3Dpremidpostoptimizedpodbumper&cmsid=496&vid=short_onecue&correlator=";
        String v18_ad_vmap = "https://in-viacom18.videoplaza.tv/proxy/distributor/v2?s=VORG&t=Language=Hindi,Series%20Title=Yo%20Ke%20Hua%20Bro,Genre=Drama,SBU=VORG,Content%20Type=Full%20Episode,Media%20ID=524406,Age=,Gender=&tt=p,m,po&rt=vmap_1.0&rnd=0.15867538995841546&pf=html5&cd=1435000&bp=464,764,1100";
        String ad_hls = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=%2F57869717%2FDeportes%2FDeportes_Videos&gdfp_req=1&env=vp&output=xml_vast3&unviewed_position_start=1&url=http%3A%2F%2Fcdnapi.kaltura.com%2Fhtml5%2Fhtml5lib%2Fv2.59%2FmwEmbedFrame.php%2Fp%2F1901501%2Fuiconf_id%2F28709932%2Fentry_id%2F0_lpgr4luv%3Fwid%3D_1901501%26iframeembed%3Dtrue%26playerId%3Dkaltura_player_1448321939%26entry_id%3D0_lpgr4luv%26flashvars%255BstreamerType%255D%3Dauto&description_url=%5Bdescription_url%5D&correlator=3547248123560359&sdkv=h.3.176.0&sdki=3c0d&scor=2332314844558947&adk=333819758&u_so=l&osd=2&frm=0&sdr=1&mpt=kaltura%2FmwEmbed&mpv=2.59&afvsz=200x200%2C250x250%2C300x250%2C336x280%2C450x50%2C468x60%2C480x70%2C728x90&ged=ve4_td2_tt0_pd2_la2000_er0.0.153.300_vi0.0.916.1127_vp100_eb24171";
        String honda_ad =  "http://pubads.g.doubleclick.net/gampad/ads?sz=400x300&iu=%2F6062%2Fhanna_MA_group%2Fvideo_comp_app&ciu_szs=&impl=s&gdfp_req=1&env=vp&output=xml_vast3&unviewed_position_start=1&m_ast=vast&url=";
        String google_ad = "http://pubads.g.doubleclick.net/gampad/ads?sz=640x360&iu=/6062/iab_vast_samples/skippable&ciu_szs=300x250,728x90&impl=s&gdfp_req=1&env=vp&output=xml_vast2&unviewed_position_start=1&url=[referrer_url]&correlator=[timestamp]";
        String two_comp  = "http://pubads.g.doubleclick.net/gampad/ads?sz=640x360&iu=/6062/iab_vast_samples/skippable&ciu_szs=300x250,728x90&impl=s&gdfp_req=1&env=vp&output=xml_vast2&unviewed_position_start=1&url=";

        String ps_pwc = "https://pubads.g.doubleclick.net/gampad/ads?sz=1920x1080&iu=%2F210325652%2FK00001&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&url=https%3A%2F%2Fwww.kocowa.com&description_url=https%3A%2F%2Fwww.kocowa.com&correlator=1500328110&ad_rule=1&cmsid=2456701&vid=0_9yjd6dhw";


        String wrapper = "https://kaltura.github.io/playkit-admanager-samples/vast-wrapper.xml";
        String pods_noam = "https://kaltura.github.io/playkit-admanager-samples/vast-pod-inline-someskip.xml";
        String vmap_preroll = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/ad_rule_samples&ciu_szs=300x250&ad_rule=1&impl=s&gdfp_req=1&env=vp&output=vmap&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ar%3Dpreonly&cmsid=496&vid=short_onecue&correlator=";
        String vmap_pre_bump = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/ad_rule_samples&ciu_szs=300x250&ad_rule=1&impl=s&gdfp_req=1&env=vp&output=vmap&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ar%3Dpreonlybumper&cmsid=496&vid=short_onecue&correlator=";
        String vmap_single_pmp = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/ad_rule_samples&ciu_szs=300x250&ad_rule=1&impl=s&gdfp_req=1&env=vp&output=vmap&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ar%3Dpremidpost&cmsid=496&vid=short_onecue&correlator=";
        String vmap_pods = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/ad_rule_samples&ciu_szs=300x250&ad_rule=1&impl=s&gdfp_req=1&env=vp&output=vmap&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ar%3Dpremidpostpod&cmsid=496&vid=short_onecue&correlator=";
        String vmap_postroll_bump = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/ad_rule_samples&ciu_szs=300x250&ad_rule=1&impl=s&gdfp_req=1&env=vp&output=vmap&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ar%3Dpostonlybumper&cmsid=496&vid=short_onecue&correlator=";
        String complicatedVmap = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/ad_rule_samples&ciu_szs=300x250&ad_rule=1&impl=s&gdfp_req=1&env=vp&output=vmap&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ar%3Dpremidpostoptimizedpodbumper&cmsid=496&vid=short_onecue&correlator=";
        String veryComplicatedVmap = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/ad_rule_samples&ciu_szs=300x250&ad_rule=1&impl=s&gdfp_req=1&env=vp&output=vmap&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ar%3Dpremidpostlongpod&cmsid=496&vid=short_tencue&correlator=";

        String voot1 = "https://in-viacom18.videoplaza.tv/proxy/distributor/v2?s=viacom18/hindi/COH&t=Content+Type=Full+Episode,Series+Title=KASAM,Gender=,GeoCity=,Age=,Carrier=,Media+ID=491766,Genre=Romance,SBU=COH,Content+Name=Rishi+is+done+with+Tanuja,OEM=samsung,Language=Hindi,WiFi=Y,appversion=1.6.119,useragent=Android+Samsung+GT-I9195,KidsPinEnabled=false&tt=p%2Cm%2Cpo&bp=368.4,724.19995,1093.2&rnd=8170019078998&cd=1219&vbw=400&ang_pbname=https%3A%2F%2Fplay.google.com%2Fstore%2Fapps%2Fdetails%3Fid%3Dcom.tv.v18.viola&pid=c18b43e8-53c1-49cb-9a82-1d3b67259cab&rt=vmap_1.0&pf=and_4.4.2&cp.useragent=Android+Samsung+GT-I9195&cp.adid=cp.optout&cp.optout=true&cp.deviceid=b2c02520d06e1bfe&cp.osversion=4.4.2";
        String voot2 = "https://in-viacom18.videoplaza.tv/proxy/distributor/v2?s=viacom18/hindi/COH&t=Content+Type=Full+Episode,Series+Title=Shani,Gender=,GeoCity=,Age=,Carrier=,Media+ID=490060,Genre=Mythology,SBU=COH,Content+Name=From+royalty+to+rubble,OEM=LGE,Language=Hindi,WiFi=Y,appversion=0.5.119,useragent=Android+LGE+google+Nexus+5,KidsPinEnabled=false&tt=p%2Cm%2Cpo&bp=366.6,727.2,1095.6&rnd=8170019078998&cd=1224&vbw=400&ang_pbname=https%3A%2F%2Fplay.google.com%2Fstore%2Fapps%2Fdetails%3Fid%3Dcom.tv.v18.viola&pid=62952433-ccf0-4952-8bef-27920bfd739a&rt=vmap_1.0&pf=and_6.0.1&cp.useragent=Android+LGE+google+Nexus+5&cp.adid=d928acbe-4276-422e-b97a-9d9b681f94c3&cp.optout=false&cp.deviceid=3a6cabcc961b0229&cp.osversion=6.0.1";
        String voot3 = "https://in-viacom18.videoplaza.tv/proxy/distributor/v2?s=viacom18/youth/MTV&t=Content+Type=Full+Episode,Series+Title=MTV+Roadies+Rising,Gender=,GeoCity=,Age=,Carrier=,Media+ID=489557,Genre=Reality,SBU=MTV,Content+Name=Ball+baby+ball%21,OEM=LYF,Language=Hindi,WiFi=Y,appversion=1.6.119,useragent=Android+LYF+LS-5015,KidsPinEnabled=false&tt=p%2Cm%2Cpo&bp=376.80002,732.0,1149.0,1527.0,1887.6,2178.0&rnd=8170019078998&cd=2514&vbw=400&ang_pbname=https%3A%2F%2Fplay.google.com%2Fstore%2Fapps%2Fdetails%3Fid%3Dcom.tv.v18.viola&pid=c11ce254-6fcb-4706-a6ed-9a8509988e81&rt=vmap_1.0&pf=and_5.1.1&cp.useragent=Android+LYF+LS-5015&cp.adid=e767741e-4fc5-433c-bc44-56c84c764124&cp.optout=false&cp.deviceid=edc0fb87a291cef0&cp.osversion=5.1.1";

        //"https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dskippablelinear&correlator=";
        //"https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/3274935/preroll&impl=s&gdfp_req=1&env=vp&output=xml_vast2&unviewed_position_start=1&url=[referrer_url]&description_url=[description_url]&correlator=[timestamp]";
        //"https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/ad_rule_samples&ciu_szs=300x250&ad_rule=1&impl=s&gdfp_req=1&env=vp&output=vmap&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ar%3Dpremidpostpod&cmsid=496&vid=short_onecue&correlator=";
        List<String> videoMimeTypes = new ArrayList<>();
        //videoMimeTypes.add(MimeTypes.APPLICATION_MP4);
        //videoMimeTypes.add(MimeTypes.APPLICATION_M3U8);
        //Map<Double, String> tagTimesMap = new HashMap<>();
        //tagTimesMap.put(2.0,"ADTAG");

        //IMAConfig adsConfig = new IMAConfig().setAdTagURL(adTagUrl);
        //config.setPluginConfig(IMAPlugin.factory.getName(), adsConfig.toJSONObject());

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

        //View playButton = rootView.findViewById(R.id.playButton);
        View playPauseToggle = rootView.findViewById(R.id.player_root);
        ViewGroup companionAdSlot = (ViewGroup) rootView.findViewById(R.id.companionAdSlot);
        mVideoTitle = (TextView) rootView.findViewById(R.id.video_title);
        playerLayout = (FrameLayout) rootView.findViewById(R.id.player_root);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBarSpinner);
        controlsView = (PlaybackControlsView) rootView.findViewById(R.id.playerControls);
        progressBar.setVisibility(View.INVISIBLE);
        adSkin = (RelativeLayout) rootView.findViewById(R.id.ad_skin);

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

//        mVideoPlayerController = new VideoPlayerController(this.getActivity(),
//                null, playPauseToggle,
//                getString(R.string.ad_ui_lang), companionAdSlot, logger);

        // If we've already selected a video, load it now.
        mLog = logger;
        if (mVideoItem != null) {
            loadVideo(mVideoItem);
        }
    }

    /**
     * Shows or hides all non-video UI elements to make the video as large as possible.
     */
    public void makeFullscreen(boolean isFullscreen) {
//        for (int i = 0; i < mVideoExampleLayout.getChildCount(); i++) {
//            View view = mVideoExampleLayout.getChildAt(i);
//            // If it's not the video element, hide or show it, depending on fullscreen status.
//            if (view.getId() != R.id.player_root) {
//                if (isFullscreen) {
//                    view.setVisibility(View.GONE);
//                } else {
//                    view.setVisibility(View.VISIBLE);
//                }
//            }
//        }
    }

    //public VideoPlayerController getVideoPlayerController() {
   //     return mVideoPlayerController;
   // }

    @Override
    public void onPause() {
        //if (mVideoPlayerController != null) {
        //    mVideoPlayerController.pause();
        //}
        super.onPause();
        if (player != null) {
            player.onApplicationPaused();
        }
    }

    @Override
    public void onResume() {
        //if (mVideoPlayerController != null) {
        //    mVideoPlayerController.resume();
        //}
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
                log("CONTENT_RESUME_REQUESTED");
            }
        }, AdPluginEvent.Type.CONTENT_RESUME_REQUESTED);


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
                AdPluginErrorEvent.AdErrorEvent adError = (AdPluginErrorEvent.AdErrorEvent) event;
                log("AD_ERROR");// + adError.adErrorEvent.type + " "  + adError.adErrorMessage);
            }
        }, AdPluginErrorEvent.Type.AD_ERROR);


        player.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                log("CONTENT_PAUSE_REQUESTED");
                appProgressBar.setVisibility(View.VISIBLE);
            }
        }, AdPluginEvent.Type.CONTENT_PAUSE_REQUESTED);

        player.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                AdPluginEvent.CuePointsChangedEvent cuePointsList = (AdPluginEvent.CuePointsChangedEvent) event;
                //AdCuePoints adCuePoints = new AdCuePoints(cuePointsList.cuePoints);
                //if (adCuePoints != null) {
                    log("CUEPOINTS_CHANGED");//"Has Postroll = " + adCuePoints.hasPostRoll());
                //}
                onCuepointChanged();
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
                PlayerEvent.TracksAvailable tracksAvailable = (PlayerEvent.TracksAvailable) event;
                //populateSpinnersWithTrackInfo(tracksAvailable.tracksInfo);
                //log("PLAYER TRACKS_AVAILABLE");

            }
        }, PlayerEvent.Type.TRACKS_AVAILABLE);

        player.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                //When the track data available, this event occurs. It brings the info object with it.
                PlayerEvent.PlayheadUpdated playheadUpdated = (PlayerEvent.PlayheadUpdated) event;
                //log.d("playheadUpdated event  position = " + playheadUpdated.position + " duration = " + playheadUpdated.duration);

            }
        }, PlayerEvent.Type.PLAYHEAD_UPDATED);
    }


    private void onCuepointChanged() {

        ((View) adSkin).findViewById(R.id.skip_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                player.getAdController().skipAd();
            }
        });

        ((View) adSkin).findViewById(R.id.learn_more_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                player.getAdController().openLearnMore();
            }
        });

        LinearLayout companionAdPlaceHolder = (LinearLayout) adSkin.findViewById(R.id.companionAdSlot);
        ((View) companionAdPlaceHolder).findViewById(R.id.imageViewCompanion).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                player.getAdController().openCompanionAdLearnMore();
            }
        });
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
