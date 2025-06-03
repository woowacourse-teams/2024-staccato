package com.on.staccato.presentation.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.staccato.R
import com.on.staccato.theme.Body4
import com.on.staccato.theme.Gray3

@Composable
fun DefaultEmptyView(
    modifier: Modifier = Modifier,
    description: String,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Image(
            painter = painterResource(R.drawable.staccato_character_gray),
            contentDescription = null,
            modifier = Modifier.size(110.dp),
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = description,
            style = Body4,
            color = Gray3,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(20.dp))
    }
}

@Composable
@Preview(showBackground = true)
private fun DefaultEmptyViewPreview() {
    DefaultEmptyView(description = "목록의 아이템이 비어있는 경우\n나타내는 화면입니다.\n글자는 가운데 정렬입니다.")
}
