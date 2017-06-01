package com.kaltura.playkit.samples.androidtv;

import java.util.ArrayList;
import java.util.List;

public final class MovieList {
    public static final String MOVIE_CATEGORY[] = {
            "Category HLS",
            "Category MPD",
            "Category MP4",
            "Category Three",
            "Category Four",
            "Category Five",
    };

    public static List<Movie> list;

    public static List<Movie> setupMovies() {
        list = new ArrayList<Movie>();
        String title[] = {
                "Harold1",
                "Harold2",
                "Harold3",
                "Harold4",
                "Harold5"
        };

        String description = "Coffee?";

        String videoUrl[] = {
                //"https://cdnapisec.kaltura.com/p/1982551/sp/198255100/playManifest/entryId/0_ch70ffu7/format/mpegdash/tags/dash/protocol/https/f/a.mpd",
                "http://cdnapi.kaltura.com/p/243342/sp/24334200/playManifest/entryId/0_uka1msg4/flavorIds/1_vqhfu6uy,1_80sohj7p,1_ry9w1l0b/format/applehttp/protocol/https/a.m3u8",
                "https://cdnapisec.kaltura.com/p/2215841/sp/221584100/playManifest/entryId/1_w9zx2eti/protocol/https/format/applehttp/falvorIds/1_1obpcggb,1_yyuvftfz,1_1xdbzoa6,1_k16ccgto,1_djdf6bk8/a.m3u8",
                "http://qa-apache-testing-ubu-01.dev.kaltura.com/p/1091/sp/109100/playManifest/entryId/0_kf6dw4jr/flavorIds/0_olodnwem,0_k1xomk0s,0_89mswgn1/format/applehttp/protocol/http/a.m3u8",
                "http://qa-apache-testing-ubu-01.dev.kaltura.com/p/1091/sp/109100/playManifest/entryId/0_n916zwym/flavorIds/0_73pxiyv9,0_k1j6qxmr,0_crt7pzof,0_hsfi74pb/format/applehttp/protocol/http/a.m3u8",
                "https://playertest.longtailvideo.com/adaptive/eleph-audio/playlist.m3u8"
        };

        String videoLic[] = {
                "", //"https://udrm.kaltura.com//cenc/widevine/license?custom_data=eyJjYV9zeXN0ZW0iOiJPVFQiLCJ1c2VyX3Rva2VuIjoiIiwiYWNjb3VudF9pZCI6MTk4MjU1MSwiY29udGVudF9pZCI6IjBfY2g3MGZmdTdfMF9mMDAwa2tiMiwwX2NoNzBmZnU3XzBfNG56Ym1xNXEsMF9jaDcwZmZ1N18wX2xwcWI3cGRlIiwiZmlsZXMiOiIiLCJ1ZGlkIjoiYWE1ZTFiNmM5Njk4OGQ2OCIsImFkZGl0aW9uYWxfY2FzX3N5c3RlbSI6MH0%3D&signature=33dgytZdOqUqIwcMnwk6dPazq18%3D&",
                "https://cdnapisec.kaltura.com/p/2215841/sp/221584100/playManifest/entryId/1_w9zx2eti/protocol/https/format/applehttp/falvorIds/1_1obpcggb,1_yyuvftfz,1_1xdbzoa6,1_k16ccgto,1_djdf6bk8/a.m3u8",
                "http://qa-apache-testing-ubu-01.dev.kaltura.com/p/1091/sp/109100/playManifest/entryId/0_kf6dw4jr/flavorIds/0_olodnwem,0_k1xomk0s,0_89mswgn1/format/applehttp/protocol/http/a.m3u8",
                "http://qa-apache-testing-ubu-01.dev.kaltura.com/p/1091/sp/109100/playManifest/entryId/0_n916zwym/flavorIds/0_73pxiyv9,0_k1j6qxmr,0_crt7pzof,0_hsfi74pb/format/applehttp/protocol/http/a.m3u8",
                "https://playertest.longtailvideo.com/adaptive/eleph-audio/playlist.m3u8"
        };

        String bgImageUrl[] = {
                "http://cfvod.kaltura.com/p/243342/sp/24334200/thumbnail/entry_id/0_uka1msg4/version/100007/width/1200/hight/780",
                "http://cfvod.kaltura.com/p/2215841/sp/221584100/thumbnail/entry_id/1_w9zx2eti/version/100007/width/1200/hight/780",
                "http://qa-apache-testing-ubu-01.dev.kaltura.com/p/1091/thumbnail/entry_id/0_kf6dw4jr/version/100007/width/1200/hight/780",
                "http://qa-apache-testing-ubu-01.dev.kaltura.com/p/1091/thumbnail/entry_id/0_n916zwym/version/100007/width/1200/hight/780",
                "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/April%20Fool's%202013/Introducing%20Google%20Nose/bg.jpg",
        };
        String cardImageUrl[] = {
                "http://cfvod.kaltura.com/p/243342/sp/24334200/thumbnail/entry_id/0_uka1msg4/version/100007/width/1200/hight/780",
                "http://cfvod.kaltura.com/p/2215841/sp/221584100/thumbnail/entry_id/1_w9zx2eti/version/100007/width/1200/hight/780",
                "http://qa-apache-testing-ubu-01.dev.kaltura.com/p/1091/thumbnail/entry_id/0_kf6dw4jr/version/100007/width/1200/hight/780",
                "http://qa-apache-testing-ubu-01.dev.kaltura.com/p/1091/thumbnail/entry_id/0_n916zwym/version/100007/width/1200/hight/780",
                "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/April%20Fool's%202013/Introducing%20Google%20Nose/card.jpg"
        };

        list.add(buildMovieInfo("category", title[0],
                description, "Studio Zero", videoUrl[0], videoLic[0], cardImageUrl[0], bgImageUrl[0], 60000));
        list.add(buildMovieInfo("category", title[1],
                description, "Studio One", videoUrl[1], "", cardImageUrl[1], bgImageUrl[1], 60000));
        list.add(buildMovieInfo("category", title[2],
                description, "Studio Two", videoUrl[2], "", cardImageUrl[2], bgImageUrl[2], 60000));
        list.add(buildMovieInfo("category", title[3],
                description, "Studio Three", videoUrl[3], "", cardImageUrl[3], bgImageUrl[3], 60000));
        list.add(buildMovieInfo("category", title[4],
                description, "Studio Four", videoUrl[4], "", cardImageUrl[4], bgImageUrl[4], 60000));

        return list;
    }

    private static Movie buildMovieInfo(String category, String title,
                                        String description, String studio, String videoUrl, String videoLic, String cardImageUrl,
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
        movie.setVideoUrl(videoUrl);
        movie.setVideoLic(videoLic);
        movie.setDuration(duration);
        return movie;
    }
}
