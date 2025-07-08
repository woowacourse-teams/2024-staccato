package com.staccato.category.service.dto.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.staccato.category.domain.Category;
import com.staccato.category.domain.CategoryMember;
import com.staccato.config.domain.BaseEntity;
import com.staccato.fixture.category.CategoryFixtures;
import com.staccato.fixture.member.MemberFixtures;
import com.staccato.member.domain.Member;

public class CategoryDetailResponseV3Test {

    @DisplayName("멤버 정렬 - HOST -> 본인 -> 나머지 생성순")
    @Test
    void sortMembersByHostThenSelfThenCreatedAt() {
        // given
        Member host = MemberFixtures.defaultMember().withNickname("host").build();
        Member guestSelf = MemberFixtures.defaultMember().withNickname("guestSelf").build();
        Member guestOld = MemberFixtures.defaultMember().withNickname("guestOld").build();
        Member guestNew = MemberFixtures.defaultMember().withNickname("guestNew").build();
        Category category = CategoryFixtures.defaultCategory()
                .withHost(host)
                .withGuests(List.of(guestOld, guestSelf, guestNew))
                .build();

        LocalDateTime base = LocalDateTime.now().minusDays(10);
        List<CategoryMember> categoryMembers = category.getCategoryMembers();
        for (int i = 0; i < categoryMembers.size(); i++) {
            setCreatedAt(categoryMembers.get(i), base.plusDays(i));
        }

        // when
        CategoryDetailResponseV3 categoryDetailResponseV3 = new CategoryDetailResponseV3(category, List.of(), guestSelf);

        // then
        assertThat(categoryDetailResponseV3.members())
                .extracting(MemberDetailResponse::nickname)
                .containsExactly("host", "guestSelf", "guestOld", "guestNew");
    }

    private void setCreatedAt(CategoryMember categoryMember, LocalDateTime createdAt) {
        try {
            Field field = BaseEntity.class.getDeclaredField("createdAt");
            field.setAccessible(true);
            field.set(categoryMember, createdAt);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
