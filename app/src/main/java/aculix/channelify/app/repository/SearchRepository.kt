package aculix.channelify.app.repository

import aculix.channelify.app.api.SearchVideoService
import aculix.channelify.app.model.SearchedVideo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SearchRepository(private val searchVideoService: SearchVideoService) {

    suspend fun searchVideos(searchQuery: String, channelId: String, pageToken: String?) = withContext(Dispatchers.IO) {
        val defaultQueryMap = HashMap<String, String>()
        defaultQueryMap.apply {
            put("part", "id,snippet")
            put("fields", "nextPageToken, items(id(videoId), snippet(publishedAt, thumbnails, title))")
            put("order", "relevance")
            put("type", "video")
        }

        searchVideoService.searchVideos(searchQuery, channelId, pageToken, defaultQueryMap)
    }
}