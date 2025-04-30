package com.on.staccato.presentation.util

import androidx.annotation.StringRes
import com.on.staccato.R

private const val REQUIRED_VALUES_ERROR_MESSAGE = "필수 값을 모두 입력해 주세요."
private const val NETWORK_ERROR_MESSAGE = "네트워크 연결이 불안정합니다.\n연결을 재설정한 후 다시 시도해 주세요."
private const val UNKNOWN_ERROR_MESSAGE = "예기치 못한 오류가 발생했습니다.\n잠시 후 다시 시도해 주세요."
private const val IMAGE_UPLOAD_ERROR_MESSAGE = "이미지 업로드에 실패했습니다."

enum class ExceptionState(val message: String) {
    NetworkError(NETWORK_ERROR_MESSAGE),
    RequiredValuesMissing(REQUIRED_VALUES_ERROR_MESSAGE),
    ImageUploadError(IMAGE_UPLOAD_ERROR_MESSAGE),
    UnknownError(UNKNOWN_ERROR_MESSAGE),
}

enum class ExceptionState2(
    @StringRes val messageId: Int,
) {
    NetworkError(R.string.error_message_network),
    RequiredValuesMissing(R.string.error_message_required_values),
    ImageUploadError(R.string.error_message_image_upload),
    UnknownError(R.string.error_message_unknown),
}
