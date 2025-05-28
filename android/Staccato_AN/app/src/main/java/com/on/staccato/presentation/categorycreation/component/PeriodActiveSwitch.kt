package com.on.staccato.presentation.categorycreation.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.on.staccato.presentation.categorycreation.viewmodel.CategoryCreationViewModel
import com.on.staccato.presentation.component.DefaultSwitch

// TODO: 카테고리 생성 전체 UI Compose로 마이그레이션 후 Screen으로 이동 예정
@Composable
fun PeriodActiveSwitch(viewModel: CategoryCreationViewModel = hiltViewModel()) {
    val isPeriodActive by viewModel.isPeriodActive.collectAsState()
    DefaultSwitch(
        checked = isPeriodActive,
    ) { viewModel.updateIsPeriodActive(it) }
}
