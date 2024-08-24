package com.example.toiletfinder


data class Location(
    val latitude: Double,
    val longitude: Double
)

data class SearchResult(
    val title: String,
    val description: String,
    val location: Location
)

data class SearchResponse(
    val results: List<SearchResult>
)
