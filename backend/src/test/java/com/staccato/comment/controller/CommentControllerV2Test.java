package com.staccato.comment.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import com.staccato.ControllerTest;
import com.staccato.comment.service.dto.request.CommentRequestV2;
import com.staccato.comment.service.dto.response.CommentResponse;
import com.staccato.comment.service.dto.response.CommentResponses;
import com.staccato.exception.ExceptionResponse;
import com.staccato.fixture.Member.MemberFixture;

public class CommentControllerV2Test extends ControllerTest {
    private static final int MAX_CONTENT_LENGTH = 500;
    private static final long MIN_STACCATO_ID = 1L;

    static Stream<Arguments> invalidCommentCreateRequestProvider() {
        return Stream.of(
                Arguments.of(
                        new CommentRequestV2(null, "예시 댓글 내용"),
                        "스타카토를 선택해주세요."
                ),
                Arguments.of(
                        new CommentRequestV2(MIN_STACCATO_ID - 1, "예시 댓글 내용"),
                        "스타카토 식별자는 양수로 이루어져야 합니다."
                ),
                Arguments.of(
                        new CommentRequestV2(MIN_STACCATO_ID, null),
                        "댓글 내용을 입력해주세요."
                ),
                Arguments.of(
                        new CommentRequestV2(MIN_STACCATO_ID, ""),
                        "댓글 내용을 입력해주세요."
                ),
                Arguments.of(
                        new CommentRequestV2(MIN_STACCATO_ID, " "),
                        "댓글 내용을 입력해주세요."
                ),
                Arguments.of(
                        new CommentRequestV2(MIN_STACCATO_ID, "1".repeat(MAX_CONTENT_LENGTH + 1)),
                        "댓글은 공백 포함 500자 이하로 입력해주세요."
                )
        );
    }

    @DisplayName("댓글 생성 요청/응답에 대한 직렬화/역직렬화에 성공한다.")
    @Test
    void createComment() throws Exception {
        // given
        when(authService.extractFromToken(any())).thenReturn(MemberFixture.create());
        String CommentCreateRequest = """
                {
                    "staccatoId": 1,
                    "content": "content"
                }
                """;
        when(commentService.createComment(any(), any())).thenReturn(1L);

        // when & then
        mockMvc.perform(post("/comments/v2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CommentCreateRequest)
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, "/comments/1"));
    }

    @DisplayName("올바르지 않은 형식으로 정보를 입력하면, 댓글을 생성할 수 없다.")
    @ParameterizedTest
    @MethodSource("invalidCommentCreateRequestProvider")
    void createCommentFail(CommentRequestV2 CommentRequestV2, String expectedMessage) throws Exception {
        // given
        when(authService.extractFromToken(any())).thenReturn(MemberFixture.create());
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), expectedMessage);

        // when & then
        mockMvc.perform(post("/comments/v2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(CommentRequestV2))
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(exceptionResponse)));
    }

    @DisplayName("댓글을 조회했을 때 응답 직렬화에 성공한다.")
    @Test
    void readCommentsBystaccatoId() throws Exception {
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
        mockMvc.perform(get("/comments/v2")
                        .param("staccatoId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @DisplayName("스타카토 식별자가 양수가 아닐 경우 댓글 읽기에 실패한다.")
    @Test
    void readCommentsBystaccatoIdFail() throws Exception {
        // given
        when(authService.extractFromToken(any())).thenReturn(MemberFixture.create());
        ExceptionResponse exceptionResponse = new ExceptionResponse(HttpStatus.BAD_REQUEST.toString(), "스타카토 식별자는 양수로 이루어져야 합니다.");

        // when & then
        mockMvc.perform(get("/comments/v2")
                        .param("staccatoId", "0")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(exceptionResponse)));
    }
}
