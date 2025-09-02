package com.baghdadhomes.Adapters

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.VideoView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.baghdadhomes.Activities.BaseActivity
import com.baghdadhomes.Models.ReelsData
import com.baghdadhomes.PreferencesService
import com.baghdadhomes.R
import com.baghdadhomes.Utils.Utility
import pl.droidsonroids.gif.GifImageView

class AdapterVideoView (var context : Context, var list : ArrayList<ReelsData>, var onClick : VideoViewAction)
    :RecyclerView.Adapter<AdapterVideoView.ViewHolder>(){

    class ViewHolder(var itemView : View): RecyclerView.ViewHolder(itemView){
        var simpleProgressBar : ProgressBar = itemView.findViewById(R.id.simpleProgressBar)
        var videoView : VideoView = itemView.findViewById(R.id.videoView)
        var imgBack : ImageView = itemView.findViewById(R.id.imgBack)
        var imgCall : ImageView = itemView.findViewById(R.id.imgCall)
        var imgMsg : ImageView = itemView.findViewById(R.id.imgMsg)
        var imgFav : ImageView = itemView.findViewById(R.id.imgFav)
        var imgShare : ImageView = itemView.findViewById(R.id.imgShare)
        var tvViewCount : TextView = itemView.findViewById(R.id.tvViewCount)
        var tvTitle : TextView = itemView.findViewById(R.id.tvTitle)
        var progressView : GifImageView = itemView.findViewById(R.id.progressView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_video_view,parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val updateHandler = Handler(Looper.getMainLooper())

        holder.progressView.visibility = View.VISIBLE
        //holder.videoView.setMediaController(MediaController(context))
        holder.videoView.setVideoURI(Uri.parse(list.get(position).video))

        val updateVideoTime: Runnable = object : Runnable {
            override fun run() {
                val currentPosition: Long = holder.videoView.currentPosition.toLong()
                holder.simpleProgressBar.progress = currentPosition.toInt()
                updateHandler.postDelayed(this, 100)
            }
        }

        holder.tvViewCount.text = Utility.formatViewCounts(list[position].reel_view!!.toInt() + 1)
        holder.videoView.setOnPreparedListener {
            holder.progressView.visibility = View.GONE
            holder.videoView.start()
            list.get(position).reel_view = (list[position].reel_view!!.toInt() + 1).toString()
            holder.tvViewCount.text = Utility.formatViewCounts(list[position].reel_view!!.toInt())
            onClick.onVideoReady()
            holder.simpleProgressBar.progress = 0
            holder.simpleProgressBar.max = holder.videoView.duration
            updateHandler.postDelayed(updateVideoTime, 100)
        }

        holder.videoView.setOnCompletionListener {
            onClick.onVideoCompleted(position)
        }
        holder.tvTitle.text = list[position].title.toString()

        if (!list.get(position).call_number.isNullOrEmpty()){
            holder.imgCall.visibility = View.VISIBLE
        } else{
            holder.imgCall.visibility = View.GONE
        }

        if (list.get(position).is_fav == true){
            holder.imgFav.contentDescription = "1"
            holder.imgFav.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_heart))
        } else{
            holder.imgFav.contentDescription = "0"
            holder.imgFav.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_heart_outline))
        }

        if (!list.get(position).link.isNullOrEmpty()){
            holder.imgShare.visibility = View.VISIBLE
        } else{
            holder.imgShare.visibility = View.GONE
        }

        /*try {
            holder.progressView.visibility = View.VISIBLE

            val exoPlayer: SimpleExoPlayer
            // BandwidthMeter is used for getting default bandwidth
            val bandwidthMeter: BandwidthMeter = DefaultBandwidthMeter()

            // track selector is used to navigate between video using a default seekbar.
            val trackSelector: TrackSelector = DefaultTrackSelector(AdaptiveTrackSelection.Factory(bandwidthMeter))

            // we are adding our track selector to exoplayer.
            exoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector)


            // we are parsing a video url and parsing its video uri.
            val videoUri = Uri.parse(list.get(position).video)

            // we are creating a variable for datasource factory and setting its user agent as 'exoplayer_view'
            val dataSourceFactory = DefaultHttpDataSourceFactory("exoplayer_video")

            // we are creating a variable for extractor factory and setting it to default extractor factory.
            val extractorsFactory: ExtractorsFactory = DefaultExtractorsFactory()

            // we are creating a media source with above variables and passing our event handler as null,
            val mediaSource: MediaSource = ExtractorMediaSource(videoUri, dataSourceFactory, extractorsFactory, null, null)

            exoPlayer!!.addListener(object : ExoPlayer.EventListener{
                override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                    when (playbackState) {
                        ExoPlayer.STATE_BUFFERING -> {
                            holder.progressView.visibility = View.VISIBLE
                            Log.d("VideoPlayerBuffering","State_buffering_$position")
                        }
                        ExoPlayer.STATE_ENDED -> {
                            Log.d("VideoPlayerCompleted","State_completed_$position")
                            exoPlayer!!.seekTo(0)
                            exoPlayer!!.playWhenReady = true

                            if (holder.adapterPosition < list.size -1){
                                onClick.onVideoCompleted(position)
                            } else{
                                //onClick.onCancelClick(position)
                            }
                        }
                        ExoPlayer.STATE_IDLE -> {
                            Log.d("VideoPlayerIdle","State_idle_$position")
                        }
                        ExoPlayer.STATE_READY -> {
                            holder.progressView.visibility = View.GONE
                            Log.d("VideoPlayerReady","State_ready_$position")
                            exoPlayer!!.seekTo(0)
                            exoPlayer!!.playWhenReady = true

                            onClick.onVideoReady(position)
                        }
                        else -> {
                          //  holder.progressView.visibility = View.GONE
                        }
                    }
                }

                override fun onTimelineChanged(timeline: Timeline?, manifest: Any?) { }

                override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {
                    exoPlayer!!.seekTo(0)
                }


                override fun onLoadingChanged(isLoading: Boolean) {

                }

                override fun onPlayerError(error: ExoPlaybackException?) {
                    holder.progressView.visibility = View.GONE
                    exoPlayer!!.prepare(mediaSource)
                    exoPlayer!!.playWhenReady = true
                }

                override fun onPositionDiscontinuity() {

                }

                override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {

                }
            })

            // we are preparing our exoplayer with media source.
            exoPlayer!!.prepare(mediaSource)

            // we are setting our exoplayer when it is ready.
           // exoPlayer.seekTo(0)

            exoPlayer!!.playWhenReady = true
            // inside our exoplayer view we are setting our player
            holder.videoView.player = exoPlayer
            holder.videoView.hideController()
            holder.videoView.useController = false
            holder.videoView.controllerHideOnTouch = true
        } catch (e: Exception) {
            // below line is used for handling our errors.
            Log.e("PlayerErrorTAG", "Error : $e")
        }*/

        holder.imgBack.setOnClickListener {
            onClick.onCancelClick()
        }

        holder.imgCall.setOnClickListener {
            onClick.onCallClick(position)
        }

        holder.imgMsg.setOnClickListener {
            onClick.onMsgClick(position)
        }

        holder.imgFav.setOnClickListener {
            //onClick.onFavClick(position)
            if(PreferencesService.instance.userLoginStatus == true){
                if(((context) as BaseActivity).isNetworkAvailable()){
                    if (holder.imgFav.contentDescription == "0"){
                        holder.imgFav.contentDescription = "1"
                        holder.imgFav.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_heart))
                    } else{
                        holder.imgFav.contentDescription = "0"
                        holder.imgFav.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_heart_outline))
                    }
                    onClick.onFavClick(position)
                } else{
                    ((context) as BaseActivity).showToast(context,context.getString(R.string.intenet_error))
                }
            } else {
                //showToast(this,getString(R.string.reel_fav_login_msg))
                //context.startActivity(Intent(context, LoginActivity::class.java))
                ((context) as BaseActivity).loginTypeDialog(false)
            }
        }

        holder.imgShare.setOnClickListener {
            onClick.onShareClick(position)
        }

        holder.tvTitle.setOnClickListener {
            onClick.onTitleClick(position)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    interface VideoViewAction{
        fun onCallClick(position: Int)
        fun onMsgClick(position: Int)
        fun onFavClick(position: Int)
        fun onShareClick(position: Int)
        fun onTitleClick(position: Int)
        fun onCancelClick()
        fun onVideoCompleted(position: Int)
        fun onVideoReady()
    }
}