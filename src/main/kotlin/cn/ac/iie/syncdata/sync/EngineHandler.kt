package cn.ac.iie.syncdata.sync

import cn.ac.iie.syncdata.configs.config
import cn.ac.iie.syncdata.data.MMData
import cn.ac.iie.syncdata.data.Metadata
import cn.ac.iie.syncdata.data.MppConf
import cn.ac.iie.syncdata.db.DBUtil
import cn.ac.iie.syncdata.server.MMSyncServer
import com.google.gson.GsonBuilder
import org.slf4j.LoggerFactory
import java.sql.ResultSet
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

import java.util.concurrent.atomic.AtomicInteger

class EngineHandler : Runnable {

    companion object {
        private val gson = GsonBuilder().disableHtmlEscaping().create()
        private val idx = AtomicInteger(0)

        private val iList = ArrayList<String>()
        private val aList = ArrayList<String>()
        private val vList = ArrayList<String>()
        private val oList = ArrayList<String>()

        private var start = AtomicBoolean(true)
        private val log = LoggerFactory.getLogger(EngineHandler::class.java)
        fun stop() {
            start.set(false)
        }

        private fun generateSequenceID(): String {
            return UUID.randomUUID().toString().replace("-".toRegex(), "${Random().nextInt(9)}").toUpperCase()
        }

        fun pushData(mm: MMData) {
            val url = ("http://" + config().ipArray[idx.getAndIncrement() % config().ipArray.size] + ":20099/get?key=" + mm.key)

            val jedis = MMSyncServer.rpp.rpL1.resource
            if (jedis != null) {
                val md = Metadata(uuid =generateSequenceID(), url = url, mppConf = MppConf(table = mm.table, u_ch_id = mm.u_ch_id, m_chat_room = mm.m_chat_room, m_ch_id = mm.m_ch_id))
                try {
                    when (mm.key!![0]) {
                        'a' -> for (idx in aList) {
                            val json = gson.toJson(md)
                            log.info("idx -> $idx, md -> $json")
                            jedis.rpush("${config().queue}.$idx", json)
                        }
                        'v' -> for (idx in vList) {
                            val json = gson.toJson(md)
                            log.info("idx -> $idx, md -> $json")
                            jedis.rpush("${config().queue}.$idx", json)
                        }
                        'i' -> for (idx in iList) {
                            val json = gson.toJson(md)
                            log.info("idx -> $idx, md -> $json")
                            jedis.rpush("${config().queue}.$idx", json)
                        }
                        'o' -> for (idx in oList) {
                            val json = gson.toJson(md)
                            log.info("idx -> $idx, md -> $json")
                            jedis.rpush("${config().queue}.$idx", json)
                        }
                        else -> log.error("mm -> {}", mm)
                    }
                } finally {
                    MMSyncServer.rpp.rpL1.putInstance(jedis)
                }
            }
        }
    }

    override fun run() {
        val sql = "select id,e_type,data_type,parent,status from engine;"
        do {
            DBUtil.selectMysql(sql = sql, print = false, resultFun = ::getEngine)
        } while (start.get())
    }

    private fun getEngine(rs: ResultSet) {
        while (rs.next()) {
            val parent = rs.getString("parent")
            if (parent.isNullOrEmpty()) {
                val idx = rs.getString("e_type") + "-" + rs.getInt("id")
                val status = rs.getInt("status")
                when (rs.getInt("data_type")) {
                    34 -> when (status) {
                        1 -> if (!aList.contains(idx)) aList.add(idx)
                        else -> if (aList.contains(idx)) aList.remove(idx)
                    }
                    43 -> when (status) {
                        1 -> if (!vList.contains(idx)) vList.add(idx)
                        else -> if (vList.contains(idx)) vList.remove(idx)
                    }
                    3 -> when (status) {
                        1 -> if (!iList.contains(idx)) iList.add(idx)
                        else -> if (iList.contains(idx)) iList.remove(idx)
                    }
                    5 -> when (status) {
                        1 -> if (!oList.contains(idx)) oList.add(idx)
                        else -> if (oList.contains(idx)) oList.remove(idx)
                    }
                }
            }
        }
    }

}
