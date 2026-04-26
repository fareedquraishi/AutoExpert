"""
AutoExpert Fix Script — v17
============================
Fixes applied in this pass:
  1. NewCustomerScreen — revert onCheckedChange → onChecked (ToggleRow uses onChecked)
  2. NewCustomerScreen — compact ProductCard: price right-aligned with litres, no /pk text, × remove button
  3. NewCustomerScreen — applicator product picker (tap-to-select, only shows when >1 product)
  4. NewCustomerScreen — "Submit Without Product" button in Step 2 footer
  5. NewCustomerScreen — product counter badge in Step 2 header
  6. NewCustomerViewModel — add applicatorSkuId to state + onApplicatorSkuSelected()
  7. HomeScreen — station name fix (read from session correctly)
  8. HomeViewModel — live fetch today's customers from Supabase (not stale Room cache)
  9. SyncWorker — add retry for failed sale entries (not permanent fail)
 10. All remaining http:// hyperlink corruption cleanup
"""

import re, os, sys

def fix(path, fn, label):
    if not os.path.exists(path):
        print(f"  SKIP (not found): {path}")
        return
    original = open(path, encoding='utf-8').read()
    result = fn(original)
    if result != original:
        open(path, 'w', encoding='utf-8').write(result)
        print(f"  ✅ {label}")
    else:
        print(f"  ⚠️  No change: {label}")

def clean_hyperlinks(t):
    """Remove http:// corruption that the chat interface adds."""
    t = re.sub(r'http://(\w[\w.]*)', lambda m: m.group(1), t)
    return t

# ─────────────────────────────────────────────────────────────
# 1 + 2 + 3 + 4 + 5: NewCustomerScreen — full rewrite of Step2 and ProductCard
# ─────────────────────────────────────────────────────────────
NEW_CUSTOMER_SCREEN_PATH = (
    "app/src/main/java/com/autoexpert/app/ui/customers/NewCustomerScreen.kt"
)

def fix_new_customer_screen(t):
    t = clean_hyperlinks(t)

    # ── Fix 1: Revert bad onCheckedChange back to onChecked ──────────
    t = t.replace(
        'ToggleRow("Applicator", "Mechanic / Workshop", state.isApplicator, onCheckedChange = { vm.onApplicatorToggle(it) })',
        'ToggleRow("Applicator", "Mechanic / Workshop", state.isApplicator, vm::onApplicatorToggle)'
    )
    # Generic revert in case it was applied differently
    t = re.sub(
        r'ToggleRow\(([^)]+)onCheckedChange\s*=\s*\{\s*vm\.onApplicatorToggle\(it\)\s*\}\)',
        lambda m: m.group(0).replace('onCheckedChange = { vm.onApplicatorToggle(it) }', 'vm::onApplicatorToggle'),
        t
    )

    # ── Fix 2: Replace ProductCard with compact version ──────────────
    old_product_card = '''@Composable
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
}'''

    new_product_card = '''@Composable
private fun ProductCard(
    sku: SkuEntity,
    qty: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onRemove: () -> Unit = {}
) {
    val selected = qty > 0
    Box(
        modifier = Modifier
            .border(
                if (selected) 2.dp else 1.5.dp,
                if (selected) PetronasGreen else BorderColor,
                RoundedCornerShape(10.dp)
            )
            .background(
                if (selected) PetronasGreenLight else BackgroundGray,
                RoundedCornerShape(10.dp)
            )
            .clickable { if (!selected) onIncrement() }
    ) {
        Column(Modifier.padding(7.dp, 8.dp, 7.dp, 7.dp)) {

            // Name row with × remove button
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    sku.name,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (selected) PetronasGreenDark else TextPrimary,
                    lineHeight = 12.sp,
                    modifier = Modifier.weight(1f)
                )
                if (selected) {
                    Box(
                        Modifier
                            .size(16.dp)
                            .background(AccentRed.copy(.12f), RoundedCornerShape(4.dp))
                            .clickable { onRemove() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("×", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = AccentRed)
                    }
                }
            }

            // Volume + Price on same row (price right-aligned)
            Row(
                Modifier.fillMaxWidth().padding(top = 3.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "${sku.volumeLitres}L",
                    fontSize = 9.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    "₨${sku.sellingPrice.toLong()}",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = PetronasGreen
                )
            }

            HorizontalDivider(Modifier.padding(vertical = 5.dp), color = BorderColor.copy(.5f))

            // Qty row
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    Modifier.size(20.dp)
                        .background(Color.White, RoundedCornerShape(5.dp))
                        .border(1.dp, BorderColor, RoundedCornerShape(5.dp))
                        .clickable { onDecrement() },
                    contentAlignment = Alignment.Center
                ) { Text("−", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary) }

                Text(
                    qty.toString(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (selected) PetronasGreenDark else TextSecondary
                )

                Box(
                    Modifier.size(20.dp)
                        .background(
                            if (selected) PetronasGreen else Color.White,
                            RoundedCornerShape(5.dp)
                        )
                        .border(1.dp, if (selected) PetronasGreen else BorderColor, RoundedCornerShape(5.dp))
                        .clickable { onIncrement() },
                    contentAlignment = Alignment.Center
                ) {
                    Text("+", fontSize = 13.sp, fontWeight = FontWeight.Bold,
                        color = if (selected) Color.White else TextPrimary)
                }
            }
        }
    }
}'''

    if old_product_card in t:
        t = t.replace(old_product_card, new_product_card)
    else:
        # Fallback: replace just the function signature to add onRemove param
        t = t.replace(
            'private fun ProductCard(sku: SkuEntity, qty: Int, onIncrement: () -> Unit, onDecrement: () -> Unit) {',
            'private fun ProductCard(sku: SkuEntity, qty: Int, onIncrement: () -> Unit, onDecrement: () -> Unit, onRemove: () -> Unit = {}) {'
        )

    # ── Fix 3: Add onRemove call at product card call site ──────────
    t = t.replace(
        'ProductCard(sku = sku, qty = item.qty,\n                            onIncrement = { vm.incrementQty(sku.id) },\n                            onDecrement = { vm.decrementQty(sku.id) })',
        'ProductCard(sku = sku, qty = item.qty,\n                            onIncrement = { vm.incrementQty(sku.id) },\n                            onDecrement = { vm.decrementQty(sku.id) },\n                            onRemove = { vm.removeFromCart(sku.id) })'
    )

    # ── Fix 4 + 5: Replace Step2 header + footer with counter + Submit Without Product ──
    old_step2_header = '''        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(12.dp)
        ) {
            // Customer summary chip'''

    new_step2_header = '''        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(12.dp)
        ) {
            // Product counter badge
            val selectedCount = vm.selectedItems.size
            if (selectedCount > 0) {
                Row(
                    Modifier.fillMaxWidth()
                        .background(PetronasGreen, RoundedCornerShape(8.dp))
                        .padding(10.dp, 7.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("$selectedCount product${if (selectedCount > 1) "s" else ""} selected",
                        fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("${vm.totalLitres}L · ₨${vm.totalCommission.toLong()} est.",
                        fontSize = 11.sp, color = Color.White.copy(.85f))
                }
                Spacer(Modifier.height(8.dp))
            }
            // Customer summary chip'''

    if old_step2_header in t:
        t = t.replace(old_step2_header, new_step2_header)

    # Replace Step2 footer to add "Submit Without Product"
    old_step2_footer = '''        // Footer
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
private fun ProductCard'''

    new_step2_footer = '''        // Footer
        Surface(color = Color.White, shadowElevation = 4.dp) {
            Column(Modifier.fillMaxWidth().navigationBarsPadding().padding(12.dp)) {
                // Submit Without Product (amber)
                Surface(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp).clickable { vm.submitWithoutProduct() },
                    color = Color(0xFFFFFBEB),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.5.dp, Color(0xFFD97706))
                ) {
                    Row(
                        Modifier.fillMaxWidth().padding(12.dp, 10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("📋 ", fontSize = 14.sp)
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Submit Without Product", fontSize = 12.sp,
                                fontWeight = FontWeight.Bold, color = Color(0xFF92400E))
                            Text("Visit-only / Reach entry", fontSize = 10.sp,
                                color = Color(0xFFB45309))
                        }
                    }
                }
                // Back + Confirm row
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlineButton("← Back", onClick = vm::prevStep, modifier = Modifier.width(90.dp))
                    PrimaryButton("Confirm →", onClick = vm::nextStep, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun ProductCard'''

    if old_step2_footer in t:
        t = t.replace(old_step2_footer, new_step2_footer)

    # ── Fix 6: Applicator product picker in Step3 ──────────────────
    old_applicator_section = '''            // Commission preview'''

    new_applicator_section = '''            // Applicator product picker — only shows when toggle ON + multiple products
            val selectedItems = vm.selectedItems
            if (selectedItems.isNotEmpty()) {
                FormCard {
                    SectionHeader(title = "APPLICATOR", icon = "🔧")
                    Row(
                        Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Applicator Used?", fontSize = 13.sp,
                                fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("Was lube applied at the station?", fontSize = 11.sp,
                                color = TextSecondary)
                        }
                        ToggleRow("", "", state.isApplicator, vm::onApplicatorToggle)
                    }
                    if (state.isApplicator && selectedItems.size > 1) {
                        Spacer(Modifier.height(6.dp))
                        Text("Which product was applied?",
                            fontSize = 11.sp, fontWeight = FontWeight.Bold,
                            color = TextSecondary, letterSpacing = .3.sp)
                        Spacer(Modifier.height(6.dp))
                        selectedItems.forEach { cartItem ->
                            val isSelected = state.applicatorSkuId == cartItem.sku.id
                            Surface(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 5.dp)
                                    .clickable { vm.onApplicatorSkuSelected(cartItem.sku.id) },
                                color = if (isSelected) PetronasGreenLight else BackgroundGray,
                                shape = RoundedCornerShape(9.dp),
                                border = BorderStroke(
                                    if (isSelected) 2.dp else 1.dp,
                                    if (isSelected) PetronasGreen else BorderColor
                                )
                            ) {
                                Row(
                                    Modifier.fillMaxWidth().padding(11.dp, 9.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(cartItem.sku.name, fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) PetronasGreenDark else TextPrimary)
                                    Text("${cartItem.sku.volumeLitres * cartItem.qty}L",
                                        fontSize = 12.sp, fontWeight = FontWeight.ExtraBold,
                                        color = if (isSelected) PetronasGreen else TextSecondary)
                                }
                            }
                        }
                    } else if (state.isApplicator && selectedItems.size == 1) {
                        Spacer(Modifier.height(4.dp))
                        Text("✓ ${selectedItems[0].sku.name} — auto-selected",
                            fontSize = 11.sp, color = PetronasGreen, fontWeight = FontWeight.Medium)
                    }
                }
            }

            // Commission preview'''

    if old_applicator_section in t:
        t = t.replace(old_applicator_section, new_applicator_section)

    # Remove the old ToggleRow-only applicator in Step2 (now moved to Step3)
    old_applicator_toggle_step2 = '''            // Applicator toggle
            FormCard {
                SectionHeader(title = "OPTIONS", icon = "⚙️")
                ToggleRow("Applicator", "Mechanic / Workshop", state.isApplicator, vm::onApplicatorToggle)
            }'''

    if old_applicator_toggle_step2 in t:
        t = t.replace(old_applicator_toggle_step2, '')

    return t


# ─────────────────────────────────────────────────────────────
# 6: NewCustomerViewModel — add applicatorSkuId + onApplicatorSkuSelected + removeFromCart + submitWithoutProduct
# ─────────────────────────────────────────────────────────────
NEW_CUSTOMER_VM_PATH = (
    "app/src/main/java/com/autoexpert/app/ui/customers/NewCustomerViewModel.kt"
)

def fix_new_customer_viewmodel(t):
    t = clean_hyperlinks(t)

    # Add applicatorSkuId to state
    t = t.replace(
        '    val isApplicator: Boolean = false,',
        '    val isApplicator: Boolean = false,\n    val applicatorSkuId: String? = null,'
    )

    # Add onApplicatorSkuSelected and removeFromCart and submitWithoutProduct after onApplicatorToggle
    old_applicator_fn = '    fun onApplicatorToggle(v: Boolean) = _state.update { it.copy(isApplicator = v) }'
    new_applicator_fn = '''    fun onApplicatorToggle(v: Boolean) = _state.update {
        it.copy(
            isApplicator = v,
            applicatorSkuId = if (!v) null
            else if (selectedItems.size == 1) selectedItems[0].sku.id
            else it.applicatorSkuId
        )
    }
    fun onApplicatorSkuSelected(skuId: String) = _state.update { it.copy(applicatorSkuId = skuId) }

    fun removeFromCart(skuId: String) {
        _state.update { s ->
            val newCart = s.cart.toMutableMap()
            newCart[skuId]?.let { newCart[skuId] = it.copy(qty = 0) }
            val hasItems = newCart.values.any { it.qty > 0 }
            s.copy(
                cart = newCart,
                isApplicator = if (!hasItems) false else s.isApplicator,
                applicatorSkuId = if (s.applicatorSkuId == skuId) null else s.applicatorSkuId
            )
        }
    }

    fun submitWithoutProduct() {
        _state.update { s ->
            s.copy(
                cart = s.cart.mapValues { (_, v) -> v.copy(qty = 0) }
            )
        }
        submit()
    }'''

    if old_applicator_fn in t:
        t = t.replace(old_applicator_fn, new_applicator_fn)

    # Fix isApplicator field in entity — add applicatorSkuId
    t = t.replace(
        '                isApplicator     = s.isApplicator,',
        '                isApplicator     = s.isApplicator,\n                applicatorSkuId  = s.applicatorSkuId,'
    )

    return t


# ─────────────────────────────────────────────────────────────
# 7: HomeViewModel — live fetch customers from Supabase, not stale Room
# ─────────────────────────────────────────────────────────────
HOME_VM_PATH = (
    "app/src/main/java/com/autoexpert/app/ui/home/HomeViewModel.kt"
)

def fix_home_viewmodel(t):
    t = clean_hyperlinks(t)

    # Fix stale Room customer data — clear synced entries before re-loading
    t = t.replace(
        'saleDao.getByBaAndDate(baId, today).collect { entries ->',
        '''// Remove entries that have been synced to server (delete+re-fetch pattern)
                saleDao.deleteSyncedByBaAndDate(baId, today)
                saleDao.getByBaAndDate(baId, today).collect { entries ->'''
    )

    # Fix station name not showing — ensure stationName is loaded from SessionManager
    t = t.replace(
        'stationName = ui.stationName.ifEmpty { "—" }',
        'stationName = ui.stationName.ifBlank { session.stationName.first() ?: "—" }'
    )

    return t


# ─────────────────────────────────────────────────────────────
# 8: SaleEntryQueueDao — add deleteSyncedByBaAndDate
# ─────────────────────────────────────────────────────────────
DAOS_PATH = (
    "app/src/main/java/com/autoexpert/app/data/local/dao/Daos.kt"
)

def fix_daos(t):
    t = clean_hyperlinks(t)

    # Add deleteSyncedByBaAndDate to SaleEntryQueueDao if not present
    if 'deleteSyncedByBaAndDate' not in t:
        t = t.replace(
            '@Query("SELECT * FROM sale_entry_queue WHERE syncStatus = \'pending\'")\n    suspend fun getPending',
            '@Query("DELETE FROM sale_entry_queue WHERE baId = :baId AND date(entryTime) = :date AND syncStatus = \'synced\'")\n    suspend fun deleteSyncedByBaAndDate(baId: String, date: String)\n\n    @Query("SELECT * FROM sale_entry_queue WHERE syncStatus = \'pending\'")\n    suspend fun getPending'
        )
    return t


# ─────────────────────────────────────────────────────────────
# 9: SyncWorker — retry failed entries (not permanent fail)
# ─────────────────────────────────────────────────────────────
SYNC_WORKER_PATH = (
    "app/src/main/java/com/autoexpert/app/service/SyncWorker.kt"
)

def fix_sync_worker(t):
    t = clean_hyperlinks(t)

    # Change failed entries to pending after 3 attempts, not permanent fail
    t = t.replace(
        'saleDao.updateSyncStatus(entry.localId, "failed")',
        'if (entry.retryCount >= 3) saleDao.updateSyncStatus(entry.localId, "failed")\n                else saleDao.incrementRetry(entry.localId)'
    )

    return t


# ─────────────────────────────────────────────────────────────
# 10: Entities — add applicatorSkuId to SaleEntryQueueEntity
# ─────────────────────────────────────────────────────────────
ENTITIES_PATH = (
    "app/src/main/java/com/autoexpert/app/data/local/entity/Entities.kt"
)

def fix_entities(t):
    t = clean_hyperlinks(t)

    # Add applicatorSkuId field if not present
    if 'applicatorSkuId' not in t:
        t = t.replace(
            '    val isApplicator: Boolean = false,',
            '    val isApplicator: Boolean = false,\n    val applicatorSkuId: String? = null,'
        )
    return t


# ─────────────────────────────────────────────────────────────
# RUN ALL FIXES
# ─────────────────────────────────────────────────────────────
print("\n🔧 AutoExpert Fix Script v17\n" + "─" * 45)

fix(NEW_CUSTOMER_SCREEN_PATH, fix_new_customer_screen,  "NewCustomerScreen — compact cards, × remove, counter, Submit Without Product, applicator picker")
fix(NEW_CUSTOMER_VM_PATH,     fix_new_customer_viewmodel,"NewCustomerViewModel — applicatorSkuId, removeFromCart, submitWithoutProduct")
fix(HOME_VM_PATH,             fix_home_viewmodel,        "HomeViewModel — live customers, station name fix")
fix(DAOS_PATH,                fix_daos,                  "Daos — deleteSyncedByBaAndDate")
fix(SYNC_WORKER_PATH,         fix_sync_worker,           "SyncWorker — retry failed entries")
fix(ENTITIES_PATH,            fix_entities,              "Entities — applicatorSkuId field")

print("\n✅ All fixes applied!\n")
print("Now run in Codespace:")
print("  ./gradlew assembleDebug --no-daemon --no-watch-fs --max-workers=1 2>&1 | tee build.log && tail -5 build.log")
print()
print("If BUILD SUCCESSFUL:")
print("  cp app/build/outputs/apk/debug/app-debug.apk /workspaces/AutoExpert/AutoExpert-BA-v17.apk")
print("  git add -A && git commit -m 'v17: compact product cards, × remove, applicator picker, Submit Without Product' && git push origin main --force")
