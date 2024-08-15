package com.staccato.moment.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.staccato.exception.StaccatoException;
import com.staccato.memory.domain.Memory;
import com.staccato.moment.fixture.MomentFixture;

class MomentImagesTest {
    @DisplayName("생성하려는 사진의 갯수가 5장을 초과할 시 예외가 발생한다.")
    @Test
    void failAddMomentImages() {
        // given & when & then
        assertThatThrownBy(() -> new MomentImages(List.of("picture1", "picture2", "picture3", "picture4", "picture5", "picture6")))
                .isInstanceOf(StaccatoException.class)
                .hasMessage("사진은 5장까지만 추가할 수 있어요.");
    }

    @DisplayName("사진을 추가할 때 총 사진의 갯수가 5장을 초과할 시 예외가 발생한다.")
    @Test
    void failUpdateMomentImages() {
        // given & when & then
        assertThatThrownBy(() -> MomentImages.builder()
                .existingImages(List.of("picture1", "picture2", "picture3", "picture4"))
                .addedImages(List.of("picture5", "picture6")).build())
                .isInstanceOf(StaccatoException.class)
                .hasMessage("사진은 5장까지만 추가할 수 있어요.");
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
}
