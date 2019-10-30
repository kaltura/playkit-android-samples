package com.kaltura.playkit.samples.vttthumbnailsample

import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.kaltura.playkit.*

class MainActivity : AppCompatActivity() {

    private val startPosition : Long = 0L
    private val mediaFormat : PKMediaFormat = PKMediaFormat.hls
    private val sourceUrl : String = "https://cdnapisec.kaltura.com/p/2215841/sp/221584100/playManifest/entryId/1_w9zx2eti/protocol/https/format/applehttp/falvorIds/1_1obpcggb,1_yyuvftfz,1_1xdbzoa6,1_k16ccgto,1_djdf6bk8/a.m3u8"
    private val licenseUrl : String? = null

    private var player : Player? = null
    private var controlsView : PlaybackControlsView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Initialize media config object.
        val mediaConfig = createMediaConfig()

        //Create instance of the player without plugins.
        player = PlayKitManager.loadPlayer(this, null)

        //Add player to the view hierarchy.
        addPlayerToView()

        //Prepare player with media configuration.
        player?.prepare(mediaConfig)

        //Start playback.
        player?.play()

    }

    /**
     * Will create [] object.
     */
    private fun createMediaConfig(): PKMediaConfig {
        //First. Create PKMediaConfig object.
        val mediaConfig = PKMediaConfig()

        //Set start position of the media. This will
        //automatically start playback from specified position.
        mediaConfig.startPosition = startPosition

        //Second. Create PKMediaEntry object.
        val mediaEntry = createMediaEntry()

        //Add it to the mediaConfig.
        mediaConfig.mediaEntry = mediaEntry

        return mediaConfig
    }

    /**
     * Create [PKMediaEntry] with minimum necessary data.
     *
     * @return - the [PKMediaEntry] object.
     */
    private fun createMediaEntry(): PKMediaEntry {
        //Create media entry.
        val mediaEntry = PKMediaEntry()

        //Set id for the entry.
        mediaEntry.id = "testEntry"

        //Set media entry type. It could be Live,Vod or Unknown.
        //In this sample we use Vod.
        mediaEntry.mediaType = PKMediaEntry.MediaEntryType.Vod

        //Create list that contains at least 1 media source.
        //Each media entry can contain a couple of different media sources.
        //All of them represent the same content, the difference is in it format.
        //For example same entry can contain PKMediaSource with dash and another
        // PKMediaSource can be with hls. The player will decide by itself which source is
        // preferred for playback.
        val mediaSources = createMediaSources()

        //Set media sources to the entry.
        mediaEntry.sources = mediaSources

        return mediaEntry
    }

    /**
     * Create list of [PKMediaSource].
     *
     * @return - the list of sources.
     */
    private fun createMediaSources(): List<PKMediaSource> {

        //Create new PKMediaSource instance.
        val mediaSource = PKMediaSource()

        //Set the id.
        mediaSource.id = "testSource"

        //Set the content url. In our case it will be link to hls source(.m3u8).
        mediaSource.url = sourceUrl

        //Set the format of the source. In our case it will be hls in case of mpd/wvm formats you have to to call mediaSource.setDrmData method as well
        mediaSource.mediaFormat = mediaFormat

        // Add DRM data if required
        if (licenseUrl != null) {
            mediaSource.drmData = listOf(PKDrmParams(licenseUrl, PKDrmParams.Scheme.WidevineCENC))
        }

        return listOf(mediaSource)
    }

    /**
     * Will add player to the view.
     */
    private fun addPlayerToView() {
        //Get the layout, where the player view will be placed.
        val layout = findViewById<LinearLayout>(R.id.player_root)
        //Add player view to the layout.
        layout.addView(player?.view)

        controlsView = findViewById(R.id.playerControls)
        controlsView?.setPlayer(player!!)
    }

    override fun onResume() {
        super.onResume()
        if (player != null) {
            player?.onApplicationResumed()
            player?.play()
        }
        if (controlsView != null) {
            controlsView?.resume()
        }
    }

    override fun onPause() {
        super.onPause()
        if (controlsView != null) {
            controlsView?.release()
        }
        if (player != null) {
            player?.onApplicationPaused()
        }
    }
}