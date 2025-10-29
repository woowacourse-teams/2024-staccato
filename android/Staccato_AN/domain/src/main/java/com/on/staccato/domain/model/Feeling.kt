package com.on.staccato.domain.model

enum class Feeling(val value: String) {
    HAPPY("happy"),
    ANGRY("angry"),
    SAD("sad"),
    SCARED("scared"),
    EXCITED("excited"),
    NOTHING("nothing"),
    ;

    companion object {
        fun fromValue(value: String): Feeling =
            when (value) {
                HAPPY.value -> HAPPY
                ANGRY.value -> ANGRY
                SAD.value -> SAD
                SCARED.value -> SCARED
                EXCITED.value -> EXCITED
                else -> NOTHING
            }
    }
}
