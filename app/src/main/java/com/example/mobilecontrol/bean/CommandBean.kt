package com.example.mobilecontrol.bean


import com.google.gson.annotations.SerializedName

data class CommandBean(
    @SerializedName("command")
    val command: String,
    @SerializedName("action")
    val action: String,
    @SerializedName("percentageX")
    val percentageX: Double,
    @SerializedName("percentageY")
    val percentageY: Double,
    @SerializedName("angle")
    val angle: Int
)