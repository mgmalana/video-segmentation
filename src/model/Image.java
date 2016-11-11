package model;

import com.sun.image.codec.jpeg.ImageFormatException;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageDecoder;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by mgmalana on 30/10/2016.
 */
public class Image {
    private int[][] RGBMatrix; //[x][y]
    private File file;

    public Image(File file) throws IOException, ImageFormatException {
        this.file = file;

        FileInputStream in = new FileInputStream(file);

        // decodes the JPEG data stream into a BufferedImage

        JPEGImageDecoder decoder = JPEGCodec.createJPEGDecoder(in);
        BufferedImage bufferedImage = decoder.decodeAsBufferedImage();

        RGBMatrix = new int[bufferedImage.getWidth()][bufferedImage.getHeight()];

        //saves the rgb values to the matrix
        for (int i = 0; i < RGBMatrix.length; i++){
            for (int j = 0; j < RGBMatrix[0].length; j++){
                ColorModel colorModel = bufferedImage.getColorModel();
                int rgbValue = bufferedImage.getRGB(i, j);

                RGBMatrix[i][j] = getCombinedRGBValue(colorModel.getRed(rgbValue), colorModel.getGreen(rgbValue), colorModel.getBlue(rgbValue));
            }
        }
    }

    //returns a combined value of rgb by getting the 2 most significant bit of each color
    public int getCombinedRGBValue(int red, int green, int blue) {
        int value = 0;

        //add red bits to the value
        value+= getBit(red, 7) * 32;
        value+= getBit(red, 6) * 16;

        //add green bits to the value
        value+= getBit(green, 7) * 8;
        value+= getBit(green, 6) * 4;

        //add blue bits to the value
        value+= getBit(blue, 7) * 2;
        value+= getBit(blue, 6);

        return value;
    }

    private int getBit(int n, int k) {
        return (n >> k) & 1;
    }

    public int[][] getRGBMatrix() {
        return RGBMatrix;
    }

    public File getFile() {
        return file;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Image image = (Image) o;

        return file.equals(image.file);

    }

    @Override
    public int hashCode() {
        return file.hashCode();
    }
}
