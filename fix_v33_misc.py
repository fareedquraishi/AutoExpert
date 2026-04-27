# Fix 1: Disable HTTP logging (saves battery/resources)
import re, os

path = "app/src/main/java/com/autoexpert/app/di/AppModule.kt"
t = open(path).read()
t = re.sub(r'\[([^\]]+)\]\(http[s]?://[^\)]+\)', r'\1', t)
t = t.replace(
    ".addInterceptor(HttpLoggingInterceptor().apply {\n            level = HttpLoggingInterceptor.Level.BODY\n        })\n        ",
    ""
)
open(path, "w").write(t)
print("AppModule: logging disabled")

# Fix 2: Check if SaleEntryQueueDao has upsertAll
dao = open("app/src/main/java/com/autoexpert/app/data/local/dao/Daos.kt").read()
print("SaleEntryQueueDao upsertAll:", "fun upsertAll" in dao and "SaleEntryQueue" in dao[dao.find("fun upsertAll")-200:dao.find("fun upsertAll")] if "fun upsertAll" in dao else False)
print("SaleEntryQueueDao insert:", "fun insert" in dao)

# Fix all kotlin files hyperlinks
fixed = 0
for root, dirs, files in os.walk("app/src/main/java"):
    for f in files:
        if f.endswith(".kt"):
            fp = os.path.join(root, f)
            t = open(fp).read()
            ft = re.sub(r'\[([^\]]+)\]\(http[s]?://[^\)]+\)', r'\1', t)
            if ft != t:
                open(fp, "w").write(ft)
                fixed += 1
print(f"Fixed hyperlinks in {fixed} files")
