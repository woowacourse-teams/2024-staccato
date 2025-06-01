package com.on.staccato.data.invitation

import com.on.staccato.data.dto.invitation.InvitationRequest
import com.on.staccato.data.dto.invitation.InvitationResponse
import com.on.staccato.data.network.ApiResult
import retrofit2.http.Body
import retrofit2.http.POST

interface InvitationApiService {
    @POST(INVITATION_PATH)
    suspend fun postInvitation(
        @Body invitation: InvitationRequest,
    ): ApiResult<InvitationResponse>

    companion object {
        const val INVITATION_PATH = "/invitations"
    }
}
