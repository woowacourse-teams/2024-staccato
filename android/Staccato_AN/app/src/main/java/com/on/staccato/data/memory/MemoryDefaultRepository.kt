package com.on.staccato.data.memory

import com.on.staccato.data.ApiResult
import com.on.staccato.data.dto.mapper.toDomain
import com.on.staccato.data.dto.memory.MemoryCreationResponse
import com.on.staccato.data.handle
import com.on.staccato.domain.model.Memory
import com.on.staccato.domain.model.MemoryCandidates
import com.on.staccato.domain.model.NewMemory
import com.on.staccato.domain.repository.MemoryRepository
import javax.inject.Inject

class MemoryDefaultRepository
    @Inject
    constructor(
        private val memoryDataSource: MemoryDataSource,
    ) : MemoryRepository {
        override suspend fun getMemory(memoryId: Long): ApiResult<Memory> = memoryDataSource.getMemory(memoryId).handle { it.toDomain() }

        override suspend fun getMemories(currentDate: String?): ApiResult<MemoryCandidates> =
            memoryDataSource.getMemories(currentDate).handle { it.toDomain() }

        override suspend fun createMemory(newMemory: NewMemory): ApiResult<MemoryCreationResponse> =
            memoryDataSource.createMemory(newMemory).handle { it }

        override suspend fun updateMemory(
            memoryId: Long,
            newMemory: NewMemory,
        ): ApiResult<Unit> = memoryDataSource.updateMemory(memoryId, newMemory).handle { Unit }

        override suspend fun deleteMemory(memoryId: Long): ApiResult<Unit> = memoryDataSource.deleteMemory(memoryId).handle { Unit }
    }
