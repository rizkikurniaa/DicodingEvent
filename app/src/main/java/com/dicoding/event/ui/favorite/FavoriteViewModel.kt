package com.dicoding.event.ui.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.dicoding.event.data.local.entity.FavoriteEvent
import com.dicoding.event.data.repository.EventRepository

class FavoriteViewModel(private val repository: EventRepository) : ViewModel() {
    fun getAllFavoriteEvents(): LiveData<List<FavoriteEvent>> = repository.getAllFavoriteEvents()
}
