package model;

import videoSegmentationMethods.HistogramComparison;

import java.util.ArrayList;

/**
 * Created by Kate on 11/28/2016.
 */
public class Unit {

    private ArrayList<XImage> images;
    private HistogramComparison hc;
    private double unitChange;

    public Unit(){
        images = new ArrayList<>();
        hc = new HistogramComparison();
    }

    public void computeUnitChange(){
         unitChange = hc.getDistance(images.get(0), images.get(images.size()-1));
    }

    public void addFrame(XImage frame){
        images.add(frame);
    }

    public double getUnitChange(){
        return unitChange;
    }

    public XImage getFirstFrame(){
        return images.get(0);
    }

    public XImage getLastFrame(){
        return images.get(images.size()-1);
    }

    public ArrayList<XImage> getAllFrames(){
        return images;
    }

    public int getNumFrames(){
        return images.size();
    }

}
