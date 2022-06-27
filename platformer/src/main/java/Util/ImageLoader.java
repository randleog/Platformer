package Util;

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
    public static Image sky1 = loadImage("sky1.png", 1920,1080);
    public static Image stickyWall = loadImage("stickyWall.png", 64);

    public static Image sandTile = loadImage("sandTile.png", 64);


    public static Image wallTile = loadImage("wallTile.png", 64);
    public static Image wallTile2 = loadImage("wallTile2.png", 64);
    public static Image wallTile3 = loadImage("wallTile3.png", 64);
    public static Image wallTile4 = loadImage("wallTile4.png", 64);

    public static Image pinkWall = loadImage("pinkWall.png", 64);
    public static Image pinkwallTile2 = loadImage("pinkwallTile2.png", 64);
    public static Image pinkwallTile3 = loadImage("pinkwallTile3.png", 64);
    public static Image pinkwallTile4 = loadImage("pinkwallTile4.png", 64);



    public static Image player = loadImage("default.png", 64);
    public static Image particle = loadImage("particle.png", 64);

    public static Image water = loadImage("water.png", 64);

    public static Image speed = loadImage("speed.png", 64);
    public static Image enemy = loadImage("enemy.png", 64);

    public static Image key = loadImage("key.png", 64);
    public static Image gate = loadImage("gateTile.png", 64);
    public static Image lock = loadImage("lock.png", 64);


    public static Image trophy = loadImage("trophy.png", 64);
    public static Image flag = loadImage("flag.png", 64);
    public static Image checkpoint = loadImage("checkpoint.png", 64);

    public static Image eraser = loadImage("eraser.png", 64);
    public static Image dimension = loadImage("dimension.png", 64);

    public static Image cursor = loadImage("cursor.png", 64);


    public static Image corner = loadImage("corner.png", 64);

    public static Image splash = loadImage("splash.png", 64);

    public static Image lava = loadImage("lava.png", 64);

    public static Image shurikan = loadImage("shurikan.png", 64);

    public static Image gear = loadImage("gear.png", 64);
    public static Image plate = loadImage("plate.png", 64);
    public static Image ninjaRight = loadImage("ninjaRight.png", 64);
    public static Image ninjaMid = loadImage("ninjaMid.png", 64);
    public static Image ninjaLeft = loadImage("ninjaLeft.png", 64);
    public static Image candle = loadImage("candle.png", 64);
    public static Image candleNot = loadImage("candleNot.png", 64);

    public static Image torso1 = loadImage("torso1.png", 64);
    public static Image legs1 = loadImage("legs1.png", 64);
    public static Image head1 = loadImage("head1.png", 64);
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
    public static Image loadImage(String fileName, int width, int height) {


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



    public static Image getImage(String name) {
        return switch (name) {
            case "wallTile" -> wallTile;
            case "stickyWall" -> stickyWall;
            case "flag" -> flag;
            case "eraser" -> eraser;
            case "player" -> player;
            case "key" -> key;
            case "cursor" -> cursor;
            case "gate" -> gate;
            case "water" -> water;
            case "corner" -> corner;
            case "lava" -> lava;
            case "sandTile" -> sandTile;
            case "gear", "gearSpeed", "gearLarge" -> gear;
            case "pink", "pinkTile" -> pinkWall;
            case "plate" -> plate;
            case "portal" -> dimension;
            case "shurikan" -> shurikan;
            case "candle" -> candle;
            case "candleNot" -> candleNot;
            default -> wallTile;

        };
    }


}
