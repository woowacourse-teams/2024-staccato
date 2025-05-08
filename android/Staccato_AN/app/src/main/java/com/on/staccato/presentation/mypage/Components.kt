package com.on.staccato.presentation.mypage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.on.staccato.R
import com.on.staccato.presentation.component.Divider
import com.on.staccato.theme.Gray1
import com.on.staccato.theme.Gray2
import com.on.staccato.theme.Gray3
import com.on.staccato.theme.StaccatoBlack
import com.on.staccato.theme.White

// Todo: 버튼 색상 다크모드 대응 필요, 배경색은 투명 처리
val MenuButtonColors =
    ButtonColors(
        containerColor = White,
        contentColor = StaccatoBlack,
        disabledContainerColor = White,
        disabledContentColor = Gray3,
    )

val MenuPaddingValues = PaddingValues(
    top = 18.dp,
    bottom = 18.dp,
    start = 24.dp,
    end = 12.dp,
)

@Composable
fun MenuButton(
    modifier: Modifier = Modifier,
    menuTitle: String,
    buttonColors: ButtonColors = MenuButtonColors,
    contentPadding: PaddingValues = MenuPaddingValues,
    onClick: () -> Unit,
) {
    Button(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RectangleShape,
        colors = buttonColors,
        elevation = null,
        contentPadding = contentPadding,
    ) {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = menuTitle)
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.icon_arrow_right),
                contentDescription = "icon",
                modifier = modifier,
                tint = Gray2,
            )
        }
    }
}

class MenuTitlePreviewParameterProvider : PreviewParameterProvider<String> {
    override val values = sequenceOf(
        "카테고리 초대 관리",
        "개인정보처리방침",
        "피드백으로 혼내주기",
    )
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

@Composable
fun MiddleDivider(
    modifier: Modifier = Modifier,
    thickness: Dp = 10.dp,
    color: Color = Gray1,
) {
    Divider(
        modifier = modifier,
        thickness = thickness,
        color = color,
    )
}

@Preview(showBackground = true)
@Composable
fun MiddleDividerPreview() {
    MiddleDivider()
}
