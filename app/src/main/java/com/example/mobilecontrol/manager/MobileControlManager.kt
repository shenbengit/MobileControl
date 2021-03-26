package com.example.mobilecontrol.manager

import android.content.Context
import android.content.res.AssetManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import com.example.mobilecontrol.bean.CapBean
import com.example.mobilecontrol.bean.CommandBean
import com.example.mobilecontrol.bean.TouchBean
import com.example.mobilecontrol.callback.MiniCapCallback
import com.example.mobilecontrol.callback.MiniTouchCallback
import com.example.mobilecontrol.callback.ScreenshotCallback
import com.example.mobilecontrol.thread.MiniCapThread
import com.example.mobilecontrol.thread.MiniTouchThread
import com.example.mobilecontrol.util.*
import com.example.mobilecontrol.util.ShellUtil
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream


/**
 * @author ShenBen
 * @date 2020/4/27 10:41
 * @email 714081644@qq.com
 */
class MobileControlManager private constructor() {
    private object SingleHolder {
        val instance = MobileControlManager()
    }

    companion object {
        fun getInstance() = SingleHolder.instance
        /**
         * cpu的arm架构的种类
         */
        private val abis = listOf("arm64-v8a", "armeabi-v7a", "x86", "x86_64")

        /**
         * 获取cpu的arm架构
         */
        private const val ABI_COMMAND = "getprop ro.product.cpu.abi"
        /**
         * 设备分辨率
         */
        private const val MINICAP_WM_SIZE_COMMAND = "wm size"

        private const val REMOTE_PATH = "/data/local/tmp/"
        private const val MINICAP = "minicap"
        private const val MINICAP_SO = "minicap.so"
        private const val MINICAP_NOPIE = "minicap-nopie"
        private const val MINITOUCH = "minitouch"
        private const val MINITOUCH_NOPIE = "minitouch-nopie"
        /**
         * 缩放比
         */
        private const val SCALE = 0.5f
    }

    private var widthPixels: Int = 0
    private var heightPixels: Int = 0
    private var mSdkVersion: Int = Build.VERSION.SDK_INT
    /**
     * 注意：sdk<16时，需要使用(minicap-nopie)替换(minicap)
     * 复制的文件和执行命令
     */
    private var minicapBin: String
    /**
     * 注意：sdk<16时，需要使用(minitouch-nopie)替换(minitouch)
     * 复制的文件和执行命令
     */
    private var minitouchBin: String
    /**
     * 是否已经初始化成功
     */
    private var isInitSuccess = false

    private var mCapBean: CapBean? = null
    private var mTouchBean: TouchBean? = null

    private val mMiniCapCallback = object : MiniCapCallback {
        override fun onCap(bean: CapBean) {
            mCapBean = bean
        }
    }

    private val mMiniTouchCallback = object : MiniTouchCallback {
        override fun onTouch(bean: TouchBean) {
            mTouchBean = bean
        }
    }

    private var mMiniCapThread: MiniCapThread? = null
    private var mMiniTouchThread: MiniTouchThread? = null


    private var mScreenshotCallback: ScreenshotCallback? = null

    init {
        minicapBin = if (mSdkVersion < Build.VERSION_CODES.JELLY_BEAN) {
            MINICAP_NOPIE
        } else {
            MINICAP
        }
        minitouchBin = if (mSdkVersion < Build.VERSION_CODES.JELLY_BEAN) {
            MINITOUCH_NOPIE
        } else {
            MINITOUCH
        }
    }

    fun init(context: Context) {
        executeThread {
            val parentFile = File(REMOTE_PATH)
            if (parentFile.exists().not()) {
                parentFile.mkdirs()
            }

            val minicapFile = File(REMOTE_PATH, minicapBin)
            val minicapSoFile = File(REMOTE_PATH, MINICAP_SO)
            val minitouchFile = File(REMOTE_PATH, minitouchBin)

            if (minicapFile.exists()
                && minicapSoFile.exists()
                && minitouchFile.exists()
            ) {
                //如果文件已经存在
                //获取分辨率
                val screenOutput = ShellUtil.execCommand(
                    MINICAP_WM_SIZE_COMMAND,
                    isRoot = true,
                    isNeedResultMsg = true
                ).successMsg?.trim()
                screenOutput?.let {
                    screenOutput.split(":".toRegex())[1].trim().apply {
                        val split = split("x".toRegex())
                        widthPixels = split[0].toInt()
                        heightPixels = split[1].toInt()
                        isInitSuccess = true
                    }
                }
            } else {
                if (ShellUtil.checkRootPermission()) {
                    val screenOutput = ShellUtil.execCommand(
                        MINICAP_WM_SIZE_COMMAND,
                        isRoot = true,
                        isNeedResultMsg = true
                    ).successMsg?.trim()
                    screenOutput?.let {
                        println("获取屏幕分辨率:"+it)
                        screenOutput.split(":".toRegex())[1].trim().apply {
                            val split = split("x".toRegex())
                            widthPixels = split[0].toInt()
                            heightPixels = split[1].toInt()
                        }
                    }

                    ShellUtil.execCommand(
                        "chmod 777 $REMOTE_PATH",
                        isRoot = true,
                        isNeedResultMsg = false
                    )
                    val cpuAbi = ShellUtil.execCommand(ABI_COMMAND, true).successMsg?.trim()
                    if ((cpuAbi in abis).not()) {
                        //cpu核心架构未找到匹配项
                        executeMainThread {
                            Toast.makeText(context, "cpu核心架构未找到匹配项:$cpuAbi", Toast.LENGTH_SHORT)
                                .show()
                        }
                        return@executeThread
                    }
                    //开始复制文件
                    minicapFile.delete()
                    minicapSoFile.delete()
                    minitouchFile.delete()

                    val assetManager = context.assets
                    copyFile(
                        assetManager.open(
                            "minicap/$cpuAbi/$minicapBin",
                            AssetManager.ACCESS_BUFFER
                        ),
                        FileOutputStream(minicapFile)
                    )
                    copyFile(
                        assetManager.open(
                            "minicap-shared/android-$mSdkVersion/$cpuAbi/minicap.so",
                            AssetManager.ACCESS_BUFFER
                        ),
                        FileOutputStream(minicapSoFile)
                    )
                    copyFile(
                        assetManager.open(
                            "minitouch/$cpuAbi/$minitouchBin",
                            AssetManager.ACCESS_BUFFER
                        ),
                        FileOutputStream(minitouchFile)
                    )
                    ShellUtil.execCommand(
                        "chmod 777 $REMOTE_PATH/mini*",
                        isRoot = true,
                        isNeedResultMsg = false
                    )
                    isInitSuccess = true
                } else {
                    //没有root权限
                    executeMainThread {
                        Toast.makeText(
                            context,
                            "没有Root权限，暂时无法使用此功能！",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            if (isInitSuccess) {
                execCommand()
            }
        }
    }

    /**
     * 执行截屏、控屏命令
     */
    private fun execCommand() {
        executeThread {
            //先判断minicap是否已经启动，避免多次启动卡死的情况
            val execCommand = ShellUtil.execCommand(
                "ps | grep /data/local/tmp/minicap",
                isRoot = true,
                isNeedResultMsg = true
            )
            if (execCommand.successMsg.isNullOrBlank()) {
                val capCommand =
                    "LD_LIBRARY_PATH=/data/local/tmp /data/local/tmp/minicap -P ${widthPixels}x${heightPixels}@${(widthPixels * SCALE).toInt()}x${(heightPixels * SCALE).toInt()}/0"
                ShellUtil.execCommand(capCommand, isRoot = true, isNeedResultMsg = false)
            } else {
                Log.w("MobileControlManager", "minicap已经启动")
            }
        }
        executeThread {
            //先判断minitouch是否已经启动，避免多次启动卡死的情况
            val execCommand = ShellUtil.execCommand(
                "ps | grep /data/local/tmp/minitouch",
                isRoot = true,
                isNeedResultMsg = true
            )
            if (execCommand.successMsg.isNullOrBlank()) {
                val touchCommand = "/data/local/tmp/minitouch"
                ShellUtil.execCommand(touchCommand, isRoot = true, isNeedResultMsg = false)
            } else {
                Log.w("MobileControlManager", "minitouch已经启动")
            }
        }
        //方便起见，直接开启监听socket
        executeThread {
            Thread.sleep(2000)
            startScreenshot()
        }
    }


    /**
     * 开始监听截屏、控屏socket
     */
    fun startScreenshot() {
        closeScreenshot()
        mMiniCapThread = MiniCapThread().apply {
            setScreenshotCallback(mScreenshotCallback)
            setMiniCapCallback(mMiniCapCallback)
            start()
        }
        mMiniTouchThread = MiniTouchThread().apply {
            setMiniTouchCallback(mMiniTouchCallback)
            start()
        }
    }

    /**
     * 关闭截屏
     */
    fun closeScreenshot() {
        mMiniCapThread?.interrupt()
        mMiniCapThread = null
        mMiniTouchThread?.close()
        mMiniTouchThread = null
    }

    /**
     * 设置触摸旋转角度
     * 0°
     * 90°
     * 180°
     * 270°
     */
    fun setAngle(angle: Int) {
        mMiniTouchThread?.setAngle(angle)
    }

    fun execAction(commandBean: CommandBean) {
        mMiniTouchThread?.let { thread ->
            when (commandBean.action) {
                "down" -> {
                    thread.down(commandBean.percentageX, commandBean.percentageY)
                }
                "move" -> {
                    thread.move(commandBean.percentageX, commandBean.percentageY)
                }
                "up" -> {
                    thread.up()
                }
            }
        }
    }

    fun setScreenshotCallback(callback: ScreenshotCallback?) {
        mScreenshotCallback = callback
    }

    /**
     * 拷贝文件
     */
    private fun copyFile(inputStream: InputStream, outputStream: OutputStream) {
        val byteArray = ByteArray(4096)
        var index: Int
        do {
            index = inputStream.read(byteArray)
            if (index == -1) {
                break
            }
            outputStream.write(byteArray, 0, index)
        } while (true)
        outputStream.flush()
        outputStream.close()
        inputStream.close()
    }
}