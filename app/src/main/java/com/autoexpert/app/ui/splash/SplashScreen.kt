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

    // Entry animations
    var visible by remember { mutableStateOf(false) }

    val logoScale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.6f,
        animationSpec = spring(dampingRatio = .55f, stiffness = 250f), label = "logo"
    )
    val logoAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(700), label = "logoAlpha"
    )
    val textAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(700, delayMillis = 350), label = "textAlpha"
    )
    val poweredAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(600, delayMillis = 700), label = "poweredAlpha"
    )

    // Pulsing glow
    val pulse = rememberInfiniteTransition(label = "pulse")
    val glowAlpha by pulse.animateFloat(
        initialValue = 0.12f, targetValue = 0.22f,
        animationSpec = infiniteRepeatable(tween(1800, easing = EaseInOut), RepeatMode.Reverse),
        label = "glow"
    )

    LaunchedEffect(Unit) {
        visible = true
        delay(2800)
        onTimeout()
    }

    Box(
        modifier = Modifier.fillMaxSize().background(DarkGradient),
        contentAlignment = Alignment.Center
    ) {
        // Glow blob
        Box(
            Modifier.size(360.dp).offset(y = (-60).dp)
                .background(
                    Brush.radialGradient(listOf(PetronasGreen.copy(glowAlpha), Color.Transparent)),
                    CircleShape
                )
        )
        // Decorative rings
        listOf(340.dp, 250.dp, 170.dp).forEachIndexed { i, size ->
            Box(
                Modifier.size(size)
                    .border(1.dp, PetronasGreen.copy(alpha = 0.07f + i * 0.02f), CircleShape)
                    .alpha(logoAlpha)
            )
        }

        // Euro logo — top left
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(20.dp)
                .size(38.dp)
                .background(Color.White.copy(.06f), RoundedCornerShape(11.dp))
                .border(1.dp, Color.White.copy(.1f), RoundedCornerShape(11.dp))
                .alpha(logoAlpha),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.euro_logo),
                contentDescription = "Euro",
                modifier = Modifier.size(26.dp)
            )
        }

        // Main content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(0.dp),
            modifier = Modifier.alpha(logoAlpha)
        ) {
            // Large Petronas logo box
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .scale(logoScale)
                    .background(Color.White.copy(.04f), RoundedCornerShape(36.dp))
                    .border(
                        1.dp,
                        Brush.linearGradient(listOf(PetronasGreen.copy(.3f), PetronasGreen.copy(.1f))),
                        RoundedCornerShape(36.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.petronas_logo),
                    contentDescription = "Petronas",
                    modifier = Modifier.size(128.dp, 104.dp)
                )
            }

            Spacer(Modifier.height(20.dp))

            // PETRONAS large
            Text(
                "PETRONAS",
                fontSize = 26.sp, fontWeight = FontWeight.Black,
                color = Color.White, letterSpacing = 4.sp,
                modifier = Modifier.alpha(textAlpha)
            )
            // Auto Expert Centre tight below
            Text(
                "Auto Expert Centre",
                fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
                color = Color.White.copy(.48f), letterSpacing = 2.5.sp,
                modifier = Modifier.alpha(textAlpha).padding(top = 4.dp)
            )

            // Divider
            Spacer(Modifier.height(16.dp))
            Box(
                Modifier.width(36.dp).height(2.dp).alpha(textAlpha)
                    .background(
                        Brush.horizontalGradient(listOf(Color.Transparent, PetronasGreen, Color.Transparent)),
                        RoundedCornerShape(1.dp)
                    )
            )
            Spacer(Modifier.height(12.dp))

            // Brand Ambassador
            Text(
                "Brand Ambassador",
                fontSize = 11.sp, fontWeight = FontWeight.Bold,
                color = Color.White.copy(.35f), letterSpacing = 3.sp,
                modifier = Modifier.alpha(textAlpha)
            )
        }

        // Powered by — bottom
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 28.dp)
                .alpha(poweredAlpha),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                "POWERED BY", fontSize = 8.sp,
                color = Color.White.copy(.2f), letterSpacing = 1.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                "Fintectual Pvt Ltd", fontSize = 10.sp,
                color = PetronasGreen.copy(.55f), letterSpacing = .3.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
