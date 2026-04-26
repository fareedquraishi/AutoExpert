#!/usr/bin/env python3
"""
AutoExpert — Master Fix Script v2
Run from: /workspaces/AutoExpert
python3 fix_all_v2.py
"""
import os, re

base = "app/src/main/java/com/autoexpert/app"

def fix(path, replacements):
    if not os.path.exists(path):
        print(f"SKIP: {path}")
        return
    t = open(path).read()
    for old, new in replacements:
        if old in t:
            t = t.replace(old, new)
            print(f"  fixed: {old[:50]}")
    open(path, 'w').write(t)
    print(f"OK: {path}")

# ── 1. AttendanceGeofenceService.kt ──────────────────────────────────────
fix(f"{base}/service/AttendanceGeofenceService.kt", [
    ("status         = \"P\"",    "isPresent        = true,\n            attendanceStatus = \"present\""),
    ("status        = \"P\"",     "isPresent        = true,\n            attendanceStatus = \"present\""),
    ("markedByGps    = true",     "method           = \"gps\""),
    ("markedByGps   = true",      "method           = \"gps\""),
    ("latitude       = location?.latitude",   "geoLatitude      = location?.latitude"),
    ("latitude      = location?.latitude",    "geoLatitude      = location?.latitude"),
    ("longitude      = location?.longitude",  "geoLongitude     = location?.longitude"),
    ("longitude     = location?.longitude",   "geoLongitude     = location?.longitude"),
])

# ── 2. SyncWorker.kt — marginPercent fix ─────────────────────────────────
fix(f"{base}/service/SyncWorker.kt", [
    ("marginPercentPercent", "marginPercent"),
    ("marginPercentSnapshot", "marginSnapshot"),
    ("marginPercent      = sku.marginPercentPercent", "marginPercent = sku.marginPercent"),
])

# ── 3. HomeViewModel.kt — notices, payouts old fields ────────────────────
fix(f"{base}/ui/home/HomeViewModel.kt", [
    # notices old fields
    ("id = n.id, message = n.message,",
     "id = n.id, message = n.message,"),
    ("title = n.title", "message = n.message"),
    ("body = n.body",   "// body removed"),
    ("createdAt = ",    "postedAt = System.currentTimeMillis() //"),
    # payouts old fields
    ("earnedDate    = p.",    "payoutDate = p.payoutDate, //"),
    ("earnedAmount  = p.",    "amount = p.amount, //"),
    ("allocatedAmount = p.",  "// allocatedAmount removed p."),
    ("balanceAmount = p.",    "// balanceAmount removed p."),
    ("paymentDate   = p.",    "// paymentDate removed p."),
    ("paidAmount    = p.",    "// paidAmount removed p."),
    ("earnedDate =",          "payoutDate ="),
    ("earnedAmount =",        "amount ="),
    ("allocatedAmount =",     "// allocatedAmount ="),
    ("balanceAmount =",       "// balanceAmount ="),
    ("paymentDate =",         "// paymentDate ="),
    ("paidAmount =",          "// paidAmount ="),
])

# ── 4. WalletScreen.kt — rewrite to use correct PayoutEntity fields ───────
wallet_path = f"{base}/ui/wallet/WalletScreen.kt"
if os.path.exists(wallet_path):
    t = open(wallet_path).read()
    t = t.replace("getTotalUnpaid", "getTotalPaid")
    t = t.replace("p.earnedDate",      "p.payoutDate")
    t = t.replace("p.earnedAmount",    "p.amount")
    t = t.replace("p.allocatedAmount", "p.amount")
    t = t.replace("p.balanceAmount",   "p.amount")
    t = t.replace("p.paymentDate",     "p.payoutDate")
    # Fix the broken WalletCell rows that use old fields
    t = t.replace(
        'WalletCell("Earned",    "₨${\"%,.0f\".format(p.earnedAmount)}",    Modifier.weight(1f))\n                            WalletCell("Allocated", "₨${\"%,.0f\".format(p.allocatedAmount)}", Modifier.weight(1f))\n                            WalletCell("Balance",   "₨${\"%,.0f\".format(p.balanceAmount)}",   Modifier.weight(1f),\n                                valueColor = if (p.balanceAmount > 0) AccentRed else PetronasGreen)',
        'WalletCell("Amount",    "₨${\"%,.0f\".format(p.amount)}",    Modifier.weight(1f))\n                            WalletCell("Date", p.payoutDate, Modifier.weight(1f))\n                            WalletCell("Note",   p.note ?: \"-\",   Modifier.weight(1f))'
    )
    t = t.replace(
        'p.paymentDate?.let {',
        'p.note?.let { _ ->'
    )
    t = t.replace(
        'Text("Paid on $it"',
        'Text("Note: ${p.note ?: \"\"}"'
    )
    open(wallet_path, 'w').write(t)
    print(f"OK: {wallet_path}")

# ── 5. NoticesScreen.kt — title/body → message ───────────────────────────
fix(f"{base}/ui/notices/NoticesScreen.kt", [
    ("notice.title", "notice.message"),
    ("notice.body",  "\"\""),
    # Remove the second Text() call that shows body since we only have message
])
notices_path = f"{base}/ui/notices/NoticesScreen.kt"
if os.path.exists(notices_path):
    t = open(notices_path).read()
    # Remove the body Text line entirely
    t = re.sub(r'Text\(notice\.body.*?\n', '', t)
    t = re.sub(r'Text\(""\s*,.*?modifier = Modifier\.padding\(top = 3\.dp\)\)', '', t, flags=re.DOTALL)
    open(notices_path, 'w').write(t)
    print(f"OK notices body removed: {notices_path}")

# ── 6. NewCustomerViewModel.kt — margin → marginPercent, sumOf fix ────────
fix(f"{base}/ui/customers/NewCustomerViewModel.kt", [
    ("marginPercentPercent", "marginPercent"),
    ("it.margin",            "it.marginPercent"),
    ("sku.margin",           "sku.marginPercent"),
    (".margin *",            ".marginPercent / 100.0 *"),
    ("cartItem.sku.marginPercent * cartItem.qty",
     "cartItem.sku.marginPercent / 100.0 * cartItem.sku.sellingPrice * cartItem.sku.volumeLitres * cartItem.qty"),
])

# ── 7. NewCustomerScreen.kt — icon reference + experimental opt-in ────────
screen_path = f"{base}/ui/customers/NewCustomerScreen.kt"
if os.path.exists(screen_path):
    t = open(screen_path).read()
    if "@file:OptIn" not in t:
        t = "@file:OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)\n" + t
    # fix .icon reference on vehicle type
    t = re.sub(r'Text\([^)]*\.icon[^)]*\)', 'Text(vt.iconKey)', t)
    t = t.replace("vt.icon", "vt.iconKey")
    t = t.replace("v.icon",  "v.iconKey")
    open(screen_path, 'w').write(t)
    print(f"OK: {screen_path}")

# ── 8. LoginScreen.kt — offset import ────────────────────────────────────
fix(f"{base}/ui/login/LoginScreen.kt", [
    ("import androidx.compose.ui.Modifier",
     "import androidx.compose.ui.Modifier\nimport androidx.compose.ui.unit.IntOffset"),
    ("import androidx.compose.ui.*",
     "import androidx.compose.ui.*\nimport androidx.compose.ui.unit.IntOffset"),
])

# ── 9. LoginViewModel.kt — fix EncryptedSharedPreferences ─────────────────
login_vm_path = f"{base}/ui/login/LoginViewModel.kt"
if os.path.exists(login_vm_path):
    t = open(login_vm_path).read()
    # Remove entire biometric save/get methods and replace with simple SharedPrefs
    old_save = '''    fun saveBiometricForCurrentPin(context: Context) {
        val prefs = androidx.security.crypto.EncryptedSharedPreferences.create(
            "bio_prefs_secure",
            androidx.security.crypto.MasterKey.Builder(context)
                .setKeyScheme(androidx.security.crypto.MasterKey.KeyScheme.AES256_GCM)
                .build(),
            context,
            androidx.security.crypto.EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            androidx.security.crypto.EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        prefs.edit().putString("enrolled_pin", pin.value).apply()
    }

    fun getBiometricPin(context: Context): String? {
        return try {
            val prefs = androidx.security.crypto.EncryptedSharedPreferences.create(
                "bio_prefs_secure",
                androidx.security.crypto.MasterKey.Builder(context)
                    .setKeyScheme(androidx.security.crypto.MasterKey.KeyScheme.AES256_GCM)
                    .build(),
                context,
                androidx.security.crypto.EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                androidx.security.crypto.EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
            prefs.getString("enrolled_pin", null)
        } catch (e: Exception) { null }
    }'''
    new_save = '''    fun saveBiometricForCurrentPin(context: Context) {
        context.getSharedPreferences("bio_prefs", android.content.Context.MODE_PRIVATE)
            .edit().putString("enrolled_pin", pin.value).apply()
    }

    fun getBiometricPin(context: Context): String? {
        return context.getSharedPreferences("bio_prefs", android.content.Context.MODE_PRIVATE)
            .getString("enrolled_pin", null)
    }'''
    if old_save in t:
        t = t.replace(old_save, new_save)
    else:
        # Try partial replacement
        t = re.sub(
            r'fun saveBiometricForCurrentPin.*?fun getBiometricPin.*?\}',
            new_save,
            t, flags=re.DOTALL
        )
    open(login_vm_path, 'w').write(t)
    print(f"OK: {login_vm_path}")

# ── 10. Fix http:// corrupted references in all kt files ──────────────────
kt_dirs = [
    f"{base}/service",
    f"{base}/ui/customers",
    f"{base}/ui/home",
    f"{base}/ui/login",
    f"{base}/ui/wallet",
    f"{base}/ui/notices",
    f"{base}/ui/messaging",
    f"{base}/ui/splash",
    f"{base}/ui/profile",
]
for d in kt_dirs:
    if not os.path.exists(d): continue
    for fn in os.listdir(d):
        if not fn.endswith('.kt'): continue
        fp = os.path.join(d, fn)
        t = open(fp).read()
        fixed = re.sub(r'http://(\w[\w.]*)', lambda m: m.group(1), t)
        if fixed != t:
            open(fp, 'w').write(fixed)
            print(f"  Cleaned hyperlinks: {fp}")

print("\n✅ All fixes applied!")
print("Now run: ./gradlew assembleDebug --no-daemon --no-watch-fs --max-workers=1 2>&1 | tee build.log && tail -5 build.log")
