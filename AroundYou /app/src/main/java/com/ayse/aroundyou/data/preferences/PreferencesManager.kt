package com.ayse.aroundyou.data.preferences

import android.content.Context
import android.content.SharedPreferences


class PreferencesManager(context: Context) {

    private val prefs:SharedPreferences=
        context.getSharedPreferences("user_permission", Context.MODE_PRIVATE)

    companion object{
        private const val KEY_NAME = "name"
        private const val KEY_EMAIL = "email"
        private const val KEY_PHOTO = "photoUrl"
        private const val KEY_IS_LOGGED_IN = "isLoggedIn"

    }
    //Kullanıcının onboarding ekranını bitirdiğini kaydeder bittiği için true
    fun setOnboardingCompleted() {
        prefs.edit().putBoolean("onboardingCompleted", true).apply()
    }

    //Kullanıcının onboarding’i bitirip bitirmediğini kontrol eder .ikk deger false
    fun isOnboardingCompleted(): Boolean {
        return prefs.getBoolean("onboardingCompleted", false)
    }

    fun saveUser(name: String? , email:String?, photoUrl:String?,isLoggedIn:Boolean){
        prefs.edit().apply{
            putString(KEY_NAME, name)
            putString(KEY_EMAIL, email)
            putString(KEY_PHOTO, photoUrl)
            putBoolean(KEY_IS_LOGGED_IN, isLoggedIn)
        }.apply()
    }

    fun getUser(): UserData? {
        val name = prefs.getString(KEY_NAME, null)
        val email = prefs.getString(KEY_EMAIL, null)
        val photo = prefs.getString(KEY_PHOTO, null)
        val isLoggedIn = prefs.getBoolean(KEY_IS_LOGGED_IN, false)
        return if (isLoggedIn && email != null) {
            UserData(name, email, photo)
        } else null
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}

data class UserData(
    val name: String?,
    val email: String,
    val photoUrl: String?
)
