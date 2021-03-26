package com.example.mobilecontrol.bean

/**
 * @author ShenBen
 * @date 2019/10/8 9:09
 * @email 714081644@qq.com
 */
class TouchBean {
    var version = 0
    var maxPoint = 0
    var maxX = 0
    var maxY = 0
    var maxPressure = 0
    var pid = 0

    override fun toString(): String {
        return "TouchBean(version=$version, maxPoint=$maxPoint, maxX=$maxX, maxY=$maxY, maxPressure=$maxPressure, pid=$pid)"
    }

}
