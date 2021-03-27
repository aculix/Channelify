package aculix.channelify.app.di

import aculix.channelify.app.repository.FavoritesRepository
import aculix.channelify.app.viewmodel.FavoritesViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val favoritesModule = module {

    // FavoriteVideoDao is already injected in videoDetailsModule

    single { FavoritesRepository(get()) }

    viewModel { FavoritesViewModel(get()) }
}
