package com.on.staccato

import com.on.staccato.data.dto.memory.MemoryRequest

fun createMemoryResponse(): String =
    """
    {
        "memoryId": 1,
        "memoryTitle": "해나의 추억",
        "mates": [
            {
                "memberId": 1,
                "nickname": "hannah",
                "memberImageUrl": "https://example.com/members/profile.jpg"
            }
        ],
        "moments": [
            {
                "momentId": 1,
                "staccatoTitle": "스타카토 제목",
                "momentImageUrl": "https://example.com/staccato/image.jpg",
                "visitedAt": "2024-12-18T19:31:09.681Z"
            }
        ]
    }
    """.trimIndent()

fun createMemoryCreationResponse(): String =
    """
    {
        "memoryId": 1
    }
    """.trimIndent()

fun createErrorBy400(): String =
    """
    {
        "status": "400 BAD_REQUEST"
        "message": "올바르지 않은 형식입니다"
    }
    """.trimIndent()

fun createErrorBy401(): String =
    """
    {
        "status": "401 UNAUTHORIZED"
        "message": "인증되지 않은 사용자입니다."
    }
    """.trimIndent()

fun createErrorBy403(): String =
    """
    {
        "status": "403 FORBIDDEN"
        "message": "요청하신 작업을 처리할 권한이 없습니다."
    }
    """.trimIndent()

fun createErrorBy413(): String =
    """
    {
        "status": "413 Payload Too Large",
        "message": "20MB 이하의 사진을 업로드해 주세요."
    }
    """.trimIndent()

fun createErrorBy500(): String =
    """
    {
        "status": "500 Internal Server Error",
        "message": "예기치 못한 서버 오류입니다. 다시 시도하세요."
    }
    """.trimIndent()

fun createValidMemoryRequest(): MemoryRequest = MemoryRequest(memoryTitle = "해나의 추억")

fun createInvalidMemoryRequest(): MemoryRequest = MemoryRequest(memoryTitle = "")
