package com.on.staccato.presentation.staccatoupdate

import com.on.staccato.presentation.common.MessageEvent

sealed interface StaccatoUpdateError

data class AllCandidates(val messageEvent: MessageEvent) : StaccatoUpdateError

data class StaccatoInitialize(val messageEvent: MessageEvent) : StaccatoUpdateError

data class StaccatoUpdate(val messageEvent: MessageEvent) : StaccatoUpdateError
