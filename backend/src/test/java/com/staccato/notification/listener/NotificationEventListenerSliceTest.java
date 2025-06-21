package com.staccato.notification.listener;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.staccato.category.domain.Category;
import com.staccato.category.repository.CategoryMemberRepository;
import com.staccato.fixture.member.MemberFixtures;
import com.staccato.member.domain.Member;
import com.staccato.notification.service.NotificationService;
import com.staccato.staccato.service.dto.event.StaccatoCreatedEvent;

@ExtendWith(MockitoExtension.class)
public class NotificationEventListenerSliceTest {

    @Mock
    CategoryMemberRepository categoryMemberRepository;
    @Mock
    NotificationService notificationService;
    @InjectMocks
    private NotificationEventListener listener;

    @DisplayName("스타카토를 만든 멤버에게는 새로운 스타카토 알림을 보내지 않는다.")
    @Test
    void handleNewStaccatoShouldExcludeCreator() {
        // given
        Member creator = MemberFixtures.defaultMember()
                .withNickname("creator").build();
        Member member1 = MemberFixtures.defaultMember()
                .withNickname("member1").build();
        Member member2 = MemberFixtures.defaultMember()
                .withNickname("member2").build();

        Category sharedCategory = mock(Category.class);
        when(sharedCategory.getIsShared()).thenReturn(true);
        when(sharedCategory.getId()).thenReturn(1L);
        when(categoryMemberRepository.findAllMembersByCategoryId(1L))
                .thenReturn(List.of(creator, member1, member2));

        StaccatoCreatedEvent event = new StaccatoCreatedEvent(creator, sharedCategory);

        // when
        listener.handleNewStaccato(event);

        // then
        verify(notificationService).sendNewStaccatoAlert(
                eq(creator),
                eq(sharedCategory),
                argThat(targets ->
                        targets.size() == 2 &&
                        !targets.contains(creator) &&
                        targets.containsAll(List.of(member1, member2))
                )
        );
    }
}
