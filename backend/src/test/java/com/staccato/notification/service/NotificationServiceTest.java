package com.staccato.notification.service;

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
import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;
import com.staccato.notification.service.dto.response.NotificationExistResponse;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationServiceTest extends ServiceSliceTest {
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CategoryInvitationRepository categoryInvitationRepository;

    @DisplayName("받은 초대 목록이 존재한다면, 알림이 존재한다.")
    @Test
    void isExistNotifications() {
        // given
        Member host = MemberFixtures.defaultMember().withNickname("host").buildAndSave(memberRepository);
        Member guest = MemberFixtures.defaultMember().withNickname("guest").buildAndSave(memberRepository);
        Category category = CategoryFixtures.defaultCategory().withHost(host).buildAndSave(categoryRepository);
        categoryInvitationRepository.save(CategoryInvitation.invite(category, host, guest));

        // when
        NotificationExistResponse result = notificationService.isExistNotifications(guest);

        // then
        assertThat(result.isExist()).isTrue();
    }

    @DisplayName("받은 초대 목록이 존재하지 않는다면, 알림이 존재하지 않는다.")
    @Test
    void isNotExistNotifications() {
        // given
        Member guest = MemberFixtures.defaultMember().withNickname("guest").buildAndSave(memberRepository);

        // when
        NotificationExistResponse result = notificationService.isExistNotifications(guest);

        // then
        assertThat(result.isExist()).isFalse();
    }
}
