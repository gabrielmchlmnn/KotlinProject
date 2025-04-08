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
}