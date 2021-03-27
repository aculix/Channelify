package aculix.channelify.app.model


import com.google.gson.annotations.SerializedName

data class Playlist(
    val items: List<Item>,
    val nextPageToken: String
) {
    data class Item(
        val contentDetails: ContentDetails,
        val id: String,
        val snippet: Snippet
    ) {
        data class ContentDetails(
            val itemCount: Int
        )

        data class Snippet(
            val description: String,
            val publishedAt: String,
            val thumbnails: Thumbnails,
            val title: String
        ) {
            data class Thumbnails(
                val default: Default,
                val high: High,
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