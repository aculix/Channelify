package aculix.channelify.app.di

import aculix.channelify.app.R
import aculix.channelify.app.api.ChannelInfoService
import aculix.channelify.app.repository.AboutRepository
import aculix.channelify.app.viewmodel.AboutViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

val aboutModule = module {

    factory { provideChannelInfoService(get()) }

    single { AboutRepository(get()) }

    viewModel { AboutViewModel(get(), androidContext().getString(R.string.channel_id), androidContext()) }
}

private fun provideChannelInfoService(retrofit: Retrofit) =
    retrofit.create(ChannelInfoService::class.java)
