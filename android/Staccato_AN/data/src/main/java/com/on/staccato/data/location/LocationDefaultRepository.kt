package com.on.staccato.data.location

import com.on.staccato.domain.repository.LocationRepository
import javax.inject.Inject

class LocationDefaultRepository
    @Inject
    constructor(
        private val locationDataSource: LocationDataSource,
    ) : LocationRepository {
        override fun getCurrentLocation(onSuccess: (Double, Double) -> Unit) {
            locationDataSource.getCurrentLocation().addOnSuccessListener {
                onSuccess(it.latitude, it.longitude)
            }
        }
    }
