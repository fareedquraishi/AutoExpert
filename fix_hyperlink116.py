path = "app/src/main/java/com/autoexpert/app/ui/customers/NewCustomerViewModel.kt"
t = open(path, encoding='utf-8').read()

# Print the exact bytes around the problem line
idx = t.find("selectedItems[0].")
print("Exact text around line 116:")
print(repr(t[idx:idx+60]))

# Fix all variations
import re
t = re.sub(r'selectedItems\[0\]\.[^\s\n]*sku\.id[^\s\n]*', 'selectedItems[0].sku.id', t)
t = re.sub(r'http://sku\.id', 'sku.id', t)

open(path, 'w', encoding='utf-8').write(t)

lines = t.split('\n')
print("\nLine 116 now:")
print(repr(lines[115]))
print("\n✅ Done — now build:")
print("  ./gradlew assembleDebug --no-daemon --no-watch-fs --max-workers=1 2>&1 | tee build.log && tail -5 build.log")
