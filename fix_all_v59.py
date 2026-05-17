import re, os

BASE = "app/src/main/java/com/autoexpert/app"

# ── 1. Fix time storage - store as local PKT, display as-is ──
path = f"{BASE}/ui/customers/NewCustomerViewModel.kt"
t = open(path).read()
t = re.sub(r'\[([^\]]+)\]\(http[s]?://[^\)]+\)', r'\1', t)

# Store as local device time (PKT) - no UTC conversion needed
t = t.replace(
    '''entryTime        = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).apply { timeZone = TimeZone.getTimeZone("UTC") }.format(Date()) + "Z",''',
    '''entryTime        = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(Date()),'''
)
t = t.replace(
    '''syncedAt         = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).apply { timeZone = TimeZone.getTimeZone("UTC") }.format(Date()) + "Z",''',
    '''syncedAt         = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(Date()),'''
)

# Add duplicate check before submit
old_submit = '        if (_state.value.isSubmitting) return\n        viewModelScope.launch {'
new_submit = '''        if (_state.value.isSubmitting) return
        viewModelScope.launch {
            // Duplicate check: same name + mobile + plate today
            val s = _state.value
            val baId = session.baId.first() ?: return@launch
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val todayEntries = saleQueueDao.getByBaAndDate(baId, today).first()
            val isDup = todayEntries.any { e ->
                e.customerName.trim().equals(s.customerName.trim(), ignoreCase = true) &&
                e.customerMobile == s.mobile.trim() &&
                !s.plateNumber.isNullOrEmpty() &&
                e.plateNumber?.equals(s.plateNumber.trim().uppercase()) == true
            }
            if (isDup) {
                _state.update { it.copy(isSubmitting = false, submitError = "Duplicate entry: same customer already logged today") }
                return@launch
            }'''

t = t.replace(old_submit, new_submit)

# Add missing imports
if 'import kotlinx.coroutines.flow.first' not in t:
    t = t.replace(
        'import kotlinx.coroutines.flow.*',
        'import kotlinx.coroutines.flow.*\nimport kotlinx.coroutines.flow.first'
    )

open(path, "w").write(t)
print("NewCustomerViewModel fixed - time + duplicate check")

# ── 2. Fix HomeScreen - time display + icon mapping ──
path = f"{BASE}/ui/home/HomeScreen.kt"
t = open(path).read()
t = re.sub(r'\[([^\]]+)\]\(http[s]?://[^\)]+\)', r'\1', t)

# Fix time display - no UTC conversion, just display as stored
t = t.replace(
    '''val timeStr = try {
        val raw = entry.entryTime.take(19).replace("T", "T")
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
        sdf.timeZone = java.util.TimeZone.getTimeZone("UTC")
        val d = sdf.parse(raw)
        val outFmt = SimpleDateFormat("hh:mm a", Locale.US)
        outFmt.timeZone = java.util.TimeZone.getTimeZone("Asia/Karachi")
        outFmt.format(d ?: Date())''',
    '''val timeStr = try {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val d = sdf.parse(entry.entryTime.take(19))
        SimpleDateFormat("hh:mm a", Locale.getDefault()).format(d ?: Date())'''
)

# Also fix the original time parsing if it exists
t = t.replace(
    '''val timeStr = try {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        sdf.timeZone = java.util.TimeZone.getTimeZone("UTC")
        val d = sdf.parse(entry.entryTime.take(19))
        val outFmt = SimpleDateFormat("hh:mm a", Locale.getDefault())
        outFmt.timeZone = java.util.TimeZone.getTimeZone("Asia/Karachi")
        outFmt.format(d ?: Date())''',
    '''val timeStr = try {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val d = sdf.parse(entry.entryTime.take(19))
        SimpleDateFormat("hh:mm a", Locale.getDefault()).format(d ?: Date())'''
)

# Fix vehicle icon mapping to match exact icon_key values from Supabase
t = t.replace(
    '''val vehicleIconRes = when (entry.vehicleTypeName?.lowercase()?.trim()) {
        "motorcycle", "bike"                -> R.drawable.ic_vehicle_motorcycle
        "pickup", "van", "pickup / van"     -> R.drawable.ic_vehicle_van
        "truck"                             -> R.drawable.ic_vehicle_truck
        "heavy vehicle", "heavy"            -> R.drawable.ic_vehicle_heavy
        "rickshaw", "auto", "tuk tuk"       -> R.drawable.ic_vehicle_rickshaw
        else                                -> R.drawable.ic_vehicle_car
    }''',
    '''val vehicleIconRes = when (entry.vehicleTypeName?.lowercase()?.trim()) {
        "motorcycle"                        -> R.drawable.ic_vehicle_motorcycle
        "pickup / van", "pickup", "van"     -> R.drawable.ic_vehicle_van
        "truck"                             -> R.drawable.ic_vehicle_truck
        "heavy vehicle"                     -> R.drawable.ic_vehicle_heavy
        "rickshaw"                          -> R.drawable.ic_vehicle_rickshaw
        else                                -> R.drawable.ic_vehicle_car
    }'''
)

# Fix nav - remove Notices, add Summary
t = t.replace(
    '''        val items = listOf(
            NavItem("Home",      Icons.Filled.Home,                 0),
            NavItem("Customers", Icons.Filled.People,               1),
            NavItem("Notices",   Icons.Filled.Notifications,        2),
            NavItem("Wallet",    Icons.Filled.AccountBalanceWallet,  3),
            NavItem("Profile",   Icons.Filled.Person,               4),
        )''',
    '''        val items = listOf(
            NavItem("Home",      Icons.Filled.Home,                 0),
            NavItem("Customers", Icons.Filled.People,               1),
            NavItem("Wallet",    Icons.Filled.AccountBalanceWallet,  2),
            NavItem("Summary",   Icons.Filled.Summarize,            3),
            NavItem("Profile",   Icons.Filled.Person,               4),
        )'''
)

# Fix nav callbacks
t = t.replace(
    '''                onSelect = { i ->
                    when (i) {
                        0 -> selectedNav = 0
                        1 -> { selectedNav = 1; onOpenCustomers() }
                        2 -> { selectedNav = 2; onOpenChat() }
                        3 -> { selectedNav = 3; onOpenWallet() }
                        4 -> { selectedNav = 4; onOpenProfile() }
                    }
                }''',
    '''                onSelect = { i ->
                    when (i) {
                        0 -> selectedNav = 0
                        1 -> { selectedNav = 1; onOpenCustomers() }
                        2 -> { selectedNav = 2; onOpenWallet() }
                        3 -> { selectedNav = 3; onOpenSummary() }
                        4 -> { selectedNav = 4; onOpenProfile() }
                    }
                }'''
)

# Add onOpenSummary parameter
t = t.replace(
    '    onOpenWallet: () -> Unit,\n    onOpenProfile: () -> Unit,',
    '    onOpenWallet: () -> Unit,\n    onOpenSummary: () -> Unit,\n    onOpenProfile: () -> Unit,'
)

# Fix HomeBottomNavBar badge - was checking idx==2 for notices, now wallet is 2
t = t.replace(
    '                        if (item.idx == 2 && unreadMessages > 0)',
    '                        if (item.idx == 0 && unreadMessages > 0)'
)

open(path, "w").write(t)
print("HomeScreen fixed - time, icons, nav")
print("http:// remaining:", t.count('http://'))

# ── 3. Fix NavGraph - add Summary route ──
nav_path = f"{BASE}/ui/navigation/AppNavGraph.kt"
t = open(nav_path).read()
t = re.sub(r'\[([^\]]+)\]\(http[s]?://[^\)]+\)', r'\1', t)

# Add Summary import
if 'SummaryScreen' not in t:
    t = t.replace(
        'import com.autoexpert.app.ui.notices.NoticesScreen',
        'import com.autoexpert.app.ui.notices.NoticesScreen\nimport com.autoexpert.app.ui.summary.SummaryScreen'
    )

# Add SUMMARY route constant
t = t.replace(
    '    const val NOTICES   = "notices"',
    '    const val NOTICES   = "notices"\n    const val SUMMARY   = "summary"'
)

# Add onOpenSummary to HomeScreen call
t = t.replace(
    '                onOpenWallet   = { navController.navigate(Routes.WALLET) },\n                onOpenProfile  = { navController.navigate(Routes.PROFILE) },',
    '                onOpenWallet   = { navController.navigate(Routes.WALLET) },\n                onOpenSummary  = { navController.navigate(Routes.SUMMARY) },\n                onOpenProfile  = { navController.navigate(Routes.PROFILE) },'
)

# Add Summary composable
t = t.replace(
    '        composable(Routes.NOTICES) {\n            NoticesScreen(onBack = { navController.popBackStack() })\n        }',
    '''        composable(Routes.NOTICES) {
            NoticesScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.SUMMARY) {
            SummaryScreen(
                onBack = { navController.popBackStack() },
                onHome = { navController.navigate(Routes.HOME) { popUpTo(Routes.HOME) { inclusive = true } } },
                onCustomers = { navController.navigate(Routes.CUSTOMERS) },
                onWallet = { navController.navigate(Routes.WALLET) },
                onProfile = { navController.navigate(Routes.PROFILE) }
            )
        }'''
)

open(nav_path, "w").write(t)
print("NavGraph fixed - Summary route added")

print("\nAll done! Now create SummaryScreen.kt separately.")
