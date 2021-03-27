package aculix.channelify.app.paging.datasource

import aculix.channelify.app.model.SearchedVideo
import aculix.channelify.app.paging.NetworkState
import aculix.channelify.app.repository.SearchRepository
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import kotlinx.coroutines.*
import timber.log.Timber

class SearchDataSource(
    private val searchRepository: SearchRepository,
    private val coroutineScope: CoroutineScope,
    private val channelId: String,
    private var searchQuery: String,
    private val emptySearchResultText: String
) : PageKeyedDataSource<String, SearchedVideo.Item>() {

    private var supervisorJob = SupervisorJob()
    private val networkState = MutableLiveData<NetworkState>()
    private var retryQuery: (() -> Any)? =
        null // Keep reference of the last query (to be able to retry it if necessary)
    private var nextPageToken: String? = null

    override fun loadInitial(
        params: LoadInitialParams<String>,
        callback: LoadInitialCallback<String, SearchedVideo.Item>
    ) {
        retryQuery = { loadInitial(params, callback) }
        executeQuery {
            callback.onResult(it, null, nextPageToken)
        }
    }

    override fun loadAfter(
        params: LoadParams<String>,
        callback: LoadCallback<String, SearchedVideo.Item>
    ) {
        retryQuery = { loadAfter(params, callback) }
        executeQuery {
            callback.onResult(it, nextPageToken)
        }
    }

    override fun loadBefore(
        params: LoadParams<String>,
        callback: LoadCallback<String, SearchedVideo.Item>
    ) {
        // Data is always fetched from the next page and hence loadBefore is never needed
    }

    private fun executeQuery(callback: (List<SearchedVideo.Item>) -> Unit) {
        networkState.postValue(NetworkState.LOADING)
        coroutineScope.launch(getJobErrorHandler() + supervisorJob) {
            val searchVideoResult =
                searchRepository.searchVideos(searchQuery, channelId, nextPageToken).body()
            nextPageToken = searchVideoResult?.nextPageToken
            val videoList = searchVideoResult?.items
            retryQuery = null
            networkState.postValue(NetworkState.LOADED)

            callback(videoList ?: emptyList())
        }
    }

    private fun getJobErrorHandler() = CoroutineExceptionHandler { _, e ->
        Timber.e("An error happened: $e")
        networkState.postValue(
            NetworkState.error(
                e.localizedMessage
            )
        )
    }

    override fun invalidate() {
        super.invalidate()
        supervisorJob.cancelChildren()   // Cancel possible running job to only keep last result searched by user
    }

    fun getNetworkState(): LiveData<NetworkState> = networkState

    fun refresh() = this.invalidate()

    fun retryFailedQuery() {
        val prevQuery = retryQuery
        retryQuery = null
        prevQuery?.invoke()
    }
}