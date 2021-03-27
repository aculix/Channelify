package aculix.channelify.app.utils

import android.util.DisplayMetrics
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.ads.AdSize

/**
 * Returns the size of the Adaptive Banner Ad based on the screen width
 */
fun FragmentActivity.getAdaptiveBannerAdSize(adViewContainer: FrameLayout): AdSize {
    val display = windowManager.defaultDisplay
    val outMetrics = DisplayMetrics()
    display.getMetrics(outMetrics)

    val density = outMetrics.density

    var adWidthPixels = adViewContainer.width.toFloat()
    if (adWidthPixels == 0f) {
        adWidthPixels = outMetrics.widthPixels.toFloat()
    }

    val adWidth = (adWidthPixels / density).toInt()
    return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth)
}