package com.staccato.moment.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import com.staccato.exception.StaccatoException;
import com.staccato.fixture.moment.MomentFixture;
import com.staccato.memory.domain.Memory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class MomentImagesTest {
    @DisplayName("생성하려는 사진의 갯수가 0장 이상, 5장 이하이면 생성에 성공한다.")
    @ParameterizedTest
    @ValueSource(ints = {0, 5})
    void addMomentImages(int size) {
        // given
        List<String> images = new ArrayList<>();
        for (int count = 1; count <= size; count++) {
            images.add("image" + count + ".jpg");
        }

        // when & then
        assertThatNoException().isThrownBy(() -> new MomentImages(images));
    }

    @DisplayName("생성하려는 사진의 갯수가 5장을 초과할 시 예외가 발생한다.")
    @Test
    void failAddMomentImages() {
        // given & when & then
        assertThatThrownBy(() -> new MomentImages(List.of("picture1", "picture2", "picture3", "picture4", "picture5", "picture6")))
                .isInstanceOf(StaccatoException.class)
                .hasMessage("사진은 5장을 초과할 수 없습니다.");
    }

    @DisplayName("사진을 추가할 때 총 사진의 갯수가 5장을 초과할 시 예외가 발생한다.")
    @Test
    void failUpdateMomentImages() {
        // given & when & then
        assertThatThrownBy(() -> new MomentImages(List.of("picture1", "picture2", "picture3", "picture4", "picture5", "picture6")))
                .isInstanceOf(StaccatoException.class)
                .hasMessage("사진은 5장을 초과할 수 없습니다.");
    }

    @DisplayName("사진들을 추가할 때 기존 사진이 포함되지 않은 경우 삭제 후 추가한다.")
    @Test
    void update() {
        // given
        Memory memory = Memory.builder().title("Sample Memory").startAt(LocalDate.now().minusDays(1))
                .endAt(LocalDate.now().plusDays(1)).build();
        MomentImages existingImages = new MomentImages(List.of("picture1", "picture3"));
        MomentImages updatedImages = new MomentImages(List.of("picture1", "picture4"));

        // when
        existingImages.update(updatedImages, MomentFixture.create(memory, LocalDateTime.now()));

        // then
        List<String> images = existingImages.getImages().stream().map(MomentImage::getImageUrl).toList();
        assertAll(
                () -> assertThat(images).containsAll(List.of("picture1", "picture4")),
                () -> assertThat(images.size()).isEqualTo(2)
        );
    }

    @DisplayName("포함되지 않는 사진들을 선별할 수 있다.")
    @Test
    void findImagesNotPresentIn() {
        // given
        MomentImages existingImages = new MomentImages(List.of("picture1", "picture3"));
        MomentImages newImages = new MomentImages(List.of("picture1", "picture4"));

        // when
        List<MomentImage> remainingExistingImages = existingImages.findImagesNotPresentIn(newImages);
        List<MomentImage> remainingNewImages = newImages.findImagesNotPresentIn(existingImages);

        // then
        assertAll(

                () -> assertThat(remainingExistingImages.size()).isEqualTo(1),
                () -> assertThat(remainingNewImages.size()).isEqualTo(1),
                () -> assertThat(remainingExistingImages.get(0).getImageUrl()).isEqualTo("picture3"),
                () -> assertThat(remainingNewImages.get(0).getImageUrl()).isEqualTo("picture4")
        );
    }
}
