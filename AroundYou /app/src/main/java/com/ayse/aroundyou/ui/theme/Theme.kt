package com.ayse.aroundyou.ui.theme

import android.os.Build
import android.util.Log
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun AroundYouTheme(
    darkTheme: Boolean = isSystemInDarkTheme(), // 🔥 Varsayılan olarak sistem temasını alır
    dynamicColor: Boolean = true,               // 🔥 Android 12+ için dinamik renk desteği
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            Log.d("AroundYouTheme", "Dynamic renk kullanılıyor, darkTheme=$darkTheme")
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> {
            Log.d("AroundYouTheme", "DarkColorScheme seçildi")
            DarkColorScheme
        }
        else -> {
            Log.d("AroundYouTheme", "LightColorScheme seçildi")
            LightColorScheme
        }
    }

    // 🔥 Seçilen renk paleti ve tipografi ile MaterialTheme uygulanır
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
