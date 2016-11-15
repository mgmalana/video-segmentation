package videoSegmentationMethods;

import model.XImage;

import java.util.Arrays;
import java.util.LinkedList;

/**
 * Created by mgmalana on 10/11/2016.
 */
public class TwinComparison {
    public static final int NUM_COLORS = 64;
    private static final int NUM_GRID = 4; //4x4

    public XImage[][] getSegmentedImages(XImage[] images, int hThreshold, int lThreshold, int tolerance){ //[x][y] x is segment, y is image index
        XImage[][] segmentedImages;
        LinkedList<Integer> cuts = new LinkedList<>();
        boolean isPossibleTransition = false;
        int cumulative = 0;
        int toleranceCount = 0;

        for (int i = 0; i < images.length; i++) {
            if(isPossibleTransition){
                if (images[i].getDistance() > lThreshold){
                    cumulative+= images[i].getDistance();
                } else {
                    if (cumulative > hThreshold){
                        toleranceCount = 0;
                        cumulative = 0;
                        isPossibleTransition = false;
                        cuts.add(i); //adds the next scene //TODO: idk if kelangan icut off yung transition
                    } else if(toleranceCount > tolerance){
                        toleranceCount = 0;
                        cumulative = 0;
                        isPossibleTransition = false;
                        cuts.removeLast(); //remove the last int added
                    } else {
                        //TODO: ask kate if kelangan iadd sya sa cumulative
                        tolerance++;
                    }
                }
            } else {
                if (images[i].getDistance() > hThreshold){
                    cuts.add(i);
                } else if (images[i].getDistance() > lThreshold){
                    cuts.add(i);
                    isPossibleTransition = true;
                    cumulative = images[i].getDistance();
                }
            }


        }
        //TODO: check check check this part

        segmentedImages = new XImage[cuts.size() + 1][];

        int prevCut = 0;
        for (int i = 0; i < segmentedImages.length - 1; i++) {
            int nextCut = cuts.removeFirst();
            segmentedImages[i] = new XImage[nextCut - prevCut];

            System.arraycopy(images, prevCut, segmentedImages[i], 0, segmentedImages[i].length);
            prevCut = nextCut;
        }

        int lastIndex = segmentedImages.length - 1;
        segmentedImages[lastIndex] = new XImage[images.length - prevCut];

        for (int i = 0; i < segmentedImages[lastIndex].length; i++){
            System.arraycopy(images, prevCut, segmentedImages[lastIndex], 0, segmentedImages[lastIndex].length);
        }

        return segmentedImages;
    }

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
