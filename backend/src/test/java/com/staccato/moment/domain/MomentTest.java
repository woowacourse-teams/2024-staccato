package com.staccato.moment.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import com.staccato.exception.StaccatoException;
import com.staccato.fixture.Member.MemberFixture;
import com.staccato.fixture.memory.MemoryFixture;
import com.staccato.fixture.moment.MomentFixture;
import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;
import com.staccato.memory.domain.Memory;
import com.staccato.memory.repository.MemoryRepository;
import com.staccato.moment.repository.MomentRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MomentTest {
    @DisplayName("추억 날짜 안에 스타카토 날짜가 포함되면 Moment을 생성할 수 있다.")
    @Test
    void createMoment() {
        // given
        Memory memory = MemoryFixture.create(LocalDate.now(), LocalDate.now().plusDays(1));
        LocalDateTime visitedAt = LocalDateTime.now().plusDays(1);

        // when & then
        assertThatCode(() -> Moment.builder()
                .visitedAt(visitedAt)
                .title("staccatoTitle")
                .placeName("placeName")
                .latitude(BigDecimal.ONE)
                .longitude(BigDecimal.ONE)
                .address("address")
                .memory(memory)
                .momentImages(new MomentImages(List.of()))
                .build())
                .doesNotThrowAnyException();
    }

    @DisplayName("추억 기간이 없는 경우 스타카토를 날짜 상관없이 생성할 수 있다.")
    @Test
    void createMomentInUndefinedDuration() {
        // given
        Memory memory = MemoryFixture.create(null, null);
        LocalDateTime visitedAt = LocalDateTime.now().plusDays(1);

        // when & then
        assertThatCode(() -> Moment.builder()
                .visitedAt(visitedAt)
                .title("staccatoTitle")
                .placeName("placeName")
                .latitude(BigDecimal.ONE)
                .longitude(BigDecimal.ONE)
                .address("address")
                .memory(memory)
                .momentImages(new MomentImages(List.of()))
                .build())
                .doesNotThrowAnyException();
    }

    @DisplayName("스타카토 생성 시 title의 앞 뒤 공백이 제거된다.")
    @Test
    void trimPlaceName() {
        // given
        Memory memory = MemoryFixture.create(LocalDate.now(), LocalDate.now().plusDays(1));
        LocalDateTime visitedAt = LocalDateTime.now().plusDays(1);
        String title = " staccatoTitle ";
        String expectedTitle = "staccatoTitle";

        // when
        Moment moment = Moment.builder()
                .visitedAt(visitedAt)
                .title(title)
                .latitude(BigDecimal.ONE)
                .longitude(BigDecimal.ONE)
                .placeName("placeName")
                .address("address")
                .memory(memory)
                .momentImages(new MomentImages(List.of()))
                .build();

        // then
        assertThat(moment.getTitle()).isEqualTo(expectedTitle);
    }

    @DisplayName("추억 날짜 안에 스타카토 날짜가 포함되지 않으면 예외를 발생시킨다.")
    @ValueSource(longs = {-1, 2})
    @ParameterizedTest
    void failCreateMoment(long plusDays) {
        // given
        Memory memory = MemoryFixture.create(LocalDate.now(), LocalDate.now().plusDays(1));
        LocalDateTime visitedAt = LocalDateTime.now().plusDays(plusDays);

        // when & then
        assertThatThrownBy(() -> Moment.builder()
                .visitedAt(visitedAt)
                .title("staccatoTitle")
                .placeName("placeName")
                .latitude(BigDecimal.ONE)
                .longitude(BigDecimal.ONE)
                .address("address")
                .memory(memory)
                .momentImages(new MomentImages(List.of()))
                .build())
                .isInstanceOf(StaccatoException.class)
                .hasMessageContaining("추억에 포함되지 않는 날짜입니다.");
    }

    @Nested
    @Transactional
    @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
    class TouchForWriteTest {
        @Autowired
        private MemberRepository memberRepository;
        @Autowired
        private MemoryRepository memoryRepository;
        @Autowired
        private MomentRepository momentRepository;
        @PersistenceContext
        private EntityManager entityManager;

        @DisplayName("Moment 생성 시 Memory의 updatedAt이 갱신된다.")
        @Test
        void updateMemoryUpdatedDateWhenMomentCreated() {
            // given
            Member member = memberRepository.save(MemberFixture.create());
            Memory memory = memoryRepository.save(MemoryFixture.createWithMember(member));
            LocalDateTime beforeCreate = memory.getUpdatedAt();

            // when
            momentRepository.save(MomentFixture.create(memory));
            entityManager.flush();
            entityManager.refresh(memory);
            LocalDateTime afterCreate = memory.getUpdatedAt();

            // then
            assertThat(afterCreate).isAfter(beforeCreate);
        }

        @DisplayName("Moment 수정 시 Memory의 updatedAt이 갱신된다.")
        @Test
        void updateMemoryUpdatedDateWhenMomentUpdated() {
            // given
            Member member = memberRepository.save(MemberFixture.create());
            Memory memory = memoryRepository.save(MemoryFixture.createWithMember(member));
            Moment moment = momentRepository.save(MomentFixture.create(memory));
            LocalDateTime beforeUpdate = memory.getUpdatedAt();

            // when
            moment.changeFeeling(Feeling.ANGRY);
            entityManager.flush();
            entityManager.refresh(memory);
            LocalDateTime afterUpdate = memory.getUpdatedAt();

            // then
            assertThat(afterUpdate).isAfter(beforeUpdate);
        }

        @DisplayName("Moment 삭제 시 Memory의 updatedAt이 갱신된다.")
        @Test
        void updateMemoryUpdatedDateWhenMomentDeleted() {
            // given
            Member member = memberRepository.save(MemberFixture.create());
            Memory memory = memoryRepository.save(MemoryFixture.createWithMember(member));
            Moment moment = momentRepository.save(MomentFixture.create(memory));
            LocalDateTime beforeDelete = memory.getUpdatedAt();

            // when
            momentRepository.delete(moment);
            entityManager.flush();
            entityManager.refresh(memory);
            LocalDateTime afterDelete = memory.getUpdatedAt();

            // then
            assertThat(afterDelete).isAfter(beforeDelete);
        }
    }
}
