package com.ayse.aroundyou.model.response

import com.google.gson.annotations.SerializedName

data class PlacesResponse(
    @SerializedName("results") val results: List<PlaceItem>,
    @SerializedName("status") val status: String
)


data class PlaceItem(
    @SerializedName("place_id") val placeId: String,
    @SerializedName("name") val name: String?,
    @SerializedName("vicinity") val vicinity: String?,
    @SerializedName("geometry") val geometry: Geometry?,
    @SerializedName("types") val types: List<String>?,
    @SerializedName("formatted_address") val formattedAddress: String?,
    @SerializedName("rating") val rating: Double?,
    @SerializedName("user_ratings_total") val userRatingsTotal: Int?,
    @SerializedName("photos") val photos: List<Photo>?,
    @SerializedName("reviews") val reviews: List<Review>?,

    // 🔥 NewsItem’daki gibi otomatik ID ekliyoruz
    val id: String = generatePlaceItemId(placeId)
)
fun generatePlaceItemId(placeId: String): String {
    return placeId.hashCode().toString()
}

data class Photo(
    @SerializedName("photo_reference") val photoReference: String?
)
data class Review(
    @SerializedName("author_name") val authorName: String?,
    @SerializedName("text") val text: String?,
    @SerializedName("rating") val rating: Double?
)
data class Geometry(
    @SerializedName("location") val location: com.ayse.aroundyou.model.response.Location
)
data class Location(
    @SerializedName("lat") val lat: Double,
    @SerializedName("lng") val lng: Double
)
data class MyLocation(
    val lat: Double,
    val lng: Double
)


/*
data class Photo(val photo_reference: String?)
data class Review(val author_name: String?, val text: String?, val rating: Double?)


data class Geometry(
    val location: Location
)
data class Location(
    val lat: Double,
    val lng: Double
)
data class MyLocation(
    val lat: Double,
    val lng: Double
)
/*
data class PlacesResponse(
    val results: List<Place>,
    val status: String
)

data class Place(
val place_id: String,
 val name: String?,
 val vicinity: String?,
 val geometry: Geometry?,
 val types: List<String>?,
 val formatted_address: String?,
 val rating: Double?,
 val user_ratings_total: Int?,
val photos: List<Photo>?,
val reviews: List<Review>? ).
 */


 */