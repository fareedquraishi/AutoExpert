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
import androidx.compose.ui.unit.*
import com.autoexpert.app.R
import com.autoexpert.app.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onTimeout: () -> Unit) {

    var visible by remember { mutableStateOf(false) }

    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(900), label = "alpha"
    )
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.85f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 180f), label = "scale"
    )
    val textAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(700, delayMillis = 500), label = "text"
    )
    val poweredAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(600, delayMillis = 900), label = "powered"
    )

    LaunchedEffect(Unit) {
        visible = true
        delay(3000)
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A1428)),
        contentAlignment = Alignment.Center
    ) {
        // Main content - centered
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .alpha(alpha)
                .scale(scale)
        ) {
            // Euro Oil Logo - large and prominent
            Image(
                painter = painterResource(R.drawable.euro_logo_white),
                contentDescription = "Euro Oil",
                modifier = Modifier.size(240.dp)
            )

            Spacer(Modifier.height(24.dp))

            // Divider
            Box(
                Modifier
                    .width(60.dp)
                    .height(2.dp)
                    .alpha(textAlpha)
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color.Transparent, PetronasGreen, Color.Transparent)
                        ),
                        RoundedCornerShape(1.dp)
                    )
            )

            Spacer(Modifier.height(16.dp))

            Text(
                "Brand Ambassador App",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White.copy(0.6f),
                letterSpacing = 2.sp,
                modifier = Modifier.alpha(textAlpha)
            )
        }

        // Powered by - bottom
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
                "POWERED BY",
                fontSize = 8.sp,
                color = Color.White.copy(0.3f),
                letterSpacing = 2.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                "Fintectual Pvt Ltd",
                fontSize = 12.sp,
                color = PetronasGreen.copy(0.8f),
                letterSpacing = 0.5.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
