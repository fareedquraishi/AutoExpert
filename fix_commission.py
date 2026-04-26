#!/usr/bin/env python3
"""
AutoExpert — Commission Fix + Vehicle Icons + UI fixes
Run from: /workspaces/AutoExpert
python3 fix_commission.py
"""
import os, re

base = "app/src/main/java/com/autoexpert/app"

# ── NewCustomerViewModel.kt — full rewrite of key sections ───────────────
path = f"{base}/ui/customers/NewCustomerViewModel.kt"
t = open(path).read()

# 1. Clean all hyperlinks
t = re.sub(r'http://(\w[\w.]*)', lambda m: m.group(1), t)

# 2. Add commissionPackageDao to constructor injection
t = t.replace(
    '@HiltViewModel',
    '@HiltViewModel'
)

# 3. Fix totalCommission calculation — use flat Rs50/litre from package
t = t.replace(
    'val totalCommission get() = selectedItems.sumOf { it.sku.marginPercent * it.qty }',
    'val totalCommission get() = selectedItems.sumOf { it.sku.volumeLitres * it.qty } * _commissionRate.value'
)

# 4. Add _commissionRate state after class declaration
old_hilt = '''@HiltViewModel
class NewCustomerViewModel @Inject constructor('''
new_hilt = '''@HiltViewModel
class NewCustomerViewModel @Inject constructor('''
# Find the class body start and inject commissionRate
t = t.replace(
    'private val _state = MutableStateFlow(CustomerEntryState())',
    'private val _state = MutableStateFlow(CustomerEntryState())\n    private val _commissionRate = MutableStateFlow(50.0) // Rs per litre, loaded from DB'
)

# 5. Fix duplicate qtyLitres
t = t.replace(
    '"qtyLitres"  to (cartItem.sku.volumeLitres * cartItem.qty),\n                    "qtyLitres"  to (cartItem.sku.volumeLitres * cartItem.qty),',
    '"qtyLitres"  to (cartItem.sku.volumeLitres * cartItem.qty),'
)

# 6. Fix commission earned per item — flat rate per litre
t = t.replace(
    '"commissionEarned" to (cartItem.sku.marginPercent / 100.0 * cartItem.sku.sellingPrice * cartItem.sku.volumeLitres * cartItem.qty)',
    '"commissionEarned" to (cartItem.sku.volumeLitres * cartItem.qty * _commissionRate.value)'
)

# 7. Add load commission rate in init block
t = t.replace(
    'fun loadReferenceData() {',
    '''fun loadCommissionRate() {
        viewModelScope.launch {
            try {
                val baId = session.baId.first() ?: return@launch
                val today = java.time.LocalDate.now().toString()
                // Check BA override first, then global package
                val override = baCommissionOverrideDao.getActiveOverride(baId, today)
                val packageId = override?.packageId
                if (packageId != null) {
                    val pkg = commissionPackageDao.getAllActive().find { it.id == packageId }
                    if (pkg != null) _commissionRate.value = pkg.flatRate
                } else {
                    val globalPkg = commissionPackageDao.getAllActive().find { it.isGlobal }
                    if (globalPkg != null) _commissionRate.value = globalPkg.flatRate
                }
            } catch (e: Exception) { /* use default 50 */ }
        }
    }

    fun loadReferenceData() {'''
)

# 8. Call loadCommissionRate in init
t = t.replace(
    'loadReferenceData()',
    'loadReferenceData()\n        loadCommissionRate()'
)

open(path, 'w').write(t)
print(f"OK: {path}")

# ── Check if commissionPackageDao and baCommissionOverrideDao are injected ─
t = open(path).read()
if 'commissionPackageDao' not in t:
    t = t.replace(
        'private val gson: Gson,',
        'private val gson: Gson,\n    private val commissionPackageDao: CommissionPackageDao,\n    private val baCommissionOverrideDao: BaCommissionOverrideDao,'
    )
    open(path, 'w').write(t)
    print("  Added commission DAOs to constructor")

# ── NewCustomerScreen.kt — vehicle icons using emoji map ─────────────────
path = f"{base}/ui/customers/NewCustomerScreen.kt"
t = open(path).read()

# Clean hyperlinks
t = re.sub(r'http://(\w[\w.]*)', lambda m: m.group(1), t)

# Add vehicle icon mapping function after imports
icon_map = '''
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

'''

if 'fun vehicleIcon' not in t:
    # Insert after last import line
    last_import = t.rfind('\nimport ')
    end_of_imports = t.find('\n', last_import + 1)
    t = t[:end_of_imports+1] + icon_map + t[end_of_imports+1:]
    print("  Added vehicleIcon function")

# Replace vt.name with vehicleIcon(vt.iconKey) in vehicle grid
t = t.replace('Text(vt.name)', 'Text(vehicleIcon(vt.iconKey))\n                        Text(vt.name, fontSize = 9.sp)')
t = t.replace('Text(v.name)', 'Text(vehicleIcon(v.iconKey))\n                        Text(v.name, fontSize = 9.sp)')

open(path, 'w').write(t)
print(f"OK: {path}")

# ── Fix targets in HomeViewModel ──────────────────────────────────────────
path = f"{base}/ui/home/HomeViewModel.kt"
t = open(path).read()
t = re.sub(r'http://(\w[\w.]*)', lambda m: m.group(1), t)
# Fix target basis field name
t = t.replace("target?.basis", "target?.targetBasis")
t = t.replace('target?.targetBasis == \"reach\"',  'target?.targetBasis == "reach"')
t = t.replace('target?.targetBasis == \"litres\"', 'target?.targetBasis == "litres"')
open(path, 'w').write(t)
print(f"OK: {path}")

# ── Fix notices sync in HomeViewModel — correct filter ────────────────────
path = f"{base}/ui/home/HomeViewModel.kt"
t = open(path).read()
# Fix messages filter — remove parentheses
t = t.replace(
    'filter = "(sender_id.eq.$baId,receiver_id.eq.$baId)"',
    'filter = "sender_id.eq.$baId,receiver_id.eq.$baId"'
)
open(path, 'w').write(t)
print(f"OK messages filter: {path}")

# ── Clean all remaining hyperlinks in all kt files ────────────────────────
dirs = [
    f"{base}/service",
    f"{base}/ui/customers",
    f"{base}/ui/home",
    f"{base}/ui/login",
    f"{base}/ui/wallet",
    f"{base}/ui/notices",
    f"{base}/ui/messaging",
    f"{base}/ui/splash",
    f"{base}/ui/profile",
    f"{base}/ui/navigation",
    f"{base}/ui/components",
    f"{base}/util",
    f"{base}/di",
]
for d in dirs:
    if not os.path.exists(d): continue
    for fn in os.listdir(d):
        if not fn.endswith('.kt'): continue
        fp = os.path.join(d, fn)
        t = open(fp).read()
        fixed = re.sub(r'http://(\w[\w.]*)', lambda m: m.group(1), t)
        if fixed != t:
            open(fp, 'w').write(fixed)
            print(f"  Cleaned hyperlinks: {fp}")

print("\n✅ Commission and icon fixes applied!")
print("Now run: ./gradlew assembleDebug --no-daemon --no-watch-fs --max-workers=1 2>&1 | tee build.log && tail -5 build.log")
