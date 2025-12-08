package com.ayse.aroundyou

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ayse.aroundyou.model.repository.LoginRepository
import com.ayse.aroundyou.ui.components.BottomNavItem
import com.ayse.aroundyou.ui.navigation.Router
import com.ayse.aroundyou.ui.theme.AroundYouTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import androidx.compose.runtime.collectAsState
import com.ayse.aroundyou.model.response.MyLocation
import com.ayse.aroundyou.utils.ProvideLocale
import com.ayse.aroundyou.viewmodel.FavoriteViewModel
import com.ayse.aroundyou.viewmodel.GoogleLoginViewModel
import com.ayse.aroundyou.viewmodel.MapViewModel
import com.ayse.aroundyou.viewmodel.ProfileViewModel
import com.google.android.gms.location.LocationServices
import android.content.Intent
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ayse.aroundyou.ui.components.BottomBarCutoutShape
import com.ayse.aroundyou.ui.screen.AIScreen
import com.ayse.aroundyou.viewmodel.ChatViewModel
import com.ayse.aroundyou.viewmodel.LocationViewModel


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var auth: LoginRepository

    private val googleLoginViewModel: GoogleLoginViewModel by viewModels()
    private val mapViewModel: MapViewModel by viewModels()
    private val favoriteViewModel: FavoriteViewModel by viewModels()
    private val locationViewModel: LocationViewModel by viewModels()
    private val chatViewModel: ChatViewModel by viewModels()
    // ⚡ Tek instance: Activity yaşam döngüsü boyunca aynı ProfileViewModel kullanılır
    private val profileViewModel: ProfileViewModel by viewModels()
    // Launcher dışarıdan erişilebilmesi için lateinit
    lateinit var permissionLauncherOnboarding: ActivityResultLauncher<Array<String>>

    // Launcher LoginScreen'de kullanılacak
    lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        registerOnboardingPermissionLauncher()
        setContent {
            val context= LocalContext.current

           // val navController = rememberNavController()
// Launcher Compose içinde oluşturuluyor ve MainActivity’de tutuluyor
            googleSignInLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartActivityForResult()
            ) { result ->
                googleLoginViewModel.handleGoogleSignInResult(result.data, context)
            }

            // Konum izin launcher burada oluşturuluyor
            val permissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestMultiplePermissions(),
                onResult = { permissions ->
                    val granted =
                        permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                                permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] == true


                    if (granted) {
                        val fusedLocationClient =
                            LocationServices.getFusedLocationProviderClient(this)
                        try {
                            fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
                                loc?.let { mapViewModel.setUserLocation(MyLocation(it.latitude, it.longitude)) }
                            }
                        } catch (e: SecurityException) {
                            e.printStackTrace()
                        }
                    } else {
                        Toast.makeText(this, "Konum izni gerekli", Toast.LENGTH_SHORT).show()
                    }
                }
            )

            // 🔹 Tema durumu
            val isDarkTheme by profileViewModel.isDarkTheme.collectAsState()

            // 🔹 Dil durumu
            val currentLocale by profileViewModel.currentLocale.collectAsState()

            // 🔹 ProvideLocale: Uygulama genelinde tüm string kaynaklarını seçilen dile göre gösterir
            ProvideLocale( currentLocale) { // <- burada context güncellendi
                AroundYouTheme(darkTheme = isDarkTheme) {
                    val navController = rememberNavController()
                    MainScreen(navController = navController,
                        profileViewModel = profileViewModel,
                        googleLoginViewModel = googleLoginViewModel,
                        mapViewModel = mapViewModel,
                        locationViewModel = locationViewModel,
                        favoriteViewModel = favoriteViewModel,
                        permissionLauncher = permissionLauncher,
                        googleSignInLauncher = googleSignInLauncher,
                        chatViewModel = chatViewModel

                        )
                }
            }
        }

    }
   private fun registerOnboardingPermissionLauncher() {
        permissionLauncherOnboarding =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->

                val granted = permissions.values.all { it }

                if (granted) {
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun MainScreen(
    navController: NavHostController,
    profileViewModel: ProfileViewModel,
    googleLoginViewModel: GoogleLoginViewModel,
    mapViewModel: MapViewModel,
    chatViewModel: ChatViewModel,
    locationViewModel: LocationViewModel,
    favoriteViewModel: FavoriteViewModel,
    permissionLauncher: ActivityResultLauncher<Array<String>>,
    googleSignInLauncher: ActivityResultLauncher<Intent>
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomNavItems = listOf(
        BottomNavItem(
            "mapScreen",
            Icons.Default.Place,
            stringResource(R.string.nav_map)
        ) { navController.navigate("mapScreen") },
        BottomNavItem(
            "feedScreen",
            Icons.Default.List,
            stringResource(R.string.nav_feed)
        ) { navController.navigate("feedScreen") },
        BottomNavItem(
            "favoriteScreen",
            Icons.Default.Favorite,
            stringResource(R.string.nav_favorites)
        ) { navController.navigate("favoriteScreen") },
        BottomNavItem(
            "asettingsScreen",
            Icons.Default.Settings,
            stringResource(R.string.nav_settings)
        ) { navController.navigate("settingsScreen") }
    )

    val fabItem = BottomNavItem("aIScreen", Icons.Default.Send, stringResource(R.string.nav_ai)) {
        navController.navigate("aIScreen")
    }

    val hideBottomBarRoutes = listOf("onboardingScreen", "loginScreen", "aIScreen")
    val colors = MaterialTheme.colorScheme


    Scaffold(
        containerColor = colors.surface, // Scaffold arka planı beyaz
        floatingActionButton = {
            if (currentRoute !in hideBottomBarRoutes) { // FAB gizli rotalarda gösterilmeyecek
                FloatingActionButton(
                    onClick = { fabItem.onClick() },
                    shape = CircleShape,
                    modifier = Modifier
                        .size(48.dp)
                        .offset(y = 35.dp),
                    containerColor = colors.surface,
                    contentColor = colors.onSurface,
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ai),
                        contentDescription = fabItem.label,
                        modifier = Modifier.size(34.dp),
                        tint = Color.Unspecified
                    )
                }
            }
        },

        floatingActionButtonPosition = FabPosition.Center,
        bottomBar = {
            if (currentRoute !in hideBottomBarRoutes) {
                NavigationBar(
                    containerColor = colors.surface,           // Light ve Dark mode uyumlu arka plan
                    contentColor = colors.surface,          // Icon ve label rengi
                    tonalElevation = 12.dp,
                    modifier = Modifier
                        .height(90.dp)
                        .clip(
                            BottomBarCutoutShape(
                                fabRadius = 24f, //fab yarıçapı
                                cutoutRadius = 100f
                            ) //oyuk  büyüklüğü
                        ), // FAB boyutuna göre ayarlandı
                ) {
                    // Sol 2 item
                    bottomNavItems.take(2).forEach { item ->
                        NavigationBarItem(
                            selected = currentRoute == item.route,
                            onClick = item.onClick,
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label, fontSize = 8.sp)},
                            alwaysShowLabel = true,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFF4FC3F7), // Seçili icon rengi mavi
                                selectedTextColor = Color(0xFF4FC3F7), // Seçili label rengi mavi
                                unselectedIconColor = colors.onSurface.copy(alpha = 0.6f),      // Seçili olmayan icon siyah
                                unselectedTextColor = colors.onSurface.copy(alpha = 0.6f)      // Seçili olmayan label siyah
                            )
                        )
                    }

                    Spacer(Modifier.width(80.dp)) // FAB için boşluk

                    // Sağ 2 item
                    bottomNavItems.drop(2).forEach { item ->
                        NavigationBarItem(
                            selected = currentRoute == item.route,
                            onClick = item.onClick,
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label, fontSize = 8.sp) },
                            alwaysShowLabel = true,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFF4FC3F7), // Seçili icon rengi mavi
                                selectedTextColor = Color(0xFF4FC3F7),
                                unselectedIconColor = colors.onSurface.copy(alpha = 0.6f),
                                unselectedTextColor = colors.onSurface.copy(alpha = 0.6f)
                            )
                        )
                    }
                }
            }
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)   // 🔥 TÜM paddingleri iptal et

    ) { innerPadding ->

            if (currentRoute == "aIScreen") {
                // AI Screen kendi padding ve layout kontrolüne sahip
                AIScreen(
                    navController = navController,
                    modifier = Modifier,
                    chatViewModel = chatViewModel
                )
            } else {
                // Diğer ekranlar için mevcut Router
                Box(modifier = Modifier.padding(innerPadding)) {
                    Router(
                        navController = navController,
                        profileViewModel = profileViewModel,
                        googleLoginViewModel = googleLoginViewModel,
                        mapViewModel = mapViewModel,
                        favoriteViewModel = favoriteViewModel,
                        locationViewModel = locationViewModel,
                        permissionLauncher = permissionLauncher,
                        permissionLauncherOnboarding = permissionLauncher,
                        googleSignInLauncher = googleSignInLauncher,
                        chatViewModel = chatViewModel
                    )

                }
            }
        }
    }
