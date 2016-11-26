package main;

import com.sun.image.codec.jpeg.ImageFormatException;
import model.XImage;
import videoSegmentationMethods.AverageHistogramTechnique;
import videoSegmentationMethods.HistogramComparison;
import videoSegmentationMethods.TwinComparison;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by mgmalana on 11/11/2016.
 */
public class VideoSegmentation {

    //TODO: set this pa. idk how to get this
    private int ALPHA = 5; //alpha for threshold computation (5 or 6)
    private int H_THRESHOLD; //tb
    private int L_THRESHOLD = 275; //ts (8 to 10)
    private int NUM_TOLERANCE = 2; //tolerance (2 or 3)

    private TwinComparison tc;

    public VideoSegmentation(){
        tc = new TwinComparison();
    }

    //segment video
    public XImage[][] videoSegment(File selectedDirectory){
        //traverse files of the selectedDirectory. if image. save the image
        ArrayList<XImage> images =  new ArrayList<>();
        HistogramComparison hc = new HistogramComparison();

        //adds all the images to the array.
        for (File file : selectedDirectory.listFiles()) {
            try {
                images.add(new XImage(file));
            }catch (ImageFormatException e) {
                System.err.println(file + " is not a JPEG file");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for(int i = 0; i < images.size() - 1; i++){
            int distance = hc.getDistance(images.get(i), images.get(i + 1));
            images.get(i).setDistance(distance);
           // System.out.println("Distance of " + images.get(i).getFile().getName() +
             //       " to " + images.get(i + 1).getFile().getName() + " is: " + distance);
            System.out.println(distance);
        }

        System.out.println("Done segmenting");
        computeThresholds(images);
        return tc.getSegmentedImages(images.toArray(new XImage[images.size()]), H_THRESHOLD, L_THRESHOLD, NUM_TOLERANCE);
    }

    public XImage[] getKeyFrames(XImage[][] segmentedVideo){
        AverageHistogramTechnique ah = new AverageHistogramTechnique();
        return ah.getKeyFrames(segmentedVideo);
    }

    public ArrayList<Integer> getTransitions(){
        return tc.getTransitions();
    }

    private void computeThresholds(ArrayList<XImage> images){
        double mean;
        double stddev;
        double sumForMean = 0;
        double sumForStddev = 0;

        //get mean
        for(int i = 0; i<images.size()-1; i++){
            sumForMean+=images.get(i).getDistance();
        }
        mean = sumForMean/images.size()-1;

        //stddev of sample (population is n-1 bc we don't get the difference of the last one)
        for(int i = 0; i<images.size()-1;i++){
            sumForStddev+=Math.pow((images.get(i).getDistance() - mean), 2);
        }
        stddev=Math.sqrt(sumForStddev/images.size()-1);

        H_THRESHOLD =  new Double(mean + (ALPHA * stddev)).intValue();

        System.out.println("tb: " + H_THRESHOLD);

    }


}
