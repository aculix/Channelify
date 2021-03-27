package aculix.channelify.app.activity

import aculix.channelify.app.R
import aculix.channelify.app.fragment.VideoDetailsFragment
import aculix.channelify.app.utils.FullScreenHelper
import aculix.channelify.app.viewmodel.VideoPlayerViewModel
import aculix.core.extensions.toast
import android.annotation.SuppressLint
import android.app.PictureInPictureParams
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Rational
import android.view.ContextThemeWrapper
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatToggleButton
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerFullScreenListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.loadOrCueVideo
import kotlinx.android.synthetic.main.activity_video_player.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class VideoPlayerActivity : AppCompatActivity(R.layout.activity_video_player) {

    companion object {
        const val VIDEO_ID = "video_id"

        fun startActivity(context: Context?, videoId: String) {
            val intent = Intent(context, VideoPlayerActivity::class.java).apply {
                putExtra(VIDEO_ID, videoId)
            }
            context?.startActivity(intent)
        }
    }

    private val viewModel by viewModel<VideoPlayerViewModel>() // Lazy inject ViewModel

    lateinit var fullScreenHelper: FullScreenHelper
    lateinit var videoId: String
    private var videoElapsedTimeInSeconds = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fullScreenHelper = FullScreenHelper(this)
        videoId = intent.getStringExtra(VIDEO_ID)!!

        // Passing the videoId as argument to the start destination
        findNavController(R.id.navHostVideoPlayer).setGraph(
            R.navigation.video_player_graph,
            bundleOf(VideoDetailsFragment.VIDEO_ID to videoId)
        )

        initYouTubePlayer()
    }

    override fun onBackPressed() {
        if (ytVideoPlayerView.isFullScreen()) ytVideoPlayerView.exitFullScreen() else super.onBackPressed()
    }

    private fun initYouTubePlayer() {
        lifecycle.addObserver(ytVideoPlayerView)

        ytVideoPlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                youTubePlayer.loadOrCueVideo(lifecycle, videoId, 0f)
                addFullScreenListenerToPlayer()
                setupCustomActions(youTubePlayer)
            }

            override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
                videoElapsedTimeInSeconds = second
            }
        })


    }

    /**
     * Adds the forward and rewind action button to the Player
     */
    private fun setupCustomActions(youTubePlayer: YouTubePlayer) {
        ytVideoPlayerView.getPlayerUiController()
            .setCustomAction1(
                ContextCompat.getDrawable(this, R.drawable.ic_rewind)!!,
                View.OnClickListener {
                    videoElapsedTimeInSeconds -= 10
                    youTubePlayer.seekTo(videoElapsedTimeInSeconds)
                })
            .setCustomAction2(
                ContextCompat.getDrawable(this, R.drawable.ic_forward)!!,
                View.OnClickListener {
                    videoElapsedTimeInSeconds += 10
                    youTubePlayer.seekTo(videoElapsedTimeInSeconds)
                })
    }

    /**
     * Changes the orientation of the activity based on the
     * change of the player state (Full screen or not)
     */
    @SuppressLint("SourceLockedOrientationActivity")
    private fun addFullScreenListenerToPlayer() {
        ytVideoPlayerView.addFullScreenListener(object : YouTubePlayerFullScreenListener {
            override fun onYouTubePlayerEnterFullScreen() {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                fullScreenHelper.enterFullScreen()
            }

            override fun onYouTubePlayerExitFullScreen() {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                fullScreenHelper.exitFullScreen()
            }
        })
    }
}
