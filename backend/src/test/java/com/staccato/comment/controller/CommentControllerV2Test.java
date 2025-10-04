package com.staccato.comment.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import com.staccato.ControllerTest;
import com.staccato.category.domain.Category;
import com.staccato.comment.domain.Comment;
import com.staccato.comment.service.dto.response.CommentResponseV2;
import com.staccato.comment.service.dto.response.CommentResponsesV2;
import com.staccato.exception.ExceptionResponse;
import com.staccato.fixture.category.CategoryFixtures;
import com.staccato.fixture.comment.CommentFixtures;
import com.staccato.fixture.member.MemberFixtures;
import com.staccato.fixture.staccato.StaccatoFixtures;
import com.staccato.member.domain.Member;
import com.staccato.staccato.domain.Staccato;

public class CommentControllerV2Test extends ControllerTest {

    @DisplayName("댓글을 조회했을 때 응답 직렬화에 성공한다.")
    @Test
    void readCommentsByStaccatoId() throws Exception {
        // given
        Category category = CategoryFixtures.ofDefault().build();
        Staccato staccato = StaccatoFixtures.ofDefault(category).build();
        when(authService.extractFromToken(any())).thenReturn(MemberFixtures.ofDefault().build());
        Member member = MemberFixtures.ofDefault()
                .withNickname("member")
                .withImageUrl("image.jpg")
                .build();
        Comment comment = CommentFixtures.ofDefault(staccato, member)
                .withContent("내용")
                .build();
        CommentResponseV2 commentResponse = new CommentResponseV2(comment);
        CommentResponsesV2 commentResponses = new CommentResponsesV2(List.of(commentResponse));
        when(commentService.readAllCommentsByStaccatoId(any(), any())).thenReturn(commentResponses);
        String expectedResponse = """
                {
                    "comments": [
                	    {
                            "commentId": null,
                            "memberId": null,
                            "nickname": "member",
                            "memberImageUrl": "image.jpg",
                            "content": "내용",
                            "createdAt": null,
                            "updatedAt": null
                        }
                    ]
                }
                """;

        // when & then
        mockMvc.perform(get("/v2/comments")
                        .param("staccatoId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse, true));
    }

    @DisplayName("스타카토 식별자가 양수가 아닐 경우 댓글 읽기에 실패한다.")
    @Test
    void readCommentsByStaccatoIdFail() throws Exception {
        // given
        when(authService.extractFromToken(any())).thenReturn(MemberFixtures.ofDefault().build());
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                HttpStatus.BAD_REQUEST.toString(), "스타카토 식별자는 양수로 이루어져야 합니다.");

        // when & then
        mockMvc.perform(get("/v2/comments")
                        .param("staccatoId", "0")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "token"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(exceptionResponse)));
    }
}
