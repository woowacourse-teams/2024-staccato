package com.on.staccato.presentation.staccato.feeling

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.on.staccato.domain.model.Feeling
import com.on.staccato.domain.repository.StaccatoRepository
import com.on.staccato.presentation.mapper.toFeelingUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StaccatoFeelingSelectionViewModel
    @Inject
    constructor(
        private val staccatoRepository: StaccatoRepository,
    ) : ViewModel(), FeelingHandler {
        private val initialFeelings: List<Feeling> =
            Feeling.entries.filterNot { it.value == Feeling.NOTHING.value }

        private val _currentFeeling = MutableLiveData(Feeling.NOTHING)
        val currentFeeling: LiveData<Feeling> get() = _currentFeeling

        private val _feelings = MutableLiveData<List<FeelingUiModel>>()
        val feelings: LiveData<List<FeelingUiModel>> get() = _feelings

        private var momentId: Long = -1L

        override fun onFeelingClicked(selectedFeeling: FeelingUiModel) {
            checkFeelingBeforeChange(selectedFeeling)
        }

        fun setMomentId(id: Long) {
            momentId = id
        }

        fun setFeelings(selectedFeeling: Feeling) {
            val newFeelings =
                initialFeelings.map { feeling ->
                    feeling.toFeelingUiModel(selectedFeeling.value)
                }
            _feelings.value = newFeelings
            _currentFeeling.value = Feeling.fromValue(selectedFeeling.value)
        }

        private fun checkFeelingBeforeChange(selectedFeeling: FeelingUiModel) {
            val feelingToChange = getFeelingToChange(selectedFeeling)
            setFeelings(feelingToChange)
            requestChangingFeeling(feelingToChange)
        }

        private fun getFeelingToChange(selectedFeeling: FeelingUiModel): Feeling {
            return if (currentFeeling.value?.value == selectedFeeling.feeling) {
                Feeling.NOTHING
            } else {
                Feeling.fromValue(selectedFeeling.feeling)
            }
        }

        private fun requestChangingFeeling(newFeeling: Feeling) {
            viewModelScope.launch {
                staccatoRepository.updateFeeling(momentId, newFeeling.value)
            }
        }
    }
