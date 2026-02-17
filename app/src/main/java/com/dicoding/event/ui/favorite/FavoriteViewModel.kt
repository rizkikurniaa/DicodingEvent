package com.dicoding.event.ui.favorite

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.dicoding.event.data.local.entity.FavoriteEvent
import com.dicoding.event.data.repository.FavoriteRepository

class FavoriteViewModel(application: Application) : ViewModel() {
    private val mFavoriteRepository: FavoriteRepository = FavoriteRepository(application)

    fun getAllFavoriteEvents(): LiveData<List<FavoriteEvent>> = mFavoriteRepository.getAllFavoriteEvents()
}
