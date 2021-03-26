package com.example.mobilecontrol

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mobilecontrol.service.MobileControlService

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //获取屏幕真实分辨率
        startService(Intent(this, MobileControlService::class.java))
    }
}
