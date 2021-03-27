package aculix.channelify.app.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_videos")
data class FavoriteVideo(@PrimaryKey val id: String,
                         val title: String,
                         val thumbnail: String,
                         val timeInMillis: Long,
                         var isChecked: Boolean)