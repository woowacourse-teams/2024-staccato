package com.on.staccato.presentation.map.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.on.staccato.data.onException
import com.on.staccato.data.onServerError
import com.on.staccato.data.onSuccess
import com.on.staccato.domain.model.StaccatoLocation
import com.on.staccato.domain.repository.LocationRepository
import com.on.staccato.domain.repository.StaccatoRepository
import com.on.staccato.presentation.common.MutableSingleLiveData
import com.on.staccato.presentation.common.SingleLiveData
import com.on.staccato.presentation.map.model.LocationUiModel
import com.on.staccato.presentation.map.model.MarkerUiModel
import com.on.staccato.presentation.util.ExceptionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapsViewModel
    @Inject
    constructor(
        private val staccatoRepository: StaccatoRepository,
        private val locationRepository: LocationRepository,
    ) : ViewModel() {
        private val staccatoLocations = MutableLiveData<List<StaccatoLocation>>()
        private val markerUiModels = MutableLiveData<List<MarkerUiModel>>()

        private val _errorMessage = MutableSingleLiveData<String>()
        val errorMessage: SingleLiveData<String> get() = _errorMessage

        private val _staccatoId = MutableLiveData<Long>()
        val staccatoId: LiveData<Long> get() = _staccatoId

        private val _focusLocation = MutableLiveData<LocationUiModel>()
        val focusLocation: LiveData<LocationUiModel> get() = _focusLocation

        private val _markersOptions = MutableLiveData<List<MarkerOptions>>()
        val markersOptions: LiveData<List<MarkerOptions>> get() = _markersOptions

        fun getCurrentLocation() {
            val currentLocation = locationRepository.getCurrentLocation()
            currentLocation.addOnSuccessListener {
                _focusLocation.value = LocationUiModel(it.latitude, it.longitude)
            }
        }

        fun updateFocusLocation(
            latitude: Double,
            longitude: Double,
        ) {
            _focusLocation.value = LocationUiModel(latitude, longitude)
        }

        fun findStaccatoId(markerId: String?) {
            _staccatoId.value =
                markerUiModels.value?.first {
                    it.markerId == markerId
                }?.staccatoId ?: throw NoSuchElementException("marker id와 일치하는 스타카토가 없습니다.")
        }

        fun loadStaccatos() {
            viewModelScope.launch {
                val result = staccatoRepository.getStaccatos()
                result.onSuccess(::setStaccatoLocations)
                    .onServerError(::handleServerError)
                    .onException(::handelException)
            }
        }

        fun updateMarkers(markers: List<Marker>) {
            staccatoLocations.value?.let { locations ->
                markerUiModels.value =
                    locations.zip(markers) { location, marker ->
                        MarkerUiModel(location.staccatoId, marker.id)
                    }
            }
        }

        private fun setStaccatoLocations(locations: List<StaccatoLocation>) {
            staccatoLocations.value = locations
            _markersOptions.value =
                staccatoLocations.value?.map {
                    val latLng = LatLng(it.latitude, it.longitude)
                    val markerOptions: MarkerOptions = MarkerOptions().position(latLng)
                    markerOptions
                }
        }

        private fun handleServerError(message: String) {
            _errorMessage.setValue(message)
        }

        private fun handelException(state: ExceptionState) {
            _errorMessage.setValue(state.message)
        }
    }
