package Map;

import GameControl.Square;
import Util.ImageLoader;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.*;
import Main.Main;


public class BackgroundObject {

    private Map map;
    private Image image;

    private double x;
    private double y;

    private double sizeX;
    private double sizeY;

    private double parallax;


    public BackgroundObject(double x, double y, double sizeX, double sizeY, Map map, Image image, double parallax) {
        this.image = image;
        this.parallax = parallax;
        this.map = map;

        this.x = x;
        this.y = y;
        this.sizeX = sizeX;
        this.sizeY = sizeY;

    }


    public Square getMainShape() {


        return new Square(this.x - ((this.sizeX < 0) ? Math.abs(this.sizeX) : 0), this.y - ((this.sizeY < 0) ? Math.abs(this.sizeY) : 0), Math.abs(this.sizeX), Math.abs(sizeY), parallax, InputAction.Default);
    }

    public void tick() {

    }


    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setSizeX(double sizeX) {
        this.sizeX = sizeX;
    }

    public void setSizeY(double sizeY) {
        this.sizeY = sizeY;
    }

    public double getSizeY() {
        return sizeY;
    }
    public double getSizeX() {
        return sizeX;
    }

    public void render(GraphicsContext g) {
        double x = Main.correctUnit(this.x-map.cameraX* parallax);
        double y = Main.correctUnit(this.y-map.cameraY* parallax);

        double sizeX = Main.correctUnit(this.sizeX);
        double sizeY = Main.correctUnit(this.sizeY);




        g.setFill(new ImagePattern(image, x, y, Main.correctUnit(40) * parallax
                , map.correctUnit(40) * parallax, false));





        g.fillRect(x, y, (int)sizeX+1, (int)sizeY+1);

        g.setFill(Color.color(0,0,0,0.5));




        g.fillRect(x, y, (int)sizeX+1, (int)sizeY+1);


        Color light = Color.color(0, 0, 0, 0.5);
        Color fade = Color.color(0, 0, 0, 0);


        //  Stop[] stops = new Stop[] { new Stop(0, fade),new Stop(0.5, light),new Stop(1, fade)};
        Stop[] stops = new Stop[]{new Stop(0, light), new Stop(1, fade)};
        LinearGradient fadeup = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops);
        g.setFill(fadeup);
        g.fillRect(x, y , sizeX, Main.correctUnit(20));



    }

    public String toString() {
        String line = "" + (int)x + " " + (int)y + " " + (int)sizeX + " " + (int)sizeY;

        return line;
    }

}
