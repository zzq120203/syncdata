package cn.ac.iie.syncdata.tools

import it.sauronsoftware.jave.*

import java.io.File

object AudioVideoEncoder {

    fun mp42wav(sourcePath: String, targetPath: String) {
        val source = File(sourcePath)
        val target = File(targetPath)
        mp42wav(source, target)
    }

    fun mp42wav(source: File, target: File) {
        val audio = AudioAttributes()
        audio.setCodec("pcm_s16le")
        val attrs = EncodingAttributes()
        attrs.setFormat("wav")
        attrs.setAudioAttributes(audio)
        val encoder = Encoder()
        encoder.encode(source, target, attrs)
    }

}
