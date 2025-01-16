package com.on.staccato.domain.model

data class MemberProfile(
    val profileImageUrl: String? = null,
    val nickname: String,
    val uuidCode: String,
) {
    fun isValid() = uuidCode.isNotEmpty() && nickname.isNotEmpty()
}
