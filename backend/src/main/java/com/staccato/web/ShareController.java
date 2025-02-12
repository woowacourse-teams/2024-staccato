package com.staccato.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ShareController {

    @GetMapping("/share-page")
    public String share(Model model) {
        return "share";
    }
}
