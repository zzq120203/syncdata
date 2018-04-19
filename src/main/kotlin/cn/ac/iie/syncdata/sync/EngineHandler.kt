package cn.ac.iie.syncdata.sync

import cn.ac.iie.syncdata.configs.config
import cn.ac.iie.syncdata.data.MMData
import cn.ac.iie.syncdata.data.Metadata
import cn.ac.iie.syncdata.data.MppConf
import cn.ac.iie.syncdata.db.DBUtil
import cn.ac.iie.syncdata.server.MMSync
import com.google.gson.GsonBuilder
import org.slf4j.LoggerFactory
import java.sql.ResultSet
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

import java.util.concurrent.atomic.AtomicInteger

class EngineHandler : Runnable {

    companion object {
        private val gson = GsonBuilder().create()
        private val idx = AtomicInteger(0)

        private val iList = ArrayList<String>()
        private val aList = ArrayList<String>()
        private val vList = ArrayList<String>()

        private var start = AtomicBoolean(true)
        private val log = LoggerFactory.getLogger(EngineHandler::class.java)
        fun stop() {
            start.set(false)
        }

        fun pushData(mm: MMData) {
            val url = ("http://" + config().ipArray[idx.getAndIncrement() % config().ipArray.size] + ":20099/get?key=" + mm.key)

            val jedis = MMSync.rpp.rpL1.resource
            if (jedis != null) {
                val md = Metadata(uuid = UUID.randomUUID().toString(), url = url, mppConf = MppConf(table = mm.table, u_ch_id = mm.u_ch_id, m_chat_room = mm.m_chat_room, m_ch_id = mm.m_ch_id))
                try {
                    when (mm.key!![0]) {
                        'a' -> for (eid in aList) {
                            val json = gson.toJson(md)
                            log.info("eid -> $eid, md -> $json")
                            jedis.rpush("${config().queue}.$eid", json)
                        }
                        'v' -> for (eid in vList) {
                            val json = gson.toJson(md)
                            log.info("eid -> $eid, md -> $json")
                            jedis.rpush("${config().queue}.$eid", json)
                        }
                        'i' -> for (eid in iList) {
                            val json = gson.toJson(md)
                            log.info("eid -> $eid, md -> $json")
                            jedis.rpush("${config().queue}.$eid", json)
                        }
                        else -> log.error("mm -> {}", mm)
                    }
                } finally {
                    MMSync.rpp.rpL1.putInstance(jedis)
                }
            }
        }
    }

    override fun run() {
        val sql = "select id,e_id,data_type,status from engine;"
        do {
            DBUtil.selectMysql(sql = sql, print = false, resultFun = ::getEngine)
        } while (start.get())
    }

    private fun getEngine(rs: ResultSet) {
        while (rs.next()) {
            val id = rs.getString("e_id") + "-" + rs.getInt("id")
            val status = rs.getInt("status")
            when (rs.getInt("data_type")) {
                34 -> when (status) {
                    1 -> if (!aList.contains(id)) aList.add(id)
                    else -> if (aList.contains(id)) aList.remove(id)
                }
                43 -> when (status) {
                    1 -> if (!vList.contains(id)) vList.add(id)
                    else -> if (vList.contains(id)) vList.remove(id)
                }
                3 -> when (status) {
                    1 -> if (!iList.contains(id)) iList.add(id)
                    else -> if (iList.contains(id)) iList.remove(id)
                }
            }
        }
    }

}
