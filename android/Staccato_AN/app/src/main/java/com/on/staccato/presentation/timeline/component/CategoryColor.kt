package com.on.staccato.presentation.timeline.component

import androidx.annotation.ColorRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.on.staccato.R
import com.on.staccato.theme.Gray1

// TODO: 파라미터 Enum Class 타입으로 변경 필요
@Composable
fun CategoryColor(
    modifier: Modifier = Modifier,
    @ColorRes color: Int = R.color.gray3,
) {
    // TODO: Box 백그라운드 컬러 번경 필요
    Box(
        modifier =
            modifier
                .size(36.dp)
                .background(color = Gray1, shape = CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.icon_folder),
            contentDescription = stringResource(id = R.string.category_creation_color),
            tint = colorResource(id = color),
        )
    }
}
