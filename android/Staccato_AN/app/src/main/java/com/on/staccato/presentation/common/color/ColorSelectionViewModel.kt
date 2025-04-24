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

        fun selectCategoryColor(color: CategoryColor) {
            _selectedColor.value = color
        }
    }
