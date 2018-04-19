package cn.ac.iie.syncdata.sync

import cn.ac.iie.syncdata.configs.config
import cn.ac.iie.syncdata.data.MMData
import cn.ac.iie.syncdata.server.MMSync
import cn.ac.iie.syncdata.tools.Silk2WavConvert
import com.lmax.disruptor.LifecycleAware
import com.lmax.disruptor.WorkHandler
import org.slf4j.LoggerFactory

import java.util.concurrent.atomic.AtomicInteger

class SyncDataHandler : WorkHandler<MMData>, LifecycleAware {
    private val log = LoggerFactory.getLogger(SyncDataHandler::class.java)
    private var oldName: String? = null
    private val name = "SyncDataHandlerThread-"
    override fun onEvent(md: MMData) {
        md.key?.let {
            val outContent = MMSync.szobs.get(it)
            if (outContent == null || outContent.isEmpty()) {
                log.error("Audio outContent:null, data -> $md")
                return
            }
            val inContent = when (it[0]) {
                'a' -> Silk2WavConvert.silk2wav(outContent)
                else -> outContent
            }
            MMSync.sbobs.put(md.key, inContent, false)
            EngineHandler.pushData(md)
            log.info("sz -> sb, data -> $md")
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
