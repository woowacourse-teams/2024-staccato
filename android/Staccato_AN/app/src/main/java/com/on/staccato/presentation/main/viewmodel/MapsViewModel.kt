package com.on.staccato.presentation.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.staccato.data.onException
import com.on.staccato.data.onServerError
import com.on.staccato.data.onSuccess
import com.on.staccato.domain.model.StaccatoLocation
import com.on.staccato.domain.repository.StaccatoRepository
import com.on.staccato.presentation.common.MutableSingleLiveData
import com.on.staccato.presentation.common.SingleLiveData
import com.on.staccato.presentation.main.model.MarkerUiModel
import com.on.staccato.presentation.map.LocationUiModel
import com.on.staccato.presentation.util.ExceptionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapsViewModel
    @Inject
    constructor(
        private val staccatoRepository: StaccatoRepository,
    ) : ViewModel() {
        private val _staccatoLocations = MutableLiveData<List<StaccatoLocation>>()
        val staccatoLocations: LiveData<List<StaccatoLocation>> get() = _staccatoLocations

        private val _markers = MutableLiveData<List<MarkerUiModel>>()
        val markers: LiveData<List<MarkerUiModel>> get() = _markers

        private val _errorMessage = MutableSingleLiveData<String>()
        val errorMessage: SingleLiveData<String> get() = _errorMessage

        private val _staccatoId = MutableLiveData<Long>()
        val staccatoId: LiveData<Long> get() = _staccatoId

        private val _currentLocation = MutableLiveData<LocationUiModel>()
        val currentLocation: LiveData<LocationUiModel> get() = _currentLocation

        fun setCurrentLocation(
            latitude: Double,
            longitude: Double,
        ) {
            _currentLocation.value = LocationUiModel(latitude, longitude)
        }

        fun setMarkers(markers: List<MarkerUiModel>) {
            _markers.value = markers
        }

        fun findStaccatoId(markerId: String?) {
            val markers = _markers.value ?: throw IllegalArgumentException()
            _staccatoId.value = markers.first { it.markerId == markerId }.staccatoId
        }

        fun loadStaccatos() {
            viewModelScope.launch {
                val result = staccatoRepository.getStaccatos()
                result.onSuccess(::setStaccatoLocations)
                    .onServerError(::handleServerError)
                    .onException(::handelException)
            }
        }

        private fun setStaccatoLocations(staccatoLocations: List<StaccatoLocation>) {
            _staccatoLocations.value = staccatoLocations
        }

        private fun handleServerError(message: String) {
            _errorMessage.setValue(message)
        }

        private fun handelException(state: ExceptionState) {
            _errorMessage.setValue(state.message)
        }
    }
