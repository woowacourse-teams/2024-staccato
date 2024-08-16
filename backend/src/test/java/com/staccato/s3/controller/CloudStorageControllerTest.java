package com.staccato.s3.controller;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.staccato.auth.service.AuthService;
import com.staccato.exception.ExceptionResponse;
import com.staccato.member.domain.Member;
import com.staccato.s3.service.CloudStorageService;

@WebMvcTest(controllers = CloudStorageController.class)
public class CloudStorageControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private CloudStorageService cloudStorageService;
    @MockBean
    private AuthService authService;

    private MockMultipartFile file1;
    private MockMultipartFile file2;
    private MockMultipartFile file3;
    private MockMultipartFile file4;
    private MockMultipartFile file5;
    private MockMultipartFile file6;

    @BeforeEach
    void setUp() {
        file1 = new MockMultipartFile("imageFiles", new byte[0]);
        file2 = new MockMultipartFile("imageFiles", new byte[0]);
        file3 = new MockMultipartFile("imageFiles", new byte[0]);
        file4 = new MockMultipartFile("imageFiles", new byte[0]);
        file5 = new MockMultipartFile("imageFiles", new byte[0]);
        file6 = new MockMultipartFile("imageFiles", new byte[0]);
    }

    @DisplayName("사진을 한 번에 최대 다섯 장까지 업로드할 수 있다.")
    @Test
    void uploadFiveFilesTest() throws Exception {
        // given
        List<String> fileUrls = List.of("ex1", "ex2", "ex3", "ex4", "ex5");
        when(authService.extractFromToken(anyString())).thenReturn(Member.builder().nickname("staccato").build());
        when(cloudStorageService.uploadFiles(anyList())).thenReturn(fileUrls);

        // when & then
        mockMvc.perform(multipart("/images")
                        .file(file1).file(file2).file(file3).file(file4).file(file5)
                        .header(HttpHeaders.AUTHORIZATION, "token")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(fileUrls)));
    }

    @DisplayName("사진을 한 번에 여섯 장 이상 업로드할 수 없다.")
    @Test
    void uploadSixFilesTest() throws Exception {
        // given
        List<String> fileUrls = List.of("ex1", "ex2", "ex3", "ex4", "ex5", "ex6");
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), "사진은 최대 다섯 장까지만 업로드할 수 있어요.");
        when(authService.extractFromToken(anyString())).thenReturn(Member.builder().nickname("staccato").build());
        when(cloudStorageService.uploadFiles(anyList())).thenReturn(fileUrls);

        // when & then
        mockMvc.perform(multipart("/images")
                        .file(file1).file(file2).file(file3).file(file4).file(file5).file(file6)
                        .header(HttpHeaders.AUTHORIZATION, "token")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(exceptionResponse)));
    }

    @DisplayName("비어 있는 멀티파일 리스트가 들어오면, 비어 있는 url 리스트가 들어온다.")
    @Test
    void uploadNoFilesTest() throws Exception {
        // given
        List<String> fileUrls = List.of();
        when(authService.extractFromToken(anyString())).thenReturn(Member.builder().nickname("staccato").build());
        when(cloudStorageService.uploadFiles(anyList())).thenReturn(fileUrls);

        // when & then
        mockMvc.perform(multipart("/images")
                        .file("imageFiles", new byte[0])
                        .header(HttpHeaders.AUTHORIZATION, "token")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(fileUrls)));
    }
}
