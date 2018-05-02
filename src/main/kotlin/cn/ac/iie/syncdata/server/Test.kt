package cn.ac.iie.syncdata.server

import cn.ac.iie.syncdata.tools.Silk2WavConvert
import java.io.File

object Test {
    @JvmStatic
    fun main(args: Array<String>) {
        val file = File(args[0])
        val wav = Silk2WavConvert.silk2wav(file.readBytes())
        wav?.let {
            File(args[1]).writeBytes(wav)
        }
    }
}