package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.FreshGreenDark
import com.example.ui.theme.FreshGreenPrimary

data class CategoryDef(
    val id: String,
    val title: String,
    val subtitle: String,
    val countLabel: String,
    val emoji: String,
    val bgColor: Color
)

@Composable
fun CategoriesScreen(
    onBack: () -> Unit,
    onSelectCategory: (String) -> Unit
) {
    val categories = listOf(
        CategoryDef("Fruits", "Fruits & Vegetables", "Fresh and Organic", "120+ Items", "🥗", Color(0xFFDCFCE7)),
        CategoryDef("Dairy", "Dairy & Eggs", "Farm Fresh Products", "85+ Items", "🥛", Color(0xFFE0F2FE)),
        CategoryDef("Snacks", "Snacks & Munchies", "Top Brands", "150+ Items", "🍿", Color(0xFFFEF3C7)),
        CategoryDef("Beverages", "Beverages", "Hot & Cold Drinks", "60+ Items", "🧃", Color(0xFFFFEDD5)),
        CategoryDef("Bakery", "Bakery & Breads", "Stone-Baked Daily", "45+ Items", "🍞", Color(0xFFFEE2E2)),
        CategoryDef("Meat", "Organic Meat & Poultry", "Prime Quality Cuts", "35+ Items", "🥩", Color(0xFFF3E8FF))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFBFDFB))
            .statusBarsPadding()
    ) {
        // Top Bar: Back arrow + Title
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("categories_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF111827)
                )
            }

            Text(
                text = "Categories",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111827),
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 100.dp, top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Promo Banner: "Big Savings On Everyday Essentials"
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFFFFF7ED), Color(0xFFFED7AA), Color(0xFFFFEDD5))
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Big Savings",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF9A3412)
                                )
                                Text(
                                    text = "On Everyday\nEssentials",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF1F2937)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clickable { onSelectCategory("Fruits") }
                                        .testTag("promo_shop_now_cat")
                                ) {
                                    Text(
                                        text = "Shop Now",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF9A3412)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = null,
                                        tint = Color(0xFF9A3412),
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .size(76.dp)
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(Color.White.copy(alpha = 0.8f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🛍️", fontSize = 42.sp)
                            }
                        }
                    }
                }
            }

            // Categories list cards
            items(categories, key = { it.id }) { cat ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectCategory(cat.id) }
                        .testTag("category_card_${cat.id}"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Category Icon box
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(cat.bgColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = cat.emoji, fontSize = 28.sp)
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = cat.title,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF111827)
                            )
                            Text(
                                text = cat.subtitle,
                                fontSize = 12.sp,
                                color = Color(0xFF6B7280)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = cat.countLabel,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = FreshGreenPrimary
                            )
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Open ${cat.title}",
                            tint = Color(0xFF9CA3AF),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}
