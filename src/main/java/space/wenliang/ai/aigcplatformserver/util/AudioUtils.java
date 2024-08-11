package space.wenliang.ai.aigcplatformserver.util;

import lombok.extern.slf4j.Slf4j;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.*;
import org.springframework.util.CollectionUtils;
import space.wenliang.ai.aigcplatformserver.bean.AudioSegment;
import space.wenliang.ai.aigcplatformserver.exception.BizException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.Buffer;
import java.nio.ShortBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import static org.bytedeco.ffmpeg.global.avutil.AV_LOG_ERROR;

@Slf4j
public class AudioUtils {

    static {
        FFmpegLogCallback.set();
        FFmpegLogCallback.setLevel(AV_LOG_ERROR);
    }

    public static byte[] audioFormat(byte[] bytes) throws Exception {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputStream)) {
                grabber.start();

                try (FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputStream, grabber.getAudioChannels())) {
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
            return outputStream.toByteArray();
        }
    }

    public static void mergeAudioFiles(List<AudioSegment> audioSegments, String outputPath) throws Exception {
        if (CollectionUtils.isEmpty(audioSegments)) {
            throw new BizException("Audio segments list cannot be null or empty");
        }

        for (AudioSegment audioSegment : audioSegments) {
            filterProcess(audioSegment);
        }

        mergeAudio(audioSegments, outputPath);
    }

    private static void filterProcess(AudioSegment audioSegment) throws Exception {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(Files.readAllBytes(Path.of(audioSegment.getAudioPath())));
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            audioFrame(inputStream, outputStream, audioSegment.getAudioSpeed(), audioSegment.getAudioVolume());
            audioSegment.setAudioBytes(outputStream.toByteArray());
        }
    }

    public static void mergeAudio(List<AudioSegment> audioSegments, String outputPath) throws Exception {
        if (CollectionUtils.isEmpty(audioSegments)) {
            return;
        }
        try (FFmpegFrameGrabber initialGrabber = new FFmpegFrameGrabber(new ByteArrayInputStream(audioSegments.getFirst().getAudioBytes()))) {
            initialGrabber.start();

            int sampleRate = initialGrabber.getSampleRate();
            int audioChannels = initialGrabber.getAudioChannels();
            int audioBitrate = initialGrabber.getAudioBitrate();
            int audioCodec = initialGrabber.getAudioCodec();
            initialGrabber.stop();

            try (FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputPath, audioChannels)) {
                recorder.setAudioCodec(audioCodec);
                recorder.setSampleRate(sampleRate);
                recorder.setAudioBitrate(audioBitrate);
                recorder.start();

                for (int i = 0; i < audioSegments.size(); i++) {
                    AudioSegment audioSegment = audioSegments.get(i);
                    try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(new ByteArrayInputStream(audioSegment.getAudioBytes()))) {
                        grabber.start();

                        audioSegment.setAudioLength(grabber.getLengthInTime() / 1000);

                        recordAudio(grabber, recorder);

                        if (i < audioSegments.size() - 1
                                && Objects.nonNull(audioSegment.getAudioInterval())
                                && audioSegment.getAudioInterval() > 0) {
                            recordSilence(recorder, sampleRate, audioChannels, audioSegment.getAudioInterval());
                        }
                    }
                    audioSegment.setAudioBytes(null);
                }

                recorder.stop();
            }
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

    public static void audioFrame(InputStream in, OutputStream out, Double audioSpeed, Double audioVolume) throws Exception {
        FFmpegLogCallback.set();
        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(in)) {
            grabber.start();

            try (FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(out, grabber.getAudioChannels())) {
                recorder.setAudioCodec(grabber.getAudioCodec());
                recorder.setSampleRate(grabber.getSampleRate());
                recorder.setAudioChannels(grabber.getAudioChannels());
                recorder.setAudioBitrate(grabber.getAudioBitrate());
                recorder.setFormat("wav");
                recorder.start();

                String filterString = String.format("atempo=%.1f,volume=%.1f", audioSpeed, audioVolume);

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
        }
    }
}