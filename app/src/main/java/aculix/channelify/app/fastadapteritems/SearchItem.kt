package aculix.channelify.app.fastadapteritems

import aculix.channelify.app.R
import aculix.channelify.app.model.SearchedVideo
import aculix.channelify.app.utils.DateTimeUtils
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import coil.api.load
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem

class SearchItem(val searchedVideo: SearchedVideo.Item?) :
    AbstractItem<SearchItem.SearchViewHolder>() {

    override val layoutRes: Int
        get() = R.layout.item_search

    override val type: Int
        get() = R.id.fastadapter_search_item_id

    override fun getViewHolder(v: View): SearchViewHolder {
        return SearchViewHolder(v)
    }


    class SearchViewHolder(private var view: View) : FastAdapter.ViewHolder<SearchItem>(view) {

        private val thumbnail: AppCompatImageView = view.findViewById(R.id.ivThumbnailSearchItem)
        private val videoTitle: AppCompatTextView = view.findViewById(R.id.tvTitleSearchItem)
        private val videoPublishedAt: AppCompatTextView =
            view.findViewById(R.id.tvTimePublishedSearchItem)

        override fun bindView(item: SearchItem, payloads: List<Any>) {
            item.searchedVideo?.snippet?.let {
                thumbnail.load(it.thumbnails.standard?.url ?: it.thumbnails.high.url)
                videoTitle.text = it.title
                videoPublishedAt.text = DateTimeUtils.getTimeAgo(it.publishedAt)
            }
        }

        override fun unbindView(item: SearchItem) {
            thumbnail.setImageDrawable(null)
            videoTitle.text = null
            videoPublishedAt.text = null
        }
    }
}