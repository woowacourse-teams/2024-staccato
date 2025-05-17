package com.on.staccato.presentation.mypage.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.on.staccato.R
import com.on.staccato.presentation.component.clickableWithoutRipple
import com.on.staccato.theme.Gray2
import com.on.staccato.theme.StaccatoBlack
import com.on.staccato.theme.Title3
import com.on.staccato.theme.White

private val MenuPaddingValues = PaddingValues(
    top = 18.dp,
    bottom = 18.dp,
    start = 24.dp,
    end = 12.dp,
)

@Composable
fun MenuButton(
    modifier: Modifier = Modifier,
    menuTitle: String,
    contentPadding: PaddingValues = MenuPaddingValues,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .background(color = White)
            .padding(contentPadding)
            .clickableWithoutRipple { onClick() },
    ) {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = menuTitle,
                style = Title3,
                color = StaccatoBlack,
            )
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.icon_arrow_right),
                contentDescription = "icon",
                modifier = modifier,
                tint = Gray2,
            )
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0L,
)
@Composable
fun MenuButtonPreview(
    @PreviewParameter(provider = MenuTitlePreviewParameterProvider::class) title: String,
) {
    MenuButton(
        menuTitle = title,
    ) {}
}

class MenuTitlePreviewParameterProvider : PreviewParameterProvider<String> {
    override val values = sequenceOf(
        "카테고리 초대 관리",
        "개인정보처리방침",
        "피드백으로 혼내주기",
    )
}
