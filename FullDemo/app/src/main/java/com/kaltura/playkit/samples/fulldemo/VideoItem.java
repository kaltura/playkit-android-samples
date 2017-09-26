package com.kaltura.playkit.samples.fulldemo;

public final class VideoItem {

    private final int mThumbnailResourceId;
    private final String mTitle;
    private final String mVideoUrl;
    private final String mAdTagUrl;

    public VideoItem(String videoUrl, String title,  String adTagUrl, int thumbnailResourceId) {
        super();
        mThumbnailResourceId = thumbnailResourceId;
        mTitle = title;
        mAdTagUrl = adTagUrl;
        mVideoUrl = videoUrl;
    }

    /**
     * Returns the video thumbnail image resource.
     */
    public int getImageResource() {
        return mThumbnailResourceId;
    }

    /**
     * Returns the title of the video item.
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Returns the URL of the content video.
     */
    public String getVideoUrl() {
        return mVideoUrl;
    }

    /**
     * Returns the ad tag for the video.
     */
    public String getAdTagUrl() {
        return mAdTagUrl;
    }
}