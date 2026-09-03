package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stock_movements")
data class StockMovement(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val productId: Long,
    val productName: String,
    val type: String, // VENDA, REPOSICAO, AJUSTE_MANUAL
    val quantityChanged: Int,
    val newStockQuantity: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val note: String = ""
)
