package space.wenliang.ai.aigcplatformserver.utils;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;

import java.nio.Buffer;
import java.nio.ShortBuffer;
import java.util.List;

public class AudioMerger {

    static class AudioSegment {
        String filePath;
        int silenceDurationMs; // Duration of silence in milliseconds

        AudioSegment(String filePath, int silenceDurationMs) {
            this.filePath = filePath;
            this.silenceDurationMs = silenceDurationMs;
        }
    }

    public static void main(String[] args) {
        List<AudioSegment> audioSegments = List.of(
                new AudioSegment("tmp/0-0-1717065164419.wav", 5000),
                new AudioSegment("tmp/1-0-1716999196890.wav", 2000),
                new AudioSegment("tmp/2-0-1716992160215.wav", 1000),
                new AudioSegment("tmp/3-0-1717072164974.wav", 4000)
        );
        String outputFile = "tmp/output.wav";

        mergeAudioFiles(audioSegments, outputFile);
    }

    public static void mergeAudioFiles(List<AudioSegment> audioSegments, String outputFile) {
        if (audioSegments == null || audioSegments.isEmpty()) {
            throw new IllegalArgumentException("Audio segments list cannot be null or empty");
        }

        try {
            // Initialize the first grabber to get audio format details
            FFmpegFrameGrabber initialGrabber = new FFmpegFrameGrabber(audioSegments.get(0).filePath);
            initialGrabber.start();

            int sampleRate = initialGrabber.getSampleRate();
            int audioChannels = initialGrabber.getAudioChannels();
            int audioBitrate = initialGrabber.getAudioBitrate();
            initialGrabber.stop();

            // Prepare the recorder for the output file
            FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputFile, audioChannels);
            recorder.setAudioCodec(avcodec.AV_CODEC_ID_PCM_S16LE);
            recorder.setSampleRate(sampleRate);
            recorder.setAudioBitrate(audioBitrate);
            recorder.start();

            // Process each audio segment
            for (int i = 0; i < audioSegments.size(); i++) {
                AudioSegment segment = audioSegments.get(i);
                FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(segment.filePath);
                grabber.start();

                // Record the audio file
                recordAudio(grabber, recorder);

                // Record silence if it's not the last file
                if (i < audioSegments.size() - 1) {
                    recordSilence(recorder, sampleRate, audioChannels, segment.silenceDurationMs);
                }

                grabber.stop();
            }

            recorder.stop();
            recorder.release();

            System.out.println("Audio files merged successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void recordAudio(FFmpegFrameGrabber grabber, FFmpegFrameRecorder recorder) throws Exception {
        Frame frame;
        while ((frame = grabber.grabFrame()) != null) {
            if (frame.samples != null) {
                recorder.record(frame);
            }
        }
    }

    private static void recordSilence(FFmpegFrameRecorder recorder, int sampleRate, int audioChannels, int durationMs) throws Exception {
        int numSamples = (int) ((sampleRate * durationMs) / 1000.0);
        short[] silentBuffer = new short[numSamples * audioChannels];
        ShortBuffer buffer = ShortBuffer.wrap(silentBuffer);

        Frame silenceFrame = new Frame();
        silenceFrame.sampleRate = sampleRate;
        silenceFrame.audioChannels = audioChannels;
        silenceFrame.samples = new Buffer[] { buffer };

        recorder.recordSamples(sampleRate, audioChannels, buffer);
    }
}
