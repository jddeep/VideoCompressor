package com.jddeep.videocompressor.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.jddeep.videocompressor.repositories.VideoRepo

class VideoViewModel : ViewModel() {
    private var videoRepo: VideoRepo = VideoRepo()

    init {
        videoRepo = VideoRepo()
    }

    fun compressVideo(inputPath: String, bitrate: String, context: Context) {
        // Coroutines could have been used to run processing in IO/Default Dispatcher scope
        videoRepo.compressVideo(inputPath, bitrate, context)
    }

    public fun getCompressedVidPath(): LiveData<String> {
        return videoRepo.getCompressVidPath()
    }

    override fun onCleared() {
        super.onCleared()
        Log.e("VideoViewModel", "VideoViewModel destroyed!")
    }
}