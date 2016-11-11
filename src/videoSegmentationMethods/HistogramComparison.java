package videoSegmentationMethods;

import model.Image;

/**
 * Created by mgmalana on 10/11/2016.
 */
public class HistogramComparison {
    public static final int NUM_COLORS = 64;

    public int getDistance(Image firstImage, Image nextImage){
        int distance = 0;
        int[] firstHistogram = getHistogram(firstImage.getRGBMatrix());
        int[] nextHistogram = getHistogram(nextImage.getRGBMatrix());

        for (int i = 0; i < NUM_COLORS; i++){
            distance+= Math.abs(firstHistogram[i] - nextHistogram[i]);
        }

        return distance;
    }

    private int[] getHistogram(int[][] rgbMatrix) {
        int[] histogramMap = new int[NUM_COLORS];

        //traverses the matrix and increments the color counter
        for (int[] lArray: rgbMatrix) {
            for (int luv: lArray) {
                histogramMap[luv]++;
            }
        }
        return histogramMap;
    }
}
