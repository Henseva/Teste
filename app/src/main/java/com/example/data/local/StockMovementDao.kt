package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.data.model.StockMovement
import kotlinx.coroutines.flow.Flow

@Dao
interface StockMovementDao {
    @Query("SELECT * FROM stock_movements ORDER BY timestamp DESC")
    fun getAllMovements(): Flow<List<StockMovement>>

    @Insert
    suspend fun insertMovement(movement: StockMovement)
}
