package aculix.channelify.app.model


data class ChannelInfo(
    val items: List<Item>
) {
    data class Item(
        val brandingSettings: BrandingSettings?,
        val snippet: Snippet,
        val statistics: Statistics
    ) {
        data class BrandingSettings(
            val image: Image?
        ) {
            data class Image(
                val bannerMobileHdImageUrl: String?,
                val bannerMobileMediumHdImageUrl: String
            )
        }

        data class Snippet(
            val description: String,
            val publishedAt: String,
            val thumbnails: Thumbnails,
            val title: String
        ) {
            data class Thumbnails(
                val default: Default,
                val high: High?,
                val medium: Medium
            ) {
                data class Default(
                    val height: Int,
                    val url: String,
                    val width: Int
                )

                data class High(
                    val height: Int,
                    val url: String,
                    val width: Int
                )

                data class Medium(
                    val height: Int,
                    val url: String,
                    val width: Int
                )
            }
        }

        data class Statistics(
            val subscriberCount: String,
            val videoCount: String,
            val viewCount: String
        )
    }
}