package com.staccato.moment.domain;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import com.staccato.exception.StaccatoException;

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
        assertThatNoException().isThrownBy(() -> MomentImages.of(images, null));
    }

    @DisplayName("생성하려는 사진의 갯수가 5장을 초과할 시 예외가 발생한다.")
    @Test
    void failAddMomentImages() {
        // given & when & then
        assertThatThrownBy(() -> MomentImages.of(List.of("picture1", "picture2", "picture3", "picture4", "picture5", "picture6"), null))
                .isInstanceOf(StaccatoException.class)
                .hasMessage("사진은 5장을 초과할 수 없습니다.");
    }

    @DisplayName("인자와 비교하여 중복되지 않는 이미지들을 반환할 수 있다.")
    @Test
    void findValuesNotPresentIn() {
        // given & when & then
        MomentImages momentImages = MomentImages.of(List.of("picture1", "picture2", "picture3", "picture4"), null);
        MomentImages newMomentImages = MomentImages.of(List.of("picture2", "picture3", "picture5"), null);

        assertAll(
                () -> assertThat(momentImages.findValuesNotPresentIn(newMomentImages)).hasSize(2)
                        .extracting(MomentImage::getImageUrl)
                        .containsExactly("picture1", "picture4"),
                () -> assertThat(newMomentImages.findValuesNotPresentIn(momentImages)).hasSize(1)
                        .extracting(MomentImage::getImageUrl)
                        .containsExactly("picture5")
        );
    }
}
