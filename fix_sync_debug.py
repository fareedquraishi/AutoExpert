path = "app/src/main/java/com/autoexpert/app/ui/home/HomeViewModel.kt"
t = open(path).read()

# Replace syncRemoteData with a version that shows results visibly
old_sync = '''    fun syncRemoteData() {
        viewModelScope.launch {
            val baId = session.baId.first() ?: run { _uiState.update { it.copy(syncError = "baId is null - not logged in") }; return@launch }
            try {
                // Reference data — SKUs, vehicle types, competitor brands
                val skuResp = api.getSkus(apiKey = apiKey, auth = authHeader)
                android.util.Log.d("AutoExpert", "SKUs: code=" + skuResp.code() + " size=" + (skuResp.body()?.size ?: -1))
                skuResp.body()?.let { list ->
                    skuDao.upsertAll(list.map { s -> SkuEntity(
                        id = s.id, name = s.name, productType = s.productType,
                        volumeMl = s.volumeMl, purchasePrice = s.purchasePrice,
                        marginPercent = s.marginPercent, sellingPrice = s.sellingPrice,
                        isActive = s.isActive
                    )})
                }
                val vtResp = api.getVehicleTypes(apiKey = apiKey, auth = authHeader)
                android.util.Log.d("AutoExpert", "VehicleTypes: code=" + vtResp.code() + " size=" + (vtResp.body()?.size ?: -1))
                vtResp.body()?.let { list ->
                    vehicleTypeDao.upsertAll(list.map { v -> VehicleTypeEntity(
                        id = v.id, name = v.name, iconKey = v.iconKey, sortOrder = v.sortOrder
                    )})
                }'''

new_sync = '''    fun syncRemoteData() {
        viewModelScope.launch {
            val baId = session.baId.first() ?: run { _uiState.update { it.copy(syncError = "baId null") }; return@launch }
            val log = StringBuilder("baId=$baId ")
            try {
                // SKUs
                val skuResp = api.getSkus(apiKey = apiKey, auth = authHeader)
                val skuCount = skuResp.body()?.size ?: 0
                log.append("SKU:${skuResp.code()}/$skuCount ")
                skuResp.body()?.let { list ->
                    skuDao.upsertAll(list.map { s -> SkuEntity(
                        id = s.id, name = s.name, productType = s.productType,
                        volumeMl = s.volumeMl, purchasePrice = s.purchasePrice,
                        marginPercent = s.marginPercent, sellingPrice = s.sellingPrice,
                        isActive = s.isActive
                    )})
                }
                // Vehicle types
                val vtResp = api.getVehicleTypes(apiKey = apiKey, auth = authHeader)
                val vtCount = vtResp.body()?.size ?: 0
                log.append("VT:${vtResp.code()}/$vtCount ")
                vtResp.body()?.let { list ->
                    vehicleTypeDao.upsertAll(list.map { v -> VehicleTypeEntity(
                        id = v.id, name = v.name, iconKey = v.iconKey, sortOrder = v.sortOrder
                    )})
                }'''

if old_sync in t:
    t = t.replace(old_sync, new_sync)
    print("Replaced sync start")
else:
    print("Pattern not found - trying line-by-line fix")

# Fix the end of syncRemoteData to show the log
t = t.replace(
    "                session.updateLastSync()\n            } catch (e: Exception) {\n                val msg = e.javaClass.simpleName + \": \" + (e.message ?: \"unknown\")\n                android.util.Log.e(\"AutoExpert\", \"Sync failed: $msg\", e)\n                _uiState.update { it.copy(syncError = msg) }\n            }",
    "                log.append(\"OK\")\n                _uiState.update { it.copy(syncError = log.toString()) }\n                session.updateLastSync()\n            } catch (e: Exception) {\n                val msg = e.javaClass.simpleName + \": \" + (e.message ?: \"?\")\n                _uiState.update { it.copy(syncError = \"ERR: $msg | $log\") }\n            }"
)

open(path, "w").write(t)
print("Done - sync status will show as Toast on home screen")
