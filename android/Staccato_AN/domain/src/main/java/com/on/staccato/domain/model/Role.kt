package com.on.staccato.domain.model

enum class Role(val value: String) {
    HOST("host"),
    GUEST("guest"),
    ;

    companion object {
        fun of(value: String) =
            when (value) {
                "host" -> HOST
                "guest" -> GUEST
                else -> throw IllegalArgumentException("유효하지 않은 Role 입니다.")
            }
    }
}
