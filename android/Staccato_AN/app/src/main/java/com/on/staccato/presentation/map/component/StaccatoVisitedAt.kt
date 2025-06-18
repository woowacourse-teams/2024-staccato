package com.on.staccato.presentation.map.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.staccato.R
import com.on.staccato.presentation.common.toYearMonthDay
import com.on.staccato.theme.Body4
import java.time.LocalDateTime

@Composable
fun StaccatoVisitedAt(vararg dateArgs: Any) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.calendar_today),
            contentDescription = null,
        )
        Spacer(modifier = Modifier.size(4.dp))
        Text(
            text = stringResource(id = R.string.all_date_dots_format, *dateArgs),
            style = Body4,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun StaccatoVisitedAtPreview() {
    StaccatoVisitedAt(
        dateArgs = LocalDateTime.now().toYearMonthDay(),
    )
}
