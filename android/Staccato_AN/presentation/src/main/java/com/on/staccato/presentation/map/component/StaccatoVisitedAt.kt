package com.on.staccato.presentation.map.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.staccato.presentation.R
import com.on.staccato.presentation.common.format.toYearMonthDay
import com.on.staccato.theme.Body4
import java.time.LocalDateTime

@Composable
fun StaccatoVisitedAt(vararg dateArgs: Any) {
    Box(
        modifier =
            Modifier
                .background(
                    // TODO: Style에 정의되지 않은 값으로, 임시로 하드코딩 처리
                    color = Color(0xFFF7F7F9),
                    shape = RoundedCornerShape(7.dp),
                )
                .padding(horizontal = 7.dp, vertical = 3.dp),
    ) {
        Text(
            text = stringResource(id = R.string.all_date_dots_format, *dateArgs),
            style = Body4,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFF)
@Composable
private fun StaccatoVisitedAtPreview() {
    StaccatoVisitedAt(
        dateArgs = LocalDateTime.now().toYearMonthDay(),
    )
}
