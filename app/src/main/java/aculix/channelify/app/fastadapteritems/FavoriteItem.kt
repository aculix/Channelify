package aculix.channelify.app.fastadapteritems

import aculix.channelify.app.R
import aculix.channelify.app.model.FavoriteVideo
import aculix.core.extensions.to64BitHash
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import coil.api.load
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem

class FavoriteItem(val favoriteVideo: FavoriteVideo) :
    AbstractItem<FavoriteItem.FavoriteViewHolder>() {

    override val layoutRes: Int
        get() = R.layout.item_favorite

    override val type: Int
        get() = R.id.fastadapter_favorite_item_id

    override fun getViewHolder(v: View): FavoriteViewHolder {
        return FavoriteViewHolder(v)
    }


    class FavoriteViewHolder(private var view: View) : FastAdapter.ViewHolder<FavoriteItem>(view) {

        private val thumbnail: AppCompatImageView = view.findViewById(R.id.ivThumbnailFavoriteItem)
        private val favoriteIcon: AppCompatImageView = view.findViewById(R.id.ivHeartFavoriteItem)
        private val videoTitle: AppCompatTextView = view.findViewById(R.id.tvTitleFavoriteItem)

        override fun bindView(item: FavoriteItem, payloads: List<Any>) {
            thumbnail.load(item.favoriteVideo.thumbnail)
            videoTitle.text = item.favoriteVideo.title

            // Favorite Icon
            if (item.favoriteVideo.isChecked) favoriteIcon.setImageDrawable(
                ContextCompat.getDrawable(
                    view.context,
                    R.drawable.ic_favorite_filled_border
                )
            ) else favoriteIcon.setImageDrawable(
                ContextCompat.getDrawable(
                    view.context,
                    R.drawable.ic_favorite_border
                )
            )
        }

        override fun unbindView(item: FavoriteItem) {
            thumbnail.setImageDrawable(null)
            videoTitle.text = null
        }
    }
}