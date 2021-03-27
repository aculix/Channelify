package aculix.channelify.app.repository

import aculix.channelify.app.api.CommentService
import aculix.channelify.app.model.Comment
import retrofit2.Response

class CommentsRepository(private val commentService: CommentService) {

    suspend fun getVideoComments(videoId: String, pageToken: String?, sortOrder: String): Response<Comment> =
        commentService.getVideoComments(videoId, pageToken, sortOrder)
}