package com.staccato.comment.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.staccato.auth.service.AuthService;
import com.staccato.comment.service.CommentService;
import com.staccato.comment.service.dto.request.CommentRequest;
import com.staccato.comment.service.dto.request.CommentUpdateRequest;
import com.staccato.comment.service.dto.response.CommentResponse;
import com.staccato.comment.service.dto.response.CommentResponses;
import com.staccato.exception.ExceptionResponse;
import com.staccato.fixture.Member.MemberFixture;
import com.staccato.fixture.comment.CommentUpdateRequestFixture;

@WebMvcTest(CommentController.class)
public class CommentControllerTest {
    private static final int MAX_CONTENT_LENGTH = 500;
    private static final long MIN_MOMENT_ID = 1L;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private CommentService commentService;
    @MockBean
    private AuthService authService;

    static Stream<Arguments> invalidCommentRequestProvider() {
        return Stream.of(
                Arguments.of(
                        new CommentRequest(null, "예시 댓글 내용"),
                        "스타카토를 선택해주세요."
                ),
                Arguments.of(
                        new CommentRequest(MIN_MOMENT_ID - 1, "예시 댓글 내용"),
                        "스타카토 식별자는 양수로 이루어져야 합니다."
                ),
                Arguments.of(
                        new CommentRequest(MIN_MOMENT_ID, null),
                        "댓글 내용을 입력해주세요."
                ),
                Arguments.of(
                        new CommentRequest(MIN_MOMENT_ID, ""),
                        "댓글 내용을 입력해주세요."
                ),
                Arguments.of(
                        new CommentRequest(MIN_MOMENT_ID, " "),
                        "댓글 내용을 입력해주세요."
                ),
                Arguments.of(
                        new CommentRequest(MIN_MOMENT_ID, "1".repeat(MAX_CONTENT_LENGTH + 1)),
                        "댓글은 공백 포함 500자 이하로 입력해주세요."
                )
        );
    }

    @DisplayName("댓글 생성 요청/응답에 대한 직렬화/역직렬화에 성공한다.")
    @Test
    void createComment() throws Exception {
        // given
        when(authService.extractFromToken(any())).thenReturn(MemberFixture.create());
        String commentRequest = """
                {
                    "momentId": 1,
                    "content": "content"
                }
                """;
        when(commentService.createComment(any(), any())).thenReturn(1L);

        // when & then
        mockMvc.perform(post("/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(commentRequest)
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, "/comments/1"));
    }

    @DisplayName("올바르지 않은 형식으로 정보를 입력하면, 댓글을 생성할 수 없다.")
    @ParameterizedTest
    @MethodSource("invalidCommentRequestProvider")
    void createCommentFail(CommentRequest commentRequest, String expectedMessage) throws Exception {
        // given
        when(authService.extractFromToken(any())).thenReturn(MemberFixture.create());
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), expectedMessage);

        // when & then
        mockMvc.perform(post("/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentRequest))
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(exceptionResponse)));
    }

    @DisplayName("댓글을 조회했을 때 응답 직렬화에 성공한다.")
    @Test
    void readCommentsByMomentId() throws Exception {
        // given
        when(authService.extractFromToken(any())).thenReturn(MemberFixture.create());
        CommentResponse commentResponse = new CommentResponse(1L, 1L, "member", "image.jpg", "내용");
        CommentResponses commentResponses = new CommentResponses(List.of(commentResponse));
        when(commentService.readAllCommentsByMomentId(any(), any())).thenReturn(commentResponses);
        String expectedResponse = """
                {
                    "comments": [
                	        {
                            "commentId": 1,
                            "memberId": 1,
                            "nickname": "member",
                            "memberImageUrl": "image.jpg",
                            "content": "내용"
                        }
                    ]
                }
                """;

        // when & then
        mockMvc.perform(get("/comments")
                        .param("momentId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @DisplayName("스타카토 식별자가 양수가 아닐 경우 댓글 읽기에 실패한다.")
    @Test
    void readCommentsByMomentIdFail() throws Exception {
        // given
        when(authService.extractFromToken(any())).thenReturn(MemberFixture.create());
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), "스타카토 식별자는 양수로 이루어져야 합니다.");

        // when & then
        mockMvc.perform(get("/comments")
                        .param("momentId", "0")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(exceptionResponse)));
    }

    @DisplayName("댓글 수정 요청 역직렬화에 성공한다.")
    @Test
    void updateComment() throws Exception {
        // given
        when(authService.extractFromToken(any())).thenReturn(MemberFixture.create());
        String commentUpdateRequest = """
                {
                    "content": "content"
                }
                """;

        // when & then
        mockMvc.perform(put("/comments")
                        .param("commentId", "1")
                        .content(commentUpdateRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isOk());
    }

    @DisplayName("댓글 식별자가 양수가 아닐 경우 댓글 수정에 실패한다.")
    @Test
    void updateCommentFail() throws Exception {
        // given
        when(authService.extractFromToken(any())).thenReturn(MemberFixture.create());
        CommentUpdateRequest commentUpdateRequest = CommentUpdateRequestFixture.create();
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), "댓글 식별자는 양수로 이루어져야 합니다.");

        // when & then
        mockMvc.perform(put("/comments")
                        .param("commentId", "0")
                        .content(objectMapper.writeValueAsString(commentUpdateRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(exceptionResponse)));
    }

    @DisplayName("댓글 내용을 입력하지 않을 경우 댓글 수정에 실패한다.")
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " "})
    void updateCommentFailByBlank(String updatedContent) throws Exception {
        // given
        when(authService.extractFromToken(any())).thenReturn(MemberFixture.create());
        CommentUpdateRequest commentUpdateRequest = new CommentUpdateRequest(updatedContent);
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), "댓글 내용을 입력해주세요.");

        // when & then
        mockMvc.perform(put("/comments")
                        .param("commentId", "1")
                        .content(objectMapper.writeValueAsString(commentUpdateRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(exceptionResponse)));
    }

    @DisplayName("올바른 형식으로 댓글 삭제를 시도하면 성공한다.")
    @Test
    void deleteComment() throws Exception {
        // given
        when(authService.extractFromToken(any())).thenReturn(MemberFixture.create());

        // when & then
        mockMvc.perform(delete("/comments")
                        .param("commentId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isOk());
    }

    @DisplayName("댓글 식별자가 양수가 아닐 경우 댓글 삭제에 실패한다.")
    @Test
    void deleteCommentFail() throws Exception {
        // given
        when(authService.extractFromToken(any())).thenReturn(MemberFixture.create());
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), "댓글 식별자는 양수로 이루어져야 합니다.");

        // when & then
        mockMvc.perform(delete("/comments")
                        .param("commentId", "0")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(exceptionResponse)));
    }
}
