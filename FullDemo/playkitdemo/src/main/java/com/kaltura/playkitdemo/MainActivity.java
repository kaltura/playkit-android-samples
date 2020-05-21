package com.kaltura.playkitdemo;

import android.Manifest;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;

import android.hardware.SensorManager;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ads.interactivemedia.v3.api.StreamRequest;
import com.google.gson.JsonObject;
import com.kaltura.android.exoplayer2.LoadControl;
import com.kaltura.android.exoplayer2.upstream.BandwidthMeter;
import com.kaltura.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.kaltura.netkit.connect.executor.APIOkRequestsExecutor;
import com.kaltura.netkit.connect.request.RequestConfiguration;
import com.kaltura.netkit.connect.response.PrimitiveResult;
import com.kaltura.netkit.utils.OnCompletion;
import com.kaltura.netkit.utils.SessionProvider;
import com.kaltura.playkit.PKDrmParams;
import com.kaltura.playkit.PKError;
import com.kaltura.playkit.PKEvent;
import com.kaltura.playkit.PKLog;
import com.kaltura.playkit.PKMediaConfig;
import com.kaltura.playkit.PKMediaEntry;
import com.kaltura.playkit.PKMediaSource;
import com.kaltura.playkit.PKPluginConfigs;
import com.kaltura.playkit.PKRequestParams;
//import com.kaltura.playkit.PKVideoCodec;
import com.kaltura.playkit.PlayKitManager;
import com.kaltura.playkit.Player;
import com.kaltura.playkit.PlayerEvent;
import com.kaltura.playkit.PlayerState;
import com.kaltura.playkit.player.ABRSettings;
import com.kaltura.playkit.player.AudioTrack;
import com.kaltura.playkit.player.BaseTrack;
import com.kaltura.playkit.player.ExoPlayerWrapper;
import com.kaltura.playkit.player.LoadControlBuffers;
import com.kaltura.playkit.player.MediaSupport;
import com.kaltura.playkit.player.PKHttpClientManager;
import com.kaltura.playkit.player.PKTracks;
import com.kaltura.playkit.player.TextTrack;
//import com.kaltura.playkit.player.VideoCodecSettings;
import com.kaltura.playkit.player.VideoTrack;
import com.kaltura.playkit.player.vr.VRInteractionMode;
import com.kaltura.playkit.player.vr.VRSettings;
import com.kaltura.playkit.plugins.ads.AdCuePoints;
import com.kaltura.playkit.plugins.ads.AdEvent;
import com.kaltura.playkit.plugins.ima.IMAConfig;
import com.kaltura.playkit.plugins.ima.IMAPlugin;
import com.kaltura.playkit.plugins.imadai.IMADAIConfig;
import com.kaltura.playkit.plugins.imadai.IMADAIPlugin;
import com.kaltura.playkit.plugins.kava.KavaAnalyticsConfig;
import com.kaltura.playkit.plugins.kava.KavaAnalyticsPlugin;
import com.kaltura.playkit.plugins.ott.OttEvent;
import com.kaltura.playkit.plugins.ott.PhoenixAnalyticsConfig;
import com.kaltura.playkit.plugins.ott.PhoenixAnalyticsEvent;
import com.kaltura.playkit.plugins.ott.PhoenixAnalyticsPlugin;
import com.kaltura.playkit.plugins.playback.KalturaPlaybackRequestAdapter;
import com.kaltura.playkit.plugins.playback.KalturaUDRMLicenseRequestAdapter;
import com.kaltura.playkit.plugins.youbora.YouboraPlugin;
import com.kaltura.playkit.providers.MediaEntryProvider;
import com.kaltura.playkit.providers.api.SimpleSessionProvider;
import com.kaltura.playkit.providers.api.phoenix.APIDefines;
import com.kaltura.playkit.providers.base.OnMediaLoadCompletion;
import com.kaltura.playkit.providers.base.OnPlaylistLoadCompletion;
import com.kaltura.playkit.providers.mock.MockMediaProvider;
import com.kaltura.playkit.providers.ott.OTTMediaAsset;
import com.kaltura.playkit.providers.ott.PhoenixMediaProvider;
import com.kaltura.playkit.providers.ovp.KalturaOvpMediaProvider;

import com.kaltura.playkit.utils.Consts;
import com.kaltura.playkitvr.VRUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.kaltura.playkit.utils.Consts.DISTANCE_FROM_LIVE_THRESHOLD;
import static com.kaltura.playkitdemo.MockParams.FormatTest;
import static com.kaltura.playkitdemo.MockParams.MediaIdTest;
import static com.kaltura.playkitdemo.MockParams.OvpUserKS;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,
        OrientationManager.OrientationListener {


    public static final boolean AUTO_PLAY_ON_RESUME = true;

    private static final PKLog log = PKLog.get("MainActivity");
    public static final String IMA_PLUGIN = "IMA";
    public static final String DAI_PLUGIN = "DAI";
    public static int READ_EXTERNAL_STORAGE_PERMISSIONS_REQUEST = 123;
    public static int changeMediaIndex = -1;
    public static Long START_POSITION = 0L;

    String preMidPostAdTagUrl = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/ad_rule_samples&ciu_szs=300x250&ad_rule=1&impl=s&gdfp_req=1&env=vp&output=vmap&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ar%3Dpremidpostpodbumper&cmsid=496&vid=short_onecue&correlator=";
    String preSkipAdTagUrl    = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dskippablelinear&correlator=";
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
    private ViewGroup companionAdSlot;

    private OrientationManager mOrientationManager;
    private boolean userIsInteracting;
    private PKTracks tracksInfo;
    private boolean isAdsEnabled = true;
    private boolean isDAIMode = false;

    private ExoPlayerWrapper.LoadControlStrategy loadControlStrategy;

    static {
        PKHttpClientManager.setHttpProvider("okhttp");
        PKHttpClientManager.warmUp(
                "https://rest-as.ott.kaltura.com/crossdomain.xml", // Some Phoenix URL
                "https://cdnapisec.kaltura.com/favicon.ico",
                "https://cfvod.kaltura.com/favicon.ico"
        );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getPermissionToReadExternalStorage();
        initDrm();
        /*try {
            ProviderInstaller.installIfNeeded(this);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }*/
        mOrientationManager = new OrientationManager(this, SensorManager.SENSOR_DELAY_NORMAL, this);
        mOrientationManager.enable();
        setContentView(R.layout.activity_main);

        log.i("PlayKitManager: " + PlayKitManager.CLIENT_TAG);

        Button button = findViewById(R.id.changeMedia);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (player != null) {
                    changeMediaIndex++;
                    OnMediaLoadCompletion playLoadedEntry = registerToLoadedMediaCallback();
                    if (changeMediaIndex % 4 == 0) {
                        startSimpleOvpMediaLoadingDRM(playLoadedEntry);
                        //startSimpleOvpMediaLoadingVR(playLoadedEntry);
                        //startMockMediaLoading(playLoadedEntry);
                    } else if (changeMediaIndex % 4 == 1) {
                        startSimpleOvpMediaLoadingHls(playLoadedEntry);
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
        companionAdSlot = findViewById(R.id.companionAdSlot);

        loadControlStrategy = new ExoPlayerWrapper.LoadControlStrategy() {
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter.Builder(MainActivity.this).build();

            @Override
            public LoadControl getCustomLoadControl() {
                return null;
            }

            @Override
            public BandwidthMeter getCustomBandwidthMeter() {
                return bandwidthMeter;
            }
        };

        registerPlugins();

        OnMediaLoadCompletion playLoadedEntry = registerToLoadedMediaCallback();

        //startPreprodOttMediaLoading(playLoadedEntry);
        //startOttMediaLoading(playLoadedEntry);
        //startSimpleOvpMediaLoadingVR(playLoadedEntry);
        //startSimpleOvpMediaLoadingHls(playLoadedEntry);
        //startSimpleOvpMediaLoadingLive1(playLoadedEntry);
        //startMockMediaLoading(playLoadedEntry);
        //startOvpMediaLoading(playLoadedEntry);
        startSimpleOvpMediaLoadingDRM(playLoadedEntry);
        //startSimpleOvpMediaLoadingHEVC(playLoadedEntry);
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

    private void getPermissionToReadExternalStorage() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
            }
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_EXTERNAL_STORAGE_PERMISSIONS_REQUEST);
        }
    }

    // Callback with the request from calling requestPermissions(...)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        // Make sure it's our original READ_CONTACTS request
        if (requestCode == READ_EXTERNAL_STORAGE_PERMISSIONS_REQUEST) {
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Read Storage permission granted", Toast.LENGTH_SHORT).show();
            } else {
                boolean showRationale = ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
                if (showRationale) {
                    // do something here to handle degraded mode
                } else {
                    Toast.makeText(this, "Read Storage permission denied", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    private void registerPlugins() {

        //PlayKitManager.registerPlugins(this, SamplePlugin.factory);
        PlayKitManager.registerPlugins(this, IMAPlugin.factory);
        PlayKitManager.registerPlugins(this, IMADAIPlugin.factory);
        //PlayKitManager.registerPlugins(this, KalturaStatsPlugin.factory);
        PlayKitManager.registerPlugins(this, KavaAnalyticsPlugin.factory);
        PlayKitManager.registerPlugins(this, YouboraPlugin.factory);
        //PlayKitManager.registerPlugins(this, TVPAPIAnalyticsPlugin.factory);
        //PlayKitManager.registerPlugins(this, PhoenixAnalyticsPlugin.factory);
    }

    @NonNull
    private OnMediaLoadCompletion registerToLoadedMediaCallback() {
        return response -> runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (response.isSuccess()) {
                    if (response.getResponse() instanceof PKMediaEntry) {
                        onMediaLoaded(response.getResponse());
                    } else if (response.getResponse() instanceof List) {
                        //PKPlaylist listofmedia = (PKPlaylist) response.getResponse();
                        //onMediaLoaded(listodmedia.get(1));
                    }
                } else {
                    Toast.makeText(MainActivity.this, "failed to fetch media data: " + (response.getError() != null ? response.getError().getMessage() : ""), Toast.LENGTH_LONG).show();
                    log.e("failed to fetch media data: " + (response.getError() != null ? response.getError().getMessage() : ""));
                }
            }
        });
    }

    private void initDrm() {
        MediaSupport.initializeDrm(this, (supportedDrmSchemes, isHardwareDrmSupported, provisionPerformed, provisionError) -> {
            if (provisionPerformed) {
                if (provisionError != null) {
                    log.e("DRM Provisioning failed", provisionError);
                } else {
                    log.d("DRM Provisioning succeeded");
                }
            }
            log.d("DRM initialized; supported: " + supportedDrmSchemes + " isHardwareDrmSupported = " + isHardwareDrmSupported);

            // Now it's safe to look at `supportedDrmSchemes`
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
        new KalturaOvpMediaProvider("https://cdnapisec.kaltura.com", 1734751, null)
                .setEntryId("1_3o1seqnv")
                .load(completion);
    }

    private void startPreprodOttMediaLoading(final OnMediaLoadCompletion completion) {

        APIOkRequestsExecutor.getSingleton().setRequestConfiguration(new RequestConfiguration().setMaxRetries(5).setReadTimeoutMs(15000));
// APIOkRequestsExecutor.getSingleton().setNetworkErrorEventListener(errorElement -> {
// log.d("XXX NetworkError code = " + errorElement.getCode() + " " + errorElement.getMessage());
//});

        String mediaId = "259153";
        String mediaFormat = "Mobile_Devices_Main_SD";

        OTTMediaAsset ottMediaAsset = new OTTMediaAsset()
                .setAssetId(mediaId)
                .setKs(null)
                 //.setReferrer()
                .setFormats(Collections.singletonList(mediaFormat))
                .setProtocol(PhoenixMediaProvider.HttpProtocol.Http)
                .setContextType(APIDefines.PlaybackContextType.Playback)
                .setAssetType(APIDefines.KalturaAssetType.Media)
                .setAssetReferenceType(APIDefines.AssetReferenceType.Media);

        mediaProvider = new PhoenixMediaProvider(MockParams.PhoenixBaseUrl, MockParams.OttPartnerId, ottMediaAsset);

        mediaProvider.load(completion);
    }

    private void startSimpleOvpMediaLoadingHEVC(OnMediaLoadCompletion completion) {
        new KalturaOvpMediaProvider("https://cdnapisec.kaltura.com", 2215841, null)
                .setEntryId("1_zhpdyrr2")
                .load(completion);
    }

    private void startSimpleOvpMediaLoadingDRM(OnMediaLoadCompletion completion) {
        new KalturaOvpMediaProvider("https://cdnapisec.kaltura.com", 2222401, null)
                .setEntryId("1_f93tepsn")//("1_asoyc5ef") //("1_uzea2uje")
                .load(completion);
    }

    private void startSimpleOvpMediaLoadingVR(OnMediaLoadCompletion completion) {
        new KalturaOvpMediaProvider()
                .setSessionProvider(new SimpleSessionProvider("https://cdnapisec.kaltura.com", 2196781, null))
                .setEntryId("1_afvj3z0u")//("1_asoyc5ef") //("1_uzea2uje")
                .load(completion);
    }

    private void startSimpleOvpMediaLoadingClear(OnMediaLoadCompletion completion) {
        new KalturaOvpMediaProvider("http://qa-apache-php7.dev.kaltura.com/", 1091, null)
                .setEntryId("0_wu32qrt3")
                .load(completion);
    }



    private void startSimpleOvpMediaLoadingLive(OnMediaLoadCompletion completion) {
        new KalturaOvpMediaProvider("http://qa-apache-php7.dev.kaltura.com/", 1091, null)
                .setEntryId("0_nwkp7jtx")
                .load(completion);
    }

    private void startSimpleOvpMediaLoadingLive1(OnMediaLoadCompletion completion) {
        new KalturaOvpMediaProvider("https://cdnapisec.kaltura.com/", 1740481, null)
                .setEntryId("1_fdv46dba")
                .load(completion);
    }

    private void startMockMediaLoading(OnMediaLoadCompletion completion) {

        mediaProvider = new MockMediaProvider("mockfiles/entries.playkit.json", getApplicationContext(), "mp4");

        mediaProvider.load(completion);
    }

    private void startOttMediaLoading(final OnMediaLoadCompletion completion) {

        OTTMediaAsset ottMediaAsset = new OTTMediaAsset()
                .setAssetId(MediaIdTest)
                .setKs(null)
                .setAssetType(APIDefines.KalturaAssetType.Media)
                .setAssetReferenceType(APIDefines.AssetReferenceType.Media)
                .setContextType(APIDefines.PlaybackContextType.Playback)
                .setProtocol(PhoenixMediaProvider.HttpProtocol.All)
                .setFormats(Collections.singletonList(FormatTest));

        mediaProvider = new PhoenixMediaProvider(MockParams.PhoenixBaseUrlUS, MockParams.OttPartnerIdTest, ottMediaAsset);
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
            KalturaPlaybackRequestAdapter.install(player, null); // in case app developer wants to give customized referrer instead the default referrer in the playmanifest
            KalturaUDRMLicenseRequestAdapter.install(player, "app://PlaykitTestApp");


//            String customAdapterData = "PEtleU9TQXV0aGVudGljYXRpb25YTUw+PERhdGE+PEdlbmVyYXRpb25UaW1lPjIwMTktMDItMDQgMTE6MjA6NTQuNzcwPC9HZW5lcmF0aW9uVGltZT48RXhwaXJhdGlvblRpbWU+MjAxOS0wMi0wNiAxMToyMDo1NC43NzA8L0V4cGlyYXRpb25UaW1lPjxVbmlxdWVJZD4yODNjMmY3ZDlkYjg0ZmE1ODdiNTlhYmM2MDA5YWEzMjwvVW5pcXVlSWQ+PFJTQVB1YktleUlkPmIxOWQ0MjJkODkwNjQ0ZDUxMTJkMDg0NjljMmU1OTQ2PC9SU0FQdWJLZXlJZD48V2lkZXZpbmVQb2xpY3kgZmxfQ2FuUGxheT0idHJ1ZSIgZmxfQ2FuUGVyc2lzdD0idHJ1ZSI+PExpY2Vuc2VEdXJhdGlvbj4xNzI4MDA8L0xpY2Vuc2VEdXJhdGlvbj48UGxheWJhY2tEdXJhdGlvbj4xNzI4MDA8L1BsYXliYWNrRHVyYXRpb24+PC9XaWRldmluZVBvbGljeT48V2lkZXZpbmVDb250ZW50S2V5U3BlYyBUcmFja1R5cGU9IkhEIj48U2VjdXJpdHlMZXZlbD4xPC9TZWN1cml0eUxldmVsPjwvV2lkZXZpbmVDb250ZW50S2V5U3BlYz48RmFpclBsYXlQb2xpY3kgcGVyc2lzdGVudD0idHJ1ZSI+PFBlcnNpc3RlbmNlU2Vjb25kcz4xNzI4MDA8L1BlcnNpc3RlbmNlU2Vjb25kcz48L0ZhaXJQbGF5UG9saWN5PjxMaWNlbnNlIHR5cGU9InNpbXBsZSI+PFBvbGljeT48SWQ+YmUxM2NiM2ItNTM3OS00N2Q1LWIyM2QtZWQ0OWU0NTFkMjU5PC9JZD48L1BvbGljeT48UGxheT48SWQ+YmQ5YjdjZTItMmUzYi00NTZlLWExZDMtM2QwMDNkMWNjZjdiPC9JZD48L1BsYXk+PC9MaWNlbnNlPjxQb2xpY3kgaWQ9ImJlMTNjYjNiLTUzNzktNDdkNS1iMjNkLWVkNDllNDUxZDI1OSIgcGVyc2lzdGVudD0idHJ1ZSI+PEV4cGlyYXRpb25BZnRlckZpcnN0UGxheT4xNzI4MDA8L0V4cGlyYXRpb25BZnRlckZpcnN0UGxheT48TWluaW11bVNlY3VyaXR5TGV2ZWw+MjAwMDwvTWluaW11bVNlY3VyaXR5TGV2ZWw+PC9Qb2xpY3k+PFBsYXkgaWQ9ImJkOWI3Y2UyLTJlM2ItNDU2ZS1hMWQzLTNkMDAzZDFjY2Y3YiI+PE91dHB1dFByb3RlY3Rpb25zPjxPUEw+PENvbXByZXNzZWREaWdpdGFsQXVkaW8+MzAwPC9Db21wcmVzc2VkRGlnaXRhbEF1ZGlvPjxVbmNvbXByZXNzZWREaWdpdGFsQXVkaW8+MzAwPC9VbmNvbXByZXNzZWREaWdpdGFsQXVkaW8+PENvbXByZXNzZWREaWdpdGFsVmlkZW8+NTAwPC9Db21wcmVzc2VkRGlnaXRhbFZpZGVvPjxVbmNvbXByZXNzZWREaWdpdGFsVmlkZW8+MzAwPC9VbmNvbXByZXNzZWREaWdpdGFsVmlkZW8+PEFuYWxvZ1ZpZGVvPjIwMDwvQW5hbG9nVmlkZW8+PC9PUEw+PC9PdXRwdXRQcm90ZWN0aW9ucz48RW5hYmxlcnM+PElkPjc4NjYyN2Q4LWMyYTYtNDRiZS04Zjg4LTA4YWUyNTViMDFhNzwvSWQ+PElkPmQ2ODUwMzBiLTBmNGYtNDNhNi1iYmFkLTM1NmYxZWEwMDQ5YTwvSWQ+PElkPjAwMmY5NzcyLTM4YTAtNDNlNS05Zjc5LTBmNjM2MWRjYzYyYTwvSWQ+PC9FbmFibGVycz48L1BsYXk+PC9EYXRhPjxTaWduYXR1cmU+b085eE9BeS9OblZFN3V4UWJtYzFTdlBPRGxGMytGNUpqV0RKR3ZCd3U4cHgyUWx2VERxUGJySU03M0Z5b0dkUHBJeWhpZENwMkJ4eWtCWThFcitMWHFzQXY5aGJZKzdCNkJOc00xMW1DOE4wbCswZlp5dmNzeHpwWGx3UlZMajNJVk9MYTNDdVcrbnV2dlYxUThheWZjeEhwVXc0b25BdEZDYlZaR3lkQS9oQTRLaXorWVhWdDZReEdsbDhVSFFJRUJVRWlDbzFvVmtRMlJGcEU1S2Jac2pTQ1FST3lvZ1MrUmo4dFo0b3FpQlNpMlNVamVvRUduY0RueHdFK1dDWkhrSUxSSjNiSktkbkZUSTRJR0prQ0traWtxUWZrUWRjSzQ0Y2d1aEY0UTJEQkd3eUxqUmFucVhuTFNaVS9obFlZUDJVbWlIa29UMWdFZ3JyYU8yanF3PT08L1NpZ25hdHVyZT48L0tleU9TQXV0aGVudGljYXRpb25YTUw+";
//            DRMAdapter.customData = customAdapterData;
//            final DRMAdapter licenseRequestAdapter = new DRMAdapter();
//            player.getSettings().setLicenseRequestAdapter(licenseRequestAdapter);

            if (mediaEntry.isVRMediaType()) {
                VRSettings vrSettings = new VRSettings();
                vrSettings.setFlingEnabled(true);
                vrSettings.setVrModeEnabled(false);
                vrSettings.setInteractionMode(VRInteractionMode.MotionWithTouch);
                vrSettings.setZoomWithPinchEnabled(true);
                VRInteractionMode interactionMode = vrSettings.getInteractionMode();
                if (!VRUtil.isModeSupported(MainActivity.this, interactionMode)) {
                    //In case when mode is not supported we switch to supported mode.
                    vrSettings.setInteractionMode(VRInteractionMode.Touch);
                }
                player.getSettings().setVRSettings(vrSettings);
            }

            player.getSettings().setCustomLoadControlStrategy(loadControlStrategy);
            player.getSettings().setAdAutoPlayOnResume(true);
            player.getSettings().setSecureSurface(false);
            player.getSettings().setAdAutoPlayOnResume(true);
            //player.getSettings().setPreferredVideoCodecSettings(new VideoCodecSettings(Collections.singletonList(PKVideoCodec.VP9), true, false));
            player.getSettings().setAllowCrossProtocolRedirect(true);
            //player.getSettings().setPlayerBuffers(new LoadControlBuffers());
            player.getSettings().enableDecoderFallback(true);
            //player.setPlaybackRate(1.5f);
            //player.getSettings().setABRSettings(new ABRSettings().setInitialBitrateEstimate(100000).setMaxVideoBitrate(80000));
            log.d("Player: " + player.getClass());
            addPlayerListeners(progressBar);

            FrameLayout layout = (FrameLayout) findViewById(R.id.player_view);
            layout.addView(player.getView());


            //SurfaceView surface = findViewById(R.id.player_view);
           // player.setVideoSurfaceView(surface);
            //surface.setVisibility(View.VISIBLE);
            player.pause();
            controlsView = (PlaybackControlsView) this.findViewById(R.id.playerControls);
            controlsView.setPlayer(player);
            initSpinners();
        } else {
            if (changeMediaIndex % 4 == 0) {
                if (isAdsEnabled) {
                    if (isDAIMode) {
                        promptMessage(DAI_PLUGIN, getDAIConfig2().getAssetTitle());
                        player.updatePluginConfig(IMADAIPlugin.factory.getName(), getDAIConfig2());
                    } else {
                        log.d("Play Ad preMidPostAdTagUrl");
                        promptMessage(IMA_PLUGIN, "preMidPostAdTagUrl");
                        player.updatePluginConfig(IMAPlugin.factory.getName(), getAdsConfig(preMidPostAdTagUrl));
                    }
                }
                player.updatePluginConfig(YouboraPlugin.factory.getName(), getYouboraJsonObject(false, "preMidPostAdTagUrl media2"));
                player.updatePluginConfig(KavaAnalyticsPlugin.factory.getName(),  getKavaAnalyticsConfig(2222401, "1_f93tepsn"));

            } else if (changeMediaIndex % 4 == 1) {
                if (isAdsEnabled) {
                    if (isDAIMode) {
                        promptMessage(DAI_PLUGIN, getDAIConfig3().getAssetTitle());
                        player.updatePluginConfig(IMADAIPlugin.factory.getName(), getDAIConfig3());
                    } else {
                        log.d("Play Ad inLinePreAdTagUrl");
                        promptMessage(IMA_PLUGIN, "inLinePreAdTagUrl");
                        player.updatePluginConfig(IMAPlugin.factory.getName(), getAdsConfig(inLinePreAdTagUrl));
                    }
                }
                player.updatePluginConfig(YouboraPlugin.factory.getName(), getYouboraJsonObject(true, "inLinePreAdTagUrl media3"));
                player.updatePluginConfig(KavaAnalyticsPlugin.factory.getName(),  getKavaAnalyticsConfig(1740481, "1_fdv46dba"));
            } if (changeMediaIndex % 4 == 2) {
                if (isAdsEnabled) {
                    if (isDAIMode) {
                        promptMessage(DAI_PLUGIN, getDAIConfig4().getAssetTitle());
                        player.updatePluginConfig(IMADAIPlugin.factory.getName(), getDAIConfig4());
                    } else {
                        log.d("Play NO Ad");
                        promptMessage(IMA_PLUGIN, "Enpty AdTag");
                        player.updatePluginConfig(IMAPlugin.factory.getName(), getAdsConfig(""));
                    }
                }
                player.updatePluginConfig(YouboraPlugin.factory.getName(), getYouboraJsonObject(false, "NO AD media4"));
                player.updatePluginConfig(KavaAnalyticsPlugin.factory.getName(),  getKavaAnalyticsConfig(1091, "0_wu32qrt3"));
            } if (changeMediaIndex % 4 == 3) {
                if (isAdsEnabled) {
                    if (isDAIMode) {
                        promptMessage(DAI_PLUGIN, getDAIConfig5().getAssetTitle());
                        player.updatePluginConfig(IMADAIPlugin.factory.getName(), getDAIConfig5());
                    } else {
                        log.d("Play Ad preSkipAdTagUrl");
                        promptMessage(IMA_PLUGIN, "preSkipAdTagUrl");
                        player.updatePluginConfig(IMAPlugin.factory.getName(), getAdsConfig(preSkipAdTagUrl));
                    }
                }
                
                player.getSettings().setPlayerBuffers(new LoadControlBuffers().
                        setMinPlayerBufferMs(2500).
                        setMaxPlayerBufferMs(50000).setAllowedVideoJoiningTimeMs(4000));

                player.updatePluginConfig(YouboraPlugin.factory.getName(), getYouboraJsonObject(false, "preSkipAdTagUrl media1"));
                player.updatePluginConfig(KavaAnalyticsPlugin.factory.getName(),  getKavaAnalyticsConfig(1734751, "1_3o1seqnv"));
            }
        }

        player.prepare(mediaConfig);
        player.play();
    }

    private void initSpinners() {
        videoSpinner = this.findViewById(R.id.videoSpinner);
        audioSpinner = this.findViewById(R.id.audioSpinner);
        textSpinner =  this.findViewById(R.id.subtitleSpinner);

        textSpinner.setOnItemSelectedListener(this);
        audioSpinner.setOnItemSelectedListener(this);
        videoSpinner.setOnItemSelectedListener(this);
    }

    private void configurePlugins(PKPluginConfigs pluginConfigs) {
        if (isAdsEnabled) {
            if (isDAIMode) {
                addIMADAIPluginConfig(pluginConfigs, 1);
            } else {
                addIMAPluginConfig(pluginConfigs);
            }
        }
        //addKaluraStatsPluginConfig(pluginConfigs, 1734751, "1_3o1seqnv");
        addYouboraPluginConfig(pluginConfigs, false, "preMidPostSingleAdTagUrl Title1");
        addKavaPluginConfig(pluginConfigs, 1734751, "1_3o1seqnv");
        //addPhoenixAnalyticsPluginConfig(pluginConfigs);
        //addTVPAPIAnalyticsPluginConfig(pluginConfigs);
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
                .setUserId("aaa@gmail.com")
                .setEntryId(ovpEntryId)
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
        pluginEntry.addProperty("username", "gilad@a.com");
        pluginEntry.addProperty("haltOnError", true);
        pluginEntry.addProperty("enableAnalytics", true);
        pluginEntry.addProperty("enableSmartAds", true);
        pluginEntry.addProperty("userObfuscateIp", true);


        //Optional - Device json o/w youbora will decide by its own.
        JsonObject deviceJson = new JsonObject();
        deviceJson.addProperty("deviceCode", "AndroidTV");
        deviceJson.addProperty("brand", "Xiaomi");
        deviceJson.addProperty("model", "Mii3");
        deviceJson.addProperty("type", "TvBox");
        deviceJson.addProperty("osName", "Android/Oreo");
        deviceJson.addProperty("osVersion", "8.1");


        //Media entry json.
        JsonObject mediaEntryJson = new JsonObject();
        mediaEntryJson.addProperty("isLive", isLive);
        mediaEntryJson.addProperty("GILAD TITLE", title);

        //Youbora ads configuration json.
        JsonObject adsJson = new JsonObject();
        adsJson.addProperty("adsExpected", true);
        adsJson.addProperty("campaign", "zzz");

        //Configure custom properties here:
        JsonObject propertiesJson = new JsonObject();
        propertiesJson.addProperty("genre", "");
        propertiesJson.addProperty("type", "");
        propertiesJson.addProperty("transactionType", "TransactionType-1");
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
        pluginEntry.add("device", deviceJson);
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

        log.d("Play Ad preSkipAdTagUrl");
        promptMessage(IMA_PLUGIN, "preSkipAdTagUrl");
        IMAConfig adsConfig = getAdsConfig(preMidPostSingleAdTagUrl).setCompanionAdConfig(companionAdSlot, 300, 250);
        config.setPluginConfig(IMAPlugin.factory.getName(), adsConfig);
    }

    private IMAConfig getAdsConfig(String adTagUrl) {
        List<String> videoMimeTypes = new ArrayList<>();
        videoMimeTypes.add("video/mp4");
        videoMimeTypes.add("application/x-mpegURL");
        videoMimeTypes.add("application/dash+xml");
        return new IMAConfig().setAdTagUrl(adTagUrl).setVideoMimeTypes(videoMimeTypes).enableDebugMode(true).setAlwaysStartWithPreroll(true).setAdLoadTimeOut(8);
    }


    private IMAConfig getAdsConfigResponse(String adResponse) {
        List<String> videoMimeTypes = new ArrayList<>();
        videoMimeTypes.add("video/mp4");
        videoMimeTypes.add("application/x-mpegURL");
        // videoMimeTypes.add("application/dash+xml");
        return new IMAConfig().setAdTagResponse(adResponse).setVideoMimeTypes(videoMimeTypes).setAlwaysStartWithPreroll(true).setAdLoadTimeOut(8);
    }

    //IMA DAI CONFIG
    private void addIMADAIPluginConfig(PKPluginConfigs config, int daiType) {
        switch (daiType) {
            case 1: {
                promptMessage(DAI_PLUGIN, getDAIConfig1().getAssetTitle());
                IMADAIConfig adsConfig = getDAIConfig1();
                config.setPluginConfig(IMADAIPlugin.factory.getName(), adsConfig);
            }
            break;
            case 2: {
                promptMessage(DAI_PLUGIN, getDAIConfig2().getAssetTitle());
                IMADAIConfig adsConfigLive = getDAIConfig2();
                config.setPluginConfig(IMADAIPlugin.factory.getName(), adsConfigLive);
            }
            break;
            case 3: {
                promptMessage(DAI_PLUGIN, getDAIConfig3().getAssetTitle());
                IMADAIConfig adsConfigDash = getDAIConfig3();
                config.setPluginConfig(IMADAIPlugin.factory.getName(), adsConfigDash);
            }
            break;
            case 4: {
                promptMessage(DAI_PLUGIN, getDAIConfig4().getAssetTitle());
                IMADAIConfig adsConfigVod2 = getDAIConfig4();
                config.setPluginConfig(IMADAIPlugin.factory.getName(), adsConfigVod2);
            }
            break;
            case 5: {
                promptMessage(DAI_PLUGIN, getDAIConfig5().getAssetTitle());
                IMADAIConfig adsConfig5 = getDAIConfig5();
                config.setPluginConfig(IMADAIPlugin.factory.getName(), adsConfig5);
            }
            break;
            case 6: {
                promptMessage(DAI_PLUGIN, getDAIConfig6().getAssetTitle());
                IMADAIConfig adsConfigError = getDAIConfig6();
                config.setPluginConfig(IMADAIPlugin.factory.getName(), adsConfigError);
            }
            break;
            default:
                break;
        }
    }

    private void promptMessage(String type, String title) {
        Toast.makeText(this, type + " " + title, Toast.LENGTH_SHORT).show();
    }

    private IMADAIConfig getDAIConfig6() {
        String assetTitle = "ERROR";
        String assetKey = null;
        String apiKey = null;
        String contentSourceId = "19823";
        String videoId = "ima-test";
        StreamRequest.StreamFormat streamFormat = StreamRequest.StreamFormat.HLS;
        String licenseUrl = null;
        return IMADAIConfig.getVodIMADAIConfig(assetTitle,
                contentSourceId + "AAAA",
                videoId,
                apiKey,
                streamFormat,
                licenseUrl).enableDebugMode(true);
    }

    private IMADAIConfig getDAIConfig5() {
        String assetTitle = "VOD - Google I/O";
        String assetKey = null;
        String apiKey = null;
        String contentSourceId = "2477953";
        String videoId = "googleio-highlights";
        StreamRequest.StreamFormat streamFormat = StreamRequest.StreamFormat.HLS;
        String licenseUrl = null;
        return IMADAIConfig.getVodIMADAIConfig(assetTitle,
                contentSourceId,
                videoId,
                apiKey,
                streamFormat,
                licenseUrl).enableDebugMode(true);
    }

    private IMADAIConfig getDAIConfig5_1() {
        String assetTitle = "AD5_1";
        String assetKey = null;
        String apiKey = null;
        String contentSourceId = "19823";
        String videoId = "ima-test";
        StreamRequest.StreamFormat streamFormat = StreamRequest.StreamFormat.HLS;
        String licenseUrl = null;
        return IMADAIConfig.getVodIMADAIConfig(assetTitle,
                contentSourceId,
                videoId,
                apiKey,
                streamFormat,
                licenseUrl).enableDebugMode(true);
    }


    @NonNull
    private IMADAIConfig getDAIConfig4() {
        String assetTitle = "AD4";
        String apiKey = null;
        String contentSourceId = "2472176";
        String videoId = "2504847";
        StreamRequest.StreamFormat streamFormat = StreamRequest.StreamFormat.HLS;
        String licenseUrl = null;
        return IMADAIConfig.getVodIMADAIConfig(assetTitle,
                contentSourceId,
                videoId,
                apiKey,
                streamFormat,
                licenseUrl);
    }

    private IMADAIConfig getDAIConfig3() {
        String assetTitle = "BBB-widevine";
        String apiKey = null;
        String contentSourceId = "2474148";
        String videoId = "bbb-widevine";
        StreamRequest.StreamFormat streamFormat = StreamRequest.StreamFormat.DASH;
        String licenseUrl = "https://proxy.uat.widevine.com/proxy";
        return IMADAIConfig.getVodIMADAIConfig(assetTitle,
                contentSourceId,
                videoId,
                apiKey,
                streamFormat,
                licenseUrl).enableDebugMode(true);
    }

    private IMADAIConfig getDAIConfig2() {
        String assetTitle = "Live Video - Big Buck Bunny";
        String assetKey = "sN_IYUG8STe1ZzhIIE_ksA";
        String apiKey = null;
        StreamRequest.StreamFormat streamFormat = StreamRequest.StreamFormat.HLS;
        String licenseUrl = null;
        return IMADAIConfig.getLiveIMADAIConfig(assetTitle,
                assetKey,
                apiKey,
                streamFormat,
                licenseUrl).setAlwaysStartWithPreroll(true).enableDebugMode(true);
    }

    private IMADAIConfig getDAIConfig1() {
        String assetTitle = "VOD - Tears of Steel";
        String apiKey = null;
        String contentSourceId = "2477953";
        String videoId = "tears-of-steel";
        StreamRequest.StreamFormat streamFormat = StreamRequest.StreamFormat.HLS;
        String licenseUrl = null;

        return IMADAIConfig.getVodIMADAIConfig(assetTitle,
                contentSourceId,
                videoId,
                apiKey,
                streamFormat,
                licenseUrl).enableDebugMode(true).setAlwaysStartWithPreroll(true);
    }

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
            controlsView.setSeekBarStateForAd(false);
            controlsView.setPlayerState(PlayerState.READY);
        });

        player.addListener(this, AdEvent.daiSourceSelected, event -> {
            log.d("DAI_SOURCE_SELECTED: " + event.sourceURL);

        });

        player.addListener(this, AdEvent.contentPauseRequested, event -> {
            log.d("AD_CONTENT_PAUSE_REQUESTED");
            appProgressBar.setVisibility(View.VISIBLE);
            controlsView.setSeekBarStateForAd(true);
            controlsView.setPlayerState(PlayerState.READY);
        });

        player.addListener(this, AdEvent.adPlaybackInfoUpdated, event -> {
            log.d("AD_PLAYBACK_INFO_UPDATED");
            log.d("playbackInfoUpdated  = " + event.width + "/" + event.height + "/" + event.bitrate);
        });

        player.addListener(this, AdEvent.cuepointsChanged, event -> {
            adCuePoints = event.cuePoints;

            if (adCuePoints != null) {
                log.d("Has Postroll = " + adCuePoints.hasPostRoll());
            }
        });

        player.addListener(this, AdEvent.adBufferStart, event -> {
            log.d("AD_BUFFER_START pos = " + event.adPosition);
            appProgressBar.setVisibility(View.VISIBLE);
        });

        player.addListener(this, AdEvent.adBufferEnd, event -> {
            log.d("AD_BUFFER_END pos = " + event.adPosition);
            appProgressBar.setVisibility(View.INVISIBLE);
        });

        player.addListener(this, AdEvent.adFirstPlay, event -> {
            log.d("AD_FIRST_PLAY");
            appProgressBar.setVisibility(View.INVISIBLE);
        });

        player.addListener(this, AdEvent.started, event -> {
            log.d("AD_STARTED w/h - " + event.adInfo.getAdWidth() + "/" + event.adInfo.getAdHeight());
            appProgressBar.setVisibility(View.INVISIBLE);
        });

        player.addListener(this, AdEvent.resumed, event -> {
            log.d("Ad Event AD_RESUMED");
            nowPlaying = true;
            appProgressBar.setVisibility(View.INVISIBLE);
        });

        player.addListener(this, AdEvent.playHeadChanged, event -> {
            appProgressBar.setVisibility(View.INVISIBLE);
            //log.d("received AD PLAY_HEAD_CHANGED " + event.adPlayHead);
        });

        player.addListener(this, AdEvent.allAdsCompleted, event -> {
            log.d("Ad Event AD_ALL_ADS_COMPLETED");
            appProgressBar.setVisibility(View.INVISIBLE);
            if (adCuePoints != null && adCuePoints.hasPostRoll()) {
                controlsView.setPlayerState(PlayerState.IDLE);
            }
        });

        player.addListener(this, AdEvent.error, event -> {
            if (event != null && event.error != null) {
                controlsView.setSeekBarStateForAd(false);
                String cause = (event.error.exception != null && event.error.exception.getCause() != null) ? event.error.exception.getCause().getMessage() : "";
                log.d("PlayerEvent.Error event  position = " + event.error.errorType + " errorMessage = " + event.error.message + " " + cause);
                if (event.error.severity == PKError.Severity.Fatal) {
                    appProgressBar.setVisibility(View.INVISIBLE);
                }
            }
        });

        player.addListener(this, AdEvent.skipped, event -> {
            log.d("Ad Event SKIPPED");
            nowPlaying = true;
        });

        player.addListener(this, PlayerEvent.surfaceAspectRationSizeModeChanged, event -> {
            log.d("resizeMode updated" + event.resizeMode);
        });


        /////// PLAYER EVENTS

        player.addListener(this, PlayerEvent.play, event -> {
            log.d("Player Event PLAY");
            nowPlaying = true;
        });

        player.addListener(this, PlayerEvent.playing, event -> {
            log.d("Player Event PLAYING");
            appProgressBar.setVisibility(View.INVISIBLE);
            nowPlaying = true;
        });

        player.addListener(this, PlayerEvent.pause, event -> {
            log.d("Player Event PAUSE");
            nowPlaying = false;
        });

        player.addListener(this, PlayerEvent.volumeChanged, event -> {
            log.d("volumeChanged " + event.volume);
        });


        player.addListener(this, PlayerEvent.playbackRateChanged, event -> {
            log.d("playbackRateChanged event  rate = " + event.rate);
        });

        player.addListener(this, PlayerEvent.tracksAvailable, event -> {
            //When the track data available, this event occurs. It brings the info object with it.
            tracksInfo = event.tracksInfo;
            if (player != null) {
                if (!tracksInfo.getVideoTracks().isEmpty()) {
                    player.changeTrack(tracksInfo.getVideoTracks().get(0).getUniqueId());
                }
            }
            populateSpinnersWithTrackInfo(event.tracksInfo);
        });

        player.addListener(this, PlayerEvent.videoTrackChanged, event -> {
            //When the track data available, this event occurs. It brings the info object with it.
            VideoTrack track = event.newTrack;
            log.d("videoTrackChanged getBitrate= " + track.getBitrate());
        });

        player.addListener(this, PlayerEvent.textTrackChanged, event -> {
            //When the track data available, this event occurs. It brings the info object with it.
            TextTrack track = event.newTrack;
            log.d("textTrackChanged " + track.getLanguage() +  "-"  + track.getLabel());
        });

        player.addListener(this, PlayerEvent.audioTrackChanged, event -> {
            //When the track data available, this event occurs. It brings the info object with it.
            AudioTrack track = event.newTrack;
            log.d("audioTrackChanged " + track.getLanguage() +  "-"  + track.getLabel());
        });


        player.addListener(this, PlayerEvent.sourceSelected, event -> {
            log.d("sourceSelected event source = " + event.source);
        });

        player.addListener(this, PlayerEvent.playbackRateChanged, event -> {
            log.d("playbackRateChanged event  rate = " + event.rate);
        });

        player.addListener(this, PlayerEvent.error, event -> {
            //When the track data available, this event occurs. It brings the info object with it.
            if (event != null && event.error != null) {
                log.d("PlayerEvent.Error event  position = " + event.error.errorType + " errorMessage = " + event.error.message);
            }
        });

        player.addListener(this, PlayerEvent.ended, event -> {
            appProgressBar.setVisibility(View.INVISIBLE);
        });

        player.addListener(this, PlayerEvent.playheadUpdated, event -> {
            //When the track data available, this event occurs. It brings the info object with it.
            //log.d("playheadUpdated event  position = " + event.position + " duration = " + event.duration);
        });

        player.addListener(this, PlayerEvent.videoFramesDropped, event -> {
            //log.d("VIDEO_FRAMES_DROPPED " + event.droppedVideoFrames);
        });

        player.addListener(this, PlayerEvent.bytesLoaded, event -> {
            //log.d("BYTES_LOADED " + event.bytesLoaded);
        });

        player.addListener(this, PlayerEvent.stateChanged, new PKEvent.Listener<PlayerEvent.StateChanged>() {
            @Override
            public void onEvent(PlayerEvent.StateChanged event) {
                log.d("State changed from " + event.oldState + " to " + event.newState);
                if (event.newState == PlayerState.BUFFERING) {
                    appProgressBar.setVisibility(View.VISIBLE);
                }
                if ((event.oldState == PlayerState.LOADING || event.oldState == PlayerState.BUFFERING) && event.newState == PlayerState.READY) {
                    appProgressBar.setVisibility(View.INVISIBLE);

                }
                if(controlsView != null){
                    controlsView.setPlayerState(event.newState);
                }
            }
        });

        /////Phoenix events

        player.addListener(this, PhoenixAnalyticsEvent.bookmarkError, event -> {
            log.d("bookmarkErrorEvent errorCode = " + event.errorCode + " message = " + event.errorMessage);
        });

        player.addListener(this, PhoenixAnalyticsEvent.concurrencyError, event -> {
            log.d("ConcurrencyErrorEvent errorCode = " + event.errorCode + " message = " + event.errorMessage);
        });

        player.addListener(this, PhoenixAnalyticsEvent.error, event -> {
            log.d("Phoenox Analytics errorEvent errorCode = " + event.errorCode + " message = " + event.errorMessage);
        });

        player.addListener(this, PhoenixAnalyticsEvent.error, event -> {
            log.d("Phoenox Analytics errorEvent errorCode = " + event.errorCode + " message = " + event.errorMessage);
        });

        player.addListener(this, OttEvent.ottEvent, event -> {
            log.d("Concurrency event = " + event.type);
        });
    }

    @Override
    protected void onResume() {
        log.d("Application onResume");
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
                //Map<Integer, AtomicInteger> channelMap = new HashMap<>();
                SparseArray<AtomicInteger> channelSparseIntArray = new SparseArray<>();

                for (int i = 0; i < trackInfos.size(); i++) {
                    if (channelSparseIntArray.get(((AudioTrack) trackInfos.get(i)).getChannelCount()) != null) {
                        channelSparseIntArray.get(((AudioTrack) trackInfos.get(i)).getChannelCount()).incrementAndGet();
                    } else {
                        channelSparseIntArray.put(((AudioTrack) trackInfos.get(i)).getChannelCount(), new AtomicInteger(1));
                    }
                }
                boolean addChannel = false;
                if (channelSparseIntArray.size() > 0 && !(new AtomicInteger(trackInfos.size()).toString().equals(channelSparseIntArray.get(((AudioTrack) trackInfos.get(0)).getChannelCount()).toString()))) {
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
                        if (!TextUtils.isEmpty(bitrate)) {
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

    //Example for Custom Licens Adapter
    static class DRMAdapter implements PKRequestParams.Adapter {

        public static String customData;
        @Override
        public PKRequestParams adapt(PKRequestParams requestParams) {
            requestParams.headers.put("customData", customData);
            return requestParams;
        }

        @Override
        public void updateParams(Player player) {
            // TODO?
        }

        @Override
        public String getApplicationName() {
            return null;
        }
    }


}
