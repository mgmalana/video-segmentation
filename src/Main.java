import main.VideoSegmentation;

import javax.swing.*;
import java.io.File;

/**
 * Created by mgmalana on 10/11/2016.
 */
public class Main {
    public static void main(String[] args) {
        VideoSegmentation vs = new VideoSegmentation();
        File selectedDirectory = initFileChooser();

        if(selectedDirectory != null){
            vs.videoSegment(selectedDirectory);
        }

    }

    public static File initFileChooser(){
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File("."));
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);


        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            System.out.println("getSelectedFile() : " + chooser.getSelectedFile());
        } else {
            System.out.println("No Selection ");
        }
        return chooser.getSelectedFile();
    }
}
