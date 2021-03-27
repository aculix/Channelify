package aculix.channelify.app.repository

import aculix.channelify.app.api.CommentRepliesService
import aculix.channelify.app.api.CommentService
import aculix.channelify.app.model.Comment
import aculix.channelify.app.model.CommentReply
import retrofit2.Response

class CommentRepliesRepository(private val commentRepliesService: CommentRepliesService) {

    suspend fun getCommentReplies(commentId: String, pageToken: String?): Response<CommentReply> =
        commentRepliesService.getCommentReplies(commentId, pageToken)
}