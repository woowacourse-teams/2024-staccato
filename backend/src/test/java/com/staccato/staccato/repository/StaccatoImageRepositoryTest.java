package com.staccato.staccato.repository;

import com.staccato.category.domain.Category;
import com.staccato.fixture.category.CategoryFixtures;
import com.staccato.fixture.staccato.StaccatoFixtures;
import com.staccato.staccato.domain.Staccato;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.staccato.RepositoryTest;
import com.staccato.category.repository.CategoryRepository;
import com.staccato.staccato.domain.StaccatoImage;

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
        Category category = CategoryFixtures.defaultCategory().buildAndSave(categoryRepository);
        Staccato staccato1 = StaccatoFixtures.defaultStaccato()
                .withCategory(category)
                .buildAndSaveWithStaccatoImages(List.of("url1", "url2"), staccatoRepository);
        Staccato staccato2 = StaccatoFixtures.defaultStaccato()
                .withCategory(category)
                .buildAndSaveWithStaccatoImages(List.of("url1", "url2"), staccatoRepository);

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
        Category category = CategoryFixtures.defaultCategory().buildAndSave(categoryRepository);
        Staccato staccato = StaccatoFixtures.defaultStaccato()
                .withCategory(category)
                .buildAndSaveWithStaccatoImages(List.of("url1", "url2", "url3"), staccatoRepository);

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
