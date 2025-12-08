package com.ayse.aroundyou.ui.screen

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ayse.aroundyou.BuildConfig
import com.ayse.aroundyou.R
import com.ayse.aroundyou.ui.components.FavoritePlaceCard
import com.ayse.aroundyou.viewmodel.FavoriteViewModel

@Composable
fun FavoriteScreen(
    favoriteViewModel: FavoriteViewModel,
    navController: NavController,
) {
    val favoritePlaces by favoriteViewModel.favoritePlacesItems.collectAsState()
    val context = LocalContext.current

    val surfaceColor = MaterialTheme.colorScheme.surface
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val secondaryColor = MaterialTheme.colorScheme.secondary

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(surfaceColor)
    ) {
        // 🔹 AppBar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
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

            Text(
                text = stringResource(id = R.string.favorites_title),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = onSurfaceColor,
                modifier = Modifier.weight(1f), // Başlığı ortalamak için weight
                textAlign = TextAlign.Center
            )
        }

        // 🔹 İçerik
        if (favoritePlaces.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.favorites_empty),
                    fontSize = 16.sp,
                    color = secondaryColor,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(surfaceColor)
                    .padding(horizontal = 16.dp)
            ) {
                items(favoritePlaces, key = { it.placeId }) { place ->
                    val photoRef = place.photos?.firstOrNull()?.photoReference
                    val photoUrl = photoRef?.let {
                        "https://maps.googleapis.com/maps/api/place/photo" +
                                "?maxwidth=400&photo_reference=$it&key=${BuildConfig.MAPS_API_KEY}"
                    }

                    val isFavorite =
                        remember { mutableStateOf(favoriteViewModel.isFavorite(place)) }

                    FavoritePlaceCard(
                        place = place,
                        photoUrl = photoUrl,
                        isFavorite = isFavorite.value,
                        onClick = {
                            val gmmIntentUri = Uri.parse(
                                "https://www.google.com/maps/search/?api=1&query=${
                                    Uri.encode(place.name ?: "")
                                }&query_place_id=${place.placeId}"
                            )
                            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                            mapIntent.setPackage("com.google.android.apps.maps")
                            context.startActivity(mapIntent)
                        },
                        onRemoveClick = {
                            favoriteViewModel.toggleFavorite(place)
                            isFavorite.value = !isFavorite.value
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}
