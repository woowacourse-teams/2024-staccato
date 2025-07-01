package com.on.staccato.presentation.place

interface GooglePlaceFragmentEventHandler {
    fun onNewPlaceSelected(
        id: String,
        name: String,
        address: String,
        longitude: Double,
        latitude: Double,
    )

    fun onSelectedPlaceCleared()
}
