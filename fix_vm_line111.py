"""
Fix line 111 syntax error in NewCustomerViewModel.
The duplicate-removal regex cut too much, leaving broken code.
This script reads lines 100-130, shows what's there, then rewrites the affected section cleanly.
"""
import re, os

path = "app/src/main/java/com/autoexpert/app/ui/customers/NewCustomerViewModel.kt"
t = open(path, encoding='utf-8').read()
lines = t.split('\n')

print("Lines 105-120:")
for i, l in enumerate(lines[104:120], 105):
    print(f"  {i}: {repr(l)}")

# The broken section around onApplicatorToggle / removeFromCart
# Find and replace the entire broken block with clean versions

# Pattern: find onApplicatorToggle and everything until submitWithoutProduct closes
broken_start = '    fun onApplicatorToggle'
broken_end_marker = '    fun submitWithoutProduct'

# Find indices
start_idx = t.find(broken_start)
end_idx = t.find(broken_end_marker)

if start_idx == -1:
    print("ERROR: onApplicatorToggle not found")
    exit(1)

# Find end of submitWithoutProduct function
end_of_submit = t.find('\n    fun ', end_idx + 10)
if end_of_submit == -1:
    end_of_submit = t.find('\n    val ', end_idx + 10)
if end_of_submit == -1:
    end_of_submit = t.find('\n}', end_idx + 10)

print(f"\nReplacing from char {start_idx} to {end_of_submit}")
print("Current block preview:")
print(repr(t[start_idx:start_idx+200]))

clean_block = '''    fun onApplicatorToggle(v: Boolean) = _state.update {
        it.copy(
            isApplicator = v,
            applicatorSkuId = if (!v) null
            else if (selectedItems.size == 1) selectedItems[0].sku.id
            else it.applicatorSkuId
        )
    }

    fun onApplicatorSkuSelected(skuId: String) = _state.update { it.copy(applicatorSkuId = skuId) }

    fun removeFromCart(skuId: String) {
        _state.update { s ->
            val newCart = s.cart.toMutableMap()
            newCart[skuId]?.let { newCart[skuId] = it.copy(qty = 0) }
            val hasItems = newCart.values.any { it.qty > 0 }
            s.copy(
                cart = newCart,
                isApplicator = if (!hasItems) false else s.isApplicator,
                applicatorSkuId = if (s.applicatorSkuId == skuId) null else s.applicatorSkuId
            )
        }
    }

    fun submitWithoutProduct() {
        _state.update { s ->
            s.copy(cart = s.cart.mapValues { (_, v) -> v.copy(qty = 0) })
        }
        submit()
    }

'''

# Find where the NEXT function starts after submitWithoutProduct
next_fn = t.find('\n    fun ', end_idx + 10)
if next_fn == -1:
    next_fn = t.find('\n    val ', end_idx + 10)

result = t[:start_idx] + clean_block + t[next_fn+1:]

open(path, 'w', encoding='utf-8').write(result)
print("\n✅ NewCustomerViewModel fixed")
print("\nVerify lines 108-140:")
lines2 = result.split('\n')
for i, l in enumerate(lines2[107:140], 108):
    print(f"  {i}: {l}")

print("\nNow build:")
print("  ./gradlew assembleDebug --no-daemon --no-watch-fs --max-workers=1 2>&1 | tee build.log && tail -5 build.log")
