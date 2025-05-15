package com.on.staccato.presentation.component.topbar

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.on.staccato.R
import com.on.staccato.theme.Body4
import com.on.staccato.theme.Gray3
import com.on.staccato.theme.Gray5
import com.on.staccato.theme.Title2

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultNavigationTopBar(
    title: String,
    subtitle: String? = null,
    isTitleCentered: Boolean = true,
    @DrawableRes vectorResource: Int = R.drawable.icon_arrow_left,
    onNavigationClick: () -> Unit,
) {
    if (isTitleCentered) {
        CenterAlignedTopAppBar(
            title = {
                TopBarTitleText(title, subtitle, isTitleCentered)
            },
            navigationIcon = {
                NavigationIconButton(vectorResource, onNavigationClick)
            },
        )
    } else {
        TopAppBar(
            title = {
                TopBarTitleText(title, subtitle, isTitleCentered)
            },
            navigationIcon = {
                NavigationIconButton(vectorResource, onNavigationClick)
            }
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
    IconButton(
        onClick = onNavigationClick,
    ) {
        NavigationIcon(vectorResource)
    }
}

@Composable
private fun NavigationIcon(
    @DrawableRes vectorResource: Int = R.drawable.icon_arrow_left,
) {
    Icon(
        imageVector = ImageVector.vectorResource(id = vectorResource),
        contentDescription = "Navigation Icon",
        tint = Gray3,
    )
}

@Preview(showBackground = true)
@Composable
private fun TopBarComponentsPreview() {
    Column {
        DefaultNavigationTopBar(
            title = "상단 앱 바 제목",
            isTitleCentered = true,
        ) { }

        DefaultNavigationTopBar(
            title = "상단 앱 바 제목",
            isTitleCentered = false,
        ) { }

        DefaultNavigationTopBar(
            title = "상단 앱 바 제목",
            subtitle = "상단 앱 바 부제목입니다",
            isTitleCentered = true,
        ) { }

        DefaultNavigationTopBar(
            title = "상단 앱 바 제목",
            subtitle = "상단 앱 바 부제목입니다",
            isTitleCentered = false,
        ) { }
    }
}
