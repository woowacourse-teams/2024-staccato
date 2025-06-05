package com.staccato.notification.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.staccato.config.log.annotation.Trace;
import com.staccato.invitation.domain.InvitationStatus;
import com.staccato.invitation.repository.CategoryInvitationRepository;
import com.staccato.member.domain.Member;
import com.staccato.notification.service.dto.response.NotificationExistResponse;
import lombok.RequiredArgsConstructor;

@Trace
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {
    public NotificationExistResponse isExistNotifications(Member member) {
        return null;
    }
}
