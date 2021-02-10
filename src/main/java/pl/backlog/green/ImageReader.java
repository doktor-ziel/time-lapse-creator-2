package pl.backlog.green;

import org.bytedeco.opencv.opencv_core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.util.function.Function;

import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;

public class ImageReader implements Function<String, Mat> {

    private final int mode;

    public ImageReader() {
        this(Imgcodecs.IMREAD_UNCHANGED);
    }

    public ImageReader(int mode) {
        this.mode = mode;
    }

    @Override
    public Mat apply(String path) {
        return imread(path, mode);
    }
}
