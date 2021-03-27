package aculix.channelify.app.api

import aculix.channelify.app.model.CommentReply
import aculix.channelify.app.utils.Constants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CommentRepliesService {

    /**
     * Gets the list of replies for a particular comment
     */
    @GET("comments")
    suspend fun getCommentReplies(
        @Query("parentId") commentId: String,
        @Query("pageToken") pageToken: String?,
        @Query("part") part: String = "snippet",
        @Query("fields") fields: String = "nextPageToken, items(snippet(authorDisplayName, authorProfileImageUrl, textOriginal, likeCount, publishedAt, updatedAt))",
        @Query("maxResults") maxResults: Int = Constants.YT_API_MAX_RESULTS
    ): Response<CommentReply>
}