package pl.backlog.green;

import org.bytedeco.opencv.opencv_core.Mat;

import java.util.function.UnaryOperator;

import static org.bytedeco.opencv.global.opencv_core.flip;
import static org.bytedeco.opencv.global.opencv_core.transpose;

public class ImageFlipper implements UnaryOperator<Mat> {

    private Side side;

    public ImageFlipper(Side side) {
        this.side = side;
    }

    @Override
    public Mat apply(Mat image) {
        if (side == Side.LEFT || side == Side.COUNTER_CLOCKWISE) {
            transpose(image, image);
            flip(image, image, 0);
        } else {
            transpose(image, image);
            flip(image, image, 1);
        }
        return image;
    }

    public enum Side {
        NONE, LEFT, RIGHT, CLOCKWISE, COUNTER_CLOCKWISE;
    }
}
