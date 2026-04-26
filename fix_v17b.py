"""
Fix v17 errors — surgical patches
  1. SyncWorker: remove retryCount/incrementRetry (just use "failed" directly)
  2. NewCustomerScreen line 359: fix ToggleRow onCheckedChange → onChecked
  3. NewCustomerViewModel: remove duplicate removeFromCart
  4. HomeViewModel: remove deleteSyncedByBaAndDate (DAO fix was skipped)
"""
import re, os

def fix(path, fn, label):
    if not os.path.exists(path):
        print(f"  SKIP: {path}")
        return
    t = open(path, encoding='utf-8').read()
    result = fn(t)
    open(path, 'w', encoding='utf-8').write(result)
    changed = result != t
    print(f"  {'✅' if changed else '⚠️ no change'} {label}")

# ── 1. SyncWorker: remove retryCount/incrementRetry references ──
def fix_sync(t):
    # Remove the retry logic we added — just mark as failed directly
    t = t.replace(
        'if (entry.retryCount >= 3) saleDao.updateSyncStatus(entry.localId, "failed")\n                else saleDao.incrementRetry(entry.localId)',
        'saleDao.updateSyncStatus(entry.localId, "failed")'
    )
    t = t.replace(
        'if (att.retryCount >= 3) attendanceDao.updateSyncStatus(att.localId, "failed")\n                else attendanceDao.incrementRetry(att.localId)',
        'attendanceDao.updateSyncStatus(att.localId, "failed")'
    )
    return t

# ── 2. NewCustomerScreen: fix ToggleRow call + remove duplicate removeFromCart in screen ──
def fix_screen(t):
    # Fix the bad ToggleRow call added in applicator section (onCheckedChange → lambda with onChecked)
    # The ToggleRow signature is: fun ToggleRow(title, subtitle, checked, onChecked: (Boolean)->Unit)
    # Our new applicator section added: ToggleRow("", "", state.isApplicator, vm::onApplicatorToggle)
    # But line 359 has onCheckedChange = {...} — find and fix it
    t = re.sub(
        r'ToggleRow\("",\s*"",\s*state\.isApplicator,\s*onCheckedChange\s*=\s*\{[^}]*\}\)',
        'ToggleRow("", "", state.isApplicator, vm::onApplicatorToggle)',
        t
    )
    # Also fix any remaining onCheckedChange inside ToggleRow calls
    t = re.sub(
        r'(ToggleRow\([^)]+),\s*onCheckedChange\s*=\s*\{\s*vm\.onApplicatorToggle\(it\)\s*\}\)',
        r'\1, vm::onApplicatorToggle)',
        t
    )
    return t

# ── 3. NewCustomerViewModel: remove duplicate removeFromCart ──
def fix_vm(t):
    # Find all occurrences of removeFromCart function definitions
    # Keep only the LAST one (the correct one from our fix)
    pattern = r'(    fun removeFromCart\(skuId: String\) \{[^}]+(?:\{[^}]*\}[^}]*)*\})'
    matches = list(re.finditer(pattern, t, re.DOTALL))
    if len(matches) >= 2:
        # Remove the first (older) one
        first = matches[0]
        t = t[:first.start()] + t[first.end():]
        print("    removed duplicate removeFromCart")
    return t

# ── 4. HomeViewModel: remove deleteSyncedByBaAndDate (DAO doesn't have it) ──
def fix_home(t):
    t = t.replace(
        '// Remove entries that have been synced to server (delete+re-fetch pattern)\n                saleDao.deleteSyncedByBaAndDate(baId, today)\n                ',
        ''
    )
    # Also fix session.stationName.first() — SessionManager may not expose stationName as Flow
    t = t.replace(
        'stationName = ui.stationName.ifBlank { session.stationName.first() ?: "—" }',
        'stationName = ui.stationName.ifBlank { "—" }'
    )
    return t

print("\n🔧 Fixing v17 build errors\n" + "─" * 40)
fix("app/src/main/java/com/autoexpert/app/service/SyncWorker.kt",              fix_sync,   "SyncWorker — removed retryCount/incrementRetry")
fix("app/src/main/java/com/autoexpert/app/ui/customers/NewCustomerScreen.kt",  fix_screen, "NewCustomerScreen — fixed ToggleRow onCheckedChange")
fix("app/src/main/java/com/autoexpert/app/ui/customers/NewCustomerViewModel.kt",fix_vm,    "NewCustomerViewModel — removed duplicate removeFromCart")
fix("app/src/main/java/com/autoexpert/app/ui/home/HomeViewModel.kt",           fix_home,   "HomeViewModel — removed deleteSyncedByBaAndDate")

print("\n✅ Done! Now build:")
print("  ./gradlew assembleDebug --no-daemon --no-watch-fs --max-workers=1 2>&1 | tee build.log && tail -5 build.log")
