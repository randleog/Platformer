
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * this class allows access to images
 * @author William Randle
 */
public class ImageLoader {

    public static Image wallTile = loadImage("wallTile.png", 64);
    public static Image defaultTile = loadImage("default.png", 64);


    /**
     * Create an input stream to read images.
     * @param fileName File name.
     * @param size Size of image.
     * @return The image.
     */
    private static Image loadImage(String fileName, int size) {


        Image image;
        FileInputStream inputstream = null;
        try {


            inputstream = new FileInputStream("res\\images\\" + fileName);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        image = new Image(inputstream, size, size, true, false);

        return image;
    }





}
