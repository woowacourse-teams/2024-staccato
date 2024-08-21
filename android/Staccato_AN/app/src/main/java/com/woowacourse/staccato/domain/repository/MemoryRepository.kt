package com.woowacourse.staccato.domain.repository

import com.woowacourse.staccato.data.ResponseResult
import com.woowacourse.staccato.data.dto.memory.MemoryCreationResponse
import com.woowacourse.staccato.domain.model.Memory
import com.woowacourse.staccato.domain.model.NewMemory

interface MemoryRepository {
    suspend fun getMemory(memoryId: Long): ResponseResult<Memory>

    suspend fun createMemory(newMemory: NewMemory): ResponseResult<MemoryCreationResponse>

    suspend fun updateMemory(
        memoryId: Long,
        newMemory: NewMemory,
    ): ResponseResult<Unit>

    suspend fun deleteMemory(memoryId: Long): ResponseResult<Unit>
}
