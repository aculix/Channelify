package aculix.channelify.app.api

import aculix.channelify.app.model.ChannelInfo
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ChannelInfoService {

    @GET("channels")
    suspend fun getChannelInfo(
        @Query("id") channelId: String,
        @Query("part") part: String = "snippet, statistics, brandingSettings",
        @Query("fields") fields: String = "items(snippet(title, description, publishedAt, thumbnails), statistics(viewCount, subscriberCount, videoCount), brandingSettings(image(bannerMobileHdImageUrl, bannerMobileMediumHdImageUrl)))",
        @Query("maxResults") maxResults: Int = 1
    ): Response<ChannelInfo>
}