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
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v17.leanback.widget.AbstractDetailsDescriptionPresenter;
import android.support.v17.leanback.widget.Action;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ClassPresenterSelector;
import android.support.v17.leanback.widget.ControlButtonPresenterSelector;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnActionClickedListener;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.PlaybackControlsRow;
import android.support.v17.leanback.widget.PlaybackControlsRow.FastForwardAction;
import android.support.v17.leanback.widget.PlaybackControlsRow.PlayPauseAction;
import android.support.v17.leanback.widget.PlaybackControlsRow.RepeatAction;
import android.support.v17.leanback.widget.PlaybackControlsRow.RewindAction;
import android.support.v17.leanback.widget.PlaybackControlsRow.ShuffleAction;
import android.support.v17.leanback.widget.PlaybackControlsRow.SkipNextAction;
import android.support.v17.leanback.widget.PlaybackControlsRow.SkipPreviousAction;
import android.support.v17.leanback.widget.PlaybackControlsRow.ThumbsDownAction;
import android.support.v17.leanback.widget.PlaybackControlsRow.ThumbsUpAction;
import android.support.v17.leanback.widget.PlaybackControlsRowPresenter;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.kaltura.playkit.player.PKTracks;
import com.kaltura.playkit.utils.Consts;

import java.util.ArrayList;
import java.util.List;

/*
 * Class for video playback with media control
 */
public class PlaybackOverlayFragment extends android.support.v17.leanback.app.PlaybackFragment {
    private static final String TAG = "PlaybackOverlayFragment";

    private static final boolean SHOW_DETAIL = true;
    private static final boolean HIDE_MORE_ACTIONS = false;
    private static final int PRIMARY_CONTROLS = 5;
    private static final boolean SHOW_IMAGE = PRIMARY_CONTROLS <= 5;
    private static final int BACKGROUND_TYPE = PlaybackOverlayFragment.BG_LIGHT;
    private static final int CARD_WIDTH = 200;
    private static final int CARD_HEIGHT = 240;
    private static final int DEFAULT_UPDATE_PERIOD = 1000;
    private static final int UPDATE_PERIOD = 16;
    private static final int SIMULATED_BUFFERED_TIME = 10000;

    private ArrayObjectAdapter mRowsAdapter;
    private ArrayObjectAdapter mPrimaryActionsAdapter;
    private ArrayObjectAdapter mSecondaryActionsAdapter;
    private PlayPauseAction mPlayPauseAction;
    private RepeatAction mRepeatAction;
    private ThumbsUpAction mThumbsUpAction;
    private ThumbsDownAction mThumbsDownAction;
    private ShuffleAction mShuffleAction;
    private FastForwardAction mFastForwardAction;
    private RewindAction mRewindAction;
    private PlaybackControlsRow.HighQualityAction mHighQualityAction;
    private PlaybackControlsRow.ClosedCaptioningAction mClosedCaptioningAction;
    private SkipNextAction mSkipNextAction;
    private SkipPreviousAction mSkipPreviousAction;
    private PlaybackControlsRow mPlaybackControlsRow;
    private PlaybackControlsRow.MoreActions mMoreActions;
    private ArrayList<Movie> mItems = new ArrayList<Movie>();
    private int mCurrentItem;
    private Handler mHandler;
    private Runnable mRunnable;
    private Movie mSelectedMovie;

    private OnPlayPauseClickedListener mCallback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mItems = new ArrayList<Movie>();
        mSelectedMovie = (Movie) getActivity().getIntent().getExtras().getParcelable(DetailsActivity.MOVIE);
               // .getIntent().getSerializableExtra(DetailsActivity.MOVIE);

        List<Movie> movies = MainActivity.list;

        for (int j = 0; j < movies.size(); j++) {
            mItems.add(movies.get(j));
            if (mSelectedMovie.getTitle().contentEquals(movies.get(j).getTitle())) {
                mCurrentItem = j;
            }
        }

        mHandler = new Handler();

        setBackgroundType(BACKGROUND_TYPE);
        setFadingEnabled(false);

        setupRows();

        setOnItemViewSelectedListener(new OnItemViewSelectedListener() {
            @Override
            public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item,
                                       RowPresenter.ViewHolder rowViewHolder, Row row) {
                Log.i(TAG, "onItemSelected: " + item + " row " + row);
            }
        });
        setOnItemViewClickedListener(new OnItemViewClickedListener() {
            @Override
            public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                      RowPresenter.ViewHolder rowViewHolder, Row row) {
                Log.i(TAG, "onItemClicked: " + item + " row " + row);
            }
        });
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        if (context instanceof OnPlayPauseClickedListener) {
            mCallback = (OnPlayPauseClickedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnPlayPauseClickedListener");
        }
    }

    private void setupRows() {

        ClassPresenterSelector ps = new ClassPresenterSelector();

        PlaybackControlsRowPresenter playbackControlsRowPresenter;
        if (SHOW_DETAIL) {
            playbackControlsRowPresenter = new PlaybackControlsRowPresenter(
                    new DescriptionPresenter());
        } else {
            playbackControlsRowPresenter = new PlaybackControlsRowPresenter();
        }

        playbackControlsRowPresenter.setOnActionClickedListener(new OnActionClickedListener() {
            public void onActionClicked(Action action) {
                Log.d(TAG, "onActionClicked action.getId() = " + action.getId());
                if (action.getId() == mRepeatAction.getId()) {
                    repeate();
                } else if (action.getId() == mPlayPauseAction.getId()) {
                    togglePlayback(mPlayPauseAction.getIndex() == PlayPauseAction.PLAY);
                } else if (action.getId() == mSkipNextAction.getId()) {
                    next();
                } else if (action.getId() == mSkipPreviousAction.getId()) {
                    prev();
                } else if (action.getId() == mFastForwardAction.getId()) {
                    Log.d(TAG, "setOnActionClickedListener getPosition = " + mCallback.getPosition() + " getDuration " + mCallback.getDuration());
                    mCallback.seekTo((mCallback.getPosition() + 15000 < mCallback.getDuration()) ? (mCallback.getPosition() + 15000) : mCallback.getDuration() - 500) ;
                } else if (action.getId() == mRewindAction.getId()) {
                    Log.d(TAG, "setOnActionClickedListener getPosition = " + mCallback.getPosition() + " getDuration " + mCallback.getDuration());
                    mCallback.seekTo(((mCallback.getPosition() - 15000) >= 0) ? (mCallback.getPosition() - 15000) : 0);
                } else if (action.getId() == mHighQualityAction.getId()) {
                    Log.d(TAG, "mHighQualityAction");
                    CharSequence[] items = null;
                    int itemIndex = 0;
                    PKTracks pkTracks = mCallback.onTracksAvailable();
                    if (pkTracks != null && pkTracks.getVideoTracks().size() > 1) {

                        final List<TrackItem> itemsList = TracksUtils.createTrackItems(Consts.TRACK_TYPE_VIDEO, pkTracks);
                        items = new CharSequence[itemsList.size()];
                        for (TrackItem videoTrack : itemsList) {
                            CharSequence cs = videoTrack.getTrackDescription();
                            items[itemIndex++] = " " + cs + " ";
                        }
                        for (CharSequence cc : items) {
                            Log.d(TAG, "items = " + cc);
                        }

                        final ArrayList<Integer> seletedItem = new ArrayList<>(1);
                        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                                .setTitle("Select Video Track")
                                .setSingleChoiceItems(items, TracksUtils.getLastSelectedTrack(Consts.TRACK_TYPE_VIDEO) == -1 ?  pkTracks.getDefaultVideoTrackIndex() : TracksUtils.getLastSelectedTrack(Consts.TRACK_TYPE_VIDEO), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int item) {
                                        seletedItem.clear();
                                        seletedItem.add(item);
                                    }
                                }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        if (!seletedItem.isEmpty()) {
                                            TracksUtils.setLastSelectedTrack(Consts.TRACK_TYPE_VIDEO, seletedItem.get(0));
                                            mCallback.changeTrack(itemsList.get(seletedItem.get(0)).getUniqueId());
                                        }
                                    }
                                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        //  Your code when user clicked on Cancel
                                    }
                                }).create();
                        dialog.show();
                    }

                } else if (action.getId() == mClosedCaptioningAction.getId()) {
                    Log.d(TAG, "mClosedCaptioningAction");
                    CharSequence[] items = null;
                    int itemIndex = 0;
                    PKTracks pkTracks = mCallback.onTracksAvailable();
                    if (pkTracks != null && pkTracks.getTextTracks().size() > 1) {
                        final List<TrackItem> itemsList = TracksUtils.createTrackItems(Consts.TRACK_TYPE_TEXT, pkTracks);
                        items = new CharSequence[itemsList.size()];
                        for (TrackItem textTrack : itemsList) {
                            CharSequence cs = textTrack.getTrackDescription();
                            items[itemIndex++] = " " + cs + " ";
                        }
                        for (CharSequence cc : items) {
                            Log.d(TAG, "items = " + cc);
                        }

                        final ArrayList<Integer> seletedItem = new ArrayList<>(1);

                        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                                .setTitle("Select Text Track")
                                .setSingleChoiceItems(items, (TracksUtils.getLastSelectedTrack(Consts.TRACK_TYPE_TEXT) == -1) ? pkTracks.getDefaultTextTrackIndex() : TracksUtils.getLastSelectedTrack(Consts.TRACK_TYPE_TEXT), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int item) {
                                        seletedItem.clear();
                                        seletedItem.add(item);
                                    }
                                }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        if (!seletedItem.isEmpty()) {
                                            TracksUtils.setLastSelectedTrack(Consts.TRACK_TYPE_TEXT, seletedItem.get(0));
                                            mCallback.changeTrack(itemsList.get(seletedItem.get(0)).getUniqueId());
                                        }
                                    }
                                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        //  Your code when user clicked on Cancel
                                    }
                                }).create();
                        dialog.show();
                    }
                } else if (action.getId() == mThumbsUpAction.getId()) {
                    Log.d(TAG, "mAudioTracks");
                    CharSequence[] items = null;
                    int itemIndex = 0;
                    PKTracks pkTracks = mCallback.onTracksAvailable();
                    if (pkTracks != null && pkTracks.getAudioTracks().size() > 1) {
                        final List<TrackItem> itemsList = TracksUtils.createTrackItems(Consts.TRACK_TYPE_AUDIO, pkTracks);
                        items = new CharSequence[itemsList.size()];
                        for (TrackItem textTrack : itemsList) {
                            CharSequence cs = textTrack.getTrackDescription();
                            items[itemIndex++] = " " + cs + " ";
                        }
                        for (CharSequence cc : items) {
                            Log.d(TAG, "items = " + cc);
                        }

                        final ArrayList<Integer> seletedItem = new ArrayList<>(1);

                        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                                .setTitle("Select Audio Track")
                                .setSingleChoiceItems(items, (TracksUtils.getLastSelectedTrack(Consts.TRACK_TYPE_AUDIO) == -1) ? pkTracks.getDefaultAudioTrackIndex() : TracksUtils.getLastSelectedTrack(Consts.TRACK_TYPE_AUDIO), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int item) {
                                        seletedItem.clear();
                                        seletedItem.add(item);
                                    }
                                }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        if (!seletedItem.isEmpty()) {
                                            TracksUtils.setLastSelectedTrack(Consts.TRACK_TYPE_AUDIO, seletedItem.get(0));
                                            mCallback.changeTrack(itemsList.get(seletedItem.get(0)).getUniqueId());
                                        }
                                    }
                                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        //  Your code when user clicked on Cancel
                                    }
                                }).create();
                        dialog.show();
                    }
                }
                if (action instanceof PlaybackControlsRow.MultiAction) {
                    ((PlaybackControlsRow.MultiAction) action).nextIndex();
                    notifyChanged(action);
                }
            }
        });
        playbackControlsRowPresenter.setSecondaryActionsHidden(HIDE_MORE_ACTIONS);

        ps.addClassPresenter(PlaybackControlsRow.class, playbackControlsRowPresenter);
        ps.addClassPresenter(ListRow.class, new ListRowPresenter());
        mRowsAdapter = new ArrayObjectAdapter(ps);

        addPlaybackControlsRow();
        addOtherRows();

        setAdapter(mRowsAdapter);
    }

    private void repeate() {
        mCallback.replay();
    }

    public void togglePlayback(boolean playPause) {
        if (playPause) {
            startProgressAutomation();
            setFadingEnabled(true);
            mCallback.onFragmentPlayPause(mItems.get(mCurrentItem),
                    mPlaybackControlsRow.getCurrentTime(), true);
            mPlayPauseAction.setIcon(mPlayPauseAction.getDrawable(PlayPauseAction.PAUSE));
        } else {
            stopProgressAutomation();
            setFadingEnabled(false);
            mCallback.onFragmentPlayPause(mItems.get(mCurrentItem),
                    mPlaybackControlsRow.getCurrentTime(), false);
            mPlayPauseAction.setIcon(mPlayPauseAction.getDrawable(PlayPauseAction.PLAY));
        }
        notifyChanged(mPlayPauseAction);
    }

    private int getDuration() {
        long duration = 0;
        Movie movie = mItems.get(mCurrentItem);
        return (int) movie.getDuration();
    }

    private void addPlaybackControlsRow() {
        if (SHOW_DETAIL) {
            mPlaybackControlsRow = new PlaybackControlsRow(mSelectedMovie);
        } else {
            mPlaybackControlsRow = new PlaybackControlsRow();
        }
        mRowsAdapter.add(mPlaybackControlsRow);

        updatePlaybackRow(mCurrentItem);

        ControlButtonPresenterSelector presenterSelector = new ControlButtonPresenterSelector();
        mPrimaryActionsAdapter = new ArrayObjectAdapter(presenterSelector);
        mSecondaryActionsAdapter = new ArrayObjectAdapter(presenterSelector);
        mPlaybackControlsRow.setPrimaryActionsAdapter(mPrimaryActionsAdapter);
        mPlaybackControlsRow.setSecondaryActionsAdapter(mSecondaryActionsAdapter);

        mPlayPauseAction = new PlayPauseAction(getActivity());
        mRepeatAction = new RepeatAction(getActivity());
        mThumbsUpAction = new ThumbsUpAction(getActivity());
        mThumbsDownAction = new ThumbsDownAction(getActivity());
        mShuffleAction = new ShuffleAction(getActivity());
        mSkipNextAction = new PlaybackControlsRow.SkipNextAction(getActivity());
        mMoreActions    = new PlaybackControlsRow.MoreActions(getActivity());
        mSkipPreviousAction = new PlaybackControlsRow.SkipPreviousAction(getActivity());
        mFastForwardAction = new PlaybackControlsRow.FastForwardAction(getActivity());
        mRewindAction = new PlaybackControlsRow.RewindAction(getActivity());
        mHighQualityAction = new PlaybackControlsRow.HighQualityAction(getActivity());
        mClosedCaptioningAction = new PlaybackControlsRow.ClosedCaptioningAction(getActivity());

        mPrimaryActionsAdapter.add(mSkipPreviousAction);
        if (PRIMARY_CONTROLS > 3) {
            mPrimaryActionsAdapter.add(new PlaybackControlsRow.RewindAction(getActivity()));
        }
        mPrimaryActionsAdapter.add(mPlayPauseAction);
        if (PRIMARY_CONTROLS > 3) {
            mPrimaryActionsAdapter.add(new PlaybackControlsRow.FastForwardAction(getActivity()));
        }
        mPrimaryActionsAdapter.add(mSkipNextAction);
        //mSecondaryActionsAdapter.add(mShuffleAction);
        mSecondaryActionsAdapter.add(mRepeatAction);
        if (PRIMARY_CONTROLS > 5) {
            mPrimaryActionsAdapter.add(mThumbsUpAction);
        } else {
            mSecondaryActionsAdapter.add(mThumbsUpAction);
        }
        if (PRIMARY_CONTROLS > 5) {
            //mPrimaryActionsAdapter.add(mThumbsDownAction);
        } else {
            //mSecondaryActionsAdapter.add(mThumbsDownAction);
        }
        mSecondaryActionsAdapter.add(mHighQualityAction);
        mSecondaryActionsAdapter.add(mClosedCaptioningAction);
        mSecondaryActionsAdapter.add(mMoreActions);
    }

    private void notifyChanged(Action action) {
        ArrayObjectAdapter adapter = mPrimaryActionsAdapter;
        if (adapter.indexOf(action) >= 0) {
            adapter.notifyArrayItemRangeChanged(adapter.indexOf(action), 1);
            return;
        }
        adapter = mSecondaryActionsAdapter;
        if (adapter.indexOf(action) >= 0) {
            adapter.notifyArrayItemRangeChanged(adapter.indexOf(action), 1);
            return;
        }
    }

    private void updatePlaybackRow(int index) {
        if (mPlaybackControlsRow.getItem() != null) {
            Movie item = (Movie) mPlaybackControlsRow.getItem();
            item.setTitle(mItems.get(mCurrentItem).getTitle());
            item.setStudio(mItems.get(mCurrentItem).getStudio());
        }
        if (SHOW_IMAGE) {
            updateVideoImage(mItems.get(mCurrentItem).getCardImageURI().toString());
        }
        mRowsAdapter.notifyArrayItemRangeChanged(0, 1);
        mPlaybackControlsRow.setTotalTime(getDuration());
        mPlaybackControlsRow.setCurrentTime(0);
        mPlaybackControlsRow.setBufferedProgress(0);
    }

    private void addOtherRows() {
        ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(new CardPresenter());
        for (Movie movie : mItems) {
            listRowAdapter.add(movie);
        }
        HeaderItem header = new HeaderItem(0, getString(R.string.related_movies));
        mRowsAdapter.add(new ListRow(header, listRowAdapter));

    }

    private int getUpdatePeriod() {
        if (getView() == null || mPlaybackControlsRow.getTotalTime() <= 0) {
            return DEFAULT_UPDATE_PERIOD;
        }
        return Math.max(UPDATE_PERIOD, mPlaybackControlsRow.getTotalTime() / getView().getWidth());
    }

    private void startProgressAutomation() {
        mRunnable = new Runnable() {
            @Override
            public void run() {
                int updatePeriod = getUpdatePeriod();

                int currentTime = (int)mCallback.getPosition();//mPlaybackControlsRow.getCurrentTime() + updatePeriod;
                int totalTime   = (int)mCallback.getDuration(); //mPlaybackControlsRow.getTotalTime();
                Log.d(TAG, "startProgressAutomation currentTime = " + currentTime + " totalTime = " + totalTime);
                mPlaybackControlsRow.setCurrentTime(currentTime);
                mPlaybackControlsRow.setBufferedProgress(currentTime + SIMULATED_BUFFERED_TIME);

                if (totalTime > 0 && totalTime <= currentTime) {
                    next();
                }
                mHandler.postDelayed(this, updatePeriod);
            }
        };
        mHandler.postDelayed(mRunnable, getUpdatePeriod());
    }

    private void next() {
        mCallback.stop();
        if (++mCurrentItem >= mItems.size()) {
            mCurrentItem = 0;
        }

        if (mPlayPauseAction.getIndex() == PlayPauseAction.PLAY) {
            mCallback.onFragmentPlayPause(mItems.get(mCurrentItem), 0, false);
        } else {
            mCallback.onFragmentPlayPause(mItems.get(mCurrentItem), 0, true);
        }
        updatePlaybackRow(mCurrentItem);
    }

    private void prev() {
        mCallback.stop();
        if (--mCurrentItem < 0) {
            mCurrentItem = mItems.size() - 1;
        }
        if (mPlayPauseAction.getIndex() == PlayPauseAction.PLAY) {
            mCallback.onFragmentPlayPause(mItems.get(mCurrentItem), 0, false);
        } else {
            mCallback.onFragmentPlayPause(mItems.get(mCurrentItem), 0, true);
        }
        updatePlaybackRow(mCurrentItem);
    }

    private void stopProgressAutomation() {
        if (mHandler != null && mRunnable != null) {
            mHandler.removeCallbacks(mRunnable);
        }
    }

    @Override
    public void onStop() {
        stopProgressAutomation();
        super.onStop();
    }

    protected void updateVideoImage(String uri) {
        Glide.with(getActivity())
                .load(uri)
                .centerCrop()
                .into(new SimpleTarget<GlideDrawable>(CARD_WIDTH, CARD_HEIGHT) {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        mPlaybackControlsRow.setImageDrawable(resource);
                        mRowsAdapter.notifyArrayItemRangeChanged(0, mRowsAdapter.size());
                    }
                });
    }

    // Container Activity must implement this interface
    public interface OnPlayPauseClickedListener {
        void onFragmentPlayPause(Movie movie, int position, Boolean playPause);
        PKTracks onTracksAvailable();
        void changeTrack(String uniqueId);
        void seekTo(long position);
        long getPosition();
        long getDuration();
        void replay();
        void stop();
    }

    static class DescriptionPresenter extends AbstractDetailsDescriptionPresenter {
        @Override
        protected void onBindDescription(ViewHolder viewHolder, Object item) {
            viewHolder.getTitle().setText(((Movie) item).getTitle());
            viewHolder.getSubtitle().setText(((Movie) item).getStudio());
        }
    }
}
