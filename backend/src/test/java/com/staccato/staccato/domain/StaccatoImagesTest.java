package com.staccato.staccato.domain;

import com.staccato.category.domain.Category;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import com.staccato.exception.StaccatoException;
import com.staccato.fixture.category.CategoryFixtures;
import com.staccato.fixture.staccato.StaccatoFixtures;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StaccatoImagesTest {
    static Stream<Arguments> provideUpdateData() {
        return Stream.of(
                Arguments.of(List.of("picture1", "picture2", "picture3"), List.of()),
                Arguments.of(List.of("picture1", "picture2", "picture3"), List.of("picture4")),
                Arguments.of(List.of("picture1", "picture2", "picture3"), List.of("picture3", "picture2")),
                Arguments.of(List.of("picture1", "picture2", "picture3"), List.of("picture3", "picture1", "picture2")),
                Arguments.of(List.of("picture1", "picture2", "picture3"), List.of("picture1", "picture2", "picture4", "picture3"))
        );
    }

    @DisplayName("생성하려는 사진의 갯수가 0장 이상, 5장 이하이면 생성에 성공한다.")
    @ParameterizedTest
    @ValueSource(ints = {0, 5})
    void addStaccatoImages(int size) {
        // given
        List<String> images = new ArrayList<>();
        for (int count = 1; count <= size; count++) {
            images.add("image" + count + ".jpg");
        }

        // when & then
        assertThatNoException().isThrownBy(() -> new StaccatoImages(images));
    }

    @DisplayName("생성하려는 사진의 갯수가 5장을 초과할 시 예외가 발생한다.")
    @Test
    void failAddStaccatoImages() {
        // given & when & then
        assertThatThrownBy(() -> new StaccatoImages(List.of("picture1", "picture2", "picture3", "picture4", "picture5", "picture6")))
                .isInstanceOf(StaccatoException.class)
                .hasMessage("사진은 5장을 초과할 수 없습니다.");
    }

    @DisplayName("사진을 추가할 때 총 사진의 갯수가 5장을 초과할 시 예외가 발생한다.")
    @Test
    void failUpdateStaccatoImages() {
        // given & when & then
        assertThatThrownBy(() -> new StaccatoImages(List.of("picture1", "picture2", "picture3", "picture4", "picture5", "picture6")))
                .isInstanceOf(StaccatoException.class)
                .hasMessage("사진은 5장을 초과할 수 없습니다.");
    }

    @DisplayName("사진들의 순서를 변경하거나 새로운 사진이 추가될 때 기존 사진을 전부 삭제 후 추가한다.")
    @MethodSource("provideUpdateData")
    @ParameterizedTest
    void update(List<String> existingImageNames, List<String> updatedImageNames) {
        // given
        Category category = CategoryFixtures.defaultCategory().build();
        StaccatoImages existingImages = new StaccatoImages(existingImageNames);
        StaccatoImages updatedImages = new StaccatoImages(updatedImageNames);

        // when
        Staccato staccato = StaccatoFixtures.defaultStaccato()
                .withCategory(category).build();
        existingImages.update(updatedImages, staccato);
        List<String> images = existingImages.getImages().stream().map(StaccatoImage::getImageUrl).toList();

        // then
        assertThat(images).containsExactlyElementsOf(updatedImageNames);
    }
}
