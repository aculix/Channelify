package aculix.core.helper

import aculix.core.R
import androidx.recyclerview.widget.RecyclerView

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat


class SimpleDividerItemDecoration(context: Context, private val paddingLeft: Int = 16, private val paddingRight: Int = 16) : RecyclerView.ItemDecoration() {

    private val mDivider: Drawable? = ContextCompat.getDrawable(context, R.drawable.bg_line_divider)
    private val density = context.resources.displayMetrics.density

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val left = Math.round(density * paddingLeft)
        val right = parent.width - Math.round(density * paddingRight)

        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)

            val params = child.layoutParams as RecyclerView.LayoutParams

            val top = child.bottom + params.bottomMargin
            val bottom = top + mDivider!!.intrinsicHeight

            mDivider.setBounds(left, top, right, bottom)
            mDivider.draw(c)
        }
    }
}