package com.example.ui.screens

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
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CartEntity
import com.example.ui.components.ProductVisual
import com.example.ui.theme.FreshGreenPrimary
import com.example.ui.viewmodel.CartSummary
import java.util.Locale

@Composable
fun CartScreen(
    cartItems: List<CartEntity>,
    cartSummary: CartSummary,
    promoCodeInput: String,
    onPromoCodeChange: (String) -> Unit,
    onApplyPromo: () -> Unit,
    promoMessage: String?,
    onBack: () -> Unit,
    onUpdateQuantity: (Long, Int) -> Unit,
    onClearCart: () -> Unit,
    onProceedToCheckout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFBFDFB))
            .statusBarsPadding()
    ) {
        // Top Bar: Back, "My Cart", Subtitle item count, "Edit" / "Limpar"
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("cart_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF111827)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "My Cart",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111827)
                )
                Text(
                    text = "${cartSummary.totalItemsCount} Items",
                    fontSize = 12.sp,
                    color = Color(0xFF6B7280)
                )
            }

            if (cartItems.isNotEmpty()) {
                TextButton(
                    onClick = onClearCart,
                    modifier = Modifier.testTag("clear_cart_btn")
                ) {
                    Text(
                        text = "Limpar",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFEF4444)
                    )
                }
            } else {
                Spacer(modifier = Modifier.width(48.dp))
            }
        }

        if (cartItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF3F4F6)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingBag,
                            contentDescription = null,
                            tint = Color(0xFF9CA3AF),
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Seu carrinho está vazio",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF374151)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Adicione produtos frescos da nossa horta!",
                        fontSize = 13.sp,
                        color = Color(0xFF9CA3AF)
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    Button(
                        onClick = onBack,
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FreshGreenPrimary)
                    ) {
                        Text("Ver Produtos")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Cart Items List
                items(cartItems, key = { it.productId }) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("cart_item_${item.productId}"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ProductVisual(
                                imageResName = item.imageResName,
                                modifier = Modifier.size(60.dp)
                            )

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.productName,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF111827)
                                )
                                Text(
                                    text = item.productUnit,
                                    fontSize = 12.sp,
                                    color = Color(0xFF6B7280)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = String.format(Locale.US, "$%.2f", item.productPrice),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF111827)
                                )
                            }

                            // Stepper [- 1 +]
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color(0xFFF3F4F6))
                                    .padding(horizontal = 4.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = { onUpdateQuantity(item.productId, item.quantity - 1) },
                                    modifier = Modifier.size(32.dp).testTag("cart_minus_${item.productId}")
                                ) {
                                    Icon(
                                        imageVector = if (item.quantity == 1) Icons.Default.DeleteOutline else Icons.Default.Remove,
                                        contentDescription = "Diminuir",
                                        tint = if (item.quantity == 1) Color(0xFFEF4444) else Color(0xFF4B5563),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                Text(
                                    text = "${item.quantity}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF111827),
                                    modifier = Modifier.padding(horizontal = 6.dp)
                                )

                                IconButton(
                                    onClick = { onUpdateQuantity(item.productId, item.quantity + 1) },
                                    modifier = Modifier.size(32.dp).testTag("cart_plus_${item.productId}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Aumentar",
                                        tint = Color(0xFF4B5563),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Promo code section
                item {
                    Column(modifier = Modifier.padding(top = 10.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFFF3F4F6))
                                .padding(horizontal = 14.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = promoCodeInput,
                                onValueChange = onPromoCodeChange,
                                placeholder = {
                                    Text(
                                        text = "Have a promo code?",
                                        fontSize = 13.sp,
                                        color = Color(0xFF9CA3AF)
                                    )
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("promo_input"),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color.Transparent,
                                    unfocusedBorderColor = Color.Transparent,
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent
                                )
                            )

                            Text(
                                text = "Apply",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = FreshGreenPrimary,
                                modifier = Modifier
                                    .clickable(onClick = onApplyPromo)
                                    .padding(8.dp)
                                    .testTag("promo_apply_btn")
                            )
                        }

                        if (promoMessage != null) {
                            Text(
                                text = promoMessage,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (cartSummary.discount > 0) FreshGreenPrimary else Color(0xFFEF4444),
                                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                            )
                        }
                    }
                }

                // Price Summary Card
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Subtotal", fontSize = 14.sp, color = Color(0xFF6B7280))
                                Text(
                                    text = String.format(Locale.US, "$%.2f", cartSummary.subtotal),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF111827)
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Delivery Fee", fontSize = 14.sp, color = Color(0xFF6B7280))
                                Text(
                                    text = if (cartSummary.deliveryFee == 0.0) "FREE" else String.format(Locale.US, "$%.2f", cartSummary.deliveryFee),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (cartSummary.deliveryFee == 0.0) FreshGreenPrimary else Color(0xFF111827)
                                )
                            }

                            if (cartSummary.discount > 0) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Discount Coupon", fontSize = 14.sp, color = FreshGreenPrimary)
                                    Text(
                                        text = String.format(Locale.US, "-$%.2f", cartSummary.discount),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = FreshGreenPrimary
                                    )
                                }
                            }

                            HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 1.dp)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Total",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF111827)
                                )
                                Text(
                                    text = String.format(Locale.US, "$%.2f", cartSummary.total),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF111827)
                                )
                            }
                        }
                    }
                }
            }

            // Bottom Proceed to Checkout Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Button(
                    onClick = onProceedToCheckout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("proceed_to_checkout_btn"),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FreshGreenPrimary,
                        contentColor = Color.White
                    )
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Proceed to Checkout",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
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
