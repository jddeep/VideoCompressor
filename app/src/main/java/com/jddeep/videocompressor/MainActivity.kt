package com.jddeep.videocompressor

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.MediaController
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.github.hiteshsondhi88.libffmpeg.FFmpeg
import com.github.hiteshsondhi88.libffmpeg.FFmpegLoadBinaryResponseHandler
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException
import com.jddeep.videocompressor.databinding.ActivityMainBinding
import com.jddeep.videocompressor.models.VideoModel
import com.jddeep.videocompressor.utils.Utils
import com.jddeep.videocompressor.viewmodels.VideoViewModel
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var mainBinding: ActivityMainBinding
    private lateinit var videoViewModel: VideoViewModel
    private lateinit var selectedVideoPath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val videoModel = VideoModel()
        videoModel.buttonText = "PICK VIDEO"
        videoModel.compressButtonText = "COMPRESS VIDEO"
        videoModel.compressBitrate = ""
        mainBinding.videoModel = videoModel
        checkPermission()
        videoViewModel = ViewModelProviders.of(this).get(VideoViewModel::class.java)
        videoViewModel.getCompressedVidPath().observe(this, androidx.lifecycle.Observer {
            if (!(it.isNullOrEmpty())) {
                val compressVidActivityIntent = Intent(this, CompressedVideoActivity::class.java)
                compressVidActivityIntent.putExtra("vidPath", it)
                Log.e("VidPath: ", it)
                startActivity(compressVidActivityIntent)
            }
        })
        pickVideoBtn.setOnClickListener {
            val intent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            )
            intent.type = "video/*"
            startActivityForResult(intent, 101)
        }
        loadFfmpeg()
        compressVidBtn.setOnClickListener {
            compressPB.visibility = View.VISIBLE
            videoViewModel.compressVideo(
                selectedVideoPath,
                bitrateET.text.toString(),
                applicationContext
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == 101) {
                Log.e("Data: ", data.toString())
                val uri = data.data
                Log.e("URI: ", uri.toString())
                Log.e("Path: ", uri?.path.toString())
                selectedVideoPath = Utils().getPath(uri, applicationContext)
                if (selectedVideoPath != "") {
                    ogVideoView.setVideoPath(selectedVideoPath)
                    val mediaController = MediaController(this)
                    ogVideoView.setMediaController(mediaController)
                    ogVideoView.start()
                }
            }
        }
    }

    private fun loadFfmpeg() {
        //Load FFMpeg library
        try {
            FFmpeg.getInstance(this).loadBinary(object : FFmpegLoadBinaryResponseHandler {
                override fun onFailure() {
                    Log.e("FFMpeg", "Failed to load FFMpeg library.")
                }

                override fun onSuccess() {
                    Log.i("FFMpeg", "FFMpeg Library loaded!")
                }

                override fun onStart() {
                    Log.i("FFMpeg", "FFMpeg Started")
                }

                override fun onFinish() {
                    Log.i("FFMpeg", "FFMpeg Stopped")
                }
            })
        } catch (e: FFmpegNotSupportedException) {
            e.printStackTrace()
            Log.e("MainActivty: ", e.message ?: "")
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("MainActivty: ", e.message ?: "")
        }
    }

    private fun checkPermission(): Boolean {
        val READ_EXTERNAL_PERMISSION =
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        val WRITE_EXTERNAL_PERMISSION =
            ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (READ_EXTERNAL_PERMISSION != PackageManager.PERMISSION_GRANTED ||
            WRITE_EXTERNAL_PERMISSION != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                0
            )
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            0 -> {
                if (grantResults.isNotEmpty() && permissions[0] == Manifest.permission.READ_EXTERNAL_STORAGE
                    && permissions[1] == Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) {
                    if (grantResults[0] == PackageManager.PERMISSION_DENIED ||
                        grantResults[1] == PackageManager.PERMISSION_DENIED
                    ) {
                        Toast.makeText(
                            applicationContext,
                            "Please allow storage permission",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }
}
