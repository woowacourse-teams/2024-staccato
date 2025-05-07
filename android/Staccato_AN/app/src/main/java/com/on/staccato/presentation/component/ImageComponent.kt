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
import coil.transform.RoundedCornersTransformation
import com.on.staccato.R
import com.on.staccato.presentation.util.dpToPx

@Composable
fun ImageComponent(
    modifier: Modifier = Modifier,
    url: String? = null,
    @DrawableRes placeHolder: Int,
    contentDescription: String,
    contentScale: ContentScale = ContentScale.Crop,
    radius: Float = 0f,
) {
    val painter =
        rememberAsyncImagePainter(
            model =
                ImageRequest.Builder(LocalContext.current)
                    .data(url)
                    .transformations(RoundedCornersTransformation(radius.dpToPx(LocalContext.current)))
                    .placeholder(placeHolder)
                    .fallback(placeHolder)
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
private fun ImageComponentPreview() {
    ImageComponent(
        url = "https://avatars.githubusercontent.com/u/103019852?v=4",
        placeHolder = R.drawable.default_image,
        contentDescription = stringResource(id = R.string.all_category_thumbnail_photo_description),
    )
}
