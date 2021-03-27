package aculix.channelify.app.paging.datasource

import aculix.channelify.app.model.PlaylistItemInfo
import aculix.channelify.app.paging.NetworkState
import aculix.channelify.app.repository.HomeRepository
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import kotlinx.coroutines.*
import timber.log.Timber

class HomeDataSource(
    private val homeRepository: HomeRepository,
    private val coroutineScope: CoroutineScope,
    private val playlistId: String
) : PageKeyedDataSource<String, PlaylistItemInfo.Item>() {

    private var supervisorJob = SupervisorJob()
    private val networkState = MutableLiveData<NetworkState>()
    private var retryQuery: (() -> Any)? = null // Keep reference of the last query (to be able to retry it if necessary)
    private var nextPageToken: String? = null

    override fun loadInitial(
        params: LoadInitialParams<String>,
        callback: LoadInitialCallback<String, PlaylistItemInfo.Item>
    ) {
        retryQuery = { loadInitial(params, callback) }
        executeQuery {
            callback.onResult(it, null, nextPageToken)
        }
    }

    override fun loadAfter(
        params: LoadParams<String>,
        callback: LoadCallback<String, PlaylistItemInfo.Item>
    ) {
        retryQuery = { loadAfter(params, callback) }
        executeQuery {
            callback.onResult(it, nextPageToken)
        }
    }

    override fun loadBefore(
        params: LoadParams<String>,
        callback: LoadCallback<String, PlaylistItemInfo.Item>
    ) {
        // Data is always fetched from the next page and hence loadBefore is never needed
    }

    private fun executeQuery(callback: (List<PlaylistItemInfo.Item>) -> Unit) {
        networkState.postValue(NetworkState.LOADING)
        coroutineScope.launch(getJobErrorHandler() + supervisorJob) {
            val playlistItemInfo = homeRepository.getLatestVideos(playlistId, nextPageToken).body()
            nextPageToken = playlistItemInfo?.nextPageToken
            val latestVideosList = playlistItemInfo?.items

            retryQuery = null
            networkState.postValue(NetworkState.LOADED)

            callback(latestVideosList ?: emptyList())
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