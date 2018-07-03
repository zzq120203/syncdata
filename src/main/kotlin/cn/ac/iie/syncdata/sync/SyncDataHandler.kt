package cn.ac.iie.syncdata.sync

import cn.ac.iie.syncdata.data.MMData
import cn.ac.iie.syncdata.server.MMSyncServer
import cn.ac.iie.syncdata.tools.Silk2WavConvert
import cn.ac.iie.syncdata.tools.Video2WavConvert
import com.lmax.disruptor.LifecycleAware
import com.lmax.disruptor.WorkHandler
import org.slf4j.LoggerFactory

import java.util.concurrent.atomic.AtomicInteger

class SyncDataHandler : WorkHandler<MMData>, LifecycleAware {
    private val log = LoggerFactory.getLogger(SyncDataHandler::class.java)
    private var oldName: String? = null
    private val name = "SyncDataHandlerThread-"
    override fun onEvent(md: MMData) {
        try {
            md.key?.let {
                val outContent = MMSyncServer.szobs.get(it)
                if (outContent == null || outContent.isEmpty()) {
                    log.error("Audio outContent:null, data -> $md")
                    return
                }
                val inContent = when (it[0]) {
                    'a' -> Silk2WavConvert.silk2wav(outContent)
                    else -> outContent
                }
                MMSyncServer.sbobs.put(md.key, inContent)
                if (!md.u_ch_id.isNullOrEmpty()) {
                    EngineHandler.pushData(md)
                }

                if (it[0] == 'v') {
                    Video2WavConvert.video2wav(it ,outContent)
                    EngineHandler.pushData(md, true)
                }

                log.info("sz -> sb, data -> $md")
            }
        } catch (e: Exception) {
            log.error("e -> ${e.javaClass}, msg -> ${e.message}")
        }
    }

    override fun onStart() {
        val currentThread = Thread.currentThread()
        oldName = currentThread.name
        currentThread.name = name + threadId.addAndGet(1)
    }

    override fun onShutdown() {
        Thread.currentThread().name = oldName!!
    }

    companion object {
        private val threadId = AtomicInteger(0)
    }
}
