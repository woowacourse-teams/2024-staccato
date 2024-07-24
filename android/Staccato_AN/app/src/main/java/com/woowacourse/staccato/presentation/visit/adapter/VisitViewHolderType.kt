package com.woowacourse.staccato.presentation.visit.adapter

enum class VisitViewHolderType(val value: Int) {
    VISIT_DEFAULT(0),
    MY_VISIT_LOG(1),
    ;

    companion object {
        fun of(value: Int): VisitViewHolderType {
            return when (value) {
                0 -> {
                    VISIT_DEFAULT
                }

                1 -> {
                    MY_VISIT_LOG
                }

                else -> {
                    throw IllegalArgumentException("")
                }
            }
        }
    }
}
