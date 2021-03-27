package aculix.channelify.app.di

import aculix.channelify.app.R
import aculix.channelify.app.api.ChannelsService
import aculix.channelify.app.api.PlaylistItemsService
import aculix.channelify.app.api.SearchVideoService
import aculix.channelify.app.repository.HomeRepository
import aculix.channelify.app.viewmodel.HomeViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

val homeModule = module {

    factory { provideChannelsService(get()) }
    factory { providePlaylistItemsService(get()) }

    single { HomeRepository(get(), get(), get()) }

    viewModel { HomeViewModel(get(), androidContext().getString(R.string.channel_id), androidContext()) }
}

private fun provideChannelsService(retrofit: Retrofit) =
    retrofit.create(ChannelsService::class.java)

private fun providePlaylistItemsService(retrofit: Retrofit) =
    retrofit.create(PlaylistItemsService::class.java)