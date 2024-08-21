package com.woowacourse.staccato.presentation.moment.feeling

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.woowacourse.staccato.domain.model.Feeling
import com.woowacourse.staccato.domain.repository.MomentRepository
import com.woowacourse.staccato.presentation.mapper.toFeelingUiModel
import kotlinx.coroutines.launch

class MomentFeelingSelectionViewModel(
    private val momentId: Long,
    private val momentRepository: MomentRepository,
) : ViewModel(), FeelingHandler {
    private val initialFeelings: List<Feeling> =
        Feeling.entries.filterNot { it.value == Feeling.NOTHING.value }

    private val _currentFeeling = MutableLiveData(Feeling.NOTHING)
    val currentFeeling: LiveData<Feeling> get() = _currentFeeling

    private val _feelings = MutableLiveData<List<FeelingUiModel>>()
    val feelings: LiveData<List<FeelingUiModel>> get() = _feelings

    override fun onFeelingClicked(selectedFeeling: FeelingUiModel) {
        checkFeelingBeforeChange(selectedFeeling)
    }

    fun setFeelings(selectedFeeling: Feeling) {
        val newFeelings = initialFeelings.map { feeling ->
            feeling.toFeelingUiModel(selectedFeeling.value)
        }
        _feelings.value = newFeelings
        _currentFeeling.value = Feeling.fromValue(selectedFeeling.value)
    }

    fun checkFeelingBeforeChange(selectedFeeling: FeelingUiModel) {
        val feelingToChange = getFeelingToChange(selectedFeeling)
        setFeelings(feelingToChange)
        requestChangingFeeling(feelingToChange)
    }

    fun getFeelingToChange(selectedFeeling: FeelingUiModel): Feeling {
        return if (currentFeeling.value?.value == selectedFeeling.feeling) {
            Feeling.NOTHING
        } else {
            Feeling.fromValue(selectedFeeling.feeling)
        }
    }

    fun requestChangingFeeling(newFeeling: Feeling) {
        viewModelScope.launch {
            // 기분 변경 요청
            momentRepository.updateFeeling(momentId, newFeeling.value)
        }
    }
}
