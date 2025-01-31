package com.staccato;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.staccato.auth.service.AuthService;
import com.staccato.comment.service.CommentService;
import com.staccato.image.service.ImageService;
import com.staccato.member.service.MemberService;
import com.staccato.memory.service.MemoryService;
import com.staccato.moment.service.MomentService;

@WebMvcTest
public abstract class ControllerTest {
    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;
    @MockBean
    protected MemberService memberService;
    @MockBean
    protected MemoryService memoryService;
    @MockBean
    protected MomentService momentService;
    @MockBean
    protected ImageService imageService;
    @MockBean
    protected AuthService authService;
    @MockBean
    protected CommentService commentService;
}
