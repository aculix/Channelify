package aculix.channelify.app.activity

import aculix.channelify.app.Channelify
import aculix.channelify.app.R
import aculix.channelify.app.utils.getAdaptiveBannerAdSize
import aculix.core.extensions.makeGone
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.ads.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var adView: AdView
    private var initialLayoutComplete = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val navController = findNavController(R.id.navHostFragment)
        bottomNavView.setupWithNavController(navController)

        if (Channelify.isAdEnabled) setupAd() else adViewContainerMain.makeGone()

    }

    override fun onPause() {
        if (Channelify.isAdEnabled) adView.pause()
        super.onPause()
    }

    override fun onResume() {
        if (Channelify.isAdEnabled) adView.resume()
        super.onResume()
    }

    override fun onDestroy() {
        if (Channelify.isAdEnabled) adView.destroy()
        super.onDestroy()
    }

    private fun setupAd() {
        adView = AdView(this)
        adViewContainerMain.addView(adView)
        adViewContainerMain.viewTreeObserver.addOnGlobalLayoutListener {
            if (!initialLayoutComplete) {
                initialLayoutComplete = true

                adView.adUnitId = getString(R.string.main_banner_ad_id)
                adView.adSize = getAdaptiveBannerAdSize(adViewContainerMain)
                adView.loadAd(AdRequest.Builder().build())
            }
        }
    }
}
