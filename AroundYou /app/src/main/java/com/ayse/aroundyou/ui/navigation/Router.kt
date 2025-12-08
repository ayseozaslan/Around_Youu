package com.ayse.aroundyou.ui.navigation

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ayse.aroundyou.data.preferences.PreferencesManager
import com.ayse.aroundyou.ui.onboardScreen.OnboardingScreen
import com.ayse.aroundyou.ui.screen.AIScreen
import com.ayse.aroundyou.ui.screen.FavoriteScreen
import com.ayse.aroundyou.ui.screen.FeedScreen
import com.ayse.aroundyou.ui.screen.LoginScreen
import com.ayse.aroundyou.ui.screen.MapScreen
import com.ayse.aroundyou.ui.screen.SettingsScreen
import com.ayse.aroundyou.viewmodel.ChatViewModel
import com.ayse.aroundyou.viewmodel.FavoriteViewModel
import com.ayse.aroundyou.viewmodel.GoogleLoginViewModel
import com.ayse.aroundyou.viewmodel.LocationViewModel
import com.ayse.aroundyou.viewmodel.MapViewModel
import com.ayse.aroundyou.viewmodel.ProfileViewModel

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@SuppressLint("ContextCastToActivity")
@Composable
fun Router(
    navController: NavHostController,
    profileViewModel: ProfileViewModel,
    googleLoginViewModel: GoogleLoginViewModel,
    mapViewModel: MapViewModel,
    chatViewModel: ChatViewModel,
    favoriteViewModel: FavoriteViewModel,
    locationViewModel: LocationViewModel,
    googleSignInLauncher: ActivityResultLauncher<Intent>,
    permissionLauncher: ActivityResultLauncher<Array<String>>,
    permissionLauncherOnboarding: ActivityResultLauncher<Array<String>>

) {
/**
“StateFlow’dan henüz bir değer gelmemişse başlangıçta null kullan.”
Initial kullanmasak ne olur?
StateFlow’un varsayılan değeri yoksa veya false/true ile başlarsa:
Compose startDestination’ı hemen false/true gibi değerlere göre belirler.
Bu yüzden onboarding ekranı yanlışlıkla açılır veya kullanıcıyı yanlış yönlendirir.
null kullanmak loading durumunu ayırt etmemizi sağlar.

 */
   // val isUserLoggedIn by googleLoginViewModel.isUserLoggedIn.collectAsState(initial = null)
    val context = LocalContext.current

   // val preferencesManager = remember { PreferencesManager(context) }
    val preferencesManager = PreferencesManager(context)
    val isOnboardingCompleted = preferencesManager.isOnboardingCompleted()

    val isUserLoggedIn by googleLoginViewModel.isUserLoggedIn.collectAsState()

    val startDestination = when {
        !isOnboardingCompleted -> "onboardingScreen"
        isUserLoggedIn == true -> "mapScreen"
        else -> "loginScreen"
    }


    /*
    val startDestination = when {
        !isOnboardingCompleted -> "onboardingScreen" // kullanıcı uygulamayı ilk kez açıyorsa veya onboarding’i bitirmediyse), onboardingScreen açılır.
        isUserLoggedIn!! -> "settingsScreen"
        /**
        Kullanıcı daha önce giriş yapmışsa (isUserLoggedIn == true), settingsScreen açılır.
        !! operatörü, isUserLoggedIn nullable (Boolean?) olduğu için “null olamaz” der.
         */
        else -> "loginScreen" // onboarding tamamlanmış ve kullanıcı giriş yapmamışsa), loginScreen açılır.
    }

     */
    NavHost(
        navController = navController,
        startDestination = startDestination

    ) {
        composable("onboardingScreen") {
            OnboardingScreen(
                navController = navController,
                onFinished = {},
                permissionLauncherOnboarding = permissionLauncherOnboarding
            )
        }
        composable("loginScreen") {
            LoginScreen(
                navController = navController,
                googleLoginViewModel = googleLoginViewModel,
                googleSignInLauncher = googleSignInLauncher
            )
        }

        composable("feedScreen") {
            FeedScreen(
                navController = navController,
                mapViewModel = mapViewModel,
                favoriteViewModel = favoriteViewModel,
                locationViewModel = locationViewModel,
                profileViewModel=profileViewModel,
                permissionLauncher = permissionLauncher

            )
        }

        composable("mapScreen") {
            MapScreen(
                // navController = navController,
                mapViewModel = mapViewModel,
                permissionLauncher = permissionLauncher
            )
        }

        composable("favoriteScreen") {
            FavoriteScreen(
                navController = navController,
                favoriteViewModel = favoriteViewModel
            ) }

        composable("settingsScreen") {
            SettingsScreen(
                navController,
                profileViewModel,
                googleLoginViewModel
            )
        }

        composable("aIScreen") {
            AIScreen(
                modifier = Modifier,
            navController,
            chatViewModel) }
    }
    }


/**
 * kullanıcının bulunduğu konumdan 2 km yarıçap içindeki restoran ve cafeler çekiliyor.
 */