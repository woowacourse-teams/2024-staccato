package com.staccato.moment.repository;

import com.staccato.category.domain.Category;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.staccato.RepositoryTest;
import com.staccato.fixture.category.CategoryFixture;
import com.staccato.fixture.moment.MomentFixture;
import com.staccato.category.repository.CategoryRepository;
import com.staccato.moment.domain.Moment;
import com.staccato.moment.domain.MomentImage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class MomentImageRepositoryTest extends RepositoryTest {
    @Autowired
    private MomentRepository momentRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private MomentImageRepository momentImageRepository;
    @PersistenceContext
    private EntityManager em;

    @DisplayName("특정 스타카토의 id 여러개를 가지고 있는 모든 스타카토 이미지들을 벌크 삭제한다.")
    @Test
    void deleteAllByMomentIdInBulk() {
        // given
        Category category = categoryRepository.save(
            CategoryFixture.create(LocalDate.of(2023, 12, 31), LocalDate.of(2024, 1, 10)));
        Moment moment1 = momentRepository.save(MomentFixture
                .createWithImages(
                    category, LocalDateTime.of(2023, 12, 31, 22, 20), List.of("url1", "url2")));
        Moment moment2 = momentRepository.save(MomentFixture
                .createWithImages(
                    category, LocalDateTime.of(2023, 12, 31, 22, 20), List.of("url1", "url2")));

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
        Category category = categoryRepository.save(
            CategoryFixture.create(LocalDate.of(2023, 12, 31), LocalDate.of(2024, 1, 10)));
        Moment moment = momentRepository.save(MomentFixture.createWithImages(
            category, LocalDateTime.of(2023, 12, 31, 22, 20), List.of("url1", "url2", "url3")));

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
