package com.kaltura.playkitdemo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.ContextMenu;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.ads.interactivemedia.v3.api.StreamRequest;
import com.google.gson.JsonObject;
import com.kaltura.netkit.BuildConfig;
import com.kaltura.netkit.connect.response.PrimitiveResult;
import com.kaltura.netkit.utils.OnCompletion;
import com.kaltura.netkit.utils.SessionProvider;
import com.kaltura.playkit.PKDrmParams;
import com.kaltura.playkit.PKEvent;
import com.kaltura.playkit.PKLog;
import com.kaltura.playkit.PKMediaConfig;
import com.kaltura.playkit.PKMediaEntry;
import com.kaltura.playkit.PKMediaSource;
import com.kaltura.playkit.PKPluginConfigs;
import com.kaltura.playkit.PKRequestParams;
import com.kaltura.playkit.PKSubtitleFormat;
import com.kaltura.playkit.PlayKitManager;
import com.kaltura.playkit.Player;
import com.kaltura.playkit.PlayerEvent;
import com.kaltura.playkit.PlayerState;
import com.kaltura.playkit.player.ABRSettings;
import com.kaltura.playkit.player.AudioTrack;
import com.kaltura.playkit.player.BaseTrack;
import com.kaltura.playkit.player.LoadControlBuffers;
import com.kaltura.playkit.player.MediaSupport;
import com.kaltura.playkit.player.PKExternalSubtitle;
import com.kaltura.playkit.player.PKHttpClientManager;
//import com.kaltura.playkit.player.PKLowLatencyConfig;
import com.kaltura.playkit.player.PKSubtitlePosition;
import com.kaltura.playkit.player.PKTracks;
import com.kaltura.playkit.player.PlayerSettings;
import com.kaltura.playkit.player.SubtitleStyleSettings;
import com.kaltura.playkit.player.TextTrack;
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
import com.kaltura.playkit.plugins.youbora.YouboraPlugin;

import com.kaltura.playkit.providers.MediaEntryProvider;
import com.kaltura.playkit.providers.api.SimpleSessionProvider;
import com.kaltura.playkit.providers.api.phoenix.APIDefines;
import com.kaltura.playkit.providers.base.OnMediaLoadCompletion;
import com.kaltura.playkit.providers.mock.MockMediaProvider;
import com.kaltura.playkit.providers.ott.PhoenixMediaProvider;
import com.kaltura.playkit.providers.ovp.KalturaOvpMediaProvider;
import com.kaltura.playkit.utils.Consts;
import com.kaltura.playkitvr.VRUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static com.kaltura.playkit.Utils.toBase64;
import static com.kaltura.playkit.plugins.youbora.pluginconfig.YouboraConfig.KEY_CONTENT_METADATA_CAST;
import static com.kaltura.playkit.plugins.youbora.pluginconfig.YouboraConfig.KEY_CONTENT_METADATA_DIRECTOR;
import static com.kaltura.playkit.plugins.youbora.pluginconfig.YouboraConfig.KEY_CONTENT_METADATA_OWNER;
import static com.kaltura.playkit.plugins.youbora.pluginconfig.YouboraConfig.KEY_CONTENT_METADATA_PARENTAL;
import static com.kaltura.playkit.plugins.youbora.pluginconfig.YouboraConfig.KEY_CONTENT_METADATA_QUALITY;
import static com.kaltura.playkit.plugins.youbora.pluginconfig.YouboraConfig.KEY_CONTENT_METADATA_RATING;
import static com.kaltura.playkit.plugins.youbora.pluginconfig.YouboraConfig.KEY_CONTENT_METADATA_YEAR;
import static com.kaltura.playkit.plugins.youbora.pluginconfig.YouboraConfig.KEY_HOUSEHOLD_ID;
import static com.kaltura.playkit.utils.Consts.DISTANCE_FROM_LIVE_THRESHOLD;
import static com.kaltura.playkit.utils.Consts.TIME_UNSET;
import static com.kaltura.playkitdemo.MockParams.FormatTest;
import static com.kaltura.playkitdemo.MockParams.MediaIdTest;
import static com.kaltura.playkitdemo.MockParams.OvpUserKS;
import static com.npaw.youbora.lib6.plugin.Options.KEY_ACCOUNT_CODE;
import static com.npaw.youbora.lib6.plugin.Options.KEY_AD_CAMPAIGN;
import static com.npaw.youbora.lib6.plugin.Options.KEY_APP_NAME;
import static com.npaw.youbora.lib6.plugin.Options.KEY_APP_RELEASE_VERSION;
import static com.npaw.youbora.lib6.plugin.Options.KEY_CONTENT_CDN;
import static com.npaw.youbora.lib6.plugin.Options.KEY_CONTENT_CHANNEL;
import static com.npaw.youbora.lib6.plugin.Options.KEY_CONTENT_ENCODING_AUDIO_CODEC;
import static com.npaw.youbora.lib6.plugin.Options.KEY_CONTENT_GENRE;
import static com.npaw.youbora.lib6.plugin.Options.KEY_CONTENT_METADATA;
import static com.npaw.youbora.lib6.plugin.Options.KEY_CONTENT_PRICE;
import static com.npaw.youbora.lib6.plugin.Options.KEY_CONTENT_TITLE;
import static com.npaw.youbora.lib6.plugin.Options.KEY_CONTENT_TRANSACTION_CODE;
import static com.npaw.youbora.lib6.plugin.Options.KEY_CONTENT_TYPE;
import static com.npaw.youbora.lib6.plugin.Options.KEY_CUSTOM_DIMENSION_1;
import static com.npaw.youbora.lib6.plugin.Options.KEY_CUSTOM_DIMENSION_2;
import static com.npaw.youbora.lib6.plugin.Options.KEY_DEVICE_BRAND;
import static com.npaw.youbora.lib6.plugin.Options.KEY_DEVICE_CODE;
import static com.npaw.youbora.lib6.plugin.Options.KEY_DEVICE_MODEL;
import static com.npaw.youbora.lib6.plugin.Options.KEY_DEVICE_OS_NAME;
import static com.npaw.youbora.lib6.plugin.Options.KEY_DEVICE_OS_VERSION;
import static com.npaw.youbora.lib6.plugin.Options.KEY_DEVICE_TYPE;
import static com.npaw.youbora.lib6.plugin.Options.KEY_ENABLED;
import static com.npaw.youbora.lib6.plugin.Options.KEY_USERNAME;
import static com.npaw.youbora.lib6.plugin.Options.KEY_USER_EMAIL;

//import com.kaltura.playkit.PKVideoCodec;
//import com.kaltura.playkit.player.VideoCodecSettings;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,
        OrientationManager.OrientationListener, PlaybackControlsView.UIListener {

    public static final String ACTION_PLAY = "ACTION_PLAY";
    public static final String ACTION_PAUSE = "ACTION_PAUSE";

    //"918330" - BUggy
    List<String> mediaArray = new ArrayList<>(Arrays.asList("922976","918331","918329","918327",
            "918326","918324","918321","918319","918318",
            "594979","570738","571796","572824","597345",
            "568308","620160","521735","522773","523739",
            "524710","526594","537514","538376","540085"));


    private String V18_PROD_KS = "djJ8MjI1fBV57xoWVqgtKpRqiTQjjcRGTx15YAGRRnO4Tm_Bw9S_N3Nyhdd47sbbmocfvV1pLrXD82whtCw4J0VpmuKG6zEqWHGykeXt13VqH6QY_MYFnbtqXli5d4vp4qfkFBCGealh_sLku-uDQzWHK_0gBxGj_Z0wcRZ9wXnSFTqSUJYTUZCiShUSVEULWN592ZM8IxJFOWqPhXPh1GAHzroKvVouLzH8Y2hqeXdqeBcXclqypx9phI7rLXI2FxQEJqIN_kdOuCioccnqts2h0MKZ1dR3uxDkregWeWOgegvBLg5RBRN7heX6F472UCYlhU-qHuDwEFGx-Tg4D6VbF5SwwZYKLm3Owy9KesrxcncANTkYpaaeB93t3NxOkWWK7ZTPwg==";

    // Vootkids skype wala issue where they face device not in household issue
    //  private String V18_STAGING_KS = "djJ8MjI1fFGtaQKGiNorkymwcFkmw67G2KE5-lESHZc58uGT05TIMeNqHvlZYKGZOUPiMTZlRyczGovHu9jyQv_BT3rDYPqOOFrmoxJIizjb4xAu9XdLn6AZlrDE1vfBH6BWTU6YtU4s_dW6_Pv_Yr-ke35ilQoC69bAFqq3z7x5Ppf_AspaPGQ2Lb0EE5CufQBcgd0IoabD_5w15py5t9V5r70SkSF-Cn4ZAhNMUiytTDoJx_hH1QdYyAl389YiHNed_65f5gEZDGYUJgpUQpTk8u43SW-uHz9MrOMekvMIeO6Z7Tg-ormQxM0O0yIREi4UX9jXcTOR4iOF6dfAb2eHM7qNXuE-mSBYj_onyRz9b6_8GBtA";

    // Device Not in household wala issue
//    private String V18_PROD_KS = "djJ8MjI1fGcixuqp4KWEfBYF5snfFQ6toIzf10grFxTZeP3jVlXsYUp8WST_van4g418bq1ZuvOFqKtWBy7-j2UUBg_DZeobrJxwOrAqIhzgG7dsnaRLJHS_vkDRFgScrR19xvNRbgpVcdQa0JU7QKyXXDTN8PtcLaJobPoBxCV-Fv7wrC-3vifH-Hrj71N0JYybCSHuDnovUX6BbV_QK-AhuqpDyvTtfy5LUnhfnbFIiqEmpSPdzSPPLzKkmwW-AgYjGdzqroBeGHDXv7DuwjC5GLxFXXuoeQVIA0IAeN8DPc-fK8JlHbzMtENaieaJcaVvm198SvzjxdY-TYsfLZC9rajMcKXuHAhG13pihAEhogUryUatGXbQFIfSI8N4wfeR0JBFafZLYYNhfjB79uuHkqwX1Rq_oixiNr8Ak0to4cAh2Ix91ZXjD0EOcrj08Z3ahV0Xxf4Y9r2gzK6LrziuqRCUbhnH1k2o1b6ZDJ2fbxqX4HAG";

    private String V18_STAGING_KS = "djJ8MjI1fOrU9_6scAYX96nhdsEIdzdotryjEqWkETkUXnXhiCPGPrIYAMuw--Vxd_9uSJqvIa2iFuNmYjsmwadzqVW7NNw2xPDoGNejxlfVNw-f_k1vxq7_LhT67wpECGtbrh7mylllqXu6tkTZuvulHgJAC76ECmwjb3WQjDVXHrsl02_wwketLqv9MaC2z06MMEbbSe6GisEC8TdcwuLj7pnmKzDhNiqOx-H9srDu3Huk372Zh0mVz2B93kSEMVf7TBTr4JSftqJ0kT3ZgaT-CSOTDGz5hOMUXbKGfpmLXfSmqRzzkxXS6freIdxjMh84gFlgz460eE9P9MQe4lpeRsdcz0nkFCM8eeDdzUdeOB2ejDXfCDoS04BbWPPFMrUQHgiNbw==";


    //   List<String> mediaArray = new ArrayList<>(Arrays.asList("353690","353691","353692","379073","353699"));

    public static final boolean AUTO_PLAY_ON_RESUME = true;

    private static final PKLog log = PKLog.get("MainActivity");
    public static final String IMA_PLUGIN = "IMA";
    public static final String DAI_PLUGIN = "DAI";
    public static int READ_EXTERNAL_STORAGE_PERMISSIONS_REQUEST = 123;
    public static int changeMediaIndex = -1;
    public static Long START_POSITION = 0L;//65L;

    public String AD_GOOGLE_SEARCH = "http://pubads.g.doubleclick.net/gampad/ads?sz=640x360&iu=/6062/iab_vast_samples/skippable&ciu_szs=300x250,728x90&impl=s&gdfp_req=1&env=vp&output=xml_vast2&unviewed_position_start=1&url=[referrer_url]&correlator=[timestamp]";
    String preMidPostAdTagUrl = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/ad_rule_samples&ciu_szs=300x250&ad_rule=1&impl=s&gdfp_req=1&env=vp&output=vmap&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ar%3Dpremidpostpodbumper&cmsid=496&vid=short_onecue&correlator=";
    String preSkipAdTagUrl    = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dskippablelinear&correlator=";
    String inLinePreAdTagUrl = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dlinear&correlator=";
    String preMidPostSingleAdTagUrl = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/ad_rule_samples&ciu_szs=300x250&ad_rule=1&impl=s&gdfp_req=1&env=vp&output=vmap&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ar%3Dpremidpost&cmsid=496&vid=short_onecue&correlator=";
    String ads5AdsEvery10Secs = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/ad_rule_samples&ciu_szs=300x250&ad_rule=1&impl=s&gdfp_req=1&env=vp&output=vmap&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ar%3Dpremidpostlongpod&cmsid=496&vid=short_tencue&correlator=";
    String KALTURA_STATS_URL = "https://stats.kaltura.com/api_v3/index.php";
    String v18_ad_tag = "https://pubads.g.doubleclick.net/gampad/live/ads?sz=640x360&iu=%2F21633895671%2FQA%2FAndroid_Native_App%2FCOI&impl=s&gdfp_req=1&env=vp&output=vmap&unviewed_position_start=1&cust_params=sample_ar%3Dskippablelinear%26Gender%3DM%26Age%3D33%26KidsPinEnabled%3DN%26distinct_id%3D42c92f17603e4ee2b4232666b9591134%26AppVersion%3D0.1.80%26DeviceModel%3Dmoto%20g(6)%26OptOut%3DFalse%26OSVersion%3D9%26PackageName%3Dcom.tv.v18.viola%26first_time%3DFalse%26logintype%3DTraditional&description_url=https%253A%252F%252Fwww.voot.com&cmsid=2467608&ppid=42c92f17603e4ee2b4232666b9591134&vid=0_im5ianso&ad_rule=1&correlator=10771&InterstitialRendered=False";
    String Kaltura_Skippable = "https://kaltura.github.io/playkit-admanager-samples/vast/pod-inline-someskip.xml";
    String V18_empty_reponse = "https://pubads.g.doubleclick.net/gampad/live/ads?sz=640x360%7C640x480&iu=%2F21633895671%2FSVOD%2FVideo%2FAndroid_Native_App%2FVOOT%20Select%20Originals&impl=s&gdfp_req=1&env=vp&output=vmap&unviewed_position_start=1&cust_params=sample_ar%3Dskippablelinear%26Gender%3DM%26Age%3D32%26KidsPinEnabled%3DN%26distinct_id%3D5a096e8094e542c99b00c84caa070e40%26AppVersion%3D0.3.3%26DeviceModel%3DONEPLUS%20A6010%26OptOut%3DTrue%26OSVersion%3D10%26PackageName%3Dcom.tv.v18.viola%26first_time%3DFalse%26logintype%3DFacebook&description_url=https%253A%252F%252Fwww.voot.com&cmsid=2511390&ppid=5a096e8094e542c99b00c84caa070e40&vid=1_wiibrart&ad_rule=1&correlator=9489&InterstitialRendered=False";
    public String AD_1 = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dlinear&correlator=";
    public String SKIP_WRONG_AD = "https://pubads.g.doubleclick.net/gampad/ads?sz=850x478%7C640x480&iu=%2F21633895671%2Fvideo%2FDesktop%2FMTV&description_url=https%3A%2F%2Fwww.voot.com&gdfp_req=1&env=vp&output=xml_vmap1&unviewed_position_start=1&ad_rule=1&cmsid=2467608&vid=0_hmq5k9d6&url=https%3A%2F%2Fvoot.com%2Fshows%2Fmtv-hustle-from-home%2F2%2F100824%2Frcr-rolls-back-the-time%2F940097&correlator=4202960119303889&cust_params=sample_ar%3Dskippablelinear%26age%3D31%26gender%3DF%26distinct_id%3D1719d89c24e14-08778a2ac6a557-396c7f07-1aeaa0-1719d89c24fa8b&ppid=1719d89c24e14-08778a2ac6a557-396c7f07-1aeaa0-1719d89c24fa8b&vpa=auto&vpmute=0&sdkv=h.3.384.1&osd=2&frm=0&vis=1&sdr=1&hl=en&is_amp=0&u_so=l&mpt=kaltura-player-js&mpv=0.53.3&adsid=ChAI8OPO9QUQxv_6ifWZmZ4LEkwA2UDr0bZ79j77YjFi4cz3CHrvnQR0mlxe2PFW4aDaNZcN_F2wc5n7BVCtHT6igb-X1L5mEOW4LIkPAhIlvOApwmeJ6-cam5ghVqEY&sdki=44d&adk=300787811&eid=420706105&dlt=1588915545856&idt=32358&dt=1588915601841&cookie=ID%3D48ac7d6646a33603%3AT%3D1587485985%3AS%3DALNI_MaoMJD4gbSk3JGY_xpeqkzfvM-81w&scor=1338166435667239&ged=ve4_td56_tt24_pd56_la0_er0.0.0.0_vi0.0.898.878_vp0_eb16491%22";
    public String singlePostRoll = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/ad_rule_samples&ciu_szs=300x250&ad_rule=1&impl=s&gdfp_req=1&env=vp&output=vmap&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ar%3Dpostonly&cmsid=496&vid=short_onecue&correlator=";
    public String vootPremiumEmptyAdTag = "https://pubads.g.doubleclick.net/gampad/live/ads?sz=640x360%7C640x480&iu=%2F21633895671%2FQASVOD%2FVideo%2FAndroid_Native_App%2FVSO&impl=s&gdfp_req=1&env=vp&output=vmap&unviewed_position_start=1&cust_params=sample_ar%3Dskippablelinear%26Gender%3DM%26Age%3D13%26KidsPinEnabled%3DN%26distinct_id%3DXmWLVbUThyY3NzJGgw7NGPsFYAp2%26AppVersion%3D0.3.4_PPE_DBG%26DeviceModel%3DAndroid%20SDK%20built%20for%20x86%26OptOut%3DFalse%26OSVersion%3D9%26PackageName%3Dcom.tv.v18.viola%26first_time%3DFalse%26logintype%3DTraditional&description_url=https%253A%252F%252Fwww.voot.com&cmsid=2510338&ppid=XmWLVbUThyY3NzJGgw7NGPsFYAp2&vid=0_1sz284bq&ad_rule=1&correlator=1440&InterstitialRendered=False";
    public String v_18_ad = "https://pubads.g.doubleclick.net/gampad/live/ads?sz=640x360&iu=%2F21633895671%2FQA%2FAndroid_Native_App%2FCOI&impl=s&gdfp_req=1&env=vp&output=vmap&unviewed_position_start=1&cust_params=sample_ar%3Dskippablelinear%26Gender%3DU%26Age%3DNULL%26KidsPinEnabled%3DN%26AppVersion%3D0.1.37%26DeviceModel%3DNexus%206&cmsid=2467608&ppid=693f0645-201b-4f2e-84c5-4879abd395aa&vid=0_b49uc8t6&ad_rule=1&correlator=12678";

    public String ad_youbora = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=%2F124319096%2Fexternal%2Fad_rule_samples&ciu_szs=300x250&ad_rule=1&gdfp_req=1&env=vp&output=xml_vmap1&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ar%3Dpremidpostpodbumper&cmsid=496&vid=short_onecue&correlator=4178520229405531&ms=upuacR6gQAr0KtuYm2jCKRfQ-dLzlTIkrpS9TCxaPhnR7jZi4aliCtq_qpOxVhKVsrwzgAxQX64X-FRZgT0yFR8oHyu_FznoTFnlKO2ie-0NTNYNu6lNmTT2DqcBvY_DN6DPu9k2ZJiR6ZaSIqTp987s99clneQsdaRaqFfpVo7suWVijnukZQ6sFtTMk9z-h6IklSeaP3XaZjN22MF15IqFaraVcMmzpwmT3I4MOPUceENqvXOa-0iGbLnyLiv61ixK65d6fu-LzaRAeK5Ogm_OuWxI6r0rF4N0SQp_mQ4n3h6FWB2jfd8-Sx7036W7eFhyU435mQC2Z_N2txoiLg&sdkv=h.3.258.1%2Fn.android.3.20.0%2Fcom.kaltura.kalturaplayertestapp&osd=2&frm=0&vis=1&sdr=1&hl=en&is_lat=0&idtype=adid&rdid=0c714bb8-889b-4297-a158-8dec2c15e2b6&is_amp=0&js=ima-android.3.20.0&an=com.kaltura.kalturaplayertestapp&msid=com.kaltura.kalturaplayertestapp&mv=82313010.com.android.vending&u_so=l&ctv=0&mpt=kaltura-vp-android&mpv=dev.2580ad14&sdki=445&sdk_apis=7%2C8&omid_p=Google1%2Fandroid.3.20.0&sid=172941A5-6E09-4564-B929-5E293F97FEC7&url=com.kaltura.kalturaplayertestapp.adsenseformobileapps.com%2F&eid=21064201%2C46130026&dlt=1608632084524&idt=102&dt=1608632084673&scor=4206523438118768";

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
    private boolean isAdsEnabled = false;
    private boolean isDAIMode = false;
    SubtitleStyleSettings subtitleStyleSettings = new SubtitleStyleSettings("MyCustomSubtitleStyle");
    PKSubtitlePosition pkSubtitlePosition = new PKSubtitlePosition(true);
    // private TextView tvSourceUrl;

    //Youbora analytics Constants - https://developer.nicepeopleatwork.com/apidocs/js6/youbora.Options.html
    public static final String ACCOUNT_CODE = "kalturatest";
    public static final String UNIQUE_USER_NAME = "gourav@bundle.com";
    public static final String MEDIA_TITLE = "your_media_title";
    public static final boolean IS_LIVE = false;
    public static final boolean ENABLE_SMART_ADS = true;
    private static final String CAMPAIGN = "your_campaign_name";
    public static final String EXTRA_PARAM_1 = "playKitPlayer";
    public static final String EXTRA_PARAM_2 = "";
    public static final String GENRE = "your_genre";
    public static final String TYPE = "your_type";
    public static final String TRANSACTION_TYPE = "your_transaction_type";
    public static final String YEAR = "your_year";
    public static final String CAST = "your_cast";
    public static final String DIRECTOR = "your_director";
    private static final String OWNER = "your_owner";
    public static final String PARENTAL = "your_parental";
    public static final String PRICE = "your_price";
    public static final String RATING = "your_rating";
    public static final String AUDIO_TYPE = "your_audio_type";
    public static final String AUDIO_CHANNELS = "your_audio_channels";
    public static final String DEVICE = "your_device";
    public static final String QUALITY = "your_quality";
    public static final String CONTENT_CDN_CODE = "AAPTLIMI";
    public static final String DEVICE_CODE = "your_device_code";

    TextView tvLiveEdge;
    //PKLowLatencyConfig pkLowLatencyConfig = new PKLowLatencyConfig(15000L);

    static {
        PKHttpClientManager.setHttpProvider("okhttp");
        //     PKHttpClientManager.setHttpProvider("system");

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
        PKLog.setGlobalLevel(PKLog.Level.verbose);
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

        //  tvSourceUrl = findViewById(R.id.sourceUrlText);
        //  registerForContextMenu(tvSourceUrl);

//        Map<String, Map<String, List<String>>> map = new HashMap<>();
//        Map<String, List<String>> nMap = new HashMap<>();
//        List<String> list = new ArrayList<String>();
//
//        list.add("ListValue");
//        nMap.put("Key2", list);
//        map.put("Key1", nMap);

        log.i("PlayKitManager: " + PlayKitManager.CLIENT_TAG);

        /*Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                log.v("Gourav 1sec In timer");
                if (player != null && player.isPlaying()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvLiveEdge.setText("Live Offset: " + player.getCurrentLiveOffset() + " ms" + "\n"
                                    + "targetOffset: " + pkLowLatencyConfig.getTargetOffsetMs() + "\n"
                                    + "MinOffset: " + pkLowLatencyConfig.getMinOffsetMs() + "\n"
                                    + "MaxOffset: " + pkLowLatencyConfig.getMaxOffsetMs() + "\n"
                                    + "MinPlaybackSpeed: " + pkLowLatencyConfig.getMinPlaybackSpeed() + "\n"
                                    + "MaxPlaybackSpeed: " + pkLowLatencyConfig.getMaxPlaybackSpeed() + "\n");
                        }
                    });
                  //  log.v("Gourav player.getCurrentLiveOffset() " +  player.getCurrentLiveOffset());
                }
                handler.postDelayed(this, 1000);
            }
        }, 1000);*/

        tvLiveEdge = findViewById(R.id.liveEdge);
        Button button = findViewById(R.id.changeMedia);
        button.setText("Change");

        AtomicBoolean click = new AtomicBoolean(false);
        button.setOnClickListener(v -> {

            //Update ABR Settings
            // player.updateABRSettings(new ABRSettings().setMaxVideoBitrate(600000));

            if (!click.get()) {
                click.set(true);
                pkSubtitlePosition.setPosition( 0, 60, Layout.Alignment.ALIGN_CENTER);
                SubtitleStyleSettings subtitleStyleSettings1 = new SubtitleStyleSettings("Replay_MyCustomSubtitleStyle1");
                subtitleStyleSettings1.setSubtitlePosition(pkSubtitlePosition);
                useSubtitleStyle(false, subtitleStyleSettings1);
            } else {
                click.set(false);
                pkSubtitlePosition.setPosition( 0, 90, Layout.Alignment.ALIGN_CENTER);
                SubtitleStyleSettings subtitleStyleSettings2 = new SubtitleStyleSettings("Replay_MyCustomSubtitleStyle2");
                subtitleStyleSettings2.setSubtitlePosition(pkSubtitlePosition);
                useSubtitleStyle(false, subtitleStyleSettings2);
            }

           /* if (q % 2 == 0) {
                player.updatePKLowLatencyConfig(pkLowLatencyConfig
                        .setTargetOffsetMs(10000L).setMaxOffsetMs(7000L).setMaxPlaybackSpeed(1.5f));

            } else {
                player.updatePKLowLatencyConfig(pkLowLatencyConfig
                        .setTargetOffsetMs(15000L).setMaxOffsetMs(12000L).setMaxPlaybackSpeed(1.5f));
            }
            q++;*/



//            if (player != null) {
//                player.replay();
//            }
        });

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        companionAdSlot = findViewById(R.id.companionAdSlot);

        registerPlugins();

        OnMediaLoadCompletion playLoadedEntry = registerToLoadedMediaCallback();

        // To check hebrew subtitle
        // startSimpleOvpMediaLoadingHlsSubtitle(playLoadedEntry);

        // "764459" : Vootkids firestick issue asset
        //   startVootOttMediaLoadingProd(playLoadedEntry, "594159"); // Dolby Content
        //  startVootOttMediaLoadingProd(playLoadedEntry, "805986"); // iOS linear

        // HEVC Clear live Dialog
        // createPlayerWithoutPhoenixProvider();



        //      startVootOttMediaLoadingProd(playLoadedEntry, "618610", "HLS_Linear_P"); // Live News18 asset


        //startVootOttMediaLoadingProd(playLoadedEntry, "873033"); // 404 on cdnapisec
        // startVootOttMediaLoadingProd(playLoadedEntry, "926531"); // DTG
        //      startVootOttMediaLoadingProd(playLoadedEntry, "977185"); // Voot device not in household media
        //    startVootOttMediaLoadingProd(playLoadedEntry, "975732", "dash Main"); // SRT/webvtt failure source error
        // startVootOttMediaLoadingProd(playLoadedEntry, "979570", "dash Main"); //Gourav
        //    startVootOttMediaLoadingProd(playLoadedEntry, "1017737"); //Gourav // no text track
        //   startVootOttMediaLoadingProd(playLoadedEntry, "1013774"); //Gourav // 1 non default text track
        //    startVootOttMediaLoadingProd(playLoadedEntry, "618620");
        // startVootOttMediaLoadingProd(playLoadedEntry, "929686"); // Working; but not on clear formats
        //  startVootOttMediaLoadingProd(playLoadedEntry, "357141"); // working with clear Not premium
        //  startVootOttMediaLoadingProd(playLoadedEntry, "357824");

        //        startVootOttMediaLoadingStaging(playLoadedEntry, "316699"); // Vootkids device not in household issue
//          startVootOttMediaLoadingStaging(playLoadedEntry, "327807"); // BE 5.6.0 Testing
        //       startVootOttMediaLoadingStaging(playLoadedEntry, "327807"); // BE 5.6.0 Testing


//        startVootOttMediaLoadingProd(playLoadedEntry, "618613", "HLS_Linear_P"); // Voot Live Channel News-18 Kannada
        //  startVootOttMediaLoadingStaging(playLoadedEntry, "326666"); // 403 Soruce Error asset for Tablet Main
        //  startVootOttMediaLoadingStaging(playLoadedEntry, "326683"); // 404 Soruce Error asset for dashclear
        // startVootOttMediaLoadingStaging(playLoadedEntry, "317519"); // Staging asset to test L1 and L3 with DASHENC_TV_PremiumHD
        // startVootOttMediaLoadingStaging(playLoadedEntry, "326510"); // Jio tokenization on clear content
        //  startVootOttMediaLoadingProd(playLoadedEntry, "805990"); // chromecast not working only 24-07-2020 [DASH_LINEAR_APP]

        //     startHorizonOttMediaLoadingLive(playLoadedEntry);
        //   startVootOttMediaLoadingStaging(playLoadedEntry, "326510");// Staging asset


        //     startVootOttMediaLoadingStaging(playLoadedEntry, "334357"); // Staging HEVC assets only HEVC
        //      startVootOttMediaLoadingStaging(playLoadedEntry, "334570"); // Staging HEVC with AVC
        //      startVootOttMediaLoadingStaging(playLoadedEntry, "334577"); // Staging HEVC assets only HEVC
        //334570,334578 HEVC with AVC

        //       startVootOttMediaLoadingStaging(playLoadedEntry, "317071"); // vootkids jio cdn audio only "audio_only_dash"

        //  startWOW(playLoadedEntry);

        //       DVR1(playLoadedEntry); // dash_widevine

//        startEmiritus(playLoadedEntry); // dash_widevine

        //    startOttMediaLoadingAstro(playLoadedEntry);
        // startOttMediaLoadingEmiritus(playLoadedEntry);

        //startOVPVootKids(playLoadedEntry);

        //    startHorizonOttMediaLoadingLive(playLoadedEntry);

        // createPlayerWithoutPhoenixProvider();

//        startSimpleOvpMediaLoadingHls(playLoadedEntry);

//        startSimpleOvpMediaLoadingMulti(playLoadedEntry);

//        startSimpleOvpMediaLoadingChromecast(playLoadedEntry);
//        startOttMediaLoading(playLoadedEntry);
//      startSimpleOvpMediaLoadingVR(playLoadedEntry);
        //     startSimpleOvpMediaLoadingHls(playLoadedEntry);
        //       startSimpleOvpMediaLoadinExtSubtitle(playLoadedEntry);
        //startSimpleOvpMediaLoadingLive1(playLoadedEntry);
        //startSimpleOvpMediaLoadingLive(playLoadedEntry);
        startMockMediaLoading(playLoadedEntry, 7);
//      startOvpMediaLoading(playLoadedEntry);
//      startSimpleOvpMediaLoadingDRM(playLoadedEntry);
//               startSimpleOvpMediaLoadingHEVC(playLoadedEntry);
        //       startSimpleOvpMediaLoadingHEVCKarin(playLoadedEntry);
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

        //   WriteAdbLogs.INSTANCE.sendYouboraLogToFile("Gourav");
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {

        menu.add(0, v.getId(),0, "Copy");
        menu.setHeaderTitle("Copy text"); //setting header title for menu
        TextView textView = (TextView) v;
        ClipboardManager manager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("text", textView.getText());
        manager.setPrimaryClip(clipData);
    }


    @Override
    public void onChangeMedia() {
        if (player != null) {
            if (controlsView != null) {
                controlsView.setSeekBarStateForAd(false);
            }
            changeMediaIndex++;

            OnMediaLoadCompletion playLoadedEntry = registerToLoadedMediaCallback();

            // startVootOttMediaLoadingProd(playLoadedEntry, mediaArray.get(getRandomNo()), "dash Main");
            if (q % 2 == 0) {
                startMockMediaLoading(playLoadedEntry, 5);
            } else {
                startVootOttMediaLoadingProd(playLoadedEntry, mediaArray.get(getRandomNo()), "dash Main");
            }
            q++;

           /* if (changeMediaIndex % 4 == 0) {
                startVootOttMediaLoadingProd(playLoadedEntry, mediaArray.get(getRandomNo()), "dash Main");

            } else if (changeMediaIndex % 4 == 1) {
                startVootOttMediaLoadingProd(playLoadedEntry, mediaArray.get(getRandomNo()), "dash Main");
            } else if (changeMediaIndex % 4 == 2) {
                startVootOttMediaLoadingProd(playLoadedEntry, mediaArray.get(getRandomNo()), "dash Main");
            } else if (changeMediaIndex % 4 == 3) {
                startVootOttMediaLoadingProd(playLoadedEntry, mediaArray.get(getRandomNo()), "dash Main");


            }*/
        }
    }

    private int getRandomNo() {
        Random r = new Random();
        int low = 1;
        int high = 5;
        return r.nextInt(high-low) + low;
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
        //PlayKitManager.registerPlugins(this, KavaAnalyticsPlugin.factory);
        PlayKitManager.registerPlugins(this, YouboraPlugin.factory);
        //PlayKitManager.registerPlugins(this, TVPAPIAnalyticsPlugin.factory);
        PlayKitManager.registerPlugins(this, PhoenixAnalyticsPlugin.factory);
    }

    @NonNull
    private OnMediaLoadCompletion registerToLoadedMediaCallback() {
        return response -> runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (response.isSuccess()) {
                    PKMediaEntry pkMediaEntry = response.getResponse();
                    //   pkMediaEntry.getSources().get(0).getDrmData().get(0).setScheme(PKDrmParams.Scheme.WidevineClassic);
                    //   pkMediaEntry.getSources().get(0).getDrmData().get(1).setScheme(PKDrmParams.Scheme.WidevineClassic);
                    onMediaLoaded(pkMediaEntry);
                } else {
                    Toast.makeText(MainActivity.this, "failed to fetch media data: " + (response.getError() != null ? response.getError().getMessage() : ""), Toast.LENGTH_LONG).show();
                    log.e("failed to fetch media data: " + (response.getError() != null ? response.getError().getMessage() : " - ")
                            + (response.getError().getCode() != null ? response.getError().getCode() : " - ")
                            + (response.getError().getName() != null ? response.getError().getName() : ""));
                }
            }
        });
    }

    /*private void initDrm() {
        MediaSupport.initializeDrm(this, (supportedDrmSchemes, isHardwareDrmSupported, provisionPerformed, provisionError) -> {
            log.e("DRM ", " isHardwareDrmSupported = "+isHardwareDrmSupported);
            if (provisionPerformed) {
                if (provisionError != null) {
                    log.e("DRM Provisioning failed", provisionError);
                } else {
                    log.d("DRM Provisioning succeeded");
                }
            }
            log.d("DRM initialized; supported: " + supportedDrmSchemes);

            // Now it's safe to look at `supportedDrmSchemes`
        });
    }*/

    private void initDrm() {
        MediaSupport.initializeDrm(this, (pkDeviceSupportInfo, provisionError) -> {
            log.e("DRM isHardwareDrmSupported = "+ pkDeviceSupportInfo.isHardwareDrmSupported());
            log.e("DRM isHardwareHevcSupported = "+ pkDeviceSupportInfo.isHardwareHevcSupported());
            log.e("DRM isSoftwareHevcSupported = "+ pkDeviceSupportInfo.isSoftwareHevcSupported());
            if (pkDeviceSupportInfo.isProvisionPerformed()) {
                if (provisionError != null) {
                    log.e("DRM Provisioning failed", provisionError);
                } else {
                    log.d("DRM Provisioning succeeded");
                }
            }
            log.d("DRM initialized; supported: " + pkDeviceSupportInfo.getSupportedDrmSchemes());

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
        new KalturaOvpMediaProvider()
                .setSessionProvider(new SimpleSessionProvider("https://cdnapisec.kaltura.com", 1982541, ""))
                .setEntryId("0_2xra7jko")
                .load(completion);
    }

    private void startSimpleOvpMediaLoadinExtSubtitle(OnMediaLoadCompletion completion) {
        new KalturaOvpMediaProvider()
                .setSessionProvider(new SimpleSessionProvider("http://qa-apache-php7.dev.kaltura.com/", 1091, ""))
                .setEntryId("0_dod3wrta")
                .load(completion);
    }

    private void startSimpleOvpMediaLoadingChromecast(OnMediaLoadCompletion completion) {
        new KalturaOvpMediaProvider()
                .setSessionProvider(new SimpleSessionProvider("http://qa-apache-php7.dev.kaltura.com/", 1091, ""))
                .setEntryId("0_1l9q18gy")
                .load(completion);
    }

    private void startSimpleOvpMediaLoadingHlsSubtitle(OnMediaLoadCompletion completion) {
        new KalturaOvpMediaProvider()
                .setSessionProvider(new SimpleSessionProvider("http://cdntesting.qa.mkaltura.com", 1091, ""))
                .setEntryId("0_hut6q26s")
                .load(completion);
    }

    private void startSimpleOvpMediaLoadingHEVC(OnMediaLoadCompletion completion) {
        new KalturaOvpMediaProvider()
                .setSessionProvider(new SimpleSessionProvider("https://cdnapisec.kaltura.com", 2215841, null))
                .setEntryId("1_zhpdyrr2")
                .load(completion);
    }

    private void startSimpleOvpMediaLoadingHEVCKarin(OnMediaLoadCompletion completion) {
        new KalturaOvpMediaProvider()
                .setSessionProvider(new SimpleSessionProvider("https://cdnapisec.kaltura.com", 2215841, null))
                .setEntryId("0_axrfacp3")
                .load(completion);
    }

    private void startSimpleOvpMediaLoadingDRM(OnMediaLoadCompletion completion) {
        new KalturaOvpMediaProvider()
                .setSessionProvider(new SimpleSessionProvider("https://cdnapisec.kaltura.com", 2222401, null))
                .setEntryId("1_f93tepsn")//("1_asoyc5ef") //("1_uzea2uje")
                .load(completion);
    }

    private void startOVPVootKids(OnMediaLoadCompletion completion) {
        new KalturaOvpMediaProvider()
                .setSessionProvider(new SimpleSessionProvider("https://cdnapisec.kaltura.com", 1982541, null))
                .setEntryId("1_xph75b0x")//("1_asoyc5ef") //("1_uzea2uje")
                .load(completion);
    }

    private void startSimpleOvpMediaLoadingVR(OnMediaLoadCompletion completion) {
        new KalturaOvpMediaProvider()
                .setSessionProvider(new SimpleSessionProvider("https://cdnapisec.kaltura.com", 2196781, null))
                .setEntryId("1_afvj3z0u")//("1_asoyc5ef") //("1_uzea2uje")
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
                .setSessionProvider(new SimpleSessionProvider("http://qa-apache-php7.dev.kaltura.com/", 1091, "NGExOGE0ODQ4MDA0Zjk2ZmIwNzJiNGMwOTM1ZjA5NzQ3ZWNhOTQ3Y3wxMDkxOzEwOTE7MTYwNjg5NDI2ODswOzE2MDY4MDc4NjguMzUxMzswO3ZpZXc6Kix3aWRnZXQ6MTs7"))
                .setEntryId("0_nwkp7jtx")
                .load(completion);
    }

    private void startSimpleOvpMediaLoadingLive1(OnMediaLoadCompletion completion) {
        new KalturaOvpMediaProvider()
                .setSessionProvider(new SimpleSessionProvider("https://cdnapisec.kaltura.com/", 1740481, null))
                .setEntryId("1_fdv46dba")
                .load(completion);
    }

    private void startMockMediaLoading(OnMediaLoadCompletion completion, int val) {
        if (val == 1) {
            mediaProvider = new MockMediaProvider("mockfiles/entries.playkit.json", getApplicationContext(), "widevine_L1");
        } else if (val == 2) {
            mediaProvider = new MockMediaProvider("mockfiles/entries.playkit.json", getApplicationContext(), "Zee5");
        } else if (val == 3) {
            mediaProvider = new MockMediaProvider("mockfiles/entries.playkit.json", getApplicationContext(), "LL-1");
        } else if (val == 4) {
            mediaProvider = new MockMediaProvider("mockfiles/entries.playkit.json", getApplicationContext(), "LL-2");
        } else if (val == 5) {
            mediaProvider = new MockMediaProvider("mockfiles/entries.playkit.json", getApplicationContext(), "HLS-BP");
        } else if (val == 6) {
            mediaProvider = new MockMediaProvider("mockfiles/entries.playkit.json", getApplicationContext(), "HLS-APPLE");
        } else {
            mediaProvider = new MockMediaProvider("mockfiles/entries.playkit.json", getApplicationContext(), "hls");
        }
        mediaProvider.load(completion);
    }

    private void startOttMediaLoading(final OnMediaLoadCompletion completion) {
        SessionProvider ksSessionProvider = new SimpleSessionProvider(MockParams.PhoenixBaseUrlUS, MockParams.OttPartnerIdTest, null);
        mediaProvider = new PhoenixMediaProvider()
                .setSessionProvider(ksSessionProvider)
                .setAssetId(MediaIdTest)
                .setAssetType(APIDefines.KalturaAssetType.Media)
                .setAssetReferenceType(APIDefines.AssetReferenceType.Media)
                .setContextType(APIDefines.PlaybackContextType.Playback)
                .setProtocol(PhoenixMediaProvider.HttpProtocol.All)
                .setFormats(FormatTest);
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

    private void startSimpleOvpMediaLoadingMulti(OnMediaLoadCompletion completion) {
        new KalturaOvpMediaProvider("http://qa-apache-php7.dev.kaltura.com/", 1091, null)
                .setEntryId("0_wu32qrt3")
                .load(completion);
    }

    int p = 0;
    int q = 1;
    private void onMediaLoaded(PKMediaEntry mediaEntry) {
        //  APIOkRequestsExecutor.getSingleton().setRequestConfiguration(new RequestConfiguration().setMaxRetries(5).setReadTimeoutMs(15000));
        if (mediaEntry.getMediaType() != PKMediaEntry.MediaEntryType.Vod) {
            START_POSITION = null; // force live streams to play from live edge
        }

        if (p % 2 == 0) {
            mediaEntry.setMediaType(PKMediaEntry.MediaEntryType.Live);
        } else {
            mediaEntry.setMediaType(PKMediaEntry.MediaEntryType.Vod);
        }

        PKMediaConfig mediaConfig = new PKMediaConfig();
//        if (changeMediaIndex % 4 == 0) {
        setExternalSubtitles(mediaEntry, false);
//        } else if (changeMediaIndex % 4 == 1) {
//            setExternalSubtitles(mediaEntry, false);
//        } else if (changeMediaIndex % 4 == 2) {
//            setExternalSubtitles(mediaEntry, true);
//        } else if (changeMediaIndex % 4 == 3) {
//               setExternalSubtitles(mediaEntry, true);
//        }

        mediaConfig.setMediaEntry(mediaEntry).setStartPosition(null);

        PKPluginConfigs pluginConfig = new PKPluginConfigs();
        if (player == null) {

            configurePlugins(pluginConfig);

            player = PlayKitManager.loadPlayer(this, pluginConfig);
            //      KalturaPlaybackRequestAdapter.install(player, "app://PlaykitTestApp"); // in case app developer wants to give customized referrer instead the default referrer in the playmanifest
            //      KalturaUDRMLicenseRequestAdapter.install(player, "app://PlaykitTestApp");


            DRMAdapter.customData = "PEtleU9TQXV0aGVudGljYXRpb25YTUw+PERhdGE+PEdlbmVyYXRpb25UaW1lPjIwMjEtMDEtMjAgMTI6MTM6NTYuMTA3PC9HZW5lcmF0aW9uVGltZT48RXhwaXJhdGlvblRpbWU+MjAyMS0wMS0yMiAxMjoxMzo1Ni4xMDc8L0V4cGlyYXRpb25UaW1lPjxVbmlxdWVJZD40YjVjYWUzZDU3MWQ0ZjU4OGVhOWRhMTczZDk3MjUzYTwvVW5pcXVlSWQ+PFJTQVB1YktleUlkPmIxOWQ0MjJkODkwNjQ0ZDUxMTJkMDg0NjljMmU1OTQ2PC9SU0FQdWJLZXlJZD48V2lkZXZpbmVQb2xpY3kgZmxfQ2FuUGxheT0idHJ1ZSIgZmxfQ2FuUGVyc2lzdD0idHJ1ZSI+PExpY2Vuc2VEdXJhdGlvbj4xNzI4MDA8L0xpY2Vuc2VEdXJhdGlvbj48UGxheWJhY2tEdXJhdGlvbj4xNzI4MDA8L1BsYXliYWNrRHVyYXRpb24+PC9XaWRldmluZVBvbGljeT48V2lkZXZpbmVDb250ZW50S2V5U3BlYyBUcmFja1R5cGU9IkhEIj48U2VjdXJpdHlMZXZlbD4xPC9TZWN1cml0eUxldmVsPjwvV2lkZXZpbmVDb250ZW50S2V5U3BlYz48RmFpclBsYXlQb2xpY3kgcGVyc2lzdGVudD0idHJ1ZSI+PFBlcnNpc3RlbmNlU2Vjb25kcz4xNzI4MDA8L1BlcnNpc3RlbmNlU2Vjb25kcz48L0ZhaXJQbGF5UG9saWN5PjxMaWNlbnNlIHR5cGU9InNpbXBsZSI+PFBvbGljeT48SWQ+ZjJjYWZjNGUtNmE0OC00ZTQ5LTkwMTEtYWZiMGMwOWI3ZTQxPC9JZD48L1BvbGljeT48UGxheT48SWQ+M2QxNDYwYzYtNzM5OC00YjUwLWFlODEtMDAyZjcwMmRjZTU1PC9JZD48L1BsYXk+PC9MaWNlbnNlPjxQb2xpY3kgaWQ9ImYyY2FmYzRlLTZhNDgtNGU0OS05MDExLWFmYjBjMDliN2U0MSIgcGVyc2lzdGVudD0idHJ1ZSI+PEV4cGlyYXRpb25BZnRlckZpcnN0UGxheT4xNzI4MDA8L0V4cGlyYXRpb25BZnRlckZpcnN0UGxheT48TWluaW11bVNlY3VyaXR5TGV2ZWw+MjAwMDwvTWluaW11bVNlY3VyaXR5TGV2ZWw+PC9Qb2xpY3k+PFBsYXkgaWQ9IjNkMTQ2MGM2LTczOTgtNGI1MC1hZTgxLTAwMmY3MDJkY2U1NSI+PE91dHB1dFByb3RlY3Rpb25zPjxPUEw+PENvbXByZXNzZWREaWdpdGFsQXVkaW8+MzAwPC9Db21wcmVzc2VkRGlnaXRhbEF1ZGlvPjxVbmNvbXByZXNzZWREaWdpdGFsQXVkaW8+MzAwPC9VbmNvbXByZXNzZWREaWdpdGFsQXVkaW8+PENvbXByZXNzZWREaWdpdGFsVmlkZW8+NTAwPC9Db21wcmVzc2VkRGlnaXRhbFZpZGVvPjxVbmNvbXByZXNzZWREaWdpdGFsVmlkZW8+MzAwPC9VbmNvbXByZXNzZWREaWdpdGFsVmlkZW8+PEFuYWxvZ1ZpZGVvPjIwMDwvQW5hbG9nVmlkZW8+PC9PUEw+PC9PdXRwdXRQcm90ZWN0aW9ucz48RW5hYmxlcnM+PElkPjc4NjYyN2Q4LWMyYTYtNDRiZS04Zjg4LTA4YWUyNTViMDFhNzwvSWQ+PElkPmQ2ODUwMzBiLTBmNGYtNDNhNi1iYmFkLTM1NmYxZWEwMDQ5YTwvSWQ+PElkPjAwMmY5NzcyLTM4YTAtNDNlNS05Zjc5LTBmNjM2MWRjYzYyYTwvSWQ+PC9FbmFibGVycz48L1BsYXk+PC9EYXRhPjxTaWduYXR1cmU+UkxPTDhmNXdNRzhtMTVpcFhRUGpyVDF2UC9UYzVodUg4UTFJUjU1Tmp5TG51UHVramhxU3QwTWh3dURzdzFhSERpNW5qK3M1ZlF5Vm5STm01V1JrWVcvbmdzeHJ2Ung1WDgvalU0OGUvNm5GMHpybXdDOWR3MUVYTGhqZDQrWnZVUzFHSUhXNzdFQk5FRG84WUlCRWNnbDl0QWlxczhNbjk4Z0FoLzcydS93UFVhZXRIL09mUXRFSVVDeG5pVkpRcXA1NjNuRDdYT042OWg5U0I0NzBremRWNU8xWVVpRzJvOVpIY2R3OVFUUVdEMlZkQThxYUVHVnNBUzRQSjE0by9LSHc5K1JnY09xdU9PMUgrRS9HWE53TkJsM0wvOUoycHJpcUROMjNYVFh4MnliMkYraFI0cW1mYkxIRks2RER4aDJiWFhOWHRnNDVWSHlNVVB3bGJRPT08L1NpZ25hdHVyZT48L0tleU9TQXV0aGVudGljYXRpb25YTUw+";
            final DRMAdapter licenseRequestAdapter = new DRMAdapter();
            //  player.getSettings().setLicenseRequestAdapter(licenseRequestAdapter);
            player.getSettings().setAllowCrossProtocolRedirect(true);
            //     player.getSettings().setPlayReadyPlayback(false);
            //     player.getSettings().forceWidevineL3Playback(true);
            // player.getSettings().setPreferredMediaFormat(PKMediaFormat.hls);
            //Gourav
            //   player.getSettings().setSubtitlePreference(PKSubtitlePreference.EXTERNAL);
//            player.getSettings().enableDecoderFallback(true);
//            player.getSettings().setPreferredVideoCodecSettings(new VideoCodecSettings().setAllowSoftwareDecoder(true));
            //   player.getSettings().setPreferredAudioCodecSettings(new AudioCodecSettings().setAllowMixedCodecs(true));

            // Audio Player Configuration
            //   player.getSettings().setAudioPlayerMode(true, createNotificationForAudioOnlyService());

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

            pkSubtitlePosition.setPosition( 0, 90, Layout.Alignment.ALIGN_CENTER);
            subtitleStyleSettings.setSubtitlePosition(pkSubtitlePosition);
            useSubtitleStyle(true, subtitleStyleSettings);

            player.getSettings().setSecureSurface(false);
            //  player.getSettings().forceSinglePlayerEngine(true);
            // player.getSettings().setPreferredMediaFormat(PKMediaFormat.dash);
            // player.getSettings().setAdAutoPlayOnResume(true);
            player.getSettings().setABRSettings(new ABRSettings().setMaxVideoBitrate(800000));
            //    player.getSettings().setPreferredVideoCodecSettings(new VideoCodecSettings().setCodecPriorityList(Collections.singletonList(PKVideoCodec.HEVC)));
            // player.getSettings().setAllowCrossProtocolRedirect(true);
            //player.getSettings().setPlayerBuffers(new LoadControlBuffers());
            // player.getSettings().enableDecoderFallback(true);
            //player.setPlaybackRate(1.5f);
            //    player.getSettings().setAdAutoPlayOnResume(true);
            //   player.getSettings().setMaxVideoBitrate(878786);
            //   player.getSettings().setMaxVideoSize(new PKMaxVideoSize(640,360));



            log.d("Player: " + player.getClass());
            addPlayerListeners(progressBar);

            FrameLayout layout = (FrameLayout) findViewById(R.id.player_view);
            layout.addView(player.getView());

            controlsView = (PlaybackControlsView) this.findViewById(R.id.playerControls);
            controlsView.setUiListener(this);
            controlsView.setPlayer(player);
            initSpinners();
        } else {
            if (changeMediaIndex % 4 == 0) {
                if (isAdsEnabled) {
                    if (isDAIMode) {
                        promptMessage(DAI_PLUGIN, getDAIConfig2().getAssetTitle());
                        player.updatePluginConfig(IMADAIPlugin.factory.getName(), getDAIConfigISSUE());
                    } else {
                        log.d("Play Ad preMidPostAdTagUrl");
                        promptMessage(IMA_PLUGIN, "preMidPostAdTagUrl");
                        player.updatePluginConfig(IMAPlugin.factory.getName(), getAdsConfig(AD_GOOGLE_SEARCH).setCompanionAdConfig(companionAdSlot, 300, 250));
                    }
                }
                player.updatePluginConfig(YouboraPlugin.factory.getName(), getYouboraJsonObject(false, "preMidPostAdTagUrl media2"));
                player.updatePluginConfig(KavaAnalyticsPlugin.factory.getName(),  getKavaAnalyticsConfig(2222401, "1_f93tepsn"));

            } else if (changeMediaIndex % 4 == 1) {
                if (isAdsEnabled) {
                    if (isDAIMode) {
                        promptMessage(DAI_PLUGIN, getDAIConfig10().getAssetTitle());
                        player.updatePluginConfig(IMADAIPlugin.factory.getName(), getDAIConfigISSUE());
                    } else {
                        log.d("Play Ad inLinePreAdTagUrl");
                        promptMessage(IMA_PLUGIN, "inLinePreAdTagUrl");
                        player.updatePluginConfig(IMAPlugin.factory.getName(), getAdsConfig(preMidPostSingleAdTagUrl).setCompanionAdConfig(companionAdSlot, 300, 250));
                    }
                }
                player.updatePluginConfig(YouboraPlugin.factory.getName(), getYouboraJsonObject(true, "inLinePreAdTagUrl media3"));
                player.updatePluginConfig(KavaAnalyticsPlugin.factory.getName(),  getKavaAnalyticsConfig(1740481, "1_fdv46dba"));
            } if (changeMediaIndex % 4 == 2) {
                if (isAdsEnabled) {
                    if (isDAIMode) {
                        promptMessage(DAI_PLUGIN, getDAIConfig8().getAssetTitle());
                        player.updatePluginConfig(IMADAIPlugin.factory.getName(), getDAIConfigISSUE());
                    } else {
                        log.d("Play NO Ad");
                        promptMessage(IMA_PLUGIN, "Enpty AdTag");
                        player.updatePluginConfig(IMAPlugin.factory.getName(), getAdsConfig(Kaltura_Skippable));
                    }
                }
                player.updatePluginConfig(YouboraPlugin.factory.getName(), getYouboraJsonObject(false, "NO AD media4"));
                player.updatePluginConfig(KavaAnalyticsPlugin.factory.getName(),  getKavaAnalyticsConfig(1091, "0_wu32qrt3"));
            } if (changeMediaIndex % 4 == 3) {
                if (isAdsEnabled) {
                    if (isDAIMode) {
                        promptMessage(DAI_PLUGIN, getDAIConfig5().getAssetTitle());
                        player.updatePluginConfig(IMADAIPlugin.factory.getName(), getDAIConfigISSUE());
                    } else {
                        log.d("Play Ad preSkipAdTagUrl");
                        promptMessage(IMA_PLUGIN, "preSkipAdTagUrl");
                        player.updatePluginConfig(IMAPlugin.factory.getName(), getAdsConfig(AD_GOOGLE_SEARCH).setCompanionAdConfig(companionAdSlot, 300, 250));
                    }
                }

                player.getSettings().setPlayerBuffers(new LoadControlBuffers().
                        setMinPlayerBufferMs(2500).
                        setMaxPlayerBufferMs(50000).setAllowedVideoJoiningTimeMs(4000));

                player.updatePluginConfig(YouboraPlugin.factory.getName(), getYouboraJsonObject(false, "preSkipAdTagUrl media1"));
                player.updatePluginConfig(KavaAnalyticsPlugin.factory.getName(),  getKavaAnalyticsConfig(1734751, "1_3o1seqnv"));
            }
            String ks = "djJ8MTk4fHFftqeAPxdlLVzZBk0Et03Vb8on1wLsKp7cbOwzNwfOvpgmOGnEI_KZDhRWTS-76jEY7pDONjKTvbWyIJb5RsP4NL4Ng5xuw6L__BeMfLGAktkVliaGNZq9SXF5n2cMYX-sqsXLSmWXF9XN89io7-k=";
            PhoenixAnalyticsConfig phoenixAnalyticsConfig = new PhoenixAnalyticsConfig(198, "http://api-preprod.ott.kaltura.com/v4_2/api_v3/", ks, 30);
            player.updatePluginConfig(PhoenixAnalyticsPlugin.factory.getName(), phoenixAnalyticsConfig);
        }

        /*if (p % 2 == 0) {
            player.getSettings().setPKLowLatencyConfig(pkLowLatencyConfig
                    .setTargetOffsetMs(15000L).setMaxOffsetMs(12000L).setMaxPlaybackSpeed(4.0f));

        } else {
            player.getSettings().setPKLowLatencyConfig(pkLowLatencyConfig.setTargetOffsetMs(Consts.TIME_UNSET).setMaxOffsetMs(Consts.TIME_UNSET).setMaxPlaybackSpeed(Consts.RATE_UNSET));
        }
        p++;*/

        Map<String,String> headers = new HashMap<>();
        headers.put("aaa", "bbb");
        headers.put("ccc","ddd");

        final MediaAdapter mediaAdapter = new MediaAdapter("app://PlaykitTestApp", player);
        mediaAdapter.customHeaders = headers;
        //  player.getSettings().setContentRequestAdapter(mediaAdapter);

        //  player.setPlaybackRate(2f);
        player.prepare(mediaConfig);
        player.play();
    }

    static class MediaAdapter implements PKRequestParams.Adapter {

        private static final String CLIENT_TAG = "customAdapter" ;
        private final String applicationName;
        private String playSessionId;
        public Map<String,String> customHeaders;

        public static void install(Player player, String applicationName) {
            if (player.getSettings() instanceof PlayerSettings &&
                    ((PlayerSettings)player.getSettings()).getContentRequestAdapter() != null &&
                    TextUtils.isEmpty(applicationName)) {
                applicationName = ((PlayerSettings)player.getSettings()).getContentRequestAdapter().getApplicationName();
            }

            MediaAdapter decorator = new MediaAdapter(applicationName, player);
            player.getSettings().setContentRequestAdapter(decorator);
        }

        private MediaAdapter(String applicationName, Player player) {
            this.applicationName = applicationName;
            updateParams(player);
        }

        @NonNull
        @Override
        public PKRequestParams adapt(PKRequestParams requestParams) {
            Uri url = requestParams.url;

            if (url != null && url.getPath().contains("/playManifest/")) {
                Uri alt = url.buildUpon()
                        .appendQueryParameter("clientTag", CLIENT_TAG)
                        .appendQueryParameter("playSessionId", playSessionId).build();

                if (!TextUtils.isEmpty(applicationName)) {
                    alt = alt.buildUpon().appendQueryParameter("referrer", toBase64(applicationName.getBytes())).build();
                }

                String lastPathSegment = requestParams.url.getLastPathSegment();
                if (lastPathSegment != null && lastPathSegment.endsWith(".wvm")) {
                    // in old android device it will not play wvc if url is not ended in wvm
                    alt = alt.buildUpon().appendQueryParameter("name", lastPathSegment).build();
                }

                setCustomHeaders(requestParams);
                return new PKRequestParams(alt, requestParams.headers);
            }
            setCustomHeaders(requestParams);
            return requestParams;
        }

        private void setCustomHeaders(PKRequestParams requestParams) {
            if (customHeaders != null && !customHeaders.isEmpty()) {
                for (Map.Entry<String, String> entry : customHeaders.entrySet()) {
                    requestParams.headers.put(entry.getKey(), entry.getValue());
                }
            }
        }

        @Override
        public void updateParams(Player player) {
            this.playSessionId = player.getSessionId();
        }

        @Override
        public String getApplicationName() {
            return applicationName;
        }
    }

    private void startWOW(final OnMediaLoadCompletion completion) {
        String KS = "djJ8MzA3OXwg98BCP1hSFcm0dMQHtqFHc8UmirTKFTcOY_wTj_ftVDP09KEE5HY2nwVNtHgwBUoiYTQ7h6OHdFOX1ORRnVZ_04ldLbSWhYnsMksuc1Rh9EWLsHu-LokYPq9cCY2hmx9Iq3G0P81KGKOiqc3LpPrWxuH07fQ920ULbHx6xSNFnldal3kR9TAN-Hbnd17VDwxIMshNKTJz9quLx6vNTSJFQQ2eqQn3qMgwjfgkaMd-PSmX_Uhh3BGZFBiPzC7KqEYMx5CV1o8YZQzT_SRnST7CVh6RZkGyMsbGq3h62ZYlUMHWAxEUKIP84yYTwPNRFiQmIr5tgRHyoswRk9NncSXp21leQAQKU6NfLRN6lSv2MR3ATLv2M5jS3a5PgUJ1FvpiWBTW6haqGbwnseJppUg7PVBzh1D68U0dV6SPISHZnA==";
        String mediaId = "4439843";
        mediaProvider = new PhoenixMediaProvider("https://rest-us.ott.kaltura.com/api_v3/", 3079, KS)
                .setAssetId(mediaId)
                .setFormats("BP_VOD_Dash")
                .setProtocol(PhoenixMediaProvider.HttpProtocol.Http)
                .setContextType(APIDefines.PlaybackContextType.Playback)
                .setAssetType(APIDefines.KalturaAssetType.Media);
        // .setPKStreamerType(APIDefines.KalturaStreamerType.Mpegdash);

        mediaProvider.load(completion);
    }

    private void startEmiritus(final OnMediaLoadCompletion completion) {
        String KS = "djJ8MzIwOHxcsKh3h1zqX_foOl6oA026WtmBo4NV3Bh5V80fCdC0tJRmJGyp7yplkRowNYCaW2LX5h52YXsiPUot8qJcYOKuGGQD73shLXRArRmJ1H285Hi0ilJDvN5YqPJ_mASRhmWIDyZrnpuDoAaTtaB9i92ZFkpUuaeQwIPkPX13USabp0QCVkBbnTZDoOzXOuI8a-4bcMFb93gngVhkvVb9hADtkn6ULolA4QuSw2AU_I_XErI2wS3xedgOkhEzPVh_cYd8zjAmGGpQrcKxfsrto_mc014bWvEI7g1KY12pekMKCSHRz0qjVvYO2kqbGdKubIuLBxEYkFspAudS8mDr4Y8JNO-59cNkHFfwr2XVDshnd4VmRYn8-ohO2KCQZtASYnfHtPSF8iBLMSnSgqrtNtXw";
        String mediaId = "338548";
        mediaProvider = new PhoenixMediaProvider("https://rest-sgs1.ott.kaltura.com/api_v3/", 3208, KS)
                .setAssetId(mediaId)
                .setFileIds("800396")
                .setProtocol(PhoenixMediaProvider.HttpProtocol.Https)
                .setContextType(APIDefines.PlaybackContextType.Playback)
                .setAssetType(APIDefines.KalturaAssetType.Media);
        // .setPKStreamerType(APIDefines.KalturaStreamerType.Mpegdash);

        mediaProvider.load(completion);
    }

    private void useSubtitleStyle(boolean setOrUpdate, SubtitleStyleSettings subtitleStyleSettings) {
        if (setOrUpdate) {
            player.getSettings().setSubtitleStyle(subtitleStyleSettings);
        } else {
            player.updateSubtitleStyle(subtitleStyleSettings);
        }
    }

    private void initSpinners() {
        videoSpinner = this.findViewById(R.id.videoSpinner);
        audioSpinner = this.findViewById(R.id.audioSpinner);
        textSpinner =  this.findViewById(R.id.subtitleSpinner);

        textSpinner.setOnItemSelectedListener(this);
        audioSpinner.setOnItemSelectedListener(this);
        videoSpinner.setOnItemSelectedListener(this);
    }

    public Notification createNotificationForAudioOnlyService() {
        Notification notification = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String NOTIFICATION_CHANNEL_ID = "com.playkit";
            String channelName = "AudioOnlyService";

            NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_HIGH);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(chan);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);

            notification = notificationBuilder.setOngoing(true)
                    .setSmallIcon(android.R.drawable.ic_media_play)
                    .setContentTitle("My Audio Service")
                    .setPriority(NotificationManager.IMPORTANCE_HIGH)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .build();
        }

        return notification;
    }

    private void configurePlugins(PKPluginConfigs pluginConfigs) {
        if (isAdsEnabled) {
            if (isDAIMode) {
                addIMADAIPluginConfig(pluginConfigs, 2);
            } else {
                addIMAPluginConfig(pluginConfigs);
            }
        }
        //addKaluraStatsPluginConfig(pluginConfigs, 1734751, "1_3o1seqnv");
        addYouboraPluginConfig(pluginConfigs, false, "preMidPostSingleAdTagUrl Title1");
        addKavaPluginConfig(pluginConfigs, 1734751, "1_3o1seqnv");
        addPhoenixAnalyticsPluginConfig(pluginConfigs);
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
                .setUserId("gourav@gmail.com")
                .setEntryId(ovpEntryId)
                .setDvrThreshold(DISTANCE_FROM_LIVE_THRESHOLD);
    }

    private void addYouboraPluginConfig(PKPluginConfigs pluginConfigs, boolean isLive, String title) {
        JsonObject pluginEntry = getYouboraJsonObject(isLive, title);

        //Set plugin entry to the plugin configs.
        pluginConfigs.setPluginConfig(YouboraPlugin.factory.getName(), getYouboraBundle());
    }

    @NonNull
    private JsonObject getYouboraJsonObject(boolean isLive, String title) {
        JsonObject pluginEntry = new JsonObject();

        pluginEntry.addProperty("accountCode", "kalturatest");
        pluginEntry.addProperty("username", "gourav@gmail.com");
        pluginEntry.addProperty("haltOnError", true);
        pluginEntry.addProperty("enableAnalytics", true);
        pluginEntry.addProperty("enableSmartAds", true);


        //Optional - Device json o/w youbora will decide by its own.
        JsonObject deviceJson = new JsonObject();
        deviceJson.addProperty("deviceCode", "AndroidTV");
        deviceJson.addProperty("brand", "Xiaomi");
        deviceJson.addProperty("id", "Gourav-MotoG6-Unique-Id");
        deviceJson.addProperty("model", "Mii3");
        deviceJson.addProperty("type", "TvBox");
        deviceJson.addProperty("osName", "Android/Oreo");
        deviceJson.addProperty("osVersion", "8.1");


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
        extraParamJson.addProperty("param1", "Gourav-Custom-Param-1");
        extraParamJson.addProperty("param2", "Gourav-Custom-Param-2");
        extraParamJson.addProperty("param3", "Gourav-Custom-Param-3");

        //Add all the json objects created before to the pluginEntry json.
        pluginEntry.add("device", deviceJson);
        pluginEntry.add("media", mediaEntryJson);
        pluginEntry.add("ads", adsJson);
        pluginEntry.add("properties", propertiesJson);
        pluginEntry.add("extraParams", extraParamJson);
        return pluginEntry;
    }

    /**
     * Youbora options Bundle (Recommended)
     *
     * Will create {@link PKPluginConfigs} object with {@link YouboraPlugin}.
     *
     * @return - the pluginConfig object that should be passed as parameter when loading the player.
     */
    private Bundle getYouboraBundle() {
        //Initialize Json object that will hold all the configurations for the plugin.

        Bundle optBundle = new Bundle();

        //Youbora config bundle. Main config goes here.
        optBundle.putString(KEY_ACCOUNT_CODE, ACCOUNT_CODE);
        optBundle.putString(KEY_USERNAME, UNIQUE_USER_NAME);
        optBundle.putString(KEY_USER_EMAIL, UNIQUE_USER_NAME);
        optBundle.putString(KEY_APP_NAME, "TestApp");
        optBundle.putString(KEY_APP_RELEASE_VERSION, "v1.0");
        optBundle.putBoolean(KEY_ENABLED, true);

        //Media entry bundle.
        optBundle.putString(KEY_CONTENT_TITLE, MEDIA_TITLE);

        //Optional - Device bundle o/w youbora will decide by its own.
        optBundle.putString(KEY_DEVICE_CODE, DEVICE_CODE);
        optBundle.putString(KEY_DEVICE_BRAND, "Xiaomi");
        optBundle.putString(KEY_DEVICE_MODEL, "Mii3");
        optBundle.putString(KEY_DEVICE_TYPE, "TvBox");
        optBundle.putString(KEY_DEVICE_OS_NAME, "Android/Oreo");
        optBundle.putString(KEY_DEVICE_OS_VERSION, "8.1");

        //Youbora ads configuration bundle.
        optBundle.putString(KEY_AD_CAMPAIGN, CAMPAIGN);

        optBundle.putString(KEY_HOUSEHOLD_ID, "householdId");

        //Configure custom properties here:
        optBundle.putString(KEY_CONTENT_GENRE, GENRE);
        optBundle.putString(KEY_CONTENT_TYPE, TYPE);
        optBundle.putString(KEY_CONTENT_TRANSACTION_CODE, TRANSACTION_TYPE); // NEED TO CHECK
        optBundle.putString(KEY_CONTENT_CDN, CONTENT_CDN_CODE);

        // Create Content Metadata bundle
        Bundle contentMetaDataBundle = new Bundle();
        contentMetaDataBundle.putString(KEY_CONTENT_METADATA_YEAR, YEAR);
        contentMetaDataBundle.putString(KEY_CONTENT_METADATA_CAST, CAST);
        contentMetaDataBundle.putString(KEY_CONTENT_METADATA_DIRECTOR, DIRECTOR);
        contentMetaDataBundle.putString(KEY_CONTENT_METADATA_OWNER, OWNER);
        contentMetaDataBundle.putString(KEY_CONTENT_METADATA_PARENTAL, PARENTAL);
        contentMetaDataBundle.putString(KEY_CONTENT_METADATA_RATING, RATING);
        contentMetaDataBundle.putString(KEY_CONTENT_METADATA_QUALITY, QUALITY);

        // Add Content Metadata bundle to the main Options bundle
        optBundle.putBundle(KEY_CONTENT_METADATA, contentMetaDataBundle);

        optBundle.putString(KEY_CONTENT_PRICE, PRICE);
        optBundle.putString(KEY_CONTENT_ENCODING_AUDIO_CODEC, AUDIO_TYPE); // NEED TO CHECK
        optBundle.putString(KEY_CONTENT_CHANNEL, AUDIO_CHANNELS);  // NEED TO CHECK

        //You can add some extra params here:
        optBundle.putString(KEY_CUSTOM_DIMENSION_1, EXTRA_PARAM_1);
        optBundle.putString(KEY_CUSTOM_DIMENSION_2, EXTRA_PARAM_2);

        return optBundle;
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
        // IMAConfig adsConfig = getAdsConfigResponsePostrollBuggy(ads5AdsEvery10Secs);
        IMAConfig adsConfig = getAdsConfig(preMidPostSingleAdTagUrl);
        config.setPluginConfig(IMAPlugin.factory.getName(), adsConfig);
    }

    private IMAConfig getAdsConfig(String adTagUrl) {
        List<String> videoMimeTypes = new ArrayList<>();
        // videoMimeTypes.add("video/mp4");
        // videoMimeTypes.add("application/x-mpegURL");
        // videoMimeTypes.add("application/dash+xml");
        //return getAdsConfigResponse("");
        // return new IMAConfig().setAdTagUrl("https://pubads.g.doubleclick.net/gampad/live/ads?sz=640x360&iu=%2F21633895671%2FQA%2FAndroid_Native_App%2FCOI&impl=s&gdfp_req=1&env=vp&output=vmap&unviewed_position_start=1&cust_params=sample_ar%3Dskippablelinear%26Gender%3DM%26Age%3D33%26KidsPinEnabled%3DN%26distinct_id%3D42c92f17603e4ee2b4232666b9591134%26AppVersion%3D0.1.80%26DeviceModel%3Dmoto%20g(6)%26OptOut%3DFalse%26OSVersion%3D9%26PackageName%3Dcom.tv.v18.viola%26first_time%3DFalse%26logintype%3DTraditional&description_url=https%253A%252F%252Fwww.voot.com&cmsid=2467608&ppid=42c92f17603e4ee2b4232666b9591134&vid=0_im5ianso&ad_rule=1&correlator=10771&InterstitialRendered=False").enableDebugMode(true).setAlwaysStartWithPreroll(true).setAdLoadTimeOut(8);
        return new IMAConfig().setAdTagUrl(adTagUrl).setVideoMimeTypes(videoMimeTypes).enableDebugMode(true).setAlwaysStartWithPreroll(true).setAdLoadTimeOut(8);
    }

    private IMAConfig getAdsConfigResponsePostrollBuggy(String ad) {
        String adResponse = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<vmap:VMAP xmlns:vmap=\"http://www.iab.net/videosuite/vmap\" version=\"1.0\">\n" +
                "    <vmap:AdBreak timeOffset=\"end\" breakType=\"linear\" breakId=\"postroll\">\n" +
                "        <vmap:AdSource id=\"postroll-ad-1\" allowMultipleAds=\"false\" followRedirects=\"true\">\n" +
                "            <vmap:AdTagURI templateType=\"vast3\">\n" +
                "                <![CDATA[https://pubads.ggg.doubleclick.net/gampad/ads?slotname=/124319096/external/ad_rule_samples&sz=640x480&ciu_szs=300x250&cust_params=deployment%3Ddevsite%26sample_ar%3Dpostonly&url=&unviewed_position_start=1&output=xml_vast3&impl=s&env=vp&gdfp_req=1&ad_rule=0&vad_type=linear&vpos=postroll&pod=1&ppos=1&lip=true&min_ad_duration=0&max_ad_duration=30000&vrid=6016&video_doc_id=short_onecue&cmsid=496&kfa=0&tfcd=0]]>\n" +
                "            </vmap:AdTagURI>\n" +
                "        </vmap:AdSource>\n" +
                "    </vmap:AdBreak>\n" +
                "</vmap:VMAP>";
        List<String> videoMimeTypes = new ArrayList<>();
        videoMimeTypes.add("video/mp4");
        videoMimeTypes.add("application/x-mpegURL");
        videoMimeTypes.add("application/dash+xml");
        return new IMAConfig().setAdTagResponse(adResponse).setVideoMimeTypes(videoMimeTypes).setAlwaysStartWithPreroll(true).setAdLoadTimeOut(8).enableDebugMode(true);
    }

    private IMAConfig getAdsConfigResponse(String adResponse) {
        List<String> videoMimeTypes = new ArrayList<>();
        videoMimeTypes.add("video/mp4");
        videoMimeTypes.add("application/x-mpegURL");
        // videoMimeTypes.add("application/dash+xml");
        return new IMAConfig().setAdTagResponse("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<VAST xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"vast.xsd\" version=\"3.0\">\n" +
                "    <Ad id=\"697200496\" sequence=\"1\">\n" +
                "        <InLine>\n" +
                "            <AdSystem>GDFP</AdSystem>\n" +
                "            <AdTitle>External NCA1C1L1 LinearInlineSkippable1</AdTitle>\n" +
                "            <Description>\n" +
                "                <![CDATA[External NCA1C1L1 LinearInlineSkippable ad]]>\n" +
                "            </Description>\n" +
                "            <Error>\n" +
                "                <![CDATA[https://pubads.g.doubleclick.net/pagead/conversion/?ai=BiAmtdpL4XZu4A9Gs3gOzmKbQCJDVj-sGAAAAEAEgqN27JjgAWLCUgsbXAWD5qvSDnBC6AQo3Mjh4OTBfeG1syAEFwAIC4AIA6gIlLzEyNDMxOTA5Ni9leHRlcm5hbC9zaW5nbGVfYWRfc2FtcGxlc_gChNIegAMBkAPIBpgD8AGoAwHgBAHSBQYQ8N65zAKQBgGgBiOoB-zVG6gH89Eb2AcB4AcL0ggHCIBhEAEYHQ&sigh=0Yw_KrP8i6U&label=videoplayfailed[ERRORCODE]]]>\n" +
                "            </Error>\n" +
                "            <Impression>\n" +
                "                <![CDATA[https://securepubads.g.doubleclick.net/pcs/view?xai=AKAOjssbJ-NIPc1thQ4jiqlJtwMcdR0cMzj-DPmwv0ZLNCGVewPk-_-qRRXzhiyRpSGQyfjxUPYMsbx1TfNP73PHwzH5Sa8xTHrSVMK14ibBcEKXIZckAU36SzpqkOsY6HFybbiv8TJ2vqCfWqbkwzVoP3_uC5Co8ODxwzjrFHyrUT99w638gemssuGGLhiE1D1oSENH-b3L_Z4X17n0EQ1Mmma6Y8OywPy5UmRUWZOFHMD-9KJ4z4IaaFCuO7toI-HjzMjKAQ&sig=Cg0ArKJSzO_u2FZZ48G4EAE&adurl=]]>\n" +
                "            </Impression>\n" +
                "            <Creatives>\n" +
                "                <Creative id=\"57860459056\" sequence=\"1\">\n" +
                "                    <Linear skipoffset=\"00:00:05\">\n" +
                "                        <Duration>00:00:10</Duration>\n" +
                "                        <TrackingEvents>\n" +
                "                            <Tracking event=\"start\">\n" +
                "                                <![CDATA[https://pubads.g.doubleclick.net/pagead/conversion/?ai=BiAmtdpL4XZu4A9Gs3gOzmKbQCJDVj-sGAAAAEAEgqN27JjgAWLCUgsbXAWD5qvSDnBC6AQo3Mjh4OTBfeG1syAEFwAIC4AIA6gIlLzEyNDMxOTA5Ni9leHRlcm5hbC9zaW5nbGVfYWRfc2FtcGxlc_gChNIegAMBkAPIBpgD8AGoAwHgBAHSBQYQ8N65zAKQBgGgBiOoB-zVG6gH89Eb2AcB4AcL0ggHCIBhEAEYHQ&sigh=0Yw_KrP8i6U&label=part2viewed&ad_mt=[AD_MT]]]>\n" +
                "                            </Tracking>\n" +
                "                            <Tracking event=\"firstQuartile\">\n" +
                "                                <![CDATA[https://pubads.g.doubleclick.net/pagead/conversion/?ai=BiAmtdpL4XZu4A9Gs3gOzmKbQCJDVj-sGAAAAEAEgqN27JjgAWLCUgsbXAWD5qvSDnBC6AQo3Mjh4OTBfeG1syAEFwAIC4AIA6gIlLzEyNDMxOTA5Ni9leHRlcm5hbC9zaW5nbGVfYWRfc2FtcGxlc_gChNIegAMBkAPIBpgD8AGoAwHgBAHSBQYQ8N65zAKQBgGgBiOoB-zVG6gH89Eb2AcB4AcL0ggHCIBhEAEYHQ&sigh=0Yw_KrP8i6U&label=videoplaytime25&ad_mt=[AD_MT]]]>\n" +
                "                            </Tracking>\n" +
                "                            <Tracking event=\"midpoint\">\n" +
                "                                <![CDATA[https://pubads.g.doubleclick.net/pagead/conversion/?ai=BiAmtdpL4XZu4A9Gs3gOzmKbQCJDVj-sGAAAAEAEgqN27JjgAWLCUgsbXAWD5qvSDnBC6AQo3Mjh4OTBfeG1syAEFwAIC4AIA6gIlLzEyNDMxOTA5Ni9leHRlcm5hbC9zaW5nbGVfYWRfc2FtcGxlc_gChNIegAMBkAPIBpgD8AGoAwHgBAHSBQYQ8N65zAKQBgGgBiOoB-zVG6gH89Eb2AcB4AcL0ggHCIBhEAEYHQ&sigh=0Yw_KrP8i6U&label=videoplaytime50&ad_mt=[AD_MT]]]>\n" +
                "                            </Tracking>\n" +
                "                            <Tracking event=\"thirdQuartile\">\n" +
                "                                <![CDATA[https://pubads.g.doubleclick.net/pagead/conversion/?ai=BiAmtdpL4XZu4A9Gs3gOzmKbQCJDVj-sGAAAAEAEgqN27JjgAWLCUgsbXAWD5qvSDnBC6AQo3Mjh4OTBfeG1syAEFwAIC4AIA6gIlLzEyNDMxOTA5Ni9leHRlcm5hbC9zaW5nbGVfYWRfc2FtcGxlc_gChNIegAMBkAPIBpgD8AGoAwHgBAHSBQYQ8N65zAKQBgGgBiOoB-zVG6gH89Eb2AcB4AcL0ggHCIBhEAEYHQ&sigh=0Yw_KrP8i6U&label=videoplaytime75&ad_mt=[AD_MT]]]>\n" +
                "                            </Tracking>\n" +
                "                            <Tracking event=\"complete\">\n" +
                "                                <![CDATA[https://pubads.g.doubleclick.net/pagead/conversion/?ai=BiAmtdpL4XZu4A9Gs3gOzmKbQCJDVj-sGAAAAEAEgqN27JjgAWLCUgsbXAWD5qvSDnBC6AQo3Mjh4OTBfeG1syAEFwAIC4AIA6gIlLzEyNDMxOTA5Ni9leHRlcm5hbC9zaW5nbGVfYWRfc2FtcGxlc_gChNIegAMBkAPIBpgD8AGoAwHgBAHSBQYQ8N65zAKQBgGgBiOoB-zVG6gH89Eb2AcB4AcL0ggHCIBhEAEYHQ&sigh=0Yw_KrP8i6U&label=videoplaytime100&ad_mt=[AD_MT]]]>\n" +
                "                            </Tracking>\n" +
                "                            <Tracking event=\"mute\">\n" +
                "                                <![CDATA[https://pubads.g.doubleclick.net/pagead/conversion/?ai=BiAmtdpL4XZu4A9Gs3gOzmKbQCJDVj-sGAAAAEAEgqN27JjgAWLCUgsbXAWD5qvSDnBC6AQo3Mjh4OTBfeG1syAEFwAIC4AIA6gIlLzEyNDMxOTA5Ni9leHRlcm5hbC9zaW5nbGVfYWRfc2FtcGxlc_gChNIegAMBkAPIBpgD8AGoAwHgBAHSBQYQ8N65zAKQBgGgBiOoB-zVG6gH89Eb2AcB4AcL0ggHCIBhEAEYHQ&sigh=0Yw_KrP8i6U&label=admute&ad_mt=[AD_MT]]]>\n" +
                "                            </Tracking>\n" +
                "                            <Tracking event=\"unmute\">\n" +
                "                                <![CDATA[https://pubads.g.doubleclick.net/pagead/conversion/?ai=BiAmtdpL4XZu4A9Gs3gOzmKbQCJDVj-sGAAAAEAEgqN27JjgAWLCUgsbXAWD5qvSDnBC6AQo3Mjh4OTBfeG1syAEFwAIC4AIA6gIlLzEyNDMxOTA5Ni9leHRlcm5hbC9zaW5nbGVfYWRfc2FtcGxlc_gChNIegAMBkAPIBpgD8AGoAwHgBAHSBQYQ8N65zAKQBgGgBiOoB-zVG6gH89Eb2AcB4AcL0ggHCIBhEAEYHQ&sigh=0Yw_KrP8i6U&label=adunmute&ad_mt=[AD_MT]]]>\n" +
                "                            </Tracking>\n" +
                "                            <Tracking event=\"rewind\">\n" +
                "                                <![CDATA[https://pubads.g.doubleclick.net/pagead/conversion/?ai=BiAmtdpL4XZu4A9Gs3gOzmKbQCJDVj-sGAAAAEAEgqN27JjgAWLCUgsbXAWD5qvSDnBC6AQo3Mjh4OTBfeG1syAEFwAIC4AIA6gIlLzEyNDMxOTA5Ni9leHRlcm5hbC9zaW5nbGVfYWRfc2FtcGxlc_gChNIegAMBkAPIBpgD8AGoAwHgBAHSBQYQ8N65zAKQBgGgBiOoB-zVG6gH89Eb2AcB4AcL0ggHCIBhEAEYHQ&sigh=0Yw_KrP8i6U&label=adrewind&ad_mt=[AD_MT]]]>\n" +
                "                            </Tracking>\n" +
                "                            <Tracking event=\"pause\">\n" +
                "                                <![CDATA[https://pubads.g.doubleclick.net/pagead/conversion/?ai=BiAmtdpL4XZu4A9Gs3gOzmKbQCJDVj-sGAAAAEAEgqN27JjgAWLCUgsbXAWD5qvSDnBC6AQo3Mjh4OTBfeG1syAEFwAIC4AIA6gIlLzEyNDMxOTA5Ni9leHRlcm5hbC9zaW5nbGVfYWRfc2FtcGxlc_gChNIegAMBkAPIBpgD8AGoAwHgBAHSBQYQ8N65zAKQBgGgBiOoB-zVG6gH89Eb2AcB4AcL0ggHCIBhEAEYHQ&sigh=0Yw_KrP8i6U&label=adpause&ad_mt=[AD_MT]]]>\n" +
                "                            </Tracking>\n" +
                "                            <Tracking event=\"resume\">\n" +
                "                                <![CDATA[https://pubads.g.doubleclick.net/pagead/conversion/?ai=BiAmtdpL4XZu4A9Gs3gOzmKbQCJDVj-sGAAAAEAEgqN27JjgAWLCUgsbXAWD5qvSDnBC6AQo3Mjh4OTBfeG1syAEFwAIC4AIA6gIlLzEyNDMxOTA5Ni9leHRlcm5hbC9zaW5nbGVfYWRfc2FtcGxlc_gChNIegAMBkAPIBpgD8AGoAwHgBAHSBQYQ8N65zAKQBgGgBiOoB-zVG6gH89Eb2AcB4AcL0ggHCIBhEAEYHQ&sigh=0Yw_KrP8i6U&label=adresume&ad_mt=[AD_MT]]]>\n" +
                "                            </Tracking>\n" +
                "                            <Tracking event=\"fullscreen\">\n" +
                "                                <![CDATA[https://pubads.g.doubleclick.net/pagead/conversion/?ai=BiAmtdpL4XZu4A9Gs3gOzmKbQCJDVj-sGAAAAEAEgqN27JjgAWLCUgsbXAWD5qvSDnBC6AQo3Mjh4OTBfeG1syAEFwAIC4AIA6gIlLzEyNDMxOTA5Ni9leHRlcm5hbC9zaW5nbGVfYWRfc2FtcGxlc_gChNIegAMBkAPIBpgD8AGoAwHgBAHSBQYQ8N65zAKQBgGgBiOoB-zVG6gH89Eb2AcB4AcL0ggHCIBhEAEYHQ&sigh=0Yw_KrP8i6U&label=adfullscreen&ad_mt=[AD_MT]]]>\n" +
                "                            </Tracking>\n" +
                "                            <Tracking event=\"creativeView\">\n" +
                "                                <![CDATA[https://pubads.g.doubleclick.net/pagead/conversion/?ai=BiAmtdpL4XZu4A9Gs3gOzmKbQCJDVj-sGAAAAEAEgqN27JjgAWLCUgsbXAWD5qvSDnBC6AQo3Mjh4OTBfeG1syAEFwAIC4AIA6gIlLzEyNDMxOTA5Ni9leHRlcm5hbC9zaW5nbGVfYWRfc2FtcGxlc_gChNIegAMBkAPIBpgD8AGoAwHgBAHSBQYQ8N65zAKQBgGgBiOoB-zVG6gH89Eb2AcB4AcL0ggHCIBhEAEYHQ&sigh=0Yw_KrP8i6U&label=vast_creativeview&ad_mt=[AD_MT]]]>\n" +
                "                            </Tracking>\n" +
                "                            <Tracking event=\"exitFullscreen\">\n" +
                "                                <![CDATA[https://pubads.g.doubleclick.net/pagead/conversion/?ai=BiAmtdpL4XZu4A9Gs3gOzmKbQCJDVj-sGAAAAEAEgqN27JjgAWLCUgsbXAWD5qvSDnBC6AQo3Mjh4OTBfeG1syAEFwAIC4AIA6gIlLzEyNDMxOTA5Ni9leHRlcm5hbC9zaW5nbGVfYWRfc2FtcGxlc_gChNIegAMBkAPIBpgD8AGoAwHgBAHSBQYQ8N65zAKQBgGgBiOoB-zVG6gH89Eb2AcB4AcL0ggHCIBhEAEYHQ&sigh=0Yw_KrP8i6U&label=vast_exit_fullscreen&ad_mt=[AD_MT]]]>\n" +
                "                            </Tracking>\n" +
                "                            <Tracking event=\"acceptInvitationLinear\">\n" +
                "                                <![CDATA[https://pubads.g.doubleclick.net/pagead/conversion/?ai=BiAmtdpL4XZu4A9Gs3gOzmKbQCJDVj-sGAAAAEAEgqN27JjgAWLCUgsbXAWD5qvSDnBC6AQo3Mjh4OTBfeG1syAEFwAIC4AIA6gIlLzEyNDMxOTA5Ni9leHRlcm5hbC9zaW5nbGVfYWRfc2FtcGxlc_gChNIegAMBkAPIBpgD8AGoAwHgBAHSBQYQ8N65zAKQBgGgBiOoB-zVG6gH89Eb2AcB4AcL0ggHCIBhEAEYHQ&sigh=0Yw_KrP8i6U&label=acceptinvitation&ad_mt=[AD_MT]]]>\n" +
                "                            </Tracking>\n" +
                "                            <Tracking event=\"closeLinear\">\n" +
                "                                <![CDATA[https://pubads.g.doubleclick.net/pagead/conversion/?ai=BiAmtdpL4XZu4A9Gs3gOzmKbQCJDVj-sGAAAAEAEgqN27JjgAWLCUgsbXAWD5qvSDnBC6AQo3Mjh4OTBfeG1syAEFwAIC4AIA6gIlLzEyNDMxOTA5Ni9leHRlcm5hbC9zaW5nbGVfYWRfc2FtcGxlc_gChNIegAMBkAPIBpgD8AGoAwHgBAHSBQYQ8N65zAKQBgGgBiOoB-zVG6gH89Eb2AcB4AcL0ggHCIBhEAEYHQ&sigh=0Yw_KrP8i6U&label=adclose&ad_mt=[AD_MT]]]>\n" +
                "                            </Tracking>\n" +
                "                            <Tracking event=\"skip\">\n" +
                "                                <![CDATA[https://pubads.g.doubleclick.net/pagead/conversion/?ai=BiAmtdpL4XZu4A9Gs3gOzmKbQCJDVj-sGAAAAEAEgqN27JjgAWLCUgsbXAWD5qvSDnBC6AQo3Mjh4OTBfeG1syAEFwAIC4AIA6gIlLzEyNDMxOTA5Ni9leHRlcm5hbC9zaW5nbGVfYWRfc2FtcGxlc_gChNIegAMBkAPIBpgD8AGoAwHgBAHSBQYQ8N65zAKQBgGgBiOoB-zVG6gH89Eb2AcB4AcL0ggHCIBhEAEYHQ&sigh=0Yw_KrP8i6U&label=videoskipped&ad_mt=[AD_MT]]]>\n" +
                "                            </Tracking>\n" +
                "                            <Tracking event=\"progress\" offset=\"00:00:05\">\n" +
                "                                <![CDATA[https://pubads.g.doubleclick.net/pagead/conversion/?ai=BiAmtdpL4XZu4A9Gs3gOzmKbQCJDVj-sGAAAAEAEgqN27JjgAWLCUgsbXAWD5qvSDnBC6AQo3Mjh4OTBfeG1syAEFwAIC4AIA6gIlLzEyNDMxOTA5Ni9leHRlcm5hbC9zaW5nbGVfYWRfc2FtcGxlc_gChNIegAMBkAPIBpgD8AGoAwHgBAHSBQYQ8N65zAKQBgGgBiOoB-zVG6gH89Eb2AcB4AcL0ggHCIBhEAEYHQ&sigh=0Yw_KrP8i6U&label=video_skip_shown&ad_mt=[AD_MT]]]>\n" +
                "                            </Tracking>\n" +
                "                            <Tracking event=\"progress\" offset=\"00:00:30\">\n" +
                "                                <![CDATA[https://pubads.g.doubleclick.net/pagead/conversion/?ai=BiAmtdpL4XZu4A9Gs3gOzmKbQCJDVj-sGAAAAEAEgqN27JjgAWLCUgsbXAWD5qvSDnBC6AQo3Mjh4OTBfeG1syAEFwAIC4AIA6gIlLzEyNDMxOTA5Ni9leHRlcm5hbC9zaW5nbGVfYWRfc2FtcGxlc_gChNIegAMBkAPIBpgD8AGoAwHgBAHSBQYQ8N65zAKQBgGgBiOoB-zVG6gH89Eb2AcB4AcL0ggHCIBhEAEYHQ&sigh=0Yw_KrP8i6U&label=video_engaged_view&ad_mt=[AD_MT]]]>\n" +
                "                            </Tracking>\n" +
                "                        </TrackingEvents>\n" +
                "                        <AdParameters>\n" +
                "                            <![CDATA[custom_param=some_value]]>\n" +
                "                        </AdParameters>\n" +
                "                        <VideoClicks>\n" +
                "                            <ClickThrough id=\"GDFP\">\n" +
                "                                <![CDATA[https://pubads.g.doubleclick.net/pcs/click?xai=AKAOjssRtEFo991pR7zwabKsfXwr_aghNnLMOIPnWfUd5qDOvHBP6JBLSaLelz1zulNEXWWR7eAs0jETJWFfaGAymWttftWZcEddHM17nyxbug9-mXgcDMU30TfS5cwe9qbYt8UbkvIVTpA3IcQ6dchlTaIojYu_5lacYvZ3OxpFUtGs0jsFDAHs8zKjUIrQpk66f9kGao8FgTUb3uD5qrBn11QFBaCWZfgmkQK_KUVpYZ-K3xD0qsAEhbaJ-E17Rr4&sig=Cg0ArKJSzPD0XJy5Qe_z&adurl=https://developers.google.com/interactive-media-ads/docs/vastinspector_dual]]>\n" +
                "                            </ClickThrough>\n" +
                "                        </VideoClicks>\n" +
                "                        <MediaFiles>\n" +
                "                            <MediaFile id=\"GDFP\" delivery=\"progressive\" width=\"1280\" height=\"720\" type=\"video/mp4\" bitrate=\"533\" scalable=\"true\" maintainAspectRatio=\"true\">\n" +
                "                                <![CDATA[https://redirector.gvt1.com/videoplayback/id/b96674ee53e47835/itag/15/source/gfp_video_ads/requiressl/yes/acao/yes/mime/video%2Fmp4/ctier/L/ip/0.0.0.0/ipbits/0/expire/1576593110/sparams/ip,ipbits,expire,id,itag,source,requiressl,acao,mime,ctier/signature/A7A30B1D1A7F7BE73A9538FD1B807350967D2AEB.9A38C814EDA47CAC281AED15E39F280599559A30/key/ck2/file/file.mp4]]>\n" +
                "                            </MediaFile>\n" +
                "                            <MediaFile id=\"GDFP\" delivery=\"progressive\" width=\"176\" height=\"144\" type=\"video/3gpp\" bitrate=\"36\" scalable=\"true\" maintainAspectRatio=\"true\">\n" +
                "                                <![CDATA[https://redirector.gvt1.com/videoplayback/id/b96674ee53e47835/itag/17/source/gfp_video_ads/requiressl/yes/acao/yes/mime/video%2F3gpp/ctier/L/ip/0.0.0.0/ipbits/0/expire/1576593110/sparams/ip,ipbits,expire,id,itag,source,requiressl,acao,mime,ctier/signature/704416F60F7B435E72AB4930D2797E711D21D267.AA2B92F31E6FB8B7B68757975771630A1A77AD67/key/ck2/file/file.3gp]]>\n" +
                "                            </MediaFile>\n" +
                "                            <MediaFile id=\"GDFP\" delivery=\"progressive\" width=\"640\" height=\"360\" type=\"video/mp4\" bitrate=\"122\" scalable=\"true\" maintainAspectRatio=\"true\">\n" +
                "                                <![CDATA[https://redirector.gvt1.com/videoplayback/id/b96674ee53e47835/itag/18/source/gfp_video_ads/requiressl/yes/acao/yes/mime/video%2Fmp4/ctier/L/ip/0.0.0.0/ipbits/0/expire/1576593110/sparams/ip,ipbits,expire,id,itag,source,requiressl,acao,mime,ctier/signature/10542A19241D1CC9E11D8AE9FA0E1A6B003C079B.8D328E7408844794F1985FBC9DC53B850E60E8AB/key/ck2/file/file.mp4]]>\n" +
                "                            </MediaFile>\n" +
                "                            <MediaFile id=\"GDFP\" delivery=\"progressive\" width=\"320\" height=\"180\" type=\"video/3gpp\" bitrate=\"74\" scalable=\"true\" maintainAspectRatio=\"true\">\n" +
                "                                <![CDATA[https://redirector.gvt1.com/videoplayback/id/b96674ee53e47835/itag/36/source/gfp_video_ads/requiressl/yes/acao/yes/mime/video%2F3gpp/ctier/L/ip/0.0.0.0/ipbits/0/expire/1576593110/sparams/ip,ipbits,expire,id,itag,source,requiressl,acao,mime,ctier/signature/682F9CB111CA36AB2B72D9CF06B97814C08AEAE9.8867580B16BC64EB7E62725EE281CE93330FD9A1/key/ck2/file/file.3gp]]>\n" +
                "                            </MediaFile>\n" +
                "                            <MediaFile id=\"GDFP\" delivery=\"progressive\" width=\"640\" height=\"360\" type=\"video/webm\" bitrate=\"125\" scalable=\"true\" maintainAspectRatio=\"true\">\n" +
                "                                <![CDATA[https://redirector.gvt1.com/videoplayback/id/b96674ee53e47835/itag/43/source/gfp_video_ads/requiressl/yes/acao/yes/mime/video%2Fwebm/ctier/L/ip/0.0.0.0/ipbits/0/expire/1576593110/sparams/ip,ipbits,expire,id,itag,source,requiressl,acao,mime,ctier/signature/6D73F2F257A32FE7EE3833AC95A2E0F83B26D30E.8AC48D55F95E5E06CE97F79999D43FCFBE58598A/key/ck2/file/file.webm]]>\n" +
                "                            </MediaFile>\n" +
                "                            <MediaFile id=\"GDFP\" delivery=\"progressive\" width=\"1280\" height=\"720\" type=\"video/mp4\" bitrate=\"252\" scalable=\"true\" maintainAspectRatio=\"true\">\n" +
                "                                <![CDATA[https://redirector.gvt1.com/videoplayback/id/b96674ee53e47835/itag/22/source/gfp_video_ads/requiressl/yes/acao/yes/mime/video%2Fmp4/ctier/L/ip/0.0.0.0/ipbits/0/expire/1576593110/sparams/ip,ipbits,expire,id,itag,source,requiressl,acao,mime,ctier/signature/2FD975ED3F1DC539431C7B1599343CA6D42BE26F.3192B70BACB2374FF6F79F2C1B910CAD061F6681/key/ck2/file/file.mp4]]>\n" +
                "                            </MediaFile>\n" +
                "                            <MediaFile id=\"GDFP\" delivery=\"progressive\" width=\"1280\" height=\"720\" type=\"video/webm\" bitrate=\"245\" scalable=\"true\" maintainAspectRatio=\"true\">\n" +
                "                                <![CDATA[https://redirector.gvt1.com/videoplayback/id/b96674ee53e47835/itag/45/source/gfp_video_ads/requiressl/yes/acao/yes/mime/video%2Fwebm/ctier/L/ip/0.0.0.0/ipbits/0/expire/1576593110/sparams/ip,ipbits,expire,id,itag,source,requiressl,acao,mime,ctier/signature/A69051380724BB05C63CBADEABE87930D903A060.5268C540E0881255FB9B1E8B06D6334D9C8FFA15/key/ck2/file/file.webm]]>\n" +
                "                            </MediaFile>\n" +
                "                            <MediaFile id=\"GDFP\" delivery=\"progressive\" width=\"854\" height=\"480\" type=\"video/webm\" bitrate=\"139\" scalable=\"true\" maintainAspectRatio=\"true\">\n" +
                "                                <![CDATA[https://redirector.gvt1.com/videoplayback/id/b96674ee53e47835/itag/44/source/gfp_video_ads/requiressl/yes/acao/yes/mime/video%2Fwebm/ctier/L/ip/0.0.0.0/ipbits/0/expire/1576593110/sparams/ip,ipbits,expire,id,itag,source,requiressl,acao,mime,ctier/signature/44ABC7593CA293C15FC6B1B609F95BAC49C1293D.9735E1E8043D00DD236FB2AEBF0686FC6E591885/key/ck2/file/file.webm]]>\n" +
                "                            </MediaFile>\n" +
                "                        </MediaFiles>\n" +
                "                    </Linear>\n" +
                "                </Creative>\n" +
                "                <Creative id=\"57857370976\" sequence=\"1\">\n" +
                "                    <CompanionAds>\n" +
                "                        <Companion id=\"57857370976\" width=\"300\" height=\"250\">\n" +
                "                            <StaticResource creativeType=\"image/png\">\n" +
                "                                <![CDATA[https://pagead2.googlesyndication.com/pagead/imgad?id=CICAgKDTwILFiwEQrAIY-gEyCAAnmA4d6uc2]]>\n" +
                "                            </StaticResource>\n" +
                "                            <TrackingEvents>\n" +
                "                                <Tracking event=\"creativeView\">\n" +
                "                                    <![CDATA[https://securepubads.g.doubleclick.net/pcs/view?xai=AKAOjsu4t45zNGfTQ06CDdiMzq_mcWro7Zdd7ypWcTFkFQDuuOQEj-HujgeZUXPEIJ7MplCd0M1fON1_sVKUBdmIQbAFCFIYbFbOjhxoBArrI3bUPnmOWqAXN8FeyABK_cK7OUS9UDtH0dH15Uo-bazAynoCqFYLWIlX2Zogydj70J9Ct7e37UFIYEMCvLoKKJX9vFr3xOOjVagqQuF4gazu364ygyPlwi4u4BQz8yvhjnbagVo65qjZRTWiuIJkq2FNiHrw7g&sig=Cg0ArKJSzM1nydF7FthbEAE&adurl=]]>\n" +
                "                                </Tracking>\n" +
                "                            </TrackingEvents>\n" +
                "                            <CompanionClickThrough>\n" +
                "                                <![CDATA[https://pubads.g.doubleclick.net/pcs/click?xai=AKAOjsvWFRuVRofvhzu2K9mtloqdcO2Z76J4Lz72yy_u-_ubft33SejmI7szO1diOL-WSyDsF7QSavCWgJKt2-2QnaL7RqZHWxnRkysUAacI0iNTQRt5rVatQYadnJ9rm75ADKmjI7RPhhkTANwajN6Ca_UnrUydp_QLhMNAwj2c9kie1CMkcTaD4nnccEQuoGHOax_r_CPU8Sgx166SLRn_2TXDk2B1odmHAcsxFzsp4FJ5nog03uxg0O5ESAU_CnY&sig=Cg0ArKJSzDaG_Mg6T5ca&adurl=https://developers.google.com/interactive-media-ads/docs/vastinspector_dual]]>\n" +
                "                            </CompanionClickThrough>\n" +
                "                        </Companion>\n" +
                "                    </CompanionAds>\n" +
                "                </Creative>\n" +
                "            </Creatives>\n" +
                "            <Extensions>\n" +
                "                <Extension type=\"waterfall\" fallback_index=\"0\"/>\n" +
                "                <Extension type=\"geo\">\n" +
                "                    <Country>IL</Country>\n" +
                "                    <Bandwidth>4</Bandwidth>\n" +
                "                    <BandwidthKbps>20000</BandwidthKbps>\n" +
                "                </Extension>\n" +
                "                <Extension type=\"activeview\">\n" +
                "                    <CustomTracking>\n" +
                "                        <Tracking event=\"viewable_impression\">\n" +
                "                            <![CDATA[https://pubads.g.doubleclick.net/pagead/conversion/?ai=BiAmtdpL4XZu4A9Gs3gOzmKbQCJDVj-sGAAAAEAEgqN27JjgAWLCUgsbXAWD5qvSDnBC6AQo3Mjh4OTBfeG1syAEFwAIC4AIA6gIlLzEyNDMxOTA5Ni9leHRlcm5hbC9zaW5nbGVfYWRfc2FtcGxlc_gChNIegAMBkAPIBpgD8AGoAwHgBAHSBQYQ8N65zAKQBgGgBiOoB-zVG6gH89Eb2AcB4AcL0ggHCIBhEAEYHQ&sigh=0Yw_KrP8i6U&label=viewable_impression&acvw=[VIEWABILITY]&gv=[GOOGLE_VIEWABILITY]&ad_mt=[AD_MT]]]>\n" +
                "                        </Tracking>\n" +
                "                        <Tracking event=\"abandon\">\n" +
                "                            <![CDATA[https://pubads.g.doubleclick.net/pagead/conversion/?ai=BiAmtdpL4XZu4A9Gs3gOzmKbQCJDVj-sGAAAAEAEgqN27JjgAWLCUgsbXAWD5qvSDnBC6AQo3Mjh4OTBfeG1syAEFwAIC4AIA6gIlLzEyNDMxOTA5Ni9leHRlcm5hbC9zaW5nbGVfYWRfc2FtcGxlc_gChNIegAMBkAPIBpgD8AGoAwHgBAHSBQYQ8N65zAKQBgGgBiOoB-zVG6gH89Eb2AcB4AcL0ggHCIBhEAEYHQ&sigh=0Yw_KrP8i6U&label=video_abandon&acvw=[VIEWABILITY]&gv=[GOOGLE_VIEWABILITY]]]>\n" +
                "                        </Tracking>\n" +
                "                    </CustomTracking>\n" +
                "                    <ActiveViewMetadata>\n" +
                "                        <![CDATA[la=1&alp=xai&alh=2089752336&]]>\n" +
                "                    </ActiveViewMetadata>\n" +
                "                </Extension>\n" +
                "                <Extension type=\"DFP\">\n" +
                "                    <SkippableAdType>Generic</SkippableAdType>\n" +
                "                </Extension>\n" +
                "                <Extension type=\"metrics\">\n" +
                "                    <FeEventId>dpL4XfqHA4nV3gOyt5moDw</FeEventId>\n" +
                "                    <AdEventId>CJvLx8aivOYCFVGWdwodM4wJig</AdEventId>\n" +
                "                </Extension>\n" +
                "                <Extension type=\"ShowAdTracking\">\n" +
                "                    <CustomTracking>\n" +
                "                        <Tracking event=\"show_ad\">\n" +
                "                            <![CDATA[https://securepubads.g.doubleclick.net/pcs/view?xai=AKAOjsuMDugqzaXeMHc5duHjTQTFZd_2h7D5m4MC5GvwNKYMmJjOkfp3zT6eTAAJ9TU383ZdaRKIluMaptIot2SeFL2moXcRpRCmh1PRrd6VPWswW2G70cRCPWblTBOeKELi7-NT3RJOHGge6yRow5LCbM3iYK8lFXgzRYAO4xN_Q2nUjZzwSFOn7lwjzQEKMbR8OobgBq1SwXVZFK60oi4F4G-94Y1muVUkgnpBLn49Zz3-eYUIxKiE7IgMDS5RuRuVCOgJui2n&sig=Cg0ArKJSzCI1ib-MTkMyEAE&adurl=]]>\n" +
                "                        </Tracking>\n" +
                "                    </CustomTracking>\n" +
                "                </Extension>\n" +
                "                <Extension type=\"video_ad_loaded\">\n" +
                "                    <CustomTracking>\n" +
                "                        <Tracking event=\"loaded\">\n" +
                "                            <![CDATA[https://pubads.g.doubleclick.net/pagead/conversion/?ai=BiAmtdpL4XZu4A9Gs3gOzmKbQCJDVj-sGAAAAEAEgqN27JjgAWLCUgsbXAWD5qvSDnBC6AQo3Mjh4OTBfeG1syAEFwAIC4AIA6gIlLzEyNDMxOTA5Ni9leHRlcm5hbC9zaW5nbGVfYWRfc2FtcGxlc_gChNIegAMBkAPIBpgD8AGoAwHgBAHSBQYQ8N65zAKQBgGgBiOoB-zVG6gH89Eb2AcB4AcL0ggHCIBhEAEYHQ&sigh=0Yw_KrP8i6U&label=video_ad_loaded]]>\n" +
                "                        </Tracking>\n" +
                "                    </CustomTracking>\n" +
                "                </Extension>\n" +
                "            </Extensions>\n" +
                "        </InLine>\n" +
                "    </Ad>\n" +
                "    <Ad id=\"697200496\" sequence=\"2\">\n" +
                "        <InLine>\n" +
                "            <AdSystem>GDFP</AdSystem>\n" +
                "            <AdTitle>External NCA1C1L1 LinearInlineSkippable2</AdTitle>\n" +
                "            <Description>\n" +
                "                <![CDATA[External NCA1C1L1 LinearInlineSkippable ad]]>\n" +
                "            </Description>\n" +
                "            <Error>\n" +
                "                <![CDATA[https://pubads.g.doubleclick.net/pagead/conversion/?ai=BiAmtdpL4XZu4A9Gs3gOzmKbQCJDVj-sGAAAAEAEgqN27JjgAWLCUgsbXAWD5qvSDnBC6AQo3Mjh4OTBfeG1syAEFwAIC4AIA6gIlLzEyNDMxOTA5Ni9leHRlcm5hbC9zaW5nbGVfYWRfc2FtcGxlc_gChNIegAMBkAPIBpgD8AGoAwHgBAHSBQYQ8N65zAKQBgGgBiOoB-zVG6gH89Eb2AcB4AcL0ggHCIBhEAEYHQ&sigh=0Yw_KrP8i6U&label=videoplayfailed[ERRORCODE]]]>\n" +
                "            </Error>\n" +
                "            <Impression>\n" +
                "                <![CDATA[https://securepubads.g.doubleclick.net/pcs/view?xai=AKAOjssbJ-NIPc1thQ4jiqlJtwMcdR0cMzj-DPmwv0ZLNCGVewPk-_-qRRXzhiyRpSGQyfjxUPYMsbx1TfNP73PHwzH5Sa8xTHrSVMK14ibBcEKXIZckAU36SzpqkOsY6HFybbiv8TJ2vqCfWqbkwzVoP3_uC5Co8ODxwzjrFHyrUT99w638gemssuGGLhiE1D1oSENH-b3L_Z4X17n0EQ1Mmma6Y8OywPy5UmRUWZOFHMD-9KJ4z4IaaFCuO7toI-HjzMjKAQ&sig=Cg0ArKJSzO_u2FZZ48G4EAE&adurl=]]>\n" +
                "            </Impression>\n" +
                "            <Creatives>\n" +
                "                <Creative id=\"57860459056\" sequence=\"1\">\n" +
                "                    <Linear skipoffset=\"00:00:07\">\n" +
                "                        <Duration>00:00:10</Duration>\n" +
                "                        <TrackingEvents>\n" +
                "                            <Tracking event=\"start\">\n" +
                "                                <![CDATA[https://pubads.g.doubleclick.net/pagead/conversion/?ai=BiAmtdpL4XZu4A9Gs3gOzmKbQCJDVj-sGAAAAEAEgqN27JjgAWLCUgsbXAWD5qvSDnBC6AQo3Mjh4OTBfeG1syAEFwAIC4AIA6gIlLzEyNDMxOTA5Ni9leHRlcm5hbC9zaW5nbGVfYWRfc2FtcGxlc_gChNIegAMBkAPIBpgD8AGoAwHgBAHSBQYQ8N65zAKQBgGgBiOoB-zVG6gH89Eb2AcB4AcL0ggHCIBhEAEYHQ&sigh=0Yw_KrP8i6U&label=part2viewed&ad_mt=[AD_MT]]]>\n" +
                "                            </Tracking>\n" +
                "                            <Tracking event=\"firstQuartile\">\n" +
                "                                <![CDATA[https://pubads.g.doubleclick.net/pagead/conversion/?ai=BiAmtdpL4XZu4A9Gs3gOzmKbQCJDVj-sGAAAAEAEgqN27JjgAWLCUgsbXAWD5qvSDnBC6AQo3Mjh4OTBfeG1syAEFwAIC4AIA6gIlLzEyNDMxOTA5Ni9leHRlcm5hbC9zaW5nbGVfYWRfc2FtcGxlc_gChNIegAMBkAPIBpgD8AGoAwHgBAHSBQYQ8N65zAKQBgGgBiOoB-zVG6gH89Eb2AcB4AcL0ggHCIBhEAEYHQ&sigh=0Yw_KrP8i6U&label=videoplaytime25&ad_mt=[AD_MT]]]>\n" +
                "                            </Tracking>\n" +
                "                            <Tracking event=\"midpoint\">\n" +
                "                                <![CDATA[https://pubads.g.doubleclick.net/pagead/conversion/?ai=BiAmtdpL4XZu4A9Gs3gOzmKbQCJDVj-sGAAAAEAEgqN27JjgAWLCUgsbXAWD5qvSDnBC6AQo3Mjh4OTBfeG1syAEFwAIC4AIA6gIlLzEyNDMxOTA5Ni9leHRlcm5hbC9zaW5nbGVfYWRfc2FtcGxlc_gChNIegAMBkAPIBpgD8AGoAwHgBAHSBQYQ8N65zAKQBgGgBiOoB-zVG6gH89Eb2AcB4AcL0ggHCIBhEAEYHQ&sigh=0Yw_KrP8i6U&label=videoplaytime50&ad_mt=[AD_MT]]]>\n" +
                "                            </Tracking>\n" +
                "                            <Tracking event=\"thirdQuartile\">\n" +
                "                                <![CDATA[https://pubads.g.doubleclick.net/pagead/conversion/?ai=BiAmtdpL4XZu4A9Gs3gOzmKbQCJDVj-sGAAAAEAEgqN27JjgAWLCUgsbXAWD5qvSDnBC6AQo3Mjh4OTBfeG1syAEFwAIC4AIA6gIlLzEyNDMxOTA5Ni9leHRlcm5hbC9zaW5nbGVfYWRfc2FtcGxlc_gChNIegAMBkAPIBpgD8AGoAwHgBAHSBQYQ8N65zAKQBgGgBiOoB-zVG6gH89Eb2AcB4AcL0ggHCIBhEAEYHQ&sigh=0Yw_KrP8i6U&label=videoplaytime75&ad_mt=[AD_MT]]]>\n" +
                "                            </Tracking>\n" +
                "                            <Tracking event=\"complete\">\n" +
                "                                <![CDATA[https://pubads.g.doubleclick.net/pagead/conversion/?ai=BiAmtdpL4XZu4A9Gs3gOzmKbQCJDVj-sGAAAAEAEgqN27JjgAWLCUgsbXAWD5qvSDnBC6AQo3Mjh4OTBfeG1syAEFwAIC4AIA6gIlLzEyNDMxOTA5Ni9leHRlcm5hbC9zaW5nbGVfYWRfc2FtcGxlc_gChNIegAMBkAPIBpgD8AGoAwHgBAHSBQYQ8N65zAKQBgGgBiOoB-zVG6gH89Eb2AcB4AcL0ggHCIBhEAEYHQ&sigh=0Yw_KrP8i6U&label=videoplaytime100&ad_mt=[AD_MT]]]>\n" +
                "                            </Tracking>\n" +
                "                            <Tracking event=\"mute\">\n" +
                "                                <![CDATA[https://pubads.g.doubleclick.net/pagead/conversion/?ai=BiAmtdpL4XZu4A9Gs3gOzmKbQCJDVj-sGAAAAEAEgqN27JjgAWLCUgsbXAWD5qvSDnBC6AQo3Mjh4OTBfeG1syAEFwAIC4AIA6gIlLzEyNDMxOTA5Ni9leHRlcm5hbC9zaW5nbGVfYWRfc2FtcGxlc_gChNIegAMBkAPIBpgD8AGoAwHgBAHSBQYQ8N65zAKQBgGgBiOoB-zVG6gH89Eb2AcB4AcL0ggHCIBhEAEYHQ&sigh=0Yw_KrP8i6U&label=admute&ad_mt=[AD_MT]]]>\n" +
                "                            </Tracking>\n" +
                "                            <Tracking event=\"unmute\">\n" +
                "                                <![CDATA[https://pubads.g.doubleclick.net/pagead/conversion/?ai=BiAmtdpL4XZu4A9Gs3gOzmKbQCJDVj-sGAAAAEAEgqN27JjgAWLCUgsbXAWD5qvSDnBC6AQo3Mjh4OTBfeG1syAEFwAIC4AIA6gIlLzEyNDMxOTA5Ni9leHRlcm5hbC9zaW5nbGVfYWRfc2FtcGxlc_gChNIegAMBkAPIBpgD8AGoAwHgBAHSBQYQ8N65zAKQBgGgBiOoB-zVG6gH89Eb2AcB4AcL0ggHCIBhEAEYHQ&sigh=0Yw_KrP8i6U&label=adunmute&ad_mt=[AD_MT]]]>\n" +
                "                            </Tracking>\n" +
                "                            <Tracking event=\"rewind\">\n" +
                "                                <![CDATA[https://pubads.g.doubleclick.net/pagead/conversion/?ai=BiAmtdpL4XZu4A9Gs3gOzmKbQCJDVj-sGAAAAEAEgqN27JjgAWLCUgsbXAWD5qvSDnBC6AQo3Mjh4OTBfeG1syAEFwAIC4AIA6gIlLzEyNDMxOTA5Ni9leHRlcm5hbC9zaW5nbGVfYWRfc2FtcGxlc_gChNIegAMBkAPIBpgD8AGoAwHgBAHSBQYQ8N65zAKQBgGgBiOoB-zVG6gH89Eb2AcB4AcL0ggHCIBhEAEYHQ&sigh=0Yw_KrP8i6U&label=adrewind&ad_mt=[AD_MT]]]>\n" +
                "                            </Tracking>\n" +
                "                            <Tracking event=\"pause\">\n" +
                "                                <![CDATA[https://pubads.g.doubleclick.net/pagead/conversion/?ai=BiAmtdpL4XZu4A9Gs3gOzmKbQCJDVj-sGAAAAEAEgqN27JjgAWLCUgsbXAWD5qvSDnBC6AQo3Mjh4OTBfeG1syAEFwAIC4AIA6gIlLzEyNDMxOTA5Ni9leHRlcm5hbC9zaW5nbGVfYWRfc2FtcGxlc_gChNIegAMBkAPIBpgD8AGoAwHgBAHSBQYQ8N65zAKQBgGgBiOoB-zVG6gH89Eb2AcB4AcL0ggHCIBhEAEYHQ&sigh=0Yw_KrP8i6U&label=adpause&ad_mt=[AD_MT]]]>\n" +
                "                            </Tracking>\n" +
                "                            <Tracking event=\"resume\">\n" +
                "                                <![CDATA[https://pubads.g.doubleclick.net/pagead/conversion/?ai=BiAmtdpL4XZu4A9Gs3gOzmKbQCJDVj-sGAAAAEAEgqN27JjgAWLCUgsbXAWD5qvSDnBC6AQo3Mjh4OTBfeG1syAEFwAIC4AIA6gIlLzEyNDMxOTA5Ni9leHRlcm5hbC9zaW5nbGVfYWRfc2FtcGxlc_gChNIegAMBkAPIBpgD8AGoAwHgBAHSBQYQ8N65zAKQBgGgBiOoB-zVG6gH89Eb2AcB4AcL0ggHCIBhEAEYHQ&sigh=0Yw_KrP8i6U&label=adresume&ad_mt=[AD_MT]]]>\n" +
                "                            </Tracking>\n" +
                "                            <Tracking event=\"fullscreen\">\n" +
                "                                <![CDATA[https://pubads.g.doubleclick.net/pagead/conversion/?ai=BiAmtdpL4XZu4A9Gs3gOzmKbQCJDVj-sGAAAAEAEgqN27JjgAWLCUgsbXAWD5qvSDnBC6AQo3Mjh4OTBfeG1syAEFwAIC4AIA6gIlLzEyNDMxOTA5Ni9leHRlcm5hbC9zaW5nbGVfYWRfc2FtcGxlc_gChNIegAMBkAPIBpgD8AGoAwHgBAHSBQYQ8N65zAKQBgGgBiOoB-zVG6gH89Eb2AcB4AcL0ggHCIBhEAEYHQ&sigh=0Yw_KrP8i6U&label=adfullscreen&ad_mt=[AD_MT]]]>\n" +
                "                            </Tracking>\n" +
                "                            <Tracking event=\"creativeView\">\n" +
                "                                <![CDATA[https://pubads.g.doubleclick.net/pagead/conversion/?ai=BiAmtdpL4XZu4A9Gs3gOzmKbQCJDVj-sGAAAAEAEgqN27JjgAWLCUgsbXAWD5qvSDnBC6AQo3Mjh4OTBfeG1syAEFwAIC4AIA6gIlLzEyNDMxOTA5Ni9leHRlcm5hbC9zaW5nbGVfYWRfc2FtcGxlc_gChNIegAMBkAPIBpgD8AGoAwHgBAHSBQYQ8N65zAKQBgGgBiOoB-zVG6gH89Eb2AcB4AcL0ggHCIBhEAEYHQ&sigh=0Yw_KrP8i6U&label=vast_creativeview&ad_mt=[AD_MT]]]>\n" +
                "                            </Tracking>\n" +
                "                            <Tracking event=\"exitFullscreen\">\n" +
                "                                <![CDATA[https://pubads.g.doubleclick.net/pagead/conversion/?ai=BiAmtdpL4XZu4A9Gs3gOzmKbQCJDVj-sGAAAAEAEgqN27JjgAWLCUgsbXAWD5qvSDnBC6AQo3Mjh4OTBfeG1syAEFwAIC4AIA6gIlLzEyNDMxOTA5Ni9leHRlcm5hbC9zaW5nbGVfYWRfc2FtcGxlc_gChNIegAMBkAPIBpgD8AGoAwHgBAHSBQYQ8N65zAKQBgGgBiOoB-zVG6gH89Eb2AcB4AcL0ggHCIBhEAEYHQ&sigh=0Yw_KrP8i6U&label=vast_exit_fullscreen&ad_mt=[AD_MT]]]>\n" +
                "                            </Tracking>\n" +
                "                            <Tracking event=\"acceptInvitationLinear\">\n" +
                "                                <![CDATA[https://pubads.g.doubleclick.net/pagead/conversion/?ai=BiAmtdpL4XZu4A9Gs3gOzmKbQCJDVj-sGAAAAEAEgqN27JjgAWLCUgsbXAWD5qvSDnBC6AQo3Mjh4OTBfeG1syAEFwAIC4AIA6gIlLzEyNDMxOTA5Ni9leHRlcm5hbC9zaW5nbGVfYWRfc2FtcGxlc_gChNIegAMBkAPIBpgD8AGoAwHgBAHSBQYQ8N65zAKQBgGgBiOoB-zVG6gH89Eb2AcB4AcL0ggHCIBhEAEYHQ&sigh=0Yw_KrP8i6U&label=acceptinvitation&ad_mt=[AD_MT]]]>\n" +
                "                            </Tracking>\n" +
                "                            <Tracking event=\"closeLinear\">\n" +
                "                                <![CDATA[https://pubads.g.doubleclick.net/pagead/conversion/?ai=BiAmtdpL4XZu4A9Gs3gOzmKbQCJDVj-sGAAAAEAEgqN27JjgAWLCUgsbXAWD5qvSDnBC6AQo3Mjh4OTBfeG1syAEFwAIC4AIA6gIlLzEyNDMxOTA5Ni9leHRlcm5hbC9zaW5nbGVfYWRfc2FtcGxlc_gChNIegAMBkAPIBpgD8AGoAwHgBAHSBQYQ8N65zAKQBgGgBiOoB-zVG6gH89Eb2AcB4AcL0ggHCIBhEAEYHQ&sigh=0Yw_KrP8i6U&label=adclose&ad_mt=[AD_MT]]]>\n" +
                "                            </Tracking>\n" +
                "                            <Tracking event=\"skip\">\n" +
                "                                <![CDATA[https://pubads.g.doubleclick.net/pagead/conversion/?ai=BiAmtdpL4XZu4A9Gs3gOzmKbQCJDVj-sGAAAAEAEgqN27JjgAWLCUgsbXAWD5qvSDnBC6AQo3Mjh4OTBfeG1syAEFwAIC4AIA6gIlLzEyNDMxOTA5Ni9leHRlcm5hbC9zaW5nbGVfYWRfc2FtcGxlc_gChNIegAMBkAPIBpgD8AGoAwHgBAHSBQYQ8N65zAKQBgGgBiOoB-zVG6gH89Eb2AcB4AcL0ggHCIBhEAEYHQ&sigh=0Yw_KrP8i6U&label=videoskipped&ad_mt=[AD_MT]]]>\n" +
                "                            </Tracking>\n" +
                "                            <Tracking event=\"progress\" offset=\"00:00:05\">\n" +
                "                                <![CDATA[https://pubads.g.doubleclick.net/pagead/conversion/?ai=BiAmtdpL4XZu4A9Gs3gOzmKbQCJDVj-sGAAAAEAEgqN27JjgAWLCUgsbXAWD5qvSDnBC6AQo3Mjh4OTBfeG1syAEFwAIC4AIA6gIlLzEyNDMxOTA5Ni9leHRlcm5hbC9zaW5nbGVfYWRfc2FtcGxlc_gChNIegAMBkAPIBpgD8AGoAwHgBAHSBQYQ8N65zAKQBgGgBiOoB-zVG6gH89Eb2AcB4AcL0ggHCIBhEAEYHQ&sigh=0Yw_KrP8i6U&label=video_skip_shown&ad_mt=[AD_MT]]]>\n" +
                "                            </Tracking>\n" +
                "                            <Tracking event=\"progress\" offset=\"00:00:30\">\n" +
                "                                <![CDATA[https://pubads.g.doubleclick.net/pagead/conversion/?ai=BiAmtdpL4XZu4A9Gs3gOzmKbQCJDVj-sGAAAAEAEgqN27JjgAWLCUgsbXAWD5qvSDnBC6AQo3Mjh4OTBfeG1syAEFwAIC4AIA6gIlLzEyNDMxOTA5Ni9leHRlcm5hbC9zaW5nbGVfYWRfc2FtcGxlc_gChNIegAMBkAPIBpgD8AGoAwHgBAHSBQYQ8N65zAKQBgGgBiOoB-zVG6gH89Eb2AcB4AcL0ggHCIBhEAEYHQ&sigh=0Yw_KrP8i6U&label=video_engaged_view&ad_mt=[AD_MT]]]>\n" +
                "                            </Tracking>\n" +
                "                        </TrackingEvents>\n" +
                "                        <AdParameters>\n" +
                "                            <![CDATA[custom_param=some_value]]>\n" +
                "                        </AdParameters>\n" +
                "                        <VideoClicks>\n" +
                "                            <ClickThrough id=\"GDFP\">\n" +
                "                                <![CDATA[https://pubads.g.doubleclick.net/pcs/click?xai=AKAOjssRtEFo991pR7zwabKsfXwr_aghNnLMOIPnWfUd5qDOvHBP6JBLSaLelz1zulNEXWWR7eAs0jETJWFfaGAymWttftWZcEddHM17nyxbug9-mXgcDMU30TfS5cwe9qbYt8UbkvIVTpA3IcQ6dchlTaIojYu_5lacYvZ3OxpFUtGs0jsFDAHs8zKjUIrQpk66f9kGao8FgTUb3uD5qrBn11QFBaCWZfgmkQK_KUVpYZ-K3xD0qsAEhbaJ-E17Rr4&sig=Cg0ArKJSzPD0XJy5Qe_z&adurl=https://developers.google.com/interactive-media-ads/docs/vastinspector_dual]]>\n" +
                "                            </ClickThrough>\n" +
                "                        </VideoClicks>\n" +
                "                        <MediaFiles>\n" +
                "                            <MediaFile id=\"GDFP\" delivery=\"progressive\" width=\"1280\" height=\"720\" type=\"video/mp4\" bitrate=\"533\" scalable=\"true\" maintainAspectRatio=\"true\">\n" +
                "                                <![CDATA[https://redirector.gvt1.com/videoplayback/id/b96674ee53e47835/itag/15/source/gfp_video_ads/requiressl/yes/acao/yes/mime/video%2Fmp4/ctier/L/ip/0.0.0.0/ipbits/0/expire/1576593110/sparams/ip,ipbits,expire,id,itag,source,requiressl,acao,mime,ctier/signature/A7A30B1D1A7F7BE73A9538FD1B807350967D2AEB.9A38C814EDA47CAC281AED15E39F280599559A30/key/ck2/file/file.mp4]]>\n" +
                "                            </MediaFile>\n" +
                "                            <MediaFile id=\"GDFP\" delivery=\"progressive\" width=\"176\" height=\"144\" type=\"video/3gpp\" bitrate=\"36\" scalable=\"true\" maintainAspectRatio=\"true\">\n" +
                "                                <![CDATA[https://redirector.gvt1.com/videoplayback/id/b96674ee53e47835/itag/17/source/gfp_video_ads/requiressl/yes/acao/yes/mime/video%2F3gpp/ctier/L/ip/0.0.0.0/ipbits/0/expire/1576593110/sparams/ip,ipbits,expire,id,itag,source,requiressl,acao,mime,ctier/signature/704416F60F7B435E72AB4930D2797E711D21D267.AA2B92F31E6FB8B7B68757975771630A1A77AD67/key/ck2/file/file.3gp]]>\n" +
                "                            </MediaFile>\n" +
                "                            <MediaFile id=\"GDFP\" delivery=\"progressive\" width=\"640\" height=\"360\" type=\"video/mp4\" bitrate=\"122\" scalable=\"true\" maintainAspectRatio=\"true\">\n" +
                "                                <![CDATA[https://redirector.gvt1.com/videoplayback/id/b96674ee53e47835/itag/18/source/gfp_video_ads/requiressl/yes/acao/yes/mime/video%2Fmp4/ctier/L/ip/0.0.0.0/ipbits/0/expire/1576593110/sparams/ip,ipbits,expire,id,itag,source,requiressl,acao,mime,ctier/signature/10542A19241D1CC9E11D8AE9FA0E1A6B003C079B.8D328E7408844794F1985FBC9DC53B850E60E8AB/key/ck2/file/file.mp4]]>\n" +
                "                            </MediaFile>\n" +
                "                            <MediaFile id=\"GDFP\" delivery=\"progressive\" width=\"320\" height=\"180\" type=\"video/3gpp\" bitrate=\"74\" scalable=\"true\" maintainAspectRatio=\"true\">\n" +
                "                                <![CDATA[https://redirector.gvt1.com/videoplayback/id/b96674ee53e47835/itag/36/source/gfp_video_ads/requiressl/yes/acao/yes/mime/video%2F3gpp/ctier/L/ip/0.0.0.0/ipbits/0/expire/1576593110/sparams/ip,ipbits,expire,id,itag,source,requiressl,acao,mime,ctier/signature/682F9CB111CA36AB2B72D9CF06B97814C08AEAE9.8867580B16BC64EB7E62725EE281CE93330FD9A1/key/ck2/file/file.3gp]]>\n" +
                "                            </MediaFile>\n" +
                "                            <MediaFile id=\"GDFP\" delivery=\"progressive\" width=\"640\" height=\"360\" type=\"video/webm\" bitrate=\"125\" scalable=\"true\" maintainAspectRatio=\"true\">\n" +
                "                                <![CDATA[https://redirector.gvt1.com/videoplayback/id/b96674ee53e47835/itag/43/source/gfp_video_ads/requiressl/yes/acao/yes/mime/video%2Fwebm/ctier/L/ip/0.0.0.0/ipbits/0/expire/1576593110/sparams/ip,ipbits,expire,id,itag,source,requiressl,acao,mime,ctier/signature/6D73F2F257A32FE7EE3833AC95A2E0F83B26D30E.8AC48D55F95E5E06CE97F79999D43FCFBE58598A/key/ck2/file/file.webm]]>\n" +
                "                            </MediaFile>\n" +
                "                            <MediaFile id=\"GDFP\" delivery=\"progressive\" width=\"1280\" height=\"720\" type=\"video/mp4\" bitrate=\"252\" scalable=\"true\" maintainAspectRatio=\"true\">\n" +
                "                                <![CDATA[https://redirector.gvt1.com/videoplayback/id/b96674ee53e47835/itag/22/source/gfp_video_ads/requiressl/yes/acao/yes/mime/video%2Fmp4/ctier/L/ip/0.0.0.0/ipbits/0/expire/1576593110/sparams/ip,ipbits,expire,id,itag,source,requiressl,acao,mime,ctier/signature/2FD975ED3F1DC539431C7B1599343CA6D42BE26F.3192B70BACB2374FF6F79F2C1B910CAD061F6681/key/ck2/file/file.mp4]]>\n" +
                "                            </MediaFile>\n" +
                "                            <MediaFile id=\"GDFP\" delivery=\"progressive\" width=\"1280\" height=\"720\" type=\"video/webm\" bitrate=\"245\" scalable=\"true\" maintainAspectRatio=\"true\">\n" +
                "                                <![CDATA[https://redirector.gvt1.com/videoplayback/id/b96674ee53e47835/itag/45/source/gfp_video_ads/requiressl/yes/acao/yes/mime/video%2Fwebm/ctier/L/ip/0.0.0.0/ipbits/0/expire/1576593110/sparams/ip,ipbits,expire,id,itag,source,requiressl,acao,mime,ctier/signature/A69051380724BB05C63CBADEABE87930D903A060.5268C540E0881255FB9B1E8B06D6334D9C8FFA15/key/ck2/file/file.webm]]>\n" +
                "                            </MediaFile>\n" +
                "                            <MediaFile id=\"GDFP\" delivery=\"progressive\" width=\"854\" height=\"480\" type=\"video/webm\" bitrate=\"139\" scalable=\"true\" maintainAspectRatio=\"true\">\n" +
                "                                <![CDATA[https://redirector.gvt1.com/videoplayback/id/b96674ee53e47835/itag/44/source/gfp_video_ads/requiressl/yes/acao/yes/mime/video%2Fwebm/ctier/L/ip/0.0.0.0/ipbits/0/expire/1576593110/sparams/ip,ipbits,expire,id,itag,source,requiressl,acao,mime,ctier/signature/44ABC7593CA293C15FC6B1B609F95BAC49C1293D.9735E1E8043D00DD236FB2AEBF0686FC6E591885/key/ck2/file/file.webm]]>\n" +
                "                            </MediaFile>\n" +
                "                        </MediaFiles>\n" +
                "                    </Linear>\n" +
                "                </Creative>\n" +
                "                <Creative id=\"57857370976\" sequence=\"1\">\n" +
                "                    <CompanionAds>\n" +
                "                        <Companion id=\"57857370976\" width=\"300\" height=\"250\">\n" +
                "                            <StaticResource creativeType=\"image/png\">\n" +
                "                                <![CDATA[https://pagead2.googlesyndication.com/pagead/imgad?id=CICAgKDTwILFiwEQrAIY-gEyCAAnmA4d6uc2]]>\n" +
                "                            </StaticResource>\n" +
                "                            <TrackingEvents>\n" +
                "                                <Tracking event=\"creativeView\">\n" +
                "                                    <![CDATA[https://securepubads.g.doubleclick.net/pcs/view?xai=AKAOjsu4t45zNGfTQ06CDdiMzq_mcWro7Zdd7ypWcTFkFQDuuOQEj-HujgeZUXPEIJ7MplCd0M1fON1_sVKUBdmIQbAFCFIYbFbOjhxoBArrI3bUPnmOWqAXN8FeyABK_cK7OUS9UDtH0dH15Uo-bazAynoCqFYLWIlX2Zogydj70J9Ct7e37UFIYEMCvLoKKJX9vFr3xOOjVagqQuF4gazu364ygyPlwi4u4BQz8yvhjnbagVo65qjZRTWiuIJkq2FNiHrw7g&sig=Cg0ArKJSzM1nydF7FthbEAE&adurl=]]>\n" +
                "                                </Tracking>\n" +
                "                            </TrackingEvents>\n" +
                "                            <CompanionClickThrough>\n" +
                "                                <![CDATA[https://pubads.g.doubleclick.net/pcs/click?xai=AKAOjsvWFRuVRofvhzu2K9mtloqdcO2Z76J4Lz72yy_u-_ubft33SejmI7szO1diOL-WSyDsF7QSavCWgJKt2-2QnaL7RqZHWxnRkysUAacI0iNTQRt5rVatQYadnJ9rm75ADKmjI7RPhhkTANwajN6Ca_UnrUydp_QLhMNAwj2c9kie1CMkcTaD4nnccEQuoGHOax_r_CPU8Sgx166SLRn_2TXDk2B1odmHAcsxFzsp4FJ5nog03uxg0O5ESAU_CnY&sig=Cg0ArKJSzDaG_Mg6T5ca&adurl=https://developers.google.com/interactive-media-ads/docs/vastinspector_dual]]>\n" +
                "                            </CompanionClickThrough>\n" +
                "                        </Companion>\n" +
                "                    </CompanionAds>\n" +
                "                </Creative>\n" +
                "            </Creatives>\n" +
                "            <Extensions>\n" +
                "                <Extension type=\"waterfall\" fallback_index=\"0\"/>\n" +
                "                <Extension type=\"geo\">\n" +
                "                    <Country>IL</Country>\n" +
                "                    <Bandwidth>4</Bandwidth>\n" +
                "                    <BandwidthKbps>20000</BandwidthKbps>\n" +
                "                </Extension>\n" +
                "                <Extension type=\"activeview\">\n" +
                "                    <CustomTracking>\n" +
                "                        <Tracking event=\"viewable_impression\">\n" +
                "                            <![CDATA[https://pubads.g.doubleclick.net/pagead/conversion/?ai=BiAmtdpL4XZu4A9Gs3gOzmKbQCJDVj-sGAAAAEAEgqN27JjgAWLCUgsbXAWD5qvSDnBC6AQo3Mjh4OTBfeG1syAEFwAIC4AIA6gIlLzEyNDMxOTA5Ni9leHRlcm5hbC9zaW5nbGVfYWRfc2FtcGxlc_gChNIegAMBkAPIBpgD8AGoAwHgBAHSBQYQ8N65zAKQBgGgBiOoB-zVG6gH89Eb2AcB4AcL0ggHCIBhEAEYHQ&sigh=0Yw_KrP8i6U&label=viewable_impression&acvw=[VIEWABILITY]&gv=[GOOGLE_VIEWABILITY]&ad_mt=[AD_MT]]]>\n" +
                "                        </Tracking>\n" +
                "                        <Tracking event=\"abandon\">\n" +
                "                            <![CDATA[https://pubads.g.doubleclick.net/pagead/conversion/?ai=BiAmtdpL4XZu4A9Gs3gOzmKbQCJDVj-sGAAAAEAEgqN27JjgAWLCUgsbXAWD5qvSDnBC6AQo3Mjh4OTBfeG1syAEFwAIC4AIA6gIlLzEyNDMxOTA5Ni9leHRlcm5hbC9zaW5nbGVfYWRfc2FtcGxlc_gChNIegAMBkAPIBpgD8AGoAwHgBAHSBQYQ8N65zAKQBgGgBiOoB-zVG6gH89Eb2AcB4AcL0ggHCIBhEAEYHQ&sigh=0Yw_KrP8i6U&label=video_abandon&acvw=[VIEWABILITY]&gv=[GOOGLE_VIEWABILITY]]]>\n" +
                "                        </Tracking>\n" +
                "                    </CustomTracking>\n" +
                "                    <ActiveViewMetadata>\n" +
                "                        <![CDATA[la=1&alp=xai&alh=2089752336&]]>\n" +
                "                    </ActiveViewMetadata>\n" +
                "                </Extension>\n" +
                "                <Extension type=\"DFP\">\n" +
                "                    <SkippableAdType>Generic</SkippableAdType>\n" +
                "                </Extension>\n" +
                "                <Extension type=\"metrics\">\n" +
                "                    <FeEventId>dpL4XfqHA4nV3gOyt5moDw</FeEventId>\n" +
                "                    <AdEventId>CJvLx8aivOYCFVGWdwodM4wJig</AdEventId>\n" +
                "                </Extension>\n" +
                "                <Extension type=\"ShowAdTracking\">\n" +
                "                    <CustomTracking>\n" +
                "                        <Tracking event=\"show_ad\">\n" +
                "                            <![CDATA[https://securepubads.g.doubleclick.net/pcs/view?xai=AKAOjsuMDugqzaXeMHc5duHjTQTFZd_2h7D5m4MC5GvwNKYMmJjOkfp3zT6eTAAJ9TU383ZdaRKIluMaptIot2SeFL2moXcRpRCmh1PRrd6VPWswW2G70cRCPWblTBOeKELi7-NT3RJOHGge6yRow5LCbM3iYK8lFXgzRYAO4xN_Q2nUjZzwSFOn7lwjzQEKMbR8OobgBq1SwXVZFK60oi4F4G-94Y1muVUkgnpBLn49Zz3-eYUIxKiE7IgMDS5RuRuVCOgJui2n&sig=Cg0ArKJSzCI1ib-MTkMyEAE&adurl=]]>\n" +
                "                        </Tracking>\n" +
                "                    </CustomTracking>\n" +
                "                </Extension>\n" +
                "                <Extension type=\"video_ad_loaded\">\n" +
                "                    <CustomTracking>\n" +
                "                        <Tracking event=\"loaded\">\n" +
                "                            <![CDATA[https://pubads.g.doubleclick.net/pagead/conversion/?ai=BiAmtdpL4XZu4A9Gs3gOzmKbQCJDVj-sGAAAAEAEgqN27JjgAWLCUgsbXAWD5qvSDnBC6AQo3Mjh4OTBfeG1syAEFwAIC4AIA6gIlLzEyNDMxOTA5Ni9leHRlcm5hbC9zaW5nbGVfYWRfc2FtcGxlc_gChNIegAMBkAPIBpgD8AGoAwHgBAHSBQYQ8N65zAKQBgGgBiOoB-zVG6gH89Eb2AcB4AcL0ggHCIBhEAEYHQ&sigh=0Yw_KrP8i6U&label=video_ad_loaded]]>\n" +
                "                        </Tracking>\n" +
                "                    </CustomTracking>\n" +
                "                </Extension>\n" +
                "            </Extensions>\n" +
                "        </InLine>\n" +
                "    </Ad>\n" +
                "</VAST>\n").setVideoMimeTypes(videoMimeTypes).enableDebugMode(true).setAlwaysStartWithPreroll(true).setAdLoadTimeOut(8);
    }

    private IMAConfig getAdsConfigResponsePostroll(String adResponse) {
        List<String> videoMimeTypes = new ArrayList<>();
        videoMimeTypes.add("video/mp4");
        videoMimeTypes.add("application/x-mpegURL");
        // videoMimeTypes.add("application/dash+xml");
        return new IMAConfig().setAdTagResponse("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<VAST xsi:noNamespaceSchemaLocation=\"vast.xsd\" version=\"3.0\">\n" +
                "<Ad id=\"709684216\">\n" +
                "<InLine>\n" +
                "<AdSystem>GDFP</AdSystem>\n" +
                "<AdTitle>External NCA1C1L1 Postroll</AdTitle>\n" +
                "<Description>External NCA1C1L1 Postroll ad</Description>\n" +
                "<Error>\n" +
                "https://pubads.g.doubleclick.net/pagead/conversion/?ai=BtyJFdtL6Xpy2E5KgvwTnoYaQCcCxj-sGAAAAEAEgqN27JjgAWODprcbXAWDlyuWDtA6yARVkZXZlbG9wZXJzLmdvb2dsZS5jb226AQo3Mjh4OTBfeG1syAEF2gFUaHR0cHM6Ly9kZXZlbG9wZXJzLmdvb2dsZS5jb20vaW50ZXJhY3RpdmUtbWVkaWEtYWRzL2RvY3Mvc2Rrcy9odG1sNS9jbGllbnQtc2lkZS90YWdzwAIC4AIA6gIjLzEyNDMxOTA5Ni9leHRlcm5hbC9hZF9ydWxlX3NhbXBsZXP4AvLRHoADAZADmgiYA6wCqAMB4AQB0gUGEPjXs9ICkAYBoAYjqAfs1RuoB_PRG6gHltgbqAfC2hvYBwHgBwvSCAcIgGEQARgdmAsB&sigh=doJoJqTltPI&label=videoplayfailed[ERRORCODE]\n" +
                "</Error>\n" +
                "<Impression>\n" +
                "https://securepubads.g.doubleclick.net/pcs/view?xai=AKAOjsslNcw-Y9YMikSWb18WnWQvH0ElPPzwVh8KuWcZsazWq9BNs7ad8ybbo3Yy_Oa3HWc6Y0xeeX5a5hqj0q9yXC9EAC7lggvao68skB2Je1knMzve5h5yd0uQbF1GHEmQX2K4xLl4NBwYjmAlkr7iD0OZ6IBWXwXVWr-CZrO9CbuLT8nAd1pIwToGZ7Z91KYanGlo8pKN13qHWnZ6MGezybENHd7MC4kS5l-vzLt8NCe0nilmyT4U75ZUVq5y5yLuH1kDgGBtn99RS3BJnMo5EIfVeSsqWF8PHA&sig=Cg0ArKJSzF6M_qkMH93mEAE&adurl=\n" +
                "</Impression>\n" +
                "<Creatives>\n" +
                "<Creative id=\"57861174496\" sequence=\"1\">\n" +
                "<Linear>\n" +
                "<Duration>00:00:10</Duration>\n" +
                "<TrackingEvents>\n" +
                "<Tracking event=\"start\">\n" +
                "https://pubads.g.doubleclick.net/pagead/conversion/?ai=BtyJFdtL6Xpy2E5KgvwTnoYaQCcCxj-sGAAAAEAEgqN27JjgAWODprcbXAWDlyuWDtA6yARVkZXZlbG9wZXJzLmdvb2dsZS5jb226AQo3Mjh4OTBfeG1syAEF2gFUaHR0cHM6Ly9kZXZlbG9wZXJzLmdvb2dsZS5jb20vaW50ZXJhY3RpdmUtbWVkaWEtYWRzL2RvY3Mvc2Rrcy9odG1sNS9jbGllbnQtc2lkZS90YWdzwAIC4AIA6gIjLzEyNDMxOTA5Ni9leHRlcm5hbC9hZF9ydWxlX3NhbXBsZXP4AvLRHoADAZADmgiYA6wCqAMB4AQB0gUGEPjXs9ICkAYBoAYjqAfs1RuoB_PRG6gHltgbqAfC2hvYBwHgBwvSCAcIgGEQARgdmAsB&sigh=doJoJqTltPI&label=part2viewed&ad_mt=[AD_MT]\n" +
                "</Tracking>\n" +
                "<Tracking event=\"firstQuartile\">\n" +
                "https://pubads.g.doubleclick.net/pagead/conversion/?ai=BtyJFdtL6Xpy2E5KgvwTnoYaQCcCxj-sGAAAAEAEgqN27JjgAWODprcbXAWDlyuWDtA6yARVkZXZlbG9wZXJzLmdvb2dsZS5jb226AQo3Mjh4OTBfeG1syAEF2gFUaHR0cHM6Ly9kZXZlbG9wZXJzLmdvb2dsZS5jb20vaW50ZXJhY3RpdmUtbWVkaWEtYWRzL2RvY3Mvc2Rrcy9odG1sNS9jbGllbnQtc2lkZS90YWdzwAIC4AIA6gIjLzEyNDMxOTA5Ni9leHRlcm5hbC9hZF9ydWxlX3NhbXBsZXP4AvLRHoADAZADmgiYA6wCqAMB4AQB0gUGEPjXs9ICkAYBoAYjqAfs1RuoB_PRG6gHltgbqAfC2hvYBwHgBwvSCAcIgGEQARgdmAsB&sigh=doJoJqTltPI&label=videoplaytime25&ad_mt=[AD_MT]\n" +
                "</Tracking>\n" +
                "<Tracking event=\"midpoint\">\n" +
                "https://pubads.g.doubleclick.net/pagead/conversion/?ai=BtyJFdtL6Xpy2E5KgvwTnoYaQCcCxj-sGAAAAEAEgqN27JjgAWODprcbXAWDlyuWDtA6yARVkZXZlbG9wZXJzLmdvb2dsZS5jb226AQo3Mjh4OTBfeG1syAEF2gFUaHR0cHM6Ly9kZXZlbG9wZXJzLmdvb2dsZS5jb20vaW50ZXJhY3RpdmUtbWVkaWEtYWRzL2RvY3Mvc2Rrcy9odG1sNS9jbGllbnQtc2lkZS90YWdzwAIC4AIA6gIjLzEyNDMxOTA5Ni9leHRlcm5hbC9hZF9ydWxlX3NhbXBsZXP4AvLRHoADAZADmgiYA6wCqAMB4AQB0gUGEPjXs9ICkAYBoAYjqAfs1RuoB_PRG6gHltgbqAfC2hvYBwHgBwvSCAcIgGEQARgdmAsB&sigh=doJoJqTltPI&label=videoplaytime50&ad_mt=[AD_MT]\n" +
                "</Tracking>\n" +
                "<Tracking event=\"thirdQuartile\">\n" +
                "https://pubads.g.doubleclick.net/pagead/conversion/?ai=BtyJFdtL6Xpy2E5KgvwTnoYaQCcCxj-sGAAAAEAEgqN27JjgAWODprcbXAWDlyuWDtA6yARVkZXZlbG9wZXJzLmdvb2dsZS5jb226AQo3Mjh4OTBfeG1syAEF2gFUaHR0cHM6Ly9kZXZlbG9wZXJzLmdvb2dsZS5jb20vaW50ZXJhY3RpdmUtbWVkaWEtYWRzL2RvY3Mvc2Rrcy9odG1sNS9jbGllbnQtc2lkZS90YWdzwAIC4AIA6gIjLzEyNDMxOTA5Ni9leHRlcm5hbC9hZF9ydWxlX3NhbXBsZXP4AvLRHoADAZADmgiYA6wCqAMB4AQB0gUGEPjXs9ICkAYBoAYjqAfs1RuoB_PRG6gHltgbqAfC2hvYBwHgBwvSCAcIgGEQARgdmAsB&sigh=doJoJqTltPI&label=videoplaytime75&ad_mt=[AD_MT]\n" +
                "</Tracking>\n" +
                "<Tracking event=\"complete\">\n" +
                "https://pubads.g.doubleclick.net/pagead/conversion/?ai=BtyJFdtL6Xpy2E5KgvwTnoYaQCcCxj-sGAAAAEAEgqN27JjgAWODprcbXAWDlyuWDtA6yARVkZXZlbG9wZXJzLmdvb2dsZS5jb226AQo3Mjh4OTBfeG1syAEF2gFUaHR0cHM6Ly9kZXZlbG9wZXJzLmdvb2dsZS5jb20vaW50ZXJhY3RpdmUtbWVkaWEtYWRzL2RvY3Mvc2Rrcy9odG1sNS9jbGllbnQtc2lkZS90YWdzwAIC4AIA6gIjLzEyNDMxOTA5Ni9leHRlcm5hbC9hZF9ydWxlX3NhbXBsZXP4AvLRHoADAZADmgiYA6wCqAMB4AQB0gUGEPjXs9ICkAYBoAYjqAfs1RuoB_PRG6gHltgbqAfC2hvYBwHgBwvSCAcIgGEQARgdmAsB&sigh=doJoJqTltPI&label=videoplaytime100&ad_mt=[AD_MT]\n" +
                "</Tracking>\n" +
                "<Tracking event=\"mute\">\n" +
                "https://pubads.g.doubleclick.net/pagead/conversion/?ai=BtyJFdtL6Xpy2E5KgvwTnoYaQCcCxj-sGAAAAEAEgqN27JjgAWODprcbXAWDlyuWDtA6yARVkZXZlbG9wZXJzLmdvb2dsZS5jb226AQo3Mjh4OTBfeG1syAEF2gFUaHR0cHM6Ly9kZXZlbG9wZXJzLmdvb2dsZS5jb20vaW50ZXJhY3RpdmUtbWVkaWEtYWRzL2RvY3Mvc2Rrcy9odG1sNS9jbGllbnQtc2lkZS90YWdzwAIC4AIA6gIjLzEyNDMxOTA5Ni9leHRlcm5hbC9hZF9ydWxlX3NhbXBsZXP4AvLRHoADAZADmgiYA6wCqAMB4AQB0gUGEPjXs9ICkAYBoAYjqAfs1RuoB_PRG6gHltgbqAfC2hvYBwHgBwvSCAcIgGEQARgdmAsB&sigh=doJoJqTltPI&label=admute&ad_mt=[AD_MT]\n" +
                "</Tracking>\n" +
                "<Tracking event=\"unmute\">\n" +
                "https://pubads.g.doubleclick.net/pagead/conversion/?ai=BtyJFdtL6Xpy2E5KgvwTnoYaQCcCxj-sGAAAAEAEgqN27JjgAWODprcbXAWDlyuWDtA6yARVkZXZlbG9wZXJzLmdvb2dsZS5jb226AQo3Mjh4OTBfeG1syAEF2gFUaHR0cHM6Ly9kZXZlbG9wZXJzLmdvb2dsZS5jb20vaW50ZXJhY3RpdmUtbWVkaWEtYWRzL2RvY3Mvc2Rrcy9odG1sNS9jbGllbnQtc2lkZS90YWdzwAIC4AIA6gIjLzEyNDMxOTA5Ni9leHRlcm5hbC9hZF9ydWxlX3NhbXBsZXP4AvLRHoADAZADmgiYA6wCqAMB4AQB0gUGEPjXs9ICkAYBoAYjqAfs1RuoB_PRG6gHltgbqAfC2hvYBwHgBwvSCAcIgGEQARgdmAsB&sigh=doJoJqTltPI&label=adunmute&ad_mt=[AD_MT]\n" +
                "</Tracking>\n" +
                "<Tracking event=\"rewind\">\n" +
                "https://pubads.g.doubleclick.net/pagead/conversion/?ai=BtyJFdtL6Xpy2E5KgvwTnoYaQCcCxj-sGAAAAEAEgqN27JjgAWODprcbXAWDlyuWDtA6yARVkZXZlbG9wZXJzLmdvb2dsZS5jb226AQo3Mjh4OTBfeG1syAEF2gFUaHR0cHM6Ly9kZXZlbG9wZXJzLmdvb2dsZS5jb20vaW50ZXJhY3RpdmUtbWVkaWEtYWRzL2RvY3Mvc2Rrcy9odG1sNS9jbGllbnQtc2lkZS90YWdzwAIC4AIA6gIjLzEyNDMxOTA5Ni9leHRlcm5hbC9hZF9ydWxlX3NhbXBsZXP4AvLRHoADAZADmgiYA6wCqAMB4AQB0gUGEPjXs9ICkAYBoAYjqAfs1RuoB_PRG6gHltgbqAfC2hvYBwHgBwvSCAcIgGEQARgdmAsB&sigh=doJoJqTltPI&label=adrewind&ad_mt=[AD_MT]\n" +
                "</Tracking>\n" +
                "<Tracking event=\"pause\">\n" +
                "https://pubads.g.doubleclick.net/pagead/conversion/?ai=BtyJFdtL6Xpy2E5KgvwTnoYaQCcCxj-sGAAAAEAEgqN27JjgAWODprcbXAWDlyuWDtA6yARVkZXZlbG9wZXJzLmdvb2dsZS5jb226AQo3Mjh4OTBfeG1syAEF2gFUaHR0cHM6Ly9kZXZlbG9wZXJzLmdvb2dsZS5jb20vaW50ZXJhY3RpdmUtbWVkaWEtYWRzL2RvY3Mvc2Rrcy9odG1sNS9jbGllbnQtc2lkZS90YWdzwAIC4AIA6gIjLzEyNDMxOTA5Ni9leHRlcm5hbC9hZF9ydWxlX3NhbXBsZXP4AvLRHoADAZADmgiYA6wCqAMB4AQB0gUGEPjXs9ICkAYBoAYjqAfs1RuoB_PRG6gHltgbqAfC2hvYBwHgBwvSCAcIgGEQARgdmAsB&sigh=doJoJqTltPI&label=adpause&ad_mt=[AD_MT]\n" +
                "</Tracking>\n" +
                "<Tracking event=\"resume\">\n" +
                "https://pubads.g.doubleclick.net/pagead/conversion/?ai=BtyJFdtL6Xpy2E5KgvwTnoYaQCcCxj-sGAAAAEAEgqN27JjgAWODprcbXAWDlyuWDtA6yARVkZXZlbG9wZXJzLmdvb2dsZS5jb226AQo3Mjh4OTBfeG1syAEF2gFUaHR0cHM6Ly9kZXZlbG9wZXJzLmdvb2dsZS5jb20vaW50ZXJhY3RpdmUtbWVkaWEtYWRzL2RvY3Mvc2Rrcy9odG1sNS9jbGllbnQtc2lkZS90YWdzwAIC4AIA6gIjLzEyNDMxOTA5Ni9leHRlcm5hbC9hZF9ydWxlX3NhbXBsZXP4AvLRHoADAZADmgiYA6wCqAMB4AQB0gUGEPjXs9ICkAYBoAYjqAfs1RuoB_PRG6gHltgbqAfC2hvYBwHgBwvSCAcIgGEQARgdmAsB&sigh=doJoJqTltPI&label=adresume&ad_mt=[AD_MT]\n" +
                "</Tracking>\n" +
                "<Tracking event=\"creativeView\">\n" +
                "https://pubads.g.doubleclick.net/pagead/conversion/?ai=BtyJFdtL6Xpy2E5KgvwTnoYaQCcCxj-sGAAAAEAEgqN27JjgAWODprcbXAWDlyuWDtA6yARVkZXZlbG9wZXJzLmdvb2dsZS5jb226AQo3Mjh4OTBfeG1syAEF2gFUaHR0cHM6Ly9kZXZlbG9wZXJzLmdvb2dsZS5jb20vaW50ZXJhY3RpdmUtbWVkaWEtYWRzL2RvY3Mvc2Rrcy9odG1sNS9jbGllbnQtc2lkZS90YWdzwAIC4AIA6gIjLzEyNDMxOTA5Ni9leHRlcm5hbC9hZF9ydWxlX3NhbXBsZXP4AvLRHoADAZADmgiYA6wCqAMB4AQB0gUGEPjXs9ICkAYBoAYjqAfs1RuoB_PRG6gHltgbqAfC2hvYBwHgBwvSCAcIgGEQARgdmAsB&sigh=doJoJqTltPI&label=vast_creativeview&ad_mt=[AD_MT]\n" +
                "</Tracking>\n" +
                "<Tracking event=\"fullscreen\">\n" +
                "https://pubads.g.doubleclick.net/pagead/conversion/?ai=BtyJFdtL6Xpy2E5KgvwTnoYaQCcCxj-sGAAAAEAEgqN27JjgAWODprcbXAWDlyuWDtA6yARVkZXZlbG9wZXJzLmdvb2dsZS5jb226AQo3Mjh4OTBfeG1syAEF2gFUaHR0cHM6Ly9kZXZlbG9wZXJzLmdvb2dsZS5jb20vaW50ZXJhY3RpdmUtbWVkaWEtYWRzL2RvY3Mvc2Rrcy9odG1sNS9jbGllbnQtc2lkZS90YWdzwAIC4AIA6gIjLzEyNDMxOTA5Ni9leHRlcm5hbC9hZF9ydWxlX3NhbXBsZXP4AvLRHoADAZADmgiYA6wCqAMB4AQB0gUGEPjXs9ICkAYBoAYjqAfs1RuoB_PRG6gHltgbqAfC2hvYBwHgBwvSCAcIgGEQARgdmAsB&sigh=doJoJqTltPI&label=adfullscreen&ad_mt=[AD_MT]\n" +
                "</Tracking>\n" +
                "<Tracking event=\"acceptInvitationLinear\">\n" +
                "https://pubads.g.doubleclick.net/pagead/conversion/?ai=BtyJFdtL6Xpy2E5KgvwTnoYaQCcCxj-sGAAAAEAEgqN27JjgAWODprcbXAWDlyuWDtA6yARVkZXZlbG9wZXJzLmdvb2dsZS5jb226AQo3Mjh4OTBfeG1syAEF2gFUaHR0cHM6Ly9kZXZlbG9wZXJzLmdvb2dsZS5jb20vaW50ZXJhY3RpdmUtbWVkaWEtYWRzL2RvY3Mvc2Rrcy9odG1sNS9jbGllbnQtc2lkZS90YWdzwAIC4AIA6gIjLzEyNDMxOTA5Ni9leHRlcm5hbC9hZF9ydWxlX3NhbXBsZXP4AvLRHoADAZADmgiYA6wCqAMB4AQB0gUGEPjXs9ICkAYBoAYjqAfs1RuoB_PRG6gHltgbqAfC2hvYBwHgBwvSCAcIgGEQARgdmAsB&sigh=doJoJqTltPI&label=acceptinvitation&ad_mt=[AD_MT]\n" +
                "</Tracking>\n" +
                "<Tracking event=\"closeLinear\">\n" +
                "https://pubads.g.doubleclick.net/pagead/conversion/?ai=BtyJFdtL6Xpy2E5KgvwTnoYaQCcCxj-sGAAAAEAEgqN27JjgAWODprcbXAWDlyuWDtA6yARVkZXZlbG9wZXJzLmdvb2dsZS5jb226AQo3Mjh4OTBfeG1syAEF2gFUaHR0cHM6Ly9kZXZlbG9wZXJzLmdvb2dsZS5jb20vaW50ZXJhY3RpdmUtbWVkaWEtYWRzL2RvY3Mvc2Rrcy9odG1sNS9jbGllbnQtc2lkZS90YWdzwAIC4AIA6gIjLzEyNDMxOTA5Ni9leHRlcm5hbC9hZF9ydWxlX3NhbXBsZXP4AvLRHoADAZADmgiYA6wCqAMB4AQB0gUGEPjXs9ICkAYBoAYjqAfs1RuoB_PRG6gHltgbqAfC2hvYBwHgBwvSCAcIgGEQARgdmAsB&sigh=doJoJqTltPI&label=adclose&ad_mt=[AD_MT]\n" +
                "</Tracking>\n" +
                "<Tracking event=\"exitFullscreen\">\n" +
                "https://pubads.g.doubleclick.net/pagead/conversion/?ai=BtyJFdtL6Xpy2E5KgvwTnoYaQCcCxj-sGAAAAEAEgqN27JjgAWODprcbXAWDlyuWDtA6yARVkZXZlbG9wZXJzLmdvb2dsZS5jb226AQo3Mjh4OTBfeG1syAEF2gFUaHR0cHM6Ly9kZXZlbG9wZXJzLmdvb2dsZS5jb20vaW50ZXJhY3RpdmUtbWVkaWEtYWRzL2RvY3Mvc2Rrcy9odG1sNS9jbGllbnQtc2lkZS90YWdzwAIC4AIA6gIjLzEyNDMxOTA5Ni9leHRlcm5hbC9hZF9ydWxlX3NhbXBsZXP4AvLRHoADAZADmgiYA6wCqAMB4AQB0gUGEPjXs9ICkAYBoAYjqAfs1RuoB_PRG6gHltgbqAfC2hvYBwHgBwvSCAcIgGEQARgdmAsB&sigh=doJoJqTltPI&label=vast_exit_fullscreen&ad_mt=[AD_MT]\n" +
                "</Tracking>\n" +
                "</TrackingEvents>\n" +
                "<VideoClicks>\n" +
                "<ClickThrough id=\"GDFP\">\n" +
                "https://pubads.g.doubleclick.net/pcs/click?xai=AKAOjsvDku9AJUH7OGQljwqIRjCBkobvEMsrensd_qs8KgvGNmlffZxWKyxYE9XSMeDiXFVHV4sox977zoYMkhknrYt3CqAKUM9sayMjdQMMcTseobpbbhvgKPql1OZyjbrNFZhK6Y2P31B1rAEFrYLlpSj5o3hVt8ZK30HxlSPeWfh1_GtZed-IvimaI5gyQY97MWi3XrukHnIc69_-6d9OiFmWUFWberQmsr-zC6kCQcnpOkwNYjZRkeHS9Hp2wRg5QuFXWjdzICgUZdQPXtuCVJBlO4El5g&sig=Cg0ArKJSzB4SJveznxbH&adurl=https://developers.google.com/interactive-media-ads/docs/vastinspector_dual\n" +
                "</ClickThrough>\n" +
                "</VideoClicks>\n" +
                "<MediaFiles>\n" +
                "<MediaFile id=\"GDFP\" delivery=\"progressive\" width=\"1280\" height=\"720\" type=\"video/mp4\" bitrate=\"423\" scalable=\"true\" maintainAspectRatio=\"true\">\n" +
                "https://redirector.gvt1.com/videoplayback/id/f4890b9f60d85691/itag/15/source/gfp_video_ads/requiressl/yes/acao/yes/mime/video%2Fmp4/ctier/L/ip/0.0.0.0/ipbits/0/expire/1593517782/sparams/ip,ipbits,expire,id,itag,source,requiressl,acao,mime,ctier/signature/243EA6146036AD334C769D972802967BFEE6B497.70B0739DE7316E24F23D307E87BC55332DF38DB5/key/ck2/file/file.mp4\n" +
                "</MediaFile>\n" +
                "<MediaFile id=\"GDFP\" delivery=\"progressive\" width=\"1280\" height=\"720\" type=\"video/mp4\" bitrate=\"236\" scalable=\"true\" maintainAspectRatio=\"true\">\n" +
                "https://redirector.gvt1.com/videoplayback/id/f4890b9f60d85691/itag/22/source/gfp_video_ads/requiressl/yes/acao/yes/mime/video%2Fmp4/ctier/L/ip/0.0.0.0/ipbits/0/expire/1593517782/sparams/ip,ipbits,expire,id,itag,source,requiressl,acao,mime,ctier/signature/18B40810C8DE5920F18F4285E28B08B73C77F27B.B31791D926500ADD9665D2CDA3A608C0E229C772/key/ck2/file/file.mp4\n" +
                "</MediaFile>\n" +
                "<MediaFile id=\"GDFP\" delivery=\"progressive\" width=\"854\" height=\"480\" type=\"video/webm\" bitrate=\"128\" scalable=\"true\" maintainAspectRatio=\"true\">\n" +
                "https://redirector.gvt1.com/videoplayback/id/f4890b9f60d85691/itag/44/source/gfp_video_ads/requiressl/yes/acao/yes/mime/video%2Fwebm/ctier/L/ip/0.0.0.0/ipbits/0/expire/1593517782/sparams/ip,ipbits,expire,id,itag,source,requiressl,acao,mime,ctier/signature/AE10F7D5983E57A55BA10D218D1DABEF8CE34724.9D06C6675ADFABEF1ED23E4E6C70974E76DC09AE/key/ck2/file/file.webm\n" +
                "</MediaFile>\n" +
                "<MediaFile id=\"GDFP\" delivery=\"progressive\" width=\"1280\" height=\"720\" type=\"video/webm\" bitrate=\"221\" scalable=\"true\" maintainAspectRatio=\"true\">\n" +
                "https://redirector.gvt1.com/videoplayback/id/f4890b9f60d85691/itag/45/source/gfp_video_ads/requiressl/yes/acao/yes/mime/video%2Fwebm/ctier/L/ip/0.0.0.0/ipbits/0/expire/1593517782/sparams/ip,ipbits,expire,id,itag,source,requiressl,acao,mime,ctier/signature/6B576F1F13257D4BF1E798540C18020B55529B87.7C13814CD22E7CF20B24B31DD0E0E1320F392D03/key/ck2/file/file.webm\n" +
                "</MediaFile>\n" +
                "<MediaFile id=\"GDFP\" delivery=\"progressive\" width=\"176\" height=\"144\" type=\"video/3gpp\" bitrate=\"34\" scalable=\"true\" maintainAspectRatio=\"true\">\n" +
                "https://redirector.gvt1.com/videoplayback/id/f4890b9f60d85691/itag/17/source/gfp_video_ads/requiressl/yes/acao/yes/mime/video%2F3gpp/ctier/L/ip/0.0.0.0/ipbits/0/expire/1593517782/sparams/ip,ipbits,expire,id,itag,source,requiressl,acao,mime,ctier/signature/40A7A186F1D47598025154C197DA7551FA4A54F0.66E2DEE429DC0FA5F77BBFA1D8D41644EB03F9E9/key/ck2/file/file.3gp\n" +
                "</MediaFile>\n" +
                "<MediaFile id=\"GDFP\" delivery=\"progressive\" width=\"320\" height=\"180\" type=\"video/3gpp\" bitrate=\"65\" scalable=\"true\" maintainAspectRatio=\"true\">\n" +
                "https://redirector.gvt1.com/videoplayback/id/f4890b9f60d85691/itag/36/source/gfp_video_ads/requiressl/yes/acao/yes/mime/video%2F3gpp/ctier/L/ip/0.0.0.0/ipbits/0/expire/1593517782/sparams/ip,ipbits,expire,id,itag,source,requiressl,acao,mime,ctier/signature/BAC411C8C9FE72B93983D9E93379BCB602743E12.9DB7AE35723EEBA093697B3B1C44ED492263854D/key/ck2/file/file.3gp\n" +
                "</MediaFile>\n" +
                "<MediaFile id=\"GDFP\" delivery=\"progressive\" width=\"640\" height=\"360\" type=\"video/webm\" bitrate=\"117\" scalable=\"true\" maintainAspectRatio=\"true\">\n" +
                "https://redirector.gvt1.com/videoplayback/id/f4890b9f60d85691/itag/43/source/gfp_video_ads/requiressl/yes/acao/yes/mime/video%2Fwebm/ctier/L/ip/0.0.0.0/ipbits/0/expire/1593517782/sparams/ip,ipbits,expire,id,itag,source,requiressl,acao,mime,ctier/signature/4314C8054E0A9CCC12C65F24D2BEC4768020899A.AC8D24C74C74CEAB27D77CEB053EE101F0AB4864/key/ck2/file/file.webm\n" +
                "</MediaFile>\n" +
                "<MediaFile id=\"GDFP\" delivery=\"progressive\" width=\"640\" height=\"360\" type=\"video/mp4\" bitrate=\"116\" scalable=\"true\" maintainAspectRatio=\"true\">\n" +
                "https://redirector.gvt1.com/videoplayback/id/f4890b9f60d85691/itag/18/source/gfp_video_ads/requiressl/yes/acao/yes/mime/video%2Fmp4/ctier/L/ip/0.0.0.0/ipbits/0/expire/1593517782/sparams/ip,ipbits,expire,id,itag,source,requiressl,acao,mime,ctier/signature/601A0B3A24601B2601A5F0DD6A9AEA7A5838108E.62A8FBC619896BCF61C07A3B36A554E8EC410616/key/ck2/file/file.mp4\n" +
                "</MediaFile>\n" +
                "</MediaFiles>\n" +
                "</Linear>\n" +
                "</Creative>\n" +
                "<Creative id=\"57857370976\" sequence=\"1\">\n" +
                "<CompanionAds>\n" +
                "<Companion id=\"57857370976\" width=\"300\" height=\"250\">\n" +
                "<StaticResource creativeType=\"image/png\">\n" +
                "https://pagead2.googlesyndication.com/simgad/14568666361913360151\n" +
                "</StaticResource>\n" +
                "<TrackingEvents>\n" +
                "<Tracking event=\"creativeView\">\n" +
                "https://securepubads.g.doubleclick.net/pcs/view?xai=AKAOjsvR4meVkrv2h42e1idfgGoLplpNhePB6jU0dM6I9QaF-xatnhPLWJjhh1zETgIntDWvtSI4KSZg_mFZjjv-6AR9aiVl9FavXJj-RUinh465g4qXzI5vILoED-cUxFQKZ6iCjUyXcrrYFIM7pZjdmZhVU0fs2DcxKhi_bbr_jEZP3_MaPYfcFgsdhcpjEnCetq5QbDRk4M6TjjCk5wF1vTGvT1-m-F_wRes2ndlrqrsggYkBhQQlWr9yR2dKrpTs9IOy7GRZmLo2j_g9-UJeRYSyGiRxg1y0Ig&sig=Cg0ArKJSzDmvqIYMm3xqEAE&adurl=\n" +
                "</Tracking>\n" +
                "</TrackingEvents>\n" +
                "<CompanionClickThrough>\n" +
                "https://pubads.g.doubleclick.net/pcs/click?xai=AKAOjsuO3RUAKpq6wKfGV_TR4H2B-lrj0xfxzt8fZCiseUjLwmgv_gRutwaccnXsbmDM9cS6QQdFj-GbLBh2mebHSccfwlA7623zCJLwmG4U-IedGB1TfmjiKOZtBgd3fOFO1DBBX19Y8KHwNFYyoyxoGJWB7E6gcZy8gnVBFE3Gjn6YRHjjllTsjExPCTpEM_PEePAsCoDB6Vplf-QaO0Vml2aMDAoIwNYTW63dOTOhu3ANtLet7axOavRnfv2luZmaYxcKZlfsLuBGTvyja5xLs6cl4mZB0Q&sig=Cg0ArKJSzJl4jNMFgxGz&adurl=https://developers.google.com/interactive-media-ads/docs/vastinspector_dual\n" +
                "</CompanionClickThrough>\n" +
                "</Companion>\n" +
                "</CompanionAds>\n" +
                "</Creative>\n" +
                "</Creatives>\n" +
                "<Extensions>\n" +
                "<Extension type=\"waterfall\" fallback_index=\"0\"/>\n" +
                "<Extension type=\"geo\">\n" +
                "<Country>IN</Country>\n" +
                "<Bandwidth>3</Bandwidth>\n" +
                "<BandwidthKbps>2660</BandwidthKbps>\n" +
                "</Extension>\n" +
                "<Extension type=\"metrics\">\n" +
                "<FeEventId>dtL6XoTZEsukwgO814PYAQ</FeEventId>\n" +
                "<AdEventId>CJzJl-_rqOoCFRLQjwod55ABkg</AdEventId>\n" +
                "</Extension>\n" +
                "<Extension type=\"ShowAdTracking\">\n" +
                "<CustomTracking>\n" +
                "<Tracking event=\"show_ad\">\n" +
                "https://securepubads.g.doubleclick.net/pcs/view?xai=AKAOjst5fFJdPCT18pWAjs1yY0WSZN3x1-WAyAV3o0AGHTc7o3Fx2zS6OLDqwmvLgsP_FM4N5acRkLHNG9JrwOToAYWXsydud9C9Ga1r7yVy8YxyefGePsO9HrmWY3WpZDhwnIqNz5w4EEQdc94lIC7OAGhagI-QWnwYBP3WOZ9fnQpB09lcjA_vofKgn77v6Ahaf5HJk8tx4dByCgkEMgRP7jhxap-LENOMk4GuKT8XIPOIbDjOXgld7QeRo5evnO0pNO6pH0ylJA3mGEWmI4dReKzfGTccGSYyucqR&sig=Cg0ArKJSzOH3HNxuBUDMEAE&adurl=\n" +
                "</Tracking>\n" +
                "</CustomTracking>\n" +
                "</Extension>\n" +
                "<Extension type=\"video_ad_loaded\">\n" +
                "<CustomTracking>\n" +
                "<Tracking event=\"loaded\">\n" +
                "https://pubads.g.doubleclick.net/pagead/conversion/?ai=BtyJFdtL6Xpy2E5KgvwTnoYaQCcCxj-sGAAAAEAEgqN27JjgAWODprcbXAWDlyuWDtA6yARVkZXZlbG9wZXJzLmdvb2dsZS5jb226AQo3Mjh4OTBfeG1syAEF2gFUaHR0cHM6Ly9kZXZlbG9wZXJzLmdvb2dsZS5jb20vaW50ZXJhY3RpdmUtbWVkaWEtYWRzL2RvY3Mvc2Rrcy9odG1sNS9jbGllbnQtc2lkZS90YWdzwAIC4AIA6gIjLzEyNDMxOTA5Ni9leHRlcm5hbC9hZF9ydWxlX3NhbXBsZXP4AvLRHoADAZADmgiYA6wCqAMB4AQB0gUGEPjXs9ICkAYBoAYjqAfs1RuoB_PRG6gHltgbqAfC2hvYBwHgBwvSCAcIgGEQARgdmAsB&sigh=doJoJqTltPI&label=video_ad_loaded\n" +
                "</Tracking>\n" +
                "</CustomTracking>\n" +
                "</Extension>\n" +
                "</Extensions>\n" +
                "</InLine>\n" +
                "</Ad>\n" +
                "</VAST>").setVideoMimeTypes(videoMimeTypes).setAlwaysStartWithPreroll(true).setAdLoadTimeOut(8);
    }

    //IMA DAI CONFIG
    private void addIMADAIPluginConfig(PKPluginConfigs config, int daiType) {
        switch (daiType) {
            case 1: {
                promptMessage(DAI_PLUGIN, getDAIConfig5_1().getAssetTitle());
                IMADAIConfig adsConfig = getDAIConfig5_1();
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
            case 7: {
                promptMessage(DAI_PLUGIN, getDAIConfig6().getAssetTitle());
                IMADAIConfig adsConfigError = getDAIConfig7();
                config.setPluginConfig(IMADAIPlugin.factory.getName(), adsConfigError);
            }
            case 8: {
                promptMessage(DAI_PLUGIN, getDAIConfig6().getAssetTitle());
                IMADAIConfig adsConfigError = getDAIConfig8();
                config.setPluginConfig(IMADAIPlugin.factory.getName(), adsConfigError);
            }
            case 9: {
                promptMessage(DAI_PLUGIN, getDAIConfig6().getAssetTitle());
                IMADAIConfig adsConfigError = getDAIConfig9();
                config.setPluginConfig(IMADAIPlugin.factory.getName(), adsConfigError);
            }
            case 10: {
                promptMessage(DAI_PLUGIN, getDAIConfig6().getAssetTitle());
                IMADAIConfig adsConfigError = getDAIConfig10();
                config.setPluginConfig(IMADAIPlugin.factory.getName(), adsConfigError);
            }
            case 11: {
                promptMessage(DAI_PLUGIN, getDAIConfigISSUE().getAssetTitle());
                IMADAIConfig adsConfigError = getDAIConfigISSUE();
                config.setPluginConfig(IMADAIPlugin.factory.getName(), adsConfigError);
            }
            case 12: {
                promptMessage(DAI_PLUGIN, getDAIConfigDASHFROMSAMPLE().getAssetTitle());
                IMADAIConfig adsConfigError = getDAIConfigDASHFROMSAMPLE();
                config.setPluginConfig(IMADAIPlugin.factory.getName(), adsConfigError);
            }
            case 13: {
                promptMessage(DAI_PLUGIN, getDAIConfigLiveHlsZee5().getAssetTitle());
                IMADAIConfig adsConfig13 = getDAIConfigLiveHlsZee5();
                config.setPluginConfig(IMADAIPlugin.factory.getName(), adsConfig13);
            }
            break;
            default:
                break;
        }
    }

    private void promptMessage(String type, String title) {
        Toast.makeText(this, type + " " + title, Toast.LENGTH_SHORT).show();
    }

    private IMADAIConfig getDAIConfig7() {
        String assetTitle = "DD Rajasthan";
        String assetKey = "Hs8aTJFeSCq7kWEvQiSGVg";
        String apiKey = null;
        StreamRequest.StreamFormat streamFormat = StreamRequest.StreamFormat.HLS;
        String licenseUrl = null;
        return IMADAIConfig.getLiveIMADAIConfig(assetTitle,
                assetKey,
                apiKey,
                streamFormat,
                licenseUrl).setAlwaysStartWithPreroll(true).enableDebugMode(true);
    }

    private IMADAIConfig getDAIConfig8() {
        String assetTitle = "DD MP";
        String assetKey = "zbV5Tfp0TJaHDssgBrFWOA";
        String apiKey = null;
        StreamRequest.StreamFormat streamFormat = StreamRequest.StreamFormat.HLS;
        String licenseUrl = null;
        return IMADAIConfig.getLiveIMADAIConfig(assetTitle,
                assetKey,
                apiKey,
                streamFormat,
                licenseUrl).setAlwaysStartWithPreroll(true).enableDebugMode(true);
    }

    private IMADAIConfig getDAIConfig9() {
        String assetTitle = "DD National";
        String assetKey = "rJNQu3LRR2q6_izClfioA";
        String apiKey = null;
        StreamRequest.StreamFormat streamFormat = StreamRequest.StreamFormat.HLS;
        String licenseUrl = null;
        return IMADAIConfig.getLiveIMADAIConfig(assetTitle,
                assetKey,
                apiKey,
                streamFormat,
                licenseUrl).setAlwaysStartWithPreroll(true).enableDebugMode(true);
    }

    private IMADAIConfig getDAIConfig10() {
        String assetTitle = "Zee News";
        String assetKey = "W1x4XiDkR5yE7K9amJwTDg";
        String apiKey = null;
        StreamRequest.StreamFormat streamFormat = StreamRequest.StreamFormat.HLS;
        String licenseUrl = null;
        return IMADAIConfig.getLiveIMADAIConfig(assetTitle,
                assetKey,
                apiKey,
                streamFormat,
                licenseUrl).setAlwaysStartWithPreroll(true).enableDebugMode(true);
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

    private IMADAIConfig getDAIConfigLiveHlsZee5() {
        String assetTitle = "Live Video - Sample";
        String assetKey = "Hs8aTJFeSCq7kWEvQiSGVg";
        String apiKey = null;
        StreamRequest.StreamFormat streamFormat = StreamRequest.StreamFormat.HLS;
        String licenseUrl = null;
        return IMADAIConfig.getLiveIMADAIConfig(assetTitle,
                assetKey,
                apiKey,
                streamFormat,
                licenseUrl).setAlwaysStartWithPreroll(true).enableDebugMode(true);
    }

    private IMADAIConfig getDAIConfigISSUE() {
        String assetTitle = "VOD - Tears of Steel";
        String apiKey = null;
        String contentSourceId = "19463";
        String videoId = "tears-of-steel";
        StreamRequest.StreamFormat streamFormat = StreamRequest.StreamFormat.HLS;
        String licenseUrl = null;

        return IMADAIConfig.getVodIMADAIConfig(assetTitle,
                contentSourceId,
                videoId,
                apiKey,
                streamFormat,
                licenseUrl).enableDebugMode(true);
    }

    private IMADAIConfig getDAIConfigDASHFROMSAMPLE() {
        String assetTitle = "VOD - DASH";
        String apiKey = null;
        String contentSourceId = "2474148";
        String videoId = "bbb-clear";
        StreamRequest.StreamFormat streamFormat = StreamRequest.StreamFormat.DASH;
        String licenseUrl = null;

        return IMADAIConfig.getVodIMADAIConfig(assetTitle,
                contentSourceId,
                videoId,
                apiKey,
                streamFormat,
                licenseUrl).enableDebugMode(true);
    }

    @Override
    protected void onPause() {


        if (controlsView != null) {
            controlsView.release();
        }
        if (player != null) {
            player.onApplicationPaused();
        }
        super.onPause();
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

        player.addListener(this, AdEvent.adProgress, event -> {
            // log.d("AD_PROGRESS " + event.currentAdPosition);
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

        player.addListener(this, AdEvent.error, event -> {
            log.d("AdEvent.error = " + event.error.severity.name());
//            log.d("AdEvent.category = " + event.error.errorCategory.name());
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
                log.e("ERROR: " + event.error.errorType + ", " + event.error.message);
            }
        });

        player.addListener(this, AdEvent.skipped, event -> {
            log.d("Ad Event SKIPPED");
            nowPlaying = true;
        });

        player.addListener(this, PlayerEvent.surfaceAspectRationSizeModeChanged, event -> {
            log.d("resizeMode updated" + event.resizeMode);
        });

        player.addListener(this, PlayerEvent.subtitlesStyleChanged, event -> {
            log.d("subtitlesStyleChanged " + event.styleName);
        });

        player.addListener(this, PlayerEvent.volumeChanged, event -> {
            log.d("volume == " + event.volume);
        });

        player.addListener(this, PlayerEvent.durationChanged, event -> {
            log.d("durationChanged == " + event.duration);
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

        player.addListener(this, PlayerEvent.Type.PLAYHEAD_UPDATED, event -> {
            //   log.d("playheadUpdated play position " + event.eventType().toString());
        });

        player.addListener(this, PlayerEvent.videoTrackChanged, event -> {
            log.d("videoTrackChanged " + event.newTrack.getBitrate());
        });

        player.addListener(this, PlayerEvent.playbackRateChanged, event -> {
            log.d("playbackRateChanged event  rate = " + event.rate);
        });

        player.addListener(this, PlayerEvent.tracksAvailable, event -> {
            //When the track data available, this event occurs. It brings the info object with it.
            tracksInfo = event.tracksInfo;
            log.d("tracksInfo.getAudioTracks() Gourav = " + tracksInfo.getAudioTracks());
            populateSpinnersWithTrackInfo(event.tracksInfo);
        });

        player.addListener(this, PlayerEvent.sourceSelected, event -> {
            log.d("sourceSelected event source = " + event.source.getUrl());
            //    tvSourceUrl.setText(event.source.getUrl());
        });

        player.addListener(this, PlayerEvent.playbackRateChanged, event -> {
            log.d("playbackRateChanged event  rate = " + event.rate);
        });

        player.addListener(this, PlayerEvent.error, event -> {
            //When the track data available, this event occurs. It brings the info object with it.
            if (event != null && event.error != null) {
                log.e("PlayerEvent.Error event  position = " + event.error.errorType + " errorMessage = " + event.error.message +
                        " event.error.errorCategory = " + event.error.errorCategory + " event.error.exception = " + event.error.exception
                        + " event.error.severity = " + event.error.severity);
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

    private void setExternalSubtitles(PKMediaEntry mediaEntry, boolean whichOne) {

        List<PKExternalSubtitle> subsList = new ArrayList<>();

//        PKExternalSubtitle pkExternalSubtitle = new PKExternalSubtitle()
//                .setUrl("https://vootvod.cdn.jio.com/s/enc/hls/p/1982551/sp/198255100/serveFlavor/entryId/0_wlu0b9eu/v/2/pv/1/ev/12/flavorId/0_t14tfa52/name/a.srt/index.m3u8/index.m3u8?__hdnea__=st=1597065030~exp=1597151430~acl=/s/enc/hls/p/1982551/sp/198255100/serveFlavor/entryId/0_wlu0b9eu/v/2/pv/1/ev/12/flavorId/0_*~hmac=dbebec71dfcdfdad2cf36dc340fc142bdc6640c65377d30a6aa9dfaf1fa68412")
//                .setMimeType(PKSubtitleFormat.vtt)
//                .setLabel("Voot-Buggy-en1")
//                .setLanguage(null);
//        subsList.add(pkExternalSubtitle);

//        PKExternalSubtitle pkExternalSubtitle1 = new PKExternalSubtitle()
//                .setUrl("https://vootvod.cdn.jio.com/s/enc/hls/p/1982551/sp/198255100/serveFlavor/entryId/0_wlu0b9eu/v/2/pv/1/ev/12/flavorId/0_t14tfa52/name/a.srt/index.m3u8/index.m3u8?__hdnea__=st=1597065030~exp=1597151430~acl=/s/enc/hls/p/1982551/sp/198255100/serveFlavor/entryId/0_wlu0b9eu/v/2/pv/1/ev/12/flavorId/0_*~hmac=dbebec71dfcdfdad2cf36dc340fc142bdc6640c65377d30a6aa9dfaf1fa68412")
//                .setMimeType(PKSubtitleFormat.srt)
//                .setLabel("Voot-Buggy-en2")
//                .setLanguage("en");
//        subsList.add(pkExternalSubtitle1);

        if (whichOne) {
            PKExternalSubtitle pkExternalSubtitleDe = new PKExternalSubtitle()
                    .setUrl("https://zee5vod.akamaized.net/drm1/elemental/dash/TV_SHOWS/ZEE_TV/January2021/04012021/Seamless/Kundali_Bhagya_CS_Ep854_Seamless_04012021_hi_f417caa5eb2c6a28aee15696f6c534cf_kaltura/manifest-en.vtt")
                    .setMimeType(PKSubtitleFormat.vtt)
                    .setLabel("Zee5-Error-VTT")
                    .setDefault()
                    .setLanguage("hi");
            subsList.add(pkExternalSubtitleDe);
        } else {
            PKExternalSubtitle pkExternalSubtitlejaC = new PKExternalSubtitle()
                    .setUrl("https://zee5vod.akamaized.net/drm1/1080p/movies/MOVIE_PROJECT/KANNADA/19122018/TIGER_MOVIE_kn.mp4/thumbnails/index.vtt")
                    .setMimeType(PKSubtitleFormat.vtt)
                    .setLabel("Zee5-working")
                    .setDefault()
                    .setLanguage("ja");
            subsList.add(pkExternalSubtitlejaC);
        }

//        PKExternalSubtitle pkExternalSubtitleDe1 = new PKExternalSubtitle()
//                .setUrl("https://zee5vod.akamaized.net/drm1/elemental/dash/Movies/TIGER_Revised_20112018_Hindi_Movie_REX_hi_292ea10f271d2643bfe36430772992a4/thumbnails/in")
//                .setMimeType(null)
//                .setLabel("Zee-2-russian2")
//                .setLanguage("ru");
//        subsList.add(pkExternalSubtitleDe1);

//        PKExternalSubtitle pkExternalSubtitlefr = new PKExternalSubtitle()
//                .setUrl("https://zee5vod.akamaized.net/drm1/1080p/movies/MOVIE_PROJECT/KANNADA/19122018/TIGER_MOVIE_kn.mp4/thumbnails/index.vtt")
//                .setMimeType(PKSubtitleFormat.vtt)
//                .setLabel("Zee-1-french")
//                .setLanguage("fr");
//        subsList.add(pkExternalSubtitlefr);

//        PKExternalSubtitle pkExternalSubtitlefr1 = new PKExternalSubtitle()
//                .setUrl("https://zee5vod.akamaized.net/drm1/elemental/dash/Movies/TIGER_Revised_20112018_Hindi_Movie_REX_hi_292ea10f271d2643bfe36430772992a4/thumbnails/index.vtt")
//                .setMimeType(PKSubtitleFormat.vtt)
//                .setLabel("Zee-2-french-1")
//                .setDefault()
//                .setLanguage("fr");
//        subsList.add(pkExternalSubtitlefr1);

//        PKExternalSubtitle pkExternalSubtitleja = new PKExternalSubtitle()
//                .setUrl("https://zee5vod.akamaized.net/drm1/1080p/movies/MOVIE_PROJECT/KANNADA/19122018/TIGER_MOVIE_kn.mp4/thumbnails/index.vtt")
//                .setMimeType(PKSubtitleFormat.vtt)
//                .setLabel("Zee-1-japan")
//                .setDefault()
//                .setLanguage("ja");
//        subsList.add(pkExternalSubtitleja);

//        PKExternalSubtitle pkExternalSubtitleja1 = new PKExternalSubtitle()
//                .setUrl("https://zee5vod.akamaized.net/drm1/elemental/dash/Movies/TIGER_Revised_20112018_Hindi_Movie_REX_hi_292ea10f271d2643bfe36430772992a4/thumbnails/index.vtt")
//                .setMimeType(PKSubtitleFormat.vtt)
//                .setLabel("Zee-2-japan")
//                .setLanguage("ja");
//        subsList.add(pkExternalSubtitleja1);

//        PKExternalSubtitle pkExternalSubtitlehe = new PKExternalSubtitle()
//                .setUrl("https://zee5vod.akamaized.net/drm1/1080p/movies/MOVIE_PROJECT/KANNADA/19122018/TIGER_MOVIE_kn.mp4/thumbnails/index.vtt")
//                .setMimeType(PKSubtitleFormat.vtt)
//                .setLabel("Zee-1-hebrew")
//                .setLanguage("Heb");
//        subsList.add(pkExternalSubtitlehe);




        mediaEntry.setExternalSubtitleList(subsList);
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

    @SuppressLint("SourceLockedOrientationActivity")
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


    /*private void startVootOttMediaLoadingProd(final OnMediaLoadCompletion completion, String mediaId) {

        log.i("mediaID:= " + mediaId);

        SessionProvider ksSessionProvider = new SessionProvider() {
            @Override
            public String baseUrl() {
                String PhoenixBaseUrl = "https://rest-sgs1.ott.kaltura.com/restful_V5_0/api_v3/";//"https://rest-sgs1.ott.kaltura.com/v4_4/api_v3/";//"https://rest-as.ott.kaltura.com/v4_4/api_v3/";
                return PhoenixBaseUrl;
            }

            @Override
            public void getSessionToken(OnCompletion<PrimitiveResult> completion) {
                // Anonymous KS
                // String PnxKS = "djJ8MjI1fHn4Cw4QmXr7PQthpXSlbprIrYuEpQvGjLJalUJ92dtMr__2yaBWlue36tfYuIIlcPSQdxbAQWM6i4tjwCrVx-hL8DkcpiS7dJj7jp0SYpyXrgHtpMCQyiJaOgSUGhlT2pwgOJfseFbuxOfzdbXzAIU=";
                // Login without UDID
                // String PnxKS = "djJ8MjI1fN-nTOp-3LkYCTaQgL11VAhRxe3N2wGXeevoyCOEVfY6CapnQFyI4M623ouxbYfaNGynur-MqJF5K9s4R84hQaMRcLbPOdsA629HMJJP-pjfEWPkzJ7M5vv4QyV14SD_vlaK6KB2lmYIi1iMS4Iu65A=";
                // Login with UDID but household is not added
                //  String PnxKS = "djJ8MjI1fLKcf44eXSWVdrzzV4Q4EZ8b_A107ZNSRfu7zTrL5d6YJI_58kQv7yIvRxkXhqTeiRCIJFRLAqb3nZ6xLo0Xr-evpADcWbMLdXkplx7LADiZ7ZsR8_EcSKZ8QbRF-A8DcIF3GKxMtVMosW29nHMn9zGvF0j30nwLNNL9PUwMykcWle-68FOMDaPU9sVEEvLTmA==";
                // Now household added but subscription not purchased yet
                //  String PnxKS = "djJ8MjI1fMiDyURBbrmn1-0miHqpxZBU0Juvjy-jtSvmMQqVe2WqZK8mNPi98x8ma5zPORbPV1mL5v25mA6NNjejDERAsChUNLzqejRgvZarK-elL8VfYUy1EHx0E-cdJ36WMNFfYJS1dcmi2kxjGwY4jiYJDmlgUPx0EiKn6_PiLsyHZdg4vW7Ne4w0T_78_iiBDtm40g==";
                // Subsription purchased



              //   String PnxKS = "djJ8MjI1fNRrd0CRif0eHdiJqKSmkadrMYj70ry0-9J_1oixwxK2fxm2mvjFpV2pV4rNFf8dRMKijfILmld5q8mpVcGjfhbJx-03greh4EVr0O2LujTT_Ysce9WbSi2i7vl_1X6bztD938a6BZ9hF92WwI-VUIpVDoVEPP4n0VXc3M61i2fc";

                String ks = "djJ8MjI1fJqYQoKdrm0lWI2qygW7jk4EYCjgnBhqzHQ4PVBsRTNQ0D1s7FcYMH4xikLtWCX95Vcc4Uw0MUgMlabou_0cMpXF3DbCGce-5CU635jnqhD8zZqQ5kGeuCj3ylu1m45VYTVrIfB3p3IGX47OMshGbBnY0gLVuSM7F2IPAMeW6pxoTXdRMdp4Ois-SGtfkca2XQ==";
                if (completion != null) {
                    completion.onComplete(new PrimitiveResult(ks));
                }
            }

            @Override
            public int partnerId() {
                int OttPartnerId = 225;
                return OttPartnerId;
            }
        };

        //  SimpleSessionProvider ksSessionProvider = new SimpleSessionProvider("https://rest-as.ott.kaltura.com/v4_4/api_v3", 255, null);

        String mediaIdPhoenix = "331593";
        //String formatHls = fileType % 2 == 0 ? "Tablet Main" : "dash Main";
        String formatHls = "dash Mobile";
        //audio
        // String mediaId = "743297";
        // String formatHls  = "audio_only";
        //  String formatDash  = "dash Main";
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
                .setAssetId(mediaIdPhoenix)
                //.setReferrer()
                .setFileIds()
                .setProtocol(PhoenixMediaProvider.HttpProtocol.Https)
                .setContextType(APIDefines.PlaybackContextType.Playback)
                .setAssetType(APIDefines.KalturaAssetType.Media)
                //  .setPKUrlType(APIDefines.PKUrlType.PlayManifest)
                .setFormats(formatHls);
        //.setFormats(formatDash);

        mediaProvider.load(completion);
        fileType++;
        if (fileType > 10) {
            fileType = 0;
        }
    }*/

    private void startVootOttMediaLoadingStaging(final OnMediaLoadCompletion completion, String mediaId) {

        log.i("mediaID:= " + mediaId);

        SessionProvider ksSessionProvider = new SessionProvider() {
            @Override
            public String baseUrl() {
                String PhoenixBaseUrl = "https://rest-sgs1.ott.kaltura.com/v5_3_5/api_v3/"; //"https://rest-sgs1.ott.kaltura.com/restful_v5_3_5/api_v3/";//"https://rest-as.ott.kaltura.com/v4_4/api_v3/";
                return PhoenixBaseUrl;
            }

            @Override
            public void getSessionToken(OnCompletion<PrimitiveResult> completion) {
                // Anonymous KS
                // String PnxKS = "djJ8MjI1fHn4Cw4QmXr7PQthpXSlbprIrYuEpQvGjLJalUJ92dtMr__2yaBWlue36tfYuIIlcPSQdxbAQWM6i4tjwCrVx-hL8DkcpiS7dJj7jp0SYpyXrgHtpMCQyiJaOgSUGhlT2pwgOJfseFbuxOfzdbXzAIU=";
                // Login without UDID
                // String PnxKS = "djJ8MjI1fN-nTOp-3LkYCTaQgL11VAhRxe3N2wGXeevoyCOEVfY6CapnQFyI4M623ouxbYfaNGynur-MqJF5K9s4R84hQaMRcLbPOdsA629HMJJP-pjfEWPkzJ7M5vv4QyV14SD_vlaK6KB2lmYIi1iMS4Iu65A=";
                // Login with UDID but household is not added
                //  String PnxKS = "djJ8MjI1fLKcf44eXSWVdrzzV4Q4EZ8b_A107ZNSRfu7zTrL5d6YJI_58kQv7yIvRxkXhqTeiRCIJFRLAqb3nZ6xLo0Xr-evpADcWbMLdXkplx7LADiZ7ZsR8_EcSKZ8QbRF-A8DcIF3GKxMtVMosW29nHMn9zGvF0j30nwLNNL9PUwMykcWle-68FOMDaPU9sVEEvLTmA==";
                // Now household added but subscription not purchased yet
                //  String PnxKS = "djJ8MjI1fMiDyURBbrmn1-0miHqpxZBU0Juvjy-jtSvmMQqVe2WqZK8mNPi98x8ma5zPORbPV1mL5v25mA6NNjejDERAsChUNLzqejRgvZarK-elL8VfYUy1EHx0E-cdJ36WMNFfYJS1dcmi2kxjGwY4jiYJDmlgUPx0EiKn6_PiLsyHZdg4vW7Ne4w0T_78_iiBDtm40g==";
                // Subsription purchased



                //   String PnxKS = "djJ8MjI1fNRrd0CRif0eHdiJqKSmkadrMYj70ry0-9J_1oixwxK2fxm2mvjFpV2pV4rNFf8dRMKijfILmld5q8mpVcGjfhbJx-03greh4EVr0O2LujTT_Ysce9WbSi2i7vl_1X6bztD938a6BZ9hF92WwI-VUIpVDoVEPP4n0VXc3M61i2fc";

                //  String ks = "djJ8MjI1fJqYQoKdrm0lWI2qygW7jk4EYCjgnBhqzHQ4PVBsRTNQ0D1s7FcYMH4xikLtWCX95Vcc4Uw0MUgMlabou_0cMpXF3DbCGce-5CU635jnqhD8zZqQ5kGeuCj3ylu1m45VYTVrIfB3p3IGX47OMshGbBnY0gLVuSM7F2IPAMeW6pxoTXdRMdp4Ois-SGtfkca2XQ==";
                if (completion != null) {
                    //  completion.onComplete(new PrimitiveResult(V18_STAGING_KS));
                    completion.onComplete(new PrimitiveResult("djJ8MjI1fFL5RCO53XgoCzWyEQDpOixJMMOEQve5n6LCNHBMsKLVBAg3rnvLw2_knDnvin45oRE-kis0t_ruysSQ2bPmNH-sYBPdE-z6OkjSSNCRXaVu4lvS4k8CQ28MaWsECH91AtPH5nOgOIeAMLuYKAmUrfxzMlnhVwCHTLrouSscfUTniy5GOC91QKjSy4YKPA27CK4mw4SQBwC1fUXDIsZsd3d-bhxCy12jU8qkD8acLQOUs-QNa94qZ_EnUSh0RPszLtnfF8d0Bxs8TjNLwlVp7k9UHWWj876RhIAlIkexDn08gspxuRKNHLdDd3MPDLodv3MgRmYcgrfMxmBwJ4Aw-XVh7TwZJdhK21MTB3ozsU3L60i2Mk6NOdp3KIQJ1wVqRg=="));
                }
            }

            @Override
            public int partnerId() {
                int OttPartnerId = 225;
                return OttPartnerId;
            }
        };

        //  SimpleSessionProvider ksSessionProvider = new SimpleSessionProvider("https://rest-as.ott.kaltura.com/v4_4/api_v3", 255, null);

        String mediaIdPhoenix = mediaId;
        //String formatHls = fileType % 2 == 0 ? "Tablet Main" : "dash Main";
        //  String formatHls = "dashclear";
        //String formatHls = "DASHENC_TV_PremiumHD";
        //String formatHls = "DASHENC_TV_PremiumHD";
        //  String formatHls = "dash Main";
        //String formatHls = "dash_widevine";

        //  String formatHls = "dashclear";
        //  String formatHls = "HEVCDASH";

        //audio
        // String mediaId = "743297";
        // String formatHls  = "audio_only";
        String formatHls  = "audio_only_dash";
        //  String formatDash  = "dash Main";
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
                .setAssetId(mediaIdPhoenix)
                //.setReferrer()
                //.setFileIds("792385") // Devive not in household wala skype ka issue
                .setProtocol(PhoenixMediaProvider.HttpProtocol.Https)
                .setContextType(APIDefines.PlaybackContextType.Playback)
                .setAssetType(APIDefines.KalturaAssetType.Media)
                //  .setPKUrlType(APIDefines.PKUrlType.PlayManifest)
                .setFormats(formatHls);
        //  .setFormats("dashclear", "DASHENC_PremiumHD");
        //.setFormats(formatDash);

        mediaProvider.load(completion);

    }

    private void startVootOttMediaLoadingProd(final OnMediaLoadCompletion completion, String mediaId, String format) {

        log.i("mediaID:= " + mediaId);

        SessionProvider ksSessionProvider = new SessionProvider() {
            @Override
            public String baseUrl() {
                String PhoenixBaseUrl = "https://rest-as.ott.kaltura.com/v4_4/api_v3/"; //"https://rest-sgs1.ott.kaltura.com/restful_v5_3_5/api_v3/";//"https://rest-as.ott.kaltura.com/v4_4/api_v3/";
                return PhoenixBaseUrl;
            }

            @Override
            public void getSessionToken(OnCompletion<PrimitiveResult> completion) {
                // Anonymous KS
                // String PnxKS = "djJ8MjI1fHn4Cw4QmXr7PQthpXSlbprIrYuEpQvGjLJalUJ92dtMr__2yaBWlue36tfYuIIlcPSQdxbAQWM6i4tjwCrVx-hL8DkcpiS7dJj7jp0SYpyXrgHtpMCQyiJaOgSUGhlT2pwgOJfseFbuxOfzdbXzAIU=";
                // Login without UDID
                // String PnxKS = "djJ8MjI1fN-nTOp-3LkYCTaQgL11VAhRxe3N2wGXeevoyCOEVfY6CapnQFyI4M623ouxbYfaNGynur-MqJF5K9s4R84hQaMRcLbPOdsA629HMJJP-pjfEWPkzJ7M5vv4QyV14SD_vlaK6KB2lmYIi1iMS4Iu65A=";
                // Login with UDID but household is not added
                //  String PnxKS = "djJ8MjI1fLKcf44eXSWVdrzzV4Q4EZ8b_A107ZNSRfu7zTrL5d6YJI_58kQv7yIvRxkXhqTeiRCIJFRLAqb3nZ6xLo0Xr-evpADcWbMLdXkplx7LADiZ7ZsR8_EcSKZ8QbRF-A8DcIF3GKxMtVMosW29nHMn9zGvF0j30nwLNNL9PUwMykcWle-68FOMDaPU9sVEEvLTmA==";
                // Now household added but subscription not purchased yet
                //  String PnxKS = "djJ8MjI1fMiDyURBbrmn1-0miHqpxZBU0Juvjy-jtSvmMQqVe2WqZK8mNPi98x8ma5zPORbPV1mL5v25mA6NNjejDERAsChUNLzqejRgvZarK-elL8VfYUy1EHx0E-cdJ36WMNFfYJS1dcmi2kxjGwY4jiYJDmlgUPx0EiKn6_PiLsyHZdg4vW7Ne4w0T_78_iiBDtm40g==";
                // Subsription purchased



                //   String PnxKS = "djJ8MjI1fNRrd0CRif0eHdiJqKSmkadrMYj70ry0-9J_1oixwxK2fxm2mvjFpV2pV4rNFf8dRMKijfILmld5q8mpVcGjfhbJx-03greh4EVr0O2LujTT_Ysce9WbSi2i7vl_1X6bztD938a6BZ9hF92WwI-VUIpVDoVEPP4n0VXc3M61i2fc";

                //  String ks = "djJ8MjI1fJqYQoKdrm0lWI2qygW7jk4EYCjgnBhqzHQ4PVBsRTNQ0D1s7FcYMH4xikLtWCX95Vcc4Uw0MUgMlabou_0cMpXF3DbCGce-5CU635jnqhD8zZqQ5kGeuCj3ylu1m45VYTVrIfB3p3IGX47OMshGbBnY0gLVuSM7F2IPAMeW6pxoTXdRMdp4Ois-SGtfkca2XQ==";
                if (completion != null) {
                    completion.onComplete(new PrimitiveResult(V18_PROD_KS));
                }
            }

            @Override
            public int partnerId() {
                int OttPartnerId = 225;
                return OttPartnerId;
            }
        };

        //  SimpleSessionProvider ksSessionProvider = new SimpleSessionProvider("https://rest-as.ott.kaltura.com/v4_4/api_v3", 255, null);

        String mediaIdPhoenix = mediaId;
        //String formatHls = fileType % 2 == 0 ? "Tablet Main" : "dash Main";
        // String formatHls = "Tablet Main";
        //  String formatHls = "DASHENC_PremiumHD"; // Device Not in household wala issue
        //  String formatHls = "TV Main";
        //String formatHls = "DASHENC_TV_PremiumHD";
        //audio
        // String mediaId = "743297";
        // String formatHls  = "audio_only";
        // String formatHls  = "dash Main";
        //String formatHls  = "DASH_LINEAR_TV";
        // String formatHls  = "HLS_Linear_P";
        String formatHls  = format;
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
                .setAssetId(mediaIdPhoenix)
                //      .setFileIds("11704300") // Device Not in household wala issue
                //.setReferrer()
                //    .setFileIds("10677547", "10677551")
                .setProtocol(PhoenixMediaProvider.HttpProtocol.Https)
                .setContextType(APIDefines.PlaybackContextType.Playback)
                .setAssetType(APIDefines.KalturaAssetType.Media)
                //     .setPKUrlType(APIDefines.PKUrlType.PlayManifest)
                .setFormats(formatHls);
        //  .setFormats("dashclear", "DASHENC_PremiumHD");
        //.setFormats(formatDash);

        mediaProvider.load(completion);

    }

    private void createPlayerWithoutPhoenixProvider() {

        List<PKDrmParams> pkDrmDataList = new ArrayList<>();
        String licenseUri = "https://udrmv3.kaltura.com/cenc/widevine/license?custom_data=eyJjYV9zeXN0ZW0iOiJodHRwczovL3Jlc3QtYXMub3R0LmthbHR1cmEuY29tL2FwaV92My9zZXJ2aWNlL2Fzc2V0RmlsZS9hY3Rpb24vZ2V0Q29udGV4dD9rcz1kako4TWpJMWZBclNHbDVhQTNrc3Vvd21HbnRaRTE0aUlMZ2ZIZGIzRHEwRWk2NGE1b1NYbTZ2QURORm9iQXlyU1hvNndtWE1XT21kV3pFTTNBSVBRSWU3WFBzWmFzZkRfN3JfbHRaWDVEYjNsNnFsRklnaWU4eHlDWjFQVVBOSjVoM3RQeWNZc2RJUVlvLUFRWGJZaUJrOU1lWmY4NXY5Tld4NXlxQ0VLaUE2Yk5obHNmeUtzcG5TVW5uLXVDZ1gydGYzUGQ1aVBpTHQ0OXRWX2RsZDh0ZmRaVF9YRVBlWVQwRExpbmF1VEpnaGsya25zekVjWG44NzU4dUFDZnQxZ2pvVlZPaXEyTVA0dEdmRkZ3Z1dEcFdEbWxEdDJRQzBCZzBQeUhQN214NHdkcGYtM3RJSktKUnlUZGZyUHZPbWV2VjZ2SENrbXhFT1BfMmk4ZFM0QWlHbXMwOXktT3V3bGloNmtzekc0OUxwTVRwZTNhMjVydnkxSkxJcDNTOEtCQmE1ckFnSzNBPT0mY29udGV4dFR5cGU9bm9uZSZpZD0xMDg0NzUwNCIsImFjY291bnRfaWQiOjE5ODI1NTEsImNvbnRlbnRfaWQiOiIwXzRicGZiMnRmXzBfNTl0N3Z4ZjAsMF80YnBmYjJ0Zl8wXzg3bHB1YnhmLDBfNGJwZmIydGZfMF9jMDEwb2x6bCIsImZpbGVzIjoiIiwidXNlcl90b2tlbiI6ImRqSjhNakkxZkFyU0dsNWFBM2tzdW93bUdudFpFMTRpSUxnZkhkYjNEcTBFaTY0YTVvU1htNnZBRE5Gb2JBeXJTWG82d21YTVdPbWRXekVNM0FJUFFJZTdYUHNaYXNmRF83cl9sdFpYNURiM2w2cWxGSWdpZTh4eUNaMVBVUE5KNWgzdFB5Y1lzZElRWW8tQVFYYllpQms5TWVaZjg1djlOV3g1eXFDRUtpQTZiTmhsc2Z5S3NwblNVbm4tdUNnWDJ0ZjNQZDVpUGlMdDQ5dFZfZGxkOHRmZFpUX1hFUGVZVDBETGluYXVUSmdoazJrbnN6RWNYbjg3NTh1QUNmdDFnam9WVk9pcTJNUDR0R2ZGRndnV0RwV0RtbER0MlFDMEJnMFB5SFA3bXg0d2RwZi0zdElKS0pSeVRkZnJQdk9tZXZWNnZIQ2tteEVPUF8yaThkUzRBaUdtczA5eS1PdXdsaWg2a3N6RzQ5THBNVHBlM2EyNXJ2eTFKTElwM1M4S0JCYTVyQWdLM0E9PSIsInVkaWQiOiI4RjZDMTk5QS1DMUQ5LTQ5NzctQTA0MC01MzEzQkM0MDcxNzIiLCJhZGRpdGlvbmFsX2Nhc19zeXN0ZW0iOjIyNX0%3d&signature=IciIHVyQmV1lH58Y8wLH83eqfeM%3d";
        PKDrmParams pkDrmParams = new PKDrmParams(licenseUri, PKDrmParams.Scheme.WidevineCENC);
        pkDrmDataList.add(pkDrmParams);

        List<PKMediaSource> mediaSourceList = new ArrayList<>();
        PKMediaSource pkMediaSource = new PKMediaSource();
        pkMediaSource.setUrl("https://cdnapisec.kaltura.com/p/1982551/sp/198255100/playManifest/protocol/https/entryId/0_4bpfb2tf/format/mpegdash/tags/dash/f/a.mpd");
        //  pkMediaSource.setId("929088");
        //   pkMediaSource.setMediaFormat(PKMediaFormat.dash);

        // pkMediaSource.setDrmData(pkDrmDataList);

        PKMediaEntry  pkMediaEntry = new PKMediaEntry();
        //  pkMediaEntry.setId("929088");
        pkMediaEntry.setDuration(0);

        mediaSourceList.add(pkMediaSource);
        pkMediaEntry.setSources(mediaSourceList);

        //External Subtitles
        List<PKExternalSubtitle> subsList = new ArrayList<>();

        PKExternalSubtitle pkExternalSubtitle = new PKExternalSubtitle()
                .setUrl("https://zee5vod.akamaized.net/drm1/elemental/dash/ORIGINAL_CONTENT_BRO/DOMESTIC/HINDI/MENTALHOOD_HINDI_EP01_BRO_BEBAAKEE_CROSS_PROMO_hi_08d64bc720265e5be53e09934ffc2848/manifest-en.vtt")
                .setMimeType(PKSubtitleFormat.vtt)
                .setLabel("Zee-Not-Sync")
                .setLanguage("En");
        subsList.add(pkExternalSubtitle);

        //  pkMediaEntry.setExternalSubtitleList(subsList);

        PKMediaConfig config = new PKMediaConfig();
        config.setMediaEntry(pkMediaEntry);


        PKPluginConfigs pluginConfigs = new PKPluginConfigs();

        player = PlayKitManager.loadPlayer(this, pluginConfigs);

        player.getSettings().setAllowCrossProtocolRedirect(true);
        addPlayerListeners(progressBar);
        FrameLayout layout = (FrameLayout) findViewById(R.id.player_view);
        layout.addView(player.getView());

        controlsView = (PlaybackControlsView) this.findViewById(R.id.playerControls);
        controlsView.setUiListener(this);
        controlsView.setPlayer(player);

        player.prepare(config);
        player.play();
        initSpinners();
    }

    private void startOttMediaLoadingEmiritus(final OnMediaLoadCompletion completion) {

        String mediaId = "338205";
        String KS = "djJ8MzIwOHxMw0yyRykjVyX6xGPviZvkQT7-_i4lvpLq5vb4YVl8xI6E7pcpFe-bUIlQVO9p7HU1Ek_zO0riW8EVzwdii1NqfQqpDZt8HRtmsspA-E_9nLC7s8MACPQpLABOzukx45T9fGwpIjbZrRWBaS2q9IEB8shp77e42G6WAUUW0o2pwB7W4obEja--yhBFo6nVXZrD_eZanHYj2UNrZ-0O_28JBSSV7m-qaoFPVQ1-eSvnqpekt9NTJhkTCgRdtUIQv731V1C52fIwYP5K4jO2-w_zWn6jBxi4z0bjM7vkarVQDe0QwksmHuROzZe54wIY1ZpMZDrcpdihugNLn9QXefrZaZ-j853_nJ9CKRnolI6e7VRno0G2XgfyYf4YfjnqsDs=";

        mediaProvider = new PhoenixMediaProvider("https://rest-sgs1.ott.kaltura.com/api_v3/", 3208, KS)
                .setAssetId(mediaId)
                .setProtocol(PhoenixMediaProvider.HttpProtocol.Https)
                .setFormats("Dash_widevine")
                .setContextType(APIDefines.PlaybackContextType.Playback)
                .setAssetType(APIDefines.KalturaAssetType.Media);

        mediaProvider.load(completion);
    }

    private void startOttMediaLoadingAstro(final OnMediaLoadCompletion completion) {

        String mediaId = "338882";
        String KS = "djJ8MzIwOXz9afY_1bcI12_NSeI36blvcAIRkX_VUGaTbP6mTVhZ37FnK9iSywL8cOMp91adP0PHoTRtt-FXooUa0tc30NDCBPOBXOhVsMjtKf_77DwQHgZ2kg_sfgEUwLC0hM4IbHS_vuprmmw99yk0xWSO5XetRrDECz-y2AXVquKcmR783zKk2wQHPYcM9SP-yFUvJAOJSRMLO1zGfAv9UgjPLDrGeKtCQA9DRZ9k4hvvccqS_tPD2-QnVNS1mAcwPRK-TkXGIhcHJAPNooj-nkpqXgPrVsHHcN0a_JyzUrOQN0_J240p5IDS0Gy-8SHLp38ariE=";

        mediaProvider = new PhoenixMediaProvider("https://rest-sgs1.ott.kaltura.com/api_v3/", 3209, KS)
                .setAssetId(mediaId)
                .setProtocol(PhoenixMediaProvider.HttpProtocol.Https)
                .setContextType(APIDefines.PlaybackContextType.Playback)
                .setFormats("dash_widevine")
                .setAssetType(APIDefines.KalturaAssetType.Media);

        mediaProvider.load(completion);
    }

    private void DVR1(final OnMediaLoadCompletion completion) {
        String KS = "djJ8MzIwMHyaR-XXfC6eWnDWHhX0_nGyb_FXT4CPNCZQe1wxnbj6wjeSN7r_-EHsJnTZ3os0BtrAZh8Woim0-w3zAxae5sfTC3DxZ5EA1Uoh5VMbowapOkgF1T9oBz3MuFvW4hJfZ4_rxreoIgt-z91_aY9TvxlQXrxaNWWhmn6TmLoykW_bfkzLJwZ73TbxMJla2KS_Zq7KjcuqHGcQFBIjdx_9c2jprUD3wE50zZHGKuRQllRsdq_IsLhkRRBcgoUAK-ZxvH9lcQ4W0hs1WVEhh-L1kBO6mOXWxtI9VH6bKIZCyrSh3OnkdsH6Ty5FpUf6J64owp4er6mUKxCTs4XFUVeKUr3UxFnLQl97JBVVQnUca8_Anw==";
        //private val FIRST_ASSET_ID = "800424"//"2106875"
        //private val SECOND_ASSET_ID = "818595";//"2106873"
        String mediaId = "800424";
        mediaProvider = new PhoenixMediaProvider("https://api.frp1.ott.kaltura.com/api_v3/", 3200, KS)
                .setAssetId(mediaId)
                // .setFormats("DASH_WV")
                .setProtocol(PhoenixMediaProvider.HttpProtocol.Https)
                .setContextType(APIDefines.PlaybackContextType.Playback)
                .setAssetType(APIDefines.KalturaAssetType.Media)
                .setPKUrlType(APIDefines.KalturaUrlType.Direct)
                .setPKStreamerType(APIDefines.KalturaStreamerType.Mpegdash);

        mediaProvider.load(completion);
    }
}
