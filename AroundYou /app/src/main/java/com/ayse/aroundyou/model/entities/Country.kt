package com.ayse.aroundyou.model.entities

data class Country(
     val name: String,
    val regions : List<Region>
)

data class Region(
    val name: String,
    val cities: List<String>
)
