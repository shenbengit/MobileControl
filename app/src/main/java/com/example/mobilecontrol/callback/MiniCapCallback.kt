package com.example.mobilecontrol.callback

import com.example.mobilecontrol.bean.CapBean

/**
 * @author ShenBen
 * @date 2020/4/29 14:37
 * @email 714081644@qq.com
 */
interface MiniCapCallback {
    /**
     *MiniCap相关配置信息回调
     */
    fun onCap(bean: CapBean)
}