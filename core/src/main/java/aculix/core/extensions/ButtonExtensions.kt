package aculix.core.extensions

import android.widget.ProgressBar
import androidx.annotation.StringRes
import com.google.android.material.button.MaterialButton

/**
 * Button enabling/disabling modifiers
 */
fun MaterialButton.disableButton() {
    isEnabled = false
}

fun MaterialButton.enableButton() {
    isEnabled = true
}


/**
 * Disables the button and hides the text. Shows the progressbar inside
 * the button
 */
fun MaterialButton.showProgressBarInButton(progressBar: ProgressBar) {
    text = ""
    disableButton()
    progressBar.makeVisible()
}

/**
 * Enables the button and shows the button text. Hides the progressbar
 * from the button
 */
fun MaterialButton.hideProgressBarFromButton(progressBar: ProgressBar, @StringRes buttonText: Int) {
    progressBar.makeInvisible()
    text = resources.getString(buttonText)
    enableButton()
}