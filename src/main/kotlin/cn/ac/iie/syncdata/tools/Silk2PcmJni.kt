package cn.ac.iie.syncdata.tools

class Silk2PcmJni {
    init {
        System.loadLibrary("silk")
    }

    external fun silk2pcm(inSilk: ByteArray): ByteArray?
}
