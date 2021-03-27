package aculix.channelify.app.fastadapteritems

import aculix.channelify.app.R
import aculix.channelify.app.model.CommentReply
import aculix.channelify.app.utils.DateTimeUtils
import android.view.View
import coil.api.load
import com.google.android.material.textview.MaterialTextView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem
import de.hdodenhof.circleimageview.CircleImageView

class CommentReplyItem(val commentReply: CommentReply.Item?) :
    AbstractItem<CommentReplyItem.CommentReplyViewHolder>() {

    override val layoutRes: Int
        get() = R.layout.item_comment_reply

    override val type: Int
        get() = R.id.fastadapter_comment_reply_item_id

    override fun getViewHolder(v: View): CommentReplyViewHolder {
        return CommentReplyViewHolder(v)
    }


    class CommentReplyViewHolder(private var view: View) :
        FastAdapter.ViewHolder<CommentReplyItem>(view) {

        private val profilePhoto: CircleImageView =
            view.findViewById(R.id.ivProfileCommentReplyItem)
        private val commenterNameTime: MaterialTextView =
            view.findViewById(R.id.tvNameTimeCommentReplyItem)
        private val commentContent: MaterialTextView =
            view.findViewById(R.id.tvContentCommentReplyItem)
        private val likeCount: MaterialTextView =
            view.findViewById(R.id.tvLikeCountCommentReplyItem)

        override fun bindView(item: CommentReplyItem, payloads: List<Any>) {
            item.commentReply?.snippet?.let {
                // Profile photo
                profilePhoto.load(it.authorProfileImageUrl)

                // Commenter name and published time
                if (it.publishedAt == it.updatedAt) {
                    // Comment not edited
                    commenterNameTime.text = view.context.getString(
                        R.string.text_commenter_name_time,
                        it.authorDisplayName,
                        DateTimeUtils.getTimeAgo(it.publishedAt)
                    )
                } else {
                    // Edited comment
                    commenterNameTime.text = view.context.getString(
                        R.string.text_commenter_name_time_edited,
                        it.authorDisplayName,
                        DateTimeUtils.getTimeAgo(it.updatedAt)
                    )
                }


                // Comment Content
                commentContent.text = it.textOriginal

                // Comments Like count
                likeCount.text = it.likeCount.toString()
            }
        }

        override fun unbindView(item: CommentReplyItem) {
            profilePhoto.setImageDrawable(null)
            commenterNameTime.text = null
            commentContent.text = null
            likeCount.text = null
        }
    }
}