package com.example.atividadefinal.database

import androidx.room.*

@Dao
interface TripDao {
    @Insert
    suspend fun insertTrip(trip: Trip)

    @Query("SELECT * FROM trips order by id desc")
    suspend fun getAllTrips(): List<Trip>

    @Delete
    suspend fun deleteTrip(trip: Trip)

    @Query("SELECT * FROM trips WHERE id = :tripId LIMIT 1")
    suspend fun getTripById(tripId: Int): Trip?

    @Update
    suspend fun updateTrip(trip: Trip)

    @Query("UPDATE trips SET sugestao = :suggestion WHERE id = :tripId")
    suspend fun updateSuggestion(tripId: Int, suggestion: String)
}