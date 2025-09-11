package com.baghdadhomes.Activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.abedelazizshe.lightcompressorlibrary.CompressionListener
import com.abedelazizshe.lightcompressorlibrary.VideoCompressor
import com.abedelazizshe.lightcompressorlibrary.VideoQuality
import com.abedelazizshe.lightcompressorlibrary.config.Configuration
import com.abedelazizshe.lightcompressorlibrary.config.SaveLocation
import com.abedelazizshe.lightcompressorlibrary.config.SharedStorageConfiguration
import com.amp.trimmy.VideoTrimmerView
import com.amp.trimmy.interfaces.VideoTrimmingListener
import com.google.gson.JsonObject
import com.baghdadhomes.R
import pl.droidsonroids.gif.GifImageView
import java.io.File

class VideoTrimmerActivity : BaseActivity(), VideoTrimmingListener {
    private lateinit var img_back : ImageView
    private lateinit var imgDone : ImageView
    private lateinit var tv_upload : TextView
    private lateinit var videoTrimmerView : VideoTrimmerView
    private lateinit var progressView : GifImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_trimmer)

        img_back = findViewById(R.id.img_back)
        imgDone = findViewById(R.id.imgDone)
        videoTrimmerView = findViewById(R.id.videoTrimmerView)
        progressView = findViewById(R.id.progressView)
        tv_upload = findViewById(R.id.tv_upload)

        val inputVideoUri: Uri? = intent?.getParcelableExtra("videoUri")
        if (inputVideoUri == null) {
            finish()
            return
        }

        videoTrimmerView.setMaxDurationInMs(30 * 1000)
        videoTrimmerView.setMinDurationInMs(1 * 1000)
        videoTrimmerView.setOnK4LVideoListener(this)
        val parentFolder = File(Environment.getExternalStorageDirectory(), "Movies")
        if (!parentFolder.exists()) {
            val flag = parentFolder.mkdirs()
        }
        val fileName = "NajafTrim.mp4"
        val trimmedVideoFile = File(parentFolder, fileName)
        videoTrimmerView.setDestinationFile(trimmedVideoFile)

        videoTrimmerView.setVideoInformationVisibility(true)
        //videoTrimmerView.setWaterMark(BitmapFactory.decodeResource(resources, R.drawable.text_logo),WaterMarkPosition.RIGHT_BOTTOM,20,20)
        videoTrimmerView.post {
            videoTrimmerView.setVideoURI(inputVideoUri)
        }

        img_back.setOnClickListener {
            finish()
        }

        tv_upload.setOnClickListener {
            videoTrimmerView.initiateTrimming()
        }
    }

    override fun onTrimStarted() {
        runOnUiThread {
            progressView.visibility = View.VISIBLE
        }
    }

    override fun onFinishedTrimming(uri: Uri?) {
        runOnUiThread {
            progressView.visibility = View.GONE
            if (uri == null) {
                showToast(this,getString(R.string.failed_trim))
            } else {
                val intent = Intent()
                intent.putExtra("videoUri", uri.toString())
                setResult(Activity.RESULT_OK,intent)
                finish()
                overridePendingTransition(0,0)
                /*val uris = mutableListOf<Uri>()
                uris.add(uri)
                compressVideo(uris)*/
            }
            //finish()
        }
    }

    override fun onTrimProgressing(progress: Double) {
        runOnUiThread {}
    }

    override fun onErrorWhileViewingVideo(
        what: Int,
        extra: Int
    ) {
        runOnUiThread {
            progressView.visibility = View.GONE
            showToast(this, getString(R.string.error_video_preview))
        }
    }

    override fun onTrimFailed(exception: Exception?) {
        runOnUiThread {
            showToast(this, getString(R.string.failed_trim))
        }
    }

    override fun onVideoPrepared() {
        runOnUiThread {}
    }

    override fun getResponse(apiType: String, respopnse: JsonObject) {
        runOnUiThread { }
    }

}