package com.dicoding.event.data.retrofit

import com.dicoding.event.data.response.DetailEventResponse
import com.dicoding.event.data.response.EventResponse
import retrofit2.http.*

interface ApiService {
    @GET("events")
    suspend fun getEvents(
        @Query("active") active: Int,
        @Query("q") q: String? = null
    ): EventResponse

    @GET("events/{id}")
    suspend fun getDetailEvent(
        @Path("id") id: String
    ): DetailEventResponse
}
