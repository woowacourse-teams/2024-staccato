package com.on.staccato.presentation.category.invite.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.staccato.R
import com.on.staccato.presentation.component.clickableWithoutRipple
import com.on.staccato.theme.Body2
import com.on.staccato.theme.Gray1
import com.on.staccato.theme.Gray3

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTextField(
    value: String,
    modifier: Modifier = Modifier,
    placeholderText: String = "",
    onValueChange: (String) -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        interactionSource = interactionSource,
        keyboardOptions =
            KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Search,
            ),
        modifier =
            modifier
                .fillMaxWidth()
                .height(46.dp)
                .background(Gray1, RoundedCornerShape(5.dp)),
        decorationBox = { innerTextField ->
            TextFieldDefaults.DecorationBox(
                shape = RoundedCornerShape(5.dp),
                value = value,
                visualTransformation = VisualTransformation.None,
                innerTextField = innerTextField,
                placeholder = { SearchTextPlaceHolder(placeholderText) },
                leadingIcon = { SearchLeadingIcon() },
                trailingIcon = { SearchTrailingIcon(value, onValueChange) },
                singleLine = true,
                enabled = true,
                isError = false,
                interactionSource = interactionSource,
                contentPadding =
                    PaddingValues(12.dp),
                colors =
                    TextFieldDefaults.colors(
                        unfocusedContainerColor = Gray1,
                        focusedContainerColor = Gray1,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        cursorColor = Gray3,
                    ),
            )
        },
    )
}

@Composable
private fun SearchTextPlaceHolder(placeholderText: String) {
    Text(
        text = placeholderText,
        color = Gray3,
        style = Body2,
    )
}

@Composable
private fun SearchLeadingIcon() {
    Icon(
        painter = painterResource(id = R.drawable.icon_search),
        contentDescription = stringResource(id = R.string.invite_member_search_description),
        tint = Gray3,
    )
}

@Composable
private fun SearchTrailingIcon(
    value: String,
    onValueChange: (String) -> Unit,
) {
    if (value.isNotEmpty()) {
        Image(
            painter = painterResource(R.drawable.icon_x_circle),
            contentDescription = null,
            modifier =
                Modifier
                    .alpha(0.3f)
                    .clickableWithoutRipple { onValueChange("") },
        )
    }
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

@Composable
@Preview(showBackground = true)
fun EnglishStaccatoSearchTextFieldPreview() {
    SearchTextField(
        value = "Hello my name",
        onValueChange = {},
        placeholderText = "",
    )
}
