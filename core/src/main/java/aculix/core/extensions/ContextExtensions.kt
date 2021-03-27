package aculix.core.extensions


import aculix.core.R
import android.app.Activity
import android.app.DownloadManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.DisplayMetrics
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import saschpe.android.customtabs.CustomTabsHelper
import saschpe.android.customtabs.WebViewFallback


/**
 * Relaunches the current activity
 */
fun Context.relaunchActivity(context: Activity, intent: Intent) {
    context.finish()
    startActivity(intent)
}

/**
 * Shows the toast message
 */
fun Context.toast(message: String, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, length).show()
}

/**
 * Opens the passed URL in the Chrome Custom Tabs
 */
fun Context.openUrl(url: String, @ColorRes toolbarColor: Int) {
    val customTabsIntent = CustomTabsIntent.Builder()
        .addDefaultShareMenuItem()
        .setToolbarColor(ContextCompat.getColor(this, toolbarColor))
        .setShowTitle(true)
        .build()

    // This is optional but recommended
    CustomTabsHelper.addKeepAliveExtra(this, customTabsIntent.intent)

    CustomTabsHelper.openCustomTab(
        this,
        customTabsIntent,
        Uri.parse(url),
        WebViewFallback() // Opens in system browser if Chrome isn't installed on device
    )
}

fun Context.openAppInGooglePlay(appPackageName: String) {
        try {
        // Try to open in the Google Play app
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
    } catch (exception: android.content.ActivityNotFoundException) {
        // Google Play app is not installed. Open URL in the browser.
        openUrl("https://play.google.com/store/apps/details?id=$appPackageName", R.color.customTabToolbarColor)
    }
}

fun Context.copyTextToClipboard(textToCopy: String, textCopiedMessage: String) {
    val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clipData = ClipData.newPlainText("Copied Text", textToCopy)
    clipboardManager.setPrimaryClip(clipData)
    toast(textCopiedMessage)
}

@Suppress("DEPRECATION")
fun Context.isInternetAvailable(): Boolean {
    var result = false

    val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        cm.run {
            cm.getNetworkCapabilities(cm.activeNetwork)?.run {
                result = when {
                    hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    else -> false
                }
            }
        }
    } else {
        cm.run {
            cm.activeNetworkInfo?.run {
                if (type == ConnectivityManager.TYPE_WIFI) {
                    result = true
                } else if (type == ConnectivityManager.TYPE_MOBILE) {
                    result = true
                }
            }
        }
    }

    return result
}

/**
 * Starts an Intent for sharing text
 */
fun Context.startShareTextIntent(shareTitle: String, shareText: String) {
    val shareIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, shareText)
        type = "text/plain"
    }

    startActivity(Intent.createChooser(shareIntent, shareTitle))
}

fun Context.startEmailIntent(toEmail: String, emailSubject: String?) {
    // Open Email app with subject and to field pre-filled
    val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:$toEmail") // only email apps should handle this
        putExtra(Intent.EXTRA_SUBJECT, emailSubject) // Email subject
    }

    // Check if an Email app is installed on the device
    if (emailIntent.resolveActivity(packageManager) != null) {
        startActivity(emailIntent)
    } else {
        toast(getString(R.string.text_no_email_app_found))
    }
}
