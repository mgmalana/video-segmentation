package videoSegmentationMethods;

import main.VideoSegmentation;
import model.XImage;

/**
 * Created by mgmalana on 15/11/2016.
 */
public class AverageHistogramTechnique {

    public XImage[] getKeyFrames(XImage[][] segmentedVideo) {
        HistogramComparison hc = new HistogramComparison();
        XImage[] keyframes = new XImage[segmentedVideo.length];

        for (XImage[] row:segmentedVideo) { //sets the histogram
            for (XImage image :row) {
                image.setHistogram(hc.getHistogram(image.getRGBMatrix()));
            }
        }

        for (int i = 0; i < segmentedVideo.length; i++) {
            keyframes[i] = getKeyFrame(segmentedVideo[i], hc);
        }

        return keyframes;
    }

    private XImage getKeyFrame(XImage[] xImages, HistogramComparison hc) {
        int[] sumHistogram = new int[HistogramComparison.NUM_COLORS];
        XImage keyFrame = xImages[0]; //default
        int distance = Integer.MAX_VALUE;

        for (XImage image: xImages) { //sum all the histogram values to the sumHistogram
            int[] imageHistogram = image.getHistogram();

            for (int i = 0; i < sumHistogram.length; i++) {
                sumHistogram[i] += imageHistogram[i];
            }
        }

        for (int i = 0; i < sumHistogram.length; i++) {
            sumHistogram[i] /= xImages.length;
        }

        for (XImage image: xImages) {
            int currentDistance = hc.getDistanceOfHistograms(sumHistogram, image.getHistogram());
            if (distance > currentDistance){
                distance = currentDistance;
                keyFrame = image;
            }
        }

        return keyFrame;
    }
}
