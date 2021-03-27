package aculix.channelify.app.repository

import aculix.channelify.app.api.ChannelsService
import aculix.channelify.app.api.PlaylistItemsService
import aculix.channelify.app.api.SearchVideoService
import aculix.channelify.app.model.ChannelUploadsPlaylistInfo
import aculix.channelify.app.model.SearchedVideo
import retrofit2.Response

class HomeRepository(private val searchVideoService: SearchVideoService,
                     private val channelsService: ChannelsService,
                     private val playlistItemsService: PlaylistItemsService) {

    suspend fun getUploadsPlaylistId(channelId: String): Response<ChannelUploadsPlaylistInfo> =
        channelsService.getChannelUploadsPlaylistInfo(channelId)

    suspend fun getLatestVideos(playlistId: String, pageToken: String?) =
        playlistItemsService.getPlaylistVideos(playlistId, pageToken)

}