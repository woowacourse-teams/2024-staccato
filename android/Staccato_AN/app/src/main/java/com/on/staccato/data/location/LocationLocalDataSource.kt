package com.on.staccato.data.location

import android.annotation.SuppressLint
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Task
import javax.inject.Inject

class LocationLocalDataSource
    @Inject
    constructor(
        private val fusedLocationProviderClient: FusedLocationProviderClient,
    ) : LocationDataSource {
        @SuppressLint("MissingPermission")
        override fun getCurrentLocation(): Task<Location> {
            return fusedLocationProviderClient.getCurrentLocation(
                PRIORITY_HIGH_ACCURACY,
                CancellationTokenSource().token,
            )
        }
    }
