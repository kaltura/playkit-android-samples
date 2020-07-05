package com.kaltura.playkit.samples.chromecastcafsample;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaLoadOptions;
import com.google.android.gms.cast.MediaTrack;
import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.CastState;
import com.google.android.gms.cast.framework.CastStateListener;
import com.google.android.gms.cast.framework.IntroductoryOverlay;
import com.google.android.gms.cast.framework.SessionManagerListener;
import com.google.android.gms.cast.framework.media.RemoteMediaClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.kaltura.playkit.PKMediaConfig;
import com.kaltura.playkit.Player;
import com.kaltura.playkit.plugins.googlecast.caf.CAFCastBuilder;
import com.kaltura.playkit.plugins.googlecast.caf.KalturaBasicCAFCastBuilder;
import com.kaltura.playkit.plugins.googlecast.caf.KalturaCastBuilder;
import com.kaltura.playkit.plugins.googlecast.caf.KalturaPhoenixCastBuilder;
import com.kaltura.playkit.plugins.googlecast.caf.MediaInfoUtils;
import com.kaltura.playkit.plugins.googlecast.caf.adsconfig.AdsConfig;
import com.kaltura.playkit.plugins.googlecast.caf.basic.Caption;
import com.kaltura.playkit.plugins.googlecast.caf.basic.Metadata;
import com.kaltura.playkit.plugins.googlecast.caf.basic.PlaybackParams;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
    private Button changeMediaButton;
    private Button playPauseButton;
    private Button basicPlayPauseButton;
    private CastStateListener mCastStateListener;
    private SessionManagerListener<CastSession> mSessionManagerListener;
    private CastContext mCastContext;
    private MenuItem mediaRouteMenuItem;
    private PlaybackLocation mLocation;
    private CastSession mCastSession;
    private MediaInfo mSelectedMedia;
    private RemoteMediaClient remoteMediaClient;
    public enum PlaybackLocation {
        LOCAL,
        REMOTE
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //NOTE - FOR OTT CASTING YOU HAVE TO CHANGE THE PRODUCT FLAVOUR TO OTT
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
        addCastBasicButton();
        addCastOttButton();
        addChangeMediaButton();


    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.browse, menu);
        mediaRouteMenuItem = CastButtonFactory.setUpMediaRouteButton(getApplicationContext(), menu, R.id.media_route_menu_item);
        return true;
    }

    private void addCastOvpButton() {
        //Get reference to the play/pause button.
        playPauseButton = this.findViewById(R.id.cast_ovp_button);
        if ("ott".equals(BuildConfig.FLAVOR)) {
            playPauseButton.setVisibility(View.INVISIBLE);
        }

        //Add clickListener.
        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadRemoteMediaOvp(0,true);
                return;
            }
        });
    }

    private void addCastBasicButton() {
        //Get reference to the play/pause button.
        basicPlayPauseButton = this.findViewById(R.id.cast_basic_button);
        if ("ott".equals(BuildConfig.FLAVOR)) {
            basicPlayPauseButton.setVisibility(View.INVISIBLE);
        }

        //Add clickListener.
        basicPlayPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadRemoteMediaBasic(0,true);
                return;
            }
        });
    }

    private void addCastOttButton() {
        //Get reference to the play/pause button.
        playPauseButton = this.findViewById(R.id.cast_ott_button);
        if ("ovp".equals(BuildConfig.FLAVOR)) {
            playPauseButton.setVisibility(View.INVISIBLE);
        }
        //Add clickListener.
        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadRemoteMediaOtt(0,true);
                return;
            }
        });
    }

    private void addChangeMediaButton() {
        //Get reference to the play/pause button.
        changeMediaButton =  this.findViewById(R.id.change_media_button);
        if (changeMediaButton != null) {
            changeMediaButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PendingResult<RemoteMediaClient.MediaChannelResult> pendingResult = null;
                    MediaLoadOptions loadOptions = new MediaLoadOptions.Builder().setAutoplay(true).setPlayPosition(0).build();
                    String vastAdTag = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dskippablelinear&correlator=" + 43543;
                    if ("ovp".equals(BuildConfig.FLAVOR)) {
                        pendingResult = remoteMediaClient.load(getOvpCastMediaInfo("0_b7s02kjl", vastAdTag, CAFCastBuilder.AdTagType.VAST, null), loadOptions);
                        pendingResult.setResultCallback(new ResultCallback<RemoteMediaClient.MediaChannelResult>() {

                            @Override
                            public void onResult(@NonNull RemoteMediaClient.MediaChannelResult mediaChannelResult) {

                                JSONObject customData = mediaChannelResult.getCustomData();
                                if (customData != null) {
                                    //log.v("loadMediaInfo. customData = " + customData.toString());
                                } else {
                                    //log.v("loadMediaInfo. customData == null");
                                }
                            }
                        });
                    } else {
                        pendingResult = remoteMediaClient.load(getOttCastMediaInfo("548576", "Mobile_Main", "", null, CAFCastBuilder.HttpProtocol.Http, null), loadOptions);
                        pendingResult.setResultCallback(new ResultCallback<RemoteMediaClient.MediaChannelResult>() {

                            @Override
                            public void onResult(@NonNull RemoteMediaClient.MediaChannelResult mediaChannelResult) {

                                JSONObject customData = mediaChannelResult.getCustomData();
                                if (customData != null) {
                                    //log.v("loadMediaInfo. customData = " + customData.toString());
                                } else {
                                    //log.v("loadMediaInfo. customData == null");
                                }
                            }
                        });
                    }
                }
            });
        }
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
                onApplicationConnected(session);
                mCastSession = session;
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
        remoteMediaClient = mCastSession.getRemoteMediaClient();
        if (remoteMediaClient == null) {
            return;
        }

        remoteMediaClient.registerCallback(new RemoteMediaClient.Callback() {
            @Override
            public void onStatusUpdated() {
                Intent intent = new Intent(MainActivity.this, ExpandedControlsActivity.class);
                startActivity(intent);
                //remoteMediaClient.removeListener(this);
            }

            @Override
            public void onMetadataUpdated() {
                super.onMetadataUpdated();
            }

            @Override
            public void onQueueStatusUpdated() {
                super.onQueueStatusUpdated();
            }

            @Override
            public void onPreloadStatusUpdated() {
                super.onPreloadStatusUpdated();
            }

            @Override
            public void onSendingRemoteMediaRequest() {
                super.onSendingRemoteMediaRequest();
            }

            @Override
            public void onAdBreakStatusUpdated() {
                super.onAdBreakStatusUpdated();
            }
        });

        PendingResult<RemoteMediaClient.MediaChannelResult> pendingResult = null;
        MediaLoadOptions loadOptions = new MediaLoadOptions.Builder().setAutoplay(true).setPlayPosition(position).build();
        String vastAdTag = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dskippablelinear&correlator=" +  11223;
        //using QA partner 1091
        pendingResult = remoteMediaClient.load(getOvpCastMediaInfo("0_ttfy4uu0", vastAdTag, CAFCastBuilder.AdTagType.VAST, getExternalVttCaptions()), loadOptions);
        pendingResult.setResultCallback(new ResultCallback<RemoteMediaClient.MediaChannelResult>() {

            @Override
            public void onResult(@NonNull RemoteMediaClient.MediaChannelResult mediaChannelResult) {

                JSONObject customData = mediaChannelResult.getCustomData();
                if (customData != null) {
                    //log.v("loadMediaInfo. customData = " + customData.toString());
                } else {
                    //log.v("loadMediaInfo. customData == null");
                }

                List<MediaTrack> tracksList = remoteMediaClient.getMediaInfo().getMediaTracks();

                Map<String, Long> audioTracks = new HashMap<>();
                Map<String, Long> textTracks = new HashMap<>();

                for (MediaTrack mediaTrack : tracksList) {
                    if (MediaTrack.TYPE_AUDIO == mediaTrack.getType()) {
                        audioTracks.put(mediaTrack.getLanguage(), mediaTrack.getId());
                    } else if (MediaTrack.TYPE_TEXT == mediaTrack.getType()) {
                        textTracks.put(mediaTrack.getLanguage(), mediaTrack.getId());
                    }
                }

                long [] tracksIndexsArray = null;  // starting from index 1
                if (audioTracks.isEmpty() && textTracks.isEmpty()) {
                    // not able to switch tracks
                    return;
                } else if (!audioTracks.isEmpty() && !textTracks.isEmpty()) {
                    if (audioTracks.get("ru") != null && textTracks.get("ru") != null) {
                        //do your tracks logic for choosing the default audio and text track
                        tracksIndexsArray = new long[]{textTracks.get("ru").longValue(), audioTracks.get("ru").longValue()};
                    }
                } else if (!audioTracks.isEmpty() && textTracks.isEmpty()) {
                    //do your default audio track logic  starting from index 1
                    if (audioTracks.get("ru") != null) {
                        tracksIndexsArray = new long[]{audioTracks.get("ru").longValue()};
                    }
                } else if (audioTracks.isEmpty() && !textTracks.isEmpty()) {
                    //do your default text track logic starting from index 1
                    if (textTracks.get("ru") != null) {
                        tracksIndexsArray = new long[]{textTracks.get("ru").longValue()};
                    }
                }

                if (tracksIndexsArray == null) {
                    return;
                }
                long[] finalTracksIndexsArray = tracksIndexsArray;
                remoteMediaClient.setActiveMediaTracks(finalTracksIndexsArray)
                        .setResultCallback(new ResultCallback<RemoteMediaClient.MediaChannelResult>() {
                            @Override
                            public void onResult(@NonNull RemoteMediaClient.MediaChannelResult mediaChannelResult) {
                                if (!mediaChannelResult.getStatus().isSuccess()) {
                                    Log.e(TAG, "Failed with status code:" +
                                            mediaChannelResult.getStatus().getStatusCode());
                                } else {
                                    Log.e(TAG, "OK with status code:" +
                                            mediaChannelResult.getStatus().getStatusCode());
                                }
                            }
                        });
            }
        });
    }

    private void loadRemoteMediaBasic(int position, boolean autoPlay) {
        if (mCastSession == null) {
            return;
        }
        remoteMediaClient = mCastSession.getRemoteMediaClient();
        if (remoteMediaClient == null) {
            return;
        }

        remoteMediaClient.registerCallback(new RemoteMediaClient.Callback() {
            @Override
            public void onStatusUpdated() {
                Intent intent = new Intent(MainActivity.this, ExpandedControlsActivity.class);
                startActivity(intent);
                //remoteMediaClient.removeListener(this);
            }

            @Override
            public void onMetadataUpdated() {
                super.onMetadataUpdated();
            }

            @Override
            public void onQueueStatusUpdated() {
                super.onQueueStatusUpdated();
            }

            @Override
            public void onPreloadStatusUpdated() {
                super.onPreloadStatusUpdated();
            }

            @Override
            public void onSendingRemoteMediaRequest() {
                super.onSendingRemoteMediaRequest();
            }

            @Override
            public void onAdBreakStatusUpdated() {
                super.onAdBreakStatusUpdated();
            }
        });

        PendingResult<RemoteMediaClient.MediaChannelResult> pendingResult = null;
        MediaLoadOptions loadOptions = new MediaLoadOptions.Builder().setAutoplay(true).setPlayPosition(position).build();
        String vastAdTag = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dskippablelinear&correlator=" +  11223;
        //using QA partner 1091



        PlaybackParams playbackParams = new PlaybackParams();
        playbackParams.poster = "https://cfvod.kaltura.com/p/2222401/sp/222240100/thumbnail/entry_id/1_f93tepsn/version/100011";
        playbackParams.id = "0_2jiaa9tb";
        playbackParams.duration = 741;
        playbackParams.type = "Vod";
        playbackParams.dvr = false;
        playbackParams.vr = null;
        playbackParams.dvr = false;
        playbackParams.progressive = new ArrayList<>();
        playbackParams.hls = new ArrayList<>();


        //playbackParams.setDashSource("0_2jiaa9tb_973",
        //             "https://cdnapisec.kaltura.com/p/2222401/sp/222240100/playManifest/entryId/1_f93tepsn/protocol/https/format/mpegdash/flavorIds/1_7cgwjy2a,1_xc3jlgr7,1_cn83nztu,1_pgoeohrs/a.mpd",
        //       "https://udrm.kaltura.com/cenc/widevine/license?custom_data=eyJjYV9zeXN0ZW0iOiJPVlAiLCJ1c2VyX3Rva2VuIjoiZGpKOE1qSXlNalF3TVh6Q1B5ZDRyd0tiUlVRQTA2RktoNFNtMWJyWDE2LW01dnh5ODBLa0JEWTN0ME9yUklCajZ4WTc2SWZWRVJFcmItZkNLN01uN1VJV1VWaU92S0JaV0h6V09paGRkdGVlc211b0lLaGQ3d2VhbkE9PSIsImFjY291bnRfaWQiOjIyMjI0MDEsImNvbnRlbnRfaWQiOiIxX2Y5M3RlcHNuIiwiZmlsZXMiOiIxXzdjZ3dqeTJhLDFfeGMzamxncjcsMV9jbjgzbnp0dSwxX3Bnb2VvaHJzIn0%3D&signature=9tenGJdH1qEVhrI1f1fnKbvNBKA%3D")


        playbackParams.setHlsSource("0_2jiaa9tb_971",
                "https://cdnapisec.kaltura.com/p/1734751/sp/173475100/playManifest/entryId/1_3o1seqnv/protocol/https/format/applehttp/flavorIds/1_l7xu37er,1_9p9dlin6,1_h6cxfg0z,1_fdpeg81m/a.m3u8");


        Metadata metadata = new Metadata();
        metadata.description = "";
        metadata.name = "Audio Tracks";
        metadata.tags = "";
        playbackParams.metadata = metadata;
        playbackParams.captions = getExternalVttCaptions();

        //pendingResult = remoteMediaClient!!.load(getBasicCastMediaInfo(playbackParams, vastAdTag, CAFCastBuilder.AdTagType.VAST), loadOptions)
        pendingResult = remoteMediaClient.load(getBasicCastMediaInfo(playbackParams, "", null), loadOptions);

        pendingResult.setResultCallback(new ResultCallback<RemoteMediaClient.MediaChannelResult>() {
            @Override
            public void onResult(@NonNull RemoteMediaClient.MediaChannelResult mediaChannelResult) {
                JSONObject customData = mediaChannelResult.getCustomData();
                if (customData != null) {
                    //log.v("loadMediaInfo. customData = " + customData.toString());
                } else {
                    //log.v("loadMediaInfo. customData == null");
                }
            }
        });
    }

    private List<Caption> getExternalVttCaptions() {
        Caption caption1 = new Caption();
        caption1.isDefault = false;
        caption1.type = "srt";
        caption1.label = "Ger";
        caption1.language = "nl";
        caption1.url = "https://qa-nginx-vod.dev.kaltura.com/api_v3/index.php/service/caption_captionasset/action/serveWebVTT/captionAssetId/0_kozg4x1x/version/2/segmentIndex/1.vtt";

        Caption caption2 = new Caption();
        caption2.isDefault = false;
        caption2.type = "srt";
        caption2.label = "Rus";
        caption2.language = "ru";
        caption2.url = "https://qa-nginx-vod.dev.kaltura.com/api_v3/index.php/service/caption_captionasset/action/serveWebVTT/captionAssetId/0_njhnv6na/version/2/segmentIndex/1.vtt";

        Caption caption3 = new Caption();
        caption3.isDefault = false;
        caption3.type = "srt";
        caption3.label = "Eng";
        caption3.language = "en";
        caption3.url = "https://qa-nginx-vod.dev.kaltura.com/api_v3/index.php/service/caption_captionasset/action/serveWebVTT/captionAssetId/0_kozg4x1x/version/2/segmentIndex/1.vtt";
        List<Caption> captions = new ArrayList<>();
        captions.add(caption1);
        captions.add(caption2);
        captions.add(caption3);
        return captions;
    }

    private void loadRemoteMediaOtt(int position, boolean autoPlay) {
        if (mCastSession == null) {
            return;
        }
        remoteMediaClient = mCastSession.getRemoteMediaClient();
        if (remoteMediaClient == null) {
            return;
        }

        remoteMediaClient.registerCallback(new RemoteMediaClient.Callback() {
            @Override
            public void onStatusUpdated() {
                Intent intent = new Intent(MainActivity.this, ExpandedControlsActivity.class);
                startActivity(intent);
                //remoteMediaClient.removeListener(this);
            }

            @Override
            public void onMetadataUpdated() {
                super.onMetadataUpdated();
            }

            @Override
            public void onQueueStatusUpdated() {
                super.onQueueStatusUpdated();
            }

            @Override
            public void onPreloadStatusUpdated() {
                super.onPreloadStatusUpdated();
            }

            @Override
            public void onSendingRemoteMediaRequest() {
                super.onSendingRemoteMediaRequest();
            }

            @Override
            public void onAdBreakStatusUpdated() {
                super.onAdBreakStatusUpdated();
            }
        });

        PendingResult<RemoteMediaClient.MediaChannelResult> pendingResult = null;
        MediaLoadOptions loadOptions = new MediaLoadOptions.Builder().setAutoplay(true).setPlayPosition(position).build();
        pendingResult = remoteMediaClient.load(getOttCastMediaInfo("548575","Mobile_Main", "", null, CAFCastBuilder.HttpProtocol.Http, getExternalVttCaptions()), loadOptions);
        pendingResult.setResultCallback(new ResultCallback<RemoteMediaClient.MediaChannelResult>() {

            @Override
            public void onResult(@NonNull RemoteMediaClient.MediaChannelResult mediaChannelResult) {

                JSONObject customData = mediaChannelResult.getCustomData();
                if (customData != null) {
                    //log.v("loadMediaInfo. customData = " + customData.toString());
                } else {
                    //log.v("loadMediaInfo. customData == null");
                }
            }
        });
    }

    private void updatePlaybackLocation(PlaybackLocation location) {
        mLocation = location;
    }


    public AdsConfig createAdsConfigVast(String adTagUrl) {
        return MediaInfoUtils.createAdsConfigVastInPosition(0, adTagUrl);
    }

    public AdsConfig createAdsConfigVmap(String adTagUrl) {
        return MediaInfoUtils.createAdsConfigVmap(adTagUrl);
    }

    private MediaInfo getBasicCastMediaInfo(PlaybackParams playbackParams, String adTagUrl, CAFCastBuilder.AdTagType adTagType) {

        KalturaBasicCAFCastBuilder basicCastBuilder = new KalturaBasicCAFCastBuilder(playbackParams)
                .setStreamType(CAFCastBuilder.StreamType.VOD);
        if (!TextUtils.isEmpty(adTagUrl)) {
            if (adTagType == CAFCastBuilder.AdTagType.VAST) {
                basicCastBuilder.setAdsConfig(createAdsConfigVast(adTagUrl));
            } else {
                basicCastBuilder.setAdsConfig(createAdsConfigVmap(adTagUrl));
            }
            basicCastBuilder.setDefaultTextLangaugeCode("en");
        }
        return basicCastBuilder.build();
    }

    private MediaInfo getOttCastMediaInfo(String mediaId, String mediaFormat, String adTagUrl, CAFCastBuilder.AdTagType adTagType, CAFCastBuilder.HttpProtocol protocol, List<Caption> externalVttCaptions) {

        List<String> formats = null;
        if (mediaFormat != null) {
            formats = new ArrayList<>();
            formats.add(mediaFormat);
        }

        CAFCastBuilder phoenixCastBuilder = new KalturaPhoenixCastBuilder()
                .setMediaEntryId(mediaId)
                .setKs("")
                .setFormats(formats)
                .setStreamType(CAFCastBuilder.StreamType.VOD)
                .setAssetReferenceType(CAFCastBuilder.AssetReferenceType.Media)
                .setContextType(CAFCastBuilder.PlaybackContextType.Playback)
                .setMediaType(CAFCastBuilder.KalturaAssetType.Media)
                .setProtocol(protocol);

        if (externalVttCaptions != null) {
            phoenixCastBuilder.setExternalVttCaptions(externalVttCaptions);
        }

        if (!TextUtils.isEmpty(adTagUrl)) {
            if (adTagType == CAFCastBuilder.AdTagType.VAST) {
                phoenixCastBuilder.setAdsConfig(createAdsConfigVast(adTagUrl));
            } else {
                phoenixCastBuilder.setAdsConfig(createAdsConfigVmap(adTagUrl));
            }
            //phoenixCastBuilder.setDefaultTextLangaugeCode("en")
        }
        return returnResult(phoenixCastBuilder);
    }


    private MediaInfo getOvpCastMediaInfo(String entryId, String adTagUrl, CAFCastBuilder.AdTagType adTagType, List<Caption> externalVttCaptions) {

        CAFCastBuilder ovpV3CastBuilder =  new KalturaCastBuilder()
                .setMediaEntryId(entryId)
                .setKs("")
                .setStreamType(CAFCastBuilder.StreamType.VOD);

        if (externalVttCaptions != null) {
            ovpV3CastBuilder.setExternalVttCaptions(externalVttCaptions);
        }

        if (!TextUtils.isEmpty(adTagUrl)) {
            if (adTagType == CAFCastBuilder.AdTagType.VAST) {
                ovpV3CastBuilder.setAdsConfig(createAdsConfigVast(adTagUrl));
            } else {
                ovpV3CastBuilder.setAdsConfig(createAdsConfigVmap(adTagUrl));
            }
            //ovpV3CastBuilder.setDefaultTextLangaugeCode("en")

        }
        return returnResult(ovpV3CastBuilder);
    }


    private MediaInfo returnResult(CAFCastBuilder cafCastBuilder) {
        return cafCastBuilder.build();
    }

//    private void setMediaMetadata(BasicCastBuilder basicCastBuilder, ConverterGoogleCast converterGoogleCast) {
//
//        MediaMetadata mediaMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);
//        ConverterMediaMetadata converterMediaMetadata = converterGoogleCast.getMediaMetadata();
//
//        if (converterMediaMetadata != null) { // MediaMetadata isn't mandatory
//
//            String title = converterMediaMetadata.getTitle();
//            String subTitle = converterMediaMetadata.getSubtitle();
//            ConverterImageUrl image = converterMediaMetadata.getImageUrl();
//
//            if (!TextUtils.isEmpty(title)) {
//                mediaMetadata.putString(MediaMetadata.KEY_TITLE, title);
//            }
//
//            if (!TextUtils.isEmpty(subTitle)) {
//                mediaMetadata.putString(MediaMetadata.KEY_SUBTITLE, subTitle);
//            }
//
//            if (image != null) {
//                Uri uri = null;
//                String url = image.getURL();
//                int width = image.getWidth();
//                int height = image.getHeight();
//
//                if (!TextUtils.isEmpty(url)) {
//                    uri = Uri.parse(url);
//                }
//
//                if (uri != null && width != 0 && height != 0) {
//                    mediaMetadata.addImage(new WebImage(uri, width, height));
//                }
//
//            }
//            basicCastBuilder.setMetadata(mediaMetadata);
//        }
//    }

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
