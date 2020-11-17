package com.kaltura.playkitdemo;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kaltura.android.exoplayer2.C;
import com.kaltura.android.exoplayer2.Timeline;
import com.kaltura.android.exoplayer2.ui.DefaultTimeBar;
import com.kaltura.android.exoplayer2.ui.TimeBar;
import com.kaltura.playkit.PKLog;
import com.kaltura.playkit.Player;
import com.kaltura.playkit.PlayerState;
import com.kaltura.playkit.ads.AdController;
import com.kaltura.playkit.utils.Consts;

import java.util.Formatter;
import java.util.Locale;

/**
 * Created by anton.afanasiev on 07/11/2016.
 */

public class PlaybackControlsView extends LinearLayout implements View.OnClickListener {

    public interface UIListener {
        void onChangeMedia();
    }

    private static final PKLog log = PKLog.get("PlaybackControlsView");
    private static final int PROGRESS_BAR_MAX = 100;

    private Player player;
    private PlayerState playerState;

    private UIListener uiListener;

    private Formatter formatter;
    private StringBuilder formatBuilder;

    private DefaultTimeBar seekBar;
    private TextView tvCurTime, tvTime;
    private ImageButton btnPlay, btnPause, btnFastForward, btnRewind, btnNext, btnPrevious, btnShuffle, btnRepeatToggle, btnVr;

    private boolean dragging = false;

    private ComponentListener componentListener;

    private Runnable updateProgressAction = this::updateProgress;

    public PlaybackControlsView(Context context) {
        this(context, null);
    }

    public PlaybackControlsView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlaybackControlsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.exo_playback_control_view_old, this);
        formatBuilder = new StringBuilder();
        formatter = new Formatter(formatBuilder, Locale.getDefault());
        componentListener = new ComponentListener();
        initPlaybackControls();
    }

    public void setUiListener(UIListener uiListener) {
        this.uiListener = uiListener;
    }

    private void initPlaybackControls() {

        btnPlay = this.findViewById(R.id.kexo_play);
        btnPause = this.findViewById(R.id.kexo_pause);
        btnFastForward = this.findViewById(R.id.kexo_ffwd);
        btnFastForward.setVisibility(GONE);
        btnRewind = this.findViewById(R.id.kexo_rew);
        btnRewind.setVisibility(GONE);
        btnNext = this.findViewById(R.id.kexo_next);
        btnPrevious = this.findViewById(R.id.kexo_prev);
        btnRepeatToggle = this.findViewById(R.id.kexo_repeat_toggle);
        btnRepeatToggle.setVisibility(GONE);
        btnShuffle = this.findViewById(R.id.kexo_shuffle);
        btnShuffle.setVisibility(GONE);
        btnVr = this.findViewById(R.id.kexo_vr);
        btnVr.setVisibility(GONE);

        btnPlay.setOnClickListener(this);
        btnPause.setOnClickListener(this);
        btnFastForward.setOnClickListener(this);
        btnRewind.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnPrevious.setOnClickListener(this);

        seekBar = this.findViewById(R.id.kexo_progress);
        seekBar.setPlayedColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        seekBar.setBufferedColor(ContextCompat.getColor(getContext(), R.color.grey));
        seekBar.setUnplayedColor(ContextCompat.getColor(getContext(), R.color.black));
        seekBar.setScrubberColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        seekBar.addListener(componentListener);

        tvCurTime = this.findViewById(R.id.kexo_position);
        tvTime = this.findViewById(R.id.kexo_duration);
    }


    private void updateProgress() {
        long duration = C.TIME_UNSET;
        long position = C.POSITION_UNSET;
        long bufferedPosition = 0;
        if (player != null) {
            AdController adController = player.getController(AdController.class);
            if (adController != null && adController.isAdDisplayed()) {
                duration = adController.getAdDuration();
                position = adController.getAdCurrentPosition();

                //log.d("adController Duration:" + duration);
                //log.d("adController Position:" + position);
            } else {
                duration = player.getDuration();
                position = player.getCurrentPosition();
                //log.d("Duration:" + duration);
                //log.d("Position:" + position);
                bufferedPosition = player.getBufferedPosition();
            }
        }

        if (duration != C.TIME_UNSET) {
            //log.d("updateProgress Set Duration:" + duration);
            tvTime.setText(stringForTime(duration));
        }

        if (!dragging && position != C.POSITION_UNSET && duration != C.TIME_UNSET) {
            //log.d("updateProgress Set Position:" + position);
            tvCurTime.setText(stringForTime(position));
            seekBar.setPosition(position);
            seekBar.setDuration(duration);
        }

        seekBar.setBufferedPosition(bufferedPosition);
        // Remove scheduled updates.
        removeCallbacks(updateProgressAction);
        // Schedule an update if necessary.
        if (playerState != PlayerState.IDLE) {
            long delayMs = 1000;
            postDelayed(updateProgressAction, delayMs);
        }
    }

    /**
     * Component Listener for Default time bar from ExoPlayer UI
     */
    private final class ComponentListener
            implements com.kaltura.android.exoplayer2.Player.EventListener, TimeBar.OnScrubListener, OnClickListener {

        @Override
        public void onScrubStart(TimeBar timeBar, long position) {
            dragging = true;
        }

        @Override
        public void onScrubMove(TimeBar timeBar, long position) {
            if (player != null) {
                tvCurTime.setText(stringForTime(position));
            }
        }

        @Override
        public void onScrubStop(TimeBar timeBar, long position, boolean canceled) {
            dragging = false;
            if (player != null) {
                player.seekTo(position);

            }
        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            updateProgress();
        }

        @Override
        public void onPositionDiscontinuity(@com.kaltura.android.exoplayer2.Player.DiscontinuityReason int reason) {
            updateProgress();
        }

        @Override
        public void onTimelineChanged(Timeline timeline, @com.kaltura.android.exoplayer2.Player.TimelineChangeReason int reason) {
            updateProgress();
        }

        @Override
        public void onClick(View view) {
        }
    }

    private int progressBarValue(long position) {
        int progressValue = 0;
        if (player != null) {

            long duration = player.getDuration();
            //log.d("position = "  + position);
            //log.d("duration = "  + duration);
            AdController adController = player.getController(AdController.class);
            if (adController != null && adController.isAdDisplayed()) {
                duration = adController.getAdDuration();
            }
            if (duration > 0) {
                //log.d("position = "  + position);
                progressValue = Math.round((position * PROGRESS_BAR_MAX) / duration);
            }
            //log.d("progressValue = "  + progressValue);
        }

        return progressValue;
    }

    private long positionValue(long progress) {
        long positionValue = 0;
        if (player != null) {
            long duration = player.getDuration();
            AdController adController = player.getController(AdController.class);
            if (adController != null && adController.isAdDisplayed()) {
                duration = adController.getAdDuration();
            }
            positionValue = Math.round((duration * progress) / PROGRESS_BAR_MAX);
        }

        return positionValue;
    }

    private String stringForTime(long timeMs) {

        long totalSeconds = (timeMs + 500) / 1000;
        long seconds = totalSeconds % 60;
        long minutes = (totalSeconds / 60) % 60;
        long hours = totalSeconds / 3600;
        formatBuilder.setLength(0);
        return hours > 0 ? formatter.format("%d:%02d:%02d", hours, minutes, seconds).toString()
                : formatter.format("%02d:%02d", minutes, seconds).toString();
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setPlayerState(PlayerState playerState) {
        this.playerState = playerState;
        updateProgress();
    }

    public void setSeekBarStateForAd(boolean isAdPlaying) {
        seekBar.setEnabled(!isAdPlaying);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.kexo_play:
                if (player != null) {
                    player.play();
                }
                break;
            case R.id.kexo_pause:
                if (player != null) {
                    player.pause();
                }
                break;
            case R.id.kexo_ffwd:
                //Do nothing for now
                break;
            case R.id.kexo_rew:
                ///Do nothing for now
                break;
            case R.id.kexo_next:
            case R.id.kexo_prev:
                uiListener.onChangeMedia();
                break;
        }
    }

    public void release() {
        removeCallbacks(updateProgressAction);
    }

    public void resume() {
        updateProgress();
    }

}
