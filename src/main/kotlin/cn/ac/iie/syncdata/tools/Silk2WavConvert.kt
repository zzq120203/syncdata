package cn.ac.iie.syncdata.tools

import java.io.*

object Silk2WavConvert {

    private var jc: Silk2PcmJni = Silk2PcmJni()

    @Throws(Exception::class)
    fun silk2wav(silk: ByteArray): ByteArray? {
        val pcm = jc.silk2pcm(silk)
        if (pcm == null || pcm.isEmpty()) {
            return null
        }
        return pcm2wav(pcm)
    }

    @Throws(Exception::class)
    fun pcm2wav(pcm: ByteArray): ByteArray? {
        val header = WaveHeader(dataHdrLeth = pcm.size)
        val h = header.getHeader()
        assert(h.size == 44) // WAV标准，头部应该是44字节
        return h.plus(pcm)
    }

    data class WaveHeader(
            var dataHdrLeth: Int = 0,
            var fileLength: Int = dataHdrLeth + (44 - 8),
            var fmtHdrLeth: Int = 16,
            var bitsPerSample: Short = 16,
            var channels: Short = 1,
            var formatTag: Short = 0x0001,
            var samplesPerSec: Int = 16000,
            var blockAlign: Short = (channels * bitsPerSample / 8).toShort(),
            var avgBytesPerSec: Int = blockAlign * samplesPerSec,
            var fileId: CharArray = charArrayOf('R', 'I', 'F', 'F'),
            var wavTag: CharArray = charArrayOf('W', 'A', 'V', 'E'),
            var fmtHdrID: CharArray = charArrayOf('f', 'm', 't', ' '),
            var dataHdrID: CharArray = charArrayOf('d', 'a', 't', 'a')
    ) {
        fun getHeader(): ByteArray =
                ByteArrayOutputStream().use {
                writeChar(it, fileId)
                writeInt(it, fileLength)
                writeChar(it, wavTag)
                writeChar(it, fmtHdrID)
                writeInt(it, fmtHdrLeth)
                writeShort(it, formatTag.toInt())
                writeShort(it, channels.toInt())
                writeInt(it, samplesPerSec)
                writeInt(it, avgBytesPerSec)
                writeShort(it, blockAlign.toInt())
                writeShort(it, bitsPerSample.toInt())
                writeChar(it, dataHdrID)
                writeInt(it, dataHdrLeth)
                it.flush()
                return@use it.toByteArray()
            }
        private fun writeShort(bos: ByteArrayOutputStream, s: Int) {
            val myByte = ByteArray(2)
            myByte[1] = (s shl 16 shr 24).toByte()
            myByte[0] = (s shl 24 shr 24).toByte()
            bos.write(myByte)
        }

        private fun writeInt(bos: ByteArrayOutputStream, n: Int) {
            val buf = ByteArray(4)
            buf[3] = (n shr 24).toByte()
            buf[2] = (n shl 8 shr 24).toByte()
            buf[1] = (n shl 16 shr 24).toByte()
            buf[0] = (n shl 24 shr 24).toByte()
            bos.write(buf)
        }

        private fun writeChar(bos: ByteArrayOutputStream, id: CharArray) {
            for (i in id.indices) bos.write((id[i] + "").toByteArray())
        }
    }

}
