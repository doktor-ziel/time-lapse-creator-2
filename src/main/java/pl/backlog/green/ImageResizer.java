package pl.backlog.green;

import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Size;

import java.util.function.UnaryOperator;

import static org.bytedeco.opencv.global.opencv_imgproc.resize;

public class ImageResizer implements UnaryOperator<Mat> {
    private int expectedSize;

    @Override
    public Mat apply(Mat image) {
        Size newSize = Utils.calculateNewSize(image, expectedSize);
        resize(image, image, newSize);
        return image;
    }

    public ImageResizer setSize(int s) {
        expectedSize = s;
        return this;
    }
}
