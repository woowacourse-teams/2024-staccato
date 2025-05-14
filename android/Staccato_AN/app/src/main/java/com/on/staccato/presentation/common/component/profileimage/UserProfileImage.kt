package com.on.staccato.presentation.common.component.profileimage

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import com.on.staccato.R
import com.on.staccato.presentation.common.component.CoilImage
import com.on.staccato.theme.Gray2

@Composable
fun UserProfileImage(
    imageUrl: String?,
    size: Dp,
    borderWidth: Dp,
    modifier: Modifier = Modifier,
) {
    if (imageUrl == null) {
        DefaultProfileImage(
            size = size,
            borderWidth = borderWidth,
            modifier = modifier,
        )
    } else {
        CoilImage(
            imageUrl = imageUrl,
            bitmapPixelSize = 100,
            modifier =
                modifier
                    .size(size)
                    .border(
                        width = borderWidth,
                        color = Gray2,
                        shape = CircleShape,
                    )
                    .clip(CircleShape),
            placeHolder = R.drawable.icon_member,
        )
    }
}
