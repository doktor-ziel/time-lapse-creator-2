package pl.backlog.green;

import org.bytedeco.javacpp.indexer.DoubleRawIndexer;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Size;

import java.util.function.UnaryOperator;

import static java.lang.StrictMath.*;

public class ImageRotator implements UnaryOperator<Mat> {
    private double angle;

    @Override
    public Mat apply(Mat image) {
        double h = image.rows();
        double w = image.cols();
        double t = toRadians(angle);
        double H = h*cos(t) + w*sin(t);
        double W = w*cos(t) + h*sin(t);

        Mat m = new Mat(2,3, opencv_core.CV_64F);
        DoubleRawIndexer indexer = m.createIndexer();
        indexer.put(0,0, cos(t)); indexer.put(0,1, -sin(t)); indexer.put(0,2, h*sin(t));
        indexer.put(1,0, sin(t)); indexer.put(1,1,cos(t)); indexer.put(1,2, w*sin(t)+h*cos(t)-H);

        opencv_imgproc.warpAffine(image, image, m, new Size((int)W, (int)H));

        double nH = (W*cos(t) - H*sin(t)) / cos(2*t);
        double nW = (H*cos(t) - W*sin(t)) / cos(2*t);
        // https://stackoverflow.com/questions/5789239/calculate-largest-rectangle-in-a-rotated-rectangle#7519376
        int x1 = (int) round(nW*sin(t)*cos(t));
        int y1 = (int) round(W*sin(t) - nW*sin(t)*sin(t));
        return image.rowRange(y1, (int)round(nW)).colRange(x1, (int)round(nH));
    }

    public static ImageRotator creatorRotator() {
        return new ImageRotator();
    }

    public ImageRotator setAngle(double angle) {
        this.angle = angle;
        return this;
    }
}
