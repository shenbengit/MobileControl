package com.example.mobilecontrol.thread

import android.util.Log
import com.example.mobilecontrol.bean.CapBean
import com.example.mobilecontrol.callback.MiniCapCallback
import com.example.mobilecontrol.callback.ScreenshotCallback
import java.util.*

/**
 * @author ShenBen
 * @date 2020/4/27 18:56
 * @email 714081644@qq.com
 */
class ImageConverterRunner constructor(private val byteQueue: Queue<ByteArray>) : Runnable {
    private var readFrameBytes = 0
    private var frameBodyLength = 0
    private var frameBody = ByteArray(0)
    private var mScreenshotCallback: ScreenshotCallback? = null
    private var mMiniCapCallback: MiniCapCallback? = null

    fun setScreenshotCallback(callback: ScreenshotCallback?) {
        mScreenshotCallback = callback
    }

    fun setMiniCapCallback(callback: MiniCapCallback?) {
        mMiniCapCallback = callback
    }

    override fun run() {
        while (true) {
            if (Thread.currentThread().isInterrupted) {
                break
            }
            if (byteQueue.isEmpty()) {
                continue
            }
            val poll = byteQueue.poll()
            poll?.let { it ->
                //第一次会发送一次配置信息，只会发送一次，长度为24
                if (it.size == 24) {
                    val capBean = CapBean()
                    it.forEachIndexed { index, byte ->
                        val i = byte.toInt() and 0xff
                        when (index) {
                            //版本号
                            0 -> {
                                capBean.version = i
                            }
                            //长度
                            1 -> {
                                capBean.length = i
                            }
                            //低位优先，进程pid
                            2, 3, 4, 5 -> {
                                capBean.pid += i shl (index - 2) * 8
                            }
                            //低位优先，实际显示宽度（以像素为单位）
                            6, 7, 8, 9 -> {
                                capBean.readWidth += i shl (index - 6) * 8
                            }
                            //低位优先，实际显示高度（以像素为单位）
                            10, 11, 12, 13 -> {
                                capBean.readHeight += i shl (index - 10) * 8
                            }
                            //低位优先，虚拟显示宽度（以像素为单位）
                            14, 15, 16, 17 -> {
                                capBean.virtualWidth += i shl (index - 14) * 8
                            }
                            //低位优先，虚拟显示高度（以像素为单位）
                            18, 19, 20, 21 -> {
                                capBean.virtualHeight += i shl (index - 18) * 8
                            }
                            //显示方向
                            22 -> {
                                capBean.orientation = i * 90
                            }
                            //怪异的位标志
                            23 -> {
                                capBean.quirks = i
                            }
                        }
                    }
                    mMiniCapCallback?.onCap(capBean)
                    println("Minicap首次:$capBean")
                } else {
                    //图片数据信息
                    val len = it.size
                    var index = 0
                    while (index < len) {
                        if (readFrameBytes < 4) {
                            val i = it[index].toInt() and 0xff
                            frameBodyLength += i shl (index * 8)
                            ++readFrameBytes
                            ++index
                        } else {
                            if (len - index >= frameBodyLength) {
                                val byteArray = subByteArray(it, index, index + frameBodyLength)
                                frameBody = mergerByteArray(frameBody, byteArray)
                                if (frameBody.isNotEmpty() && (frameBody[0].toInt() != -1 || frameBody[1].toInt() != -40)) {
                                    Log.e("", "Frame body does not start with JPG header")
                                    break
                                }
                                val finalBytes = subByteArray(frameBody, 0, frameBody.size)
                                //发送数据
                                mScreenshotCallback?.callback(finalBytes)
                                restore()
                                break
                            } else {
                                val byteArray = subByteArray(it, index, len)
                                frameBody = mergerByteArray(frameBody, byteArray)
                                frameBodyLength -= (len - index)
                                break
                            }
                        }
                    }
                }
            }
        }
        restore()
    }

    private fun subByteArray(originalArray: ByteArray, start: Int, end: Int): ByteArray {
        if (end - start <= 0) {
            return ByteArray(0)
        }
        val byteArray = ByteArray(end - start)
        System.arraycopy(originalArray, start, byteArray, 0, end - start)
        return byteArray
    }

    /**
     * 合并数组
     */
    private fun mergerByteArray(
        originalArray1: ByteArray,
        originalArray2: ByteArray
    ): ByteArray {
        val byteArray = ByteArray(originalArray1.size + originalArray2.size)
        System.arraycopy(originalArray1, 0, byteArray, 0, originalArray1.size)
        System.arraycopy(originalArray2, 0, byteArray, originalArray1.size, originalArray2.size)
        return byteArray
    }

    /**
     * 重置
     */
    private fun restore() {
        readFrameBytes = 0
        frameBodyLength = 0
        frameBody = ByteArray(0)
    }
}