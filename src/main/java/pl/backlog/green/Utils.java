package pl.backlog.green;

import org.bytedeco.javacv.FrameRecorder;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Size;
import picocli.CommandLine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class Utils {
    public static Size calculateNewSize(Mat image, int expectedSize) {
        Size oldSize = image.size();
        double biggerDimension = oldSize.height() > oldSize.width() ? oldSize.height() : oldSize.width();
        if (biggerDimension < expectedSize) {
            throw new IllegalArgumentException("This call is about to scale image up!");
        }
        double scale = expectedSize / biggerDimension;
        return new Size((int)(scale*oldSize.width()),(int)(scale*oldSize.height()));
    }

    private static OpenCVFrameConverter.ToMat converter = null;

    public static OpenCVFrameConverter.ToMat getConverter() {
        if (converter == null) {
            converter = new OpenCVFrameConverter.ToMat();
        }
        return converter;
    }

    public static Stream<Mat> readStream(String path) throws IOException {
        Path p = Paths.get(path);
        if (! Files.exists(p)) {
            throw new CommandLine.PicocliException("Wrong path - I cannot read image(s)");
        }
        if (Files.isDirectory(p)) {
            return Files.list(p)
                    .map(Path::toString)
                    .map(new ImageReader());
        } else {
            return Stream.generate(() -> opencv_imgcodecs.imread(p.toString()));
        }
    }

    public static Recorder createRecorder(String path) throws FrameRecorder.Exception {
        return new Recorder(path);
    }

    public static Mat readImage(String path) {
        Path p = Paths.get(path);
        if (! Files.exists(p)) {
            throw new CommandLine.PicocliException("Wrong path - I cannot read image(s)");
        }
        return new ImageReader().apply(p.toString());
    }

    public static Size parseSize(String value) {
        String[] arr = value.split(":");
        return new Size(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]));
    }
}
