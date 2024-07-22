package com.woowacourse.staccato.presentation

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import coil.load
import coil.transform.RoundedCornersTransformation
import com.bumptech.glide.Glide

@BindingAdapter(
    value = ["coilImageUrl", "coilPlaceHolder"],
    requireAll = false,
)
fun ImageView.loadImageWithCoil(
    url: String?,
    placeHolder: Drawable? = null,
) {
    url?.let {
        this.load(it) {
            placeholder(placeHolder)
            error(placeHolder)
        }
    }
}

@BindingAdapter(
    value = ["coilCircleImageUrl", "coilPlaceHolder"],
    requireAll = false,
)
fun ImageView.setCircleImageWithCoil(
    url: String?,
    placeHolder: Drawable? = null,
) {
    url?.let {
        this.load(it) {
            placeholder(placeHolder)
            transformations(RoundedCornersTransformation(1000f))
            error(placeHolder)
        }
    }
}

@BindingAdapter(
    value = ["glideImageUrl", "glidePlaceHolder"],
    requireAll = false,
)
fun ImageView.loadImageWithGlide(
    url: String?,
    placeHolder: Drawable? = null,
) {
    Glide.with(context)
        .load(url)
        .placeholder(placeHolder)
        .error(placeHolder)
        .into(this)
}

@BindingAdapter(
    value = ["glideCircleImageUrl", "glidePlaceHolder"],
    requireAll = false,
)
fun ImageView.setCircleImageWithGlide(
    url: String?,
    placeHolder: Drawable? = null,
) {
    Glide.with(context)
        .load(url)
        .placeholder(placeHolder)
        .circleCrop()
        .error(placeHolder)
        .into(this)
}
