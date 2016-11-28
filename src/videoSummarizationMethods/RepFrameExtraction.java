package videoSummarizationMethods;

import com.sun.image.codec.jpeg.ImageFormatException;
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
    private ArrayList<XImage> extraFrames;
    private int NUMFRAMES = 3; //expected N'= # of original frames * % of video you want to keep;
                            // how many representative frames we want
                            //actual value can be <= NUMFRAMES
                            //manually set in paper, researchers looked at video to count content changes which became basis for #
    private int LENGTH = 3; //L = usually 3 for short videos I think; # of frames per unit
    private double CRATIO = 0.3; //given through experiments by researchers

    public RepFrameExtraction(ArrayList<XImage> images){
        this.images = images;
    }

    public ArrayList<XImage> getRepresentativeFrames(){
        //First Run
        findRepresentativeFrames(images);
        while(!isDesiredNumberFrames()){ //Loop
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

    private void partitionVideo(ArrayList<XImage> videoInput){
        units = new ArrayList<>();
        extraFrames = new ArrayList<>();
        int index = 0;
        while(index<videoInput.size()){
            Unit u = new Unit();
            for(int i = 0; i<LENGTH; i++){
                if(index < videoInput.size()){
                    u.addFrame(videoInput.get(index));
                }
                index++;
            }
            if(u.getNumFrames()==LENGTH){
                units.add(u);
            }else{
                extraFrames.addAll(u.getAllFrames());
            }
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
        int smallClusterSize = (int) Math.round((CRATIO * units.size())+0.5);
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
            if(!isExtracted(u.getFirstFrame())){
                extractedFrames.add(u.getFirstFrame());
            }
            if(!isExtracted(u.getLastFrame())){
                extractedFrames.add(u.getLastFrame());
            }
        }
        for(Unit u : largeCluster){
            extractedFrames.addAll(u.getAllFrames());
        }
        extractedFrames.addAll(extraFrames);
    }

    private boolean isExtracted(XImage image){
        if(extractedFrames.contains(image)){
            return true;
        }else{
            return false;
        }
    }

    private boolean isDesiredNumberFrames(){
        if(extractedFrames.size()<=NUMFRAMES){
            return true;
        } else{
            return false;
        }
    }



}
