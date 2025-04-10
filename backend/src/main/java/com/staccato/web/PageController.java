package com.staccato.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.staccato.staccato.service.StaccatoShareService;
import com.staccato.staccato.service.dto.response.StaccatoSharedResponse;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class PageController {
    private final StaccatoShareService staccatoShareService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/share/{token}")
    public String share(@PathVariable String token, Model model) {
        StaccatoSharedResponse response = staccatoShareService.readSharedStaccatoByToken(token);
        String thumbnailUrl = response.staccatoImageUrls().stream()
                .findFirst()
                .orElse("https://image.staccato.kr/web/share/frame.png");

        model.addAttribute("token", token);
        model.addAttribute("thumbnailUrl", thumbnailUrl);

        return "share";
    }
}
