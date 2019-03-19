package cn.ac.iie.syncdata.tools

import it.sauronsoftware.jave.*
import java.io.File

object Video2WavConvert {

    fun video2wav(key: String, outContent: ByteArray) {
        val tmp = File("tmp/$key").apply {
            writeBytes(outContent)
        }
        mp42wav(tmp, File("a/$key"))
    }

    fun video2wav(file: File): ByteArray {
        val f = File("a/${file.name}")
        mp42wav(file, f)
        return f.readBytes()
    }

    private fun mp42wav(source: File, target: File) {
        val audio = AudioAttributes()
        audio.setCodec("pcm_s16le")
        val attrs = EncodingAttributes()
        attrs.setFormat("wav")
        attrs.setAudioAttributes(audio)
        val encoder = Encoder()
        encoder.encode(source, target, attrs)
    }
}
