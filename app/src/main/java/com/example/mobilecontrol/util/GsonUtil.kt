package com.example.mobilecontrol.util

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser

import java.util.ArrayList

/**
 * @author ShenBen
 * @date 2019/8/21 14:13
 * @email 714081644@qq.com
 */
internal object GsonUtil {

    private var sGson: Gson? = null

    init {
        sGson = GsonBuilder().serializeNulls().setLenient().create()
    }

    fun toJson(any: Any): String {
        return sGson!!.toJson(any)
    }

    fun <T> jsonToBean(json: String, tClass: Class<T>): T {
        return sGson!!.fromJson(json, tClass)
    }

    /**
     * json字符串转成list
     *
     * @param json
     * @param cls
     * @return
     */
    fun <T> jsonToList(json: String, cls: Class<T>): List<T> {
        val list = ArrayList<T>()
        val array = JsonParser().parse(json).asJsonArray
        for (elem in array) {
            list.add(sGson!!.fromJson(elem, cls))
        }
        return list
    }

}
