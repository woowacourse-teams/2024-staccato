package com.on.staccato.presentation.timeline.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.staccato.presentation.R
import com.on.staccato.presentation.color.CategoryColor
import com.on.staccato.theme.Icons

@Composable
fun CategoryFolder(
    modifier: Modifier = Modifier,
    color: CategoryColor,
) {
    Box(
        modifier =
            modifier
                .size(34.dp)
                .background(color = color.iconBackgroundColor, shape = CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Folder,
            contentDescription = stringResource(id = R.string.category_creation_color),
            tint = color.color,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFF)
@Composable
private fun CategoryFolderPreview() {
    CategoryFolder(color = CategoryColor.LIGHT_INDIGO)
}
