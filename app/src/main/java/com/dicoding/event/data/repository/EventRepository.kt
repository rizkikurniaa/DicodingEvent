package com.dicoding.event.data.repository

import androidx.lifecycle.LiveData
import com.dicoding.event.data.local.entity.FavoriteEvent
import com.dicoding.event.data.local.room.FavoriteEventDao
import com.dicoding.event.data.response.ListEventsItem
import com.dicoding.event.data.retrofit.ApiService

class EventRepository private constructor(
    private val apiService: ApiService,
    private val favoriteEventDao: FavoriteEventDao
) {
    // API Section menggunakan suspend function
    suspend fun getEvents(active: Int, query: String? = null): List<ListEventsItem> {
        return apiService.getEvents(active, query).listEvents
    }

    suspend fun getDetailEvent(id: String): ListEventsItem {
        return apiService.getDetailEvent(id).event
    }

    // Local Section
    fun getAllFavoriteEvents(): LiveData<List<FavoriteEvent>> = favoriteEventDao.getAllFavoriteEvents()

    fun getFavoriteById(id: String): LiveData<FavoriteEvent> = favoriteEventDao.getFavoriteEventById(id)

    suspend fun insertFavorite(event: ListEventsItem) {
        val favorite = FavoriteEvent(event.id.toString(), event.name, event.mediaCover)
        favoriteEventDao.insert(favorite)
    }

    suspend fun deleteFavorite(event: ListEventsItem) {
        val favorite = FavoriteEvent(event.id.toString(), event.name, event.mediaCover)
        favoriteEventDao.delete(favorite)
    }

    companion object {
        @Volatile
        private var INSTANCE: EventRepository? = null
        fun getInstance(apiService: ApiService, favoriteEventDao: FavoriteEventDao): EventRepository =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: EventRepository(apiService, favoriteEventDao)
            }.also { INSTANCE = it }
    }
}
