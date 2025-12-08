package com.ayse.aroundyou.model.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
class LoginRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    suspend fun saveOrUpdateGoogleUser() = withContext(Dispatchers.IO) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Log.d("LoginRepository", "currentUser NULL — kayıt iptal edildi.")
            return@withContext
        } else {
            Log.d("LoginRepository", "currentUser bulundu: ${currentUser.email}")
        }

        val userDocRef = firestore.collection("users").document(currentUser.uid)

        try {
            //önce kullanıcının firestore kayıtlı olup olmadığını kontrol et
            val snapshot = userDocRef.get().await()

            // Kullanıcının Firestore'a kaydedilecek verileri bir map olarak hazırla
            val userMap = hashMapOf(
                "uid" to currentUser.uid,
                "name" to (currentUser.displayName ?: ""),
                "email" to (currentUser.email ?: ""),
                "photoUrl" to (currentUser.photoUrl?.toString() ?: ""),
                "provider" to "google",
                "lastLogin" to System.currentTimeMillis()
            )

            // 🔹 Eğer kullanıcı zaten Firestore’da varsa (snapshot.exists == true)
            if (snapshot.exists()) {
                // 🔹 Sadece `lastLogin` alanını güncelliyoruz (tüm veriyi yeniden yazmıyoruz)
                userDocRef.set(mapOf("lastLogin" to System.currentTimeMillis()), SetOptions.merge()).await()
                Log.d("LoginRepository", "🔄 Kullanıcı zaten kayıtlı, lastLogin güncellendi.")
            } else {
                // 🔹 Eğer kullanıcı Firestore'da yoksa (yeni kullanıcı)
                // 🔹 userMap içindeki tüm verilerle yeni bir belge oluşturuluyor
                userDocRef.set(userMap).await()
                Log.d("LoginRepository", "✅ Yeni Google kullanıcısı Firestore’a eklendi.")
            }

        } catch (e: Exception) {
            Log.d("LoginRepository", "Firestore kayıt/güncelleme hatası: ${e.message}")
            e.printStackTrace()
        }
    }

    fun checkUserLoggegIn(): Boolean {
        return auth.currentUser != null // Firebase'de kullanıcı varsa true döner
    }
}
