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
        String formattedVisitedAt = visitedAt.format(DateTimeFormatter.ofPattern("yyyy년 M월 d일 a h시"));

        String expiredAtStr = "2025-12-31T17:00:00.000Z";
        LocalDateTime expiredAt = LocalDateTime.parse(expiredAtStr, DateTimeFormatter.ISO_DATE_TIME);
        String formattedExpiredAt = expiredAt.format(DateTimeFormatter.ofPattern("yyyy년 M월 d일"));

        String feeling = "angry";
        Map<String, String> emojiPaths = new HashMap<>();
        emojiPaths.put("happy", feeling.equals("happy") ? "/images/share/happy.png" : "/images/share/happy-gray.png");
        emojiPaths.put("angry", feeling.equals("angry") ? "/images/share/angry.png" : "/images/share/angry-gray.png");
        emojiPaths.put("sad", feeling.equals("sad") ? "/images/share/sad.png" : "/images/share/sad-gray.png");
        emojiPaths.put("scared", feeling.equals("scared") ? "/images/share/scared.png" : "/images/share/scared-gray.png");
        emojiPaths.put("excited", feeling.equals("excited") ? "/images/share/excited.png" : "/images/share/excited-gray.png");
        model.addAttribute("emojiPaths", emojiPaths);

        model.addAttribute("userName", "폭포");
        model.addAttribute("expiredAt", formattedExpiredAt);
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
                        "nickname", "낙낙",
                        "memberImageUrl", "https://image.staccato.kr/dev/naknak.png",
                        "content", "이 편지는 영국에서 최초로 시작되어 일년에 한바퀴를 돌면서 받는 사람에게 행운을 주었고 지금은 당신에게로 옮겨진 이 편지는 4일 안에 당신 곁을 떠나야 합니다. 이 편지를 포함해서 7통을 행운이 필요한 사람에게 보내 주셔야 합니다. 복사를 해도 좋습니다. 혹 미신이라 하실지 모르지만 사실입니다."
                ),
                Map.of(
                        "nickname", "폭포",
                        "memberImageUrl", "https://image.staccato.kr/dev/squirrel.png",
                        "content", "이 편지는 영국에서 최초로 시작되어 일년에 한바퀴를 돌면서 받는 사람에게 행운을 주었고 지금은 당신에게로 옮겨진 이 편지는 4일 안에 당신 곁을 떠나야 합니다. 이 편지를 포함해서 7통을 행운이 필요한 사람에게 보내 주셔야 합니다. 복사를 해도 좋습니다. 혹 미신이라 하실지 모르지만 사실입니다."
                )
        ));
    }
}
