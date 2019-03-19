package cn.ac.iie.syncdata.tools;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.*;
import java.util.Arrays;

public class Mp32Pcm {

    public static byte[] convertAudioFiles(byte[] wavbyte) {
        try {
            return Arrays.copyOfRange(wavbyte, 44, wavbyte.length);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static AudioInputStream mp3Convertpcm(File mp3) throws Exception {
        //原MP3文件转AudioInputStream
        AudioInputStream mp3audioStream = AudioSystem.getAudioInputStream(mp3);
        //将AudioInputStream MP3文件 转换为PCM AudioInputStream
        //准备转换的流输出到OutputStream
        return AudioSystem.getAudioInputStream(AudioFormat.Encoding.PCM_SIGNED, mp3audioStream);
    }

}
