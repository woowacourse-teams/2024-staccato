package com.on.staccato

import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okio.BufferedSink

fun makeFakeImageFile(): MultipartBody.Part = MultipartBody.Part.createFormData(name = "image", filename = "fileName", FakeRequestBody())

class FakeRequestBody : RequestBody() {
    override fun contentType(): MediaType? = "image/jpeg".toMediaTypeOrNull()

    override fun writeTo(sink: BufferedSink) {
        sink.write("fake image data".toByteArray())
    }
}
