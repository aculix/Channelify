package aculix.channelify.app.paging.datasourcefactory

import aculix.channelify.app.model.Comment
import aculix.channelify.app.paging.datasource.CommentsDataSource
import aculix.channelify.app.repository.CommentsRepository
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import kotlinx.coroutines.CoroutineScope
import timber.log.Timber

class CommentsDataSourceFactory(
    private val commentsRepository: CommentsRepository,
    private val coroutineScope: CoroutineScope,
    private val videoId: String,
    private var sortOrder: String
) : DataSource.Factory<String, Comment.Item>() {

    private var commentsDataSource: CommentsDataSource? = null
    val commentsDataSourceLiveData = MutableLiveData<CommentsDataSource>()

    override fun create(): DataSource<String, Comment.Item> {
    // Also called every time when invalidate() is executed
            commentsDataSource =
                CommentsDataSource(
                    commentsRepository,
                    coroutineScope,
                    videoId,
                    sortOrder
                )
            commentsDataSourceLiveData.postValue(commentsDataSource)

        return commentsDataSource!!
    }

    fun setSortOrder(updatedSortOrder: String) {
        sortOrder = updatedSortOrder
    }

    fun getSource() = commentsDataSourceLiveData.value
}