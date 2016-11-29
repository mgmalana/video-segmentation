package videoSummarizationMethods;

import com.sun.image.codec.jpeg.ImageFormatException;
import model.XImage;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by Kate on 11/29/2016.
 */
public class RSequenceGeneration {

    private ArrayList<XImage> representativeFrames;
    private ArrayList<XImage> images;

    public RSequenceGeneration(File selectedDirectory){
        loadVideo(selectedDirectory);
    }

    public XImage[] generateSequence(int smoothingFactor, int numFrames){
        RepFrameExtraction rfe = new RepFrameExtraction(images, numFrames);
        representativeFrames = rfe.getRepresentativeFrames();
        sortFileName();

        Set<XImage> newVideo = new LinkedHashSet<>();
        for(XImage rFrame : representativeFrames){
            int index = images.indexOf(rFrame);
            for(int i = 0; i<smoothingFactor; i++){
                if(index+i <images.size()){
                    newVideo.add(images.get(index+i));
                }
            }
        }

        return newVideo.toArray(new XImage[newVideo.size()]);
    }

    private void loadVideo(File selectedDirectory){
        //traverse files of the selectedDirectory. if image. save the image
        images =  new ArrayList<>();

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
    }

    private void sortFileName() { //put frames in chronological order based on filenames
        Collections.sort(representativeFrames, new Comparator<XImage>() {
            @Override
            public int compare(XImage o1, XImage o2) {
                return ((o1.getFile().getName()).compareToIgnoreCase((o2.getFile().getName())));
            }
        });
    }



}
