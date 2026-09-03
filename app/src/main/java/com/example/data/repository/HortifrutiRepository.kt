package com.example.data.repository

import androidx.room.withTransaction
import com.example.data.local.AppDatabase
import com.example.data.model.CartEntity
import com.example.data.model.Order
import com.example.data.model.Product
import com.example.data.model.StockMovement
import kotlinx.coroutines.flow.Flow
import kotlin.random.Random

class HortifrutiRepository(private val database: AppDatabase) {
    private val productDao = database.productDao()
    private val cartDao = database.cartDao()
    private val orderDao = database.orderDao()
    private val stockMovementDao = database.stockMovementDao()

    val allProducts: Flow<List<Product>> = productDao.getAllProducts()
    val flashDeals: Flow<List<Product>> = productDao.getFlashDeals()
    val bestSelling: Flow<List<Product>> = productDao.getBestSelling()
    val cartItems: Flow<List<CartEntity>> = cartDao.getCartItems()
    val allOrders: Flow<List<Order>> = orderDao.getAllOrders()
    val stockMovements: Flow<List<StockMovement>> = stockMovementDao.getAllMovements()
    val lowStockProducts: Flow<List<Product>> = productDao.getLowStockProducts()

    fun getProductById(id: Long): Flow<Product?> = productDao.getProductById(id)
    fun getProductsByCategory(category: String): Flow<List<Product>> = productDao.getProductsByCategory(category)

    suspend fun addToCart(product: Product, quantityToAdd: Int = 1) {
        val existing = database.cartDao().getCartItems()
        // We'll insert or update
        cartDao.insertOrUpdate(
            CartEntity(
                productId = product.id,
                productName = product.name,
                productUnit = product.unit,
                productPrice = product.price,
                imageResName = product.imageResName,
                quantity = quantityToAdd
            )
        )
    }

    suspend fun updateCartItemQuantity(productId: Long, newQuantity: Int) {
        if (newQuantity <= 0) {
            cartDao.removeItem(productId)
        } else {
            val product = productDao.getProductDirect(productId)
            if (product != null) {
                cartDao.insertOrUpdate(
                    CartEntity(
                        productId = product.id,
                        productName = product.name,
                        productUnit = product.unit,
                        productPrice = product.price,
                        imageResName = product.imageResName,
                        quantity = newQuantity
                    )
                )
            }
        }
    }

    suspend fun removeCartItem(productId: Long) {
        cartDao.removeItem(productId)
    }

    suspend fun clearCart() {
        cartDao.clearCart()
    }

    /**
     * Executes order checkout with atomic real-time inventory deduction.
     */
    suspend fun placeOrder(
        cartList: List<CartEntity>,
        deliveryAddress: String,
        deliveryMethod: String,
        deliveryFee: Double,
        paymentMethod: String,
        discount: Double = 0.0
    ): Order {
        val totalProductsPrice = cartList.sumOf { it.productPrice * it.quantity }
        val finalTotal = (totalProductsPrice - discount + deliveryFee).coerceAtLeast(0.0)
        val orderCode = "#HF-" + Random.nextInt(1000, 9999)
        val summaryText = cartList.joinToString(", ") { "${it.quantity}x ${it.productName}" }

        val order = Order(
            orderNumber = orderCode,
            timestamp = System.currentTimeMillis(),
            itemsSummary = summaryText,
            totalAmount = finalTotal,
            deliveryFee = deliveryFee,
            deliveryMethod = deliveryMethod,
            paymentMethod = paymentMethod,
            deliveryAddress = deliveryAddress,
            status = "Confirmado & Em Preparação"
        )

        database.withTransaction {
            // 1. Decrement real-time inventory for each item
            for (cartItem in cartList) {
                val product = productDao.getProductDirect(cartItem.productId)
                if (product != null) {
                    val updatedStock = (product.stockQuantity - cartItem.quantity).coerceAtLeast(0)
                    productDao.updateStock(product.id, updatedStock)

                    // Log movement
                    stockMovementDao.insertMovement(
                        StockMovement(
                            productId = product.id,
                            productName = product.name,
                            type = "VENDA",
                            quantityChanged = -cartItem.quantity,
                            newStockQuantity = updatedStock,
                            note = "Venda no Pedido $orderCode"
                        )
                    )
                }
            }

            // 2. Insert order
            orderDao.insertOrder(order)

            // 3. Clear cart
            cartDao.clearCart()
        }

        return order
    }

    /**
     * Real-time manual stock update by store manager.
     */
    suspend fun updateProductStock(productId: Long, newStockQuantity: Int, note: String = "Ajuste de inventário") {
        val product = productDao.getProductDirect(productId) ?: return
        val change = newStockQuantity - product.stockQuantity
        if (change == 0) return

        productDao.updateStock(productId, newStockQuantity.coerceAtLeast(0))

        stockMovementDao.insertMovement(
            StockMovement(
                productId = product.id,
                productName = product.name,
                type = if (change > 0) "REPOSICAO" else "AJUSTE_MANUAL",
                quantityChanged = change,
                newStockQuantity = newStockQuantity.coerceAtLeast(0),
                note = note
            )
        )
    }

    /**
     * Quick adjust by delta (+5, -1, etc.)
     */
    suspend fun adjustProductStockBy(productId: Long, delta: Int, note: String = "Ajuste rápido") {
        val product = productDao.getProductDirect(productId) ?: return
        val updated = (product.stockQuantity + delta).coerceAtLeast(0)
        updateProductStock(productId, updated, note)
    }

    /**
     * Batch restock simulation (simulates arrival of fresh supplier trucks)
     */
    suspend fun simulateFreshBatchRestock() {
        database.withTransaction {
            val all = productDao.getProductDirect(1) // check if exists
            val lowItems = listOf(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L, 12L, 13L)
            for (id in lowItems) {
                val prod = productDao.getProductDirect(id)
                if (prod != null) {
                    val incoming = Random.nextInt(15, 40)
                    val newStock = prod.stockQuantity + incoming
                    productDao.updateStock(id, newStock)
                    stockMovementDao.insertMovement(
                        StockMovement(
                            productId = prod.id,
                            productName = prod.name,
                            type = "REPOSICAO",
                            quantityChanged = incoming,
                            newStockQuantity = newStock,
                            note = "Recebimento de Lote Hortifruti Matinal"
                        )
                    )
                }
            }
        }
    }
}
