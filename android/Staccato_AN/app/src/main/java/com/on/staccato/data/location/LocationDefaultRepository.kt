package com.on.staccato.data.location

import android.location.Location
import com.google.android.gms.tasks.Task
import com.on.staccato.domain.repository.LocationRepository
import javax.inject.Inject

class LocationDefaultRepository
    @Inject
    constructor(
        private val locationDataSource: LocationDataSource,
    ) : LocationRepository {
        override fun getCurrentLocation(): Task<Location> = locationDataSource.getCurrentLocation()
    }
