package com.on.staccato.presentation.bindingadapter

import android.widget.Button
import androidx.databinding.BindingAdapter
import com.google.android.material.button.MaterialButton
import com.on.staccato.R
import com.on.staccato.domain.model.MemoryCandidate
import com.on.staccato.presentation.momentcreation.model.AttachedPhotosUiModel
import java.time.LocalDate
import java.time.LocalDateTime

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
