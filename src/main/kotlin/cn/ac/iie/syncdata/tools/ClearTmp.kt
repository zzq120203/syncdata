package cn.ac.iie.syncdata.tools

import cn.ac.iie.syncdata.extend.isStart
import org.slf4j.LoggerFactory
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean

class ClearTmp: Runnable {
    private val log = LoggerFactory.getLogger(ClearTmp::class.java)

    companion object {
        private val status = AtomicBoolean(true)
        fun stop() = status.set(false)
    }

    override fun run() {
        while (status.isStart()) try {
            val time = System.currentTimeMillis()
            arrayListOf("a", "tmp")
                    .map {
                        val dir = File(it)
                        if (!dir.exists()) dir.createNewFile()
                        dir.listFiles()
                    }
                    .forEach { it.filter { it.lastModified() < (time - 3 * 60 * 60 * 1000) }.map { it.delete() } }
        } catch (e: Exception) {
            log.error("e -> ${e.javaClass}, msg -> ${e.message}")
        }
    }
}