package com.on.staccato.presentation.invitation.component

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import com.on.staccato.presentation.component.topbar.DefaultNavigationTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvitationManagementTopBar(onNavigationClick: () -> Unit) {
    DefaultNavigationTopBar(
        title = "카테고리 초대 관리",
        onNavigationClick = onNavigationClick,
    )
}
