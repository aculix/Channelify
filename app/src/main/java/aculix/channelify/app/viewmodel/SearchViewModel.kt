package aculix.channelify.app.viewmodel

import aculix.channelify.app.model.SearchedVideo
import aculix.channelify.app.paging.NetworkState
import aculix.channelify.app.paging.datasourcefactory.SearchDataSourceFactory
import aculix.channelify.app.repository.SearchRepository
import aculix.channelify.app.utils.Constants
import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import kotlinx.coroutines.launch
import timber.log.Timber

class SearchViewModel(
    private val searchRepository: SearchRepository,
    private val channelId: String,
    private val emptySearchResultText: String
) : ViewModel() {

    lateinit var searchDataSourceFactory: SearchDataSourceFactory
    var searchResultLiveData: LiveData<PagedList<SearchedVideo.Item>>? = null
    var networkStateLiveData: LiveData<NetworkState>? = null
    private var _emptyStateLiveData = MutableLiveData<Boolean>()
    val emptyStateLiveData: LiveData<Boolean>
        get() = _emptyStateLiveData

    fun searchVideos(searchQuery: String) {
        if (searchResultLiveData == null) {
            viewModelScope.launch {
                searchDataSourceFactory =
                    SearchDataSourceFactory(
                        searchRepository,
                        viewModelScope,
                        channelId,
                        searchQuery,
                        emptySearchResultText
                    )

                searchResultLiveData =
                    LivePagedListBuilder(searchDataSourceFactory, pagedListConfig())
                        .setBoundaryCallback(object :
                            PagedList.BoundaryCallback<SearchedVideo.Item>() {
                            override fun onZeroItemsLoaded() {
                                super.onZeroItemsLoaded()
                                _emptyStateLiveData.value = true
                            }

                            override fun onItemAtFrontLoaded(itemAtFront: SearchedVideo.Item) {
                                super.onItemAtFrontLoaded(itemAtFront)
                                _emptyStateLiveData.value = false
                            }

                            override fun onItemAtEndLoaded(itemAtEnd: SearchedVideo.Item) {
                                super.onItemAtEndLoaded(itemAtEnd)
                                _emptyStateLiveData.value = false
                            }
                        }).build()
                networkStateLiveData =
                    Transformations.switchMap(searchDataSourceFactory.searchDataSourceLiveData) { it.getNetworkState() }
            }
        }
    }

    fun setSearchQuery(updatedSearchQuery: String) {
        searchDataSourceFactory.setSearchQuery(updatedSearchQuery)
        searchDataSourceFactory.searchDataSourceLiveData.value?.invalidate()
    }

    /**
     * Retry possible last paged request (ie: network issue)
     */
    fun refreshFailedRequest() =
        searchDataSourceFactory.getSource()?.retryFailedQuery()

    private fun pagedListConfig() = PagedList.Config.Builder()
        .setInitialLoadSizeHint(Constants.INITIAL_PAGE_LOAD_SIZE)
        .setEnablePlaceholders(false)
        .setPageSize(Constants.PAGE_SIZE)
        .build()
}
