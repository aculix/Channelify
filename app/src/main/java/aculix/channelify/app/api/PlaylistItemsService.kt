package aculix.channelify.app.api

import aculix.channelify.app.model.ChannelUploadsPlaylistInfo
import aculix.channelify.app.model.PlaylistItemInfo
import aculix.channelify.app.utils.Constants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PlaylistItemsService {

    @GET("playlistItems")
    suspend fun getPlaylistVideos(
        @Query("playlistId") playlistId: String,
        @Query("pageToken") pageToken: String?,
        @Query("part") part: String = "snippet,contentDetails",
        @Query("fields") fields: String = "nextPageToken, prevPageToken, items(snippet(title, thumbnails), contentDetails(videoId, videoPublishedAt))",
        @Query("maxResults") maxResults: Int = Constants.YT_API_MAX_RESULTS
    ): Response<PlaylistItemInfo>
}