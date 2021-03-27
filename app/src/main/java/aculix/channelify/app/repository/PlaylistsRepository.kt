package aculix.channelify.app.repository

import aculix.channelify.app.api.PlaylistsService

class PlaylistsRepository(private val playlistsService: PlaylistsService) {

    suspend fun getPlaylists(channelId: String, pageToken: String?) =
        playlistsService.getPlaylists(channelId, pageToken)
}