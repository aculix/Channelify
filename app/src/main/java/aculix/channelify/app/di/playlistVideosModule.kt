package aculix.channelify.app.di

import aculix.channelify.app.repository.PlaylistVideosRepository
import aculix.channelify.app.viewmodel.PlaylistVideosViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * No need to provide PlaylistItemsService since it is already being provided by Koin in the
 * homeModule
 */
val playlistVideosModule = module {

    single { PlaylistVideosRepository(get()) }

    viewModel { PlaylistVideosViewModel(get()) }
}
