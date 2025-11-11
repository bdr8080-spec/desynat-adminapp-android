package com.desynatadmin.ui.home.brandManagement.response

import com.google.gson.annotations.SerializedName

data class PlacePrediction(
    val description: String,
    val place_id: String
)

data class PlacesResponse(
    val predictions: ArrayList<PlacePrediction>?,
    val status: String
)



data class LocationLatLng(
    @SerializedName("lat") val lat: Double?,
    @SerializedName("lng") val lng: Double?
)

// OSM Overpass Response
data class OSMResponse(
    val elements: List<OSMElement>
)

data class OSMElement(
    val type: String,
    val id: Long,
    val lat: Double?,
    val lon: Double?,
    val nodes: List<Long>?,
    val tags: Map<String, String>?,
    val geometry: List<OSMNode>?
)

data class OSMNode(
    val lat: Double,
    val lon: Double
)


data class PlaceDetailsResponse(
    @SerializedName("result") val result: PlaceResult?,
    @SerializedName("status") val status: String?
)

data class PlaceResult(
    @SerializedName("name") val name: String?,
    @SerializedName("geometry") val geometry: Geometry?,
    @SerializedName("address_components") val addressComponents: List<AddressComponent>?
)

data class Geometry(
    @SerializedName("location") val location: LocationData?
)

data class LocationData(
    @SerializedName("lat") val lat: Double?,
    @SerializedName("lng") val lng: Double?
)

data class AddressComponent(
    @SerializedName("long_name") val long_name: String?,
    @SerializedName("short_name") val short_name: String?,
    @SerializedName("types") val types: List<String>
)