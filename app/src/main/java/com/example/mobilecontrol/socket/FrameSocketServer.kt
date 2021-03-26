package com.example.mobilecontrol.socket

import android.util.Log
import com.example.mobilecontrol.callback.ScreenshotCallback
import com.example.mobilecontrol.constant.Constant
import com.example.mobilecontrol.manager.MobileControlManager
import com.example.mobilecontrol.thread.DistributeMessageThread
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.lang.Exception
import java.net.InetSocketAddress
import java.util.*
import java.util.concurrent.LinkedBlockingDeque

/**
 * 图片帧发送服务
 *
 * @author ShenBen
 * @date 2020/4/27 10:38
 * @email 714081644@qq.com
 */
class FrameSocketServer : WebSocketServer(InetSocketAddress(Constant.WEB_SOCKET_PORT)) {
    private var msgQueue: Queue<String> = LinkedBlockingDeque()
    private var distributeMessageThread: DistributeMessageThread? = null
    private var mLastFrame: ByteArray? = null
    override fun onStart() {
        Log.i("FrameSocketServer", "onStart")
        distributeMessageThread = DistributeMessageThread(msgQueue).apply {
            start()
        }
        MobileControlManager.getInstance().setScreenshotCallback(object : ScreenshotCallback {
            override fun callback(byteArray: ByteArray) {
                mLastFrame = byteArray
                broadcast(byteArray)
            }
        })
    }

    override fun onOpen(conn: WebSocket?, handshake: ClientHandshake?) {
        Log.i("FrameSocketServer", "onOpen")
        mLastFrame?.let {
            conn?.send(it)
        }
        if (connections.size == 1) {
            //首次开始，说明有一个连接进来
//            MobileControlManager.getInstance().startScreenshot()
        }
    }

    override fun onMessage(conn: WebSocket?, message: String?) {
        Log.i("FrameSocketServer", "onMessage:$message")
        message?.let {
            msgQueue.offer(it)
        }
    }

    override fun onClose(conn: WebSocket?, code: Int, reason: String?, remote: Boolean) {
        Log.w("FrameSocketServer", "onClose")
        if (connections.isEmpty()) {
//            MobileControlManager.getInstance().closeScreenshot()
        }
    }

    override fun onError(conn: WebSocket?, ex: Exception?) {
        Log.e("FrameSocketServer", "onError:${ex?.message}")
    }

}