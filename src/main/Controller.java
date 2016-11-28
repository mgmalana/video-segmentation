package main;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import model.XImage;
import videoSummarizationMethods.RSequenceGeneration;
import videoSummarizationMethods.RepFrameExtraction;

import java.io.File;
import java.util.ArrayList;

public class Controller {
    public Label directoryLabel;
    public SplitPane segmentButtonWrap;
    public SplitPane summarizeButtonWrap;
    public Button summarizeButton;
    public Button segmentButton;
    public BarChart<Number, Number> barChart;
    public ImageView imageView;
    public ImageView keyImageView;

    private File selectedDirectory;
    private Image[] keyframes;

    public void openFileChooser(ActionEvent actionEvent) {
        selectedDirectory = initFileChooser();

        if(selectedDirectory != null){
            directoryLabel.setText(selectedDirectory.getName());
            segmentButtonWrap.setTooltip(null);
            segmentButton.setDisable(false);
            summarizeButtonWrap.setTooltip(null);
            summarizeButton.setDisable(false);
        }
    }

    public File initFileChooser(){
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setInitialDirectory(new File("videos/"));

        File file = chooser.showDialog(null);
        if (file != null) {
            System.out.println("getSelectedFile() : " + file);
        } else {
            System.out.println("No Selection ");
        }
        return file;
    }

    public void segment(ActionEvent actionEvent) {
        VideoSegmentation vs = new VideoSegmentation();
        XImage[][] images = vs.videoSegment(selectedDirectory);
        XImage[] keyFrames = vs.getKeyFrames(images);
        ArrayList <Integer> transitions = vs.getTransitions();

        this.keyframes = new Image[keyFrames.length];

        for (int i = 0; i < keyFrames.length ; i++) { //get images
            if (!transitions.contains(i)){
                this.keyframes[i] = new Image(keyFrames[i].getFile().toURI().toString());
            }
        }

        barChart.getData().clear();
        addImagesToBarChart(images);
    }

    public void summarize(ActionEvent actionEvent){
        RSequenceGeneration rsg = new RSequenceGeneration(selectedDirectory);
        ArrayList<XImage> newVideo = rsg.generateSequence(10); //smoothing factor as param should be input
        for(XImage img : newVideo){
            System.out.println(img.getFile().getName());
        }

    }

    private void addImagesToBarChart(XImage[][] images) {
        int indexArray = 0;
        int seriesIndex = 0;

        for (XImage[] imageArray: images) {
            XYChart.Series <Number, Number> series = new XYChart.Series();
            ObservableList data= series.getData();

            for (int i = 0; i < imageArray.length; i++) {
                data.add(new XYChart.Data(i+indexArray + "", imageArray[i].getDistance()));
            }

            barChart.getData().add(series);
            for(int i = 0; i < series.getData().size(); i++){ //for indiv image
                int finalX = i;
                int finalSeriesIndex = seriesIndex;
                series.getData().get(i).getNode().setOnMouseEntered(new EventHandler<MouseEvent>() {

                    Image image = new Image(imageArray[finalX].getFile().toURI().toString());

                    @Override
                    public void handle(MouseEvent event) {
                        //System.out.println("x: " + finalX + " series " + finalSeriesIndex);
                        keyImageView.setImage(keyframes[finalSeriesIndex]);
                        imageView.setImage(image);

                    }
                });
            }
            indexArray += imageArray.length;//TODO: check if tama
            seriesIndex++;
        }
    }
}
