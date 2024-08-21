package com.woowacourse.staccato.data.memory

import com.woowacourse.staccato.data.ResponseResult
import com.woowacourse.staccato.data.dto.memory.MemoryCreationResponse
import com.woowacourse.staccato.data.dto.memory.MemoryResponse
import com.woowacourse.staccato.domain.model.NewMemory

interface MemoryDataSource {
    suspend fun getMemory(memoryId: Long): ResponseResult<MemoryResponse>

    suspend fun createMemory(newMemory: NewMemory): ResponseResult<MemoryCreationResponse>

    suspend fun updateMemory(
        memoryId: Long,
        newMemory: NewMemory,
    ): ResponseResult<Unit>

    suspend fun deleteMemory(memoryId: Long): ResponseResult<Unit>
}
