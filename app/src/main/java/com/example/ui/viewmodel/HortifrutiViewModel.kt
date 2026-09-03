package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.CartEntity
import com.example.data.model.Order
import com.example.data.model.Product
import com.example.data.model.StockMovement
import com.example.data.repository.HortifrutiRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface AppScreen {
    data object Onboarding : AppScreen
    data object PhoneLogin : AppScreen
    data object Home : AppScreen
    data object Categories : AppScreen
    data class ProductDetail(val productId: Long) : AppScreen
    data object Cart : AppScreen
    data object Checkout : AppScreen
    data object StockManagement : AppScreen
    data object Orders : AppScreen
    data class OrderTracking(val orderId: Long) : AppScreen
    data class DriverChat(val orderId: Long) : AppScreen
    data object Favorites : AppScreen
    data object Vouchers : AppScreen
    data object Profile : AppScreen
    data object HelpCenter : AppScreen
}

data class CartSummary(
    val subtotal: Double = 0.0,
    val deliveryFee: Double = 1.00,
    val discount: Double = 0.0,
    val total: Double = 0.0,
    val totalItemsCount: Int = 0
)

class HortifrutiViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application, viewModelScope)
    private val repository = HortifrutiRepository(database)

    // Current Screen
    private val _currentScreen = MutableStateFlow<AppScreen>(AppScreen.Home)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    // Backstack history for back navigation
    private val backStack = mutableListOf<AppScreen>()

    // Flash deals countdown timer (seconds remaining: e.g. 2h 45m 30s = 9930 seconds)
    private val _flashDealsSeconds = MutableStateFlow(9930L)
    val flashDealsSeconds: StateFlow<Long> = _flashDealsSeconds.asStateFlow()

    // Search & Filter
    val searchQuery = MutableStateFlow("")
    val selectedCategory = MutableStateFlow("All")

    // Database flows with instant initial in-memory fallback to avoid blank preview state
    val allProducts: StateFlow<List<Product>> = repository.allProducts
        .stateIn(viewModelScope, SharingStarted.Eagerly, AppDatabase.defaultProducts)

    val flashDeals: StateFlow<List<Product>> = repository.flashDeals
        .stateIn(viewModelScope, SharingStarted.Eagerly, AppDatabase.defaultProducts.filter { it.isFlashDeal })

    val bestSelling: StateFlow<List<Product>> = repository.bestSelling
        .stateIn(viewModelScope, SharingStarted.Eagerly, AppDatabase.defaultProducts.filter { it.isBestSelling })

    val cartItems: StateFlow<List<CartEntity>> = repository.cartItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allOrders: StateFlow<List<Order>> = repository.allOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val stockMovements: StateFlow<List<StockMovement>> = repository.stockMovements
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val lowStockProducts: StateFlow<List<Product>> = repository.lowStockProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Promo Code
    val promoCodeInput = MutableStateFlow("")
    val appliedPromoCode = MutableStateFlow<String?>(null)
    val promoDiscountRate = MutableStateFlow(0.0)
    val promoMessage = MutableStateFlow<String?>(null)

    // Checkout Details
    val deliveryAddress = MutableStateFlow("Meu Apartamento, Rua das Flores, 123 - Apto 42")
    val selectedDeliveryMethod = MutableStateFlow("Express Delivery") // Express or Free
    val selectedPaymentMethod = MutableStateFlow("Pix") // Pix, Visa, Boleto, Dinheiro
    val isPlacingOrder = MutableStateFlow(false)
    val lastCompletedOrder = MutableStateFlow<Order?>(null)

    // Dark Mode Theme toggle (Full Light / Dark mode support)
    val isDarkMode = MutableStateFlow(false)

    // User Profile
    val userName = MutableStateFlow("Amelia Barlow")
    val userPhone = MutableStateFlow("+55 (11) 98765-4321")
    val userEmail = MutableStateFlow("amelia.barlow@email.com")

    // Favorites state
    val favoriteProductIds = MutableStateFlow<Set<Long>>(setOf(1L, 3L, 5L))

    // Advanced Product Filters
    val isFilterSheetOpen = MutableStateFlow(false)
    val filterMinPrice = MutableStateFlow(0f)
    val filterMaxPrice = MutableStateFlow(50f)
    val filterOnlyInStock = MutableStateFlow(false)
    val filterMinRating = MutableStateFlow(0f)

    // Active Tracking Order ID
    val activeTrackingOrderId = MutableStateFlow<Long?>(null)

    // Product Detail
    val selectedGalleryIndex = MutableStateFlow(0)
    val detailProductQuantity = MutableStateFlow(1)

    // Stock Management filter
    val stockFilter = MutableStateFlow("Todos") // Todos, Baixo Estoque, Esgotado, Frutas, Legumes

    // Cart calculations (with automatic 5% Pix discount)
    val cartSummary: StateFlow<CartSummary> = combine(
        cartItems,
        promoDiscountRate,
        selectedDeliveryMethod,
        selectedPaymentMethod
    ) { items, discountRate, deliveryType, paymentMethod ->
        val subtotal = items.sumOf { it.productPrice * it.quantity }
        val count = items.sumOf { it.quantity }
        val deliveryFee = if (deliveryType == "Free Delivery" || subtotal >= 30.0) 0.0 else 1.00
        val pixBonus = if (paymentMethod == "Pix") 0.05 else 0.0
        val totalDiscountRate = (discountRate + pixBonus).coerceAtMost(0.50)
        val discount = subtotal * totalDiscountRate
        val total = (subtotal - discount + deliveryFee).coerceAtLeast(0.0)
        CartSummary(
            subtotal = subtotal,
            deliveryFee = deliveryFee,
            discount = discount,
            total = total,
            totalItemsCount = count
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CartSummary())

    init {
        // Guarantee database is populated with initial catalog
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                if (database.productDao().count() == 0) {
                    AppDatabase.populateInitialData(database)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Countdown timer loop for Flash Deals
        viewModelScope.launch {
            while (true) {
                delay(1000L)
                if (_flashDealsSeconds.value > 0) {
                    _flashDealsSeconds.value -= 1
                } else {
                    _flashDealsSeconds.value = 10800L // reset 3 hours
                }
            }
        }
    }

    fun navigateTo(screen: AppScreen) {
        if (_currentScreen.value != screen) {
            backStack.add(_currentScreen.value)
            _currentScreen.value = screen
        }
    }

    fun navigateBack(): Boolean {
        return if (backStack.isNotEmpty()) {
            _currentScreen.value = backStack.removeAt(backStack.size - 1)
            true
        } else {
            false
        }
    }

    fun openProductDetail(productId: Long) {
        selectedGalleryIndex.value = 0
        detailProductQuantity.value = 1
        navigateTo(AppScreen.ProductDetail(productId))
    }

    fun addToCart(product: Product, quantity: Int = 1) {
        if (product.stockQuantity <= 0) return
        viewModelScope.launch {
            val existingItem = cartItems.value.find { it.productId == product.id }
            val currentQty = existingItem?.quantity ?: 0
            val newQty = (currentQty + quantity).coerceAtMost(product.stockQuantity)
            if (newQty > currentQty) {
                repository.updateCartItemQuantity(product.id, newQty)
            }
        }
    }

    fun updateCartQuantity(productId: Long, newQuantity: Int) {
        viewModelScope.launch {
            val prod = allProducts.value.find { it.id == productId }
            val maxStock = prod?.stockQuantity ?: 999
            val clamped = newQuantity.coerceIn(0, maxStock)
            repository.updateCartItemQuantity(productId, clamped)
        }
    }

    fun removeCartItem(productId: Long) {
        viewModelScope.launch {
            repository.removeCartItem(productId)
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            repository.clearCart()
        }
    }

    fun applyPromoCode() {
        val code = promoCodeInput.value.trim().uppercase()
        when (code) {
            "FRESH10", "HORTI10" -> {
                appliedPromoCode.value = code
                promoDiscountRate.value = 0.10
                promoMessage.value = "Cupom de 10% aplicado com sucesso!"
            }
            "FRESH20", "HORTI20" -> {
                appliedPromoCode.value = code
                promoDiscountRate.value = 0.20
                promoMessage.value = "Super desconto de 20% aplicado!"
            }
            else -> {
                promoMessage.value = "Cupom inválido. Tente FRESH10 ou FRESH20"
            }
        }
    }

    fun placeOrder(onSuccess: (Order) -> Unit) {
        val currentItems = cartItems.value
        if (currentItems.isEmpty()) return

        isPlacingOrder.value = true
        viewModelScope.launch {
            // Simulated network payment processing
            delay(1200L)
            val summary = cartSummary.value
            val order = repository.placeOrder(
                cartList = currentItems,
                deliveryAddress = deliveryAddress.value,
                deliveryMethod = selectedDeliveryMethod.value,
                deliveryFee = summary.deliveryFee,
                paymentMethod = when (selectedPaymentMethod.value) {
                    "Pix" -> "Pix Instantâneo (5% off)"
                    "Visa" -> "Cartão de Crédito (•••• 4242)"
                    "Boleto" -> "Boleto Bancário Digital"
                    "Dinheiro" -> "Dinheiro na Entrega"
                    else -> "Cartão de Débito / Crédito"
                },
                discount = summary.discount
            )
            lastCompletedOrder.value = order
            isPlacingOrder.value = false
            onSuccess(order)
        }
    }

    // Real-time stock management operations
    fun adjustStock(productId: Long, delta: Int) {
        viewModelScope.launch {
            repository.adjustProductStockBy(productId, delta, "Ajuste manual de estoque")
        }
    }

    fun setStock(productId: Long, newStock: Int, note: String = "Contagem física de inventário") {
        viewModelScope.launch {
            repository.updateProductStock(productId, newStock, note)
        }
    }

    fun simulateRestockBatch() {
        viewModelScope.launch {
            repository.simulateFreshBatchRestock()
        }
    }

    fun toggleFavorite(productId: Long) {
        val current = favoriteProductIds.value
        if (current.contains(productId)) {
            favoriteProductIds.value = current - productId
        } else {
            favoriteProductIds.value = current + productId
        }
    }

    fun toggleDarkMode() {
        isDarkMode.value = !isDarkMode.value
    }

    fun applyVoucherDirect(code: String, rate: Double, message: String) {
        appliedPromoCode.value = code
        promoCodeInput.value = code
        promoDiscountRate.value = rate
        promoMessage.value = message
    }

    fun trackOrder(orderId: Long) {
        activeTrackingOrderId.value = orderId
        navigateTo(AppScreen.OrderTracking(orderId))
    }

    fun openDriverChat(orderId: Long) {
        activeTrackingOrderId.value = orderId
        navigateTo(AppScreen.DriverChat(orderId))
    }

    fun resetFilters() {
        filterMinPrice.value = 0f
        filterMaxPrice.value = 50f
        filterOnlyInStock.value = false
        filterMinRating.value = 0f
    }
}
