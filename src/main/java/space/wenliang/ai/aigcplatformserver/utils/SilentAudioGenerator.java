package space.wenliang.ai.aigcplatformserver.utils;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;

import java.nio.Buffer;
import java.nio.ShortBuffer;

public class SilentAudioGenerator {

    public static void main(String[] args) {
        String outputFile = "tmp/silent.wav";
        int sampleRate = 32000; // Sample rate in Hz
        int audioChannels = 1; // Number of audio channels (mono)
        int bitRate = 512000; // Bit rate in bits per second (bps)
        int durationMs = 5000; // Duration in milliseconds

        generateSilentAudio(outputFile, sampleRate, audioChannels, bitRate, durationMs);
    }

    public static void generateSilentAudio(String outputFile, int sampleRate, int audioChannels, int bitRate, int durationMs) {
        try {
            // Prepare the recorder for the silent audio file
            FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputFile, audioChannels);
            recorder.setAudioCodec(avcodec.AV_CODEC_ID_PCM_S16LE);
            recorder.setSampleRate(sampleRate);
            recorder.setAudioBitrate(bitRate);
            recorder.start();

            // Calculate the number of samples needed for the duration
            int numSamples = (int) ((sampleRate * durationMs) / 1000.0);
            short[] silentBuffer = new short[numSamples * audioChannels];
            ShortBuffer buffer = ShortBuffer.wrap(silentBuffer);

            // Create a frame containing the silent audio buffer
            Frame silenceFrame = new Frame();
            silenceFrame.sampleRate = sampleRate;
            silenceFrame.audioChannels = audioChannels;
            silenceFrame.samples = new Buffer[]{buffer};

            // Record the silent frame
            recorder.recordSamples(sampleRate, audioChannels, buffer);

            recorder.stop();
            recorder.release();

            System.out.println("Silent audio generated successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
