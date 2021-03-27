package aculix.channelify.app.viewmodel

import aculix.channelify.app.model.Playlist
import aculix.channelify.app.paging.NetworkState
import aculix.channelify.app.paging.datasourcefactory.PlaylistsDataSourceFactory
import aculix.channelify.app.repository.PlaylistsRepository
import aculix.channelify.app.utils.Constants
import android.content.Context
import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    private val playlistsRepository: PlaylistsRepository,
    private val channelId: String
) : ViewModel() {

    lateinit var playlistsDataSourceFactory: PlaylistsDataSourceFactory
    var playlistsLiveData: LiveData<PagedList<Playlist.Item>>? = null
    var networkStateLiveData: LiveData<NetworkState>? = null
    private var _emptyStateLiveData = MutableLiveData<Boolean>()
    val emptyStateLiveData: LiveData<Boolean>
        get() = _emptyStateLiveData

    fun getPlaylists() {
        if (playlistsLiveData == null) {
            viewModelScope.launch {
                playlistsDataSourceFactory =
                    PlaylistsDataSourceFactory(
                        playlistsRepository,
                        viewModelScope,
                        channelId
                    )

                playlistsLiveData = LivePagedListBuilder(playlistsDataSourceFactory, pagedListConfig())
                    .setBoundaryCallback(object :
                        PagedList.BoundaryCallback<Playlist.Item>() {
                        override fun onZeroItemsLoaded() {
                            super.onZeroItemsLoaded()
                            _emptyStateLiveData.value = true
                        }

                        override fun onItemAtFrontLoaded(itemAtFront: Playlist.Item) {
                            super.onItemAtFrontLoaded(itemAtFront)
                            _emptyStateLiveData.value = false
                        }

                        override fun onItemAtEndLoaded(itemAtEnd: Playlist.Item) {
                            super.onItemAtEndLoaded(itemAtEnd)
                            _emptyStateLiveData.value = false
                        }
                    })
                    .build()
                networkStateLiveData = Transformations.switchMap(playlistsDataSourceFactory.playlistsDataSourceLiveData) { it.getNetworkState() }
            }
        }

    }

    /**
     * Retry possible last paged request (ie: network issue)
     */
    fun refreshFailedRequest() =
        playlistsDataSourceFactory.getSource()?.retryFailedQuery()

    private fun pagedListConfig() = PagedList.Config.Builder()
        .setInitialLoadSizeHint(Constants.INITIAL_PAGE_LOAD_SIZE)
        .setEnablePlaceholders(false)
        .setPageSize(Constants.PAGE_SIZE)
        .build()
}
