package com.staccato.web;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ShareController {

    @GetMapping("/share-page")
    public String share(Model model) {
        return "share";
    }

    @GetMapping("/share")
    @ResponseBody
    public Map<String, Object> getComments() {
        Map<String, Object> response = new HashMap<>();

        response.put("userName", "폭포");
        response.put("expiredAt", "2025-12-31T17:00:00.000Z");
        response.put("momentImageUrls", List.of(
                "https://image.staccato.kr/dev/squirrel.png",
                "https://image.staccato.kr/dev/squirrel.png",
                "https://image.staccato.kr/dev/squirrel.png",
                "https://image.staccato.kr/dev/squirrel.png",
                "https://image.staccato.kr/dev/%E1%84%80%E1%85%B5%E1%84%87%E1%85%AE%E1%84%82%E1%85%B5%E1%84%83%E1%85%B3%E1%86%AF.jpeg"
        ));
        response.put("staccatoTitle", "귀여운 스타카토 키링");
        response.put("placeName", "한국 루터회관 8층");
        response.put("address", "대한민국 서울특별시 송파구 올림픽로35다길 42 한국루터회관 8층");
        response.put("visitedAt", "2024-09-29T17:00:00.000Z");
        response.put("feeling", "angry");
        response.put("comments", List.of(
                Map.of("nickname", "낙낙", "content", "안녕하세요~", "memberImageUrl", "https://image.staccato.kr/dev/naknak.png"),
                Map.of("nickname", "폭포", "content", "반갑습니다 ^^", "memberImageUrl", "https://image.staccato.kr/dev/squirrel.png")
        ));

        return response;
    }
}
