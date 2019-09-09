package com.js.floatingwindow.view

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.VideoView
import com.js.floatingwindow.R

/**
 * Author: double.
 * CreateTime: 19-9-6 上午11:26.
 * Description:
 */
class SVideoView : FrameLayout {
    private lateinit var mContext: Context
    //关闭按钮
    private lateinit var mIvClose: ImageView
    //缩放view 点击用
    private lateinit var mFlZoom: FrameLayout
    //缩放按钮
    private lateinit var mIvZoom: ImageView

    private lateinit var mVideoView: VideoView
    private lateinit var mBgView: View

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        mContext = context
        initView()
    }

    private fun initView() {
        val parent = LayoutInflater.from(mContext).inflate(R.layout.view_video, this)

        mIvClose = parent.findViewById(R.id.iv_close)
        mIvZoom = parent.findViewById(R.id.iv_zoom)
        mFlZoom = parent.findViewById(R.id.flt_zoom)
        mVideoView = parent.findViewById(R.id.vv_video)
        mBgView= parent.findViewById(R.id.bg_view)

        viewTreeObserver.addOnGlobalLayoutListener {
            mVideoView.run {
                holder.setFixedSize(width,height)
                requestLayout()
            }
        }
    }

    fun playVideo(path: Uri) {
        mVideoView.setVideoURI(path)
        mVideoView.start()
        mVideoView.requestFocus()
        mVideoView.setOnCompletionListener {
            mVideoView.start()
        }
    }

    fun stop(){
        mVideoView.stopPlayback()
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
}