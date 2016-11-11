import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

/**
 * Created by mgmalana on 10/11/2016.
 */
public class Main {
    public static void main(String[] args) {
        Main main = new Main();
        File selectedFile = main.initFileChooser();

        if(selectedFile != null){
            main.videoSegment(selectedFile);
        }

    }

    public File initFileChooser(){
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

    public void videoSegment(File selectedDirectory){
        
    }
}
