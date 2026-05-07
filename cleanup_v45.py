import re, os

# ── 1. Remove biometric from LoginScreen ──
path = "app/src/main/java/com/autoexpert/app/ui/login/LoginScreen.kt"
t = open(path).read()
t = re.sub(r'\[([^\]]+)\]\(http[s]?://[^\)]+\)', r'\1', t)

# Remove biometric imports
t = t.replace("import androidx.biometric.BiometricManager\n", "")
t = t.replace("import androidx.biometric.BiometricPrompt\n", "")
t = t.replace("import androidx.fragment.app.FragmentActivity\n", "")

# Remove onBiometric parameter from PinPad call
t = t.replace(
    "                onBiometric = { triggerBiometric(context, vm, onLoginSuccess) },\n",
    ""
)

# Remove BiometricButton block
t = re.sub(r'\s*// Biometric button\s*\n\s*BiometricButton \{[^}]+\}', '', t)

# Remove onBiometric from PinPad composable parameter
t = t.replace("    onBiometric: () -> Unit,\n", "")

# Remove BIO case from when block
t = re.sub(r'\s*"BIO"\s*->\s*onBiometric\(\)', '', t)

# Remove BiometricButton function
t = re.sub(r'\nprivate fun BiometricButton\(onClick: \(\) -> Unit\) \{.*?\n\}', '', t, flags=re.DOTALL)

# Remove triggerBiometric function entirely
t = re.sub(r'\nprivate fun triggerBiometric\(.*?\n\}', '', t, flags=re.DOTALL)

open(path, "w").write(t)
print("Biometric removed from LoginScreen")

# ── 2. Update NavGraph - remove messaging route, keep notices ──
nav_path = "app/src/main/java/com/autoexpert/app/ui/navigation/AppNavGraph.kt"
t = open(nav_path).read()
t = re.sub(r'\[([^\]]+)\]\(http[s]?://[^\)]+\)', r'\1', t)

# Remove messaging import
t = t.replace("import com.autoexpert.app.ui.messaging.MessagingScreen\n", "")

# Remove MESSAGING route constant
t = t.replace('    const val MESSAGING = "messaging"\n', "")

# Change onOpenChat to go to notices instead
t = t.replace(
    'onOpenChat     = { navController.navigate(Routes.MESSAGING) },',
    'onOpenChat     = { navController.navigate(Routes.NOTICES) },'
)

# Remove MessagingScreen composable block
t = re.sub(r'\s*composable\(Routes\.MESSAGING\) \{[^}]+\}', '', t)

open(nav_path, "w").write(t)
print("Messaging route removed from NavGraph")

# ── 3. Update HomeScreen nav bar - Chat→Notices, remove Leave ──
home_path = "app/src/main/java/com/autoexpert/app/ui/home/HomeScreen.kt"
t = open(home_path).read()
t = re.sub(r'\[([^\]]+)\]\(http[s]?://[^\)]+\)', r'\1', t)

# Change nav bar: 4 items only - Home, Customers, Notices, Wallet, Profile
t = t.replace(
    'NavItem("Chat",      Icons.Filled.Chat,         2),',
    'NavItem("Notices",   Icons.Filled.Notifications, 2),'
)

open(home_path, "w").write(t)
print("HomeScreen nav updated - Chat replaced with Notices")

print("\nAll done! Summary:")
print("- Biometric removed from login")
print("- Chat tab now opens Notices")
print("- Messaging screen removed from nav")
