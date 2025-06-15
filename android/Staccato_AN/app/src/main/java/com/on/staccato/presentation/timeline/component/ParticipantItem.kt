package com.on.staccato.presentation.timeline.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.staccato.R
import com.on.staccato.presentation.component.DefaultAsyncImage
import com.on.staccato.theme.White

@Composable
fun ParticipantItem(profileImageUrl: String? = null) {
    Box(
        modifier =
            Modifier.size(25.dp)
                .shadow(2.dp, shape = CircleShape, clip = true)
                .background(Color.White)
                .padding(1.dp),
    ) {
        DefaultAsyncImage(
            modifier = Modifier.clip(CircleShape),
            bitmapPixelSize = 150,
            url = profileImageUrl,
            errorImageRes = R.drawable.icon_member,
            contentDescription = R.string.mates_profile_image_description,
            contentScale = ContentScale.Crop,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun ParticipantItemPreview() {
    ParticipantItem(
        profileImageUrl = "https://avatars.githubusercontent.com/u/103019852?v=4",
    )
}
