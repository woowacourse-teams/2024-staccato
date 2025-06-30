package com.on.staccato.domain.model

data class Participants(val members: List<Participant>)

val emptyParticipants = Participants(emptyList())

fun Participants.toMembers() = Members(members.map { it.member })
