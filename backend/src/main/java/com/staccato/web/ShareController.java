package com.staccato.web;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

        // 현재 사용자 이름
        response.put("userName", "폭포");

        // 댓글 데이터
        List<Map<String, String>> comments = Arrays.asList(
                Map.of("nickname", "낙낙", "content", "미안하다 이거 보여주려고 어그로끌었다.. 나루토 사스케 싸움수준 ㄹㅇ실화냐? 진짜 세계관최강자들의 싸움이다.. 그찐따같던 나루토가 맞나? 진짜 나루토는 전설이다..진짜옛날에 맨날나루토봤는데 왕같은존재인 호카게 되서 세계최강 전설적인 영웅이된나루토보면 진짜내가다 감격스럽고 나루토 노래부터 명장면까지 가슴울리는장면들이 뇌리에 스치면서 가슴이 웅장해진다..", "memberImageUrl", "https://image.staccato.kr/dev/naknak.png"),
                Map.of("nickname", "폭포", "content", "ㄹㅇ 실화냐", "memberImageUrl", "https://image.staccato.kr/dev/squirrel.png")
        );
        response.put("comments", comments);

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
        response.put("emojiPaths", emojiPaths);

        response.put("expiredAt", formattedExpiredAt);
        response.put("staccatoTitle", "귀여운 스타카토 키링");
        response.put("momentImageUrls", List.of(
                "https://image.staccato.kr/dev/squirrel.png",
                "https://image.staccato.kr/dev/squirrel.png",
                "https://image.staccato.kr/dev/squirrel.png",
                "https://image.staccato.kr/dev/squirrel.png",
                "https://image.staccato.kr/dev/%E1%84%80%E1%85%B5%E1%84%87%E1%85%AE%E1%84%82%E1%85%B5%E1%84%83%E1%85%B3%E1%86%AF.jpeg"
        ));
        response.put("visitedAt", formattedVisitedAt);
        response.put("placeName", "한국 루터회관 8층");
        response.put("address", "대한민국 서울특별시 송파구 올림픽로35다길 42 한국루터회관 8층");

        return response;
    }
}
