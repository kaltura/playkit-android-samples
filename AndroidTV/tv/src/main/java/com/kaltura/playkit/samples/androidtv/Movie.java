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

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;

import com.kaltura.playkit.PKMediaEntry;

import java.net.URI;
import java.net.URISyntaxException;

/*
 * Movie class represents video entity with title, description, image thumbs and video url.
 *
 */
public class Movie implements Parcelable, Comparable<Movie> {
    static final long serialVersionUID = 727566175075960653L;
    private static long count = 0;
    private long id;
    private String title;
    private String description;
    private String bgImageUrl;
    private String cardImageUrl;
    private String videoUrl;
    private String studio;
    private String category;
    private int duration;
    private PKMediaEntry pkMediaEntry;
    public int index;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Movie() {
    }

    public PKMediaEntry getPkMediaEntry() {
        return pkMediaEntry;
    }

    public void setPkMediaEntry(PKMediaEntry pkMediaEntry) {
        this.pkMediaEntry = pkMediaEntry;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public static long getCount() {
        return count;
    }

    public static void incCount() {
        count++;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStudio() {
        return studio;
    }

    public void setStudio(String studio) {
        this.studio = studio;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getBackgroundImageUrl() {
        return bgImageUrl;
    }

    public void setBackgroundImageUrl(String bgImageUrl) {
        this.bgImageUrl = bgImageUrl;
    }

    public String getCardImageUrl() {
        return cardImageUrl;
    }

    public void setCardImageUrl(String cardImageUrl) {
        this.cardImageUrl = cardImageUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public URI getBackgroundImageURI() {
        try {
            Log.d("BACK MOVIE: ", bgImageUrl);
            return new URI(getBackgroundImageUrl());
        } catch (URISyntaxException e) {
            Log.d("URI exception: ", bgImageUrl);
            return null;
        }
    }

    public URI getCardImageURI() {
        try {
            return new URI(getCardImageUrl());
        } catch (URISyntaxException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", videoUrl='" + videoUrl + '\'' +
                ", backgroundImageUrl='" + bgImageUrl + '\'' +
                ", backgroundImageURI='" + getBackgroundImageURI().toString() + '\'' +
                ", cardImageUrl='" + cardImageUrl + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeString(this.bgImageUrl);
        dest.writeString(this.cardImageUrl);
        dest.writeString(this.videoUrl);
        dest.writeString(this.studio);
        dest.writeString(this.category);
        dest.writeInt(this.duration);
        dest.writeParcelable(this.pkMediaEntry, flags);
    }

    protected Movie(Parcel in) {
        this.id = in.readLong();
        this.title = in.readString();
        this.description = in.readString();
        this.bgImageUrl = in.readString();
        this.cardImageUrl = in.readString();
        this.videoUrl = in.readString();
        this.studio = in.readString();
        this.category = in.readString();
        this.duration = in.readInt();
        this.pkMediaEntry = in.readParcelable(PKMediaEntry.class.getClassLoader());
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @Override
    public int compareTo(@NonNull Movie movie) {
        if ( this.getIndex() > movie.getIndex() )
            return 1;
        else if ( this.getIndex() < movie.getIndex() )
            return -1;
        else {
            return  0;
        }
    }
}
