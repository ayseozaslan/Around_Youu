package com.ayse.aroundyou.ui.screen

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.ayse.aroundyou.BuildConfig
import com.ayse.aroundyou.R
import com.ayse.aroundyou.utils.AdManager
import com.ayse.aroundyou.viewmodel.MapViewModel
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch
import com.ayse.aroundyou.utils.findActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import androidx.compose.ui.viewinterop.AndroidView


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MissingPermission")
@Composable
fun MapScreen(
    mapViewModel: MapViewModel,
    permissionLauncher: ActivityResultLauncher<Array<String>>
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val userLocation by mapViewModel.userLocation.observeAsState()
    val selectedPlace by mapViewModel.selectedPlace.observeAsState()

    // ❗ Artık sadece 1 kez collect ediyoruz
    val searchResults by mapViewModel.placesMapSearch.collectAsState()

    // 🔥 marker listesi: arama varsa arama sonucunu kullan
    val markerList = if (searchResults.isNotEmpty()) searchResults else mapViewModel.placesMap

    // LOG → MarkerList boyutu
    LaunchedEffect(markerList) {
        Log.d("MapScreen", "MarkerList size = ${markerList.size}")
        markerList.forEach {
            Log.d("MapScreen", "Marker: ${it.name} - ${it.geometry?.location}")
        }
    }

    // LOG → userLocation değişimi
    LaunchedEffect(userLocation) {
        Log.d("MapScreen", "UserLocation = $userLocation")
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showSheet by remember { mutableStateOf(false) }

    // İlk açılışta izin iste
    LaunchedEffect(Unit) {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    Box(modifier = Modifier.fillMaxSize()
       ) {

        // Harita sadece userLocation gelince açılıyor
        if (userLocation != null) {

            val location = userLocation!!

            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(
                        LatLng(location.lat ?: 0.0, location.lng ?: 0.0),
                        15f
                    )
                },
                properties = MapProperties(isMyLocationEnabled = true)
            ) {

                // 🔥 LOG: Marker çiziliyor mu?
                Log.d("MapScreen", "Drawing markers: ${markerList.size}")

                markerList.forEach { place ->
                    val loc = place.geometry?.location
                    if (loc != null) {
                        Marker(
                            state = MarkerState(LatLng(loc.lat, loc.lng)),
                            title = place.name ?: "",
                            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED),
                            onClick = {
                                mapViewModel.markerClick(place)
                                showSheet = true
                                coroutineScope.launch { sheetState.show() }
                                true
                            }
                        )
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.check_internet_location),
                    fontSize = 12.sp,
                    color = Color.DarkGray
                )
            }
        }

        // Üstte SearchBar
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(top = 18.dp) // üstten boşluk ekliyoruz
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            MapSearchBar(
                query = mapViewModel.searchQuery,
                onQueryChange = { mapViewModel.updateSearchQuery(it) },
                onSearch = { query ->
                    if (userLocation != null) {
                        mapViewModel.searchPlaces(query)
                    } else {
                        Log.e("MapScreen", "Search blocked. Location is NULL")
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        // BottomSheet
        if (showSheet && selectedPlace != null) {
            ModalBottomSheet(
                onDismissRequest = { showSheet = false },
                sheetState = sheetState,
                modifier = Modifier.fillMaxWidth(),
                containerColor = Color(0xFFDDDDDD),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            ) {
                selectedPlace?.let { place ->
                    Column(modifier = Modifier.padding(16.dp)) {

                        Text(
                            text = place.name ?: stringResource(id = R.string.unknown_place),
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 2.dp, bottom = 8.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.google),
                                contentDescription = "Google Maps",
                                tint = Color.Unspecified,
                                modifier = Modifier.size(20.dp)
                            )

                            Spacer(modifier = Modifier.width(4.dp))

                            place.rating?.let { rating ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    repeat(rating.toInt()) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.baseline_star_24),
                                            contentDescription = "Yıldız",
                                            tint = Color(0xFFFFD700),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Text(
                                        text = " $rating",
                                        color = Color.DarkGray,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(start = 2.dp)
                                    )
                                }
                            }
                        }

                        val photoRef = place.photos?.firstOrNull()?.photoReference
                        val photoUrl = photoRef?.let {
                            "https://maps.googleapis.com/maps/api/place/photo" +
                                    "?maxwidth=400&photo_reference=$it&key=${BuildConfig.MAPS_API_KEY}"
                        }

                        photoUrl?.let {
                            AsyncImage(
                                model = it,
                                contentDescription = place.name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        Button(
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
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text(
                                stringResource(id = R.string.cd_google_maps_icon),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            }
        }
        // 4️⃣ Banner reklam (en sonda)
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(50.dp)
        ) {


        }
    }


    // Geri tuşunu devre dışı bırak
    BackHandler {
        // Boş bırakırsan geri gitme işlemi olmaz
    }
}


@Composable
fun MapSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    TextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = {
            Text(
                text = stringResource(id = R.string.search_placeholder_feed),
                color = Color.Gray,
                fontSize = 12.sp
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = Color.Gray
            )
        },
        singleLine = true,
        shape = RoundedCornerShape(24.dp), // 🔹 Oval çerçeve
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
           // cursorColor = colors.primary
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .shadow(4.dp, RoundedCornerShape(24.dp)) // 🔹 Hafif gölge efekti
            .background(Color.White, RoundedCornerShape(24.dp)), // 🔹 Arka plan ve şekil
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = {
                Log.d("MapSearchBar", "Search pressed: $query")
                onSearch(query)
                keyboardController?.hide()
            }
        )
    )
}
