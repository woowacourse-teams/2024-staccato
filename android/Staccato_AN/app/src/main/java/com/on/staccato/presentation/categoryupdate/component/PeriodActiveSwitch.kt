package com.on.staccato.presentation.categoryupdate.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.on.staccato.presentation.categoryupdate.viewmodel.CategoryUpdateViewModel
import com.on.staccato.presentation.component.DefaultSwitch

@Composable
fun PeriodActiveSwitch(viewModel: CategoryUpdateViewModel = hiltViewModel()) {
    val isPeriodActive by viewModel.isPeriodActive.collectAsState()
    DefaultSwitch(
        checked = isPeriodActive,
    ) { viewModel.updateIsPeriodActive(it) }
}
