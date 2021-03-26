package com.example.mobilecontrol.util

import android.util.Log
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader


/**
 * ShellUtil
 *
 * **Check root**
 *  * [ShellUtil.checkRootPermission]
 *
 *
 * **Execte command**
 *  * [ShellUtil.execCommand]
 *  * [ShellUtil.execCommand]
 *  * [ShellUtil.execCommand]
 *  * [ShellUtil.execCommand]
 *  * [ShellUtil.execCommand]
 *  * [ShellUtil.execCommand]
 *
 *
 * @author [Trinea](http://www.trinea.cn) 2013-5-16
 */
internal object ShellUtil {

    val COMMAND_SU = "su"//daemonsu   to  su
    val COMMAND_SUXC = "suxc"
    val COMMAND_SH = "sh"
    val COMMAND_EXIT = "exit\n"
    val COMMAND_LINE_END = "\n"


    /**
     * check whether has root permission
     *
     * @return
     */
    fun checkRootPermission(): Boolean {
        return execCommand("echo root", isRoot = true, isNeedResultMsg = true).result == 0
    }


    /**
     * execute shell command, default return result msg
     *
     * @param command command
     * @param isRoot whether need to run with root
     * @return
     * @see ShellUtil.execCommand
     */
    fun execCommand(command: String, isRoot: Boolean): CommandResult {
        return execCommand(arrayOf(command), isRoot, true)
    }


    /**
     * execute shell commands, default return result msg
     *
     * @param commands command list
     * @param isRoot whether need to run with root
     * @return
     * @see ShellUtil.execCommand
     */
    fun execCommand(commands: List<String>?, isRoot: Boolean): CommandResult {
        return execCommand(commands?.toTypedArray(), isRoot, true)
    }


    /**
     * execute shell command
     *
     * @param command command
     * @param isRoot whether need to run with root
     * @param isNeedResultMsg whether need result msg
     * @return
     * @see ShellUtil.execCommand
     */
    fun execCommand(command: String, isRoot: Boolean, isNeedResultMsg: Boolean): CommandResult {
        return execCommand(arrayOf(command), isRoot, isNeedResultMsg)
    }


    /**
     * execute shell commands
     *
     * @param commands command list
     * @param isRoot whether need to run with root
     * @param isNeedResultMsg whether need result msg
     * @return
     * @see ShellUtil.execCommand
     */
    fun execCommand(
        commands: List<String>?,
        isRoot: Boolean,
        isNeedResultMsg: Boolean
    ): CommandResult {
        return execCommand(commands?.toTypedArray(), isRoot, isNeedResultMsg)
    }

    /**
     * execute shell commands
     *
     * @param commands command array
     * @param isRoot whether need to run with root
     * @param isNeedResultMsg whether need result msg
     * @return
     *  * if isNeedResultMsg is false, [CommandResult.successMsg] is null and
     * [CommandResult.errorMsg] is null.
     *  * if [CommandResult.result] is -1, there maybe some excepiton.
     *
     */
    @JvmOverloads
    fun execCommand(
        commands: Array<String>?,
        isRoot: Boolean,
        isNeedResultMsg: Boolean = true
    ): CommandResult {
        var result = -1
        if (commands == null || commands.isEmpty()) {
            return CommandResult(result, null, null)
        }
        var process: Process? = null
        var successResult: BufferedReader? = null
        var errorResult: BufferedReader? = null
        var successMsg: StringBuilder? = null
        var errorMsg: StringBuilder? = null
        var os: DataOutputStream? = null
        try {
            process =
                Runtime.getRuntime().exec(if (isRoot) COMMAND_SU else COMMAND_SH)//maybe add SH
            os = DataOutputStream(process!!.outputStream)
            for (command in commands) {
                os.write(command.toByteArray())
                os.writeBytes(COMMAND_LINE_END)
                os.flush()
            }
            os.writeBytes(COMMAND_EXIT)
            os.flush()

            //System.out.println("waiting for commands excute result...");
            result = process.waitFor()
            // get command result
            if (isNeedResultMsg) {
                result = 0
                successMsg = StringBuilder()
                errorMsg = StringBuilder()
                successResult = BufferedReader(InputStreamReader(process.inputStream))
                errorResult = BufferedReader(InputStreamReader(process.errorStream))
                var s: String?
                do {
                    s = successResult.readLine()
                    if (s.isNullOrBlank()) {
                        break
                    }
                    successMsg.append(s).append('\n')
                    result = 0
                } while (true)

                do {
                    s = errorResult.readLine()
                    if (s == null) {
                        break
                    }
                    errorMsg.append(s).append('\n')
                    result = 0
                } while (true)
            }
        } catch (e: Exception) {
            Log.e("ShellUtil", "执行Shell命令出错:${e.message}")
        } finally {
            try {
                os?.close()
                successResult?.close()
                errorResult?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            process?.destroy()
        }
        return CommandResult(result, successMsg?.toString(), errorMsg?.toString())
    }


    /**
     * result of command
     *
     *  * [CommandResult.result] means result of command, 0 means normal, else means error, same to excute in
     * linux shell
     *  * [CommandResult.successMsg] means success message of command result
     *  * [CommandResult.errorMsg] means error message of command result
     *
     *
     * @author [Trinea](http://www.trinea.cn) 2013-5-16
     */
    class CommandResult {


        /** result of command  */
        var result: Int = 0
        /** success message of command result  */
        var successMsg: String? = null
        /** error message of command result  */
        var errorMsg: String? = null


        constructor(result: Int) {
            this.result = result
        }

        constructor(result: Int, successMsg: String?, errorMsg: String?) {
            this.result = result
            this.successMsg = successMsg
            this.errorMsg = errorMsg
        }

        override fun toString(): String {
            return "CommandResult(result=$result, successMsg=$successMsg, errorMsg=$errorMsg)"
        }


    }
}
