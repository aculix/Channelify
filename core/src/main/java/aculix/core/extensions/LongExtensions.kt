package aculix.core.extensions

import android.annotation.SuppressLint
import kotlin.math.ln
import kotlin.math.pow

/**
 * Formats the number in thousands, millions, billions etc.
 */
@SuppressLint("DefaultLocale")
fun Long.getFormattedNumberInString(): String {
    if (this < 1000) return this.toString()
    val exp = (ln(this.toDouble()) / ln(1000.0)).toInt()
    return java.lang.String.format(
        "%.1f %c",
        this / 1000.0.pow(exp.toDouble()),
        "KMBTPE"[exp - 1]
    )
}