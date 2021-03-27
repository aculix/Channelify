package aculix.channelify.app.paging.datasourcefactory

import aculix.channelify.app.model.Playlist
import aculix.channelify.app.paging.datasource.PlaylistsDataSource
import aculix.channelify.app.repository.PlaylistsRepository
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import kotlinx.coroutines.CoroutineScope

class PlaylistsDataSourceFactory(
    private val playlistRepository: PlaylistsRepository,
    private val coroutineScope: CoroutineScope,
    private val channelId: String
) : DataSource.Factory<String, Playlist.Item>() {

    private var playlistsDataSource: PlaylistsDataSource? = null
    val playlistsDataSourceLiveData = MutableLiveData<PlaylistsDataSource>()

    override fun create(): DataSource<String, Playlist.Item> {
        if (playlistsDataSource == null) {
            playlistsDataSource =
                PlaylistsDataSource(
                    playlistRepository,
                    coroutineScope,
                    channelId
                )
            playlistsDataSourceLiveData.postValue(playlistsDataSource)
        }
        return playlistsDataSource!!
    }

    fun getSource() = playlistsDataSourceLiveData.value
}