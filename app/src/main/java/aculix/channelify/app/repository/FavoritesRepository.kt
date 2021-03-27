package aculix.channelify.app.repository

import aculix.channelify.app.db.FavoriteVideoDao
import aculix.channelify.app.model.FavoriteVideo
import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FavoritesRepository(private val favoriteVideoDao: FavoriteVideoDao) {

    suspend fun getFavoriteVideosFromDb(): List<FavoriteVideo> = withContext(Dispatchers.IO) {
        favoriteVideoDao.getAllFavoriteVideos()
    }

    suspend fun removeVideoFromFavorites(favoriteVideo: FavoriteVideo) {
        withContext(Dispatchers.IO) {
            favoriteVideoDao.removeFavoriteVideo(favoriteVideo)
        }
    }

    suspend fun addVideoToFavorites(favoriteVideo: FavoriteVideo) {
        withContext(Dispatchers.IO) {
            favoriteVideoDao.addFavoriteVideo(favoriteVideo)
        }
    }
}