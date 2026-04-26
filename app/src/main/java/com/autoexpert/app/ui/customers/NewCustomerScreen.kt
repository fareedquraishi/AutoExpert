@file:OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
package com.autoexpert.app.ui.customers

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.autoexpert.app.data.local.entity.*
import com.autoexpert.app.ui.components.*
import com.autoexpert.app.ui.theme.*

// Vehicle icon mapping from iconKey
fun vehicleIcon(iconKey: String): String = when(iconKey.lowercase()) {
    "car"        -> "🚗"
    "motorcycle" -> "🏍️"
    "van"        -> "🚐"
    "truck"      -> "🚛"
    "suv"        -> "🚙"
    "rickshaw"   -> "🛺"
    "heavy"      -> "🚌"
    "tractor"    -> "🚜"
    "pickup"     -> "🛻"
    else         -> "🚙"
}


@Composable
fun NewCustomerScreen(
    onBack: () -> Unit,
    onSuccess: () -> Unit,
    vm: NewCustomerViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsState()

    if (state.submitSuccess) {
        SuccessScreen(
            commission = vm.totalCommission,
            onNewCustomer = { vm.reset() },
            onDone = onSuccess
        )
        return
    }

    Column(Modifier.fillMaxSize().background(BackgroundGray)) {

        // App bar
        Surface(color = Color.White, shadowElevation = 1.dp) {
            Column(Modifier.statusBarsPadding()) {
                Row(
                    Modifier.fillMaxWidth().padding(14.dp, 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { if (state.step > 1) vm.prevStep() else onBack() },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(
                            Modifier.fillMaxSize().background(BackgroundGray, RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.ArrowBack, null, tint = TextPrimary, modifier = Modifier.size(18.dp))
                        }
                    }
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text("New Customer", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = TextPrimary)
                        Text(
                            "Step ${state.step} of 3 — ${listOf("Customer Info", "Select Products", "Confirm & Submit")[state.step - 1]}",
                            fontSize = 10.sp, color = TextSecondary
                        )
                    }
                    if (state.step == 2) {
                        val sel = vm.selectedItems.size
                        if (sel > 0) {
                            Surface(
                                color = PetronasGreenLight, shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, PetronasGreen.copy(.25f))
                            ) {
                                Text(
                                    "$sel selected", fontSize = 11.sp, fontWeight = FontWeight.Bold,
                                    color = PetronasGreenDark, modifier = Modifier.padding(10.dp, 5.dp)
                                )
                            }
                        }
                    }
                }
                // Step progress bar
                Row(Modifier.fillMaxWidth().padding(horizontal = 14.dp).padding(bottom = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    repeat(3) { i ->
                        Box(
                            Modifier.weight(1f).height(3.dp)
                                .background(
                                    when {
                                        i < state.step - 1 -> Brush.horizontalGradient(listOf(PetronasGreen, PetronasGreen))
                                        i == state.step - 1 -> Brush.horizontalGradient(listOf(PetronasGreen, BorderColor))
                                        else -> Brush.horizontalGradient(listOf(BorderColor, BorderColor))
                                    },
                                    RoundedCornerShape(2.dp)
                                )
                        )
                    }
                }
            }
        }

        // Step content
        when (state.step) {
            1 -> Step1CustomerInfo(state, vm)
            2 -> Step2Products(state, vm)
            3 -> Step3Confirm(state, vm)
        }
    }
}

@Composable
private fun Step1CustomerInfo(state: CustomerEntryState, vm: NewCustomerViewModel) {
    Column(Modifier.fillMaxSize()) {
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState())
                .padding(13.dp)
        ) {
            // Customer info card
            FormCard {
                SectionHeader(title = "CUSTOMER DETAILS", icon = "👤")
                FormField("Full Name") {
                    OutlinedTextField(
                        value = state.customerName,
                        onValueChange = vm::onNameChanged,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("e.g. Ahmad Raza", color = TextDim) },
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                        shape = RoundedCornerShape(12.dp),
                        colors = outlinedTextFieldColors(),
                        singleLine = true
                    )
                }
                FormField("Mobile Number") {
                    OutlinedTextField(
                        value = state.mobile, onValueChange = vm::onMobileChanged,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("03xx-xxxxxxx", color = TextDim) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        shape = RoundedCornerShape(12.dp),
                        colors = outlinedTextFieldColors(), singleLine = true
                    )
                }
                FormField("Plate Number") {
                    OutlinedTextField(
                        value = state.plateNumber, onValueChange = vm::onPlateChanged,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("e.g. LQN-450", color = TextDim) },
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
                        shape = RoundedCornerShape(12.dp),
                        colors = outlinedTextFieldColors(), singleLine = true
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            // Vehicle type
            FormCard {
                SectionHeader(title = "VEHICLE TYPE", icon = "🚗")
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    horizontalArrangement = Arrangement.spacedBy(7.dp),
                    verticalArrangement = Arrangement.spacedBy(7.dp),
                    modifier = Modifier.heightIn(max = 200.dp)
                ) {
                    items(state.vehicleTypes.size) { i ->
                        val vt = state.vehicleTypes[i]
                        val sel = state.vehicleTypeId == vt.id
                        Column(
                            modifier = Modifier
                                .border(2.dp,
                                    if (sel) PetronasGreen else BorderColor,
                                    RoundedCornerShape(12.dp))
                                .background(
                                    if (sel) PetronasGreenLight else BackgroundGray,
                                    RoundedCornerShape(12.dp))
                                .clickable { vm.onVehicleSelected(vt.id, vt.name) }
                                .padding(4.dp, 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(vehicleIcon(vt.iconKey))
                        Text(vt.name, fontSize = 9.sp)
                            Text(vt.name, fontSize = 8.sp, fontWeight = FontWeight.Bold,
                                color = if (sel) PetronasGreenDark else TextSecondary)
                        }
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            // Toggles + competitor
            FormCard {
                SectionHeader(title = "STATUS", icon = "🏷️")
                ToggleRow("Petronas Customer", "Already using Petronas?",
                    state.isRepeat, vm::onRepeatToggle)
                HorizontalDivider(color = BorderColor.copy(.5f))
                if (!state.isRepeat && state.competitorBrands.isNotEmpty()) {
                    Spacer(Modifier.height(10.dp))
                    Text("Previous Brand", fontSize = 11.sp, fontWeight = FontWeight.Bold,
                        color = TextSecondary, letterSpacing = .5.sp)
                    Spacer(Modifier.height(7.dp))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        state.competitorBrands.forEach { brand ->
                            val sel = state.competitorBrandId == brand.id
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = if (sel) AccentRed.copy(.07f) else BackgroundGray,
                                border = BorderStroke(1.5.dp, if (sel) AccentRed else BorderColor),
                                modifier = Modifier.clickable { vm.onBrandSelected(brand.id, brand.name) }
                            ) {
                                Text(brand.name, fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
                                    color = if (sel) AccentRed else TextPrimary,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
                            }
                        }
                    }
                }
            }
        }

        // Footer
        Surface(color = Color.White, shadowElevation = 4.dp) {
            Row(
                Modifier.fillMaxWidth().navigationBarsPadding().padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PrimaryButton(
                    "Next: Products →", onClick = vm::nextStep,
                    modifier = Modifier.weight(1f),
                    enabled = state.customerName.isNotBlank() && state.vehicleTypeId.isNotEmpty()
                )
            }
        }
    }
}

@Composable
private fun Step2Products(state: CustomerEntryState, vm: NewCustomerViewModel) {
    Column(Modifier.fillMaxSize()) {
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(12.dp)
        ) {
            // Customer summary chip
            Row(
                Modifier.fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
                    .padding(9.dp, 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(9.dp)
            ) {
                Box(
                    Modifier.size(32.dp)
                        .background(GreenGradient, RoundedCornerShape(9.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(state.customerName.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                        fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                }
                Column {
                    Text(state.customerName, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text("${state.vehicleTypeName} · ${state.plateNumber.ifEmpty { "No plate" }}",
                        fontSize = 10.sp, color = TextSecondary)
                }
            }

            Spacer(Modifier.height(10.dp))

            // Products — 3-column compact grid
            FormCard {
                SectionHeader(title = "PRODUCTS", icon = "🛢️")
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(7.dp),
                    verticalArrangement = Arrangement.spacedBy(7.dp),
                    modifier = Modifier.heightIn(max = 800.dp) // allows scrolling with outer scroll
                ) {
                    items(state.skus.size) { i ->
                        val sku = state.skus[i]
                        val item = state.cart[sku.id]!!
                        ProductCard(sku = sku, qty = item.qty,
                            onIncrement = { vm.incrementQty(sku.id) },
                            onDecrement = { vm.decrementQty(sku.id) })
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            // Competitor brand
            if (!state.isRepeat && state.competitorBrands.isNotEmpty()) {
                FormCard {
                    SectionHeader(title = "PREVIOUS BRAND", icon = "🏷️")
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        state.competitorBrands.forEach { brand ->
                            val sel = state.competitorBrandId == brand.id
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = if (sel) AccentRed.copy(.07f) else BackgroundGray,
                                border = BorderStroke(1.5.dp, if (sel) AccentRed else BorderColor),
                                modifier = Modifier.clickable { vm.onBrandSelected(brand.id, brand.name) }
                            ) {
                                Text(brand.name, fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
                                    color = if (sel) AccentRed else TextPrimary,
                                    modifier = Modifier.padding(11.dp, 5.dp))
                            }
                        }
                    }
                }
                Spacer(Modifier.height(10.dp))
            }

            // Applicator toggle
            FormCard {
                SectionHeader(title = "OPTIONS", icon = "⚙️")
                ToggleRow("Applicator", "Mechanic / Workshop", state.isApplicator, vm::onApplicatorToggle)
            }
        }

        // Footer
        Surface(color = Color.White, shadowElevation = 4.dp) {
            Row(
                Modifier.fillMaxWidth().navigationBarsPadding().padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlineButton("← Back", onClick = vm::prevStep, modifier = Modifier.width(90.dp))
                PrimaryButton("Confirm →", onClick = vm::nextStep, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun ProductCard(sku: SkuEntity, qty: Int, onIncrement: () -> Unit, onDecrement: () -> Unit) {
    val selected = qty > 0
    Box(
        modifier = Modifier
            .border(
                if (selected) 2.dp else 1.5.dp,
                if (selected) PetronasGreen else BorderColor,
                RoundedCornerShape(12.dp)
            )
            .background(
                if (selected) PetronasGreenLight else BackgroundGray,
                RoundedCornerShape(12.dp)
            )
            .clickable { onIncrement() }
    ) {
        // Top accent bar
        Box(
            Modifier.fillMaxWidth().height(2.dp)
                .background(
                    if (selected) GreenGradient
                    else Brush.linearGradient(listOf(BorderColor, BorderColor)),
                    RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                )
        )
        Column(Modifier.padding(8.dp, 10.dp, 8.dp, 8.dp)) {
            Text(sku.name, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold,
                color = TextPrimary, lineHeight = 13.sp)
            Text("${sku.volumeLitres}L/pk", fontSize = 9.sp, color = TextSecondary,
                modifier = Modifier.padding(top = 2.dp))
            Text("₨${sku.sellingPrice.toLong()}", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold,
                color = PetronasGreenDark, modifier = Modifier.padding(top = 4.dp))
            Text("/pk", fontSize = 8.sp, color = TextDim)

            HorizontalDivider(Modifier.padding(vertical = 6.dp), color = BorderColor.copy(.5f))

            // Qty row
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    Modifier.size(22.dp)
                        .background(Color.White, RoundedCornerShape(6.dp))
                        .border(1.dp, BorderColor, RoundedCornerShape(6.dp))
                        .clickable { onDecrement() },
                    contentAlignment = Alignment.Center
                ) { Text("−", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary) }

                Text(qty.toString(), fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary)

                Box(
                    Modifier.size(22.dp)
                        .background(
                            if (selected) PetronasGreen else Color.White,
                            RoundedCornerShape(6.dp))
                        .border(1.dp, if (selected) PetronasGreen else BorderColor, RoundedCornerShape(6.dp))
                        .clickable { onIncrement() },
                    contentAlignment = Alignment.Center
                ) { Text("+", fontSize = 14.sp, fontWeight = FontWeight.Bold,
                    color = if (selected) Color.White else TextPrimary) }
            }
        }
    }
}

@Composable
private fun Step3Confirm(state: CustomerEntryState, vm: NewCustomerViewModel) {
    Column(Modifier.fillMaxSize()) {
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(13.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            FormCard {
                SectionHeader(title = "CUSTOMER", icon = "👤")
                ConfirmRow("Name", state.customerName)
                ConfirmRow("Mobile", state.mobile.ifEmpty { "—" })
                ConfirmRow("Plate", state.plateNumber.ifEmpty { "—" })
                ConfirmRow("Vehicle", state.vehicleTypeName)
                ConfirmRow("Status", if (state.isRepeat) "Petronas Customer" else "Non-Petronas")
                state.competitorBrandName?.let { ConfirmRow("Previous Brand", it) }
            }

            FormCard {
                SectionHeader(title = "PRODUCTS", icon = "🛢️")
                val items = vm.selectedItems
                if (items.isEmpty()) {
                    Text("No products selected — submit as visit only", fontSize = 12.sp, color = TextSecondary)
                } else {
                    items.forEach { cartItem ->
                        Row(
                            Modifier.fillMaxWidth().padding(vertical = 5.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("${cartItem.sku.name} ×${cartItem.qty}", fontSize = 12.sp,
                                fontWeight = FontWeight.Medium, color = TextPrimary)
                            Text("${cartItem.sku.volumeLitres * cartItem.qty}L",
                                fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PetronasGreenDark)
                        }
                    }
                    HorizontalDivider(Modifier.padding(vertical = 6.dp), color = BorderColor)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total Litres", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("${vm.totalLitres}L", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = PetronasGreenDark)
                    }
                }
            }

            // Commission preview
            Surface(
                color = PetronasGreenLight, shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, PetronasGreen.copy(.2f))
            ) {
                Row(Modifier.fillMaxWidth().padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically) {
                    Text("Commission Earned", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PetronasGreenDark)
                    Text("₨ ${vm.totalCommission.toLong()}", fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold, color = PetronasGreen)
                }
            }

            state.submitError?.let {
                Text(it, color = AccentRed, fontSize = 12.sp)
            }
        }

        Surface(color = Color.White, shadowElevation = 4.dp) {
            Row(
                Modifier.fillMaxWidth().navigationBarsPadding().padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlineButton("← Back", onClick = vm::prevStep, modifier = Modifier.width(90.dp))
                PrimaryButton(
                    if (state.isSubmitting) "Submitting…" else "✅ Submit",
                    onClick = vm::submit,
                    modifier = Modifier.weight(1f),
                    enabled = !state.isSubmitting
                )
            }
        }

        if (state.isSubmitting) LoadingOverlay("Saving entry…")
    }
}

@Composable
private fun SuccessScreen(commission: Double, onNewCustomer: () -> Unit, onDone: () -> Unit) {
    Column(
        Modifier.fillMaxSize().background(BackgroundGray).padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("✅", fontSize = 56.sp)
        Spacer(Modifier.height(16.dp))
        Text("Entry Submitted!", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
        Text("Saved locally and syncing to server", fontSize = 13.sp, color = TextSecondary, modifier = Modifier.padding(top = 6.dp))
        Spacer(Modifier.height(24.dp))
        Surface(color = PetronasGreenLight, shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, PetronasGreen.copy(.2f))) {
            Column(Modifier.padding(24.dp, 18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Commission This Entry", fontSize = 11.sp, color = PetronasGreenDark, fontWeight = FontWeight.Bold)
                Text("₨ ${commission.toLong()}", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = PetronasGreen)
            }
        }
        Spacer(Modifier.height(24.dp))
        PrimaryButton("＋ New Customer", onClick = onNewCustomer, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(10.dp))
        OutlineButton("Back to Home", onClick = onDone, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun FormCard(content: @Composable ColumnScope.() -> Unit) {
    Surface(
        color = Color.White, shape = RoundedCornerShape(15.dp),
        border = BorderStroke(1.dp, BorderColor), shadowElevation = 1.dp
    ) {
        Column(Modifier.fillMaxWidth().padding(12.dp), content = content)
    }
}

@Composable
private fun FormField(label: String, content: @Composable () -> Unit) {
    Column(Modifier.fillMaxWidth().padding(bottom = 10.dp)) {
        Text(label.uppercase(), fontSize = 9.sp, fontWeight = FontWeight.Bold,
            color = TextSecondary, letterSpacing = .8.sp, modifier = Modifier.padding(bottom = 5.dp))
        content()
    }
}

@Composable
private fun ConfirmRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontSize = 12.sp, color = TextSecondary)
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
    }
}

@Composable
private fun outlinedTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor   = PetronasGreen,
    unfocusedBorderColor = BorderColor,
    focusedContainerColor= Color.White,
    unfocusedContainerColor = BackgroundGray,
)
