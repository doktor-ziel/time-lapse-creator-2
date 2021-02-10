package pl.backlog.green;

import java.util.concurrent.Callable;
import java.util.stream.Stream;

import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Size;
import picocli.CommandLine;

import static org.bytedeco.opencv.global.opencv_imgproc.resize;

@CommandLine.Command(
        name = "timelapse.sh",
        mixinStandardHelpOptions = true,
        version = "2.0",
        description = "Simple program to generate timelapse"
)
public class App implements Callable<Integer> {

    @CommandLine.Option(
            names = {"-i", "--input"},
            required = true,
            paramLabel = "DIR",
            description = "path to input directory"
    )
    private Stream<Mat> input;

    @CommandLine.Option(
            names = {"-o", "--output"},
            required = true,
            paramLabel = "FILE",
            description = "path to output video file"
    )
    private Recorder recorder;

    @CommandLine.Option(
            names = {"-w", "--watermark"},
            required = false,
            paramLabel = "FILE",
            description = "path to watermark image",
            defaultValue = ""
    )
    private String watermarkPath;

    @CommandLine.Option(
            names = {"-f", "--fps"},
            required = true,
            paramLabel = "FLOAT",
            description = "FPS in output video"
    )
    private double fps;

    @CommandLine.Option(
            names = {"-s", "--scale"},
            required = false,
            paramLabel = "FLOAT",
            description = "how much output video needs to be scaled",
            defaultValue = "1.0"
    )
    private double scale;

    @CommandLine.Option(
            names = {"-r", "--rotate"},
            required = false,
            paramLabel = "DEGREES",
            description = "angle for rotation",
            defaultValue = "0.0"
    )
    private double angle;

    WatermarkAdder adder = null;
    ImageRotator rotator = null;

    private Mat transformOneFrame(Mat frame) {
        if (scale < 1) {
            Size oldSize = frame.size();
            Size newSize = new Size((int)(oldSize.width() * scale), (int)(oldSize.height()*scale));
            resize(frame, frame, newSize);
        }
        if (adder != null) {
            frame = adder.apply(frame);
        }
        if (rotator != null) {
            frame = rotator.apply(frame);
        }
        return frame;
    }

    @Override
    public Integer call() throws Exception {
        if (!watermarkPath.isEmpty()) {
            adder = WatermarkAdder.create(watermarkPath).addResizer(80);
        }
        if (angle != 0.0) {
            rotator = ImageRotator.creatorRotator().setAngle(angle);
        }
        input.map(this::transformOneFrame)
                .map(Utils.getConverter()::convert)
                .collect(recorder.setFps(fps));
        return 0;
    }

    public static void main(String[] args) {

        CommandLine commandLine = new CommandLine(new App());
        commandLine.registerConverter(Recorder.class, Utils::createRecorder);
        commandLine.registerConverter(Reader.class, Reader::new);
        commandLine.registerConverter(Mat.class, Utils::readImage);
        commandLine.registerConverter(Stream.class, Utils::readStream);

        int exitCode = commandLine.execute(args);
        System.exit(exitCode);
    }
}

