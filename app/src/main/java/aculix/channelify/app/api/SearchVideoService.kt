package aculix.channelify.app.api

import aculix.channelify.app.model.SearchedVideo
import aculix.channelify.app.utils.Constants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface SearchVideoService {

    @GET("search")
    suspend fun searchVideos(
        @Query("q") searchQuery: String,
        @Query("channelId") channelId: String,
        @Query("pageToken") pageToken: String?,
        @QueryMap defaultQueryMap: HashMap<String, String>,
        @Query("maxResults") maxResults: Int = Constants.YT_API_MAX_RESULTS
    ): Response<SearchedVideo>
}