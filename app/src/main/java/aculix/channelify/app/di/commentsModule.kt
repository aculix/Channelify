package aculix.channelify.app.di

import aculix.channelify.app.api.CommentService
import aculix.channelify.app.repository.CommentsRepository
import aculix.channelify.app.viewmodel.CommentsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

val commentsModule = module {

    factory { provideCommentService(get()) }

    single { CommentsRepository(get()) }

    viewModel { CommentsViewModel(get(), androidContext()) }
}

private fun provideCommentService(retrofit: Retrofit) =
    retrofit.create(CommentService::class.java)
