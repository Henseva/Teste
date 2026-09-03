package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.FreshGreenDark
import com.example.ui.theme.FreshGreenPrimary

data class VoucherItem(
    val code: String,
    val discountTitle: String,
    val discountRate: Double,
    val description: String,
    val minSpend: String,
    val expiration: String,
    val category: String, // "All", "Shipping", "Discount"
    val badgeColor: Color
)

@Composable
fun VouchersScreen(
    appliedCode: String?,
    onApplyVoucher: (String, Double, String) -> Unit,
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Todos", "Super Descontos", "Frete Grátis")

    val vouchers = listOf(
        VoucherItem(
            code = "HORTI50",
            discountTitle = "50% DE DESCONTO",
            discountRate = 0.50,
            description = "Metade do preço em toda a seleção de hortifruti orgânico",
            minSpend = "Em pedidos acima de $35.00",
            expiration = "Válido até 31/12/2026",
            category = "Discount",
            badgeColor = Color(0xFFEF4444)
        ),
        VoucherItem(
            code = "FRESH20",
            discountTitle = "20% OFF GERAL",
            discountRate = 0.20,
            description = "Economize 20% em qualquer compra de frutas e legumes",
            minSpend = "Sem valor mínimo",
            expiration = "Válido até amanhã",
            category = "Discount",
            badgeColor = FreshGreenPrimary
        ),
        VoucherItem(
            code = "FRETEGRATIS",
            discountTitle = "FRETE ZERO",
            discountRate = 0.05,
            description = "Entrega expressa totalmente gratuita para sua residência",
            minSpend = "Válido na primeira compra",
            expiration = "Válido esta semana",
            category = "Shipping",
            badgeColor = Color(0xFF3B82F6)
        ),
        VoucherItem(
            code = "PIX5",
            discountTitle = "5% EXTRA VIA PIX",
            discountRate = 0.05,
            description = "Desconto automático cumulativo ao selecionar Pix",
            minSpend = "Cumulativo com outros cupons",
            expiration = "Oferta por tempo indeterminado",
            category = "Discount",
            badgeColor = Color(0xFF8B5CF6)
        )
    )

    val filteredVouchers = when (selectedTab) {
        1 -> vouchers.filter { it.category == "Discount" }
        2 -> vouchers.filter { it.category == "Shipping" }
        else -> vouchers
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("vouchers_back_btn")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }

            Text(
                text = "Cupons & Vouchers",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        // Hero Promo Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFF065F46), FreshGreenPrimary)
                        )
                    )
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "SEMANA DO ORGÂNICO",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFD1FADF)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Economize até 50%",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Aproveite cupons exclusivos em hortifruti fresco direto da fazenda.",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ConfirmationNumber,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }

        // Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = FreshGreenPrimary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = FreshGreenPrimary
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp
                        )
                    }
                )
            }
        }

        // Voucher Cards List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(filteredVouchers, key = { it.code }) { voucher ->
                val isApplied = appliedCode == voucher.code

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("voucher_card_${voucher.code}"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
                    border = if (isApplied) androidx.compose.foundation.BorderStroke(2.dp, FreshGreenPrimary) else null
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(voucher.badgeColor.copy(alpha = 0.12f))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = voucher.discountTitle,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = voucher.badgeColor
                                )
                            }

                            Text(
                                text = voucher.expiration,
                                fontSize = 11.sp,
                                color = Color(0xFF9CA3AF)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = voucher.description,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = voucher.minSpend,
                            fontSize = 12.sp,
                            color = Color(0xFF6B7280)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Coupon Code Box
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFF3F4F6))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = voucher.code,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp,
                                    color = Color(0xFF374151)
                                )
                            }

                            // Apply Button
                            Button(
                                onClick = {
                                    onApplyVoucher(voucher.code, voucher.discountRate, "Cupom ${voucher.code} aplicado com sucesso!")
                                },
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isApplied) Color(0xFF059669) else FreshGreenPrimary
                                )
                            ) {
                                if (isApplied) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Aplicado!", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                } else {
                                    Text("Aplicar Cupom", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
