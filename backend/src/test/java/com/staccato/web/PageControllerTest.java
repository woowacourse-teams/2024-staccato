package com.staccato.web;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.staccato.ControllerTest;
import com.staccato.category.domain.Category;
import com.staccato.fixture.category.CategoryFixtures;
import com.staccato.fixture.member.MemberFixtures;
import com.staccato.fixture.staccato.StaccatoFixtures;
import com.staccato.member.domain.Member;
import com.staccato.staccato.domain.Staccato;
import com.staccato.staccato.service.dto.response.StaccatoSharedResponse;

public class PageControllerTest extends ControllerTest {
    private static final String DEFAULT_THUMBNAIL_URL = "https://image.staccato.kr/web/share/frame.png";

    @DisplayName("루트 페이지에 접근하면 index 뷰를 반환한다.")
    @Test
    void returnIndexViewWhenAccessingRoot() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @DisplayName("공유 페이지 접근 시, 이미지가 있을 경우 첫 번째 이미지를 썸네일로 사용한다.")
    @Test
    void useFirstImageAsThumbnailWhenImagesExist() throws Exception {
        // given
        String token = "test-token";
        List<String> imageUrls = List.of(
                "https://image.staccato.kr/test-image1.png",
                "https://image.staccato.kr/test-image2.png",
                "https://image.staccato.kr/test-image3.png"
        );

        Category category = CategoryFixtures.defaultCategory().build();
        Staccato staccato = StaccatoFixtures.defaultStaccato()
                .withCategory(category)
                .withStaccatoImages(imageUrls).build();
        Member member = MemberFixtures.defaultMember().build();
        StaccatoSharedResponse response = new StaccatoSharedResponse(LocalDateTime.now(), staccato, member, List.of());

        when(staccatoShareService.readSharedStaccatoByToken(token)).thenReturn(response);

        // when & then
        mockMvc.perform(get("/share/{token}", token))
                .andExpect(status().isOk())
                .andExpect(view().name("share"))
                .andExpect(model().attribute("token", token))
                .andExpect(model().attribute("thumbnailUrl", imageUrls.get(0)));
    }

    @DisplayName("공유 페이지 접근 시, 이미지가 없을 경우 기본 썸네일을 사용한다.")
    @Test
    void useDefaultImageAsThumbnailWhenNoImagesExist() throws Exception {
        // given
        String token = "test-token";
        List<String> imageUrls = List.of();

        Category category = CategoryFixtures.defaultCategory().build();
        Staccato staccato = StaccatoFixtures.defaultStaccato()
                .withCategory(category)
                .withStaccatoImages(imageUrls).build();
        Member member = MemberFixtures.defaultMember().build();
        StaccatoSharedResponse response = new StaccatoSharedResponse(LocalDateTime.now(), staccato, member, List.of());

        when(staccatoShareService.readSharedStaccatoByToken(token)).thenReturn(response);

        // when & then
        mockMvc.perform(get("/share/{token}", token))
                .andExpect(status().isOk())
                .andExpect(view().name("share"))
                .andExpect(model().attribute("token", token))
                .andExpect(model().attribute("thumbnailUrl", DEFAULT_THUMBNAIL_URL));
    }
}
