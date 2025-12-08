package com.ayse.aroundyou.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.ayse.aroundyou.data.preferences.FavoriteManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject


@HiltViewModel
class ProfileViewModel  @Inject constructor(
    private  var favoriteManager : FavoriteManager,
     @ApplicationContext private var  context : Context

) : ViewModel(){
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _userName= MutableStateFlow("")
    val userName :StateFlow<String> = _userName

    private val _email = MutableStateFlow("")
    val email :StateFlow<String> = _email

    // Tema durumu (true = dark, false = light)
    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    // 🔹 Dil ile ilgili StateFlow.UI tarafında dil değişikliğini gözlemleme
    private val _currentLocale = MutableStateFlow(favoriteManager.loadLocale() ?: "tr") // varsayılan Türkçe
    val currentLocale: StateFlow<String> = _currentLocale.asStateFlow()

    init {
        loadUserData()
        //SharedPreferences'tan tema bilgisi oku
        _isDarkTheme.value = favoriteManager.loadTheme()
    }

    // Kullanıcı seçtiği dili al ve kaydet
    fun changeLanguage(locale: String) {
        _currentLocale.value = locale              // StateFlow'u güncelle (UI anlık değişir)
        favoriteManager.saveLocale(locale) // seçilen dili kaydet
    }



    fun toggleTheme() {
        // 🔹 Mevcut temayı tersine çeviriyoruz: açıksa koyu, koyuysa açık yap
        val newTheme = !_isDarkTheme.value
        // 🔹 StateFlow'daki tema değerini güncelliyoruz
        _isDarkTheme.value = newTheme
        // 🔹 Yeni tema değerini SharedPreferences'a kaydediyoruz, böylece uygulama tekrar açıldığında aynı tema kalır
        favoriteManager.saveTheme(newTheme)
        // 🔹 Log ile hangi temanın aktif olduğunu görebiliyoruz (debug için)
        Log.d("ProfileViewModel", "toggleTheme: $newTheme")
    }




    private fun loadUserData() {
        val userId = auth.currentUser?.uid ?: return

        firestore.collection("users").document(userId)
            .addSnapshotListener{ snapshot, _ ->
                if(snapshot!=null && snapshot.exists()){
                    _userName.value = snapshot.getString("name") ?: ""
                    _email.value  =snapshot.getString("email") ?: ""
                }

            }
    }

}