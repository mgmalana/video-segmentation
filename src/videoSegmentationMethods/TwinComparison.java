package videoSegmentationMethods;

import model.XImage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
        int transitionStart = -1;
        int[] frameIndex = new int[images.length]; //1 if abrupt, 4 if transition
        //mark abrupt camera breaks first
        for(int i = 1; i < images.length; i++) {
            if (images[i].getDistance() > lThreshold) {
                if (images[i].getDistance() > hThreshold) { //if abrupt break
                    frameIndex[i] = 1;
                    if (transitionStart != -1 && cumulative > hThreshold) {    //if in middle of possible transition and abrupt break
                        frameIndex[transitionStart] = 4;
                        cumulative = 0;
                        transitionStart = -1;
                        toleranceCount = 0;
                    }
                } else if (images[i].getDistance() <= hThreshold) { //if possible transition
                        if (transitionStart == -1) {
                            transitionStart = i;
                        }
                        cumulative += images[i].getDistance();
                }
            } else if (images[i].getDistance() <= lThreshold) { //if it cuts into transition
                if(toleranceCount < tolerance){
                    cumulative += images[i].getDistance();
                    toleranceCount++;
                } else if (transitionStart != -1 && cumulative > hThreshold) {    //if is a transition
                    frameIndex[transitionStart] = 4;
                    cumulative = 0;
                    transitionStart = -1;
                    toleranceCount = 0;
                } else if((transitionStart != -1 && cumulative <= hThreshold)
                            || toleranceCount >= tolerance){                    //if not a transition
                    cumulative = 0;
                    transitionStart = -1;
                    toleranceCount = 0;

                }
            }

        }

        for(int i = 0; i<frameIndex.length; i++){
            if(frameIndex[i] == 1){
                System.out.println("CUT: " + i);
                cuts.add(i);
            }
            if(frameIndex[i] == 4){
                System.out.println("TRANSITION: " + i);
                cuts.add(i);
                transitions.add(cuts.size()-1); //remove this part if we want keyframes for all, including transitions
            }
        }

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
