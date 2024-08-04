package com.staccato.visit.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.staccato.exception.StaccatoException;
import com.staccato.travel.domain.Travel;
import com.staccato.visit.fixture.VisitFixture;

class VisitImagesTest {

    @DisplayName("생성하려는 사진의 갯수가 5장을 초과할 시 예외가 발생한다.")
    @Test
    void failAddVisitImages(){
        // given & when & then
        assertThatThrownBy(() -> new VisitImages(List.of("picture1", "picture2", "picture3", "picture4", "picture5", "picture6")))
                .isInstanceOf(StaccatoException.class)
                .hasMessage("사진은 5장까지만 추가할 수 있어요.");
    }

    @DisplayName("사진을 추가할 때 총 사진의 갯수가 5장을 초과할 시 예외가 발생한다.")
    @Test
    void failUpdateVisitImages(){
        // given & when & then
        assertThatThrownBy(() -> new VisitImages(List.of("picture1", "picture2", "picture3", "picture4"), List.of( "picture5", "picture6")))
                .isInstanceOf(StaccatoException.class)
                .hasMessage("사진은 5장까지만 추가할 수 있어요.");
    }

    @DisplayName("사진들을 추가할 때 기존 사진이 포함되지 않은 경우 삭제 후 추가한다.")
    @Test
    void update(){
        // given
        Travel travel = Travel.builder().title("Sample Travel").startAt(LocalDate.now().minusDays(1)).endAt(LocalDate.now().plusDays(1)).build();
        VisitImages existingImages = new VisitImages(List.of("picture1", "picture3"));
        VisitImages updatedImages = new VisitImages(List.of("picture1", "picture4"));

        // when
        existingImages.update(updatedImages, VisitFixture.create(travel, LocalDate.now()));

        // then
        assertAll(
                () -> assertThat(existingImages.getImages().get(0).getImageUrl()).isEqualTo("picture1"),
                () -> assertThat(existingImages.getImages().get(1).getImageUrl()).isEqualTo("picture4"),
                () -> assertThat(existingImages.getImages().size()).isEqualTo(2)
        );
    }
}
