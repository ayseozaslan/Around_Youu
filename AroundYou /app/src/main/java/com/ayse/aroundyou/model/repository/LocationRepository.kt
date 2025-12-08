package com.ayse.aroundyou.model.repository

import android.content.Context
import com.ayse.aroundyou.model.entities.Country
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class LocationRepository   @Inject constructor (
    @ApplicationContext private val context:Context) {

    fun getCountries() :List<Country> {
        return try {
            val jsonString= context.assets.open("locations.json")
                .bufferedReader()
                .use { it.readText() }
            val gson= Gson()
            gson.fromJson(jsonString, Array<Country> ::class.java).toList()
        } catch (e:Exception){
            e.printStackTrace()
            emptyList()
        }
    }
}