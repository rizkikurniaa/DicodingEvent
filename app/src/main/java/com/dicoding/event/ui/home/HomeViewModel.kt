package com.dicoding.event.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.event.data.repository.EventRepository
import com.dicoding.event.data.response.ListEventsItem
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: EventRepository) : ViewModel() {

    private val _upcomingEvents = MutableLiveData<List<ListEventsItem>>()
    val upcomingEvents: LiveData<List<ListEventsItem>> = _upcomingEvents

    private val _finishedEvents = MutableLiveData<List<ListEventsItem>>()
    val finishedEvents: LiveData<List<ListEventsItem>> = _finishedEvents

    private val _isLoadingUpcoming = MutableLiveData<Boolean>()
    val isLoadingUpcoming: LiveData<Boolean> = _isLoadingUpcoming

    private val _isLoadingFinished = MutableLiveData<Boolean>()
    val isLoadingFinished: LiveData<Boolean> = _isLoadingFinished

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun fetchUpcomingEvents() {
        _isLoadingUpcoming.value = true
        viewModelScope.launch {
            try {
                val events = repository.getEvents(1)
                _upcomingEvents.value = events
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoadingUpcoming.value = false
            }
        }
    }

    fun fetchFinishedEvents() {
        _isLoadingFinished.value = true
        viewModelScope.launch {
            try {
                val events = repository.getEvents(0)
                _finishedEvents.value = events
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoadingFinished.value = false
            }
        }
    }
}
