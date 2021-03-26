package com.example.mobilecontrol.thread

import com.example.mobilecontrol.bean.CommandBean
import com.example.mobilecontrol.manager.MobileControlManager
import com.example.mobilecontrol.util.GsonUtil
import java.util.*

/**
 * 分发消息线程
 *
 * @author ShenBen
 * @date 2020/4/28 11:27
 * @email 714081644@qq.com
 */
class DistributeMessageThread constructor(private val msgQueue: Queue<String>) :
    Thread("DistributeMessageThread") {

    override fun run() {
        while (true) {
            if (isInterrupted) {
                break
            }
            val poll = msgQueue.poll()
            poll?.let {
                val commandBean = GsonUtil.jsonToBean(it, CommandBean::class.java)
                when (commandBean.command) {
                    "remote_control" -> {
                        MobileControlManager.getInstance().execAction(commandBean)
                    }
                    "set_angle" -> {
                        MobileControlManager.getInstance().setAngle(commandBean.angle)
                    }
                    "refresh" -> {
                        MobileControlManager.getInstance().startScreenshot()
                    }
                }
            }
        }
    }
}