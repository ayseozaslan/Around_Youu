package com.ayse.aroundyou.viewmodel

import androidx.lifecycle.ViewModel
import com.ayse.aroundyou.data.preferences.FavoriteManager
import com.ayse.aroundyou.model.response.PlaceItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val favoriteManager: FavoriteManager
) : ViewModel() {

    private val _favoritePlacesItems = MutableStateFlow<List<PlaceItem>>(emptyList())
    val favoritePlacesItems: StateFlow<List<PlaceItem>> = _favoritePlacesItems.asStateFlow()

    init {
        loadFavorites()
    }

    fun loadFavorites() {
        _favoritePlacesItems.value = favoriteManager.getFavorites()
    }

    fun toggleFavorite(place: PlaceItem) {
        if (favoriteManager.isFavorite(place)) {
            favoriteManager.removeFavorite(place)
        } else {
            favoriteManager.addFavorite(place)
        }
        loadFavorites() // UI güncelle
    }

    fun isFavorite(place: PlaceItem): Boolean {
        return favoriteManager.isFavorite(place)
    }
}


/*
@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val favoriteManager: FavoriteManager
) : ViewModel() {

    // StateFlow ile favori ID’leri tutuyoruz

    private val _placesItems = MutableStateFlow<ApiResult<List<PlaceItem>>>(ApiResult.Loading)
    val placesItems: StateFlow<ApiResult<List<PlaceItem>>> = _placesItems

    private val _favoritePlacesItems=MutableStateFlow<List<PlaceItem>>(emptyList())
    val favoritePlacesItems:StateFlow<List<PlaceItem>> = _favoritePlacesItems

    private val _placesDetail = MutableStateFlow<ApiResult<PlaceItem>>(ApiResult.Loading)
    val placesDetail: StateFlow<ApiResult<PlaceItem>> = _placesDetail

    // Mevcut mekanlar (API'den gelen) buradan geçiyor
    private val _allPlaces = MutableStateFlow<List<PlaceItem>>(emptyList())
    val allPlaces: StateFlow<List<PlaceItem>> = _allPlaces.asStateFlow()
    /*
    private val _favoriteIds = MutableStateFlow<Set<String>>(favoriteManager.getFavorites())
    val favoriteIds: StateFlow<Set<String>> = _favoriteIds
     */
    private val _favoriteIds = MutableStateFlow<List<String>>(emptyList())
    val favoriteIds = _favoriteIds.asStateFlow()


    init {
        loadFavorites() // ViewModel başladığında tüm mekanları yükle
    }
/*
    fun isFavorite(placeId: String): Boolean {
        // 🔹 UI güncellemesini tetikleyecek StateFlow'u baz alıyoruz
        return _favoriteIds.value.contains(placeId)
    }
 */
fun isFavorite(placesId: String): Boolean {
    return favoriteManager.isFavorite(placesId)
}


    fun toggleFavorite(place: PlaceItem) {
        Log.d("FavoriteViewModel", "toggleFavorite called for: $place.placeId")
        Log.d("FavoriteViewModel", "_favoriteIds before toggle: ${_favoriteIds.value}")
        if (_favoriteIds.value.contains(place.placeId)) {
            favoriteManager.removeFavorite(place.placeId)
        } else {
            favoriteManager.addFavorite(place)
        }
        // 🔹 anlık update
        _favoriteIds.value = favoriteManager.getFavorites().toList()
        Log.d("FavoriteViewModel", "_favoriteIds updated after toggle: ${_favoriteIds.value}")
    }

    // Tüm mekanları API'den yükleyen yeni fonksiyon
    fun loadFavorites() {
        _favoriteIds.value = favoriteManager.getFavorites().toList()
    }
    /*
     fun loadFavorites() {
        viewModelScope.launch {
            val favoriteIds = favoriteManager.getFavorites()
            Log.d("ViewModel", "Loaded favorite IDs: $favoriteIds")

            val placesItemsResult = _placesItems.value
            if (placesItemsResult is ApiResult.Success) {
                val favoriteNews = placesItemsResult.data.filter { it.id in favoriteIds }
                _favoritePlacesItems.value = favoriteNews
                Log.d("ViewModel", "Favorite news: $favoriteNews")
            } else {
                _favoritePlacesItems.value = emptyList()
            }
        }
    }

     */


}


 */