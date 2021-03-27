package aculix.channelify.app.fastadapteritems

import aculix.channelify.app.R
import aculix.channelify.app.model.Comment
import aculix.channelify.app.utils.DateTimeUtils
import aculix.core.extensions.makeGone
import aculix.core.extensions.makeVisible
import android.view.View
import coil.api.load
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem
import de.hdodenhof.circleimageview.CircleImageView

class CommentItem(val comment: Comment.Item?) : AbstractItem<CommentItem.CommentViewHolder>() {

    override val layoutRes: Int
        get() = R.layout.item_comment

    override val type: Int
        get() = R.id.fastadapter_comment_item_id

    override fun getViewHolder(v: View): CommentViewHolder {
        return CommentViewHolder(v)
    }


    class CommentViewHolder(private var view: View) : FastAdapter.ViewHolder<CommentItem>(view) {

        private val profilePhoto: CircleImageView = view.findViewById(R.id.ivProfileCommentItem)
        private val commenterNameTime: MaterialTextView =
            view.findViewById(R.id.tvNameTimeCommentItem)
        private val commentContent: MaterialTextView = view.findViewById(R.id.tvContentCommentItem)
        private val likeCount: MaterialTextView = view.findViewById(R.id.tvLikeCountCommentItem)
        private val repliesCount: MaterialTextView =
            view.findViewById(R.id.tvCommentCountCommentItem)
        private val viewReplies: MaterialButton = view.findViewById(R.id.btnViewRepliesCommentItem)

        override fun bindView(item: CommentItem, payloads: List<Any>) {
            item.comment?.snippet?.let {
                // Profile photo
                profilePhoto.load(it.topLevelComment.snippet.authorProfileImageUrl)

                // Commenter name and published time
                if(it.topLevelComment.snippet.publishedAt == it.topLevelComment.snippet.updatedAt) {
                    // Comment not edited
                    commenterNameTime.text = view.context.getString(
                        R.string.text_commenter_name_time,
                        it.topLevelComment.snippet.authorDisplayName,
                        DateTimeUtils.getTimeAgo(it.topLevelComment.snippet.publishedAt)
                    )
                } else {
                    // Edited comment
                    commenterNameTime.text = view.context.getString(
                        R.string.text_commenter_name_time_edited,
                        it.topLevelComment.snippet.authorDisplayName,
                        DateTimeUtils.getTimeAgo(it.topLevelComment.snippet.updatedAt)
                    )
                }


                // Comments count
                commentContent.text = it.topLevelComment.snippet.textOriginal

                // Comments Like count
                likeCount.text = it.topLevelComment.snippet.likeCount.toString()

                // Comments Reply count
                repliesCount.text = it.totalReplyCount.toString()

                // View Replies button
                if (it.totalReplyCount > 0) {
                    viewReplies.makeVisible()
                    viewReplies.text = view.context.resources.getQuantityString(R.plurals.btn_view_replies, it.totalReplyCount, it.totalReplyCount)
                } else {
                    viewReplies.makeGone()
                }
            }
        }

        override fun unbindView(item: CommentItem) {
            profilePhoto.setImageDrawable(null)
            commenterNameTime.text = null
            commentContent.text = null
            likeCount.text = null
            repliesCount.text = null
        }
    }
}