package com.jddeep.videocompressor

import android.net.Uri
import android.os.Bundle
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import com.jddeep.videocompressor.utils.Utils
import kotlinx.android.synthetic.main.activity_compressed_video.*

class CompressedVideoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compressed_video)

        val bundle = intent.extras
        val vidPath: String = bundle?.get("vidPath").toString()

        compressedVideoView.setVideoPath(
            Utils()
                .getPath(Uri.parse(vidPath), applicationContext)
        )
        val mediaController = MediaController(this)
        compressedVideoView.setMediaController(mediaController)
        compressedVideoView.start()
    }
}
