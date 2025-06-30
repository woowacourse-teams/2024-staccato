package com.on.staccato.presentation.common.categoryselection

import androidx.lifecycle.LiveData
import com.on.staccato.domain.model.CategoryCandidate
import com.on.staccato.domain.model.CategoryCandidates

interface CategorySelectionViewModel {
    val selectableCategories: LiveData<CategoryCandidates>
    val selectedCategory: LiveData<CategoryCandidate>

    fun selectCategory(position: Int)
}
