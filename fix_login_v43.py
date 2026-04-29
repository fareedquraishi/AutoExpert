import re

# Fix LoginScreen - replace all broken references
path = "app/src/main/java/com/autoexpert/app/ui/login/LoginScreen.kt"
t = open(path).read()

# Fix all hyperlinks
t = re.sub(r'\[([^\]]+)\]\(http[s]?://[^\)]+\)', r'\1', t)

# Fix pin reference
t = t.replace('val pin by vm.pin.collectAsState()', 'val pin = vm.pin')
t = t.replace('val pin = vm.pin.collectAsState()', 'val pin = vm.pin')

# Fix pin.value.length reference (viewModel.pin.value.length -> pin.value.length)
t = t.replace('viewModel.pin.value.length', 'pin.value.length')

# Fix function name mismatches
t = t.replace('vm.appendPin(', 'vm.addDigit(')
t = t.replace('viewModel.appendPin(', 'vm.addDigit(')
t = t.replace('vm.getBiometricPin()', 'vm.pin.value')
t = t.replace('viewModel.getBiometricPin()', 'vm.pin.value')

open(path, "w").write(t)
print("LoginScreen fixed")
print("http:// remaining:", t.count('http://'))
print("appendPin remaining:", t.count('appendPin'))
print("getBiometricPin remaining:", t.count('getBiometricPin'))

# Fix LoginViewModel - fix saveSession call and API calls
vpath = "app/src/main/java/com/autoexpert/app/ui/login/LoginViewModel.kt"
t = open(vpath).read()

# Fix positional saveSession call -> named params
t = t.replace(
    'session.saveSession(remote.id, remote.name, baStationId, stationName, stLat, stLng, stRad)',
    '''session.saveSession(
                    baId        = remote.id,
                    baName      = remote.name,
                    stationId   = baStationId,
                    stationName = stationName,
                    lat         = stLat,
                    lng         = stLng,
                    radius      = stRad
                )'''
)

# Fix API calls missing apiKey/auth
t = t.replace(
    'api.getBaByPin("eq." + pin.value, apiKey, authHeader)',
    'api.getBaByPin(appPin = "eq." + pin.value, apiKey = apiKey, auth = authHeader)'
)
t = t.replace(
    'api.getStationById("eq." + baStationId, apiKey, authHeader)',
    'api.getStationById(id = "eq." + baStationId, apiKey = apiKey, auth = authHeader)'
)

open(vpath, "w").write(t)
print("LoginViewModel fixed")
print("http:// remaining:", t.count('http://'))
