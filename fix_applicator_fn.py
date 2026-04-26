"""
Rewrite onApplicatorToggle cleanly - avoid the corrupted line entirely
by replacing the whole function with a simpler one-liner version
"""
path = "app/src/main/java/com/autoexpert/app/ui/customers/NewCustomerViewModel.kt"
t = open(path, encoding='utf-8').read()

# Print raw bytes of the problematic line to see exactly what's there
lines = t.split('\n')
for i, l in enumerate(lines[110:120], 111):
    print(f"{i}: {repr(l)}")

# Replace the entire onApplicatorToggle block (lines 112-119)
# with a simpler version that avoids the corrupted selectedItems[0].sku.id reference
old_blocks = [
    # Variant 1 - with markdown link
    '    fun onApplicatorToggle(v: Boolean) = _state.update {\n        it.copy(\n            isApplicator = v,\n            applicatorSkuId = if (!v) null\n            else if (selectedItems.size == 1) selectedItems[0].[sku.id](http://sku.id)\n            else it.applicatorSkuId\n        )\n    }',
]

new_block = '''    fun onApplicatorToggle(v: Boolean) {
        val autoId = if (v && selectedItems.size == 1) selectedItems.first().sku.id else null
        _state.update { it.copy(isApplicator = v, applicatorSkuId = if (!v) null else autoId ?: it.applicatorSkuId) }
    }'''

replaced = False
for old in old_blocks:
    if old in t:
        t = t.replace(old, new_block)
        replaced = True
        print("\n✅ Replaced via exact match")
        break

if not replaced:
    # Nuclear option: find the function by line numbers and rewrite
    lines = t.split('\n')
    start = None
    for i, l in enumerate(lines):
        if 'fun onApplicatorToggle' in l:
            start = i
            break
    if start is not None:
        # Find closing brace
        depth = 0
        end = start
        for i in range(start, min(start+15, len(lines))):
            depth += lines[i].count('{') - lines[i].count('}')
            if depth <= 0 and i > start:
                end = i
                break
        print(f"\nReplacing lines {start+1} to {end+1}")
        print("Old:", repr('\n'.join(lines[start:end+1])))
        lines[start:end+1] = new_block.split('\n')
        t = '\n'.join(lines)
        replaced = True
        print("✅ Replaced via line scan")

if replaced:
    open(path, 'w', encoding='utf-8').write(t)
    lines2 = t.split('\n')
    print("\nResult lines 108-125:")
    for i, l in enumerate(lines2[107:125], 108):
        print(f"  {i}: {l}")
    print("\n✅ Done - now build:")
    print("  ./gradlew assembleDebug --no-daemon --no-watch-fs --max-workers=1 2>&1 | tee build.log && tail -5 build.log")
else:
    print("❌ Could not find the function to replace")
