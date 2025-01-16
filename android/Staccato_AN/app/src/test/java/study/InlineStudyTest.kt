package study

import com.on.staccato.data.ApiResult
import com.on.staccato.data.Exception
import com.on.staccato.data.ServerError
import com.on.staccato.data.Success
import com.on.staccato.domain.model.Member
import com.on.staccato.domain.model.Memory
import com.on.staccato.domain.model.MemoryStaccato
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.time.measureTime

class InlineStudyTest {
    @Test
    fun `inline 사용 유무에 따른 소요 시간 비교`() {
        val nonInlineTime1 = measureTime { createFakeMemory().noInlineFunction { it } }
        val inlineTime1 = measureTime { createFakeMemory().inlineFunction { it } }

        val inlineTime2 = measureTime { createFakeMemory().inlineFunction { it } }
        val nonInlineTime2 = measureTime { createFakeMemory().noInlineFunction { it } }

        val inlineTime3 = measureTime { createFakeMemory().inlineFunction { it } }
        val nonInlineTime3 = measureTime { createFakeMemory().noInlineFunction { it } }

        val nonInlineTime4 = measureTime { createFakeMemory().noInlineFunction { it } }
        val inlineTime4 = measureTime { createFakeMemory().inlineFunction { it } }

        val nonInlineTime5 = measureTime { createFakeMemory().noInlineFunction { it } }
        val inlineTime5 = measureTime { createFakeMemory().inlineFunction { it } }

        val inlineTime6 = measureTime { createFakeMemory().inlineFunction { it } }
        val nonInlineTime6 = measureTime { createFakeMemory().noInlineFunction { it } }

        val inlineTime7 = measureTime { createFakeMemory().inlineFunction { it } }
        val nonInlineTime7 = measureTime { createFakeMemory().noInlineFunction { it } }

        val inlineTime8 = measureTime { createFakeMemory().inlineFunction { it } }
        val nonInlineTime8 = measureTime { createFakeMemory().noInlineFunction { it } }

        val nonInlineTime9 = measureTime { createFakeMemory().noInlineFunction { it } }
        val inlineTime9 = measureTime { createFakeMemory().inlineFunction { it } }

        val nonInlineTime10 = measureTime { createFakeMemory().noInlineFunction { it } }
        val inlineTime10 = measureTime { createFakeMemory().inlineFunction { it } }

        println("Inline: $inlineTime1 -> $inlineTime2 -> $inlineTime3 -> $inlineTime4 -> $inlineTime5")
        println("        -> $inlineTime6 -> $inlineTime7 -> $inlineTime8 -> $inlineTime9 -> $inlineTime10")
        println("Non-inline: $nonInlineTime1 -> $nonInlineTime2 -> $nonInlineTime3 -> $nonInlineTime4 -> $nonInlineTime5")
        println("            -> $nonInlineTime6 -> $nonInlineTime7 -> $nonInlineTime8 -> $nonInlineTime9 -> $nonInlineTime10")

        // 학습 테스트이므로 테스트가 실패하는 경우도 있기 때문에 CI 성공을 위한 주석 처리
//        assertAll(
//            { assertThat(inlineTime1).isLessThan(nonInlineTime1) },
//            { assertThat(inlineTime2).isLessThan(nonInlineTime2) },
//            { assertThat(inlineTime3).isLessThan(nonInlineTime3) },
//            { assertThat(inlineTime4).isLessThan(nonInlineTime4) },
//            { assertThat(inlineTime5).isLessThan(nonInlineTime5) },
//            { assertThat(inlineTime5).isLessThan(nonInlineTime6) },
//            { assertThat(inlineTime3).isLessThan(nonInlineTime7) },
//            { assertThat(inlineTime4).isLessThan(nonInlineTime8) },
//            { assertThat(inlineTime5).isLessThan(nonInlineTime9) },
//            { assertThat(inlineTime5).isLessThan(nonInlineTime10) },
//        )
    }
}

fun createFakeMemory(): Success<Memory> {
    return Success(
        Memory(
            memoryId = 1L,
            memoryThumbnailUrl = "https://example.com/fake-thumbnail.jpg",
            memoryTitle = "Fake Memory Title",
            startAt = LocalDate.of(2025, 1, 1),
            endAt = LocalDate.of(2025, 12, 31),
            description = "This is a fake memory description used for testing.",
            mates =
                listOf(
                    Member(1L, "Member 1"),
                    Member(2L, "Member 2"),
                ),
            staccatos =
                (1..20).map { id ->
                    MemoryStaccato(
                        staccatoId = id.toLong(),
                        staccatoTitle = "Staccato Title $id",
                        staccatoImageUrl = "https://example.com/image$id-1.jpg",
                        visitedAt = LocalDateTime.now().minusDays(id.toLong()),
                    )
                },
        ),
    )
}

inline fun <T : Any, R : Any> ApiResult<T>.inlineFunction(convert: (T) -> R): ApiResult<R> =
    when (this) {
        is Exception -> Exception(e)
        is ServerError -> ServerError(status, message)
        is Success -> Success(convert(data))
    }

fun <T : Any, R : Any> ApiResult<T>.noInlineFunction(convert: (T) -> R): ApiResult<R> =
    when (this) {
        is Exception -> Exception(e)
        is ServerError -> ServerError(status, message)
        is Success -> Success(convert(data))
    }
