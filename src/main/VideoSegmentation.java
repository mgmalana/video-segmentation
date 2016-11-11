package main;

import com.sun.image.codec.jpeg.ImageFormatException;
import model.XImage;
import videoSegmentationMethods.HistogramComparison;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by mgmalana on 11/11/2016.
 */
public class VideoSegmentation {
    //segment video
    public XImage[] videoSegment(File selectedDirectory){
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
            System.out.println("Distance of " + images.get(i).getFile().getName() +
                    " to " + images.get(i + 1).getFile().getName() + " is: " + distance);
        }

        System.out.println("Done segmenting");

        return images.toArray(new XImage[images.size()]);
    }
}
