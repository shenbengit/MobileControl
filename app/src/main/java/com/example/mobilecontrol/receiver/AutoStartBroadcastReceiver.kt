package com.example.mobilecontrol.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.mobilecontrol.service.MobileControlService

/**
 * @author ShenBen
 * @date 2020/06/15 9:28
 * @email 714081644@qq.com
 */
class AutoStartBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            context?.let { ctx ->
                ctx.startService(Intent(ctx, MobileControlService::class.java))
            }
        }
    }

}
