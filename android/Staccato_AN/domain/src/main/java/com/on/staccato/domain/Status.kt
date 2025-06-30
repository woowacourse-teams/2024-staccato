package com.on.staccato.domain

sealed class Status {
    data class Code(val code: Int) : Status()

    data class Message(val message: String) : Status()
}
