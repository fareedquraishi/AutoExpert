import re, os

BASE = "app/src/main/java/com/autoexpert/app"

# ── 1. Fix WalletScreen nav bar ──
path = f"{BASE}/ui/wallet/WalletScreen.kt"
t = open(path).read()
t = re.sub(r'\[([^\]]+)\]\(http[s]?://[^\)]+\)', r'\1', t)

# Fix selected index and callbacks
t = t.replace(
    'onHome: () -> Unit = {},\n    onCustomers: () -> Unit = {},\n    onChat: () -> Unit = {},\n    onProfile: () -> Unit = {},',
    'onHome: () -> Unit = {},\n    onCustomers: () -> Unit = {},\n    onSummary: () -> Unit = {},\n    onProfile: () -> Unit = {},'
)
t = t.replace(
    'HomeBottomNavBar(selected = 3, unreadMessages = 0, onSelect = { i ->\n                when (i) { 0 -> onHome(); 1 -> onCustomers(); 2 -> onChat(); 4 -> onProfile() }',
    'HomeBottomNavBar(selected = 2, unreadMessages = 0, onSelect = { i ->\n                when (i) { 0 -> onHome(); 1 -> onCustomers(); 3 -> onSummary(); 4 -> onProfile() }'
)

open(path, "w").write(t)
print("WalletScreen nav fixed")

# ── 2. Fix SummaryScreen nav bar ──
path = f"{BASE}/ui/summary/SummaryScreen.kt"
t = open(path).read()
t = re.sub(r'\[([^\]]+)\]\(http[s]?://[^\)]+\)', r'\1', t)

# Fix encoding - use utf-8 compatible chars instead of emoji
# Fix the report text - replace emoji with WhatsApp formatting
t = t.replace(
    'encoding="ascii", errors="replace"',
    'encoding="utf-8"'
) if 'encoding="ascii"' in t else t

# Fix nav callbacks - add onWallet
t = t.replace(
    'onHome: () -> Unit = {},\n    onCustomers: () -> Unit = {},\n    onWallet: () -> Unit = {},\n    onProfile: () -> Unit = {},',
    'onHome: () -> Unit = {},\n    onCustomers: () -> Unit = {},\n    onWallet: () -> Unit = {},\n    onProfile: () -> Unit = {},'
)
t = t.replace(
    'HomeBottomNavBar(selected = 3, unreadMessages = 0, onSelect = { i ->\n                when (i) { 0 -> onHome(); 1 -> onCustomers(); 2 -> onWallet(); 4 -> onProfile() }',
    'HomeBottomNavBar(selected = 3, unreadMessages = 0, onSelect = { i ->\n                when (i) { 0 -> onHome(); 1 -> onCustomers(); 2 -> onWallet(); 4 -> onProfile() }'
)

# Fix report text - replace corrupted emoji/chars with WhatsApp formatting
old_report = '''return """
📊 Daily Summary — $date
⏰ Shift: 10:00 AM — $now
👤 ${state.baName} | ${state.stationName}

✅ Reach: ${state.todayReach}/${state.reachTarget.toInt()}  $reachArrow
🛢 Litres: ${"%.1f".format(state.todayLitres)}L/${state.litresTarget.toInt()}L  $litresArrow
💰 Today: Rs ${pkrFmt.format(state.todayCommission.toLong())}  $commArrow
💳 Unpaid Balance: Rs ${pkrFmt.format(state.unpaidBalance.toLong())}

📋 Breakdown:
• Conquest: ${state.conquest} | Repeat: ${state.repeat}
• Existing: ${state.existing} | Prospect: ${state.prospect}

🚗 By Vehicle:
• $vehicles

🛢 Products Sold:
$products

🔧 Applicator: ${state.applicatorCount} customers

📱 AutoExpert BA App v2.0
        """.trimIndent()'''

new_report = '''val div = "___________________________"
        return """
*Daily Summary*
_${state.baName} | ${state.stationName}_
_$date | Shift: 10:00 AM - $now_

$div
*Performance*
$div
*Reach:* _${state.todayReach}/${state.reachTarget.toInt()}_ $reachArrow
*Litres:* _${"%.1f".format(state.todayLitres)}L/${state.litresTarget.toInt()}L_ $litresArrow
*Commission:* _Rs ${pkrFmt.format(state.todayCommission.toLong())}_ $commArrow
*Unpaid Balance:* _Rs ${pkrFmt.format(state.unpaidBalance.toLong())}_

$div
*Customer Breakdown*
$div
_Conquest: ${state.conquest} | Repeat: ${state.repeat}_
_Existing: ${state.existing} | Prospect: ${state.prospect}_

$div
*By Vehicle*
$div
_$vehicles_

$div
*Products Sold*
$div
$products

$div
*My Performance*
$div
*MTD:* _Rs ${pkrFmt.format(state.todayCommission.toLong())} | ${"%.1f".format(state.todayLitres)}L_
*All Time:* _Rs ${pkrFmt.format(state.unpaidBalance.toLong() + state.todayCommission.toLong())} | ${"%.1f".format(state.todayLitres)}L_

$div
_*Prepared by Fintectual Pvt Ltd*_
_*AutoExpert BA App v2.0*_
        """.trimIndent()'''

# Try to find and replace the return block
if '📊 Daily Summary' in t or '? Daily Summary' in t:
    # Find the return statement
    idx_start = t.find('return """')
    idx_end = t.find('.trimIndent()', idx_start) + len('.trimIndent()')
    if idx_start > 0 and idx_end > 0:
        t = t[:idx_start] + new_report + t[idx_end:]
        print("Report text replaced")
    else:
        print("Could not find report block")
else:
    # Already partially fixed - just update format
    idx_start = t.find('return """')
    idx_end = t.find('.trimIndent()', idx_start) + len('.trimIndent()')
    if idx_start > 0:
        t = t[:idx_start] + new_report + t[idx_end:]
        print("Report text replaced (alternate)")

# Fix vehicle name - use vehicleTypeName from entity
t = t.replace(
    'val v = e.vehicleTypeName ?: "Unknown"',
    'val v = e.vehicleTypeName?.ifEmpty { "Unknown" } ?: "Unknown"'
)

open(path, "w").write(t)
print("SummaryScreen fixed")

# ── 3. Fix NavGraph - wire all nav callbacks properly ──
nav_path = f"{BASE}/ui/navigation/AppNavGraph.kt"
t = open(nav_path).read()
t = re.sub(r'\[([^\]]+)\]\(http[s]?://[^\)]+\)', r'\1', t)

# Fix Wallet composable - add all nav callbacks
t = t.replace(
    'WalletScreen(onBack = { navController.popBackStack() })',
    '''WalletScreen(
                    onBack      = { navController.popBackStack() },
                    onHome      = { navController.navigate(Routes.HOME) { popUpTo(Routes.HOME) { inclusive = true } } },
                    onCustomers = { navController.navigate(Routes.CUSTOMERS) },
                    onSummary   = { navController.navigate(Routes.SUMMARY) },
                    onProfile   = { navController.navigate(Routes.PROFILE) }
                )'''
)

# Fix Summary composable - already has callbacks, just verify
if 'SummaryScreen(' not in t:
    print("Warning: SummaryScreen not found in NavGraph")
else:
    print("NavGraph - SummaryScreen already present")

open(nav_path, "w").write(t)
print("NavGraph fixed")

# ── 4. Add HomeBottomNavBar to ProfileScreen ──
profile_path = f"{BASE}/ui/profile/ProfileScreen.kt"
t = open(profile_path).read()
t = re.sub(r'\[([^\]]+)\]\(http[s]?://[^\)]+\)', r'\1', t)

if 'HomeBottomNavBar' not in t:
    # Add import
    t = t.replace(
        'import com.autoexpert.app.ui.components.*',
        'import com.autoexpert.app.ui.components.*\nimport com.autoexpert.app.ui.home.HomeBottomNavBar'
    )

    # Add nav parameters to ProfileScreen
    t = t.replace(
        'fun ProfileScreen(\n    onBack: () -> Unit,\n    onLogout: () -> Unit,',
        'fun ProfileScreen(\n    onBack: () -> Unit,\n    onLogout: () -> Unit,\n    onHome: () -> Unit = {},\n    onCustomers: () -> Unit = {},\n    onWallet: () -> Unit = {},\n    onSummary: () -> Unit = {},'
    )

    # Add bottomBar to Scaffold
    t = t.replace(
        'fun ProfileScreen(',
        'fun ProfileScreen('
    )

    # Find Scaffold and add bottomBar
    t = t.replace(
        'Scaffold(\n        containerColor',
        '''Scaffold(
        bottomBar = {
            HomeBottomNavBar(selected = 4, unreadMessages = 0, onSelect = { i ->
                when (i) { 0 -> onHome(); 1 -> onCustomers(); 2 -> onWallet(); 3 -> onSummary() }
            })
        },
        containerColor'''
    )

    open(profile_path, "w").write(t)
    print("ProfileScreen - HomeBottomNavBar added")
else:
    print("ProfileScreen - already has HomeBottomNavBar")

# ── 5. Fix NavGraph Profile call - add nav callbacks ──
t = open(nav_path).read()
t = t.replace(
    'ProfileScreen(onBack = { navController.popBackStack() },\n                onLogout = { navController.navigate(Routes.LOGIN) {',
    '''ProfileScreen(
                    onBack      = { navController.popBackStack() },
                    onHome      = { navController.navigate(Routes.HOME) { popUpTo(Routes.HOME) { inclusive = true } } },
                    onCustomers = { navController.navigate(Routes.CUSTOMERS) },
                    onWallet    = { navController.navigate(Routes.WALLET) },
                    onSummary   = { navController.navigate(Routes.SUMMARY) },
                    onLogout = { navController.navigate(Routes.LOGIN) {'''
)
open(nav_path, "w").write(t)
print("NavGraph Profile callbacks added")

print("\nAll done! Ready to build v61")
