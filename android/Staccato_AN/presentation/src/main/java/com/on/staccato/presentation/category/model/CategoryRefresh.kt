package com.on.staccato.presentation.category.model

sealed interface CategoryRefresh {
    data object None : CategoryRefresh

    data object All : CategoryRefresh
}
