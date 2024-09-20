package com.staccato.member.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.staccato.member.domain.Member;
import com.staccato.member.service.dto.response.MemberProfileResponse;

@Service
public class MemberService {
    public MemberProfileResponse changeProfile(Member member, MultipartFile image) {
        return null;
    }
}
