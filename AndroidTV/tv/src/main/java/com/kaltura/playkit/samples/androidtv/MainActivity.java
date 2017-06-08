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
import android.util.Log;

import com.kaltura.netkit.connect.response.ResultElement;
import com.kaltura.playkit.PKMediaConfig;
import com.kaltura.playkit.PKMediaEntry;
import com.kaltura.playkit.api.ovp.SimpleOvpSessionProvider;
import com.kaltura.playkit.mediaproviders.base.OnMediaLoadCompletion;
import com.kaltura.playkit.mediaproviders.ovp.KalturaOvpMediaProvider;

import java.util.ArrayList;
import java.util.List;

/*
 * MainActivity class that loads MainFragment
 */
public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    public static int movieIdex = 0;
    public static String CDN_URL  = "http://cdnapi.kaltura.com";
     public static int PARTNER_ID = 2222401;
    public static List<Movie> list = new ArrayList<>();
    /**
     * Called when the activity is first created.
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startSimpleOvpMediaLoading(playLoadedEntry, CDN_URL, PARTNER_ID, "1_utjroirc");
        startSimpleOvpMediaLoading(playLoadedEntry, CDN_URL, PARTNER_ID, "1_b8ppdt98");
        startSimpleOvpMediaLoading(playLoadedEntry, CDN_URL, PARTNER_ID, "1_cwdmd8il");
        startSimpleOvpMediaLoading(playLoadedEntry, CDN_URL, PARTNER_ID, "1_nxyj6abx");
        startSimpleOvpMediaLoading(playLoadedEntry, CDN_URL, PARTNER_ID, "1_q81a5nbp");



        setContentView(R.layout.activity_main);
    }

    private void onMediaLoaded(PKMediaEntry mediaEntry) {
        if (mediaEntry == null) {
            Log.d(TAG, "mediaEntry == null");
            return;
        }
        if (movieIdex == 5) {
            Log.e(TAG, "movieIdex ALREADY == 5 return");
            return;
        }
        PKMediaConfig mediaConfig = new PKMediaConfig().setMediaEntry(mediaEntry).setStartPosition(0);
        if (list != null && mediaConfig != null) {
            String thumbnailUrl = CDN_URL + "/p/" + PARTNER_ID + "/sp/" + PARTNER_ID + "00" + "/thumbnail/entry_id/" + mediaEntry.getId() + "/version/100007/width/1200/hight/780";
            Log.d(TAG, "onMediaLoaded  "  + mediaConfig.getMediaEntry().getName() + " " + mediaEntry.getId()  + " " + PARTNER_ID);
            list.add(buildMovieInfoOvp("category", mediaConfig.getMediaEntry().getName(), descriptionOvp, "Kaltura LTD", mediaConfig, thumbnailUrl, thumbnailUrl, (int)mediaEntry.getDuration()));
            movieIdex++;
        } else {
            Log.d(TAG, "onMediaLoaded mediaConfig == null");

        }

    }

    private static Movie buildMovieInfoOvp(String category, String title,
                                           String description, String studio, PKMediaConfig mediaConfig, String cardImageUrl,
                                           String bgImageUrl, int duration) {
        Movie movie = new Movie();
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
            "Category MP4"
    };

}
