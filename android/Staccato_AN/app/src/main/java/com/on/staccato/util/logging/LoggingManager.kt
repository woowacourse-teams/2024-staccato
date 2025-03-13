package com.on.staccato.util.logging

interface LoggingManager {
    fun <T : Any> logEvent(
        name: String,
        vararg params: Param<T>,
    )
}
