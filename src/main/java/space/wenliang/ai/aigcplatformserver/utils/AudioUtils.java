package space.wenliang.ai.aigcplatformserver.utils;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.FFmpegFrameFilter;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.springframework.util.CollectionUtils;
import space.wenliang.ai.aigcplatformserver.bean.text.ChapterInfo;

import java.nio.Buffer;
import java.nio.ShortBuffer;
import java.util.List;

public class AudioUtils {

    public static void mp3ToWav(String input, String output) throws Exception {
        // 创建FFmpegFrameGrabber以读取MP3文件
        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(input)) {
            grabber.start();

            // 创建FFmpegFrameRecorder以写入WAV文件
            try (FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(output, grabber.getAudioChannels())) {
                recorder.setAudioCodec(avcodec.AV_CODEC_ID_PCM_S16LE);
                recorder.setSampleRate(32000);  // 设置采样率
                recorder.setAudioChannels(1);   // 设置音频通道数
                recorder.start();

                Frame frame;
                while ((frame = grabber.grabFrame()) != null) {
                    recorder.record(frame);
                }

                recorder.stop();
            }

            grabber.stop();
        }
    }

    public static void mergeAudioFiles(List<ChapterInfo> chapterInfos, String outputFile) {
        if (CollectionUtils.isEmpty(chapterInfos)) {
            throw new IllegalArgumentException("Audio segments list cannot be null or empty");
        }

        FFmpegFrameGrabber initialGrabber = null;
        FFmpegFrameRecorder recorder = null;

        try {
            initialGrabber = new FFmpegFrameGrabber(chapterInfos.getFirst().getAudioPath());
            initialGrabber.start();

            int sampleRate = initialGrabber.getSampleRate();
            int audioChannels = initialGrabber.getAudioChannels();
            int audioBitrate = initialGrabber.getAudioBitrate();
            int audioCodec = initialGrabber.getAudioCodec();
            initialGrabber.stop();

            recorder = new FFmpegFrameRecorder(outputFile, audioChannels);
            recorder.setAudioCodec(audioCodec);
            recorder.setSampleRate(sampleRate);
            recorder.setAudioBitrate(audioBitrate);
            recorder.start();

            for (int i = 0; i < chapterInfos.size(); i++) {
                ChapterInfo chapterInfo = chapterInfos.get(i);
                try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(chapterInfo.getAudioPath())) {
                    grabber.start();

                    // 保存音频时长
                    chapterInfo.setLengthInMs((long) (grabber.getLengthInTime() / (1000 * chapterInfo.getSpeed())));

                    recordAudio(grabber, recorder, chapterInfo.getSpeed(), chapterInfo.getVolume() / 100.0); // 固定值 1.0

                    if (i < chapterInfos.size() - 1) {
                        recordSilence(recorder, sampleRate, audioChannels, chapterInfo.getInterval());
                    }
                }
            }
            System.out.println("Audio files merged successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (initialGrabber != null) {
                    initialGrabber.release();
                }
                if (recorder != null) {
                    recorder.stop();
                    recorder.release();
                }
            } catch (FFmpegFrameGrabber.Exception | FFmpegFrameRecorder.Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void recordAudio(FFmpegFrameGrabber grabber, FFmpegFrameRecorder recorder, double speed, double volume) throws Exception {
        String filterString = String.format("atempo=%.1f,volume=%.1f", speed, volume);

        try (FFmpegFrameFilter filter = new FFmpegFrameFilter(filterString, grabber.getAudioChannels())) {
            filter.setSampleRate(grabber.getSampleRate());
            filter.start();
            Frame frame;

            while ((frame = grabber.grabFrame()) != null) {
                filter.push(frame);
                Frame filteredFrame;
                while ((filteredFrame = filter.pull()) != null) {
                    recorder.record(filteredFrame);
                }

            }
        }
    }

    private static void recordSilence(FFmpegFrameRecorder recorder, int sampleRate, int audioChannels, int durationMs) throws Exception {
        int numSamples = (int) ((sampleRate * durationMs) / 1000.0);
        short[] silentBuffer = new short[numSamples * audioChannels];
        ShortBuffer buffer = ShortBuffer.wrap(silentBuffer);

        try (Frame silenceFrame = new Frame()) {
            silenceFrame.sampleRate = sampleRate;
            silenceFrame.audioChannels = audioChannels;
            silenceFrame.samples = new Buffer[]{buffer};

            recorder.recordSamples(sampleRate, audioChannels, buffer);
        }
    }
}