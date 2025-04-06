package com.staccato.member.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import com.staccato.ControllerTest;
import com.staccato.fixture.member.MemberFixtures;
import com.staccato.image.service.dto.ImageUrlResponse;
import com.staccato.member.domain.Member;
import com.staccato.member.service.dto.response.MemberProfileImageResponse;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MyPageControllerTest extends ControllerTest {
    @DisplayName("사용자의 프로필 사진을 변경한다.")
    @Test
    void changeProfile() throws Exception {
        // given
        MockMultipartFile image = new MockMultipartFile("imageFile", new byte[0]);
        ImageUrlResponse imageUrlResponse = new ImageUrlResponse("imageUrl");
        MemberProfileImageResponse memberProfileImageResponse = new MemberProfileImageResponse("imageUrl");
        when(authService.extractFromToken(anyString())).thenReturn(Member.builder().nickname("staccato").build());
        when(imageService.uploadImage(any(MultipartFile.class))).thenReturn(imageUrlResponse);
        when(memberService.changeProfileImage(any(Member.class), any(String.class))).thenReturn(memberProfileImageResponse);
        String expectedResponse = """
                {
                  "profileImageUrl": "imageUrl"
                }
                """;

        // when & then
        mockMvc.perform(multipart("/mypage/images")
                        .file(image)
                        .header(HttpHeaders.AUTHORIZATION, "token")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @DisplayName("마이페이지의 사용자 정보를 조회한다.")
    @Test
    void readMyPage() throws Exception {
        // given
        Member member = MemberFixtures.defaultMember()
                .withCode("550e8400-e29b-41d4-a716-446655440000").build();
        when(authService.extractFromToken(anyString())).thenReturn(member);
        String expectedResponse = """
                {
                	"nickname": "nickname",
                    "profileImageUrl": "https://example.com/memberImage.png",
                    "code": "550e8400-e29b-41d4-a716-446655440000"
                }
                """;

        // when & then
        mockMvc.perform(get("/mypage")
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }
}
