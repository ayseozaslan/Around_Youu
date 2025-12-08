package com.ayse.aroundyou.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ayse.aroundyou.R
import com.ayse.aroundyou.ui.theme.lightBlue
import com.ayse.aroundyou.utils.findActivity
import com.ayse.aroundyou.viewmodel.GoogleLoginViewModel
import com.ayse.aroundyou.viewmodel.ProfileViewModel
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: ProfileViewModel,
    googleLoginViewModel: GoogleLoginViewModel
) {
   

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding() // Status bar boşluğu
            .padding(horizontal = 16.dp, vertical = 8.dp) // Genel padding
    ) {
        // 🔹 AppBar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Geri icon
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Go to Map",
                modifier = Modifier
                    .size(24.dp)
                    .clickable {
                        navController.navigate("mapScreen") {
                            launchSingleTop = true
                        }
                    }
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Başlık
            Text(
                text = stringResource(id = R.string.settings_title),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f), // Ortalamak için weight
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.width(36.dp)) // Geri ikon kadar sağda boşluk
        }

        Spacer(modifier = Modifier.height(16.dp))

        // İçerik
        ProfileCard(googleLoginViewModel)
        Spacer(modifier = Modifier.height(16.dp))
        SettingsCard(viewModel)
        Spacer(modifier = Modifier.height(16.dp))
        AccountCard(
            navController = navController,
            googleLoginViewModel = googleLoginViewModel
        )
    }
}


// 🟢 Kullanıcı arayüzü: Tema ve Dil Seçimi
@Composable
fun SettingsCard(viewModel: ProfileViewModel) {

    val isDark by viewModel.isDarkTheme.collectAsState()
    val currentLocale by viewModel.currentLocale.collectAsState()
    var expanded by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // ✔ Interstitial reklam için state
    var mInterstitialAd by remember { mutableStateOf<InterstitialAd?>(null) }

    // ✔ Reklamı yükle
    LaunchedEffect(true) {
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            context,
            "ca-app-pub-3940256099942544/1033173712", // TEST ID
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    mInterstitialAd = ad
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    mInterstitialAd = null
                }
            }
        )
    }

    val languages = listOf(
        "tr" to stringResource(id = R.string.lang_turkish),
        "en" to stringResource(id = R.string.lang_english),
        "es" to stringResource(id = R.string.lang_spanish)
    )

    val selectedLanguageName = languages.find { it.first == currentLocale }?.second
        ?: stringResource(id = R.string.select_language)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .border(1.dp, lightBlue, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (isDark) stringResource(id = R.string.dark_theme)
                    else stringResource(id = R.string.light_theme),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))
                Switch(
                    checked = isDark,
                    onCheckedChange = { viewModel.toggleTheme() }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Dil seçimi
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(id = R.string.language_label),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.weight(1f))

                Box {
                    Button(
                        onClick = { expanded = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Text(selectedLanguageName)
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        languages.forEach { (locale, name) ->
                            DropdownMenuItem(
                                text = { Text(name) },
                                onClick = {
                                    viewModel.changeLanguage(locale)
                                    expanded = false

                                    // ✔ Kullanıcı dil değiştirince reklam göster
                                    val activity = context.findActivity()
                                    if (activity != null) {
                                        mInterstitialAd?.show(activity)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}


// 🟢 Profil Kartı
@Composable
fun ProfileCard(viewModel: GoogleLoginViewModel ) {
    /*
    val name by viewModel.userName.collectAsState()
    val email by viewModel.email.collectAsState()

     */

    val currentUser by viewModel.currentUser.collectAsState()


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .border(1.dp, lightBlue, RoundedCornerShape(12.dp)), // 🔹 İnce border
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(id = R.string.profile_info),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Kullanıcı Adı
            Text(
                text = stringResource(id = R.string.username_label),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = currentUser?.name ?: "", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Email
            Text(
                text = stringResource(id = R.string.email_label),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Email, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = currentUser?.email ?: "", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}

@Composable
fun AccountCard(
    navController: NavController,
    googleLoginViewModel: GoogleLoginViewModel
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .border(1.dp, lightBlue, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)){
             Text(
                 text = stringResource(id = R.string.account_title),
                 fontSize = 12.sp,
                 fontWeight = FontWeight.Bold,
                 color = MaterialTheme.colorScheme.onSurface
             )
         }

        Row(
            modifier = Modifier
                .fillMaxWidth(),
                //.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val context = LocalContext.current
            IconButton(onClick = {
                googleLoginViewModel.signOut(context) // 🔹 Kullanıcıyı çıkış yap
                navController.navigate("loginScreen") {
                    popUpTo(navController.graph.id) { inclusive = true }
                    launchSingleTop = true
                }
//(navController.graph.id)  navigation dahi tüm sayfaları arkaplandna temizle. inclusive = true } navgraph dosyası dahil temizle
            }) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = "Çıkış Yap",
                    tint = MaterialTheme.colorScheme.error
                )
            }
            Text(
                text = stringResource(id=R.string.account_logout),
                color = MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }
    }
}

