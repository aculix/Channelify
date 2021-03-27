package aculix.channelify.app.viewmodel

import aculix.channelify.app.model.PlaylistItemInfo
import aculix.channelify.app.paging.NetworkState
import aculix.channelify.app.paging.datasourcefactory.PlaylistVideosDataSourceFactory
import aculix.channelify.app.repository.PlaylistVideosRepository
import aculix.channelify.app.utils.Constants
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import kotlinx.coroutines.launch

class PlaylistVideosViewModel(
    private val playlistVideosRepository: PlaylistVideosRepository
) : ViewModel() {

    lateinit var playlistVideosDataSourceFactory: PlaylistVideosDataSourceFactory
    var playlistVideosLiveData: LiveData<PagedList<PlaylistItemInfo.Item>>? = null
    var networkStateLiveData: LiveData<NetworkState>? = null

    fun getPlaylistVideos(playlistId: String) {
        if (playlistVideosLiveData == null) {
            viewModelScope.launch {
                playlistVideosDataSourceFactory =
                    PlaylistVideosDataSourceFactory(
                        playlistVideosRepository,
                        viewModelScope,
                        playlistId
                    )

                playlistVideosLiveData =
                    LivePagedListBuilder(playlistVideosDataSourceFactory, pagedListConfig()).build()
                networkStateLiveData =
                    Transformations.switchMap(playlistVideosDataSourceFactory.playlistVideosDataSourceLiveData) { it.getNetworkState() }
            }
        }

    }

    /**
     * Retry possible last paged request (ie: network issue)
     */
    fun refreshFailedRequest() =
        playlistVideosDataSourceFactory.getSource()?.retryFailedQuery()

    private fun pagedListConfig() = PagedList.Config.Builder()
        .setInitialLoadSizeHint(Constants.INITIAL_PAGE_LOAD_SIZE)
        .setEnablePlaceholders(false)
        .setPageSize(Constants.PAGE_SIZE)
        .build()
}
