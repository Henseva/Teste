package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Product
import com.example.data.model.StockMovement
import com.example.ui.components.ProductVisual
import com.example.ui.theme.FreshGreenPrimary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun StockManagementScreen(
    products: List<Product>,
    stockMovements: List<StockMovement>,
    onAdjustStock: (productId: Long, delta: Int) -> Unit,
    onSetStock: (productId: Long, newStock: Int) -> Unit,
    onSimulateBatch: () -> Unit,
    onBack: () -> Unit
) {
    var activeTab by remember { mutableIntStateOf(0) } // 0 = Produtos & Estoque, 1 = Histórico em Tempo Real
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Todos") } // Todos, Baixo Estoque, Esgotado, Frutas, Legumes

    // State for direct edit dialog
    var productToEdit by remember { mutableStateOf<Product?>(null) }
    var editQuantityText by remember { mutableStateOf("") }

    val totalItemsInStock = remember(products) { products.sumOf { it.stockQuantity } }
    val lowStockCount = remember(products) { products.count { it.isLowStock } }
    val outOfStockCount = remember(products) { products.count { it.isOutOfStock } }

    val filteredProducts = remember(products, searchQuery, selectedFilter) {
        products.filter { p ->
            val matchesQuery = searchQuery.isBlank() ||
                p.name.contains(searchQuery, ignoreCase = true) ||
                p.category.contains(searchQuery, ignoreCase = true)

            val matchesFilter = when (selectedFilter) {
                "Baixo Estoque" -> p.isLowStock
                "Esgotados" -> p.isOutOfStock
                "Frutas" -> p.category.equals("Fruits", ignoreCase = true)
                "Legumes" -> p.category.equals("Vegetables", ignoreCase = true)
                else -> true
            }

            matchesQuery && matchesFilter
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFBFDFB))
            .statusBarsPadding()
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("stock_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF111827)
                    )
                }

                Column {
                    Text(
                        text = "Gestão de Estoque",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF111827)
                    )
                    Text(
                        text = "Sincronização em Tempo Real",
                        fontSize = 11.sp,
                        color = FreshGreenPrimary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Quick restock button
            Button(
                onClick = onSimulateBatch,
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.testTag("simulate_restock_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Receber Lote", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Real-Time Dashboard KPI Cards
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            KpiCard(
                title = "Total Peças",
                value = "$totalItemsInStock",
                color = FreshGreenPrimary,
                bgColor = Color(0xFFD1FADF),
                modifier = Modifier.weight(1f)
            )

            KpiCard(
                title = "Baixo Estoque",
                value = "$lowStockCount",
                color = Color(0xFFB45309),
                bgColor = Color(0xFFFEF3C7),
                modifier = Modifier.weight(1f)
            )

            KpiCard(
                title = "Esgotados",
                value = "$outOfStockCount",
                color = Color(0xFFB91C1C),
                bgColor = Color(0xFFFEE2E2),
                modifier = Modifier.weight(1f)
            )
        }

        // Tabs: Produtos vs Histórico de Movimentações
        TabRow(
            selectedTabIndex = activeTab,
            containerColor = Color.White,
            contentColor = FreshGreenPrimary,
            modifier = Modifier.padding(top = 4.dp)
        ) {
            Tab(
                selected = activeTab == 0,
                onClick = { activeTab = 0 },
                text = { Text("Inventário de Itens (${products.size})", fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = activeTab == 1,
                onClick = { activeTab = 1 },
                text = { Text("Movimentações (${stockMovements.size})", fontWeight = FontWeight.Bold) }
            )
        }

        if (activeTab == 0) {
            // Search & Filter row
            Column(modifier = Modifier.padding(top = 8.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Buscar no estoque...", fontSize = 13.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = Color(0xFF9CA3AF),
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF3F4F6),
                        unfocusedContainerColor = Color(0xFFF3F4F6),
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Filter pills
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val filterOptions = listOf("Todos", "Baixo Estoque", "Esgotados", "Frutas", "Legumes")
                    items(filterOptions) { filterName ->
                        val isSelected = selectedFilter == filterName
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSelected) FreshGreenPrimary else Color(0xFFE5E7EB))
                                .clickable { selectedFilter = filterName }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = filterName,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else Color(0xFF374151)
                            )
                        }
                    }
                }
            }

            // Products Inventory List
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredProducts, key = { it.id }) { product ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("stock_item_${product.id}"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                ProductVisual(
                                    imageResName = product.imageResName,
                                    modifier = Modifier.size(54.dp)
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = product.name,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF111827)
                                    )
                                    Text(
                                        text = "${product.unit} • ${product.category}",
                                        fontSize = 12.sp,
                                        color = Color(0xFF6B7280)
                                    )
                                }

                                // Stock status badge
                                val badgeBg = when {
                                    product.isOutOfStock -> Color(0xFFFEE2E2)
                                    product.isLowStock -> Color(0xFFFEF3C7)
                                    else -> Color(0xFFDCFCE7)
                                }
                                val badgeTextColor = when {
                                    product.isOutOfStock -> Color(0xFFB91C1C)
                                    product.isLowStock -> Color(0xFF92400E)
                                    else -> Color(0xFF166534)
                                }
                                val badgeText = when {
                                    product.isOutOfStock -> "Esgotado"
                                    product.isLowStock -> "Baixo Estoque"
                                    else -> "Em Estoque"
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(badgeBg)
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = badgeText,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = badgeTextColor
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Current Stock Counter & Progress Bar
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Estoque Atual: ${product.stockQuantity} un.",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF111827)
                                )

                                IconButton(
                                    onClick = {
                                        productToEdit = product
                                        editQuantityText = product.stockQuantity.toString()
                                    },
                                    modifier = Modifier.size(28.dp).testTag("edit_stock_btn_${product.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Editar",
                                        tint = Color(0xFF0284C7),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            LinearProgressIndicator(
                                progress = { (product.stockQuantity / 120f).coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = when {
                                    product.isOutOfStock -> Color(0xFFEF4444)
                                    product.isLowStock -> Color(0xFFF59E0B)
                                    else -> FreshGreenPrimary
                                },
                                trackColor = Color(0xFFF3F4F6)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Quick Adjust Buttons: [-5] [-1] [+1] [+5]
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                QuickDeltaButton("-5", isNegative = true) {
                                    onAdjustStock(product.id, -5)
                                }
                                QuickDeltaButton("-1", isNegative = true) {
                                    onAdjustStock(product.id, -1)
                                }
                                QuickDeltaButton("+1", isNegative = false) {
                                    onAdjustStock(product.id, 1)
                                }
                                QuickDeltaButton("+5", isNegative = false) {
                                    onAdjustStock(product.id, 5)
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Tab 1: Real-time Stock Movements Audit Trail
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (stockMovements.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Nenhuma movimentação registrada ainda.", color = Color(0xFF9CA3AF))
                        }
                    }
                } else {
                    items(stockMovements, key = { it.id }) { movement ->
                        val isPositive = movement.quantityChanged > 0
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(if (isPositive) Color(0xFFD1FADF) else Color(0xFFFEE2E2)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (isPositive) "+" else "-",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isPositive) FreshGreenPrimary else Color(0xFFDC2626)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${movement.type}: ${movement.productName}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF111827)
                                    )
                                    Text(
                                        text = movement.note.ifEmpty { "Registro automático de estoque" },
                                        fontSize = 11.sp,
                                        color = Color(0xFF6B7280)
                                    )
                                    val timeFormat = SimpleDateFormat("dd/MM HH:mm:ss", Locale.getDefault())
                                    Text(
                                        text = timeFormat.format(Date(movement.timestamp)),
                                        fontSize = 10.sp,
                                        color = Color(0xFF9CA3AF)
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "${if (isPositive) "+" else ""}${movement.quantityChanged}",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isPositive) FreshGreenPrimary else Color(0xFFDC2626)
                                    )
                                    Text(
                                        text = "Saldo: ${movement.newStockQuantity}",
                                        fontSize = 11.sp,
                                        color = Color(0xFF6B7280)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Direct Edit Quantity Dialog
    if (productToEdit != null) {
        val prod = productToEdit!!
        AlertDialog(
            onDismissRequest = { productToEdit = null },
            title = { Text("Ajustar Estoque de ${prod.name}") },
            text = {
                Column {
                    Text("Digite a nova quantidade exata em estoque:")
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = editQuantityText,
                        onValueChange = { editQuantityText = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val parsed = editQuantityText.toIntOrNull()
                        if (parsed != null && parsed >= 0) {
                            onSetStock(prod.id, parsed)
                            productToEdit = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FreshGreenPrimary)
                ) {
                    Text("Salvar")
                }
            },
            dismissButton = {
                TextButton(onClick = { productToEdit = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun KpiCard(
    title: String,
    value: String,
    color: Color,
    bgColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = color
            )
        }
    }
}

@Composable
private fun QuickDeltaButton(
    label: String,
    isNegative: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isNegative) Color(0xFFFEE2E2) else Color(0xFFDCFCE7))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (isNegative) Color(0xFFB91C1C) else Color(0xFF15803D)
        )
    }
}
