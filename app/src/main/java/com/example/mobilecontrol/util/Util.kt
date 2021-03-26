package com.example.mobilecontrol.util

import android.os.Handler
import android.os.Looper
import java.lang.StringBuilder
import java.util.concurrent.Executors

/**
 * @author ShenBen
 * @date 2020/3/27 14:42
 * @email 714081644@qq.com
 */

private val mainThreadHandler = Handler(Looper.getMainLooper())

private val newFixedThreadPool = Executors.newFixedThreadPool(10)

fun executeMainThread(function: () -> Unit) = mainThreadHandler.post { function() }

fun executePostDelayedMainThread(function: () -> Unit, delayMillis: Long) =
    mainThreadHandler.postDelayed({ function() }, delayMillis)

fun executeThread(function: () -> Unit) = newFixedThreadPool.execute { function() }

fun removeCallbacksAndMessages() {
    mainThreadHandler.removeCallbacksAndMessages(null)
}

/**
 * 时间转为HH:mm:ss格式
 * @param time 单位秒
 */
fun getTimeString(time: Int): String {
    val hour = time / 60 / 60
    val minute = time / 60 % 60
    val second = time % 60
    val builder = StringBuilder()
    if (hour < 10) {
        builder.append(0)
    }
    builder.append(hour).append(":")
    if (minute < 10) {
        builder.append(0)
    }
    builder.append(minute).append(":")
    if (second < 10) {
        builder.append(0)
    }
    builder.append(second)
    return builder.toString()
}



