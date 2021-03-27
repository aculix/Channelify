package aculix.channelify.app.model


import com.google.gson.annotations.SerializedName

data class Video(
    val items: List<Item>
) {
    data class Item(
        val snippet: Snippet,
        val statistics: Statistics
    ) {

        data class Snippet(
            val description: String,
            val publishedAt: String,
            val title: String,
            val thumbnails: Thumbnails
        ) {
            data class Thumbnails(
                val default: Default,
                val high: High,
                val maxres: Maxres,
                val medium: Medium,
                val standard: Standard?
            ) {
                data class Default(
                    val height: Int,
                    val url: String,
                    val width: Int
                )

                data class High(
                    val height: Int,
                    val url: String,
                    val width: Int
                )

                data class Maxres(
                    val height: Int,
                    val url: String,
                    val width: Int
                )

                data class Medium(
                    val height: Int,
                    val url: String,
                    val width: Int
                )

                data class Standard(
                    val height: Int,
                    val url: String,
                    val width: Int
                )
            }
        }

        data class Statistics(
            val commentCount: String,
            val dislikeCount: String?,
            val favoriteCount: String,
            val likeCount: String?,
            val viewCount: String?
        )
    }
}