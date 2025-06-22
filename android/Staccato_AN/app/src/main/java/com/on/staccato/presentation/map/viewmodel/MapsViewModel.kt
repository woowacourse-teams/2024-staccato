package com.on.staccato.presentation.map.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.staccato.data.network.onException2
import com.on.staccato.data.network.onServerError
import com.on.staccato.data.network.onSuccess
import com.on.staccato.domain.model.StaccatoMarker
import com.on.staccato.domain.repository.LocationRepository
import com.on.staccato.domain.repository.StaccatoRepository
import com.on.staccato.presentation.common.MutableSingleLiveData
import com.on.staccato.presentation.common.SingleLiveData
import com.on.staccato.presentation.map.model.LocationUiModel
import com.on.staccato.presentation.map.model.StaccatoMarkerUiModel
import com.on.staccato.presentation.mapper.toUiModel
import com.on.staccato.presentation.util.ExceptionState2
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapsViewModel
    @Inject
    constructor(
        private val staccatoRepository: StaccatoRepository,
        private val locationRepository: LocationRepository,
    ) : ViewModel() {
        private val _staccatoMarkers = MutableLiveData<List<StaccatoMarkerUiModel>>()
        val staccatoMarkers: LiveData<List<StaccatoMarkerUiModel>> get() = _staccatoMarkers

        private val _clusterStaccatoMarkers = MutableStateFlow<List<StaccatoMarkerUiModel>>(emptyList())
        val clusterStaccatoMarkers: StateFlow<List<StaccatoMarkerUiModel>> = _clusterStaccatoMarkers.asStateFlow()

        private var _isClusterMode = MutableStateFlow(false)
        val isClusterMode: StateFlow<Boolean> = _isClusterMode.asStateFlow()

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

        fun loadStaccatoMarkers() {
            viewModelScope.launch {
                val result = staccatoRepository.getStaccatoMarkers()
                result.onSuccess(::updateStaccatoMarkers)
                    .onServerError(::handleServerError)
                    .onException2(::handleException)
            }
        }

        fun switchClusterMode(
            isClusterMode: Boolean,
            markers: List<StaccatoMarkerUiModel>? = null,
        ) {
            viewModelScope.launch {
                _isClusterMode.emit(isClusterMode)
                updateClusterStaccatoMarkers(isClusterMode, markers)
            }
        }

        private suspend fun updateClusterStaccatoMarkers(
            isClusterMode: Boolean,
            markers: List<StaccatoMarkerUiModel>?,
        ) {
            if (isClusterMode && markers != null) {
                val sortedMarkers = markers.sortedByDescending { it.visitedAt }
                _clusterStaccatoMarkers.emit(sortedMarkers)
            } else {
                _clusterStaccatoMarkers.emit(emptyList())
            }
        }

        private fun updateStaccatoMarkers(markers: List<StaccatoMarker>) {
            _staccatoMarkers.value = markers.map { it.toUiModel() }
        }

        private fun handleServerError(message: String) {
            _errorMessage.setValue(message)
        }

        private fun handleException(state: ExceptionState2) {
            _exception.setValue(state)
        }
    }
