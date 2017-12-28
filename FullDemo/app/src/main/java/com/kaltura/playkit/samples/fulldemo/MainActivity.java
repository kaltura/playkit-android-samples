package com.kaltura.playkit.samples.fulldemo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity  implements VideoListFragment.OnVideoSelectedListener, SharedPreferences.OnSharedPreferenceChangeListener,
        VideoListFragment.OnVideoListFragmentResumedListener,
        VideoFragment.OnVideoFragmentViewCreatedListener ,OrientationManager.OrientationListener {

    private static final String VIDEO_PLAYLIST_FRAGMENT_TAG = "video_playlist_fragment_tag";
    private static final String VIDEO_EXAMPLE_FRAGMENT_TAG = "video_example_fragment_tag";
    public static final String SOURCE_URL1 = "https://cdnapisec.kaltura.com/p/2215841/sp/221584100/playManifest/entryId/1_w9zx2eti/protocol/https/format/applehttp/falvorIds/1_1obpcggb,1_yyuvftfz,1_1xdbzoa6,1_k16ccgto,1_djdf6bk8/a.m3u8";
    public static final String VOOT_URL1 = "https://cdnapisec.kaltura.com/p/1982551/sp/198255100/playManifest/entryId/0_akiyt8xz/format/applehttp/tags/iphonenew/protocol/https/f/a.m3u8";
    public static final String LIC_URL1 = "";

    public static final String SOURCE_URL2 = "https://cdnapisec.kaltura.com/p/2222401/sp/222240100/playManifest/entryId/1_f93tepsn/protocol/https/format/url/flavorIds/0_n80ojk1z,0_pke26hka/a.wvm";
    public static final String LIC_URL2 = "https://udrmv3.kaltura.com//widevine/license?custom_data=eyJjYV9zeXN0ZW0iOiJPVlAiLCJ1c2VyX3Rva2VuIjoiZGpKOE1qSXlNalF3TVh4cF90ajBCRlpHOE5MdUNiV0VBenFUV0NiS1RaREpscTROWTlYSGd1dW5HV3d4dUxDY3VoUUZLeDRvTFdEV0NyLWxEUUhxbU1JcDFMbVg4NnYydXhkLS0ySDU5bWszVnhZLUtpaWFrZTl3Y0E9PSIsImFjY291bnRfaWQiOjIyMjI0MDEsImNvbnRlbnRfaWQiOiIxX2Y5M3RlcHNuIiwiZmlsZXMiOiIwX244MG9qazF6LDBfcGtlMjZoa2EifQ%3D%3D&signature=o1EUbBf%2BGRXaS2tUwdYPbGXgeEo%3D";

    public static final String AD_1 = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dlinear&correlator=";
    public static final String AD_2 = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dskippablelinear&correlator=";
    public static final String AD_3 = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/ad_rule_samples&ciu_szs=300x250&ad_rule=1&impl=s&gdfp_req=1&env=vp&output=vmap&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ar%3Dpostonly&cmsid=496&vid=short_onecue&correlator=";
    public static final String AD_4 = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/ad_rule_samples&ciu_szs=300x250&ad_rule=1&impl=s&gdfp_req=1&env=vp&output=vmap&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ar%3Dpremidpost&cmsid=496&vid=short_onecue&correlator=";
    public static final String AD_5 = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/ad_rule_samples&ciu_szs=300x250&ad_rule=1&impl=s&gdfp_req=1&env=vp&output=vmap&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ar%3Dpremidpostpod&cmsid=496&vid=short_onecue&correlator=";
    public static final String AD_6 = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dredirectlinear&correlator=";
    public static final String AD_7 = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/ad_rule_samples&ciu_szs=300x250&ad_rule=1&impl=s&gdfp_req=1&env=vp&output=vmap&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ar%3Dpremidpostoptimizedpodbumper&cmsid=496&vid=short_onecue&correlator=";
    public static final String AD_8 = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/ad_rule_samples&ciu_szs=300x250&ad_rule=1&impl=s&gdfp_req=1&env=vp&output=vmap&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ar%3Dpremidpostlongpod&cmsid=496&vid=short_tencue&correlator=";
    public static final String AD_9 = "http://pubads.g.doubleclick.net/gampad/ads?slotname=/3510761/adRulesSampleTags&sz=640x480&ciu_szs=160x600,300x250,728x90&cust_params=adrule%3Dpremidpostwithpod&url=%5Breferrer_url%5D&unviewed_position_start=1&impl=s&env=vp&gdfp_req=1&ad_rule=0&output=xml_vast2&vad_type=linear&vpos=postroll&pod=2&vrid=6961&max_ad_duration=30000&min_ad_duration=0&video_doc_id=12345&cmsid=3601&kfa=0&tfcd=0";
    public static final String AD_GOOGLE_SEARCH = "http://pubads.g.doubleclick.net/gampad/ads?sz=640x360&iu=/6062/iab_vast_samples/skippable&ciu_szs=300x250,728x90&impl=s&gdfp_req=1&env=vp&output=xml_vast2&unviewed_position_start=1&url=[referrer_url]&correlator=[timestamp]";
    public static final String AD_VOOT1 = "https://pubads.g.doubleclick.net/gampad/live/ads?sz=640x480&iu=%2F21633895671%2FAndroid_App_Video&impl=s&gdfp_req=1&env=vp&output=vmap&unviewed_position_start=1&cust_params=sample_ar%3Dskippablelinear&cmsid=2467608&vid=0_9ryp89yj&ad_rule=1&correlator=11588";
    public static final String AD_VOOT2 = "https://pubads.g.doubleclick.net/gampad/live/ads?sz=640x480&iu=%2F21633895671%2FAndroid_App_Video&impl=s&gdfp_req=1&env=vp&output=vmap&unviewed_position_start=1&cust_params=sample_ar%3Dskippablelinear&cmsid=2466114&vid=0_akiyt8xz&ad_rule=1&correlator=12732";
    public static final String AUTO_PLAY = "AUTO_PLAY";
    public static final String START_FROM = "START_FROM";
    public static final String MIN_AD_DURATION_FOR_SKIP_BUTTON = "MIN_AD_DURATION_FOR_SKIP_BUTTON";
    public static final String AD_LOAD_TIMEOUT = "AD_LOAD_TIMEOUT";
    public static final String MIME_TYPE = "MIME_TYPE";
    public static final String PREFERRED_BITRATE = "PREFERRED_BITRATE";
    public static final String COMPANION_AD_WIDTH = "COMPANION_AD_WIDTH";
    public static final String COMPANION_AD_HEIGHT = "COMPANION_AD_HEIGHT";

    private OrientationManager mOrientationManager;
    private int minAdDurationForSkipButton;
    private boolean isAutoPlay;
    private int startPosition;
    private int adLoadTimeOut;
    private String videoMimeType;
    private int videoBitrate;
    private int companionAdWidth;
    private int companionAdHeight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mOrientationManager = new OrientationManager(this, SensorManager.SENSOR_DELAY_NORMAL, this);
        mOrientationManager.enable();
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.findFragmentByTag(VIDEO_PLAYLIST_FRAGMENT_TAG) == null) {
            VideoListFragment videoListFragment = new VideoListFragment();
            Bundle bundle = new Bundle();
            bundle.putBoolean(AUTO_PLAY, isAutoPlay);
            bundle.putInt(START_FROM, startPosition);
            bundle.putInt(MIN_AD_DURATION_FOR_SKIP_BUTTON, minAdDurationForSkipButton);
            bundle.putInt(AD_LOAD_TIMEOUT, adLoadTimeOut);
            bundle.putString(MIME_TYPE, videoMimeType);
            bundle.putInt(PREFERRED_BITRATE, videoBitrate);
            bundle.putInt(COMPANION_AD_WIDTH, companionAdWidth);
            bundle.putInt(COMPANION_AD_HEIGHT, companionAdHeight);

            videoListFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.video_example_container, videoListFragment,
                            VIDEO_PLAYLIST_FRAGMENT_TAG)
                    .commit();
        }
        setupSharedPreferences();
        orientAppUi();
    }


    private void setupSharedPreferences() {
        // Get all of the values from shared preferences to set it up
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        String companionAdDimentions = sharedPreferences.getString(getString(R.string.pref_companion_key), "0x0");
        String [] dimentions = companionAdDimentions.split("x");
        companionAdWidth = Integer.valueOf(dimentions[0]);
        companionAdHeight = Integer.valueOf(dimentions[1]);


        minAdDurationForSkipButton = 1000 * Integer.valueOf(sharedPreferences.getString(getString(R.string.pref_min_ad_duration_for_skip_button_key),
                "" + Integer.valueOf(getString(R.string.pref_min_ad_duration_for_skip_button_default))));

        adLoadTimeOut = 1000 * Integer.valueOf(sharedPreferences.getString(getString(R.string.pref_ad_load_timeout_key),
               "" +  Integer.valueOf(getString(R.string.pref_ad_load_timeout_default))));

        videoBitrate = Integer.valueOf(sharedPreferences.getString(getString(R.string.pref_bitrate_key),
                "" + Integer.valueOf(getString(R.string.pref_bitrate_value))));

        startPosition = Integer.valueOf(sharedPreferences.getString(getString(R.string.pref_start_from_key),
                "" + Integer.valueOf(getString(R.string.pref_start_from_default))));

        isAutoPlay = sharedPreferences.getBoolean(getString(R.string.pref_auto_play_key),
                getResources().getBoolean(R.bool.pref_auto_play_default));

        videoMimeType = sharedPreferences.getString(getString(R.string.pref_mime_type_key),
                getString(R.string.pref_mime_type_value));

        // Register the listener
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    private void orientAppUi() {
        int orientation = getResources().getConfiguration().orientation;
        boolean isLandscape = (orientation == Configuration.ORIENTATION_LANDSCAPE);
        // Hide the non-video content when in landscape so the video is as large as possible.
        FragmentManager fragmentManager = getSupportFragmentManager();
        VideoFragment videoFragment = (VideoFragment) fragmentManager
                .findFragmentByTag(VIDEO_EXAMPLE_FRAGMENT_TAG);

        Fragment videoListFragment = fragmentManager.findFragmentByTag(
                VIDEO_PLAYLIST_FRAGMENT_TAG);

        if (videoFragment != null) {
            // If the video playlist is onscreen (tablets) then hide that fragment.
            if (videoListFragment != null) {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                if (isLandscape) {
                    fragmentTransaction.hide(videoListFragment);
                } else {
                    fragmentTransaction.show(videoListFragment);
                }
                fragmentTransaction.commit();
            }
            videoFragment.makeFullscreen(isLandscape);
            if (isLandscape) {
                hideStatusBar();
            } else {
                showStatusBar();
            }
        } else {
            // If returning to the list from a fullscreen video, check if the video
            // list fragment exists and is hidden. If so, show it.
            if (videoListFragment != null && videoListFragment.isHidden()) {
                fragmentManager.beginTransaction().show(videoListFragment).commit();
                showStatusBar();
            }
        }
    }

    @Override
    public void onVideoSelected(VideoItem videoItem) {

        VideoFragment videoFragment = (VideoFragment)
                getSupportFragmentManager().findFragmentByTag(VIDEO_EXAMPLE_FRAGMENT_TAG);

        // Add the video fragment if it's missing (phone form factor), but only if the user
        // manually selected the video.
        if (videoFragment == null) {
            VideoListFragment videoListFragment = (VideoListFragment) getSupportFragmentManager()
                    .findFragmentByTag(VIDEO_PLAYLIST_FRAGMENT_TAG);
            int videoPlaylistFragmentId = videoListFragment.getId();

            videoFragment = new VideoFragment();
            Bundle bundle = new Bundle();
            bundle.putBoolean(AUTO_PLAY, isAutoPlay);
            bundle.putInt(START_FROM, startPosition);
            bundle.putInt(MIN_AD_DURATION_FOR_SKIP_BUTTON, minAdDurationForSkipButton);
            bundle.putInt(AD_LOAD_TIMEOUT, adLoadTimeOut);
            bundle.putString(MIME_TYPE, videoMimeType);
            bundle.putInt(PREFERRED_BITRATE, videoBitrate);
            bundle.putInt(COMPANION_AD_WIDTH, companionAdWidth);
            bundle.putInt(COMPANION_AD_HEIGHT, companionAdHeight);

            videoFragment.setArguments(bundle);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(videoPlaylistFragmentId, videoFragment, VIDEO_EXAMPLE_FRAGMENT_TAG)
                    .addToBackStack(null)
                    .commit();
        }
        videoFragment.loadVideo(videoItem);
        invalidateOptionsMenu();
        orientAppUi();
    }


    @Override
    public void onVideoListFragmentResumed() {
        invalidateOptionsMenu();
        orientAppUi();
    }

    private void hideStatusBar() {
        if (Build.VERSION.SDK_INT >= 16) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN);
            getSupportActionBar().hide();
        }
    }

    private void showStatusBar() {
        if (Build.VERSION.SDK_INT >= 16) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            getSupportActionBar().show();
        }
    }

    @Override
    public void onVideoFragmentViewCreated() {
        orientAppUi();
    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        orientAppUi();
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
        }
    }

    /**
     * Methods for setting up the menu
     **/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our visualizer_menu layout to this menu */
        inflater.inflate(R.menu.visualizer_menu, menu);
        /* Return true so that the visualizer_menu is displayed in the Toolbar */
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_ad_load_timeout_key))) {
            adLoadTimeOut = 1000 * Integer.valueOf(sharedPreferences.getString(getString(R.string.pref_ad_load_timeout_key), getString(R.string.pref_ad_load_timeout_default) ));
        } else if (key.equals(getString(R.string.pref_min_ad_duration_for_skip_button_key))) {
            minAdDurationForSkipButton = 1000 * Integer.valueOf(sharedPreferences.getString(getString(R.string.pref_min_ad_duration_for_skip_button_key),getString(R.string.pref_min_ad_duration_for_skip_button_default)));
        } else if (key.equals(getString(R.string.pref_auto_play_key))) {
            isAutoPlay = sharedPreferences.getBoolean(getString(R.string.pref_auto_play_key), getResources().getBoolean(R.bool.pref_auto_play_default));
        } else if (key.equals(getString(R.string.pref_start_from_key))) {
            startPosition = Integer.valueOf(sharedPreferences.getString(getString(R.string.pref_start_from_key), getString(R.string.pref_start_from_default)));
        } else if (key.equals(getString(R.string.pref_bitrate_key))) {
            videoBitrate = Integer.valueOf(sharedPreferences.getString(getString(R.string.pref_bitrate_key), getString(R.string.pref_bitrate_value)));
        } else if (key.equals(getString(R.string.pref_companion_key))) {
            String companionAdDimentions = sharedPreferences.getString(getString(R.string.pref_companion_key), "0x0");
            String [] dimentions = companionAdDimentions.split("x");
            companionAdWidth = Integer.valueOf(dimentions[0]);
            companionAdHeight = Integer.valueOf(dimentions[1]);
        } else if (key.equals(getString(R.string.pref_mime_type_key))) {
            videoMimeType = sharedPreferences.getString(getString(R.string.pref_mime_type_key), getString(R.string.pref_mime_type_value));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister VisualizerActivity as an OnPreferenceChangedListener to avoid any memory leaks.
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}
