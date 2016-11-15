package videoSegmentationMethods;

import model.XImage;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by mgmalana on 12/11/2016.
 */
public class TwinComparison {
    private ArrayList<Integer> transitions;

    public TwinComparison(){
        transitions = new ArrayList<>();
    }

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
                        transitions.add(cuts.size() - 1); //adds the index of transition in cuts
                        cuts.add(i); //adds the next scene
                    } else if(toleranceCount >= tolerance){
                        toleranceCount = 0;
                        cumulative = 0;
                        isPossibleTransition = false;
                        cuts.removeLast(); //remove the last int added
                    } else {
                        toleranceCount++;
                        //TODO: ask kate if kelangan iadd sya sa cumulative
//                        cumulative+= images[i].getDistance();
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

    public ArrayList<Integer> getTransitions() {
        return transitions;
    }
}
