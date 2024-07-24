package com.woowacourse.staccato.presentation.visit.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.woowacourse.staccato.presentation.visit.model.VisitDetailUiModel

class VisitViewModel : ViewModel() {
    private val _visitDefault = MutableLiveData<VisitDetailUiModel.VisitDefaultUiModel>()
    val visitDefault: LiveData<VisitDetailUiModel.VisitDefaultUiModel> get() = _visitDefault

    private val _visitLogs = MutableLiveData<List<VisitDetailUiModel.VisitLogUiModel>>()
    val visitLogs: LiveData<List<VisitDetailUiModel.VisitLogUiModel>> get() = _visitLogs

    fun fetchVisitDetailData() {
        fetchVisitDefault()
        fetchVisitLogs()
    }

    private fun fetchVisitDefault() {
        _visitDefault.value =
            VisitDetailUiModel.VisitDefaultUiModel(
                1,
                "곽지해수욕장",
                "https://postfiles.pstatic.net/MjAyNDA3MTZfMTMg/MDAxNzIxMDk5MjU5NTY0._coi0xlHtVjiwV7XjIABXv9EeRqi-kbJL3A7WeElA5og" +
                    ".mv0BFE8-Sv3TKhzZiw38YdkhGTW3NRYoO16ZHH2d5YEg.JPEG/%EC%A0%9C%EC%A3%BC%EB%8F%84.jpg?type=w966",
                "제주특별자치도 제주시 애월읍 곽지리",
                "2024년 7월 15일에 처음 방문했어요!",
                3,
            )
    }

    private fun fetchVisitLogs() {
        _visitLogs.value =
            listOf(
                VisitDetailUiModel.VisitLogUiModel(
                    0,
                    0,
                    "s6m1n",
                    "https://search.pstatic.net/common/?src=http%3A%2F%2Fimage.nmv.nav" +
                        "er.net%2Fcafe_2023_09_19_1290%2Fd9bcfa00-56af-11ee-9ba4-a0369ffb3258_01.jpg&type=sc960_832",
                    "오랜만에 우테코 친구들과 여행!\n바다 색깔이 너무 예뻤다.",
                ),
                VisitDetailUiModel.VisitLogUiModel(
                    0,
                    0,
                    "haena",
                    "https://search.pstatic.net/sunny/?src=https%3A%2F%2Fdcimg2.dcinsi" +
                        "de.co.kr%2Fviewimage.php%3Fid%3D2fb4de21f1ed2c%26no%3D24b0d769e1d3" +
                        "2ca73fe987fa1bd8233c0baf48e1ef0fb2b7008f21f62cd074543b96b120f51c61e" +
                        "c086ef073c6e6f22b31f9b2a3bf236a7c5adcb183d4e6504c158ab2a2ea18145a1436&type=sc960_832",
                    "안녕 ? 나는 해나야\n오늘은 내가 비눗방울 만드는 법을 알려줄게",
                ),
                VisitDetailUiModel.VisitLogUiModel(
                    0,
                    0,
                    "hodu",
                    "https://search.pstatic.net/common/?src=http%3A%2F%2Fcafefiles." +
                        "naver.net%2FMjAyMDA1MDZfMTQw%2FMDAxNTg4NzY1ODYxODA1.vFk_nJQvHMHtT" +
                        "IDzK7DiXYZHV-SBqiDgZzn0mxTRG0gg.sgBRmTE6nynrV5tir0KjwG_kKwytvMhVV" +
                        "EhZ0A6CL-8g.JPEG%2FexternalFile.jpg&type=sc960_832",
                    "좋은 사람, 좋은 시간.\n갓 두 ~!?",
                ),
            )
    }
}
