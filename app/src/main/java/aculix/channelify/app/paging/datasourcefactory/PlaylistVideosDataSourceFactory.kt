package aculix.channelify.app.paging.datasourcefactory

import aculix.channelify.app.model.PlaylistItemInfo
import aculix.channelify.app.paging.datasource.PlaylistVideosDataSource
import aculix.channelify.app.repository.PlaylistVideosRepository
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import kotlinx.coroutines.CoroutineScope

class PlaylistVideosDataSourceFactory(
    private val playlistVideosRepository: PlaylistVideosRepository,
    private val coroutineScope: CoroutineScope,
    private val playlistId: String
) : DataSource.Factory<String, PlaylistItemInfo.Item>() {

    private var playlistVideosDataSource: PlaylistVideosDataSource? = null
    val playlistVideosDataSourceLiveData = MutableLiveData<PlaylistVideosDataSource>()

    override fun create(): DataSource<String, PlaylistItemInfo.Item> {
        if (playlistVideosDataSource == null) {
            playlistVideosDataSource =
                PlaylistVideosDataSource(
                    playlistVideosRepository,
                    coroutineScope,
                    playlistId
                )
            playlistVideosDataSourceLiveData.postValue(playlistVideosDataSource)
        }
        return playlistVideosDataSource!!
    }

    fun getSource() = playlistVideosDataSourceLiveData.value
}