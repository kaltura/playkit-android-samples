/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.kaltura.playkit.samples.androidtv;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.kaltura.netkit.connect.response.PrimitiveResult;
import com.kaltura.netkit.connect.response.ResultElement;
import com.kaltura.netkit.utils.OnCompletion;
import com.kaltura.netkit.utils.SessionProvider;
import com.kaltura.playkit.PKMediaConfig;
import com.kaltura.playkit.PKMediaEntry;
import com.kaltura.playkit.api.ovp.SimpleOvpSessionProvider;
import com.kaltura.playkit.mediaproviders.base.OnMediaLoadCompletion;
import com.kaltura.playkit.mediaproviders.ott.PhoenixMediaProvider;
import com.kaltura.playkit.mediaproviders.ovp.KalturaOvpMediaProvider;

import java.util.ArrayList;
import java.util.List;

/*
 * MainActivity class that loads MainFragment
 */
public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    public static int movieIdex = 0;
    public static String CDN_URL = "http://cdnapi.kaltura.com";
    public static String SEC_CDN_URL = "https://cdnapisec.kaltura.com/";
    public static String SEC_CDN_QA_URL  = "https://qa-apache-php7.dev.kaltura.com/";
    public static String CDN_QA_URL  = "http://qa-apache-php7.dev.kaltura.com/";
    public static int QA_PARTNER_ID = 1091;
    public static String CDN_DRM_QA_URL  = "https://qa-apache-php7.dev.kaltura.com/";
    public static int QA_DRM_PARTNER_ID = 4171;
    private static final int PHOENIX_PROVIDER_PARTNER_ID = 198;
    public static String PHOENIX_PROVIDER_BASE_URL = "http://api-preprod.ott.kaltura.com/v4_5/api_v3/";
    public static String PHOENIX_PROVIDER_KS = "";
    public static int PARTNER_ID = 2222401;
    public static int QA_UICONF_ID = 15203039;
    public static List<Movie> list = new ArrayList<>();
    public static  Handler handler;

    /**
     * Called when the activity is first created.
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler(Looper.getMainLooper());
//        startSimpleOvpMediaLoading(playLoadedEntry, CDN_URL, PARTNER_ID, "1_utjroirc");
//        startSimpleOvpMediaLoading(playLoadedEntry, CDN_URL, PARTNER_ID, "1_b8ppdt98");
//        startSimpleOvpMediaLoading(playLoadedEntry, CDN_URL, PARTNER_ID, "1_cwdmd8il");
//        startSimpleOvpMediaLoading(playLoadedEntry, CDN_URL, PARTNER_ID, "1_nxyj6abx");
//        startSimpleOvpMediaLoading(playLoadedEntry, CDN_URL, PARTNER_ID, "1_q81a5nbp");



        //HLS
        startSimpleOvpMediaLoading(playLoadedEntry, CDN_QA_URL, QA_PARTNER_ID, "0_wifqaipd");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startSimpleOvpMediaLoading(playLoadedEntry, CDN_QA_URL, QA_PARTNER_ID, "0_fl4ioobl");
            }
        }, 2500);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startSimpleOvpMediaLoading(playLoadedEntry, CDN_QA_URL, QA_PARTNER_ID, "0_nwkp7jtx");
            }
        }, 5000);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startSimpleOvpMediaLoading(playLoadedEntry, CDN_QA_URL, QA_PARTNER_ID, "0_15xrxwvo");
            }
        }, 7500);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startSimpleOvpMediaLoading(playLoadedEntry, CDN_QA_URL, QA_PARTNER_ID, "0_gwn0i9zy");
            }
        }, 10000);


        //DASH
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startSimpleOvpMediaLoading(playLoadedEntry, CDN_QA_URL, QA_PARTNER_ID, "0_4ktof5po");
            }
        }, 11000);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startSimpleOvpMediaLoading(playLoadedEntry, CDN_QA_URL, QA_PARTNER_ID, "0_ttfy4uu0");
            }
        }, 12000);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startSimpleOvpMediaLoading(playLoadedEntry, CDN_QA_URL, QA_PARTNER_ID, "0_wu32qrt3");
            }
        }, 13000);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startSimpleOvpMediaLoading(playLoadedEntry, SEC_CDN_URL, PARTNER_ID, "1_f93tepsn");
            }
        }, 14000);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startSimpleOvpMediaLoading(playLoadedEntry, CDN_QA_URL, QA_PARTNER_ID, "0_qq9jh1i3");
            }
        }, 15000);


        //MP4
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startSimpleOvpMediaLoading(playLoadedEntry, CDN_QA_URL, QA_PARTNER_ID, "0_4ktof5po");
            }
        }, 16000);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startSimpleOvpMediaLoading(playLoadedEntry, CDN_QA_URL, QA_PARTNER_ID, "0_wmo6tbfn");
            }
        }, 17000);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startSimpleOvpMediaLoading(playLoadedEntry, CDN_QA_URL, QA_PARTNER_ID, "0_wu32qrt3");
            }
        }, 18000);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startSimpleOvpMediaLoading(playLoadedEntry, CDN_QA_URL, QA_PARTNER_ID, "0_q0vsecey"/*"0_fl4ioobl"*/);
            }
        }, 19000);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startSimpleOvpMediaLoading(playLoadedEntry, CDN_QA_URL, QA_PARTNER_ID, "0_k3s28s8f");
            }
        }, 20000);



        //DRM
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startSimpleOvpMediaLoading(playLoadedEntry, CDN_DRM_QA_URL, QA_DRM_PARTNER_ID, "0_2jiaa9tb");
            }
        }, 21000);

        //DRM
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startSimpleOvpMediaLoading(playLoadedEntry, CDN_DRM_QA_URL, QA_DRM_PARTNER_ID, "0_7o8zceol");
            }
        }, 22000);

        //DRM
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startSimpleOvpMediaLoading(playLoadedEntry, CDN_DRM_QA_URL, QA_DRM_PARTNER_ID, "0_xf1tomip");
            }
        }, 23000);

        //DRM
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startSimpleOvpMediaLoading(playLoadedEntry, CDN_DRM_QA_URL, QA_DRM_PARTNER_ID, "0_2oolfkpt");
            }
        }, 24000);

        //DRM
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startSimpleOvpMediaLoading(playLoadedEntry, SEC_CDN_URL, PARTNER_ID, "1_f93tepsn");
            }
        }, 25000);




        setContentView(R.layout.activity_main);
    }

    private synchronized void onMediaLoaded(PKMediaEntry mediaEntry) {
        Log.d(TAG, "onMediaLoaded movieIdex = " + movieIdex);
        if (mediaEntry == null) {
            Log.e(TAG, "mediaEntry == null");
            return;
        }

        if (movieIdex == MainFragment.NUM_COLS) {
            Log.e(TAG, "movieIdex ALREADY == " + MainFragment.NUM_COLS +  " return");
            return;
        }
        PKMediaConfig mediaConfig = new PKMediaConfig().setMediaEntry(mediaEntry).setStartPosition(0);
        if (list != null && mediaConfig != null) {
            String thumbnailUrl = CDN_QA_URL + "/p/" + QA_PARTNER_ID + "/sp/" + QA_PARTNER_ID + "00" + "/thumbnail/entry_id/" + mediaEntry.getId() + "/version/100007/width/1200/hight/780";
            if (mediaEntry.getId().equals("1_f93tepsn")) {
                thumbnailUrl = SEC_CDN_URL + "/p/" + PARTNER_ID + "/sp/" + PARTNER_ID + "00" + "/thumbnail/entry_id/" + mediaEntry.getId() + "/version/100007/width/1200/hight/780";
            }
            if (mediaEntry.getId().equals("0_2jiaa9tb") || mediaEntry.getId().equals("0_7o8zceol") || mediaEntry.getId().equals("0_xf1tomip") || mediaEntry.getId().equals("0_4ktof5po")) {
                thumbnailUrl = CDN_DRM_QA_URL + "/p/" + QA_DRM_PARTNER_ID + "/sp/" + QA_DRM_PARTNER_ID + "00" + "/thumbnail/entry_id/" + mediaEntry.getId() + "/version/100007/width/1200/hight/780";
            }
            Log.d(TAG, "onMediaLoaded "  + mediaConfig.getMediaEntry().getName() + " " + mediaEntry.getId()  + " " + QA_PARTNER_ID);
            list.add(buildMovieInfoOvp(movieIdex,"category", mediaConfig.getMediaEntry().getName(), descriptionOvp, "Kaltura LTD", mediaConfig, thumbnailUrl, thumbnailUrl, (int)mediaEntry.getDuration()));
            movieIdex++;
        } else {
            Log.d(TAG, "onMediaLoaded mediaConfig == null");
        }
    }


    private synchronized void onMediaLoadedOTT(PKMediaEntry mediaEntry) {
        Log.d(TAG, "onMediaLoaded");
        if (mediaEntry == null) {
            Log.e(TAG, "mediaEntry == null");
            return;
        }

        if (movieIdex == MainFragment.NUM_COLS) {
            Log.e(TAG, "movieIdex ALREADY == " +MainFragment.NUM_COLS +  " return");
            return;
        }
        PKMediaConfig mediaConfig = new PKMediaConfig().setMediaEntry(mediaEntry).setStartPosition(0);
        if (list != null && mediaConfig != null) {
            String thumbnailUrl = "http://cfvod.kaltura.com/p/243342/sp/24334200/thumbnail/entry_id/0_uka1msg4/version/100007/width/1200/hight/780";
            // ( mediaEntry.getMetadata().containsKey("1200X780")) ? mediaEntry.getMetadata().get("1200X780") : "";//CDN_URL + "/p/" + PARTNER_ID + "/sp/" + PARTNER_ID + "00" + "/thumbnail/entry_id/" + mediaEntry.getId() + "/version/100007/width/1200/hight/780";

            Log.d(TAG, "onMediaLoaded "  + mediaConfig.getMediaEntry().getName() + " " + mediaEntry.getId()  + " " + PARTNER_ID);
            list.add(buildMovieInfoOvp(movieIdex, "category", mediaConfig.getMediaEntry().getName(), descriptionOvp, "Kaltura LTD", mediaConfig, thumbnailUrl, thumbnailUrl, (int)mediaEntry.getDuration()));
            movieIdex++;
        } else {
            Log.d(TAG, "onMediaLoaded mediaConfig == null");

        }

    }
    private static Movie buildMovieInfoOvp(int index, String category, String title,
                                           String description, String studio, PKMediaConfig mediaConfig, String cardImageUrl,
                                           String bgImageUrl, int duration) {
        Movie movie = new Movie();
        movie.setIndex(index);
        movie.setId(Movie.getCount());
        Movie.incCount();
        movie.setTitle(title);
        movie.setDescription(description);
        movie.setStudio(studio);
        movie.setCategory(category);
        movie.setCardImageUrl(cardImageUrl);
        movie.setBackgroundImageUrl(bgImageUrl);
        movie.setPkMediaEntry(mediaConfig.getMediaEntry());
        movie.setVideoUrl(mediaConfig.getMediaEntry().getSources().get(0).getUrl());
        movie.setDuration(duration);
        return movie;
    }

    private OnMediaLoadCompletion playLoadedEntry = new OnMediaLoadCompletion() {
        @Override
        public void onComplete(final ResultElement<PKMediaEntry> response) {
            if (response.isSuccess()) {
                onMediaLoaded(response.getResponse());
            } else {
                Log.e(TAG, "Error " + response.getError().getCode() + "-" + response.getError().getMessage());
            }
        }
    };

    private OnMediaLoadCompletion playLoadedAsset = new OnMediaLoadCompletion() {
        @Override
        public void onComplete(final ResultElement<PKMediaEntry> response) {
            if (response.isSuccess()) {
                onMediaLoadedOTT(response.getResponse());
            } else {
                Log.e(TAG, "Error " + response.getError().getCode() + "-" + response.getError().getMessage());

            }
        }
    };

    private static void startSimpleOvpMediaLoading(OnMediaLoadCompletion completion, String cdnUrl , int partnerId, String entryId) {
        new KalturaOvpMediaProvider()
                .setSessionProvider(new SimpleOvpSessionProvider(cdnUrl, partnerId, null))
                .setEntryId(entryId)
                .load(completion);
    }

    public static String descriptionOvp = "Description...";

    public static final String MOVIE_CATEGORY[] = {
            "Category HLS",
            "Category MPD",
            "Category MP4",
            "Category DRM"
    };

    private void startSimpleOttMediaLoading(OnMediaLoadCompletion completion, String assetId) {

        PhoenixMediaProvider mediaProvider = new PhoenixMediaProvider();
        SessionProvider sessionProvider = createSessionProvider();
        mediaProvider.setAssetId(assetId);
        mediaProvider.setSessionProvider(sessionProvider);
        mediaProvider.load(completion);
    }

    private SessionProvider createSessionProvider() {
        return new SessionProvider() {
            @Override
            public String baseUrl() {
                return PHOENIX_PROVIDER_BASE_URL;
            }

            @Override
            public void getSessionToken(OnCompletion<PrimitiveResult> completion) {
                completion.onComplete(new PrimitiveResult(PHOENIX_PROVIDER_KS));
            }

            @Override
            public int partnerId() {
                return PHOENIX_PROVIDER_PARTNER_ID;
            }
        };
    }

}
