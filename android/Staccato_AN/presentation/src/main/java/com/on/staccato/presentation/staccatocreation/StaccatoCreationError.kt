package com.on.staccato.presentation.staccatocreation

import com.on.staccato.presentation.common.MessageEvent

sealed interface StaccatoCreationError

data class AllCandidates(val messageEvent: MessageEvent) : StaccatoCreationError

data class StaccatoCreate(val messageEvent: MessageEvent) : StaccatoCreationError
