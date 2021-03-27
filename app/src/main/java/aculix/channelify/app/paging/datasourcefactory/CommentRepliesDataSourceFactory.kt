package aculix.channelify.app.paging.datasourcefactory

import aculix.channelify.app.model.CommentReply
import aculix.channelify.app.paging.datasource.CommentRepliesDataSource
import aculix.channelify.app.repository.CommentRepliesRepository
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import kotlinx.coroutines.CoroutineScope

class CommentRepliesDataSourceFactory(
    private val commentRepliesRepository: CommentRepliesRepository,
    private val coroutineScope: CoroutineScope,
    private val commentId: String
) : DataSource.Factory<String, CommentReply.Item>() {

    private var commentRepliesDataSource: CommentRepliesDataSource? = null
    val commentRepliesDataSourceLiveData = MutableLiveData<CommentRepliesDataSource>()

    override fun create(): DataSource<String, CommentReply.Item> {
        // Also called every time when invalidate() is executed
        commentRepliesDataSource =
            CommentRepliesDataSource(
                commentRepliesRepository,
                coroutineScope,
                commentId
            )
        commentRepliesDataSourceLiveData.postValue(commentRepliesDataSource)

        return commentRepliesDataSource!!
    }

    fun getSource() = commentRepliesDataSourceLiveData.value
}