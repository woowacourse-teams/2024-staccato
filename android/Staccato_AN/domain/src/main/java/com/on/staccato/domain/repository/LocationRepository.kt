package com.on.staccato.domain.repository

interface LocationRepository {
    fun getCurrentLocation(onSuccess: (Double, Double) -> Unit)
}
