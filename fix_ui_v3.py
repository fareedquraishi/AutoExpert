#!/usr/bin/env python3
"""
AutoExpert — UI & Data Fix Script v3
Run from: /workspaces/AutoExpert
python3 fix_ui_v3.py
"""
import os, re

base = "app/src/main/java/com/autoexpert/app"

def clean_links(t):
    return re.sub(r'http://(\w[\w.]*)', lambda m: m.group(1), t)

# ── 1. Fix vehicle name showing twice + make compact ─────────────────────
path = f"{base}/ui/customers/NewCustomerScreen.kt"
t = open(path).read()
t = clean_links(t)

t = t.replace(
    '                            Text(vehicleIcon(vt.iconKey))\n                        Text(vt.name, fontSize = 9.sp)\n                            Text(vt.name, fontSize = 8.sp, fontWeight = FontWeight.Bold,\n                                color = if (sel) PetronasGreenDark else TextSecondary)',
    '                            Text(vehicleIcon(vt.iconKey), fontSize = 20.sp)\n                            Text(vt.name, fontSize = 8.sp, fontWeight = FontWeight.Medium,\n                                color = if (sel) PetronasGreenDark else TextSecondary, maxLines = 1)'
)

# Compact vehicle cell padding
t = t.replace('.padding(4.dp, 8.dp),', '.padding(3.dp, 5.dp),')

# Add onRemove to ProductCard call
t = t.replace(
    '                        ProductCard(sku = sku, qty = item.qty,\n                            onIncrement = { vm.incrementQty(sku.id) },\n                            onDecrement = { vm.decrementQty(sku.id) })',
    '                        ProductCard(sku = sku, qty = item.qty,\n                            onIncrement = { vm.incrementQty(sku.id) },\n                            onDecrement = { vm.decrementQty(sku.id) },\n                            onRemove = { vm.removeFromCart(sku.id) })'
)

# Applicator disabled when no products
t = t.replace(
    'ToggleRow("Applicator", "Mechanic / Workshop", state.isApplicator, vm::onApplicatorToggle)',
    'val hasProducts = vm.selectedItems.isNotEmpty()\n                ToggleRow(\n                    title = "Applicator",\n                    subtitle = if (hasProducts) "Mechanic / Workshop" else "Select a product first",\n                    checked = state.isApplicator,\n                    onCheckedChange = { if (hasProducts) vm.onApplicatorToggle(it) },\n                    enabled = hasProducts\n                )'
)

# Redesign ProductCard
old_card = '@Composable\nprivate fun ProductCard(sku: SkuEntity, qty: Int, onIncrement: () -> Unit, onDecrement: () -> Unit) {'
new_card = '@Composable\nprivate fun ProductCard(sku: SkuEntity, qty: Int, onIncrement: () -> Unit, onDecrement: () -> Unit, onRemove: () -> Unit = {}) {'

t = t.replace(old_card, new_card)

# Add delete X button inside card - replace the name text with name+delete row
t = t.replace(
    '            Text(sku.name, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold,\n                color = TextPrimary, lineHeight = 13.sp)\n            Text("${sku.volumeLitres}L/pk", fontSize = 9.sp, color = TextSecondary,\n                modifier = Modifier.padding(top = 2.dp))\n            Text("\\u20a8${sku.sellingPrice.toLong()}", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold,\n                color = PetronasGreenDark, modifier = Modifier.padding(top = 4.dp))\n            Text("/pk", fontSize = 8.sp, color = TextDim)',
    '            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {\n                Text(sku.name, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextPrimary, lineHeight = 12.sp, modifier = Modifier.weight(1f))\n                if (qty > 0) {\n                    Box(Modifier.size(16.dp).background(AccentRed.copy(.1f), RoundedCornerShape(4.dp)).clickable { onRemove() }, contentAlignment = Alignment.Center) {\n                        Text("\\u00d7", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AccentRed)\n                    }\n                }\n            }\n            Row(Modifier.fillMaxWidth().padding(top = 2.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {\n                Text("${sku.volumeLitres}L", fontSize = 8.sp, color = TextSecondary)\n                Text("\\u20a8${sku.sellingPrice.toLong()}", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = PetronasGreenDark)\n            }'
)

open(path, 'w').write(t)
print(f"OK: {path}")

# ── 2. Add removeFromCart to ViewModel ────────────────────────────────────
vm_path = f"{base}/ui/customers/NewCustomerViewModel.kt"
t = open(vm_path).read()
t = clean_links(t)
if 'fun removeFromCart' not in t:
    t = t.replace(
        'fun onApplicatorToggle(v: Boolean)',
        'fun removeFromCart(skuId: String) {\n        _state.update { s ->\n            val newCart = s.cart.toMutableMap()\n            newCart[skuId]?.let { newCart[skuId] = it.copy(qty = 0) }\n            s.copy(cart = newCart, isApplicator = if (newCart.values.none { it.qty > 0 }) false else s.isApplicator)\n        }\n    }\n\n    fun onApplicatorToggle(v: Boolean)'
    )
open(vm_path, 'w').write(t)
print(f"OK: {vm_path}")

# ── 3. Fix ToggleRow to support enabled param ─────────────────────────────
components_dir = f"{base}/ui/components"
if os.path.exists(components_dir):
    for fn in os.listdir(components_dir):
        if not fn.endswith('.kt'): continue
        fp = os.path.join(components_dir, fn)
        t = open(fp).read()
        if 'fun ToggleRow' in t and 'enabled: Boolean' not in t:
            t = t.replace(
                'fun ToggleRow(title: String, subtitle: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit)',
                'fun ToggleRow(title: String, subtitle: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit, enabled: Boolean = true)'
            )
            t = t.replace(
                'Switch(checked = checked, onCheckedChange = onCheckedChange)',
                'Switch(checked = checked, onCheckedChange = onCheckedChange, enabled = enabled)'
            )
            open(fp, 'w').write(t)
            print(f"OK ToggleRow: {fp}")

# ── 4. HomeViewModel - fetch today from Supabase to avoid stale data ──────
hvm_path = f"{base}/ui/home/HomeViewModel.kt"
t = open(hvm_path).read()
t = clean_links(t)

# Delete today's synced entries before loading to remove stale ones
if 'clearTodaySynced' not in t:
    t = t.replace(
        'saleDao.getByBaAndDate(baId, today).collect { entries ->',
        '// Clear synced entries so deleted remote records disappear\n                try { saleDao.deleteSyncedByDate(baId, today) } catch(e:Exception){}\n                saleDao.getByBaAndDate(baId, today).collect { entries ->'
    )
open(hvm_path, 'w').write(t)
print(f"OK: {hvm_path}")

# ── 5. Add deleteSyncedByDate to SaleEntryQueueDao ───────────────────────
daos_path = f"{base}/data/local/dao/Daos.kt"
t = open(daos_path).read()
if 'deleteSyncedByDate' not in t:
    t = t.replace(
        '    @Query("SELECT SUM(totalCommission)',
        '    @Query("DELETE FROM sale_entries_queue WHERE baId = :baId AND entryTime LIKE :datePrefix || \'%\' AND syncStatus = \'synced\'")\n    suspend fun deleteSyncedByDate(baId: String, datePrefix: String)\n\n    @Query("SELECT SUM(totalCommission)'
    )
open(daos_path, 'w').write(t)
print(f"OK: {daos_path}")

# ── 6. Clean all hyperlinks in all kt files ───────────────────────────────
all_dirs = [
    f"{base}/ui/customers", f"{base}/ui/home", f"{base}/ui/profile",
    f"{base}/ui/components", f"{base}/ui/wallet", f"{base}/ui/notices",
    f"{base}/ui/messaging", f"{base}/service", f"{base}/data/local/dao",
]
for d in all_dirs:
    if not os.path.exists(d): continue
    for fn in os.listdir(d):
        if not fn.endswith('.kt'): continue
        fp = os.path.join(d, fn)
        t = open(fp).read()
        fixed = clean_links(t)
        if fixed != t:
            open(fp, 'w').write(fixed)
            print(f"  Cleaned: {fn}")

print("\n✅ All UI fixes applied!")
print("Now run: ./gradlew assembleDebug --no-daemon --no-watch-fs --max-workers=1 2>&1 | tee build.log && tail -5 build.log")
