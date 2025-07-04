package com.on.staccato.presentation.bindingadapter

import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.databinding.BindingAdapter
import com.google.android.material.button.MaterialButton
import com.on.staccato.domain.model.CategoryCandidate
import com.on.staccato.domain.model.NicknameState
import com.on.staccato.presentation.R
import com.on.staccato.presentation.color.CategoryColor
import com.on.staccato.presentation.common.input.InputState
import com.on.staccato.presentation.mapper.toInputState
import com.on.staccato.presentation.photo.PhotosUiModel
import java.time.LocalDate
import java.time.LocalDateTime

@BindingAdapter("loginButtonEnabled")
fun Button.setLoginButtonEnabled(nicknameState: NicknameState) {
    val inputState = nicknameState.toInputState(context)
    isEnabled =
        when (inputState) {
            is InputState.Empty -> {
                setTextColor(resources.getColor(R.color.gray4, null))
                false
            }

            is InputState.Invalid -> {
                setTextColor(resources.getColor(R.color.gray4, null))
                false
            }

            is InputState.Valid -> {
                setTextColor(resources.getColor(R.color.white, null))
                true
            }
        }
}

@BindingAdapter(
    value = ["staccatoTitle", "staccatoAddress", "staccatoVisitedAt", "staccatoPhotos", "staccatoCategory"],
)
fun Button.setStaccatoSaveButtonEnabled(
    staccatoTitle: String?,
    staccatoAddress: String?,
    staccatoVisitedAt: LocalDateTime?,
    staccatoPhotos: PhotosUiModel?,
    staccatoCategory: CategoryCandidate?,
) {
    isEnabled =
        if (staccatoTitle.isNullOrBlank() ||
            staccatoAddress == null ||
            staccatoCategory == null ||
            staccatoVisitedAt == null ||
            staccatoPhotos?.hasNotSuccessPhoto() == true
        ) {
            setTextColor(resources.getColor(R.color.gray4, null))
            false
        } else {
            setTextColor(resources.getColor(R.color.white, null))
            true
        }
}

@BindingAdapter(
    value = ["categoryTitle", "categoryStartDate", "categoryEndDate", "isPeriodActive", "isPhotoUploading"],
)
fun Button.setCategorySaveButtonEnabled(
    categoryTitle: String?,
    categoryStartDate: LocalDate?,
    categoryEndDate: LocalDate?,
    isPeriodActive: Boolean,
    isPhotoUploading: Boolean?,
) {
    val isPeriodNotExistent = (categoryStartDate == null) || (categoryEndDate == null)
    val isPeriodRequirementsInvalid = isPeriodActive && isPeriodNotExistent
    isEnabled =
        if (categoryTitle.isNullOrBlank() || isPhotoUploading == true || isPeriodRequirementsInvalid) {
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

@BindingAdapter("colorSelectButtonEnabled")
fun Button.setColorSelectButtonEnabled(selectedColor: CategoryColor?) {
    isEnabled =
        if (selectedColor == null) {
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

@BindingAdapter("visibilityByIsBottomSheetExpanded")
fun ImageButton.setVisibilityBy(isBottomSheetExpanded: Boolean?) {
    visibility =
        if (isBottomSheetExpanded == true) {
            View.VISIBLE
        } else {
            View.GONE
        }
}

private const val MAX_RECOVERY_CODE = 36
