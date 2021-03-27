package aculix.channelify.app.db

import aculix.channelify.app.model.FavoriteVideo
import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [FavoriteVideo::class], version = 1)
abstract class ChannelifyDatabase : RoomDatabase() {

    abstract fun favoriteVideoDao(): FavoriteVideoDao
}