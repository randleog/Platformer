
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
    public static Image wallTile2 = loadImage("wallTile2.png", 64);
    public static Image wallTile3 = loadImage("wallTile3.png", 64);
    public static Image wallTile4 = loadImage("wallTile4.png", 64);
    public static Image defaultTile = loadImage("default.png", 64);
    public static Image sky1 = loadImage("sky1.png", 1920,1080);
    public static Image particle = loadImage("particle.png", 64);

    public static Image speed = loadImage("speed.png", 64);
    public static Image enemy = loadImage("enemy.png", 64);
    public static Image key = loadImage("key.png", 64);
    public static Image gate = loadImage("gateTile.png", 64);
    public static Image trophy = loadImage("trophy.png", 64);
    public static Image flag = loadImage("flag.png", 64);

    public static Image dimension = loadImage("dimension.png", 64);
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

    /**
     * Create an input stream to read images.
     * @param fileName File name.
     * @param width Size of image.
     * @param height Size of image.
     * @return The image.
     */
    private static Image loadImage(String fileName, int width, int height) {


        Image image;
        FileInputStream inputstream = null;
        try {


            inputstream = new FileInputStream("res\\images\\" + fileName);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
            image = new Image(inputstream, width, height, true, false);

        return image;
    }



}
