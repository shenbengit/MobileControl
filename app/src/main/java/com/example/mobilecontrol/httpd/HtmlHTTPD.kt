package com.example.mobilecontrol.httpd

import android.content.Context
import com.example.mobilecontrol.constant.Constant
import fi.iki.elonen.NanoHTTPD

/**
 *
 * @author ShenBen
 * @date 2019/12/16 18:12
 * @email 714081644@qq.com
 */
class HtmlHTTPD(private val context: Context) : NanoHTTPD(Constant.HTML_PORT) {

    override fun serve(session: IHTTPSession): Response {
        return newChunkedResponse(Response.Status.OK, MIME_HTML,context.assets.open("html/index.html"))
    }
}