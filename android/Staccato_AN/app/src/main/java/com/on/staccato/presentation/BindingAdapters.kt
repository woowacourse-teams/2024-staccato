package com.on.staccato.presentation

import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.NumberPicker
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import coil.load
import coil.transform.RoundedCornersTransformation
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.on.staccato.R
import com.on.staccato.domain.model.MemoryCandidate
import com.on.staccato.domain.model.MemoryCandidates
import com.on.staccato.presentation.momentcreation.model.AttachedPhotosUiModel
import okhttp3.internal.format
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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

@BindingAdapter("setLoginButtonActive")
fun Button.setLoginButtonActive(nickName: String?) {
    isEnabled =
        if (nickName.isNullOrBlank()) {
            setTextColor(resources.getColor(R.color.gray4, null))
            false
        } else {
            setTextColor(resources.getColor(R.color.white, null))
            true
        }
}

@BindingAdapter(
    value = ["memoryTitle", "startDate", "endDate", "isPhotoPosting"],
)
fun Button.setMemorySaveButtonActive(
    title: String?,
    startDate: LocalDate?,
    endDate: LocalDate?,
    isPhotoPosting: Boolean?,
) {
    isEnabled =
        if (title.isNullOrBlank() || startDate == null || endDate == null || isPhotoPosting == true) {
            setTextColor(resources.getColor(R.color.gray4, null))
            false
        } else {
            setTextColor(resources.getColor(R.color.white, null))
            true
        }
}

@BindingAdapter(
    value = ["memoryTitle", "startDate", "endDate", "photoUri", "photoUrl"],
)
fun Button.setMemorySaveButtonActive(
    title: String?,
    startDate: LocalDate?,
    endDate: LocalDate?,
    photoUri: Uri?,
    photoUrl: String?,
) {
    isEnabled =
        if (title.isNullOrBlank() || startDate == null || endDate == null || (photoUri != null && photoUrl == null)) {
            setTextColor(resources.getColor(R.color.gray4, null))
            false
        } else {
            setTextColor(resources.getColor(R.color.white, null))
            true
        }
}

@BindingAdapter(
    value = ["staccatoTitle", "address", "visitedAt", "photos", "selectedMemory"],
)
fun Button.setStaccatoCreationButtonActive(
    title: String?,
    address: String?,
    visitedAt: LocalDateTime?,
    photos: AttachedPhotosUiModel?,
    selectedMemory: MemoryCandidate?,
) {
    isEnabled =
        if (title.isNullOrBlank() || address == null || selectedMemory == null || visitedAt == null || photos?.isLoading() == true) {
            setTextColor(resources.getColor(R.color.gray4, null))
            false
        } else {
            setTextColor(resources.getColor(R.color.white, null))
            true
        }
}

@BindingAdapter(
    value = ["staccatoTitle", "address", "visitedAt", "photos"],
)
fun Button.setStaccatoUpdateButtonActive(
    title: String?,
    address: String?,
    visitedAt: LocalDateTime?,
    photos: AttachedPhotosUiModel?,
) {
    isEnabled =
        if (title.isNullOrBlank() || address == null || visitedAt == null || photos?.isLoading() == true) {
            setTextColor(resources.getColor(R.color.gray4, null))
            false
        } else {
            setTextColor(resources.getColor(R.color.white, null))
            true
        }
}

@BindingAdapter(value = ["setSelectedMemory", "setMemoryCandidates"])
fun TextView.setSelectedMemory(
    selectedMemory: MemoryCandidate?,
    memoryCandidates: MemoryCandidates?,
) {
    if (memoryCandidates?.memoryCandidate?.isEmpty() == true) {
        text = resources.getString(R.string.visit_creation_no_memory_hint)
        setTextColor(resources.getColor(R.color.gray3, null))
        isClickable = false
        isFocusable = false
    } else if (selectedMemory == null) {
        text = resources.getString(R.string.visit_creation_memory_selection_hint)
        setTextColor(resources.getColor(R.color.gray3, null))
        isClickable = true
        isFocusable = true
    } else {
        text = selectedMemory.memoryTitle
        setTextColor(resources.getColor(R.color.staccato_black, null))
    }
}

@BindingAdapter(
    value = ["selectedMemory", "visitedAt"],
)
fun Button.setVisitUpdateButtonActive(
    memory: MemoryCandidate?,
    visitedAt: LocalDate?,
) {
    isEnabled =
        if (memory == null || visitedAt == null) {
            setTextColor(resources.getColor(R.color.gray4, null))
            false
        } else {
            setTextColor(resources.getColor(R.color.white, null))
            true
        }
}

@BindingAdapter(
    value = ["startDate", "endDate"],
)
fun TextView.setMemoryPeriod(
    startDate: LocalDate?,
    endDate: LocalDate?,
) {
    if (startDate == null || endDate == null) {
        text = resources.getString(R.string.memory_creation_period_hint)
    } else {
        text =
            resources.getString(R.string.memory_creation_period_description)
                .format(startDate, endDate)
        setTextColor(resources.getColor(R.color.staccato_black, null))
    }
}

@BindingAdapter("visitedAtConfirmButtonActive")
fun Button.setVisitedAtConfirmButtonActive(items: List<LocalDate>?) {
    isEnabled =
        if (items.isNullOrEmpty()) {
            setTextColor(resources.getColor(R.color.gray4, null))
            false
        } else {
            setTextColor(resources.getColor(R.color.white, null))
            true
        }
}

@BindingAdapter("setDateTimeWithAmPm")
fun TextView.setDateTimeWithAmPm(setNowDateTime: LocalDateTime?) {
    text = setNowDateTime?.let {
        val year = setNowDateTime.year
        val month = setNowDateTime.monthValue
        val day = setNowDateTime.dayOfMonth
        val hour = if (setNowDateTime.hour % 12 == 0) 12 else setNowDateTime.hour % 12
        val noonText = if (setNowDateTime.hour < 12) "오전" else "오후"
        resources.getString(R.string.all_date_time_am_pm_kr_format)
            .format(year, month, day, noonText, hour)
    } ?: ""
}

@BindingAdapter("visitedAtNumberPickerItems")
fun NumberPicker.setVisitedAtNumberPickerItems(items: List<LocalDate>?) {
    if (items.isNullOrEmpty()) {
        isGone = true
    } else {
        displayedValues = items.map { it.toString() }.toTypedArray()
    }
}

@BindingAdapter("visitedAtIsEmptyVisibility")
fun TextView.setVisitedAtIsEmptyVisibility(items: List<LocalDate>?) {
    isGone = !items.isNullOrEmpty()
}

@BindingAdapter("visitedAt")
fun TextView.combineVisitedAt(visitedAt: LocalDateTime?) {
    if (visitedAt != null) {
        text =
            format(
                resources.getString(R.string.visit_history),
                visitedAt.year,
                visitedAt.monthValue,
                visitedAt.dayOfMonth,
            )
    } else {
        text = ""
    }
}

@BindingAdapter(
    value = ["startAt", "endAt"],
)
fun TextView.convertLocalDateToDatePeriodString(
    startAt: LocalDate,
    endAt: LocalDate,
) {
    val fullFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")
    val monthFormatter = DateTimeFormatter.ofPattern("MM.dd")
    val dayFormatter = DateTimeFormatter.ofPattern("dd")

    val datePeriod =
        if (startAt.year != endAt.year) {
            "${startAt.format(fullFormatter)} - ${endAt.format(fullFormatter)}"
        } else if (startAt.monthValue != endAt.monthValue) {
            "${startAt.format(fullFormatter)} - ${endAt.format(monthFormatter)}"
        } else if (startAt.dayOfMonth != endAt.dayOfMonth) {
            "${startAt.format(fullFormatter)} - ${endAt.format(dayFormatter)}"
        } else {
            startAt.format(fullFormatter)
        }
    text = datePeriod
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

@BindingAdapter("setAttachedPhotoVisibility")
fun FrameLayout.setAttachedPhotoVisibility(items: Array<Uri>?) {
    isInvisible = !items.isNullOrEmpty()
}

@BindingAdapter("setEnabled")
fun Button.setEnabled(isUpdateCompleted: Boolean?) {
    isEnabled = !(isUpdateCompleted ?: true)
}

@BindingAdapter("setLoginEnabled")
fun Button.setLoginEnabled(nickName: String?) {
    isEnabled = !nickName.isNullOrEmpty()
}

@BindingAdapter(value = ["currentPhotoNumbers", "maxPhotoNumbers"])
fun TextView.setPhotoNumbers(
    currentPhotoNumbers: Int,
    maxPhotoNumbers: Int,
) {
    text =
        resources.getString(R.string.all_photo_number).format(currentPhotoNumbers, maxPhotoNumbers)
}

@BindingAdapter("setSelected")
fun ImageView.setSelectedState(selected: Boolean) {
    isSelected = selected
}

@BindingAdapter("setImageResource")
fun ImageView.setImageResourceWithId(resId: Int) {
    setImageResource(resId)
}

@BindingAdapter("setAddress")
fun TextView.setAddress(address: String?) {
    text =
        if (address == null) {
            setTextColor(resources.getColor(R.color.gray3, null))
            context.getString(R.string.staccato_loading_address)
        } else {
            setTextColor(resources.getColor(R.color.staccato_black, null))
            address
        }
}

@BindingAdapter("app:enableSendButton")
fun ImageView.enableSendButton(commentInput: MutableLiveData<String>) {
    commentInput.observeForever { inputText ->
        isEnabled = !inputText.isBlankOrEmpty()
    }
}

fun String?.isBlankOrEmpty(): Boolean {
    return this == null || this.trim().isEmpty()
}

@BindingAdapter(value = ["thumbnailUri", "thumbnailUrl"])
fun ProgressBar.setThumbnailLoadingProgressBar(
    thumbnailUri: Uri?,
    thumbnailUrl: String?,
) {
    visibility =
        if (thumbnailUri != null && thumbnailUrl == null) {
            View.VISIBLE
        } else {
            View.GONE
        }
}

@BindingAdapter(value = ["thumbnailUri", "thumbnailUrl"])
fun View.setThumbnail(
    thumbnailUri: Uri?,
    thumbnailUrl: String?,
) {
    visibility =
        if (thumbnailUri == null && thumbnailUrl == null) {
            View.VISIBLE
        } else {
            View.GONE
        }
}
