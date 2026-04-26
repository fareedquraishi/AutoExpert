import os

content = """\
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

fun vehicleIcon(iconKey: String): String = when (iconKey.lowercase()) {
    "car"        -> "\uD83D\uDE97"
    "motorcycle" -> "\uD83C\uDFCD\uFE0F"
    "van"        -> "\uD83D\uDE90"
    "truck"      -> "\uD83D\uDE9B"
    "suv"        -> "\uD83D\uDE99"
    "rickshaw"   -> "\uD83D\uDEFA"
    "heavy"      -> "\uD83D\uDE8C"
    "tractor"    -> "\uD83D\uDE9C"
    "pickup"     -> "\uD83D\uDEFB"
    else         -> "\uD83D\uDE99"
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
            commission  = vm.totalCommission,
            onNewCustomer = { vm.reset() },
            onDone      = onSuccess
        )
        return
    }
    Column(Modifier.fillMaxSize().background(BackgroundGray)) {
        TopBar(
            title  = when (state.step) { 1 -> "New Customer"; 2 -> "Products"; else -> "Confirm" },
            onBack = if (state.step == 1) onBack else vm::prevStep
        )
        StepIndicator(state.step)
        when (state.step) {
            1    -> Step1Info(state, vm)
            2    -> Step2Products(state, vm)
            else -> Step3Confirm(state, vm)
        }
    }
}

@Composable
private fun TopBar(title: String, onBack: () -> Unit) {
    Surface(color = Color.White, shadowElevation = 2.dp) {
        Row(
            Modifier.fillMaxWidth().statusBarsPadding().padding(4.dp, 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
            }
            Text(title, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
        }
    }
}

@Composable
private fun StepIndicator(step: Int) {
    Row(
        Modifier.fillMaxWidth().background(Color.White).padding(12.dp, 8.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment     = Alignment.CenterVertically
    ) {
        repeat(3) { i ->
            Box(
                Modifier.weight(1f).height(3.dp).background(
                    if (i < step) PetronasGreen else BorderColor,
                    RoundedCornerShape(2.dp)
                )
            )
        }
        Text("Step $step of 3", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
    }
}

@Composable
private fun Step1Info(state: CustomerEntryState, vm: NewCustomerViewModel) {
    Column(Modifier.fillMaxSize()) {
        Column(
            Modifier.weight(1f)
                .verticalScroll(androidx.compose.foundation.rememberScrollState())
                .padding(12.dp)
        ) {
            FormCard {
                SectionHeader(title = "CUSTOMER INFO", icon = "\uD83D\uDC64")
                FormField("Full Name") {
                    OutlinedTextField(
                        value         = state.customerName,
                        onValueChange = vm::onNameChanged,
                        modifier      = Modifier.fillMaxWidth(),
                        placeholder   = { Text("Enter name") },
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                        colors        = outlinedTextFieldColors(),
                        singleLine    = true
                    )
                }
                FormField("Mobile Number") {
                    OutlinedTextField(
                        value         = state.mobile,
                        onValueChange = vm::onMobileChanged,
                        modifier      = Modifier.fillMaxWidth(),
                        placeholder   = { Text("03XX-XXXXXXX") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        colors        = outlinedTextFieldColors(),
                        singleLine    = true
                    )
                }
                FormField("Plate Number (optional)") {
                    OutlinedTextField(
                        value         = state.plateNumber,
                        onValueChange = vm::onPlateChanged,
                        modifier      = Modifier.fillMaxWidth(),
                        placeholder   = { Text("ABC-123") },
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
                        colors        = outlinedTextFieldColors(),
                        singleLine    = true
                    )
                }
            }
            Spacer(Modifier.height(10.dp))
            FormCard {
                SectionHeader(title = "VEHICLE TYPE", icon = "\uD83D\uDE97")
                LazyVerticalGrid(
                    columns                  = GridCells.Fixed(3),
                    horizontalArrangement    = Arrangement.spacedBy(6.dp),
                    verticalArrangement      = Arrangement.spacedBy(6.dp),
                    modifier                 = Modifier.heightIn(max = 400.dp)
                ) {
                    items(state.vehicleTypes.size) { i ->
                        val vt  = state.vehicleTypes[i]
                        val sel = state.vehicleTypeId == vt.id
                        Surface(
                            shape    = RoundedCornerShape(8.dp),
                            color    = if (sel) PetronasGreenLight else BackgroundGray,
                            border   = BorderStroke(if (sel) 2.dp else 1.5.dp, if (sel) PetronasGreen else BorderColor),
                            modifier = Modifier.clickable { vm.onVehicleSelected(vt.id, vt.name) }
                        ) {
                            Column(
                                Modifier.fillMaxWidth().padding(6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(vehicleIcon(vt.iconKey), fontSize = 22.sp)
                                Text(
                                    vt.name, fontSize = 10.sp, fontWeight = FontWeight.Bold,
                                    color    = if (sel) PetronasGreenDark else TextSecondary,
                                    modifier = Modifier.padding(top = 3.dp)
                                )
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
            FormCard {
                SectionHeader(title = "CUSTOMER STATUS", icon = "\u2705")
                Surface(
                    shape    = RoundedCornerShape(10.dp),
                    color    = if (state.isRepeat) PetronasGreenLight else BackgroundGray,
                    border   = BorderStroke(1.5.dp, if (state.isRepeat) PetronasGreen else BorderColor),
                    modifier = Modifier.fillMaxWidth().clickable { vm.onRepeatToggle(!state.isRepeat) }
                ) {
                    Row(
                        Modifier.fillMaxWidth().padding(13.dp, 11.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Already a Petronas Customer?", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("Turn on if they already use Petronas products", fontSize = 11.sp, color = TextSecondary)
                        }
                        Switch(
                            checked         = state.isRepeat,
                            onCheckedChange = vm::onRepeatToggle,
                            colors          = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = PetronasGreen)
                        )
                    }
                }
                if (!state.isRepeat && state.competitorBrands.isNotEmpty()) {
                    Spacer(Modifier.height(10.dp))
                    Text("PREVIOUS BRAND", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextSecondary, letterSpacing = 0.8.sp)
                    Spacer(Modifier.height(7.dp))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        state.competitorBrands.forEach { brand ->
                            val sel = state.competitorBrandId == brand.id
                            Surface(
                                shape    = RoundedCornerShape(20.dp),
                                color    = if (sel) AccentRed.copy(.07f) else BackgroundGray,
                                border   = BorderStroke(1.5.dp, if (sel) AccentRed else BorderColor),
                                modifier = Modifier.clickable { vm.onBrandSelected(brand.id, brand.name) }
                            ) {
                                Text(
                                    brand.name, fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
                                    color    = if (sel) AccentRed else TextPrimary,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
        Surface(color = Color.White, shadowElevation = 4.dp) {
            Row(Modifier.fillMaxWidth().navigationBarsPadding().padding(12.dp)) {
                PrimaryButton(
                    "Next: Products \u2192", onClick = vm::nextStep,
                    modifier = Modifier.weight(1f),
                    enabled  = state.customerName.isNotBlank() && state.vehicleTypeId.isNotEmpty()
                )
            }
        }
    }
}

@Composable
private fun Step2Products(state: CustomerEntryState, vm: NewCustomerViewModel) {
    val selCount  = vm.selectedItems.size
    val totLitres = vm.totalLitres
    val totComm   = vm.totalCommission
    Column(Modifier.fillMaxSize()) {
        Column(
            Modifier.weight(1f)
                .verticalScroll(androidx.compose.foundation.rememberScrollState())
                .padding(12.dp)
        ) {
            if (selCount > 0) {
                Row(
                    Modifier.fillMaxWidth()
                        .background(PetronasGreen, RoundedCornerShape(8.dp))
                        .padding(10.dp, 7.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text(
                        "$selCount product${if (selCount > 1) "s" else ""} selected",
                        fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White
                    )
                    Text(
                        "${totLitres}L \u00B7 \u20A8${totComm.toLong()} est.",
                        fontSize = 11.sp, color = Color.White.copy(.85f)
                    )
                }
                Spacer(Modifier.height(8.dp))
            }
            Row(
                Modifier.fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
                    .padding(9.dp, 8.dp),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(9.dp)
            ) {
                Box(
                    Modifier.size(32.dp).background(GreenGradient, RoundedCornerShape(9.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        state.customerName.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                        fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = Color.White
                    )
                }
                Column {
                    Text(state.customerName, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(
                        "${state.vehicleTypeName} \u00B7 ${state.plateNumber.ifEmpty { "No plate" }}",
                        fontSize = 10.sp, color = TextSecondary
                    )
                }
            }
            Spacer(Modifier.height(10.dp))
            FormCard {
                SectionHeader(title = "PRODUCTS", icon = "\uD83D\uDEE2\uFE0F")
                LazyVerticalGrid(
                    columns               = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(7.dp),
                    verticalArrangement   = Arrangement.spacedBy(7.dp),
                    modifier              = Modifier.heightIn(max = 1000.dp)
                ) {
                    items(state.skus.size) { i ->
                        val sku      = state.skus[i]
                        val cartItem = state.cart[sku.id]!!
                        ProductCard(
                            sku         = sku,
                            qty         = cartItem.qty,
                            onIncrement = { vm.incrementQty(sku.id) },
                            onDecrement = { vm.decrementQty(sku.id) },
                            onRemove    = { vm.removeFromCart(sku.id) }
                        )
                    }
                }
            }
        }
        Surface(color = Color.White, shadowElevation = 4.dp) {
            Column(Modifier.fillMaxWidth().navigationBarsPadding().padding(12.dp)) {
                Surface(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp).clickable { vm.submitWithoutProduct() },
                    color    = Color(0xFFFFFBEB),
                    shape    = RoundedCornerShape(10.dp),
                    border   = BorderStroke(1.5.dp, Color(0xFFD97706))
                ) {
                    Row(Modifier.fillMaxWidth().padding(12.dp, 10.dp), horizontalArrangement = Arrangement.Center) {
                        Text(
                            "\uD83D\uDCCB  Submit Without Product  \u2022  Visit-only entry",
                            fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF92400E)
                        )
                    }
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlineButton("\u2190 Back", onClick = vm::prevStep, modifier = Modifier.width(90.dp))
                    PrimaryButton("Confirm \u2192", onClick = vm::nextStep, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun Step3Confirm(state: CustomerEntryState, vm: NewCustomerViewModel) {
    val selItems  = vm.selectedItems
    val totLitres = vm.totalLitres
    val totComm   = vm.totalCommission
    Column(Modifier.fillMaxSize()) {
        Column(
            Modifier.weight(1f)
                .verticalScroll(androidx.compose.foundation.rememberScrollState())
                .padding(13.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            FormCard {
                SectionHeader(title = "CUSTOMER", icon = "\uD83D\uDC64")
                ConfirmRow("Name",   state.customerName)
                ConfirmRow("Mobile", state.mobile.ifEmpty { "\u2014" })
                ConfirmRow("Plate",  state.plateNumber.ifEmpty { "\u2014" })
                ConfirmRow("Vehicle", state.vehicleTypeName)
                ConfirmRow("Status", if (state.isRepeat) "Petronas Customer" else "Non-Petronas")
                state.competitorBrandName?.let { ConfirmRow("Previous Brand", it) }
            }
            FormCard {
                SectionHeader(title = "PRODUCTS", icon = "\uD83D\uDEE2\uFE0F")
                if (selItems.isEmpty()) {
                    Text("No products \u2014 submitting as visit only", fontSize = 12.sp, color = TextSecondary)
                } else {
                    selItems.forEach { cartItem ->
                        Row(
                            Modifier.fillMaxWidth().padding(vertical = 5.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("${cartItem.sku.name} \u00D7${cartItem.qty}", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                            Text("${cartItem.sku.volumeLitres * cartItem.qty}L", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PetronasGreenDark)
                        }
                    }
                    HorizontalDivider(Modifier.padding(vertical = 6.dp), color = BorderColor)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total Litres", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("${totLitres}L", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = PetronasGreenDark)
                    }
                }
            }
            if (selItems.isNotEmpty()) {
                FormCard {
                    SectionHeader(title = "APPLICATOR", icon = "\uD83D\uDD27")
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Applicator Used?", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("Was lube applied at the station?", fontSize = 11.sp, color = TextSecondary)
                        }
                        Switch(
                            checked         = state.isApplicator,
                            onCheckedChange = vm::onApplicatorToggle,
                            colors          = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = PetronasGreen)
                        )
                    }
                    if (state.isApplicator && selItems.size > 1) {
                        Spacer(Modifier.height(8.dp))
                        Text("Which product was applied?", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                        Spacer(Modifier.height(6.dp))
                        selItems.forEach { cartItem ->
                            val isSel = state.applicatorSkuId == cartItem.sku.id
                            Surface(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 5.dp)
                                    .clickable { vm.onApplicatorSkuSelected(cartItem.sku.id) },
                                color    = if (isSel) PetronasGreenLight else BackgroundGray,
                                shape    = RoundedCornerShape(9.dp),
                                border   = BorderStroke(if (isSel) 2.dp else 1.dp, if (isSel) PetronasGreen else BorderColor)
                            ) {
                                Row(
                                    Modifier.fillMaxWidth().padding(11.dp, 9.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(cartItem.sku.name, fontSize = 13.sp, fontWeight = FontWeight.Bold,
                                        color = if (isSel) PetronasGreenDark else TextPrimary)
                                    Text("${cartItem.sku.volumeLitres * cartItem.qty}L", fontSize = 12.sp,
                                        fontWeight = FontWeight.ExtraBold, color = if (isSel) PetronasGreen else TextSecondary)
                                }
                            }
                        }
                    } else if (state.isApplicator && selItems.size == 1) {
                        Spacer(Modifier.height(4.dp))
                        Text("\u2713 ${selItems[0].sku.name} \u2014 auto-selected", fontSize = 11.sp, color = PetronasGreen)
                    }
                }
            }
            Surface(
                color  = PetronasGreenLight,
                shape  = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, PetronasGreen.copy(.2f))
            ) {
                Row(
                    Modifier.fillMaxWidth().padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text("Commission Earned", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PetronasGreenDark)
                    Text("\u20A8 ${totComm.toLong()}", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = PetronasGreen)
                }
            }
            state.submitError?.let { Text(it, color = AccentRed, fontSize = 12.sp) }
        }
        Surface(color = Color.White, shadowElevation = 4.dp) {
            Row(
                Modifier.fillMaxWidth().navigationBarsPadding().padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlineButton("\u2190 Back", onClick = vm::prevStep, modifier = Modifier.width(90.dp))
                PrimaryButton(
                    if (state.isSubmitting) "Submitting\u2026" else "\u2705 Submit",
                    onClick  = vm::submit,
                    modifier = Modifier.weight(1f),
                    enabled  = !state.isSubmitting
                )
            }
        }
        if (state.isSubmitting) LoadingOverlay("Saving entry\u2026")
    }
}

@Composable
private fun ProductCard(
    sku: SkuEntity, qty: Int,
    onIncrement: () -> Unit, onDecrement: () -> Unit, onRemove: () -> Unit = {}
) {
    val selected = qty > 0
    Box(
        modifier = Modifier
            .border(if (selected) 2.dp else 1.5.dp, if (selected) PetronasGreen else BorderColor, RoundedCornerShape(10.dp))
            .background(if (selected) PetronasGreenLight else BackgroundGray, RoundedCornerShape(10.dp))
            .clickable { if (!selected) onIncrement() }
    ) {
        Column(Modifier.padding(7.dp, 8.dp, 7.dp, 7.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Text(sku.name, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold,
                    color = if (selected) PetronasGreenDark else TextPrimary, lineHeight = 12.sp, modifier = Modifier.weight(1f))
                if (selected) {
                    Box(
                        Modifier.size(16.dp).background(AccentRed.copy(.12f), RoundedCornerShape(4.dp)).clickable { onRemove() },
                        contentAlignment = Alignment.Center
                    ) { Text("\u00D7", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = AccentRed) }
                }
            }
            Row(Modifier.fillMaxWidth().padding(top = 3.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("${sku.volumeLitres}L", fontSize = 9.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                Text("\u20A8${sku.sellingPrice.toLong()}", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = PetronasGreen)
            }
            HorizontalDivider(Modifier.padding(vertical = 5.dp), color = BorderColor.copy(.5f))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(20.dp).background(Color.White, RoundedCornerShape(5.dp)).border(1.dp, BorderColor, RoundedCornerShape(5.dp)).clickable { onDecrement() }, contentAlignment = Alignment.Center) {
                    Text("\u2212", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
                Text(qty.toString(), fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = if (selected) PetronasGreenDark else TextSecondary)
                Box(Modifier.size(20.dp).background(if (selected) PetronasGreen else Color.White, RoundedCornerShape(5.dp)).border(1.dp, if (selected) PetronasGreen else BorderColor, RoundedCornerShape(5.dp)).clickable { onIncrement() }, contentAlignment = Alignment.Center) {
                    Text("+", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = if (selected) Color.White else TextPrimary)
                }
            }
        }
    }
}

@Composable
private fun SuccessScreen(commission: Double, onNewCustomer: () -> Unit, onDone: () -> Unit) {
    Column(Modifier.fillMaxSize().background(BackgroundGray).padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("\u2705", fontSize = 56.sp)
        Spacer(Modifier.height(16.dp))
        Text("Entry Submitted!", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
        Text("Saved locally and syncing to server", fontSize = 13.sp, color = TextSecondary, modifier = Modifier.padding(top = 6.dp))
        Spacer(Modifier.height(24.dp))
        Surface(color = PetronasGreenLight, shape = RoundedCornerShape(16.dp), border = BorderStroke(1.dp, PetronasGreen.copy(.2f))) {
            Column(Modifier.padding(24.dp, 18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Commission This Entry", fontSize = 11.sp, color = PetronasGreenDark, fontWeight = FontWeight.Bold)
                Text("\u20A8 ${commission.toLong()}", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = PetronasGreen)
            }
        }
        Spacer(Modifier.height(24.dp))
        PrimaryButton("\uFF0B New Customer", onClick = onNewCustomer, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(10.dp))
        OutlineButton("Back to Home", onClick = onDone, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun FormCard(content: @Composable ColumnScope.() -> Unit) {
    Surface(color = Color.White, shape = RoundedCornerShape(15.dp), border = BorderStroke(1.dp, BorderColor), shadowElevation = 1.dp) {
        Column(Modifier.fillMaxWidth().padding(12.dp), content = content)
    }
}

@Composable
private fun FormField(label: String, content: @Composable () -> Unit) {
    Column(Modifier.fillMaxWidth().padding(bottom = 10.dp)) {
        Text(label.uppercase(), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextSecondary, letterSpacing = .8.sp, modifier = Modifier.padding(bottom = 5.dp))
        content()
    }
}

@Composable
private fun ConfirmRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 5.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontSize = 12.sp, color = TextSecondary)
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
    }
}

@Composable
private fun outlinedTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor      = PetronasGreen,
    unfocusedBorderColor    = BorderColor,
    focusedContainerColor   = Color.White,
    unfocusedContainerColor = BackgroundGray,
)
"""

dst = "app/src/main/java/com/autoexpert/app/ui/customers/NewCustomerScreen.kt"
with open(dst, "w", encoding="utf-8") as f:
    f.write(content)
lines = content.count("\n")
print(f"Written {lines} lines to {dst}")
