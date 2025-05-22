package com.staccato.invitation.service;

import org.springframework.stereotype.Service;
import com.staccato.category.repository.CategoryMemberRepository;
import com.staccato.config.log.annotation.Trace;
import com.staccato.invitation.repository.CategoryInvitationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Trace
@Service
@RequiredArgsConstructor
public class InviteProcessor {
    private final CategoryMemberRepository categoryMemberRepository;
    private final CategoryInvitationRepository categoryInvitationRepository;


}
