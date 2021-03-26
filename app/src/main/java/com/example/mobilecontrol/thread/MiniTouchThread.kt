package com.example.mobilecontrol.thread

import android.net.LocalSocket
import android.net.LocalSocketAddress
import android.util.Log
import android.widget.Toast
import com.example.mobilecontrol.bean.PositionBean
import com.example.mobilecontrol.bean.TouchBean
import com.example.mobilecontrol.callback.MiniTouchCallback
import com.example.mobilecontrol.util.executeMainThread
import org.jetbrains.annotations.NotNull
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException

/**
 * @author ShenBen
 * @date 2020/3/3 8:56
 * @email 714081644@qq.com
 */
class MiniTouchThread : Thread("MiniTouchThread") {
    /**
     * 最大触摸X
     */
    private var maxX = 0
    /**
     * 最大触摸Y
     */
    private var maxY = 0
    /**
     * 最大压力
     */
    private var maxPressure = 0
    /**
     * 触摸旋转角度
     * 0°(默认)
     * 90°
     * 180°
     * 270°
     */
    private var angle = 0

    private var outputStream: DataOutputStream? = null
    private lateinit var socket: LocalSocket

    private var mMiniTouchCallback: MiniTouchCallback? = null

    fun setMiniTouchCallback(callback: MiniTouchCallback?) {
        mMiniTouchCallback = callback
    }

    override fun run() {
        super.run()
        println("MiniTouchThread启动")
        try {
            socket = LocalSocket()
            socket.connect(LocalSocketAddress("minitouch"))
            outputStream = DataOutputStream(socket.outputStream)
            val dataInputStream = DataInputStream(socket.inputStream)
            var count: Int
            do {
                count = dataInputStream.available()
            } while (count == 0)
            val bytes = ByteArray(count)
            dataInputStream.read(bytes)
            val string = String(bytes)
            val array =
                string.split("[ \n]".toRegex()).dropLastWhile { it.isEmpty() }
            if (array.size == 9) {
                val bean = TouchBean()
                bean.version = array[1].takeIf { it.matches(Regex("[1-9]\\d*")) }?.toInt() ?: 0
                bean.maxPoint = array[3].takeIf { it.matches(Regex("[1-9]\\d*")) }?.toInt() ?: 0
                bean.maxX = array[4].takeIf { it.matches(Regex("[1-9]\\d*")) }?.toInt() ?: 0
                bean.maxY = array[5].takeIf { it.matches(Regex("[1-9]\\d*")) }?.toInt() ?: 0
                bean.maxPressure = array[6].takeIf { it.matches(Regex("[1-9]\\d*")) }?.toInt() ?: 0
                bean.pid = array[8].takeIf { it.matches(Regex("[1-9]\\d*")) }?.toInt() ?: 0
                maxX = bean.maxX
                maxY = bean.maxY
                maxPressure = bean.maxPressure
                mMiniTouchCallback?.onTouch(bean)
                println("Minitouch首次:$bean")
            }
        } catch (e: IOException) {
            Log.e("MiniTouchThread", "Socket Error: " + e.message)
        }
    }

    /**
     * 设置触摸旋转角度
     * 0°
     * 90°
     * 180°
     * 270°
     */
    fun setAngle(angle: Int) {
        this.angle = angle
    }

    /**
     * 按下
     */
    fun down(percentageX: Double, percentageY: Double) {
        if (maxX == 0 || maxY == 0) {
            Log.w("MiniTouchThread", "down->未接收到minitouch发送配置数据")
            return
        }
        try {
            outputStream?.let {
                val positionBean = PositionBean(percentageX, percentageY, maxX, maxY, angle)
                it.writeBytes("d 0 ${positionBean.x} ${positionBean.y} $maxPressure\n")
                it.flush()
                it.writeBytes("c\n")
                it.flush()
            }
        } catch (exception: Exception) {

        }
    }

    /**
     * 移动
     */
    fun move(percentageX: Double, percentageY: Double) {
        if (maxX == 0 || maxY == 0) {
            Log.w("MiniTouchThread", "move->未接收到minitouch发送配置数据")
            return
        }
        outputStream?.let {
            try {
                val positionBean = PositionBean(percentageX, percentageY, maxX, maxY, angle)
                it.writeBytes("m 0 ${positionBean.x} ${positionBean.y} $maxPressure\n")
                it.flush()
                it.writeBytes("c\n")
                it.flush()
            } catch (exception: Exception) {

            }
        }
    }

    /**
     * 抬起
     */
    fun up() {
        outputStream?.let {
            try {
                it.writeBytes("u 0\n")
                it.flush()
                it.writeBytes("c\n")
                it.flush()
            } catch (exception: Exception) {

            }
        }
    }

    fun close() {
        try {
            outputStream?.close()
            outputStream = null
            socket.close()
        } catch (exception: Exception) {

        }
    }
}