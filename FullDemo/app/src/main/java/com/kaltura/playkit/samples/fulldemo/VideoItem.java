package com.kaltura.playkit.samples.fulldemo;

public final class VideoItem {

    private final int mThumbnailResourceId;
    private final String mTitle;
    private final String mVideoUrl;
    private final String mVideoLic;


    private final String mAdTagUrl;

    /** The LIVE IMADAI **/
    private final String mAssetKey;

    /** The VOD IMADAI **/
    private final String mContentSourceId;
    private final String mVideoId;


    public VideoItem(String title, String videoUrl, String videoLic, String adTagUrl, int thumbnailResourceId) {
        super();
        this.mVideoUrl = videoUrl;
        this.mVideoLic = videoLic;
        this.mThumbnailResourceId = thumbnailResourceId;
        this.mTitle = title;
        this.mAdTagUrl = adTagUrl;
        this.mAssetKey = null;
        this.mContentSourceId = null;
        this.mVideoId = null;

    }

    public VideoItem(String title, String videoUrl, String videoLic, String assetKey, String contentSourceId, String videoId, int thumbnailResourceId) {
        super();
        this.mVideoUrl = videoUrl;
        this.mVideoLic = videoLic;
        this.mThumbnailResourceId = thumbnailResourceId;
        this.mTitle = title;
        this.mAssetKey = assetKey;
        this.mContentSourceId = contentSourceId;
        this.mVideoId = videoId;
        this.mAdTagUrl = null;
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

    /**
     * Returns the Video lic url for the video.
     */
    public String getVideoLic() {
        return mVideoLic;
    }

    public String getAssetKey() {
        return mAssetKey;
    }

    public String getContentSourceId() {
        return mContentSourceId;
    }

    public String getVideoId() {
        return mVideoId;
    }
}