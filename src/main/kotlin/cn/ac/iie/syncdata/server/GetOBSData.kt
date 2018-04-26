package cn.ac.iie.syncdata.server

import cn.ac.iie.syncdata.configs.config
import cn.ac.iie.syncdata.data.MMData
import com.google.gson.GsonBuilder
import com.lmax.disruptor.RingBuffer
import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicBoolean

class GetOBSData(private val ringBuffer: RingBuffer<MMData>): Runnable {
    private val log = LoggerFactory.getLogger(GetOBSData::class.java)
    private val gson = GsonBuilder().disableHtmlEscaping().create()
    companion object {
        private val start = AtomicBoolean(true)
        fun stop() {
            start.set(false)
        }
    }

    override fun run() {
        var seq = 0L
        var mmd: MMData
        do {
            val jedis = MMSync.szobs.pc.rpL1.resource
            jedis?.let {
                try {
                    val map = it.hgetAll(config().obsQueue)
                    map.forEach { key, value ->
                        val md = gson.fromJson(value, MMData::class.java)
                        try {
                            seq = ringBuffer.next()
                            mmd = ringBuffer.get(seq)
                            mmd.key = key
                            mmd.g_id = md.g_id
                            mmd.table = mmd.table
                            mmd.m_ch_id = md.m_ch_id
                            mmd.m_chat_room = md.m_chat_room
                            mmd.u_ch_id = mmd.u_ch_id
                            mmd.type = mmd.type
                            mmd.q_id = md.q_id
                            mmd.q_mm_md5_rk = md.q_mm_md5_rk
                        } finally {
                            ringBuffer.publish(seq)
                        }
                    }
                } finally {
                    MMSync.szobs.pc.rpL1.putInstance(it)
                }
            }
        } while (start.get())
    }
}