import re

# ── 1. Fix HomeScreen - nav bar + timezone + KPI color ──
path = "app/src/main/java/com/autoexpert/app/ui/home/HomeScreen.kt"
t = open(path).read()
t = re.sub(r'\[([^\]]+)\]\(http[s]?://[^\)]+\)', r'\1', t)

# Fix bottom nav onSelect - was missing wallet and profile callbacks
t = t.replace(
    '''                onSelect = { i ->
                    selectedNav = i
                    when (i) {
                        1 -> onOpenCustomers()
                        2 -> onOpenChat()
                        3 -> onOpenWallet()
                        4 -> onOpenProfile()
                    }
                }''',
    '''                onSelect = { i ->
                    when (i) {
                        0 -> selectedNav = 0
                        1 -> { selectedNav = 1; onOpenCustomers() }
                        2 -> { selectedNav = 2; onOpenChat() }
                        3 -> { selectedNav = 3; onOpenWallet() }
                        4 -> { selectedNav = 4; onOpenProfile() }
                    }
                }'''
)

# Fix timezone - use PKT UTC+5
t = t.replace(
    'val timeStr = try {\n        val d = SimpleDateFormat("yyyy-MM-dd\'T\'HH:mm:ss", Locale.getDefault())\n            .parse(entry.entryTime.take(19))\n        SimpleDateFormat("hh:mm a", Locale.getDefault()).format(d ?: Date())',
    '''val timeStr = try {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        sdf.timeZone = java.util.TimeZone.getTimeZone("UTC")
        val d = sdf.parse(entry.entryTime.take(19))
        val outFmt = SimpleDateFormat("hh:mm a", Locale.getDefault())
        outFmt.timeZone = java.util.TimeZone.getTimeZone("Asia/Karachi")
        outFmt.format(d ?: Date())'''
)

# Fix KPI req/hr - make it amber color and larger font (already amber/green, just increase size)
t = t.replace(
    'KpiCell(col4Val, col4Lbl, Modifier.weight(1.3f), green = true)',
    'KpiCell(col4Val, col4Lbl, Modifier.weight(1.3f), amber = true)'
)

# Fix KpiCell value font size from 12sp to 14sp
t = t.replace(
    'Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold,',
    'Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold,'
)

open(path, "w").write(t)
print("HomeScreen fixed")
print("http:// remaining:", t.count('http://'))

# ── 2. Fix WalletScreen - reduce header padding ──
wpath = "app/src/main/java/com/autoexpert/app/ui/wallet/WalletScreen.kt"
t = open(wpath).read()
t = t.replace(
    '.padding(16.dp, 24.dp, 16.dp, 28.dp)',
    '.padding(16.dp, 16.dp, 16.dp, 20.dp)'
)
t = t.replace(
    'Text("Rs ${pkrFmt.format(ui.unpaidBalance.toLong())}",\n                                fontSize = 38.sp,',
    'Text("Rs ${pkrFmt.format(ui.unpaidBalance.toLong())}",\n                                fontSize = 32.sp,'
)
t = t.replace(
    'Spacer(Modifier.height(20.dp))',
    'Spacer(Modifier.height(12.dp))'
)
open(wpath, "w").write(t)
print("WalletScreen header reduced")
