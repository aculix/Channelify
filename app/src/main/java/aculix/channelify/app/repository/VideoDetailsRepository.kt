package aculix.channelify.app.repository

import aculix.channelify.app.api.VideosService
import aculix.channelify.app.db.FavoriteVideoDao
import aculix.channelify.app.model.FavoriteVideo
import aculix.channelify.app.model.Video
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class VideoDetailsRepository(
    private val videosService: VideosService,
    private val favoriteVideoDao: FavoriteVideoDao
) {

    suspend fun getVideoInfo(videoId: String): Response<Video> =
        videosService.getVideoInfo(videoId)

    suspend fun addVideoToFavorites(favoriteVideo: FavoriteVideo) {
        withContext(Dispatchers.IO) {
            favoriteVideoDao.addFavoriteVideo(favoriteVideo)
        }
    }

    suspend fun removeVideoFromFavorites(favoriteVideo: FavoriteVideo) {
        withContext(Dispatchers.IO) {
            favoriteVideoDao.removeFavoriteVideo(favoriteVideo)
        }
    }

    suspend fun isVideoAddedToFavorites(videoId: String): Boolean = withContext(Dispatchers.IO) {
        favoriteVideoDao.getFavoriteVideoId(videoId) != null
    }

}