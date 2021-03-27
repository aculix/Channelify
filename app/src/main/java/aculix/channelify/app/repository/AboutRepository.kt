package aculix.channelify.app.repository

import aculix.channelify.app.api.ChannelInfoService

class AboutRepository(private val channelInfoService: ChannelInfoService) {

    suspend fun getChannelInfo(channelId: String) =
        channelInfoService.getChannelInfo(channelId)
}