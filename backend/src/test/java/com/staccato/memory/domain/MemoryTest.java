package com.staccato.memory.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.staccato.exception.StaccatoException;
import com.staccato.fixture.Member.MemberFixture;
import com.staccato.fixture.memory.MemoryFixture;
import com.staccato.fixture.moment.MomentFixture;
import com.staccato.member.domain.Member;
import com.staccato.member.domain.Nickname;
import com.staccato.moment.domain.Moment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MemoryTest {
    @DisplayName("추억 생성 시 제목에는 앞뒤 공백이 잘린다.")
    @Test
    void trimMemoryTitle() {
        // given
        String expectedTitle = "title";

        // when
        Memory memory = Memory.builder().title(" title ").build();

        // then
        assertThat(memory.getTitle()).isEqualTo(expectedTitle);
    }

    @DisplayName("기본 추억은 멤버 이름으로 만들어진다.")
    @Test
    void createBasicMemoryWithMemberNickname() {
        // given
        String nickname = "staccato";

        // when
        Memory memory = Memory.basic(new Nickname(nickname));

        // then
        assertThat(memory.getTitle()).isEqualTo(nickname + "의 추억");
    }

    @DisplayName("추억을 수정 시 기존 스타카토 기록 날짜를 포함하지 않는 경우 수정에 실패한다.")
    @Test
    void validateDuration() {
        // given
        Memory memory = MemoryFixture.create(LocalDate.now(), LocalDate.now().plusDays(1));
        Memory updatedMemory = MemoryFixture.create(LocalDate.now().plusDays(1), LocalDate.now().plusDays(2));
        Moment moment = MomentFixture.create(memory, LocalDateTime.now());

        // when & then
        assertThatThrownBy(() -> memory.update(updatedMemory, List.of(moment)))
                .isInstanceOf(StaccatoException.class)
                .hasMessage("기간이 이미 존재하는 스타카토를 포함하지 않아요. 다시 설정해주세요.");
    }

    @DisplayName("주어진 문자열과 제목이 같으면 거짓을 반환한다.")
    @Test
    void isNotSameTitle() {
        // given
        String title = "title";
        Memory memory = Memory.builder().title(title).build();

        // when
        boolean result = memory.isNotSameTitle(title);

        // then
        assertThat(result).isFalse();
    }
}
