package com.ayse.aroundyou.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ayse.aroundyou.model.response.PlaceItem


@Composable
fun FavoritePlaceCard(
    place: PlaceItem,
    photoUrl: String?,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onRemoveClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable {onClick() }
            .border(2.dp, Color(0xFF4FC3F7), RoundedCornerShape(15.dp)),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            // ▌ ÜST FOTOĞRAF ALANI
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp))
                    .background(Color.Transparent)
            ) {
                if (photoUrl != null) {
                    AsyncImage(
                        model = photoUrl,
                        contentDescription = place.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(colors.onSurface.copy(alpha = 0.4f))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.Bottom,
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = (place.types?.firstOrNull() ?: "PLACE").uppercase(),
                            fontSize = 13.sp,
                            color = colors.onPrimary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .background(Color.Red, shape = RoundedCornerShape(4.dp))
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = place.name ?: "Bilinmeyen Mekan",
                            fontSize = 12.sp,
                            color = colors.onPrimary,
                            fontWeight = FontWeight.Bold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ▌ ALT KISIM - Adres ve rating
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(7f)) {

                    Text(
                        text = place.vicinity
                            ?: place.formattedAddress
                            ?: "Konum bilgisi yok",
                        fontSize = 12.sp,
                        color = colors.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    Text(
                        text = "⭐ ${place.rating ?: 0.0}   (${place.userRatingsTotal ?: 0} yorum)",
                        fontSize = 12.sp,
                        color = colors.onSurfaceVariant
                    )
                }

                // ❌ FAVORİDEN ÇIKARMA TUŞU
                IconButton(
                    onClick = onRemoveClick,
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
                        .background(colors.surfaceVariant.copy(alpha = 0.5f), shape = RoundedCornerShape(50))
                        .size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Remove from favorites",
                        tint = if (isFavorite) Color.Red else Color.Black,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }
        }
    }
}
