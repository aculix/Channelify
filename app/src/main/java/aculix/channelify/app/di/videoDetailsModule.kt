package aculix.channelify.app.di

import aculix.channelify.app.api.VideosService
import aculix.channelify.app.db.ChannelifyDatabase
import aculix.channelify.app.repository.VideoDetailsRepository
import aculix.channelify.app.repository.VideoPlayerRepository
import aculix.channelify.app.viewmodel.VideoDetailsViewModel
import aculix.channelify.app.viewmodel.VideoPlayerViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

val videoDetailsModule = module {
    factory { provideVideosService(get()) }

    single { provideFavoriteVideoDao(get()) }
    single { VideoDetailsRepository(get(), get()) }

    viewModel { VideoDetailsViewModel(get(), androidContext()) }
}

private fun provideVideosService(retrofit: Retrofit) =
    retrofit.create(VideosService::class.java)

private fun provideFavoriteVideoDao(channelifyDatabase: ChannelifyDatabase) =
    channelifyDatabase.favoriteVideoDao()


