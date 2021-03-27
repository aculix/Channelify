package aculix.channelify.app.paging.datasourcefactory

import aculix.channelify.app.model.PlaylistItemInfo
import aculix.channelify.app.paging.datasource.HomeDataSource
import aculix.channelify.app.repository.HomeRepository
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import kotlinx.coroutines.CoroutineScope

class HomeDataSourceFactory(
    private val homeRepository: HomeRepository,
    private val coroutineScope: CoroutineScope,
    private val playlistId: String
) : DataSource.Factory<String, PlaylistItemInfo.Item>() {

    private var homeDataSource: HomeDataSource? = null
    val homeDataSourceLiveData = MutableLiveData<HomeDataSource>()

    override fun create(): DataSource<String, PlaylistItemInfo.Item> {
        if (homeDataSource == null) {
            homeDataSource =
                HomeDataSource(
                    homeRepository,
                    coroutineScope,
                    playlistId
                )
            homeDataSourceLiveData.postValue(homeDataSource)
        }
        return homeDataSource!!
    }

    fun getSource() = homeDataSourceLiveData.value
}