package com.baghdadhomes.Utils

import android.content.Context
import android.net.Uri
import android.util.Log
import java.io.File



object FFmpegVideoCompressor {

    fun compressVideos(
        context: Context,
        uris: MutableList<Uri>,
        outputDir: File,
        onProgress: (index: Int, percent: Int) -> Unit,
        onComplete: (List<File>) -> Unit
    ) {
        val compressedFiles = mutableListOf<File>()

        fun compressNext(index: Int) {
            if (index >= uris.size) {
                onComplete(compressedFiles)
                return
            }

            val uri = uris[index]
            val inputPath = FileUtils.getPath(context, uri) // resolve Uri -> path
            if (inputPath == null) {
                Log.e("FFmpegCompressor", "Invalid Uri: $uri")
                compressNext(index + 1)
                return
            }

            val outputFile = File(outputDir, "compressed_$index.mp4")
            val command =
                "-y -i $inputPath -vcodec libx264 -crf 28 -preset veryfast -acodec aac ${outputFile.absolutePath}"

            Log.d("FFmpegCompressor", "Running: $command")

            FFmpegKit.executeAsync(command, { session ->
                val returnCode = session.returnCode
                if (returnCode.isValueSuccess) {
                    Log.d("FFmpegCompressor", "Success: ${outputFile.absolutePath}")
                    compressedFiles.add(outputFile)
                } else {
                    Log.e("FFmpegCompressor", "Failed: $inputPath")
                }
                compressNext(index + 1)
            }) { log ->
                // Parse logs for progress (basic example)
                val msg = log.message
                if (msg.contains("time=")) {
                    onProgress(index, 50) // rough progress update
                }
            }
        }

        compressNext(0)
    }
}
