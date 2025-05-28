package com.on.staccato.presentation.category.invite.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.staccato.R
import com.on.staccato.presentation.component.clickableWithoutRipple
import com.on.staccato.theme.Body2
import com.on.staccato.theme.Gray1
import com.on.staccato.theme.Gray3

@Composable
fun SearchTextField(
    value: String,
    modifier: Modifier = Modifier,
    placeholderText: String = "",
    keyboardOptions: KeyboardOptions =
        KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Search,
            showKeyboardOnFocus = true,
        ),
    onValueChange: (String) -> Unit,
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        keyboardOptions = keyboardOptions,
        leadingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.icon_search),
                contentDescription = stringResource(id = R.string.invite_member_search_description),
                tint = Gray3,
            )
        },
        trailingIcon = {
            if (value.isNotEmpty()) {
                Image(
                    painter = painterResource(R.drawable.icon_x_circle),
                    contentDescription = null,
                    modifier =
                        Modifier
                            .alpha(0.3f)
                            .clickableWithoutRipple(onClick = { onValueChange("") }),
                )
            }
        },
        placeholder = {
            Text(
                text = placeholderText,
                color = Gray3,
                style = Body2,
            )
        },
        colors =
            TextFieldDefaults.colors(
                unfocusedContainerColor = Gray1,
                focusedContainerColor = Gray1,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                cursorColor = Gray3,
            ),
        shape = RoundedCornerShape(5.dp),
        modifier =
            modifier
                .fillMaxWidth()
                .height(46.dp),
    )
}

@Composable
@Preview(showBackground = true)
fun StaccatoSearchEmptyTextFieldPreview() {
    SearchTextField(
        value = "",
        onValueChange = {},
        placeholderText = "검색어를 입력해주세요",
    )
}

@Composable
@Preview(showBackground = true)
fun StaccatoSearchTextFieldPreview() {
    SearchTextField(
        value = "아주아주 긴 입력값 아주아주 긴 입력값 아주아주 긴 입력값 아주아주 긴 입력값",
        onValueChange = {},
        placeholderText = "",
    )
}
