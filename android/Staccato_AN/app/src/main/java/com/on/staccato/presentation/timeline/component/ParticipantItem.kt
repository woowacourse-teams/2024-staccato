package com.on.staccato.presentation.timeline.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.staccato.R
import com.on.staccato.presentation.component.DefaultAsyncImage
import com.on.staccato.theme.White

@Composable
fun ParticipantItem(profileImageUrl: String? = null) {
    DefaultAsyncImage(
        modifier =
            Modifier
                .shadow(2.dp, shape = CircleShape, clip = false)
                .border(width = 1.dp, color = White, shape = CircleShape)
                .size(25.dp),
        bitmapPixelSize = 150,
        url = profileImageUrl,
        placeHolder = R.drawable.icon_member,
        contentDescription = R.string.mates_profile_image_description,
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun ParticipantItemPreview() {
    ParticipantItem(
        profileImageUrl = "https://avatars.githubusercontent.com/u/103019852?v=4",
    )
}
