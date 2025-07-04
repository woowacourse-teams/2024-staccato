package com.on.staccato.presentation.category.adapter

enum class MembersViewType(val viewType: Int) {
    MEMBER_INVITE(0),
    MEMBER_PROFILE(1),
    ;

    companion object {
        fun from(viewType: Int): MembersViewType {
            return when (viewType) {
                0 -> MEMBER_INVITE
                1 -> MEMBER_PROFILE
                else -> throw IllegalArgumentException("Invalid viewType: $viewType")
            }
        }
    }
}
