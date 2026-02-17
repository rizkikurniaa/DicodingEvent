package com.dicoding.event.di

import android.content.Context
import com.dicoding.event.data.local.room.FavoriteRoomDatabase
import com.dicoding.event.data.repository.EventRepository
import com.dicoding.event.data.retrofit.ApiConfig

object Injection {
    fun provideRepository(context: Context): EventRepository {
        val apiService = ApiConfig.getApiService()
        val database = FavoriteRoomDatabase.getDatabase(context)
        val dao = database.favoriteEventDao()
        return EventRepository.getInstance(apiService, dao)
    }
}
