package com.js.floatingwindow

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import android.widget.Button

class MainActivity : AppCompatActivity() {
    private lateinit var mVideoService: VideoService

    private val mServiceConnectin: ServiceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {

            mVideoService = (service as VideoService.MyBinder).getBinder()
        }

    }

    private fun isBuildVersionGreaterM(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    }

    override fun onStop() {
        super.onStop()
        unbindService(mServiceConnectin)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_main)

        val button = findViewById<Button>(R.id.btn_open)

        button.setOnClickListener {

            if (isBuildVersionGreaterM() && !Settings.canDrawOverlays(this)) {
                startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")))
            } else {
                if (!mVideoService.isOpen()) {
                    mVideoService.addView(this)
                    mVideoService.addData("sdcard/弦内之音.mp4")
                }
            }
        }

        val intent = Intent(this, VideoService::class.java)
        bindService(intent, mServiceConnectin, Context.BIND_AUTO_CREATE)
    }
}
