package com.woowacourse.staccato.presentation.travel.model

data class MateUiModel(
    val id: Long,
    val nickName: String,
    val memberImage: String,
)

val dummyMates: List<MateUiModel> =
    listOf(
        MateUiModel(
            id = 1L,
            nickName = "hxeyexn",
            memberImage =
                "https://mblogthumb-phinf.pstatic.net/MjAxODA2MjZfMjkw/MDAxNTI5OTQ3MjI2MDkw." +
                    "rQGJoaHxSRynjcOYQeLNJG8oOLK_o1s0K_jJchjmTCcg.8tusXVn2oRB6RdIASkgU2oXo0iDF9ocMEocb" +
                    "Sox0ZGog.JPEG.sinnam88/%EB%94%94%EC%A6%88%EB%8B%88_%EB%9D%BC%ED%91%BC%EC%A0%A4_%E" +
                    "B%B0%B0%EA%B2%BD%ED%99%94%EB%A9%B4_%EA%B3%A0%ED%99%94%EC%A7%88_%EB%AA%A8%EC%9D%8C_%283%29.jpg?type=w800",
        ),
        MateUiModel(
            id = 2L,
            nickName = "s6m1n",
            memberImage =
                "https://search.pstatic.net/common/?src=http%3A%2F%2Fimage.nmv.nav" +
                    "er.net%2Fcafe_2023_09_19_1290%2Fd9bcfa00-56af-11ee-9ba4-a0369ffb3258_01.jpg&type=sc960_832",
        ),
        MateUiModel(
            id = 3L,
            nickName = "hodu",
            memberImage =
                "https://mblogthumb-phinf.pstatic.net/20160314_83/siriusdhk_145788795704" +
                    "0ztVBc_PNG/nick_vector_by_simmeh-d9j99me.png?type=w420",
        ),
        MateUiModel(
            id = 4L,
            nickName = "linirini",
            memberImage = "",
        ),
        MateUiModel(
            id = 5L,
            nickName = "Ho-Tea",
            memberImage = "",
        ),
        MateUiModel(
            id = 6L,
            nickName = "카고",
            memberImage = "",
        ),
        MateUiModel(
            id = 7L,
            nickName = "폭포",
            memberImage = "",
        ),
    )
