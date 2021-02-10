package pl.backlog.green;

import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Rect;

import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import static java.util.function.Predicate.not;
import static org.bytedeco.opencv.global.opencv_core.CV_8UC4;
import static org.bytedeco.opencv.global.opencv_core.addWeighted;

public class WatermarkAdder implements UnaryOperator<Mat> {
    private Mat watermark;
    private Mat transparentLayer;
    private double alpha = 1;
    private double beta = 0.4;
    private double gamma = 0.0;
    private String path;
    ImageResizer resizer;

    @Override
    public Mat apply(Mat image) {
        if (transparentLayer == null) {
            prepareTransparentLayer(image.rows(), image.cols());
        } else {
            assertTransparentLayerSize(image.rows(), image.cols());
        }
        addWeighted(image, alpha, transparentLayer, beta, gamma, image);
        return image;
    }

    private void prepareTransparentLayer(int rows, int cols) {
        watermark = Stream.of(path)
                .map(new ImageReader())
                .filter(not(Mat::empty))
                .map(resizer)
                .findFirst()
                .orElseThrow();
        transparentLayer = new Mat(rows, cols, CV_8UC4);
        Rect roi = new Rect(
                cols - watermark.cols()-5,
                rows - watermark.rows()-5,
                watermark.cols(),
                watermark.rows());
        watermark.copyTo(transparentLayer.apply(roi));
    }

    private void assertTransparentLayerSize(int rows, int cols) {
        if (rows != transparentLayer.rows() || cols != transparentLayer.cols()) {
            throw new IllegalArgumentException("Wrong size of input image");
        }
    }

    public static WatermarkAdder create(String s) {
        WatermarkAdder adder = new WatermarkAdder();
        adder.path = s;
        return adder;
    }

    public WatermarkAdder addResizer(int size) {
        resizer = new ImageResizer().setSize(size);
        return this;
    }
}
