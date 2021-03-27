package aculix.channelify.app.api

import aculix.channelify.app.model.ChannelUploadsPlaylistInfo
import aculix.channelify.app.model.SearchedVideo
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ChannelsService {

    @GET("channels")
    suspend fun getChannelUploadsPlaylistInfo(
        @Query("id") channelId: String,
        @Query("part") part: String = "contentDetails"
    ): Response<ChannelUploadsPlaylistInfo>
}