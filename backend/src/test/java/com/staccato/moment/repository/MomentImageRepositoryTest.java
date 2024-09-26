package com.staccato.moment.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import com.staccato.fixture.memory.MemoryFixture;
import com.staccato.fixture.moment.MomentFixture;
import com.staccato.memory.domain.Memory;
import com.staccato.memory.repository.MemoryRepository;
import com.staccato.moment.domain.Moment;
import com.staccato.moment.domain.MomentImages;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
class MomentImageRepositoryTest {
    @Autowired
    private MomentRepository momentRepository;
    @Autowired
    private MemoryRepository memoryRepository;
    @Autowired
    private MomentImageRepository momentImageRepository;
    @PersistenceContext
    private EntityManager em;

    @DisplayName("특정 스타카토의 id를 가지고 있는 모든 스타카토 이미지들을 삭제한다.")
    @Test
    void deleteAllByMomentIdInBatch() {
        // given
        Memory memory = memoryRepository.save(MemoryFixture.create(LocalDate.of(2023, 12, 31), LocalDate.of(2024, 1, 10)));
        Moment moment = momentRepository.save(MomentFixture
                .createWithImages(memory, LocalDateTime.of(2023, 12, 31, 22, 20), new MomentImages(List.of("url1", "url2"))));

        // when
        momentImageRepository.deleteAllByMomentIdInBatch(moment.getId());
        em.flush();
        em.clear();

        // then
        assertAll(
                () -> assertThat(momentImageRepository.findAll()).isEqualTo(List.of()),
                () -> assertThat(momentRepository.findById(moment.getId()).get().getMomentImages()
                        .isNotEmpty()).isFalse()
        );
    }
}
