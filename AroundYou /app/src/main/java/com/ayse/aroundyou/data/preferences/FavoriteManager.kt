package com.ayse.aroundyou.data.preferences

import android.content.SharedPreferences
import com.ayse.aroundyou.model.response.PlaceItem
import com.google.gson.Gson
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoriteManager @Inject constructor(
private val sharedPreferences: SharedPreferences
) {

    private val FAVORITES_KEY = "favorites"
    private val gson = Gson()
    //seçili switch modu kaydı
    private val KEY_IS_DARK = "is_dark"
    private val KEY_LOCALE = "locale"


    // Dil kaydetme
    // SharedPreferences içine, kullanıcı hangi dili seçtiyse onu saklar
    fun setLanguage(lang: String) {
        sharedPreferences.edit().putString(KEY_LOCALE, lang).apply()
    }

    // 🔹 Daha önce kaydedilmiş dili okumak için kullanılır
    fun getLanguage(): String {
        return sharedPreferences.getString(KEY_LOCALE, "tr") ?: "tr" // default "tr"
    }

    // Locale (dil) bilgisini SharedPreferences içine kaydeder
    fun saveLocale(locale: String) {
        sharedPreferences.edit().putString(KEY_LOCALE, locale).apply()
    }

    // 🔹 Daha önce kaydedilmiş locale değerini yükler
    fun loadLocale(): String? {
        return sharedPreferences.getString(KEY_LOCALE, "tr") // varsayılan Türkçe
    }


    // Seçilen tema kaydı
    fun saveTheme(isDark: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_IS_DARK, isDark).apply()
    }


    //seçilen tema kaydını yükleyecek fonksiyon
    fun loadTheme(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_DARK, false)
    }

    // Tüm favorileri al
    fun getFavorites(): List<PlaceItem> {
        val favoritesJson = sharedPreferences.getStringSet(FAVORITES_KEY, emptySet()) ?: emptySet()
        return favoritesJson.mapNotNull { json ->
            try {
                gson.fromJson(json, PlaceItem::class.java)
            } catch (e: Exception) {
                null
            }
        }
    }

    // Favori ekle
    fun addFavorite(place: PlaceItem) {
        val favoritesJson = sharedPreferences.getStringSet(FAVORITES_KEY, emptySet())?.toMutableSet() ?: mutableSetOf()
        val placeJson = gson.toJson(place)
        favoritesJson.add(placeJson)
        sharedPreferences.edit().putStringSet(FAVORITES_KEY, favoritesJson).apply()
    }

    // Favoriden çıkar
    fun removeFavorite(place: PlaceItem) {
        val favoritesJson = sharedPreferences.getStringSet(FAVORITES_KEY, emptySet())?.toMutableSet() ?: mutableSetOf()
        val placeJson = gson.toJson(place)
        favoritesJson.remove(placeJson)
        sharedPreferences.edit().putStringSet(FAVORITES_KEY, favoritesJson).apply()
    }

    // Favori kontrol
    fun isFavorite(place: PlaceItem): Boolean {
        val favorites = getFavorites()
        return favorites.any { it.placeId == place.placeId }
    }
}
