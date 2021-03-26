package com.example.mobilecontrol.constant

import android.os.Environment
import java.io.File

/**
 * @author ShenBen
 * @date 2019/11/11 15:06
 * @email 714081644@qq.com
 */
internal object Constant {

    const val TAG = "MobileControl"

    /**
     * 基本文件保存路径
     */
    private val BASE_FILE_PATH =
        Environment.getExternalStorageDirectory().path + File.separator + TAG + File.separator
    /**
     * 下载文件保存目录
     */
    val DOWNLOAD_FILE_PATH = BASE_FILE_PATH + "download" + File.separator

    const val HTML_PORT: Int = 9099

    const val WEB_SOCKET_PORT: Int = 9098
    const val FILE_SOCKET_PORT: Int = 9097

    /**
     * ip地址正则
     */
    const val IP_REGEX =
        "(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)"

    const val NUMBER_REGEX = "[1-9]\\d*"
}