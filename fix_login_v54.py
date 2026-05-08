import re

path = "app/src/main/java/com/autoexpert/app/ui/login/LoginScreen.kt"
t = open(path).read()

# Fix hyperlinks
t = re.sub(r'\[([^\]]+)\]\(http[s]?://[^\)]+\)', r'\1', t)

# Add version after Fintectual
old = 'Text("Fintectual Pvt Ltd", fontSize = 10.sp, color = PetronasGreen.copy(.5f),\n                fontWeight = FontWeight.Bold, letterSpacing = .3.sp)'
new = old + '\n            Text("v2.0", fontSize = 9.sp, color = Color.White.copy(.25f), letterSpacing = 1.sp)'

if old in t:
    t = t.replace(old, new)
    print("Version added to login")
else:
    print("Pattern not found - searching...")
    idx = t.find("Fintectual Pvt Ltd")
    print(repr(t[idx:idx+200]))

# Remove euro_logo image from login screen
# Find the Box containing euro_logo and remove it
t = re.sub(r'\n\s*//.*[Ee]uro.*\n\s*Box\([^)]+\)[^{]*\{[^}]*Image\([^)]*euro_logo[^)]*\)[^}]*\}', '', t)
t = re.sub(r'\n\s*Image\(\s*painterResource\(R\.drawable\.euro_logo\)[^)]*\)', '', t)

open(path, "w").write(t)
print("Done. http:// remaining:", t.count('http://'))
