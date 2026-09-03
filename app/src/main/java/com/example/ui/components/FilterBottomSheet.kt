package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.FreshGreenPrimary
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    isOpen: Boolean,
    minPrice: Float,
    maxPrice: Float,
    onlyInStock: Boolean,
    minRating: Float,
    selectedCategory: String,
    onCategorySelect: (String) -> Unit,
    onApply: (Float, Float, Boolean, Float) -> Unit,
    onReset: () -> Unit,
    onDismiss: () -> Unit
) {
    if (!isOpen) return

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var currentMinPrice by remember { mutableFloatStateOf(minPrice) }
    var currentMaxPrice by remember { mutableFloatStateOf(maxPrice) }
    var currentOnlyInStock by remember { mutableStateOf(onlyInStock) }
    var currentMinRating by remember { mutableFloatStateOf(minRating) }

    val categories = listOf("Todos", "Frutas", "Legumes", "Folhas", "Raízes", "Orgânicos")
    val ratingOptions = listOf(0f to "Todas", 3f to "3★ ou mais", 4f to "4★ ou mais", 4.5f to "4.5★+")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filtros de Produtos",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Fechar",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Categories Filter
            Text(
                text = "Categorias",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { cat ->
                    val isSelected = (cat == "Todos" && (selectedCategory.isEmpty() || selectedCategory == "All" || selectedCategory == "Todos")) ||
                            (cat.equals(selectedCategory, ignoreCase = true))
                    Card(
                        modifier = Modifier.clickable {
                            onCategorySelect(if (cat == "Todos") "All" else cat)
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) FreshGreenPrimary else MaterialTheme.colorScheme.background
                        ),
                        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB))
                    ) {
                        Text(
                            text = cat,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Price Range Slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Faixa de Preço",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = String.format(Locale.US, "$%.2f - $%.2f", currentMinPrice, currentMaxPrice),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = FreshGreenPrimary
                )
            }
            RangeSlider(
                value = currentMinPrice..currentMaxPrice,
                onValueChange = { range ->
                    currentMinPrice = range.start
                    currentMaxPrice = range.endInclusive
                },
                valueRange = 0f..50f,
                colors = SliderDefaults.colors(
                    thumbColor = FreshGreenPrimary,
                    activeTrackColor = FreshGreenPrimary,
                    inactiveTrackColor = Color(0xFFE5E7EB)
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // In Stock Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Apenas Produtos em Estoque",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Ocultar itens esgotados temporariamente",
                        fontSize = 11.sp,
                        color = Color(0xFF9CA3AF)
                    )
                }
                Switch(
                    checked = currentOnlyInStock,
                    onCheckedChange = { currentOnlyInStock = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = FreshGreenPrimary
                    )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Min Rating
            Text(
                text = "Avaliação Mínima",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(ratingOptions) { (rating, label) ->
                    val isSelected = currentMinRating == rating
                    Card(
                        modifier = Modifier.clickable { currentMinRating = rating },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) FreshGreenPrimary else MaterialTheme.colorScheme.background
                        ),
                        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            if (rating > 0f) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = if (isSelected) Color.White else Color(0xFFF59E0B),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(26.dp))

            // Bottom Actions: Reset & Apply
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        currentMinPrice = 0f
                        currentMaxPrice = 50f
                        currentOnlyInStock = false
                        currentMinRating = 0f
                        onReset()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text("Limpar", color = Color(0xFF6B7280))
                }

                Button(
                    onClick = {
                        onApply(currentMinPrice, currentMaxPrice, currentOnlyInStock, currentMinRating)
                        onDismiss()
                    },
                    modifier = Modifier
                        .weight(1.5f)
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FreshGreenPrimary)
                ) {
                    Text("Aplicar Filtros", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
