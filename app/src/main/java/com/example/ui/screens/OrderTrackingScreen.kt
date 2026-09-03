package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Order
import com.example.ui.theme.FreshGreenDark
import com.example.ui.theme.FreshGreenPrimary
import java.util.Locale

@Composable
fun OrderTrackingScreen(
    order: Order?,
    onBack: () -> Unit,
    onOpenChat: (Long) -> Unit,
    onCallDriver: () -> Unit
) {
    val scrollState = rememberScrollState()

    val pulseTransition = rememberInfiniteTransition(label = "RadarPulse")
    val pulseRadarScale by pulseTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "RadarScale"
    )
    val pulseRadarAlpha by pulseTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "RadarAlpha"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .testTag("tracking_back_btn")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }

            Text(
                text = "Rastreamento ao Vivo",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(FreshGreenPrimary.copy(alpha = 0.15f))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = order?.orderNumber ?: "#ORD-789",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = FreshGreenDark
                )
            }
        }

        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Interactive Delivery Route Map Visualizer Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFFE2E8F0))
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    // Background map terrain: Park areas
                    drawCircle(
                        color = Color(0xFFDCFCE7),
                        radius = 90.dp.toPx(),
                        center = Offset(w * 0.25f, h * 0.35f)
                    )
                    drawCircle(
                        color = Color(0xFFDCFCE7),
                        radius = 70.dp.toPx(),
                        center = Offset(w * 0.85f, h * 0.8f)
                    )

                    // River curve
                    val riverPath = Path().apply {
                        moveTo(0f, h * 0.7f)
                        cubicTo(w * 0.3f, h * 0.65f, w * 0.6f, h * 0.85f, w, h * 0.75f)
                    }
                    drawPath(
                        path = riverPath,
                        color = Color(0xFFBAE6FD),
                        style = Stroke(width = 24.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Roads grid
                    val roadPaint = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                    val roadColor = Color.White
                    drawLine(roadColor, Offset(0f, h * 0.25f), Offset(w, h * 0.25f), strokeWidth = 8.dp.toPx())
                    drawLine(roadColor, Offset(0f, h * 0.55f), Offset(w, h * 0.55f), strokeWidth = 8.dp.toPx())
                    drawLine(roadColor, Offset(w * 0.35f, 0f), Offset(w * 0.35f, h), strokeWidth = 8.dp.toPx())
                    drawLine(roadColor, Offset(w * 0.75f, 0f), Offset(w * 0.75f, h), strokeWidth = 8.dp.toPx())

                    // Route path line (Store -> Scooter -> House)
                    val routePath = Path().apply {
                        moveTo(w * 0.2f, h * 0.75f) // Store
                        lineTo(w * 0.35f, h * 0.55f)
                        lineTo(w * 0.60f, h * 0.55f) // Scooter location
                        lineTo(w * 0.75f, h * 0.3f)
                        lineTo(w * 0.85f, h * 0.25f) // Destination Home
                    }

                    // Dashed outline
                    drawPath(
                        path = routePath,
                        color = FreshGreenPrimary,
                        style = Stroke(
                            width = 6.dp.toPx(),
                            cap = StrokeCap.Round,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 15f), 0f)
                        )
                    )
                }

                // Map Pin: Store
                Box(
                    modifier = Modifier
                        .offset(x = 35.dp, y = 175.dp)
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF3B82F6))
                        .shadow(4.dp, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Storefront,
                        contentDescription = "Hortifruti Loja",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Map Pin: Scooter Driver (Animated with radar pulse)
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(x = 25.dp, y = 10.dp)
                        .size(46.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Radar Ring
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .graphicsLayer {
                                scaleX = pulseRadarScale
                                scaleY = pulseRadarScale
                                alpha = pulseRadarAlpha
                            }
                            .clip(CircleShape)
                            .background(FreshGreenPrimary)
                    )
                    // Scooter Icon Pin
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(FreshGreenPrimary)
                            .border(2.dp, Color.White, CircleShape)
                            .shadow(6.dp, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DirectionsBike,
                            contentDescription = "Entregador",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                // Map Pin: Customer Home
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = (-30).dp, y = 45.dp)
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEF4444))
                        .shadow(4.dp, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Destino",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Floating Estimated Time Badge
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 12.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = FreshGreenPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Chegando em ~10 minutos",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF111827)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Delivery Status Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "A Caminho do Destino",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "Entregador já retirou seus produtos frescos",
                                fontSize = 12.sp,
                                color = Color(0xFF6B7280)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFD1FADF))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "AO VIVO",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = FreshGreenDark
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 4-Step Stepper Timeline
                    TrackingTimelineStep(
                        title = "Pedido Confirmado",
                        time = "10:15",
                        isCompleted = true,
                        isCurrent = false
                    )
                    TrackingTimelineStep(
                        title = "Separado no Hortifruti",
                        time = "10:22",
                        isCompleted = true,
                        isCurrent = false
                    )
                    TrackingTimelineStep(
                        title = "A Caminho com Entregador",
                        time = "10:35",
                        isCompleted = true,
                        isCurrent = true
                    )
                    TrackingTimelineStep(
                        title = "Entrega Concluída",
                        time = "Previsto 10:45",
                        isCompleted = false,
                        isCurrent = false,
                        isLast = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Driver Profile Card with Call and Chat Buttons
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE0E7FF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "AQ",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4338CA)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Abdulmalik Qasim",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "⭐ 4.9 • Moto Honda 160",
                                fontSize = 12.sp,
                                color = Color(0xFF6B7280)
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Call button
                        IconButton(
                            onClick = onCallDriver,
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(FreshGreenPrimary.copy(alpha = 0.12f))
                                .testTag("call_driver_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Call,
                                contentDescription = "Ligar",
                                tint = FreshGreenPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Chat button
                        IconButton(
                            onClick = { onOpenChat(order?.id ?: 1L) },
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(FreshGreenPrimary)
                                .testTag("chat_driver_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Chat,
                                contentDescription = "Chat",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Order Destination & Summary Details
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Endereço de Entrega",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = order?.deliveryAddress ?: "Meu Apartamento, Rua das Flores, 123",
                        fontSize = 13.sp,
                        color = Color(0xFF6B7280)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Itens do Pedido",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = order?.itemsSummary ?: "Maçãs Gala (1kg), Banana Prata (2kg)",
                        fontSize = 13.sp,
                        color = Color(0xFF6B7280)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Total Pago",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF6B7280)
                        )
                        Text(
                            text = String.format(Locale.US, "$%.2f", order?.totalAmount ?: 24.50),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = FreshGreenPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun TrackingTimelineStep(
    title: String,
    time: String,
    isCompleted: Boolean,
    isCurrent: Boolean,
    isLast: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isCurrent -> FreshGreenPrimary
                            isCompleted -> FreshGreenPrimary
                            else -> Color(0xFFE5E7EB)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    )
                }
            }

            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(30.dp)
                        .background(
                            if (isCompleted) FreshGreenPrimary else Color(0xFFE5E7EB)
                        )
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.padding(bottom = if (isLast) 0.dp else 14.dp)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                color = if (isCompleted || isCurrent) MaterialTheme.colorScheme.onBackground else Color(0xFF9CA3AF)
            )
            Text(
                text = time,
                fontSize = 11.sp,
                color = Color(0xFF9CA3AF)
            )
        }
    }
}
