package main;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.chart.Axis;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.input.MouseEvent;
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


        for(int i = 0; i < series.getData().size(); i++){
            int finalX = i;
            series.getData().get(i).getNode().setOnMouseMoved(new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent event) {
                    System.out.println("x: " + finalX);
                }
            });
        }

    }

//    public void onMouseMoved(MouseEvent mouseEvent) {
//        Point2D pointInScene = new Point2D(mouseEvent.getSceneX(), mouseEvent.getSceneY());
//        Axis<Number> xAxis = barChart.getXAxis();
//        Axis<Number> yAxis = barChart.getYAxis();
//        double xPosInAxis = xAxis.sceneToLocal(new Point2D(pointInScene.getX(), 0)).getX();
//        double yPosInAxis = yAxis.sceneToLocal(new Point2D(0, pointInScene.getY())).getY();
//
//        System.out.println("X: "+xAxis.getValueForDisplay(xPosInAxis)+", Y:"+ yAxis.getValueForDisplay(yPosInAxis));
//    }
}
