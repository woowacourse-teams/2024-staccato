package com.on.staccato.presentation.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.on.staccato.R

@Composable
fun ImageComponent(
    modifier: Modifier = Modifier,
    url: String? = null,
    @DrawableRes placeHolder: Int,
    contentDescription: String,
    contentScale: ContentScale = ContentScale.Crop,
) {
    val painter =
        rememberAsyncImagePainter(
            model =
                ImageRequest.Builder(LocalContext.current)
                    .data(url)
                    .placeholder(placeHolder)
                    .build(),
        )
    Image(
        modifier = modifier,
        painter = painter,
        contentDescription = contentDescription,
        contentScale = contentScale,
    )
}

@Preview
@Composable
fun ImageComponentPreview() {
    ImageComponent(
        url = "https://avatars.githubusercontent.com/u/103019852?v=4",
        placeHolder = R.drawable.default_image,
        contentDescription = stringResource(id = R.string.all_category_thumbnail_photo_description),
    )
}
