package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartEntity(
    @PrimaryKey val productId: Long,
    val productName: String,
    val productUnit: String,
    val productPrice: Double,
    val imageResName: String,
    val quantity: Int
)
