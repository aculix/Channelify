package aculix.channelify.app.viewmodel

import aculix.channelify.app.R
import aculix.channelify.app.model.PlaylistItemInfo
import aculix.channelify.app.paging.datasourcefactory.HomeDataSourceFactory
import aculix.channelify.app.paging.NetworkState
import aculix.channelify.app.repository.HomeRepository
import aculix.channelify.app.utils.Constants
import aculix.core.helper.ResultWrapper
import android.content.Context
import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import timber.log.Timber

class HomeViewModel(
    private val homeRepository: HomeRepository,
    private val channelId: String,
    private val context: Context
) : ViewModel() {

    private val _uploadsPlaylistIdLiveData = MutableLiveData<ResultWrapper>()
    val uploadsPlaylistIdLiveData: LiveData<ResultWrapper>
        get() = _uploadsPlaylistIdLiveData

    lateinit var homeDataSourceFactory: HomeDataSourceFactory
    var latestVideoLiveData: LiveData<PagedList<PlaylistItemInfo.Item>>? = null
    var networkStateLiveData: LiveData<NetworkState>? = null

    fun getLatestVideos() {
        if (latestVideoLiveData == null) {
            viewModelScope.launch {
                _uploadsPlaylistIdLiveData.value = ResultWrapper.Loading

                val uploadsPlaylistIdRequest = async(Dispatchers.IO) { homeRepository.getUploadsPlaylistId(channelId) }
                val response = uploadsPlaylistIdRequest.await()

                if (response.isSuccessful) {
                    val playlistId = response.body()!!.items[0].contentDetails.relatedPlaylists.uploads
                    homeDataSourceFactory =
                        HomeDataSourceFactory(
                            homeRepository,
                            viewModelScope,
                            playlistId
                        )

                    latestVideoLiveData = LivePagedListBuilder(homeDataSourceFactory, pagedListConfig()).build()
                    networkStateLiveData = Transformations.switchMap(homeDataSourceFactory.homeDataSourceLiveData) { it.getNetworkState() }
                    _uploadsPlaylistIdLiveData.value = ResultWrapper.Success("")
                } else {
                    Timber.e("Error: ${response.raw()}")
                    _uploadsPlaylistIdLiveData.value = ResultWrapper.Error(context.getString(R.string.error_fetch_videos))
                }
            }
        }

    }

    /**
     * Retry possible last paged request (ie: network issue)
     */
    fun refreshFailedRequest() =
        homeDataSourceFactory.getSource()?.retryFailedQuery()

    private fun pagedListConfig() = PagedList.Config.Builder()
        .setInitialLoadSizeHint(Constants.INITIAL_PAGE_LOAD_SIZE)
        .setEnablePlaceholders(false)
        .setPageSize(Constants.PAGE_SIZE)
        .build()
}
