package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.LunchDining
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R

@Composable
fun ProductVisual(
    imageResName: String,
    modifier: Modifier = Modifier,
    size: Dp = 80.dp,
    contentScale: ContentScale = ContentScale.Crop
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF6F8F6)),
        contentAlignment = Alignment.Center
    ) {
        when (imageResName) {
            "avocado" -> {
                Image(
                    painter = painterResource(id = R.drawable.img_avocado_detail),
                    contentDescription = "Fresh Avocado",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = contentScale
                )
            }
            "banana" -> {
                FruitBadge(
                    emoji = "🍌",
                    bgGradient = listOf(Color(0xFFFEF9C3), Color(0xFFFEF08A)),
                    iconColor = Color(0xFFEAB308)
                )
            }
            "apple" -> {
                FruitBadge(
                    emoji = "🍎",
                    bgGradient = listOf(Color(0xFFFEE2E2), Color(0xFFFECACA)),
                    iconColor = Color(0xFFEF4444)
                )
            }
            "orange" -> {
                FruitBadge(
                    emoji = "🍊",
                    bgGradient = listOf(Color(0xFFFFEDD5), Color(0xFFFED7AA)),
                    iconColor = Color(0xFFF97316)
                )
            }
            "milk" -> {
                FruitBadge(
                    emoji = "🥛",
                    bgGradient = listOf(Color(0xFFE0F2FE), Color(0xFFBAE6FD)),
                    iconColor = Color(0xFF0284C7)
                )
            }
            "bread" -> {
                FruitBadge(
                    emoji = "🍞",
                    bgGradient = listOf(Color(0xFFFEF3C7), Color(0xFFFDE68A)),
                    iconColor = Color(0xFFD97706)
                )
            }
            "blueberry" -> {
                FruitBadge(
                    emoji = "🫐",
                    bgGradient = listOf(Color(0xFFEDE9FE), Color(0xFFDDD6FE)),
                    iconColor = Color(0xFF7C3AED)
                )
            }
            "strawberry" -> {
                FruitBadge(
                    emoji = "🍓",
                    bgGradient = listOf(Color(0xFFFFE4E6), Color(0xFFFECDD3)),
                    iconColor = Color(0xFFE11D48)
                )
            }
            "kiwi" -> {
                FruitBadge(
                    emoji = "🥝",
                    bgGradient = listOf(Color(0xFFECFDF5), Color(0xFFA7F3D0)),
                    iconColor = Color(0xFF059669)
                )
            }
            "broccoli" -> {
                FruitBadge(
                    emoji = "🥦",
                    bgGradient = listOf(Color(0xFFDCFCE7), Color(0xFFBBF7D0)),
                    iconColor = Color(0xFF16A34A)
                )
            }
            "carrot" -> {
                FruitBadge(
                    emoji = "🥕",
                    bgGradient = listOf(Color(0xFFFFEDD5), Color(0xFFFED7AA)),
                    iconColor = Color(0xFFEA580C)
                )
            }
            "tomato" -> {
                FruitBadge(
                    emoji = "🍅",
                    bgGradient = listOf(Color(0xFFFEE2E2), Color(0xFFFECACA)),
                    iconColor = Color(0xFFDC2626)
                )
            }
            "juice" -> {
                FruitBadge(
                    emoji = "🧃",
                    bgGradient = listOf(Color(0xFFFEF3C7), Color(0xFFFDE047)),
                    iconColor = Color(0xFFCA8A04)
                )
            }
            "eggs" -> {
                FruitBadge(
                    emoji = "🥚",
                    bgGradient = listOf(Color(0xFFFFFBEB), Color(0xFFFEF3C7)),
                    iconColor = Color(0xFFD97706)
                )
            }
            "cheese" -> {
                FruitBadge(
                    emoji = "🧀",
                    bgGradient = listOf(Color(0xFFFEF9C3), Color(0xFFFDE047)),
                    iconColor = Color(0xFFEAB308)
                )
            }
            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFE8F5E9)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Eco,
                        contentDescription = null,
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun FruitBadge(
    emoji: String,
    bgGradient: List<Color>,
    iconColor: Color
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.radialGradient(bgGradient)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = emoji,
            fontSize = 38.sp,
            fontWeight = FontWeight.Normal
        )
    }
}
