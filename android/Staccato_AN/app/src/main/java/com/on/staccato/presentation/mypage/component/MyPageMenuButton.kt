package com.on.staccato.presentation.mypage.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.on.staccato.R
import com.on.staccato.presentation.component.clickableWithoutRipple
import com.on.staccato.theme.Accents4
import com.on.staccato.theme.Gray2
import com.on.staccato.theme.StaccatoBlack
import com.on.staccato.theme.Title3
import com.on.staccato.theme.White

private val MenuPaddingValues =
    PaddingValues(
        top = 18.dp,
        bottom = 18.dp,
        start = 24.dp,
        end = 12.dp,
    )

@Composable
fun MyPageMenuButton(
    menuTitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = MenuPaddingValues,
    hasNotification: Boolean = false,
) {
    Box(
        modifier =
            modifier
                .background(color = White)
                .padding(contentPadding)
                .clickableWithoutRipple { onClick() },
    ) {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BadgedBox(
                badge = { if (hasNotification) Badge(containerColor = Accents4) },
            ) {
                Text(
                    text = menuTitle,
                    style = Title3,
                    color = StaccatoBlack,
                )
            }
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.icon_arrow_right),
                contentDescription = stringResource(id = R.string.mypage_menu_navigation_icon_description),
                modifier = modifier,
                tint = Gray2,
            )
        }
    }
}

@Preview(name = "마이페이지 메뉴 버튼")
@Composable
private fun MyPageMenuButtonPreview(
    @PreviewParameter(provider = MenuTitlePreviewParameterProvider::class) title: String,
) {
    MyPageMenuButton(menuTitle = title, onClick = {})
}

private class MenuTitlePreviewParameterProvider(
    override val values: Sequence<String> =
        sequenceOf(
            "카테고리 초대 관리",
            "개인정보처리방침",
            "피드백으로 혼내주기",
        ),
) : PreviewParameterProvider<String>

@Preview(name = "알림이 있는 경우")
@Composable
private fun MyPageMenuButtonWithBadgePreview() {
    MyPageMenuButton(menuTitle = "알림이 있는 경우", onClick = {}, hasNotification = true)
}
