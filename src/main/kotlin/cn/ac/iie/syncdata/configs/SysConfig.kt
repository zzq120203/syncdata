package cn.ac.iie.syncdata.configs

import com.google.gson.GsonBuilder
import java.io.File

object ConfLoading {
    internal var config: ConfigAll? = null

    fun init(path: String): ConfigAll {
        val gson = GsonBuilder().disableHtmlEscaping().create()
        val file = File(path)
        config = gson.fromJson(file.readText(Charsets.UTF_8), ConfigAll::class.java)
        return config ?: ConfigAll()
    }
}

data class ConfigAll(
        val ip: String = "http://10.144.58.20:9210",
        val urls: String = "STA://localhost:20099",
        val authToken: String? = null,
        val obsQueue: String = "obsQueue",//多媒体数据
        val queue: String = "tea.data.queue",//任务队列
        val ipArray: ArrayList<String> = arrayListOf(),
        val mysqlConf: Map<String, String> = mapOf(),
        val sbClientUrl: String = "STL://localhost:20099",
        val szClientUrl: String = "STL://localhost:20099",
        val syncTNum: Int = 1
)

fun config(init: Boolean = true) = if (init) ConfLoading.config ?: throw RuntimeException("configs no init") else ConfigAll()