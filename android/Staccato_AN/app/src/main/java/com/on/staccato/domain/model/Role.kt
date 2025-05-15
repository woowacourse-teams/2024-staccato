package com.on.staccato.domain.model

enum class Role(val value: String) {
    Host("host"),
    Guest("guest"),
    ;

    companion object {
        fun of(value: String) =
            when (value) {
                "host" -> Host
                "guest" -> Guest
                else -> throw IllegalArgumentException("유효하지 않은 Role 입니다.")
            }
    }
}
