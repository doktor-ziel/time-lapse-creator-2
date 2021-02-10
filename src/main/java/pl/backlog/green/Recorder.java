package pl.backlog.green;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameRecorder;

import java.util.Collections;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class Recorder implements Collector<Frame, FrameRecorder, FrameRecorder> {
    private String path;
    private double fps;
    private int codec = avcodec.AV_CODEC_ID_MPEG4;
    private double quality = 0;
    private int width = 1920;
    private int height = 1280;
    private boolean isSizeSet = false;

    public Recorder(String path) {
        this.path = path;
    }

    @Override
    public Supplier<FrameRecorder> supplier() {
        return () -> {
            try {
                FrameRecorder recorder = FrameRecorder.createDefault(path, width, height);
                recorder.setVideoCodec(codec);
                recorder.setFrameRate(fps);
                recorder.setVideoQuality(0);
                return recorder;
            } catch (FrameRecorder.Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Error during creation recorder", e);
            }
        };
    }

    @Override
    public BiConsumer<FrameRecorder, Frame> accumulator() {
        return (recorder, frame) -> {
            try {
                if (!isSizeSet) {
                    isSizeSet = true;
                    width = frame.imageWidth;
                    height = frame.imageHeight;
                    recorder.setImageHeight(height);
                    recorder.setImageWidth(width);
                    recorder.start();
                }
                recorder.record(frame);
            } catch (FrameRecorder.Exception e) {
                throw new RuntimeException("Error during recording a frame", e);
            }
        };
    }

    @Override
    public BinaryOperator<FrameRecorder> combiner() {
        return (f1, f2) -> f1;
    }

    @Override
    public Function<FrameRecorder, FrameRecorder> finisher() {
        return f -> {
            try {
                f.stop();
                return f;
            } catch (FrameRecorder.Exception e) {
                throw new RuntimeException("Error during closing recorder", e);
            }
        };
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Collections.emptySet();
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Recorder setFps(double fps) {
        this.fps = fps;
        return this;
    }

    public Recorder setCodec(int codec) {
        this.codec = codec;
        return this;
    }

    public void setQuality(double quality) {
        this.quality = quality;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setSizeSet(boolean sizeSet) {
        isSizeSet = sizeSet;
    }
}
