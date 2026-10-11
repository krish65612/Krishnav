package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.SubscriptionTier
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class PaymentMethodType {
    PAYMENT_APPS, // Google Pay, PhonePe, Paytm, UPI
    CARD,
    PAYPAL
}

@Composable
fun PaymentGatewayDialog(
    tier: SubscriptionTier?,
    isDarkTheme: Boolean,
    onDismiss: () -> Unit,
    onPaymentSuccess: (String) -> Unit
) {
    if (tier == null) return

    val coroutineScope = rememberCoroutineScope()
    var isProcessing by remember { mutableStateOf(false) }
    var isConfirmed by remember { mutableStateOf(false) }

    var selectedMethod by remember { mutableStateOf(PaymentMethodType.PAYMENT_APPS) }
    var selectedApp by remember { mutableStateOf("Google Pay") }
    var upiId by remember { mutableStateOf("krishnav@upi") }

    var cardNumber by remember { mutableStateOf("4242 •••• •••• 9842") }
    var expiryDate by remember { mutableStateOf("10/28") }
    var cvv by remember { mutableStateOf("842") }

    val bgColor = if (isDarkTheme) ObsidianCardElevated else PureWhiteCardElevated
    val textColor = if (isDarkTheme) TextWhitePrimary else TextDarkPrimary
    val textSub = if (isDarkTheme) TextWhiteSecondary else TextDarkSecondary

    Dialog(
        onDismissRequest = { if (!isProcessing) onDismiss() },
        properties = DialogProperties(dismissOnClickOutside = !isProcessing, usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .padding(vertical = 16.dp)
                .testTag("payment_gateway_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = bgColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (!isConfirmed) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.Security,
                                contentDescription = "Secure",
                                tint = LiveGreen,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "UNIVERSAL PAYMENT GATEWAY",
                                color = LiveGreen,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        IconButton(
                            onClick = onDismiss,
                            enabled = !isProcessing,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = textSub)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Tier Summary Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(NeonMagenta.copy(alpha = 0.12f))
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = tier.name,
                                    color = textColor,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Monthly Indie Subscription • Ad-Free",
                                    color = textSub,
                                    fontSize = 11.sp
                                )
                            }
                            Text(
                                text = "$${tier.priceMonthly}/mo",
                                color = NeonMagenta,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Choose Any Payment Method or App",
                        color = textColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Method Selector Tabs
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PaymentTabChip("Payment Apps (UPI/GPay)", selectedMethod == PaymentMethodType.PAYMENT_APPS, textColor) {
                            selectedMethod = PaymentMethodType.PAYMENT_APPS
                        }
                        PaymentTabChip("Card", selectedMethod == PaymentMethodType.CARD, textColor) {
                            selectedMethod = PaymentMethodType.CARD
                        }
                        PaymentTabChip("PayPal", selectedMethod == PaymentMethodType.PAYPAL, textColor) {
                            selectedMethod = PaymentMethodType.PAYPAL
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Method Specific Fields
                    when (selectedMethod) {
                        PaymentMethodType.PAYMENT_APPS -> {
                            // Any payment app selection: Google Pay, PhonePe, Paytm, Amazon Pay
                            Text(
                                text = "Select Installed Payment App:",
                                color = textSub,
                                fontSize = 11.sp,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            val appOptions = listOf("Google Pay", "PhonePe", "Paytm", "Any UPI App")
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                appOptions.forEach { appName ->
                                    val isAppSelected = selectedApp == appName
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(
                                                if (isAppSelected) NeonCyan.copy(alpha = 0.2f)
                                                else (if (isDarkTheme) ObsidianCard else PureWhiteSurface)
                                            )
                                            .border(
                                                width = 1.dp,
                                                color = if (isAppSelected) NeonCyan else Color.Transparent,
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .clickable { selectedApp = appName }
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = appName,
                                            fontSize = 10.sp,
                                            fontWeight = if (isAppSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isAppSelected) (if (isDarkTheme) NeonCyan else Color(0xFF006B78)) else textSub
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = upiId,
                                onValueChange = { upiId = it },
                                label = { Text("Or Enter Any UPI ID (e.g. name@bank)", fontSize = 11.sp) },
                                leadingIcon = {
                                    Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = NeonCyan)
                                },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                enabled = !isProcessing
                            )
                        }

                        PaymentMethodType.CARD -> {
                            OutlinedTextField(
                                value = cardNumber,
                                onValueChange = { cardNumber = it },
                                label = { Text("Debit / Credit Card (Visa, MC, Amex)", fontSize = 11.sp) },
                                leadingIcon = {
                                    Icon(Icons.Default.CreditCard, contentDescription = null, tint = NeonCyan)
                                },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                enabled = !isProcessing
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = expiryDate,
                                    onValueChange = { expiryDate = it },
                                    label = { Text("MM/YY", fontSize = 11.sp) },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    enabled = !isProcessing
                                )
                                OutlinedTextField(
                                    value = cvv,
                                    onValueChange = { cvv = it },
                                    label = { Text("CVV", fontSize = 11.sp) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    enabled = !isProcessing
                                )
                            }
                        }

                        PaymentMethodType.PAYPAL -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isDarkTheme) ObsidianCard else PureWhiteSurface)
                                    .padding(14.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Payment, contentDescription = null, tint = NeonCyan)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "Connected PayPal Account",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = textColor
                                        )
                                        Text(
                                            text = "krish***@gmail.com • Auto-renews monthly",
                                            fontSize = 11.sp,
                                            color = textSub
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Guarantees
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = LiveGreen,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Ads permanently removed on all songs",
                            color = textSub,
                            fontSize = 11.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    if (isProcessing) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = NeonCyan, modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Connecting to ${if (selectedMethod == PaymentMethodType.PAYMENT_APPS) selectedApp else "Payment Gateway"}...",
                                color = textSub,
                                fontSize = 12.sp
                            )
                        }
                    } else {
                        Button(
                            onClick = {
                                isProcessing = true
                                coroutineScope.launch {
                                    delay(1600)
                                    isProcessing = false
                                    isConfirmed = true
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("pay_confirm_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = LiveGreen),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Icon(Icons.Outlined.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Pay with ${if (selectedMethod == PaymentMethodType.PAYMENT_APPS) selectedApp else "Secure Gateway"} ($${tier.priceMonthly})",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                } else {
                    // Success Confirmation Receipt
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(vertical = 12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(LiveGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Success",
                                tint = Color.White,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Payment Authorized!",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Paid via $selectedApp for ${tier.name}.",
                            color = textSub,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(NeonCyan.copy(alpha = 0.2f))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "✨ 100% AD-FREE & VIP BADGE UNLOCKED",
                                color = NeonCyan,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = { onPaymentSuccess(tier.id) },
                            colors = ButtonDefaults.buttonColors(containerColor = textColor),
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("done_subscription_button")
                        ) {
                            Text(
                                text = "Listen Ad-Free Now",
                                color = bgColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RowScope.PaymentTabChip(
    title: String,
    isSelected: Boolean,
    textColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) textColor else Color.Transparent)
            .border(
                width = 1.dp,
                color = if (isSelected) textColor else textColor.copy(alpha = 0.2f),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) (if (textColor == TextWhitePrimary) Color.Black else Color.White) else textColor
        )
    }
}
