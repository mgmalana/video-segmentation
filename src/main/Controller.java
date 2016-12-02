package main;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import model.XImage;
import videoSummarizationMethods.RSequenceGeneration;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
    public TextField repFramesTextField;
    public TextField sFactorTextField;
    public TextField lThresholdTextField;

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
        //chooser.setInitialDirectory(new File("videos/"));

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
        int lThreshold = 2250; //avg threshold of three videos (only used just in case no input was given; should be manually input)
        if(!lThresholdTextField.getText().isEmpty()){
            lThreshold = Integer.parseInt(lThresholdTextField.getText());
        } else {
            lThresholdTextField.setText(lThreshold+"");
        }
        XImage[][] images = vs.videoSegment(selectedDirectory, lThreshold);
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
        int smoothingFactor = 10;
        int repFrames = 10; //default, but it really should be manually set
        if(!repFramesTextField.getText().isEmpty()){
            repFrames = Integer.parseInt(repFramesTextField.getText());
        } else {
            repFramesTextField.setText(repFrames+"");
        }
        if(!sFactorTextField.getText().isEmpty()){
            smoothingFactor = Integer.parseInt(sFactorTextField.getText());
        } else {
            sFactorTextField.setText(smoothingFactor+"");
        }
        XImage[] newVideo = rsg.generateSequence(smoothingFactor, repFrames); //smoothing factor and repFrames as param should be input

        generateHTML("Summary of the video", newVideo);
    }

    private void generateHTML(String title, XImage[] images){
        try{
            File file = new File(selectedDirectory.getName()+"_"+title.split(" ")[0]+".html");
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write("<html>");
            bw.write("<body>");
            bw.write("<h2>Query: "+selectedDirectory.getName()+"<h2><br>");
            bw.write("<h2>" +title+ "</h2>");
            for (XImage image: images){
                bw.write("<img src=\"" + image.getFile().getPath() + "\"</img>" +
                        image.getFile().getName() +
                        "<hr>");
                //System.out.print(f.getName() +", ");
            }
            bw.write("</body>");
            bw.write("</html>");
            bw.close();

            Desktop.getDesktop().browse(file.toURI());
        } catch (IOException e){
            e.printStackTrace();
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
                        System.out.println("x: " + finalX + " series " + finalSeriesIndex);
                        keyImageView.setImage(keyframes[finalSeriesIndex]);
                        imageView.setImage(image);

                    }
                });
            }
            indexArray += imageArray.length;
            seriesIndex++;
        }
    }
}
