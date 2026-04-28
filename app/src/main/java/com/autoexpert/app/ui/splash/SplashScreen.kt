package com.autoexpert.app.ui.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.autoexpert.app.R
import com.autoexpert.app.ui.components.DarkGradient
import com.autoexpert.app.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onTimeout: () -> Unit) {

    var visible by remember { mutableStateOf(false) }

    val logoScale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.5f,
        animationSpec = spring(dampingRatio = .5f, stiffness = 200f), label = "logo"
    )
    val logoAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(800), label = "logoAlpha"
    )
    val textAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(700, delayMillis = 400), label = "textAlpha"
    )
    val poweredAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(600, delayMillis = 800), label = "poweredAlpha"
    )
    val euroAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(600, delayMillis = 600), label = "euroAlpha"
    )

    // Pulsing glow ring
    val pulse = rememberInfiniteTransition(label = "pulse")
    val glowAlpha by pulse.animateFloat(
        initialValue = 0.10f, targetValue = 0.25f,
        animationSpec = infiniteRepeatable(tween(2000, easing = EaseInOut), RepeatMode.Reverse),
        label = "glow"
    )
    val ringScale by pulse.animateFloat(
        initialValue = 0.95f, targetValue = 1.05f,
        animationSpec = infiniteRepeatable(tween(2000, easing = EaseInOut), RepeatMode.Reverse),
        label = "ring"
    )

    LaunchedEffect(Unit) {
        visible = true
        delay(3000)
        onTimeout()
    }

    Box(
        modifier = Modifier.fillMaxSize().background(DarkGradient),
        contentAlignment = Alignment.Center
    ) {
        // Background glow blob
        Box(
            Modifier
                .size(400.dp)
                .offset(y = (-40).dp)
                .scale(ringScale)
                .background(
                    Brush.radialGradient(
                        listOf(PetronasGreen.copy(glowAlpha), Color.Transparent)
                    ),
                    RoundedCornerShape(50)
                )
        )

        // Decorative rings
        listOf(360.dp, 270.dp, 190.dp).forEachIndexed { i, size ->
            Box(
                Modifier
                    .size(size)
                    .scale(if (i == 0) ringScale else 1f)
                    .border(1.dp, PetronasGreen.copy(alpha = 0.06f + i * 0.03f), RoundedCornerShape(50))
                    .alpha(logoAlpha)
            )
        }

        // Euro logo — top left, larger and more visible
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(16.dp)
                .size(80.dp)
                .background(Color(0xFF0A1428), RoundedCornerShape(14.dp))
                .border(1.dp, PetronasGreen.copy(.5f), RoundedCornerShape(14.dp))
                .alpha(euroAlpha),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.euro_logo),
                contentDescription = "Euro",
                modifier = Modifier.size(64.dp)
            )
        }

        // Main content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(0.dp),
            modifier = Modifier
                .alpha(logoAlpha)
                .scale(logoScale)
        ) {
            // Petronas logo box — large prominent
            Box(
                modifier = Modifier
                    .size(170.dp)
                    .background(Color.White.copy(.05f), RoundedCornerShape(38.dp))
                    .border(
                        1.dp,
                        Brush.linearGradient(
                            listOf(PetronasGreen.copy(.4f), PetronasGreen.copy(.1f))
                        ),
                        RoundedCornerShape(38.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.petronas_logo),
                    contentDescription = "Petronas",
                    modifier = Modifier.size(160.dp, 160.dp)
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                "PETRONAS",
                fontSize = 28.sp, fontWeight = FontWeight.Black,
                color = Color.White, letterSpacing = 5.sp,
                modifier = Modifier.alpha(textAlpha)
            )
            Text(
                "Auto Expert Centre",
                fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
                color = Color.White.copy(.5f), letterSpacing = 2.5.sp,
                modifier = Modifier.alpha(textAlpha).padding(top = 5.dp)
            )

            Spacer(Modifier.height(18.dp))

            // Divider line with glow
            Box(
                Modifier
                    .width(48.dp)
                    .height(2.dp)
                    .alpha(textAlpha)
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color.Transparent, PetronasGreen, Color.Transparent)
                        ),
                        RoundedCornerShape(1.dp)
                    )
            )

            Spacer(Modifier.height(14.dp))

            Text(
                "Brand Ambassador",
                fontSize = 11.sp, fontWeight = FontWeight.Bold,
                color = Color.White.copy(.38f), letterSpacing = 3.sp,
                modifier = Modifier.alpha(textAlpha)
            )
        }

        // Powered by — bottom
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 32.dp)
                .alpha(poweredAlpha),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                "POWERED BY", fontSize = 8.sp,
                color = Color.White.copy(.22f), letterSpacing = 1.5.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                "Fintectual Pvt Ltd", fontSize = 11.sp,
                color = PetronasGreen.copy(.65f), letterSpacing = .5.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
