package aculix.core.extensions

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat

/**
 * Converts a Vector drawable to a Bitmap
 *
 * @param tintColor Tint color to be applied to the Vector drawable
 *
 *@return Converted Bitmap
 */
fun Int.toBitmap(context: Context, @ColorRes tintColor: Int? = null): Bitmap? {

    // retrieve the actual drawable
    val drawable = ContextCompat.getDrawable(context, this) ?: return null
    drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
    val bm = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)

    // add the tint if it exists
    tintColor?.let {
        DrawableCompat.setTint(drawable, ContextCompat.getColor(context, it))
    }
    // draw it onto the bitmap
    val canvas = Canvas(bm)
    drawable.draw(canvas)
    return bm
}