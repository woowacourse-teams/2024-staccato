package com.on.staccato.presentation.component.topbar

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.staccato.R
import com.on.staccato.presentation.component.clickableWithoutRipple
import com.on.staccato.theme.Body4
import com.on.staccato.theme.Gray3
import com.on.staccato.theme.Gray5
import com.on.staccato.theme.StaccatoBlack
import com.on.staccato.theme.Title2
import com.on.staccato.theme.White

@OptIn(ExperimentalMaterial3Api::class)
private val TopAppBarColors =
    TopAppBarColors(
        containerColor = White,
        scrolledContainerColor = White,
        navigationIconContentColor = Gray3,
        titleContentColor = StaccatoBlack,
        actionIconContentColor = Gray3,
    )

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultNavigationTopBar(
    title: String? = null,
    subtitle: String? = null,
    isTitleCentered: Boolean = true,
    @DrawableRes vectorResource: Int = R.drawable.icon_arrow_left,
    onNavigationClick: () -> Unit,
    colors: TopAppBarColors = TopAppBarColors,
) {
    if (isTitleCentered) {
        CenterAlignedTopAppBar(
            title = {
                if (title != null) TopBarTitleText(title, subtitle, isTitleCentered)
            },
            navigationIcon = {
                NavigationIconButton(vectorResource, onNavigationClick)
            },
            colors = colors,
        )
    } else {
        TopAppBar(
            title = {
                if (title != null) TopBarTitleText(title, subtitle, isTitleCentered)
            },
            navigationIcon = {
                NavigationIconButton(vectorResource, onNavigationClick)
            },
            colors = colors,
        )
    }
}

@Composable
private fun TopBarTitleText(
    title: String,
    subtitle: String?,
    isTitleCentered: Boolean,
) {
    val titleAlignment =
        if (isTitleCentered) {
            Alignment.CenterHorizontally
        } else {
            Alignment.Start
        }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = titleAlignment,
    ) {
        Text(
            text = title,
            style = Title2,
            color = Gray5,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (subtitle != null) {
            Text(
                text = subtitle,
                style = Body4,
                color = Gray5,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun NavigationIconButton(
    @DrawableRes vectorResource: Int,
    onNavigationClick: () -> Unit,
) {
    Icon(
        modifier =
            Modifier
                .padding(5.dp)
                .clickableWithoutRipple { onNavigationClick() },
        imageVector = ImageVector.vectorResource(id = vectorResource),
        contentDescription = stringResource(id = R.string.top_bar_navigation_back_icon),
        tint = Gray3,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "제목이 없는 경우")
@Composable
private fun DefaultNavigationTopBarPreview() {
    DefaultNavigationTopBar(
        onNavigationClick = {},
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "제목이 중앙에 위치")
@Composable
private fun CenteredTitleTopBarPreview() {
    DefaultNavigationTopBar(
        title = "상단 앱 바 제목",
        onNavigationClick = {},
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "제목이 왼쪽에 위치")
@Composable
private fun LeftSideTitleTopBarPreview() {
    DefaultNavigationTopBar(
        title = "상단 앱 바 제목",
        isTitleCentered = false,
        onNavigationClick = {},
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "제목과 부제목이 중앙에 위치")
@Composable
private fun CenteredTitleWithSubTitleTopBarPreview() {
    DefaultNavigationTopBar(
        title = "상단 앱 바 제목",
        subtitle = "상단 앱 바 부제목입니다",
        onNavigationClick = {},
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "제목과 부제목이 왼쪽에 위치")
@Composable
private fun LeftSideTitleWithSubTitleTopBarPreview() {
    DefaultNavigationTopBar(
        title = "상단 앱 바 제목",
        subtitle = "상단 앱 바 부제목입니다",
        isTitleCentered = false,
        onNavigationClick = {},
    )
}
