package Map;

import Map.Map;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import Util.ImageLoader;

/**
 * currently unused
 */
public class CheckPoint extends GameEntity {

    private int code;

    public CheckPoint(double x, double y, Map map, double parallax, int code) {
        super(x,y,map, InputAction.Default, FillType.Image, parallax);
        this.code = code;
        this.sizeX = 50;
        this.sizeY = 50;
        this.color = Color.color(1,0.5,0);
        this.image = ImageLoader.checkpoint;

    }




    public void tick() {


        if(this.getMainShape().intersect(map.player.getMainShape())) {
       //     collectKey();
        }
    }

    public void render(GraphicsContext g) {
        renderSquare(g);
    }

    public String toString() {
        String line = "key " + (int)x + " " + (int)y + " " + code;

        return line;
    }
}
