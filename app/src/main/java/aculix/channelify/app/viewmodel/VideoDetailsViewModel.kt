package aculix.channelify.app.viewmodel

import aculix.channelify.app.R
import aculix.channelify.app.model.FavoriteVideo
import aculix.channelify.app.repository.VideoDetailsRepository
import aculix.core.helper.ResultWrapper
import android.content.Context
import androidx.lifecycle.*
import kotlinx.coroutines.launch

class VideoDetailsViewModel(
    private val videoDetailsRepository: VideoDetailsRepository,
    private val context: Context
) : ViewModel() {

    private val _videoInfoLiveData = MutableLiveData<ResultWrapper>()
    val videoInfoLiveData: LiveData<ResultWrapper>
        get() = _videoInfoLiveData

    private val _favoriteVideoLiveData = MutableLiveData<Boolean>()
    val favoriteVideoLiveData: LiveData<Boolean>
        get() = _favoriteVideoLiveData

    fun getVideoInfo(videoId: String) {
        viewModelScope.launch {
            _videoInfoLiveData.value = ResultWrapper.Loading

            val response = videoDetailsRepository.getVideoInfo(videoId)
            if (response.isSuccessful) {
                _videoInfoLiveData.value = ResultWrapper.Success(response.body())
            } else {
                _videoInfoLiveData.value =
                    ResultWrapper.Error(context.getString(R.string.error_video_details))
            }
        }
    }

    fun addVideoToFavorites(favoriteVideo: FavoriteVideo) {
        viewModelScope.launch {
            videoDetailsRepository.addVideoToFavorites(favoriteVideo)
        }
    }

    fun removeVideoFromFavorites(favoriteVideo: FavoriteVideo) {
        viewModelScope.launch {
            videoDetailsRepository.removeVideoFromFavorites(favoriteVideo)
        }
    }

    fun getVideoFavoriteStatus(videoId: String) {
        viewModelScope.launch {
            _favoriteVideoLiveData.value =  videoDetailsRepository.isVideoAddedToFavorites(videoId)
        }
    }
}