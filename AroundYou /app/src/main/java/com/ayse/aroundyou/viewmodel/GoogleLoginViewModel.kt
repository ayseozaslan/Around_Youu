package com.ayse.aroundyou.viewmodel

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ayse.aroundyou.R
import com.ayse.aroundyou.data.preferences.PreferencesManager
import com.ayse.aroundyou.model.entities.MyUser
import com.ayse.aroundyou.model.repository.LoginRepository
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class GoogleLoginViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val repository: LoginRepository
) : ViewModel(){

 private val _isLoading = mutableStateOf(false)
    val isLoading : State<Boolean> = _isLoading

    private val _loginSuccess = mutableStateOf(false)
    val loginSuccess : State<Boolean> = _loginSuccess

    private val _errorMessage= mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    /*
    private val _isUserLoggedIn = MutableStateFlow<Boolean?>(null)
    val isUserLoggedIn: StateFlow<Boolean?> = _isUserLoggedIn

     */
    private val _isUserLoggedIn = MutableStateFlow(false)
    val isUserLoggedIn = _isUserLoggedIn

    private val _currentUser = MutableStateFlow<MyUser?>(null)
    val currentUser: StateFlow<MyUser?> = _currentUser


    init {
     //   auth.signOut()

        // Başlangıçta FirebaseAuth üzerinden kontrol
        //Böylece uygulama açıldığında StateFlow artık boş değil,
        // Compose tarafından gözlemlenebilir hale geldi.
            val firebaseUser = auth.currentUser
            if (firebaseUser != null) {
                _currentUser.value = MyUser(
                    uid = firebaseUser.uid,
                    name = firebaseUser.displayName ?: "",
                    email = firebaseUser.email ?: ""
                )
                _isUserLoggedIn.value = true
            } else {
                _isUserLoggedIn.value = false
            }
    }

    fun handleGoogleSignInResult(data: Intent?, context: Context) {
        try {
            // 🔹 Google oturum açma sonucunu 'Intent' içinden alıyoruz.
            //    Bu intent, Google Sign-In ekranından dönen verileri içerir.
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            // 🔹 Google hesabını task içinden çekiyoruz.
            //    Eğer kullanıcı iptal etmediyse veya hata yoksa, hesap bilgilerini döndürür.
            val account = task.getResult(ApiException::class.java)

            // 🔹 Firebase Authentication için Google kimlik bilgisini (credential) oluşturuyoruz.
            //    Bu credential sayesinde kullanıcıyı Firebase'e tanıtırız.
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)

            // 🔹 Ekranda yükleniyor durumu göstermek için loading state'i true yapıyoruz.
            _isLoading.value = true

            // 🔹 Firebase Authentication’a Google kimlik bilgisiyle giriş yapıyoruz.
            auth.signInWithCredential(credential)
                .addOnSuccessListener {
                    _isLoading.value = false
                    val firebaseUser = auth.currentUser

                    if (firebaseUser != null) {
                        // 🔹 Firestore’a kaydet
                        saveGoogleUser()

                        // 🔹 SharedPreferences veya local session
                        googleLoginSuccess(context, firebaseUser)

                        // 🔹 StateFlow güncelle (Compose bu state’i gözlemler)
                        _currentUser.value = MyUser(
                            uid = firebaseUser.uid,
                            name = firebaseUser.displayName ?: "",
                            email = firebaseUser.email ?: ""
                        )
                    }

                    // 🔹 (3) Login işleminin başarılı olduğunu View tarafına bildiriyoruz.
                    //     Bu değer, LoginScreen içinde gözlemlenip navigasyon yapılmasını sağlar.
                    _loginSuccess.value = true
                }
                // ❌ Eğer giriş başarısız olursa burası çalışır:
                .addOnFailureListener { e ->
                    // 🔹 Hata durumunda loading state’i kapatılır.
                    _isLoading.value = false

                    // 🔹 Hata mesajı LiveData’ya aktarılır.
                    //     Böylece ekranda Toast veya uyarı mesajı olarak gösterilebilir.
                    _errorMessage.value = e.message
                }

        } catch (e: ApiException) {
            // 🔹 Google oturum açma sürecinde (örneğin kullanıcı iptal ettiğinde)
            //     oluşabilecek hataları yakalıyoruz.
            _errorMessage.value = "Google giriş hatası: ${e.statusCode}"
        }
    }

    fun saveGoogleUser(){
        viewModelScope.launch {
            Log.d("LoginViewModel", "saveGoogleUser() called")
            repository.saveOrUpdateGoogleUser()
        }

    }

    /**
    Google hesabıyla giriş yapan kullanıcının bilgilerini SharedPreferences’a kaydeder.
    Yani artık uygulama yeniden açıldığında bile bu bilgiler saklıdır.
     */
    fun googleLoginSuccess(context:Context, user:FirebaseUser){
         val preferencesManager = PreferencesManager(context)
        preferencesManager.saveUser(
            name =  user.displayName,
            email = user.email,
            photoUrl = user.photoUrl.toString(),
            isLoggedIn = true
        )
    }

    fun isUserLoggedIn() :Boolean{
        return repository.checkUserLoggegIn()

    }
    fun signOut(context: Context) {
        // Firebase çıkış
        auth.signOut()

        // Google çıkış
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleClient = GoogleSignIn.getClient(context, gso)
        googleClient.signOut()
        // State’i sıfırla
        _isUserLoggedIn.value = false //kullanıcının giirş yapıp yapmadığını tutar.false çıkış yaptı veya giirş yapmadı
        _loginSuccess.value = false // son login girişi sıfırlanır.Çünkü kullanıcı tekrar giirş yapmak isterse eski değer kayıtlı kayıp yanlış yönlendirme yapılmasını engeller
    }
}