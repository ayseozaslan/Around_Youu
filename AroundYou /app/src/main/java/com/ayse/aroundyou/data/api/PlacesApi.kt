package com.ayse.aroundyou.data.api

import com.ayse.aroundyou.BuildConfig
import com.ayse.aroundyou.model.response.PlacesResponse
import retrofit2.http.GET
import retrofit2.http.Query
interface PlacesApi {


    //  Yakınlardaki yerleri almak için
    @GET("place/nearbysearch/json")
    suspend fun getNearbyPlaces(
        @Query("location") location: String, // "lat,lng" kullanııc koordinatı
        @Query("radius") radius: Int, //kaç metre yakınındaki yerler
        @Query("keyword") keyword: String,//arama kriteri örn hospital veya hastane her ikisindede aynı cevabı verir
        @Query("key") apiKey: String
    ): PlacesResponse

    //seçilen konumun detayları
    @GET("place/details/json")
    suspend fun  getPlaceDetails(
        @Query("place_id") placeId: String,
        @Query("fields") fields: String = "name,rating,formatted_address,photos,reviews,geometry",
        @Query("key") apiKey: String
    ) : PlacesResponse

    @GET("maps/api/place/textsearch/json")
    suspend fun searchPlaces(
        @Query("query") query: String,
        @Query("location") location: String,
        @Query("radius") radius: Int = 2000,
        @Query("key") apiKey: String = BuildConfig.MAPS_API_KEY
    ): PlacesResponse

    @GET("place/textsearch/json")
    suspend fun getNearby(
        @Query("query") query: String,
        @Query("key") apiKey: String = BuildConfig.MAPS_API_KEY
    ):PlacesResponse


        @GET("maps/api/place/nearbysearch/json")
        suspend fun getPlaces(
            @Query("location") location: String,      // ör: "41.0082,28.9784"
            @Query("radius") radius: Int ,      // metre cinsinden
            @Query("type") type: String = "restaurant", // opsiyonel: restoran, kafe, park vs
            @Query("key") apiKey: String = BuildConfig.MAPS_API_KEY
        ): PlacesResponse


}