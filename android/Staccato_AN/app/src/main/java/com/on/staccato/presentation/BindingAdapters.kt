package com.on.staccato.presentation

import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.NumberPicker
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.databinding.BindingAdapter
import coil.load
import coil.transform.RoundedCornersTransformation
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.button.MaterialButton
import com.on.staccato.R
import com.on.staccato.domain.model.MemoryCandidate
import com.on.staccato.domain.model.MemoryCandidates
import com.on.staccato.presentation.momentcreation.model.AttachedPhotosUiModel
import com.on.staccato.presentation.timeline.model.TimelineUiModel
import okhttp3.internal.format
import java.time.LocalDate
import java.time.LocalDateTime

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

@BindingAdapter(
    value = ["memoryTitle", "startDate", "endDate", "isPeriodActive", "isPhotoPosting"],
)
fun Button.setMemorySaveButtonActive(
    title: String?,
    startDate: LocalDate?,
    endDate: LocalDate?,
    isPeriodActive: Boolean,
    isPhotoPosting: Boolean?,
) {
    val isPeriodNotExistent = (startDate == null) || (endDate == null)
    val isPeriodRequirementsInvalid = isPeriodActive && isPeriodNotExistent
    isEnabled =
        if (title.isNullOrBlank() || isPhotoPosting == true || isPeriodRequirementsInvalid) {
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

@BindingAdapter(value = ["setDateTimeWithAmPm", "setMemoryCandidates"])
fun TextView.setDateTimeWithAmPm(
    setNowDateTime: LocalDateTime?,
    memoryCandidates: MemoryCandidates?,
) {
    if (memoryCandidates?.memoryCandidate?.isEmpty() == true) {
        text = resources.getString(R.string.visit_creation_memory_selection_hint)
        setTextColor(resources.getColor(R.color.gray3, null))
        isClickable = false
        isFocusable = false
    } else {
        text = setNowDateTime?.let(::getFormattedLocalDateTime) ?: ""
        setTextColor(resources.getColor(R.color.staccato_black, null))
        isClickable = true
        isFocusable = true
    }
}

@BindingAdapter("setDateTimeWithAmPm")
fun TextView.setDateTimeWithAmPm(setNowDateTime: LocalDateTime?) {
    text = setNowDateTime?.let(::getFormattedLocalDateTime) ?: ""
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
        setTextColor(resources.getColor(R.color.gray3, null))
    } else {
        text =
            resources.getString(R.string.memory_creation_selected_period)
                .format(startDate, endDate)
        setTextColor(resources.getColor(R.color.staccato_black, null))
    }
}

@BindingAdapter("visitedAtConfirmButtonActive")
fun Button.setVisitedAtConfirmButtonActive(items: List<Int>?) {
    isEnabled =
        if (items.isNullOrEmpty()) {
            setTextColor(resources.getColor(R.color.gray4, null))
            false
        } else {
            setTextColor(resources.getColor(R.color.white, null))
            true
        }
}

@BindingAdapter("memoryVisitedAtConfirmButtonActive")
fun Button.setMemoryVisitedAtConfirmButtonActive(items: List<LocalDate>?) {
    isEnabled =
        if (items.isNullOrEmpty()) {
            setTextColor(resources.getColor(R.color.gray4, null))
            false
        } else {
            setTextColor(resources.getColor(R.color.white, null))
            true
        }
}

@BindingAdapter("visitedAtNumberPickerItems")
fun NumberPicker.setVisitedAtNumberPickerItems(items: List<LocalDateTime>?) {
    items?.map { it.toLocalDate() }
    if (items.isNullOrEmpty()) {
        isGone = true
    } else {
        displayedValues = items.map(::getFormattedLocalDateTime).toTypedArray()
    }
}

@BindingAdapter("localDateNumberPickerItems")
fun NumberPicker.setLocalDateNumberPickerItems(items: List<LocalDate>?) {
    if (items.isNullOrEmpty()) {
        isGone = true
    } else {
        displayedValues = items.map(::getFormattedLocalDate).toTypedArray()
    }
}

private fun View.getFormattedLocalDate(setNowDate: LocalDate) =
    setNowDate.let {
        val year = setNowDate.year
        val month = setNowDate.monthValue
        val day = setNowDate.dayOfMonth
        resources.getString(R.string.all_date_kr_format)
            .format(year, month, day)
    }

private fun View.getFormattedLocalDateTime(setNowDateTime: LocalDateTime) =
    setNowDateTime.let {
        val year = setNowDateTime.year
        val month = setNowDateTime.monthValue
        val day = setNowDateTime.dayOfMonth
        val hour = if (setNowDateTime.hour % 12 == 0) 12 else setNowDateTime.hour % 12
        val noonText = if (setNowDateTime.hour < 12) "오전" else "오후"
        resources.getString(R.string.all_date_time_am_pm_kr_format)
            .format(year, month, day, noonText, hour)
    }

@BindingAdapter("memoryIsEmptyVisibility")
fun TextView.setMemoryIsEmptyVisibility(memoryCandidates: MemoryCandidates?) {
    isGone = !memoryCandidates?.memoryCandidate.isNullOrEmpty()
}

@BindingAdapter("visitedAtIsEmptyVisibility")
fun TextView.setVisitedAtIsEmptyVisibility(items: List<Int>?) {
    isGone = !items.isNullOrEmpty()
}

@BindingAdapter("visitedAt")
fun TextView.combineVisitedAt(visitedAt: LocalDateTime?) {
    text =
        if (visitedAt != null) {
            val hour = if (visitedAt.hour % 12 == 0) 12 else visitedAt.hour % 12
            val noonText = if (visitedAt.hour < 12) "오전" else "오후"
            format(
                resources.getString(R.string.visit_history),
                visitedAt.year,
                visitedAt.monthValue,
                visitedAt.dayOfMonth,
                noonText,
                hour,
            )
        } else {
            ""
        }
}

@BindingAdapter(
    value = ["startAt", "endAt"],
)
fun TextView.convertLocalDateToDatePeriodString(
    startAt: LocalDate?,
    endAt: LocalDate?,
) {
    val periodFormatString = resources.getString(R.string.memory_period_dot)
    text =
        if (startAt != null && endAt != null) {
            visibility = View.VISIBLE
            format(
                periodFormatString,
                startAt.year,
                startAt.monthValue,
                startAt.dayOfMonth,
                endAt.year,
                endAt.monthValue,
                endAt.dayOfMonth,
            )
        } else {
            visibility = View.INVISIBLE
            null
        }
}

@BindingAdapter(
    value = ["memoryStartAt", "memoryEndAt"],
)
fun TextView.convertLocalDateToDatePeriodStringInMemory(
    startAt: LocalDate?,
    endAt: LocalDate?,
) {
    val periodFormatString = resources.getString(R.string.memory_period_dot)
    text =
        if (startAt != null && endAt != null) {
            visibility = View.VISIBLE
            format(
                periodFormatString,
                startAt.year,
                startAt.monthValue,
                startAt.dayOfMonth,
                endAt.year,
                endAt.monthValue,
                endAt.dayOfMonth,
            )
        } else {
            visibility = View.GONE
            null
        }
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

@BindingAdapter("loginEnabled")
fun Button.setLoginEnabled(nickName: String?) {
    isEnabled =
        if (nickName.isNullOrBlank()) {
            setTextColor(resources.getColor(R.color.gray4, null))
            false
        } else {
            setTextColor(resources.getColor(R.color.white, null))
            true
        }
}

@BindingAdapter(value = ["currentPhotoNumbers", "maxPhotoNumbers"])
fun TextView.setPhotoNumbers(
    currentPhotoNumbers: Int,
    maxPhotoNumbers: Int,
) {
    text =
        resources.getString(R.string.all_photo_number).format(currentPhotoNumbers, maxPhotoNumbers)
}

@BindingAdapter("photoDragHintVisibility")
fun TextView.setPhotoDragHintVisibility(currentPhotoNumbers: Int) {
    isGone = currentPhotoNumbers < 2
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

@BindingAdapter("setAddress")
fun TextView.setAddress(address: String?) {
    text = address ?: context.getString(R.string.visit_creation_empty_address)
}

@BindingAdapter("sendEnabled")
fun ImageView.setSendEnabled(comment: String?) {
    isEnabled = !comment.isNullOrBlank()
}

@BindingAdapter("setLoadingLottieVisibility")
fun LottieAnimationView.setLoadingLottieVisibility(isLoading: Boolean?) {
    if (isLoading == true) {
        visibility = View.VISIBLE
    } else {
        visibility = View.GONE
    }
}

@BindingAdapter("setCurrentLocationButtonLoading")
fun MaterialButton.setCurrentLocationButtonLoading(isLoading: Boolean?) {
    isClickable = isLoading == false
    if (isLoading == true) {
        setText(R.string.all_empty)
        setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
    } else {
        setText(R.string.visit_creation_load_current_location)
    }
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

@BindingAdapter("periodSelectionVisibility")
fun TextView.setPeriodSelectionVisibility(isChecked: Boolean) {
    isGone = !isChecked
}

@BindingAdapter("recoveryEnabled")
fun Button.setRecoveryEnabled(recoveryCode: String?) {
    isEnabled =
        if (recoveryCode.isNullOrBlank() || recoveryCode.length < 36) {
            setTextColor(resources.getColor(R.color.gray4, null))
            false
        } else {
            setTextColor(resources.getColor(R.color.white, null))
            true
        }
}

@BindingAdapter("scrollToBottom")
fun ScrollView.scrollToBottom(isPeriodActive: Boolean) {
    if (isPeriodActive) {
        post { fullScroll(ScrollView.FOCUS_DOWN) }
    }
}

@BindingAdapter(
    value = ["visibilityByTimeline", "visibilityByLoading"],
)
fun View.setTimelineEmptyViewVisible(
    timeLine: List<TimelineUiModel>? = null,
    isTimelineLoading: Boolean,
) {
    visibility =
        if (timeLine.isNullOrEmpty() && isTimelineLoading.not()) {
            View.VISIBLE
        } else {
            View.GONE
        }
}

@BindingAdapter(
    value = ["visibilityByTimeline", "visibilityByLoading"],
)
fun ViewGroup.setMemoryAddButtonVisible(
    timeLine: List<TimelineUiModel>? = null,
    isTimelineLoading: Boolean,
) {
    visibility =
        if (timeLine.isNullOrEmpty() && isTimelineLoading.not()) {
            View.GONE
        } else {
            View.VISIBLE
        }
}
