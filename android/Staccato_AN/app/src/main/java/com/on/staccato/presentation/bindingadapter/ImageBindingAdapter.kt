package com.on.staccato.presentation.bindingadapter

import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import coil.load
import coil.transform.RoundedCornersTransformation
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

@BindingAdapter(
    value = ["coilImageUrl", "coilPlaceHolder"],
)
fun ImageView.loadImageWithCoil(
    url: String?,
    placeHolder: Drawable? = null,
) {
    load(url) {
        placeholder(placeHolder)
        error(placeHolder)
    }
}

@BindingAdapter(
    value = ["coilImageUri", "coilPlaceHolder"],
)
fun ImageView.loadImageByUriWithCoil(
    uri: Uri?,
    placeHolder: Drawable? = null,
) {
    load(uri) {
        placeholder(placeHolder)
        error(placeHolder)
    }
}

@BindingAdapter(
    value = ["coilCircleImageUrl", "coilPlaceHolder"],
)
fun ImageView.setCircleImageWithCoil(
    url: String?,
    placeHolder: Drawable? = null,
) {
    load(url) {
        placeholder(placeHolder)
        transformations(RoundedCornersTransformation(1000f))
        error(placeHolder)
    }
}

@BindingAdapter(
    value = ["coilRoundedCornerImageUrl", "coilPlaceHolder", "coilRoundingRadius"],
)
fun ImageView.setRoundedCornerImageWithCoil(
    url: String?,
    placeHolder: Drawable? = null,
    roundingRadius: Float,
) {
    load(url) {
        placeholder(placeHolder)
        transformations(RoundedCornersTransformation(roundingRadius))
        error(placeHolder)
    }
}

@BindingAdapter(
    value = ["coilUriRoundedCorner", "coilUriPlaceHolder", "coilUriRoundingRadius"],
)
fun ImageView.setRoundedCornerUriWithCoil(
    uri: Uri?,
    placeHolder: Drawable? = null,
    roundingRadius: Float,
) {
    load(uri) {
        placeholder(placeHolder)
        transformations(RoundedCornersTransformation(roundingRadius))
        error(placeHolder)
    }
}

@BindingAdapter(
    value = ["coilImageUrl", "coilImageUri", "coilPlaceHolder", "coilRoundingRadius"],
)
fun ImageView.setRoundedCornerUpdateImageWithCoil(
    url: String?,
    uri: Uri?,
    placeHolder: Drawable? = null,
    roundingRadius: Float,
) {
    val image = uri ?: url
    load(image) {
        placeholder(placeHolder)
        transformations(RoundedCornersTransformation(roundingRadius))
        error(placeHolder)
    }
}

@BindingAdapter(
    value = ["glideImageUrl", "glidePlaceHolder"],
)
fun ImageView.loadImageWithGlide(
    url: String?,
    placeHolder: Drawable? = null,
) {
    Glide.with(context)
        .load(url)
        .placeholder(placeHolder)
        .centerCrop()
        .error(placeHolder)
        .into(this)
}

@BindingAdapter(
    value = ["glideCircleImageUrl", "glidePlaceHolder"],
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

@BindingAdapter(
    value = ["glideRoundedCornerImageUrl", "glidePlaceHolder", "glideRoundingRadius"],
)
fun ImageView.setRoundedCornerImageWithGlide(
    url: String?,
    placeHolder: Drawable? = null,
    roundingRadius: Int,
) {
    Glide.with(context)
        .load(url)
        .placeholder(placeHolder)
        .centerCrop()
        .apply(RequestOptions.bitmapTransform(RoundedCorners(roundingRadius)))
        .error(placeHolder)
        .into(this)
}

@BindingAdapter(value = ["glideUriRoundedCornerImageUri", "glideUriPlaceHolder", "glideUriRoundingRadius"])
fun ImageView.setRoundedCornerImageByUriWithGlide(
    uri: Uri?,
    placeHolder: Drawable? = null,
    roundingRadius: Int,
) {
    Glide.with(context)
        .load(uri)
        .centerCrop()
        .apply(RequestOptions.bitmapTransform(RoundedCorners(roundingRadius)))
        .error(placeHolder)
        .into(this)
}

@BindingAdapter("setAttachedPhotoVisibility")
fun ImageView.setAttachedPhotoVisibility(items: Array<Uri>?) {
    if (items.isNullOrEmpty()) {
        visibility = View.GONE
    } else {
        visibility = View.VISIBLE
        Glide.with(context)
            .load(items[0])
            .centerCrop()
            .into(this)
    }
}

@BindingAdapter("setSelected")
fun ImageView.setSelectedState(selected: Boolean) {
    isSelected = selected
}

@BindingAdapter(
    value = [
        "colorImageResource",
        "grayImageResource",
    ],
)
fun ImageView.setImageResourceWithId(
    colorResId: Int,
    grayResId: Int,
) {
    setImageResource(
        if (isSelected) {
            colorResId
        } else {
            grayResId
        },
    )
}

@BindingAdapter("sendEnabled")
fun ImageView.setSendEnabled(comment: String?) {
    isEnabled = !comment.isNullOrBlank()
}
