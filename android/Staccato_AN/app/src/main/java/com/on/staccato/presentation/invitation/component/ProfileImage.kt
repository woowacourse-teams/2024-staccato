package com.on.staccato.presentation.invitation.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.staccato.R
import com.on.staccato.presentation.component.DefaultAsyncImage

@Composable
fun ProfileImage(
    modifier: Modifier = Modifier,
    url: String?,
) {
    DefaultAsyncImage(
        modifier = modifier.clip(shape = CircleShape),
        bitmapPixelSize = 150,
        url = url,
        placeHolder = R.drawable.icon_member,
        contentDescription = R.string.all_category_thumbnail_photo_description,
    )
}

@Preview(showBackground = true)
@Composable
private fun ProfileImagePreview() {
    Box(modifier = Modifier.padding(10.dp)) {
        ProfileImage(url = null)
    }
}
