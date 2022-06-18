package Map;

import Map.Map;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import Util.ImageLoader;

public class Flag extends GameEntity {

    public Flag(double x, double y, Map map) {
        super(x,y,map, InputAction.Default, FillType.Image, 1);
        this.sizeX = 75;
        this.sizeY = 75;
        this.color = Color.color(1,0.5,0);
        this.image = ImageLoader.flag;

    }


    public void tick() {

        if (map.player == null)  {
            return;
        }

        if (this.getMainShape().intersect(map.player.getMainShape())) {
            map.winGame();

        }

    }

    public void render(GraphicsContext g) {
        renderSquare(g);
    }


    public String toString() {
        String line = "flag " + (int)x + " " + (int)y;

        return line;
    }
}
