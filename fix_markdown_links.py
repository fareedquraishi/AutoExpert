path = "app/src/main/java/com/autoexpert/app/ui/customers/NewCustomerViewModel.kt"
t = open(path, encoding='utf-8').read()

# The corruption is markdown link format: [sku.id](http://sku.id)
# Replace ALL such markdown link patterns in the entire file
import re

before = t.count('[sku.id](http://sku.id)')
t = t.replace('[sku.id](http://sku.id)', 'sku.id')

# Also fix any other markdown-link corruptions in the whole file
# Pattern: [word.word](http://word.word)  →  word.word
t = re.sub(r'\[(\w+\.\w+)\]\(http://\1\)', r'\1', t)
# Pattern: [word.word.word](http://word.word.word)  →  word.word.word  
t = re.sub(r'\[(\w+\.\w+\.\w+)\]\(http://\1\)', r'\1', t)

after = t.count('[sku.id](http://sku.id)')
open(path, 'w', encoding='utf-8').write(t)

print(f"Replaced {before - after} occurrences of markdown links")
print("Line 116:", t.split('\n')[115])
print("\n✅ Done — now build:")
print("  ./gradlew assembleDebug --no-daemon --no-watch-fs --max-workers=1 2>&1 | tee build.log && tail -5 build.log")
