package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import com.example.ui.theme.FreshGreenPrimary

data class FaqItem(
    val id: Int,
    val question: String,
    val answer: String
)

@Composable
fun HelpCenterScreen(
    onBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val expandedSet = remember { mutableStateListOf(1) }

    val faqs = remember {
        listOf(
            FaqItem(
                id = 1,
                question = "Como funciona a entrega expressa?",
                answer = "Nossas entregas expressas saem direto dos centros de distribuição mais próximos da sua região em até 15 a 30 minutos após a confirmação do pedido."
            ),
            FaqItem(
                id = 2,
                question = "Os vegetais e frutas são realmente orgânicos?",
                answer = "Sim! Trabalhamos com produtores certificados. Nossos produtos são colhidos diariamente sem agrotóxicos ou pesticidas sintéticos."
            ),
            FaqItem(
                id = 3,
                question = "Como rastrear o meu pedido no mapa?",
                answer = "Na aba 'Pedidos', toque em qualquer pedido em andamento para abrir o mapa ao vivo. Você verá a localização exata do entregador e poderá até conversar via chat."
            ),
            FaqItem(
                id = 4,
                question = "Quais são as formas de pagamento disponíveis?",
                answer = "Aceitamos Pix Instantâneo (com 5% de desconto automático), Cartões de Crédito e Débito (Visa, Mastercard, Elo), Boleto Bancário e Dinheiro na entrega."
            ),
            FaqItem(
                id = 5,
                question = "Como solicitar troca se algum item chegar danificado?",
                answer = "Garantimos frescor 100%. Caso algo não atenda ao seu padrão de qualidade, abra o chat do pedido ou fale com nosso suporte para reembolso ou reenvio imediato."
            )
        )
    }

    val filteredFaqs = faqs.filter {
        it.question.contains(searchQuery, ignoreCase = true) || it.answer.contains(searchQuery, ignoreCase = true)
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
                modifier = Modifier.testTag("help_back_btn")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }

            Text(
                text = "Central de Ajuda",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        // Search Input
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Buscar dúvidas frequentes...", fontSize = 14.sp) },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = Color(0xFF9CA3AF))
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color(0xFFE5E7EB),
                focusedBorderColor = FreshGreenPrimary
            ),
            singleLine = true
        )

        // FAQ List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Dúvidas Frequentes (FAQ)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            items(filteredFaqs, key = { it.id }) { faq ->
                val isExpanded = expandedSet.contains(faq.id)

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (isExpanded) expandedSet.remove(faq.id) else expandedSet.add(faq.id)
                        },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = faq.question,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null,
                                tint = FreshGreenPrimary
                            )
                        }

                        AnimatedVisibility(visible = isExpanded) {
                            Column {
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = faq.answer,
                                    fontSize = 13.sp,
                                    color = Color(0xFF6B7280),
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            }

            // Contact Channels
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Ainda precisa de ajuda?",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { /* WhatsApp support simulation */ },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FreshGreenPrimary)
                    ) {
                        Icon(imageVector = Icons.Default.HeadsetMic, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Suporte 24h", fontSize = 12.sp)
                    }

                    Button(
                        onClick = { /* Email support */ },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB))
                    ) {
                        Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = FreshGreenPrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("E-mail", color = MaterialTheme.colorScheme.onBackground, fontSize = 12.sp)
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
