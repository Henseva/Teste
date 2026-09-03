package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.CartEntity
import com.example.data.model.Order
import com.example.data.model.Product
import com.example.data.model.StockMovement
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Product::class, CartEntity::class, Order::class, StockMovement::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun cartDao(): CartDao
    abstract fun orderDao(): OrderDao
    abstract fun stockMovementDao(): StockMovementDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val defaultProducts: List<Product> = listOf(
            Product(
                id = 1,
                name = "Fresh Avocado",
                category = "Fruits",
                unit = "1kg",
                price = 3.45,
                originalPrice = 4.20,
                discountPercent = 18,
                rating = 4.8,
                ratingCount = 230,
                description = "Creamy, nutritious and perfectly ripe avocados from organic orchard. Rich in healthy monounsaturated fats, potassium and dietary fiber.",
                imageResName = "avocado",
                stockQuantity = 45,
                minStockThreshold = 10,
                isFlashDeal = false,
                isBestSelling = true
            ),
            Product(
                id = 2,
                name = "Banana",
                category = "Fruits",
                unit = "1kg",
                price = 1.25,
                originalPrice = 1.60,
                discountPercent = 22,
                rating = 4.9,
                ratingCount = 420,
                description = "Sweet tropical bananas, naturally ripened and full of sustained energy, vitamin B6 and potassium.",
                imageResName = "banana",
                stockQuantity = 120,
                minStockThreshold = 15,
                isFlashDeal = true,
                isBestSelling = true
            ),
            Product(
                id = 3,
                name = "Red Apple",
                category = "Fruits",
                unit = "1kg",
                price = 2.45,
                originalPrice = 3.00,
                discountPercent = 18,
                rating = 4.7,
                ratingCount = 310,
                description = "Crisp, sweet and delightfully crunchy red gala apples, hand-picked at peak freshness.",
                imageResName = "apple",
                stockQuantity = 80,
                minStockThreshold = 15,
                isFlashDeal = true,
                isBestSelling = true
            ),
            Product(
                id = 4,
                name = "Orange",
                category = "Fruits",
                unit = "1kg",
                price = 1.85,
                originalPrice = 2.30,
                discountPercent = 20,
                rating = 4.8,
                ratingCount = 195,
                description = "Juicy sun-ripened oranges packed with Vitamin C and refreshing sweet citrus flavor.",
                imageResName = "orange",
                stockQuantity = 65,
                minStockThreshold = 12,
                isFlashDeal = true,
                isBestSelling = true
            ),
            Product(
                id = 5,
                name = "Milk Full Cream",
                category = "Dairy",
                unit = "1L",
                price = 1.80,
                originalPrice = 2.10,
                discountPercent = 14,
                rating = 4.9,
                ratingCount = 540,
                description = "Fresh farm pasteurized whole milk with rich natural creaminess and calcium.",
                imageResName = "milk",
                stockQuantity = 35,
                minStockThreshold = 10,
                isFlashDeal = false,
                isBestSelling = true
            ),
            Product(
                id = 6,
                name = "Whole Wheat Bread",
                category = "Bakery",
                unit = "400g",
                price = 2.25,
                originalPrice = 2.80,
                discountPercent = 20,
                rating = 4.6,
                ratingCount = 160,
                description = "Artisanal 100% whole grain sliced loaf, freshly stone-baked every morning.",
                imageResName = "bread",
                stockQuantity = 25,
                minStockThreshold = 8,
                isFlashDeal = false,
                isBestSelling = true
            ),
            Product(
                id = 7,
                name = "Blueberry",
                category = "Fruits",
                unit = "250g",
                price = 2.25,
                originalPrice = 2.90,
                discountPercent = 22,
                rating = 4.9,
                ratingCount = 112,
                description = "Fresh plump blueberries, bursting with sweet-tart juice and high antioxidant levels.",
                imageResName = "blueberry",
                stockQuantity = 14,
                minStockThreshold = 10,
                isFlashDeal = false,
                isBestSelling = false
            ),
            Product(
                id = 8,
                name = "Strawberry",
                category = "Fruits",
                unit = "250g",
                price = 2.15,
                originalPrice = 2.80,
                discountPercent = 23,
                rating = 4.8,
                ratingCount = 280,
                description = "Fragrant organic garden strawberries, sweet and vibrant red.",
                imageResName = "strawberry",
                stockQuantity = 18,
                minStockThreshold = 10,
                isFlashDeal = false,
                isBestSelling = false
            ),
            Product(
                id = 9,
                name = "Kiwi",
                category = "Fruits",
                unit = "500g",
                price = 1.85,
                originalPrice = 2.20,
                discountPercent = 16,
                rating = 4.7,
                ratingCount = 95,
                description = "Nutrient-dense green kiwifruit with tender edible seeds and tangy sweet flesh.",
                imageResName = "kiwi",
                stockQuantity = 40,
                minStockThreshold = 10,
                isFlashDeal = false,
                isBestSelling = false
            ),
            Product(
                id = 10,
                name = "Fresh Broccoli",
                category = "Vegetables",
                unit = "500g",
                price = 1.65,
                originalPrice = 2.10,
                discountPercent = 21,
                rating = 4.7,
                ratingCount = 145,
                description = "Crisp deep-green organic broccoli crowns directly harvested from local organic growers.",
                imageResName = "broccoli",
                stockQuantity = 30,
                minStockThreshold = 10,
                isFlashDeal = true,
                isBestSelling = true
            ),
            Product(
                id = 11,
                name = "Organic Carrot",
                category = "Vegetables",
                unit = "1kg",
                price = 1.40,
                originalPrice = 1.80,
                discountPercent = 22,
                rating = 4.6,
                ratingCount = 88,
                description = "Sweet farm-fresh orange carrots, crisp and rich in beta-carotene.",
                imageResName = "carrot",
                stockQuantity = 70,
                minStockThreshold = 15,
                isFlashDeal = false,
                isBestSelling = true
            ),
            Product(
                id = 12,
                name = "Organic Tomato",
                category = "Vegetables",
                unit = "1kg",
                price = 2.10,
                originalPrice = 2.60,
                discountPercent = 19,
                rating = 4.8,
                ratingCount = 210,
                description = "Aromatic vine-ripened red tomatoes, perfect for fresh salads and homemade sauces.",
                imageResName = "tomato",
                stockQuantity = 55,
                minStockThreshold = 12,
                isFlashDeal = false,
                isBestSelling = true
            ),
            Product(
                id = 13,
                name = "Natural Juice",
                category = "Beverages",
                unit = "1L",
                price = 3.20,
                originalPrice = 3.90,
                discountPercent = 18,
                rating = 4.9,
                ratingCount = 310,
                description = "100% pure cold-pressed natural orange juice, 100% pure fruit with zero added sugar or preservatives.",
                imageResName = "juice",
                stockQuantity = 28,
                minStockThreshold = 10,
                isFlashDeal = true,
                isBestSelling = true
            )
        )

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "hortifruti_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance

                // Immediately ensure database is populated without waiting for callbacks
                scope.launch(Dispatchers.IO) {
                    try {
                        if (instance.productDao().count() == 0) {
                            populateInitialData(instance)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                instance
            }
        }

        suspend fun populateInitialData(database: AppDatabase) {
            val productDao = database.productDao()
            val cartDao = database.cartDao()
            val movementDao = database.stockMovementDao()

            if (productDao.count() == 0) {
                productDao.insertAll(defaultProducts)

                // Initial cart items matching Mockup Screen 5:
                // Banana 1x ($1.25), Milk Full Cream 1x ($1.80), Whole Wheat Bread 1x ($2.25)
                cartDao.insertOrUpdate(CartEntity(2, "Banana", "1kg", 1.25, "banana", 1))
                cartDao.insertOrUpdate(CartEntity(5, "Milk Full Cream", "1L", 1.80, "milk", 1))
                cartDao.insertOrUpdate(CartEntity(6, "Whole Wheat Bread", "400g", 2.25, "bread", 1))

                // Initial stock movement logs
                movementDao.insertMovement(
                    StockMovement(
                        productId = 1,
                        productName = "Fresh Avocado",
                        type = "REPOSICAO",
                        quantityChanged = 45,
                        newStockQuantity = 45,
                        note = "Entrada de lote fresco Fazenda Santa Helena"
                    )
                )
                movementDao.insertMovement(
                    StockMovement(
                        productId = 2,
                        productName = "Banana",
                        type = "REPOSICAO",
                        quantityChanged = 120,
                        newStockQuantity = 120,
                        note = "Colheita semanal orgânica"
                    )
                )
            }
        }
    }
}
