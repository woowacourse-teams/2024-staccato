package com.on.staccato.presentation.bindingadapter

import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat.getColor
import androidx.core.view.isGone
import androidx.databinding.BindingAdapter
import coil.load
import coil.transform.RoundedCornersTransformation
import com.on.staccato.presentation.R
import com.on.staccato.presentation.photo.PhotoUploadState
import com.on.staccato.presentation.timeline.model.FilterType
import com.on.staccato.presentation.util.dpToPx

@BindingAdapter("isGone")
fun ImageView.setIsGone(isGone: Boolean) {
    this.isGone = isGone
}

@BindingAdapter("isInvisible")
fun ImageView.setVisibility(isInvisible: Boolean) {
    visibility = if (isInvisible) View.INVISIBLE else View.VISIBLE
}

@BindingAdapter("imageButtonIcon")
fun ImageButton.setColorSelectionIcon(
    @DrawableRes drawableRes: Int,
) {
    setImageResource(drawableRes)
}

@BindingAdapter("tintColorRes")
fun ImageView.setTintColor(
    @ColorRes colorRes: Int,
) {
    val color = getColor(context, colorRes)
    setColorFilter(color, PorterDuff.Mode.SRC_IN)
}

@BindingAdapter("colorSelectionIcon")
fun ImageView.setColorSelectionIcon(isSelected: Boolean) {
    setImageResource(
        if (isSelected) R.drawable.icon_checked_circle else R.drawable.icon_circle,
    )
}

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
            null -> resources.getColor(R.color.gray3, null)
            else -> resources.getColor(R.color.staccato_blue, null)
        }
    imageTintList = ColorStateList.valueOf(color)
}

@BindingAdapter("retryVisibilityByState")
fun ImageView.setRetryVisibilityByPhotoState(photoState: PhotoUploadState) {
    visibility =
        if (photoState == PhotoUploadState.Retry) {
            View.VISIBLE
        } else {
            View.GONE
        }
}
