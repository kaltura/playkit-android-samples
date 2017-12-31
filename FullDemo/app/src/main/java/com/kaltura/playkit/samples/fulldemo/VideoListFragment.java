package com.kaltura.playkit.samples.fulldemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import static com.kaltura.playkit.samples.fulldemo.MainActivity.SOURCE_URL1;
import static com.kaltura.playkit.samples.fulldemo.R.id.customTag;
import static com.kaltura.playkit.samples.fulldemo.R.id.mediaLic;
import static com.kaltura.playkit.samples.fulldemo.R.id.mediaUrl;


public class VideoListFragment extends Fragment {

    private OnVideoSelectedListener mSelectedCallback;
    LayoutInflater mInflater;
    ViewGroup mContainer;

    private int minAdDurationForSkipButton;
    private boolean isAutoPlay;
    private int startPosition;
    private int adLoadTimeOut;
    private String videoMimeType;
    private int videoBitrate;
    private int companionAdWidth;
    private int companionAdHeight;

    /**
     * Listener called when the user selects a video from the list.
     * Container activity must implement this interface.
     */
    public interface OnVideoSelectedListener {
        public void onVideoSelected(VideoItem videoItem);
    }

    private OnVideoListFragmentResumedListener mResumeCallback;

    /**
     * Listener called when the video list fragment resumes.
     */
    public interface OnVideoListFragmentResumedListener {
        public void onVideoListFragmentResumed();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mSelectedCallback = (OnVideoSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement " + OnVideoSelectedListener.class.getName());
        }

        try {
            mResumeCallback = (OnVideoListFragmentResumedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement " + OnVideoListFragmentResumedListener.class.getName());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mInflater = inflater;
        mContainer = container;

        isAutoPlay    = getArguments().getBoolean(MainActivity.AUTO_PLAY);
        startPosition = getArguments().getInt(MainActivity.START_FROM);
        minAdDurationForSkipButton = getArguments().getInt(MainActivity.MIN_AD_DURATION_FOR_SKIP_BUTTON);
        adLoadTimeOut = getArguments().getInt(MainActivity.AD_LOAD_TIMEOUT);
        videoMimeType = getArguments().getString(MainActivity.MIME_TYPE);
        videoBitrate  = getArguments().getInt(MainActivity.PREFERRED_BITRATE);
        companionAdWidth  = getArguments().getInt(MainActivity.COMPANION_AD_WIDTH);
        companionAdHeight = getArguments().getInt(MainActivity.COMPANION_AD_HEIGHT);

        View rootView = inflater.inflate(R.layout.fragment_video_list, container, false);

        final ListView listView = (ListView) rootView.findViewById(R.id.videoListView);
        VideoItemAdapter videoItemAdapter = new VideoItemAdapter(rootView.getContext(),
                R.layout.video_item, getVideoItems());
        listView.setAdapter(videoItemAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if (mSelectedCallback != null) {
                    VideoItem selectedVideo = (VideoItem) listView.getItemAtPosition(position);

                    // If applicable, prompt the user to input a custom ad tag.
                    if (selectedVideo.getAdTagUrl().equals(getString(
                            R.string.custom_ad_tag_value))) {
                        getCustomAdTag(selectedVideo);
                    } else {
                        mSelectedCallback.onVideoSelected(selectedVideo);
                    }
                }
            }
        });

        return rootView;
    }

    private void getCustomAdTag(VideoItem originalVideoItem) {
        View dialogueView = mInflater.inflate(R.layout.custom_ad_tag, mContainer, false);

        final EditText videoUrl = (EditText) dialogueView.findViewById(mediaUrl);
        videoUrl.setHint("Media URL");

        final EditText licUrl = (EditText) dialogueView.findViewById(mediaLic);
        licUrl.setHint("Media Lic URL");

        final EditText adUrl = (EditText) dialogueView.findViewById(customTag);
        adUrl.setHint("Ad Tag URL/XML");
        final VideoItem videoItem = originalVideoItem;

        new AlertDialog.Builder(this.getActivity())
                .setTitle("Custom Ad Tag URL/XML(plaint text)")
                .setView(dialogueView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String customMediaUrl = (!TextUtils.isEmpty(videoUrl.getText().toString())) ? videoUrl.getText().toString() : SOURCE_URL1;
                        String customMediaLicUrl = (!TextUtils.isEmpty(licUrl.getText())) ? licUrl.getText().toString() : "";
                        String customAdTagUrl = adUrl.getText().toString();

                        VideoItem customAdTagVideoItem = new VideoItem(customMediaUrl,  customMediaLicUrl,
                                videoItem.getTitle(), customAdTagUrl, videoItem.getImageResource());

                        if (mSelectedCallback != null) {
                            mSelectedCallback.onVideoSelected(customAdTagVideoItem);
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .show();
    }

    private List<VideoItem> getVideoItems() {
        final List<VideoItem> videoItems = new ArrayList<VideoItem>();

        // Iterate through the videos' metadata and add video items to list.
        for (int i = 0; i < VideoMetadata.APP_VIDEOS.size(); i++) {
            VideoMetadata videoMetadata = VideoMetadata.APP_VIDEOS.get(i);
            videoItems.add(new VideoItem(videoMetadata.videoUrl, videoMetadata.videoLic, videoMetadata.title,
                    videoMetadata.adTagUrl, videoMetadata.thumbnail));
        }

        return videoItems;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mResumeCallback != null) {
            mResumeCallback.onVideoListFragmentResumed();
        }
    }
}