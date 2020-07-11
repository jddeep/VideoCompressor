package com.jddeep.videocompressor.repositories

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.hiteshsondhi88.libffmpeg.FFmpeg
import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler
import java.util.*


class VideoRepo {
    private var compressedVideoPath = MutableLiveData<String>()
    private lateinit var outputPath: String
    private var status: String = ""

    init {
        compressedVideoPath = MutableLiveData()
    }

    fun compressVideo(inputPath: String, bitrate: String, context: Context) {

        outputPath =
            context.filesDir.path + "/CompressedVids/" + Calendar.getInstance().timeInMillis.toString() + ".mp4"

        val commandParams = arrayOfNulls<String>(26)
        commandParams[0] = "-y"
        commandParams[1] = "-i"
        commandParams[2] = inputPath
        commandParams[3] = "-s"
        commandParams[4] = "240x320"
        commandParams[5] = "-r"
        commandParams[6] = "20"
        commandParams[7] = "-c:v"
        commandParams[8] = "libx264"
        commandParams[9] = "-preset"
        commandParams[10] = "ultrafast"
        commandParams[11] = "-c:a"
        commandParams[12] = "copy"
        commandParams[13] = "-me_method"
        commandParams[14] = "zero"
        commandParams[15] = "-tune"
        commandParams[16] = "fastdecode"
        commandParams[17] = "-tune"
        commandParams[18] = "zerolatency"
        commandParams[19] = "-strict"
        commandParams[20] = "-2"
        commandParams[21] = "-b:v"
        commandParams[22] = bitrate.trim() + "k"
        commandParams[23] = "-pix_fmt"
        commandParams[24] = "yuv420p"
        commandParams[25] = outputPath

        startCompress(commandParams, context)
    }

    private fun startCompress(commandParams: Array<String?>, context: Context) {
        try {
            FFmpeg.getInstance(context)
                .execute(commandParams, object : FFmpegExecuteResponseHandler {
                    override fun onSuccess(message: String) {
                        status = "SUCCESS"
                        Log.e("SuccessMessage: ", message)
                        compressedVideoPath.postValue(outputPath)
                    }

                    override fun onProgress(message: String) {
                        status = "RUNNING"
                        Log.e("VideoCronProgress", message)
                    }

                    override fun onFailure(message: String) {
                        status = "FAILED"
                        compressedVideoPath.postValue(null)
                        Log.e("VideoCompressor", message)
                    }

                    override fun onStart() {}
                    override fun onFinish() {
                        Log.e("VideoCronProgress", "finished$outputPath")
                        status = "FINISHED"
                        compressedVideoPath.postValue(outputPath)
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
            status = "FAILED"
            Log.e("Catch Exception: ", e.message ?: "")
        }
    }

    fun getCompressVidPath(): LiveData<String> {
        return compressedVideoPath
    }
}