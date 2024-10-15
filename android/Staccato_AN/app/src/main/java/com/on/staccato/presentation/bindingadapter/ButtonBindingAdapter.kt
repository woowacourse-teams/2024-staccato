package com.on.staccato.presentation.bindingadapter

import android.widget.Button
import android.widget.ImageButton
import androidx.databinding.BindingAdapter
import com.google.android.material.button.MaterialButton
import com.on.staccato.R
import com.on.staccato.domain.model.MemoryCandidate
import com.on.staccato.presentation.staccatocreation.model.AttachedPhotosUiModel
import java.time.LocalDate
import java.time.LocalDateTime

@BindingAdapter("loginButtonEnabled")
fun Button.setLoginButtonEnabled(nickName: String?) {
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
    value = ["staccatoTitle", "staccatoAddress", "staccatoVisitedAt", "staccatoPhotos", "staccatoMemory"],
)
fun Button.setStaccatoSaveButtonEnabled(
    staccatoTitle: String?,
    staccatoAddress: String?,
    staccatoVisitedAt: LocalDateTime?,
    staccatoPhotos: AttachedPhotosUiModel?,
    staccatoMemory: MemoryCandidate?,
) {
    isEnabled =
        if (staccatoTitle.isNullOrBlank() ||
            staccatoAddress == null ||
            staccatoMemory == null ||
            staccatoVisitedAt == null ||
            staccatoPhotos?.isLoading() == true
        ) {
            setTextColor(resources.getColor(R.color.gray4, null))
            false
        } else {
            setTextColor(resources.getColor(R.color.white, null))
            true
        }
}

@BindingAdapter(
    value = ["memoryTitle", "memoryStartDate", "memoryEndDate", "isPeriodActive", "isPhotoUploading"],
)
fun Button.setMemorySaveButtonEnabled(
    memoryTitle: String?,
    memoryStartDate: LocalDate?,
    memoryEndDate: LocalDate?,
    isPeriodActive: Boolean,
    isPhotoUploading: Boolean?,
) {
    val isPeriodNotExistent = (memoryStartDate == null) || (memoryEndDate == null)
    val isPeriodRequirementsInvalid = isPeriodActive && isPeriodNotExistent
    isEnabled =
        if (memoryTitle.isNullOrBlank() || isPhotoUploading == true || isPeriodRequirementsInvalid) {
            setTextColor(resources.getColor(R.color.gray4, null))
            false
        } else {
            setTextColor(resources.getColor(R.color.white, null))
            true
        }
}

@BindingAdapter("visitedAtSelectButtonEnabled")
fun Button.setVisitedAtSelectButtonEnabled(years: List<Int>?) {
    isEnabled =
        if (years.isNullOrEmpty()) {
            setTextColor(resources.getColor(R.color.gray4, null))
            false
        } else {
            setTextColor(resources.getColor(R.color.white, null))
            true
        }
}

@BindingAdapter("recoveryButtonEnabled")
fun Button.setRecoveryButtonEnabled(recoveryCode: String?) {
    isEnabled =
        if (recoveryCode.isNullOrBlank() || recoveryCode.length < MAX_RECOVERY_CODE) {
            setTextColor(resources.getColor(R.color.gray4, null))
            false
        } else {
            setTextColor(resources.getColor(R.color.white, null))
            true
        }
}

@BindingAdapter("currentLocationButtonLoading")
fun MaterialButton.setCurrentLocationButtonLoading(isLoading: Boolean?) {
    isClickable = isLoading == false
    if (isLoading == true) {
        setText(R.string.all_empty)
        setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
    } else {
        setText(R.string.staccato_creation_load_current_location)
    }
}

@BindingAdapter("sendButtonEnabled")
fun ImageButton.setSendButtonEnabled(value: String?) {
    isEnabled = !value.isNullOrBlank()
}

private const val MAX_RECOVERY_CODE = 36