package aculix.channelify.app.repository

import aculix.channelify.app.api.PlaylistItemsService

class PlaylistVideosRepository(private val playlistItemsService: PlaylistItemsService) {

    suspend fun getPlaylistVideos(playlistId: String, pageToken: String?) =
        playlistItemsService.getPlaylistVideos(playlistId, pageToken)
}