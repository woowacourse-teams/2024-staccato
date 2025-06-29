package com.on.staccato.presentation.common.notification

enum class NotificationType(val channelType: ChannelType) {
    ACCEPT_INVITATION(ChannelType.INVITATION),
    RECEIVE_INVITATION(ChannelType.CATEGORY),
    STACCATO_CREATED(ChannelType.CATEGORY),
    COMMENT_CREATED(ChannelType.COMMENT),
    ;

    companion object {
        fun from(type: String): NotificationType? = entries.firstOrNull { it.name == type }
    }
}
