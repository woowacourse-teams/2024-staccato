package com.on.staccato.presentation.category.invite.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.staccato.R
import com.on.staccato.domain.model.Member
import com.on.staccato.domain.model.dummyMember
import com.on.staccato.presentation.component.DefaultAsyncImage
import com.on.staccato.presentation.component.clickableWithoutRipple
import com.on.staccato.theme.Body5
import com.on.staccato.theme.Gray2

@Composable
fun SelectedMemberItem(
    item: Member,
    onDeselect: (Member) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier =
            modifier
                .width(60.dp),
    ) {
        Box(modifier = Modifier.size(44.dp)) {
            DefaultAsyncImage(
                url = item.memberImage,
                bitmapPixelSize = 150,
                placeHolder = R.drawable.icon_member,
                modifier =
                    Modifier
                        .size(40.dp)
                        .border(
                            width = 0.3.dp,
                            color = Gray2,
                            shape = CircleShape,
                        )
                        .clip(CircleShape)
                        .align(Alignment.Center),
                contentDescription = R.string.all_category_thumbnail_photo_description,
            )
            Image(
                painter = painterResource(id = R.drawable.icon_x_circle),
                contentDescription = stringResource(id = R.string.all_cancel),
                modifier =
                    Modifier
                        .size(15.dp)
                        .alpha(0.9f)
                        .align(Alignment.TopEnd)
                        .clickableWithoutRipple { onDeselect(item) },
            )
        }
        Spacer(Modifier.size(5.dp))
        Text(
            text = item.nickname,
            style = Body5,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            modifier =
                Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(horizontal = 3.dp),
        )
    }
}

@Composable
@Preview(showBackground = true)
fun SelectedMemberItemPreview() {
    SelectedMemberItem(
        item = dummyMember,
        onDeselect = {},
    )
}

@Composable
@Preview(showBackground = true)
fun LongNamePreview() {
    SelectedMemberItem(
        item = dummyMember.copy(nickname = "매우엄청나게짱긴이름"),
        onDeselect = {},
    )
}
