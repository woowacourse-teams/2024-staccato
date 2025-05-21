package com.staccato.invitation.service;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.staccato.ServiceSliceTest;
import com.staccato.category.domain.Category;
import com.staccato.category.repository.CategoryRepository;
import com.staccato.fixture.category.CategoryFixtures;
import com.staccato.fixture.member.MemberFixtures;
import com.staccato.invitation.domain.CategoryInvitation;
import com.staccato.invitation.repository.CategoryInvitationRepository;
import com.staccato.invitation.service.dto.response.InvitationResultResponse;
import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class InviteProcessorTest extends ServiceSliceTest {

    @Autowired
    private InviteProcessor inviteProcessor;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private CategoryInvitationRepository categoryInvitationRepository;

    private Member host;
    private Member guest;
    private Category category;

    @BeforeEach
    void setUp() {
        host = MemberFixtures.defaultMember().withNickname("host").buildAndSave(memberRepository);
        guest = MemberFixtures.defaultMember().withNickname("guest").buildAndSave(memberRepository);
        category = CategoryFixtures.defaultCategory()
                .withHost(host)
                .buildAndSave(categoryRepository);
    }

    @DisplayName("초대 요청이 성공하면 성공 응답을 반환한다.")
    @Test
    void processSuccess() {
        // when
        InvitationResultResponse result = inviteProcessor.process(category, host, guest);

        // then
        assertAll(
                () -> assertThat(result.inviteeId()).isEqualTo(guest.getId()),
                () -> assertThat(result.statusCode()).isEqualTo("200 OK"),
                () -> assertThat(result.message()).isEqualTo("초대 요청에 성공하였습니다."),
                () -> assertThat(result.invitationId()).isNotNull(),
                () -> assertThat(categoryInvitationRepository.findAll()).hasSize(1)
        );
    }

    @DisplayName("이미 카테고리 멤버인 경우 실패 응답을 반환한다.")
    @Test
    void failIfAlreadyCategoryMember() {
        // given
        category = CategoryFixtures.defaultCategory()
                .withHost(host)
                .withGuests(List.of(guest))
                .buildAndSave(categoryRepository);

        // when
        InvitationResultResponse result = inviteProcessor.process(category, host, guest);

        // then
        assertAll(
                () -> assertThat(result.statusCode()).isEqualTo("400 BAD_REQUEST"),
                () -> assertThat(result.message()).contains("이미 카테고리에 함께하고 있는 사용자입니다."),
                () -> assertThat(categoryInvitationRepository.findAll()).hasSize(0)
        );
    }

    @DisplayName("이미 초대 요청을 보낸 경우 실패 응답을 반환한다.")
    @Test
    void failIfAlreadyRequested() {
        // given
        categoryInvitationRepository.save(CategoryInvitation.invite(category, host, guest));

        // when
        InvitationResultResponse result = inviteProcessor.process(category, host, guest);

        // then
        assertAll(
                () -> assertThat(result.statusCode()).isEqualTo("400 BAD_REQUEST"),
                () -> assertThat(result.message()).contains("이미 초대 요청을 보낸 사용자입니다."),
                () -> assertThat(categoryInvitationRepository.findAll()).hasSize(1)
        );
    }
}
