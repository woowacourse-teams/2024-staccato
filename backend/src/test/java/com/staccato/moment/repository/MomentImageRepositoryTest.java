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
import com.staccato.moment.domain.MomentImage;
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

    @DisplayName("특정 스타카토의 id 여러개를 가지고 있는 모든 스타카토 이미지들을 벌크 삭제한다.")
    @Test
    void deleteAllByMomentIdInBulk() {
        // given
        Memory memory = memoryRepository.save(MemoryFixture.create(LocalDate.of(2023, 12, 31), LocalDate.of(2024, 1, 10)));
        Moment moment1 = momentRepository.save(MomentFixture
                .createWithImages(memory, LocalDateTime.of(2023, 12, 31, 22, 20), new MomentImages(List.of("url1", "url2"))));
        Moment moment2 = momentRepository.save(MomentFixture
                .createWithImages(memory, LocalDateTime.of(2023, 12, 31, 22, 20), new MomentImages(List.of("url1", "url2"))));

        // when
        momentImageRepository.deleteAllByMomentIdInBulk(List.of(moment1.getId(), moment2.getId()));
        em.clear();

        // then
        assertAll(
                () -> assertThat(momentImageRepository.findAll()).isEqualTo(List.of()),
                () -> assertThat(momentRepository.findById(moment1.getId()).get().getMomentImages()
                        .isNotEmpty()).isFalse(),
                () -> assertThat(momentRepository.findById(moment2.getId()).get().getMomentImages()
                        .isNotEmpty()).isFalse()
        );
    }

    @DisplayName("특정 스타카토 이미지들을 벌크 삭제한다.")
    @Test
    void deleteAllByIdInBulk() {
        // given
        Memory memory = memoryRepository.save(MemoryFixture.create(LocalDate.of(2023, 12, 31), LocalDate.of(2024, 1, 10)));
        MomentImages momentImages = new MomentImages(List.of("url1", "url2", "url3"));
        Moment moment = momentRepository.save(MomentFixture.createWithImages(memory, LocalDateTime.of(2023, 12, 31, 22, 20), momentImages));

        List<Long> imageIds = moment.getMomentImages()
                .getImages()
                .stream()
                .map(MomentImage::getId)
                .toList();

        // when
        momentImageRepository.deleteAllByIdInBulk(imageIds);

        // then
        assertThat(momentImageRepository.findAll()).isEmpty();
    }
}
