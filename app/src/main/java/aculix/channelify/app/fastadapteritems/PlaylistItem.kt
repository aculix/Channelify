package aculix.channelify.app.fastadapteritems

import aculix.channelify.app.R
import aculix.channelify.app.model.Playlist
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import coil.api.load
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem

class PlaylistItem(val playlistItem: Playlist.Item?) :
    AbstractItem<PlaylistItem.PlaylistViewHolder>() {

    override val layoutRes: Int
        get() = R.layout.item_playlist

    override val type: Int
        get() = R.id.fastadapter_playlist_item_id

    override fun getViewHolder(v: View): PlaylistViewHolder {
        return PlaylistViewHolder(v)
    }


    class PlaylistViewHolder(private var view: View) : FastAdapter.ViewHolder<PlaylistItem>(view) {

        private val thumbnail: AppCompatImageView = view.findViewById(R.id.ivThumbnailPlaylistItem)
        private val playlistName: AppCompatTextView = view.findViewById(R.id.tvNamePlaylistItem)
        private val videoCount: AppCompatTextView = view.findViewById(R.id.tvVideoCountPlaylistItem)

        override fun bindView(item: PlaylistItem, payloads: List<Any>) {
            thumbnail.load(
                item.playlistItem?.snippet?.thumbnails?.standard?.url
                    ?: item.playlistItem?.snippet?.thumbnails?.high?.url
            )
            playlistName.text = item.playlistItem?.snippet?.title
            videoCount.text = view.context.resources.getQuantityString(R.plurals.text_playlist_video_count, item.playlistItem?.contentDetails?.itemCount!!, item.playlistItem.contentDetails.itemCount)
        }

        override fun unbindView(item: PlaylistItem) {
            thumbnail.setImageDrawable(null)
            playlistName.text = null
            videoCount.text = null
        }
    }
}