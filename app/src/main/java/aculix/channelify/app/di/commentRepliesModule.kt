package aculix.channelify.app.di

import aculix.channelify.app.api.CommentRepliesService
import aculix.channelify.app.api.CommentService
import aculix.channelify.app.repository.CommentRepliesRepository
import aculix.channelify.app.repository.CommentsRepository
import aculix.channelify.app.viewmodel.CommentRepliesViewModel
import aculix.channelify.app.viewmodel.CommentsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

val commentRepliesModule = module {

    factory { provideCommentRepliesService(get()) }

    single { CommentRepliesRepository(get()) }

    viewModel { CommentRepliesViewModel(get()) }
}

private fun provideCommentRepliesService(retrofit: Retrofit) =
    retrofit.create(CommentRepliesService::class.java)
