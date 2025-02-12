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
        response.put("feeling", "scared");

        String firstCommentContent = "나랏말싸미 듕귁에 달아 문자와로 서로 사맛디 아니할쎄\n" +
                "이런 젼차로 어린 백셩이 니르고져 홇베이셔도\n" +
                "마참내 제 뜨들 시러펴디 몯핧 노미하니라\n" +
                "내 이랄 윙하여 어엿비너겨 새로 스믈 여덟 짜랄 맹가노니\n" +
                "사람마다 해여 수비니겨 날로 쑤메 뼌한킈 하고져 할따라미니라.";
        String secondCommentContent = "우리 나라 말이 중국과 달라 한자와는 서로 말이 통하지 아니하여서\n" +
                "이런 까닭으로 어리석은 백성이 말하고자 하는 바가 있어도\n" +
                "마침내 제 뜻을 펴지 못하는 사람이 많다.\n" +
                "내가 이것을 가엾게 여겨 새로 스물 여덟 글자를 만드니\n" +
                "모든 사람들로 하여금 쉽게 익혀서 날마다 쓰는 게 편하게 하고자 할 따름이니라.";
        String thirdCommentContent = "짧은 코멘트 테스트";
        response.put("comments", List.of(
                Map.of("nickname", "낙낙", "content", firstCommentContent, "memberImageUrl", "https://image.staccato.kr/dev/naknak.png"),
                Map.of("nickname", "폭포", "content", secondCommentContent, "memberImageUrl", "https://image.staccato.kr/dev/squirrel.png"),
                Map.of("nickname", "폭포", "content", thirdCommentContent, "memberImageUrl", "https://image.staccato.kr/dev/squirrel.png")
        ));

        return response;
    }
}
