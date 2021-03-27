package aculix.channelify.app.model


import com.google.gson.annotations.SerializedName

/**
 * Model class that is returned when an API call is made to
 * request details about the Uploads playlist of the channel
 *
 * URL: https://www.googleapis.com/youtube/v3/channels
 */
data class ChannelUploadsPlaylistInfo(
    val items: List<Item>
) {
    data class Item(
        val contentDetails: ContentDetails
    ) {
        data class ContentDetails(
            val relatedPlaylists: RelatedPlaylists
        ) {
            data class RelatedPlaylists(
                val uploads: String
            )
        }
    }
}