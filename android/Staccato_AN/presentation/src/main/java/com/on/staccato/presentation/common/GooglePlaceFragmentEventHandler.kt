package com.on.staccato.presentation.common

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
