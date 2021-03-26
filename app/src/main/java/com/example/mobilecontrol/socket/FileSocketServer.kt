package com.example.mobilecontrol.socket

import android.util.Log
import com.example.mobilecontrol.bean.FileBean
import com.example.mobilecontrol.callback.ScreenshotCallback
import com.example.mobilecontrol.constant.Constant
import com.example.mobilecontrol.manager.MobileControlManager
import com.example.mobilecontrol.thread.DistributeMessageThread
import com.example.mobilecontrol.util.GsonUtil
import com.example.mobilecontrol.util.executeThread
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.lang.Exception
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.util.*
import java.util.concurrent.LinkedBlockingDeque

/**
 * 图片帧发送服务
 *
 * @author ShenBen
 * @date 2020/4/27 10:38
 * @email 714081644@qq.com
 */
class FileSocketServer : WebSocketServer(InetSocketAddress(Constant.FILE_SOCKET_PORT)) {
    private val socketFileMap = linkedMapOf<WebSocket, FileBean>()
    override fun onStart() {
        Log.i("FileSocketServer", "onStart")

    }

    override fun onOpen(conn: WebSocket?, handshake: ClientHandshake?) {
        Log.i("FileSocketServer", "onOpen")

    }

    override fun onMessage(conn: WebSocket?, message: String?) {
        Log.i("FileSocketServer", "onMessage-String:$message")
        if (conn != null && message != null) {
            val bean = GsonUtil.jsonToBean(message, FileBean::class.java)
            socketFileMap[conn] = bean
            conn.send("start_upload")
        }
    }

    override fun onMessage(conn: WebSocket?, message: ByteBuffer?) {
        Log.i("FileSocketServer", "onMessage-ByteBuffer:$message")
        executeThread {
            if (conn != null && message != null) {
                val fileBean = socketFileMap[conn]
                fileBean?.let { bean ->
                    val file = File(bean.filePath ?: Constant.DOWNLOAD_FILE_PATH, bean.fileName)
                    file.parentFile?.let { parentFile ->
                        if (parentFile.exists().not()) {
                            parentFile.mkdirs()
                        }
                    }
                    if (file.exists()) {
                        file.delete()
                    }
                    val outputStream = FileOutputStream(file)
                    outputStream.write(message.array())
                    outputStream.flush()
                    outputStream.close()
                }
            }
        }
    }

    override fun onClose(conn: WebSocket?, code: Int, reason: String?, remote: Boolean) {
        Log.w("FileSocketServer", "onClose")

    }

    override fun onError(conn: WebSocket?, ex: Exception?) {
        Log.e("FileSocketServer", "onError:${ex?.message}")
    }

}