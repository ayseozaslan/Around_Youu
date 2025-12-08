package com.ayse.aroundyou.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ayse.aroundyou.data.api.PlacesApi
import com.ayse.aroundyou.data.preferences.FavoriteManager
import com.ayse.aroundyou.model.entities.Country
import com.ayse.aroundyou.model.entities.Region
import com.ayse.aroundyou.model.repository.LocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val repository: LocationRepository,
    private val api:PlacesApi,
    private val favoriteManager : FavoriteManager
) : ViewModel() {

    private val _countries = mutableStateOf<List<Country>>(emptyList())
    val countries: State<List<Country>> = _countries

    private val _selectedCountry = mutableStateOf<Country?>(null)
    val selectedCountry: State<Country?> = _selectedCountry

    private val _selectedRegion = mutableStateOf<Region?>(null)
    val selectedRegion: State<Region?> = _selectedRegion

    private val _selectedCity = mutableStateOf<String?>(null)
    val selectedCity: State<String?> = _selectedCity



    init {
        loadCountries()
    }

    private fun loadCountries() {
        viewModelScope.launch {
            val list = repository.getCountries()
            _countries.value = list
            Log.d("LocationVM", "Countries loaded: $list") // ✅ Log

        }
    }

    fun selectCountry(country: Country) {
        _selectedCountry.value = country
        _selectedRegion.value = null
        _selectedCity.value = null
    }

    fun selectRegion(region: Region) {
        _selectedRegion.value = region
        _selectedCity.value = null
    }

    fun selectCity(city: String) {
        _selectedCity.value = city
    }


}
