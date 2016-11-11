import com.sun.image.codec.jpeg.ImageFormatException;
import model.Image;
import videoSegmentationMethods.HistogramComparison;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by mgmalana on 11/11/2016.
 */
public class VideoSegmentation {
    //segment video
    public void videoSegment(File selectedDirectory){
        //traverse files of the selectedDirectory. if image. save the image
        ArrayList<Image> images =  new ArrayList<>();
        HistogramComparison hc = new HistogramComparison();

        //adds all the images to the array.
        for (File file : selectedDirectory.listFiles()) {
            try {
                images.add(new Image(file));
            }catch (ImageFormatException e) {
                System.err.println(file + " is not a JPEG file");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for(int i = 0; i < images.size() - 1; i++){
            System.out.println("Distance of " + images.get(i).getFile().getName() +
                    " to " + images.get(i + 1).getFile().getName() + " is: " +
                    hc.getDistance(images.get(i), images.get(i + 1)));


        }
    }
}
