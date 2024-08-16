package com.woowacourse.staccato.presentation.moment.adapter

enum class MomentViewHolderType(val value: Int) {
    MOMENT_DEFAULT(0),
    MY_COMMENTS(1),
    ;

    companion object {
        fun from(value: Int): MomentViewHolderType {
            return when (value) {
                0 -> {
                    MOMENT_DEFAULT
                }

                1 -> {
                    MY_COMMENTS
                }

                else -> {
                    throw IllegalArgumentException("")
                }
            }
        }
    }
}
