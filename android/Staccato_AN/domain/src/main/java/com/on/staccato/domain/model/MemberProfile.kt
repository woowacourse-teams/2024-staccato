package com.on.staccato.domain.model

data class MemberProfile(
    val memberId: Long,
    val profileImageUrl: String? = null,
    val nickname: String,
    val uuidCode: String,
) {
    fun isValid() = memberId != INVALID_MEMBER_ID && uuidCode.isNotEmpty() && nickname.isNotEmpty()

    companion object {
        const val EMPTY_STRING = ""
        const val INVALID_MEMBER_ID = 0L
    }
}
