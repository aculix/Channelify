package aculix.channelify.app.model


import com.google.gson.annotations.SerializedName

data class Comment(
    val items: List<Item>,
    val nextPageToken: String?
) {
    data class Item(
        val snippet: Snippet
    ) {
        data class Snippet(
            val topLevelComment: TopLevelComment,
            val totalReplyCount: Int
        ) {
            data class TopLevelComment(
                val id: String,
                val snippet: Snippet
            ) {
                data class Snippet(
                    val authorDisplayName: String,
                    val authorProfileImageUrl: String,
                    val likeCount: Int,
                    val publishedAt: String,
                    val textOriginal: String,
                    val updatedAt: String
                )
            }
        }
    }
}