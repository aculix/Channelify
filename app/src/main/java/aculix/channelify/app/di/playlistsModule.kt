package aculix.channelify.app.di

import aculix.channelify.app.R
import aculix.channelify.app.api.PlaylistsService
import aculix.channelify.app.api.SearchVideoService
import aculix.channelify.app.repository.PlaylistsRepository
import aculix.channelify.app.viewmodel.PlaylistsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

val playlistsModule = module {

    factory { providePlaylistsService(get()) }

    single { PlaylistsRepository(get()) }

    viewModel { PlaylistsViewModel(get(), androidContext().getString(R.string.channel_id)) }
}

private fun providePlaylistsService(retrofit: Retrofit) =
    retrofit.create(PlaylistsService::class.java)
