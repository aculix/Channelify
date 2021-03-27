package aculix.channelify.app.model


/**
 * Model class that is returned when a Video Search API call is made.
 *
 * URL: https://www.googleapis.com/youtube/v3/search
 */
data class SearchedVideo(
    val items: List<Item>,
    val nextPageToken: String?
) {
    data class Item(
        val id: Id,
        val snippet: Snippet
    ) {
        data class Id(
            val videoId: String
        )

        data class Snippet(
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