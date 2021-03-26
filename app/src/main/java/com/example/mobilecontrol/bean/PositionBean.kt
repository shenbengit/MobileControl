package com.example.mobilecontrol.bean

/**
 * 位置转换bean
 *
 * @author ShenBen
 * @date 2020/5/8 14:44
 * @email 714081644@qq.com
 */
class PositionBean constructor(
    percentageX: Double,
    percentageY: Double,
    maxX: Int,
    maxY: Int,
    angle: Int
) {
    var x: Int = 0
    var y: Int = 0

    init {
        //实际的x百分比
        var actualPercentageX = percentageX
        //实际的y百分比
        var actualPercentageY = percentageY
        when (angle) {
            0 -> {
                actualPercentageX = percentageX
                actualPercentageY = percentageY
            }
            90 -> {
                actualPercentageX = 1 - percentageY
                actualPercentageY = percentageX
            }
            180 -> {
                actualPercentageX = 1 - percentageX
                actualPercentageY = 1 - percentageY
            }
            270 -> {
                actualPercentageX = percentageY
                actualPercentageY = 1 - percentageX
            }
        }
        x = when {
            actualPercentageX > 1 -> maxX
            actualPercentageX < 0 -> 0
            else -> (maxX * actualPercentageX).toInt()
        }
        y = when {
            actualPercentageY > 1 -> maxY
            actualPercentageY < 0 -> 0
            else -> (maxY * actualPercentageY).toInt()
        }
    }
}