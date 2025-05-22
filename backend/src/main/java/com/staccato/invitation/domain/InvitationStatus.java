package com.staccato.invitation.domain;

public enum InvitationStatus {

    REQUESTED,
    CANCELED,
    ACCEPTED,
    REJECTED;

    public boolean isAccepted() {
        return this == ACCEPTED;
    }

    public boolean isRejected() {
        return this == REJECTED;
    }
}
