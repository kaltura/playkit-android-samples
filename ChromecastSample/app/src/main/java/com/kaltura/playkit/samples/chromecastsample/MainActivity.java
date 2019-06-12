package com.kaltura.playkit.samples.chromecastsample;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.CastState;
import com.google.android.gms.cast.framework.CastStateListener;
import com.google.android.gms.cast.framework.IntroductoryOverlay;
import com.google.android.gms.cast.framework.SessionManagerListener;
import com.google.android.gms.cast.framework.media.RemoteMediaClient;
import com.google.android.gms.common.images.WebImage;
import com.kaltura.playkit.PKMediaConfig;
import com.kaltura.playkit.Player;
import com.kaltura.playkit.plugins.googlecast.BasicCastBuilder;
import com.kaltura.playkit.plugins.googlecast.OVPCastBuilder;
import com.kaltura.playkit.plugins.googlecast.TVPAPICastBuilder;


public class MainActivity extends AppCompatActivity {

    //Tag for logging.
    private static final String TAG = MainActivity.class.getSimpleName();
    private IntroductoryOverlay mIntroductoryOverlay;
    //Media entry configuration constants.
    private static final String SOURCE_URL = "https://cdnapisec.kaltura.com/p/2215841/sp/221584100/playManifest/entryId/1_w9zx2eti/protocol/https/format/applehttp/falvorIds/1_1obpcggb,1_yyuvftfz,1_1xdbzoa6,1_k16ccgto,1_djdf6bk8/a.m3u8";
    private static final String ENTRY_ID = "entry_id";
    private static final String MEDIA_SOURCE_ID = "source_id";

    //Ad configuration constants.
    private static final String AD_TAG_URL = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dskippablelinear&correlator=";
    private static final String INCORRECT_AD_TAG_URL = "incorrect_ad_tag_url";
    private static final int PREFERRED_AD_BITRATE = 600;

    private Player player;
    private PKMediaConfig mediaConfig;
    private Button playPauseButton;
    private CastStateListener mCastStateListener;
    private SessionManagerListener<CastSession> mSessionManagerListener;
    private CastContext mCastContext;
    private MenuItem mediaRouteMenuItem;
    private PlaybackLocation mLocation;
    private CastSession mCastSession;
    private MediaInfo mSelectedMedia;
    public enum PlaybackLocation {
        LOCAL,
        REMOTE
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //NOTE - FOR OTT CASTING YOU HAVE TO CHANGE THE REVIVER ID TO E4D66C10 in strings.xml
        mCastStateListener = new CastStateListener() {
            @Override
            public void onCastStateChanged(int newState) {
                if (newState != CastState.NO_DEVICES_AVAILABLE) {
                    showIntroductoryOverlay();
                }
            }
        };

        setupCastListener();
        mCastContext = CastContext.getSharedInstance(this);
        mCastContext.getSessionManager().addSessionManagerListener(
                mSessionManagerListener, CastSession.class);
        // mCastContext.registerLifecycleCallbacksBeforeIceCreamSandwich(this, savedInstanceState);
        // mCastSession = mCastContext.getSessionManager().getCurrentCastSession();


        addCastOvpButton();
        addCastOttButton();


    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.browse, menu);
        mediaRouteMenuItem = CastButtonFactory.setUpMediaRouteButton(getApplicationContext(), menu, R.id.media_route_menu_item);
        return true;
    }

    private void addCastOvpButton() {
        //Get reference to the play/pause button.
        playPauseButton = (Button) this.findViewById(R.id.cast_ovp_button);
        //Add clickListener.
        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadRemoteMediaOvp(0,true);
                return;
            }
        });
    }

    private void addCastOttButton() {
        //Get reference to the play/pause button.
        playPauseButton = (Button) this.findViewById(R.id.cast_ott_button);
        //Add clickListener.
        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadRemoteMediaOtt(0,true);
                return;
            }
        });
    }



    private void showIntroductoryOverlay() {
        if (mIntroductoryOverlay != null) {
            mIntroductoryOverlay.remove();
        }
        if ((mediaRouteMenuItem != null) && mediaRouteMenuItem.isVisible()) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    mIntroductoryOverlay = new IntroductoryOverlay.Builder(
                            MainActivity.this, mediaRouteMenuItem)
                            .setTitleText("Introducing Cast")
                            .setSingleTime()
                            .setOnOverlayDismissedListener(
                                    new IntroductoryOverlay.OnOverlayDismissedListener() {
                                        @Override
                                        public void onOverlayDismissed() {
                                            mIntroductoryOverlay = null;
                                        }
                                    })
                            .build();
                    mIntroductoryOverlay.show();
                }
            });
        }
    }
    private void setupCastListener() {
        mSessionManagerListener = new SessionManagerListener<CastSession>() {

            @Override
            public void onSessionEnded(CastSession session, int error) {
                onApplicationDisconnected();
                invalidateOptionsMenu();
            }

            @Override
            public void onSessionResumed(CastSession session, boolean wasSuspended) {
                onApplicationConnected(session);            mCastSession = session;
                invalidateOptionsMenu();
            }

            @Override
            public void onSessionResumeFailed(CastSession session, int error) {
                onApplicationDisconnected();
            }

            @Override
            public void onSessionStarted(CastSession session, String sessionId) {
                onApplicationConnected(session);
                invalidateOptionsMenu();
            }

            @Override
            public void onSessionStartFailed(CastSession session, int error) {
                onApplicationDisconnected();
                invalidateOptionsMenu();
            }

            @Override
            public void onSessionStarting(CastSession session) {
            }

            @Override
            public void onSessionEnding(CastSession session) {}

            @Override
            public void onSessionResuming(CastSession session, String sessionId) {}

            @Override
            public void onSessionSuspended(CastSession session, int reason) {}

            private void onApplicationConnected(CastSession castSession) {
                mCastSession = castSession;

                if (null != mSelectedMedia) {

                    if (player.isPlaying()) {
                        player.pause();
                        loadRemoteMediaOvp((int)player.getCurrentPosition(), true);
                        finish();
                        return;
                    } else {
                        updatePlaybackLocation(PlaybackLocation.REMOTE);
                    }
                }
                supportInvalidateOptionsMenu();
            }

            private void onApplicationDisconnected() {
                updatePlaybackLocation(PlaybackLocation.LOCAL);
                mLocation = PlaybackLocation.LOCAL;
                supportInvalidateOptionsMenu();
            }
        };
    }

    private void loadRemoteMediaOvp(int position, boolean autoPlay) {
        if (mCastSession == null) {
            return;
        }
        final RemoteMediaClient remoteMediaClient = mCastSession.getRemoteMediaClient();
        if (remoteMediaClient == null) {
            return;
        }
        remoteMediaClient.addListener(new RemoteMediaClient.Listener() {
            @Override
            public void onStatusUpdated() {
                Intent intent = new Intent(MainActivity.this, ExpandedControlsActivity.class);
                startActivity(intent);
                //remoteMediaClient.removeListener(this);
            }

            @Override
            public void onMetadataUpdated() {
            }

            @Override
            public void onQueueStatusUpdated() {
            }

            @Override
            public void onPreloadStatusUpdated() {
            }

            @Override
            public void onSendingRemoteMediaRequest() {
            }

            @Override
            public void onAdBreakStatusUpdated() {
            }
        });
        remoteMediaClient.load(getOvpCastMediaInfo(getConverterCastForOvp(), false), autoPlay, position);
    }


    private void loadRemoteMediaOtt(int position, boolean autoPlay) {
        if (mCastSession == null) {
            return;
        }
        final RemoteMediaClient remoteMediaClient = mCastSession.getRemoteMediaClient();
        if (remoteMediaClient == null) {
            return;
        }
        remoteMediaClient.addListener(new RemoteMediaClient.Listener() {
            @Override
            public void onStatusUpdated() {
                Intent intent = new Intent(MainActivity.this, ExpandedControlsActivity.class);
                startActivity(intent);
                //remoteMediaClient.removeListener(this);
            }

            @Override
            public void onMetadataUpdated() {
            }

            @Override
            public void onQueueStatusUpdated() {
            }

            @Override
            public void onPreloadStatusUpdated() {
            }

            @Override
            public void onSendingRemoteMediaRequest() {
            }

            @Override
            public void onAdBreakStatusUpdated() {
            }
        });
        remoteMediaClient.load(getOttCastMediaInfo(getConverterCastForOtt(), false), autoPlay, position);
    }

    private void updatePlaybackLocation(PlaybackLocation location) {
        mLocation = location;
    }

    private MediaInfo getOttCastMediaInfo(ConverterOttCast converterOttCast, boolean setAdTagUrl) {

        TVPAPICastBuilder tvpapiCastBuilder = new TVPAPICastBuilder()
                .setFormat(converterOttCast.getFormat())
                .setInitObject(converterOttCast.getInitObject().toString());
        //tvpapiCastBuilder.setDefaultTextLanguageLabel("en");
        return returnResult(tvpapiCastBuilder, converterOttCast, setAdTagUrl);
    }


    private MediaInfo getOvpCastMediaInfo(ConverterOvpCast converterOvpCast, boolean setAdTagUrl) {

        String ks = converterOvpCast.getKs();

        OVPCastBuilder ovpCastBuilder = new OVPCastBuilder();

        if (!TextUtils.isEmpty(ks)) { // ks isn't mandatory in OVP environment
            ovpCastBuilder.setKs(ks);
        }
        //ovpCastBuilder.setDefaultTextLanguageLabel("en");
        return returnResult(ovpCastBuilder, converterOvpCast, setAdTagUrl);
    }


    private MediaInfo returnResult(BasicCastBuilder basicCastBuilder, ConverterGoogleCast converterGoogleCast, boolean setAdTagUrl) {

        setAdTagUrl(basicCastBuilder, converterGoogleCast, setAdTagUrl);
        setMediaMetadata(basicCastBuilder, converterGoogleCast);

        MediaInfo mediaInfo = basicCastBuilder
                .setMwEmbedUrl(converterGoogleCast.getMwEmbedURL())
                .setPartnerId(converterGoogleCast.getPartnerId())
                .setAdTagUrl(converterGoogleCast.getAdTagURL())
                .setUiConfId(converterGoogleCast.getUiconfId())
                .setMediaEntryId(converterGoogleCast.getEntryId())
                .setStreamType(BasicCastBuilder.StreamType.VOD)
                .build();
        return mediaInfo;
    }

    private void setMediaMetadata(BasicCastBuilder basicCastBuilder, ConverterGoogleCast converterGoogleCast) {

        MediaMetadata mediaMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);
        ConverterMediaMetadata converterMediaMetadata = converterGoogleCast.getMediaMetadata();

        if (converterMediaMetadata != null) { // MediaMetadata isn't mandatory

            String title = converterMediaMetadata.getTitle();
            String subTitle = converterMediaMetadata.getSubtitle();
            ConverterImageUrl image = converterMediaMetadata.getImageUrl();

            if (!TextUtils.isEmpty(title)) {
                mediaMetadata.putString(MediaMetadata.KEY_TITLE, title);
            }

            if (!TextUtils.isEmpty(subTitle)) {
                mediaMetadata.putString(MediaMetadata.KEY_SUBTITLE, subTitle);
            }

            if (image != null) {
                Uri uri = null;
                String url = image.getURL();
                int width = image.getWidth();
                int height = image.getHeight();

                if (!TextUtils.isEmpty(url)) {
                    uri = Uri.parse(url);
                }

                if (uri != null && width != 0 && height != 0) {
                    mediaMetadata.addImage(new WebImage(uri, width, height));
                }

            }
            basicCastBuilder.setMetadata(mediaMetadata);
        }
    }

    private void setAdTagUrl(BasicCastBuilder basicCastBuilder, ConverterGoogleCast converterGoogleCast,
                             boolean setAdTagUrl) {

        String adTagUrl = converterGoogleCast.getAdTagURL();

        if (!TextUtils.isEmpty(adTagUrl) && setAdTagUrl) { // adTagUrl isn't mandatory
            basicCastBuilder.setAdTagUrl(adTagUrl);
        }
    }


    private ConverterOvpCast getConverterCastForOvp() {
        ConverterMediaMetadata converterMediaMetadata = new ConverterMediaMetadata("Cofee", "Folgers",
                new ConverterImageUrl("https://cfvod.kaltura.com/p/243342/sp/24334200/thumbnail/entry_id/0_uka1msg4/version/100007/width/1200/hight/780",1200, 780));

        ConverterOvpCast converterOvpCast = new ConverterOvpCast(
                ConverterGoogleCast.ReceiverEnvironmentType.RECEIVER_OVP_ENVIRONMENT,
                "", //ks
                "0_uka1msg4",
                "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dskippablelinear&correlator=",
                "", //mwEmbed
                "243342",
                "21099702",
                converterMediaMetadata);

        return converterOvpCast;
    }

    private ConverterOttCast getConverterCastForOtt() {
        ConverterMediaMetadata converterMediaMetadata = new ConverterMediaMetadata("Sintel", "Blender foundation",
                new ConverterImageUrl("https://commondatastorage.googleapis.com/gtv-videos-bucket/CastVideos/images/780x1200/BigBuckBunny-780x1200.jpg",1200, 780));


        KInitObjModel initObj = new KInitObjModel();
        initObj.setApiUser("tvpapi_198");
        initObj.setApiPass("11111");
        initObj.setDomainID(362595);
        initObj.setSiteGuid("739182");
        initObj.setUDID("fd6c5420-8647-3dad-91d8-815309f8e319");
        initObj.setPlatform("Cellular");
        KLocaleModel kLocaleModel = new KLocaleModel();
        kLocaleModel.setLocaleCountry("");
        kLocaleModel.setLocaleDevice("");
        kLocaleModel.setLocaleLanguage("en");
        kLocaleModel.setLocaleUserState("Unknown");
        initObj.setLocale(kLocaleModel);
        ConverterOttCast converterOttCast = new ConverterOttCast(
                ConverterGoogleCast.ReceiverEnvironmentType.RECEIVER_TVPAPI_ENVIRONMENT,
                initObj.toJson(),
                "Mobile_Devices_Main_SD",
                "259153",
                "https://pubsssads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dskippablelinear&correlator=",
                "http://player-preprod.ott.kaltura.com/v2.61/mwEmbed/",
                "198",
                "8413355",
                converterMediaMetadata);

        return converterOttCast;
    }

    @Override
    protected void onResume() {
        mCastContext.addCastStateListener(mCastStateListener);
        super.onResume();
    }

    @Override
    protected void onPause() {
        mCastContext.removeCastStateListener(mCastStateListener);
        super.onPause();
    }
}
