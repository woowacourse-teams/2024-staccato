package com.on.staccato.presentation.common.photo.originalphoto

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.staccato.presentation.component.topbar.DefaultNavigationTopBar
import com.on.staccato.presentation.component.topbar.DefaultTopAppBarColors
import com.on.staccato.theme.TranslucentBlack
import com.on.staccato.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OriginalPhotoTopBar(
    isVisible: Boolean,
    title: String? = null,
    onNavigationClick: () -> Unit,
) {
    AnimatedVisibility(
        visible = isVisible,
        enter =
            slideInVertically(
                initialOffsetY = { -it },
                animationSpec = tween(durationMillis = 300),
            ),
        exit =
            slideOutVertically(
                targetOffsetY = { -it },
                animationSpec = tween(durationMillis = 300),
            ),
    ) {
        DefaultNavigationTopBar(
            title = title,
            onNavigationClick = onNavigationClick,
            colors =
                DefaultTopAppBarColors.copy(
                    containerColor = TranslucentBlack,
                    navigationIconContentColor = White,
                    titleContentColor = White,
                ),
        )
    }
}

@Preview(name = "제목이 있는 경우", showBackground = true, backgroundColor = 0xFFFFFF)
@Composable
private fun OriginalPhotoTopBarWithTitlePreview() {
    Column {
        OriginalPhotoTopBar(
            isVisible = true,
            title = "반투명한 상단 바",
            onNavigationClick = {},
        )
        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Preview(name = "제목이 없는 경우", showBackground = true, backgroundColor = 0xFFFFFF)
@Composable
private fun OriginalPhotoTopBarWithoutTitlePreview() {
    Column {
        OriginalPhotoTopBar(
            isVisible = true,
            onNavigationClick = {},
        )
        Spacer(modifier = Modifier.height(30.dp))
    }
}
