package com.staccato.moment.repository;

import com.staccato.category.domain.Category;
import com.staccato.moment.domain.Staccato;
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
import com.staccato.fixture.moment.StaccatoFixture;
import com.staccato.category.repository.CategoryRepository;
import com.staccato.moment.domain.StaccatoImage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class StaccatoImageRepositoryTest extends RepositoryTest {
    @Autowired
    private StaccatoRepository staccatoRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private StaccatoImageRepository staccatoImageRepository;
    @PersistenceContext
    private EntityManager em;

    @DisplayName("특정 스타카토의 id 여러개를 가지고 있는 모든 스타카토 이미지들을 벌크 삭제한다.")
    @Test
    void deleteAllByStaccatoIdInBulk() {
        // given
        Category category = categoryRepository.save(
            CategoryFixture.create(LocalDate.of(2023, 12, 31), LocalDate.of(2024, 1, 10)));
        Staccato staccato1 = staccatoRepository.save(StaccatoFixture
                .createWithImages(
                    category, LocalDateTime.of(2023, 12, 31, 22, 20), List.of("url1", "url2")));
        Staccato staccato2 = staccatoRepository.save(StaccatoFixture
                .createWithImages(
                    category, LocalDateTime.of(2023, 12, 31, 22, 20), List.of("url1", "url2")));

        // when
        staccatoImageRepository.deleteAllByStaccatoIdInBulk(List.of(staccato1.getId(), staccato2.getId()));
        em.clear();

        // then
        assertAll(
                () -> assertThat(staccatoImageRepository.findAll()).isEqualTo(List.of()),
                () -> assertThat(staccatoRepository.findById(staccato1.getId()).get().getStaccatoImages()
                        .isNotEmpty()).isFalse(),
                () -> assertThat(staccatoRepository.findById(staccato2.getId()).get().getStaccatoImages()
                        .isNotEmpty()).isFalse()
        );
    }

    @DisplayName("특정 스타카토 이미지들을 벌크 삭제한다.")
    @Test
    void deleteAllByIdInBulk() {
        // given
        Category category = categoryRepository.save(
            CategoryFixture.create(LocalDate.of(2023, 12, 31), LocalDate.of(2024, 1, 10)));
        Staccato staccato = staccatoRepository.save(StaccatoFixture.createWithImages(
            category, LocalDateTime.of(2023, 12, 31, 22, 20), List.of("url1", "url2", "url3")));

        List<Long> imageIds = staccato.getStaccatoImages()
                .getImages()
                .stream()
                .map(StaccatoImage::getId)
                .toList();

        // when
        staccatoImageRepository.deleteAllByIdInBulk(imageIds);

        // then
        assertThat(staccatoImageRepository.findAll()).isEmpty();
    }
}
