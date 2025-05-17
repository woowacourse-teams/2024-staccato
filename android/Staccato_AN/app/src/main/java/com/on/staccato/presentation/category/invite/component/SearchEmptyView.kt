package com.on.staccato.presentation.category.invite.component

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.staccato.R
import com.on.staccato.theme.Body4
import com.on.staccato.theme.Gray3

@Composable
fun SearchEmptyView(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Image(
            painter = painterResource(R.drawable.staccato_chracter_gray),
            contentDescription = null,
            modifier = Modifier.size(110.dp),
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = "친구를 초대해 함께 기록해보세요!",
            style = Body4,
            color = Gray3,
        )
        Spacer(Modifier.height(20.dp))
    }
}

@Composable
@Preview(showBackground = true)
fun SearchEmptyViewPreview() {
    SearchEmptyView()
}
