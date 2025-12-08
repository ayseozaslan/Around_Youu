package com.ayse.aroundyou.viewmodel


import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ayse.aroundyou.model.entities.Country
import com.ayse.aroundyou.model.repository.PlacesRepository
import com.ayse.aroundyou.model.response.MyLocation
import com.ayse.aroundyou.model.response.PlaceItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.ayse.aroundyou.model.entities.Region as MyRegion


@HiltViewModel
class MapViewModel @Inject constructor(
    private val repository: PlacesRepository
) : ViewModel() {

    //kullanıcının konumu

    private val _userLocation = MutableLiveData<MyLocation>()
    val userLocation: LiveData<MyLocation> = _userLocation

    //Yakınlardaki yerler
    private val _placesMapSearch = MutableStateFlow<List<PlaceItem>>(emptyList())
    val placesMapSearch = _placesMapSearch.asStateFlow()

    //seçilen yer
    private val _selectedPlace = MutableLiveData<PlaceItem?>()
    val selectedPlace: LiveData<PlaceItem?> = _selectedPlace

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    var selectedCountry by mutableStateOf<Country?>(null)
    var selectedRegion by mutableStateOf<MyRegion?>(null)
    var selectedCity by mutableStateOf<String?>(null)
    var selectedCategories by mutableStateOf<List<String>>(emptyList())

    var placesMap = mutableStateListOf<PlaceItem>()           // Yakın mekanlar
    var searchQuery by mutableStateOf("")                // SearchBar query



    fun applyFilters(
        country: Country?,
        region: MyRegion?,
        city: String?,
        categories: List<String>
    ) {
        selectedCountry = country
        selectedRegion = region
        selectedCity = city
        selectedCategories = categories
        fetchPlaces()
    }

/*
    fun loadPlaces( apiKey:String){
        val location = userLocation.value ?: return// return => userLocation.value null değilse, onu location değişkenine ata.

        viewModelScope.launch {
            val list = repository.getCocktailPlaces(location.lat, location.lng,apiKey)
            _placesMapSearch.value = list
        }
    }

 */
fun loadPlaces(query: String) {
    val location = userLocation.value ?: return// return => userLocation.value null değilse, onu location değişkenine ata.

    viewModelScope.launch {
        val result = repository.getCocktailPlaces(
            location.lat, location.lng, query
        )
        _placesMapSearch.value = result
    }
}


    private fun fetchPlaces() {
        val cityName = selectedCity ?: return
        val type = selectedCategories.firstOrNull() ?: "restaurant"

        viewModelScope.launch {
            isLoading = true
            try {
                val response = repository.getNearby(cityName, type)
               // placess = response.results ?: emptyList()
                _placesMapSearch.value = response.results ?: emptyList()  // ✔ UI otomatik güncellenir

                errorMessage = null
            } catch (e: Exception) {
                errorMessage = e.localizedMessage
            } finally {
                isLoading = false
            }
        }
    }


    //konumu viewmodel da set et.
    fun setUserLocation(location: MyLocation) {
        _userLocation.value = location
        fetchNearbyPlaces(location.lat ?: 0.0, location.lng ?: 0.0)
    }

    // Kullanıcı text'i yazdıkça güncellenen fonksiyon
    fun updateSearchQuery(newValue: String){
        searchQuery= newValue
        //  otomatik arama için searchPlaces(newValue)
    }

    // SearchBar araması
    fun searchPlaces(query: String) {
        Log.d("MapViewModel", "searchPlaces çağrıldı. query=$query")

        val location = userLocation.value ?: return// return => userLocation.value null değilse, onu location değişkenine ata.
        //null ise, fonksiyondan  çık
        viewModelScope.launch {
            val results = repository.getNearbyPlacesSearch(location.lat, location.lng, query)
            Log.d("MapViewModel", "ViewModel'e dönen sonuç sayısı: ${results.size}")
            _placesMapSearch.value = results
        }
    }
    //
    fun searchPlacesFeed(query: String) {
        Log.d("SEARCH_FEED", "Fonksiyon çağrıldı, query = $query")

        val location = userLocation.value ?: return// return => userLocation.value null değilse, onu location değişkenine ata.
        //null ise, fonksiyondan  çık
        Log.d("SEARCH_FEED", "Konum durumu: $location")

        viewModelScope.launch {
            val results = repository.getNearbyPlacesSearch(location.lat, location.lng, query)
            Log.d("SEARCH_FEED", "Gelen sonuç sayısı: ${results.size}")
            _placesMapSearch.value = results
            Log.e("SEARCH_FEED", "Konum NULL → arama yapılamadı!")

        }
    }

    //Repository üzerinden mekanları çekme
    private fun fetchNearbyPlaces(lat: Double, lng: Double) {
        viewModelScope.launch {
            try {
                val response = repository.getNearbyPlaces(lat, lng, "restaurant")
                _placesMapSearch.value = response.results
               // _places.value = response.results
            } catch (e: Exception) {
                Log.e("ViewModel", "Error fetching places: ${e.localizedMessage}")
            }
        }
    }


    fun markerClick(place: PlaceItem) {
        _selectedPlace.value = place
    }


}
