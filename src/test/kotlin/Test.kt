import cn.ac.iie.syncdata.tools.Silk2WavConvert
import javazoom.spi.mpeg.sampled.file.MpegAudioFileReader
import java.io.ByteArrayInputStream
import java.io.File
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem



object Test {
    @JvmStatic
    fun main(args: Array<String>) {
        val file = File("test.mp3")
        val mp3audioStream = AudioSystem.getAudioInputStream(ByteArrayInputStream(file.readBytes()))

        val sourceFormat = mp3audioStream.format
        val convertFormat = AudioFormat(AudioFormat.Encoding.PCM_SIGNED, sourceFormat.sampleRate, 16, sourceFormat.channels, sourceFormat.channels * 2, sourceFormat.sampleRate, false)

        val inputStream = AudioSystem.getAudioInputStream(convertFormat, mp3audioStream)

        val wav = Silk2WavConvert.pcm2wav(inputStream.readBytes()) ?: byteArrayOf()
    }
}