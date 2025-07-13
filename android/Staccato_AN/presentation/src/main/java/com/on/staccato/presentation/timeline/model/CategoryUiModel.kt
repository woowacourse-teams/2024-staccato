package com.on.staccato.presentation.timeline.model

import com.on.staccato.presentation.color.CategoryColor
import java.time.LocalDate

data class CategoryUiModel(
    val categoryId: Long,
    val categoryTitle: String,
    val categoryThumbnailUrl: String? = null,
    val color: CategoryColor,
    val startAt: LocalDate? = null,
    val endAt: LocalDate? = null,
    val isShared: Boolean,
    val participants: ParticipantsUiModel,
    val staccatoCount: Long,
)

val dummyCategoryUiModel =
    CategoryUiModel(
        categoryId = 1,
        categoryThumbnailUrl = null,
        categoryTitle = "카테고리 제목",
        startAt = LocalDate.of(2025, 1, 1),
        endAt = LocalDate.of(2025, 12, 31),
        isShared = true,
        color = CategoryColor.GRAY,
        participants = dummyParticipantsUiModels[3],
        staccatoCount = 100,
    )

val dummyCommonCategoryWithPeriodUiModel =
    CategoryUiModel(
        categoryId = 1,
        categoryThumbnailUrl = "https://d21sl3b5mt1ob9.cloudfront.net/dev/d47fbaf5-3cf1-4425-a33b-2f81cbc4ac49",
        categoryTitle = "기간이 있는 공동 카테고리",
        startAt = LocalDate.of(2025, 1, 1),
        endAt = LocalDate.of(2025, 12, 31),
        isShared = true,
        color = CategoryColor.LIGHT_BLUE,
        participants = dummyParticipantsUiModels[1],
        staccatoCount = 100,
    )

val dummyCommonCategoryWithoutPeriodUiModel =
    CategoryUiModel(
        categoryId = 1,
        categoryThumbnailUrl = "https://d21sl3b5mt1ob9.cloudfront.net/dev/9e3da3f3-0c97-4ec5-accc-f8ea6f5dd66e",
        categoryTitle = "기간이 없는 공동 카테고리",
        startAt = null,
        endAt = null,
        isShared = true,
        color = CategoryColor.LIGHT_MINT,
        participants = dummyParticipantsUiModels[0],
        staccatoCount = 100,
    )

val dummyPersonalCategoryWithPeriodUiModel =
    CategoryUiModel(
        categoryId = 1,
        categoryThumbnailUrl = "https://d21sl3b5mt1ob9.cloudfront.net/dev/4c12b792-f85c-452f-8f17-19ede098db19",
        categoryTitle = "기간이 있는 개인 카테고리",
        startAt = LocalDate.of(2025, 1, 1),
        endAt = LocalDate.of(2025, 12, 31),
        isShared = false,
        color = CategoryColor.LIGHT_INDIGO,
        participants = dummyParticipantsUiModels[3],
        staccatoCount = 100,
    )

val dummyPersonalCategoryWithoutPeriodUiModel =
    CategoryUiModel(
        categoryId = 1,
        categoryThumbnailUrl = "https://d21sl3b5mt1ob9.cloudfront.net/dev/6e2a8f1e-636e-410d-978a-2cadd59386d5",
        categoryTitle = "기간이 없는 개인 카테고리",
        startAt = null,
        endAt = null,
        isShared = false,
        color = CategoryColor.LIGHT_PURPLE,
        participants = dummyParticipantsUiModels[3],
        staccatoCount = 100,
    )

val dummyCategoryUiModels: List<CategoryUiModel> =
    listOf(
        dummyCommonCategoryWithoutPeriodUiModel.copy(categoryId = 1L),
        dummyCommonCategoryWithPeriodUiModel.copy(categoryId = 2L),
        dummyPersonalCategoryWithPeriodUiModel.copy(categoryId = 3L),
        dummyPersonalCategoryWithoutPeriodUiModel.copy(categoryId = 4L),
        dummyCommonCategoryWithPeriodUiModel.copy(
            categoryId = 5L,
            categoryThumbnailUrl = "https://d21sl3b5mt1ob9.cloudfront.net/dev/466a03d8-d8c5-4edc-9f1d-639449bd7d38",
            categoryTitle = "아주아주 긴 제목을 가진 기간이 있는 공동 카테고리~~",
            participants = dummyParticipantsUiModels[2],
        ),
        dummyCommonCategoryWithoutPeriodUiModel.copy(
            categoryId = 6L,
            categoryThumbnailUrl = "https://d21sl3b5mt1ob9.cloudfront.net/dev/0fe7f45f-195b-4072-9fde-b22d5b28784f",
            categoryTitle = "아주아주 긴 제목을 가진 기간이 없는 공동 카테고리~~",
        ),
        dummyCommonCategoryWithoutPeriodUiModel.copy(
            categoryId = 7L,
            categoryThumbnailUrl = "https://d21sl3b5mt1ob9.cloudfront.net/dev/b25009e2-e392-4f39-ac87-6b3604239cd1",
            categoryTitle = "아주아주 긴 제목을 가진 기간이 없는 공동 카테고리~~",
        ),
        dummyPersonalCategoryWithoutPeriodUiModel.copy(
            categoryId = 8L,
            categoryThumbnailUrl = "https://d21sl3b5mt1ob9.cloudfront.net/dev/2e378041-1707-4bbf-a30b-dc1c87a12370",
            categoryTitle = "아주아주 긴 제목을 가진 기간이 없는 개인 카테고리~~",
        ),
        dummyPersonalCategoryWithoutPeriodUiModel.copy(
            categoryId = 9L,
            categoryThumbnailUrl = "https://d21sl3b5mt1ob9.cloudfront.net/dev/315c3378-d218-4e65-8741-4012e92cc1f6",
            categoryTitle = "아주아주 긴 제목을 가진 기간이 없는 개인 카테고리~~",
        ),
        dummyCommonCategoryWithoutPeriodUiModel.copy(
            categoryId = 10L,
            categoryThumbnailUrl = "https://d21sl3b5mt1ob9.cloudfront.net/dev/9e3da3f3-0c97-4ec5-accc-f8ea6f5dd66e",
            categoryTitle = "기간이 없는 공동 카테고리",
            color = CategoryColor.LIGHT_BROWN,
        ),
        dummyCommonCategoryWithPeriodUiModel.copy(
            categoryId = 11L,
            categoryThumbnailUrl = "https://d21sl3b5mt1ob9.cloudfront.net/dev/d47fbaf5-3cf1-4425-a33b-2f81cbc4ac49",
            categoryTitle = "기간이 있는 공동 카테고리",
            color = CategoryColor.LIGHT_GRAY,
        ),
        dummyCommonCategoryWithPeriodUiModel.copy(
            categoryId = 12L,
            categoryThumbnailUrl = "https://d21sl3b5mt1ob9.cloudfront.net/dev/466a03d8-d8c5-4edc-9f1d-639449bd7d38",
            categoryTitle = "아주아주 긴 제목을 가진 기간이 있는 공동 카테고리~~",
            color = CategoryColor.LIGHT_INDIGO,
        ),
        dummyPersonalCategoryWithoutPeriodUiModel.copy(
            categoryId = 13L,
            categoryThumbnailUrl = "https://d21sl3b5mt1ob9.cloudfront.net/dev/6e2a8f1e-636e-410d-978a-2cadd59386d5",
            categoryTitle = "기간이 없는 개인 카테고리",
            color = CategoryColor.LIGHT_ORANGE,
        ),
        dummyPersonalCategoryWithPeriodUiModel.copy(
            categoryId = 14L,
            categoryThumbnailUrl = "https://d21sl3b5mt1ob9.cloudfront.net/dev/4c12b792-f85c-452f-8f17-19ede098db19",
            categoryTitle = "기간이 있는 개인 카테고리",
        ),
        dummyCommonCategoryWithoutPeriodUiModel.copy(
            categoryId = 15L,
            categoryThumbnailUrl = "https://d21sl3b5mt1ob9.cloudfront.net/dev/0fe7f45f-195b-4072-9fde-b22d5b28784f",
            categoryTitle = "아주아주 긴 제목을 가진 기간이 없는 공동 카테고리~~",
        ),
        dummyCommonCategoryWithoutPeriodUiModel.copy(
            categoryId = 16L,
            categoryThumbnailUrl = "https://d21sl3b5mt1ob9.cloudfront.net/dev/b25009e2-e392-4f39-ac87-6b3604239cd1",
            categoryTitle = "아주아주 긴 제목을 가진 기간이 없는 공동 카테고리~~",
        ),
        dummyCommonCategoryWithoutPeriodUiModel.copy(
            categoryId = 17L,
            categoryThumbnailUrl = "https://d21sl3b5mt1ob9.cloudfront.net/dev/2e378041-1707-4bbf-a30b-dc1c87a12370",
            categoryTitle = "아주아주 긴 제목을 가진 기간이 없는 공동 카테고리~~",
        ),
        dummyCommonCategoryWithoutPeriodUiModel.copy(
            categoryId = 18L,
            categoryThumbnailUrl = "https://d21sl3b5mt1ob9.cloudfront.net/dev/315c3378-d218-4e65-8741-4012e92cc1f6",
            categoryTitle = "아주아주 긴 제목을 가진 기간이 없는 공동 카테고리~~",
        ),
        dummyCommonCategoryWithoutPeriodUiModel.copy(
            categoryId = 19L,
            categoryThumbnailUrl = "https://d21sl3b5mt1ob9.cloudfront.net/dev/2e378041-1707-4bbf-a30b-dc1c87a12370",
            categoryTitle = "아주아주 긴 제목을 가진 기간이 없는 공동 카테고리~~",
        ),
        dummyCommonCategoryWithoutPeriodUiModel.copy(
            categoryId = 20L,
            categoryThumbnailUrl = "https://d21sl3b5mt1ob9.cloudfront.net/dev/315c3378-d218-4e65-8741-4012e92cc1f6",
            categoryTitle = "아주아주 긴 제목을 가진 기간이 없는 공동 카테고리~~",
        ),
    )
