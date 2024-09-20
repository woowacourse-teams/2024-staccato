package com.staccato.member.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.staccato.auth.service.AuthService;
import com.staccato.exception.ExceptionResponse;
import com.staccato.image.service.ImageService;
import com.staccato.image.service.dto.ImageUrlResponse;
import com.staccato.member.domain.Member;
import com.staccato.member.service.MemberService;
import com.staccato.member.service.dto.response.MemberProfileResponse;

@WebMvcTest(controllers = MemberController.class)
class MemberControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private MemberService memberService;
    @MockBean
    private ImageService imageService;
    @MockBean
    private AuthService authService;

    @DisplayName("사용자의 프로필 사진을 변경한다.")
    @Test
    void changeProfile() throws Exception {
        // given
        MockMultipartFile image = new MockMultipartFile("imageFile", new byte[0]);
        ImageUrlResponse imageUrlResponse = new ImageUrlResponse("imageUrl");
        MemberProfileResponse memberProfileResponse = new MemberProfileResponse("imageUrl");
        when(authService.extractFromToken(anyString())).thenReturn(Member.builder().nickname("staccato").build());
        when(imageService.uploadImage(any(MultipartFile.class))).thenReturn(imageUrlResponse);
        when(memberService.changeProfileImage(any(Member.class), anyLong(), any(String.class))).thenReturn(memberProfileResponse);
        String expectedResponse = """
                {
                  "profileImageUrl": "imageUrl"
                }
                """;

        // when & then
        mockMvc.perform(multipart("/members/{memberId}/profiles/images", 1)
                        .file(image)
                        .header(HttpHeaders.AUTHORIZATION, "token")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(content().json(expectedResponse));
    }

    @DisplayName("사용자 식별자는 양수여야 한다.")
    @Test
    void cannotChangeProfile() throws Exception {
        // given
        MockMultipartFile image = new MockMultipartFile("imageFile", new byte[0]);
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), "사용자 식별자는 양수로 이루어져야 합니다.");

        // when & then
        mockMvc.perform(multipart("/members/{memberId}/profiles/images", 0)
                        .file(image)
                        .header(HttpHeaders.AUTHORIZATION, "token")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(exceptionResponse)));
    }
}
