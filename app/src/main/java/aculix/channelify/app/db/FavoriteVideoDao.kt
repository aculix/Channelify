package aculix.channelify.app.db

import aculix.channelify.app.model.FavoriteVideo
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FavoriteVideoDao {

    @Insert
    suspend fun addFavoriteVideo(favoriteVideo: FavoriteVideo)

    @Delete
    suspend fun removeFavoriteVideo(favoriteVideo: FavoriteVideo)

    @Query("DELETE FROM favorite_videos where  id in (:idList)")
    suspend fun removeMultipleFavoriteVideos(idList: List<String>)

    @Query("SELECT id FROM favorite_videos WHERE id = :id LIMIT 1")
    suspend fun getFavoriteVideoId(id: String): String?

    @Query("SELECT * FROM favorite_videos ORDER BY timeInMillis DESC")
    suspend fun getAllFavoriteVideos(): List<FavoriteVideo>

    @Query("DELETE FROM favorite_videos")
    suspend fun removeAllFavoriteVideos()



}