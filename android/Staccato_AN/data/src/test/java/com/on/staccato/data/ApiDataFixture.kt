package com.on.staccato.data

import com.on.staccato.data.dto.PostRequest

fun createGetResponse(): String =
    """
    {
        "id": 1
        "content": "post 성공"
    }
    """.trimIndent()

fun createPostResponse(): String =
    """
    {
        "id": 1
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

fun createValidRequest(): PostRequest = PostRequest(content = "유효한 형식")

fun createInvalidRequest(): PostRequest = PostRequest(content = "유효하지 않은 형식")
