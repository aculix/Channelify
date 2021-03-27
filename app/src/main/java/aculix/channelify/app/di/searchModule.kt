package aculix.channelify.app.di

import aculix.channelify.app.R
import aculix.channelify.app.api.SearchVideoService
import aculix.channelify.app.repository.SearchRepository
import aculix.channelify.app.viewmodel.SearchViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

val searchModule = module {

    factory { provideSearchVideoService(get()) }

    single { SearchRepository(get()) }

    viewModel { SearchViewModel(get(), androidContext().getString(R.string.channel_id), androidContext().getString(R.string.error_search_empty_result_title)) }
}

private fun provideSearchVideoService(retrofit: Retrofit) =
    retrofit.create(SearchVideoService::class.java)
