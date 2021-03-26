package com.example.mobilecontrol.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.mobilecontrol.R
import com.example.mobilecontrol.httpd.HtmlHTTPD
import com.example.mobilecontrol.manager.MobileControlManager
import com.example.mobilecontrol.socket.FileSocketServer
import com.example.mobilecontrol.socket.FrameSocketServer

/**
 * @author ShenBen
 * @date 2020/4/29 14:22
 * @email 714081644@qq.com
 */
class MobileControlService : Service() {
    private lateinit var mHtmlHTTPD: HtmlHTTPD
    private lateinit var mFrameSocketServer: FrameSocketServer
    private lateinit var mFileSocketServer: FileSocketServer
    override fun onCreate() {
        super.onCreate()
        MobileControlManager.getInstance().init(this)

        mHtmlHTTPD = HtmlHTTPD(this).apply {
            start()
        }
        mFrameSocketServer = FrameSocketServer().apply { start() }
        mFileSocketServer = FileSocketServer().apply { start() }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = NotificationCompat
            .Builder(this, "1")
            .setContentTitle(getString(R.string.app_name))
            .setSmallIcon(R.mipmap.ic_launcher)
            .build()
        //设置为前台服务
        startForeground(101, notification)
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        stopForeground(true)
        mHtmlHTTPD.stop()
        mFrameSocketServer.stop()
        mFileSocketServer.stop()
    }

}