package com.on.staccato.domain.repository

import android.location.Location
import com.google.android.gms.tasks.Task

interface LocationRepository {
    fun getCurrentLocation(): Task<Location>
}
