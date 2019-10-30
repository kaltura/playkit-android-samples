package com.kaltura.playkit.samples.vttthumbnailsample

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.kaltura.android.exoplayer2.C
import com.kaltura.android.exoplayer2.Timeline
import com.kaltura.android.exoplayer2.ui.DefaultTimeBar
import com.kaltura.android.exoplayer2.ui.TimeBar
import com.kaltura.playkit.Player
import com.kaltura.playkit.PlayerState
import com.kaltura.playkit.ads.AdController
import java.util.*

class PlaybackControlsView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr), View.OnClickListener {

    private val progressBarMax = 100
    private var player: Player? = null
    private var playerState: PlayerState? = null

    private val formatter: Formatter
    private val formatBuilder: StringBuilder

    private var seekBar: DefaultTimeBar? = null
    private var tvCurTime: TextView? = null
    private var tvTime: TextView? = null
    private var btnPlay: ImageButton? = null
    private var btnPause: ImageButton? = null

    private var dragging = false

    private val componentListener: ComponentListener

    private val updateProgressAction = Runnable { this.updateProgress() }

    init {
        LayoutInflater.from(context).inflate(R.layout.exo_playback_control_view_old, this)
        formatBuilder = StringBuilder()
        formatter = Formatter(formatBuilder, Locale.getDefault())
        componentListener = ComponentListener()
        initPlaybackControls()
    }

    private fun initPlaybackControls() {

        btnPlay = this.findViewById(R.id.exo_play)
        btnPause = this.findViewById(R.id.exo_pause)

        btnPlay!!.setOnClickListener(this)
        btnPause!!.setOnClickListener(this)

        seekBar = this.findViewById(R.id.exo_progress)
        seekBar!!.setPlayedColor(ContextCompat.getColor(context, R.color.colorAccent))
        seekBar!!.setBufferedColor(ContextCompat.getColor(context, R.color.grey))
        seekBar!!.setUnplayedColor(ContextCompat.getColor(context, R.color.black))
        seekBar!!.setScrubberColor(ContextCompat.getColor(context, R.color.colorAccent))
        seekBar!!.addListener(componentListener)

        tvCurTime = this.findViewById(R.id.exo_position)
        tvTime = this.findViewById(R.id.exo_duration)
    }

    private fun updateProgress() {
        var duration = C.TIME_UNSET
        var position = C.POSITION_UNSET.toLong()
        var bufferedPosition: Long = 0
        if (player != null) {
            val adController = player!!.getController(AdController::class.java)
            if (adController != null && adController.isAdDisplayed) {
                duration = adController.adDuration
                position = adController.adCurrentPosition
            } else {
                duration = player!!.duration
                position = player!!.currentPosition
                bufferedPosition = player!!.bufferedPosition
            }
        }

        if (duration != C.TIME_UNSET) {
            //log.d("updateProgress Set Duration:" + duration);
            tvTime!!.text = stringForTime(duration)
        }

        if (!dragging && position != C.POSITION_UNSET.toLong() && duration != C.TIME_UNSET) {
            //log.d("updateProgress Set Position:" + position);
            tvCurTime!!.text = stringForTime(position)
            seekBar!!.setPosition(progressBarValue(position).toLong())
            seekBar!!.setDuration(progressBarValue(duration).toLong())
        }

        seekBar!!.setBufferedPosition(progressBarValue(bufferedPosition).toLong())
        // Remove scheduled updates.
        removeCallbacks(updateProgressAction)
        // Schedule an update if necessary.
        if (playerState != PlayerState.IDLE) {
            val delayMs: Long = 500
            postDelayed(updateProgressAction, delayMs)
        }
    }

    /**
     * Component Listener for Default time bar from ExoPlayer UI
     */
    private inner class ComponentListener : com.kaltura.android.exoplayer2.Player.EventListener, TimeBar.OnScrubListener, View.OnClickListener {

        override fun onScrubStart(timeBar: TimeBar, position: Long) {
            dragging = true
        }

        override fun onScrubMove(timeBar: TimeBar, position: Long) {
            if (player != null) {
                tvCurTime!!.text = stringForTime(position * player!!.duration / progressBarMax)
            }
        }

        override fun onScrubStop(timeBar: TimeBar, position: Long, canceled: Boolean) {
            dragging = false
            if (player != null) {
                player!!.seekTo(position * player!!.duration / progressBarMax)

            }
        }

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            updateProgress()
        }

        override fun onPositionDiscontinuity(@com.kaltura.android.exoplayer2.Player.DiscontinuityReason reason: Int) {
            updateProgress()
        }

        override fun onTimelineChanged(timeline: Timeline?, manifest: Any?, @com.kaltura.android.exoplayer2.Player.TimelineChangeReason reason: Int) {
            updateProgress()
        }

        override fun onClick(view: View) {}
    }

    private fun progressBarValue(position: Long): Int {
        var progressValue = 0
        if (player != null) {

            var duration = player!!.duration
            //log.d("position = "  + position);
            //log.d("duration = "  + duration);
            val adController = player!!.getController(AdController::class.java)
            if (adController != null && adController.isAdDisplayed) {
                duration = adController.adDuration
            }
            if (duration > 0) {
                //log.d("position = "  + position);
                progressValue = Math.round((position * progressBarMax / duration).toFloat())
            }
            //log.d("progressValue = "  + progressValue);
        }

        return progressValue
    }

    private fun stringForTime(timeMs: Long): String {

        val totalSeconds = (timeMs + 500) / 1000
        val seconds = totalSeconds % 60
        val minutes = totalSeconds / 60 % 60
        val hours = totalSeconds / 3600
        formatBuilder.setLength(0)
        return if (hours > 0)
            formatter.format("%d:%02d:%02d", hours, minutes, seconds).toString()
        else
            formatter.format("%02d:%02d", minutes, seconds).toString()
    }

    fun setPlayer(player: Player) {
        this.player = player
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.exo_play -> if (player != null) {
                player!!.play()
            }
            R.id.exo_pause -> if (player != null) {
                player!!.pause()
            }
        }
    }

    fun release() {
        removeCallbacks(updateProgressAction)
    }

    fun resume() {
        updateProgress()
    }
}
