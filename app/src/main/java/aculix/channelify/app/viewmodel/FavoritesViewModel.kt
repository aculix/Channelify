package aculix.channelify.app.viewmodel

import aculix.channelify.app.model.FavoriteVideo
import aculix.channelify.app.repository.FavoritesRepository
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavoritesViewModel(private val favoritesRepository: FavoritesRepository) : ViewModel() {

    var favoriteVideosLiveData: LiveData<List<FavoriteVideo>> = liveData(Dispatchers.IO) {
        emit(favoritesRepository.getFavoriteVideosFromDb())
    }

    fun removeVideoFromFavorites(favoriteVideo: FavoriteVideo) {
        viewModelScope.launch {
            favoritesRepository.removeVideoFromFavorites(favoriteVideo)
        }
    }

    fun addVideoToFavorites(favoriteVideo: FavoriteVideo) {
        viewModelScope.launch {
            favoritesRepository.addVideoToFavorites(favoriteVideo)
        }
    }
}