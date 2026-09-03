package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.data.model.Order
import com.example.ui.theme.FreshGreenPrimary
import com.example.ui.viewmodel.CartSummary
import java.util.Locale

@Composable
fun CheckoutScreen(
    cartSummary: CartSummary,
    deliveryAddress: String,
    onUpdateAddress: (String) -> Unit,
    selectedDeliveryMethod: String,
    onSelectDeliveryMethod: (String) -> Unit,
    selectedPaymentMethod: String,
    onSelectPaymentMethod: (String) -> Unit,
    isPlacingOrder: Boolean,
    onPlaceOrder: (onSuccess: (Order) -> Unit) -> Unit,
    onBack: () -> Unit,
    onViewOrders: () -> Unit,
    onGoHome: () -> Unit
) {
    var showAddressDialog by remember { mutableStateOf(false) }
    var tempAddress by remember { mutableStateOf(deliveryAddress) }
    var completedOrder by remember { mutableStateOf<Order?>(null) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFBFDFB))
            .statusBarsPadding()
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("checkout_back_btn")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF111827)
                )
            }

            Text(
                text = "Checkout",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111827),
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Delivery Address Card
            item {
                Column {
                    Text(
                        text = "Delivery Address",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF111827)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFD1FADF)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = FreshGreenPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Home",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF111827)
                                )
                                Text(
                                    text = deliveryAddress,
                                    fontSize = 12.sp,
                                    color = Color(0xFF6B7280)
                                )
                            }

                            Text(
                                text = "Change",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = FreshGreenPrimary,
                                modifier = Modifier
                                    .clickable {
                                        tempAddress = deliveryAddress
                                        showAddressDialog = true
                                    }
                                    .testTag("change_address_btn")
                            )
                        }
                    }
                }
            }

            // 2. Delivery Time Selection
            item {
                Column {
                    Text(
                        text = "Delivery Time",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF111827)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        DeliveryTimeCard(
                            title = "Express Delivery",
                            timeSubtitle = "30~45 mins",
                            isSelected = selectedDeliveryMethod == "Express Delivery",
                            onClick = { onSelectDeliveryMethod("Express Delivery") },
                            modifier = Modifier.weight(1f).testTag("delivery_express")
                        )

                        DeliveryTimeCard(
                            title = "Free Delivery",
                            timeSubtitle = "2-3 days",
                            isSelected = selectedDeliveryMethod == "Free Delivery",
                            onClick = { onSelectDeliveryMethod("Free Delivery") },
                            modifier = Modifier.weight(1f).testTag("delivery_free")
                        )
                    }
                }
            }

            // 3. Payment Method Selection
            // Payment Method Section with comprehensive Brazilian / International integrations
            item {
                Column {
                    Text(
                        text = "Forma de Pagamento",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF111827)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        PaymentOptionRow(
                            icon = "⚡",
                            title = "Pix Instantâneo (5% de Desconto)",
                            subtitle = "Aprovação em segundos com QR Code e Copia e Cola",
                            isSelected = selectedPaymentMethod == "Pix",
                            onClick = { onSelectPaymentMethod("Pix") },
                            tag = "payment_pix"
                        )

                        PaymentOptionRow(
                            icon = "💳",
                            title = "Cartão de Crédito / Débito",
                            subtitle = "Até 6x sem juros • Visa, Mastercard, Elo",
                            isSelected = selectedPaymentMethod == "Visa",
                            onClick = { onSelectPaymentMethod("Visa") },
                            tag = "payment_visa"
                        )

                        PaymentOptionRow(
                            icon = "📄",
                            title = "Boleto Bancário",
                            subtitle = "Compensação em até 3 dias úteis",
                            isSelected = selectedPaymentMethod == "Boleto",
                            onClick = { onSelectPaymentMethod("Boleto") },
                            tag = "payment_boleto"
                        )

                        PaymentOptionRow(
                            icon = "💵",
                            title = "Dinheiro na Entrega",
                            subtitle = "Pague ao entregador com troco fácil",
                            isSelected = selectedPaymentMethod == "Dinheiro",
                            onClick = { onSelectPaymentMethod("Dinheiro") },
                            tag = "payment_cash"
                        )
                    }
                }
            }

            // Interactive Pix Panel
            item {
                AnimatedVisibility(visible = selectedPaymentMethod == "Pix") {
                    var pixCopied by remember { mutableStateOf(false) }
                    val clipboardManager = androidx.compose.ui.platform.LocalClipboardManager.current
                    val pixKey = "00020126580014BR.GOV.BCB.PIX0136hortifrut-verde@pix.com5204000053039865405${String.format(Locale.US, "%.2f", cartSummary.total)}5802BR5913Hortifruti6009SaoPaulo62070503***6304"

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.5.dp, FreshGreenPrimary, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.QrCode2,
                                    contentDescription = "Pix QR",
                                    tint = FreshGreenPrimary,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "QR Code Pix Gerado",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF065F46)
                                    )
                                    Text(
                                        text = "Expira em 14:59 minutos",
                                        fontSize = 11.sp,
                                        color = Color(0xFF047857)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Simulated high-contrast QR Code matrix visual
                            Box(
                                modifier = Modifier
                                    .size(160.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White)
                                    .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(12.dp))
                                    .padding(12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.QrCode2,
                                    contentDescription = "Pix QR Code Visual",
                                    tint = Color(0xFF111827),
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Copy Pix Code Button
                            Button(
                                onClick = {
                                    clipboardManager.setText(androidx.compose.ui.text.AnnotatedString(pixKey))
                                    pixCopied = true
                                },
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (pixCopied) Color(0xFF059669) else FreshGreenPrimary
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("copy_pix_btn")
                            ) {
                                Icon(
                                    imageVector = if (pixCopied) Icons.Default.CheckCircle else Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (pixCopied) "Código Pix Copiado!" else "Copiar Código Pix (Copia e Cola)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Abra seu app do banco, escolha a opção Pix Copia e Cola e cole o código acima para concluir com 5% off.",
                                fontSize = 11.sp,
                                color = Color(0xFF4B5563),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }

            // Interactive Credit Card Panel
            item {
                AnimatedVisibility(visible = selectedPaymentMethod == "Visa") {
                    var cardNumber by remember { mutableStateOf("4532 8920 1200 4242") }
                    var cardHolder by remember { mutableStateOf("MARIA SILVA") }
                    var cardExpiry by remember { mutableStateOf("08/28") }
                    var cardCvv by remember { mutableStateOf("882") }
                    var selectedInstallment by remember { mutableStateOf(1) }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Visual Credit Card Simulator
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(140.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        androidx.compose.ui.graphics.Brush.linearGradient(
                                            listOf(Color(0xFF1E293B), Color(0xFF0F172A), Color(0xFF065F46))
                                        )
                                    )
                                    .padding(16.dp)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "HortiFrut Pay",
                                            color = Color(0xFF86EFAC),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "VISA / MASTER",
                                            color = Color.White,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    }

                                    Text(
                                        text = cardNumber.ifEmpty { "•••• •••• •••• ••••" },
                                        color = Color.White,
                                        fontSize = 16.sp,
                                        letterSpacing = 2.sp,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = cardHolder.ifEmpty { "NOME DO TITULAR" }.uppercase(),
                                            color = Color(0xFFE2E8F0),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = "VAL $cardExpiry",
                                            color = Color(0xFFE2E8F0),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Parcelamento Selector
                            Text(
                                text = "Opções de Parcelamento",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF111827)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                listOf(1, 2, 3, 6).forEach { times ->
                                    val isCurrent = selectedInstallment == times
                                    val installmentValue = cartSummary.total / times
                                    Card(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { selectedInstallment = times },
                                        shape = RoundedCornerShape(10.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isCurrent) Color(0xFFE8F5E9) else Color(0xFFF8FAFC)
                                        ),
                                        border = if (isCurrent) androidx.compose.foundation.BorderStroke(1.5.dp, FreshGreenPrimary) else null
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(6.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = "${times}x",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isCurrent) FreshGreenPrimary else Color(0xFF111827)
                                            )
                                            Text(
                                                text = String.format(Locale.US, "$%.2f", installmentValue),
                                                fontSize = 10.sp,
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

            // Interactive Boleto Panel
            item {
                AnimatedVisibility(visible = selectedPaymentMethod == "Boleto") {
                    var boletoCopied by remember { mutableStateOf(false) }
                    val clipboardManager = androidx.compose.ui.platform.LocalClipboardManager.current
                    val linhaDigitavel = "34191.79001 01043.510047 91020.150008 5 962300000${(cartSummary.total * 100).toInt()}"

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "Boleto Bancário Digital",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF111827)
                            )
                            Text(
                                text = "Vencimento: em 3 dias úteis",
                                fontSize = 11.sp,
                                color = Color(0xFF6B7280)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = linhaDigitavel,
                                fontSize = 11.sp,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                color = Color(0xFF334155),
                                modifier = Modifier
                                    .background(Color.White, RoundedCornerShape(8.dp))
                                    .padding(8.dp)
                                    .fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedButton(
                                onClick = {
                                    clipboardManager.setText(androidx.compose.ui.text.AnnotatedString(linhaDigitavel))
                                    boletoCopied = true
                                },
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(if (boletoCopied) "Código de Barras Copiado!" else "Copiar Linha Digitável do Boleto")
                            }
                        }
                    }
                }
            }

            // Interactive Dinheiro Panel
            item {
                AnimatedVisibility(visible = selectedPaymentMethod == "Dinheiro") {
                    var changeOption by remember { mutableStateOf("Sem troco (Troco exato)") }
                    val changeOptions = listOf("Sem troco", "Troco p/ R$ 50", "Troco p/ R$ 100", "Troco p/ R$ 200")

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEFCE8))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "Precisa de troco para a entrega?",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF854D0E)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                changeOptions.forEach { opt ->
                                    val isSel = changeOption == opt
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (isSel) Color(0xFFCA8A04) else Color.White)
                                            .border(1.dp, if (isSel) Color(0xFFA16207) else Color(0xFFE5E7EB), RoundedCornerShape(10.dp))
                                            .clickable { changeOption = opt }
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = opt,
                                            fontSize = 10.sp,
                                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSel) Color.White else Color(0xFF4B5563),
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Bottom Bar: Total Amount + Place Order Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 14.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Total Amount",
                        fontSize = 14.sp,
                        color = Color(0xFF6B7280)
                    )
                    Text(
                        text = String.format(Locale.US, "$%.2f", cartSummary.total),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF111827)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        onPlaceOrder { order ->
                            completedOrder = order
                            showSuccessDialog = true
                        }
                    },
                    enabled = !isPlacingOrder && cartSummary.totalItemsCount > 0,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("place_order_button"),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FreshGreenPrimary,
                        contentColor = Color.White
                    )
                ) {
                    if (isPlacingOrder) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = Color.White,
                            strokeWidth = 2.5.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Processando Pagamento...", fontSize = 15.sp)
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Place Order",
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

    // Change Address Dialog
    if (showAddressDialog) {
        AlertDialog(
            onDismissRequest = { showAddressDialog = false },
            title = { Text("Alterar Endereço de Entrega", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Digite o novo endereço completo para entrega rápida:")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = tempAddress,
                        onValueChange = { tempAddress = it },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateAddress(tempAddress)
                        showAddressDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FreshGreenPrimary)
                ) {
                    Text("Salvar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddressDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Order Success Dialog
    if (showSuccessDialog && completedOrder != null) {
        val order = completedOrder!!
        AlertDialog(
            onDismissRequest = { /* force action */ },
            title = null,
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFD1FADF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Success",
                            tint = FreshGreenPrimary,
                            modifier = Modifier.size(46.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Pedido Realizado com Sucesso!",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF111827)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Código do Pedido: ${order.orderNumber}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = FreshGreenPrimary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F4F6)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "⚡ Estoque Atualizado em Tempo Real!",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF065F46)
                            )
                            Text(
                                text = "Os itens comprados foram deduzidos automaticamente do inventário local.",
                                fontSize = 11.sp,
                                color = Color(0xFF4B5563)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Método: ${order.paymentMethod}\nTotal: ${String.format(Locale.US, "$%.2f", order.totalAmount)}",
                        fontSize = 13.sp,
                        color = Color(0xFF4B5563)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        onViewOrders()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FreshGreenPrimary)
                ) {
                    Text("Ver Meus Pedidos")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        showSuccessDialog = false
                        onGoHome()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("Voltar ao Início")
                }
            }
        )
    }
}

@Composable
private fun DeliveryTimeCard(
    title: String,
    timeSubtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable(onClick = onClick)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) FreshGreenPrimary else Color(0xFFE5E7EB),
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFF0FDF4) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .border(
                            width = if (isSelected) 6.dp else 1.5.dp,
                            color = if (isSelected) FreshGreenPrimary else Color(0xFFD1D5DB),
                            shape = CircleShape
                        )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111827)
            )

            Text(
                text = timeSubtitle,
                fontSize = 11.sp,
                color = Color(0xFF6B7280)
            )
        }
    }
}

@Composable
private fun PaymentOptionRow(
    icon: String,
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    tag: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag(tag)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) FreshGreenPrimary else Color(0xFFE5E7EB),
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFF0FDF4) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = icon, fontSize = 22.sp)

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111827)
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = Color(0xFF6B7280)
                )
            }

            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) FreshGreenPrimary else Color.Transparent)
                    .border(
                        width = if (isSelected) 0.dp else 1.5.dp,
                        color = if (isSelected) FreshGreenPrimary else Color(0xFFD1D5DB),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}
