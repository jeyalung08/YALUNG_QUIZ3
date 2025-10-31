package ph.edu.auf.emman.yalung.yalung_quiz3.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val EarthToneLightColors = lightColorScheme(
    primary = Color(0xFF8E735B),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD7C4B0),
    onPrimaryContainer = Color(0xFF3E2F20),

    secondary = Color(0xFF5A6E58),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFC9D4C0),
    onSecondaryContainer = Color(0xFF2C3B2A),

    background = Color(0xFFF5F0E6),
    onBackground = Color(0xFF3E3E3E),

    surface = Color(0xFFEDE6DA),
    onSurface = Color(0xFF3E3E3E),
    surfaceVariant = Color(0xFFD6CFC2),
    onSurfaceVariant = Color(0xFF5C5044),

    error = Color(0xFFB00020),
    onError = Color.White
)

@Composable
fun EarthToneTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = EarthToneLightColors,
        typography = Typography(),
        content = content
    )
}