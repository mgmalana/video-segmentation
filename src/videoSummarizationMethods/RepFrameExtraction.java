package videoSummarizationMethods;

import com.sun.image.codec.jpeg.ImageFormatException;
import com.sun.tools.javac.code.Attribute;
import model.Unit;
import model.XImage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Kate on 11/28/2016.
 */
public class RepFrameExtraction {

    private ArrayList<XImage> images;
    private ArrayList<Unit> units;
    private ArrayList<Unit> smallCluster;
    private ArrayList<Unit> largeCluster;
    private ArrayList<XImage> extractedFrames;
    private int NUMFRAMES = 5; //N' can be changed
    private int LENGTH = 3; //L = usually 3 for short videos I think
    private double CRATIO = 0.5; //not sure what it should be but it's 0<r<1

    public RepFrameExtraction(File selectedDirectory){
        loadVideo(selectedDirectory);
    }

    public ArrayList<XImage> getRepresentativeFrames(){
        //First Run
        findRepresentativeFrames(images);
        while(!isDesiredNumberFrames()){
            findRepresentativeFrames(extractedFrames);
        }
        return extractedFrames;
    }

    private void findRepresentativeFrames(ArrayList<XImage> input){
        partitionVideo(input);
        computeChanges();
        sortUnits();
        makeSmallLargeClusters();
        extractFrames();
    }

    public void loadVideo(File selectedDirectory){
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

    private void partitionVideo(ArrayList<XImage> videoInput){
        units = new ArrayList<>();
        int index = 0;
        while(index!=videoInput.size()-1){
            Unit u = new Unit();
            for(int i = 0; i<LENGTH; i++){
                u.addFrame(videoInput.get(index));
                index++;
            }
            units.add(u);
        }
    }

    private void computeChanges(){
        for(Unit u : units){
            u.computeUnitChange();
        }
    }

    private void sortUnits(){
        Collections.sort(units, new Comparator<Unit>() {
            @Override
            public int compare(Unit o1, Unit o2) {
                if(o1.getUnitChange() < o2.getUnitChange()){
                    return -1;
                } else if(o1.getUnitChange() > o2.getUnitChange()){
                    return 1;
                } else{
                    return 0;
                }
            }
        });
    }

    private void makeSmallLargeClusters(){
        int smallClusterSize = (int) (CRATIO * units.size());
        //int largeClusterSize = units.size() - smallClusterSize;

        smallCluster = new ArrayList<>();
        largeCluster = new ArrayList<>();

        for(int i = 0; i<smallClusterSize; i++){
            smallCluster.add(units.get(i));
        }

        for(int i = smallClusterSize; i<units.size(); i++){
            largeCluster.add(units.get(i));
        }
    }

    private void extractFrames(){
        extractedFrames = new ArrayList<>();
        for(Unit u : smallCluster){
            extractedFrames.add(u.getFirstFrame());
            extractedFrames.add(u.getLastFrame());
        }
        for(Unit u : largeCluster){
            extractedFrames.addAll(u.getAllFrames());
        }
    }

    private boolean isDesiredNumberFrames(){
        if(extractedFrames.size()>=NUMFRAMES){
            return true;
        } else{
            return false;
        }
    }



}
