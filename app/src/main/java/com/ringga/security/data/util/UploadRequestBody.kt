package com.ringga.security.data.util

import android.os.Handler
import android.os.Looper
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.File
import java.io.FileInputStream

class UploadRequestBody(
        private val file: File,
        private val contentType: String
) : RequestBody() {

    /**
     * file ini berfungsi untuk validasi file dari internal memory
     * */

    override fun contentType() = MediaType.parse("$contentType/*")

    override fun contentLength() = file.length()

    override fun writeTo(sink: BufferedSink) {
        val length = file.length()
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        val fileInputStream = FileInputStream(file)
        var uploaded = 0L
        fileInputStream.use { inputStream ->
            var read: Int
            val handler = Handler(Looper.getMainLooper())
            while (inputStream.read(buffer).also { read = it } != -1) {

                uploaded += read
                sink.write(buffer, 0, read)
            }
        }
    }


    companion object {
        private const val DEFAULT_BUFFER_SIZE = 10240
    }
}