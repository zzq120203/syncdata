package cn.ac.iie.syncdata.server

import cn.ac.iie.syncdata.configs.config
import cn.ac.iie.syncdata.data.MMData
import com.google.gson.GsonBuilder
import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicBoolean

class Dispense: Runnable {
    private val log = LoggerFactory.getLogger(Dispense::class.java)
    private val gson = GsonBuilder().disableHtmlEscaping().create()

    companion object {
        private val start = AtomicBoolean(true)
    }
    override fun run() {
        do {
            try {
                val jedis = MMSyncServer.szobs.pc.rpL1.resource
                jedis?.let {
                    val list = it.hkeys("sync.ip").toList()
                    try {
                        val map = it.hgetAll(config().obsQueue)
                        var i = 0
                        map.forEach { key, value ->
                            val data = gson.fromJson(value, MMData::class.java)
                            data.key = key
                            it.lpush(list[i++], gson.toJson(data))
                        }
                    } finally {
                        MMSyncServer.szobs.pc.rpL1.putInstance(it)
                    }
                }
            } catch (e: Exception) {
                log.error("e -> ${e.javaClass}, msg -> ${e.message}")
            }
        } while (start.get())
    }

}