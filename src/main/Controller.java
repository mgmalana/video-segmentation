package main;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.stage.DirectoryChooser;
import model.Image;

import java.io.File;

public class Controller {
    public Label directoryLabel;
    public SplitPane segmentButtonWrap;
    public Button segmentButton;

    public BarChart<Number, Number> barChart;

    private File selectedDirectory;

    public void openFileChooser(ActionEvent actionEvent) {
        selectedDirectory = initFileChooser();

        if(selectedDirectory != null){
            directoryLabel.setText(selectedDirectory.getName());
            segmentButtonWrap.setTooltip(null);
            segmentButton.setDisable(false);
//
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
        Image[] images = vs.videoSegment(selectedDirectory);

        addImagesToBarChart(images);
    }

    private void addImagesToBarChart(Image[] images) {
        XYChart.Series <Number, Number> series = new XYChart.Series();
        ObservableList data= series.getData();

        for (int i = 0; i < images.length; i++) {
            data.add(new XYChart.Data(i + "", images[i].getDistance()));
        }

        barChart.getData().add(series);
    }


}
