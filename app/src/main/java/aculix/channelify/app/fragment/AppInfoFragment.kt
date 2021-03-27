package aculix.channelify.app.fragment

import aculix.channelify.app.BuildConfig
import aculix.channelify.app.R
import aculix.core.extensions.openAppInGooglePlay
import aculix.core.extensions.openUrl
import aculix.core.extensions.startEmailIntent
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import coil.api.load
import kotlinx.android.synthetic.main.fragment_app_info.*

class AppInfoFragment : Fragment(R.layout.fragment_app_info) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        ivLogoAppInfo.load(R.drawable.logo_splash)

        onWebsiteClick()
        onGooglePlayClick()
        onInstagramClick()
        onEmailClick()
        onNavigationViewMenuItemClick()
    }

    private fun setupToolbar() {
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        toolbarAppInfo.setupWithNavController(navController, appBarConfiguration)
    }

    private fun onWebsiteClick() {
        ivWebsiteAppInfo.setOnClickListener {
            context?.openUrl(getString(R.string.text_website_url), R.color.defaultBgColor)
        }
    }

    private fun onGooglePlayClick() {
        ivGooglePlayAppInfo.setOnClickListener {
            try {
                // Try to open in the Google Play app
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://search?q=pub:${getString(R.string.text_google_play_developer_name)}")
                    )
                )
            } catch (exception: Throwable) {
                // Google Play app is not installed. Open URL in the browser.
                context?.openUrl(
                    "https://play.google.com/store/apps/dev?id=${getString(R.string.text_google_play_developer_id)}",
                    R.color.defaultBgColor
                )
            }
        }
    }

    private fun onInstagramClick() {
        ivInstagramAppInfo.setOnClickListener {
            try {
                // Try to open in the Instagram app
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://instagram.com/_u/${getString(R.string.text_instagram_user_name)}")
                    )
                )
            } catch (exception: android.content.ActivityNotFoundException) {
                // Instagram app is not installed. Open URL in the browser.
                context?.openUrl(
                    "https://instagram.com/${getString(R.string.text_instagram_user_name)}",
                    R.color.defaultBgColor
                )
            }
        }
    }

    private fun onEmailClick() {
        ivEmailAppInfo.setOnClickListener {
            context?.startEmailIntent(getString(R.string.text_contact_email), null)
        }
    }

    private fun onNavigationViewMenuItemClick() {
        nvAppInfo.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.miRateAppInfo -> {
                    context?.openAppInGooglePlay(BuildConfig.APPLICATION_ID)
                }
                R.id.miFeedbackAppInfo -> {
                    context?.startEmailIntent(
                        getString(R.string.text_contact_email),
                        getString(R.string.text_app_feedback_email_subject)
                    )
                }
                R.id.miTosAppInfo -> {
                    context?.openUrl(getString(R.string.text_tos_url), R.color.defaultBgColor)
                }
                R.id.miPrivacyPolicyAppInfo -> {
                    context?.openUrl(
                        getString(R.string.text_privacy_policy_url),
                        R.color.defaultBgColor
                    )
                }
            }

            true
        }
    }
}
