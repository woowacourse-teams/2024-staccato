package com.staccato.moment.domain;

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
import com.staccato.fixture.moment.MomentFixture;
import com.staccato.memory.domain.Memory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MomentImagesTest {
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

    @DisplayName("사진들의 순서를 변경하거나 새로운 사진이 추가될 때 기존 사진을 전부 삭제 후 추가한다.")
    @MethodSource("provideUpdateData")
    @ParameterizedTest
    void update(List<String> existingImageNames, List<String> updatedImageNames) {
        // given
        Memory memory = Memory.builder().title("Sample Memory").startAt(LocalDate.now().minusDays(1))
                .endAt(LocalDate.now().plusDays(1)).build();
        MomentImages existingImages = new MomentImages(existingImageNames);
        MomentImages updatedImages = new MomentImages(updatedImageNames);

        // when
        existingImages.update(updatedImages, MomentFixture.create(memory, LocalDateTime.now()));
        List<String> images = existingImages.getImages().stream().map(MomentImage::getImageUrl).toList();

        // then
        assertThat(images).containsExactlyElementsOf(updatedImageNames);
    }
}
