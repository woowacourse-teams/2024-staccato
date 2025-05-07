package com.on.staccato.presentation.timeline.compose

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.staccato.R
import com.on.staccato.presentation.component.ImageComponent
import com.on.staccato.theme.White

@Composable
fun MemberItem(memberImageUrl: String? = null) {
    ImageComponent(
        modifier =
            Modifier
                .shadow(2.dp, shape = CircleShape, clip = false)
                .border(width = 1.dp, color = White, shape = CircleShape)
                .size(23.5.dp),
        url = memberImageUrl,
        placeHolder = R.drawable.icon_member,
        contentDescription = stringResource(id = R.string.mates_profile_image_description),
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun MemberItemPreview() {
    MemberItem(
        memberImageUrl = "https://avatars.githubusercontent.com/u/103019852?v=4",
    )
}
