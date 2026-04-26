"""
Fix two issues in NewCustomerViewModel:
1. Lines 111-113: dangling s.copy block (orphaned from old removeFromCart)
2. Line 119: http://sku.id hyperlink corruption
"""
path = "app/src/main/java/com/autoexpert/app/ui/customers/NewCustomerViewModel.kt"
t = open(path, encoding='utf-8').read()

# Fix 1: Remove the dangling orphaned block (lines 111-113)
# This is the leftover tail of the OLD removeFromCart that wasn't cleaned up
t = t.replace(
    "\n            s.copy(cart = newCart, isApplicator = if (newCart.values.none { it.qty > 0 }) false else s.isApplicator)\n        }\n    }\n\n    fun onApplicatorToggle",
    "\n\n    fun onApplicatorToggle"
)

# Fix 2: Remove hyperlink corruption
t = t.replace("selectedItems[0].http://sku.id", "selectedItems[0].sku.id")
t = t.replace("http://sku.id", "sku.id")

open(path, 'w', encoding='utf-8').write(t)

# Verify
lines = t.split('\n')
print("Lines 105-125:")
for i, l in enumerate(lines[104:125], 105):
    print(f"  {i}: {l}")
print("\n✅ Fixed — now build:")
print("  ./gradlew assembleDebug --no-daemon --no-watch-fs --max-workers=1 2>&1 | tee build.log && tail -5 build.log")
