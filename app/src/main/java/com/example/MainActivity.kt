package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.FilterBottomSheet
import com.example.ui.components.HortifrutiBottomBar
import com.example.ui.screens.CartScreen
import com.example.ui.screens.CategoriesScreen
import com.example.ui.screens.CheckoutScreen
import com.example.ui.screens.DriverChatScreen
import com.example.ui.screens.FavoritesScreen
import com.example.ui.screens.HelpCenterScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.OrdersScreen
import com.example.ui.screens.OrderTrackingScreen
import com.example.ui.screens.PhoneLoginScreen
import com.example.ui.screens.ProductDetailScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.StockManagementScreen
import com.example.ui.screens.VouchersScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.HortifrutiViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val viewModel: HortifrutiViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()
            MyApplicationTheme(darkTheme = isDarkMode) {
                HortifrutiApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun HortifrutiApp(viewModel: HortifrutiViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val allProducts by viewModel.allProducts.collectAsStateWithLifecycle()
    val flashDeals by viewModel.flashDeals.collectAsStateWithLifecycle()
    val bestSelling by viewModel.bestSelling.collectAsStateWithLifecycle()
    val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
    val cartSummary by viewModel.cartSummary.collectAsStateWithLifecycle()
    val allOrders by viewModel.allOrders.collectAsStateWithLifecycle()
    val stockMovements by viewModel.stockMovements.collectAsStateWithLifecycle()
    val flashCountdownSeconds by viewModel.flashDealsSeconds.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val promoCodeInput by viewModel.promoCodeInput.collectAsStateWithLifecycle()
    val promoMessage by viewModel.promoMessage.collectAsStateWithLifecycle()
    val appliedPromoCode by viewModel.appliedPromoCode.collectAsStateWithLifecycle()
    val deliveryAddress by viewModel.deliveryAddress.collectAsStateWithLifecycle()
    val selectedDeliveryMethod by viewModel.selectedDeliveryMethod.collectAsStateWithLifecycle()
    val selectedPaymentMethod by viewModel.selectedPaymentMethod.collectAsStateWithLifecycle()
    val isPlacingOrder by viewModel.isPlacingOrder.collectAsStateWithLifecycle()
    val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()
    val userName by viewModel.userName.collectAsStateWithLifecycle()
    val userPhone by viewModel.userPhone.collectAsStateWithLifecycle()
    val userEmail by viewModel.userEmail.collectAsStateWithLifecycle()
    val favoriteProductIds by viewModel.favoriteProductIds.collectAsStateWithLifecycle()
    val isFilterSheetOpen by viewModel.isFilterSheetOpen.collectAsStateWithLifecycle()
    val filterMinPrice by viewModel.filterMinPrice.collectAsStateWithLifecycle()
    val filterMaxPrice by viewModel.filterMaxPrice.collectAsStateWithLifecycle()
    val filterOnlyInStock by viewModel.filterOnlyInStock.collectAsStateWithLifecycle()
    val filterMinRating by viewModel.filterMinRating.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Handle system back navigation
    BackHandler(enabled = currentScreen != AppScreen.Home) {
        if (!viewModel.navigateBack()) {
            // Already at root
        }
    }

    // Screens that show bottom navigation bar
    val showBottomBar = when (currentScreen) {
        AppScreen.Home,
        AppScreen.Categories,
        AppScreen.Orders,
        AppScreen.StockManagement,
        AppScreen.Profile -> true
        else -> false
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (showBottomBar) {
                HortifrutiBottomBar(
                    currentScreen = currentScreen,
                    cartItemCount = cartSummary.totalItemsCount,
                    onNavigate = { screen -> viewModel.navigateTo(screen) }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = if (showBottomBar) innerPadding.calculateBottomPadding() else androidx.compose.ui.unit.Dp(0f))
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "ScreenTransition"
            ) { targetScreen ->
                when (targetScreen) {
                    AppScreen.Onboarding -> {
                        OnboardingScreen(
                            onGetStarted = { viewModel.navigateTo(AppScreen.Home) }
                        )
                    }

                    AppScreen.PhoneLogin -> {
                        PhoneLoginScreen(
                            currentPhone = userPhone,
                            onPhoneConfirmed = { newPhone ->
                                viewModel.userPhone.value = newPhone
                                viewModel.navigateBack()
                            },
                            onBack = { viewModel.navigateBack() },
                            onSkip = { viewModel.navigateBack() }
                        )
                    }

                    AppScreen.Home -> {
                        HomeScreen(
                            products = allProducts,
                            flashDeals = flashDeals,
                            bestSelling = bestSelling,
                            searchQuery = searchQuery,
                            onSearchChange = { viewModel.searchQuery.value = it },
                            selectedCategory = selectedCategory,
                            onSelectCategory = { viewModel.selectedCategory.value = it },
                            flashCountdownSeconds = flashCountdownSeconds,
                            deliveryAddress = deliveryAddress,
                            favoriteProductIds = favoriteProductIds,
                            onToggleFavorite = { viewModel.toggleFavorite(it) },
                            onOpenFilter = { viewModel.isFilterSheetOpen.value = true },
                            onOpenVouchers = { viewModel.navigateTo(AppScreen.Vouchers) },
                            onOpenFavorites = { viewModel.navigateTo(AppScreen.Favorites) },
                            onProductClick = { productId -> viewModel.openProductDetail(productId) },
                            onAddToCart = { product ->
                                viewModel.addToCart(product, 1)
                                scope.launch {
                                    snackbarHostState.showSnackbar("${product.name} adicionado ao carrinho!")
                                }
                            },
                            onOpenCategories = { viewModel.navigateTo(AppScreen.Categories) },
                            onOpenStockManagement = { viewModel.navigateTo(AppScreen.StockManagement) }
                        )
                    }

                    AppScreen.Categories -> {
                        CategoriesScreen(
                            onBack = { viewModel.navigateBack() },
                            onSelectCategory = { categoryId ->
                                viewModel.selectedCategory.value = categoryId
                                viewModel.navigateTo(AppScreen.Home)
                            }
                        )
                    }

                    is AppScreen.ProductDetail -> {
                        val product = allProducts.find { it.id == targetScreen.productId }
                        ProductDetailScreen(
                            product = product,
                            recommendedProducts = allProducts,
                            isFavorite = product != null && favoriteProductIds.contains(product.id),
                            onToggleFavorite = {
                                if (product != null) viewModel.toggleFavorite(product.id)
                            },
                            onBack = { viewModel.navigateBack() },
                            onAddToCart = { prod, qty ->
                                viewModel.addToCart(prod, qty)
                                scope.launch {
                                    snackbarHostState.showSnackbar("$qty x ${prod.name} adicionado ao carrinho!")
                                }
                            },
                            onProductClick = { prodId -> viewModel.openProductDetail(prodId) }
                        )
                    }

                    AppScreen.Cart -> {
                        CartScreen(
                            cartItems = cartItems,
                            cartSummary = cartSummary,
                            promoCodeInput = promoCodeInput,
                            onPromoCodeChange = { viewModel.promoCodeInput.value = it },
                            onApplyPromo = { viewModel.applyPromoCode() },
                            promoMessage = promoMessage,
                            onBack = { viewModel.navigateBack() },
                            onUpdateQuantity = { prodId, newQty ->
                                viewModel.updateCartQuantity(prodId, newQty)
                            },
                            onClearCart = { viewModel.clearCart() },
                            onProceedToCheckout = { viewModel.navigateTo(AppScreen.Checkout) }
                        )
                    }

                    AppScreen.Checkout -> {
                        CheckoutScreen(
                            cartSummary = cartSummary,
                            deliveryAddress = deliveryAddress,
                            onUpdateAddress = { viewModel.deliveryAddress.value = it },
                            selectedDeliveryMethod = selectedDeliveryMethod,
                            onSelectDeliveryMethod = { viewModel.selectedDeliveryMethod.value = it },
                            selectedPaymentMethod = selectedPaymentMethod,
                            onSelectPaymentMethod = { viewModel.selectedPaymentMethod.value = it },
                            isPlacingOrder = isPlacingOrder,
                            onPlaceOrder = { onSuccess ->
                                viewModel.placeOrder(onSuccess)
                            },
                            onBack = { viewModel.navigateBack() },
                            onViewOrders = { viewModel.navigateTo(AppScreen.Orders) },
                            onGoHome = { viewModel.navigateTo(AppScreen.Home) }
                        )
                    }

                    AppScreen.StockManagement -> {
                        StockManagementScreen(
                            products = allProducts,
                            stockMovements = stockMovements,
                            onAdjustStock = { prodId, delta ->
                                viewModel.adjustStock(prodId, delta)
                            },
                            onSetStock = { prodId, newStock ->
                                viewModel.setStock(prodId, newStock)
                            },
                            onSimulateBatch = {
                                viewModel.simulateRestockBatch()
                                scope.launch {
                                    snackbarHostState.showSnackbar("Novo lote de hortifruti recebido com sucesso!")
                                }
                            },
                            onBack = { viewModel.navigateBack() }
                        )
                    }

                    AppScreen.Orders -> {
                        OrdersScreen(
                            orders = allOrders,
                            onTrackOrder = { order -> viewModel.trackOrder(order.id) },
                            onBack = { viewModel.navigateBack() },
                            onStartShopping = { viewModel.navigateTo(AppScreen.Home) }
                        )
                    }

                    is AppScreen.OrderTracking -> {
                        val order = allOrders.find { it.id == targetScreen.orderId } ?: allOrders.firstOrNull()
                        OrderTrackingScreen(
                            order = order,
                            onBack = { viewModel.navigateBack() },
                            onOpenChat = { orderId -> viewModel.openDriverChat(orderId) },
                            onCallDriver = {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Ligando para o entregador Carlos Mendes...")
                                }
                            }
                        )
                    }

                    is AppScreen.DriverChat -> {
                        DriverChatScreen(
                            orderId = targetScreen.orderId,
                            onBack = { viewModel.navigateBack() },
                            onCallDriver = {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Ligando para Carlos Mendes...")
                                }
                            }
                        )
                    }

                    AppScreen.Favorites -> {
                        val favoriteProducts = remember(allProducts, favoriteProductIds) {
                            allProducts.filter { favoriteProductIds.contains(it.id) }
                        }
                        FavoritesScreen(
                            favoriteProducts = favoriteProducts,
                            onProductClick = { prodId -> viewModel.openProductDetail(prodId) },
                            onToggleFavorite = { prodId -> viewModel.toggleFavorite(prodId) },
                            onAddToCart = { prod ->
                                viewModel.addToCart(prod, 1)
                                scope.launch {
                                    snackbarHostState.showSnackbar("${prod.name} adicionado ao carrinho!")
                                }
                            },
                            onBack = { viewModel.navigateBack() },
                            onStartShopping = { viewModel.navigateTo(AppScreen.Home) }
                        )
                    }

                    AppScreen.Vouchers -> {
                        VouchersScreen(
                            appliedCode = appliedPromoCode,
                            onApplyVoucher = { code, rate, message ->
                                viewModel.applyVoucherDirect(code, rate, message)
                                scope.launch {
                                    snackbarHostState.showSnackbar("Cupom $code aplicado com sucesso!")
                                }
                            },
                            onBack = { viewModel.navigateBack() }
                        )
                    }

                    AppScreen.Profile -> {
                        ProfileScreen(
                            userName = userName,
                            userPhone = userPhone,
                            userEmail = userEmail,
                            favoritesCount = favoriteProductIds.size,
                            isDarkMode = isDarkMode,
                            onToggleDarkMode = { viewModel.toggleDarkMode() },
                            onOpenFavorites = { viewModel.navigateTo(AppScreen.Favorites) },
                            onOpenVouchers = { viewModel.navigateTo(AppScreen.Vouchers) },
                            onOpenStock = { viewModel.navigateTo(AppScreen.StockManagement) },
                            onOpenHelp = { viewModel.navigateTo(AppScreen.HelpCenter) },
                            onOpenLogin = { viewModel.navigateTo(AppScreen.PhoneLogin) },
                            onBack = { viewModel.navigateBack() }
                        )
                    }

                    AppScreen.HelpCenter -> {
                        HelpCenterScreen(
                            onBack = { viewModel.navigateBack() }
                        )
                    }
                }
            }

            // Filter Bottom Sheet Modal
            FilterBottomSheet(
                isOpen = isFilterSheetOpen,
                minPrice = filterMinPrice,
                maxPrice = filterMaxPrice,
                onlyInStock = filterOnlyInStock,
                minRating = filterMinRating,
                selectedCategory = selectedCategory,
                onCategorySelect = { viewModel.selectedCategory.value = it },
                onApply = { min, max, stockOnly, rating ->
                    viewModel.filterMinPrice.value = min
                    viewModel.filterMaxPrice.value = max
                    viewModel.filterOnlyInStock.value = stockOnly
                    viewModel.filterMinRating.value = rating
                    viewModel.isFilterSheetOpen.value = false
                },
                onReset = {
                    viewModel.resetFilters()
                    viewModel.isFilterSheetOpen.value = false
                },
                onDismiss = { viewModel.isFilterSheetOpen.value = false }
            )
        }
    }
}
