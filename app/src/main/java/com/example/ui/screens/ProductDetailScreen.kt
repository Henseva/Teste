package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import com.example.data.local.AppDatabase
import com.example.ui.theme.MyApplicationTheme
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.data.model.Product
import com.example.ui.components.ProductVisual
import com.example.ui.theme.FreshGreenPrimary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

data class GalleryPhotoAngle(
    val title: String,
    val subtitle: String,
    val tag: String
)

@Composable
fun ProductDetailScreen(
    product: Product?,
    recommendedProducts: List<Product>,
    isFavorite: Boolean = false,
    onToggleFavorite: () -> Unit = {},
    onBack: () -> Unit,
    onAddToCart: (Product, Int) -> Unit,
    onProductClick: (Long) -> Unit
) {
    if (product == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Produto não encontrado")
        }
        return
    }

    var favoriteScaleTarget by remember { mutableStateOf(1f) }
    val favoriteScale by animateFloatAsState(
        targetValue = favoriteScaleTarget,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioHighBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "DetailFavScale"
    )

    var wasJustAdded by remember { mutableStateOf(false) }
    var buttonScaleTarget by remember { mutableStateOf(1f) }
    val buttonScale by animateFloatAsState(
        targetValue = buttonScaleTarget,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "DetailBtnScale"
    )
    val coroutineScope = rememberCoroutineScope()

    var quantity by remember { mutableIntStateOf(1) }
    var selectedPhotoIndex by remember { mutableIntStateOf(0) }
    var isLightboxOpen by remember { mutableStateOf(false) }
    var detailTab by remember { mutableIntStateOf(0) }

    // Multi-angle photo gallery definition
    val galleryAngles = remember {
        listOf(
            GalleryPhotoAngle("Visão Geral", "Fruto inteiro fresco", "whole"),
            GalleryPhotoAngle("Corte & Polpa", "Ponto de maturação ideal", "cut"),
            GalleryPhotoAngle("Lote & Embalagem", "Proteção ecológica", "box"),
            GalleryPhotoAngle("Colheita Orgânica", "Direto do produtor", "farm")
        )
    }

    // Fullscreen Photo Lightbox Dialog
    if (isLightboxOpen) {
        Dialog(
            onDismissRequest = { isLightboxOpen = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.95f))
                    .statusBarsPadding()
                    .navigationBarsPadding()
            ) {
                // Close button top right
                IconButton(
                    onClick = { isLightboxOpen = false },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .size(44.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                        .testTag("close_lightbox_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Fechar Galeria",
                        tint = Color.White
                    )
                }

                // Top Caption info
                Column(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(16.dp)
                ) {
                    Text(
                        text = product.name,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Foto ${selectedPhotoIndex + 1} de ${galleryAngles.size} • ${galleryAngles[selectedPhotoIndex].title}",
                        color = Color(0xFFD1FADF),
                        fontSize = 13.sp
                    )
                }

                // Center main image view
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(380.dp)
                        .align(Alignment.Center)
                        .padding(horizontal = 24.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF1E293B)),
                    contentAlignment = Alignment.Center
                ) {
                    ProductGalleryVisualView(
                        product = product,
                        angleIndex = selectedPhotoIndex,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }

                // Bottom Thumbnail selector in Lightbox
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = galleryAngles[selectedPhotoIndex].subtitle,
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 13.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        galleryAngles.forEachIndexed { index, angle ->
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (selectedPhotoIndex == index) FreshGreenPrimary else Color.White.copy(alpha = 0.15f))
                                    .border(
                                        width = if (selectedPhotoIndex == index) 2.dp else 1.dp,
                                        color = if (selectedPhotoIndex == index) Color.White else Color.Transparent,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable { selectedPhotoIndex = index }
                                    .padding(4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                ProductGalleryVisualView(
                                    product = product,
                                    angleIndex = index,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFBFDFB))
            .statusBarsPadding()
    ) {
        // Top Bar: Back, "Product Details", Favorite heart
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("product_detail_back_btn")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF111827)
                )
            }

            Text(
                text = "Detalhes do Produto",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111827)
            )

            IconButton(
                onClick = {
                    onToggleFavorite()
                    favoriteScaleTarget = 1.35f
                    coroutineScope.launch {
                        delay(120)
                        favoriteScaleTarget = 1.0f
                    }
                },
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = favoriteScale
                        scaleY = favoriteScale
                    }
                    .testTag("favorite_btn")
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (isFavorite) Color(0xFFEF4444) else Color(0xFF111827)
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Photo Gallery Hero Section with Multi-Angle View
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(290.dp)
                        .padding(horizontal = 20.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .background(Color(0xFFF0FDF4))
                        .clickable { isLightboxOpen = true }
                        .testTag("product_gallery_hero"),
                    contentAlignment = Alignment.Center
                ) {
                    AnimatedContent(
                        targetState = selectedPhotoIndex,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "GalleryTransition"
                    ) { targetAngle ->
                        ProductGalleryVisualView(
                            product = product,
                            angleIndex = targetAngle,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    // Fresh / Organic Badge Top Left
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(14.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.92f))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Eco,
                                contentDescription = null,
                                tint = FreshGreenPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "100% Orgânico",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF065F46)
                            )
                        }
                    }

                    // Zoom / Expand Icon Top Right
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(14.dp)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.92f))
                            .clickable { isLightboxOpen = true }
                            .testTag("zoom_gallery_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Fullscreen,
                            contentDescription = "Ver Galeria Completa",
                            tint = Color(0xFF111827),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Photo Angle Pills Overlay on Bottom
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 12.dp)
                            .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        galleryAngles.forEachIndexed { index, angle ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (selectedPhotoIndex == index) FreshGreenPrimary else Color.Transparent
                                    )
                                    .clickable { selectedPhotoIndex = index }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                    .testTag("gallery_angle_$index")
                            ) {
                                Text(
                                    text = angle.title,
                                    fontSize = 11.sp,
                                    fontWeight = if (selectedPhotoIndex == index) FontWeight.Bold else FontWeight.Medium,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            // Interactive Thumbnail Strip directly under hero photo
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        galleryAngles.forEachIndexed { index, angle ->
                            val isSelected = selectedPhotoIndex == index
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedPhotoIndex = index }
                                    .testTag("thumb_btn_$index"),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) Color(0xFFE8F5E9) else Color(0xFFF3F4F6)
                                ),
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, FreshGreenPrimary) else null
                            ) {
                                Column(
                                    modifier = Modifier.padding(6.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color.White),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        ProductGalleryVisualView(
                                            product = product,
                                            angleIndex = index,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = angle.title,
                                        fontSize = 10.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) FreshGreenPrimary else Color(0xFF4B5563),
                                        maxLines = 1,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Title, Rating, Weight, Price
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = product.name,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF111827)
                        )

                        // Rating badge
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(Color(0xFFFEF3C7), RoundedCornerShape(12.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFF59E0B),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${product.rating} (${product.ratingCount})",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF92400E)
                            )
                        }
                    }

                    Text(
                        text = "${product.unit} • Categoria: ${product.category}",
                        fontSize = 13.sp,
                        color = Color(0xFF6B7280),
                        modifier = Modifier.padding(top = 2.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Price and Discount row
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = String.format(Locale.US, "$%.2f", product.price),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF111827)
                        )

                        if (product.originalPrice > product.price) {
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = String.format(Locale.US, "$%.2f", product.originalPrice),
                                fontSize = 16.sp,
                                color = Color(0xFF9CA3AF),
                                textDecoration = TextDecoration.LineThrough
                            )
                        }

                        if (product.discountPercent > 0) {
                            Spacer(modifier = Modifier.width(10.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFD1FADF))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "${product.discountPercent}% OFF",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF065F46)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Real-Time Stock Status Panel
                    val stockBg = when {
                        product.isOutOfStock -> Color(0xFFFEE2E2)
                        product.isLowStock -> Color(0xFFFEF3C7)
                        else -> Color(0xFFF0FDF4)
                    }
                    val stockBorder = when {
                        product.isOutOfStock -> Color(0xFFFCA5A5)
                        product.isLowStock -> Color(0xFFFCD34D)
                        else -> Color(0xFFA7F3D0)
                    }
                    val stockTextColor = when {
                        product.isOutOfStock -> Color(0xFF991B1B)
                        product.isLowStock -> Color(0xFF92400E)
                        else -> Color(0xFF065F46)
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, stockBorder, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = stockBg)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = when {
                                            product.isOutOfStock -> Icons.Default.Warning
                                            product.isLowStock -> Icons.Default.Warning
                                            else -> Icons.Default.Verified
                                        },
                                        contentDescription = null,
                                        tint = stockTextColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = when {
                                            product.isOutOfStock -> "PRODUTO ESGOTADO"
                                            product.isLowStock -> "ESTOQUE BAIXO (Últimas Unidades)"
                                            else -> "ESTOQUE EM TEMPO REAL"
                                        },
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = stockTextColor
                                    )
                                }

                                Text(
                                    text = if (product.isOutOfStock) "0 un." else "${product.stockQuantity} un.",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = stockTextColor
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            LinearProgressIndicator(
                                progress = { (product.stockQuantity / 100f).coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = when {
                                    product.isOutOfStock -> Color(0xFFEF4444)
                                    product.isLowStock -> Color(0xFFF59E0B)
                                    else -> FreshGreenPrimary
                                },
                                trackColor = Color.White.copy(alpha = 0.6f),
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = when {
                                    product.isOutOfStock -> "Estamos repondo o estoque com a colheita fresca em breve."
                                    product.isLowStock -> "Garanta o seu antes que acabe! Restam poucas unidades no armazém."
                                    else -> "Garantia de frescor matinal. Pronto para envio imediato hoje."
                                },
                                fontSize = 11.sp,
                                color = stockTextColor.copy(alpha = 0.9f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 4 Feature / Nutrition Badges from Reference Image 2
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        FeatureSpecCard(
                            title = "100%",
                            subtitle = "Orgânico",
                            badgeColor = Color(0xFF0E9F6E),
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        FeatureSpecCard(
                            title = "Frescor",
                            subtitle = "Colhido Hoje",
                            badgeColor = Color(0xFF3B82F6),
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        FeatureSpecCard(
                            title = "${product.rating} ★",
                            subtitle = "(${product.ratingCount})",
                            badgeColor = Color(0xFFF59E0B),
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        FeatureSpecCard(
                            title = "80 kcal",
                            subtitle = "100 Gramas",
                            badgeColor = Color(0xFF8B5CF6),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Tab selector: Descrição vs Avaliações
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF3F4F6))
                            .padding(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (detailTab == 0) Color.White else Color.Transparent)
                                .clickable { detailTab = 0 }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Descrição",
                                fontSize = 13.sp,
                                fontWeight = if (detailTab == 0) FontWeight.Bold else FontWeight.Medium,
                                color = if (detailTab == 0) Color(0xFF111827) else Color(0xFF6B7280)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (detailTab == 1) Color.White else Color.Transparent)
                                .clickable { detailTab = 1 }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Avaliações (${product.ratingCount})",
                                fontSize = 13.sp,
                                fontWeight = if (detailTab == 1) FontWeight.Bold else FontWeight.Medium,
                                color = if (detailTab == 1) Color(0xFF111827) else Color(0xFF6B7280)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (detailTab == 0) {
                        Text(
                            text = product.description.ifEmpty { "Produto selecionado fresco da horta, cultivado sem agrotóxicos e colhido no ponto ideal de maturação." },
                            fontSize = 13.sp,
                            lineHeight = 20.sp,
                            color = Color(0xFF4B5563)
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            CustomerReviewItem(
                                author = "Mariana Silva",
                                rating = 5,
                                date = "Ontem",
                                comment = "Chegou super fresco, muito bem embalado e sabor incomparável!"
                            )
                            CustomerReviewItem(
                                author = "Carlos Eduardo",
                                rating = 5,
                                date = "Há 3 dias",
                                comment = "Qualidade impecável e entrega em 20 minutos. Recomendo!"
                            )
                        }
                    }
                }
            }

            // "You May Also Like" Carousel
            item {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    Text(
                        text = "Você também pode gostar",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF111827),
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                    )

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(recommendedProducts.filter { it.id != product.id }.take(5), key = { it.id }) { recItem ->
                            Card(
                                modifier = Modifier
                                    .width(120.dp)
                                    .clickable { onProductClick(recItem.id) }
                                    .testTag("rec_item_${recItem.id}"),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    ProductVisual(
                                        imageResName = recItem.imageResName,
                                        modifier = Modifier
                                            .size(100.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = recItem.name,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF111827),
                                        maxLines = 1
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = String.format(Locale.US, "$%.2f", recItem.price),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF111827)
                                        )
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clip(CircleShape)
                                                .background(FreshGreenPrimary)
                                                .clickable { onAddToCart(recItem, 1) },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Add,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Bottom Bar: Quantity Selector [- 1 +] and "Add to Cart" button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .border(1.dp, Color(0xFFF3F4F6))
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Quantity Stepper
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFFF3F4F6))
                    .padding(horizontal = 6.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { if (quantity > 1) quantity-- },
                    enabled = !product.isOutOfStock,
                    modifier = Modifier.size(36.dp).testTag("detail_qty_minus")
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Diminuir",
                        tint = if (quantity > 1 && !product.isOutOfStock) Color(0xFF4B5563) else Color(0xFF9CA3AF),
                        modifier = Modifier.size(16.dp)
                    )
                }

                Text(
                    text = if (product.isOutOfStock) "0" else "$quantity",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111827),
                    modifier = Modifier.padding(horizontal = 10.dp)
                )

                IconButton(
                    onClick = {
                        if (quantity < product.stockQuantity) quantity++
                    },
                    enabled = !product.isOutOfStock && quantity < product.stockQuantity,
                    modifier = Modifier.size(36.dp).testTag("detail_qty_plus")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Aumentar",
                        tint = if (quantity < product.stockQuantity && !product.isOutOfStock) Color(0xFF4B5563) else Color(0xFF9CA3AF),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Add to Cart Button with tactile spring feedback
            Button(
                onClick = {
                    buttonScaleTarget = 0.94f
                    coroutineScope.launch {
                        delay(100)
                        buttonScaleTarget = 1.0f
                        wasJustAdded = true
                        delay(900)
                        wasJustAdded = false
                    }
                    onAddToCart(product, quantity)
                },
                enabled = !product.isOutOfStock,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .graphicsLayer {
                        scaleX = buttonScale
                        scaleY = buttonScale
                    }
                    .testTag("detail_add_to_cart_btn"),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (product.isOutOfStock) Color(0xFF9CA3AF)
                    else if (wasJustAdded) Color(0xFF059669)
                    else FreshGreenPrimary,
                    contentColor = Color.White
                )
            ) {
                Crossfade(targetState = wasJustAdded, label = "DetailBtnCrossfade") { added ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (added) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Adicionado!",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Text(
                                text = if (product.isOutOfStock) "Produto Esgotado" else "Adicionar ao Carrinho",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Renders different photo angles / perspectives for the product gallery.
 * Handles photo views: 0: Whole/Main, 1: Cut/Pulp, 2: Box/Packaging, 3: Farm Harvest.
 */
@Composable
fun ProductGalleryVisualView(
    product: Product,
    angleIndex: Int,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        if (product.imageResName == "avocado") {
            when (angleIndex) {
                0 -> {
                    Image(
                        painter = painterResource(id = R.drawable.img_avocado_detail),
                        contentDescription = "${product.name} Visão Geral",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = contentScale
                    )
                }
                1 -> {
                    // Cut / Pulp closeup angle
                    Image(
                        painter = painterResource(id = R.drawable.img_avocado_detail),
                        contentDescription = "${product.name} Corte",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    // Badge overlay indicating slice view
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(8.dp)
                            .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text("Corte Transversal", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
                2 -> {
                    // Harvest box
                    Image(
                        painter = painterResource(id = R.drawable.img_hero_basket),
                        contentDescription = "${product.name} Caixa Lote",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                else -> {
                    // Farm orchard
                    Image(
                        painter = painterResource(id = R.drawable.img_promo_veg),
                        contentDescription = "${product.name} Colheita na Fazenda",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        } else {
            // For other fruits/vegetables, render angle-specific rich visualizations
            when (angleIndex) {
                0 -> ProductVisual(imageResName = product.imageResName, modifier = Modifier.fillMaxSize(), contentScale = contentScale)
                1 -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFFFFF7ED)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            ProductVisual(imageResName = product.imageResName, modifier = Modifier.size(110.dp))
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Ponto de Maturação 100%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFC2410C))
                        }
                    }
                }
                2 -> {
                    Image(
                        painter = painterResource(id = R.drawable.img_hero_basket),
                        contentDescription = "Embalagem Lote",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                else -> {
                    Image(
                        painter = painterResource(id = R.drawable.img_promo_veg),
                        contentDescription = "Colheita Fresca",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}

@Composable
private fun FeatureSpecCard(
    title: String,
    subtitle: String,
    badgeColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = badgeColor.copy(alpha = 0.08f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, badgeColor.copy(alpha = 0.25f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = badgeColor,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = Color(0xFF6B7280),
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun CustomerReviewItem(
    author: String,
    rating: Int,
    date: String,
    comment: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE0E7FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = author.take(1),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4338CA)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = author,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF111827)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    repeat(rating) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(13.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = date, fontSize = 11.sp, color = Color(0xFF9CA3AF))
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = comment,
                fontSize = 12.sp,
                color = Color(0xFF4B5563),
                lineHeight = 16.sp
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProductDetailScreenPreview() {
    val sampleProducts = AppDatabase.defaultProducts
    MyApplicationTheme {
        ProductDetailScreen(
            product = sampleProducts.first(),
            recommendedProducts = sampleProducts.drop(1).take(4),
            isFavorite = true,
            onToggleFavorite = {},
            onBack = {},
            onAddToCart = { _, _ -> },
            onProductClick = {}
        )
    }
}
