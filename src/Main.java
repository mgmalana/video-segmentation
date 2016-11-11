import model.Image;
import com.sun.image.codec.jpeg.ImageFormatException;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

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
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);


        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            System.out.println("getSelectedFile() : " + chooser.getSelectedFile());
        } else {
            System.out.println("No Selection ");
        }
        return chooser.getSelectedFile();
    }
}
