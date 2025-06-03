package com.on.staccato.data.invitation

import com.on.staccato.data.dto.invitation.InvitationRequest
import com.on.staccato.data.dto.invitation.InvitationResponse
import com.on.staccato.data.dto.invitation.ReceivedInvitationsResponse
import com.on.staccato.data.dto.invitation.SentInvitationsResponse
import com.on.staccato.data.network.ApiResult
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface InvitationApiService {
    @POST(INVITATION_PATH)
    suspend fun postInvitation(
        @Body invitation: InvitationRequest,
    ): ApiResult<InvitationResponse>

    @GET(RECEIVED_INVITATION_PATH)
    suspend fun getReceivedInvitations(): ApiResult<ReceivedInvitationsResponse>

    @POST(INVITATION_ACCEPT_PATH)
    suspend fun postInvitationAccept(
        @Path(INVITATION_ID) invitationId: Long,
    ): ApiResult<Unit>

    @POST(INVITATION_REJECT_PATH)
    suspend fun postInvitationReject(
        @Path(INVITATION_ID) invitationId: Long,
    ): ApiResult<Unit>

    @GET(SENT_INVITATION_PATH)
    suspend fun getSentInvitations(): ApiResult<SentInvitationsResponse>

    @POST(INVITATION_CANCEL_PATH)
    suspend fun postInvitationCancel(
        @Path(INVITATION_ID) invitationId: Long,
    ): ApiResult<Unit>

    companion object {
        const val INVITATION_PATH = "/invitations"
        private const val INVITATION_ID = "invitationId"
        private const val RECEIVED_INVITATION_PATH = "$INVITATION_PATH/received"
        private const val SENT_INVITATION_PATH = "$INVITATION_PATH/sent"
        private const val INVITATION_PATH_WITH_ID = "$INVITATION_PATH/{$INVITATION_ID}"
        private const val INVITATION_ACCEPT_PATH = "$INVITATION_PATH_WITH_ID/accept"
        private const val INVITATION_REJECT_PATH = "$INVITATION_PATH_WITH_ID/reject"
        private const val INVITATION_CANCEL_PATH = "$INVITATION_PATH_WITH_ID/cancel"
    }
}
