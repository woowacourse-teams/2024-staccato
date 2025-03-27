package com.staccato.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class PageController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/share/{token}")
    public String share(@PathVariable String token, Model model) {
        model.addAttribute("token", token);
        return "share";
    }
}
