package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class Order(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val orderNumber: String,
    val timestamp: Long = System.currentTimeMillis(),
    val itemsSummary: String,
    val totalAmount: Double,
    val deliveryFee: Double,
    val deliveryMethod: String,
    val paymentMethod: String,
    val deliveryAddress: String,
    val status: String = "Preparando para entrega"
)
