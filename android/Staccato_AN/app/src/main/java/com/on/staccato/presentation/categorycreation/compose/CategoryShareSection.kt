package com.on.staccato.presentation.categorycreation.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.staccato.presentation.component.CustomSwitchComponent
import com.on.staccato.presentation.component.TextComponent
import com.on.staccato.theme.Body4
import com.on.staccato.theme.Gray3
import com.on.staccato.theme.Title2

const val CATEGORY_SHARE_TITLE = "카테고리 공유"
const val CATEGORY_SHARE_HINT = "친구들을 초대해 함께 카테고리를 채워보세요."

@Composable
fun CategoryShareSection(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier =
            Modifier
                .padding(top = 24.dp, end = 20.dp)
                .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.padding(start = 24.dp),
            horizontalAlignment = AbsoluteAlignment.Left,
        ) {
            TextComponent(
                description = CATEGORY_SHARE_TITLE,
                style = Title2,
            )
            Spacer(modifier = Modifier.padding(top = 8.dp))
            TextComponent(
                color = Gray3,
                description = CATEGORY_SHARE_HINT,
                style = Body4,
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        CustomSwitchComponent(
            checked = checked,
            onCheckedChange = onCheckedChange,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CategoryShareSectionPreview() {
    CategoryShareSection(false) {}
}
