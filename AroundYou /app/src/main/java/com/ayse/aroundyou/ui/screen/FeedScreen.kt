package com.ayse.aroundyou.ui.screen


import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.ayse.aroundyou.BuildConfig
import com.ayse.aroundyou.R
import com.ayse.aroundyou.model.entities.Country
import com.ayse.aroundyou.ui.components.FavoritePlaceCard
import com.ayse.aroundyou.utils.ProvideLocale
import com.ayse.aroundyou.viewmodel.FavoriteViewModel
import com.ayse.aroundyou.viewmodel.LocationViewModel
import com.ayse.aroundyou.viewmodel.MapViewModel
import com.ayse.aroundyou.viewmodel.ProfileViewModel
import java.util.Locale
import com.ayse.aroundyou.utils.findActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import androidx.compose.runtime.*



@SuppressLint("ContextCastToActivity")
@Composable
fun FeedScreen(
    navController: NavController,
    mapViewModel: MapViewModel,
    favoriteViewModel: FavoriteViewModel,
    locationViewModel: LocationViewModel,
    profileViewModel: ProfileViewModel,
    permissionLauncher: ActivityResultLauncher<Array<String>> // MainActivity’den geçiyoruz
) {
    var showFilterDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    BackHandler(enabled = true) { /* geri tuşu kapalı */ }

    // İlk açılışta izin iste
    LaunchedEffect(Unit) {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }
    // 🔹 2) Interstitial reklam yükleyen LaunchedEffect
    var mInterstitialAd by remember { mutableStateOf<InterstitialAd?>(null) }

    LaunchedEffect(true) {
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            context,
            "ca-app-pub-3940256099942544/1033173712",  // TEST ID
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding() // status bar boşluğu ekler
            .background(MaterialTheme.colorScheme.background)
    ) {

        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
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
                Spacer(modifier = Modifier.width(12.dp)) // icon ile başlık arası boşluk

                Text(
                    text = stringResource(id = R.string.feed_title),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            FeedSearchBar(
                query = mapViewModel.searchQuery,
                onQueryChange = { mapViewModel.updateSearchQuery(it) },
                onFilterClick = { showFilterDialog = true },
                onSearch = { query ->
                    Log.d("SEARCH_BAR", "Tetiklendi: $query")

                    mapViewModel.searchPlacesFeed(query)
                   // mapViewModel.loadPlaces( query)
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            PlacesList(
                mapViewModel = mapViewModel,
                favoriteViewModel = favoriteViewModel
            )
        }

        val currentLocale by profileViewModel.currentLocale.collectAsState()
        // Kullanıcının seçtiği locale

        key(currentLocale) {
        if (showFilterDialog) {
           // 🔹 Locale değişirse dialog yeniden oluşturulur
                FilterDialog(
                    locationViewModel = locationViewModel,
                    mapViewModel = mapViewModel,
                    profileViewModel = profileViewModel,
                    onDismiss = { showFilterDialog = false },
                    onApply = { country, city, region, categories ->
                        Log.d(
                            "Filter",
                            "Ülke: $country Şehir: $city Bölge: $region Kategoriler: $categories"
                        )
                        showFilterDialog = false
                        // Reklam göster
                        mInterstitialAd?.show(context.findActivity()!!)
                    }
                )
            }
        }


    }
}

// SEARCH BAR
@Composable
fun FeedSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onFilterClick: () -> Unit,
    onSearch: (String) -> Unit
) {

    val keyboardController = LocalSoftwareKeyboardController.current
    val colors = MaterialTheme.colorScheme

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        TextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text(stringResource(id=R.string.search_placeholder_map), color = colors.onSurfaceVariant, fontSize = 12.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = colors.onSurfaceVariant) },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions (
                onSearch = {
                    onSearch(query)
                    keyboardController?.hide()
                }
            ),
            textStyle = TextStyle(color = colors.onSurface),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = colors.surfaceVariant,
                unfocusedContainerColor = colors.surfaceVariant,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = colors.primary
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .weight(1f)
                .height(52.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(colors.surfaceVariant)
                .clickable { onFilterClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Menu, contentDescription = "Filtrele", tint = colors.onSurfaceVariant)
        }

    }
}


// FILTER DIALOG
// 🔹 Locale-aware string helper (EKLENDİ)
@SuppressLint("LocalContextConfigurationRead")
@Composable
//“Şu string ID’yi telefonun sistem diline göre değil, benim seçtiğim Locale’e göre çevir.”
fun localizedString(@StringRes id: Int, locale: Locale): String {
    val context = LocalContext.current
    val config = Configuration(context.resources.configuration)/*Telefonun mevcut konfigürasyonunu (dil, ekran boyutu, yazı tipi boyutu vb.) kopyalıyoruz.
    Kopyalamamızın nedeni:
    👉 Mevcut ayarları bozmadan sade bir şekilde locale değiştirmek.
    */
    config.setLocale(locale)//configürasyona yeni dili uygula
    return context.createConfigurationContext(config).resources.getString(id)
}

@Composable
fun FilterDialog(
    locationViewModel: LocationViewModel,
    mapViewModel: MapViewModel,
    profileViewModel: ProfileViewModel,
    onDismiss: () -> Unit,
    onApply: (String, String, String, List<String>) -> Unit
) {
    var selectedCategories by remember { mutableStateOf(listOf<String>()) }

    val countries by locationViewModel.countries
    val selectedCountry by locationViewModel.selectedCountry
    val selectedRegion by locationViewModel.selectedRegion
    val selectedCity by locationViewModel.selectedCity

    // 🔹 Kullanıcının seçtiği mevcut dil (ZATEN VARDI)
    val currentLocale by profileViewModel.currentLocale.collectAsState()
    val locale = remember(currentLocale) {
        Locale(currentLocale) // "tr", "en", "fr" → Locale objesine dönüşür
    }

    // 🔹 stringResource yerine localizedString() kullanıldı (DİL DEĞİŞİMİNE DİNAMİK TEPKİ)
    val categories = listOf(
        localizedString(R.string.cat_restaurant,locale),
        localizedString(R.string.cat_cafe, locale),
        localizedString(R.string.cat_hotel, locale),
        localizedString(R.string.cat_pizza, locale),
        localizedString(R.string.cat_bar, locale),
        localizedString(R.string.cat_dessert, locale),
        localizedString(R.string.cat_kokteyl, locale),
        localizedString(R.string.cat_ocakbasi, locale),
        localizedString(R.string.cat_workshop, locale)
    )

    // 🔹 ikona eşleşen localized kategoriler
    val categoryIcons = mapOf(
        localizedString(R.string.cat_restaurant, locale) to R.drawable.restorant,
        localizedString(R.string.cat_cafe, locale) to R.drawable.kafe,
        localizedString(R.string.cat_hotel, locale) to R.drawable.otel,
        localizedString(R.string.cat_pizza, locale) to R.drawable.pizza,
        localizedString(R.string.cat_bar, locale) to R.drawable.bar,
        localizedString(R.string.cat_dessert, locale) to R.drawable.tatli,
        localizedString(R.string.cat_kokteyl,locale)  to R.drawable.kokteyl,
        localizedString(R.string.cat_ocakbasi, locale) to R.drawable.ocakbasi,
        localizedString(R.string.cat_workshop, locale) to R.drawable.workshop
    )

    // 🔹 renk eşleşmesi de localized string ile yapılıyor
    val categoryColors = mapOf(
        localizedString(R.string.cat_restaurant, locale) to Color(0xFFFFC107),
        localizedString(R.string.cat_cafe, locale) to Color(0xFF795548),
        localizedString(R.string.cat_hotel, locale) to Color(0xFF2196F3),
        localizedString(R.string.cat_pizza,locale) to Color(0xFFFF5722),
        localizedString(R.string.cat_bar, locale) to Color(0xFF9C27B0),
        localizedString(R.string.cat_dessert, locale) to Color(0xFFE91E63),
        localizedString(R.string.cat_kokteyl, locale) to Color(0xFF4CAF50),
        localizedString(R.string.cat_ocakbasi, locale) to Color(0xFFC983D5),
        localizedString(R.string.cat_workshop, locale) to Color(0xFFDCB079)
    )

    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(10.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {

                // 🔹 Başlık güncellendi
                Text(
                    localizedString(R.string.filter, locale),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(3.dp))

                // 🔹 Açıklama güncellendi
                Text(
                    localizedString(R.string.filter_desc, locale),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 10.sp
                )

                // 🔹 Ülke seçim dropdown
                CountryDropdown(
                    countries = countries,
                    selectedCountry = selectedCountry,
                    profileViewModel = profileViewModel,
                    onCountrySelected = { locationViewModel.selectCountry(it) }
                )

                Spacer(Modifier.height(12.dp))

                RegionDropdown(
                    regions = selectedCountry?.regions?.map { it.name } ?: emptyList(),
                    selectedRegion = selectedRegion?.name,
                    onRegionSelected = { regionName ->
                        val region = selectedCountry?.regions?.find { it.name == regionName }
                        region?.let { locationViewModel.selectRegion(it) }
                    },
                    profileViewModel = profileViewModel,
                )

                Spacer(Modifier.height(12.dp))

                CityDropdown(
                    cities = selectedRegion?.cities ?: emptyList(),
                    selectedCity = selectedCity,
                    onCitySelected = { locationViewModel.selectCity(it) },
                    profileViewModel = profileViewModel
                )

                Spacer(Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.category),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))

                    // 🔹 Kategoriler başlığı güncellendi
                    Text(
                        text = localizedString(R.string.categories, locale),
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 12.sp
                    )
                }

                Spacer(Modifier.height(10.dp))

                // 🔹 kategori butonları
                CategoryButtons(
                    categories = categories,
                    categoryIcons = categoryIcons,
                    categoryColors = categoryColors,
                    selectedCategories = selectedCategories,
                    onCategoryClick = { clicked ->
                        selectedCategories =
                            if (clicked in selectedCategories) selectedCategories - clicked
                            else selectedCategories + clicked
                    }
                )

                Spacer(Modifier.height(20.dp))

                Button(
                    onClick = {
                        mapViewModel.applyFilters(
                            selectedCountry,
                            selectedRegion,
                            selectedCity,
                            selectedCategories
                        )
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4FC3F7),
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {

                    // 🔹 Apply butonu güncellendi
                    Text(
                        localizedString(R.string.apply, locale),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}

//strignResource telefon dilini kullanır .Telefonu kontrol eder.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountryDropdown(
    countries: List<Country>,
    selectedCountry: Country?,
    profileViewModel: ProfileViewModel,
    onCountrySelected: (Country) -> Unit,
) {
    val currentLocale by profileViewModel.currentLocale.collectAsState() // 🔹 Locale değişimini dinle
    var expanded by remember { mutableStateOf(false) }//Bu, açılır listenin (DropdownMenu) açık mı kapalı mı olduğunu tutan bir değişken. başlangıc false kapalı

    val userLocale = remember(currentLocale) { //currentLocale kullanıcının seçtiği dil
        Locale(currentLocale)   // örn: "en" → Locale("en")
        //Locale(currentLocale) → Android'in Locale sınıfına çevirir.
    }

    // 🔹 Compose her recomposition'da bu değerleri tekrar hesaplayacak
    /*
    Eğer kullanıcı bir ülke seçtiyse → selectedCountry.name
    Eğer seçmediyse → localizedString(R.string.select, userLocale) yani:
   "Select" / "Seç" / "Auswählen" gibi, seçilen dildeki çeviri
     */
    val text = selectedCountry?.name ?: localizedString(R.string.select, userLocale)
    val labelText = localizedString(R.string.country, userLocale)
      /*   etiket metni (label):"Country""Ülke""Land""País"
             Bu metin her zaman seçilen dile göre değişir.
       */
    val colors = MaterialTheme.colorScheme

    // 🔹 currentLocale değişirse Composable yeniden oluşturulsun
    ProvideLocale( currentLocale) {
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            OutlinedTextField(
                value = text,
                onValueChange = {},
                readOnly = true,
                label = { Text(labelText, color = colors.onSurface) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = colors.onSurface,
                    unfocusedTextColor = colors.onSurfaceVariant,
                    cursorColor = colors.primary,
                    focusedLabelColor = colors.primary,
                    unfocusedLabelColor = colors.onSurfaceVariant,
                    focusedContainerColor = colors.surfaceVariant,
                    unfocusedContainerColor = colors.surfaceVariant,
                    focusedBorderColor = Color(0xFF4FC3F7),
                    unfocusedBorderColor = Color(0xFF4FC3F7)
                )
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(colors.surface)
            ) {
                countries.forEach { country ->
                    DropdownMenuItem(
                        text = { Text(country.name ?: "", color = colors.onSurface) },
                        onClick = {
                            onCountrySelected(country)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}


// REGION DROPDOWN
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegionDropdown(
    regions: List<String>,
    selectedRegion: String?,
    profileViewModel: ProfileViewModel,
    onRegionSelected: (String) -> Unit
) {
    val currentLocale by profileViewModel.currentLocale.collectAsState()
    var expanded by remember { mutableStateOf(false) }

    val userLocale = remember(currentLocale) {
        Locale(currentLocale)   // örn: "en" → Locale("en")
    }
    val text = selectedRegion ?: localizedString(R.string.select,userLocale)
    val labelText = localizedString(R.string.region,userLocale)

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = {
        expanded = !expanded
        Log.d("Dropdown", "Region open: $expanded")
    }) {

        OutlinedTextField(
            value = text,
            onValueChange = {},
            readOnly = true,
            label = { Text(labelText, color = MaterialTheme.colorScheme.onSurface) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                cursorColor = MaterialTheme.colorScheme.primary,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedBorderColor = Color(0xFF4FC3F7),
                unfocusedBorderColor = Color(0xFF4FC3F7)
            )
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        ) {
            regions.forEach { region ->
                DropdownMenuItem(
                    text = { Text(region, color = MaterialTheme.colorScheme.onSurface) },
                    onClick = {
                        onRegionSelected(region)
                        expanded = false
                        Log.d("Dropdown", "Bölge seçildi: $region")
                    }
                )
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityDropdown(
    cities: List<String>,
    selectedCity: String?,
    profileViewModel: ProfileViewModel,
    onCitySelected: (String) -> Unit
) {
     val currentLocale by profileViewModel.currentLocale.collectAsState()
    var expanded by remember { mutableStateOf(false) }

    val userLocale = remember(currentLocale) {
        Locale(currentLocale)
    }
    val text = selectedCity ?: localizedString(R.string.select,userLocale)
    val labelText = localizedString(R.string.city,userLocale)

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {

        OutlinedTextField(
            value = text,
            onValueChange = {},
            readOnly = true,
            label = { Text(labelText, color = MaterialTheme.colorScheme.onSurface) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                cursorColor = MaterialTheme.colorScheme.primary,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedBorderColor = Color(0xFF4FC3F7),
                unfocusedBorderColor = Color(0xFF4FC3F7)
            )
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        ) {
            cities.forEach { city ->
                DropdownMenuItem(
                    text = { Text(city, color = MaterialTheme.colorScheme.onSurface) },
                    onClick = {
                        onCitySelected(city)
                        expanded = false
                    }
                )
            }
        }
    }
}


// CATEGORY BUTTONS

@Composable
fun CategoryButtons(
    categories: List<String>,
    categoryColors: Map<String, Color>,
    categoryIcons: Map<String, Int>,
    selectedCategories: List<String>,
    onCategoryClick: (String) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3), // 🔹 3 sütunlu grid
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 0.dp, max = 250.dp), // 🔹 Yükseklik limiti
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        items(categories) { category ->

            val isSelected = selectedCategories.contains(category) // 🔹 Seçili mi?
            val backgroundColor =
                if (isSelected) categoryColors[category] ?: Color.Gray else Color.LightGray // 🔹 Arka plan
            val contentColor = if (isSelected) Color.White else Color.Black // 🔹 İç yazı rengi

            Button(
                onClick = { onCategoryClick(category) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = backgroundColor,
                    contentColor = contentColor
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
            ) {

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    val iconRes = categoryIcons[category] // 🔹 İkon resmi
                    if (iconRes != null) {
                        Image(
                            painter = painterResource(id = iconRes),
                            contentDescription = category,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    Text(
                        text = category,
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/*
fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}

 */


@Composable
fun PlacesList(
    mapViewModel : MapViewModel,
    favoriteViewModel : FavoriteViewModel
) {
    val places by mapViewModel.placesMapSearch.collectAsState()
    val context = LocalContext.current

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(items = places, key = { it.placeId }) { place ->

            // 🔹 Fotoğraf URL'si
            val photoRef = place.photos?.firstOrNull()?.photoReference
            val photoUrl = photoRef?.let {
                "https://maps.googleapis.com/maps/api/place/photo" +
                        "?maxwidth=400&photo_reference=$it&key=${BuildConfig.MAPS_API_KEY}"
            }

            // 🔹 Favori durumu UI için
            val isFavorite = remember { mutableStateOf(favoriteViewModel.isFavorite(place)) }

            FavoritePlaceCard(
                place = place,
                photoUrl = photoUrl,
                isFavorite = isFavorite.value, // 🔹 Favori durumunu gönderiyoruz
                onClick = {
                    val activity = context.findActivity()

                    val gmmIntentUri = Uri.parse(
                        "https://www.google.com/maps/search/?api=1&query=${Uri.encode(place.name ?: "")}&query_place_id=${place.placeId}"
                    )
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                    mapIntent.setPackage("com.google.android.apps.maps")

                    if (activity != null) {
                        // Activity context varsa direkt aç
                        activity.startActivity(mapIntent)
                    } else {
                        // Activity yoksa Application context ile aç, FLAG_ACTIVITY_NEW_TASK ekle
                        mapIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        context.startActivity(mapIntent)
                    }
                },
                onRemoveClick = {
                    // Favori ekle / çıkar
                    favoriteViewModel.toggleFavorite(place)
                    isFavorite.value = !isFavorite.value // UI güncellemesi
                }
            )
        }
    }
}
