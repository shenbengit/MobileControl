package com.example.mobilecontrol.callback

import com.example.mobilecontrol.bean.TouchBean

/**
 * @author ShenBen
 * @date 2020/4/29 14:37
 * @email 714081644@qq.com
 */
interface MiniTouchCallback {
    /**
     *MiniTouch相关配置信息回调
     */
    fun onTouch(bean: TouchBean)
}