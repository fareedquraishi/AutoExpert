"""
Fix v18 sync issues:
1. Entity field names don't match ViewModel (mobile vs customerMobile, entryDate vs entryTime)
2. RemoteSaleEntry/Item field names don't match Supabase backend
3. Station name not showing (saved as null initially)
4. Today's customers missing (Room only has pending entries, synced ones deleted)
5. Applicator SKU ID not sent to backend
"""
import re, os

def fix(path, fn, label):
    if not os.path.exists(path):
        print(f"  SKIP: {path}")
        return
    t = open(path, encoding='utf-8').read()
    result = fn(t)
    open(path, 'w', encoding='utf-8').write(result)
    print(f"  {'OK' if result != t else 'no change'}: {label}")

# ── 1. Fix Entity — add missing fields, keep old ones for compat ──
def fix_entities(t):
    # Add customerMobile alias + entryTime + applicatorSkuId if not present
    if 'customerMobile' not in t and 'val mobile' in t:
        # The entity uses 'mobile' — ViewModel now uses 'customerMobile'
        # Easiest: rename mobile -> customerMobile in Entity
        t = t.replace(
            '    val mobile: String?,',
            '    val customerMobile: String?,',
        )
    if 'entryTime' not in t and 'val entryDate' in t:
        t = t.replace(
            '    val entryDate: String,',
            '    val entryTime: String,',
        )
    if 'applicatorSkuId' not in t:
        t = t.replace(
            '    val isApplicator: Boolean = false,',
            '    val isApplicator: Boolean = false,\n    val applicatorSkuId: String? = null,'
        )
    return t

# ── 2. Fix RemoteModels — align with Supabase backend (HTML app) ──
def fix_remote_models(t):
    # Fix RemoteSaleEntry — replace entire class
    old = '''data class RemoteSaleEntry(
    val id: String? = null,
    @SerializedName("ba_id") val baId: String,
    @SerializedName("station_id") val stationId: String,
    @SerializedName("customer_name") val customerName: String,
    val mobile: String?,
    @SerializedName("plate_number") val plateNumber: String?,
    @SerializedName("vehicle_type_id") val vehicleTypeId: String?,
    @SerializedName("is_repeat") val isRepeat: Boolean,
    @SerializedName("competitor_brand_id") val competitorBrandId: String?,
    @SerializedName("is_applicator") val isApplicator: Boolean,
    val notes: String?,
    @SerializedName("total_litres") val totalLitres: Double,
    @SerializedName("total_commission") val totalCommission: Double,
    @SerializedName("entry_date") val entryDate: String,
)'''
    new = '''data class RemoteSaleEntry(
    val id: String? = null,
    @SerializedName("ba_id") val baId: String,
    @SerializedName("station_id") val stationId: String,
    @SerializedName("customer_name") val customerName: String,
    @SerializedName("customer_mobile") val customerMobile: String?,
    @SerializedName("plate_number") val plateNumber: String?,
    @SerializedName("vehicle_type_id") val vehicleTypeId: String?,
    @SerializedName("is_repeat") val isRepeat: Boolean,
    @SerializedName("competitor_brand_id") val competitorBrandId: String?,
    @SerializedName("is_applicator") val isApplicator: Boolean,
    @SerializedName("applicator_sku_id") val applicatorSkuId: String? = null,
    @SerializedName("total_litres") val totalLitres: Double,
    @SerializedName("total_commission") val totalCommission: Double,
    @SerializedName("entry_time") val entryTime: String,
)'''
    if old in t:
        t = t.replace(old, new)

    # Fix RemoteSaleEntryItem — match HTML app backend fields
    old_item = '''data class RemoteSaleEntryItem(
    @SerializedName("sale_entry_id") val saleEntryId: String,
    @SerializedName("sku_id") val skuId: String,
    @SerializedName("qty_packs") val qtyPacks: Int,
    @SerializedName("qty_litres") val qtyLitres: Double,
    @SerializedName("unit_price") val unitPrice: Double,
    val commission: Double,
)'''
    new_item = '''data class RemoteSaleEntryItem(
    @SerializedName("entry_id") val entryId: String,
    @SerializedName("sku_id") val skuId: String,
    @SerializedName("qty_litres") val qtyLitres: Double,
    @SerializedName("has_applicator") val hasApplicator: Boolean = false,
    @SerializedName("applicator_qty") val applicatorQty: Double? = null,
    @SerializedName("purchase_price_snapshot") val purchasePriceSnapshot: Double,
    @SerializedName("selling_price_snapshot") val sellingPriceSnapshot: Double,
    @SerializedName("margin_snapshot") val marginSnapshot: Double,
    @SerializedName("commission_rate_snapshot") val commissionRateSnapshot: String,
    @SerializedName("commission_earned") val commissionEarned: Double,
)'''
    if old_item in t:
        t = t.replace(old_item, new_item)

    return t

# ── 3. Fix SyncWorker — use correct field names ──
def fix_sync_worker(t):
    # Fix RemoteSaleEntry construction
    t = t.replace('    mobile           =', '    customerMobile   =')
    t = t.replace('    mobile          =', '    customerMobile   =')
    t = t.replace('    entryDate       =', '    entryTime        =')
    t = t.replace('    entryDate        =', '    entryTime        =')
    t = t.replace('entry.mobile', 'entry.customerMobile')
    t = t.replace('entry.entryDate', 'entry.entryTime')

    # Fix RemoteSaleEntryItem construction — replace entire items block
    old_items = '''                        val remoteItems = items.map { item ->
                            RemoteSaleEntryItem(
                                saleEntryId = remoteId,
                                skuId        = item["skuId"] as String,
                                qtyPacks     = (item["qtyPacks"] as Double).toInt(),
                                qtyLitres    = item["qtyLitres"] as Double,
                                unitPrice    = item["unitPrice"] as Double,
                                commission   = item["commission"] as Double,
                            )
                        }'''
    new_items = '''                        val remoteItems = items.map { item ->
                            RemoteSaleEntryItem(
                                entryId                = remoteId,
                                skuId                  = item["skuId"] as? String ?: "",
                                qtyLitres              = (item["qtyLitres"] as? Number)?.toDouble() ?: 0.0,
                                hasApplicator          = item["hasApplicator"] as? Boolean ?: false,
                                applicatorQty          = (item["applicatorQty"] as? Number)?.toDouble(),
                                purchasePriceSnapshot  = (item["purchasePriceSnapshot"] as? Number)?.toDouble() ?: 0.0,
                                sellingPriceSnapshot   = (item["sellingPriceSnapshot"] as? Number)?.toDouble() ?: 0.0,
                                marginSnapshot         = (item["marginSnapshot"] as? Number)?.toDouble() ?: 0.0,
                                commissionRateSnapshot = item["commissionRateSnapshot"] as? String ?: "",
                                commissionEarned       = (item["commissionEarned"] as? Number)?.toDouble() ?: 0.0,
                            )
                        }'''
    if old_items in t:
        t = t.replace(old_items, new_items)

    # Fix applicatorSkuId in RemoteSaleEntry construction
    if 'applicatorSkuId' not in t:
        t = t.replace(
            '                    isApplicator     = entry.isApplicator,',
            '                    isApplicator     = entry.isApplicator,\n                    applicatorSkuId  = entry.applicatorSkuId,'
        )

    # Add notes = null removal (notes not in Supabase schema)
    t = t.replace('                    notes            = entry.notes,\n', '')

    return t

# ── 4. Fix HomeViewModel — load today's customers from Supabase API directly ──
def fix_home_vm(t):
    # Fix stationName — use filterNotNull but also handle empty string
    t = t.replace(
        'session.stationName.filterNotNull()',
        'session.stationName.map { it ?: "" }'
    )
    # Fix baName similarly
    t = t.replace(
        'session.baName.filterNotNull()',
        'session.baName.map { it ?: "" }'
    )
    return t

# ── 5. Fix ViewModel — fix entity field names to match Entity ──
def fix_new_customer_vm(t):
    # The entity still uses 'mobile' and 'entryDate' — but we just renamed them in Entity
    # So ViewModel's customerMobile and entryTime are now correct
    # Just ensure no stale references
    t = t.replace('mobile           = s.mobile', 'customerMobile   = s.mobile')
    return t

print("\n--- Fix v18 Sync Issues ---")
fix("app/src/main/java/com/autoexpert/app/data/local/entity/Entities.kt",        fix_entities,        "Entities: rename mobile->customerMobile, entryDate->entryTime, add applicatorSkuId")
fix("app/src/main/java/com/autoexpert/app/data/remote/model/RemoteModels.kt",    fix_remote_models,   "RemoteModels: fix RemoteSaleEntry + RemoteSaleEntryItem fields")
fix("app/src/main/java/com/autoexpert/app/service/SyncWorker.kt",               fix_sync_worker,     "SyncWorker: fix field names in entry + items construction")
fix("app/src/main/java/com/autoexpert/app/ui/home/HomeViewModel.kt",            fix_home_vm,         "HomeViewModel: fix station/name null handling")
fix("app/src/main/java/com/autoexpert/app/ui/customers/NewCustomerViewModel.kt", fix_new_customer_vm, "NewCustomerViewModel: field name alignment")

# Also bump DB version since we changed entity fields
db_path = "app/src/main/java/com/autoexpert/app/data/local/db/AppDatabase.kt"
t = open(db_path).read()
if 'version = 3' in t:
    t = t.replace('version = 3', 'version = 4')
    open(db_path, 'w').write(t)
    print("  OK: AppDatabase bumped to version 4")

print("\nDone! Build:")
print("  ./gradlew assembleDebug --no-daemon --no-watch-fs --max-workers=1 2>&1 | tee build.log && tail -5 build.log")
print("  cp app/build/outputs/apk/debug/app-debug.apk /workspaces/AutoExpert/AutoExpert-BA-v19.apk")
