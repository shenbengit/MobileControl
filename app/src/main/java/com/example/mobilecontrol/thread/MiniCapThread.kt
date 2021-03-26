package com.example.mobilecontrol.thread

import android.net.LocalSocket
import android.net.LocalSocketAddress
import android.util.Log
import com.example.mobilecontrol.callback.MiniCapCallback
import com.example.mobilecontrol.callback.ScreenshotCallback
import java.io.DataInputStream
import java.io.IOException
import java.util.*
import java.util.concurrent.LinkedBlockingDeque

/**
 * 接收Minicap数据Socket
 *
 * @author ShenBen
 * @date 2019/10/30 15:59
 * @email 714081644@qq.com
 */
class MiniCapThread : Thread("MiniCapThread") {

    private val byteQueue: Queue<ByteArray> = LinkedBlockingDeque()

    private lateinit var mImageConverterThread: Thread
    private var mImageConverter: ImageConverterRunner


    init {
        mImageConverter = ImageConverterRunner(byteQueue)
    }

    fun setScreenshotCallback(callback: ScreenshotCallback?) {
        mImageConverter.setScreenshotCallback(callback)
    }

    fun setMiniCapCallback(callback: MiniCapCallback?) {
        mImageConverter.setMiniCapCallback(callback)
    }

    override fun run() {
        super.run()
        println("MiniCapThread启动")
        mImageConverterThread = Thread(mImageConverter).apply { start() }
        var socket: LocalSocket? = null
        var inputStream: DataInputStream? = null
        try {
            socket = LocalSocket()
            val address = LocalSocketAddress(
                "minicap",
                LocalSocketAddress.Namespace.ABSTRACT
            )
            socket.connect(address)
            inputStream = DataInputStream(socket.inputStream)
            var count: Int
            var bytes: ByteArray
            do {
                do {
                    if (isInterrupted) {
                        return
                    }
                    count = inputStream.available()
                } while (count == 0)
                bytes = ByteArray(count)
                inputStream.read(bytes)
                byteQueue.offer(bytes)
            } while (true)
        } catch (exception: IOException) {
            Log.e("MiniCapThread", "minicap-Socket，Error：${exception.message}")
        } finally {
            try {
                socket?.let {
                    if (it.isConnected) {
                        it.close()
                    }
                }
                inputStream?.close()
            } catch (exception: IOException) {

            } finally {
                //清除数据
                byteQueue.clear()
                mImageConverterThread.interrupt()
                Log.w("MiniCapThread", "MiniCapThread -关闭")
            }
        }
    }
}