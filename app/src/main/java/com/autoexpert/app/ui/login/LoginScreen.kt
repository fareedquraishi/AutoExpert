package com.autoexpert.app.ui.login

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import com.autoexpert.app.R
import com.autoexpert.app.ui.components.DarkGradient
import com.autoexpert.app.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    vm: LoginViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsState()
    val pin by vm.pin.collectAsState()
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    var showResetDialog by remember { mutableStateOf(false) }
    var showResetSentDialog by remember { mutableStateOf(false) }

    // Shake animation on error
    val shakeOffset = remember { Animatable(0f) }
    LaunchedEffect(state) {
        when (state) {
            is LoginState.Success -> onLoginSuccess()
            is LoginState.Error -> {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                repeat(4) {
                    shakeOffset.animateTo(if (it % 2 == 0) 10f else -10f,
                        tween(60, easing = LinearEasing))
                }
                shakeOffset.animateTo(0f, tween(60))
            }
            is LoginState.ResetSent -> showResetSentDialog = true
            else -> {}
        }
    }

    Box(Modifier.fillMaxSize().background(DarkGradient)) {

        // Glow
        Box(
            Modifier.size(320.dp).align(Alignment.Center).offset(y = (-100).dp)
                .background(
                    Brush.radialGradient(listOf(PetronasGreen.copy(.11f), Color.Transparent)),
                    CircleShape
                )
        )

        // Euro icon — top left
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(18.dp)
                .size(34.dp)
                .background(Color.White.copy(.05f), RoundedCornerShape(10.dp))
                .border(1.dp, Color.White.copy(.08f), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Image(painterResource(R.drawable.euro_logo), "Euro", Modifier.size(110.dp))
        }

        Column(
            modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Petronas logo box
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .background(Color.White.copy(.04f), RoundedCornerShape(24.dp))
                    .border(1.dp,
                        Brush.linearGradient(listOf(PetronasGreen.copy(.28f), PetronasGreen.copy(.1f))),
                        RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(painterResource(R.drawable.petronas_logo), "Petronas",
                    Modifier.size(72.dp, 58.dp))
            }

            Spacer(Modifier.height(14.dp))
            Text("PETRONAS", fontSize = 18.sp, fontWeight = FontWeight.Black,
                color = Color.White, letterSpacing = 3.sp)
            Text("Auto Expert Centre", fontSize = 10.sp, color = Color.White.copy(.42f),
                letterSpacing = 2.sp, modifier = Modifier.padding(top = 3.dp))
            Text("Brand Ambassador App", fontSize = 10.sp, color = Color.White.copy(.26f),
                letterSpacing = 1.5.sp, modifier = Modifier.padding(top = 7.dp))

            // Divider
            Spacer(Modifier.height(14.dp))
            Box(Modifier.width(28.dp).height(1.5.dp)
                .background(Brush.horizontalGradient(
                    listOf(Color.Transparent, PetronasGreen.copy(.45f), Color.Transparent)),
                    RoundedCornerShape(1.dp)))
            Spacer(Modifier.height(16.dp))

            // PIN label
            Text("ENTER YOUR PIN", fontSize = 9.sp, fontWeight = FontWeight.Bold,
                color = Color.White.copy(.38f), letterSpacing = 2.sp)
            Spacer(Modifier.height(12.dp))

            // PIN dots with shake
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.offset(x = shakeOffset.value.dp)
            ) {
                repeat(6) { i ->
                    val filled = i < pin.length
                    val isError = state is LoginState.Error
                    Box(
                        Modifier.size(12.dp)
                            .background(
                                when {
                                    isError && filled -> AccentRed
                                    filled -> PetronasGreen
                                    else -> Color.Transparent
                                },
                                CircleShape
                            )
                            .border(2.dp,
                                when {
                                    isError && filled -> AccentRed
                                    filled -> PetronasGreen
                                    else -> Color.White.copy(.18f)
                                },
                                CircleShape
                            )
                    )
                }
            }

            // Error message
            val errorMsg = (state as? LoginState.Error)?.message ?: ""
            Text(
                text = errorMsg,
                fontSize = 11.sp, color = AccentRed,
                modifier = Modifier.height(22.dp).padding(top = 6.dp),
                textAlign = TextAlign.Center
            )

            // PIN Pad
            PinPad(
                onDigit = { vm.appendPin(it); haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove) },
                onDelete = { vm.deletePin(); haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove) },
                onBiometric = { triggerBiometric(context, vm, onLoginSuccess) },
                isLoading = state is LoginState.Checking
            )

            Spacer(Modifier.height(16.dp))

            // Biometric button
            BiometricButton {
                triggerBiometric(context, vm, onLoginSuccess)
            }

            Spacer(Modifier.height(10.dp))

            // Reset link
            Text(
                "Forgot PIN? Request Reset →",
                fontSize = 11.sp, color = Color.White.copy(.25f),
                modifier = Modifier.clickable { showResetDialog = true }
            )
        }

        // Powered by
        Column(
            modifier = Modifier.align(Alignment.BottomCenter)
                .navigationBarsPadding().padding(bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text("POWERED BY", fontSize = 8.sp, color = Color.White.copy(.2f), letterSpacing = 1.sp)
            Text("Fintectual Pvt Ltd", fontSize = 10.sp, color = PetronasGreen.copy(.5f),
                fontWeight = FontWeight.Bold, letterSpacing = .3.sp)
        }

        // Loading overlay
        if (state is LoginState.Checking) {
            Box(Modifier.fillMaxSize().background(Color.Black.copy(.4f)),
                contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    CircularProgressIndicator(color = PetronasGreen, modifier = Modifier.size(40.dp))
                    Text("Verifying...", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    // Reset confirmation dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Request PIN Reset", fontWeight = FontWeight.Bold) },
            text = { Text("A message will be sent to the Admin requesting a PIN reset. Continue?") },
            confirmButton = {
                TextButton(onClick = {
                    showResetDialog = false
                    vm.sendPinResetRequest()
                }) { Text("Send Request", color = PetronasGreen, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (showResetSentDialog) {
        AlertDialog(
            onDismissRequest = { showResetSentDialog = false },
            title = { Text("Request Sent ✅", fontWeight = FontWeight.Bold) },
            text = { Text("Your PIN reset request has been sent to the Admin. Please wait for them to reset it.") },
            confirmButton = {
                TextButton(onClick = { showResetSentDialog = false }) {
                    Text("OK", color = PetronasGreen, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
private fun PinPad(
    onDigit: (String) -> Unit,
    onDelete: () -> Unit,
    onBiometric: () -> Unit,
    isLoading: Boolean
) {
    val keys = listOf(
        "1" to "", "2" to "ABC", "3" to "DEF",
        "4" to "GHI", "5" to "JKL", "6" to "MNO",
        "7" to "PQRS", "8" to "TUV", "9" to "WXYZ",
        "BIO" to "", "0" to "", "DEL" to ""
    )
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        keys.chunked(3).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 28.dp)) {
                row.forEach { (key, alpha) ->
                    PinKey(
                        key = key, subLabel = alpha,
                        onClick = {
                            when (key) {
                                "DEL" -> onDelete()
                                "BIO" -> onBiometric()
                                else  -> onDigit(key)
                            }
                        },
                        enabled = !isLoading,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun PinKey(
    key: String, subLabel: String,
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    val bgColor = when (key) {
        "DEL" -> Color(0xFFEF4444).copy(.1f)
        "BIO" -> PetronasGreen.copy(.1f)
        else  -> Color.White.copy(.065f)
    }
    val borderColor = when (key) {
        "DEL" -> Color(0xFFEF4444).copy(.18f)
        "BIO" -> PetronasGreen.copy(.22f)
        else  -> Color.White.copy(.09f)
    }
    Box(
        modifier = modifier
            .height(58.dp)
            .background(bgColor, RoundedCornerShape(16.dp))
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable(enabled = enabled) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        when (key) {
            "BIO" -> Text("☝️", fontSize = 22.sp)
            "DEL" -> Text("⌫", fontSize = 18.sp, color = Color(0xFFEF4444))
            else  -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(key, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                if (subLabel.isNotEmpty()) {
                    Text(subLabel, fontSize = 8.sp, color = Color.White.copy(.27f), letterSpacing = 1.8.sp)
                }
            }
        }
    }
}

@Composable
private fun BiometricButton(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .background(PetronasGreen.copy(.09f), RoundedCornerShape(28.dp))
            .border(1.dp, PetronasGreen.copy(.22f), RoundedCornerShape(28.dp))
            .clickable { onClick() }
            .padding(horizontal = 18.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        Text("☝️", fontSize = 16.sp)
        Text("Use Fingerprint / Face ID",
            fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PetronasGreen)
    }
}

private fun triggerBiometric(context: Context, vm: LoginViewModel, onSuccess: () -> Unit) {
    val bio = vm.getBiometricPin(context) ?: return
    val executor = ContextCompat.getMainExecutor(context)
    val activity = context as? FragmentActivity ?: return

    val prompt = BiometricPrompt(activity, executor, object : BiometricPrompt.AuthenticationCallback() {
        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
            // Directly verify stored PIN
            repeat(bio.length) { vm.appendPin(bio[it].toString()) }
        }
        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {}
        override fun onAuthenticationFailed() {}
    })

    val info = BiometricPrompt.PromptInfo.Builder()
        .setTitle("Auto Expert Centre")
        .setSubtitle("Confirm your identity")
        .setNegativeButtonText("Use PIN")
        .build()

    prompt.authenticate(info)
}
