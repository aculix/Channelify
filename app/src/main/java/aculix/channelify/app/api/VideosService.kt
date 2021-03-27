package aculix.channelify.app.api

import aculix.channelify.app.model.Video
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface VideosService {

    @GET("videos")
    suspend fun getVideoInfo(
        @Query("id") videoId: String,
        @Query("part") part: String = "snippet, statistics",
        @Query("fields") fields: String = "items(snippet(publishedAt, title, description, thumbnails), statistics)",
        @Query("maxResults") maxResults: Int = 1
    ): Response<Video>
}