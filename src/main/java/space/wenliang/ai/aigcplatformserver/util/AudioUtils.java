package space.wenliang.ai.aigcplatformserver.util;

import lombok.extern.slf4j.Slf4j;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.*;
import space.wenliang.ai.aigcplatformserver.entity.ChapterInfoEntity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ShortBuffer;
import java.util.List;

import static org.bytedeco.ffmpeg.global.avutil.AV_LOG_ERROR;

@Slf4j
public class AudioUtils {

    static {
        FFmpegLogCallback.set();
        FFmpegLogCallback.setLevel(AV_LOG_ERROR);
    }

    public static void wavFormat(byte[] bytes, String output) throws Exception {
        InputStream inputStream = new ByteArrayInputStream(bytes);

        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputStream)) {
            grabber.start();

            try (FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(output, grabber.getAudioChannels())) {
                recorder.setAudioCodec(avcodec.AV_CODEC_ID_PCM_S16LE);
                recorder.setSampleRate(32000);
                recorder.setAudioChannels(1);
                recorder.setAudioBitrate(512000);
                recorder.setFormat("wav");
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

    public static void mergeAudioFiles(List<ChapterInfoEntity> chapterInfos, String outputFile) throws Exception {
        if (chapterInfos == null || chapterInfos.isEmpty()) {
            throw new IllegalArgumentException("Audio segments list cannot be null or empty");
        }

        for (ChapterInfoEntity chapterInfo : chapterInfos) {
            filterProcess(chapterInfo);
        }

        try (FFmpegFrameGrabber initialGrabber = new FFmpegFrameGrabber(new ByteArrayInputStream(chapterInfos.getFirst().getAudioBytes()))) {
            initialGrabber.start();

            int sampleRate = initialGrabber.getSampleRate();
            int audioChannels = initialGrabber.getAudioChannels();
            int audioBitrate = initialGrabber.getAudioBitrate();
            int audioCodec = initialGrabber.getAudioCodec();
            initialGrabber.stop();

            try (FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputFile, audioChannels)) {
                recorder.setAudioCodec(audioCodec);
                recorder.setSampleRate(sampleRate);
                recorder.setAudioBitrate(audioBitrate);
                recorder.start();

                for (int i = 0; i < chapterInfos.size(); i++) {
                    ChapterInfoEntity chapterInfo = chapterInfos.get(i);
                    try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(new ByteArrayInputStream(chapterInfo.getAudioBytes()))) {
                        grabber.start();

                        chapterInfo.setAudioLength(grabber.getLengthInTime() / 1000);

                        recordAudio(grabber, recorder);

                        if (i < chapterInfos.size() - 1) {
                            recordSilence(recorder, sampleRate, audioChannels, chapterInfo.getNextAudioInterval());
                        }
                    }
                    chapterInfo.setAudioBytes(null);
                }

                recorder.stop();
            }
        }
    }

    private static void filterProcess(ChapterInfoEntity chapterInfo) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(chapterInfo.getAudioPath())) {
            grabber.start();

            try (FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputStream, grabber.getAudioChannels())) {
                recorder.setAudioCodec(grabber.getAudioCodec());
                recorder.setSampleRate(grabber.getSampleRate());
                recorder.setAudioChannels(grabber.getAudioChannels());
                recorder.setAudioBitrate(grabber.getAudioBitrate());
                recorder.setFormat("wav");
                recorder.start();

                String filterString = String.format("atempo=%.1f,volume=%.1f", chapterInfo.getAudioSpeed(), chapterInfo.getAudioVolume());

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

                    filter.stop();
                }

                recorder.stop();
            }

            grabber.stop();

            chapterInfo.setAudioBytes(outputStream.toByteArray());
        }
    }

    private static void recordAudio(FFmpegFrameGrabber grabber, FFmpegFrameRecorder recorder) throws Exception {
        Frame frame;
        while ((frame = grabber.grabFrame()) != null) {
            recorder.record(frame);
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