package com.staccato;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.staccato.auth.service.AuthService;
import com.staccato.category.service.CategoryService;
import com.staccato.comment.service.CommentService;
import com.staccato.image.service.ImageService;
import com.staccato.invitation.service.InvitationService;
import com.staccato.member.service.MemberService;
import com.staccato.notification.service.NotificationService;
import com.staccato.staccato.service.StaccatoService;
import com.staccato.staccato.service.StaccatoShareService;

@WebMvcTest
public abstract class ControllerTest {
    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;
    @MockBean
    protected MemberService memberService;
    @MockBean
    protected CategoryService categoryService;
    @MockBean
    protected StaccatoService staccatoService;
    @MockBean
    protected ImageService imageService;
    @MockBean
    protected AuthService authService;
    @MockBean
    protected CommentService commentService;
    @MockBean
    protected StaccatoShareService staccatoShareService;
    @MockBean
    protected InvitationService invitationService;
    @MockBean
    protected NotificationService notificationService;
}
