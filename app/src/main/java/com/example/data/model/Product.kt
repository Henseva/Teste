package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val category: String, // Fruits, Vegetables, Dairy, Beverages, Snacks, Bakery, Meat
    val unit: String,     // 1kg, 500g, 1L, etc.
    val price: Double,
    val originalPrice: Double = 0.0,
    val discountPercent: Int = 0,
    val rating: Double = 4.8,
    val ratingCount: Int = 120,
    val description: String = "",
    val imageResName: String = "",
    val galleryResNames: String = "", // Comma separated tags or identifiers
    val stockQuantity: Int = 50,
    val minStockThreshold: Int = 10,
    val isFlashDeal: Boolean = false,
    val isBestSelling: Boolean = false
) {
    val isLowStock: Boolean
        get() = stockQuantity in 1..minStockThreshold

    val isOutOfStock: Boolean
        get() = stockQuantity <= 0
}
