package com.js.floatingwindow

import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.v4.content.FileProvider
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import com.js.floatingwindow.view.SVideoView
import java.io.File

/**
 * Author: double.
 * CreateTime: 19-9-6 上午11:07.
 * Description:创建悬浮窗口，和数据输入的服务
 */
class VideoService : Service() {
    companion object {
        const val WINDOW_SMALL = 0
        const val WINDOW_NORMAL = 1
        const val WINDOW_BIG = 2
    }

    private lateinit var mParams: WindowManager.LayoutParams
    private lateinit var mWindowManager: WindowManager
    private var mWindowStatus: Int = 0
    private var mIsChangeBig = false
    private var mIsOpen = false

    private var mWidth: Int = 0
    private var mHeight: Int = 0

    private var mSmallWidth = 0
    private var mSmallHeight = 0

    private var mNormalWidth: Int = 0
    private var mNormalHeight: Int = 0

    private var mTouchX = 0f
    private var mTouchY = 0f

    //主VideoView播放
    private lateinit var mVideoView: SVideoView
    //主surfaceView播放
    //private lateinit var mVideoView: SSurfaceView

    override fun onBind(intent: Intent?): IBinder? {
        return MyBinder()
    }

    override fun onCreate() {
        super.onCreate()
        createWindow()
    }

    /**
     * 初始化windows数据
     */
    private fun createWindow() {
        mParams = WindowManager.LayoutParams()
        mWindowManager = application.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        mParams.type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
        }
        val dm = DisplayMetrics()
        mWindowManager.defaultDisplay.getMetrics(dm)
        mWidth = dm.widthPixels
        mHeight = dm.heightPixels

        mSmallWidth = mWidth / 4
        mSmallHeight = mHeight / 4

        mNormalWidth = mWidth / 2
        mNormalHeight = mHeight / 2

        mParams.format = PixelFormat.RGBA_8888
        //设置flags.不可聚焦及不可使用按钮对悬浮窗进行操控.
        mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_FULLSCREEN or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN

        //设置窗口初始停靠位置.
        mParams.gravity = Gravity.LEFT or Gravity.TOP
        mParams.x = 0
        mParams.y = 100
    }

    /**
     * 创建悬浮窗口View
     */
    fun addView(context: Activity) {
        mVideoView = SVideoView(context)
        //mVideoView = SSurfaceView(context)
        mVideoView.setCloseListener(View.OnClickListener {
            removeView()
        })

        mVideoView.setZoomListener(View.OnClickListener {
            when (mWindowStatus) {
                WINDOW_SMALL -> {
                    mWindowStatus = WINDOW_NORMAL
                    mParams.run {
                        width = mNormalWidth
                        height = mNormalHeight
                        y = mNormalHeight
                        mIsChangeBig = true
                        mVideoView.setZoomStatusBig(true)
                    }
                }
                WINDOW_NORMAL -> {
                    mWindowStatus = if (mIsChangeBig) {
                        mParams.run {
                            width = mWidth
                            height = mHeight
                            y = 0
                            mVideoView.setZoomStatusBig(true)
                        }
                        WINDOW_BIG
                    } else {
                        mParams.run {
                            width = mSmallWidth
                            height = mSmallHeight
                            y = mHeight
                            mVideoView.setZoomStatusBig(false)
                        }
                        WINDOW_SMALL
                    }

                }
                WINDOW_BIG -> {
                    mWindowStatus = WINDOW_NORMAL
                    mParams.run {
                        width = mNormalWidth
                        height = mNormalHeight
                        mIsChangeBig = false
                        y = mNormalHeight
                        mVideoView.setZoomStatusBig(false)
                    }
                }

            }
            mWindowManager.updateViewLayout(mVideoView, mParams)
        })

        mVideoView.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    mTouchX = event.x
                    mTouchY = event.y
                }
                MotionEvent.ACTION_MOVE -> {
                    if (mWindowStatus != 2) {
                        mParams.x = (event.rawX - mTouchX).toInt()
                        mParams.y = (event.rawY - mTouchY).toInt()
                        mWindowManager.updateViewLayout(mVideoView, mParams)
                    }
                }
            }
            false
        }

        mParams.width = mSmallWidth
        mParams.height = mSmallHeight
        mParams.y = mHeight
        mIsOpen = true
        mWindowManager.addView(mVideoView, mParams)
    }

    fun addData(path: String) {
        val uri = FileProvider.getUriForFile(this, "com.js.floatingwindow.fileprovider", File(path))
        mVideoView?.playVideo(uri)
//        mVideoView?.playVideo(path)
    }

    private fun removeView() {
        mIsOpen = false
        mVideoView.stop()
        mWindowManager.removeView(mVideoView)

    }

    inner class MyBinder : Binder() {
        fun getBinder(): VideoService {
            return this@VideoService
        }
    }

    public fun isOpen(): Boolean {
        return mIsOpen
    }
}