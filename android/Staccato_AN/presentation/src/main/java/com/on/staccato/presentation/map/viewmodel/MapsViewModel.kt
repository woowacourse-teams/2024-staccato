package com.on.staccato.presentation.map.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.staccato.domain.model.StaccatoMarker
import com.on.staccato.domain.onException
import com.on.staccato.domain.onServerError
import com.on.staccato.domain.onSuccess
import com.on.staccato.domain.repository.LocationRepository
import com.on.staccato.domain.repository.StaccatoRepository
import com.on.staccato.presentation.common.MessageEvent
import com.on.staccato.presentation.common.event.MutableSingleLiveData
import com.on.staccato.presentation.common.event.SingleLiveData
import com.on.staccato.presentation.map.model.LocationUiModel
import com.on.staccato.presentation.map.model.StaccatoMarkerUiModel
import com.on.staccato.presentation.mapper.toUiModel
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

        private val _messageEvent = MutableSingleLiveData<MessageEvent>()
        val messageEvent: SingleLiveData<MessageEvent> get() = _messageEvent

        private val _staccatoId = MutableLiveData<Long>()
        val staccatoId: LiveData<Long> get() = _staccatoId

        private val _focusLocation = MutableLiveData<LocationUiModel>()
        val focusLocation: LiveData<LocationUiModel> get() = _focusLocation

        fun getCurrentLocation() {
            locationRepository.getCurrentLocation { latitude, longitude ->
                _focusLocation.value = LocationUiModel(latitude, longitude)
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
                    .onServerError { updateMessageEvent(MessageEvent.from(it)) }
                    .onException { updateMessageEvent(MessageEvent.from(it)) }
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

        private fun updateMessageEvent(messageEvent: MessageEvent) {
            _messageEvent.setValue(messageEvent)
        }
    }
