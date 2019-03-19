package cn.ac.iie.syncdata.http

import cn.ac.iie.syncdata.data.Status
import cn.ac.iie.syncdata.data.SysCallback
import cn.ac.iie.syncdata.extend.toJson
import io.javalin.Javalin
import org.slf4j.LoggerFactory
import java.io.File

class MyHttpServer {

    private val log = LoggerFactory.getLogger(MyHttpServer::class.java)

    private lateinit var app: Javalin

    fun start(port: Int = 8099): MyHttpServer {
        app = Javalin.create().apply {
            port(port)
            defaultCharacterEncoding("utf-8")
            start()
        }.exception(Exception::class.java) { e, ctx ->
            log.error("e -> ${e.javaClass}, msg -> ${e.message}")
            //log.error(e.message, e)
            ctx.result(SysCallback(id = "1", status = Status("-1", e.message)).toJson())
        }.error(404) { ctx ->
            val result = SysCallback(id = "1", status = Status("404", "地址错误"))
            ctx.result(result.toJson())
        }.error(401) { ctx ->
            val result = SysCallback(id = "1", status = Status("401", "没有访问权限, 请先登录或更换账号, 如果问题依然存在, 请与管理员联系。"))
            ctx.result(result.toJson())
        }
        return this
    }

    fun handle(): MyHttpServer {
        app.get("/a/:key") { ctx ->
            val key = ctx.param("key")
            ctx.result(File("a/$key").inputStream())
        }
        return this
    }



}

fun main(args: Array<String>) {
    MyHttpServer().start().handle()
}
