package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.FreshGreenLight
import com.example.ui.theme.FreshGreenPrimary
import com.example.ui.viewmodel.AppScreen

@Composable
fun HortifrutiBottomBar(
    currentScreen: AppScreen,
    cartItemCount: Int,
    onNavigate: (AppScreen) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 10.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(70.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. Home
                BottomNavItem(
                    label = "Início",
                    iconFilled = Icons.Filled.Home,
                    iconOutlined = Icons.Outlined.Home,
                    isSelected = currentScreen is AppScreen.Home,
                    onClick = { onNavigate(AppScreen.Home) },
                    tag = "nav_home"
                )

                // 2. Categories
                BottomNavItem(
                    label = "Categorias",
                    iconFilled = Icons.Filled.Category,
                    iconOutlined = Icons.Outlined.Category,
                    isSelected = currentScreen is AppScreen.Categories,
                    onClick = { onNavigate(AppScreen.Categories) },
                    tag = "nav_categories"
                )

                // Center Spacer for elevated Cart FAB
                Spacer(modifier = Modifier.size(56.dp))

                // 4. Orders
                BottomNavItem(
                    label = "Pedidos",
                    iconFilled = Icons.Filled.ReceiptLong,
                    iconOutlined = Icons.Outlined.ReceiptLong,
                    isSelected = currentScreen is AppScreen.Orders,
                    onClick = { onNavigate(AppScreen.Orders) },
                    tag = "nav_orders"
                )

                // 5. Profile
                BottomNavItem(
                    label = "Perfil",
                    iconFilled = Icons.Filled.Person,
                    iconOutlined = Icons.Outlined.Person,
                    isSelected = currentScreen is AppScreen.Profile || currentScreen is AppScreen.StockManagement,
                    onClick = { onNavigate(AppScreen.Profile) },
                    tag = "nav_profile"
                )
            }

            // Center Elevated Cart FAB with pulsating glow animation
            val infiniteTransition = rememberInfiniteTransition(label = "FabPulse")
            val pulseScale by infiniteTransition.animateFloat(
                initialValue = 1.0f,
                targetValue = if (cartItemCount > 0) 1.22f else 1.0f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1200, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "PulseScale"
            )
            val pulseAlpha by infiniteTransition.animateFloat(
                initialValue = if (cartItemCount > 0) 0.45f else 0f,
                targetValue = 0f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1200, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "PulseAlpha"
            )

            // Badge bounce animation when cart count changes
            var lastCount by remember { mutableIntStateOf(cartItemCount) }
            var triggerBadgeBounce by remember { mutableIntStateOf(0) }
            LaunchedEffect(cartItemCount) {
                if (cartItemCount != lastCount) {
                    lastCount = cartItemCount
                    triggerBadgeBounce++
                }
            }

            val badgeScale by animateFloatAsState(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                ),
                label = "BadgeScale"
            )

            Box(
                modifier = Modifier
                    .offset(y = (-16).dp)
                    .size(62.dp),
                contentAlignment = Alignment.Center
            ) {
                // Pulsing ambient glow ring when cart has items
                if (cartItemCount > 0) {
                    Box(
                        modifier = Modifier
                            .size(58.dp)
                            .graphicsLayer {
                                scaleX = pulseScale
                                scaleY = pulseScale
                                alpha = pulseAlpha
                            }
                            .clip(CircleShape)
                            .background(FreshGreenPrimary)
                    )
                }

                // Main elevated circular FAB button
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .shadow(8.dp, CircleShape)
                        .clip(CircleShape)
                        .background(
                            Brush.verticalGradient(
                                listOf(FreshGreenLight, FreshGreenPrimary)
                            )
                        )
                        .clickable(
                            onClick = { onNavigate(AppScreen.Cart) }
                        )
                        .testTag("nav_cart_fab"),
                    contentAlignment = Alignment.Center
                ) {
                    BadgedBox(
                        badge = {
                            if (cartItemCount > 0) {
                                Badge(
                                    containerColor = Color(0xFFEF4444),
                                    contentColor = Color.White,
                                    modifier = Modifier.graphicsLayer {
                                        scaleX = badgeScale
                                        scaleY = badgeScale
                                    }
                                ) {
                                    Text(
                                        text = "$cartItemCount",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ShoppingCart,
                            contentDescription = "Carrinho",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BottomNavItem(
    label: String,
    iconFilled: ImageVector,
    iconOutlined: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    tag: String
) {
    // Dynamic bouncy scale when selected
    val itemScale by animateFloatAsState(
        targetValue = if (isSelected) 1.12f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "NavItemScale"
    )

    val indicatorWidth by animateDpAsState(
        targetValue = if (isSelected) 18.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "IndicatorWidth"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .graphicsLayer {
                scaleX = itemScale
                scaleY = itemScale
            }
            .testTag(tag)
    ) {
        Icon(
            imageVector = if (isSelected) iconFilled else iconOutlined,
            contentDescription = label,
            tint = if (isSelected) FreshGreenPrimary else Color(0xFF9CA3AF),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) FreshGreenPrimary else Color(0xFF6B7280)
        )
        Spacer(modifier = Modifier.height(2.dp))
        // Animated indicator dot/pill under active item
        Box(
            modifier = Modifier
                .height(3.dp)
                .width(indicatorWidth)
                .clip(RoundedCornerShape(1.5.dp))
                .background(if (isSelected) FreshGreenPrimary else Color.Transparent)
        )
    }
}
