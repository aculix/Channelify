package aculix.channelify.app.viewmodel

import aculix.channelify.app.R
import aculix.channelify.app.repository.AboutRepository
import aculix.core.helper.ResultWrapper
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class AboutViewModel(
    private val aboutRepository: AboutRepository,
    private val channelId: String,
    private val context: Context
) : ViewModel() {

    private val _channelInfoLiveData = MutableLiveData<ResultWrapper>()
    val channelInfoLiveData: LiveData<ResultWrapper>
        get() = _channelInfoLiveData

    fun getChannelInfo() {
        viewModelScope.launch {
            _channelInfoLiveData.value = ResultWrapper.Loading

            val response = aboutRepository.getChannelInfo(channelId)
            if (response.isSuccessful) {
                _channelInfoLiveData.value = ResultWrapper.Success(response.body())
            } else {
                _channelInfoLiveData.value =
                    ResultWrapper.Error(context.getString(R.string.error_channel_info))
            }
        }
    }


}