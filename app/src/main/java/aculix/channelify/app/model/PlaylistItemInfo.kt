package aculix.channelify.app.model


import com.google.gson.annotations.SerializedName

data class PlaylistItemInfo(
    val nextPageToken: String?,
    val prevPageToken: String?,
    val items: List<Item>
) {
    data class Item(
        val contentDetails: ContentDetails,
        val snippet: Snippet
    ) {
        data class ContentDetails(
            val videoId: String,
            val videoPublishedAt: String
        )

        data class Snippet(
            val thumbnails: Thumbnails,
            val title: String
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
    }
}