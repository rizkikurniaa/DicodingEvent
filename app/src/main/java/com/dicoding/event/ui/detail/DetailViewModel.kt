package com.dicoding.event.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.event.data.local.entity.FavoriteEvent
import com.dicoding.event.data.repository.EventRepository
import com.dicoding.event.data.response.ListEventsItem
import kotlinx.coroutines.launch

class DetailViewModel(private val repository: EventRepository) : ViewModel() {

    private val _eventDetail = MutableLiveData<ListEventsItem>()
    val eventDetail: LiveData<ListEventsItem> = _eventDetail

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun getDetailEvent(id: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val event = repository.getDetailEvent(id)
                _eventDetail.value = event
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getFavoriteById(id: String): LiveData<FavoriteEvent> = repository.getFavoriteById(id)

    fun setFavorite(event: ListEventsItem) {
        viewModelScope.launch {
            repository.insertFavorite(event)
        }
    }

    fun deleteFavorite(event: ListEventsItem) {
        viewModelScope.launch {
            repository.deleteFavorite(event)
        }
    }
}
