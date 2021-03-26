package com.example.mobilecontrol.bean

import com.google.gson.annotations.SerializedName

/**
 * @author ShenBen
 * @date 2020/5/9 14:01
 * @email 714081644@qq.com
 */
data class FileBean(
    /**
     * 文件名
     */
    @SerializedName("fileName")
    val fileName: String,
    /**
     * 文件类型
     */
    @SerializedName("fileType")
    val fileType: String,
    /**
     * 单位字节，B
     */
    @SerializedName("fileSize")
    val fileSize: Long,
    /**
     * 保存文件的目录地址
     */
    @SerializedName("filePath")
    val filePath: String?
)