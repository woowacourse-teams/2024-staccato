package com.staccato.web;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ShareController {

    @GetMapping("/share")
    public String share(Model model) {
        addAttributeToModel(model);
        return "share";
    }

    private void addAttributeToModel(Model model) {
        String visitedAtStr = "2024-09-29T17:00:00.000Z";
        LocalDateTime visitedAt = LocalDateTime.parse(visitedAtStr, DateTimeFormatter.ISO_DATE_TIME);
        String formattedVisitedAt = visitedAt.format(DateTimeFormatter.ofPattern("yyyy년 M월 d일 a h")) + "시에 방문했어요";

        String feeling = "angry";
        Map<String, String> emojiPaths = new HashMap<>();
        emojiPaths.put("happy", feeling.equals("happy") ? "/images/share/happy.png" : "/images/share/happy_gray.png");
        emojiPaths.put("angry", feeling.equals("angry") ? "/images/share/angry.png" : "/images/share/angry_gray.png");
        emojiPaths.put("sad", feeling.equals("sad") ? "/images/share/sad.png" : "/images/share/sad_gray.png");
        emojiPaths.put("scared", feeling.equals("scared") ? "/images/share/scared.png" : "/images/share/scared_gray.png");
        emojiPaths.put("excited", feeling.equals("excited") ? "/images/share/excited.png" : "/images/share/excited_gray.png");
        model.addAttribute("emojiPaths", emojiPaths);

        model.addAttribute("staccatoTitle", "귀여운 스타카토 키링");
        model.addAttribute("momentImageUrls", List.of(
                "https://image.staccato.kr/dev/squirrel.png",
                "https://image.staccato.kr/dev/squirrel.png",
                "https://image.staccato.kr/dev/squirrel.png",
                "https://image.staccato.kr/dev/squirrel.png",
                "https://image.staccato.kr/dev/%E1%84%80%E1%85%B5%E1%84%87%E1%85%AE%E1%84%82%E1%85%B5%E1%84%83%E1%85%B3%E1%86%AF.jpeg"
        ));
        model.addAttribute("visitedAt", formattedVisitedAt);
        model.addAttribute("placeName", "한국 루터회관 8층");
        model.addAttribute("address", "대한민국 서울특별시 송파구 올림픽로35다길 42 한국루터회관 8층");
        model.addAttribute("comments", List.of(
                Map.of(
                        "nickname", "sample nickname",
                        "memberImageUrl", "https://image.staccato.kr/dev/squirrel.png",
                        "content", "sample content"
                )
        ));
    }
}
