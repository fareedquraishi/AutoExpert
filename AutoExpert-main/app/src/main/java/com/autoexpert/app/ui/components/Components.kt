package com.autoexpert.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import com.autoexpert.app.ui.theme.*

// ── Gradient backgrounds ──────────────────────────────────────────────────
val DarkGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF0A1628), Color(0xFF0C1E35), Color(0xFF081A0E)),
    start = androidx.compose.ui.geometry.Offset(0f, 0f),
    end   = androidx.compose.ui.geometry.Offset(400f, 1200f)
)
val HomeHeaderGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF0A1628), Color(0xFF0D2A1A)),
    start = androidx.compose.ui.geometry.Offset(0f, 0f),
    end   = androidx.compose.ui.geometry.Offset(800f, 400f)
)
val GreenGradient = Brush.linearGradient(
    colors = listOf(PetronasGreen, PetronasGreenDark)
)

// ── StatusBadge ───────────────────────────────────────────────────────────
@Composable
fun StatusBadge(text: String, type: BadgeType) {
    val (bg, fg) = when (type) {
        BadgeType.GREEN  -> Color(0xFFDCFCE7) to Color(0xFF166534)
        BadgeType.BLUE   -> Color(0xFFDBEAFE) to Color(0xFF1D4ED8)
        BadgeType.AMBER  -> Color(0xFFFEF9C3) to Color(0xFF854D0E)
        BadgeType.PURPLE -> Color(0xFFF3E8FF) to Color(0xFF6B21A8)
        BadgeType.GRAY   -> Color(0xFFF3F4F6) to Color(0xFF6B7280)
        BadgeType.RED    -> Color(0xFFFEE2E2) to Color(0xFF991B1B)
    }
    Surface(color = bg, shape = RoundedCornerShape(20.dp)) {
        Text(
            text = text, color = fg,
            fontSize = 9.sp, fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

enum class BadgeType { GREEN, BLUE, AMBER, PURPLE, GRAY, RED }

// ── PrimaryButton ─────────────────────────────────────────────────────────
@Composable
fun PrimaryButton(
    text: String, onClick: () -> Unit,
    modifier: Modifier = Modifier, enabled: Boolean = true,
    icon: ImageVector? = null
) {
    Button(
        onClick = onClick, enabled = enabled,
        modifier = modifier.height(50.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        contentPadding = PaddingValues(0.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
                .background(if (enabled) GreenGradient else Brush.linearGradient(listOf(Color(0xFF9CA3AF), Color(0xFF6B7280))))
                .clip(RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                icon?.let { Icon(it, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp)) }
                Text(text, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}

// ── OutlineButton ─────────────────────────────────────────────────────────
@Composable
fun OutlineButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(50.dp),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.5.dp, BorderColor),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary)
    ) {
        Text(text, fontWeight = FontWeight.Bold, fontSize = 13.sp)
    }
}

// ── AppTopBar ─────────────────────────────────────────────────────────────
@Composable
fun AppTopBar(
    title: String, subtitle: String? = null,
    onBack: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Surface(color = Color.White, shadowElevation = 1.dp) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            onBack?.let {
                IconButton(onClick = it, modifier = Modifier.size(36.dp)) {
                    Box(
                        Modifier.fillMaxSize()
                            .background(BackgroundGray, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back",
                            tint = TextPrimary, modifier = Modifier.size(18.dp))
                    }
                }
                Spacer(Modifier.width(10.dp))
            }
            Column(Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = TextPrimary,
                    maxLines = 1, overflow = TextOverflow.Ellipsis)
                subtitle?.let {
                    Text(it, fontSize = 10.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                }
            }
            Row(content = actions)
        }
    }
}

// ── SectionHeader ─────────────────────────────────────────────────────────
@Composable
fun SectionHeader(title: String, icon: String = "", action: String? = null, onAction: (() -> Unit)? = null) {
    Row(
        Modifier.fillMaxWidth().padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            if (icon.isNotEmpty()) Text(icon, fontSize = 13.sp)
            Text(
                text = title,
                fontSize = 11.sp, fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 1.2.sp
            )
        }
        action?.let {
            Text(it, fontSize = 11.sp, fontWeight = FontWeight.Bold,
                color = PetronasGreen,
                modifier = Modifier.clickable { onAction?.invoke() })
        }
    }
}

// ── KpiCard ───────────────────────────────────────────────────────────────
@Composable
fun KpiCard(
    label: String, value: String, sub: String,
    progress: Float? = null, modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(Color.White.copy(alpha = 0.085f), RoundedCornerShape(14.dp))
            .border(1.dp, Color.White.copy(alpha = 0.11f), RoundedCornerShape(14.dp))
            .padding(10.dp, 10.dp)
    ) {
        Column {
            Text(label, fontSize = 9.sp, fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.42f), letterSpacing = .8.sp)
            Text(value, fontSize = 19.sp, fontWeight = FontWeight.ExtraBold,
                color = Color.White, modifier = Modifier.padding(vertical = 2.dp))
            Text(sub, fontSize = 9.sp, fontWeight = FontWeight.SemiBold,
                color = PetronasGreen.copy(alpha = 0.85f))
            progress?.let {
                Spacer(Modifier.height(5.dp))
                Box(
                    Modifier.fillMaxWidth().height(3.dp)
                        .background(Color.White.copy(.1f), RoundedCornerShape(2.dp))
                ) {
                    Box(
                        Modifier.fillMaxWidth(it.coerceIn(0f, 1f)).fillMaxHeight()
                            .background(GreenGradient, RoundedCornerShape(2.dp))
                    )
                }
            }
        }
    }
}

// ── Toggle Row ────────────────────────────────────────────────────────────
@Composable
fun ToggleRow(title: String, subtitle: String, checked: Boolean, onChecked: (Boolean) -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Text(subtitle, fontSize = 10.sp, color = TextSecondary, modifier = Modifier.padding(top = 1.dp))
        }
        Switch(
            checked = checked, onCheckedChange = onChecked,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = PetronasGreen,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = BorderColor
            )
        )
    }
}

// ── LoadingOverlay ────────────────────────────────────────────────────────
@Composable
fun LoadingOverlay(message: String = "Loading...") {
    Box(Modifier.fillMaxSize().background(Color.Black.copy(.45f)), contentAlignment = Alignment.Center) {
        Surface(shape = RoundedCornerShape(20.dp), color = Color.White) {
            Column(
                Modifier.padding(32.dp, 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                CircularProgressIndicator(color = PetronasGreen, modifier = Modifier.size(40.dp))
                Text(message, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            }
        }
    }
}
