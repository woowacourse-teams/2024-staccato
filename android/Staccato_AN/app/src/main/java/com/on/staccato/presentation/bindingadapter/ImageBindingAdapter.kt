package com.on.staccato.presentation.bindingadapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.TypedValue
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import coil.load
import coil.transform.RoundedCornersTransformation
import com.on.staccato.R
import com.on.staccato.presentation.timeline.model.FilterType

@BindingAdapter(
    value = ["coilImageUrl", "coilPlaceHolder"],
)
fun ImageView.loadCoilImage(
    url: String?,
    placeHolder: Drawable? = null,
) {
    load(url) {
        placeholder(placeHolder)
        error(placeHolder)
    }
}

@BindingAdapter(
    value = ["coilCircleImageUrl", "coilPlaceHolder"],
)
fun ImageView.loadCoilCircleImage(
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
    value = ["coilRoundedCornerImageUrl", "coilRoundedCornerPlaceHolder", "coilRoundingRadiusDp"],
)
fun ImageView.setCoilRoundedCornerImage(
    url: String?,
    placeHolder: Drawable? = null,
    radiusDp: Float,
) {
    load(url) {
        placeholder(placeHolder)
        transformations(RoundedCornersTransformation(radiusDp.dpToPx(context)))
        error(placeHolder)
    }
}

@BindingAdapter(
    value = ["coilRoundedCornerImageUrl", "coilRoundedCornerImageUri", "coilRoundedCornerPlaceHolder", "coilRoundingRadiusDp"],
)
fun ImageView.setCoilRoundedCornerImageWithUri(
    url: String?,
    uri: Uri?,
    placeHolder: Drawable? = null,
    radiusDp: Float,
) {
    val image = uri ?: url
    load(image) {
        placeholder(placeHolder)
        transformations(RoundedCornersTransformation(radiusDp.dpToPx(context)))
        error(placeHolder)
    }
}

private fun Float.dpToPx(context: Context): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this,
        context.resources.displayMetrics,
    )
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

@BindingAdapter("categoryFilter")
fun ImageView.setCategoryFilter(filterType: FilterType?) {
    val color =
        when (filterType) {
            FilterType.TERM -> resources.getColor(R.color.staccato_blue, null)
            else -> resources.getColor(R.color.gray3, null)
        }
    imageTintList = ColorStateList.valueOf(color)
}
