package com.staccato.s3.controller;

import static org.mockito.ArgumentMatchers.any;
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
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.staccato.auth.service.AuthService;
import com.staccato.image.controller.ImageController;
import com.staccato.image.service.ImageService;
import com.staccato.image.service.dto.ImageUrlResponse;
import com.staccato.member.domain.Member;

@WebMvcTest(controllers = ImageController.class)
public class ImageControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ImageService imageService;
    @MockBean
    private AuthService authService;

    @DisplayName("사진을 한 장 업로드하고 S3 url을 가져올 수 있다.")
    @Test
    void uploadFiveFilesTest() throws Exception {
        // given
        MockMultipartFile image = new MockMultipartFile("imageFile", new byte[0]);
        ImageUrlResponse imageUrlResponse = new ImageUrlResponse("imageUrl");
        when(authService.extractFromToken(anyString())).thenReturn(Member.builder().nickname("staccato").build());
        when(imageService.uploadImage(any())).thenReturn(imageUrlResponse);

        // when & then
        mockMvc.perform(multipart("/images")
                        .file(image)
                        .header(HttpHeaders.AUTHORIZATION, "token")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(imageUrlResponse)));
    }
}
