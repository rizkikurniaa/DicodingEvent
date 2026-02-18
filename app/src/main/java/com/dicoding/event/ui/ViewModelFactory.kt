package com.dicoding.event.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.event.data.repository.EventRepository
import com.dicoding.event.di.Injection
import com.dicoding.event.ui.detail.DetailViewModel
import com.dicoding.event.ui.favorite.FavoriteViewModel
import com.dicoding.event.ui.home.HomeViewModel
import com.dicoding.event.ui.setting.SettingPreferences
import com.dicoding.event.ui.setting.SettingViewModel
import com.dicoding.event.ui.setting.dataStore

class ViewModelFactory private constructor(
    private val eventRepository: EventRepository,
    private val pref: SettingPreferences? = null
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(EventViewModel::class.java) -> {
                EventViewModel(eventRepository) as T
            }
            modelClass.isAssignableFrom(DetailViewModel::class.java) -> {
                DetailViewModel(eventRepository) as T
            }
            modelClass.isAssignableFrom(FavoriteViewModel::class.java) -> {
                FavoriteViewModel(eventRepository) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(eventRepository) as T
            }
            modelClass.isAssignableFrom(SettingViewModel::class.java) -> {
                SettingViewModel(pref!!) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        fun getInstance(context: Context): ViewModelFactory =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: ViewModelFactory(
                    Injection.provideRepository(context),
                    SettingPreferences.getInstance(context.dataStore)
                )
            }.also { INSTANCE = it }
    }
}
