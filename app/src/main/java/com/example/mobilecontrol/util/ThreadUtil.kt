package com.example.mobilecontrol.util

import android.util.Log
import com.lzh.easythread.Callback
import com.lzh.easythread.EasyThread

/**
 * @author ShenBen
 * @date 2019/8/21 14:29
 * @email 714081644@qq.com
 */
internal object ThreadUtil {
    val io: EasyThread
    val cache: EasyThread
    val calculator: EasyThread
    val file: EasyThread

    init {
        io = EasyThread.Builder
            .createFixed(6)
            .setName("IO")
            .setPriority(7)
            .setCallback(DefaultCallback())
            .build()
        cache = EasyThread.Builder
            .createCacheable()
            .setName("cache")
            .setCallback(DefaultCallback())
            .build()
        calculator = EasyThread.Builder
            .createFixed(4)
            .setName("calculator")
            .setPriority(Thread.MAX_PRIORITY)
            .setCallback(DefaultCallback())
            .build()
        file = EasyThread.Builder
            .createFixed(4)
            .setName("file")
            .setPriority(3)
            .setCallback(DefaultCallback())
            .build()
    }

    private  class DefaultCallback : Callback {

        override fun onError(threadName: String, t: Throwable) {
            Log.e(
                "DefaultCallback",
                "Task with thread $threadName has occurs an error: ${t.message}"
            )
        }

        override fun onCompleted(threadName: String) {

        }

        override fun onStart(threadName: String) {

        }
    }
}
