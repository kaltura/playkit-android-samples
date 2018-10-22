package com.kaltura.playkitdemo;

import android.Manifest;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ads.interactivemedia.v3.api.StreamRequest;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.NativeCustomTemplateAd;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
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
import com.kaltura.playkit.PKMediaFormat;
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
import com.kaltura.playkit.plugins.ott.PhoenixAnalyticsConfig;
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
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static com.kaltura.playkit.utils.Consts.DISTANCE_FROM_LIVE_THRESHOLD;
import static com.kaltura.playkitdemo.MockParams.Format;
import static com.kaltura.playkitdemo.MockParams.Format2;
import static com.kaltura.playkitdemo.MockParams.Format_HD_Dash;
import static com.kaltura.playkitdemo.MockParams.Format_SD_Dash;
import static com.kaltura.playkitdemo.MockParams.OvpUserKS;
import static com.kaltura.playkitdemo.MockParams.PnxKS;
import static com.kaltura.playkitdemo.MockParams.SingMediaId;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,
        OrientationManager.OrientationListener {

    private static final String DFP_AD_UNIT_ID = "/6499/example/native";
    private static final String SIMPLE_TEMPLATE_ID = "10104090";

    public static final boolean AUTO_PLAY_ON_RESUME = true;

    private static final PKLog log = PKLog.get("MainActivity");
    public static final Long START_POSITION = 0L;

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

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        registerPlugins();

        OnMediaLoadCompletion playLoadedEntry = new OnMediaLoadCompletion() {
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
        
        startMockMediaLoading(playLoadedEntry);
//      startOvpMediaLoading(playLoadedEntry);
//      startOttMediaLoading(playLoadedEntry);
//        startSimpleOvpMediaLoading(playLoadedEntry);
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

        Button refresh = findViewById(R.id.btn_refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshAd(true,
                        true);
            }
        });
    }

    private void startVootOttMediaLoadingProd(final OnMediaLoadCompletion completion) {
        SessionProvider ksSessionProvider = new SessionProvider() {
            @Override
            public String baseUrl() {
                String PhoenixBaseUrl = "https://rest-as.ott.kaltura.com/v4_4/api_v3/";//"https://rest-sgs1.ott.kaltura.com/restful_V4_4/api_v3/";//"https://rest-as.ott.kaltura.com/v4_4/api_v3/";
                return PhoenixBaseUrl;
            }

            @Override
            public void getSessionToken(OnCompletion<PrimitiveResult> completion) {
                String ks1 = "djJ8MjI1fI9tjvRYGjEgsFMKzLjP6DfsVRp3lLk7HOuwIx9FjvigREzqdh1wTjdRKj0h7JdRSuAKT1TprH_FJnehrk50OMp3t7efc70L8pTYz0miqor_2ilWbBYjXCCpMHUd7stKCRdZVg1YM72av7PJnCoBjQUVlgCvwSZr7pLKzciYacwGF2Fw7JRhSgypqsDY2hs9WQ==";
                String PnxKS = "";
                if (completion != null) {
                    completion.onComplete(new PrimitiveResult(PnxKS));
                }
            }

            @Override
            public int partnerId() {
                int OttPartnerId = 225;
                return OttPartnerId;
            }
        };

        String mediaId = "603038";//"311078";//"561107";
        String format  = "dash Main";//"Tablet Main";//

//                "Format": "dash Mobile",
//                "Format": "ism Main",
//                "Format": "Tablet Main",
//                "Format": "dash Main",
//                "Format": "widevine_sbr Download High",
//                "Format": "widevine_mbr Main",
//                "Format": "TV Main",
//                "Format": "Web New",
//                "Format": "360_Main",
//                "Format": "HLS_Mobile_SD",
//                "Format": "HLS_Mobile_HD",
//                "Format": "HLSFPS_Mobile_SD",
//                "Format": "HLSFPS_Mobile_HD",
//                "Format": "HLS_Web_SD",
//                "Format": "HLS_Web_HD",
//                "Format": "HLS_360_SD",
//                "Format": "HLS_360_HD",
//                "Format": "HLS_TV_SD",
//                "Format": "HLS_TV_HD",
//                "Format": "HLSFPS_Main",
//                "Format": "WVC_Low",
//                "Format": "WVC_Auto",
//                "Format": "DASH_Mobile_SD",
//                "Format": "DASH_Mobile_HD",
//                "Format": "WVC_High",
//                "Format": "JIO_MAIN",
//                "Format": "SBR256",
//                "Format": "HLS_Linear_P",
//                "Format": "HLS_Linear_B",



        mediaProvider = new PhoenixMediaProvider()
                .setSessionProvider(ksSessionProvider)
                .setAssetId(mediaId)
                //.setReferrer()
                .setProtocol(PhoenixMediaProvider.HttpProtocol.Https)
                .setContextType(APIDefines.PlaybackContextType.Playback)
                .setAssetType(APIDefines.KalturaAssetType.Media)
                .setFormats(format);

        mediaProvider.load(completion);
    }

    private void startVootOttMediaLoadingStg(final OnMediaLoadCompletion completion) {
        SessionProvider ksSessionProvider = new SessionProvider() {
            @Override
            public String baseUrl() {
                String PhoenixBaseUrl = "https://rest-sgs1.ott.kaltura.com/restful_V4_4/api_v3/";
                return PhoenixBaseUrl;
            }

            @Override
            public void getSessionToken(OnCompletion<PrimitiveResult> completion) {
                String ks1 = "djJ8MjI1fI9tjvRYGjEgsFMKzLjP6DfsVRp3lLk7HOuwIx9FjvigREzqdh1wTjdRKj0h7JdRSuAKT1TprH_FJnehrk50OMp3t7efc70L8pTYz0miqor_2ilWbBYjXCCpMHUd7stKCRdZVg1YM72av7PJnCoBjQUVlgCvwSZr7pLKzciYacwGF2Fw7JRhSgypqsDY2hs9WQ==";
                String PnxKS = "";
                if (completion != null) {
                    completion.onComplete(new PrimitiveResult(PnxKS));
                }
            }

            @Override
            public int partnerId() {
                int OttPartnerId = 225;
                return OttPartnerId;
            }
        };

        String mediaId = "315369";//"311078";//"561107";
        String format  = "HLS_Linear_P";//"Tablet Main";//

//                "Format": "dash Mobile",
//                "Format": "ism Main",
//                "Format": "Tablet Main",
//                "Format": "dash Main",
//                "Format": "widevine_sbr Download High",
//                "Format": "widevine_mbr Main",
//                "Format": "TV Main",
//                "Format": "Web New",
//                "Format": "360_Main",
//                "Format": "HLS_Mobile_SD",
//                "Format": "HLS_Mobile_HD",
//                "Format": "HLSFPS_Mobile_SD",
//                "Format": "HLSFPS_Mobile_HD",
//                "Format": "HLS_Web_SD",
//                "Format": "HLS_Web_HD",
//                "Format": "HLS_360_SD",
//                "Format": "HLS_360_HD",
//                "Format": "HLS_TV_SD",
//                "Format": "HLS_TV_HD",
//                "Format": "HLSFPS_Main",
//                "Format": "WVC_Low",
//                "Format": "WVC_Auto",
//                "Format": "DASH_Mobile_SD",
//                "Format": "DASH_Mobile_HD",
//                "Format": "WVC_High",
//                "Format": "JIO_MAIN",
//                "Format": "SBR256",
//                "Format": "HLS_Linear_P",
//                "Format": "HLS_Linear_B",



        mediaProvider = new PhoenixMediaProvider()
                .setSessionProvider(ksSessionProvider)
                .setAssetId(mediaId)
                //.setReferrer()
                .setProtocol(PhoenixMediaProvider.HttpProtocol.Https)
                .setContextType(APIDefines.PlaybackContextType.Playback)
                .setAssetType(APIDefines.KalturaAssetType.Media)
                .setFormats(format);

        mediaProvider.load(completion);
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
                .setSessionProvider(new SimpleSessionProvider("https://cdnapisec.kaltura.com", 243342, null))
                .setEntryId("0_uka1msg4")
                .load(completion);
    }

    private void startSimpleOvpMediaLoading(OnMediaLoadCompletion completion) {
        new KalturaOvpMediaProvider()
                .setSessionProvider(new SimpleSessionProvider("https://cdnapisec.kaltura.com", 2222401, null))
                .setEntryId("1_f93tepsn")//("1_asoyc5ef") //("1_uzea2uje")
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
                .setAssetId(SingMediaId) //bunny no horses id = "485380"
                .setAssetType(APIDefines.KalturaAssetType.Media)
                .setFormats(Format_SD_Dash, Format_HD_Dash, Format, Format2);

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

        PKMediaConfig mediaConfig = new PKMediaConfig().setMediaEntry(mediaEntry).setStartPosition(START_POSITION);
        PKPluginConfigs pluginConfig = new PKPluginConfigs();
        if (player == null) {

            configurePlugins(pluginConfig);

            player = PlayKitManager.loadPlayer(this, pluginConfig);
            KalturaPlaybackRequestAdapter.install(player, "myApp"); // in case app developer wants to give customized referrer instead the default referrer in the playmanifest
            KalturaUDRMLicenseRequestAdapter.install(player, "myApp");

            player.getSettings().setSecureSurface(false);
            player.getSettings().setAdAutoPlayOnResume(true);
           // player.getSettings().setPreferredMediaFormat(PKMediaFormat.hls);

            //player.setPlaybackRate(1.5f);
            log.d("Player: " + player.getClass());
            addPlayerListeners(progressBar);

            FrameLayout layout = (FrameLayout) findViewById(R.id.player_root);
            layout.addView(player.getView());

            controlsView = (PlaybackControlsView) this.findViewById(R.id.playerControls);
            controlsView.setPlayer(player);
            initSpinners();
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
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("delay", 1200);
        pluginConfigs.setPluginConfig("Sample", jsonObject);
        addIMAPluginConfig(pluginConfigs);
        //addIMADAIPluginConfig(pluginConfigs);
        addKaluraStatsPluginConfig(pluginConfigs);

        addYouboraPluginConfig(pluginConfigs);
        addKavaPluginConfig(pluginConfigs);
        //addPhoenixAnalyticsPluginConfig(pluginConfigs);
        //addTVPAPIAnalyticsPluginConfig(pluginConfigs);

        //pluginConfigs.setPluginConfig("IMASimplePlugin", jsonObject);
        //pluginConfigs.setPluginConfig("KalturaStatistics", jsonObject);
        //pluginConfigs.setPluginConfig("PhoenixAnalytics", jsonObject);
        //pluginConfigs.setPluginConfig("Youbora", jsonObject);

    }

    private void addKaluraStatsPluginConfig(PKPluginConfigs pluginConfigs) {
        String KALTURA_STATS_URL = "https://stats.kaltura.com/api_v3/index.php";
        KalturaStatsConfig kalturaStatsConfig = new KalturaStatsConfig(true)
                .setBaseUrl(KALTURA_STATS_URL)
                .setPartnerId(2222401)
                .setEntryId("1_f93tepsn")
                .setTimerInterval(30);
        //Set plugin entry to the plugin configs.
        pluginConfigs.setPluginConfig(KalturaStatsPlugin.factory.getName(), kalturaStatsConfig);
    }


    private void addKavaPluginConfig(PKPluginConfigs pluginConfigs) {
        KavaAnalyticsConfig kavaAnalyticsConfig = new KavaAnalyticsConfig()
                .setApplicationVersion(BuildConfig.VERSION_NAME)
                .setPartnerId(Integer.valueOf(2222401))
                .setEntryId("1_f93tepsn")
                .setDvrThreshold(DISTANCE_FROM_LIVE_THRESHOLD);
        //Set plugin entry to the plugin configs.
        pluginConfigs.setPluginConfig(KavaAnalyticsPlugin.factory.getName(), kavaAnalyticsConfig);
    }

    private void addYouboraPluginConfig(PKPluginConfigs pluginConfigs) {
        JsonObject pluginEntry = new JsonObject();

        //Youbora config json. Main config goes here.
        JsonObject youboraConfigJson = new JsonObject();
        youboraConfigJson.addProperty("accountCode", "KALTURA_TEST");
        youboraConfigJson.addProperty("username", "a@a.com");
        youboraConfigJson.addProperty("haltOnError", true);
        youboraConfigJson.addProperty("enableAnalytics", true);
        youboraConfigJson.addProperty("enableSmartAds", true);


        //Media entry json.
        JsonObject mediaEntryJson = new JsonObject();
        mediaEntryJson.addProperty("isLive", false);
        mediaEntryJson.addProperty("title", "the media title");

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

        //Set plugin entry to the plugin configs.
        pluginConfigs.setPluginConfig(YouboraPlugin.factory.getName(), pluginEntry);
    }

//    private void addPhoenixAnalyticsPluginConfig(PKPluginConfigs config) {
//        String ks = "djJ8MTk4fHFftqeAPxdlLVzZBk0Et03Vb8on1wLsKp7cbOwzNwfOvpgmOGnEI_KZDhRWTS-76jEY7pDONjKTvbWyIJb5RsP4NL4Ng5xuw6L__BeMfLGAktkVliaGNZq9SXF5n2cMYX-sqsXLSmWXF9XN89io7-k=";
//        PhoenixAnalyticsConfig phoenixAnalyticsConfig = new PhoenixAnalyticsConfig(198, "http://api-preprod.ott.kaltura.com/v4_2/api_v3/", ks, 30);
//        config.setPluginConfig(PhoenixAnalyticsPlugin.factory.getName(),phoenixAnalyticsConfig);
//    }
//
//    private void addTVPAPIAnalyticsPluginConfig(PKPluginConfigs config) {
//        TVPAPILocale locale = new TVPAPILocale("","","", "");
//        TVPAPIInitObject tvpapiInitObject = new TVPAPIInitObject(716158, "tvpapi_198", 354531, "e8aa934c-eae4-314f-b6a0-f55e96498786", "11111", "Cellular", locale);
//        TVPAPIAnalyticsConfig tvpapiAnalyticsConfig = new TVPAPIAnalyticsConfig("http://tvpapi-preprod.ott.kaltura.com/v4_2/gateways/jsonpostgw.aspx?", 30, tvpapiInitObject);
//        config.setPluginConfig(TVPAPIAnalyticsPlugin.factory.getName(),tvpapiAnalyticsConfig);
//    }


    private void addPhoenixAnalyticsPlugin(PKPluginConfigs config) {

        //Important!!! First you need to register your plugin.
        PlayKitManager.registerPlugins(this, PhoenixAnalyticsPlugin.factory);

        //Phoenix analytics constants
        String PHOENIX_ANALYTICS_BASE_URL = "https://rest-as.ott.kaltura.com/v4_4/api_v3/";
        int PHOENIX_ANALYTICS_PARTNER_ID = 225;
        String PHOENIX_ANALYTICS_KS = "ks";
        int ANALYTIC_TRIGGER_INTERVAL = 30; //Interval in which analytics report should be triggered (in seconds).



        //Set plugin entry to the plugin configs.
        PhoenixAnalyticsConfig phoenixAnalyticsConfig = new PhoenixAnalyticsConfig(PHOENIX_ANALYTICS_PARTNER_ID, PHOENIX_ANALYTICS_BASE_URL, PHOENIX_ANALYTICS_KS, ANALYTIC_TRIGGER_INTERVAL);
        config.setPluginConfig(PhoenixAnalyticsPlugin.factory.getName(), phoenixAnalyticsConfig);
    }

    private void addIMAPluginConfig(PKPluginConfigs config) {
        String preMidPostAdTagUrl = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/ad_rule_samples&ciu_szs=300x250&ad_rule=1&impl=s&gdfp_req=1&env=vp&output=vmap&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ar%3Dpremidpostpodbumper&cmsid=496&vid=short_onecue&correlator=";
        String preSKipAdTagUrl = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dskippablelinear&correlator=";
        //"https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dskippablelinear&correlator=";
        //"https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/3274935/preroll&impl=s&gdfp_req=1&env=vp&output=xml_vast2&unviewed_position_start=1&url=[referrer_url]&description_url=[description_url]&correlator=[timestamp]";
        //"https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/ad_rule_samples&ciu_szs=300x250&ad_rule=1&impl=s&gdfp_req=1&env=vp&output=vmap&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ar%3Dpremidpostpod&cmsid=496&vid=short_onecue&correlator=";
        List<String> videoMimeTypes = new ArrayList<>();
        videoMimeTypes.add("video/mp4");
        videoMimeTypes.add("application/x-mpegURL");
        // videoMimeTypes.add("application/dash+xml");
        //Map<Double, String> tagTimesMap = new HashMap<>();
        //tagTimesMap.put(2.0,"ADTAG");

        IMAConfig adsConfig = new IMAConfig().setAdTagURL(preSKipAdTagUrl).setVideoMimeTypes(videoMimeTypes).enableDebugMode(true).setAdLoadTimeOut(8);
        config.setPluginConfig(IMAPlugin.factory.getName(), adsConfig);
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

    private void addPlayerListeners(final ProgressBar appProgressBar) {

        player.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                log.d("AD_CONTENT_PAUSE_REQUESTED");
                appProgressBar.setVisibility(View.INVISIBLE);
            }
        }, AdEvent.Type.CONTENT_RESUME_REQUESTED);

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

        player.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                log.d("AD_CONTENT_PAUSE_REQUESTED");
                appProgressBar.setVisibility(View.VISIBLE);
                controlsView.setPlayerState(PlayerState.READY);
            }

        }, AdEvent.Type.CONTENT_PAUSE_REQUESTED);
        player.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                AdEvent.AdCuePointsUpdateEvent cuePointsList = (AdEvent.AdCuePointsUpdateEvent) event;
                adCuePoints = cuePointsList.cuePoints;
                if (adCuePoints != null) {
                    log.d("Has Postroll = " + adCuePoints.hasPostRoll());
                }
            }
        }, AdEvent.Type.CUEPOINTS_CHANGED);

        player.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                AdEvent.AdBufferStart adBufferStartEvent = (AdEvent.AdBufferStart) event;
                log.d("AD_BUFFER_START pos = " + adBufferStartEvent.adPosition);
                appProgressBar.setVisibility(View.VISIBLE);
            }
        }, AdEvent.Type.AD_BUFFER_START);

        player.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                AdEvent.AdBufferEnd adBufferEnd = (AdEvent.AdBufferEnd) event;
                log.d("AD_BUFFER_END pos = " + adBufferEnd.adPosition);
                appProgressBar.setVisibility(View.INVISIBLE);
            }
        }, AdEvent.Type.AD_BUFFER_END);

        player.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                log.d("AD_FIRST_PLAY");
                appProgressBar.setVisibility(View.INVISIBLE);
            }
        }, AdEvent.Type.AD_FIRST_PLAY);

        player.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                log.d("AD_STARTED");
                AdEvent.AdStartedEvent adStartedEvent = (AdEvent.AdStartedEvent) event;
                appProgressBar.setVisibility(View.INVISIBLE);
            }
        }, AdEvent.Type.STARTED);

        player.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                log.d("Ad Event AD_RESUMED");
                nowPlaying = true;
                appProgressBar.setVisibility(View.INVISIBLE);
            }
        }, AdEvent.Type.RESUMED);

        player.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                log.d("Ad Event AD_ALL_ADS_COMPLETED");
                appProgressBar.setVisibility(View.INVISIBLE);
                if (adCuePoints != null && adCuePoints.hasPostRoll()) {
                    controlsView.setPlayerState(PlayerState.IDLE);
                }
            }
        }, AdEvent.Type.ALL_ADS_COMPLETED);
        player.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                nowPlaying = true;
            }
        }, PlayerEvent.Type.PLAY);

        player.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                nowPlaying = true;
            }
        }, PlayerEvent.Type.PLAYING);


        player.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                AdEvent.Error adErrorEvent = (AdEvent.Error) event;
                if (adErrorEvent != null && adErrorEvent.error != null) {
                    log.e("ERROR: " + adErrorEvent.error.errorType + ", " + adErrorEvent.error.message);
                }
            }
        }, AdEvent.Type.ERROR);

        player.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                nowPlaying = false;
            }
        }, PlayerEvent.Type.PAUSE);

        player.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                nowPlaying = true;
            }
        }, AdEvent.Type.SKIPPED);

        player.addEventListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                PlayerEvent.PlaybackRateChanged playbackRateChanged = (PlayerEvent.PlaybackRateChanged) event;
                log.d("playbackRateChanged event  rate = " + playbackRateChanged.rate);
            }
        }, PlayerEvent.Type.PLAYBACK_RATE_CHANGED);

        player.addStateChangeListener(new PKEvent.Listener() {
            @Override
            public void onEvent(PKEvent event) {
                if (event instanceof PlayerEvent.StateChanged) {
                    PlayerEvent.StateChanged stateChanged = (PlayerEvent.StateChanged) event;
                    log.d("State changed from " + stateChanged.oldState + " to " + stateChanged.newState);
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
                populateSpinnersWithTrackInfo(tracksAvailable.tracksInfo);

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

                for (int i = 0; i < trackInfos.size(); i++) {
                    AudioTrack audioTrackInfo = (AudioTrack) trackInfos.get(i);
                    if(audioTrackInfo.isAdaptive()){
                        trackItems[i] = new TrackItem("Auto", audioTrackInfo.getUniqueId());
                    }else{
                        String label = audioTrackInfo.getLanguage() != null ? audioTrackInfo.getLanguage() : audioTrackInfo.getLabel();
                        String bitrate = (audioTrackInfo.getBitrate() >  0)? "" + audioTrackInfo.getBitrate() : "";
                        trackItems[i] = new TrackItem(label + " " + bitrate, audioTrackInfo.getUniqueId());
                    }
                }
                break;
            case Consts.TRACK_TYPE_TEXT:
                TextView tvSubtitle = (TextView) this.findViewById(R.id.tvText);
                changeSpinnerVisibility(textSpinner, tvSubtitle, trackInfos);

                for (int i = 0; i < trackInfos.size(); i++) {

                    TextTrack textTrackInfo = (TextTrack) trackInfos.get(i);
                    String lang = (textTrackInfo.getLanguage() != null) ? textTrackInfo.getLanguage() : "unknown";
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



    /**
     * Populates a {@link View} object with data from a {@link NativeCustomTemplateAd}. This method
     * handles a particular "simple" custom native ad format.
     *
     * @param nativeCustomTemplateAd the object containing the ad's assets
     * @param adView                 the view to be populated
     */
    private void populateSimpleTemplateAdView(final NativeCustomTemplateAd nativeCustomTemplateAd,
                                              View adView) {

        FrameLayout mediaPlaceholder = adView.findViewById(R.id.simplecustom_media_placeholder);

        // Get the video controller for the ad. One will always be provided, even if the ad doesn't
        // have a video asset.
        VideoController vc = nativeCustomTemplateAd.getVideoController();

        // Apps can check the VideoController's hasVideoContent property to determine if the
        // NativeCustomTemplateAd has a video asset.
        if (vc.hasVideoContent()) {
            mediaPlaceholder.addView(nativeCustomTemplateAd.getVideoMediaView());

            // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
            // VideoController will call methods on this object when events occur in the video
            // lifecycle.
            vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                public void onVideoEnd() {
                    // Publishers should allow native ads to complete video playback before
                    // refreshing or replacing them with another ad in the same UI location.
                    super.onVideoEnd();
                }
            });
        } else {
            ImageView mainImage = new ImageView(this);
            mainImage.setAdjustViewBounds(true);
            mainImage.setImageDrawable(nativeCustomTemplateAd.getImage("MainImage").getDrawable());

            mainImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    nativeCustomTemplateAd.performClick("MainImage");
                }
            });
            mediaPlaceholder.addView(mainImage);
        }
    }

    /**
     * Creates a request for a new native ad based on the boolean parameters and calls the
     * corresponding "populate" method when one is successfully returned.
     *
     * @param requestUnifiedNativeAds  indicates whether unified native ads should be requested
     * @param requestCustomTemplateAds indicates whether custom template ads should be requested
     */
    private void refreshAd(boolean requestUnifiedNativeAds,
                           boolean requestCustomTemplateAds) {
        if (!requestUnifiedNativeAds && !requestCustomTemplateAds) {
            Toast.makeText(this, "At least one ad format must be checked to request an ad.",
                    Toast.LENGTH_SHORT).show();
            return;
        }


        AdLoader.Builder builder = new AdLoader.Builder(this, DFP_AD_UNIT_ID);

        if (requestCustomTemplateAds) {
            builder.forCustomTemplateAd(SIMPLE_TEMPLATE_ID,
                    new NativeCustomTemplateAd.OnCustomTemplateAdLoadedListener() {
                        @Override
                        public void onCustomTemplateAdLoaded(NativeCustomTemplateAd ad) {
                            FrameLayout frameLayout = findViewById(R.id.fl_adplaceholder);
                            View adView = getLayoutInflater()
                                    .inflate(R.layout.ad_simple_custom_template, null);
                            populateSimpleTemplateAdView(ad, adView);
                            frameLayout.removeAllViews();
                            frameLayout.addView(adView);
                        }
                    },
                    new NativeCustomTemplateAd.OnCustomClickListener() {
                        @Override
                        public void onCustomClick(NativeCustomTemplateAd ad, String s) {
                            Toast.makeText(MainActivity.this,
                                    "A custom click has occurred in the simple template",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        VideoOptions videoOptions = new VideoOptions.Builder()
                .setStartMuted(false)
                .build();

        NativeAdOptions adOptions = new NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .build();

        builder.withNativeAdOptions(adOptions);

        AdLoader adLoader = builder.withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int errorCode) {
                Toast.makeText(MainActivity.this, "Failed to load native ad: "
                        + errorCode, Toast.LENGTH_SHORT).show();
            }
        }).build();

        adLoader.loadAd(new PublisherAdRequest.Builder().build());

    }
}
