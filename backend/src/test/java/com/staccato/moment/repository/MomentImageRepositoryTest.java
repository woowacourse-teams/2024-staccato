package com.staccato.moment.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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

    @DisplayName("특정 스타카토의 id 여러개를 가지고 있는 모든 스타카토 이미지들을 삭제한다.")
    @Test
    void deleteAllByMomentIdInBatch() {
        // given
        Memory memory = memoryRepository.save(MemoryFixture.create(LocalDate.of(2023, 12, 31), LocalDate.of(2024, 1, 10)));
        Moment moment1 = momentRepository.save(MomentFixture.create(memory, LocalDateTime.of(2023, 12, 31, 22, 20)));
        Moment moment2 = momentRepository.save(MomentFixture.create(memory, LocalDateTime.of(2023, 12, 31, 22, 20)));
        List<MomentImage> momentImages1 = List.of(new MomentImage("url1", moment1), new MomentImage("url2", moment1));
        List<MomentImage> momentImages2 = List.of(new MomentImage("url1", moment2), new MomentImage("url2", moment2));
        momentImageRepository.saveAll(momentImages1);
        momentImageRepository.saveAll(momentImages2);

        // when
        momentImageRepository.deleteAllByMomentIdInBatch(List.of(moment1.getId(), moment2.getId()));

        // then
        assertAll(
                () -> assertThat(momentImageRepository.findAll()).isEqualTo(List.of()),
                () -> assertThat(momentImageRepository.findAllByMomentId(moment1.getId()).size()).isEqualTo(0),
                () -> assertThat(momentImageRepository.findAllByMomentId(moment2.getId()).size()).isEqualTo(0)
        );
    }

    @DisplayName("특정 스타카토 이미지들을 배치 삭제한다.")
    @Test
    void deleteAllByIdInBatch() {
        // given
        Memory memory = memoryRepository.save(MemoryFixture.create(LocalDate.of(2023, 12, 31), LocalDate.of(2024, 1, 10)));
        Moment moment = momentRepository.save(MomentFixture.create(memory, LocalDateTime.of(2023, 12, 31, 22, 20)));

        MomentImage momentImage1 = new MomentImage("url1", moment);
        MomentImage momentImage2 = new MomentImage("url2", moment);
        MomentImage momentImage3 = new MomentImage("url3", moment);
        momentImageRepository.saveAll(List.of(momentImage1, momentImage2, momentImage3));

        // when
        momentImageRepository.deleteAllByIdInBatch(List.of(momentImage1.getId(), momentImage2.getId(), momentImage3.getId()));

        // then
        assertThat(momentImageRepository.findAll()).isEmpty();
    }

    @DisplayName("특정 스타카토의 모든 스타카토 이미지를 조회한다.")
    @Test
    void findAllByMomentId() {
        // given
        Memory memory = memoryRepository.save(MemoryFixture.create(LocalDate.of(2023, 12, 31), LocalDate.of(2024, 1, 10)));
        Moment moment = momentRepository.save(MomentFixture.create(memory, LocalDateTime.of(2023, 12, 31, 22, 20)));

        List<MomentImage> momentImages = List.of(new MomentImage("url1", moment), new MomentImage("url2", moment), new MomentImage("url3", moment));
        momentImageRepository.saveAll(momentImages);

        // when
        List<MomentImage> retrievedImages = momentImageRepository.findAllByMomentId(moment.getId());

        // then
        assertThat(retrievedImages).hasSize(3)
                .extracting(MomentImage::getImageUrl)
                .containsExactlyInAnyOrder("url1", "url2", "url3");
    }

    @DisplayName("특정 스타카토의 이미지 중 가장 작은 ID 값을 가진 이미지를 조회한다.")
    @Test
    void findFirstByMomentIdOrderByIdAsc() {
        // given
        Memory memory = memoryRepository.save(MemoryFixture.create(LocalDate.of(2023, 12, 31), LocalDate.of(2024, 1, 10)));
        Moment moment = momentRepository.save(MomentFixture.create(memory, LocalDateTime.of(2023, 12, 31, 22, 20)));

        List<MomentImage> momentImages = List.of(new MomentImage("url1", moment), new MomentImage("url2", moment), new MomentImage("url3", moment));
        momentImageRepository.saveAll(momentImages);

        // when
        Optional<MomentImage> image = momentImageRepository.findFirstByMomentIdOrderByIdAsc(moment.getId());

        // then
        assertThat(image.get().getImageUrl()).isEqualTo("url1");
    }
}
