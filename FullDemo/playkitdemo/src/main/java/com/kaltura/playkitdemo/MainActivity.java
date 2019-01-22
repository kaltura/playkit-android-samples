package com.kaltura.playkitdemo;

import android.Manifest;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.kaltura.netkit.connect.response.PrimitiveResult;
import com.kaltura.netkit.connect.response.ResultElement;
import com.kaltura.netkit.utils.OnCompletion;
import com.kaltura.netkit.utils.SessionProvider;
import com.kaltura.playkit.PKDrmParams;
import com.kaltura.playkit.PKEvent;
import com.kaltura.playkit.PKLog;
import com.kaltura.playkit.PKMediaConfig;
import com.kaltura.playkit.PKMediaEntry;
import com.kaltura.playkit.PKMediaSource;
import com.kaltura.playkit.PKPluginConfigs;
import com.kaltura.playkit.PlayKitManager;
import com.kaltura.playkit.Player;
import com.kaltura.playkit.PlayerEvent;
import com.kaltura.playkit.PlayerState;
import com.kaltura.playkit.player.AudioTrack;
import com.kaltura.playkit.player.BaseTrack;
import com.kaltura.playkit.player.MediaSupport;
import com.kaltura.playkit.player.PKTracks;
import com.kaltura.playkit.player.TextTrack;
import com.kaltura.playkit.player.VideoTrack;
import com.kaltura.playkit.plugins.SamplePlugin;
import com.kaltura.playkit.plugins.ads.AdCuePoints;
import com.kaltura.playkit.plugins.ads.AdEvent;
import com.kaltura.playkit.plugins.ima.IMAConfig;
import com.kaltura.playkit.plugins.ima.IMAPlugin;
//import com.kaltura.playkit.plugins.imadai.IMADAIConfig;
//import com.kaltura.playkit.plugins.imadai.IMADAIPlugin;
import com.kaltura.playkit.plugins.kava.KavaAnalyticsConfig;
import com.kaltura.playkit.plugins.kava.KavaAnalyticsPlugin;
import com.kaltura.playkit.plugins.ott.OttEvent;
import com.kaltura.playkit.plugins.ott.PhoenixAnalyticsConfig;
import com.kaltura.playkit.plugins.ott.PhoenixAnalyticsEvent;
import com.kaltura.playkit.plugins.ott.PhoenixAnalyticsPlugin;
import com.kaltura.playkit.plugins.ovp.KalturaStatsConfig;
import com.kaltura.playkit.plugins.ovp.KalturaStatsPlugin;
import com.kaltura.playkit.plugins.playback.KalturaPlaybackRequestAdapter;
import com.kaltura.playkit.plugins.playback.KalturaUDRMLicenseRequestAdapter;
import com.kaltura.playkit.plugins.youbora.YouboraPlugin;
import com.kaltura.playkit.providers.MediaEntryProvider;
import com.kaltura.playkit.providers.api.SimpleSessionProvider;
import com.kaltura.playkit.providers.api.phoenix.APIDefines;
import com.kaltura.playkit.providers.base.OnMediaLoadCompletion;
import com.kaltura.playkit.providers.mock.MockMediaProvider;
import com.kaltura.playkit.providers.ott.PhoenixMediaProvider;
import com.kaltura.playkit.providers.ovp.KalturaOvpMediaProvider;
import com.kaltura.playkit.utils.Consts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static com.kaltura.playkit.utils.Consts.DISTANCE_FROM_LIVE_THRESHOLD;
import static com.kaltura.playkitdemo.MockParams.Format;
import static com.kaltura.playkitdemo.MockParams.Format2;
import static com.kaltura.playkitdemo.MockParams.Format_HD_Dash;
import static com.kaltura.playkitdemo.MockParams.Format_SD_Dash;
import static com.kaltura.playkitdemo.MockParams.OvpUserKS;
import static com.kaltura.playkitdemo.MockParams.PnxKS;
import static com.kaltura.playkitdemo.MockParams.SingMediaId;
import static com.kaltura.playkitdemo.MockParams.SingMediaId4;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,
        OrientationManager.OrientationListener {


    public static final boolean AUTO_PLAY_ON_RESUME = true;

    private static final PKLog log = PKLog.get("MainActivity");
    public static int changeMediaIndex = -1;
    public static Long START_POSITION = 0L;

    String preMidPostAdTagUrl = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/ad_rule_samples&ciu_szs=300x250&ad_rule=1&impl=s&gdfp_req=1&env=vp&output=vmap&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ar%3Dpremidpostpodbumper&cmsid=496&vid=short_onecue&correlator=";
    String preSKipAdTagUrl    = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dskippablelinear&correlator=";
    String inLinePreAdTagUrl = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dlinear&correlator=";
    String preMidPostSingleAdTagUrl = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/ad_rule_samples&ciu_szs=300x250&ad_rule=1&impl=s&gdfp_req=1&env=vp&output=vmap&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ar%3Dpremidpost&cmsid=496&vid=short_onecue&correlator=";
    String KALTURA_STATS_URL = "https://stats.kaltura.com/api_v3/index.php";

    private Player player;
    private MediaEntryProvider mediaProvider;
    private PlaybackControlsView controlsView;
    private boolean nowPlaying;
    private boolean isFullScreen;
    ProgressBar progressBar;
    private RelativeLayout playerContainer;
    private RelativeLayout spinerContainer;
    private AppCompatImageView fullScreenBtn;
    private AdCuePoints adCuePoints;
    private Spinner videoSpinner, audioSpinner, textSpinner;

    private OrientationManager mOrientationManager;
    private boolean userIsInteracting;
    private PKTracks tracksInfo;

    private void registerPlugins() {

        PlayKitManager.registerPlugins(this, SamplePlugin.factory);
        PlayKitManager.registerPlugins(this, IMAPlugin.factory);
        //PlayKitManager.registerPlugins(this, IMADAIPlugin.factory);
        PlayKitManager.registerPlugins(this, KalturaStatsPlugin.factory);
        PlayKitManager.registerPlugins(this, KavaAnalyticsPlugin.factory);
        PlayKitManager.registerPlugins(this, YouboraPlugin.factory);
        //PlayKitManager.registerPlugins(this, TVPAPIAnalyticsPlugin.factory);
        //PlayKitManager.registerPlugins(this, PhoenixAnalyticsPlugin.factory);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDrm();

        mOrientationManager = new OrientationManager(this, SensorManager.SENSOR_DELAY_NORMAL, this);
        mOrientationManager.enable();
        setContentView(R.layout.activity_main);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(this, "Please tap ALLOW", Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);
        }

        log.i("PlayKitManager: " + PlayKitManager.CLIENT_TAG);

        Button button = findViewById(R.id.changeMedia);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (player != null) {
                    changeMediaIndex++;
                    OnMediaLoadCompletion playLoadedEntry = registerToLoadedMediaCallback();
                    if (changeMediaIndex % 4 == 0) {
                        startSimpleOvpMediaLoadingDRM(playLoadedEntry);
                    } else if (changeMediaIndex % 4 == 1) {
                        startSimpleOvpMediaLoadingLive1(playLoadedEntry);
                    } if (changeMediaIndex % 4 == 2) {
                        startSimpleOvpMediaLoadingClear(playLoadedEntry);
                    } if (changeMediaIndex % 4 == 3) {
                        startSimpleOvpMediaLoadingHls(playLoadedEntry);
                    }
                }
            }
        });

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        registerPlugins();

        OnMediaLoadCompletion playLoadedEntry = registerToLoadedMediaCallback();
        
        startSimpleOvpMediaLoadingHls(playLoadedEntry);
//      startSimpleOvpMediaLoadingLive1(playLoadedEntry);
//      startMockMediaLoading(playLoadedEntry);
//      startOvpMediaLoading(playLoadedEntry);
//      startOttMediaLoading(playLoadedEntry);
//      startSimpleOvpMediaLoadingDRM(playLoadedEntry);
//      LocalAssets.start(this, playLoadedEntry);
        playerContainer = (RelativeLayout)findViewById(R.id.player_container);
        spinerContainer = (RelativeLayout)findViewById(R.id.spiner_container);
        fullScreenBtn = (AppCompatImageView)findViewById(R.id.full_screen_switcher);
        fullScreenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int orient;
                if (isFullScreen) {
                    orient = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                }
                else {
                    orient = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                }
                setRequestedOrientation(orient);
            }
        });
    }

    @NonNull
    private OnMediaLoadCompletion registerToLoadedMediaCallback() {
        return new OnMediaLoadCompletion() {
                            @Override
                            public void onComplete(final ResultElement<PKMediaEntry> response) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (response.isSuccess()) {
                                            onMediaLoaded(response.getResponse());
                                        } else {

                                            Toast.makeText(MainActivity.this, "failed to fetch media data: " + (response.getError() != null ? response.getError().getMessage() : ""), Toast.LENGTH_LONG).show();
                                            log.e("failed to fetch media data: " + (response.getError() != null ? response.getError().getMessage() : ""));
                                        }
                                    }
                                });
                            }
                        };
    }

    private void initDrm() {
        MediaSupport.initializeDrm(this, new MediaSupport.DrmInitCallback() {
            @Override
            public void onDrmInitComplete(Set<PKDrmParams.Scheme> supportedDrmSchemes, boolean provisionPerformed, Exception provisionError) {
                if (provisionPerformed) {
                    if (provisionError != null) {
                        log.e("DRM Provisioning failed", provisionError);
                    } else {
                        log.d("DRM Provisioning succeeded");
                    }
                }
                log.d("DRM initialized; supported: " + supportedDrmSchemes);

                // Now it's safe to look at `supportedDrmSchemes`
            }
        });
    }

    private PKMediaEntry simpleMediaEntry(String id, String contentUrl, String licenseUrl, PKDrmParams.Scheme scheme) {
        return new PKMediaEntry()
                .setSources(Collections.singletonList(new PKMediaSource()
                        .setUrl(contentUrl)
                        .setDrmData(Collections.singletonList(
                                new PKDrmParams(licenseUrl, scheme)
                                )
                        )))
                .setId(id);
    }

    private PKMediaEntry simpleMediaEntry(String id, String contentUrl) {
        return new PKMediaEntry()
                .setSources(Collections.singletonList(new PKMediaSource()
                        .setUrl(contentUrl)
                ))
                .setId(id);
    }

    private void startSimpleOvpMediaLoadingHls(OnMediaLoadCompletion completion) {
        new KalturaOvpMediaProvider()
                .setSessionProvider(new SimpleSessionProvider("https://cdnapisec.kaltura.com", 1734751, null))
                .setEntryId("1_3o1seqnv")
                .load(completion);
    }

    private void startSimpleOvpMediaLoadingDRM(OnMediaLoadCompletion completion) {
        new KalturaOvpMediaProvider()
                .setSessionProvider(new SimpleSessionProvider("https://cdnapisec.kaltura.com", 2222401, null))
                .setEntryId("1_f93tepsn")//("1_asoyc5ef") //("1_uzea2uje")
                .load(completion);
    }

    private void startSimpleOvpMediaLoadingClear(OnMediaLoadCompletion completion) {
        new KalturaOvpMediaProvider()
                .setSessionProvider(new SimpleSessionProvider("http://qa-apache-php7.dev.kaltura.com/", 1091, null))
                .setEntryId("0_wu32qrt3")
                .load(completion);
    }

    private void startSimpleOvpMediaLoadingLive(OnMediaLoadCompletion completion) {
        new KalturaOvpMediaProvider()
                .setSessionProvider(new SimpleSessionProvider("http://qa-apache-php7.dev.kaltura.com/", 1091, null))
                .setEntryId("0_nwkp7jtx")
                .load(completion);
    }

    private void startSimpleOvpMediaLoadingLive1(OnMediaLoadCompletion completion) {
        new KalturaOvpMediaProvider()
                .setSessionProvider(new SimpleSessionProvider("https://cdnapisec.kaltura.com/", 1740481, null))
                .setEntryId("1_fdv46dba")

                .load(completion);
    }

    private void startMockMediaLoading(OnMediaLoadCompletion completion) {

        mediaProvider = new MockMediaProvider("mockfiles/entries.playkit.json", getApplicationContext(), "mp4");

        mediaProvider.load(completion);
    }

    private void startOttMediaLoading(final OnMediaLoadCompletion completion) {
        SessionProvider ksSessionProvider = new SessionProvider() {
            @Override
            public String baseUrl() {
                return MockParams.PhoenixBaseUrl;
            }

            @Override
            public void getSessionToken(OnCompletion<PrimitiveResult> completion) {
                if (completion != null) {
                    completion.onComplete(new PrimitiveResult(PnxKS));
                }
            }

            @Override
            public int partnerId() {
                return MockParams.OttPartnerId;
            }
        };

        mediaProvider = new PhoenixMediaProvider()
                .setSessionProvider(ksSessionProvider)
                .setAssetId(SingMediaId4) //bunny no horses id = "485380"
                .setAssetType(APIDefines.KalturaAssetType.Media)
                .setAssetReferenceType(APIDefines.AssetReferenceType.Media)
                .setContextType(APIDefines.PlaybackContextType.Playback)
                .setProtocol(PhoenixMediaProvider.HttpProtocol.Http)
                .setFormats(Format2);
        mediaProvider.load(completion);
    }

    private void startOvpMediaLoading(final OnMediaLoadCompletion completion) {
        SessionProvider ksSessionProvider = new SessionProvider() {
            @Override
            public String baseUrl() {
                return MockParams.OvpBaseUrl;
            }

            @Override
            public void getSessionToken(OnCompletion<PrimitiveResult> completion) {
                if (completion != null) {
                    completion.onComplete(new PrimitiveResult(OvpUserKS));
                }
            }

            @Override
            public int partnerId() {
                return MockParams.OvpPartnerId;
            }
        };

        mediaProvider = new KalturaOvpMediaProvider().setSessionProvider(ksSessionProvider).setEntryId(MockParams.DRMEntryIdAnm);
        mediaProvider.load(completion);
    }

    private void onMediaLoaded(PKMediaEntry mediaEntry) {

        if (mediaEntry.getMediaType() != PKMediaEntry.MediaEntryType.Vod) {
            START_POSITION = null; // force live streams to play from live edge
        }

        PKMediaConfig mediaConfig = new PKMediaConfig().setMediaEntry(mediaEntry).setStartPosition(START_POSITION);
        PKPluginConfigs pluginConfig = new PKPluginConfigs();
        if (player == null) {

            configurePlugins(pluginConfig);

            player = PlayKitManager.loadPlayer(this, pluginConfig);
            KalturaPlaybackRequestAdapter.install(player, "PlaykitTestApp"); // in case app developer wants to give customized referrer instead the default referrer in the playmanifest
            KalturaUDRMLicenseRequestAdapter.install(player, "PlaykitTestApp");

            player.getSettings().setSecureSurface(false);
            player.getSettings().setAdAutoPlayOnResume(true);
            //player.getSettings().setAllowCrossProtocolRedirect(true);
            // player.getSettings().setPreferredMediaFormat(PKMediaFormat.hls);

            //player.setPlaybackRate(1.5f);
            log.d("Player: " + player.getClass());
            addPlayerListeners(progressBar);

            FrameLayout layout = (FrameLayout) findViewById(R.id.player_root);
            layout.addView(player.getView());

            controlsView = (PlaybackControlsView) this.findViewById(R.id.playerControls);
            controlsView.setPlayer(player);
            initSpinners();
        } else {
            if (changeMediaIndex % 4 == 0) {
                log.d("Play Ad preMidPostAdTagUrl");
                player.updatePluginConfig(IMAPlugin.factory.getName(), getAdsConfig(preMidPostAdTagUrl));
                player.updatePluginConfig(YouboraPlugin.factory.getName(), getYouboraJsonObject(false, "preMidPostAdTagUrl media2"));
                player.updatePluginConfig(KalturaStatsPlugin.factory.getName(), getKalturaStatsConfig(2222401, "1_f93tepsn"));
                player.updatePluginConfig(KavaAnalyticsPlugin.factory.getName(),  getKavaAnalyticsConfig(2222401, "1_f93tepsn"));

            } else if (changeMediaIndex % 4 == 1) {
                log.d("Play Ad inLinePreAdTagUrl");
                player.updatePluginConfig(IMAPlugin.factory.getName(), getAdsConfig(inLinePreAdTagUrl));
                player.updatePluginConfig(YouboraPlugin.factory.getName(), getYouboraJsonObject(true, "inLinePreAdTagUrl media3"));
                player.updatePluginConfig(KalturaStatsPlugin.factory.getName(), getKalturaStatsConfig(1740481, "1_fdv46dba"));
                player.updatePluginConfig(KavaAnalyticsPlugin.factory.getName(),  getKavaAnalyticsConfig(1740481, "1_fdv46dba"));
            } if (changeMediaIndex % 4 == 2) {
                log.d("Play NO Ad");
                player.updatePluginConfig(IMAPlugin.factory.getName(), getAdsConfig(""));
                player.updatePluginConfig(YouboraPlugin.factory.getName(), getYouboraJsonObject(false, "NO AD media4"));
                player.updatePluginConfig(KalturaStatsPlugin.factory.getName(), getKalturaStatsConfig(1091, "0_wu32qrt3"));
                player.updatePluginConfig(KavaAnalyticsPlugin.factory.getName(),  getKavaAnalyticsConfig(1091, "0_wu32qrt3"));
            } if (changeMediaIndex % 4 == 3) {
                log.d("Play Ad preSKipAdTagUrl");
                player.updatePluginConfig(IMAPlugin.factory.getName(), getAdsConfig(preSKipAdTagUrl));
                player.updatePluginConfig(YouboraPlugin.factory.getName(), getYouboraJsonObject(false, "preSKipAdTagUrl media1"));
                player.updatePluginConfig(KalturaStatsPlugin.factory.getName(), getKalturaStatsConfig(1734751, "1_3o1seqnv"));
                player.updatePluginConfig(KavaAnalyticsPlugin.factory.getName(),  getKavaAnalyticsConfig(1734751, "1_3o1seqnv"));
            }
        }

        player.prepare(mediaConfig);
        player.play();
    }

    private void initSpinners() {
        videoSpinner = (Spinner) this.findViewById(R.id.videoSpinner);
        audioSpinner = (Spinner) this.findViewById(R.id.audioSpinner);
        textSpinner = (Spinner) this.findViewById(R.id.subtitleSpinner);

        textSpinner.setOnItemSelectedListener(this);
        audioSpinner.setOnItemSelectedListener(this);
        videoSpinner.setOnItemSelectedListener(this);
    }

    private void configurePlugins(PKPluginConfigs pluginConfigs) {
        addIMAPluginConfig(pluginConfigs);
        //addIMADAIPluginConfig(pluginConfigs);
        addKaluraStatsPluginConfig(pluginConfigs, 1734751, "1_3o1seqnv");

        addYouboraPluginConfig(pluginConfigs, false, "preMidPostSingleAdTagUrl Title1");
        addKavaPluginConfig(pluginConfigs, 1734751, "1_3o1seqnv");
        //addPhoenixAnalyticsPluginConfig(pluginConfigs);
        //addTVPAPIAnalyticsPluginConfig(pluginConfigs);
    }

    private void addKaluraStatsPluginConfig(PKPluginConfigs pluginConfigs, int partnerId, String ovpEntryId) {

        KalturaStatsConfig kalturaStatsConfig = getKalturaStatsConfig(partnerId, ovpEntryId);
        //Set plugin entry to the plugin configs.
        pluginConfigs.setPluginConfig(KalturaStatsPlugin.factory.getName(), kalturaStatsConfig);
    }

    private KalturaStatsConfig getKalturaStatsConfig(int partnerId, String ovpEntryId) {
        return new KalturaStatsConfig(true)
                    .setBaseUrl(KALTURA_STATS_URL)
                    .setPartnerId(partnerId)
                    .setEntryId(ovpEntryId)
                    .setTimerInterval(30);
    }


    private void addKavaPluginConfig(PKPluginConfigs pluginConfigs, int partnerId, String ovpEntryId) {
        KavaAnalyticsConfig kavaAnalyticsConfig = getKavaAnalyticsConfig(partnerId, ovpEntryId);
        //Set plugin entry to the plugin configs.
        pluginConfigs.setPluginConfig(KavaAnalyticsPlugin.factory.getName(), kavaAnalyticsConfig);
    }

    private KavaAnalyticsConfig getKavaAnalyticsConfig(int partnerId, String ovpEntryId) {
        return new KavaAnalyticsConfig()
                    .setApplicationVersion(BuildConfig.VERSION_NAME)
                    .setPartnerId(partnerId)
                    .setEntryId("ovpEntryId")
                    .setDvrThreshold(DISTANCE_FROM_LIVE_THRESHOLD);
    }

    private void addYouboraPluginConfig(PKPluginConfigs pluginConfigs, boolean isLive, String title) {
        JsonObject pluginEntry = getYouboraJsonObject(isLive, title);

        //Set plugin entry to the plugin configs.
        pluginConfigs.setPluginConfig(YouboraPlugin.factory.getName(), pluginEntry);
    }

    @NonNull
    private JsonObject getYouboraJsonObject(boolean isLive, String title) {
        JsonObject pluginEntry = new JsonObject();

        pluginEntry.addProperty("accountCode", "kalturatest");
        pluginEntry.addProperty("username", "a@a.com");
        pluginEntry.addProperty("haltOnError", true);
        pluginEntry.addProperty("enableAnalytics", true);
        pluginEntry.addProperty("enableSmartAds", true);


        //Media entry json.
        JsonObject mediaEntryJson = new JsonObject();
        mediaEntryJson.addProperty("isLive", isLive);
        mediaEntryJson.addProperty("title", title);

        //Youbora ads configuration json.
        JsonObject adsJson = new JsonObject();
        adsJson.addProperty("adsExpected", true);
        adsJson.addProperty("campaign", "zzz");

        //Configure custom properties here:
        JsonObject propertiesJson = new JsonObject();
        propertiesJson.addProperty("genre", "");
        propertiesJson.addProperty("type", "");
        propertiesJson.addProperty("transaction_type", "");
        propertiesJson.addProperty("year", "");
        propertiesJson.addProperty("cast", "");
        propertiesJson.addProperty("director", "");
        propertiesJson.addProperty("owner", "");
        propertiesJson.addProperty("parental", "");
        propertiesJson.addProperty("price", "");
        propertiesJson.addProperty("rating", "");
        propertiesJson.addProperty("audioType", "");
        propertiesJson.addProperty("audioChannels", "");
        propertiesJson.addProperty("device", "");
        propertiesJson.addProperty("quality", "");

        //You can add some extra params here:
        JsonObject extraParamJson = new JsonObject();
        extraParamJson.addProperty("param1", "param1");
        extraParamJson.addProperty("param2", "param2");

        //Add all the json objects created before to the pluginEntry json.
        pluginEntry.add("media", mediaEntryJson);
        pluginEntry.add("ads", adsJson);
        pluginEntry.add("properties", propertiesJson);
        pluginEntry.add("extraParams", extraParamJson);
        return pluginEntry;
    }
    
    private void addPhoenixAnalyticsPluginConfig(PKPluginConfigs config) {
        String ks = "djJ8MTk4fHFftqeAPxdlLVzZBk0Et03Vb8on1wLsKp7cbOwzNwfOvpgmOGnEI_KZDhRWTS-76jEY7pDONjKTvbWyIJb5RsP4NL4Ng5xuw6L__BeMfLGAktkVliaGNZq9SXF5n2cMYX-sqsXLSmWXF9XN89io7-k=";
        PhoenixAnalyticsConfig phoenixAnalyticsConfig = new PhoenixAnalyticsConfig(198, "http://api-preprod.ott.kaltura.com/v4_2/api_v3/", ks, 30);
        config.setPluginConfig(PhoenixAnalyticsPlugin.factory.getName(), phoenixAnalyticsConfig);
    }

    private void addIMAPluginConfig(PKPluginConfigs config) {
         //"https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dskippablelinear&correlator=";
        //"https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/3274935/preroll&impl=s&gdfp_req=1&env=vp&output=xml_vast2&unviewed_position_start=1&url=[referrer_url]&description_url=[description_url]&correlator=[timestamp]";
        //"https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/ad_rule_samples&ciu_szs=300x250&ad_rule=1&impl=s&gdfp_req=1&env=vp&output=vmap&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ar%3Dpremidpostpod&cmsid=496&vid=short_onecue&correlator=";

        log.d("Play Ad preSKipAdTagUrl");
        IMAConfig adsConfig = getAdsConfig(preMidPostSingleAdTagUrl);
        config.setPluginConfig(IMAPlugin.factory.getName(), adsConfig);
    }

    private IMAConfig getAdsConfig(String adTagUrl) {
        List<String> videoMimeTypes = new ArrayList<>();
        videoMimeTypes.add("video/mp4");
        videoMimeTypes.add("application/x-mpegURL");
        // videoMimeTypes.add("application/dash+xml");
        //Map<Double, String> tagTimesMap = new HashMap<>();
        //tagTimesMap.put(2.0,"ADTAG");
        return new IMAConfig().setAdTagURL(adTagUrl).setVideoMimeTypes(videoMimeTypes).enableDebugMode(true).setAlwaysStartWithPreroll(true).setAdLoadTimeOut(8);
    }


    //IMA CONFIG
//    private void addIMADAIPluginConfig(PKPluginConfigs config) {
//        String adTagUrl = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/ad_rule_samples&ciu_szs=300x250&ad_rule=1&impl=s&gdfp_req=1&env=vp&output=vmap&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ar%3Dpremidpostpodbumper&cmsid=496&vid=short_onecue&correlator=";
//        //"https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dskippablelinear&correlator=";
//        //"https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/3274935/preroll&impl=s&gdfp_req=1&env=vp&output=xml_vast2&unviewed_position_start=1&url=[referrer_url]&description_url=[description_url]&correlator=[timestamp]";
//        //"https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/ad_rule_samples&ciu_szs=300x250&ad_rule=1&impl=s&gdfp_req=1&env=vp&output=vmap&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ar%3Dpremidpostpod&cmsid=496&vid=short_onecue&correlator=";
//        List<String> videoMimeTypes = new ArrayList<>();
//        videoMimeTypes.add("video/mp4");
//        videoMimeTypes.add("application/x-mpegURL");
//        // videoMimeTypes.add("application/dash+xml");
//        //Map<Double, String> tagTimesMap = new HashMap<>();
//        //tagTimesMap.put(2.0,"ADTAG");
//
//
//        String assetTitle = "VOD - Tears of Steel";
//        String assetKey = null;
//        String apiKey = null;
//        String contentSourceId = "19463";
//        String videoId = "tears-of-steel";
//        StreamRequest.StreamFormat streamFormat = StreamRequest.StreamFormat.HLS;
//        String licenseUrl = null;
//
//        IMADAIConfig adsConfig = new IMADAIConfig(assetTitle,
//                assetKey, // null for VOD
//                contentSourceId, // null for Live
//                apiKey, // seems to be always null in demos
//                videoId, // null for Live
//                streamFormat,
//                licenseUrl).enableDebugMode(true);
//
//
//        String assetTitle1 = "Live Video - Big Buck Bunny";
//        String assetKey1 = "sN_IYUG8STe1ZzhIIE_ksA";
//        String apiKey1 = null;
//        String contentSourceId1 = null;
//        String videoId1 = null;
//        StreamRequest.StreamFormat streamFormat1 = StreamRequest.StreamFormat.HLS;
//        String licenseUrl1 = null;
//        IMADAIConfig adsConfigLive = new IMADAIConfig(assetTitle1,
//                assetKey1, // null for VOD
//                contentSourceId1, // null for Live
//                apiKey1, // seems to be always null in demos
//                videoId1, // null for Live
//                streamFormat1,
//                licenseUrl1).enableDebugMode(true);
//
//
//        String assetTitle2 = "BBB-widevine";
//        String assetKey2 = null;
//        String apiKey2 = null;
//        String contentSourceId2 = "2474148";
//        String videoId2 = "bbb-widevine";
//        StreamRequest.StreamFormat streamFormat2 = StreamRequest.StreamFormat.DASH;
//        String licenseUrl2 = "https://proxy.uat.widevine.com/proxy";
//        IMADAIConfig adsConfigDash = new IMADAIConfig(assetTitle2,
//                assetKey2, // null for VOD
//                contentSourceId2, // null for Live
//                apiKey2, // seems to be always null in demos
//                videoId2, // null for Live
//                streamFormat2,
//                licenseUrl2).enableDebugMode(true);
//
//        String assetTitle3 = "JW1";
//        String assetKey3 = null;
//        String apiKey3 = null;
//        String contentSourceId3 = "2472176";
//        String videoId3 = "2504847";
//        StreamRequest.StreamFormat streamFormat3 = StreamRequest.StreamFormat.HLS;
//        String licenseUrl3 = null;
//        IMADAIConfig adsConfigVod2 = new IMADAIConfig(assetTitle3,
//                assetKey3, // null for VOD
//                contentSourceId3, // null for Live
//                apiKey3, // seems to be always null in demos
//                videoId3, // null for Live
//                streamFormat3,
//                licenseUrl3);
//
//
//        String assetTitle4 = "JW4";
//        String assetKey4 = null;
//        String apiKey4 = null;
//        String contentSourceId4 = "19823";
//        String videoId4 = "ima-test";
//        StreamRequest.StreamFormat streamFormat4 = StreamRequest.StreamFormat.HLS;
//        String licenseUrl4 = null;
//        IMADAIConfig adsConfigVod4 = new IMADAIConfig(assetTitle4,
//                assetKey4, // null for VOD
//                contentSourceId4, // null for Live
//                apiKey4, // seems to be always null in demos
//                videoId4, // null for Live
//                streamFormat4,
//                licenseUrl4).enableDebugMode(true);
//
//
//        IMADAIConfig adsConfigError = new IMADAIConfig(assetTitle4,
//                assetKey4, // null for VOD
//                contentSourceId4 + "AAAA", // null for Live
//                apiKey4, // seems to be always null in demos
//                videoId4, // null for Live
//                streamFormat4,
//                licenseUrl4).enableDebugMode(true);
//
//        config.setPluginConfig(IMADAIPlugin.factory.getName(), adsConfig);
//    }

    @Override
    protected void onPause() {
        super.onPause();

        if (controlsView != null) {
            controlsView.release();
        }
        if (player != null) {
            player.onApplicationPaused();
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

    private void addPlayerListeners(final ProgressBar appProgressBar) {


        player.addListener(this, AdEvent.contentResumeRequested, event -> {
            log.d("CONTENT_RESUME_REQUESTED");
            appProgressBar.setVisibility(View.INVISIBLE);
            controlsView.setPlayerState(PlayerState.READY);
        });


//
//        player.addEventListener(new PKEvent.Listener() {
//            @Override
//            public void onEvent(PKEvent event) {
//                log.d("DAI_SOURCE_SELECTED");
//                AdEvent.AdDAISourceSelected adDAISourceSelected = (AdEvent.AdDAISourceSelected) event;
//                player.prepare(adDAISourceSelected.mediaConfig);
//                player.play();
//            }
//        }, AdEvent.Type.DAI_SOURCE_SELECTED);


        player.addListener(this, AdEvent.contentPauseRequested, event -> {
            log.d("AD_CONTENT_PAUSE_REQUESTED");
            appProgressBar.setVisibility(View.VISIBLE);
            controlsView.setPlayerState(PlayerState.READY);
        });

        player.addListener(this, AdEvent.adPlaybackInfoUpdated, event -> {
            log.d("AD_PLAYBACK_INFO_UPDATED");
            AdEvent.AdPlaybackInfoUpdated playbackInfoUpdated = event;
            log.d("XXX playbackInfoUpdated  = " + playbackInfoUpdated.width + "/" + playbackInfoUpdated.height + "/" + playbackInfoUpdated.bitrate);
        });

        player.addListener(this, AdEvent.cuepointsChanged, event -> {
            AdEvent.AdCuePointsUpdateEvent cuePointsList = event;
            adCuePoints = cuePointsList.cuePoints;
            if (adCuePoints != null) {
                log.d("Has Postroll = " + adCuePoints.hasPostRoll());
            }
        });

        player.addListener(this, AdEvent.adBufferStart, event -> {
            AdEvent.AdBufferStart adBufferStartEvent = event;
            log.d("AD_BUFFER_START pos = " + adBufferStartEvent.adPosition);
            appProgressBar.setVisibility(View.VISIBLE);
        });

        player.addListener(this, AdEvent.adBufferEnd, event -> {
            AdEvent.AdBufferEnd adBufferEnd = event;
            log.d("AD_BUFFER_END pos = " + adBufferEnd.adPosition);
            appProgressBar.setVisibility(View.INVISIBLE);
        });

        player.addListener(this, AdEvent.adFirstPlay, event -> {
            log.d("AD_FIRST_PLAY");
            appProgressBar.setVisibility(View.INVISIBLE);
        });

        player.addListener(this, AdEvent.started, event -> {
            log.d("AD_STARTED");
            AdEvent.AdStartedEvent adStartedEvent = event;
            log.d("AD_STARTED w/h - " + adStartedEvent.adInfo.getAdWidth() + "/" + adStartedEvent.adInfo.getAdHeight());
            appProgressBar.setVisibility(View.INVISIBLE);
        });

        player.addListener(this, AdEvent.resumed, event -> {
            log.d("Ad Event AD_RESUMED");
            nowPlaying = true;
            appProgressBar.setVisibility(View.INVISIBLE);
        });

        player.addListener(this, AdEvent.playHeadChanged, event -> {
            appProgressBar.setVisibility(View.INVISIBLE);
            AdEvent.AdPlayHeadEvent adEventProress = event;
            //log.d("received AD PLAY_HEAD_CHANGED " + adEventProress.adPlayHead);
        });

        player.addListener(this, AdEvent.allAdsCompleted, event -> {
            log.d("Ad Event AD_ALL_ADS_COMPLETED");
            appProgressBar.setVisibility(View.INVISIBLE);
            if (adCuePoints != null && adCuePoints.hasPostRoll()) {
                controlsView.setPlayerState(PlayerState.IDLE);
            }
        });

        player.addListener(this, AdEvent.error, event -> {
            AdEvent.Error adErrorEvent = event;
            if (adErrorEvent != null && adErrorEvent.error != null) {
                log.e("ERROR: " + adErrorEvent.error.errorType + ", " + adErrorEvent.error.message);
            }
        });

        player.addListener(this, AdEvent.skipped, event -> {
            log.d("Ad Event SKIPPED");
            nowPlaying = true;
        });

        /////// PLAYER EVENTS

        player.addListener(this, PlayerEvent.play, event -> {
            log.d("Player Event PLAY");
            nowPlaying = true;
        });

        player.addListener(this, PlayerEvent.playing, event -> {
            log.d("Player Event PLAYING");
            nowPlaying = true;
        });

        player.addListener(this, PlayerEvent.pause, event -> {
            log.d("Player Event PAUSE");
            nowPlaying = false;
        });

        player.addListener(this, PlayerEvent.playbackRateChanged, event -> {
            PlayerEvent.PlaybackRateChanged playbackRateChanged = event;
            log.d("playbackRateChanged event  rate = " + playbackRateChanged.rate);
        });

        player.addListener(this, PlayerEvent.tracksAvailable, event -> {
            //When the track data available, this event occurs. It brings the info object with it.
            PlayerEvent.TracksAvailable tracksAvailable = event;
            tracksInfo = tracksAvailable.tracksInfo;
            populateSpinnersWithTrackInfo(tracksAvailable.tracksInfo);
        });

        player.addListener(this, PlayerEvent.playbackRateChanged, event -> {
            PlayerEvent.PlaybackRateChanged playbackRateChanged = event;
            log.d("playbackRateChanged event  rate = " + playbackRateChanged.rate);
        });

        player.addListener(this, PlayerEvent.error, event -> {
            //When the track data available, this event occurs. It brings the info object with it.
            PlayerEvent.Error playerError = event;
            if (playerError != null && playerError.error != null) {
                log.d("PlayerEvent.Error event  position = " + playerError.error.errorType + " errorMessage = " + playerError.error.message);
            }
        });

        player.addListener(this, PlayerEvent.playheadUpdated, event -> {
            //When the track data available, this event occurs. It brings the info object with it.
            PlayerEvent.PlayheadUpdated playheadUpdated = event;
            //log.d("playheadUpdated event  position = " + playheadUpdated.position + " duration = " + playheadUpdated.duration);
        });

        player.addListener(this, PlayerEvent.videoFramesDropped, event -> {
            PlayerEvent.VideoFramesDropped videoFramesDropped = event;
            //log.d("VIDEO_FRAMES_DROPPED " + videoFramesDropped.droppedVideoFrames);
        });

        player.addListener(this, PlayerEvent.bytesLoaded, event -> {
            PlayerEvent.BytesLoaded bytesLoaded = event;
            //log.d("BYTES_LOADED " + bytesLoaded.bytesLoaded);
        });

        player.addListener(this, PlayerEvent.stateChanged, new PKEvent.Listener<PlayerEvent.StateChanged>() {
            @Override
            public void onEvent(PlayerEvent.StateChanged event) {
                PlayerEvent.StateChanged stateChanged = event;
                log.d("State changed from " + stateChanged.oldState + " to " + stateChanged.newState);
                if(controlsView != null){
                    controlsView.setPlayerState(stateChanged.newState);
                }
            }
        });

        /////Phoenox events

        player.addListener(this, PhoenixAnalyticsEvent.bookmarkError, event -> {
            PhoenixAnalyticsEvent.BookmarkErrorEvent bookmarkErrorEvent = event;
            log.d("bookmarkErrorEvent errorCode = " + bookmarkErrorEvent.errorCode + " message = " + bookmarkErrorEvent.errorMessage);
        });

        player.addListener(this, PhoenixAnalyticsEvent.concurrencyError, event -> {
            PhoenixAnalyticsEvent.ConcurrencyErrorEvent concurrencyErrorEvent = event;
            log.d("ConcurrencyErrorEvent errorCode = " + concurrencyErrorEvent.errorCode + " message = " + concurrencyErrorEvent.errorMessage);

        });

        player.addListener(this, PhoenixAnalyticsEvent.error, event -> {
            PhoenixAnalyticsEvent.ErrorEvent errorEvent = event;
            log.d("Phoenox Analytics errorEvent errorCode = " + errorEvent.errorCode + " message = " + errorEvent.errorMessage);
        });

        player.addListener(this, PhoenixAnalyticsEvent.error, event -> {
            PhoenixAnalyticsEvent.ErrorEvent errorEvent = event;
            log.d("Phoenox Analytics errorEvent errorCode = " + errorEvent.errorCode + " message = " + errorEvent.errorMessage);
        });

        player.addListener(this, OttEvent.ottEvent, event -> {
            OttEvent concEvent = (OttEvent) event;
            log.d("Concurrency event = " + concEvent.type);
        });
    }

    @Override
    protected void onResume() {
        log.d("Ad Event onResume");
        super.onResume();
        if (player != null) {
            player.onApplicationResumed();
        }
        if (controlsView != null) {
            controlsView.resume();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        setFullScreen(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE);
        super.onConfigurationChanged(newConfig);
        Log.v("orientation", "state = "+newConfig.orientation);
    }


    private void setFullScreen(boolean isFullScreen) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)playerContainer.getLayoutParams();
        // Checks the orientation of the screen
        this.isFullScreen = isFullScreen;
        if (isFullScreen) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            fullScreenBtn.setImageResource(R.drawable.ic_no_fullscreen);
            spinerContainer.setVisibility(View.GONE);
            params.height = RelativeLayout.LayoutParams.MATCH_PARENT;
            params.width = RelativeLayout.LayoutParams.MATCH_PARENT;

        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            fullScreenBtn.setImageResource(R.drawable.ic_fullscreen);
            spinerContainer.setVisibility(View.VISIBLE);
            params.height = (int)getResources().getDimension(R.dimen.player_height);
            params.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        }
        playerContainer.requestLayout();
    }

    /**
     * populating spinners with track info.
     *
     * @param tracksInfo - the track info.
     */
    private void populateSpinnersWithTrackInfo(PKTracks tracksInfo) {

        //Retrieve info that describes available tracks.(video/audio/subtitle).
        TrackItem[] videoTrackItems = obtainRelevantTrackInfo(Consts.TRACK_TYPE_VIDEO, tracksInfo.getVideoTracks());
        //populate spinner with this info.

        applyAdapterOnSpinner(videoSpinner, videoTrackItems, tracksInfo.getDefaultVideoTrackIndex());

        TrackItem[] audioTrackItems = obtainRelevantTrackInfo(Consts.TRACK_TYPE_AUDIO, tracksInfo.getAudioTracks());
        applyAdapterOnSpinner(audioSpinner, audioTrackItems, tracksInfo.getDefaultAudioTrackIndex());

        TrackItem[] subtitlesTrackItems = obtainRelevantTrackInfo(Consts.TRACK_TYPE_TEXT, tracksInfo.getTextTracks());
        applyAdapterOnSpinner(textSpinner, subtitlesTrackItems, tracksInfo.getDefaultTextTrackIndex());
    }

    /**
     * Obtain info that user is interested in.
     * For example if user want to display in UI bitrate of the available tracks,
     * he can do it, by obtaining the tackType of video, and getting the getBitrate() from videoTrackInfo.
     *
     * @param trackType  - tyoe of the track you are interested in.
     * @param trackInfos - all availables tracks.
     * @return
     */
    private TrackItem[] obtainRelevantTrackInfo(int trackType, List<? extends BaseTrack> trackInfos) {
        TrackItem[] trackItems = new TrackItem[trackInfos.size()];
        switch (trackType) {
            case Consts.TRACK_TYPE_VIDEO:
                TextView tvVideo = (TextView) this.findViewById(R.id.tvVideo);
                changeSpinnerVisibility(videoSpinner, tvVideo, trackInfos);

                for (int i = 0; i < trackInfos.size(); i++) {
                    VideoTrack videoTrackInfo = (VideoTrack) trackInfos.get(i);
                    if(videoTrackInfo.isAdaptive()){
                        trackItems[i] = new TrackItem("Auto", videoTrackInfo.getUniqueId());
                    }else{
                        trackItems[i] = new TrackItem(String.valueOf(videoTrackInfo.getBitrate()), videoTrackInfo.getUniqueId());
                    }
                }

                break;
            case Consts.TRACK_TYPE_AUDIO:
                TextView tvAudio = (TextView) this.findViewById(R.id.tvAudio);
                changeSpinnerVisibility(audioSpinner, tvAudio, trackInfos);
                Map<Integer, AtomicInteger> channelMap = new HashMap<>();
                for (int i = 0; i < trackInfos.size(); i++) {
                    if (channelMap.containsKey(((AudioTrack) trackInfos.get(i)).getChannelCount())) {
                        channelMap.get(((AudioTrack) trackInfos.get(i)).getChannelCount()).incrementAndGet();
                    } else {
                        channelMap.put(((AudioTrack) trackInfos.get(i)).getChannelCount(), new AtomicInteger(1));
                    }
                }
                boolean addChannel = false;
                if (channelMap.keySet().size() > 0 && !(new AtomicInteger(trackInfos.size()).toString().equals(channelMap.get(((AudioTrack) trackInfos.get(0)).getChannelCount()).toString()))) {
                    addChannel = true;
                }
                for (int i = 0; i < trackInfos.size(); i++) {
                    AudioTrack audioTrackInfo = (AudioTrack) trackInfos.get(i);
                        String label = audioTrackInfo.getLabel() != null ? audioTrackInfo.getLabel() : audioTrackInfo.getLanguage();
                        String bitrate = (audioTrackInfo.getBitrate() >  0) ? "" + audioTrackInfo.getBitrate() : "";
                        if (TextUtils.isEmpty(bitrate) && addChannel) {
                           bitrate = buildAudioChannelString(audioTrackInfo.getChannelCount());
                        }
                        if (audioTrackInfo.isAdaptive()) {
                            if (bitrate != null) {
                                bitrate += " Adaptive";
                            } else {
                                bitrate = "Adaptive";
                            }
                            if (label == null) {
                                label = "";
                            }
                        }
                        trackItems[i] = new TrackItem(label + " " + bitrate, audioTrackInfo.getUniqueId());
                }
                break;
            case Consts.TRACK_TYPE_TEXT:
                TextView tvSubtitle = (TextView) this.findViewById(R.id.tvText);
                changeSpinnerVisibility(textSpinner, tvSubtitle, trackInfos);

                for (int i = 0; i < trackInfos.size(); i++) {

                    TextTrack textTrackInfo = (TextTrack) trackInfos.get(i);
                    String lang = (textTrackInfo.getLabel() != null) ? textTrackInfo.getLabel() : "unknown";
                    trackItems[i] = new TrackItem(lang, textTrackInfo.getUniqueId());
                }
                break;
        }
        return trackItems;
    }

    private void changeSpinnerVisibility(Spinner spinner, TextView textView, List<? extends BaseTrack> trackInfos) {
        //hide spinner if no data available.
        if (trackInfos.isEmpty()) {
            textView.setVisibility(View.GONE);
            spinner.setVisibility(View.GONE);
        } else {
            textView.setVisibility(View.VISIBLE);
            spinner.setVisibility(View.VISIBLE);
        }
    }

    private void applyAdapterOnSpinner(Spinner spinner, TrackItem[] trackInfo, int defaultSelectedIndex) {
        TrackItemAdapter trackItemAdapter = new TrackItemAdapter(this, R.layout.track_items_list_row, trackInfo);
        spinner.setAdapter(trackItemAdapter);
        if (defaultSelectedIndex > 0) {
            spinner.setSelection(defaultSelectedIndex);
        }
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        userIsInteracting = true;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (!userIsInteracting) {
            return;
        }
        TrackItem trackItem = (TrackItem) parent.getItemAtPosition(position);
        //tell to the player, to switch track based on the user selection.

        player.changeTrack(trackItem.getUniqueId());

        //String selectedIndex = getQualityIndex(BitRateRange.QualityType.Auto, currentTracks.getVideoTracks());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onOrientationChange(OrientationManager.ScreenOrientation screenOrientation) {
        switch(screenOrientation){
            case PORTRAIT:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
            case REVERSED_PORTRAIT:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                break;
            case LANDSCAPE:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                break;
            case REVERSED_LANDSCAPE:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                break;
            default:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
        }
    }

    protected String getQualityIndex(BitRateRange.QualityType videoQuality, List<VideoTrack> videoTrackInfo) {
        String uniqueTrackId = null;
        long bitRateValue = 0;
        BitRateRange bitRateRange = null;

        switch (videoQuality) {
            case Low:
                bitRateRange = BitRateRange.getLowQuality();
                List<VideoTrack> lowBitrateMatchedTracks = getVideoTracksInRange(videoTrackInfo, bitRateRange);
                Collections.sort(lowBitrateMatchedTracks, bitratesComperator());

                for (VideoTrack track : lowBitrateMatchedTracks) {
                    bitRateValue = track.getBitrate();
                    if (isBitrateInRange(bitRateValue, bitRateRange.getLow(), bitRateRange.getHigh())) {
                        uniqueTrackId = track.getUniqueId();
                        break;
                    }
                }
                break;
            case Mediun:
                bitRateRange = BitRateRange.getMedQuality();
                List<VideoTrack> medBitratesMatchedTracks = getVideoTracksInRange(videoTrackInfo, bitRateRange);
                Collections.sort(medBitratesMatchedTracks, bitratesComperator());

                for (VideoTrack track : medBitratesMatchedTracks) {
                    bitRateValue = track.getBitrate();
                    if (isBitrateInRange(bitRateValue, bitRateRange.getLow(), bitRateRange.getHigh())) {
                        uniqueTrackId = track.getUniqueId();
                        break;
                    }
                }
                break;
            case High:
                bitRateRange = BitRateRange.getHighQuality();
                Collections.sort(videoTrackInfo, bitratesComperator());
                for (BaseTrack entry : videoTrackInfo) {
                    bitRateValue = ((VideoTrack) entry).getBitrate();
                    if (bitRateValue >= bitRateRange.getLow()) {
                        uniqueTrackId = entry.getUniqueId();
                        break;
                    }
                }
                break;
            case Auto:
            default:
                for (VideoTrack track : videoTrackInfo) {
                    if (track.isAdaptive()) {
                        uniqueTrackId = track.getUniqueId();
                        break;
                    }
                }
                break;
        }

        //null protection
        if (uniqueTrackId == null && tracksInfo != null) {
            tracksInfo.getDefaultVideoTrackIndex();
        }
        return uniqueTrackId;
    }

    private static List<VideoTrack> getVideoTracksInRange(List<VideoTrack> videoTracks, BitRateRange bitRateRange) {
        List<VideoTrack> videoTrackInfo = new ArrayList<>() ;
        long bitRate;
        for (VideoTrack track : videoTracks) {
            bitRate = track.getBitrate();
            if (bitRate >= bitRateRange.getLow() && bitRate <= bitRateRange.getHigh()) {
                videoTrackInfo.add(track);
            }
        }
        return videoTrackInfo;
    }

    private boolean isBitrateInRange(long bitRate, long low, long high) {
        return low <= bitRate && bitRate <= high;
    }

    @NonNull
    private Comparator<VideoTrack> bitratesComperator() {
        return new Comparator<VideoTrack>() {
            @Override
            public int compare(VideoTrack track1, VideoTrack track2) {
                return Long.valueOf(track1.getBitrate()).compareTo(track2.getBitrate());
            }
        };
    }

    private String buildAudioChannelString(int channelCount) {
        switch (channelCount) {
            case 1:
                return "Mono";
            case 2:
                return "Stereo";
            case 6:
            case 7:
                return "Surround_5.1";
            case 8:
                return "Surround_7.1";
            default:
                return "Surround";
        }
    }

}
