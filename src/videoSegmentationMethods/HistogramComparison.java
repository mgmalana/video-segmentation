package videoSegmentationMethods;

import model.XImage;

import java.util.Arrays;

/**
 * Created by mgmalana on 10/11/2016.
 */
public class HistogramComparison {
    public static final int NUM_COLORS = 64;
    private static final int NUM_GRID = 4; //4x4

    public int getDistance(XImage firstImage, XImage nextImage){
        return getDistance(firstImage.getRGBMatrix(), nextImage.getRGBMatrix());
    }

    private int getDistance(int[][] firstImageMatrix, int[][] nextImageMatrix) {
        int[][] firstImageHistograms = getGridHistograms(firstImageMatrix);
        int[][] nextImageHistograms = getGridHistograms(nextImageMatrix);
        int[] distances = new int[firstImageHistograms.length];
        int distance = 0;

        for(int i = 0; i < firstImageHistograms.length; i++){
            distances[i] = getDistanceOfHistograms(firstImageHistograms[i], nextImageHistograms[i]);
        }

        Arrays.sort(distances);

        for(int i = NUM_GRID; i < distances.length - NUM_GRID; i++) { //only gets the median distances
            distance += distances[i];
        }

        return distance;
    }

    private int getDistanceOfHistograms(int[] firstImageHistogram, int[] nextImageHistogram) {
        int distance = 0;
        for (int i = 0; i < NUM_COLORS; i++){
            distance+= Math.abs(firstImageHistogram[i] - nextImageHistogram[i]);
        }

        return distance;
    }

    private int[][] getGridHistograms(int[][] matrix) {
        int[][] imageGrid = new int[NUM_GRID * NUM_GRID][]; //int[x][y] = x is number of grid, y is histogram color
        int width = matrix.length;
        int height = matrix[0].length;

        for (int i = 0; i < NUM_GRID; i++){
            for (int j = 0; j < NUM_GRID; j++){
                imageGrid[i * NUM_GRID + j] = getHistogram(matrix,
                        (int)((1.0*i / NUM_GRID) * width), (int)((1.0*j / NUM_GRID) * height),
                        (int)(((1.0*i + 1)/ NUM_GRID) * width), (int)(((1.0*j + 1)/ NUM_GRID) * height));
            }
        }

        return imageGrid;
    }

    private int[] getHistogram(int[][] rgbMatrix, int x1, int y1, int x2, int y2) {
        int[] histogramMap = new int[NUM_COLORS];

        for (int i = x1; i < x2; i++){
            for (int j = y1; j < y2; j++){
                histogramMap[rgbMatrix[i][j]]++;
            }
        }

        return histogramMap;
    }
}
