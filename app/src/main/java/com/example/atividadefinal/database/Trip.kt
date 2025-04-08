package com.example.atividadefinal.database


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trips")
data class Trip(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val destino: String,
    val tipo: String,
    val dataInicio: String,
    val dataFinal: String,
    val orcamento: Double
)
