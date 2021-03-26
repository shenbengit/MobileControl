package com.example.mobilecontrol.bean

/**
 * @author ShenBen
 * @date 2019/10/8 9:09
 * @email 714081644@qq.com
 */
class CapBean {
    var version: Int = 0
    var length: Int = 0
    var pid: Int = 0
    var readWidth: Int = 0
    var readHeight: Int = 0
    var virtualWidth: Int = 0
    var virtualHeight: Int = 0
    var orientation: Int = 0
    var quirks: Int = 0

    override fun toString(): String {
        return "CapBean(version=$version, length=$length, pid=$pid, readWidth=$readWidth, readHeight=$readHeight, virtualWidth=$virtualWidth, virtualHeight=$virtualHeight, orientation=$orientation, quirks=$quirks)"
    }
}
