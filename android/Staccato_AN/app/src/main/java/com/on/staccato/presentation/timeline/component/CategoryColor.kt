package com.on.staccato.presentation.timeline.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.on.staccato.R
import com.on.staccato.presentation.common.color.CategoryColor.Companion.getColorBy
import com.on.staccato.presentation.common.color.CategoryColor.Companion.getIconBackgroundColorBy

@Composable
fun CategoryColor(
    modifier: Modifier = Modifier,
    color: String,
) {
    Box(
        modifier =
            modifier
                .size(36.dp)
                .background(color = getIconBackgroundColorBy(color), shape = CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.icon_folder),
            contentDescription = stringResource(id = R.string.category_creation_color),
            tint = getColorBy(color),
        )
    }
}
