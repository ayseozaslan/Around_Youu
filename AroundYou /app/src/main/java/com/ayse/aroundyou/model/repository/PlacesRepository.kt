package com.ayse.aroundyou.model.repository

import android.util.Log
import com.ayse.aroundyou.BuildConfig
import com.ayse.aroundyou.data.api.PlacesApi
import com.ayse.aroundyou.model.response.PlaceItem
import com.ayse.aroundyou.model.response.PlacesResponse
import javax.inject.Inject


class PlacesRepository @Inject constructor(
    private val api: PlacesApi
) {


    //Google Places Text Search API üzerinden yakın yerleri aramak için kullanılıyor.
    //örn: cafe in istanbul
    //Google Text Search API normal insan diliyle yazılmış aramaları kabul eder:

    suspend fun getCocktailPlaces(lat: Double, lng: Double, apiKey: String) : List<PlaceItem> {
        val searchResponse = api.searchPlaces(
            query = "cocktail bar OR cocktail restaurant OR cocktail",
            location = "$lat, $lng",
            apiKey = api.toString()
        )
        //val resultList = mutableListOf<PlaceItem>()

        /* 2) Her mekanın detaylarını getir
        searchResponse.results.forEach { item ->
            item.placeId?.let {
                val detail = api.getPlaceDetails(it, apiKey = apiKey)
                detail.results?.let { details ->
                    resultList.add(details)
                }
            }
        }

         */
        return searchResponse.results
    }

    suspend fun getNearby(city:String, type:String) :PlacesResponse{
        return api.getNearby(
            query = "$type in $city")
    }

    // Yakın mekanları getir
    suspend fun getNearbyPlacesSearch(lat: Double, lng: Double, query: String): List<PlaceItem> {
        Log.d("PlacesRepository", "API Çağrılıyor -> lat=$lat, lng=$lng, query=$query")

        val response = api.getNearbyPlaces(
            location = "$lat,$lng",
            radius = 5000, // 5 km
            keyword = query, // Aranan kelime, örn: "hospital" veya "hastane"
            apiKey = BuildConfig.MAPS_API_KEY
        )
        // response.results: List<Place>?, nullable olduğu için ?: emptyList() ekliyoruz
        Log.d("PlacesRepository", "API Response Status: ${response.status}")
        Log.d("PlacesRepository", "API Dönen Sonuç Sayısı: ${response.results?.size ?: 0}")
        return response.results ?: emptyList()
    }

    //yakınlardaki mekanları bul.
    suspend fun getNearbyPlaces(
        lat: Double,
        lng: Double,
        query: String
    ): PlacesResponse{
        return api.getNearbyPlaces(
            location = "$lat,$lng",
            radius = 5000,
            keyword = query,
            apiKey = BuildConfig.MAPS_API_KEY
        )
    }

    //Detaylı Places gösterimi
    suspend fun getPlaceDetails(placeId: String) : PlacesResponse {
        return api.getPlaceDetails(
            placeId = placeId,
            fields = "name,rating,formatted_address,photos,reviews",
            apiKey = BuildConfig.MAPS_API_KEY
        )
    }


}