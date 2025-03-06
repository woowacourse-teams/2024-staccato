package com.on.staccato.data.location

import android.location.Location
import com.google.android.gms.tasks.Task

interface LocationDataSource {
    fun getCurrentLocation(): Task<Location>
}
