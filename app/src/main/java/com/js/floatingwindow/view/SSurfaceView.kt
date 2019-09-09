package com.js.floatingwindow.view

import android.content.Context
import android.media.MediaPlayer
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import com.js.floatingwindow.R

/**
 * Author: double.
 * CreateTime: 19-9-6 上午11:26.
 * Description:
 */
class SSurfaceView : FrameLayout, SurfaceHolder.Callback {

    private lateinit var mContext: Context
    //关闭按钮
    private lateinit var mIvClose: ImageView
    //缩放view 点击用
    private lateinit var mFlZoom: FrameLayout
    //缩放按钮
    private lateinit var mIvZoom: ImageView

    private lateinit var mVideoView: SurfaceView
    private lateinit var mPlayer: MediaPlayer


    private lateinit var mBgView: View

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        mContext = context
        initView()
    }

    private fun initView() {
        val parent = LayoutInflater.from(mContext).inflate(R.layout.view_video_2, this)

        mIvClose = parent.findViewById(R.id.iv_close)
        mIvZoom = parent.findViewById(R.id.iv_zoom)
        mFlZoom = parent.findViewById(R.id.flt_zoom)
        mVideoView = parent.findViewById(R.id.vv_video)
        mBgView = parent.findViewById(R.id.bg_view)

        mVideoView.setZOrderOnTop(false)
        mVideoView.holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
        mVideoView.holder.addCallback(this)
        mPlayer = MediaPlayer()

        viewTreeObserver.addOnGlobalLayoutListener {
            changeVideoSize()
        }

    }

    fun playVideo(path: String) {

        mPlayer.isLooping = true
        try {
            mPlayer.setDataSource(path.toString())
        } catch (e: Exception) {

        }
        mPlayer.prepareAsync()
        mPlayer.setOnPreparedListener {
            mPlayer.start()
        }
        mPlayer.setOnVideoSizeChangedListener { mp, width, height ->
            Log.e("js", "setOnVideoSizeChangedListener$width $height")
            changeVideoSize()
        }
    }

    fun stop() {
        if (mPlayer.isPlaying) {
            mPlayer.stop()
            mPlayer.release()
        }
    }

    fun changeVideoSize() {
        try {
            var width: Int = mPlayer.videoWidth
            var height: Int = mPlayer.videoHeight

            val surfaceWidth = getWidth()
            val surfaceHeight = getHeight()

            val max: Float = Math.max(width / surfaceWidth.toFloat(), height / surfaceHeight.toFloat())

            width = Math.ceil((width / max).toDouble()).toInt()
            height = Math.ceil((height / max).toDouble()).toInt()

            val params = FrameLayout.LayoutParams(width, height)
            params.gravity = Gravity.CENTER
            mVideoView.layoutParams = params
            mVideoView.invalidate()
        } catch (e: IllegalStateException) {
            //mPlayer可能出现异常
        }

    }

    /**
     * 设置关闭事件
     * @param click OnClickListener
     */
    fun setCloseListener(click: OnClickListener) {
        mIvClose?.setOnClickListener(click)
    }

    /**
     * 点击缩放事件
     * @param click OnClickListener
     */
    fun setZoomListener(click: OnClickListener) {
        mFlZoom.setOnClickListener(click)
    }

    /**
     * 设置缩放图片显示
     * @param isBig Boolean view是否是大的状态，是true
     */
    fun setZoomStatusBig(isBig: Boolean) {
        mIvZoom.run {
            if (isBig) {
                setImageResource(R.drawable.ic_zoom_small)
            } else {
                setImageResource(R.drawable.ic_zoom_big)
            }
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {

    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {

    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        mPlayer.setDisplay(holder)
    }
}