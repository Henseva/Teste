package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import com.example.data.local.AppDatabase
import com.example.ui.theme.MyApplicationTheme
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Product
import com.example.ui.components.ProductVisual
import com.example.ui.theme.FreshGreenDark
import com.example.ui.theme.FreshGreenPrimary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun HomeScreen(
    products: List<Product>,
    flashDeals: List<Product>,
    bestSelling: List<Product>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    selectedCategory: String,
    onSelectCategory: (String) -> Unit,
    flashCountdownSeconds: Long,
    deliveryAddress: String,
    onProductClick: (Long) -> Unit,
    onAddToCart: (Product) -> Unit,
    onOpenCategories: () -> Unit,
    onOpenStockManagement: () -> Unit,
    onOpenFilter: () -> Unit = {},
    onOpenVouchers: () -> Unit = {},
    onOpenFavorites: () -> Unit = {},
    favoriteProductIds: Set<Long> = emptySet(),
    onToggleFavorite: (Long) -> Unit = {}
) {
    val filteredProducts = remember(products, searchQuery, selectedCategory) {
        products.filter { product ->
            val matchesQuery = searchQuery.isBlank() ||
                product.name.contains(searchQuery, ignoreCase = true) ||
                product.category.contains(searchQuery, ignoreCase = true)
            val matchesCat = matchesCategoryFilter(product.category, selectedCategory)
            matchesQuery && matchesCat
        }
    }

    // Format countdown timer (HH : MM : SS)
    val hours = flashCountdownSeconds / 3600
    val minutes = (flashCountdownSeconds % 3600) / 60
    val seconds = flashCountdownSeconds % 60
    val formattedTime = String.format(Locale.US, "%02d : %02d : %02d", hours, minutes, seconds)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // 1. Top Header: Location + Notification Bell + Stock Quick Link
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Location selector
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFD1FADF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Location",
                            tint = FreshGreenPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = "Entregar em",
                            fontSize = 11.sp,
                            color = Color(0xFF6B7280),
                            fontWeight = FontWeight.Medium
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = deliveryAddress.ifBlank { "Rua das Flores, 123" },
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.widthIn(max = 140.dp)
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // Actions: Vouchers, Favorites & Notification Bell
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Vouchers shortcut
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFEF3C7))
                            .clickable(onClick = onOpenVouchers)
                            .testTag("vouchers_shortcut_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ConfirmationNumber,
                            contentDescription = "Cupons",
                            tint = Color(0xFFD97706),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Favorites shortcut
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFEE2E2))
                            .clickable(onClick = onOpenFavorites)
                            .testTag("fav_shortcut_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Favoritos",
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Stock Management Shortcut Button
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE0F2FE))
                            .clickable(onClick = onOpenStockManagement)
                            .testTag("stock_shortcut_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Inventory2,
                            contentDescription = "Gerenciamento de Estoque",
                            tint = Color(0xFF0284C7),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // 2. Search Bar + Filter Icon
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("search_text_field"),
                    placeholder = {
                        Text(
                            text = "Search for products...",
                            fontSize = 14.sp,
                            color = Color(0xFF9CA3AF)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = Color(0xFF9CA3AF),
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF3F4F6),
                        unfocusedContainerColor = Color(0xFFF3F4F6),
                        disabledContainerColor = Color(0xFFF3F4F6),
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.width(12.dp))

                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(FreshGreenPrimary)
                        .clickable { onOpenFilter() }
                        .testTag("filter_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Categories / Filters",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        // 3. Hero Promo Banner: "Fresh Vegetables Up to 30% Off" with animated organic gradient sheen
        item {
            val bannerTransition = rememberInfiniteTransition(label = "BannerTransition")
            val gradientOffset by bannerTransition.animateFloat(
                initialValue = 0f,
                targetValue = 600f,
                animationSpec = infiniteRepeatable(
                    animation = tween(4000, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "GradientOffset"
            )
            val basketFloat by bannerTransition.animateFloat(
                initialValue = -3f,
                targetValue = 3f,
                animationSpec = infiniteRepeatable(
                    animation = tween(2000, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "BasketFloat"
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .clip(RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFFDCFCE7), Color(0xFFFEF3C7), Color(0xFFFFEDD5)),
                                start = Offset(gradientOffset, 0f),
                                end = Offset(gradientOffset + 500f, 400f)
                            )
                        )
                        .padding(horizontal = 20.dp, vertical = 18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(FreshGreenPrimary.copy(alpha = 0.15f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "SUPER OFERTA",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = FreshGreenDark
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Hortifruti Fresco",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF065F46)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Até 30% de Desconto",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF111827)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = onOpenCategories,
                                modifier = Modifier
                                    .height(34.dp)
                                    .testTag("shop_now_banner_button"),
                                shape = RoundedCornerShape(17.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = FreshGreenPrimary,
                                    contentColor = Color.White
                                ),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp)
                            ) {
                                Text(
                                    text = "Aproveitar Agora",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Image(
                            painter = painterResource(id = R.drawable.img_promo_veg),
                            contentDescription = "Cesta de Vegetais",
                            modifier = Modifier
                                .size(92.dp)
                                .graphicsLayer {
                                    translationY = basketFloat
                                }
                                .clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }

        // 4. Categories Icons Grid
        item {
            val categoriesList = listOf(
                Pair("Frutas", "🍎"),
                Pair("Legumes", "🥦"),
                Pair("Laticínios", "🥛"),
                Pair("Bebidas", "🧃"),
                Pair("Snacks", "🍿"),
                Pair("Padaria", "🍞"),
                Pair("Orgânicos", "🌱"),
                Pair("Todos", "📦")
            )

            Column(modifier = Modifier.padding(top = 8.dp)) {
                // First Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    categoriesList.take(4).forEach { (catName, emoji) ->
                        CategoryItem(
                            title = catName,
                            emoji = emoji,
                            isSelected = selectedCategory == catName,
                            onClick = {
                                onSelectCategory(if (selectedCategory == catName) "All" else catName)
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Second Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    categoriesList.drop(4).forEach { (catName, emoji) ->
                        CategoryItem(
                            title = catName,
                            emoji = emoji,
                            isSelected = selectedCategory == catName,
                            onClick = {
                                if (catName == "Todos") {
                                    onOpenCategories()
                                } else {
                                    onSelectCategory(if (selectedCategory == catName) "All" else catName)
                                }
                            }
                        )
                    }
                }
            }
        }

        // 5. Flash Deals Section with Pulsing Live Indicator and Timer
        item {
            val livePulseTransition = rememberInfiniteTransition(label = "LivePulse")
            val liveAlpha by livePulseTransition.animateFloat(
                initialValue = 0.3f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(800, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "LiveAlpha"
            )

            Column(modifier = Modifier.padding(top = 22.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Ofertas Relâmpago",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF111827)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        // Pulsing Live Indicator
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFFEE2E2))
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .graphicsLayer { alpha = liveAlpha }
                                    .clip(CircleShape)
                                    .background(Color(0xFFEF4444))
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "AO VIVO",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFB91C1C)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))
                        // Timer pill badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFFEF3C7))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = formattedTime,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFB45309)
                            )
                        }
                    }

                    TextButton(onClick = onOpenCategories) {
                        Text(
                            text = "Ver Todas",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = FreshGreenPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(if (flashDeals.isNotEmpty()) flashDeals else products.take(4), key = { it.id }) { product ->
                        ProductDealCard(
                            product = product,
                            isFavorite = favoriteProductIds.contains(product.id),
                            onToggleFavorite = { onToggleFavorite(product.id) },
                            onCardClick = { onProductClick(product.id) },
                            onAdd = { onAddToCart(product) }
                        )
                    }
                }
            }
        }

        // 6. Best Selling Section
        item {
            Column(modifier = Modifier.padding(top = 24.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Mais Vendidos da Semana",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    TextButton(onClick = onOpenCategories) {
                        Text(
                            text = "Ver Mais",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = FreshGreenPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // Best selling product items (or filtered search results)
        val isFilteringCategory = selectedCategory.isNotBlank() && selectedCategory != "All" && selectedCategory != "Todos"
        val displayList = if (searchQuery.isNotBlank() || isFilteringCategory) {
            filteredProducts
        } else if (bestSelling.isNotEmpty()) {
            bestSelling
        } else {
            products
        }

        if (displayList.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "🧺", fontSize = 44.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Nenhum produto nesta seleção",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Tente outra categoria ou limpe os filtros de busca.",
                        fontSize = 13.sp,
                        color = Color.Gray,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    androidx.compose.material3.OutlinedButton(
                        onClick = {
                            onSearchChange("")
                            onSelectCategory("Todos")
                        }
                    ) {
                        Text(text = "Ver Todos os Produtos", color = FreshGreenPrimary)
                    }
                }
            }
        }

        items(displayList, key = { it.id }) { product ->
            BestSellingProductRow(
                product = product,
                isFavorite = favoriteProductIds.contains(product.id),
                onToggleFavorite = { onToggleFavorite(product.id) },
                onCardClick = { onProductClick(product.id) },
                onAdd = { onAddToCart(product) }
            )
        }
    }
}

fun matchesCategoryFilter(productCategory: String, selectedCategory: String): Boolean {
    if (selectedCategory.isBlank() ||
        selectedCategory.equals("All", ignoreCase = true) ||
        selectedCategory.equals("Todos", ignoreCase = true)
    ) {
        return true
    }
    val sel = selectedCategory.trim().lowercase()
    val prod = productCategory.trim().lowercase()
    if (sel == prod) return true

    return when (sel) {
        "frutas", "fruits", "fruit" -> prod in listOf("fruits", "frutas", "fruit")
        "legumes", "vegetais", "vegetables", "folhas", "raízes", "raizes" -> prod in listOf("vegetables", "legumes", "vegetais", "folhas", "raízes", "raizes")
        "laticínios", "laticinios", "dairy", "leite" -> prod in listOf("dairy", "laticínios", "laticinios")
        "bebidas", "beverages", "drinks", "sucos" -> prod in listOf("beverages", "drinks", "bebidas")
        "padaria", "bakery", "pães", "paes" -> prod in listOf("bakery", "padaria")
        "snacks", "petiscos" -> prod in listOf("snacks", "petiscos")
        "orgânicos", "organicos", "organic" -> true
        else -> prod.contains(sel) || sel.contains(prod)
    }
}

@Composable
private fun CategoryItem(
    title: String,
    emoji: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.08f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "CatScale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 6.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .testTag("cat_item_$title")
    ) {
        Box(
            modifier = Modifier
                .size(62.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(if (isSelected) Color(0xFFD1FADF) else Color(0xFFF3F4F6))
                .border(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) FreshGreenPrimary else Color(0xFFE5E7EB),
                    shape = RoundedCornerShape(18.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(text = emoji, fontSize = 26.sp)
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) FreshGreenDark else Color(0xFF4B5563)
        )
    }
}

@Composable
private fun ProductDealCard(
    product: Product,
    isFavorite: Boolean = false,
    onToggleFavorite: () -> Unit = {},
    onCardClick: () -> Unit,
    onAdd: () -> Unit
) {
    var favoriteScaleTarget by remember { mutableStateOf(1f) }
    val favoriteScale by animateFloatAsState(
        targetValue = favoriteScaleTarget,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioHighBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "FavScale"
    )

    var wasJustAdded by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var buttonScaleTarget by remember { mutableStateOf(1f) }
    val buttonScale by animateFloatAsState(
        targetValue = buttonScaleTarget,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "BtnScale"
    )

    Card(
        modifier = Modifier
            .width(135.dp)
            .clickable(onClick = onCardClick)
            .testTag("deal_card_${product.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.5.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            // Product image badge with Favorite & Stock Badge
            Box(
                modifier = Modifier
                    .size(115.dp)
                    .clip(RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                ProductVisual(
                    imageResName = product.imageResName,
                    modifier = Modifier.fillMaxSize()
                )

                // Interactive Favorite Heart Button with spring bounce
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.85f))
                        .clickable {
                            onToggleFavorite()
                            favoriteScaleTarget = 1.35f
                            scope.launch {
                                delay(120)
                                favoriteScaleTarget = 1.0f
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorito",
                        tint = if (isFavorite) Color(0xFFEF4444) else Color(0xFF9CA3AF),
                        modifier = Modifier
                            .size(15.dp)
                            .graphicsLayer {
                                scaleX = favoriteScale
                                scaleY = favoriteScale
                            }
                    )
                }

                // Real-time stock status badge
                if (product.isOutOfStock) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .background(Color(0xFFEF4444), RoundedCornerShape(4.dp))
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text("Esgotado", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                } else if (product.isLowStock) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .background(Color(0xFFF59E0B), RoundedCornerShape(4.dp))
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text("${product.stockQuantity} restam", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = product.name,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = product.unit,
                fontSize = 11.sp,
                color = Color(0xFF9CA3AF)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = String.format(Locale.US, "$%.2f", product.price),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                // Tactile Bouncy Add Button with Checkmark Feedback
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .graphicsLayer {
                            scaleX = buttonScale
                            scaleY = buttonScale
                        }
                        .clip(CircleShape)
                        .background(
                            if (product.isOutOfStock) Color(0xFFD1D5DB)
                            else if (wasJustAdded) Color(0xFF059669)
                            else FreshGreenPrimary
                        )
                        .clickable(
                            enabled = !product.isOutOfStock,
                            onClick = {
                                buttonScaleTarget = 0.82f
                                scope.launch {
                                    delay(100)
                                    buttonScaleTarget = 1.0f
                                    wasJustAdded = true
                                    delay(800)
                                    wasJustAdded = false
                                }
                                onAdd()
                            }
                        )
                        .testTag("add_deal_btn_${product.id}"),
                    contentAlignment = Alignment.Center
                ) {
                    Crossfade(targetState = wasJustAdded, label = "BtnIconFade") { added ->
                        if (added) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Adicionado",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add to Cart",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BestSellingProductRow(
    product: Product,
    isFavorite: Boolean = false,
    onToggleFavorite: () -> Unit = {},
    onCardClick: () -> Unit,
    onAdd: () -> Unit
) {
    var favoriteScaleTarget by remember { mutableStateOf(1f) }
    val favoriteScale by animateFloatAsState(
        targetValue = favoriteScaleTarget,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioHighBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "FavRowScale"
    )

    var wasJustAdded by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var buttonScaleTarget by remember { mutableStateOf(1f) }
    val buttonScale by animateFloatAsState(
        targetValue = buttonScaleTarget,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "BtnRowScale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .clickable(onClick = onCardClick)
            .testTag("best_sell_card_${product.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProductVisual(
                imageResName = product.imageResName,
                modifier = Modifier.size(64.dp)
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "${product.unit} • ${product.category}",
                    fontSize = 12.sp,
                    color = Color(0xFF6B7280)
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Real-time stock status badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val stockColor = when {
                        product.isOutOfStock -> Color(0xFFEF4444)
                        product.isLowStock -> Color(0xFFF59E0B)
                        else -> FreshGreenPrimary
                    }
                    val stockLabel = when {
                        product.isOutOfStock -> "Esgotado"
                        product.isLowStock -> "Últimas ${product.stockQuantity} un."
                        else -> "Estoque: ${product.stockQuantity} un."
                    }

                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(stockColor)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stockLabel,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = stockColor
                    )
                }
            }

            // Right side: Price & Actions
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Heart Favorite Icon
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF9FAFB))
                        .clickable {
                            onToggleFavorite()
                            favoriteScaleTarget = 1.35f
                            scope.launch {
                                delay(120)
                                favoriteScaleTarget = 1.0f
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorito",
                        tint = if (isFavorite) Color(0xFFEF4444) else Color(0xFF9CA3AF),
                        modifier = Modifier
                            .size(16.dp)
                            .graphicsLayer {
                                scaleX = favoriteScale
                                scaleY = favoriteScale
                            }
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = String.format(Locale.US, "$%.2f", product.price),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    if (product.originalPrice > product.price) {
                        Text(
                            text = String.format(Locale.US, "$%.2f", product.originalPrice),
                            fontSize = 11.sp,
                            color = Color(0xFF9CA3AF),
                            textDecoration = TextDecoration.LineThrough
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Tactile Bouncy Add Button
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .graphicsLayer {
                                scaleX = buttonScale
                                scaleY = buttonScale
                            }
                            .clip(CircleShape)
                            .background(
                                if (product.isOutOfStock) Color(0xFFD1D5DB)
                                else if (wasJustAdded) Color(0xFF059669)
                                else FreshGreenPrimary
                            )
                            .clickable(
                                enabled = !product.isOutOfStock,
                                onClick = {
                                    buttonScaleTarget = 0.82f
                                    scope.launch {
                                        delay(100)
                                        buttonScaleTarget = 1.0f
                                        wasJustAdded = true
                                        delay(800)
                                        wasJustAdded = false
                                    }
                                    onAdd()
                                }
                            )
                            .testTag("add_bestsell_btn_${product.id}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Crossfade(targetState = wasJustAdded, label = "BtnRowIconFade") { added ->
                            if (added) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Adicionado",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add to Cart",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    val sampleProducts = AppDatabase.defaultProducts
    MyApplicationTheme {
        HomeScreen(
            products = sampleProducts,
            flashDeals = sampleProducts.filter { it.isFlashDeal },
            bestSelling = sampleProducts.filter { it.isBestSelling },
            searchQuery = "",
            onSearchChange = {},
            selectedCategory = "Todos",
            onSelectCategory = {},
            flashCountdownSeconds = 7200L,
            deliveryAddress = "Rua das Flores, 123",
            onProductClick = {},
            onAddToCart = {},
            onOpenCategories = {},
            onOpenStockManagement = {}
        )
    }
}
