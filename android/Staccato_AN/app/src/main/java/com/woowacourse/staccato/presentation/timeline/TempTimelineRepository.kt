package com.woowacourse.staccato.presentation.timeline

import com.woowacourse.staccato.domain.repository.TimelineRepository

class TempTimelineRepository : TimelineRepository {
    private val travels =
        listOf(
            TimelineTravelUiModel(
                travelId = 0L,
                travelThumbnail = null,
                travelPeriod = "2024.07.23",
                travelTitle = "우테코 선릉캠 탐방",
            ),
            TimelineTravelUiModel(
                travelId = 1L,
                travelThumbnail = "https://cdn.tourtoctoc.com/news/photo/202305/520_2742_5617.jpg",
                travelPeriod = "2024.06.30 - 07.04",
                travelTitle = "제주도 여행",
            ),
            TimelineTravelUiModel(
                travelId = 2L,
                travelThumbnail = null,
                travelPeriod = "2024.06.28",
                travelTitle = "파리 여행",
            ),
            TimelineTravelUiModel(
                travelId = 3L,
                travelThumbnail = "https://pds.joongang.co.kr/news/component/htmlphoto_mmdata/202203/11/97c3e727-0d4c-4fba-83c7-7558d9455651.jpg",
                travelPeriod = "2024.06.26",
                travelTitle = "포항 영일대 당일치기",
            ),
            TimelineTravelUiModel(
                travelId = 4L,
                travelThumbnail = "https://triptogo.world/web/product/big/202104/e827b41e2d22aeddc8015b018df9aa5b.png",
                travelPeriod = "2024.05.28 - 29",
                travelTitle = "서울 나들이",
            ),
            TimelineTravelUiModel(
                travelId = 5L,
                travelThumbnail = "https://triptogo.world/web/product/big/202104/e827b41e2d22aeddc8015b018df9aa5b.png",
                travelPeriod = "2024.05.28 - 29",
                travelTitle = "서울 나들이",
            ),
            TimelineTravelUiModel(
                travelId = 6L,
                travelThumbnail = "https://triptogo.world/web/product/big/202104/e827b41e2d22aeddc8015b018df9aa5b.png",
                travelPeriod = "2024.05.28 - 29",
                travelTitle = "서울 나들이",
            ),
            TimelineTravelUiModel(
                travelId = 7L,
                travelThumbnail = "https://triptogo.world/web/product/big/202104/e827b41e2d22aeddc8015b018df9aa5b.png",
                travelPeriod = "2024.05.28 - 29",
                travelTitle = "서울 나들이",
            ),
        )

    override fun loadTravels(): List<TimelineTravelUiModel> {
        return travels
    }
}
