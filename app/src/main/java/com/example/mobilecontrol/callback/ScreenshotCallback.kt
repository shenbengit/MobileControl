package com.example.mobilecontrol.callback


/**
 * @author ShenBen
 * @date 2019/12/18 13:32
 * @email 714081644@qq.com
 */
interface ScreenshotCallback {
    /**
     *MiniCap截屏数据回调
     */
    fun callback(byteArray: ByteArray)
}