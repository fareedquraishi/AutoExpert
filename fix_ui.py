import os
path = "app/src/main/java/com/autoexpert/app/ui/login/LoginScreen.kt"
with open(path, "r") as f:
    content = f.read()

# Add missing imports if not present
if "androidx.compose.runtime.getValue" not in content:
    content = content.replace("package com.autoexpert.app.ui.login", "package com.autoexpert.app.ui.login\n\nimport androidx.compose.runtime.getValue\nimport androidx.compose.runtime.collectAsState")

# Replace broken references
content = content.replace("val state = viewModel.state.collectAsState()", "val state by viewModel.state.collectAsState()")
content = content.replace("viewModel.appendPin(", "viewModel.addDigit(")
content = content.replace("pin.length", "viewModel.pin.value.length")
content = content.replace("viewModel.getBiometricPin()", "viewModel.pin.value")

with open(path, "w") as f:
    f.write(content)
print("UI file fixed successfully.")
