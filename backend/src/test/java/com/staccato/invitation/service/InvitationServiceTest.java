package com.staccato.invitation.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import com.staccato.ServiceSliceTest;
import com.staccato.category.domain.Category;
import com.staccato.category.domain.CategoryMember;
import com.staccato.category.repository.CategoryRepository;
import com.staccato.exception.ForbiddenException;
import com.staccato.exception.StaccatoException;
import com.staccato.fixture.category.CategoryFixtures;
import com.staccato.fixture.member.MemberFixtures;
import com.staccato.invitation.domain.CategoryInvitation;
import com.staccato.invitation.domain.InvitationStatus;
import com.staccato.invitation.repository.CategoryInvitationRepository;
import com.staccato.invitation.service.dto.request.CategoryInvitationRequest;
import com.staccato.invitation.service.dto.response.CategoryInvitationRequestedResponse;
import com.staccato.invitation.service.dto.response.CategoryInvitationRequestedResponses;
import com.staccato.invitation.service.dto.response.InvitationResultResponse;
import com.staccato.invitation.service.dto.response.InvitationResultResponses;
import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class InvitationServiceTest extends ServiceSliceTest {
    @Autowired
    private InvitationService invitationService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CategoryInvitationRepository categoryInvitationRepository;

    private Member host;
    private Member guest;
    private Category category;

    @BeforeEach
    void init() {
        host = MemberFixtures.defaultMember().withNickname("host").buildAndSave(memberRepository);
        guest = MemberFixtures.defaultMember().withNickname("guest").buildAndSave(memberRepository);
        category = CategoryFixtures.defaultCategory()
                .withHost(host)
                .buildAndSave(categoryRepository);
    }

    @DisplayName("HOST가 카테고리에 guest과 guest2에 초대 요청(REQUESTED)을 한다.")
    @Test
    void invite() {
        // given
        Member guest2 = MemberFixtures.defaultMember().withNickname("guest2").buildAndSave(memberRepository);
        CategoryInvitationRequest invitationRequest = new CategoryInvitationRequest(category.getId(), Set.of(guest.getId(), guest2.getId()));

        // when
        InvitationResultResponses responses = invitationService.invite(host, invitationRequest);

        // then
        List<CategoryMember> categoryMembers = categoryRepository.findWithCategoryMembersById(category.getId()).get()
                .getCategoryMembers();
        List<CategoryInvitation> invitations = categoryInvitationRepository.findAllWithCategoryAndInviteeByInviterIdOrderByCreatedAtDesc(host.getId());
        List<Member> invitees = invitations.stream().map(CategoryInvitation::getInvitee).collect(Collectors.toList());
        Set<Category> categories = invitations.stream().map(CategoryInvitation::getCategory)
                .collect(Collectors.toSet());
        Set<InvitationStatus> statuses = invitations.stream().map(CategoryInvitation::getStatus)
                .collect(Collectors.toSet());

        assertAll(
                () -> assertThat(responses.invitationResults()).hasSize(2),
                () -> assertThat(responses.invitationResults().get(0).statusCode()).isEqualTo(HttpStatus.OK.toString()),
                () -> assertThat(responses.invitationResults().get(1).statusCode()).isEqualTo(HttpStatus.OK.toString()),
                () -> assertThat(categoryMembers).hasSize(1),
                () -> assertThat(invitees).containsExactlyInAnyOrder(guest, guest2),
                () -> assertThat(categories).containsExactly(category),
                () -> assertThat(statuses).containsExactly(InvitationStatus.REQUESTED)
        );
    }

    @DisplayName("특정 카테고리의 HOST가 아닌 멤버가 카테고리 초대 요청을 보내면 예외가 발생한다.")
    @Test
    void failToInviteIfNotHost() {
        // given
        Member anotherUser = MemberFixtures.defaultMember().withNickname("anotherUser").buildAndSave(memberRepository);
        CategoryInvitationRequest invitationRequest = new CategoryInvitationRequest(category.getId(), Set.of(guest.getId()));

        // when & then
        assertThatThrownBy(() -> invitationService.invite(anotherUser, invitationRequest))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("요청하신 작업을 처리할 권한이 없습니다.");
    }

    @DisplayName("닉네임 목록 중 일부가 존재하지 않아도 존재하는 멤버만 초대 요청된다.")
    @Test
    void inviteOnlyExistingMembers() {
        // given
        long unknownId = 0;
        CategoryInvitationRequest invitationRequest = new CategoryInvitationRequest(category.getId(), Set.of(guest.getId(), unknownId));

        // when
        invitationService.invite(host, invitationRequest);

        // then
        List<CategoryInvitation> invitations = categoryInvitationRepository.findAllWithCategoryAndInviteeByInviterIdOrderByCreatedAtDesc(host.getId());

        assertAll(
                () -> assertThat(invitations).hasSize(1),
                () -> assertThat(invitations.get(0).getInvitee()).isEqualTo(guest)
        );
    }

    @DisplayName("이미 함께하는 사람을 다시 초대하려고 하면 예외를 반환한다.")
    @Test
    void cannotInviteIfAlreadyCategoryMember() {
        // given
        category = CategoryFixtures.defaultCategory()
                .withHost(host)
                .withGuests(List.of(guest))
                .buildAndSave(categoryRepository);
        CategoryInvitationRequest invitationRequest = new CategoryInvitationRequest(category.getId(), Set.of(guest.getId()));

        // when
        InvitationResultResponses responses = invitationService.invite(host, invitationRequest);

        // then
        assertThat(responses.invitationResults()).hasSize(1)
                .containsExactly(InvitationResultResponse.fail(guest, "이미 카테고리에 함께하고 있는 사용자입니다."));
    }

    @DisplayName("초대 요청을 한 사용자를 다시 초대하려고 하면 예외를 반환한다.")
    @Test
    void cannotInviteIfAlreadyRequested() {
        // given
        CategoryInvitationRequest invitationRequest = new CategoryInvitationRequest(category.getId(), Set.of(guest.getId()));
        invitationService.invite(host, invitationRequest);

        // when
        InvitationResultResponses responses = invitationService.invite(host, invitationRequest);

        // then
        assertThat(responses.invitationResults()).hasSize(1)
                .containsExactly(InvitationResultResponse.fail(guest, "이미 초대 요청을 보낸 사용자입니다."));
    }

    @DisplayName("초대 요청 목록을 조회하면, 최근에 요청을 보낸 사용자 순으로 보여준다.")
    @Test
    void readAllInvitationRequested() {
        // given
        Member guest2 = MemberFixtures.defaultMember().withNickname("guest2").buildAndSave(memberRepository);
        CategoryInvitation invitation = categoryInvitationRepository.save(CategoryInvitation.invite(category, host, guest));
        CategoryInvitation invitation2 = categoryInvitationRepository.save(CategoryInvitation.invite(category, host, guest2));

        // when
        CategoryInvitationRequestedResponses responses = invitationService.readInvitations(host);

        // then
        assertAll(
                () -> assertThat(responses.invitations()).hasSize(2),
                () -> assertThat(responses.invitations()).containsExactly(
                        new CategoryInvitationRequestedResponse(invitation2),
                        new CategoryInvitationRequestedResponse(invitation)
                )
        );
    }

    @DisplayName("inviter는 본인이 보낸 초대 요청을 취소할 수 있다.")
    @Test
    void cancelInvitation() {
        // given
        CategoryInvitation invitation = categoryInvitationRepository.save(CategoryInvitation.invite(category, host, guest));

        // when
        invitationService.cancel(host, invitation.getId());

        // then
        assertThat(categoryInvitationRepository.findById(invitation.getId()).get()
                .getStatus()).isEqualTo(InvitationStatus.CANCELED);
    }

    @DisplayName("초대 요청을 보낸 사용자가 아닌 다른 사용자가 초대 요청을 취소하면 예외가 발생한다.")
    @Test
    void failToCancelIfNotInviter() {
        // given
        CategoryInvitation invitation = categoryInvitationRepository.save(CategoryInvitation.invite(category, host, guest));

        // when & then
        assertThatThrownBy(() -> invitationService.cancel(guest, invitation.getId()))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("요청하신 작업을 처리할 권한이 없습니다.");
    }

    @DisplayName("존재하지 않는 초대 요청을 취소하면 예외가 발생한다.")
    @Test
    void failToCancelIfNotExists() {
        // given
        long unknownId = 0;

        // then
        assertThatThrownBy(() -> invitationService.cancel(host, unknownId))
                .isInstanceOf(StaccatoException.class)
                .hasMessage("요청하신 초대 정보를 찾을 수 없어요.");
    }
}
