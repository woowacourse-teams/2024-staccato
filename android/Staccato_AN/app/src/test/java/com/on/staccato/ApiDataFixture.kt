package com.on.staccato

import com.on.staccato.data.dto.memory.MemoryRequest

val memoryResponse: String =
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

val memoryCreationResponse: String =
    """
    {
        "memoryId": 1
    }
    """.trimIndent()

val validMemoryRequest = MemoryRequest(memoryTitle = "해나의 추억")

val invalidMemoryRequest = MemoryRequest(memoryTitle = "")

val errorBy400: String =
    """
    {
        "status": "400 BAD_REQUEST"
        "message": "올바르지 않은 형식입니다"
    }
    """.trimIndent()

val errorBy401: String =
    """
    {
        "status": "401 UNAUTHORIZED"
        "message": "인증되지 않은 사용자입니다."
    }
    """.trimIndent()

val errorBy403: String =
    """
    {
        "status": "403 FORBIDDEN"
        "message": "요청하신 작업을 처리할 권한이 없습니다."
    }
    """.trimIndent()

val errorBy413: String =
    """
    {
        "status": "413 Payload Too Large",
        "message": "20MB 이하의 사진을 업로드해 주세요."
    }
    """.trimIndent()

val errorBy500: String =
    """
    {
        "status": "500 Internal Server Error",
        "message": "예기치 못한 서버 오류입니다. 다시 시도하세요."
    }
    """.trimIndent()
