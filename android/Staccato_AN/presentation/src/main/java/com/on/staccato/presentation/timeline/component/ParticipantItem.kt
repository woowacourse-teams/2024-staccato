package com.on.staccato.presentation.timeline.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.staccato.benchmark.trace
import com.on.staccato.presentation.R
import com.on.staccato.presentation.component.DefaultAsyncImage

@Composable
fun ParticipantItem(
    profileImageUrl: String? = null,
    modifier: Modifier = Modifier,
) {
    trace("ParticipantItem") {
        DefaultAsyncImage(
            modifier =
                modifier.size(25.dp)
                    .shadow(2.dp, shape = CircleShape)
                    .background(Color.White, shape = CircleShape)
                    .padding(1.dp)
                    .clip(CircleShape)
                    .background(Color.White),
            bitmapPixelSize = 150,
            url = profileImageUrl,
            placeHolder = R.drawable.icon_member,
            errorImageRes = R.drawable.icon_member,
            contentDescription = R.string.mates_profile_image_description,
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
