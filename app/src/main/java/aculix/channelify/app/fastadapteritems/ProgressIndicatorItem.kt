package aculix.channelify.app.fastadapteritems

import aculix.channelify.app.R
import android.view.View
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem

class ProgressIndicatorItem : AbstractItem<ProgressIndicatorItem.ProgressIndicatorViewHolder>() {

    override val type: Int
        get() = R.id.progress_indicator_item_id

    override val layoutRes: Int
        get() = R.layout.item_progress_indicator

    override fun getViewHolder(v: View): ProgressIndicatorViewHolder {
        return ProgressIndicatorViewHolder(v)
    }

    class ProgressIndicatorViewHolder(view: View) : FastAdapter.ViewHolder<ProgressIndicatorItem>(view) {

        override fun bindView(item: ProgressIndicatorItem, payloads: List<Any>) {
            // No data needs to be set as only ProgressBar is shown
        }

        override fun unbindView(item: ProgressIndicatorItem) {
            // No data set and hence no unbinding needed
        }
    }
}

