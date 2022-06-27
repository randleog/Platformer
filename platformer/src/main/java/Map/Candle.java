package Map;

import Map.Map;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.paint.*;
import Util.ImageLoader;

public class Candle extends GameEntity {

    private double ratio;

    public Candle(double x, double y, double sizeX, double sizeY, Map map) {
        super(x,y,map, InputAction.Default, FillType.Image, 1);
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.color = Color.color(1,0.5,0);
        this.image = ImageLoader.candle;



        ratio = sizeX/sizeY;
    }


    public void tick() {




    }

    public void render(GraphicsContext g) {

        renderSquare(g);

    }


    public String toString() {
        String line = "candle " + (int)x + " " + (int)y+ " " + (int)sizeX + " " + (int)sizeY;

        return line;
    }
}
