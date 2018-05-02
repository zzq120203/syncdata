package cn.ac.iie.syncdata.server

import cn.ac.iie.client.OBSSClient
import cn.ac.iie.common.RPoolProxy
import cn.ac.iie.syncdata.configs.ConfLoading
import cn.ac.iie.syncdata.configs.config
import cn.ac.iie.syncdata.data.MMData
import cn.ac.iie.syncdata.sync.EngineHandler
import cn.ac.iie.syncdata.sync.SyncDataHandler
import com.lmax.disruptor.BlockingWaitStrategy
import com.lmax.disruptor.EventFactory
import com.lmax.disruptor.dsl.Disruptor
import com.lmax.disruptor.dsl.ProducerType
import com.lmax.disruptor.util.DaemonThreadFactory
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.core.LoggerContext
import org.slf4j.LoggerFactory
import java.io.File
import java.util.concurrent.TimeUnit

object MMSyncServer {
    private val log = LoggerFactory.getLogger(MMSyncServer::class.java)

    private val disruptor = Disruptor(EventFactory { MMData() }, 256, DaemonThreadFactory.INSTANCE, ProducerType.MULTI, BlockingWaitStrategy())

    val rpp = RPoolProxy(null)

    val sbobs = OBSSClient()
    val szobs = OBSSClient()

    init {
        val logContext = LogManager.getContext(false) as LoggerContext
        val log4jFile = File("configs/log4j2.xml")
        logContext.configLocation = log4jFile.toURI()
        logContext.reconfigure()

        ConfLoading.init("configs/config.json")

        rpp.init(config().urls, config().authToken)
        rpp.init(config().sbClientUrl, null)
        rpp.init(config().szClientUrl, null)
    }

    @JvmStatic
    fun main(args: Array<String>) {
        log.info("MMSyncServer starting")
        try {
            Thread(EngineHandler(), "EngineHandlerThread").start()

            val sync = arrayOfNulls<SyncDataHandler>(config().syncTNum)
            for (i in sync.indices) {
                sync[i] = SyncDataHandler()
            }
            disruptor.handleEventsWithWorkerPool(*sync)

            val ringBuffer = disruptor.start()
            val getMetaData = Thread(GetOBSData(ringBuffer), "GetOBSDataThread")
            getMetaData.start()

            Runtime.getRuntime().addShutdownHook(Thread {
                log.info("MMSyncServer stopping")
                GetOBSData.stop()
                EngineHandler.stop()

                disruptor.shutdown(100, TimeUnit.SECONDS)
            })


        } catch (e: Exception) {

        }
    }
}