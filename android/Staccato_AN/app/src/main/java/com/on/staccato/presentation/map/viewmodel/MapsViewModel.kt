package com.on.staccato.presentation.map.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.staccato.data.network.onException2
import com.on.staccato.data.network.onServerError
import com.on.staccato.data.network.onSuccess
import com.on.staccato.domain.model.StaccatoLocation
import com.on.staccato.domain.repository.LocationRepository
import com.on.staccato.domain.repository.StaccatoRepository
import com.on.staccato.presentation.common.MutableSingleLiveData
import com.on.staccato.presentation.common.SingleLiveData
import com.on.staccato.presentation.map.model.LocationUiModel
import com.on.staccato.presentation.map.model.MarkerUiModel
import com.on.staccato.presentation.util.ExceptionState2
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
        private val _staccatoLocations = MutableLiveData<List<StaccatoLocation>>()
        val staccatoLocations: LiveData<List<StaccatoLocation>> get() = _staccatoLocations

        private val markerUiModels = MutableLiveData<List<MarkerUiModel>>()

        private val _errorMessage = MutableSingleLiveData<String>()
        val errorMessage: SingleLiveData<String> get() = _errorMessage

        private val _staccatoId = MutableLiveData<Long>()
        val staccatoId: LiveData<Long> get() = _staccatoId

        private val _focusLocation = MutableLiveData<LocationUiModel>()
        val focusLocation: LiveData<LocationUiModel> get() = _focusLocation

        private val _exception = MutableSingleLiveData<ExceptionState2>()
        val exception: SingleLiveData<ExceptionState2> get() = _exception

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
                    .onException2(::handelException)
            }
        }

        fun updateMarkers(
            markerIds: List<String>,
            staccatoIds: List<Long>,
        ) {
            markerUiModels.value =
                markerIds.zip(staccatoIds) { markerId, staccatoId ->
                    MarkerUiModel(
                        staccatoId = staccatoId,
                        markerId = markerId,
                    )
                }
        }

        private fun setStaccatoLocations(locations: List<StaccatoLocation>) {
            _staccatoLocations.value = locations
        }

        private fun handleServerError(message: String) {
            _errorMessage.setValue(message)
        }

        private fun handelException(state: ExceptionState2) {
            _exception.setValue(state)
        }
    }
