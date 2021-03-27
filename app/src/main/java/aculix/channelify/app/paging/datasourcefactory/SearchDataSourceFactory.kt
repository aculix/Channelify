package aculix.channelify.app.paging.datasourcefactory

import aculix.channelify.app.model.SearchedVideo
import aculix.channelify.app.paging.datasource.SearchDataSource
import aculix.channelify.app.repository.SearchRepository
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import kotlinx.coroutines.CoroutineScope
import timber.log.Timber

class SearchDataSourceFactory(
    private val searchRepository: SearchRepository,
    private val coroutineScope: CoroutineScope,
    private val channelId: String,
    private var searchQuery: String,
    private val emptySearchResultText: String
) : DataSource.Factory<String, SearchedVideo.Item>() {

    private var searchDataSource: SearchDataSource? = null
    val searchDataSourceLiveData = MutableLiveData<SearchDataSource>()

    override fun create(): DataSource<String, SearchedVideo.Item> {
        // Also called every time when invalidate() is executed
        searchDataSource =
            SearchDataSource(
                searchRepository,
                coroutineScope,
                channelId,
                searchQuery,
                emptySearchResultText
            )
        searchDataSourceLiveData.postValue(searchDataSource)

        return searchDataSource!!
    }

    fun setSearchQuery(updatedSearchQuery: String) {
        searchQuery = updatedSearchQuery
    }

    fun getSource() = searchDataSourceLiveData.value
}