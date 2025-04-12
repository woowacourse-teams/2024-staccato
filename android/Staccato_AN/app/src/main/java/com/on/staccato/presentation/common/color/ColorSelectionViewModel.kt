package com.on.staccato.presentation.common.color

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ColorSelectionViewModel
    @Inject
    constructor() : ViewModel() {
        private val _selectedColor = MutableLiveData<CategoryColor>()
        val selectedColor: LiveData<CategoryColor> get() = _selectedColor

        private val _colorSelectionResult = MutableLiveData<CategoryColorChangeEvent>()
        val colorSelectionResult: LiveData<CategoryColorChangeEvent> get() = _colorSelectionResult

        private val _categoryId = MutableLiveData<Long>()

        fun setCategoryId(id: Long) {
            _categoryId.value = id
        }

        fun selectCategoryColor(color: CategoryColor) {
            _selectedColor.value = color
        }

        fun changeCategoryColor() {
            _colorSelectionResult.value =
                CategoryColorChangeEvent(
                    _categoryId.value ?: return,
                    _selectedColor.value ?: return,
                )
        }
    }
