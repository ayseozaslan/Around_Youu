package com.ayse.aroundyou.utils


import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import java.util.Locale


@Composable
fun ProvideLocale(
    locale: String,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    // Activity bulunamazsa fallback olarak context'i kullan
    val updatedContext = remember(locale) {
        val activity = context.findActivityOrNull()
        if (activity != null) {
            activity.updateLocale(locale)
        } else {
            context // Activity yoksa orijinal context ile devam et
        }
    }

    CompositionLocalProvider(LocalContext provides updatedContext) {
        content()
    }
}

// Null-safe extension
fun Context.findActivityOrNull(): ComponentActivity? {
    return when (this) {
        is ComponentActivity -> this
        is ContextWrapper -> baseContext.findActivityOrNull()
        else -> null
    }
}

// Locale güncelleme fonksiyonu
fun Context.updateLocale(localeCode: String): Context {
    val locale = Locale(localeCode)
    Locale.setDefault(locale)
    val config = resources.configuration
    config.setLocale(locale)
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        createConfigurationContext(config)
    } else {
        resources.updateConfiguration(config, resources.displayMetrics)
        this
    }
}
