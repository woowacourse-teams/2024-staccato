package com.on.staccato.presentation.categoryselection

import com.on.staccato.domain.model.CategoryCandidate
import com.on.staccato.domain.model.CategoryCandidates
import kotlinx.coroutines.flow.StateFlow

interface CategorySelectionViewModel {
    val selectableCategories: StateFlow<CategoryCandidates>
    val selectedCategory: StateFlow<CategoryCandidate?>

    fun selectCategory(position: Int)
}
