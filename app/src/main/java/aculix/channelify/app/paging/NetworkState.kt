package aculix.channelify.app.paging

/**
 * Class used to handle network state
 */
data class NetworkState constructor(val status: Status,
                                    val msg: String? = null) {

    companion object {
        val LOADED = NetworkState(Status.SUCCESS)
        val LOADING = NetworkState(Status.LOADING)
        fun error(msg: String?) = NetworkState(Status.FAILED, msg)
    }
}

enum class Status {
    LOADING,
    SUCCESS,
    FAILED
}