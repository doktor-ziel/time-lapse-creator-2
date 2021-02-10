package pl.backlog.green;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;

import java.util.function.Supplier;

public class Reader implements Supplier<Frame> {

    private final FrameGrabber grabber;

    public Reader(String path) throws FrameGrabber.Exception {
        grabber = FFmpegFrameGrabber.createDefault(path);
        grabber.start();
    }

    @Override
    public Frame get() {
        try {
            return grabber.grab();
        } catch (FrameGrabber.Exception e) {
            return null;
        }
    }

    public void stop() throws FrameGrabber.Exception {
        grabber.stop();
    }

    public Recorder updateRecorder(Recorder recorder) {
        return recorder
                .setCodec(grabber.getVideoCodec())
                .setFps(grabber.getFrameRate());
    }
}
