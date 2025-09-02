package com.amp.trimmy

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.VideoView
import com.amp.trimmy.view.RangeSeekBarView
import com.amp.trimmy.view.TimeLineView

class VideoTrimmerView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet,
  defStyleAttr: Int = 0
) : BaseVideoTrimmerView(context, attrs, defStyleAttr) {
  private fun stringForTime(timeMs: Int): String {
    val totalSeconds = timeMs / 1000
    val seconds = totalSeconds % 60
    val minutes = totalSeconds / 60 % 60
    val hours = totalSeconds / 3600
    val timeFormatter = java.util.Formatter()
    return if (hours > 0)
      timeFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString()
    else
      timeFormatter.format("%02d:%02d", minutes, seconds).toString()
  }

  override fun initRootView() {
    LayoutInflater.from(context)
        .inflate(R.layout.video_trimmer, this, true)
    //initiateTrimming()
  }

  override fun getTimeLineView(): TimeLineView = findViewById(R.id.timeLineView)//timeLineView

  override fun getTimeInfoContainer(): View = findViewById(R.id.TotalTimeTextView)//TotalTimeTextView

  override fun getTimeInfoContainer2(): View = findViewById(R.id.trimTimeRangeTextView)//trimTimeRangeTextView

  override fun getPlayView(): View = findViewById(R.id.playIndicatorView)//playIndicatorView

  override fun getVideoView(): VideoView = findViewById(R.id.videoView)//videoView

  override fun getVideoViewContainer(): View = findViewById(R.id.videoViewContainer)//videoViewContainer

  override fun getRangeSeekBarView(): RangeSeekBarView = findViewById(R.id.rangeSeekBarView)//rangeSeekBarView

  override fun onRangeUpdated(
    startTimeInMs: Int,
    endTimeInMs: Int
  ) {
    var trimTimeRangeTextView : TextView = getTimeInfoContainer2() as TextView
    var TotalTimeTextView : TextView = getTimeInfoContainer() as TextView
    trimTimeRangeTextView.text =
      "${stringForTime(startTimeInMs)} - ${stringForTime(endTimeInMs)}"
    TotalTimeTextView.text = "${stringForTime(endTimeInMs - startTimeInMs)}"
  }

  override fun onVideoPlaybackReachingTime(timeInMs: Int) {
    // playbackTimeTextView.text = "${stringForTime(timeInMs)}"
  }

  override fun onGotVideoFileSize(videoFileSize: Long) {
    // videoFileSizeTextView.text = Formatter.formatShortFileSize(context, videoFileSize)
  }
}
