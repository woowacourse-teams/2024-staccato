package com.staccato.moment.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.staccato.auth.service.AuthService;
import com.staccato.exception.ExceptionResponse;
import com.staccato.fixture.moment.MomentDetailResponseFixture;
import com.staccato.member.domain.Member;
import com.staccato.moment.service.MomentService;
import com.staccato.moment.service.dto.request.FeelingRequest;
import com.staccato.moment.service.dto.request.MomentRequest;
import com.staccato.moment.service.dto.request.MomentUpdateRequest;
import com.staccato.moment.service.dto.response.MomentDetailResponse;
import com.staccato.moment.service.dto.response.MomentIdResponse;

@WebMvcTest(controllers = MomentController.class)
class MomentControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private MomentService momentService;
    @MockBean
    private AuthService authService;

    static Stream<Arguments> invalidMomentRequestProvider() {
        return Stream.of(
                Arguments.of(
                        new MomentRequest("placeName", "address", BigDecimal.ONE, BigDecimal.ONE, LocalDateTime.of(2023, 7, 1, 10, 0), 0L),
                        "추억 식별자는 양수로 이루어져야 합니다."
                ),
                Arguments.of(
                        new MomentRequest(null, "address", BigDecimal.ONE, BigDecimal.ONE, LocalDateTime.of(2023, 7, 1, 10, 0), 1L),
                        "순간한 장소의 이름을 입력해주세요."
                ),
                Arguments.of(
                        new MomentRequest("  ", "address", BigDecimal.ONE, BigDecimal.ONE, LocalDateTime.of(2023, 7, 1, 10, 0), 1L),
                        "순간한 장소의 이름을 입력해주세요."
                ),
                Arguments.of(
                        new MomentRequest("가".repeat(31), "address", BigDecimal.ONE, BigDecimal.ONE, LocalDateTime.of(2023, 7, 1, 10, 0), 1L),
                        "순간한 장소의 이름은 공백 포함 1자 이상 30자 이하로 설정해주세요."
                ),
                Arguments.of(
                        new MomentRequest("placeName", "address", null, BigDecimal.ONE, LocalDateTime.of(2023, 7, 1, 10, 0), 1L),
                        "순간한 장소의 위도를 입력해주세요."
                ),
                Arguments.of(
                        new MomentRequest("placeName", "address", BigDecimal.ONE, null, LocalDateTime.of(2023, 7, 1, 10, 0), 1L),
                        "순간한 장소의 경도를 입력해주세요."
                ),
                Arguments.of(
                        new MomentRequest("placeName", null, BigDecimal.ONE, BigDecimal.ONE, LocalDateTime.of(2023, 7, 1, 10, 0), 1L),
                        "순간한 장소의 주소를 입력해주세요."
                ),
                Arguments.of(
                        getMomentRequest(null),
                        "방문 날짜를 입력해주세요."
                )
        );
    }

    @DisplayName("순간 기록 생성 시 사진 5장까지는 첨부 가능하다.")
    @Test
    void createMoment() throws Exception {
        // given
        MomentRequest momentRequest = getMomentRequest(LocalDateTime.now());
        String momentRequestJson = objectMapper.writeValueAsString(momentRequest);
        MockMultipartFile momentRequestPart = new MockMultipartFile("data", "momentRequest.json", "application/json", momentRequestJson.getBytes());
        MockMultipartFile file1 = new MockMultipartFile("momentImageFiles", "test-image1.jpg", "image/jpeg", "dummy image content".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("momentImageFiles", "test-image2.jpg", "image/jpeg", "dummy image content".getBytes());
        MockMultipartFile file3 = new MockMultipartFile("momentImageFiles", "test-image3.jpg", "image/jpeg", "dummy image content".getBytes());
        MockMultipartFile file4 = new MockMultipartFile("momentImageFiles", "test-image4.jpg", "image/jpeg", "dummy image content".getBytes());
        MockMultipartFile file5 = new MockMultipartFile("momentImageFiles", "test-image5.jpg", "image/jpeg", "dummy image content".getBytes());
        MomentIdResponse momentIdResponse = new MomentIdResponse(1L);
        when(authService.extractFromToken(anyString())).thenReturn(Member.builder().nickname("staccato").build());
        when(momentService.createMoment(any(MomentRequest.class), any(List.class), any(Member.class))).thenReturn(new MomentIdResponse(1L));

        // when & then
        mockMvc.perform(multipart("/moments")
                        .file(momentRequestPart)
                        .file(file1)
                        .file(file2)
                        .file(file3)
                        .file(file4)
                        .file(file5)
                        .header(HttpHeaders.AUTHORIZATION, "token")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, "/moments/1"))
                .andExpect(content().json(objectMapper.writeValueAsString(momentIdResponse)));
    }

    @DisplayName("올바르지 않은 날짜 형식으로 순간 기록 생성을 요청하면 예외가 발생한다.")
    @Test
    void failCreateMomentWithInvalidVistedAt() throws Exception {
        // given
        String momentRequestJson = """
                    {
                        "placeName": "런던 박물관",
                        "address": "Great Russell St, London WC1B 3DG",
                        "latitude": 51.51978412729915,
                        "longitude": -0.12712788587027796,
                        "visitedAt": "2024/07/27T10:00:00",
                        "memoryId": 1
                    }
                """;
        MockMultipartFile momentRequestPart = new MockMultipartFile(
                "data",
                "",
                "application/json",
                momentRequestJson.getBytes()
        );
        MockMultipartFile momentImageFiles = new MockMultipartFile(
                "momentImageFiles",
                "",
                "application/json",
                objectMapper.writeValueAsString(List.of()).getBytes()
        );
        when(authService.extractFromToken(anyString())).thenReturn(Member.builder().nickname("staccato").build());
        when(momentService.createMoment(any(MomentRequest.class), any(List.class), any(Member.class))).thenReturn(new MomentIdResponse(1L));

        // when & then
        mockMvc.perform(multipart("/moments")
                        .file(momentRequestPart)
                        .file(momentImageFiles)
                        .header(HttpHeaders.AUTHORIZATION, "token")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("요청 본문을 읽을 수 없습니다. 올바른 형식으로 데이터를 제공해주세요."));
    }

    @DisplayName("사진이 5장을 초과하면 순간 기록 생성에 실패한다.")
    @Test
    void failCreateMomentByImageCount() throws Exception {
        // given
        MomentRequest momentRequest = getMomentRequest(LocalDateTime.now());
        String momentRequestJson = objectMapper.writeValueAsString(momentRequest);
        MockMultipartFile momentRequestPart = new MockMultipartFile("data", "momentRequest.json", "application/json", momentRequestJson.getBytes());
        MockMultipartFile file1 = new MockMultipartFile("momentImageFiles", "test-image1.jpg", "image/jpeg", "dummy image content".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("momentImageFiles", "test-image2.jpg", "image/jpeg", "dummy image content".getBytes());
        MockMultipartFile file3 = new MockMultipartFile("momentImageFiles", "test-image3.jpg", "image/jpeg", "dummy image content".getBytes());
        MockMultipartFile file4 = new MockMultipartFile("momentImageFiles", "test-image4.jpg", "image/jpeg", "dummy image content".getBytes());
        MockMultipartFile file5 = new MockMultipartFile("momentImageFiles", "test-image5.jpg", "image/jpeg", "dummy image content".getBytes());
        MockMultipartFile file6 = new MockMultipartFile("momentImageFiles", "test-image6.jpg", "image/jpeg", "dummy image content".getBytes());
        MockMultipartHttpServletRequestBuilder builder = multipart("/moments");
        builder.file(momentRequestPart);
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), "사진은 5장까지만 추가할 수 있어요.");
        when(authService.extractFromToken(anyString())).thenReturn(Member.builder().nickname("staccato").build());

        // when & then
        mockMvc.perform(multipart("/moments")
                        .file(momentRequestPart)
                        .file(file1)
                        .file(file2)
                        .file(file3)
                        .file(file4)
                        .file(file5)
                        .file(file6)
                        .header(HttpHeaders.AUTHORIZATION, "token")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(exceptionResponse)));
    }

    private static MomentRequest getMomentRequest(LocalDateTime visitedAt) {
        return new MomentRequest("placeName", "address", BigDecimal.ONE, BigDecimal.ONE, visitedAt, 1L);
    }

    @DisplayName("사용자가 잘못된 요청 형식으로 정보를 입력하면, 순간 기록을 생성할 수 없다.")
    @ParameterizedTest
    @MethodSource("invalidMomentRequestProvider")
    void failCreateMoment(MomentRequest momentRequest, String expectedMessage) throws Exception {
        // given
        String momentRequestJson = objectMapper.writeValueAsString(momentRequest);
        MockMultipartFile momentRequestPart = new MockMultipartFile("data", "momentRequest.json", "application/json", momentRequestJson.getBytes());
        MockMultipartFile imageFilePart = new MockMultipartFile("momentImageFiles", "test-image1.jpg", "image/jpeg", "dummy image content".getBytes());
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), expectedMessage);
        when(authService.extractFromToken(anyString())).thenReturn(Member.builder().nickname("staccato").build());
        when(momentService.createMoment(any(MomentRequest.class), any(List.class), any(Member.class))).thenReturn(new MomentIdResponse(1L));

        // when & then
        mockMvc.perform(multipart("/moments")
                        .file(momentRequestPart)
                        .file(imageFilePart)
                        .header(HttpHeaders.AUTHORIZATION, "token")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(exceptionResponse)));
    }

    @DisplayName("적합한 경로변수를 통해 순간 기록 조회에 성공한다.")
    @Test
    void readMomentById() throws Exception {
        // given
        long momentId = 1L;
        when(authService.extractFromToken(anyString())).thenReturn(Member.builder().nickname("staccato").build());
        MomentDetailResponse response = MomentDetailResponseFixture.create(momentId, LocalDate.now());
        when(momentService.readMomentById(anyLong(), any(Member.class))).thenReturn(response);

        // when & then
        mockMvc.perform(get("/moments/{momentId}", momentId)
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @DisplayName("적합하지 않은 경로변수의 경우 순간 기록 조회에 실패한다.")
    @Test
    void failReadMomentById() throws Exception {
        // given
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), "순간 기록 식별자는 양수로 이루어져야 합니다.");

        // when & then
        mockMvc.perform(get("/moments/{momentId}", 0))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(exceptionResponse)));
    }

    @DisplayName("적합한 경로변수를 통해 순간 기록 수정에 성공한다.")
    @Test
    void updateMomentById() throws Exception {
        // given
        long momentId = 1L;
        MomentUpdateRequest updateRequest = new MomentUpdateRequest("placeName", List.of("https://example1.com.jpg"));
        MockMultipartFile file = new MockMultipartFile("momentImageFiles", "namsan_tower.jpg".getBytes());
        when(authService.extractFromToken(anyString())).thenReturn(Member.builder().nickname("staccato").build());

        // when & then
        mockMvc.perform(multipart("/moments/{momentId}", momentId)
                        .file(file)
                        .file(new MockMultipartFile("data", "", "application/json", objectMapper.writeValueAsString(updateRequest)
                                .getBytes()))
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .header(HttpHeaders.AUTHORIZATION, "token")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());
    }

    @DisplayName("추가하려는 사진이 5장이 넘는다면 순간 기록 수정에 실패한다.")
    @Test
    void failUpdateMomentByImagesSize() throws Exception {
        // given
        long momentId = 1L;
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), "사진은 5장까지만 추가할 수 있어요.");
        MomentUpdateRequest updateRequest = new MomentUpdateRequest("placeName", List.of("https://example1.com.jpg"));
        MockMultipartFile file1 = new MockMultipartFile("momentImageFiles", "namsan_tower1.jpg".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("momentImageFiles", "namsan_tower2.jpg".getBytes());
        MockMultipartFile file3 = new MockMultipartFile("momentImageFiles", "namsan_tower3.jpg".getBytes());
        MockMultipartFile file4 = new MockMultipartFile("momentImageFiles", "namsan_tower4.jpg".getBytes());
        MockMultipartFile file5 = new MockMultipartFile("momentImageFiles", "namsan_tower5.jpg".getBytes());
        MockMultipartFile file6 = new MockMultipartFile("momentImageFiles", "namsan_tower6.jpg".getBytes());
        when(authService.extractFromToken(anyString())).thenReturn(Member.builder().nickname("staccato").build());

        // when & then
        mockMvc.perform(multipart("/moments/{momentId}", momentId)
                        .file(file1)
                        .file(file2)
                        .file(file3)
                        .file(file4)
                        .file(file5)
                        .file(file6)
                        .file(new MockMultipartFile("data", "", "application/json", objectMapper.writeValueAsString(updateRequest)
                                .getBytes()))
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .header(HttpHeaders.AUTHORIZATION, "token")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(exceptionResponse)));
    }

    @DisplayName("적합하지 않은 경로변수의 경우 순간 기록 수정에 실패한다.")
    @Test
    void failUpdateMomentById() throws Exception {
        // given
        long momentId = 0L;
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), "순간 기록 식별자는 양수로 이루어져야 합니다.");
        MomentUpdateRequest updateRequest = new MomentUpdateRequest("placeName", List.of("https://example1.com.jpg"));
        MockMultipartFile file = new MockMultipartFile("momentImageFiles", "namsan_tower.jpg".getBytes());
        when(authService.extractFromToken(anyString())).thenReturn(Member.builder().nickname("staccato").build());

        // when & then
        mockMvc.perform(multipart("/moments/{momentId}", momentId)
                        .file(file)
                        .file(new MockMultipartFile("data", "", "application/json", objectMapper.writeValueAsString(updateRequest)
                                .getBytes()))
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .header(HttpHeaders.AUTHORIZATION, "token")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(exceptionResponse)));
    }

    @DisplayName("순간 기록 수정 시 장소 이름을 입력하지 않은 경우 수정에 실패한다.")
    @Test
    void failUpdateMomentByPlaceName() throws Exception {
        // given
        long momentId = 1L;
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), "순간한 장소의 이름을 입력해주세요.");
        MomentUpdateRequest updateRequest = new MomentUpdateRequest(null, List.of("https://example1.com.jpg"));
        MockMultipartFile file = new MockMultipartFile("momentImageFiles", "namsan_tower.jpg".getBytes());
        when(authService.extractFromToken(anyString())).thenReturn(Member.builder().nickname("staccato").build());

        // when & then
        mockMvc.perform(multipart("/moments/{momentId}", momentId)
                        .file(file)
                        .file(new MockMultipartFile("data", "", "application/json", objectMapper.writeValueAsString(updateRequest)
                                .getBytes()))
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .header(HttpHeaders.AUTHORIZATION, "token")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(exceptionResponse)));
    }

    @DisplayName("순간 기록을 삭제한다.")
    @Test
    void deleteMomentById() throws Exception {
        // given
        long momentId = 1L;
        when(authService.extractFromToken(anyString())).thenReturn(Member.builder().nickname("staccato").build());

        // when & then
        mockMvc.perform(delete("/moments/{momentId}", momentId)
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isOk());
    }

    @DisplayName("양수가 아닌 id로 순간 기록을 삭제할 수 없다.")
    @Test
    void failDeleteMomentById() throws Exception {
        // given
        long momentId = 0L;
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), "순간 기록 식별자는 양수로 이루어져야 합니다.");

        // when & then
        mockMvc.perform(delete("/moments/{momentId}", momentId)
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(exceptionResponse)));
    }

    @DisplayName("기분 선택을 하지 않은 경우 기분 수정에 실패한다.")
    @Test
    void failUpdateMomentFeelingById() throws Exception {
        // given
        long momentId = 1L;
        FeelingRequest feelingRequest = new FeelingRequest(null);
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), "기분 값을 입력해주세요.");

        // when & then
        mockMvc.perform(post("/moments/{momentId}/feeling", momentId)
                        .header(HttpHeaders.AUTHORIZATION, "token")
                        .content(objectMapper.writeValueAsString(feelingRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(exceptionResponse)));
    }
}
